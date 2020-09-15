/*
 * Copyright (C) 2015-2020 52°North Initiative for Geospatial Open Source
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
 * if the distribution is compliant with both the GNU General Public License
 * version 2 and the aforementioned licenses.
 *
 * This program is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
 * or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License
 * for more details.
 */
package org.n52.dataset;

import javax.persistence.EntityManager;

import org.n52.io.extension.RenderingHintsExtension;
import org.n52.io.extension.StatusIntervalsExtension;
import org.n52.io.extension.metadata.DatabaseMetadataExtension;
import org.n52.io.extension.metadata.MetadataAssembler;
import org.n52.io.extension.resulttime.ResultTimeAssembler;
import org.n52.io.extension.resulttime.ResultTimeExtension;
import org.n52.io.extension.resulttime.ResultTimeService;
import org.n52.io.handler.DefaultIoFactory;
import org.n52.io.response.ParameterOutput;
import org.n52.io.response.dataset.AbstractValue;
import org.n52.io.response.dataset.DatasetOutput;
import org.n52.io.response.dataset.TimeseriesMetadataOutput;
import org.n52.io.response.extension.LicenseExtension;
import org.n52.series.db.DataRepositoryTypeFactory;
import org.n52.series.db.old.dao.DbQueryFactory;
import org.n52.series.db.repositories.core.DatasetRepository;
import org.n52.web.ctrl.DatasetController;
import org.n52.web.ctrl.ParameterController;
import org.n52.web.ctrl.TimeseriesMetadataController;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@ComponentScan(basePackages = "org.n52.web.ctrl")
public class ControllerConfig {

    private <T extends ParameterController<ParameterOutput>> T withLicenseExtension(T controller) {
        controller.addMetadataExtension(new LicenseExtension());
        return controller;
    }

    @Bean
    public DefaultIoFactory<DatasetOutput<AbstractValue< ? >>, AbstractValue< ? >> defaultIoFactory() {
        return new DefaultIoFactory<>();
    }

    @Bean
    public DatabaseMetadataExtension databaseMetadataExtension(DatasetRepository datasetRepository, DbQueryFactory dbQueryFactory) {
        MetadataAssembler repository = new MetadataAssembler(datasetRepository, dbQueryFactory);
        return new DatabaseMetadataExtension(repository);
    }

    @Bean
    public ResultTimeExtension resultTimeExtension(EntityManager entityManager, DatasetRepository datasetRepository,
            DbQueryFactory dbQueryFactory, DatasetController datasetController) {
        ResultTimeAssembler repository = new ResultTimeAssembler(entityManager, datasetRepository,  dbQueryFactory);
        ResultTimeService resultTimeService = new ResultTimeService(repository);
        ResultTimeExtension extension = new ResultTimeExtension(resultTimeService);
        datasetController.addMetadataExtension(extension);
        return extension;
    }

    @Bean
    public StatusIntervalsExtension<DatasetOutput< ? >> statusIntervalExtension(DatasetController datasetController) {
        StatusIntervalsExtension<DatasetOutput< ? >> extension = new StatusIntervalsExtension<>();
        datasetController.addMetadataExtension(extension);
        return extension;
    }

    @Bean
    public RenderingHintsExtension<DatasetOutput< ? >> renderingHintsExtension(DatasetController datasetController) {
        RenderingHintsExtension<DatasetOutput< ? >> extension = new RenderingHintsExtension<>();
        datasetController.addMetadataExtension(extension);
        return extension;
    }

}
