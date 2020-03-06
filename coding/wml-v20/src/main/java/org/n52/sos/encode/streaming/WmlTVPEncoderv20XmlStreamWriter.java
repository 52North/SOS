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
package org.n52.sos.encode.streaming;

import java.util.List;

import javax.xml.stream.XMLStreamException;

import org.apache.commons.lang.StringEscapeUtils;
import org.n52.sos.encode.EncodingValues;
import org.n52.sos.ogc.gml.CodeType;
import org.n52.sos.ogc.gml.GmlConstants;
import org.n52.sos.ogc.om.MultiObservationValues;
import org.n52.sos.ogc.om.ObservationValue;
import org.n52.sos.ogc.om.OmConstants;
import org.n52.sos.ogc.om.OmObservableProperty;
import org.n52.sos.ogc.om.OmObservation;
import org.n52.sos.ogc.om.SingleObservationValue;
import org.n52.sos.ogc.om.StreamingValue;
import org.n52.sos.ogc.om.TimeValuePair;
import org.n52.sos.ogc.om.values.CountValue;
import org.n52.sos.ogc.om.values.ProfileValue;
import org.n52.sos.ogc.om.values.QuantityValue;
import org.n52.sos.ogc.om.values.SweDataArrayValue;
import org.n52.sos.ogc.om.values.TVPValue;
import org.n52.sos.ogc.om.values.TextValue;
import org.n52.sos.ogc.om.values.Value;
import org.n52.sos.ogc.ows.OwsExceptionReport;
import org.n52.sos.ogc.series.wml.MeasurementTimeseriesMetadata;
import org.n52.sos.ogc.series.wml.WaterMLConstants;
import org.n52.sos.ogc.series.wml.WaterMLConstants.InterpolationType;
import org.n52.sos.util.StringHelper;
import org.n52.sos.w3c.W3CConstants;

/**
 * Implementation of {@link AbstractOmV20XmlStreamWriter} to write WaterML 2.0
 * encoded {@link OmObservation}s to stream
 * 
 * @author Carsten Hollmann <c.hollmann@52north.org>
 * @since 4.1.0
 *
 */
public class WmlTVPEncoderv20XmlStreamWriter extends AbstractOmV20XmlStreamWriter {

    /**
     * constructor
     */
    public WmlTVPEncoderv20XmlStreamWriter() {
        super();
    }

    /**
     * constructor
     * 
     * @param observation
     *            {@link OmObservation} to write to stream
     */
    public WmlTVPEncoderv20XmlStreamWriter(OmObservation observation) {
        super(observation);
    }

    @Override
    protected void writeResult(OmObservation observation, EncodingValues encodingValues) throws XMLStreamException,
            OwsExceptionReport {
        start(OmConstants.QN_OM_20_RESULT);
        namespace(WaterMLConstants.NS_WML_20_PREFIX, WaterMLConstants.NS_WML_20);
        writeNewLine();
        start(WaterMLConstants.QN_MEASUREMENT_TIMESERIES);
        attr(GmlConstants.QN_ID_32, "timeseries." + observation.getObservationID());
        writeNewLine();
        // TODO update here: move down because required information is available when the first value from the database is streamed
        writeMeasurementTimeseriesMetadata(observation);
        writeNewLine();
        if (observation.getValue() instanceof SingleObservationValue) {
            SingleObservationValue<?> observationValue = (SingleObservationValue<?>) observation.getValue();
            writeDefaultPointMetadata(observationValue, observationValue.getValue().getUnit());
            writeNewLine();
            if (observationValue.getValue() instanceof SweDataArrayValue) {
                SweDataArrayValue sweDataArrayValue = (SweDataArrayValue) observationValue.getValue();
                for (List<String> list : sweDataArrayValue.getValue().getValues()) {
                    for (int i = 0; i < list.size(); i = i + 2) {
                        writePoint(list.get(i), list.get(i + 1));
                        writeNewLine();
                    }
                }
            } else {
                String time = getTimeString(observationValue.getPhenomenonTime());
                writePoint(time, getValue(observation.getValue().getValue()));
                writeNewLine();
            }
            close();
        } else if (observation.getValue() instanceof MultiObservationValues) {
            // XML streaming to client
            MultiObservationValues<?> observationValue = (MultiObservationValues<?>) observation.getValue();
            writeDefaultPointMetadata(observationValue, observationValue.getValue().getUnit());
            writeNewLine();
            TVPValue tvpValue = (TVPValue) observationValue.getValue();
            List<TimeValuePair> timeValuePairs = tvpValue.getValue();
            for (TimeValuePair timeValuePair : timeValuePairs) {
                if (timeValuePair != null) {
                    writePoint(getTimeString(timeValuePair.getTime()), getValue(timeValuePair.getValue()));
                    writeNewLine();
                }
            }
            close();
        } else if (observation.getValue() instanceof StreamingValue) {
            // Database streaming + XML streaming to client
            StreamingValue observationValue = (StreamingValue) observation.getValue();
            if (observationValue.isSetUnit()) {
                writeDefaultPointMetadata(observationValue, observationValue.getUnit());
            } else if (observation.getObservationConstellation().getObservableProperty() instanceof OmObservableProperty
                && ((OmObservableProperty) observation.getObservationConstellation().getObservableProperty())
                        .isSetUnit()) {
                writeDefaultPointMetadata(observationValue, ((OmObservableProperty) observation.getObservationConstellation().getObservableProperty())
                        .getUnit());
            } else {
                writeDefaultPointMetadata(observationValue, null);
            }
            writeNewLine();
            while (observationValue.hasNextValue()) {
                TimeValuePair timeValuePair = observationValue.nextValue();
                if (timeValuePair != null) {
                    writePoint(getTimeString(timeValuePair.getTime()), getValue(timeValuePair.getValue()));
                    writeNewLine();
                }
            }
            close();
        } else {
            super.writeResult(observation, encodingValues);
        }
    }

