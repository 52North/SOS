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

import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import net.opengis.om.x20.NamedValueType;
import net.opengis.om.x20.OMObservationDocument;
import net.opengis.om.x20.OMObservationPropertyType;
import net.opengis.om.x20.OMObservationType;
import net.opengis.om.x20.OMProcessPropertyType;
import net.opengis.om.x20.TimeObjectPropertyType;

import org.apache.xmlbeans.XmlBoolean;
import org.apache.xmlbeans.XmlDouble;
import org.apache.xmlbeans.XmlInteger;
import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.XmlOptions;
import org.apache.xmlbeans.XmlString;
import org.isotc211.x2005.gmd.AbstractDQElementDocument;
import org.isotc211.x2005.gmd.DQElementPropertyType;
import org.n52.sos.coding.CodingRepository;
import org.n52.sos.convert.Converter;
import org.n52.sos.convert.ConverterException;
import org.n52.sos.convert.ConverterRepository;
import org.n52.sos.encode.streaming.StreamingEncoder;
import org.n52.sos.exception.ows.NoApplicableCodeException;
import org.n52.sos.exception.ows.concrete.UnsupportedEncoderInputException;
import org.n52.sos.ogc.gml.AbstractFeature;
import org.n52.sos.ogc.gml.CodeWithAuthority;
import org.n52.sos.ogc.gml.GmlConstants;
import org.n52.sos.ogc.gml.time.Time;
import org.n52.sos.ogc.gml.time.TimeInstant;
import org.n52.sos.ogc.gml.time.TimePeriod;
import org.n52.sos.ogc.om.NamedValue;
import org.n52.sos.ogc.om.ObservationValue;
import org.n52.sos.ogc.om.OmCompositePhenomenon;
import org.n52.sos.ogc.om.OmConstants;
import org.n52.sos.ogc.om.OmObservableProperty;
import org.n52.sos.ogc.om.OmObservation;
import org.n52.sos.ogc.om.SingleObservationValue;
import org.n52.sos.ogc.om.quality.OmResultQuality;
import org.n52.sos.ogc.om.values.BooleanValue;
import org.n52.sos.ogc.om.values.CategoryValue;
import org.n52.sos.ogc.om.values.CountValue;
import org.n52.sos.ogc.om.values.GeometryValue;
import org.n52.sos.ogc.om.values.HrefAttributeValue;
import org.n52.sos.ogc.om.values.QuantityValue;
import org.n52.sos.ogc.om.values.ReferenceValue;
import org.n52.sos.ogc.om.values.TextValue;
import org.n52.sos.ogc.om.values.Value;
import org.n52.sos.ogc.ows.OwsExceptionReport;
import org.n52.sos.ogc.sos.SosConstants;
import org.n52.sos.ogc.sos.SosConstants.HelperValues;
import org.n52.sos.ogc.sos.SosProcedureDescription;
import org.n52.sos.service.Configurator;
import org.n52.sos.service.profile.Profile;
import org.n52.sos.util.CodingHelper;
import org.n52.sos.util.Constants;
import org.n52.sos.util.GmlHelper;
import org.n52.sos.util.JavaHelper;
import org.n52.sos.util.StringHelper;
import org.n52.sos.util.XmlHelper;
import org.n52.sos.util.XmlOptionsHelper;
import org.n52.sos.w3c.W3CConstants;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;

