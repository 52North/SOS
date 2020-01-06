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

import org.hamcrest.core.Is;
import org.hamcrest.core.IsNull;
import org.junit.Assert;

import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;

import org.junit.Test;
import org.n52.shetland.ogc.ows.OWSConstants;
import org.n52.shetland.ogc.sensorML.SensorMLConstants;
import org.n52.shetland.ogc.sos.Sos1Constants;
import org.n52.shetland.ogc.sos.Sos2Constants;
import org.n52.shetland.ogc.sos.SosConstants;
import org.n52.shetland.util.MinMax;

/**
 * @since 4.0.0
 *
 */
public class SosHelperTest
        extends SosHelper {

    public static final int EPSG4326 = 4326;

    public static final int EPSG31466 = 31466;

    public static final int DEFAULT_EPSG = EPSG4326;

    public static final String FOI_ID = "test_foi";

    public static final String PROC_ID = "test_proc";

    public static final String VERSION_1 = "1.0.0";

    public static final String VERSION_2 = "2.0.0";

    public static final String SERVICE_URL = "http://localhos:8080/SOS/service";

    private static final String UTF8 = "UTF-8";

    @Test
    public void shouldValidHttpGetGetFeatureOfInterestRequest() throws MalformedURLException {
        Assert.assertThat(createFoiGetUrl(FOI_ID, VERSION_1, SERVICE_URL), Is.is(getFoi100Url()));
        Assert.assertThat(createFoiGetUrl(FOI_ID, VERSION_2, SERVICE_URL), Is.is(getFoi200Url()));
    }

    @Test
    public void shouldValidHttpGetDescribeSensorRequest() throws MalformedURLException, UnsupportedEncodingException {
        Assert.assertThat(getDescribeSensorUrl(VERSION_1, SERVICE_URL, PROC_ID,
                SensorMLConstants.SENSORML_OUTPUT_FORMAT_MIME_TYPE), Is.is(getProcDesc100Url()));
        Assert.assertThat(
                getDescribeSensorUrl(VERSION_2, SERVICE_URL, PROC_ID, SensorMLConstants.SENSORML_OUTPUT_FORMAT_URL),
                Is.is(getProcDesc200Url()));
    }

    protected void checkMinMax(MinMax<String> minmax, double minY, double minX, double maxY, double maxX) {
        Assert.assertThat(minmax, Is.is(IsNull.notNullValue()));
        Assert.assertThat(minmax.getMinimum(), Is.is(minY + " " + minX));
        Assert.assertThat(minmax.getMaximum(), Is.is(maxY + " " + maxX));
    }

    protected URL getFoi100Url() throws MalformedURLException {
        StringBuilder builder = new StringBuilder();
        builder.append(SERVICE_URL);
        builder.append("?").append(OWSConstants.RequestParams.service.name()).append("=").append(SosConstants.SOS);
        builder.append("&").append(OWSConstants.RequestParams.version.name()).append("=").append(VERSION_1);
        builder.append("&").append(OWSConstants.RequestParams.request.name()).append("=")
                .append(SosConstants.Operations.GetFeatureOfInterest.name());
        builder.append("&").append(Sos1Constants.GetFeatureOfInterestParams.featureOfInterestID.name()).append("=")
                .append(FOI_ID);
        return new URL(builder.toString());
    }

    protected URL getFoi200Url() throws MalformedURLException {
        StringBuilder builder = new StringBuilder();
        builder.append(SERVICE_URL);
        builder.append("?").append(OWSConstants.RequestParams.service.name()).append("=").append(SosConstants.SOS);
        builder.append("&").append(OWSConstants.RequestParams.version.name()).append("=").append(VERSION_2);
        builder.append("&").append(OWSConstants.RequestParams.request.name()).append("=")
                .append(SosConstants.Operations.GetFeatureOfInterest.name());
        builder.append("&").append(Sos2Constants.GetFeatureOfInterestParams.featureOfInterest.name()).append("=")
                .append(FOI_ID);
        return new URL(builder.toString());
    }

    protected URL getProcDesc100Url() throws UnsupportedEncodingException, MalformedURLException {
        StringBuilder builder = new StringBuilder();
        builder.append(SERVICE_URL);
        builder.append("?").append(OWSConstants.RequestParams.service.name()).append("=").append(SosConstants.SOS);
        builder.append("&").append(OWSConstants.RequestParams.version.name()).append("=").append(VERSION_1);
        builder.append("&").append(OWSConstants.RequestParams.request.name()).append("=")
                .append(SosConstants.Operations.DescribeSensor.name());
        builder.append("&").append(SosConstants.DescribeSensorParams.procedure.name()).append("=").append(PROC_ID);
        builder.append("&").append(Sos1Constants.DescribeSensorParams.outputFormat.name()).append("=")
                .append(URLEncoder.encode(SensorMLConstants.SENSORML_OUTPUT_FORMAT_MIME_TYPE, UTF8));
        return new URL(builder.toString());
    }

    protected URL getProcDesc200Url() throws UnsupportedEncodingException, MalformedURLException {
        StringBuilder builder = new StringBuilder();
        builder.append(SERVICE_URL);
        builder.append("?").append(OWSConstants.RequestParams.service.name()).append("=").append(SosConstants.SOS);
        builder.append("&").append(OWSConstants.RequestParams.version.name()).append("=").append(VERSION_2);
        builder.append("&").append(OWSConstants.RequestParams.request.name()).append("=")
                .append(SosConstants.Operations.DescribeSensor.name());
        builder.append("&").append(SosConstants.DescribeSensorParams.procedure.name()).append("=").append(PROC_ID);
        builder.append("&").append(Sos2Constants.DescribeSensorParams.procedureDescriptionFormat.name()).append("=")
                .append(URLEncoder.encode(SensorMLConstants.SENSORML_OUTPUT_FORMAT_URL, UTF8));
        return new URL(builder.toString());
    }

}
