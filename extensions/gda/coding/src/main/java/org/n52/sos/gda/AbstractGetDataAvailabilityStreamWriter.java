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
package org.n52.sos.gda;

import java.io.OutputStream;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamException;

import org.joda.time.DateTime;
import org.n52.sos.encode.EncodingValues;
import org.n52.sos.encode.XmlEventWriter;
import org.n52.sos.exception.ows.concrete.DateTimeFormatException;
import org.n52.sos.gda.GetDataAvailabilityResponse.DataAvailability;
import org.n52.sos.ogc.gml.GmlConstants;
import org.n52.sos.ogc.gml.time.Time.TimeFormat;
import org.n52.sos.ogc.gml.time.TimeInstant;
import org.n52.sos.ogc.gml.time.TimePeriod;
import org.n52.sos.ogc.om.NamedValue;
import org.n52.sos.ogc.om.OmConstants;
import org.n52.sos.ogc.ows.OwsExceptionReport;
import org.n52.sos.ogc.sos.Sos2Constants;
import org.n52.sos.ogc.sos.SosConstants.HelperValues;
import org.n52.sos.ogc.swe.SweConstants;
import org.n52.sos.util.CodingHelper;
import org.n52.sos.util.DateTimeHelper;
import org.n52.sos.w3c.W3CConstants;

import com.google.common.collect.Maps;

public abstract class AbstractGetDataAvailabilityStreamWriter extends XmlEventWriter<List<DataAvailability>> {

    protected static final String TIME_PERIOD_PREFIX = "tp_";

    protected static final String DATA_AVAILABILITY_PREFIX = "dam_";

    protected static final String RESULT_TIME = "resultTime";

    private List<DataAvailability> gdas;

    protected final Map<TimePeriod, String> times;

    protected final String version;

    protected int dataAvailabilityCount = 1;

    protected int timePeriodCount = 1;

    protected int resultTimeCount = 1;
    
    public AbstractGetDataAvailabilityStreamWriter(String version, List<DataAvailability> gdas) {
        this.gdas = gdas == null ? Collections.<DataAvailability> emptyList() : gdas;
        this.times = new HashMap<TimePeriod, String>(this.gdas.size());
        this.version = version == null ? Sos2Constants.SERVICEVERSION : version;
    }
    
    @Override
    public void write(OutputStream out) throws XMLStreamException, OwsExceptionReport {
        init(out);
        start(true);
        writeGetDataAvailabilityResponse();
        end();
        finish();
    }

    @Override
    public void write(OutputStream out, EncodingValues encodingValues) throws XMLStreamException, OwsExceptionReport {
        write(out);
    }

    @Override
    public void write(List<DataAvailability> elementToStream, OutputStream out) throws XMLStreamException,
            OwsExceptionReport {
       this.gdas = elementToStream;
       write(out);
    }

    @Override
    public void write(List<DataAvailability> elementToStream, OutputStream out, EncodingValues encodingValues)
            throws XMLStreamException, OwsExceptionReport {
        this.gdas = elementToStream;
        write(out);
    }
    
    protected abstract void writeGetDataAvailabilityResponse() throws XMLStreamException, OwsExceptionReport;
    
    protected abstract void wirteDataAvailabilityMember(DataAvailability da) throws XMLStreamException, OwsExceptionReport;
    
    protected void writePhenomenonTime(DataAvailability da, QName element) throws DateTimeFormatException, XMLStreamException {
        start(element);
        if (times.containsKey(da.getPhenomenonTime())) {
            attr(GetDataAvailabilityConstants.XLINK_HREF, "#" + times.get(da.getPhenomenonTime()));
        } else {
            da.getPhenomenonTime().setGmlId(TIME_PERIOD_PREFIX + timePeriodCount++);
            times.put(da.getPhenomenonTime(), da.getPhenomenonTime().getGmlId());
            writeTimePeriod(da.getPhenomenonTime());
        }
        end(element);
    }
    
    protected void writeFeatureOfInterest(DataAvailability da, QName element) throws XMLStreamException {
        start(element);
        attr(GetDataAvailabilityConstants.XLINK_HREF, da.getFeatureOfInterest().getHref());
        if (da.getFeatureOfInterest().isSetTitle()) {
            attr(GetDataAvailabilityConstants.XLINK_TITLE, da.getFeatureOfInterest().getTitle());
        } else {
            attr(GetDataAvailabilityConstants.XLINK_TITLE, da.getFeatureOfInterest().getTitleFromHref());
        }
        end(element);
    }

    protected void writeProcedure(DataAvailability da, QName element) throws XMLStreamException {
        start(element);
        attr(GetDataAvailabilityConstants.XLINK_HREF, da.getProcedure().getHref());
        if (da.getProcedure().isSetTitle()) {
            attr(GetDataAvailabilityConstants.XLINK_TITLE, da.getProcedure().getTitle());
        } else {
            attr(GetDataAvailabilityConstants.XLINK_TITLE, da.getProcedure().getTitleFromHref());
        }
        end(element);
    }

