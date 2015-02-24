/**
 * Copyright (C) 2012-2015 52°North Initiative for Geospatial Open Source
 * Software GmbH
 *
 * This program is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License version 2 as published
 * by the Free Software Foundation.
 *
 * If the program is linked with libraries which are licensed under one of
 * the following licenses, the combination of the program with the linked
 * library is not considered a "derivative work" of the program:
 *
 *     - Apache License, version 2.0
 *     - Apache Software License, version 1.0
 *     - GNU Lesser General Public License, version 3
 *     - Mozilla Public License, versions 1.0, 1.1 and 2.0
 *     - Common Development and Distribution License (CDDL), version 1.0
 *
 * Therefore the distribution of the program linked with libraries licensed
 * under the aforementioned licenses, is permitted by the copyright holders
 * if the distribution is compliant with both the GNU General Public
 * License version 2 and the aforementioned licenses.
 *
 * This program is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General
 * Public License for more details.
 */
package org.n52.sos.encode;

import static java.util.Collections.singletonMap;
import static org.n52.sos.util.CodingHelper.encoderKeysForElements;
import static org.n52.sos.util.CollectionHelper.union;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import net.opengis.om.x10.CategoryObservationDocument;
import net.opengis.om.x10.CategoryObservationType;
import net.opengis.om.x10.CountObservationDocument;
import net.opengis.om.x10.CountObservationType;
import net.opengis.om.x10.GeometryObservationDocument;
import net.opengis.om.x10.GeometryObservationType;
import net.opengis.om.x10.MeasurementDocument;
import net.opengis.om.x10.MeasurementType;
import net.opengis.om.x10.ObservationCollectionDocument;
import net.opengis.om.x10.ObservationCollectionType;
import net.opengis.om.x10.ObservationDocument;
import net.opengis.om.x10.ObservationPropertyType;
import net.opengis.om.x10.ObservationType;
import net.opengis.om.x10.TruthObservationDocument;
import net.opengis.om.x10.TruthObservationType;

import org.apache.xmlbeans.XmlBoolean;
import org.apache.xmlbeans.XmlInteger;
import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.XmlString;
import org.joda.time.DateTime;
import org.n52.sos.exception.ows.InvalidParameterValueException;
import org.n52.sos.exception.ows.concrete.UnsupportedEncoderInputException;
import org.n52.sos.ogc.gml.AbstractFeature;
import org.n52.sos.ogc.gml.GmlConstants;
import org.n52.sos.ogc.gml.time.Time;
import org.n52.sos.ogc.gml.time.TimeInstant;
import org.n52.sos.ogc.gml.time.TimePeriod;
import org.n52.sos.ogc.om.MultiObservationValues;
import org.n52.sos.ogc.om.OmCompositePhenomenon;
import org.n52.sos.ogc.om.OmConstants;
import org.n52.sos.ogc.om.OmObservableProperty;
import org.n52.sos.ogc.om.OmObservation;
import org.n52.sos.ogc.om.SingleObservationValue;
import org.n52.sos.ogc.om.StreamingValue;
import org.n52.sos.ogc.om.features.samplingFeatures.SamplingFeature;
import org.n52.sos.ogc.om.values.BooleanValue;
import org.n52.sos.ogc.om.values.CategoryValue;
import org.n52.sos.ogc.om.values.CountValue;
import org.n52.sos.ogc.om.values.GeometryValue;
import org.n52.sos.ogc.om.values.QuantityValue;
import org.n52.sos.ogc.om.values.TextValue;
import org.n52.sos.ogc.ows.OwsExceptionReport;
import org.n52.sos.ogc.sos.Sos1Constants;
import org.n52.sos.ogc.sos.SosConstants;
import org.n52.sos.ogc.sos.SosConstants.HelperValues;
import org.n52.sos.ogc.sos.SosEnvelope;
import org.n52.sos.ogc.swe.SweConstants;
import org.n52.sos.ogc.swe.SweDataArray;
import org.n52.sos.response.GetObservationByIdResponse;
import org.n52.sos.response.GetObservationResponse;
import org.n52.sos.service.Configurator;
import org.n52.sos.service.ServiceConstants.SupportedTypeKey;
import org.n52.sos.service.profile.Profile;
import org.n52.sos.util.CodingHelper;
import org.n52.sos.util.CollectionHelper;
import org.n52.sos.util.Constants;
import org.n52.sos.util.GmlHelper;
import org.n52.sos.util.N52XmlHelper;
import org.n52.sos.util.OMHelper;
import org.n52.sos.util.StringHelper;
import org.n52.sos.util.SweHelper;
import org.n52.sos.util.XmlHelper;
import org.n52.sos.util.XmlOptionsHelper;
import org.n52.sos.util.http.MediaType;
import org.n52.sos.w3c.SchemaLocation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Joiner;
import com.google.common.base.Strings;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;

