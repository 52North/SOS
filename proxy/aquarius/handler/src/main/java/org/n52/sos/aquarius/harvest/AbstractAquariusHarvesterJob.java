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

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

import javax.inject.Inject;

import org.n52.iceland.ds.ConnectionProviderException;
import org.n52.sensorweb.server.db.factory.ServiceEntityFactory;
import org.n52.series.db.beans.CategoryEntity;
import org.n52.series.db.beans.DatasetEntity;
import org.n52.series.db.beans.FeatureEntity;
import org.n52.series.db.beans.OfferingEntity;
import org.n52.series.db.beans.PhenomenonEntity;
import org.n52.series.db.beans.PlatformEntity;
import org.n52.series.db.beans.ProcedureEntity;
import org.n52.series.db.beans.ServiceEntity;
import org.n52.shetland.ogc.ows.exception.CodedException;
import org.n52.shetland.ogc.ows.exception.OwsExceptionReport;
import org.n52.sos.aquarius.AquariusConstants;
import org.n52.sos.aquarius.dao.AquariusGetObservationDao;
import org.n52.sos.aquarius.ds.AquariusConnectionFactory;
import org.n52.sos.aquarius.ds.AquariusConnector;
import org.n52.sos.aquarius.ds.AquariusHelper;
import org.n52.sos.aquarius.pojo.Location;
import org.n52.sos.aquarius.pojo.Parameter;
import org.n52.sos.aquarius.pojo.Parameters;
import org.n52.sos.aquarius.pojo.TimeSeriesDescription;
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
public abstract class AbstractAquariusHarvesterJob extends AbstractHarvesterJob implements AquariusHarvesterHelper {

    private static final Logger LOGGER = LoggerFactory.getLogger(AbstractAquariusHarvesterJob.class);

    protected Map<String, ProcedureEntity> procedures = new HashMap<>();

    protected Map<String, PhenomenonEntity> phenomenon = new HashMap<>();

    protected Map<String, CategoryEntity> categories = new HashMap<>();

    protected Map<String, OfferingEntity> offerings = new HashMap<>();

    protected Map<String, FeatureEntity> features = new HashMap<>();

    protected Map<String, PlatformEntity> platforms = new HashMap<>();

    protected Map<String, Parameter> parameters = new HashMap<>();

    @Inject
    private AquariusConnectionFactory connectionFactory;

    @Inject
    private ServiceEntityFactory serviceEntityFactory;

    @Inject
    private AquariusHelper aquariusHelper;

    public ServiceEntity getServiceEntity() {
        ServiceEntity serviceEntity = serviceEntityFactory.getServiceEntity();
        if (serviceEntity != null && (serviceEntity.getConnector() == null || serviceEntity.getConnector()
                .isEmpty())) {
            serviceEntity.setConnector(AquariusGetObservationDao.class.getName());
        }
        return serviceEntity;
    }

    @Override
    public String getConnectorName() {
        return AquariusConstants.CONNECTOR;
    }

    public AquariusHelper getAquariusHelper() {
        return aquariusHelper;
    }

    protected AquariusConnector getConnector() throws ConnectionProviderException {
        return connectionFactory.getConnection();
    }

    @Override
    protected void executeInternal(JobExecutionContext context) throws JobExecutionException {
        try {
            LOGGER.debug(context.getJobDetail()
                    .getKey() + " execution starts.");
            save(context, getConnector());
        } catch (Exception ex) {
            LOGGER.error("Error while harvesting data!", ex);
        } finally {
            LOGGER.debug(context.getJobDetail()
                    .getKey() + " execution finished.");
            getEventBus().submit(new UpdateCache());
        }
    }

    protected abstract void save(JobExecutionContext context, AquariusConnector connector) throws OwsExceptionReport;

    protected Map<String, Location> getLocations(AquariusConnector connector) throws OwsExceptionReport {
        Map<String, Location> locs = new LinkedHashMap<>();
        for (Location location : connector.getLocations(getAquariusHelper().getLocationDescriptionListRequest())) {
            locs.put(location.getIdentifier(), location);
        }
        return locs;
    }

    protected Map<String, Parameter> getParameterList(AquariusConnector connector) throws OwsExceptionReport {
        Map<String, Parameter> paramMap = new HashMap<>();
        Parameters params = connector.getParameterList();
        if (params.hasParameters()) {
            for (Parameter param : params.getParameters()) {
                paramMap.put(param.getIdentifier(), param);
            }
        }
        return paramMap;
    }

    protected Set<TimeSeriesDescription> getTimeSeries(AquariusConnector connector) throws OwsExceptionReport {
        Set<TimeSeriesDescription> set = new HashSet<>();
        for (TimeSeriesDescription timeSeries : connector
                .getTimeSeriesDescriptions(getAquariusHelper().getGetTimeSeriesDescriptionListRequest())) {
            set.add(timeSeries);
            getAquariusHelper().addDataset(timeSeries);
        }
        return set;
    }

    protected Set<TimeSeriesDescription> getTimeSeries(String locationIdentifier, AquariusConnector connector)
            throws OwsExceptionReport {
        Set<TimeSeriesDescription> set = new HashSet<>();
        for (TimeSeriesDescription timeSeries : connector
                .getTimeSeriesDescriptions(getAquariusHelper().getGetTimeSeriesDescriptionListRequest()
                        .setLocationIdentifier(locationIdentifier))) {
            set.add(timeSeries);
            getAquariusHelper().addDataset(timeSeries);
        }
        return set;
    }

    protected void harvestDatasets(Map<String, Location> locations, Collection<TimeSeriesDescription> timeseries,
            ServiceEntity service, AquariusConnector connector) throws CodedException {
        for (TimeSeriesDescription ts : timeseries) {
            if (checkLocation(ts.getLocationIdentifier(), locations)) {
                Location location = locations.get(ts.getLocationIdentifier());
                ProcedureEntity procedure = createProcedure(location, procedures, service);
                FeatureEntity feature = createFeature(location, features, service);
                PlatformEntity platform = createPlatform(location, platforms, service);
                harvestDatasets(location, ts, feature, procedure, platform, service, connector);
            }
        }
    }

    protected void harvestDatasets(Location location, TimeSeriesDescription timeSeries, FeatureEntity feature,
            ProcedureEntity procedure, PlatformEntity platform, ServiceEntity service, AquariusConnector connector) {
        if (feature.isSetGeometry()) {
            try {
                if (getAquariusHelper().checkForData(timeSeries)) {
                    OfferingEntity offering = createOffering(offerings, timeSeries,
                            parameters.get(timeSeries.getParameter()), procedure, service, getAquariusHelper());
                    DatasetEntity dataset = createDataset(procedure, offering, feature, platform, timeSeries,
                            parameters.get(timeSeries.getParameter()), service);
                    updateFirstLastObservation(dataset, timeSeries, connector);
                    if (dataset != null) {
                        getAquariusHelper().addLocation(location);
                        getAquariusHelper().addParameter(parameters.get(timeSeries.getParameter()));
                        PhenomenonEntity phen =
                                createPhenomenon(parameters.get(timeSeries.getParameter()), phenomenon, service);
                        dataset.setPhenomenon(phen);
                        dataset.setCategory(createCategory(phen, categories, service));
                        getInsertionRepository().insertDataset(dataset);
                    }
                }
            } catch (Exception e) {
                LOGGER.error(String.format("Error harvesting timeseries '%s'!", timeSeries.getUniqueId()), e);
            }
        }
    }

}
