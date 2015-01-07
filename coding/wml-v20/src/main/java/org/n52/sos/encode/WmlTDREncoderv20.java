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
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.xml.stream.XMLStreamException;

import net.opengis.gml.x32.MeasureOrNilReasonListType;
import net.opengis.gml.x32.QuantityListDocument;
import net.opengis.om.x20.OMObservationType;
import net.opengis.watermlDr.x20.MeasurementTimeseriesCoverageType;
import net.opengis.watermlDr.x20.MeasurementTimeseriesDomainRangeDocument;
import net.opengis.watermlDr.x20.TimePositionListDocument;
import net.opengis.watermlDr.x20.TimePositionListType;

import org.apache.xmlbeans.XmlObject;
import org.n52.sos.encode.streaming.WmlTDREncoderv20XmlStreamWriter;
import org.n52.sos.exception.ows.NoApplicableCodeException;
import org.n52.sos.exception.ows.concrete.UnsupportedEncoderInputException;
import org.n52.sos.ogc.OGCConstants;
import org.n52.sos.ogc.gml.AbstractFeature;
import org.n52.sos.ogc.gmlcov.GmlCoverageConstants;
import org.n52.sos.ogc.om.AbstractObservationValue;
import org.n52.sos.ogc.om.AbstractPhenomenon;
import org.n52.sos.ogc.om.MultiObservationValues;
import org.n52.sos.ogc.om.ObservationValue;
import org.n52.sos.ogc.om.OmConstants;
import org.n52.sos.ogc.om.OmObservableProperty;
import org.n52.sos.ogc.om.OmObservation;
import org.n52.sos.ogc.om.SingleObservationValue;
import org.n52.sos.ogc.om.TimeValuePair;
import org.n52.sos.ogc.om.values.CountValue;
import org.n52.sos.ogc.om.values.QuantityValue;
import org.n52.sos.ogc.om.values.TVPValue;
import org.n52.sos.ogc.ows.OwsExceptionReport;
import org.n52.sos.ogc.sos.Sos2Constants;
import org.n52.sos.ogc.sos.SosConstants;
import org.n52.sos.ogc.sos.SosConstants.HelperValues;
import org.n52.sos.ogc.swe.SweConstants;
import org.n52.sos.ogc.swe.SweDataRecord;
import org.n52.sos.ogc.swe.SweField;
import org.n52.sos.ogc.swe.simpleType.SweQuantity;
import org.n52.sos.ogc.wml.WaterMLConstants;
import org.n52.sos.response.GetObservationResponse;
import org.n52.sos.service.ServiceConstants.SupportedTypeKey;
import org.n52.sos.util.CodingHelper;
import org.n52.sos.util.CollectionHelper;
import org.n52.sos.util.StringHelper;
import org.n52.sos.w3c.SchemaLocation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Joiner;
import com.google.common.base.Strings;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

/**
 * Encoder class for WaterML 2.0 TimeseriesDomainRange (TDR)
 * 
 * @author Carsten Hollmann <c.hollmann@52north.org>
 * @since 4.0.0
 * 
 */
public class WmlTDREncoderv20 extends AbstractWmlEncoderv20 {

    private static final Logger LOGGER = LoggerFactory.getLogger(WmlTDREncoderv20.class);

    // TODO: change to correct conformance class
    private static final Set<String> CONFORMANCE_CLASSES = ImmutableSet.of();

    private static final Set<EncoderKey> ENCODER_KEYS = createEncoderKeys();

    private static final Map<SupportedTypeKey, Set<String>> SUPPORTED_TYPES = Collections.singletonMap(
            SupportedTypeKey.ObservationType, Collections.singleton(WaterMLConstants.OBSERVATION_TYPE_MEASURMENT_TDR));;

    private static final Map<String, Map<String, Set<String>>> SUPPORTED_RESPONSE_FORMATS = Collections.singletonMap(
            SosConstants.SOS,
            Collections.singletonMap(Sos2Constants.SERVICEVERSION,
                    Collections.singleton(WaterMLConstants.NS_WML_20_DR)));

    public WmlTDREncoderv20() {
        LOGGER.debug("Encoder for the following keys initialized successfully: {}!", Joiner.on(", ")
                .join(ENCODER_KEYS));
    }