    protected void writeObservedProperty(DataAvailability da, QName element) throws XMLStreamException {
        start(element);
        attr(GetDataAvailabilityConstants.XLINK_HREF, da.getObservedProperty().getHref());
        if (da.getObservedProperty().isSetTitle()) {
            attr(GetDataAvailabilityConstants.XLINK_TITLE, da.getObservedProperty().getTitle());
        } else {
            attr(GetDataAvailabilityConstants.XLINK_TITLE, da.getObservedProperty().getTitleFromHref());
        }
        end(element);
    }

    protected void writeTimePeriod(TimePeriod tp) throws XMLStreamException, DateTimeFormatException {
        start(GmlConstants.QN_TIME_PERIOD_32);
        attr(GmlConstants.QN_ID_32, tp.getGmlId());
        writeBegin(tp);
        writeEnd(tp);
        end(GmlConstants.QN_TIME_PERIOD_32);
    }

    protected void writeBegin(TimePeriod tp) throws XMLStreamException, DateTimeFormatException {
        start(GmlConstants.QN_BEGIN_POSITION_32);
        if (tp.isSetStartIndeterminateValue()) {
            attr(GmlConstants.AN_INDETERMINATE_POSITION, tp.getStartIndet().name());
        }
        if (tp.isSetStart()) {
            writeTimeString(tp.getStart(), tp.getTimeFormat());
        }
        end(GmlConstants.QN_BEGIN_POSITION_32);
    }

    protected void writeEnd(TimePeriod tp) throws XMLStreamException, DateTimeFormatException {
        start(GmlConstants.QN_END_POSITION_32);
        if (tp.isSetEndIndeterminateValue()) {
            attr(GmlConstants.AN_INDETERMINATE_POSITION, tp.getEndIndet().name());
        }
        if (tp.isSetEnd()) {
            writeTimeString(tp.getEnd(), tp.getTimeFormat());
        } 
        end(GmlConstants.QN_END_POSITION_32);
    }

    protected void writeTimeString(DateTime time, TimeFormat format) throws XMLStreamException,
            DateTimeFormatException {
        chars(DateTimeHelper.formatDateTime2String(time, format));
    }

    protected void writeCount(long count, QName element) throws XMLStreamException {
        start(element);
        chars(Long.toString(count));
        end(element);
    }

    protected void writeResultTimes(List<TimeInstant> resultTimes, QName element) throws XMLStreamException, OwsExceptionReport {
        start(element);
        start(SweConstants.QN_DATA_RECORD_SWE_200);
        attr("definition", RESULT_TIME);
        for (TimeInstant resultTime : resultTimes) {
            start(SweConstants.QN_FIELD_200);
            attr("name", RESULT_TIME + resultTimeCount++);
            writeTime(resultTime);
            end(SweConstants.QN_FIELD_200);
        }
        end(SweConstants.QN_DATA_RECORD_SWE_200);
        end(element);
    }

    protected void writeTime(TimeInstant ti) throws XMLStreamException, DateTimeFormatException {
        start(SweConstants.QN_TIME_SWE_200);
        writeUom();
        writeValue(ti);
        end(SweConstants.QN_TIME_SWE_200);
    }

    private void writeUom() throws XMLStreamException {
        start(SweConstants.QN_UOM_SWE_200);
        attr(W3CConstants.QN_XLINK_HREF, OmConstants.PHEN_UOM_ISO8601);
        end(SweConstants.QN_UOM_SWE_200);

    }

    protected void writeValue(TimeInstant ti) throws XMLStreamException, DateTimeFormatException {
        start(SweConstants.QN_VALUE_SWE_200);
        writeTimeString(ti.getValue(), ti.getTimeFormat());
        end(SweConstants.QN_VALUE_SWE_200);
    }
    
    protected void writeElementWithStringValue(String value, QName element) throws XMLStreamException {
        start(element);
        chars(value);
        end(element);
    }

    @SuppressWarnings("rawtypes")
    protected void writeMetadata(Map<String, NamedValue> metadata, QName element) throws XMLStreamException, OwsExceptionReport {
        for (String key : metadata.keySet()) {
            start(GetDataAvailabilityConstants.GDA_EXTENSION);
            attr("name", key);
            Map<HelperValues, String> additionalValues = Maps.newHashMap();
            additionalValues.put(HelperValues.DOCUMENT, "true");
            rawText(CodingHelper.encodeObjectToXmlText(OmConstants.NS_OM_2, metadata.get(key), additionalValues));
            end(GetDataAvailabilityConstants.GDA_EXTENSION);
        }
    }
    
    protected List<DataAvailability> getGDAs() {
        return gdas;
    }
}
