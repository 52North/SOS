/*
 * Copyright (C) 2012-2016 52°North Initiative for Geospatial Open Source
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
import static org.n52.shetland.util.CollectionHelper.union;
import static org.n52.sos.util.CodingHelper.encoderKeysForElements;

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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.n52.iceland.ogc.swe.SweConstants;
import org.n52.janmayen.http.MediaType;
import org.n52.shetland.ogc.SupportedType;
import org.n52.shetland.ogc.gml.AbstractFeature;
import org.n52.shetland.ogc.gml.GmlConstants;
import org.n52.shetland.ogc.gml.time.Time;
import org.n52.shetland.ogc.gml.time.TimeInstant;
import org.n52.shetland.ogc.gml.time.TimePeriod;
import org.n52.shetland.ogc.om.MultiObservationValues;
import org.n52.shetland.ogc.om.OmCompositePhenomenon;
import org.n52.shetland.ogc.om.OmConstants;
import org.n52.shetland.ogc.om.OmObservableProperty;
import org.n52.shetland.ogc.om.OmObservation;
import org.n52.shetland.ogc.om.SingleObservationValue;
import org.n52.shetland.ogc.om.features.samplingFeatures.SamplingFeature;
import org.n52.shetland.ogc.om.values.BooleanValue;
import org.n52.shetland.ogc.om.values.CategoryValue;
import org.n52.shetland.ogc.om.values.CountValue;
import org.n52.shetland.ogc.om.values.GeometryValue;
import org.n52.shetland.ogc.om.values.QuantityValue;
import org.n52.shetland.ogc.om.values.TextValue;
import org.n52.shetland.ogc.sos.Sos1Constants;
import org.n52.shetland.ogc.sos.Sos2Constants;
import org.n52.shetland.ogc.sos.SosConstants;
import org.n52.shetland.ogc.swe.SweDataArray;
import org.n52.shetland.util.CollectionHelper;
import org.n52.shetland.util.ReferencedEnvelope;
import org.n52.shetland.w3c.SchemaLocation;
import org.n52.sos.coding.encode.ObservationEncoder;
import org.n52.sos.ogc.om.StreamingValue;
import org.n52.sos.response.GetObservationByIdResponse;
import org.n52.sos.response.GetObservationResponse;
import org.n52.sos.service.profile.Profile;
import org.n52.sos.service.profile.ProfileHandler;
import org.n52.sos.util.GmlHelper;
import org.n52.sos.util.N52XmlHelper;
import org.n52.sos.util.OMHelper;
import org.n52.sos.util.SweHelper;
import org.n52.sos.util.XmlHelper;
import org.n52.svalbard.HelperValues;
import org.n52.svalbard.encode.Encoder;
import org.n52.svalbard.encode.EncoderKey;
import org.n52.svalbard.encode.exception.EncodingException;
import org.n52.svalbard.encode.exception.UnsupportedEncoderInputException;
import org.n52.svalbard.xml.AbstractXmlEncoder;

import com.google.common.base.Joiner;
import com.google.common.base.Strings;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;

/**
 * @since 4.0.0
 *
 */
public class OmEncoderv100 extends AbstractXmlEncoder<XmlObject, Object> implements ObservationEncoder<XmlObject, Object> {

    private static final Logger LOGGER = LoggerFactory
            .getLogger(OmEncoderv100.class);

    private static final Set<SupportedType> SUPPORTED_TYPES = ImmutableSet
            .<SupportedType>builder()
            .add(OmConstants.OBS_TYPE_SWE_ARRAY_OBSERVATION_TYPE)
            //.add(OmConstants.OBS_TYPE_COMPLEX_OBSERVATION_TYPE)
            //.add(OmConstants.OBS_TYPE_GEOMETRY_OBSERVATION_TYPE)
            .add(OmConstants.OBS_TYPE_CATEGORY_OBSERVATION_TYPE)
            .add(OmConstants.OBS_TYPE_COUNT_OBSERVATION_TYPE)
            .add(OmConstants.OBS_TYPE_MEASUREMENT_TYPE)
            .add(OmConstants.OBS_TYPE_TEXT_OBSERVATION_TYPE)
            .add(OmConstants.OBS_TYPE_TRUTH_OBSERVATION_TYPE)
            .build();

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
    public Set<EncoderKey> getKeys() {
        return Collections.unmodifiableSet(ENCODER_KEYS);
    }

    @Override
    public Set<SupportedType> getSupportedTypes() {
        return Collections.unmodifiableSet(SUPPORTED_TYPES);
    }

