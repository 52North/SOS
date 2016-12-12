/*
 * Copyright (C) 2012-2016 52°North Initiative for Geospatial Open Source
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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.n52.shetland.ogc.ows.exception.OwsExceptionReport;
import org.n52.svalbard.decode.exception.DecodingException;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.CoordinateFilter;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.PrecisionModel;
import com.vividsolutions.jts.io.ParseException;
import com.vividsolutions.jts.io.WKTReader;

/**
 * Utility class for the Java Topology Suite.
 *
 * @since 4.0.0
 *
 */
public class JTSHelper {

    private static final Logger LOGGER = LoggerFactory.getLogger(JTSHelper.class);
    public static final String WKT_POLYGON = "Polygon";
    public static final String WKT_POINT = "Point";

    public static final CoordinateFilter COORDINATE_SWITCHING_FILTER = coord -> {
        double tmp = coord.x;
        coord.x = coord.y;
        coord.y = tmp;
    };

    protected JTSHelper() {
    }

    /**
     * Creates a JTS Geometry from an WKT representation. Switches the
     * coordinate order if needed.
     *
     * @param wkt
     *            WKT representation of the geometry
     * @param srid
     *            the SRID of the newly created geometry
     * @return JTS Geometry object
     * @throws DecodingException
     *             If an error occurs
     */
    public static Geometry createGeometryFromWKT(String wkt, int srid) throws DecodingException {
        WKTReader wktReader = getWKTReaderForSRID(srid);
        try {
            LOGGER.debug("FOI Geometry: {}", wkt);
            return wktReader.read(wkt);
        } catch (ParseException pe) {
            throw new DecodingException("Error while parsing the geometry of featureOfInterest parameter", pe);
        }
    }

    public static WKTReader getWKTReaderForSRID(int srid) throws DecodingException {
        if (srid <= 0) {
            return new WKTReader(new GeometryFactory());
        }
        return new WKTReader(getGeometryFactoryForSRID(srid));
    }

    /**
     * Get the coordinates of a Geometry as String.
     *
     * @param geom
     *            Geometry to get coordinates
     * @return Coordinates as String
     */
    public static String getCoordinatesString(Geometry geom) {
        StringBuilder builder = new StringBuilder();
        Coordinate[] sourceCoords = geom.getCoordinates();
        if (sourceCoords.length > 0) {
            getCoordinateString(builder, sourceCoords[0]);
            for (int i = 1; i < sourceCoords.length; ++i) {
                getCoordinateString(builder.append(' '), sourceCoords[i]);
            }
        }
        return builder.toString();
    }

    protected static StringBuilder getCoordinateString(StringBuilder builder, Coordinate coordinate) {
        builder.append(coordinate.x);
        builder.append(' ');
        builder.append(coordinate.y);
        if (!Double.isNaN(coordinate.z)) {
            builder.append(' ');
            builder.append(coordinate.z);
        }
        return builder;
    }

    /**
     * Creates a WKT Polygon representation from lower and upper corner values.
     *
     * @param lowerCorner
     *            Lower corner coordinates
     * @param upperCorner
     *            Upper corner coordinates
     * @return WKT Polygon
     */
    public static String createWKTPolygonFromEnvelope(String lowerCorner, String upperCorner) {
        final String[] splittedLowerCorner = lowerCorner.split(" ");
        final String[] splittedUpperCorner = upperCorner.split(" ");
        String minx = splittedLowerCorner[0];
        String miny = splittedLowerCorner[1];
        String maxx = splittedUpperCorner[0];
        String maxy = splittedUpperCorner[1];

        return createWKTPolygonFromEnvelope(minx, miny, maxx, maxy);
    }

    private static String createWKTPolygonFromEnvelope(String minx, String miny, String maxx, String maxy) {
        StringBuilder sb = new StringBuilder();
        sb.append(WKT_POLYGON).append(" ((");
        sb.append(minx).append(' ').append(miny).append(',');
        sb.append(minx).append(' ').append(maxy).append(',');
        sb.append(maxx).append(' ').append(maxy).append(',');
        sb.append(maxx).append(' ').append(miny).append(',');
        sb.append(minx).append(' ').append(miny).append("))");
        return sb.toString();
    }

    public static Geometry createPolygonFromEnvelope(double[] envelope, int srid) {
        if (envelope.length != 4) {
            throw new IllegalArgumentException();
        }
        return createPolygonFromEnvelope(envelope[0], envelope[1], envelope[2], envelope[3], srid);
    }

    public static Geometry createPolygonFromEnvelope(double minx, double miny, double maxx, double maxy, int srid) {
        GeometryFactory fac = getGeometryFactoryForSRID(srid);
        return fac.createPolygon(new Coordinate[] {
            new Coordinate(minx, miny),
            new Coordinate(minx, maxy),
            new Coordinate(maxx, maxy),
            new Coordinate(maxx, miny),
            new Coordinate(minx, miny)
        });
    }

    /**
     * Switches the coordinates of a JTS Geometry.
     *
     * @param <G>
     *            the geometry type
     * @param geometry
     *            Geometry to switch coordinates.
     * @return Geometry with switched coordinates
     * @throws OwsExceptionReport
     */
    public static <G extends Geometry> G switchCoordinateAxisOrder(G geometry) throws OwsExceptionReport {
        if (geometry == null) {
            return null;
        }
        @SuppressWarnings("unchecked")
        G geom = (G) geometry.clone();
        geom.apply(COORDINATE_SWITCHING_FILTER);
        geom.geometryChanged();
        return geom;
    }

    public static GeometryFactory getGeometryFactory(Geometry geometry) {
        if (geometry.getFactory().getSRID() > 0 || geometry.getSRID() == 0) {
            return geometry.getFactory();
        } else {
            return getGeometryFactoryForSRID(geometry.getSRID());
        }
    }

    public static GeometryFactory getGeometryFactoryForSRID(int srid) {
        return new GeometryFactory(new PrecisionModel(PrecisionModel.FLOATING), srid);
    }

    /**
     * Creates a WKT Point string form coordinate string.
     *
     * @param coordinates
     *            Coordinate string
     * @return WKT Point string
     */
    public static String createWKTPointFromCoordinateString(String coordinates) {
        return WKT_POINT + "(" + coordinates + ")";
    }

    public static boolean isNotEmpty(Geometry geometry) {
        return geometry != null && !geometry.isEmpty();
    }

}
