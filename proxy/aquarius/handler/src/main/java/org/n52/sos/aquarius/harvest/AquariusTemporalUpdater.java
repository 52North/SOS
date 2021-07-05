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
package org.n52.sos.aquarius.harvest;

import java.util.HashSet;
import java.util.Set;

import javax.inject.Inject;

import org.joda.time.DateTime;
import org.n52.sensorweb.server.db.factory.ServiceEntityFactory;
import org.n52.sensorweb.server.db.repositories.core.DatasetRepository;
import org.n52.series.db.beans.DatasetEntity;
import org.n52.shetland.ogc.ows.exception.OwsExceptionReport;
import org.n52.sos.aquarius.AquariusConstants;
import org.n52.sos.aquarius.ds.AquariusConnector;
import org.n52.sos.aquarius.ds.AquariusHelper;
import org.n52.sos.aquarius.pojo.TimeSeriesDescription;
import org.n52.sos.aquarius.requests.GetTimeSeriesDescriptionList;
import org.n52.sos.proxy.da.InsertionRepository;
import org.quartz.JobDataMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.annotation.Transactional;

public class AquariusTemporalUpdater implements AquariusHarvesterHelper, AquariusEntityBuilder {

    private static final Logger LOGGER = LoggerFactory.getLogger(AquariusTemporalUpdater.class);

    @Inject
    private ServiceEntityFactory serviceEntityFactory;

    @Inject
    private DatasetRepository datasetRepository;

    @Inject
    private InsertionRepository insertionRepository;

    @Inject
    private AquariusHelper aquariusHelper;

    @Transactional(rollbackFor = Exception.class)
    public void update(DateTime now, JobDataMap mergedJobDataMap, AquariusConnector connector) {

    }

    @Override
    public InsertionRepository getInsertionRepository() {
        return insertionRepository;
    }

    @Override
    public ServiceEntityFactory getServiceEntityFactory() {
        return serviceEntityFactory;
    }

    @Override
    public DatasetRepository getDatasetRepository() {
        return datasetRepository;
    }

    @Override
    public String getConnectorName() {
        return AquariusConstants.CONNECTOR;
    }

    public AquariusHelper getAquariusHelper() {
        return aquariusHelper;
    }

    protected Set<TimeSeriesDescription> getTimeSeriesChangedSince(AquariusConnector connector, DateTime changedSince)
            throws OwsExceptionReport {
        Set<TimeSeriesDescription> set = new HashSet<>();

        for (TimeSeriesDescription timeSeries : connector.getTimeSeriesDescriptions(
                (GetTimeSeriesDescriptionList) getAquariusHelper().getGetTimeSeriesDescriptionListRequest()
                        .withChangesSinceToken(changedSince.toString()))) {
            DatasetEntity dataset = getDatasetRepository().getOneByIdentifier(timeSeries.getUniqueId());
            updateFirstLastObservation(dataset, timeSeries, connector);
            getDatasetRepository().saveAndFlush(dataset);
        }
        return set;
    }

}
