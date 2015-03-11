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
package org.n52.sos.decode.json.impl;

import static java.lang.Integer.parseInt;
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
import static org.n52.sos.coding.json.JSONConstants.NAME;
import static org.n52.sos.coding.json.JSONConstants.POINT;
import static org.n52.sos.coding.json.JSONConstants.POLYGON;
import static org.n52.sos.coding.json.JSONConstants.PROPERTIES;
import static org.n52.sos.coding.json.JSONConstants.TYPE;

import org.n52.sos.coding.json.GeoJSONException;
import org.n52.sos.coding.json.JSONValidator;
import org.n52.sos.coding.json.SchemaConstants;
import org.n52.sos.decode.json.JSONDecoder;
import org.n52.sos.ogc.ows.OwsExceptionReport;
import org.n52.sos.util.Constants;

import com.fasterxml.jackson.databind.JsonNode;
import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryCollection;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.LineString;
import com.vividsolutions.jts.geom.LinearRing;
import com.vividsolutions.jts.geom.MultiLineString;
import com.vividsolutions.jts.geom.MultiPoint;
import com.vividsolutions.jts.geom.MultiPolygon;
import com.vividsolutions.jts.geom.Point;
import com.vividsolutions.jts.geom.Polygon;
import com.vividsolutions.jts.geom.PrecisionModel;

/**
 * TODO JavaDoc
 * 
 * @author Christian Autermann <c.autermann@52north.org>
 * 
 * @since 4.0.0
 */
public class GeoJSONDecoder extends JSONDecoder<Geometry> {
    private static final String[] SRS_LINK_PREFIXES = { "http://www.opengis.net/def/crs/EPSG/0/",
            "http://spatialreference.org/ref/epsg/" };

    private static final String[] SRS_NAME_PREFIXES = { "urn:ogc:def:crs:EPSG::", "EPSG::", "EPSG:" };

    private static final int DEFAULT_SRID = Constants.EPSG_WGS84;

    private static final PrecisionModel DEFAULT_PRECISION_MODEL = new PrecisionModel(PrecisionModel.FLOATING);

    private static final GeometryFactory DEFAULT_GEOMETRY_FACTORY = new GeometryFactory(DEFAULT_PRECISION_MODEL,
            DEFAULT_SRID);

    public static final int DIM_2D = 2;

    public static final int DIM_3D = 3;

    public GeoJSONDecoder() {
        super(Geometry.class);
    }

    @Override
    public Geometry decodeJSON(JsonNode node, boolean validate) throws OwsExceptionReport {
        if (node == null || node.isNull() || node.isMissingNode()) {
            return null;
        } else {
            if (validate) {
                JSONValidator.getInstance().validateAndThrow(node, SchemaConstants.Common.GEOMETRY);
            }
            return decodeGeometry(node, DEFAULT_GEOMETRY_FACTORY);
        }
    }

    protected Coordinate[] decodeCoordinates(JsonNode node) throws GeoJSONException {
        if (!node.isArray()) {
            throw new GeoJSONException("expected array");
        }
        Coordinate[] coordinates = new Coordinate[node.size()];
        for (int i = 0; i < node.size(); ++i) {
            coordinates[i] = decodeCoordinate(node.get(i));
        }
        return coordinates;
    }

    protected Polygon decodePolygonCoordinates(JsonNode coordinates, GeometryFactory fac) throws GeoJSONException {
        if (!coordinates.isArray()) {
            throw new GeoJSONException("expected array");
        }
        if (coordinates.size() < 1) {
            throw new GeoJSONException("missing polygon shell");
        }
        LinearRing shell = fac.createLinearRing(decodeCoordinates(coordinates.get(0)));
        LinearRing[] holes = new LinearRing[coordinates.size() - 1];
        for (int i = 1; i < coordinates.size(); ++i) {
            holes[i - 1] = fac.createLinearRing(decodeCoordinates(coordinates.get(i)));
        }
        return fac.createPolygon(shell, holes);
    }

    protected Geometry decodeGeometry(Object o, GeometryFactory parentFactory) throws GeoJSONException {
        if (!(o instanceof JsonNode)) {
            throw new GeoJSONException("Cannot decode " + o);
        }
        final JsonNode node = (JsonNode) o;
        final String type = getType(node);
        final GeometryFactory factory = getGeometryFactory(node, parentFactory);
        if (type.equals(POINT)) {
            return decodePoint(node, factory);
        } else if (type.equals(MULTI_POINT)) {
            return decodeMultiPoint(node, factory);
        } else if (type.equals(LINE_STRING)) {
            return decodeLineString(node, factory);
        } else if (type.equals(MULTI_LINE_STRING)) {
            return decodeMultiLineString(node, factory);
        } else if (type.equals(POLYGON)) {
            return decodePolygon(node, factory);
        } else if (type.equals(MULTI_POLYGON)) {
            return decodeMultiPolygon(node, factory);
        } else if (type.equals(GEOMETRY_COLLECTION)) {
            return decodeGeometryCollection(node, factory);
        } else {
            throw new GeoJSONException("Unkown geometry type: " + type);
        }
    }

    protected MultiLineString decodeMultiLineString(JsonNode node, GeometryFactory fac) throws GeoJSONException {
        JsonNode coordinates = requireCoordinates(node);
        LineString[] lineStrings = new LineString[coordinates.size()];
        for (int i = 0; i < coordinates.size(); ++i) {
            JsonNode coords = coordinates.get(i);
            lineStrings[i] = fac.createLineString(decodeCoordinates(coords));
        }
        return fac.createMultiLineString(lineStrings);
    }

    protected LineString decodeLineString(JsonNode node, GeometryFactory fac) throws GeoJSONException {
        Coordinate[] coordinates = decodeCoordinates(requireCoordinates(node));
        return fac.createLineString(coordinates);
    }