    @Override
    protected void writeAddtitionalNamespaces() throws XMLStreamException {
        namespace(WaterMLConstants.NS_WML_20_PREFIX, WaterMLConstants.NS_WML_20);
    }
    
    @Override
    protected void checkAndWriteIdentifier() throws OwsExceptionReport, XMLStreamException {
        if (getObservation().getObservationConstellation().isSetIdentifier()) {
            writeIdentifier(getObservation().getObservationConstellation().getIdentifierCodeWithAuthority());
            writeNewLine();
        } else {
            super.checkAndWriteIdentifier();
        }
    }
    
    @Override
    protected void checkAndWriteName() throws OwsExceptionReport, XMLStreamException {
        if (getObservation().getObservationConstellation().isSetName()) {
            for (CodeType name : getObservation().getObservationConstellation().getName()) {
                writeName(name);
                writeNewLine();   
            }
        } else {
            super.checkAndWriteName();
        }
    }
    
    @Override
    protected void checkAndWriteDescription() throws XMLStreamException {
        if (getObservation().getObservationConstellation().isSetDescription()) {
            writeDescription(getObservation().getObservationConstellation().getDescription());
            writeNewLine();
        } else {
            super.checkAndWriteDescription();
        }
    }

    /**
     * Close written wml:MeasurementTimeseries and om:result tags
     * 
     * @throws XMLStreamException
     *             If an error occurs when writing to stream
     */
    private void close() throws XMLStreamException {
        indent--;
        end(WaterMLConstants.QN_MEASUREMENT_TIMESERIES);
        writeNewLine();
        end(OmConstants.QN_OM_20_RESULT);
        indent++;
    }

    /**
     * Write timeseries metadata to stream
     * 
     * @param observation.getPhenomenonTime().getGmlId()
     *            Observation id
     * @throws XMLStreamException
     *             If an error occurs when writing to stream
     */
    private void writeMeasurementTimeseriesMetadata(OmObservation o) throws XMLStreamException {
        start(WaterMLConstants.QN_METADATA);
        writeNewLine();
        start(WaterMLConstants.QN_MEASUREMENT_TIMESERIES_METADATA);
        writeNewLine();
        empty(WaterMLConstants.QN_TEMPORAL_EXTENT);
        addXlinkHrefAttr("#" + o.getPhenomenonTime().getGmlId());
        if (o.isSetValue() 
                && o.getValue().isSetMetadata() 
                && o.getValue().getMetadata().isSetTimeseriesMetadata()
                && o.getValue().getMetadata().getTimeseriesmetadata() instanceof MeasurementTimeseriesMetadata) {
            start(WaterMLConstants.QN_CUMULATIVE);
            chars(Boolean.toString(((MeasurementTimeseriesMetadata)o.getValue().getMetadata().getTimeseriesmetadata()).isCumulative()));
            endInline(WaterMLConstants.QN_CUMULATIVE);
        }
        writeNewLine();
        indent--;
        end(WaterMLConstants.QN_MEASUREMENT_TIMESERIES_METADATA);
        writeNewLine();
        end(WaterMLConstants.QN_METADATA);
        indent++;
    }

    /**
     * Write wml:defaultPointMetadata to stream
     * 
     * @param unit
     * @throws XMLStreamException
     *             If an error occurs when writing to stream
     */
    private void writeDefaultPointMetadata(ObservationValue<?> value, String unit) throws XMLStreamException {
        start(WaterMLConstants.QN_DEFAULT_POINT_METADATA);
        writeNewLine();
        start(WaterMLConstants.QN_DEFAULT_TVP_MEASUREMENT_METADATA);
        writeNewLine();
        writeUOM(unit);
        writeNewLine();
        writeInterpolationType(value);
        writeNewLine();
        indent--;
        end(WaterMLConstants.QN_DEFAULT_TVP_MEASUREMENT_METADATA);
        writeNewLine();
        end(WaterMLConstants.QN_DEFAULT_POINT_METADATA);
        indent++;
    }

