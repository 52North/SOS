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
package org.n52.sos.decode.kvp.v2;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;

import java.util.HashMap;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.n52.sos.ogc.ows.OWSConstants.RequestParams;
import org.n52.sos.ogc.ows.OwsExceptionReport;
import org.n52.sos.ogc.sos.Sos2Constants;
import org.n52.sos.ogc.sos.Sos2Constants.DeleteSensorParams;
import org.n52.sos.ogc.sos.SosConstants;
import org.n52.sos.request.DeleteSensorRequest;

/**
 * @author Christian Autermann <c.autermann@52north.org>
 * @since 4.0.0
 */
public class DeleteSensorKvpDecoderv20Test extends DeleteSensorKvpDecoderv20 {
    private static final String PROCEDURE = "testprocedure";

    private static final String SERVICE = SosConstants.SOS;

    private static final String VERSION = Sos2Constants.SERVICEVERSION;

    private static final String ADDITIONAL_PARAMETER = "additionalParameter";

    private static final String EMPTY_STRING = "";

    private DeleteSensorKvpDecoderv20 decoder;

    @Before
    public void setUp() {
        this.decoder = new DeleteSensorKvpDecoderv20();
    }

    @Test
    public void correctMap() throws OwsExceptionReport {
        DeleteSensorRequest req = decoder.decode(createMap(SERVICE, VERSION, PROCEDURE));
        assertThat(req, is(notNullValue()));
        assertThat(req.getOperationName(), is(Sos2Constants.Operations.DeleteSensor.name()));
        assertThat(req.getProcedureIdentifier(), is(PROCEDURE));
        assertThat(req.getService(), is(SERVICE));
        assertThat(req.getVersion(), is(VERSION));
    }

    @Test(expected = OwsExceptionReport.class)
    public void additionalParameter() throws OwsExceptionReport {
        final Map<String, String> map = createMap(SERVICE, VERSION, PROCEDURE);
        map.put(ADDITIONAL_PARAMETER, ADDITIONAL_PARAMETER);
        decoder.decode(map);
    }

    @Test(expected = OwsExceptionReport.class)
    public void missingService() throws OwsExceptionReport {
        decoder.decode(createMap(null, VERSION, PROCEDURE));
    }

    @Test(expected = OwsExceptionReport.class)
    public void missingVersion() throws OwsExceptionReport {
        decoder.decode(createMap(SERVICE, null, PROCEDURE));
    }

    @Test(expected = OwsExceptionReport.class)
    public void missingProcedure() throws OwsExceptionReport {
        decoder.decode(createMap(SERVICE, VERSION, null));
    }

    @Test(expected = OwsExceptionReport.class)
    public void emptyService() throws OwsExceptionReport {
        decoder.decode(createMap(EMPTY_STRING, VERSION, PROCEDURE));
    }

    @Test(expected = OwsExceptionReport.class)
    public void emptyVersion() throws OwsExceptionReport {
        decoder.decode(createMap(SERVICE, EMPTY_STRING, PROCEDURE));
    }

    @Test(expected = OwsExceptionReport.class)
    public void emptyProcedure() throws OwsExceptionReport {
        decoder.decode(createMap(SERVICE, VERSION, EMPTY_STRING));
    }

    private Map<String, String> createMap(String service, String version, String procedure) {
        Map<String, String> map = new HashMap<String, String>(3);
        if (service != null) {
            map.put(RequestParams.service.name(), service);
        }
        if (version != null) {
            map.put(RequestParams.version.name(), version);
        }
        if (procedure != null) {
            map.put(DeleteSensorParams.procedure.name(), procedure);
        }
        return map;
    }
}