    protected MultiPoint decodeMultiPoint(JsonNode node, GeometryFactory fac) throws GeoJSONException {
        Coordinate[] coordinates = decodeCoordinates(requireCoordinates(node));
        return fac.createMultiPoint(coordinates);
    }

    protected Point decodePoint(JsonNode node, GeometryFactory fac) throws GeoJSONException {
        Coordinate parsed = decodeCoordinate(requireCoordinates(node));
        return fac.createPoint(parsed);
    }

    protected Polygon decodePolygon(JsonNode node, GeometryFactory fac) throws GeoJSONException {
        JsonNode coordinates = requireCoordinates(node);
        return decodePolygonCoordinates(coordinates, fac);
    }

    protected MultiPolygon decodeMultiPolygon(JsonNode node, GeometryFactory fac) throws GeoJSONException {
        JsonNode coordinates = requireCoordinates(node);
        Polygon[] polygons = new Polygon[coordinates.size()];
        for (int i = 0; i < coordinates.size(); ++i) {
            polygons[i] = decodePolygonCoordinates(coordinates.get(i), fac);
        }
        return fac.createMultiPolygon(polygons);
    }

    protected GeometryCollection decodeGeometryCollection(JsonNode node, GeometryFactory fac) throws GeoJSONException {
        final JsonNode geometries = node.path(GEOMETRIES);
        if (!geometries.isArray()) {
            throw new GeoJSONException("expected 'geometries' array");
        }
        Geometry[] geoms = new Geometry[geometries.size()];
        for (int i = 0; i < geometries.size(); ++i) {
            geoms[i] = decodeGeometry(geometries.get(i), fac);
        }
        return fac.createGeometryCollection(geoms);
    }

    protected JsonNode requireCoordinates(JsonNode node) throws GeoJSONException {
        if (!node.path(COORDINATES).isArray()) {
            throw new GeoJSONException("missing 'coordinates' field");
        }
        return node.path(COORDINATES);
    }

    protected Coordinate decodeCoordinate(JsonNode node) throws GeoJSONException {
        if (!node.isArray()) {
            throw new GeoJSONException("expected array");
        }
        final int dim = node.size();
        if (dim < DIM_2D) {
            throw new GeoJSONException("coordinates may have at least 2 dimensions");
        }
        if (dim > DIM_3D) {
            throw new GeoJSONException("coordinates may have at most 3 dimensions");
        }
        final Coordinate coordinate = new Coordinate();
        for (int i = 0; i < dim; ++i) {
            if (node.get(i).isNumber()) {
                coordinate.setOrdinate(i, node.get(i).doubleValue());
            } else {
                throw new GeoJSONException("coordinate index " + i + " has to be a number");
            }
        }
        return coordinate;
    }

    protected GeometryFactory getGeometryFactory(JsonNode node, GeometryFactory factory) throws GeoJSONException {
        if (!node.hasNonNull(CRS)) {
            return factory;
        } else {
            return decodeCRS(node, factory);
        }
    }

    protected GeometryFactory decodeCRS(JsonNode node, GeometryFactory factory) throws GeoJSONException {
        if (!node.path(CRS).hasNonNull(TYPE)) {
            throw new GeoJSONException("Missing CRS type");
        }
        String type = node.path(CRS).path(TYPE).textValue();
        JsonNode properties = node.path(CRS).path(PROPERTIES);
        if (type.equals(NAME)) {
            return decodeNamedCRS(properties, factory);
        } else if (type.equals(LINK)) {
            return decodeLinkedCRS(properties, factory);
        } else {
            throw new GeoJSONException("Unknown CRS type: " + type);
        }
    }

    protected GeometryFactory decodeNamedCRS(JsonNode properties, GeometryFactory factory) throws GeoJSONException {
        String name = properties.path(NAME).textValue();
        if (name == null) {
            throw new GeoJSONException("Missing name attribute for name crs");
        }
        for (String prefix : SRS_NAME_PREFIXES) {
            if (name.startsWith(prefix)) {
                try {
                    int srid = parseInt(name.substring(prefix.length()));
                    return getGeometryFactory(srid, factory);
                } catch (NumberFormatException e) {
                    throw new GeoJSONException("Invalid CRS name", e);
                }
            }
        }
        throw new GeoJSONException("Unsupported named crs: " + name);
    }

    protected GeometryFactory decodeLinkedCRS(JsonNode properties, GeometryFactory factory) throws GeoJSONException {
        String href = properties.path(HREF).textValue();
        if (href == null) {
            throw new GeoJSONException("Missing href attribute for link crs");
        }
        for (String prefix : SRS_LINK_PREFIXES) {
            if (href.startsWith(prefix)) {
                try {
                    int srid = parseInt(href.substring(prefix.length()));
                    return getGeometryFactory(srid, factory);
                } catch (NumberFormatException e) {
                    throw new GeoJSONException("Invalid CRS link", e);
                }
            }
        }
        throw new GeoJSONException("Unsupported linked crs: " + href);
    }

    protected GeometryFactory getGeometryFactory(int srid, GeometryFactory factory) {
        if (srid == factory.getSRID()) {
            return factory;
        } else {
            return new GeometryFactory(DEFAULT_PRECISION_MODEL, srid);
        }
    }

    protected String getType(final JsonNode node) throws GeoJSONException {
        if (!node.has(TYPE)) {
            throw new GeoJSONException("Can not determine geometry type (missing 'type' field)");
        }
        if (!node.path(TYPE).isTextual()) {
            throw new GeoJSONException("'type' field has to be a string");
        }
        return node.path(TYPE).textValue();
    }

    protected boolean isNumber(JsonNode x) {
        return x == null || !x.isNumber();
    }
}