    /**
     * Write UOM attribute to stream
     * 
     * @param code
     *            UOM code
     * @throws XMLStreamException
     *             If an error occurs when writing to stream
     */
    private void writeUOM(String code) throws XMLStreamException {
        if (StringHelper.isNotEmpty(code)) {
            empty(WaterMLConstants.UOM);
            attr("code", code);
        }
    }

    /**
     * Write wml:interpolationType to stream
     * @param value 
     * 
     * @throws XMLStreamException
     *             If an error occurs when writing to stream
     */
    private void writeInterpolationType(ObservationValue<?> value) throws XMLStreamException {
        empty(WaterMLConstants.QN_INTERPOLATION_TYPE);
        if (value != null && value.isSetMetadata() && value.getDefaultPointMetadata().isSetDefaultTVPMeasurementMetadata() && value.getDefaultPointMetadata().getDefaultTVPMeasurementMetadata().isSetInterpolationType()) {
            InterpolationType interpolationtype = value.getDefaultPointMetadata().getDefaultTVPMeasurementMetadata().getInterpolationtype();
            addXlinkHrefAttr(interpolationtype.getIdentifier());
            addXlinkTitleAttr(interpolationtype.getTitle());
        } else {
            addXlinkHrefAttr("http://www.opengis.net/def/timeseriesType/WaterML/2.0/continuous");
            addXlinkTitleAttr("Instantaneous");
        }
    }

    /**
     * Get the {@link String} representation of {@link Value}
     * 
     * @param value
     *            {@link Value} to get {@link String} representation from
     * @return {@link String} representation of {@link Value}
     */
    private String getValue(Value<?> value) {
        if (value != null && value.isSetValue()) {
            if (value instanceof QuantityValue) {
                QuantityValue quantityValue = (QuantityValue) value;
                return Double.toString(quantityValue.getValue().doubleValue());
            } else if (value instanceof ProfileValue) {
                ProfileValue gwglcValue = (ProfileValue)value;
                if (gwglcValue.isSetValue()) {
                    return getValue(gwglcValue.getValue().iterator().next().getSimpleValue());
                }       
            } else if (value instanceof CountValue) {
                CountValue countValue = (CountValue) value;
                return Integer.toString(countValue.getValue().intValue());
            } else if (value instanceof TextValue) {
                TextValue textValue = (TextValue) value;
                String nonXmlEscapedText = textValue.getValue();
                return StringEscapeUtils.escapeXml(nonXmlEscapedText);
            }
        }
        return null;
    }

    /**
     * Write wml:point to stream
     * 
     * @param time
     *            time as {@link String}
     * @param value
     *            value as {@link String}
     * @throws XMLStreamException
     *             If an error occurs when writing to stream
     */
    private void writePoint(String time, String value) throws XMLStreamException {
        if (StringHelper.isNotEmpty(time)) {
            start(WaterMLConstants.QN_POINT);
            writeNewLine();
            writeMeasurementTVP(time, value);
            writeNewLine();
            indent--;
            end(WaterMLConstants.QN_POINT);
            indent++;
        }
    }

    /**
     * Write wml:MeasurementTVP to stream
     * 
     * @param time
     *            time as {@link String}
     * @param value
     *            value as {@link String}
     * @throws XMLStreamException
     *             If an error occurs when writing to stream
     */
    private void writeMeasurementTVP(String time, String value) throws XMLStreamException {
        start(WaterMLConstants.QN_MEASUREMENT_TVP);
        writeNewLine();
        writeTime(time);
        writeNewLine();
        writeValue(value);
        writeNewLine();
        indent--;
        end(WaterMLConstants.QN_MEASUREMENT_TVP);
        indent++;
    }

    /**
     * Write wml:time to stream
     * 
     * @param time
     *            time to write
     * @throws XMLStreamException
     *             If an error occurs when writing to stream
     */
    private void writeTime(String time) throws XMLStreamException {
        start(WaterMLConstants.QN_TIME);
        chars(time);
        endInline(WaterMLConstants.QN_TIME);
    }

    /**
     * Write wml:value to stream
     * 
     * @param value
     *            value to write
     * @throws XMLStreamException
     *             If an error occurs when writing to stream
     */
    private void writeValue(String value) throws XMLStreamException {
        if (StringHelper.isNotEmpty(value)) {
            start(WaterMLConstants.QN_VALUE);
            chars(value);
            endInline(WaterMLConstants.QN_VALUE);
        } else {
            empty(WaterMLConstants.QN_VALUE);
            attr(W3CConstants.QN_XSI_NIL, "true");
            writeValueMetadata();
        }
    }

    /**
     * Write missing value metadata to stream
     * 
     * @throws XMLStreamException
     *             If an error occurs when writing to stream
     */
    private void writeValueMetadata() throws XMLStreamException {
        start(WaterMLConstants.QN_METADATA);
        start(WaterMLConstants.QN_TVP_MEASUREMENT_METADATA);
        empty(WaterMLConstants.QN_NIL_REASON);
        addXlinkHrefAttr("missing");
        endInline(WaterMLConstants.QN_TVP_MEASUREMENT_METADATA);
        endInline(WaterMLConstants.QN_METADATA);

    }

}
