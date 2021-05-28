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
package org.n52.sos.statistics.sos.handlers.requests;

import java.util.Map;

import org.junit.Assert;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.n52.shetland.ogc.gml.CodeWithAuthority;
import org.n52.shetland.ogc.ows.exception.OwsExceptionReport;
import org.n52.shetland.ogc.sos.request.InsertResultTemplateRequest;
import org.n52.sos.statistics.sos.SosDataMapping;

import basetest.HandlerBaseTest;

public class InsertResultTemplateRequestHandlerTest extends HandlerBaseTest {

    private static final String ID = "id";

    @InjectMocks
    private InsertResultTemplateRequestHandler handler;

    @Test
    public void validateAllFields() throws OwsExceptionReport {
        InsertResultTemplateRequest request = new InsertResultTemplateRequest();
        request.setIdentifier(ID);
        request.setObservationTemplate(omConstellation);
        // SosResultEncoding encoding = new SosResultEncoding();
        // encoding.setEncoding(new SweTextEncoding());
        // request.setResultEncoding(encoding);

        Map<String, Object> map = handler.resolveAsMap(request);

        Assert.assertEquals(new CodeWithAuthority(ID), map.get(SosDataMapping.IRT_IDENTIFIER.getName()));
        Assert.assertNotNull(map.get(SosDataMapping.IRT_OBSERVATION_TEMPLATE.getName()));
        // Assert.assertEquals("xml",
        // map.get(SosDataMapping.IRT_RESULT_ENCODING));
    }
}
