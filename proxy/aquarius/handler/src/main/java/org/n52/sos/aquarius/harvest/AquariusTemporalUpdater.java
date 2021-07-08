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

import org.n52.series.db.beans.DatasetEntity;
import org.n52.shetland.ogc.ows.exception.OwsExceptionReport;
import org.n52.sos.aquarius.AquariusConstants;
import org.n52.sos.aquarius.ds.AquariusConnector;
import org.n52.sos.aquarius.pojo.TimeSeriesDescription;
import org.n52.sos.aquarius.requests.GetTimeSeriesDescriptionList;
import org.quartz.JobDataMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.annotation.Transactional;

public class AquariusTemporalUpdater extends AbstractAquariusHarvester {

    private static final Logger LOGGER = LoggerFactory.getLogger(AquariusTemporalUpdater.class);

    @Transactional(rollbackFor = Exception.class)
    public void update(JobDataMap mergedJobDataMap, AquariusConnector connector) {
        update(connector, (String) mergedJobDataMap.get(AquariusConstants.LAST_UPDATE_TIME));
    }

    protected Set<TimeSeriesDescription> update(AquariusConnector connector, String changedSince) {
        Set<TimeSeriesDescription> set = new HashSet<>();
        try {
            for (TimeSeriesDescription timeSeries : connector.getTimeSeriesDescriptions(
                    (GetTimeSeriesDescriptionList) getAquariusHelper().getGetTimeSeriesDescriptionListRequest()
                            .withChangesSinceToken(changedSince.toString()))) {
                try {
                    DatasetEntity dataset = getDatasetRepository().getOneByIdentifier(timeSeries.getUniqueId());
                    updateFirstLastObservation(dataset, timeSeries, connector);
                    getDatasetRepository().saveAndFlush(dataset);
                } catch (OwsExceptionReport e) {
                    LOGGER.error("Error while updating time series!", e);
                }
            }
        } catch (OwsExceptionReport e) {
            LOGGER.error("Error while updating!", e);
        }
        return set;
    }

}
