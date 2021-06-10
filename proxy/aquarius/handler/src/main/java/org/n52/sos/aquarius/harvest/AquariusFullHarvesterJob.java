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

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.n52.series.db.beans.ServiceEntity;
import org.n52.shetland.ogc.ows.exception.OwsExceptionReport;
import org.n52.sos.aquarius.ds.AquariusConnector;
import org.n52.sos.aquarius.pojo.Location;
import org.n52.sos.aquarius.pojo.TimeSeriesDescription;
import org.n52.sos.proxy.harvest.FullHarvesterJob;
import org.quartz.DisallowConcurrentExecution;
import org.quartz.JobExecutionContext;
import org.quartz.PersistJobDataAfterExecution;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@PersistJobDataAfterExecution
@DisallowConcurrentExecution
public class AquariusFullHarvesterJob extends AbstractAquariusHarvesterJob implements FullHarvesterJob {

    private static final Logger LOGGER = LoggerFactory.getLogger(AquariusFullHarvesterJob.class);

    @Override
    protected void save(JobExecutionContext context, AquariusConnector connector) throws OwsExceptionReport {
        procedures.clear();
        phenomenon.clear();
        categories.clear();
        offerings.clear();
        parameters.clear();
        features.clear();
        platforms.clear();
        Set<Location> locations = getLocationList(connector);
        Map<String, Set<TimeSeriesDescription>> locationDataSets = new HashMap<>();
        parameters = getParameterList(connector);
        ServiceEntity service = getServiceEntity();
        for (Location location : locations) {
            if (location.getLatitude() != null && location.getLongitude() != null) {
                try {
                    Set<TimeSeriesDescription> timeseries = getTimeSeries(location.getIdentifier(), connector);
                    if (timeseries != null && !timeseries.isEmpty()) {
                        locationDataSets.put(location.getIdentifier(), timeseries);
                        harvestDatasets(location, timeseries, service, connector);
                    }
                } catch (OwsExceptionReport e) {
                    LOGGER.error(
                            String.format("Error harvesting timeseries for location '%s'!", location.getIdentifier()),
                            e);
                }
            }
        }
    }

}
