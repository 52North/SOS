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
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.inject.Inject;

import org.hibernate.Hibernate;
import org.n52.sensorweb.server.db.factory.ServiceEntityFactory;
import org.n52.sensorweb.server.db.repositories.core.DatasetRepository;
import org.n52.series.db.beans.CategoryEntity;
import org.n52.series.db.beans.DataEntity;
import org.n52.series.db.beans.DatasetEntity;
import org.n52.series.db.beans.FeatureEntity;
import org.n52.series.db.beans.OfferingEntity;
import org.n52.series.db.beans.PhenomenonEntity;
import org.n52.series.db.beans.PlatformEntity;
import org.n52.series.db.beans.ProcedureEntity;
import org.n52.series.db.beans.ServiceEntity;
import org.n52.shetland.ogc.ows.exception.OwsExceptionReport;
import org.n52.sos.aquarius.dao.AquariusGetObservationDao;
import org.n52.sos.aquarius.ds.AquariusConnector;
import org.n52.sos.aquarius.ds.AquariusHelper;
import org.n52.sos.aquarius.pojo.Location;
import org.n52.sos.aquarius.pojo.Parameter;
import org.n52.sos.aquarius.pojo.Parameters;
import org.n52.sos.aquarius.pojo.TimeSeriesData;
import org.n52.sos.aquarius.pojo.TimeSeriesDescription;
import org.n52.sos.aquarius.pojo.Unit;
import org.n52.sos.aquarius.pojo.Units;
import org.n52.sos.aquarius.pojo.data.Point;
import org.n52.sos.aquarius.requests.GetLocationDescriptionList;
import org.n52.sos.proxy.da.InsertionRepository;
import org.n52.sos.proxy.harvest.AbstractProxyHelper;
import org.n52.sos.proxy.harvest.Harvester;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class AbstractAquariusHarvester implements Harvester, AquariusEntityBuilder {

    private static final Logger LOGGER = LoggerFactory.getLogger(AbstractAquariusHarvester.class);

    protected Map<String, ProcedureEntity> procedures = new HashMap<>();

    protected Map<String, PhenomenonEntity> phenomenon = new HashMap<>();

    protected Map<String, CategoryEntity> categories = new HashMap<>();

    protected Map<String, OfferingEntity> offerings = new HashMap<>();

    protected Map<String, FeatureEntity> features = new HashMap<>();

    protected Map<String, PlatformEntity> platforms = new HashMap<>();

    protected Map<String, Parameter> parameters = new HashMap<>();

    protected Map<String, Unit> units = new HashMap<>();

    protected Map<String, Location> locations = new HashMap<>();

    @Inject
    private ServiceEntityFactory serviceEntityFactory;

    @Inject
    private DatasetRepository datasetRepository;

    @Inject
    private InsertionRepository insertionRepository;

    @Inject
    private AquariusHelper aquariusHelper;

    public AquariusHelper getAquariusHelper() {
        return aquariusHelper;
    }

    @Override
    public AbstractProxyHelper getProxyHelper() {
        return getAquariusHelper();
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
        return AquariusGetObservationDao.class.getName();
    }

    @Override
    public <T> T unproxy(T entity) {
        return (T) Hibernate.unproxy(entity);
    }

    protected Map<String, Location> getLocations(AquariusConnector connector) throws OwsExceptionReport {
        return getLocations(getAquariusHelper().getLocationDescriptionListRequest(), connector);
    }

    private Map<String, Location> getLocations(GetLocationDescriptionList request, AquariusConnector connector)
            throws OwsExceptionReport {
        Map<String, Location> locs = new LinkedHashMap<>();
        for (Location location : connector.getLocations(request)) {
            locs.put(location.getIdentifier(), location);
        }
        this.locations.putAll(locs);
        return locations;
    }

    protected Location getLocation(String locationIdentifier, AquariusConnector connector) throws OwsExceptionReport {
        if (!this.locations.containsKey(locationIdentifier)) {
            getLocations(getAquariusHelper().getLocationDescriptionListRequest(locationIdentifier), connector);
        }
        return this.locations.get(locationIdentifier);
    }

    protected Map<String, Parameter> getParameterList(AquariusConnector connector) throws OwsExceptionReport {
        Map<String, Parameter> paramMap = new HashMap<>();
        Parameters params = connector.getParameterList();
        if (params.hasParameters()) {
            for (Parameter param : params.getParameters()) {
                paramMap.put(param.getIdentifier(), param);
            }
        }
        this.parameters.putAll(paramMap);
        return parameters;
    }

    protected Map<String, Unit> getUnitList(AquariusConnector connector) throws OwsExceptionReport {
        Map<String, Unit> unitMap = new HashMap<>();
        Units us = connector.getUnitList();
        if (us.hasUnits()) {
            for (Unit unit : us.getUnits()) {
                unitMap.put(unit.getIdentifier(), unit);
            }
        }
        this.units.putAll(unitMap);
        return units;
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

    protected void harvestDatasets(ServiceEntity service, AquariusConnector connector) throws OwsExceptionReport {
        Map<String, Location> locs = getLocations(connector);
        Map<String, DatasetEntity> datasets = getIdentifierDatasetMap(service);
        for (Entry<String, Location> entry : locs.entrySet()) {
            Location location = entry.getValue();
            if (checkLocation(location)) {
                for (TimeSeriesDescription ts : getTimeSeries(location.getIdentifier(), connector)) {
                    ProcedureEntity procedure = createProcedure(location, procedures, service);
                    FeatureEntity feature = createFeature(location, features, service);
                    PlatformEntity platform = createPlatform(location, platforms, service);
                    harvestDatasets(location, ts, feature, procedure, platform, service, connector);
                    datasets.remove(ts.getUniqueId());
                }
            }
        }
        if (!datasets.isEmpty()) {
            LOGGER.debug("Start removing datasets/timeSeries!");
            for (Entry<String, DatasetEntity> entry : datasets.entrySet()) {
                LOGGER.debug("Removing timeSeries with id '{}' from metadata!", entry.getKey());
                DatasetEntity dataset = entry.getValue();
                if (getAquariusHelper().isDeletePhysically()) {
                    getInsertionRepository().removeRelatedData(dataset);
                    getDatasetRepository().delete(dataset);
                } else {
                    dataset.setDeleted(true);
                    getDatasetRepository().saveAndFlush(dataset);
                }
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
                            parameters.get(timeSeries.getParameter()), units.get(timeSeries.getUnit()), service);
                    if (dataset != null) {
                        getAquariusHelper().addLocation(location);
                        getAquariusHelper().addParameter(parameters.get(timeSeries.getParameter()));
                        PhenomenonEntity phen =
                                createPhenomenon(parameters.get(timeSeries.getParameter()), phenomenon, service);
                        dataset.setPhenomenon(phen);
                        dataset.setCategory(createCategory(phen, categories, service));
                        DatasetEntity insertDataset = getInsertionRepository().insertDataset(dataset);
                        updateFirstLastObservation(insertDataset, timeSeries, connector);
                    }
                }
            } catch (Exception e) {
                LOGGER.error(String.format("Error harvesting timeseries '%s'!", timeSeries.getUniqueId()), e);
            }
        }
    }

    protected boolean checkLocation(String identifier, Map<String, Location> locations) {
        if (locations.containsKey(identifier)) {
            Location location = locations.get(identifier);
            return checkLocation(location);
        }
        return false;
    }

    protected boolean checkLocation(Location location) {
        return location != null && location.getLatitude() != null && location.getLongitude() != null;
    }

    protected void updateFirstLastObservation(DatasetEntity dataset, TimeSeriesDescription timeSeries,
            AquariusConnector connector) throws OwsExceptionReport {
        TimeSeriesData firstTimeSeriesData = connector.getTimeSeriesDataFirstPoint(timeSeries.getUniqueId());
        TimeSeriesData lastTimeSeriesData = connector.getTimeSeriesDataLastPoint(timeSeries.getUniqueId());
        updateDataset(dataset, firstTimeSeriesData, lastTimeSeriesData);
    }

    protected void updateDataset(DatasetEntity entity, TimeSeriesData firstTimeSeriesData,
            TimeSeriesData lastTimeSeriesDataLast) {
        if (firstTimeSeriesData != null) {
            insertData(entity, getAquariusHelper().applyQualifierChecker(firstTimeSeriesData)
                    .getFirstPoint());
        }
        if (lastTimeSeriesDataLast != null) {
            insertData(entity, getAquariusHelper().applyQualifierChecker(lastTimeSeriesDataLast)
                    .getLastPoint());
        }
    }

    protected DataEntity<?> insertData(DatasetEntity dataset, Point point) {
        return insertData(dataset, createDataEntity(dataset, point, (Long) null, aquariusHelper.isSetApplyRounding()));
    }

    protected DataEntity<?> insertData(DatasetEntity dataset, DataEntity<?> data) {
        return getInsertionRepository().insertData(dataset, data);
    }
}
