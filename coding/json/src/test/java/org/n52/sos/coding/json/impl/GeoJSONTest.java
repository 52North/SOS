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
package org.n52.sos.coding.json.impl;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.Assert.assertThat;
import static org.n52.sos.coding.json.matchers.ValidationMatchers.instanceOf;

import java.io.IOException;
import java.util.Random;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ErrorCollector;

import org.n52.svalbard.decode.exception.DecodingException;
import org.n52.svalbard.encode.exception.EncodingException;
import org.n52.sos.coding.json.GeoJSONDecodingException;
import org.n52.sos.coding.json.SchemaConstants;
import org.n52.sos.decode.json.impl.GeoJSONDecoder;
import org.n52.sos.encode.json.JSONEncodingException;
import org.n52.sos.encode.json.impl.GeoJSONEncoder;

import com.fasterxml.jackson.databind.JsonNode;
import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.CoordinateFilter;
import com.vividsolutions.jts.geom.CoordinateSequenceComparator;
import com.vividsolutions.jts.geom.CoordinateSequenceFilter;
import com.vividsolutions.jts.geom.Envelope;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryCollection;
import com.vividsolutions.jts.geom.GeometryComponentFilter;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.GeometryFilter;
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
 * @author Christian Autermann <autermann@uni-muenster.de>
 *
 * @since 4.0.0
 */
public class GeoJSONTest {
    private static final int EPSG_4326 = 4326;
    @Rule
    public final ErrorCollector errors = new ErrorCollector();

    private final GeometryFactory geometryFactory = new GeometryFactory(new PrecisionModel(PrecisionModel.FLOATING));

    private final Random random = new Random();

    private final GeoJSONEncoder enc = new GeoJSONEncoder();

    private final GeoJSONDecoder dec = new GeoJSONDecoder();

    private Coordinate randomCoordinate() {
        return new Coordinate(random.nextInt(1000), random.nextInt(1000));
    }

    private LineString randomLineString(int srid) {
        LineString geometry =
                geometryFactory.createLineString(new Coordinate[] { randomCoordinate(), randomCoordinate(),
                        randomCoordinate() });
        geometry.setSRID(srid);
        return geometry;
    }

    private MultiLineString randomMultiLineString(int srid) {
        return geometryFactory.createMultiLineString(new LineString[] { randomLineString(srid),
                randomLineString(srid), randomLineString(srid) });
    }

    private Point randomPoint(int srid) {
        Point geometry = geometryFactory.createPoint(randomCoordinate());
        geometry.setSRID(srid);
        return geometry;
    }

    private LinearRing randomLinearRing(int srid) {
        Coordinate p = randomCoordinate();
        LinearRing geometry =
                geometryFactory.createLinearRing(new Coordinate[] { p, randomCoordinate(), randomCoordinate(),
                        randomCoordinate(), p });
        geometry.setSRID(srid);
        return geometry;
    }

    private Polygon randomPolygon(int srid) {
        Polygon geometry =
                geometryFactory.createPolygon(randomLinearRing(srid), new LinearRing[] { randomLinearRing(srid),
                        randomLinearRing(srid), randomLinearRing(srid) });
        geometry.setSRID(srid);
        return geometry;
    }

    private MultiPoint randomMultiPoint(int srid) {
        MultiPoint geometry =
                geometryFactory.createMultiPoint(new Coordinate[] { randomCoordinate(), randomCoordinate(),
                        randomCoordinate(), randomCoordinate(), randomCoordinate(), randomCoordinate() });
        geometry.setSRID(srid);
        return geometry;
    }

    private MultiPolygon randomMultiPolygon(int srid) {
        MultiPolygon geometry =
                geometryFactory.createMultiPolygon(new Polygon[] { randomPolygon(srid), randomPolygon(srid),
                        randomPolygon(srid) });
        geometry.setSRID(srid);
        return geometry;
    }

