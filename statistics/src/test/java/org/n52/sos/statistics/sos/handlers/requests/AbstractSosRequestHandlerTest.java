/*
 * Copyright (C) 2012-2022 52°North Initiative for Geospatial Open Source
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

import java.util.List;
import java.util.Map;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mockito;
import org.n52.iceland.statistics.api.mappings.ServiceEventDataMapping;
import org.n52.janmayen.net.IPAddress;
import org.n52.shetland.ogc.ows.extension.Extensions;
import org.n52.shetland.ogc.ows.service.GetCapabilitiesRequest;
import org.n52.shetland.ogc.swe.simpleType.SweText;
import org.n52.shetland.ogc.swes.SwesExtension;

import basetest.HandlerBaseTest;

public class AbstractSosRequestHandlerTest extends HandlerBaseTest {

    // any handler would do
    @InjectMocks
    private GetCapabilitiesRequestHandler handler;

    @Before
    public void setUp() {
        Mockito.when(locationUtil.resolveOriginalIpAddress(Mockito.any())).thenReturn(new IPAddress("123.123.123.123"));
    }

    @Test
    public void validateAllFields() {

        GetCapabilitiesRequest request = new GetCapabilitiesRequest("SOS");
        request.setVersion("2.0.0");
        request.setRequestContext(requestContext);

        Extensions extensions = new Extensions();
        SwesExtension<SweText> ext = new SwesExtension<>(new SweText().setValue("value1"));
        extensions.addExtension(ext);

        SwesExtension<SweText> ext2 = new SwesExtension<>(new SweText().setValue("value2"));
        ext2.setDefinition("def2");
        ext2.setIdentifier("id2");
        extensions.addExtension(ext2);

        request.addExtensions(extensions);
        Map<String, Object> map = handler.resolveAsMap(request);

        Assert.assertNotNull(map.get(ServiceEventDataMapping.SR_VERSION_FIELD.getName()));
        Assert.assertNotNull(map.get(ServiceEventDataMapping.SR_SERVICE_FIELD.getName()));
        Assert.assertNotNull(map.get(ServiceEventDataMapping.SR_PROXIED_REQUEST_FIELD.getName()));
        Assert.assertNotNull(map.get(ServiceEventDataMapping.SR_OPERATION_NAME_FIELD.getName()));
        Assert.assertNotNull(map.get(ServiceEventDataMapping.SR_IP_ADDRESS_FIELD.getName()));
        Assert.assertNotNull(map.get(ServiceEventDataMapping.SR_CONTENT_TYPE.getName()));
        Assert.assertNotNull(map.get(ServiceEventDataMapping.SR_ACCEPT_TYPES.getName()));
        Assert.assertEquals(2, ((List<?>) map.get(ServiceEventDataMapping.SR_EXTENSIONS.getName())).size());
    }
}
