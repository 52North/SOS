/*
 * Copyright (C) 2012-2018 52°North Initiative for Geospatial Open Source
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

import java.util.LinkedList;
import java.util.List;

import org.n52.shetland.ogc.ows.exception.CodedException;
import org.n52.shetland.ogc.ows.exception.NoApplicableCodeException;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Envelope;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.PrecisionModel;
import com.vividsolutions.jts.io.ParseException;
import com.vividsolutions.jts.io.WKTReader;
import com.vividsolutions.jts.io.WKTWriter;

public interface JTSConverter {


    public static Geometry convert(org.locationtech.jts.geom.Geometry geometry) throws CodedException {
        try {
            return new WKTReader(convertGeometryFactory(geometry.getFactory())).read(new org.locationtech.jts.io.WKTWriter().write(geometry));
        } catch (ParseException e) {
            throw new NoApplicableCodeException().causedBy(e);
        }
    }

    public static org.locationtech.jts.geom.Geometry convert(Geometry geometry) throws CodedException {
        try {
            return new org.locationtech.jts.io.WKTReader(convertGeometryFactory(geometry.getFactory())).read(new WKTWriter().write(geometry));
        } catch (org.locationtech.jts.io.ParseException e) {
            throw new NoApplicableCodeException().causedBy(e);
        }
    }

    public static GeometryFactory convertGeometryFactory(org.locationtech.jts.geom.GeometryFactory factory) {
        return new GeometryFactory(new PrecisionModel(), factory.getSRID());
    }

    public static org.locationtech.jts.geom.GeometryFactory convertGeometryFactory(GeometryFactory factory) {
        return new org.locationtech.jts.geom.GeometryFactory(new org.locationtech.jts.geom.PrecisionModel(), factory.getSRID());
    }


    public static Coordinate[] convert(org.locationtech.jts.geom.Coordinate[] coordinates) {
        List<Coordinate> l = new LinkedList<Coordinate>();
        for (org.locationtech.jts.geom.Coordinate c : coordinates) {
            l.add(convert(c));
        }
        return l.toArray(new Coordinate[0]);
    }

    public static org.locationtech.jts.geom.Coordinate[] convert(Coordinate[] coordinates) {
        List<org.locationtech.jts.geom.Coordinate> l = new LinkedList<org.locationtech.jts.geom.Coordinate>();
        for (Coordinate c : coordinates) {
            l.add(convert(c));
        }
        return l.toArray(new org.locationtech.jts.geom.Coordinate[0]);
    }

    public static Coordinate convert(org.locationtech.jts.geom.Coordinate c) {
        return new Coordinate(c.x, c.y, c.z);
    }

    public static org.locationtech.jts.geom.Coordinate convert(Coordinate c) {
        return new org.locationtech.jts.geom.Coordinate(c.x, c.y, c.z);
    }

    public static Envelope convert(org.locationtech.jts.geom.Envelope e) {
        return new Envelope(e.getMinX(), e.getMaxX(), e.getMinY(), e.getMaxY());
    }

    public static org.locationtech.jts.geom.Envelope convert(Envelope e) {
        return new org.locationtech.jts.geom.Envelope(e.getMinX(), e.getMaxX(), e.getMinY(), e.getMaxY());
    }
}
