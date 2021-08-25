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

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.joda.time.DateTime;
import org.n52.series.db.beans.DatasetEntity;
import org.n52.series.db.beans.FeatureEntity;
import org.n52.series.db.beans.PlatformEntity;
import org.n52.series.db.beans.ProcedureEntity;
import org.n52.series.db.beans.ServiceEntity;
import org.n52.shetland.ogc.ows.exception.OwsExceptionReport;
import org.n52.sos.aquarius.AquariusConstants;
import org.n52.sos.aquarius.ds.AquariusConnector;
import org.n52.sos.aquarius.pojo.Location;
import org.n52.sos.aquarius.pojo.TimeSeriesDescription;
import org.n52.sos.aquarius.pojo.TimeSeriesUniqueId;
import org.n52.sos.aquarius.pojo.TimeSeriesUniqueIds;
import org.n52.sos.aquarius.requests.AbstractAquariusGetRequest.ChangeEvent;
import org.n52.sos.aquarius.requests.GetTimeSeriesDescriptionsByUniqueId;
import org.n52.sos.aquarius.requests.GetTimeSeriesUniqueIdList;
import org.quartz.JobDataMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.collect.Lists;

public class AquariusTemporalUpdater extends AbstractAquariusHarvester {

    private static final Logger LOGGER = LoggerFactory.getLogger(AquariusTemporalUpdater.class);

    @Transactional(rollbackFor = Exception.class)
    public TemporalUpdateResponse update(JobDataMap mergedJobDataMap, AquariusConnector connector) {
        return update(connector, (DateTime) mergedJobDataMap.get(AquariusConstants.LAST_UPDATE_TIME));
    }

    protected TemporalUpdateResponse update(AquariusConnector connector, DateTime changedSince) {
        boolean updated = false;
        try {
            ServiceEntity service = null;
            TimeSeriesUniqueIds timeSeriesUniqueIds = connector.getTimeSeriesUniqueIds(
                    (GetTimeSeriesUniqueIdList) getAquariusHelper().getTimeSeriesUniqueIdsRequest()
                            .withChangesSinceToken(changedSince.toString()).withChangeEventType(ChangeEvent.Data));
            if (timeSeriesUniqueIds.hasTimeSeriesUniqueIds()) {
                for (TimeSeriesDescription timeSeries : getTimeSeriesDescriptions(
                        timeSeriesUniqueIds.getTimeSeriesUniqueIds(), connector)) {
                    DatasetEntity dataset = getDatasetRepository().getOneByIdentifier(timeSeries.getUniqueId());
                    if (dataset != null) {
                        updateFirstLastObservation(dataset, timeSeries, connector);
                        getDatasetRepository().saveAndFlush(dataset);
                        updated = true;
                    } else {
                        if (getAquariusHelper().isCreateTemporal()) {
                            Location location = getLocation(timeSeries.getLocationIdentifier(), connector);
                            if (checkLocation(location)) {
                                if (service == null) {
                                    service = getOrInsertServiceEntity();
                                }
                                ProcedureEntity procedure = createProcedure(location, procedures, service);
                                FeatureEntity feature = createFeature(location, features, service);
                                PlatformEntity platform = createPlatform(location, platforms, service);
                                harvestDatasets(location, timeSeries, feature, procedure, platform, service, connector);
                                updated = true;
                            }
                        }
                    }
                }
            }
            return new TemporalUpdateResponse(updated, timeSeriesUniqueIds.getNextToken());
        } catch (OwsExceptionReport e) {
            LOGGER.error("Error while updating!", e);
        }
        return new TemporalUpdateResponse(updated, null);
    }

    private List<TimeSeriesDescription> getTimeSeriesDescriptions(List<TimeSeriesUniqueId> timeSeriesUniqueIds,
            AquariusConnector connector) throws OwsExceptionReport {
        Set<String> ids = timeSeriesUniqueIds.stream()
                .map(t -> t.getUniqueId())
                .collect(Collectors.toSet());
        if (!getAquariusHelper().isCreateTemporal()) {
            Set<String> datasets = getDatasetRepository().findAll().stream().map(d -> d.getIdentifier())
            .collect(Collectors.toSet());
            if (datasets != null && !datasets.isEmpty()) {
                ids.retainAll(datasets);
            }
        }
        if (ids.size() == 0) {
            return Collections.emptyList();
        }
        if (ids.size() > 50) {
            List<TimeSeriesDescription> data = new LinkedList<>();
            for (List<String> list : Lists.partition(new LinkedList<>(ids), 50)) {
                data.addAll(
                        connector.getTimeSeriesDescriptionsByUniqueId(new GetTimeSeriesDescriptionsByUniqueId(list)));
            }
            return data;
        }
        return connector.getTimeSeriesDescriptionsByUniqueId(new GetTimeSeriesDescriptionsByUniqueId(ids));
    }

    @Override
    public Logger getLogger() {
        return LOGGER;
    }

}
