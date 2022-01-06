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
package org.n52.sos.aquarius.harvest;

import javax.inject.Inject;

import org.n52.iceland.ds.ConnectionProviderException;
import org.n52.shetland.ogc.ows.exception.OwsExceptionReport;
import org.n52.sos.aquarius.AquariusConstants;
import org.n52.sos.aquarius.ds.AquariusConnectionFactory;
import org.n52.sos.aquarius.ds.AquariusConnector;
import org.n52.sos.event.events.UpdateCache;
import org.n52.sos.proxy.harvest.AbstractHarvesterJob;
import org.quartz.DisallowConcurrentExecution;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.PersistJobDataAfterExecution;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@PersistJobDataAfterExecution
@DisallowConcurrentExecution
public abstract class AbstractAquariusHarvesterJob extends AbstractHarvesterJob {

    private static final Logger LOGGER = LoggerFactory.getLogger(AbstractAquariusHarvesterJob.class);

    @Inject
    private AquariusConnectionFactory connectionFactory;

    protected AquariusConnector getConnector() throws ConnectionProviderException {
        return connectionFactory.getConnection();
    }

    @Override
    protected String getGroup() {
        return AquariusConstants.GROUP;
    }

    @Override
    protected void executeInternal(JobExecutionContext context) throws JobExecutionException {
        Long start = System.currentTimeMillis();
        boolean processed = false;
        try {
            LOGGER.debug(context.getJobDetail()
                    .getKey() + " execution starts.");
            processed = process(context, getConnector());
        } catch (Exception ex) {
            LOGGER.error("Error while harvesting data!", ex);
        } finally {
            LOGGER.debug(context.getJobDetail()
                    .getKey() + " execution finished in {} ms.", System.currentTimeMillis() - start);
            if (processed) {
                getEventBus().submit(new UpdateCache());
            }
        }
    }

    protected abstract boolean process(JobExecutionContext context, AquariusConnector connector)
            throws OwsExceptionReport;

}
