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

import java.io.OutputStream;
import java.math.BigInteger;
import java.util.Collections;
import java.util.EnumMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.xml.stream.XMLStreamException;

import net.opengis.om.x20.OMObservationType;

import org.apache.xmlbeans.XmlBoolean;
import org.apache.xmlbeans.XmlInteger;
import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.XmlString;
import org.n52.sos.encode.streaming.OmV20XmlStreamWriter;
import org.n52.sos.exception.ows.NoApplicableCodeException;
import org.n52.sos.exception.ows.concrete.UnsupportedEncoderInputException;
import org.n52.sos.ogc.gml.GmlConstants;
import org.n52.sos.ogc.om.AbstractObservationValue;
import org.n52.sos.ogc.om.MultiObservationValues;
import org.n52.sos.ogc.om.NamedValue;
import org.n52.sos.ogc.om.ObservationValue;
import org.n52.sos.ogc.om.OmConstants;
import org.n52.sos.ogc.om.OmObservation;
import org.n52.sos.ogc.om.SingleObservationValue;
import org.n52.sos.ogc.om.features.SfConstants;
import org.n52.sos.ogc.om.values.BooleanValue;
import org.n52.sos.ogc.om.values.CategoryValue;
import org.n52.sos.ogc.om.values.CountValue;
import org.n52.sos.ogc.om.values.GeometryValue;
import org.n52.sos.ogc.om.values.QuantityValue;
import org.n52.sos.ogc.om.values.TextValue;
import org.n52.sos.ogc.ows.OwsExceptionReport;
import org.n52.sos.ogc.sensorML.SensorMLConstants;
import org.n52.sos.ogc.sos.ConformanceClasses;
import org.n52.sos.ogc.sos.Sos2Constants;
import org.n52.sos.ogc.sos.SosConstants;
import org.n52.sos.ogc.sos.SosConstants.HelperValues;
import org.n52.sos.ogc.swe.SweConstants;
import org.n52.sos.ogc.swe.SweDataArray;
import org.n52.sos.service.ServiceConstants.SupportedTypeKey;
import org.n52.sos.util.CodingHelper;
import org.n52.sos.util.OMHelper;
import org.n52.sos.util.StringHelper;
import org.n52.sos.util.SweHelper;
import org.n52.sos.util.XmlOptionsHelper;
import org.n52.sos.util.http.MediaType;
import org.n52.sos.w3c.SchemaLocation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Joiner;
import com.google.common.collect.Sets;

/**
 * @since 4.0.0
 * 
 */
public class OmEncoderv20 extends AbstractOmEncoderv20 {

