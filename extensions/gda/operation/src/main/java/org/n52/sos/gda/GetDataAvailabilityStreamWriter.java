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
package org.n52.sos.gda;

import java.io.OutputStream;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.stream.XMLStreamException;

import org.joda.time.DateTime;
import org.n52.sos.encode.EncodingValues;
import org.n52.sos.encode.XmlStreamWriter;
import org.n52.sos.exception.ows.NoApplicableCodeException;
import org.n52.sos.exception.ows.concrete.DateTimeFormatException;
import org.n52.sos.gda.GetDataAvailabilityResponse.DataAvailability;
import org.n52.sos.ogc.gml.GmlConstants;
import org.n52.sos.ogc.gml.time.Time.TimeFormat;
import org.n52.sos.ogc.gml.time.TimePeriod;
import org.n52.sos.ogc.om.OmConstants;
import org.n52.sos.ogc.ows.OwsExceptionReport;
import org.n52.sos.ogc.sos.Sos2Constants;
import org.n52.sos.ogc.sos.SosConstants;
import org.n52.sos.util.CollectionHelper;
import org.n52.sos.util.DateTimeHelper;
import org.n52.sos.util.StringHelper;
import org.n52.sos.w3c.W3CConstants;

/**
 * @author Christian Autermann <c.autermann@52north.org>
 * 
 * @since 4.0.0
 */
public class GetDataAvailabilityStreamWriter extends XmlStreamWriter<GetDataAvailabilityResponse> {
    private static final String TIME_PERIOD_PREFIX = "tp_";

    private static final String DATA_AVAILABILITY_PREFIX = "dam_";

    private GetDataAvailabilityResponse response = null;

    private List<DataAvailability> gdas;

    private Map<TimePeriod, String> times;

    private String version;

    private int dataAvailabilityCount = 1;

    private int timePeriodCount = 1;

    /**
     * default constructor
     */
    public GetDataAvailabilityStreamWriter() {
    }

    /**
     * constructor
     * 
     * @param response
     *            {@link GetDataAvailabilityResponse} to write to stream
     */
    public GetDataAvailabilityStreamWriter(GetDataAvailabilityResponse response) {
        setResponse(response);
        setVersion(response.getVersion());
        setDataAvailabilities(response.getDataAvailabilities());
    }

    /**
     * construcor
     * 
     * @param version
     *            Service version
     * @param gdas
     *            {@link DataAvailability}s to encode and write to stream
     */
    public GetDataAvailabilityStreamWriter(String version, List<DataAvailability> gdas) {
        setDataAvailabilities(gdas);
        setVersion(version);
    }

    private void setVersion(String version) {
        this.version = version == null ? Sos2Constants.SERVICEVERSION : version;
    }

    private void setDataAvailabilities(List<DataAvailability> gdas) {
        this.gdas = gdas == null ? Collections.<DataAvailability> emptyList() : gdas;
        this.times = new HashMap<TimePeriod, String>(this.gdas.size());
    }

    private void setResponse(GetDataAvailabilityResponse response) {
        this.response = response;
    }

    private GetDataAvailabilityResponse getResponse() {
        return response;
    }

    private boolean isSetEncodingValues() {
        return isSetVersion() && isSetDataAvailability();
    }

    private boolean isSetDataAvailability() {
        return CollectionHelper.isNotEmpty(gdas);
    }

    private boolean isSetVersion() {
        return StringHelper.isNotEmpty(version);
    }

    public void write(OutputStream out) throws XMLStreamException, OwsExceptionReport {
        write(getResponse(), out);
    }

    @Override
    public void write(OutputStream out, EncodingValues encodingValues) throws XMLStreamException, OwsExceptionReport {
        write(getResponse(), out, encodingValues);

    }

    @Override
    public void write(GetDataAvailabilityResponse elementToStream, OutputStream out) throws XMLStreamException,
            OwsExceptionReport {
        write(response, out, new EncodingValues());
    }

    @Override
    public void write(GetDataAvailabilityResponse elementToStream, OutputStream out, EncodingValues encodingValues)
            throws XMLStreamException, OwsExceptionReport {
        try {
            if (!isSetEncodingValues()) {
                setVersion(elementToStream.getVersion());
                setDataAvailabilities(elementToStream.getDataAvailabilities());
            }
            init(out, encodingValues);
            start(encodingValues.isEmbedded());
            writeGetDataAvailabilityResponse();
            end();
            finish();
        } catch (XMLStreamException xmlse) {
            throw new NoApplicableCodeException().causedBy(xmlse);
        }

    }

    protected void writeGetDataAvailabilityResponse() throws XMLStreamException, DateTimeFormatException {
        start(GetDataAvailabilityConstants.SOS_GET_DATA_AVAILABILITY_RESPONSE);
        attr(GetDataAvailabilityConstants.AN_SERVICE, SosConstants.SOS);
        attr(GetDataAvailabilityConstants.AN_VERSION, version);
        namespace(SosConstants.NS_SOS_PREFIX, Sos2Constants.NS_SOS_20);
        namespace(GmlConstants.NS_GML_PREFIX, GmlConstants.NS_GML_32);
        namespace(OmConstants.NS_OM_PREFIX, OmConstants.NS_OM_2);
        namespace(W3CConstants.NS_XLINK_PREFIX, W3CConstants.NS_XLINK);
        writeNewLine();
        int currentIndent = indent;
        for (DataAvailability da : this.gdas) {
            indent = currentIndent;
            wirteDataAvailabilityMember(da);
            writeNewLine();
        }
        end(GetDataAvailabilityConstants.SOS_GET_DATA_AVAILABILITY_RESPONSE);
    }

