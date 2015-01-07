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

import javax.xml.namespace.QName;
import javax.xml.stream.XMLEventFactory;
import javax.xml.stream.XMLEventWriter;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;

import org.joda.time.DateTime;
import org.n52.sos.exception.CodedException;
import org.n52.sos.exception.ows.concrete.DateTimeFormatException;
import org.n52.sos.gda.GetDataAvailabilityResponse.DataAvailability;
import org.n52.sos.ogc.gml.GmlConstants;
import org.n52.sos.ogc.gml.time.Time.TimeFormat;
import org.n52.sos.ogc.gml.time.TimePeriod;
import org.n52.sos.ogc.om.OmConstants;
import org.n52.sos.ogc.sos.Sos2Constants;
import org.n52.sos.ogc.sos.SosConstants;
import org.n52.sos.util.DateTimeHelper;
import org.n52.sos.w3c.W3CConstants;

/**
 * Stream writer for the old GetDataAvailability version
 * 
 * @author Christian Autermann <c.autermann@52north.org>
 * 
 * @since 4.0.0
 */
@Deprecated
public class SosGetDataAvailabilityStreamWriter {
    private static final String TIME_PERIOD_PREFIX = "tp_";

    private static final String DATA_AVAILABILITY_PREFIX = "dam_";

    private final XMLEventFactory eventFactory = XMLEventFactory.newInstance();

    private final XMLOutputFactory outputFactory = XMLOutputFactory.newInstance();

    private final List<DataAvailability> gdas;

    private final Map<TimePeriod, String> times;

    private final String version;

    private int dataAvailabilityCount = 1;

    private int timePeriodCount = 1;

    private XMLEventWriter w;

    public SosGetDataAvailabilityStreamWriter(String version, List<DataAvailability> gdas) {
        this.gdas = gdas == null ? Collections.<DataAvailability> emptyList() : gdas;
        this.times = new HashMap<TimePeriod, String>(this.gdas.size());
        this.version = version == null ? Sos2Constants.SERVICEVERSION : version;
    }

    protected void attr(QName name, String value) throws XMLStreamException {
        w.add(eventFactory.createAttribute(name, value));
    }

    protected void attr(String name, String value) throws XMLStreamException {
        w.add(eventFactory.createAttribute(name, value));
    }

    protected void chars(String chars) throws XMLStreamException {
        w.add(eventFactory.createCharacters(chars));
    }
    
    protected void comment(String text) throws XMLStreamException {
        w.add(eventFactory.createComment(text));
    }

    protected void end(QName name) throws XMLStreamException {
        w.add(eventFactory.createEndElement(name.getPrefix(), name.getNamespaceURI(), name.getLocalPart()));
    }

    protected void end() throws XMLStreamException {
        w.add(eventFactory.createEndDocument());
    }

    protected void namespace(String prefix, String namespace) throws XMLStreamException {
        w.add(eventFactory.createNamespace(prefix, namespace));
    }

    protected void start(QName name) throws XMLStreamException {
        w.add(eventFactory.createStartElement(name.getPrefix(), name.getNamespaceURI(), name.getLocalPart()));
    }

    protected void start() throws XMLStreamException {
        w.add(eventFactory.createStartDocument());
    }

    public void write(OutputStream out) throws XMLStreamException, CodedException {
        this.w = outputFactory.createXMLEventWriter(out, "UTF-8");
        start();
        writeGetDataAvailabilityResponse();
        end();
        this.w.flush();
        this.w.close();
    }

    protected void writeGetDataAvailabilityResponse() throws XMLStreamException, CodedException {
        start(GetDataAvailabilityConstants.SOS_GET_DATA_AVAILABILITY_RESPONSE);
        attr(GetDataAvailabilityConstants.AN_SERVICE, SosConstants.SOS);
        attr(GetDataAvailabilityConstants.AN_VERSION, version);
        namespace(SosConstants.NS_SOS_PREFIX, Sos2Constants.NS_SOS_20);
        namespace(GmlConstants.NS_GML_PREFIX, GmlConstants.NS_GML_32);
        namespace(OmConstants.NS_OM_PREFIX, OmConstants.NS_OM_2);
        namespace(W3CConstants.NS_XLINK_PREFIX, W3CConstants.NS_XLINK);
        comment(String.format("You requested the old GetDataAvailability request version which is deprecated. Please check the XML schema: %s", GetDataAvailabilityConstants.SCHEMA_LOCATION_URL_GET_DATA_AVAILABILITY));
        for (DataAvailability da : this.gdas) {
            wirteDataAvailabilityMember(da);
        }
        end(GetDataAvailabilityConstants.SOS_GET_DATA_AVAILABILITY_RESPONSE);
    }

