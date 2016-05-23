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

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;

import org.junit.BeforeClass;
import org.junit.Test;
import org.n52.sos.ogc.ows.OwsExceptionReport;

import com.google.common.base.Joiner;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.io.ParseException;
import com.vividsolutions.jts.io.WKTReader;

public class GeometryHandlerTest {
    
    private static final double DISTANCE = 0.0001;
    
    private static final double DISTANCE_TRANSFORMED = 10.0;

    private static GeometryHandler geometryHandler;

    private static String wkt4326;

    private static String wkt4326Switched;

    private static String wkt31467;

    private static String wkt31467Switched;

    private static Geometry geometry4326;

    private static Geometry geometry4326Switched;

    private static Geometry geometry31467;

    private static Geometry geometry31467Switched;

    // northing first
    private static int EPSG_4326 = 4326;

    // easting first
    private static int EPSG_31467 = 31467;
    
    private static String supportedCRS = Joiner.on(Constants.COMMA_CHAR).join(EPSG_4326, EPSG_31467);
    
    private static String nortingFirstCRS = Joiner.on(Constants.SEMICOLON_CHAR).join(EPSG_4326, EPSG_31467);
    
    @BeforeClass
    public static void init() throws ParseException {
        geometryHandler = GeometryHandler.getInstance();
        WKTReader reader = new WKTReader();
        double lat_4326 = 52.7;
        double lon_4326 = 7.52;
        double lat_31467 = 5841822;
        double lon_31467 = 3400029;
        
        geometryHandler.setSupportedCRS(supportedCRS);

        wkt4326 = geometryHandler.getWktString(lat_4326, lon_4326);
        geometry4326 = reader.read(wkt4326);
        geometry4326.setSRID(EPSG_4326);

        wkt4326Switched = geometryHandler.getWktString(lon_4326, lat_4326);
        geometry4326Switched = reader.read(wkt4326Switched);
        geometry4326Switched.setSRID(EPSG_4326);

        wkt31467 = geometryHandler.getWktString(lat_31467, lon_31467);
        geometry31467 = reader.read(wkt31467);
        geometry31467.setSRID(EPSG_31467);

        wkt31467Switched = geometryHandler.getWktString(lon_31467, lat_31467);
        geometry31467Switched = reader.read(wkt31467Switched);
        geometry31467Switched.setSRID(EPSG_31467);
        geometryHandler.setEpsgCodesWithNorthingFirstAxisOrder(nortingFirstCRS);
    }
    
    private Geometry get4326Geometry() {
        return (Geometry)geometry4326.clone();
    }
    
    private Geometry get4326SwitchedGeometry() {
        return (Geometry)geometry4326Switched.clone();
    }
    
    private Geometry get31467Geometry() {
        return (Geometry)geometry31467.clone();
    }
    
    private Geometry get31467SwitchedGeometry() {
        return (Geometry)geometry31467Switched.clone();
    }

    @Test
    public void schouldTransformToStorageEPSG4326() throws OwsExceptionReport {
        geometryHandler.clearSupportedCRSMap();
        geometryHandler.setStorageEpsg(EPSG_4326);
        Geometry transformToStorageEpsg = geometryHandler.transformToStorageEpsg(get31467Geometry());
        assertEquals(EPSG_4326, transformToStorageEpsg.getSRID());
        assertThat((transformToStorageEpsg.distance(get4326Geometry()) < DISTANCE_TRANSFORMED), is(true));
    }
    
    @Test
    public void schouldTransformToStorageEPSG31467() throws OwsExceptionReport {
        geometryHandler.clearSupportedCRSMap();
        geometryHandler.setStorageEpsg(EPSG_31467);
        Geometry transformToStorageEpsg = geometryHandler.transformToStorageEpsg(get4326Geometry());
        assertEquals(EPSG_31467, transformToStorageEpsg.getSRID());
        assertThat((transformToStorageEpsg.distance(get31467Geometry()) < DISTANCE_TRANSFORMED), is(true));
    }
    
    @Test
    public void shouldSwitchGeometryForDatasourceNorthingFalseEpsg4326() throws OwsExceptionReport {
        geometryHandler.clearSupportedCRSMap();
        geometryHandler.setDatasourceNorthingFirst(false);
        geometryHandler.setStorageEpsg(EPSG_4326);
        assertThat((geometryHandler.switchCoordinateAxisFromToDatasourceIfNeeded(get4326Geometry()).distance(get4326SwitchedGeometry()) < DISTANCE), is(true));
    }

    @Test
    public void shouldNotSwitchGeometryForDatasourceNorthingTrueEpsg4326() throws OwsExceptionReport {
        geometryHandler.clearSupportedCRSMap();
        geometryHandler.setDatasourceNorthingFirst(true);
        geometryHandler.setStorageEpsg(EPSG_4326);
        assertThat((geometryHandler.switchCoordinateAxisFromToDatasourceIfNeeded(get4326Geometry()).distance(get4326Geometry()) < DISTANCE), is(true));
    }

    @Test
    public void shouldSwitchGeometryForDatasourceNorthingFalseEpsg31467() throws OwsExceptionReport {
        geometryHandler.clearSupportedCRSMap();
        geometryHandler.setDatasourceNorthingFirst(false);
        geometryHandler.setStorageEpsg(EPSG_31467);
        assertThat((geometryHandler.switchCoordinateAxisFromToDatasourceIfNeeded(get31467Geometry()).distance(get31467SwitchedGeometry()) < DISTANCE), is(true));
    }

    @Test
    public void shouldvGeometryForDatasourceNorthingTrueEpsg31467() throws OwsExceptionReport {
        geometryHandler.clearSupportedCRSMap();
        geometryHandler.setDatasourceNorthingFirst(true);
        geometryHandler.setStorageEpsg(EPSG_31467);
        assertThat((geometryHandler.switchCoordinateAxisFromToDatasourceIfNeeded(get31467Geometry()).distance(get31467Geometry()) < DISTANCE), is(true));
    }
}
