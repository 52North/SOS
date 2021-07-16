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
package org.n52.sos.proxy.harvest;

import javax.inject.Inject;

import org.n52.io.task.ScheduledJob;
import org.n52.janmayen.event.EventBus;
import org.n52.sensorweb.server.db.factory.ServiceEntityFactory;
import org.n52.sensorweb.server.db.repositories.core.DatasetRepository;
import org.n52.sos.proxy.da.InsertionRepository;
import org.quartz.DisallowConcurrentExecution;
import org.quartz.Job;
import org.quartz.JobBuilder;
import org.quartz.JobDetail;
import org.quartz.PersistJobDataAfterExecution;

@PersistJobDataAfterExecution
@DisallowConcurrentExecution
public abstract class AbstractHarvesterJob extends ScheduledJob implements Job {

    @Inject
    private InsertionRepository insertionRepository;
    @Inject
    private ServiceEntityFactory serviceEntityFactory;
    @Inject
    private DatasetRepository datasetRepository;
    @Inject
    private EventBus eventBus;

    @Override
    public JobDetail createJobDetails() {
        return JobBuilder.newJob(this.getClass()).withIdentity(getJobName()).build();
    }

    public InsertionRepository getInsertionRepository() {
        return insertionRepository;
    }

    public ServiceEntityFactory getServiceEntityFactory() {
        return serviceEntityFactory;
    }

    public DatasetRepository getDatasetRepository() {
        return datasetRepository;
    }

    protected EventBus getEventBus() {
        return eventBus;
    }
}
