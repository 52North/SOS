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

import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.Envelope;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.PrecisionModel;
import org.locationtech.jts.io.ParseException;
import org.locationtech.jts.io.WKTReader;
import org.locationtech.jts.io.WKTWriter;
import org.n52.shetland.ogc.ows.exception.CodedException;
import org.n52.shetland.ogc.ows.exception.NoApplicableCodeException;

/**
 * Interface to convert JTS from Vividsolutions to the successor JTS from
 * LocationTech.
 *
 * @author <a href="mailto:c.hollmann@52north.org">Carsten Hollmann</a>
 *
 * @since 5.0.2
 *
 */
public interface JTSConverter {

    /**
     * Convert the {@link com.vividsolutions.jts.geom.Geometry} to
     * {@link Geometry}
     *
     * @param geometry
     *            {@link com.vividsolutions.jts.geom.Geometry} to convert
     * @return converted {@link Geometry}
     * @throws CodedException
     *             If an error occurs during conversion. If an error occurs
     *             during conversion.
     */
    public static Geometry convert(com.vividsolutions.jts.geom.Geometry geometry)
            throws CodedException {
        try {
            return geometry != null ? new WKTReader(convertGeometryFactory(geometry.getFactory()))
                    .read(new com.vividsolutions.jts.io.WKTWriter().write(geometry)) : null;
        } catch (ParseException e) {
            throw new NoApplicableCodeException().causedBy(e);
        }
    }

    /**
     * Convert the {@link Geometry} to
     * {@link com.vividsolutions.jts.geom.Geometry}
     *
     * @param geometry
     *            {@link Geometry} to convert
     * @return converted {@link com.vividsolutions.jts.geom.Geometry}
     * @throws CodedException
     *             If an error occurs during conversion.
     */
    public static com.vividsolutions.jts.geom.Geometry convert(Geometry geometry)
            throws CodedException {
        try {
            return geometry != null
                    ? new com.vividsolutions.jts.io.WKTReader(convertGeometryFactory(geometry.getFactory()))
                            .read(new WKTWriter().write(geometry))
                    : null;
        } catch (com.vividsolutions.jts.io.ParseException e) {
            throw new NoApplicableCodeException().causedBy(e);
        }
    }

    /**
     * Convert the {@link com.vividsolutions.jts.geom.GeometryFactory} to
     * {@link GeometryFactory}
     *
     * @param factory
     *            {@link com.vividsolutions.jts.geom.GeometryFactory} to convert
     * @return converted {@link GeometryFactory}
     */
    public static GeometryFactory convertGeometryFactory(com.vividsolutions.jts.geom.GeometryFactory factory) {
        return factory != null ? new GeometryFactory(new PrecisionModel(), factory.getSRID()) : null;
    }

    /**
     * Convert the {@link GeometryFactory} to
     * {@link com.vividsolutions.jts.geom.GeometryFactory}
     *
     * @param factory
     *            {@link GeometryFactory} to convert
     * @return converted {@link com.vividsolutions.jts.geom.GeometryFactory}
     */
    public static com.vividsolutions.jts.geom.GeometryFactory convertGeometryFactory(GeometryFactory factory) {
        return factory != null
                ? new com.vividsolutions.jts.geom.GeometryFactory(new com.vividsolutions.jts.geom.PrecisionModel(),
                        factory.getSRID())
                : null;
    }

    /**
     * Convert {@link com.vividsolutions.jts.geom.Coordinate} array to
     * {@link Coordinate} array
     *
     * @param coordinates
     *            {@link com.vividsolutions.jts.geom.Coordinate} array to
     *            convert
     * @return converted {@link Coordinate} array
     */
    public static Coordinate[] convert(com.vividsolutions.jts.geom.Coordinate[] coordinates) {
        if (coordinates != null) {
            List<Coordinate> l = new LinkedList<Coordinate>();
            for (com.vividsolutions.jts.geom.Coordinate c : coordinates) {
                l.add(convert(c));
            }
            return l.toArray(new Coordinate[0]);
        }
        return null;
    }

    /**
     * Convert {@link Coordinate} array to
     * {@link com.vividsolutions.jts.geom.Coordinate} array
     *
     * @param coordinates
     *            {@link Coordinate} array to convert
     * @return converted {@link com.vividsolutions.jts.geom.Coordinate} array
     */
    public static com.vividsolutions.jts.geom.Coordinate[] convert(Coordinate[] coordinates) {
        if (coordinates != null) {
            List<com.vividsolutions.jts.geom.Coordinate> l = new LinkedList<com.vividsolutions.jts.geom.Coordinate>();
            for (Coordinate c : coordinates) {
                l.add(convert(c));
            }
            return l.toArray(new com.vividsolutions.jts.geom.Coordinate[0]);
        }
        return null;
    }

    /**
     * Convert {@link com.vividsolutions.jts.geom.Coordinate} to
     * {@link Coordinate}
     *
     * @param c
     *            {@link com.vividsolutions.jts.geom.Coordinate} to convert
     * @return converted {@link Coordinate}
     */
    public static Coordinate convert(com.vividsolutions.jts.geom.Coordinate c) {
        return c != null ? new Coordinate(c.x, c.y, c.z) : null;
    }

    /**
     * Convert {@link Coordinate} to
     * {@link com.vividsolutions.jts.geom.Coordinate}
     *
     * @param c
     *            {@link Coordinate} to convert
     * @return converted {@link com.vividsolutions.jts.geom.Coordinate}
     */
    public static com.vividsolutions.jts.geom.Coordinate convert(Coordinate c) {
        return c != null ? new com.vividsolutions.jts.geom.Coordinate(c.x, c.y, c.z) : null;
    }

    /**
     * Convert {@link com.vividsolutions.jts.geom.Envelope} to {@link Envelope}
     *
     * @param e
     *            {@link com.vividsolutions.jts.geom.Envelope} to convert
     * @return converted {@link Envelope}
     */
    public static Envelope convert(com.vividsolutions.jts.geom.Envelope e) {
        return e != null ? new Envelope(e.getMinX(), e.getMaxX(), e.getMinY(), e.getMaxY()) : null;
    }

    /**
     * Convert {@link Envelope} to {@link com.vividsolutions.jts.geom.Envelope}
     *
     * @param e
     *            {@link Envelope} to convert
     * @return converted {@link com.vividsolutions.jts.geom.Envelope}
     */
    public static com.vividsolutions.jts.geom.Envelope convert(Envelope e) {
        return e != null ? new com.vividsolutions.jts.geom.Envelope(e.getMinX(), e.getMaxX(), e.getMinY(), e.getMaxY())
                : null;
    }
}
