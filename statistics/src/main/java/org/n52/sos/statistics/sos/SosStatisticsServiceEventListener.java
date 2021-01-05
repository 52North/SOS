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
package org.n52.sos.statistics.sos;

import javax.inject.Inject;

import org.n52.iceland.event.events.RequestEvent;
import org.n52.iceland.event.events.ResponseEvent;
import org.n52.iceland.statistics.api.interfaces.StatisticsServiceEventResolver;
import org.n52.iceland.statistics.impl.AbstractStatisticsServiceEventListener;
import org.n52.iceland.statistics.impl.StatisticsResolverFactory;
import org.n52.sos.statistics.sos.resolvers.SosRequestEventResolver;
import org.n52.sos.statistics.sos.resolvers.SosResponseEventResolver;

import com.google.common.collect.ImmutableSet;

import org.n52.janmayen.event.Event;

public class SosStatisticsServiceEventListener extends AbstractStatisticsServiceEventListener {

    @Inject
    private StatisticsResolverFactory resolverFactory;

    public SosStatisticsServiceEventListener() {
        registerEventType(ImmutableSet.<Class<? extends Event>> of(RequestEvent.class, ResponseEvent.class));
    }

    @Override
    protected StatisticsServiceEventResolver<?> findResolver(Event serviceEvent) {
        StatisticsServiceEventResolver<?> evtResolver = null;
        if (serviceEvent instanceof RequestEvent) {
            SosRequestEventResolver sosRequestEventResolver =
                    resolverFactory.getPrototypeBean(SosRequestEventResolver.class);
            sosRequestEventResolver.setEvent((RequestEvent) serviceEvent);
            evtResolver = sosRequestEventResolver;
        } else if (serviceEvent instanceof ResponseEvent) {
            SosResponseEventResolver responseEventResolver =
                    resolverFactory.getPrototypeBean(SosResponseEventResolver.class);
            responseEventResolver.setEvent((ResponseEvent) serviceEvent);
            evtResolver = responseEventResolver;
        }
        return evtResolver;
    }

}