    @SuppressWarnings("unchecked")
    private static Set<EncoderKey> createEncoderKeys() {
        return CollectionHelper.union(getDefaultEncoderKeys(), CodingHelper.encoderKeysForElements(
                WaterMLConstants.NS_WML_20_DR, GetObservationResponse.class, OmObservation.class,
                AbstractFeature.class, SingleObservationValue.class, MultiObservationValues.class));
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
        super.addNamespacePrefixToMap(nameSpacePrefixMap);
        nameSpacePrefixMap.put(WaterMLConstants.NS_WML_20_DR, WaterMLConstants.NS_WML_20_DR_PREFIX);
        nameSpacePrefixMap.put(GmlCoverageConstants.NS_GML_COV, GmlCoverageConstants.NS_GML_COV_PREFIX);
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
    public Set<SchemaLocation> getSchemaLocations() {
        return Sets.newHashSet(WaterMLConstants.WML_20_SCHEMA_LOCATION, WaterMLConstants.WML_20_DR_SCHEMA_LOCATION,
                GmlCoverageConstants.GML_COVERAGE_10_SCHEMA_LOCATION);
    }

    @Override
    public boolean supportsResultStreamingForMergedValues() {
        return false;
    }

    @Override
    public XmlObject encode(Object element, Map<HelperValues, String> additionalValues) throws OwsExceptionReport,
            UnsupportedEncoderInputException {
        XmlObject encodedObject = null;
        if (element instanceof ObservationValue) {
            encodedObject = encodeResult((ObservationValue<?>) element);
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
                new WmlTDREncoderv20XmlStreamWriter().write((OmObservation) objectToEncode, outputStream,
                        encodingValues);
            } catch (XMLStreamException xmlse) {
                throw new NoApplicableCodeException().causedBy(xmlse).withMessage(
                        "Error while writing element to stream!");
            }
        } else {
            super.encode(objectToEncode, outputStream, encodingValues);
        }
    }

    @Override
    protected XmlObject createResult(OmObservation sosObservation) throws OwsExceptionReport {
        return createMeasurementDomainRange(sosObservation);
    }

    @Override
    protected XmlObject encodeResult(ObservationValue<?> observationValue) throws OwsExceptionReport {
        return createMeasurementDomainRange((AbstractObservationValue<?>) observationValue);
    }

    @Override
    protected void addObservationType(OMObservationType xbObservation, String observationType) {
        if (StringHelper.isNotEmpty(observationType)) {
            if (observationType.equals(OmConstants.OBS_TYPE_MEASUREMENT)
                    || observationType.equals(WaterMLConstants.OBSERVATION_TYPE_MEASURMENT_TDR)) {
                xbObservation.addNewType().setHref(WaterMLConstants.OBSERVATION_TYPE_MEASURMENT_TDR);
            } else if (observationType.equals(OmConstants.OBS_TYPE_CATEGORY_OBSERVATION)
                    || observationType.equals(WaterMLConstants.OBSERVATION_TYPE_CATEGORICAL_TDR)) {
                xbObservation.addNewType().setHref(WaterMLConstants.OBSERVATION_TYPE_CATEGORICAL_TDR);
            }
        }
    }

    /**
     * Create a XML MeasurementTimeseriesDomainRange object from SOS observation
     * for om:result
     * 
     * @param sosObservation
     *            SOS observation
     * @return XML MeasurementTimeseriesDomainRange object for om:result
     * @throws OwsExceptionReport
     *             If an error occurs
     */
    private XmlObject createMeasurementDomainRange(OmObservation sosObservation) throws OwsExceptionReport {
        if (!sosObservation.getObservationConstellation().isSetObservationType()
                || (sosObservation.getObservationConstellation().isSetObservationType() && isInvalidObservationType(sosObservation
                        .getObservationConstellation().getObservationType()))) {
            throw new UnsupportedEncoderInputException(this, sosObservation.getObservationConstellation().isSetObservationType());
        }
        MeasurementTimeseriesDomainRangeDocument xbMearuementTimeseriesDomainRangeDoc =
                MeasurementTimeseriesDomainRangeDocument.Factory.newInstance();
        MeasurementTimeseriesCoverageType xbMeasurementTimeseriesDomainRange =
                xbMearuementTimeseriesDomainRangeDoc.addNewMeasurementTimeseriesDomainRange();
        xbMeasurementTimeseriesDomainRange.setId("timeseries_" + sosObservation.getObservationID());

        // set time position list
        xbMeasurementTimeseriesDomainRange.addNewDomainSet().set(getTimePositionList(sosObservation));
        // initialize unit
        AbstractPhenomenon observableProperty = sosObservation.getObservationConstellation().getObservableProperty();
        String unit = "";
        // create quantity list from values
        QuantityListDocument quantityListDoc = QuantityListDocument.Factory.newInstance();
        MeasureOrNilReasonListType quantityList = quantityListDoc.addNewQuantityList();
        if (sosObservation.getValue() instanceof MultiObservationValues) {
            MultiObservationValues<?> observationValue = (MultiObservationValues<?>) sosObservation.getValue();
            TVPValue tvpValue = (TVPValue) observationValue.getValue();
            List<TimeValuePair> timeValuePairs = tvpValue.getValue();
            if (Strings.isNullOrEmpty(unit) && CollectionHelper.isNotEmpty(timeValuePairs)
                    && timeValuePairs.get(0).getValue().isSetUnit()) {
                unit = timeValuePairs.get(0).getValue().getUnit();
            }
            quantityList.setListValue(getValueList(timeValuePairs));
        }

        if (Strings.isNullOrEmpty(unit)) {
            unit = OGCConstants.UNKNOWN;
        }
        quantityList.setUom(unit);
        // set unit to SosObservableProperty if not set.
        if (observableProperty instanceof OmObservableProperty
                && !((OmObservableProperty) observableProperty).isSetUnit()) {
            ((OmObservableProperty) observableProperty).setUnit(unit);
        }
        // set up range set
        xbMeasurementTimeseriesDomainRange.addNewRangeSet().set(quantityListDoc);
        // set up rangeType
        xbMeasurementTimeseriesDomainRange.addNewRangeType().set(createDataRecord(sosObservation));

        // set om:Result
        return xbMearuementTimeseriesDomainRangeDoc;
    }

