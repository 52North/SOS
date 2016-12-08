/*
 * Copyright (C) 2012-2016 52°North Initiative for Geospatial Open Source
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
package org.n52.sos.ds.hibernate.cache.proxy;

import java.util.Date;
import java.util.logging.Level;

import javax.inject.Inject;

import org.n52.iceland.ds.ConnectionProvider;
import org.n52.io.task.ScheduledJob;
import org.n52.proxy.config.DataSourcesConfig;
import org.n52.proxy.config.DataSourcesConfig.DataSourceConfig;
import org.n52.proxy.connector.EntityBuilder;
import org.n52.proxy.db.da.InsertRepository;
import org.n52.series.db.beans.CategoryEntity;
import org.n52.series.db.beans.CountDatasetEntity;
import org.n52.series.db.beans.FeatureEntity;
import org.n52.series.db.beans.MeasurementDatasetEntity;
import org.n52.series.db.beans.OfferingEntity;
import org.n52.series.db.beans.PhenomenonEntity;
import org.n52.series.db.beans.ProcedureEntity;
import org.n52.series.db.beans.ServiceEntity;
import org.n52.series.db.beans.UnitEntity;
import org.n52.shetland.ogc.ows.exception.OwsExceptionReport;
import org.n52.sos.ds.hibernate.HibernateSessionHolder;
import org.quartz.DisallowConcurrentExecution;
import org.quartz.Job;
import org.quartz.JobBuilder;
import org.quartz.JobDataMap;
import org.quartz.JobDetail;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.PersistJobDataAfterExecution;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

@PersistJobDataAfterExecution
@DisallowConcurrentExecution
public class HibernateDataSourceHarvesterJob extends ScheduledJob implements Job {

    private final static Logger LOGGER = LoggerFactory.getLogger(HibernateDataSourceHarvesterJob.class);

    @Inject
    private InsertRepository insertRepository;
    private HibernateSessionHolder sessionHolder;

    public void setConnectionProvider(ConnectionProvider connectionProvider) {
        this.sessionHolder = new HibernateSessionHolder(connectionProvider);
    }

    public InsertRepository getInsertRepository() {
        return insertRepository;
    }

    public void setInsertRepository(InsertRepository insertRepository) {
        this.insertRepository = insertRepository;
    }

    @Override
    public JobDetail createJobDetails() {
        return JobBuilder.newJob(HibernateDataSourceHarvesterJob.class)
                .withIdentity(getJobName())
                .build();
    }

    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {
//        try {
            LOGGER.info(context.getJobDetail().getKey() + " execution starts.");

            ServiceEntity service = insertRepository.insertService(EntityBuilder.createService("localDB", "description of localDB", "localhost", "2.0.0"));

            insertRepository.prepareInserting(service);

            ProcedureEntity procedure = EntityBuilder.createProcedure("procedure", true, false, service);
//            FeatureEntity feature = EntityBuilder.createFeature("feature", EntityBuilder.createGeometry((52 + Math.random()), (7 + Math.random())), service);
            OfferingEntity offering = EntityBuilder.createOffering("offering", service);
            CategoryEntity category = EntityBuilder.createCategory("category" + new Date().getMinutes(), service);
            CategoryEntity category1 = EntityBuilder.createCategory("category", service);
            PhenomenonEntity phenomenon = EntityBuilder.createPhenomenon("phen", service);
            UnitEntity unit = EntityBuilder.createUnit("unit", service);

//            MeasurementDatasetEntity measurement = EntityBuilder.createMeasurementDataset(procedure, category, feature, offering, phenomenon, unit, service);
//            CountDatasetEntity countDataset = EntityBuilder.createCountDataset(procedure, category1, feature, offering, phenomenon, service);

//            insertRepository.insertDataset(measurement);
//            insertRepository.insertDataset(countDataset);

            insertRepository.cleanUp(service);

            LOGGER.info(context.getJobDetail().getKey() + " execution ends.");
//        } catch (OwsExceptionReport ex) {
//            java.util.logging.Logger.getLogger(HibernateDataSourceHarvesterJob.class.getName()).log(Level.SEVERE, null, ex);
//        }
    }

}