    private GeometryCollection randomGeometryCollection(int srid) {
        GeometryCollection geometry =
                geometryFactory.createGeometryCollection(new Geometry[] { randomPoint(srid), randomMultiPoint(srid),
                        randomLineString(srid), randomMultiLineString(srid), randomPolygon(srid),
                        randomMultiPolygon(srid) });
        geometry.setSRID(srid);
        return geometry;
    }

    @Test
    public void testGeometryCollection() throws GeoJSONDecodingException, IOException {
        readWriteTest(geometryFactory.createGeometryCollection(new Geometry[] { randomGeometryCollection(EPSG_4326),
                randomGeometryCollection(2000) }));
    }

    @Test
    public void testGeometryCollectionWithZCoordinate() throws GeoJSONDecodingException, IOException {
        GeometryCollection geometry =
                geometryFactory.createGeometryCollection(new Geometry[] { randomGeometryCollection(EPSG_4326),
                        randomGeometryCollection(2000) });
        geometry.apply(new RandomZCoordinateFilter());
        geometry.geometryChanged();
        readWriteTest(geometry);
    }

    @Test
    public void testPolygon() throws GeoJSONDecodingException, IOException {
        readWriteTest(randomPolygon(EPSG_4326));
    }

    @Test
    public void testPolygonWithZCoordinate() throws GeoJSONDecodingException, IOException {
        Polygon geometry = randomPolygon(EPSG_4326);
        geometry.apply(new RandomZCoordinateFilter());
        geometry.geometryChanged();
        readWriteTest(geometry);
    }

    @Test
    public void testMultiPolygon() throws GeoJSONDecodingException, IOException {
        readWriteTest(randomMultiPolygon(EPSG_4326));
    }

    @Test
    public void testMultiPolygonWithZCoordinate() throws GeoJSONDecodingException, IOException {
        MultiPolygon geometry = randomMultiPolygon(EPSG_4326);
        geometry.apply(new RandomZCoordinateFilter());
        geometry.geometryChanged();
        readWriteTest(geometry);
    }

    @Test
    public void testPoint() throws GeoJSONDecodingException, IOException {
        readWriteTest(randomPoint(2000));
    }

    @Test
    public void testCrsCombinations() throws GeoJSONDecodingException, IOException {
        testCrs(0, 0);
        testCrs(2000, 0);
        testCrs(EPSG_4326, 0);
        testCrs(EPSG_4326, 2000);
        testCrs(0, 2000);
        testCrs(0, EPSG_4326);
        testCrs(2000, 2000);
        testCrs(EPSG_4326, EPSG_4326);
        testCrs(2000, 2001);
    }

    private void testCrs(int parent, int child) {
        final GeometryCollection col = geometryFactory.createGeometryCollection(new Geometry[] { randomPoint(child) });
        col.setSRID(parent);
        readWriteTest(col);
    }

    @Test
    public void testPointWithZCoordinate() throws GeoJSONDecodingException, IOException {
        Point geometry = randomPoint(2000);
        geometry.apply(new RandomZCoordinateFilter());
        geometry.geometryChanged();
        readWriteTest(geometry);
    }

    @Test
    public void testMultiPoint() {
        readWriteTest(randomMultiPoint(EPSG_4326));
    }

    @Test
    public void testMultiPointWithZCoordinate() {
        MultiPoint geometry = randomMultiPoint(EPSG_4326);
        geometry.apply(new RandomZCoordinateFilter());
        geometry.geometryChanged();
        readWriteTest(geometry);
    }

    @Test
    public void testLineString() {
        readWriteTest(randomLineString(EPSG_4326));
    }

    @Test
    public void testLineStringWithZCoordinate() {
        LineString geometry = randomLineString(EPSG_4326);
        geometry.apply(new RandomZCoordinateFilter());
        geometry.geometryChanged();
        readWriteTest(geometry);
    }

    @Test
    public void testMultiLineString() {
        readWriteTest(randomMultiLineString(EPSG_4326));
    }

