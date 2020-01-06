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
package org.n52.sos.service.it.functional;

import static org.hamcrest.Matchers.notNullValue;
import static org.junit.Assert.assertThat;

import java.io.IOException;
import java.io.InputStream;

import org.junit.Test;
import org.n52.janmayen.http.MediaTypes;
import org.n52.shetland.ogc.sos.Sos1Constants;
import org.n52.shetland.ogc.sos.Sos2Constants;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import net.opengis.ows.x11.ExceptionReportDocument;

public class ObservationJsonEncodingsTest extends AbstractObservationEncodingsTest {

    @Test
    public void testSos2GetObsJson() throws IOException {
        testGetObsJson(Sos2Constants.SERVICEVERSION);
    }

    @Test
    public void testSos1GetObsJson() throws IOException {
        // json not implemented for SOS 1.0.0, expect an ExceptionDocument
        testGetObsXmlResponse(Sos1Constants.SERVICEVERSION, MediaTypes.APPLICATION_JSON.toString(),
                ExceptionReportDocument.class);
    }

    private void testGetObsJson(String serviceVersion) throws IOException {
        InputStream responseStream = sendGetObsKvp(serviceVersion, MediaTypes.APPLICATION_JSON.toString())
                .asInputStream();
        JsonNode json = new ObjectMapper().readTree(responseStream);
        assertThat(json, notNullValue());
    }

}