    protected void wirteDataAvailabilityMember(DataAvailability da) throws XMLStreamException, DateTimeFormatException {
        start(GetDataAvailabilityConstants.SOS_DATA_AVAILABILITY_MEMBER);
        attr(GetDataAvailabilityConstants.GML_ID, DATA_AVAILABILITY_PREFIX + dataAvailabilityCount++);
        int currentIndent = indent;
        writeNewLine();
        writeFeatureOfInterest(da);
        writeNewLine();
        writeProcedure(da);
        writeNewLine();
        writeObservedProperty(da);
        writeNewLine();
        writePhenomenonTime(da);
        writeNewLine();
        if (da.isSetValueCount()) {
            indent = currentIndent;
            writeValueCount(da.getValueCount());
            writeNewLine();
        }
        indent = --currentIndent;
        end(GetDataAvailabilityConstants.SOS_DATA_AVAILABILITY_MEMBER);
    }

    protected void writePhenomenonTime(DataAvailability da) throws DateTimeFormatException, XMLStreamException {
        if (times.containsKey(da.getPhenomenonTime())) {
            empty(GetDataAvailabilityConstants.OM_PHENOMENON_TIME);
            attr(GetDataAvailabilityConstants.XLINK_HREF, "#" + times.get(da.getPhenomenonTime()));
        } else {
            start(GetDataAvailabilityConstants.OM_PHENOMENON_TIME);
            writeNewLine();
            da.getPhenomenonTime().setGmlId(TIME_PERIOD_PREFIX + timePeriodCount++);
            times.put(da.getPhenomenonTime(), da.getPhenomenonTime().getGmlId());
            writeTimePeriod(da.getPhenomenonTime());
            writeNewLine();
            end(GetDataAvailabilityConstants.OM_PHENOMENON_TIME);
        }
    }

    protected void writeFeatureOfInterest(DataAvailability da) throws XMLStreamException {
        empty(GetDataAvailabilityConstants.OM_FEATURE_OF_INTEREST);
        attr(GetDataAvailabilityConstants.XLINK_HREF, da.getFeatureOfInterest().getHref());
        if (da.getFeatureOfInterest().isSetTitle()) {
            attr(GetDataAvailabilityConstants.XLINK_TITLE, da.getFeatureOfInterest().getTitle());
        } else {
            attr(GetDataAvailabilityConstants.XLINK_TITLE, da.getFeatureOfInterest().getTitleFromHref());
        }
    }

    protected void writeProcedure(DataAvailability da) throws XMLStreamException {
        empty(GetDataAvailabilityConstants.OM_PROCEDURE);
        attr(GetDataAvailabilityConstants.XLINK_HREF, da.getProcedure().getHref());
        if (da.getProcedure().isSetTitle()) {
            attr(GetDataAvailabilityConstants.XLINK_TITLE, da.getProcedure().getTitle());
        } else {
            attr(GetDataAvailabilityConstants.XLINK_TITLE, da.getProcedure().getTitleFromHref());
        }
    }

    protected void writeObservedProperty(DataAvailability da) throws XMLStreamException {
        empty(GetDataAvailabilityConstants.OM_OBSERVED_PROPERTY);
        attr(GetDataAvailabilityConstants.XLINK_HREF, da.getObservedProperty().getHref());
        if (da.getObservedProperty().isSetTitle()) {
            attr(GetDataAvailabilityConstants.XLINK_TITLE, da.getObservedProperty().getTitle());
        } else {
            attr(GetDataAvailabilityConstants.XLINK_TITLE, da.getObservedProperty().getTitleFromHref());
        }
    }

    protected void writeTimePeriod(TimePeriod tp) throws XMLStreamException, DateTimeFormatException {
        start(GetDataAvailabilityConstants.GML_TIME_PERIOD);
        attr(GetDataAvailabilityConstants.GML_ID, tp.getGmlId());
        writeNewLine();
        writeBegin(tp);
        writeNewLine();
        writeEnd(tp);
        writeNewLine();
        indent--;
        end(GetDataAvailabilityConstants.GML_TIME_PERIOD);
    }

    protected void writeValueCount(long valueCount) throws XMLStreamException {
        start(GetDataAvailabilityConstants.SOS_VALUE_COUNT);
        chars(Long.toString(valueCount));
        endInline(GetDataAvailabilityConstants.SOS_VALUE_COUNT);
    }

    protected void writeBegin(TimePeriod tp) throws XMLStreamException, DateTimeFormatException {
        start(GetDataAvailabilityConstants.GML_BEGIN_POSITION);
        writeTimeString(tp.getStart(), tp.getTimeFormat());
        endInline(GetDataAvailabilityConstants.GML_BEGIN_POSITION);
    }

    protected void writeEnd(TimePeriod tp) throws XMLStreamException, DateTimeFormatException {
        start(GetDataAvailabilityConstants.GML_END_POSITION);
        writeTimeString(tp.getEnd(), tp.getTimeFormat());
        endInline(GetDataAvailabilityConstants.GML_END_POSITION);
    }

    protected void writeTimeString(DateTime time, TimeFormat format) throws XMLStreamException,
            DateTimeFormatException {
        chars(DateTimeHelper.formatDateTime2String(time, format));
    }
}
