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
package org.n52.sos.encode.json.impl;

import static org.n52.sos.coding.json.JSONConstants.COORDINATES;
import static org.n52.sos.coding.json.JSONConstants.CRS;
import static org.n52.sos.coding.json.JSONConstants.GEOMETRIES;
import static org.n52.sos.coding.json.JSONConstants.GEOMETRY_COLLECTION;
import static org.n52.sos.coding.json.JSONConstants.HREF;
import static org.n52.sos.coding.json.JSONConstants.LINE_STRING;
import static org.n52.sos.coding.json.JSONConstants.LINK;
import static org.n52.sos.coding.json.JSONConstants.MULTI_LINE_STRING;
import static org.n52.sos.coding.json.JSONConstants.MULTI_POINT;
import static org.n52.sos.coding.json.JSONConstants.MULTI_POLYGON;
import static org.n52.sos.coding.json.JSONConstants.POINT;
import static org.n52.sos.coding.json.JSONConstants.POLYGON;
import static org.n52.sos.coding.json.JSONConstants.PROPERTIES;
import static org.n52.sos.coding.json.JSONConstants.TYPE;

import org.n52.sos.encode.json.JSONEncoder;
import org.n52.sos.encode.json.JSONEncodingException;
import org.n52.sos.util.Constants;

import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.common.base.Preconditions;
import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.CoordinateSequence;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryCollection;
import com.vividsolutions.jts.geom.LineString;
import com.vividsolutions.jts.geom.MultiLineString;
import com.vividsolutions.jts.geom.MultiPoint;
import com.vividsolutions.jts.geom.MultiPolygon;
import com.vividsolutions.jts.geom.Point;
import com.vividsolutions.jts.geom.Polygon;

/**
 * TODO JavaDoc
 * 
 * @author Christian Autermann <autermann@uni-muenster.de>
 * 
 * @since 4.0.0
 */
public class GeoJSONEncoder extends JSONEncoder<Geometry> {
    public static final int DEFAULT_SRID = Constants.EPSG_WGS84;

    public static final String SRID_LINK_PREFIX = "http://www.opengis.net/def/crs/EPSG/0/";

    private final JsonNodeFactory jsonFactory = JsonNodeFactory.withExactBigDecimals(false);

    public GeoJSONEncoder() {
        super(Geometry.class);
    }

    @Override
    public ObjectNode encodeJSON(Geometry value) throws JSONEncodingException {
        if (value == null) {
            return null;
        } else {
            return encodeGeometry(value, DEFAULT_SRID);
        }
    }

    protected ObjectNode encodeGeometry(Geometry geometry, int parentSrid) throws JSONEncodingException {
        Preconditions.checkNotNull(geometry);
        if (geometry.isEmpty()) {
            return null;
        } else if (geometry instanceof Point) {
            return encode((Point) geometry, parentSrid);
        } else if (geometry instanceof LineString) {
            return encode((LineString) geometry, parentSrid);
        } else if (geometry instanceof Polygon) {
            return encode((Polygon) geometry, parentSrid);
        } else if (geometry instanceof MultiPoint) {
            return encode((MultiPoint) geometry, parentSrid);
        } else if (geometry instanceof MultiLineString) {
            return encode((MultiLineString) geometry, parentSrid);
        } else if (geometry instanceof MultiPolygon) {
            return encode((MultiPolygon) geometry, parentSrid);
        } else if (geometry instanceof GeometryCollection) {
            return encode((GeometryCollection) geometry, parentSrid);
        } else {
            throw new JSONEncodingException("unknown geometry type " + geometry.getGeometryType());
        }
    }

    protected ObjectNode encode(Point geometry, int parentSrid) {
        Preconditions.checkNotNull(geometry);
        ObjectNode json = jsonFactory.objectNode();
        json.put(TYPE, POINT);
        json.put(COORDINATES, encodeCoordinates(geometry));
        encodeCRS(json, geometry, parentSrid);
        return json;
    }

    protected ObjectNode encode(LineString geometry, int parentSrid) {
        Preconditions.checkNotNull(geometry);
        ObjectNode json = jsonFactory.objectNode();
        json.put(TYPE, LINE_STRING).put(COORDINATES, encodeCoordinates(geometry));
        encodeCRS(json, geometry, parentSrid);
        return json;
    }