    /**
     * logger, used for logging while initializing the constants from config
     * file
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(OmEncoderv20.class);
    
    private static final Set<EncoderKey> ENCODER_KEYS = CodingHelper.encoderKeysForElements(OmConstants.NS_OM_2,
            OmObservation.class, NamedValue.class, SingleObservationValue.class, MultiObservationValues.class);

    // TODO: change to correct conformance class
    private static final Set<String> CONFORMANCE_CLASSES = Sets.newHashSet(ConformanceClasses.OM_V2_MEASUREMENT,
            ConformanceClasses.OM_V2_CATEGORY_OBSERVATION, ConformanceClasses.OM_V2_COUNT_OBSERVATION,
            ConformanceClasses.OM_V2_TRUTH_OBSERVATION, ConformanceClasses.OM_V2_GEOMETRY_OBSERVATION,
            ConformanceClasses.OM_V2_TEXT_OBSERVATION);

    private static final Map<SupportedTypeKey, Set<String>> SUPPORTED_TYPES = Collections.singletonMap(
            SupportedTypeKey.ObservationType, (Set<String>) Sets.newHashSet(OmConstants.OBS_TYPE_CATEGORY_OBSERVATION,
                    OmConstants.OBS_TYPE_COUNT_OBSERVATION, OmConstants.OBS_TYPE_GEOMETRY_OBSERVATION,
                    OmConstants.OBS_TYPE_MEASUREMENT, OmConstants.OBS_TYPE_TEXT_OBSERVATION,
                    OmConstants.OBS_TYPE_TRUTH_OBSERVATION, OmConstants.OBS_TYPE_SWE_ARRAY_OBSERVATION));

    private static final Map<String, Map<String, Set<String>>> SUPPORTED_RESPONSE_FORMATS = Collections.singletonMap(
            SosConstants.SOS,
            Collections.singletonMap(Sos2Constants.SERVICEVERSION, Collections.singleton(OmConstants.NS_OM_2)));

    public OmEncoderv20() {
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
    public boolean isObservationAndMeasurmentV20Type() {
        return true;
    }

    @Override
    public Set<String> getSupportedResponseFormats(String service, String version) {
        if (SUPPORTED_RESPONSE_FORMATS.get(service) != null
                && SUPPORTED_RESPONSE_FORMATS.get(service).get(version) != null) {
            return SUPPORTED_RESPONSE_FORMATS.get(service).get(version);
        }
        return new HashSet<String>(0);
    }

    @Override
    public boolean shouldObservationsWithSameXBeMerged() {
        return false;
    }

    @Override
    public boolean supportsResultStreamingForMergedValues() {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public MediaType getContentType() {
        return OmConstants.CONTENT_TYPE_OM_2;
    }

    @Override
    public Set<SchemaLocation> getSchemaLocations() {
        return Sets.newHashSet(OmConstants.OM_20_SCHEMA_LOCATION);
    }
    
    @Override
    public XmlObject encode(Object element, Map<HelperValues, String> additionalValues) throws OwsExceptionReport,
            UnsupportedEncoderInputException {
        XmlObject encodedObject = null;
        if (element instanceof ObservationValue) {
            encodedObject = encodeResult((ObservationValue<?>)element);
        } else {
            encodedObject = super.encode(element, additionalValues);
        }
        return encodedObject;
    }
    
    @Override
    public void encode(Object objectToEncode, OutputStream outputStream, EncodingValues encodingValues)
            throws OwsExceptionReport {
        encodingValues.setEncoder(this);
        if (objectToEncode instanceof OmObservation) {
            try {
                new OmV20XmlStreamWriter().write((OmObservation)objectToEncode, outputStream, encodingValues);
            } catch (XMLStreamException xmlse) {
                throw new NoApplicableCodeException().causedBy(xmlse).withMessage("Error while writing element to stream!");
            }
        } else {
            super.encode(objectToEncode, outputStream, encodingValues);
        }
    }

    @Override
    protected XmlObject createResult(OmObservation sosObservation) throws OwsExceptionReport {
        // TODO if OM_SWEArrayObservation and get ResultEncoding and
        // ResultStructure exists,
        if (sosObservation.getValue() instanceof AbstractObservationValue) {
            ((AbstractObservationValue<?>)sosObservation.getValue()).setValuesForResultEncoding(sosObservation);
            return encodeResult(sosObservation.getValue());
        } else {
            if (sosObservation.getValue() instanceof SingleObservationValue) {
                return createSingleObservationToResult(sosObservation);
            } else if (sosObservation.getValue() instanceof MultiObservationValues) {
                return createMultiObservationValueToResult(sosObservation);
            }
        }
        return null;
    }
    
    

    @Override
    protected XmlObject encodeResult(ObservationValue<?> observationValue) throws OwsExceptionReport {
        if (observationValue instanceof SingleObservationValue) {
            return createSingleObservationToResult((SingleObservationValue<?>)observationValue);
        } else if (observationValue instanceof MultiObservationValues) {
            return createMultiObservationValueToResult((MultiObservationValues<?>)observationValue);
        }
        return null;
    }

    @Override
    protected void addObservationType(OMObservationType xbObservation, String observationType) {
        if (StringHelper.isNotEmpty(observationType)) {
            xbObservation.addNewType().setHref(observationType);
        }
    }

    @Override
    public String getDefaultFeatureEncodingNamespace() {
        return SfConstants.NS_SAMS;
    }

    @Override
    protected String getDefaultProcedureEncodingNamspace() {
        return SensorMLConstants.NS_SML;
    }

    @Override
    protected boolean convertEncodedProcedure() {
        return false;
    }

    // FIXME String.equals(QName)!?
    /**
     * Create a om:result content XmlBeans object from a SOS single value
     * observation object
     * 
     * @param sosObservation
     *            SOS observation
     * @return XmlBeans object for om:result
     * @throws OwsExceptionReport
     *             If an error occurs
     */
    @Deprecated
    private XmlObject createSingleObservationToResult(OmObservation sosObservation) throws OwsExceptionReport {
        SingleObservationValue<?> observationValue = (SingleObservationValue<?>) sosObservation.getValue();
        final String observationType;
        if (sosObservation.getObservationConstellation().isSetObservationType()) {
            observationType = sosObservation.getObservationConstellation().getObservationType();
        } else {
            observationType = OMHelper.getObservationTypeFor(observationValue.getValue());
        }
        if ((observationType.equals(OmConstants.OBS_TYPE_MEASUREMENT))
                && observationValue.getValue() instanceof QuantityValue) {
            QuantityValue quantityValue = (QuantityValue) observationValue.getValue();
            return CodingHelper.encodeObjectToXml(GmlConstants.NS_GML_32, quantityValue);
        } else if ((observationType.equals(OmConstants.OBS_TYPE_COUNT_OBSERVATION))
                && observationValue.getValue() instanceof CountValue) {
            CountValue countValue = (CountValue) observationValue.getValue();
            XmlInteger xbInteger = XmlInteger.Factory.newInstance(XmlOptionsHelper.getInstance().getXmlOptions());
            if (countValue.getValue() != null && countValue.getValue() != Integer.MIN_VALUE) {
                xbInteger.setBigIntegerValue(new BigInteger(countValue.getValue().toString()));
            } else {
                xbInteger.setNil();
            }
            return xbInteger;
        } else if ((observationType.equals(OmConstants.OBS_TYPE_TEXT_OBSERVATION))
                && observationValue.getValue() instanceof TextValue) {
            TextValue textValue = (TextValue) observationValue.getValue();
            XmlString xbString = XmlString.Factory.newInstance(XmlOptionsHelper.getInstance().getXmlOptions());
            if (textValue.getValue() != null && !textValue.getValue().isEmpty()) {
                xbString.setStringValue(textValue.getValue());
            } else {
                xbString.setNil();
            }
            return xbString;
        } else if ((observationType.equals(OmConstants.OBS_TYPE_TRUTH_OBSERVATION))
                && observationValue.getValue() instanceof BooleanValue) {
            BooleanValue booleanValue = (BooleanValue) observationValue.getValue();
            XmlBoolean xbBoolean = XmlBoolean.Factory.newInstance(XmlOptionsHelper.getInstance().getXmlOptions());
            if (booleanValue.getValue() != null) {
                xbBoolean.setBooleanValue(booleanValue.getValue());
            } else {
                xbBoolean.setNil();
            }
            return xbBoolean;
        } else if ((observationType.equals(OmConstants.OBS_TYPE_CATEGORY_OBSERVATION))
                && observationValue.getValue() instanceof CategoryValue) {
            CategoryValue categoryValue = (CategoryValue) observationValue.getValue();
            if (categoryValue.getValue() != null && !categoryValue.getValue().isEmpty()) {
                Map<HelperValues, String> additionalValue = new EnumMap<HelperValues, String>(HelperValues.class);
                additionalValue
                        .put(HelperValues.GMLID, SosConstants.OBS_ID_PREFIX + sosObservation.getObservationID());
                XmlObject xmlObject =
                        CodingHelper.encodeObjectToXml(GmlConstants.NS_GML_32, categoryValue, additionalValue);
                return xmlObject;
            } else {
                return null;
            }
        } else if ((observationType.equals(OmConstants.OBS_TYPE_GEOMETRY_OBSERVATION))
                && observationValue.getValue() instanceof GeometryValue) {

            GeometryValue geometryValue = (GeometryValue) observationValue.getValue();
            if (geometryValue.getValue() != null) {
                Map<HelperValues, String> additionalValue = new EnumMap<HelperValues, String>(HelperValues.class);
                additionalValue
                        .put(HelperValues.GMLID, SosConstants.OBS_ID_PREFIX + sosObservation.getObservationID());
                additionalValue.put(HelperValues.PROPERTY_TYPE, null);
                XmlObject xmlObject =
                        CodingHelper.encodeObjectToXml(GmlConstants.NS_GML_32, geometryValue.getValue(),
                                additionalValue);
                return xmlObject;
            } else {
                return null;
            }
        } else if (observationType.equals(OmConstants.OBS_TYPE_SWE_ARRAY_OBSERVATION)) {
            // TODO create SosSweDataArray
            SweDataArray dataArray = SweHelper.createSosSweDataArray(sosObservation);
            Map<HelperValues, String> additionalValues =
                    new EnumMap<SosConstants.HelperValues, String>(SosConstants.HelperValues.class);
            additionalValues.put(HelperValues.FOR_OBSERVATION, null);
            // TODO create SosSweDataArray
            Object encodedObj = CodingHelper.encodeObjectToXml(SweConstants.NS_SWE_20, dataArray, additionalValues);
            if (encodedObj instanceof XmlObject) {
                return (XmlObject) encodedObj;
            } else {
                throw new NoApplicableCodeException().withMessage(
                        "Encoding of observation value of type \"%s\" failed. Result: %s",
                        observationValue.getValue() != null ? observationValue.getValue().getClass().getName()
                                : observationValue.getValue(), encodedObj != null ? encodedObj.getClass().getName()
                                : encodedObj);
            }
        }
        return null;
    }