    @Test
    public void testMultiLineStringWithZCoordinate() {
        MultiLineString geometry = randomMultiLineString(EPSG_4326);
        geometry.apply(new RandomZCoordinateFilter());
        geometry.geometryChanged();
        readWriteTest(geometry);
    }

    protected void readWriteTest(final Geometry geom) {
        try {
            JsonNode json = enc.encodeJSON(geom);
            Geometry parsed = dec.decodeJSON(json, false);
            JsonNode json2 = enc.encodeJSON(parsed);
            errors.checkThat(geom, is(equalTo(parsed)));
            errors.checkThat(json, is(instanceOf(SchemaConstants.Common.GEOMETRY)));
            errors.checkThat(json2, is(instanceOf(SchemaConstants.Common.GEOMETRY)));
            errors.checkThat(json, is(equalTo(json2)));
        } catch (EncodingException | DecodingException ex) {
            errors.addError(ex);
        }

    }

    @Test
    public void testNull() throws JSONEncodingException {
        assertThat(enc.encodeJSON(null), is(nullValue()));
    }

    @Test(expected = JSONEncodingException.class)
    public void testUnknownGeometry() throws JSONEncodingException {
        enc.encodeJSON(new UnknownGeometry(geometryFactory));
    }

    @Test
    public void testEmpty() throws JSONEncodingException {
        assertThat(enc.encodeJSON(new EmptyGeometry(geometryFactory)), is(nullValue()));
    }

    private class RandomZCoordinateFilter implements CoordinateFilter {
        @Override
        public void filter(Coordinate coord) {
            coord.z = random.nextInt(1000);
        }
    }

    private class UnknownGeometry extends Geometry {
        private static final long serialVersionUID = 1L;

        private final String type = "geom";

        private final Point delegate = geometryFactory.createPoint(new Coordinate(1, 2, 3));

        UnknownGeometry(GeometryFactory factory) {
            super(factory);
        }

        @Override
        public String getGeometryType() {
            return type;
        }

        @Override
        public Coordinate getCoordinate() {
            return delegate.getCoordinate();
        }

        @Override
        public Coordinate[] getCoordinates() {
            return delegate.getCoordinates();
        }

        @Override
        public int getNumPoints() {
            return delegate.getNumPoints();
        }

        @Override
        public boolean isEmpty() {
            return delegate.isEmpty();
        }

        @Override
        public int getDimension() {
            return delegate.getDimension();

        }

        @Override
        public Geometry getBoundary() {
            return delegate.getBoundary();
        }

        @Override
        public int getBoundaryDimension() {
            return delegate.getBoundaryDimension();
        }

        @Override
        public Geometry reverse() {
            return delegate.reverse();
        }

        @Override
        public boolean equalsExact(Geometry other, double tolerance) {
            return delegate.equalsExact(other, tolerance);
        }

        @Override
        public void apply(CoordinateFilter filter) {
            delegate.apply(filter);
        }

        @Override
        public void apply(CoordinateSequenceFilter filter) {
            delegate.apply(filter);
        }

        @Override
        public void apply(GeometryFilter filter) {
            delegate.apply(filter);
        }

        @Override
        public void apply(GeometryComponentFilter filter) {
            delegate.apply(filter);
        }

        @Override
        public void normalize() {
            delegate.normalize();
        }

        @Override
        protected Envelope computeEnvelopeInternal() {
            return delegate.getEnvelopeInternal();
        }

        @Override
        protected int compareToSameClass(Object o) {
            return delegate.compareTo(o);
        }

        @Override
        protected int compareToSameClass(Object o, CoordinateSequenceComparator comp) {
            return delegate.compareTo(o, comp);
        }
    }

    private class EmptyGeometry extends UnknownGeometry {
        private static final long serialVersionUID = 1L;

        EmptyGeometry(GeometryFactory factory) {
            super(factory);
        }

        @Override
        public boolean isEmpty() {
            return true;
        }
    }
}