/**
 * @since 4.0.0
 * 
 */
public class OmEncoderv100 extends AbstractXmlEncoder<Object> implements ObservationEncoder<XmlObject, Object> {

    private static final Logger LOGGER = LoggerFactory.getLogger(OmEncoderv100.class);

    private static final Map<SupportedTypeKey, Set<String>> SUPPORTED_TYPES = Collections.singletonMap(
            SupportedTypeKey.ObservationType, (Set<String>) ImmutableSet.of(OmConstants.OBS_TYPE_CATEGORY_OBSERVATION,
                    OmConstants.OBS_TYPE_COUNT_OBSERVATION,
                    // OMConstants.OBS_TYPE_GEOMETRY_OBSERVATION,
                    OmConstants.OBS_TYPE_MEASUREMENT, OmConstants.OBS_TYPE_TEXT_OBSERVATION,
                    OmConstants.OBS_TYPE_TRUTH_OBSERVATION, OmConstants.OBS_TYPE_SWE_ARRAY_OBSERVATION));

    // TODO: change to correct conformance class
    private static final Set<String> CONFORMANCE_CLASSES = ImmutableSet.of(
            "http://www.opengis.net/spec/OMXML/1.0/conf/measurement",
            "http://www.opengis.net/spec/OMXML/1.0/conf/categoryObservation",
            "http://www.opengis.net/spec/OMXML/1.0/conf/countObservation",
            "http://www.opengis.net/spec/OMXML/1.0/conf/truthObservation",
            "http://www.opengis.net/spec/OMXML/1.0/conf/geometryObservation",
            "http://www.opengis.net/spec/OMXML/1.0/conf/textObservation");

    private static final Map<String, Map<String, Set<String>>> SUPPORTED_RESPONSE_FORMATS = singletonMap(
            SosConstants.SOS,
            singletonMap(Sos1Constants.SERVICEVERSION,
                    (Set<String>) ImmutableSet.of(OmConstants.CONTENT_TYPE_OM.toString())));

    @SuppressWarnings("unchecked")
    private static final Set<EncoderKey> ENCODER_KEYS = union(
            encoderKeysForElements(OmConstants.NS_OM, OmObservation.class, GetObservationResponse.class,
                    GetObservationByIdResponse.class),
            encoderKeysForElements(OmConstants.CONTENT_TYPE_OM.toString(), OmObservation.class,
                    GetObservationResponse.class, GetObservationByIdResponse.class));

    public OmEncoderv100() {
        LOGGER.debug("Encoder for the following keys initialized successfully: {}!", Joiner.on(", ")
                .join(ENCODER_KEYS));
    }

    @Override
    public Set<EncoderKey> getEncoderKeyType() {
        return Collections.unmodifiableSet(ENCODER_KEYS);
    }

    @Override
    public Map<SupportedTypeKey, Set<String>> getSupportedTypes() {
        return Collections.unmodifiableMap(SUPPORTED_TYPES);
    }

    @Override
    public Set<String> getConformanceClasses() {
        return Collections.unmodifiableSet(CONFORMANCE_CLASSES);
    }

    @Override
    public void addNamespacePrefixToMap(Map<String, String> nameSpacePrefixMap) {
        nameSpacePrefixMap.put(OmConstants.NS_OM, OmConstants.NS_OM_PREFIX);
    }

    @Override
    public boolean isObservationAndMeasurmentV20Type() {
        return false;
    }

