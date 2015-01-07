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
package org.n52.sos.encode.json.base;

import static org.n52.sos.util.DateTimeHelper.formatDateTime2IsoString;
import static org.n52.sos.util.DateTimeHelper.formatDateTime2String;

import org.n52.sos.encode.json.JSONEncoder;
import org.n52.sos.exception.ows.concrete.DateTimeFormatException;
import org.n52.sos.ogc.gml.time.Time;
import org.n52.sos.ogc.gml.time.TimeInstant;
import org.n52.sos.ogc.gml.time.TimePeriod;
import org.n52.sos.ogc.gml.time.TimePosition;
import org.n52.sos.ogc.ows.OwsExceptionReport;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;

/**
 * TODO JavaDoc
 * 
 * @author Christian Autermann <c.autermann@52north.org>
 * 
 * @since 4.0.0
 */
public class TimeJSONEncoder extends JSONEncoder<Time> {
    public TimeJSONEncoder() {
        super(Time.class);
    }

    private String encodeTimePosition(TimePosition timePosition) throws DateTimeFormatException {
        if (timePosition.isSetIndeterminateValue()) {
            return timePosition.getIndeterminateValue().name();
        } else if (timePosition.isSetTimeFormat()) {
            return formatDateTime2String(timePosition.getTime(), timePosition.getTimeFormat());
        } else if (timePosition.isSetTime()) {
            return formatDateTime2IsoString(timePosition.getTime());
        } else {
            return null;
        }
    }

    @Override
    public JsonNode encodeJSON(Time time) throws OwsExceptionReport {
        if (time instanceof TimeInstant) {
            TimeInstant ti = (TimeInstant) time;
            return nodeFactory().textNode(encodeTimePosition(ti.getTimePosition()));
        }
        if (time instanceof TimePeriod) {
            TimePeriod tp = (TimePeriod) time;
            ArrayNode a = nodeFactory().arrayNode();
            a.add(encodeTimePosition(tp.getStartTimePosition()));
            a.add(encodeTimePosition(tp.getEndTimePosition()));
            return a;
        } else {
            return null;
        }
    }
}
