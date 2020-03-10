/*
 * Copyright (C) 2012-2020 52°North Initiative for Geospatial Open Source
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

import org.hamcrest.MatcherAssert;
import org.hamcrest.core.Is;
import org.junit.Assert;

import org.junit.Before;
import org.junit.Test;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.io.ParseException;

import org.n52.faroe.ConfigurationError;
import org.n52.shetland.ogc.ows.exception.OwsExceptionReport;
import org.n52.shetland.util.JTSHelper;

import com.google.common.base.Joiner;

public class GeometryHandlerTest {

    private static final double DISTANCE = 0.0001;

    private static final double DISTANCE_TRANSFORMED = 10.0;

    // northing first
    private static final int EPSG_4326 = 4326;

    // easting first
    private static final int EPSG_31467 = 31467;

    private static final String EPSG_4326_WITH_PREFIX = "EPSG:4326";

    private static final String SUPPORTED_CRS = Joiner.on(",").join(EPSG_4326, EPSG_31467);

    private static final String NORTHING_FIRST_CRS = Joiner.on(";").join(EPSG_4326, EPSG_31467);

    private GeometryHandler geometryHandler;

    private Geometry geometry4326;

    private Geometry geometry4326Switched;

    private Geometry geometry31467;

    private Geometry geometry31467Switched;

    @Before
    public void init() throws ParseException {
        geometryHandler = new GeometryHandler();
        geometryHandler.setAuthority("EPSG");
        geometryHandler.setStorageEpsg(EPSG_4326);
        geometryHandler.setSupportedCRS(SUPPORTED_CRS);
        geometryHandler.setEpsgCodesWithNorthingFirstAxisOrder(NORTHING_FIRST_CRS);
        geometryHandler.setDatasourceNorthingFirst(false);
        geometryHandler.init();

        GeometryFactory f4326 = JTSHelper.getGeometryFactoryForSRID(EPSG_4326);
        GeometryFactory f31467 = JTSHelper.getGeometryFactoryForSRID(EPSG_31467);

        double lat_4326 = 52.7;
        double lon_4326 = 7.52;
        double lat_31467 = 5841822;
        double lon_31467 = 3400029;

        geometry4326 = f4326.createPoint(new Coordinate(lat_4326, lon_4326));
        geometry4326Switched = f4326.createPoint(new Coordinate(lon_4326, lat_4326));

        geometry31467 = f31467.createPoint(new Coordinate(lat_31467, lon_31467));
        geometry31467Switched = f31467.createPoint(new Coordinate(lon_31467, lat_31467));
    }

    private Geometry get4326Geometry() {
        return geometry4326.copy();
    }

    private Geometry get4326SwitchedGeometry() {
        return geometry4326Switched.copy();
    }

    private Geometry get31467Geometry() {
        return geometry31467.copy();
    }

    private Geometry get31467SwitchedGeometry() {
        return geometry31467Switched.copy();
    }

    @Test
    public void schouldTransformToStorageEPSG4326() throws OwsExceptionReport {
        geometryHandler.clearSupportedCRSMap();
        geometryHandler.setStorageEpsg(EPSG_4326);
        Geometry transformToStorageEpsg = geometryHandler.transformToStorageEpsg(get31467Geometry());
        Assert.assertEquals(EPSG_4326, transformToStorageEpsg.getSRID());
        MatcherAssert.assertThat(transformToStorageEpsg.distance(get4326Geometry()) < DISTANCE_TRANSFORMED, Is.is(true));
    }

    @Test
    public void schouldTransformToStorageEPSG31467() throws OwsExceptionReport {
        geometryHandler.clearSupportedCRSMap();
        geometryHandler.setStorageEpsg(EPSG_31467);
        Geometry transformToStorageEpsg = geometryHandler.transformToStorageEpsg(get4326Geometry());
        Assert.assertEquals(EPSG_31467, transformToStorageEpsg.getSRID());
        MatcherAssert.assertThat(transformToStorageEpsg.distance(get31467Geometry()) < DISTANCE_TRANSFORMED, Is.is(true));
    }

    @Test
    public void shouldSwitchGeometryForDatasourceNorthingFalseEpsg4326() throws OwsExceptionReport {
        geometryHandler.clearSupportedCRSMap();
        geometryHandler.setDatasourceNorthingFirst(false);
        geometryHandler.setStorageEpsg(EPSG_4326);
        MatcherAssert.assertThat(geometryHandler.switchCoordinateAxisFromToDatasourceIfNeeded(get4326Geometry())
                .distance(get4326SwitchedGeometry()) < DISTANCE, Is.is(true));
    }

    @Test
    public void shouldNotSwitchGeometryForDatasourceNorthingTrueEpsg4326() throws OwsExceptionReport {
        geometryHandler.clearSupportedCRSMap();
        geometryHandler.setDatasourceNorthingFirst(true);
        geometryHandler.setStorageEpsg(EPSG_4326);
        MatcherAssert.assertThat(geometryHandler.switchCoordinateAxisFromToDatasourceIfNeeded(get4326Geometry())
                .distance(get4326Geometry()) < DISTANCE, Is.is(true));
    }

    @Test
    public void shouldSwitchGeometryForDatasourceNorthingFalseEpsg31467() throws OwsExceptionReport {
        geometryHandler.clearSupportedCRSMap();
        geometryHandler.setDatasourceNorthingFirst(false);
        geometryHandler.setStorageEpsg(EPSG_31467);
        MatcherAssert.assertThat(geometryHandler.switchCoordinateAxisFromToDatasourceIfNeeded(get31467Geometry())
                .distance(get31467SwitchedGeometry()) < DISTANCE, Is.is(true));
    }

    @Test
    public void shouldGeometryForDatasourceNorthingTrueEpsg31467() throws OwsExceptionReport {
        geometryHandler.clearSupportedCRSMap();
        geometryHandler.setDatasourceNorthingFirst(true);
        geometryHandler.setStorageEpsg(EPSG_31467);
        MatcherAssert.assertThat(geometryHandler.switchCoordinateAxisFromToDatasourceIfNeeded(get31467Geometry())
                .distance(get31467Geometry()) < DISTANCE, Is.is(true));
    }

    @Test
    public void changeEpsgCodesWithNorthingFirstAxisOrder() throws OwsExceptionReport {
        MatcherAssert.assertThat(geometryHandler.isNorthingFirstEpsgCode(EPSG_31467), Is.is(true));
        MatcherAssert.assertThat(geometryHandler.isNorthingFirstEpsgCode(EPSG_4326), Is.is(true));
    }

    @Test
    public void shouldShowExceptionWhenReceivingNonNumericalString() throws OwsExceptionReport {
        Assert.assertThrows(ConfigurationError.class, () -> {
            MatcherAssert.assertThat(geometryHandler.setEpsgCodesWithNorthingFirstAxisOrder(EPSG_4326_WITH_PREFIX), Is
                    .is("Invalid format of entry in 'misc.switchCoordinatesForEpsgCodes': " + EPSG_4326_WITH_PREFIX));
        });
    }

    @Test
    public void changeSupportedCRS() throws OwsExceptionReport {
        MatcherAssert.assertThat(geometryHandler.getSupportedCRS().contains(String.valueOf(EPSG_31467)), Is.is(true));

        geometryHandler.setSupportedCRS(String.valueOf(EPSG_4326));

        MatcherAssert.assertThat(geometryHandler.getSupportedCRS().contains(String.valueOf(EPSG_4326)), Is.is(true));
        MatcherAssert.assertThat(geometryHandler.getSupportedCRS().contains(String.valueOf(EPSG_31467)), Is.is(false));
    }
}