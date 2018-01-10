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
package org.n52.sos.statistics.sos.resolvers;

import java.util.Map;

import org.n52.iceland.event.events.ResponseEvent;
import org.n52.shetland.ogc.ows.service.OwsServiceResponse;
import org.n52.iceland.statistics.api.interfaces.StatisticsServiceEventHandler;
import org.n52.iceland.statistics.api.interfaces.StatisticsServiceEventResolver;
import org.n52.iceland.statistics.api.utils.EventHandlerFinder;

public class SosResponseEventResolver implements StatisticsServiceEventResolver<ResponseEvent> {

    private ResponseEvent event;
    private Map<String, StatisticsServiceEventHandler<?>> handlers;

    @Override
    public Map<String, Object> resolve() {
        if (event == null || event.getResponse() == null) {
            return null;
        }
        OwsServiceResponse response = event.getResponse();
        StatisticsServiceEventHandler<OwsServiceResponse> handler = EventHandlerFinder.findHandler(response, handlers);

        return handler.resolveAsMap(response);
    }

    @Override
    public void setEvent(ResponseEvent event) {
        this.event = event;
    }

    @Override
    public ResponseEvent getEvent() {
        return event;
    }

    @Override
    public void setHandlers(Map<String, StatisticsServiceEventHandler<?>> handlers) {
        this.handlers = handlers;
    }

}