    /**
     * Create a om:result content XmlBeans object from a SOS multi value
     * observation object
     * 
     * @param sosObservation
     *            SOS observation
     * @return XmlBeans object for om:result
     * @throws OwsExceptionReport
     *             If an error occurs
     */
    @Deprecated
    private XmlObject createMultiObservationValueToResult(OmObservation sosObservation) throws OwsExceptionReport {
        MultiObservationValues<?> observationValue = (MultiObservationValues<?>) sosObservation.getValue();
        // TODO create SosSweDataArray
        SweDataArray dataArray = SweHelper.createSosSweDataArray(sosObservation);
        Map<HelperValues, String> additionalValues =
                new EnumMap<SosConstants.HelperValues, String>(SosConstants.HelperValues.class);
        additionalValues.put(HelperValues.FOR_OBSERVATION, null);
        Object encodedObj = CodingHelper.encodeObjectToXml(SweConstants.NS_SWE_20, dataArray, additionalValues);
        if (encodedObj instanceof XmlObject) {
            return (XmlObject) encodedObj;
        } else {
            throw new NoApplicableCodeException().withMessage(
                    "Encoding of observation value of type \"%s\" failed. Result: %s",
                    observationValue.getValue() != null ? observationValue.getValue().getClass().getName()
                            : observationValue.getValue(), encodedObj != null ? encodedObj.getClass().getName()
                            : encodedObj);
        }
    }
    
