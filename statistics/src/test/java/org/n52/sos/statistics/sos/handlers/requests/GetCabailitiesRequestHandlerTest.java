/*
 * Copyright (C) 2012-2018 52°North Initiative for Geospatial Open Source
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
package org.n52.sos.statistics.sos.handlers.requests;

import java.util.Arrays;
import java.util.Collection;
import java.util.Map;

import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.InjectMocks;

import org.n52.shetland.ogc.ows.service.GetCapabilitiesRequest;
import org.n52.sos.statistics.sos.SosDataMapping;

import basetest.HandlerBaseTest;

public class GetCabailitiesRequestHandlerTest extends HandlerBaseTest {

    @InjectMocks
    GetCapabilitiesRequestHandler handler;

    @SuppressWarnings("unchecked")
    @Test
    public void testAllFields() {
        GetCapabilitiesRequest request = new GetCapabilitiesRequest("SOS");
        request.setAcceptVersions(Arrays.asList("ver1","ver2"));
        request.setAcceptFormats(Arrays.asList("for1","for2"));
        request.setSections(Arrays.asList("a","b","c"));
        request.setUpdateSequence("update-norbi");

        Map<String, Object> map = handler.resolveAsMap(request);

        Assert.assertEquals("update-norbi", map.get(SosDataMapping.GC_UPDATE_SEQUENCE.getName()));
        Assert.assertThat((Collection<String>) map.get(SosDataMapping.GC_VERSIONS_FIELD.getName()), CoreMatchers.hasItems("ver1","ver2"));
        Assert.assertThat((Collection<String>) map.get(SosDataMapping.GC_FORMATS_FIELD.getName()), CoreMatchers.hasItems("for1","for2"));
        Assert.assertThat((Collection<String>) map.get(SosDataMapping.GC_SECTIONS.getName()), CoreMatchers.hasItems("a","b","c"));
    }

}
