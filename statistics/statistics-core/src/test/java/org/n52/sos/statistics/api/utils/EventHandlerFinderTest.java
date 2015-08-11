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
package org.n52.sos.statistics.api.utils;

import java.util.HashMap;
import java.util.Map;

import org.junit.Assert;
import org.junit.Test;
import org.n52.iceland.exception.ows.OperationNotSupportedException;
import org.n52.iceland.request.GetCapabilitiesRequest;
import org.n52.iceland.statistics.api.interfaces.StatisticsServiceEventHandler;
import org.n52.iceland.statistics.api.utils.EventHandlerFinder;
import org.n52.iceland.statistics.impl.handlers.exceptions.CodedExceptionEventHandler;
import org.n52.sos.statistics.sos.handlers.requests.GetCapabilitiesRequestHandler;

public class EventHandlerFinderTest {

    @Test
    public void findDirectGetCapabilitiesHandler() {
        Map<String, StatisticsServiceEventHandler<?>> handlers = new HashMap<>();
        GetCapabilitiesRequestHandler handler = new GetCapabilitiesRequestHandler();

        GetCapabilitiesRequest request = new GetCapabilitiesRequest("SOS");
        handlers.put(request.getClass().getSimpleName(), handler);

        Assert.assertNotNull(EventHandlerFinder.findHandler(request, handlers));
    }

    @Test(
            expected = NullPointerException.class)
    public void findNoHandlers() {
        Map<String, StatisticsServiceEventHandler<?>> handlers = new HashMap<>();
        GetCapabilitiesRequestHandler handler = new GetCapabilitiesRequestHandler();

        handlers.put("morpheus", handler);
        GetCapabilitiesRequest request = new GetCapabilitiesRequest("SOS");

        EventHandlerFinder.findHandler(request, handlers);
    }

    @Test
    public void findSubclassAsHandler() {
        Map<String, StatisticsServiceEventHandler<?>> handlers = new HashMap<>();
        CodedExceptionEventHandler handler = new CodedExceptionEventHandler();

        OperationNotSupportedException exception = new OperationNotSupportedException("GetCapabilities");

        handlers.put("CodedException", handler);

        Assert.assertNotNull(EventHandlerFinder.findHandler(exception, handlers));
    }
}
