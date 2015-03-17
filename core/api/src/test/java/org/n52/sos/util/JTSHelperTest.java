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

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;
import static org.n52.sos.util.ReverseOf.reverseOf;
import static org.n52.sos.util.JTSHelperForTesting.*;


import org.junit.Test;
import org.n52.sos.ogc.ows.OwsExceptionReport;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.LineString;
import com.vividsolutions.jts.geom.LinearRing;
import com.vividsolutions.jts.geom.Polygon;

/**
 * TODO JavaDoc
 * 
 * @author Christian Autermann <c.autermann@52north.org>
 * 
 * @since 4.0.0
 */
public class JTSHelperTest extends JTSHelper {


    @Test
    public void factoryFromSridShouldSetSrid() {
        GeometryFactory factory = getGeometryFactoryForSRID(Constants.EPSG_WGS84);
        assertThat(factory, is(notNullValue()));
        Geometry g = factory.createPoint(new Coordinate(1, 2));
        assertThat(g, is(notNullValue()));
        assertThat(g.getSRID(), is(Constants.EPSG_WGS84));
    }

    @Test
    public void factoryFromGeometryShouldSetSrid() {
        GeometryFactory factory = getGeometryFactoryForSRID(Constants.EPSG_WGS84);
        assertThat(factory, is(notNullValue()));
        Geometry g = factory.createPoint(new Coordinate(1, 2));
        factory = getGeometryFactory(g);
        assertThat(factory, is(notNullValue()));
        g = factory.createPoint(new Coordinate(1, 2));
        assertThat(g, is(notNullValue()));
        assertThat(g.getSRID(), is(Constants.EPSG_WGS84));
    }

    @Test
    public void shouldPointWKTString() throws OwsExceptionReport {
        String coordinates = "52.0 7.0";
        StringBuilder builder = new StringBuilder();
        builder.append(JTSConstants.WKT_POINT);
        builder.append("(");
        builder.append(coordinates);
        builder.append(")");
        assertEquals(builder.toString(), createWKTPointFromCoordinateString(coordinates));
        assertEquals(createGeometryFromWKT(builder.toString(), Constants.EPSG_WGS84),
                createGeometryFromWKT(createWKTPointFromCoordinateString(coordinates), Constants.EPSG_WGS84));
    }

    @Test
    public void shouldReverseLinearRing() throws OwsExceptionReport {
        testReverse(getGeometryFactoryForSRID(Constants.EPSG_WGS84).createLinearRing(randomCoordinateRing(10)));
    }

    @Test
    public void shouldReversePoint() throws OwsExceptionReport {
        testReverse(getGeometryFactoryForSRID(Constants.EPSG_WGS84).createPoint(randomCoordinate()));
    }

    @Test
    public void shouldReverseLineString() throws OwsExceptionReport {
        testReverse(getGeometryFactoryForSRID(Constants.EPSG_WGS84).createLineString(randomCoordinates(10)));
    }

    @Test
    public void shouldReversePolygon() throws OwsExceptionReport {
        final GeometryFactory factory = getGeometryFactoryForSRID(Constants.EPSG_WGS84);
        testReverse(factory.createPolygon(
                factory.createLinearRing(randomCoordinateRing(10)),
                new LinearRing[] { factory.createLinearRing(randomCoordinateRing(10)),
                        factory.createLinearRing(randomCoordinateRing(41)),
                        factory.createLinearRing(randomCoordinateRing(13)) }));
    }

    @Test
    public void shouldReverseMultiPoint() throws OwsExceptionReport {
        final GeometryFactory factory = getGeometryFactoryForSRID(Constants.EPSG_WGS84);
        testReverse(factory.createMultiPoint(randomCoordinates(20)));
    }

    @Test
    public void shouldReverseMultiLineString() throws OwsExceptionReport {
        final GeometryFactory factory = getGeometryFactoryForSRID(Constants.EPSG_WGS84);
        testReverse(factory.createMultiLineString(new LineString[] {
                factory.createLineString(randomCoordinateRing(21)),
                factory.createLineString(randomCoordinateRing(21)),
                factory.createLineString(randomCoordinateRing(15)), }));
    }

    @Test
    public void shouldReverseMultiPolygon() throws OwsExceptionReport {
        final GeometryFactory factory = getGeometryFactoryForSRID(Constants.EPSG_WGS84);
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
        final GeometryFactory factory = getGeometryFactoryForSRID(Constants.EPSG_WGS84);
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
                getGeometryFactoryForSRID(Constants.EPSG_WGS84).createLineString(randomCoordinates(10)),
                getGeometryFactoryForSRID(Constants.EPSG_WGS84).createLineString(randomCoordinates(10)) }));
    }

    @Test
    public void shouldReverseUnknownGeometry() throws OwsExceptionReport {
        testReverse(new UnknownGeometry(getGeometryFactoryForSRID(Constants.EPSG_WGS84).createLineString(randomCoordinates(5))));
    }

    protected void testReverse(Geometry geometry) throws OwsExceptionReport {
        Geometry reversed = switchCoordinateAxisOrder(geometry);
        assertThat(reversed, is(instanceOf(geometry.getClass())));
        assertThat(reversed, is(not(sameInstance(geometry))));
        assertThat(reversed, is(notNullValue()));
        assertThat(reversed, is(reverseOf(geometry)));
    }

}