    @Override
    public Set<String> getSupportedResponseFormats(String service, String version) {
        if (SUPPORTED_RESPONSE_FORMATS.get(service) != null
                && SUPPORTED_RESPONSE_FORMATS.get(service).get(version) != null) {
            return SUPPORTED_RESPONSE_FORMATS.get(service).get(version);
        }
        return Collections.emptySet();
    }

    @Override
    public boolean shouldObservationsWithSameXBeMerged() {
        return true;
    }

    @Override
    public boolean supportsResultStreamingForMergedValues() {
        return false;
    }

    @Override
    public MediaType getContentType() {
        return OmConstants.CONTENT_TYPE_OM;
    }

    @Override
    public Set<SchemaLocation> getSchemaLocations() {
        return Sets.newHashSet(OmConstants.OM_100_SCHEMA_LOCATION);
    }

    @Override
    public XmlObject encode(Object element, Map<HelperValues, String> additionalValues) throws OwsExceptionReport {
        XmlObject encodedObject = null;
        if (element instanceof OmObservation) {
            encodedObject = createObservation((OmObservation) element, additionalValues);
        } else if (element instanceof GetObservationResponse) {
            GetObservationResponse response = (GetObservationResponse) element;
            encodedObject =
                    createObservationCollection(response.getObservationCollection(), response.getResultModel());
        } else if (element instanceof GetObservationByIdResponse) {
            GetObservationByIdResponse response = (GetObservationByIdResponse) element;
            encodedObject =
                    createObservationCollection(response.getObservationCollection(), response.getResultModel());
        } else {
            throw new UnsupportedEncoderInputException(this, element);
        }
        LOGGER.debug("Encoded object {} is valid: {}", encodedObject.schemaType().toString(),
                XmlHelper.validateDocument(encodedObject));
        return encodedObject;
    }

    private XmlObject createObservation(OmObservation sosObservation, Map<HelperValues, String> additionalValues)
            throws OwsExceptionReport {
        String observationType = checkObservationType(sosObservation);
        if (OmConstants.OBS_TYPE_MEASUREMENT.equals(observationType)) {
            return createMeasurement(sosObservation, additionalValues);
        } else if (OmConstants.OBS_TYPE_CATEGORY_OBSERVATION.equals(observationType)) {
            return createCategoryObservation(sosObservation, additionalValues);
        } else if (OmConstants.OBS_TYPE_COUNT_OBSERVATION.equals(observationType)) {
            return createCountObservation(sosObservation, additionalValues);
        } else if (OmConstants.OBS_TYPE_TRUTH_OBSERVATION.equals(observationType)) {
            return createTruthObservation(sosObservation, additionalValues);
        } else if (OmConstants.OBS_TYPE_GEOMETRY_OBSERVATION.equals(observationType)) {
            return createGeometryObservation(sosObservation, additionalValues);
        } else {
            return createOmObservation(sosObservation, additionalValues);
        }
    }

    private String checkObservationType(OmObservation sosObservation) {
        if (sosObservation.isSetResultType()) {
            return sosObservation.getResultType();
        } else if (sosObservation.getValue() instanceof SingleObservationValue) {
            SingleObservationValue<?> observationValue = (SingleObservationValue<?>) sosObservation.getValue();
            return OMHelper.getObservationTypeFor(observationValue.getValue());
        }
        return OmConstants.OBS_TYPE_OBSERVATION;
    }

