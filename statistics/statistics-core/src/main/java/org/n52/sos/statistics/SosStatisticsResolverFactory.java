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
package org.n52.sos.statistics;

import javax.inject.Inject;

import org.n52.sos.statistics.impl.resolvers.CountingOutputstreamEventResolver;
import org.n52.sos.statistics.impl.resolvers.DefaultServiceEventResolver;
import org.n52.sos.statistics.impl.resolvers.OutgoingResponseEventResolver;
import org.n52.sos.statistics.impl.resolvers.SosExceptionEventResolver;
import org.n52.sos.statistics.sos.resolvers.SosRequestEventResolver;
import org.n52.sos.statistics.sos.resolvers.SosResponseEventResolver;
import org.springframework.context.ApplicationContext;

public class SosStatisticsResolverFactory {

    @Inject
    private ApplicationContext ctx;

    // prototype instance dependencies
    public SosRequestEventResolver getSosRequestEventResolver() {
        return ctx.getBean(SosRequestEventResolver.class);
    }

    public SosResponseEventResolver getSosResponseEventResolver() {
        return ctx.getBean(SosResponseEventResolver.class);
    }

    public SosExceptionEventResolver getSosExceptionEventResolver() {
        return ctx.getBean(SosExceptionEventResolver.class);
    }

    public DefaultServiceEventResolver getDefaultServiceEventResolver() {
        return ctx.getBean(DefaultServiceEventResolver.class);
    }

    public OutgoingResponseEventResolver getOutgoingResponseEventResolver() {
        return ctx.getBean(OutgoingResponseEventResolver.class);
    }

    public CountingOutputstreamEventResolver getCountingOutputstreamEventResolver() {
        return ctx.getBean(CountingOutputstreamEventResolver.class);
    }

}
