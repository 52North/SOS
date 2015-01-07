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
import static org.hamcrest.core.IsNull.notNullValue;
import static org.junit.Assert.assertThat;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import org.junit.BeforeClass;
import org.junit.Test;
import org.n52.sos.ogc.ows.OWSConstants;
import org.n52.sos.ogc.sensorML.SensorMLConstants;
import org.n52.sos.ogc.sos.Sos1Constants;
import org.n52.sos.ogc.sos.Sos2Constants;
import org.n52.sos.ogc.sos.SosConstants;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Envelope;

/**
 * @since 4.0.0
 * 
 */
public class SosHelperTest extends SosHelper {
    public static final int EPSG4326 = 4326;

    public static final int EPSG31466 = 31466;

    public static final int DEFAULT_EPSG = EPSG4326;

    public static final String FOI_ID = "test_foi";

    public static final String PROC_ID = "test_proc";

    public static final String VERSION_1 = "1.0.0";

    public static final String VERSION_2 = "2.0.0";

    public static final String SERVICE_URL = "http://localhos:8080/SOS";

    public static final String URL_PATTERN = "/kvp";

    @BeforeClass
    public static void setUp() {
        setConfiguration(new Configuration());
    }

    @Test
    public void envelopeForEpsg4326() {
        double maxY = 52.15034, maxX = 8.05847;
        double minY = 51.95104, minX = 7.61353;
        Envelope e = new Envelope(new Coordinate(minY, minX), new Coordinate(maxY, maxX));
        checkMinMax(getMinMaxFromEnvelope(e), minY, minX, maxY, maxX);
    }

    @Test
    public void envelopeForEpsg31466() {
        double maxX = 3435628, maxY = 5780049;
        double minX = 3404751, minY = 5758364;
        Envelope e = new Envelope(new Coordinate(minX, minY), new Coordinate(maxX, maxY));
        checkMinMax(getMinMaxFromEnvelope(e), minX, minY, maxX, maxY);
    }

    @Test
    public void shouldValidHttpGetGetFeatureOfInterestRequest() {
        assertThat(createFoiGetUrl(FOI_ID, VERSION_1, SERVICE_URL, URL_PATTERN), is(getFoi100Url()));
        assertThat(createFoiGetUrl(FOI_ID, VERSION_2, SERVICE_URL, URL_PATTERN), is(getFoi200Url()));
    }

    @Test
    public void shouldValidHttpGetDescribeSensorRequest() throws UnsupportedEncodingException {
        assertThat(
                getDescribeSensorUrl(VERSION_1, SERVICE_URL, PROC_ID, URL_PATTERN,
                        SensorMLConstants.SENSORML_OUTPUT_FORMAT_MIME_TYPE), is(getProcDesc100Url()));
        assertThat(
                getDescribeSensorUrl(VERSION_2, SERVICE_URL, PROC_ID, URL_PATTERN,
                        SensorMLConstants.SENSORML_OUTPUT_FORMAT_URL), is(getProcDesc200Url()));
    }

    protected void checkMinMax(MinMax<String> minmax, double minY, double minX, double maxY, double maxX) {
        assertThat(minmax, is(notNullValue()));
        assertThat(minmax.getMinimum(), is(minY + " " + minX));
        assertThat(minmax.getMaximum(), is(maxY + " " + maxX));
    }

    protected String getFoi100Url() {
        StringBuilder builder = new StringBuilder();
        builder.append(SERVICE_URL).append(URL_PATTERN);
        builder.append("?").append(OWSConstants.RequestParams.request.name()).append("=")
                .append(SosConstants.Operations.GetFeatureOfInterest.name());
        builder.append("&").append(OWSConstants.RequestParams.service.name()).append("=").append(SosConstants.SOS);
        builder.append("&").append(OWSConstants.RequestParams.version.name()).append("=").append(VERSION_1);
        builder.append("&").append(Sos1Constants.GetFeatureOfInterestParams.featureOfInterestID.name()).append("=")
                .append(FOI_ID);
        return builder.toString();
    }

    protected String getFoi200Url() {
        StringBuilder builder = new StringBuilder();
        builder.append(SERVICE_URL).append(URL_PATTERN);
        builder.append("?").append(OWSConstants.RequestParams.request.name()).append("=")
                .append(SosConstants.Operations.GetFeatureOfInterest.name());
        builder.append("&").append(OWSConstants.RequestParams.service.name()).append("=").append(SosConstants.SOS);
        builder.append("&").append(OWSConstants.RequestParams.version.name()).append("=").append(VERSION_2);
        builder.append("&").append(Sos2Constants.GetFeatureOfInterestParams.featureOfInterest.name()).append("=")
                .append(FOI_ID);
        return builder.toString();
    }

    protected String getProcDesc100Url() throws UnsupportedEncodingException {
        StringBuilder builder = new StringBuilder();
        builder.append(SERVICE_URL).append(URL_PATTERN);
        builder.append("?").append(OWSConstants.RequestParams.request.name()).append("=")
                .append(SosConstants.Operations.DescribeSensor.name());
        builder.append("&").append(OWSConstants.RequestParams.service.name()).append("=").append(SosConstants.SOS);
        builder.append("&").append(OWSConstants.RequestParams.version.name()).append("=").append(VERSION_1);
        builder.append("&").append(SosConstants.DescribeSensorParams.procedure.name()).append("=").append(PROC_ID);
        builder.append("&").append(Sos1Constants.DescribeSensorParams.outputFormat.name()).append("=")
                .append(URLEncoder.encode(SensorMLConstants.SENSORML_OUTPUT_FORMAT_MIME_TYPE, "UTF-8"));
        return builder.toString();
    }

    protected String getProcDesc200Url() throws UnsupportedEncodingException {
        StringBuilder builder = new StringBuilder();
        builder.append(SERVICE_URL).append(URL_PATTERN);
        builder.append("?").append(OWSConstants.RequestParams.request.name()).append("=")
                .append(SosConstants.Operations.DescribeSensor.name());
        builder.append("&").append(OWSConstants.RequestParams.service.name()).append("=").append(SosConstants.SOS);
        builder.append("&").append(OWSConstants.RequestParams.version.name()).append("=").append(VERSION_2);
        builder.append("&").append(SosConstants.DescribeSensorParams.procedure.name()).append("=").append(PROC_ID);
        builder.append("&").append(Sos2Constants.DescribeSensorParams.procedureDescriptionFormat.name()).append("=")
                .append(URLEncoder.encode(SensorMLConstants.SENSORML_OUTPUT_FORMAT_URL, "UTF-8"));
        return builder.toString();
    }

}
