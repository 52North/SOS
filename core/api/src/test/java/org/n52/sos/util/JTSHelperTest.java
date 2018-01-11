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

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.CoreMatchers.sameInstance;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import org.junit.Test;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.LineString;
import org.locationtech.jts.geom.LinearRing;
import org.locationtech.jts.geom.Polygon;
import org.locationtech.jts.io.ParseException;
import org.n52.shetland.ogc.ows.exception.OwsExceptionReport;
import org.n52.shetland.util.JTSHelper;
import static org.n52.shetland.util.JTSHelper.WKT_POINT;
import static org.n52.shetland.util.JTSHelper.createGeometryFromWKT;
import static org.n52.shetland.util.JTSHelper.createWKTPointFromCoordinateString;
import static org.n52.shetland.util.JTSHelper.getGeometryFactory;
import static org.n52.shetland.util.JTSHelper.getGeometryFactoryForSRID;
import static org.n52.shetland.util.JTSHelper.switchCoordinateAxisOrder;
import static org.n52.sos.util.JTSHelperForTesting.randomCoordinate;
import static org.n52.sos.util.JTSHelperForTesting.randomCoordinateRing;
import static org.n52.sos.util.JTSHelperForTesting.randomCoordinates;
import static org.n52.sos.util.ReverseOf.reverseOf;
import org.n52.svalbard.decode.exception.DecodingException;

/**
 * TODO JavaDoc
 *
 * @author <a href="mailto:c.autermann@52north.org">Christian Autermann</a>
 *
 * @since 4.0.0
 */
public class JTSHelperTest extends JTSHelper {

    @Test
    public void factoryFromSridShouldSetSrid() {
        GeometryFactory factory = getGeometryFactoryForSRID(4326);
        assertThat(factory, is(notNullValue()));
        Geometry g = factory.createPoint(new Coordinate(1, 2));
        assertThat(g, is(notNullValue()));
        assertThat(g.getSRID(), is(4326));
    }

    @Test
    public void factoryFromGeometryShouldSetSrid() {
        GeometryFactory factory = getGeometryFactoryForSRID(4326);
        assertThat(factory, is(notNullValue()));
        Geometry g = factory.createPoint(new Coordinate(1, 2));
        factory = getGeometryFactory(g);
        assertThat(factory, is(notNullValue()));
        g = factory.createPoint(new Coordinate(1, 2));
        assertThat(g, is(notNullValue()));
        assertThat(g.getSRID(), is(4326));
    }

    @Test
    public void shouldPointWKTString() throws OwsExceptionReport, DecodingException, ParseException {
        String coordinates = "52.0 7.0";
        StringBuilder builder = new StringBuilder();
        builder.append(WKT_POINT);
        builder.append("(");
        builder.append(coordinates);
        builder.append(")");
        assertEquals(builder.toString(), createWKTPointFromCoordinateString(coordinates));
        assertEquals(createGeometryFromWKT(builder.toString(), 4326),
                createGeometryFromWKT(createWKTPointFromCoordinateString(coordinates), 4326));
    }

    @Test
    public void shouldReverseLinearRing() throws OwsExceptionReport {
        testReverse(getGeometryFactoryForSRID(4326).createLinearRing(randomCoordinateRing(10)));
    }

    @Test
    public void shouldReversePoint() throws OwsExceptionReport {
        testReverse(getGeometryFactoryForSRID(4326).createPoint(randomCoordinate()));
    }

    @Test
    public void shouldReverseLineString() throws OwsExceptionReport {
        testReverse(getGeometryFactoryForSRID(4326).createLineString(randomCoordinates(10)));
    }

    @Test
    public void shouldReversePolygon() throws OwsExceptionReport {
        final GeometryFactory factory = getGeometryFactoryForSRID(4326);
        testReverse(factory.createPolygon(
                factory.createLinearRing(randomCoordinateRing(10)),
                new LinearRing[] { factory.createLinearRing(randomCoordinateRing(10)),
                        factory.createLinearRing(randomCoordinateRing(41)),
                        factory.createLinearRing(randomCoordinateRing(13)) }));
    }

    @Test
    public void shouldReverseMultiPoint() throws OwsExceptionReport {
        final GeometryFactory factory = getGeometryFactoryForSRID(4326);
        testReverse(factory.createMultiPoint(randomCoordinates(20)));
    }

