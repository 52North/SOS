/*
 * Copyright (C) 2012-2022 52°North Spatial Information Research GmbH
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
package org.n52.sos.aquarius.adapters.harvest;

import org.n52.iceland.ds.ConnectionProviderException;
import org.n52.sensorweb.server.helgoland.adapters.harvest.HarvestContext;
import org.n52.sos.aquarius.ds.AquariusConnectionFactory;
import org.n52.sos.prox.harvest.AbstractAdaptersServiceConstellation;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionException;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

@SuppressFBWarnings({ "EI_EXPOSE_REP2" })
public class AquariusServiceConstellation extends AbstractAdaptersServiceConstellation {

    private AquariusConnectionFactory factory;

    public AquariusServiceConstellation(AquariusConnectionFactory factory) {
        super(AquariusFullHarvester.class.getName(), AquariusTemporalUpdater.class.getName());
        this.factory = factory;
    }

    public HarvestContext getHavesterContext() throws JobExecutionException {
        try {
            return new AquariusHarvesterContext(this, factory.getConnection());
        } catch (ConnectionProviderException e) {
            throw new JobExecutionException(e);
        }
    }

    public HarvestContext getHavesterContext(JobDataMap jobDataMap) throws JobExecutionException {
        try {
            return new AquariusHarvesterContext(this, jobDataMap, factory.getConnection());
        } catch (ConnectionProviderException e) {
            throw new JobExecutionException(e);
        }
    }
}
