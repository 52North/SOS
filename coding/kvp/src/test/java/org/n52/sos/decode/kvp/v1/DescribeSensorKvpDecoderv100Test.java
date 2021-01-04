/*
 * Copyright (C) 2012-2021 52°North Initiative for Geospatial Open Source
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
package org.n52.sos.decode.kvp.v1;

import java.util.HashMap;
import java.util.Map;

import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.junit.Before;
import org.junit.Test;
import org.n52.shetland.ogc.ows.OWSConstants.GetCapabilitiesParams;
import org.n52.shetland.ogc.ows.OWSConstants.RequestParams;
import org.n52.shetland.ogc.sos.Sos1Constants;
import org.n52.shetland.ogc.sos.SosConstants;
import org.n52.shetland.ogc.sos.request.DescribeSensorRequest;
import org.n52.sos.decode.kvp.v2.DeleteSensorKvpDecoderv20;
import org.n52.svalbard.decode.exception.DecodingException;

/**
 * @author <a href="mailto:shane@axiomalaska.com">Shane StClair</a>
 * @since 4.0.0
 */
public class DescribeSensorKvpDecoderv100Test extends DeleteSensorKvpDecoderv20 {
    private static final String PROCEDURE = "testprocedure";

    private static final String OUTPUT_FORMAT = "text/xml;subtype=\"some/fake/subtype\"";

    private static final String ADDITIONAL_PARAMETER = "additionalParameter";

    private static final String EMPTY_STRING = "";

    private DescribeSensorKvpDecoderv100 decoder;

    @Before
    public void setUp() {
        this.decoder = new DescribeSensorKvpDecoderv100();
    }

    @Test
    public void basic() throws DecodingException {
        DescribeSensorRequest req =
                decoder.decode(createMap(SosConstants.SOS, Sos1Constants.SERVICEVERSION, PROCEDURE, OUTPUT_FORMAT));
        MatcherAssert.assertThat(req, CoreMatchers.is(CoreMatchers.notNullValue()));
        MatcherAssert.assertThat(req.getOperationName(), CoreMatchers.is(SosConstants.Operations.DescribeSensor.name()));
        MatcherAssert.assertThat(req.getService(), CoreMatchers.is(SosConstants.SOS));
        MatcherAssert.assertThat(req.getVersion(), CoreMatchers.is(Sos1Constants.SERVICEVERSION));
        MatcherAssert.assertThat(req.getProcedure(), CoreMatchers.is(PROCEDURE));
    }

    @Test(expected = DecodingException.class)
    public void additionalParameter() throws DecodingException {
        final Map<String, String> map =
                createMap(SosConstants.SOS, Sos1Constants.SERVICEVERSION, PROCEDURE, OUTPUT_FORMAT);
        map.put(ADDITIONAL_PARAMETER, ADDITIONAL_PARAMETER);
        decoder.decode(map);
    }

    @Test(expected = DecodingException.class)
    public void emptyParam() throws DecodingException {
        final Map<String, String> map =
                createMap(SosConstants.SOS, Sos1Constants.SERVICEVERSION, PROCEDURE, OUTPUT_FORMAT);
        map.put(GetCapabilitiesParams.AcceptVersions.name(), EMPTY_STRING);
        decoder.decode(map);
    }

    private Map<String, String> createMap(String service, String version, String procedure, String outputFormat) {
        Map<String, String> map = new HashMap<>(1);
        map.put(RequestParams.service.name(), service);
        map.put(RequestParams.request.name(), SosConstants.Operations.DescribeSensor.name());
        map.put(RequestParams.version.name(), version);
        map.put(SosConstants.DescribeSensorParams.procedure.name(), procedure);
        map.put(Sos1Constants.DescribeSensorParams.outputFormat.name(), outputFormat);
        return map;
    }
}
