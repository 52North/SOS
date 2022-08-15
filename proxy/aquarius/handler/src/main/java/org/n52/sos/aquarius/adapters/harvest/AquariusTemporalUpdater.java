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

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.joda.time.DateTime;
import org.n52.sensorweb.server.helgoland.adapters.harvest.HarvestContext;
import org.n52.sensorweb.server.helgoland.adapters.harvest.HarvesterResponse;
import org.n52.sensorweb.server.helgoland.adapters.harvest.TemporalHarvester;
import org.n52.sensorweb.server.helgoland.adapters.harvest.TemporalHarvesterResponse;
import org.n52.series.db.beans.DatasetEntity;
import org.n52.series.db.beans.FeatureEntity;
import org.n52.series.db.beans.PlatformEntity;
import org.n52.series.db.beans.ProcedureEntity;
import org.n52.series.db.beans.ServiceEntity;
import org.n52.shetland.ogc.ows.exception.OwsExceptionReport;
import org.n52.sos.aquarius.AquariusConstants.ChangeEvent;
import org.n52.sos.aquarius.ds.AquariusConnector;
import org.n52.sos.aquarius.harvest.AbstractAquariusHarvester;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.annotation.Transactional;

import com.aquaticinformatics.aquarius.sdk.timeseries.servicemodels.Publish.LocationDataServiceResponse;
import com.aquaticinformatics.aquarius.sdk.timeseries.servicemodels.Publish.TimeSeriesDescription;
import com.aquaticinformatics.aquarius.sdk.timeseries.servicemodels.Publish.TimeSeriesDescriptionListByUniqueIdServiceRequest;
import com.aquaticinformatics.aquarius.sdk.timeseries.servicemodels.Publish.TimeSeriesDescriptionListByUniqueIdServiceResponse;
import com.aquaticinformatics.aquarius.sdk.timeseries.servicemodels.Publish.TimeSeriesUniqueIdListServiceResponse;
import com.aquaticinformatics.aquarius.sdk.timeseries.servicemodels.Publish.TimeSeriesUniqueIds;
import com.google.common.collect.Lists;

public class AquariusTemporalUpdater extends AbstractAquariusHarvester implements TemporalHarvester {

    private static final Logger LOGGER = LoggerFactory.getLogger(AquariusTemporalUpdater.class);
    private static final String ERROR_UPDATE = "Error while updating!";

    @Override
    @Transactional(rollbackFor = Exception.class)
    public HarvesterResponse process(HarvestContext context) {
        if (context instanceof AquariusHarvesterContext) {
            try {
                AquariusConnector connector = ((AquariusHarvesterContext) context).getConnector();
                return update(connector, context.getLastUpdateTime());
            } catch (Exception e) {
                LOGGER.error(ERROR_UPDATE, e);
            }
        }
        return new TemporalHarvesterResponse();
    }

    protected TemporalHarvesterResponse update(AquariusConnector connector, DateTime changedSince) {
        boolean updated = false;
        try {
            ServiceEntity service = getOrInsertServiceEntity();
            TimeSeriesUniqueIdListServiceResponse timeSeriesUniqueIds =
                    connector.getTimeSeriesUniqueIds(getAquariusHelper().getTimeSeriesUniqueIdsRequest()
                            .setChangesSinceToken(changedSince.toDate().toInstant())
                            .setChangeEventType(ChangeEvent.Data.name()));
            if (timeSeriesUniqueIds != null && timeSeriesUniqueIds.getTimeSeriesUniqueIds() != null) {
                for (TimeSeriesDescription timeSeries : getTimeSeriesDescriptions(
                        timeSeriesUniqueIds.getTimeSeriesUniqueIds(), connector)) {
                    DatasetEntity dataset = getDatasetRepository().getOneByIdentifier(timeSeries.getUniqueId());
                    if (dataset != null) {
                        updateFirstLastObservation(dataset, timeSeries, connector);
                        getDatasetRepository().saveAndFlush(dataset);
                        updated = true;
                    } else {
                        if (getAquariusHelper().isCreateTemporal()) {
                            LocationDataServiceResponse location =
                                    getLocation(timeSeries.getLocationIdentifier(), connector);
                            if (checkLocation(location)) {
                                ProcedureEntity procedure = createProcedure(location, getProcedures(), service);
                                FeatureEntity feature = createFeature(location, getFeatures(), service);
                                PlatformEntity platform = createPlatform(location, getPlatforms(), service);
                                harvestDatasets(location, timeSeries, feature, procedure, platform, service,
                                        connector);
                                updated = true;
                            }
                        }
                    }
                }
                return new TemporalHarvesterResponse(updated, timeSeriesUniqueIds.getNextToken().toString());
            }
        } catch (OwsExceptionReport e) {
            LOGGER.error(ERROR_UPDATE, e);
        }
        return new TemporalHarvesterResponse(updated, null);
    }

    private List<TimeSeriesDescription> getTimeSeriesDescriptions(List<TimeSeriesUniqueIds> timeSeriesUniqueIds,
            AquariusConnector connector) throws OwsExceptionReport {
        Set<String> ids = timeSeriesUniqueIds.stream().map(t -> t.getUniqueId()).collect(Collectors.toSet());
        if (!getAquariusHelper().isCreateTemporal()) {
            Set<String> datasets =
                    getDatasetRepository().findAll().stream().map(d -> d.getIdentifier()).collect(Collectors.toSet());
            if (datasets != null && !datasets.isEmpty()) {
                ids.retainAll(datasets);
            }
        }
        if (ids.size() == 0) {
            return Collections.emptyList();
        }
        if (ids.size() > 50) {
            List<TimeSeriesDescription> data = new LinkedList<>();
            for (List<String> list : Lists.partition(new ArrayList<>(ids), 50)) {
                TimeSeriesDescriptionListByUniqueIdServiceResponse response = connector
                        .getTimeSeriesDescriptionsByUniqueId(new TimeSeriesDescriptionListByUniqueIdServiceRequest()
                                .setTimeSeriesUniqueIds(new ArrayList<>(list)));
                if (response != null && response.getTimeSeriesDescriptions() != null) {
                    data.addAll(response.getTimeSeriesDescriptions());
                }
            }
            return data;
        }
        TimeSeriesDescriptionListByUniqueIdServiceResponse response = connector.getTimeSeriesDescriptionsByUniqueId(
                new TimeSeriesDescriptionListByUniqueIdServiceRequest().setTimeSeriesUniqueIds(new ArrayList<>(ids)));
        if (response != null && response.getTimeSeriesDescriptions() != null) {
            return response.getTimeSeriesDescriptions();
        }
        return Collections.emptyList();
    }

    @Override
    public Logger getLogger() {
        return LOGGER;
    }

}