public abstract class AbstractOmEncoderv20 extends AbstractXmlEncoder<Object> implements
        ObservationEncoder<XmlObject, Object>, StreamingEncoder<XmlObject, Object> {

    /**
     * Method to create the om:result element content
     *
     * @param sosObservation
     *            SosObservation to be encoded
     * @return XML encoded result object, e.g a gml:MeasureType
     * @throws OwsExceptionReport
     *             if an error occurs
     */
    protected abstract XmlObject createResult(OmObservation sosObservation) throws OwsExceptionReport;

    protected abstract XmlObject encodeResult(ObservationValue<?> observationValue) throws OwsExceptionReport;

    /**
     * Method to add the observation type to the om:Observation. Subclasses
     * should have mappings to set the correct type, e.g. O&M .../Measurement ==
     * .../MeasurementTimeseriesTVPObservation in WaterML 2.0
     *
     * @param xbObservation
     *            XmlBeans object of observation
     * @param observationType
     *            Observation type
     */
    protected abstract void addObservationType(OMObservationType xbObservation, String observationType);

    /**
     * Get the default encoding Namespace for FeatureOfInterest
     *
     * @return Encoding namespace
     */
    public abstract String getDefaultFeatureEncodingNamespace();

    /**
     * Get the default encoding Namespace for Procedures
     *
     * @return Encoding namespace
     */
    protected abstract String getDefaultProcedureEncodingNamspace();

    /**
     * Indicator whether the procedure is to be encoded
     *
     * @return Indicator
     */
    protected abstract boolean convertEncodedProcedure();

    @Override
    public boolean forceStreaming() {
        return false;
    }

    @Override
    public XmlObject encode(Object element, Map<HelperValues, String> additionalValues) throws OwsExceptionReport,
            UnsupportedEncoderInputException {
        XmlObject encodedObject = null;
        if (element instanceof OmObservation) {
            encodedObject = createOmObservation((OmObservation) element, additionalValues);
        } else if (element instanceof NamedValue) {
            encodedObject = createNamedValue((NamedValue<?>) element);
        } else if (element instanceof AbstractFeature) {
            encodedObject = addFeatureOfInterest((AbstractFeature) element);
        } else if (element instanceof SosProcedureDescription) {
            encodedObject = encodeProcedureDescription((SosProcedureDescription) element);
        } else {
            throw new UnsupportedEncoderInputException(this, element);
        }
        // LOGGER.debug("Encoded object {} is valid: {}",
        // encodedObject.schemaType().toString(),
        // XmlHelper.validateDocument(encodedObject));
        return encodedObject;
    }

    @Override
    public void encode(Object objectToEncode, OutputStream outputStream) throws OwsExceptionReport {
        encode(objectToEncode, outputStream, new EncodingValues());
    }

    @Override
    public void encode(Object objectToEncode, OutputStream outputStream, EncodingValues encodingValues)
            throws OwsExceptionReport {
        try {
            XmlOptions xmlOptions = XmlOptionsHelper.getInstance().getXmlOptions();
            if (encodingValues.isEmbedded()) {
                xmlOptions.setSaveNoXmlDecl();
            }
            // writeIndent(encodingValues.getIndent(), outputStream);
            encode(objectToEncode, encodingValues.getAdditionalValues()).save(outputStream, xmlOptions);
        } catch (IOException ioe) {
            throw new NoApplicableCodeException().causedBy(ioe).withMessage("Error while writing element to stream!");
        } finally {
            if (encodingValues.isEmbedded()) {
                XmlOptionsHelper.getInstance().getXmlOptions().remove(XmlOptions.SAVE_NO_XML_DECL);
            }
        }
    }

    @Override
    public void addNamespacePrefixToMap(Map<String, String> nameSpacePrefixMap) {
        nameSpacePrefixMap.put(OmConstants.NS_OM_2, OmConstants.NS_OM_PREFIX);
    }

    /**
     * Method to create an O&M 2.0 observation XmlBeans object
     *
     * @param sosObservation
     *            SosObservation to be encoded
     * @param additionalValues
     *            Additional values which are used during the encoding
     * @return XmlBeans representation of O&M 2.0 observation
     * @throws OwsExceptionReport
     *             If an error occurs
     */
    protected XmlObject createOmObservation(OmObservation sosObservation, Map<HelperValues, String> additionalValues)
            throws OwsExceptionReport {
        OMObservationType xbObservation =
                OMObservationType.Factory.newInstance(XmlOptionsHelper.getInstance().getXmlOptions());
        if (!sosObservation.isSetObservationID()) {
            sosObservation.setObservationID(JavaHelper.generateID(Double.toString(System.currentTimeMillis()
                    * Math.random())));
        }
        String observationID = sosObservation.getObservationID();
        if (!sosObservation.isSetGmlID()) {
            sosObservation.setGmlId("o_" + observationID);
        }
        // set a unique gml:id
        xbObservation.setId(sosObservation.getGmlId());

        // set observation identifier if available
        if (sosObservation.isSetIdentifier()) {
            Encoder<?, CodeWithAuthority> encoder =
                    CodingRepository.getInstance().getEncoder(
                            CodingHelper.getEncoderKey(GmlConstants.NS_GML_32,
                                    sosObservation.getIdentifierCodeWithAuthority()));
            if (encoder != null) {
                XmlObject xmlObject = (XmlObject) encoder.encode(sosObservation.getIdentifierCodeWithAuthority());
                xbObservation.addNewIdentifier().set(xmlObject);
            } else {
                throw new NoApplicableCodeException()
                        .withMessage("Error while encoding geometry value, needed encoder is missing!");
            }
        }
        // set observation description
        if (sosObservation.isSetDescription()) {
            xbObservation.addNewDescription().setStringValue(sosObservation.getDescription());
        }

        // add observationType if set
        addObservationType(xbObservation, sosObservation.getObservationConstellation().getObservationType());

        // set phenomenonTime
        Time phenomenonTime = sosObservation.getPhenomenonTime();
        if (phenomenonTime.getGmlId() == null) {
            phenomenonTime.setGmlId(OmConstants.PHENOMENON_TIME_NAME + "_" + observationID);
        }
        addPhenomenonTime(xbObservation.addNewPhenomenonTime(), phenomenonTime);

        // set resultTime
        addResultTime(xbObservation, sosObservation);

        // set procedure
        addProcedure(xbObservation.addNewProcedure(), sosObservation.getObservationConstellation().getProcedure(),
                observationID);

        // set parameter
        if (sosObservation.isSetParameter()) {
            addParameter(xbObservation, sosObservation.getParameter());
        }

        // set observedProperty (phenomenon)
        xbObservation.addNewObservedProperty().setHref(
                sosObservation.getObservationConstellation().getObservableProperty().getIdentifier());
        // set name as xlink:title
        if (sosObservation.getObservationConstellation().getObservableProperty().isSetName()
                && sosObservation.getObservationConstellation().getObservableProperty().getFirstName().isSetValue()) {
            xbObservation.getObservedProperty().setTitle(
                    sosObservation.getObservationConstellation().getObservableProperty().getFirstName().getValue());
        }
        List<OmObservableProperty> phenComponents;
        if (sosObservation.getObservationConstellation().getObservableProperty() instanceof OmObservableProperty) {
            phenComponents = new ArrayList<OmObservableProperty>(1);
            phenComponents.add((OmObservableProperty) sosObservation.getObservationConstellation()
                    .getObservableProperty());
        } else if (sosObservation.getObservationConstellation().getObservableProperty() instanceof OmCompositePhenomenon) {
            OmCompositePhenomenon compPhen =
                    (OmCompositePhenomenon) sosObservation.getObservationConstellation().getObservableProperty();
            phenComponents = compPhen.getPhenomenonComponents();
        }
        // set feature
        xbObservation.addNewFeatureOfInterest().set(
                addFeatureOfInterest(sosObservation.getObservationConstellation().getFeatureOfInterest()));

        addResultQualities(xbObservation, sosObservation);

        // set result
        XmlObject createResult = createResult(sosObservation);
        XmlObject addNewResult = xbObservation.addNewResult();
        if (createResult != null) {
            addNewResult.set(createResult);
        }
        if (additionalValues.containsKey(HelperValues.PROPERTY_TYPE)) {
            OMObservationPropertyType observationPropertyType =
                    OMObservationPropertyType.Factory.newInstance(XmlOptionsHelper.getInstance().getXmlOptions());
            observationPropertyType.setOMObservation(xbObservation);
            return observationPropertyType;
        } else if (additionalValues.containsKey(HelperValues.DOCUMENT)) {
            OMObservationDocument observationDoc =
                    OMObservationDocument.Factory.newInstance(XmlOptionsHelper.getInstance().getXmlOptions());
            observationDoc.setOMObservation(xbObservation);
            return observationDoc;
        }
        if (additionalValues.containsKey(HelperValues.DOCUMENT)) {
            OMObservationDocument obsDoc =
                    OMObservationDocument.Factory.newInstance(XmlOptionsHelper.getInstance().getXmlOptions());
            obsDoc.setOMObservation(xbObservation);
            return obsDoc;
        }
        return xbObservation;
    }

    private void addResultQualities(OMObservationType xbObservation, OmObservation sosObservation)
            throws OwsExceptionReport {
        if (sosObservation.isSetResultQuality()) {
            addResultQualities(xbObservation, sosObservation.getResultQuality());
        } else if (sosObservation.getValue() instanceof SingleObservationValue) {
            addResultQualities(xbObservation, ((SingleObservationValue<?>) sosObservation.getValue()).getQualityList());
        }
    }

    private void addResultQualities(OMObservationType xbObservation, Set<OmResultQuality> resultQuality)
            throws OwsExceptionReport {
        for (OmResultQuality quality : resultQuality) {
            AbstractDQElementDocument encodedQuality =
                    (AbstractDQElementDocument) CodingHelper.encodeObjectToXml(null, quality,
                            ImmutableMap.of(HelperValues.DOCUMENT, "true"));
            DQElementPropertyType addNewResultQuality = xbObservation.addNewResultQuality();
            addNewResultQuality.setAbstractDQElement(encodedQuality.getAbstractDQElement());
            XmlHelper.substituteElement(addNewResultQuality.getAbstractDQElement(),
                    encodedQuality.getAbstractDQElement());
        }
    }

    private XmlObject encodeProcedureDescription(SosProcedureDescription procedureDescription)
            throws OwsExceptionReport {
        OMProcessPropertyType procedure = OMProcessPropertyType.Factory.newInstance();
        addProcedure(procedure, procedureDescription, null);
        return procedure;
    }

    /**
     * Method that adds the procedure as reference or as encoded object to the
     * XML observation object
     *
     * @param procedure
     *            XML process type
     * @param procedureDescription
     *            SosProcedureDescription to be encoded
     * @param observationID
     *            GML observation id.
     * @throws OwsExceptionReport
     *             If an error occurs
     */
    private void addProcedure(OMProcessPropertyType procedure, SosProcedureDescription procedureDescription,
            String observationID) throws OwsExceptionReport {
        if (checkEncodProcedureForEncoderKeys()) {
            SosProcedureDescription procedureToEncode = null;
            // should the procedure be converted
            if (convertEncodedProcedure()) {
                Converter<SosProcedureDescription, SosProcedureDescription> converter =
                        ConverterRepository.getInstance().getConverter(procedureDescription.getDescriptionFormat(),
                                getDefaultProcedureEncodingNamspace());
                if (converter != null) {
                    try {
                        procedureToEncode = converter.convert(procedureDescription);
                    } catch (ConverterException e) {
                        throw new NoApplicableCodeException().causedBy(e).withMessage(
                                "Error while converting procedureDescription!");
                    }
                } else {
                    throw new NoApplicableCodeException().withMessage("No converter (%s -> %s) found!",
                            procedureDescription.getDescriptionFormat(), getDefaultProcedureEncodingNamspace());
                }
            } else {
                procedureToEncode = procedureDescription;
            }
            // encode procedure or add reference
            XmlObject encodedProcedure =
                    CodingHelper.encodeObjectToXml(procedureToEncode.getDescriptionFormat(), procedureToEncode);
            if (encodedProcedure != null) {
                procedure.set(encodedProcedure);
            } else {
                procedure.setHref(procedureDescription.getIdentifier());
            }
        } else {
            procedure.setHref(procedureDescription.getIdentifier());
        }
     // set name as xlink:title
        if (procedure.isSetHref() && procedureDescription.isSetName() && procedureDescription.getFirstName().isSetValue()) {
            procedure.setTitle(procedureDescription.getFirstName().getValue());
        }
    }

    /**
     * Method to check whether the procedure should be encoded
     *
     * @return True or false
     */
    private boolean checkEncodProcedureForEncoderKeys() {
        Set<EncoderKey> encoderKeyType = getEncoderKeyType();
        for (EncoderKey encoderKey : encoderKeyType) {
            if (encoderKey instanceof XmlEncoderKey) {
                if (Configurator.getInstance().getProfileHandler().getActiveProfile()
                        .isEncodeProcedureInObservation(((XmlEncoderKey) encoderKey).getNamespace())) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Method to add the phenomenon time to the XML observation object
     *
     * @param timeObjectPropertyType
     *            XML time object from XML observation object
     * @param time
     *            SOS phenomenon time representation
     * @throws OwsExceptionReport
     *             If an error occurs
     */
    private void addPhenomenonTime(TimeObjectPropertyType timeObjectPropertyType, Time time) throws OwsExceptionReport {
        Encoder<?, Time> encoder =
                CodingRepository.getInstance().getEncoder(CodingHelper.getEncoderKey(GmlConstants.NS_GML_32, time));
        if (encoder != null) {
            XmlObject xmlObject = (XmlObject) encoder.encode(time);
            XmlObject substitution =
                    timeObjectPropertyType.addNewAbstractTimeObject().substitute(
                            GmlHelper.getGml321QnameForITime(time), xmlObject.schemaType());
            substitution.set(xmlObject);
        } else {
            throw new NoApplicableCodeException()
                    .withMessage("Error while encoding phenomenon time, needed encoder is missing!");
        }
    }

    /**
     * Method to add the result time to the XML observation object
     *
     * @param xbObs
     *            XML observation object
     * @param sosObservation
     *            SOS observation object
     * @throws OwsExceptionReport
     *             If an error occurs.
     */
    private void addResultTime(OMObservationType xbObs, OmObservation sosObservation) throws OwsExceptionReport {
        TimeInstant resultTime = sosObservation.getResultTime();
        Time phenomenonTime = sosObservation.getPhenomenonTime();
        // get result time from SOS result time representation
        if (sosObservation.getResultTime() != null) {
            if (resultTime.equals(phenomenonTime)) {
                xbObs.addNewResultTime().setHref(Constants.NUMBER_SIGN_STRING + phenomenonTime.getGmlId());
            } else {
                addResultTime(xbObs, resultTime);
            }
        }
        // if result time is not set, get result time from phenomenon time
        // representation
        else {
            if (phenomenonTime instanceof TimeInstant) {
                xbObs.addNewResultTime().setHref(Constants.NUMBER_SIGN_STRING + phenomenonTime.getGmlId());
            } else if (phenomenonTime instanceof TimePeriod) {
                TimeInstant rsTime = new TimeInstant(((TimePeriod) sosObservation.getPhenomenonTime()).getEnd());
                addResultTime(xbObs, rsTime);
            }
        }
    }

    /**
     * Method to add the result time to the XML observation object
     *
     * @param xbObs
     *            XML observation object
     * @param time
     *            SOS result time representation
     * @throws OwsExceptionReport
     *             If an error occurs.
     */
    private void addResultTime(OMObservationType xbObs, TimeInstant time) throws OwsExceptionReport {
        XmlObject xmlObject = CodingHelper.encodeObjectToXml(GmlConstants.NS_GML_32, time);
        xbObs.addNewResultTime().addNewTimeInstant().set(xmlObject);
        XmlObject substitution =
                xbObs.getResultTime().getTimeInstant()
                        .substitute(GmlHelper.getGml321QnameForITime(time), xmlObject.schemaType());
        substitution.set(xmlObject);
    }

    private void addParameter(OMObservationType xbObservation, Collection<NamedValue<?>> parameter)
            throws OwsExceptionReport {
        for (NamedValue<?> namedValue : parameter) {
            xbObservation.addNewParameter().setNamedValue(createNamedValue(namedValue));
        }
    }

    /**
     * Method to add the featureOfInterest to the XML observation object
     *
     * @param feature
     *            SOS feature representation
     * @return Encoded featureOfInterest
     * @throws OwsExceptionReport
     *             If an error occurs.
     */
    private XmlObject addFeatureOfInterest(AbstractFeature feature) throws OwsExceptionReport {
        Map<HelperValues, String> additionalValues =
                new EnumMap<SosConstants.HelperValues, String>(HelperValues.class);
        Profile activeProfile = Configurator.getInstance().getProfileHandler().getActiveProfile();
        additionalValues.put(HelperValues.ENCODE,
                Boolean.toString(activeProfile.isEncodeFeatureOfInterestInObservations()));
        if (StringHelper.isNotEmpty(activeProfile.getEncodingNamespaceForFeatureOfInterest())) {
            additionalValues.put(HelperValues.ENCODE_NAMESPACE,
                    activeProfile.getEncodingNamespaceForFeatureOfInterest());
        } else {
            additionalValues.put(HelperValues.ENCODE_NAMESPACE, getDefaultFeatureEncodingNamespace());
        }
        return CodingHelper.encodeObjectToXml(GmlConstants.NS_GML_32, feature, additionalValues);
    }

    /**
     * Method to encode a SOS NamedValue to an XmlBeans representation
     *
     * @param sosNamedValue
     *            SOS NamedValue
     * @return XmlBeans object
     * @throws OwsExceptionReport
     *             If an error occurs.
     */
    protected NamedValueType createNamedValue(NamedValue<?> sosNamedValue) throws OwsExceptionReport {
        // encode value (any)
        XmlObject namedValuePropertyValue = getNamedValueValue(sosNamedValue.getValue());
        if (namedValuePropertyValue != null) {
            NamedValueType xbNamedValue =
                    NamedValueType.Factory.newInstance(XmlOptionsHelper.getInstance().getXmlOptions());
            // encode gml:ReferenceType
            XmlObject encodeObjectToXml =
                    CodingHelper.encodeObjectToXml(GmlConstants.NS_GML_32, sosNamedValue.getName());
            xbNamedValue.addNewName().set(encodeObjectToXml);
            // set value (any)
            xbNamedValue.setValue(namedValuePropertyValue);
            return xbNamedValue;
        }
        return null;
    }

    /**
     * Get the XmlBeans object for SOS value
     *
     * @param value
     *            SOS value object
     * @return XmlBeans object
     * @throws OwsExceptionReport
     *             If an error occurs.
     */
    private XmlObject getNamedValueValue(Value<?> value) throws OwsExceptionReport {
        Map<SosConstants.HelperValues, String> helperValues = Maps.newHashMap();
        helperValues.put(HelperValues.PROPERTY_TYPE, null);
        helperValues.put(HelperValues.GMLID, JavaHelper.generateID(value.toString()));
        if (value.isSetValue()) {
            if (value instanceof BooleanValue) {
                BooleanValue booleanValue = (BooleanValue) value;
                XmlBoolean xbBoolean = XmlBoolean.Factory.newInstance();
                xbBoolean.setBooleanValue(booleanValue.getValue());
                return xbBoolean;
                // return CodingHelper.encodeObjectToXml(SweConstants.NS_SWE_20,
                // new SweBoolean().setValue(booleanValue.getValue()),
                // helperValues);
            } else if (value instanceof CategoryValue) {
                CategoryValue categoryValue = (CategoryValue) value;
                if (categoryValue.isSetValue()) {
                    XmlString xmlString = XmlString.Factory.newInstance();
                    xmlString.setStringValue(categoryValue.getValue());
                    return xmlString;
                    // return CodingHelper
                    // .encodeObjectToXml(
                    // SweConstants.NS_SWE_20,
                    // new
                    // SweCategory().setValue(categoryValue.getValue()).setCodeSpace(
                    // categoryValue.getUnit()), helperValues);
                }
            } else if (value instanceof CountValue) {
                CountValue countValue = (CountValue) value;
                XmlInteger xmlInteger = XmlInteger.Factory.newInstance();
                xmlInteger.setStringValue(countValue.getValue().toString());
                return xmlInteger;
                // return CodingHelper.encodeObjectToXml(SweConstants.NS_SWE_20,
                // new SweCount().setValue(countValue.getValue()),
                // helperValues);
            } else if (value instanceof GeometryValue) {
                GeometryValue geometryValue = (GeometryValue) value;
                if (geometryValue.getValue() != null) {
                    return CodingHelper.encodeObjectToXml(GmlConstants.NS_GML_32, geometryValue, helperValues);
                }
            } else if (value instanceof QuantityValue) {
                QuantityValue quantityValue = (QuantityValue) value;
                XmlDouble xmlDouble = XmlDouble.Factory.newInstance();
                xmlDouble.setDoubleValue(quantityValue.getValue().doubleValue());
                return xmlDouble;
                // return CodingHelper.encodeObjectToXml(
                // SweConstants.NS_SWE_20,
                // new
                // SweQuantity().setValue(quantityValue.getValue().doubleValue()).setUom(
                // quantityValue.getUnit()), helperValues);
            } else if (value instanceof TextValue) {
                TextValue textValue = (TextValue) value;
                XmlString xmlString = XmlString.Factory.newInstance();
                xmlString.setStringValue(textValue.getValue());
                return xmlString;
                // return CodingHelper.encodeObjectToXml(SweConstants.NS_SWE_20,
                // new SweText().setValue(textValue.getValue()), helperValues);
            } else if (value instanceof ReferenceValue) {
                ReferenceValue referenceValue = (ReferenceValue) value;
                if (referenceValue.isSetValue()) {
                    return CodingHelper.encodeObjectToXml(GmlConstants.NS_GML_32, referenceValue.getValue(),
                            helperValues);
                }
            } else if (value instanceof HrefAttributeValue) {
                HrefAttributeValue hrefAttributeValue = (HrefAttributeValue) value;
                if (hrefAttributeValue.isSetValue()) {
                    return CodingHelper.encodeObjectToXml(W3CConstants.NS_XLINK, hrefAttributeValue.getValue(),
                            helperValues);
                }
            }
        }
        return null;
    }
}
