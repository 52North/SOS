/**
 * Copyright (C) 2012-2014 52°North Initiative for Geospatial Open Source
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

import org.n52.sos.encode.EncodingValues;
import org.n52.sos.ogc.gml.GmlConstants;
import org.n52.sos.ogc.om.MultiObservationValues;
import org.n52.sos.ogc.om.OmConstants;
import org.n52.sos.ogc.om.OmObservation;
import org.n52.sos.ogc.om.SingleObservationValue;
import org.n52.sos.ogc.om.StreamingValue;
import org.n52.sos.ogc.om.TimeValuePair;
import org.n52.sos.ogc.om.values.CountValue;
import org.n52.sos.ogc.om.values.QuantityValue;
import org.n52.sos.ogc.om.values.TVPValue;
import org.n52.sos.ogc.om.values.Value;
import org.n52.sos.ogc.ows.OwsExceptionReport;
import org.n52.sos.ogc.wml.WaterMLConstants;
import org.n52.sos.util.StringHelper;
import org.n52.sos.w3c.W3CConstants;

public class WmlTVPEncoderv20XmlStreamWriter extends AbstractOmV20XmlStreamWriter {
    
    public WmlTVPEncoderv20XmlStreamWriter() {
        super();
    }
    
    public WmlTVPEncoderv20XmlStreamWriter(OmObservation observation) {
        super(observation);
    }

    @Override
    protected void writeResult(OmObservation observation, EncodingValues encodingValues) throws XMLStreamException, OwsExceptionReport {
         start(OmConstants.QN_OM_20_RESULT);
         writeNewLine();
         start(WaterMLConstants.QN_MEASUREMENT_TIMESERIES);
         attr(GmlConstants.QN_ID_32, "timeseries." + observation.getObservationID());
         writeNewLine();
         writeMeasurementTimeseriesMetadata(observation.getPhenomenonTime().getGmlId());
         writeNewLine();
        if (observation.getValue() instanceof SingleObservationValue) {
            SingleObservationValue<?> observationValue = (SingleObservationValue<?>) observation.getValue();
            writeDefaultPointMetadata(observationValue.getValue().getUnit());
            String time = getTimeString(observationValue.getPhenomenonTime());
            writePoint(time, getValue(observation.getValue().getValue()));
            close();
        } else if (observation.getValue() instanceof MultiObservationValues) {
            MultiObservationValues<?> observationValue = (MultiObservationValues<?>) observation.getValue();
            writeDefaultPointMetadata(observationValue.getValue().getUnit());
            TVPValue tvpValue = (TVPValue) observationValue.getValue();
            List<TimeValuePair> timeValuePairs = tvpValue.getValue();
            for (TimeValuePair timeValuePair : timeValuePairs) {
                writePoint(getTimeString(timeValuePair.getTime()), getValue(timeValuePair.getValue()));
                writeNewLine();
            }
            close();
        } else if (observation.getValue() instanceof StreamingValue) {
            StreamingValue observationValue = (StreamingValue) observation.getValue();
            writeDefaultPointMetadata(observationValue.getUnit());
            writeNewLine();
            while (observationValue.hasNextValue()) {
                TimeValuePair timeValuePair = observationValue.nextValue();
                writePoint(getTimeString(timeValuePair.getTime()), getValue(timeValuePair.getValue()));
                writeNewLine();
            }
            close();
        } else {
            super.writeResult(observation, encodingValues);
        }
    }
        
    private void close() throws XMLStreamException {
        indent--;
        end(WaterMLConstants.QN_MEASUREMENT_TIMESERIES);
        writeNewLine();
        end(OmConstants.QN_OM_20_RESULT);
        indent++;
    }

    private void writeMeasurementTimeseriesMetadata(String id) throws XMLStreamException {
        start(WaterMLConstants.QN_METADATA);
        writeNewLine();
        start(WaterMLConstants.QN_TIMESERIES_METADATA);
        writeNewLine();
        empty(WaterMLConstants.QN_TEMPORAL_EXTENT);
        addXlinkHrefAttr("#" + id);
        writeNewLine();
        indent--;
        end(WaterMLConstants.QN_TIMESERIES_METADATA);
        writeNewLine();
        end(WaterMLConstants.QN_METADATA);
        indent++;
    }

    private void writeDefaultPointMetadata(String unit) throws XMLStreamException {
        start(WaterMLConstants.QN_DEFAULT_POINT_METADATA);
        writeNewLine();
        start(WaterMLConstants.QN_DEFAULT_TVP_MEASUREMENT_METADATA);
        writeNewLine();
        writeUOM(unit);
        writeNewLine();
        writeInterpolationType();
        writeNewLine();
        indent--;
        end(WaterMLConstants.QN_DEFAULT_TVP_MEASUREMENT_METADATA);
        writeNewLine();
        end(WaterMLConstants.QN_DEFAULT_POINT_METADATA);
        indent++;
    }

    private void writeUOM(String code) throws XMLStreamException {
        if (StringHelper.isNotEmpty(code)) {
            empty(WaterMLConstants.UOM);
            attr("code", code);
        }
    }

    private void writeInterpolationType() throws XMLStreamException {
        empty(WaterMLConstants.QN_INTERPOLATION_TYPE);
        addXlinkHrefAttr("http://www.opengis.net/def/timeseriesType/WaterML/2.0/continuous");
        addXlinkTitleAttr("Instantaneous");
    }

    private String getValue(Value<?> value) {
        if (value instanceof QuantityValue) {
            QuantityValue quantityValue = (QuantityValue) value;
            return Double.toString(quantityValue.getValue().doubleValue());
        } else if (value instanceof CountValue) {
            CountValue countValue = (CountValue) value;
            return Integer.toString(countValue.getValue().intValue());
        }
        return null;
    }

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

    private void writeTime(String time) throws XMLStreamException {
        start(WaterMLConstants.QN_TIME);
        chars(time);
        endInline(WaterMLConstants.QN_TIME);
    }

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

    private void writeValueMetadata() throws XMLStreamException {
        start(WaterMLConstants.QN_METADATA);
        start(WaterMLConstants.QN_TVP_MEASUREMENT_METADATA);
        empty(WaterMLConstants.QN_NIL_REASON);
        attr("nilreason", "missing");
        endInline(WaterMLConstants.QN_TVP_MEASUREMENT_METADATA);
        endInline(WaterMLConstants.QN_METADATA);
        
    }


}