    protected ObjectNode encode(Polygon geometry, int parentSrid) {
        Preconditions.checkNotNull(geometry);
        ObjectNode json = jsonFactory.objectNode();
        json.put(TYPE, POLYGON).put(COORDINATES, encodeCoordinates(geometry));
        encodeCRS(json, geometry, parentSrid);
        return json;
    }

    protected ObjectNode encode(MultiPoint geometry, int parentSrid) {
        Preconditions.checkNotNull(geometry);
        ObjectNode json = jsonFactory.objectNode();
        ArrayNode list = json.put(TYPE, MULTI_POINT).putArray(COORDINATES);
        for (int i = 0; i < geometry.getNumGeometries(); ++i) {
            list.add(encodeCoordinates((Point) geometry.getGeometryN(i)));
        }
        encodeCRS(json, geometry, parentSrid);
        return json;
    }

    protected ObjectNode encode(MultiLineString geometry, int parentSrid) {
        Preconditions.checkNotNull(geometry);
        ObjectNode json = jsonFactory.objectNode();
        ArrayNode list = json.put(TYPE, MULTI_LINE_STRING).putArray(COORDINATES);
        for (int i = 0; i < geometry.getNumGeometries(); ++i) {
            list.add(encodeCoordinates((LineString) geometry.getGeometryN(i)));
        }
        encodeCRS(json, geometry, parentSrid);
        return json;
    }

    protected ObjectNode encode(MultiPolygon geometry, int parentSrid) {
        Preconditions.checkNotNull(geometry);
        ObjectNode json = jsonFactory.objectNode();
        ArrayNode list = json.put(TYPE, MULTI_POLYGON).putArray(COORDINATES);
        for (int i = 0; i < geometry.getNumGeometries(); ++i) {
            list.add(encodeCoordinates((Polygon) geometry.getGeometryN(i)));
        }
        encodeCRS(json, geometry, parentSrid);
        return json;
    }

    public ObjectNode encode(GeometryCollection geometry, int parentSrid) throws JSONEncodingException {
        Preconditions.checkNotNull(geometry);
        ObjectNode json = jsonFactory.objectNode();
        ArrayNode geometries = json.put(TYPE, GEOMETRY_COLLECTION).putArray(GEOMETRIES);
        int srid = encodeCRS(json, geometry, parentSrid);
        for (int i = 0; i < geometry.getNumGeometries(); ++i) {
            geometries.add(encodeGeometry(geometry.getGeometryN(i), srid));
        }
        return json;
    }

    protected ArrayNode encodeCoordinate(Coordinate coordinate) {

        ArrayNode array = jsonFactory.arrayNode().add(coordinate.x).add(coordinate.y);

        if (!Double.isNaN(coordinate.z)) {
            array.add(coordinate.z);
        }

        return array;
    }

    protected ArrayNode encodeCoordinates(CoordinateSequence coordinates) {
        ArrayNode list = jsonFactory.arrayNode();
        for (int i = 0; i < coordinates.size(); ++i) {
            list.add(encodeCoordinate(coordinates.getCoordinate(i)));
        }
        return list;
    }

    protected ArrayNode encodeCoordinates(Point geometry) {
        return encodeCoordinate(geometry.getCoordinate());
    }

    protected ArrayNode encodeCoordinates(LineString geometry) {
        return encodeCoordinates(geometry.getCoordinateSequence());
    }

    protected ArrayNode encodeCoordinates(Polygon geometry) {
        ArrayNode list = jsonFactory.arrayNode();
        list.add(encodeCoordinates(geometry.getExteriorRing()));
        for (int i = 0; i < geometry.getNumInteriorRing(); ++i) {
            list.add(encodeCoordinates(geometry.getInteriorRingN(i)));
        }
        return list;
    }

    protected int encodeCRS(ObjectNode json, Geometry geometry, int parentSrid) {
        return encodeCRS(geometry.getSRID(), parentSrid, json);
    }

    protected int encodeCRS(int srid, int parentSrid, ObjectNode json) {
        if (srid == parentSrid || srid == 0 || (parentSrid == DEFAULT_SRID && srid == DEFAULT_SRID)) {
            return parentSrid;
        } else {
            json.putObject(CRS).put(TYPE, LINK).putObject(PROPERTIES).put(HREF, SRID_LINK_PREFIX + srid);
            return srid;
        }
    }
}
