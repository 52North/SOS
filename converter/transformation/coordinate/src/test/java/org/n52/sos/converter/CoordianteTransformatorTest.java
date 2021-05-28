/*
 * Copyright (C) 2012-2021 52°North Spatial Information Research GmbH
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
package org.n52.sos.converter;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

import javax.naming.ConfigurationException;

import org.junit.Test;

import org.n52.iceland.ds.ConnectionProviderException;
import org.n52.shetland.ogc.ows.exception.OwsExceptionReport;
import org.n52.shetland.ogc.swe.SweConstants.SweCoordinateNames;

/**
 * Test class for {@link CoordianteTransformator}
 *
 * @author <a href="mailto:c.hollmann@52north.org">Carsten Hollmann</a>
 * @since 4.4.0
 *
 */
public class CoordianteTransformatorTest {

    private final CoordinateTransformator transformer = new CoordinateTransformator();

    private final int EPSG = 4326;

    private final int TARGET_EPSG = 31466;

    private final String HTTP_PREFIX = "http://test.org/i/am/an/epsg/";

    private final String URN_PREFIX = "urn:ogc:epsg:";

    private final String EPSG_PREFIX = "EPSG::";

//    @AfterClass
//    public static void cleanUp() {
//        SettingsServiceImpl.getInstance().cleanup();
//    }

    @Test
    public void testGetCrsFromStringHttp() throws OwsExceptionReport {
        assertThat(transformer.getCrsFromString(HTTP_PREFIX + EPSG), is(EPSG));
    }

    @Test
    public void testGetCrsFromStringUrn() throws OwsExceptionReport {
        assertThat(transformer.getCrsFromString(URN_PREFIX + EPSG), is(EPSG));
    }

    @Test
    public void testGetCrsFromStringEpsg() throws OwsExceptionReport {
        assertThat(transformer.getCrsFromString(EPSG_PREFIX + EPSG), is(EPSG));
    }

    @Test(expected = OwsExceptionReport.class)
    public void testGetCrsFromStringException() throws OwsExceptionReport {
        transformer.getCrsFromString(HTTP_PREFIX);
    }

    @Test
    public void testTransformReferenceFrameHttp() {
        assertThat(transformer.transformReferenceFrame(HTTP_PREFIX + EPSG, EPSG, TARGET_EPSG),
                is(HTTP_PREFIX + TARGET_EPSG));
    }

    @Test
    public void testTransformReferenceFrameUrn() {
        assertThat(transformer.transformReferenceFrame(URN_PREFIX + EPSG, EPSG, TARGET_EPSG),
                is(URN_PREFIX + TARGET_EPSG));
    }

    @Test
    public void testTransformReferenceFrameEpsg() {
        assertThat(transformer.transformReferenceFrame(EPSG_PREFIX + EPSG, EPSG, TARGET_EPSG),
                is(EPSG_PREFIX + TARGET_EPSG));
    }

    @Test
    public void testTransformReferenceFrameNotTransformed() {
        assertThat(transformer.transformReferenceFrame(HTTP_PREFIX + EPSG, -1, -1), is(HTTP_PREFIX + EPSG));
    }

    @Test
    public void testCheckAltitudeName() throws ConnectionProviderException, ConfigurationException {
        assertThat(transformer.checkAltitudeName(SweCoordinateNames.ALTITUDE), is(true));
        assertThat(transformer.checkAltitudeName("AltITuDe"), is(true));
        assertThat(transformer.checkAltitudeName(SweCoordinateNames.HEIGHT), is(true));
        assertThat(transformer.checkAltitudeName(SweCoordinateNames.DEPTH), is(true));
        assertThat(transformer.checkAltitudeName("IamAltitude"), is(false));
        // Add value to setting
        transformer.setAltitudeNames("testAltitude, secondTestAltitude");
        assertThat(transformer.checkAltitudeName("testAltitude"), is(true));
    }

    @Test
    public void testCheckNorthingName() {
        assertThat(transformer.checkNorthingName(SweCoordinateNames.NORTHING), is(true));
        assertThat(transformer.checkNorthingName("nOrTHinG"), is(true));
        assertThat(transformer.checkNorthingName(SweCoordinateNames.SOUTHING), is(true));
        assertThat(transformer.checkNorthingName(SweCoordinateNames.LATITUDE), is(true));
        assertThat(transformer.checkNorthingName("IamNorthing"), is(false));
        // Add value to setting
        transformer.setNorthingNames("testNorthing, secondTestNorthing");
        assertThat(transformer.checkNorthingName("testNorthing"), is(true));
    }

    @Test
    public void testcheckEastingName() {
        assertThat(transformer.checkEastingName(SweCoordinateNames.EASTING), is(true));
        assertThat(transformer.checkEastingName("EaSTinG"), is(true));
        assertThat(transformer.checkEastingName(SweCoordinateNames.WESTING), is(true));
        assertThat(transformer.checkEastingName(SweCoordinateNames.LONGITUDE), is(true));
        assertThat(transformer.checkEastingName("IamEasting"), is(false));
        // Add value to setting
        transformer.setEastingNames("testEasting, secondTestEasting");
        assertThat(transformer.checkEastingName("testEasting"), is(true));
    }

}