    @Override
    public Set<String> getConformanceClasses(String service, String version) {
        if(SosConstants.SOS.equals(service) && Sos2Constants.SERVICEVERSION.equals(version)) {
            return Collections.unmodifiableSet(CONFORMANCE_CLASSES);
        }
        return Collections.emptySet();
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
    public XmlObject encode(Object element, Map<HelperValues, String> additionalValues) throws EncodingException {
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
        XmlHelper.validateDocument(encodedObject, EncodingException::new);
        return encodedObject;
    }

    private XmlObject createObservation(OmObservation sosObservation, Map<HelperValues, String> additionalValues)
            throws EncodingException {
        String observationType = checkObservationType(sosObservation);
        if (null != observationType) {
            switch (observationType) {
                case OmConstants.OBS_TYPE_MEASUREMENT:
                    return createMeasurement(sosObservation, additionalValues);
                case OmConstants.OBS_TYPE_CATEGORY_OBSERVATION:
                    return createCategoryObservation(sosObservation, additionalValues);
                case OmConstants.OBS_TYPE_COUNT_OBSERVATION:
                    return createCountObservation(sosObservation, additionalValues);
                case OmConstants.OBS_TYPE_TRUTH_OBSERVATION:
                    return createTruthObservation(sosObservation, additionalValues);
                case OmConstants.OBS_TYPE_GEOMETRY_OBSERVATION:
                    return createGeometryObservation(sosObservation, additionalValues);
                default:
                    return createOmObservation(sosObservation, additionalValues);
            }
        } else {
            return createOmObservation(sosObservation, additionalValues);
        }
    }

    private String checkObservationType(OmObservation sosObservation) throws EncodingException {
        if (sosObservation.isSetResultType()) {
            return sosObservation.getResultType();
        } else if (sosObservation.getValue() instanceof SingleObservationValue) {
            SingleObservationValue<?> observationValue = (SingleObservationValue<?>) sosObservation.getValue();
            return OMHelper.getObservationTypeFor(observationValue.getValue());
        }
        return OmConstants.OBS_TYPE_OBSERVATION;
    }

    private XmlObject createObservationCollection(List<OmObservation> sosObservationCollection, String resultModel)
            throws EncodingException {
        ObservationCollectionDocument xbObservationCollectionDoc =
                ObservationCollectionDocument.Factory.newInstance(getXmlOptions());
        ObservationCollectionType xbObservationCollection = xbObservationCollectionDoc.addNewObservationCollection();
        xbObservationCollection.setId(SosConstants.OBS_COL_ID_PREFIX + new DateTime().getMillis());
        if (CollectionHelper.isNotEmpty(sosObservationCollection)) {
            ReferencedEnvelope sosEnvelope = getEnvelope(sosObservationCollection);
            Encoder<XmlObject, ReferencedEnvelope> envEncoder = getEncoder(GmlConstants.NS_GML, sosEnvelope);
            xbObservationCollection.addNewBoundedBy().addNewEnvelope().set(envEncoder.encode(sosEnvelope));
            for (OmObservation sosObservation : sosObservationCollection) {
                String observationType = checkObservationType(sosObservation);
                if (Strings.isNullOrEmpty(resultModel)
                        || (!Strings.isNullOrEmpty(resultModel) && observationType.equals(resultModel))) {
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
                    throw new EncodingException("The requested resultModel '%s' is invalid for the resulting observations!",
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

    private ReferencedEnvelope getEnvelope(List<OmObservation> sosObservationCollection) {
        ReferencedEnvelope sosEnvelope = new ReferencedEnvelope();
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
            throws EncodingException {
        MeasurementDocument xbMeasurementDoc =
                MeasurementDocument.Factory.newInstance(getXmlOptions());
        MeasurementType xbObs = xbMeasurementDoc.addNewMeasurement();
        addValuesToObservation(xbObs, sosObservation, additionalValues);
        addSingleObservationToResult(xbObs.addNewResult(), sosObservation);
        return xbMeasurementDoc;
    }

    private XmlObject createCategoryObservation(OmObservation sosObservation,
            Map<HelperValues, String> additionalValues) throws EncodingException {
        CategoryObservationDocument xbCategoryObservationDoc =
                CategoryObservationDocument.Factory.newInstance(getXmlOptions());
        CategoryObservationType xbObs = xbCategoryObservationDoc.addNewCategoryObservation();
        addValuesToObservation(xbObs, sosObservation, additionalValues);
        addSingleObservationToResult(xbObs.addNewResult(), sosObservation);
        return xbCategoryObservationDoc;
    }

    private XmlObject createCountObservation(OmObservation sosObservation, Map<HelperValues, String> additionalValues)
            throws EncodingException {
        CountObservationDocument xbCountObservationDoc =
                CountObservationDocument.Factory.newInstance(getXmlOptions());
        CountObservationType xbObs = xbCountObservationDoc.addNewCountObservation();
        addValuesToObservation(xbObs, sosObservation, additionalValues);
        addSingleObservationToResult(xbObs.addNewResult(), sosObservation);
        return xbCountObservationDoc;
    }

    private XmlObject createTruthObservation(OmObservation sosObservation, Map<HelperValues, String> additionalValues)
            throws EncodingException {
        TruthObservationDocument xbTruthObservationDoc =
                TruthObservationDocument.Factory.newInstance(getXmlOptions());
        TruthObservationType xbObs = xbTruthObservationDoc.addNewTruthObservation();
        addValuesToObservation(xbObs, sosObservation, additionalValues);
        addSingleObservationToResult(xbObs.addNewResult(), sosObservation);
        return xbTruthObservationDoc;
    }

    private XmlObject createGeometryObservation(OmObservation sosObservation,
            Map<HelperValues, String> additionalValues) throws EncodingException {
        GeometryObservationDocument xbGeometryObservationDoc =
                GeometryObservationDocument.Factory.newInstance(getXmlOptions());
        GeometryObservationType xbObs = xbGeometryObservationDoc.addNewGeometryObservation();
        addValuesToObservation(xbObs, sosObservation, additionalValues);
        addSingleObservationToResult(xbObs.addNewResult(), sosObservation);
        return xbGeometryObservationDoc;
    }

    private XmlObject createOmObservation(OmObservation sosObservation, Map<HelperValues, String> additionalValues)
            throws EncodingException {
        ObservationDocument xbObservationDoc =
                ObservationDocument.Factory.newInstance(getXmlOptions());
        ObservationType xbObs = xbObservationDoc.addNewObservation();
        List<OmObservableProperty> phenComponents = addValuesToObservation(xbObs, sosObservation, additionalValues);
        addResultToObservation(xbObs.addNewResult(), sosObservation, phenComponents);
        return xbObservationDoc;
    }

    private List<OmObservableProperty> addValuesToObservation(ObservationType xbObs, OmObservation sosObservation,
            Map<HelperValues, String> additionalValues) throws EncodingException {
        xbObs.setId("o_" + Long.toString(System.currentTimeMillis()));
        if (!sosObservation.isSetObservationID()) {
            sosObservation.setObservationID(xbObs.getId().replace("o_", ""));
        }
        String observationID = sosObservation.getObservationID();
        // set samplingTime
        Time samplingTime = sosObservation.getPhenomenonTime();
        if (samplingTime.getGmlId() == null) {
            samplingTime.setGmlId(OmConstants.PHENOMENON_TIME_NAME + "_" + observationID);
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
            phenComponents = new ArrayList<>(1);
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

    private void addSamplingTime(ObservationType xbObservation, Time iTime) throws EncodingException {
        XmlObject xmlObject = encodeObjectToXml(GmlConstants.NS_GML, iTime);
        XmlObject substitution =
                xbObservation.addNewSamplingTime().addNewTimeObject()
                        .substitute(GmlHelper.getGml311QnameForITime(iTime), xmlObject.schemaType());
        substitution.set(xmlObject);
    }

    private void addResultTime(ObservationType xbObs, OmObservation sosObservation) throws EncodingException {
        Time phenomenonTime = sosObservation.getPhenomenonTime();
        if (sosObservation.isSetResultTime()) {
            if (sosObservation.getResultTime().equals(phenomenonTime)) {
                xbObs.addNewResultTime().setHref("#".concat(phenomenonTime.getGmlId()));
            } else {
                TimeInstant resultTime = sosObservation.getResultTime();
                if (!resultTime.isSetGmlId()) {
                    resultTime.setGmlId("resultTime_".concat(sosObservation.getObservationID()));
                }
                addResultTime(xbObs, resultTime);
            }
        } else {
            if (phenomenonTime instanceof TimeInstant) {
                xbObs.addNewResultTime().setHref("#".concat(phenomenonTime.getGmlId()));
            } else if (phenomenonTime instanceof TimePeriod) {
                TimeInstant resultTime = new TimeInstant(((TimePeriod) sosObservation.getPhenomenonTime()).getEnd());
                resultTime.setGmlId("resultTime_" + sosObservation.getObservationID());
                addResultTime(xbObs, resultTime);
            }
        }
    }

    private void addResultTime(ObservationType xbObs, TimeInstant iTime) throws EncodingException {
        XmlObject xmlObject = encodeObjectToXml(GmlConstants.NS_GML, iTime);
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
            List<OmObservableProperty> phenComponents) throws EncodingException {
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
            throws EncodingException {
        String observationType = sosObservation.getObservationConstellation().getObservationType();
        SingleObservationValue<?> observationValue = (SingleObservationValue<?>) sosObservation.getValue();
        if (observationValue.getValue() instanceof QuantityValue) {
            QuantityValue quantityValue = (QuantityValue) observationValue.getValue();
            xbResult.set(encodeObjectToXml(GmlConstants.NS_GML, quantityValue));
        } else if (observationValue.getValue() instanceof CountValue) {
            CountValue countValue = (CountValue) observationValue.getValue();
            XmlInteger xbInteger = XmlInteger.Factory.newInstance(getXmlOptions());
            if (countValue.getValue() != null && countValue.getValue() != Integer.MIN_VALUE) {
                xbInteger.setBigIntegerValue(new BigInteger(countValue.getValue().toString()));
            } else {
                xbInteger.setNil();
            }
            xbResult.set(xbInteger);
        } else if (observationValue.getValue() instanceof TextValue) {
            TextValue textValue = (TextValue) observationValue.getValue();
            XmlString xbString = XmlString.Factory.newInstance(getXmlOptions());
            if (textValue.getValue() != null && !textValue.getValue().isEmpty()) {
                xbString.setStringValue(textValue.getValue());
            } else {
                xbString.setNil();
            }
            xbResult.set(xbString);
        } else if (observationValue.getValue() instanceof BooleanValue) {
            BooleanValue booleanValue = (BooleanValue) observationValue.getValue();
            XmlBoolean xbBoolean = XmlBoolean.Factory.newInstance(getXmlOptions());
            if (booleanValue.getValue() != null) {
                xbBoolean.setBooleanValue(booleanValue.getValue());
            } else {
                xbBoolean.setNil();
            }
            xbResult.set(xbBoolean);
        } else if (observationValue.getValue() instanceof CategoryValue) {
            CategoryValue categoryValue = (CategoryValue) observationValue.getValue();
            if (categoryValue.getValue() != null && !categoryValue.getValue().isEmpty()) {
                Map<HelperValues, String> additionalValue = new EnumMap<>(HelperValues.class);
                additionalValue.put(HelperValues.GMLID, SosConstants.OBS_ID_PREFIX + sosObservation.getObservationID());
                xbResult.set(encodeObjectToXml(GmlConstants.NS_GML, categoryValue, additionalValue));
            } else {
                xbResult.setNil();
            }
        } else if (observationValue.getValue() instanceof GeometryValue) {
            GeometryValue geometryValue = (GeometryValue) observationValue.getValue();
            if (geometryValue.getValue() != null) {
                Map<HelperValues, String> additionalValue = new EnumMap<>(HelperValues.class);
                additionalValue.put(HelperValues.GMLID, SosConstants.OBS_ID_PREFIX + sosObservation.getObservationID());
                xbResult.set(encodeObjectToXml(GmlConstants.NS_GML, geometryValue.getValue(),
                        additionalValue));
            } else {
                xbResult.setNil();
            }
        } else if (OmConstants.OBS_TYPE_SWE_ARRAY_OBSERVATION.equals(observationType)
                || OmConstants.RESULT_MODEL_OBSERVATION.getLocalPart().equals(observationType)) {
            SweDataArray dataArray = SweHelper.createSosSweDataArray(sosObservation);
            Map<HelperValues, String> additionalValues = new EnumMap<>(HelperValues.class);
            additionalValues.put(HelperValues.FOR_OBSERVATION, null);
            xbResult.set(encodeObjectToXml(SweConstants.NS_SWE_101, dataArray, additionalValues));
        }
    }

    private void addMultiObservationValueToResult(XmlObject xbResult, OmObservation sosObservation)
            throws EncodingException {
        Map<HelperValues, String> additionalValues = new EnumMap<>(HelperValues.class);
        additionalValues.put(HelperValues.FOR_OBSERVATION, null);
        SweDataArray dataArray = SweHelper.createSosSweDataArray(sosObservation);
        xbResult.set(encodeObjectToXml(SweConstants.NS_SWE_101, dataArray, additionalValues));
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
     * @throws EncodingException
     */
    private void addFeatureOfInterest(ObservationType observation, AbstractFeature feature) throws EncodingException {
        Map<HelperValues, String> additionalValues =
                new EnumMap<>(HelperValues.class);
        Profile activeProfile = ProfileHandler.getInstance().getActiveProfile();
        additionalValues.put(HelperValues.ENCODE,
                Boolean.toString(activeProfile.isEncodeFeatureOfInterestInObservations()));
        XmlObject encodeObjectToXml = encodeObjectToXml(GmlConstants.NS_GML, feature, additionalValues);
        observation.addNewFeatureOfInterest().set(encodeObjectToXml);
    }
}
