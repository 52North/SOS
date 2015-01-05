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
package org.n52.sos.decode.kvp;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.n52.sos.decode.kvp.v2.DeleteSensorKvpDecoderv20;
import org.n52.sos.ogc.ows.OWSConstants.RequestParams;
import org.n52.sos.ogc.ows.OwsExceptionReport;
import org.n52.sos.ogc.sos.SosConstants;
import org.n52.sos.request.GetCapabilitiesRequest;

/**
 * @author Shane StClair <shane@axiomalaska.com>
 * @since 4.0.0
 */
public class GetCapabilitiesKvpDecoderTest extends DeleteSensorKvpDecoderv20 {
    private static final String ADDITIONAL_PARAMETER = "additionalParameter";

    private static final String ACCEPT_VERSIONS = "1.0.0,2.0";

    private static final String EMPTY_STRING = "";

    private GetCapabilitiesKvpDecoder decoder;

    @Before
    public void setUp() {
        this.decoder = new GetCapabilitiesKvpDecoder();
    }

    @Test
    public void basic() throws OwsExceptionReport {
        GetCapabilitiesRequest req = decoder.decode(createMap());
        assertThat(req, is(notNullValue()));
        assertThat(req.getOperationName(), is(SosConstants.Operations.GetCapabilities.name()));
        assertThat(req.getService(), is(SosConstants.SOS));
    }

    @Test
    public void acceptVersions() throws OwsExceptionReport {
        final Map<String, String> map = createMap();
        map.put(SosConstants.GetCapabilitiesParams.AcceptVersions.name(), ACCEPT_VERSIONS);
        GetCapabilitiesRequest req = decoder.decode(map);
        assertThat(req, is(notNullValue()));
        assertThat(req.getOperationName(), is(SosConstants.Operations.GetCapabilities.name()));
        assertThat(req.getService(), is(SosConstants.SOS));
        assertThat(req.getAcceptVersions(), is(Arrays.asList(ACCEPT_VERSIONS.split(","))));
    }

    @Test(expected = OwsExceptionReport.class)
    public void additionalParameter() throws OwsExceptionReport {
        final Map<String, String> map = createMap();
        map.put(ADDITIONAL_PARAMETER, ADDITIONAL_PARAMETER);
        decoder.decode(map);
    }

    @Test(expected = OwsExceptionReport.class)
    public void emptyParam() throws OwsExceptionReport {
        final Map<String, String> map = createMap();
        map.put(SosConstants.GetCapabilitiesParams.AcceptVersions.name(), EMPTY_STRING);
        decoder.decode(map);
    }

    private Map<String, String> createMap() {
        Map<String, String> map = new HashMap<String, String>(1);
        map.put(RequestParams.service.name(), SosConstants.SOS);
        return map;
    }
}