    private XmlObject createObservationCollection(List<OmObservation> sosObservationCollection, String resultModel)
            throws OwsExceptionReport {
        ObservationCollectionDocument xbObservationCollectionDoc =
                ObservationCollectionDocument.Factory.newInstance(XmlOptionsHelper.getInstance().getXmlOptions());
        ObservationCollectionType xbObservationCollection = xbObservationCollectionDoc.addNewObservationCollection();
        xbObservationCollection.setId(SosConstants.OBS_COL_ID_PREFIX + new DateTime().getMillis());
        if (CollectionHelper.isNotEmpty(sosObservationCollection)) {
            SosEnvelope sosEnvelope = getEnvelope(sosObservationCollection);
            Encoder<XmlObject, SosEnvelope> envEncoder = CodingHelper.getEncoder(GmlConstants.NS_GML, sosEnvelope);
            xbObservationCollection.addNewBoundedBy().addNewEnvelope().set(envEncoder.encode(sosEnvelope));
            for (OmObservation sosObservation : sosObservationCollection) {
                String observationType = checkObservationType(sosObservation);
                if (Strings.isNullOrEmpty(resultModel)
                        || (StringHelper.isNotEmpty(resultModel) && observationType.equals(resultModel))) {
                    if (sosObservation.getValue() instanceof StreamingValue) {
                        StreamingValue streamingValue = (StreamingValue) sosObservation.getValue();
                        while (streamingValue.hasNextValue()) {
                            xbObservationCollection.addNewMember().set(
                                    createObservation(streamingValue.nextSingleObservation(), null));
                        }
                    } else {
                        xbObservationCollection.addNewMember().set(createObservation(sosObservation, null));
                    }
                } else {
                    throw new InvalidParameterValueException().at(Sos1Constants.GetObservationParams.resultModel)
                            .withMessage("The requested resultModel '%s' is invalid for the resulting observations!",
                                    OMHelper.getEncodedResultModelFor(resultModel));
                }
            }
        } else {
            ObservationPropertyType xbObservation = xbObservationCollection.addNewMember();
            xbObservation.setHref(GmlConstants.NIL_INAPPLICABLE);
        }
        XmlHelper.makeGmlIdsUnique(xbObservationCollectionDoc.getDomNode());
        N52XmlHelper.setSchemaLocationsToDocument(xbObservationCollectionDoc, Sets.newHashSet(
                N52XmlHelper.getSchemaLocationForSOS100(), N52XmlHelper.getSchemaLocationForOM100(),
                N52XmlHelper.getSchemaLocationForSA100()));
        return xbObservationCollectionDoc;
    }

    private SosEnvelope getEnvelope(List<OmObservation> sosObservationCollection) {
        SosEnvelope sosEnvelope = new SosEnvelope();
        for (OmObservation sosObservation : sosObservationCollection) {
            sosObservation.getObservationConstellation().getFeatureOfInterest();
            SamplingFeature samplingFeature =
                    (SamplingFeature) sosObservation.getObservationConstellation().getFeatureOfInterest();
            sosEnvelope.setSrid(samplingFeature.getGeometry().getSRID());
            sosEnvelope.expandToInclude(samplingFeature.getGeometry().getEnvelopeInternal());
        }
        return sosEnvelope;
    }

    private XmlObject createMeasurement(OmObservation sosObservation, Map<HelperValues, String> additionalValues)
            throws OwsExceptionReport {
        MeasurementDocument xbMeasurementDoc =
                MeasurementDocument.Factory.newInstance(XmlOptionsHelper.getInstance().getXmlOptions());
        MeasurementType xbObs = xbMeasurementDoc.addNewMeasurement();
        addValuesToObservation(xbObs, sosObservation, additionalValues);
        addSingleObservationToResult(xbObs.addNewResult(), sosObservation);
        return xbMeasurementDoc;
    }

    private XmlObject createCategoryObservation(OmObservation sosObservation,
            Map<HelperValues, String> additionalValues) throws OwsExceptionReport {
        CategoryObservationDocument xbCategoryObservationDoc =
                CategoryObservationDocument.Factory.newInstance(XmlOptionsHelper.getInstance().getXmlOptions());
        CategoryObservationType xbObs = xbCategoryObservationDoc.addNewCategoryObservation();
        addValuesToObservation(xbObs, sosObservation, additionalValues);
        addSingleObservationToResult(xbObs.addNewResult(), sosObservation);
        return xbCategoryObservationDoc;
    }

    private XmlObject createCountObservation(OmObservation sosObservation, Map<HelperValues, String> additionalValues)
            throws OwsExceptionReport {
        CountObservationDocument xbCountObservationDoc =
                CountObservationDocument.Factory.newInstance(XmlOptionsHelper.getInstance().getXmlOptions());
        CountObservationType xbObs = xbCountObservationDoc.addNewCountObservation();
        addValuesToObservation(xbObs, sosObservation, additionalValues);
        addSingleObservationToResult(xbObs.addNewResult(), sosObservation);
        return xbCountObservationDoc;
    }

