/**
 * Copyright (C) 2012-2020 52°North Initiative for Geospatial Open Source
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

import org.apache.xmlbeans.XmlBoolean;
import org.apache.xmlbeans.XmlInteger;
import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.XmlString;
import org.n52.sos.encode.streaming.OmV20XmlStreamWriter;
import org.n52.sos.exception.ows.NoApplicableCodeException;
import org.n52.sos.exception.ows.concrete.UnsupportedEncoderInputException;
import org.n52.sos.ogc.om.AbstractObservationValue;
import org.n52.sos.ogc.om.MultiObservationValues;
import org.n52.sos.ogc.om.NamedValue;
import org.n52.sos.ogc.om.ObservationValue;
import org.n52.sos.ogc.om.OmConstants;
import org.n52.sos.ogc.om.OmObservation;
import org.n52.sos.ogc.om.SingleObservationValue;
import org.n52.sos.ogc.om.values.BooleanValue;
import org.n52.sos.ogc.om.values.CategoryValue;
import org.n52.sos.ogc.om.values.ComplexValue;
import org.n52.sos.ogc.om.values.CountValue;
import org.n52.sos.ogc.om.values.CvDiscretePointCoverage;
import org.n52.sos.ogc.om.values.GeometryValue;
import org.n52.sos.ogc.om.values.HrefAttributeValue;
import org.n52.sos.ogc.om.values.MultiPointCoverage;
import org.n52.sos.ogc.om.values.NilTemplateValue;
import org.n52.sos.ogc.om.values.ProfileValue;
import org.n52.sos.ogc.om.values.QuantityRangeValue;
import org.n52.sos.ogc.om.values.QuantityValue;
import org.n52.sos.ogc.om.values.RectifiedGridCoverage;
import org.n52.sos.ogc.om.values.ReferenceValue;
import org.n52.sos.ogc.om.values.SweDataArrayValue;
import org.n52.sos.ogc.om.values.TLVTValue;
import org.n52.sos.ogc.om.values.TVPValue;
import org.n52.sos.ogc.om.values.TextValue;
import org.n52.sos.ogc.om.values.UnknownValue;
import org.n52.sos.ogc.om.values.XmlValue;
import org.n52.sos.ogc.om.values.visitor.ValueVisitor;
import org.n52.sos.ogc.ows.OwsExceptionReport;
import org.n52.sos.ogc.sensorML.SensorMLConstants;
import org.n52.sos.ogc.series.wml.WaterMLConstants;
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
import org.n52.sos.util.http.MediaType;
import org.n52.sos.w3c.SchemaLocation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Joiner;
import com.google.common.collect.Sets;

import net.opengis.om.x20.OMObservationType;

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
            ConformanceClasses.OM_V2_TEXT_OBSERVATION, ConformanceClasses.OM_V2_COMPLEX_OBSERVATION);

    private static final Map<SupportedTypeKey, Set<String>> SUPPORTED_TYPES = Collections.singletonMap(
            SupportedTypeKey.ObservationType, (Set<String>) Sets.newHashSet(OmConstants.OBS_TYPE_CATEGORY_OBSERVATION,
                    OmConstants.OBS_TYPE_COUNT_OBSERVATION, OmConstants.OBS_TYPE_GEOMETRY_OBSERVATION,
                    OmConstants.OBS_TYPE_MEASUREMENT, OmConstants.OBS_TYPE_TEXT_OBSERVATION,
                    OmConstants.OBS_TYPE_TRUTH_OBSERVATION, OmConstants.OBS_TYPE_SWE_ARRAY_OBSERVATION,
                    OmConstants.OBS_TYPE_COMPLEX_OBSERVATION, OmConstants.OBS_TYPE_REFERENCE_OBSERVATION));

    private static final Map<String, Map<String, Set<String>>> SUPPORTED_RESPONSE_FORMATS = Collections.singletonMap(
            SosConstants.SOS,
            Collections.singletonMap(Sos2Constants.SERVICEVERSION, Collections.singleton(OmConstants.NS_OM_2)));
    private final SweHelper helper = new SweHelper();
    
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
    public Map<String, Set<String>> getSupportedResponseFormatObservationTypes() {
        return Collections.singletonMap(OmConstants.NS_OM_2, getSupportedTypes().get(SupportedTypeKey.ObservationType));
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
        return new HashSet<>(0);
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
        if (element instanceof ObservationValue) {
            return encodeResult((ObservationValue<?>)element);
        }
        return super.encode(element, additionalValues);
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
    
    protected OMObservationType createOmObservationType() {
        return OMObservationType.Factory.newInstance(getXmlOptions());
    }

    @Override
    protected XmlObject createResult(OmObservation sosObservation) throws OwsExceptionReport {
        ObservationValue<?> value = sosObservation.getValue();
        // TODO if OM_SWEArrayObservation and get ResultEncoding and
        // ResultStructure exists,
        if (value instanceof AbstractObservationValue) {
            AbstractObservationValue<?> abstractObservationValue
                    = (AbstractObservationValue<?>) value;
            abstractObservationValue.setValuesForResultEncoding(sosObservation);
            return encodeResult(abstractObservationValue);
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
        return null;
    }

    @Override
    protected String getDefaultProcedureEncodingNamspace() {
        return SensorMLConstants.NS_SML;
    }

    @Override
    protected boolean convertEncodedProcedure() {
        return false;
    }
    
    @Override
    protected void addAddtitionalInformation(OMObservationType omot, OmObservation observation) throws OwsExceptionReport {
        // do nothing
    }

    private XmlObject createSingleObservationToResult(final SingleObservationValue<?> observationValue) throws OwsExceptionReport {
        final String observationType;
        if (observationValue.isSetObservationType()) {
            observationType = observationValue.getObservationType();
        } else {
            observationType = OMHelper.getObservationTypeFor(observationValue.getValue());
        }
        if (observationType.equals(OmConstants.OBS_TYPE_SWE_ARRAY_OBSERVATION)) {
            SweDataArray dataArray = helper.createSosSweDataArray(observationValue);
            Map<HelperValues, String> additionalValues = new EnumMap<>(SosConstants.HelperValues.class);
            additionalValues.put(HelperValues.FOR_OBSERVATION, null);
            return encodeSWE(dataArray, additionalValues);
        }

        return observationValue.getValue().accept(new ResultValueVisitor(observationType, observationValue.getObservationID()));
    }

    private XmlObject createMultiObservationValueToResult(MultiObservationValues<?> observationValue) throws OwsExceptionReport {
        // TODO create SosSweDataArray
        SweDataArray dataArray = helper.createSosSweDataArray(observationValue);
        Map<HelperValues, String> additionalValues = new EnumMap<>(SosConstants.HelperValues.class);
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
    
    protected static XmlObject encodeSWE(Object o) throws OwsExceptionReport {
        return CodingHelper.encodeObjectToXml(SweConstants.NS_SWE_20, o);
    }
    protected static XmlObject encodeSWE(Object o, Map<HelperValues, String> additionalValues) throws OwsExceptionReport {
        return CodingHelper.encodeObjectToXml(SweConstants.NS_SWE_20, o, additionalValues);
    }
    protected static XmlString createXmlString() {
        return XmlString.Factory.newInstance(getXmlOptions());
    }
    protected static XmlInteger createXmlInteger() {
        return XmlInteger.Factory.newInstance(getXmlOptions());
    }
    protected static XmlBoolean createXmlBoolean() {
        return XmlBoolean.Factory.newInstance(getXmlOptions());
    }

    private static class ResultValueVisitor implements ValueVisitor<XmlObject> {
        private final String observationType;
        private final String observationId;

        ResultValueVisitor(String observationType,
                           String observationId) {
            this.observationType = observationType;
            this.observationId = observationId;
        }

        @Override
        public XmlObject visit(BooleanValue value)
                throws OwsExceptionReport {
            if (observationType.equals(OmConstants.OBS_TYPE_TRUTH_OBSERVATION)){
                XmlBoolean xbBoolean = createXmlBoolean();
                if (value.isSetValue()) {
                    xbBoolean.setBooleanValue(value.getValue());
                } else {
                    xbBoolean.setNil();
                }
                return xbBoolean;
            } else  {
                return null;
            }
        }

        @Override
        public XmlObject visit(CategoryValue value)
                throws OwsExceptionReport {
            if (observationType.equals(OmConstants.OBS_TYPE_CATEGORY_OBSERVATION)){
                if (value.isSetValue() && !value.getValue().isEmpty()) {
                    return encodeGML(value);
                }
            }
            return null;
        }

        @Override
        public XmlObject visit(ComplexValue value)
                throws OwsExceptionReport {

            if (observationType.equals(OmConstants.OBS_TYPE_COMPLEX_OBSERVATION)) {
                if (value.isSetValue()) {
                    Map<HelperValues, String> additionalValue = new EnumMap<>(HelperValues.class);
                    additionalValue.put(HelperValues.FOR_OBSERVATION, null);
                    return encodeSWE(value.getValue(), additionalValue);
                }
            }
            return null;
        }

        @Override
        public XmlObject visit(CountValue value)
                throws OwsExceptionReport {
            if (observationType.equals(OmConstants.OBS_TYPE_COUNT_OBSERVATION)) {
                XmlInteger xbInteger = createXmlInteger();
                if (value.isSetValue() && value.getValue() != Integer.MIN_VALUE) {
                    xbInteger.setBigIntegerValue(new BigInteger(value.getValue().toString()));
                } else {
                    xbInteger.setNil();
                }
                return xbInteger;
            } else {
                return null;
            }
        }

        @Override
        public XmlObject visit(GeometryValue value)
                throws OwsExceptionReport {
            if (observationType.equals(OmConstants.OBS_TYPE_GEOMETRY_OBSERVATION)) {
                if (value.isSetValue()) {
                    Map<HelperValues, String> additionalValue = new EnumMap<>(HelperValues.class);
                    additionalValue.put(HelperValues.GMLID, SosConstants.OBS_ID_PREFIX + this.observationId);
                    additionalValue.put(HelperValues.PROPERTY_TYPE, null);
                    return encodeGML(value.getValue(), additionalValue);
                }
            }
                return null;
        }

        @Override
        public XmlObject visit(HrefAttributeValue value)
                throws OwsExceptionReport {
            return null;
        }

        @Override
        public XmlObject visit(NilTemplateValue value)
                throws OwsExceptionReport {
            return null;
        }

        @Override
        public XmlObject visit(QuantityValue value)
                throws OwsExceptionReport {
            if (observationType.equals(OmConstants.OBS_TYPE_MEASUREMENT)
                    || observationType.equals(WaterMLConstants.OBSERVATION_TYPE_MEASURMENT_TVP)
                    || observationType.equals(WaterMLConstants.OBSERVATION_TYPE_MEASURMENT_TDR)) {
                if (value.isSetValue()) {
                    return encodeGML(value);
                }
            }
            return null;
        }

        @Override
        public XmlObject visit(ReferenceValue value)
                throws OwsExceptionReport {
            if (value.isSetValue()) {
                return encodeGML(value.getValue());
            }
            return null;
        }

        @Override
        public XmlObject visit(SweDataArrayValue value)
                throws OwsExceptionReport {
            Map<HelperValues, String> additionalValues = new EnumMap<>(HelperValues.class);
            additionalValues.put(HelperValues.FOR_OBSERVATION, null);
            return encodeSWE(value.getValue(), additionalValues);
        }

        @Override
        public XmlObject visit(TVPValue value)
                throws OwsExceptionReport {
            return null;
        }
        
        @Override
        public XmlObject visit(TLVTValue value)
                throws OwsExceptionReport {
            return null;
        }

        @Override
        public XmlObject visit(TextValue value)
                throws OwsExceptionReport {
            if (observationType.equals(OmConstants.OBS_TYPE_TEXT_OBSERVATION)) {
                XmlString xbString = createXmlString();
                if (value.isSetValue()) {
                    xbString.setStringValue(value.getValue());
                } else {
                    xbString.setNil();
                }
                return xbString;
            } else {
                return null;
            }
        }

        @Override
        public XmlObject visit(UnknownValue value)
                throws OwsExceptionReport {
            return null;
        }

        @Override
        public XmlObject visit(XmlValue value)
                throws OwsExceptionReport {
            return null;
        }

        @Override
        public XmlObject visit(CvDiscretePointCoverage value) throws OwsExceptionReport {
            return null;
        }

        @Override
        public XmlObject visit(MultiPointCoverage value) throws OwsExceptionReport {
            return null;
        }

        @Override
        public XmlObject visit(RectifiedGridCoverage value) throws OwsExceptionReport {
            return null;
        }

        @Override
        public XmlObject visit(ProfileValue value) throws OwsExceptionReport {
            return  CodingHelper.encodeObjectToXmlPropertyType(value.getDefaultElementEncoding(), value);
        }

        @Override
        public XmlObject visit(QuantityRangeValue value) throws OwsExceptionReport {
            return null;
        }

    }
}