    /**
     * Create a SOS DataRecord object from SOS observation and encode to
     * XmlBeans object
     * 
     * @param sosObservation
     *            SOS observation
     * @return XML DataRecord object
     * @throws OwsExceptionReport
     *             If an error occurs
     */
    private XmlObject createDataRecord(OmObservation sosObservation) throws OwsExceptionReport {
        AbstractPhenomenon observableProperty = sosObservation.getObservationConstellation().getObservableProperty();
        SweDataRecord dataRecord = new SweDataRecord();
        dataRecord.setIdentifier("datarecord_" + sosObservation.getObservationID());
        SweQuantity quantity = new SweQuantity();
        quantity.setDefinition(observableProperty.getIdentifier());
        quantity.setDescription(observableProperty.getDescription());
        if (observableProperty instanceof OmObservableProperty
                && ((OmObservableProperty) observableProperty).isSetUnit()) {
            quantity.setUom(((OmObservableProperty) observableProperty).getUnit());
        }
        SweField field = new SweField("observed_value", quantity);
        dataRecord.addField(field);
        Map<HelperValues, String> additionalValues = Maps.newEnumMap(HelperValues.class);
        additionalValues.put(HelperValues.FOR_OBSERVATION, null);
        return CodingHelper.encodeObjectToXml(SweConstants.NS_SWE_20, dataRecord, additionalValues);
    }

    /**
     * Create a TimePositionList XML object from time values
     * 
     * @param sosObservation
     *            SOS observation
     * @return XML TimePositionList object
     * @throws OwsExceptionReport
     *             If an error occurs
     */
    private TimePositionListDocument getTimePositionList(OmObservation sosObservation) throws OwsExceptionReport {
        TimePositionListDocument timePositionListDoc = TimePositionListDocument.Factory.newInstance();
        TimePositionListType timePositionList = timePositionListDoc.addNewTimePositionList();
        timePositionList.setId("timepositionList_" + sosObservation.getObservationID());
        if (sosObservation.getValue() instanceof SingleObservationValue<?>) {
            timePositionList.setTimePositionList(Lists.newArrayList(getTimeString(sosObservation.getValue().getPhenomenonTime())));
        } else if (sosObservation.getValue() instanceof MultiObservationValues<?>) {
            timePositionList.setTimePositionList(getTimeArray((MultiObservationValues<?>) sosObservation.getValue()));
        }
        return timePositionListDoc;
    }

    /**
     * Create a array from time values
     * 
     * @param sosObservationValues
     *            SOS multi value observation object
     * @return List with string representations of time values
     * @throws OwsExceptionReport
     *             If an error occurs
     */
    private List<String> getTimeArray(MultiObservationValues<?> sosObservationValues) throws OwsExceptionReport {
        TVPValue tvpValue = (TVPValue) sosObservationValues.getValue();
        List<TimeValuePair> timeValuePairs = tvpValue.getValue();
        List<String> toList = Lists.newArrayListWithCapacity(timeValuePairs.size());
        for (TimeValuePair timeValuePair : timeValuePairs) {
            toList.add(getTimeString(timeValuePair.getTime()));
        }
        return toList;
    }

    /**
     * Get a value list from SOS TimeValuePair objects
     * 
     * @param timeValuePairs
     *            SOS TimeValuePair objects
     * @return List with value objects
     * @throws OwsExceptionReport
     *             If an error occurs
     */
    private List<Object> getValueList(List<TimeValuePair> timeValuePairs) throws OwsExceptionReport {
        ArrayList<Object> values = new ArrayList<Object>(timeValuePairs.size());
        for (TimeValuePair timeValuePair : timeValuePairs) {
            if (timeValuePair.getValue() != null
                    && (timeValuePair.getValue() instanceof CountValue || timeValuePair.getValue() instanceof QuantityValue)) {
                values.add(timeValuePair.getValue().getValue());
            } else {
                values.add("");
            }
        }
        return values;
    }