    private XmlObject createTruthObservation(OmObservation sosObservation, Map<HelperValues, String> additionalValues)
            throws OwsExceptionReport {
        TruthObservationDocument xbTruthObservationDoc =
                TruthObservationDocument.Factory.newInstance(XmlOptionsHelper.getInstance().getXmlOptions());
        TruthObservationType xbObs = xbTruthObservationDoc.addNewTruthObservation();
        addValuesToObservation(xbObs, sosObservation, additionalValues);
        addSingleObservationToResult(xbObs.addNewResult(), sosObservation);
        return xbTruthObservationDoc;
    }

    private XmlObject createGeometryObservation(OmObservation sosObservation,
            Map<HelperValues, String> additionalValues) throws OwsExceptionReport {
        GeometryObservationDocument xbGeometryObservationDoc =
                GeometryObservationDocument.Factory.newInstance(XmlOptionsHelper.getInstance().getXmlOptions());
        GeometryObservationType xbObs = xbGeometryObservationDoc.addNewGeometryObservation();
        addValuesToObservation(xbObs, sosObservation, additionalValues);
        addSingleObservationToResult(xbObs.addNewResult(), sosObservation);
        return xbGeometryObservationDoc;
    }

    private XmlObject createOmObservation(OmObservation sosObservation, Map<HelperValues, String> additionalValues)
            throws OwsExceptionReport {
        ObservationDocument xbObservationDoc =
                ObservationDocument.Factory.newInstance(XmlOptionsHelper.getInstance().getXmlOptions());
        ObservationType xbObs = xbObservationDoc.addNewObservation();
        List<OmObservableProperty> phenComponents = addValuesToObservation(xbObs, sosObservation, additionalValues);
        addResultToObservation(xbObs.addNewResult(), sosObservation, phenComponents);
        return xbObservationDoc;
    }

    private List<OmObservableProperty> addValuesToObservation(ObservationType xbObs, OmObservation sosObservation,
            Map<HelperValues, String> additionalValues) throws OwsExceptionReport {
        xbObs.setId("o_" + Long.toString(System.currentTimeMillis()));
        if (!sosObservation.isSetObservationID()) {
            sosObservation.setObservationID(xbObs.getId().replace("o_", Constants.EMPTY_STRING));
        }
        String observationID = sosObservation.getObservationID();
        // set samplingTime
        Time samplingTime = sosObservation.getPhenomenonTime();
        if (samplingTime.getGmlId() == null) {
            samplingTime.setGmlId(OmConstants.PHENOMENON_TIME_NAME + Constants.UNDERSCORE_STRING + observationID);
        }
        addSamplingTime(xbObs, samplingTime);
        // set resultTime
        addResultTime(xbObs, sosObservation);

        // set procedure
        xbObs.addNewProcedure().setHref(sosObservation.getObservationConstellation().getProcedure().getIdentifier());
        // set observedProperty (phenomenon)
        List<OmObservableProperty> phenComponents = null;
        if (sosObservation.getObservationConstellation().getObservableProperty() instanceof OmObservableProperty) {
            xbObs.addNewObservedProperty().setHref(
                    sosObservation.getObservationConstellation().getObservableProperty().getIdentifier());
            phenComponents = new ArrayList<OmObservableProperty>(1);
            phenComponents.add((OmObservableProperty) sosObservation.getObservationConstellation()
                    .getObservableProperty());
        } else if (sosObservation.getObservationConstellation().getObservableProperty() instanceof OmCompositePhenomenon) {
            OmCompositePhenomenon compPhen =
                    (OmCompositePhenomenon) sosObservation.getObservationConstellation().getObservableProperty();
            xbObs.addNewObservedProperty().setHref(compPhen.getIdentifier());
            phenComponents = compPhen.getPhenomenonComponents();
        }
        // set feature
        addFeatureOfInterest(xbObs, sosObservation.getObservationConstellation().getFeatureOfInterest());
        return phenComponents;
    }