    @Test
    public void shouldReverseMultiLineString() throws OwsExceptionReport {
        final GeometryFactory factory = getGeometryFactoryForSRID(4326);
        testReverse(factory.createMultiLineString(new LineString[] {
                factory.createLineString(randomCoordinateRing(21)),
                factory.createLineString(randomCoordinateRing(21)),
                factory.createLineString(randomCoordinateRing(15)), }));
    }

    @Test
    public void shouldReverseMultiPolygon() throws OwsExceptionReport {
        final GeometryFactory factory = getGeometryFactoryForSRID(4326);
        testReverse(factory.createMultiPolygon(new Polygon[] {
                factory.createPolygon(
                        factory.createLinearRing(randomCoordinateRing(13)),
                        new LinearRing[] { factory.createLinearRing(randomCoordinateRing(130)),
                                factory.createLinearRing(randomCoordinateRing(4121)),
                                factory.createLinearRing(randomCoordinateRing(12)) }),
                factory.createPolygon(
                        factory.createLinearRing(randomCoordinateRing(8)),
                        new LinearRing[] { factory.createLinearRing(randomCoordinateRing(1101)),
                                factory.createLinearRing(randomCoordinateRing(413)),
                                factory.createLinearRing(randomCoordinateRing(123)) }),
                factory.createPolygon(
                        factory.createLinearRing(randomCoordinateRing(89)),
                        new LinearRing[] { factory.createLinearRing(randomCoordinateRing(112)),
                                factory.createLinearRing(randomCoordinateRing(4)),
                                factory.createLinearRing(randomCoordinateRing(43)) }) }));
    }

    @Test
    public void shouldReverseGeometryCollection() throws OwsExceptionReport {
        final GeometryFactory factory = getGeometryFactoryForSRID(4326);
        testReverse(factory.createGeometryCollection(new Geometry[] {
                factory.createMultiPolygon(new Polygon[] {
                        factory.createPolygon(
                                factory.createLinearRing(randomCoordinateRing(13)),
                                new LinearRing[] { factory.createLinearRing(randomCoordinateRing(130)),
                                        factory.createLinearRing(randomCoordinateRing(4121)),
                                        factory.createLinearRing(randomCoordinateRing(12)) }),
                        factory.createPolygon(
                                factory.createLinearRing(randomCoordinateRing(8)),
                                new LinearRing[] { factory.createLinearRing(randomCoordinateRing(1101)),
                                        factory.createLinearRing(randomCoordinateRing(413)),
                                        factory.createLinearRing(randomCoordinateRing(123)) }),
                        factory.createPolygon(
                                factory.createLinearRing(randomCoordinateRing(89)),
                                new LinearRing[] { factory.createLinearRing(randomCoordinateRing(112)),
                                        factory.createLinearRing(randomCoordinateRing(4)),
                                        factory.createLinearRing(randomCoordinateRing(43)) }) }),
                factory.createMultiLineString(new LineString[] { factory.createLineString(randomCoordinateRing(21)),
                        factory.createLineString(randomCoordinateRing(21)),
                        factory.createLineString(randomCoordinateRing(15)), }),
                factory.createPolygon(
                        factory.createLinearRing(randomCoordinateRing(10)),
                        new LinearRing[] { factory.createLinearRing(randomCoordinateRing(10)),
                                factory.createLinearRing(randomCoordinateRing(41)),
                                factory.createLinearRing(randomCoordinateRing(13)) }),
                getGeometryFactoryForSRID(4326).createLineString(randomCoordinates(10)),
                getGeometryFactoryForSRID(4326).createLineString(randomCoordinates(10)) }));
    }

    @Test
    public void shouldReverseUnknownGeometry() throws OwsExceptionReport {
        testReverse(new UnknownGeometry(getGeometryFactoryForSRID(4326).createLineString(randomCoordinates(5))));
    }

    protected void testReverse(Geometry geometry) throws OwsExceptionReport {
        Geometry reversed = switchCoordinateAxisOrder(geometry);
        assertThat(reversed, is(instanceOf(geometry.getClass())));
        assertThat(reversed, is(not(sameInstance(geometry))));
        assertThat(reversed, is(notNullValue()));
        assertThat(reversed, is(reverseOf(geometry)));
    }

}
