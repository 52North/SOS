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
package org.n52.sos.gda;

import java.io.OutputStream;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
import org.n52.sos.ogc.om.OmConstants;
import org.n52.sos.ogc.ows.OwsExceptionReport;
import org.n52.sos.ogc.sos.Sos2Constants;
import org.n52.sos.ogc.swe.SweConstants;
import org.n52.sos.util.DateTimeHelper;
import org.n52.sos.w3c.W3CConstants;

/**
 * GetDataAvailability response stream writer.
 * 
 * @author Christian Autermann <c.autermann@52north.org>
 * 
 * @since 4.0.0
 */
public class GetDataAvailabilityStreamWriter extends XmlEventWriter<List<DataAvailability>> {
    private static final String TIME_PERIOD_PREFIX = "tp_";

    private static final String DATA_AVAILABILITY_PREFIX = "dam_";

    private static final String RESULT_TIME = "resultTime";

    private List<DataAvailability> gdas;

    private final Map<TimePeriod, String> times;

    private final String version;

    private int dataAvailabilityCount = 1;

    private int timePeriodCount = 1;

    private int resultTimeCount = 1;

    public GetDataAvailabilityStreamWriter(String version, List<DataAvailability> gdas) {
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

    protected void writeGetDataAvailabilityResponse() throws XMLStreamException, OwsExceptionReport {
        start(GetDataAvailabilityConstants.GDA_GET_DATA_AVAILABILITY_RESPONSE);
        namespace(GetDataAvailabilityConstants.NS_GDA_PREFIX, GetDataAvailabilityConstants.NS_GDA);
        namespace(GmlConstants.NS_GML_PREFIX, GmlConstants.NS_GML_32);
        namespace(SweConstants.NS_SWE_PREFIX, SweConstants.NS_SWE_20);
        namespace(W3CConstants.NS_XLINK_PREFIX, W3CConstants.NS_XLINK);
        for (DataAvailability da : this.gdas) {
            wirteDataAvailabilityMember(da);
        }
        end(GetDataAvailabilityConstants.GDA_GET_DATA_AVAILABILITY_RESPONSE);
    }

    protected void wirteDataAvailabilityMember(DataAvailability da) throws XMLStreamException, OwsExceptionReport {
        start(GetDataAvailabilityConstants.GDA_DATA_AVAILABILITY_MEMBER);
        attr(GmlConstants.QN_ID_32, DATA_AVAILABILITY_PREFIX + dataAvailabilityCount++);
        writeProcedure(da);
        writeObservedProperty(da);
        writeFeatureOfInterest(da);
        writePhenomenonTime(da);
        if (da.isSetCount()) {
            writeCount(da.getCount());
        }
        if (da.isSetResultTime()) {
            writeResultTimes(da.getResultTimes());
        }
        end(GetDataAvailabilityConstants.GDA_DATA_AVAILABILITY_MEMBER);
    }

    protected void writePhenomenonTime(DataAvailability da) throws DateTimeFormatException, XMLStreamException {
        start(GetDataAvailabilityConstants.GDA_PHENOMENON_TIME);
        if (times.containsKey(da.getPhenomenonTime())) {
            attr(GetDataAvailabilityConstants.XLINK_HREF, "#" + times.get(da.getPhenomenonTime()));
        } else {
            da.getPhenomenonTime().setGmlId(TIME_PERIOD_PREFIX + timePeriodCount++);
            times.put(da.getPhenomenonTime(), da.getPhenomenonTime().getGmlId());
            writeTimePeriod(da.getPhenomenonTime());
        }
        end(GetDataAvailabilityConstants.GDA_PHENOMENON_TIME);
    }

    protected void writeFeatureOfInterest(DataAvailability da) throws XMLStreamException {
        start(GetDataAvailabilityConstants.GDA_FEATURE_OF_INTEREST);
        attr(GetDataAvailabilityConstants.XLINK_HREF, da.getFeatureOfInterest().getHref());
        if (da.getFeatureOfInterest().isSetTitle()) {
            attr(GetDataAvailabilityConstants.XLINK_TITLE, da.getFeatureOfInterest().getTitle());
        } else {
            attr(GetDataAvailabilityConstants.XLINK_TITLE, da.getFeatureOfInterest().getTitleFromHref());
        }
        end(GetDataAvailabilityConstants.GDA_FEATURE_OF_INTEREST);
    }

    protected void writeProcedure(DataAvailability da) throws XMLStreamException {
        start(GetDataAvailabilityConstants.GDA_PROCEDURE);
        attr(GetDataAvailabilityConstants.XLINK_HREF, da.getProcedure().getHref());
        if (da.getProcedure().isSetTitle()) {
            attr(GetDataAvailabilityConstants.XLINK_TITLE, da.getProcedure().getTitle());
        } else {
            attr(GetDataAvailabilityConstants.XLINK_TITLE, da.getProcedure().getTitleFromHref());
        }
        end(GetDataAvailabilityConstants.GDA_PROCEDURE);
    }

    protected void writeObservedProperty(DataAvailability da) throws XMLStreamException {
        start(GetDataAvailabilityConstants.GDA_OBSERVED_PROPERTY);
        attr(GetDataAvailabilityConstants.XLINK_HREF, da.getObservedProperty().getHref());
        if (da.getObservedProperty().isSetTitle()) {
            attr(GetDataAvailabilityConstants.XLINK_TITLE, da.getObservedProperty().getTitle());
        } else {
            attr(GetDataAvailabilityConstants.XLINK_TITLE, da.getObservedProperty().getTitleFromHref());
        }
        end(GetDataAvailabilityConstants.GDA_OBSERVED_PROPERTY);
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

    protected void writeCount(long count) throws XMLStreamException {
        start(GetDataAvailabilityConstants.GDA_COUNT);
        chars(Long.toString(count));
        end(GetDataAvailabilityConstants.GDA_COUNT);
    }

    protected void writeResultTimes(List<TimeInstant> resultTimes) throws XMLStreamException, OwsExceptionReport {
        start(GetDataAvailabilityConstants.GDA_EXTENSION);
        start(SweConstants.QN_DATA_RECORD_SWE_200);
        attr("definition", RESULT_TIME);
        for (TimeInstant resultTime : resultTimes) {
            start(SweConstants.QN_FIELD_200);
            attr("name", RESULT_TIME + resultTimeCount++);
            writeTime(resultTime);
            end(SweConstants.QN_FIELD_200);
        }
        end(SweConstants.QN_DATA_RECORD_SWE_200);
        end(GetDataAvailabilityConstants.GDA_EXTENSION);
    }

    protected void writeTime(TimeInstant ti) throws XMLStreamException, DateTimeFormatException {
        start(SweConstants.QN_TIME_SWE_200);
        writeValue(ti);
        writeUom();
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

}