    private void addSamplingTime(ObservationType xbObservation, Time iTime) throws OwsExceptionReport {
        XmlObject xmlObject = CodingHelper.encodeObjectToXml(GmlConstants.NS_GML, iTime);
        XmlObject substitution =
                xbObservation.addNewSamplingTime().addNewTimeObject()
                        .substitute(GmlHelper.getGml311QnameForITime(iTime), xmlObject.schemaType());
        substitution.set(xmlObject);
    }

    private void addResultTime(ObservationType xbObs, OmObservation sosObservation) throws OwsExceptionReport {
        Time phenomenonTime = sosObservation.getPhenomenonTime();
        if (sosObservation.isSetResultTime()) {
            if (sosObservation.getResultTime().equals(phenomenonTime)) {
                xbObs.addNewResultTime().setHref(Constants.NUMBER_SIGN_STRING + phenomenonTime.getGmlId());
            } else {
                TimeInstant resultTime = sosObservation.getResultTime();
                if (!resultTime.isSetGmlId()) {
                    resultTime.setGmlId("resultTime_" + sosObservation.getObservationID());
                }
                addResultTime(xbObs, resultTime);
            }
        } else {
            if (phenomenonTime instanceof TimeInstant) {
                xbObs.addNewResultTime().setHref(Constants.NUMBER_SIGN_STRING + phenomenonTime.getGmlId());
            } else if (phenomenonTime instanceof TimePeriod) {
                TimeInstant resultTime = new TimeInstant(((TimePeriod) sosObservation.getPhenomenonTime()).getEnd());
                resultTime.setGmlId("resultTime_" + sosObservation.getObservationID());
                addResultTime(xbObs, resultTime);
            }
        }
    }

    private void addResultTime(ObservationType xbObs, TimeInstant iTime) throws OwsExceptionReport {
        XmlObject xmlObject = CodingHelper.encodeObjectToXml(GmlConstants.NS_GML, iTime);
        XmlObject substitution =
                xbObs.addNewResultTime().addNewTimeObject()
                        .substitute(GmlHelper.getGml311QnameForITime(iTime), xmlObject.schemaType());
        substitution.set(xmlObject);
    }

    private XmlObject createCompositePhenomenon(String compPhenId, Collection<String> phenComponents) {
        // Currently not used for SOS 2.0 and OM 2.0 encoding.
        return null;
    }

    private void addResultToObservation(XmlObject xbResult, OmObservation sosObservation,
            List<OmObservableProperty> phenComponents) throws OwsExceptionReport {
        // TODO if OM_SWEArrayObservation and get ResultEncoding and
        // ResultStructure exists,
        if (sosObservation.getValue() instanceof SingleObservationValue) {
            addSingleObservationToResult(xbResult, sosObservation);
        } else if (sosObservation.getValue() instanceof MultiObservationValues) {
            addMultiObservationValueToResult(xbResult, sosObservation);
        }
    }