    protected void wirteDataAvailabilityMember(DataAvailability da) throws XMLStreamException, DateTimeFormatException {
        if (da != null) {
            start(GetDataAvailabilityConstants.SOS_DATA_AVAILABILITY_MEMBER);
            attr(GmlConstants.QN_ID_32, DATA_AVAILABILITY_PREFIX + dataAvailabilityCount++);
            writeFeatureOfInterest(da);
            writeProcedure(da);
            writeObservedProperty(da);
            writePhenomenonTime(da);
            if (da.isSetCount()) {
                writeValueCount(da.getCount());
            }
            end(GetDataAvailabilityConstants.SOS_DATA_AVAILABILITY_MEMBER);
        }
    }

    protected void writePhenomenonTime(DataAvailability da) throws DateTimeFormatException, XMLStreamException {
        start(GetDataAvailabilityConstants.OM_PHENOMENON_TIME);
        if (times.containsKey(da.getPhenomenonTime())) {
            attr(GetDataAvailabilityConstants.XLINK_HREF, "#" + times.get(da.getPhenomenonTime()));
        } else {
            da.getPhenomenonTime().setGmlId(TIME_PERIOD_PREFIX + timePeriodCount++);
            times.put(da.getPhenomenonTime(), da.getPhenomenonTime().getGmlId());
            writeTimePeriod(da.getPhenomenonTime());
        }
        end(GetDataAvailabilityConstants.OM_PHENOMENON_TIME);
    }

    protected void writeFeatureOfInterest(DataAvailability da) throws XMLStreamException {
        start(GetDataAvailabilityConstants.OM_FEATURE_OF_INTEREST);
        attr(GetDataAvailabilityConstants.XLINK_HREF, da.getFeatureOfInterest().getHref());
        if (da.getFeatureOfInterest().isSetTitle()) {
            attr(GetDataAvailabilityConstants.XLINK_TITLE, da.getFeatureOfInterest().getTitle());
        } else {
            attr(GetDataAvailabilityConstants.XLINK_TITLE, da.getFeatureOfInterest().getTitleFromHref());
        }
        end(GetDataAvailabilityConstants.OM_FEATURE_OF_INTEREST);
    }

    protected void writeProcedure(DataAvailability da) throws XMLStreamException {
        start(GetDataAvailabilityConstants.OM_PROCEDURE);
        attr(GetDataAvailabilityConstants.XLINK_HREF, da.getProcedure().getHref());
        if (da.getProcedure().isSetTitle()) {
            attr(GetDataAvailabilityConstants.XLINK_TITLE, da.getProcedure().getTitle());
        } else {
            attr(GetDataAvailabilityConstants.XLINK_TITLE, da.getProcedure().getTitleFromHref());
        }
        end(GetDataAvailabilityConstants.OM_PROCEDURE);
    }

    protected void writeObservedProperty(DataAvailability da) throws XMLStreamException {
        start(GetDataAvailabilityConstants.OM_OBSERVED_PROPERTY);
        attr(GetDataAvailabilityConstants.XLINK_HREF, da.getObservedProperty().getHref());
        if (da.getObservedProperty().isSetTitle()) {
            attr(GetDataAvailabilityConstants.XLINK_TITLE, da.getObservedProperty().getTitle());
        } else {
            attr(GetDataAvailabilityConstants.XLINK_TITLE, da.getObservedProperty().getTitleFromHref());
        }
        end(GetDataAvailabilityConstants.OM_OBSERVED_PROPERTY);
    }

    protected void writeTimePeriod(TimePeriod tp) throws XMLStreamException, DateTimeFormatException {
        start(GmlConstants.QN_TIME_PERIOD_32);
        attr(GmlConstants.QN_ID_32, tp.getGmlId());
        writeBegin(tp);
        writeEnd(tp);
        end(GmlConstants.QN_TIME_PERIOD_32);
    }
    
    protected void writeValueCount(long valueCount) throws XMLStreamException {
        start(GetDataAvailabilityConstants.SOS_COUNT);
        chars(Long.toString(valueCount));
        end(GetDataAvailabilityConstants.SOS_COUNT);
    }

    protected void writeBegin(TimePeriod tp) throws XMLStreamException, DateTimeFormatException {
        start(GmlConstants.QN_BEGIN_POSITION_32);
        writeTimeString(tp.getStart(), tp.getTimeFormat());
        end(GmlConstants.QN_BEGIN_POSITION_32);
    }

    protected void writeEnd(TimePeriod tp) throws XMLStreamException, DateTimeFormatException {
        start(GmlConstants.QN_END_POSITION_32);
        writeTimeString(tp.getEnd(), tp.getTimeFormat());
        end(GmlConstants.QN_END_POSITION_32);
    }

    protected void writeTimeString(DateTime time, TimeFormat format) throws XMLStreamException,
            DateTimeFormatException {
        chars(DateTimeHelper.formatDateTime2String(time, format));
    }
}
