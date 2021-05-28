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
package org.n52.sos.decode.kvp;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.junit.Before;
import org.junit.Test;
import org.n52.shetland.ogc.ows.OWSConstants.GetCapabilitiesParams;
import org.n52.shetland.ogc.ows.OWSConstants.RequestParams;
import org.n52.shetland.ogc.ows.service.GetCapabilitiesRequest;
import org.n52.shetland.ogc.sos.SosConstants;
import org.n52.sos.decode.kvp.v2.DeleteSensorKvpDecoderv20;
import org.n52.svalbard.decode.exception.DecodingException;

/**
 * @author <a href="mailto:shane@axiomalaska.com">Shane StClair</a>
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
    public void basic() throws DecodingException {
        GetCapabilitiesRequest req = decoder.decode(createMap());
        MatcherAssert.assertThat(req, CoreMatchers.is(CoreMatchers.notNullValue()));
        MatcherAssert.assertThat(req.getOperationName(), CoreMatchers.is(SosConstants.Operations.GetCapabilities.name()));
        MatcherAssert.assertThat(req.getService(), CoreMatchers.is(SosConstants.SOS));
    }

    @Test
    public void acceptVersions() throws DecodingException {
        final Map<String, String> map = createMap();
        map.put(GetCapabilitiesParams.AcceptVersions.name(), ACCEPT_VERSIONS);
        GetCapabilitiesRequest req = decoder.decode(map);
        MatcherAssert.assertThat(req, CoreMatchers.is(CoreMatchers.notNullValue()));
        MatcherAssert.assertThat(req.getOperationName(), CoreMatchers.is(SosConstants.Operations.GetCapabilities.name()));
        MatcherAssert.assertThat(req.getService(), CoreMatchers.is(SosConstants.SOS));
        MatcherAssert.assertThat(req.getAcceptVersions(), CoreMatchers.is(Arrays.asList(ACCEPT_VERSIONS.split(","))));
    }

    @Test(expected = DecodingException.class)
    public void additionalParameter() throws DecodingException {
        final Map<String, String> map = createMap();
        map.put(ADDITIONAL_PARAMETER, ADDITIONAL_PARAMETER);
        decoder.decode(map);
    }

    private Map<String, String> createMap() {
        Map<String, String> map = new HashMap<String, String>(1);
        map.put(RequestParams.service.name(), SosConstants.SOS);
        return map;
    }
}