    // FIXME String.equals(QName) !?
    private void addSingleObservationToResult(XmlObject xbResult, OmObservation sosObservation)
            throws OwsExceptionReport {
        String observationType = sosObservation.getObservationConstellation().getObservationType();
        SingleObservationValue<?> observationValue = (SingleObservationValue<?>) sosObservation.getValue();
        if (observationValue.getValue() instanceof QuantityValue) {
            QuantityValue quantityValue = (QuantityValue) observationValue.getValue();
            xbResult.set(CodingHelper.encodeObjectToXml(GmlConstants.NS_GML, quantityValue));
        } else if (observationValue.getValue() instanceof CountValue) {
            CountValue countValue = (CountValue) observationValue.getValue();
            XmlInteger xbInteger = XmlInteger.Factory.newInstance(XmlOptionsHelper.getInstance().getXmlOptions());
            if (countValue.getValue() != null && countValue.getValue() != Integer.MIN_VALUE) {
                xbInteger.setBigIntegerValue(new BigInteger(countValue.getValue().toString()));
            } else {
                xbInteger.setNil();
            }
            xbResult.set(xbInteger);
        } else if (observationValue.getValue() instanceof TextValue) {
            TextValue textValue = (TextValue) observationValue.getValue();
            XmlString xbString = XmlString.Factory.newInstance(XmlOptionsHelper.getInstance().getXmlOptions());
            if (textValue.getValue() != null && !textValue.getValue().isEmpty()) {
                xbString.setStringValue(textValue.getValue());
            } else {
                xbString.setNil();
            }
            xbResult.set(xbString);
        } else if (observationValue.getValue() instanceof BooleanValue) {
            BooleanValue booleanValue = (BooleanValue) observationValue.getValue();
            XmlBoolean xbBoolean = XmlBoolean.Factory.newInstance(XmlOptionsHelper.getInstance().getXmlOptions());
            if (booleanValue.getValue() != null) {
                xbBoolean.setBooleanValue(booleanValue.getValue());
            } else {
                xbBoolean.setNil();
            }
            xbResult.set(xbBoolean);
        } else if (observationValue.getValue() instanceof CategoryValue) {
            CategoryValue categoryValue = (CategoryValue) observationValue.getValue();
            if (categoryValue.getValue() != null && !categoryValue.getValue().isEmpty()) {
                Map<HelperValues, String> additionalValue = new EnumMap<HelperValues, String>(HelperValues.class);
                additionalValue
                        .put(HelperValues.GMLID, SosConstants.OBS_ID_PREFIX + sosObservation.getObservationID());
                xbResult.set(CodingHelper.encodeObjectToXml(GmlConstants.NS_GML, categoryValue, additionalValue));
            } else {
                xbResult.setNil();
            }
        } else if (observationValue.getValue() instanceof GeometryValue) {
            GeometryValue geometryValue = (GeometryValue) observationValue.getValue();
            if (geometryValue.getValue() != null) {
                Map<HelperValues, String> additionalValue = new EnumMap<HelperValues, String>(HelperValues.class);
                additionalValue
                        .put(HelperValues.GMLID, SosConstants.OBS_ID_PREFIX + sosObservation.getObservationID());
                xbResult.set(CodingHelper.encodeObjectToXml(GmlConstants.NS_GML, geometryValue.getValue(),
                        additionalValue));
            } else {
                xbResult.setNil();
            }
        } else if (OmConstants.OBS_TYPE_SWE_ARRAY_OBSERVATION.equals(observationType)
                || OmConstants.RESULT_MODEL_OBSERVATION.equals(observationType)) {
            SweDataArray dataArray = SweHelper.createSosSweDataArray(sosObservation);
            Map<HelperValues, String> additionalValues =
                    new EnumMap<SosConstants.HelperValues, String>(SosConstants.HelperValues.class);
            additionalValues.put(HelperValues.FOR_OBSERVATION, null);
            xbResult.set(CodingHelper.encodeObjectToXml(SweConstants.NS_SWE_101, dataArray, additionalValues));
        }
    }

    private void addMultiObservationValueToResult(XmlObject xbResult, OmObservation sosObservation)
            throws OwsExceptionReport {
        Map<HelperValues, String> additionalValues =
                new EnumMap<SosConstants.HelperValues, String>(SosConstants.HelperValues.class);
        additionalValues.put(HelperValues.FOR_OBSERVATION, null);
        SweDataArray dataArray = SweHelper.createSosSweDataArray(sosObservation);
        xbResult.set(CodingHelper.encodeObjectToXml(SweConstants.NS_SWE_101, dataArray, additionalValues));
    }

    /**
     * Encodes a SosAbstractFeature to an SpatialSamplingFeature under
     * consideration of duplicated SpatialSamplingFeature in the XML document.
     * 
     * @param observation
     *            XmlObject O&M observation
     * @param feature
     *            SOS observation
     * 
     * 
     * @throws OwsExceptionReport
     */
    private void addFeatureOfInterest(ObservationType observation, AbstractFeature feature) throws OwsExceptionReport {
        Map<HelperValues, String> additionalValues =
                new EnumMap<SosConstants.HelperValues, String>(HelperValues.class);
        Profile activeProfile = Configurator.getInstance().getProfileHandler().getActiveProfile();
        additionalValues.put(HelperValues.ENCODE,
                Boolean.toString(activeProfile.isEncodeFeatureOfInterestInObservations()));
        XmlObject encodeObjectToXml = CodingHelper.encodeObjectToXml(GmlConstants.NS_GML, feature, additionalValues);
        observation.addNewFeatureOfInterest().set(encodeObjectToXml);
    }
}