    private XmlObject createMeasurementDomainRange(AbstractObservationValue<?> observationValue)
            throws OwsExceptionReport {
        if (!observationValue.isSetObservationType()
                || (observationValue.isSetObservationType() && isInvalidObservationType(observationValue
                        .getObservationType()))) {
            return null;
        }

        MeasurementTimeseriesDomainRangeDocument xbMearuementTimeseriesDomainRangeDoc =
                MeasurementTimeseriesDomainRangeDocument.Factory.newInstance();
        MeasurementTimeseriesCoverageType xbMeasurementTimeseriesDomainRange =
                xbMearuementTimeseriesDomainRangeDoc.addNewMeasurementTimeseriesDomainRange();
        xbMeasurementTimeseriesDomainRange.setId("timeseries_" + observationValue.getObservationID());

        // set time position list
        xbMeasurementTimeseriesDomainRange.addNewDomainSet().set(getTimePositionList(observationValue));
        // initialize unit
        // AbstractPhenomenon observableProperty =
        // observationValue.getObservableProperty();
        String unit = "";
        // create quantity list from values
        QuantityListDocument quantityListDoc = QuantityListDocument.Factory.newInstance();
        MeasureOrNilReasonListType quantityList = quantityListDoc.addNewQuantityList();
        if (observationValue instanceof MultiObservationValues) {
            TVPValue tvpValue = (TVPValue) ((MultiObservationValues<?>) observationValue).getValue();
            List<TimeValuePair> timeValuePairs = tvpValue.getValue();
            if (Strings.isNullOrEmpty(unit) && CollectionHelper.isNotEmpty(timeValuePairs)
                    && timeValuePairs.get(0).getValue().isSetUnit()) {
                unit = timeValuePairs.get(0).getValue().getUnit();
            }
            quantityList.setListValue(getValueList(timeValuePairs));
        }

        if (Strings.isNullOrEmpty(unit)) {
            unit = OGCConstants.UNKNOWN;
        }
        quantityList.setUom(unit);
        // set unit to SosObservableProperty if not set.
        // if (observableProperty instanceof OmObservableProperty
        // && !((OmObservableProperty) observableProperty).isSetUnit()) {
        // ((OmObservableProperty) observableProperty).setUnit(unit);
        // }
        // set up range set
        xbMeasurementTimeseriesDomainRange.addNewRangeSet().set(quantityListDoc);
        // set up rangeType
        xbMeasurementTimeseriesDomainRange.addNewRangeType().set(createDataRecord(observationValue, unit));

        // set om:Result
        return xbMearuementTimeseriesDomainRangeDoc;
    }

    private boolean isInvalidObservationType(String observationType) {
        return !(OmConstants.OBS_TYPE_COUNT_OBSERVATION.equals(observationType)
                || OmConstants.OBS_TYPE_MEASUREMENT.equals(observationType)
                || OmConstants.OBS_TYPE_SWE_ARRAY_OBSERVATION.equals(observationType));
    }

    private XmlObject createDataRecord(AbstractObservationValue<?> observationValue, String unit)
            throws OwsExceptionReport {
        // AbstractPhenomenon observableProperty =
        // sosObservation.getObservationConstellation().getObservableProperty();
        SweDataRecord dataRecord = new SweDataRecord();
        dataRecord.setIdentifier("datarecord_" + observationValue.getObservationID());
        SweQuantity quantity = new SweQuantity();
        quantity.setDefinition(observationValue.getObservableProperty());
        quantity.setUom(unit);
        SweField field = new SweField("observed_value", quantity);
        dataRecord.addField(field);
        Map<HelperValues, String> additionalValues = Maps.newEnumMap(HelperValues.class);
        additionalValues.put(HelperValues.FOR_OBSERVATION, null);
        return CodingHelper.encodeObjectToXml(SweConstants.NS_SWE_20, dataRecord, additionalValues);
    }

    private TimePositionListDocument getTimePositionList(AbstractObservationValue<?> observationValue)
            throws OwsExceptionReport {
        TimePositionListDocument timePositionListDoc = TimePositionListDocument.Factory.newInstance();
        TimePositionListType timePositionList = timePositionListDoc.addNewTimePositionList();
        timePositionList.setId("timepositionList_" + observationValue.getObservationID());
        timePositionList.setTimePositionList(getTimeArray((MultiObservationValues<?>) observationValue));
        return timePositionListDoc;
    }
}
