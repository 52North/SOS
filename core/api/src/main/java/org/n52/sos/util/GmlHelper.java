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
package org.n52.sos.util;

import javax.xml.namespace.QName;

import org.joda.time.DateTime;
import org.n52.sos.ogc.gml.GmlConstants;
import org.n52.sos.ogc.gml.time.Time;
import org.n52.sos.ogc.gml.time.TimeInstant;
import org.n52.sos.ogc.gml.time.TimePeriod;

import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.LineString;
import com.vividsolutions.jts.geom.Point;
import com.vividsolutions.jts.geom.Polygon;

/**
 * Helper class for OGC GML. Contains methods to get QName for geometry or time
 * objects.
 * 
 * @since 4.0.0
 * 
 */
public final class GmlHelper {

    public static QName getGml321QnameForGeometry(Geometry geom) {
        if (geom instanceof Point) {
            return new QName(GmlConstants.NS_GML_32, GmlConstants.EN_POINT, GmlConstants.NS_GML);
        } else if (geom instanceof LineString) {
            return new QName(GmlConstants.NS_GML_32, GmlConstants.EN_LINE_STRING, GmlConstants.NS_GML);
        } else if (geom instanceof Polygon) {
            return new QName(GmlConstants.NS_GML_32, GmlConstants.EN_POLYGON, GmlConstants.NS_GML);
        }
        return new QName(GmlConstants.NS_GML_32, GmlConstants.EN_ABSTRACT_GEOMETRY, GmlConstants.NS_GML);
    }

    public static QName getGml321QnameForITime(Time iTime) {
        if (iTime instanceof TimeInstant) {
            return GmlConstants.QN_TIME_INSTANT_32;
        } else if (iTime instanceof TimePeriod) {
            return GmlConstants.QN_TIME_PERIOD_32;
        }
        return GmlConstants.QN_ABSTRACT_TIME_32;
    }

    public static QName getGml311QnameForITime(Time iTime) {
        if (iTime instanceof TimeInstant) {
            return GmlConstants.QN_TIME_INSTANT;
        } else if (iTime instanceof TimePeriod) {
            return GmlConstants.QN_TIME_PERIOD;
        }
        return GmlConstants.QN_ABSTRACT_TIME_32;
    }
    
    /**
     * Create {@link Time} from {@link DateTime}s
     * 
     * @param start
     *            Start {@link DateTime}
     * @param end
     *            End {@link DateTime}
     * @return Resulting {@link Time}
     */
    public static Time createTime(DateTime start, DateTime end) {
        if (start.equals(end)) {
            return new TimeInstant(start);
        } else {
            return new TimePeriod(start, end);
        }
    }

    private GmlHelper() {
    }
}