    private XmlObject createSingleObservationToResult(SingleObservationValue<?> observationValue) throws OwsExceptionReport {
        final String observationType;
        if (observationValue.isSetObservationType()) {
            observationType = observationValue.getObservationType();
        } else {
            observationType = OMHelper.getObservationTypeFor(observationValue.getValue());
        }
        if ((observationType.equals(OmConstants.OBS_TYPE_MEASUREMENT))
                && observationValue.getValue() instanceof QuantityValue) {
            QuantityValue quantityValue = (QuantityValue) observationValue.getValue();
            return CodingHelper.encodeObjectToXml(GmlConstants.NS_GML_32, quantityValue);
        } else if ((observationType.equals(OmConstants.OBS_TYPE_COUNT_OBSERVATION))
                && observationValue.getValue() instanceof CountValue) {
            CountValue countValue = (CountValue) observationValue.getValue();
            XmlInteger xbInteger = XmlInteger.Factory.newInstance(XmlOptionsHelper.getInstance().getXmlOptions());
            if (countValue.getValue() != null && countValue.getValue() != Integer.MIN_VALUE) {
                xbInteger.setBigIntegerValue(new BigInteger(countValue.getValue().toString()));
            } else {
                xbInteger.setNil();
            }
            return xbInteger;
        } else if ((observationType.equals(OmConstants.OBS_TYPE_TEXT_OBSERVATION))
                && observationValue.getValue() instanceof TextValue) {
            TextValue textValue = (TextValue) observationValue.getValue();
            XmlString xbString = XmlString.Factory.newInstance(XmlOptionsHelper.getInstance().getXmlOptions());
            if (textValue.getValue() != null && !textValue.getValue().isEmpty()) {
                xbString.setStringValue(textValue.getValue());
            } else {
                xbString.setNil();
            }
            return xbString;
        } else if ((observationType.equals(OmConstants.OBS_TYPE_TRUTH_OBSERVATION))
                && observationValue.getValue() instanceof BooleanValue) {
            BooleanValue booleanValue = (BooleanValue) observationValue.getValue();
            XmlBoolean xbBoolean = XmlBoolean.Factory.newInstance(XmlOptionsHelper.getInstance().getXmlOptions());
            if (booleanValue.getValue() != null) {
                xbBoolean.setBooleanValue(booleanValue.getValue());
            } else {
                xbBoolean.setNil();
            }
            return xbBoolean;
        } else if ((observationType.equals(OmConstants.OBS_TYPE_CATEGORY_OBSERVATION))
                && observationValue.getValue() instanceof CategoryValue) {
            CategoryValue categoryValue = (CategoryValue) observationValue.getValue();
            if (categoryValue.getValue() != null && !categoryValue.getValue().isEmpty()) {
                Map<HelperValues, String> additionalValue = new EnumMap<HelperValues, String>(HelperValues.class);
                additionalValue
                        .put(HelperValues.GMLID, SosConstants.OBS_ID_PREFIX + observationValue.getObservationID());
                XmlObject xmlObject =
                        CodingHelper.encodeObjectToXml(GmlConstants.NS_GML_32, categoryValue, additionalValue);
                return xmlObject;
            } else {
                return null;
            }
        } else if ((observationType.equals(OmConstants.OBS_TYPE_GEOMETRY_OBSERVATION))
                && observationValue.getValue() instanceof GeometryValue) {

            GeometryValue geometryValue = (GeometryValue) observationValue.getValue();
            if (geometryValue.getValue() != null) {
                Map<HelperValues, String> additionalValue = new EnumMap<HelperValues, String>(HelperValues.class);
                additionalValue
                        .put(HelperValues.GMLID, SosConstants.OBS_ID_PREFIX + observationValue.getObservationID());
                additionalValue.put(HelperValues.PROPERTY_TYPE, null);
                XmlObject xmlObject =
                        CodingHelper.encodeObjectToXml(GmlConstants.NS_GML_32, geometryValue.getValue(),
                                additionalValue);
                return xmlObject;
            } else {
                return null;
            }
        } else if (observationType.equals(OmConstants.OBS_TYPE_SWE_ARRAY_OBSERVATION)) {
            // TODO create SosSweDataArray
            SweDataArray dataArray = SweHelper.createSosSweDataArray(observationValue);
            Map<HelperValues, String> additionalValues =
                    new EnumMap<SosConstants.HelperValues, String>(SosConstants.HelperValues.class);
            additionalValues.put(HelperValues.FOR_OBSERVATION, null);
            // TODO create SosSweDataArray
            Object encodedObj = CodingHelper.encodeObjectToXml(SweConstants.NS_SWE_20, dataArray, additionalValues);
            if (encodedObj instanceof XmlObject) {
                return (XmlObject) encodedObj;
            } else {
                throw new NoApplicableCodeException().withMessage(
                        "Encoding of observation value of type \"%s\" failed. Result: %s",
                        observationValue.getValue() != null ? observationValue.getValue().getClass().getName()
                                : observationValue.getValue(), encodedObj != null ? encodedObj.getClass().getName()
                                : encodedObj);
            }
        }
        return null;
    }

    private XmlObject createMultiObservationValueToResult(MultiObservationValues<?> observationValue) throws OwsExceptionReport {
        // TODO create SosSweDataArray
        SweDataArray dataArray = SweHelper.createSosSweDataArray(observationValue);
        Map<HelperValues, String> additionalValues =
                new EnumMap<SosConstants.HelperValues, String>(SosConstants.HelperValues.class);
        additionalValues.put(HelperValues.FOR_OBSERVATION, null);
        Object encodedObj = CodingHelper.encodeObjectToXml(SweConstants.NS_SWE_20, dataArray, additionalValues);
        if (encodedObj instanceof XmlObject) {
            return (XmlObject) encodedObj;
        } else {
            throw new NoApplicableCodeException().withMessage(
                    "Encoding of observation value of type \"%s\" failed. Result: %s",
                    observationValue.getValue() != null ? observationValue.getValue().getClass().getName()
                            : observationValue.getValue(), encodedObj != null ? encodedObj.getClass().getName()
                            : encodedObj);
        }
    }

}
