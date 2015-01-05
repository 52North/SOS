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

import org.n52.sos.exception.ows.InvalidParameterValueException;
import org.n52.sos.exception.ows.concrete.InvalidSridException;
import org.n52.sos.ogc.ows.OwsExceptionReport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
public class JTSHelper implements Constants {

    private static final Logger LOGGER = LoggerFactory.getLogger(JTSHelper.class);

    public static final CoordinateFilter COORDINATE_SWITCHING_FILTER = new CoordinateFilter() {
        @Override
        public void filter(Coordinate coord) {
            double tmp = coord.x;
            coord.x = coord.y;
            coord.y = tmp;
        }
    };

    /**
     * Creates a JTS Geometry from an WKT representation. Switches the
     * coordinate order if needed.
     * <p/>
     * 
     * @param wkt
     *            WKT representation of the geometry
     * @param srid
     *            the SRID of the newly created geometry
     *            <p/>
     * @return JTS Geometry object
     *         <p/>
     * @throws OwsExceptionReport
     *             If an error occurs
     */
    public static Geometry createGeometryFromWKT(String wkt, int srid) throws OwsExceptionReport {
        WKTReader wktReader = getWKTReaderForSRID(srid);
        try {
            LOGGER.debug("FOI Geometry: {}", wkt);
            return wktReader.read(wkt);
        } catch (ParseException pe) {
            throw new InvalidParameterValueException().causedBy(pe).withMessage(
                    "Error while parsing the geometry of featureOfInterest parameter");
        }
    }

    public static WKTReader getWKTReaderForSRID(int srid) throws OwsExceptionReport {
        if (srid <= 0) {
            throw new InvalidSridException(srid);
        }
        return new WKTReader(getGeometryFactoryForSRID(srid));
    }

    /**
     * Get the coordinates of a Geometry as String.
     * 
     * @param geom
     *            Geometry to get coordinates
     *            <p/>
     * @return Coordinates as String
     *         <p/>
     * @throws OwsExceptionReport
     *             if the SRID is <= 0
     */
    public static String getCoordinatesString(Geometry geom) throws OwsExceptionReport {
        StringBuilder builder = new StringBuilder();
        Coordinate[] sourceCoords = geom.getCoordinates();
        if (sourceCoords.length > 0) {
            getCoordinateString(builder, sourceCoords[0]);
            for (int i = 1; i < sourceCoords.length; ++i) {
                getCoordinateString(builder.append(BLANK_CHAR), sourceCoords[i]);
            }
        }
        return builder.toString();
    }

    protected static StringBuilder getCoordinateString(StringBuilder builder, Coordinate coordinate) {
        builder.append(coordinate.x);
        builder.append(BLANK_CHAR);
        builder.append(coordinate.y);
        if (!Double.isNaN(coordinate.z)) {
            builder.append(BLANK_CHAR);
            builder.append(coordinate.z);
        }
        return builder;
    }

    /**
     * Creates a WKT Polygon representation from lower and upper corner values.
     * <p/>
     * 
     * @param lowerCorner
     *            Lower corner coordinates
     * @param upperCorner
     *            Upper corner coordinates
     *            <p/>
     * @return WKT Polygon
     */
    public static String createWKTPolygonFromEnvelope(String lowerCorner, String upperCorner) {
        final String[] splittedLowerCorner = lowerCorner.split(BLANK_STRING);
        final String[] splittedUpperCorner = upperCorner.split(BLANK_STRING);
        final String minX = splittedLowerCorner[0];
        final String minY = splittedLowerCorner[1];
        final String maxX = splittedUpperCorner[0];
        final String maxY = splittedUpperCorner[1];
        StringBuilder sb = new StringBuilder();
        sb.append(JTSConstants.WKT_POLYGON).append(" ((");
        sb.append(minX).append(BLANK_CHAR).append(minY).append(COMMA_CHAR);
        sb.append(minX).append(BLANK_CHAR).append(maxY).append(COMMA_CHAR);
        sb.append(maxX).append(BLANK_CHAR).append(maxY).append(COMMA_CHAR);
        sb.append(maxX).append(BLANK_CHAR).append(minY).append(COMMA_CHAR);
        sb.append(minX).append(BLANK_CHAR).append(minY).append("))");
        return sb.toString();
    }

    /**
     * Switches the coordinates of a JTS Geometry.
     * <p/>
     * 
     * @param <G>
     *            the geometry type
     * @param geometry
     *            Geometry to switch coordinates.
     *            <p/>
     * @return Geometry with switched coordinates
     *         <p/>
     * @throws OwsExceptionReport
     *             *
     *             <p/>
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
        return JTSConstants.WKT_POINT + "(" + coordinates + ")";
    }

    protected JTSHelper() {
    }

    public static boolean isNotEmpty(Geometry geometry) {
        return geometry != null && !geometry.isEmpty();
    }
}
