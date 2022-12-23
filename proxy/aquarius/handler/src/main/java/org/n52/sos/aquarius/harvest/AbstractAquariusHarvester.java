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
package org.n52.sos.aquarius.harvest;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.inject.Inject;

import org.hibernate.Hibernate;
import org.n52.series.db.beans.CategoryEntity;
import org.n52.series.db.beans.DataEntity;
import org.n52.series.db.beans.DatasetEntity;
import org.n52.series.db.beans.FeatureEntity;
import org.n52.series.db.beans.OfferingEntity;
import org.n52.series.db.beans.PhenomenonEntity;
import org.n52.series.db.beans.PlatformEntity;
import org.n52.series.db.beans.ProcedureEntity;
import org.n52.series.db.beans.ServiceEntity;
import org.n52.series.db.beans.parameter.ParameterEntity;
import org.n52.shetland.ogc.om.series.wml.WaterMLConstants;
import org.n52.shetland.ogc.ows.exception.OwsExceptionReport;
import org.n52.sos.aquarius.AquariusConstants;
import org.n52.sos.aquarius.dao.AquariusGetObservationDao;
import org.n52.sos.aquarius.ds.AquariusConnector;
import org.n52.sos.aquarius.ds.AquariusHelper;
import org.n52.sos.aquarius.ds.Point;
import org.n52.sos.proxy.harvest.AbstractHarvester;
import org.n52.sos.proxy.harvest.AbstractProxyHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.aquaticinformatics.aquarius.sdk.timeseries.servicemodels.Publish.GradeListServiceResponse;
import com.aquaticinformatics.aquarius.sdk.timeseries.servicemodels.Publish.InterpolationType;
import com.aquaticinformatics.aquarius.sdk.timeseries.servicemodels.Publish.LocationDataServiceResponse;
import com.aquaticinformatics.aquarius.sdk.timeseries.servicemodels.Publish.LocationDescriptionListServiceRequest;
import com.aquaticinformatics.aquarius.sdk.timeseries.servicemodels.Publish.ParameterListServiceResponse;
import com.aquaticinformatics.aquarius.sdk.timeseries.servicemodels.Publish.ParameterMetadata;
import com.aquaticinformatics.aquarius.sdk.timeseries.servicemodels.Publish.QualifierListServiceResponse;
import com.aquaticinformatics.aquarius.sdk.timeseries.servicemodels.Publish.TimeSeriesDataServiceResponse;
import com.aquaticinformatics.aquarius.sdk.timeseries.servicemodels.Publish.TimeSeriesDescription;
import com.aquaticinformatics.aquarius.sdk.timeseries.servicemodels.Publish.UnitListServiceResponse;
import com.aquaticinformatics.aquarius.sdk.timeseries.servicemodels.Publish.UnitMetadata;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

@SuppressFBWarnings({ "EI_EXPOSE_REP", "MS_MUTABLE_COLLECTION" })
public abstract class AbstractAquariusHarvester extends AbstractHarvester implements AquariusEntityBuilder {

    private static final Logger LOGGER = LoggerFactory.getLogger(AbstractAquariusHarvester.class);

    private static final String UNKNOWN = "Unknown";

    private static final Map<String, ProcedureEntity> PROCEDURES = new HashMap<>();

    private static final Map<String, PhenomenonEntity> PHENOMEON = new HashMap<>();

    private static final Map<String, CategoryEntity> CATEGORIES = new HashMap<>();

    private static final Map<String, OfferingEntity> OFFERINGS = new HashMap<>();

    private static final Map<String, FeatureEntity> FEATURES = new HashMap<>();

    private static final Map<String, PlatformEntity> PLATFORMS = new HashMap<>();

    private static final Map<String, ParameterMetadata> PARAMETERS = new HashMap<>();

    private static final Map<String, UnitMetadata> UNITS = new HashMap<>();

    private static final Map<String, LocationDataServiceResponse> LOCATIONS = new HashMap<>();

    @Inject
    private AquariusHelper aquariusHelper;

    protected void clearMaps() {
        PROCEDURES.clear();
        PHENOMEON.clear();
        CATEGORIES.clear();
        OFFERINGS.clear();
        PARAMETERS.clear();
        FEATURES.clear();
        PLATFORMS.clear();
        LOCATIONS.clear();
        UNITS.clear();
    }

    protected Map<String, PlatformEntity> getPlatforms() {
        return PLATFORMS;
    }

    protected Map<String, FeatureEntity> getFeatures() {
        return FEATURES;
    }

    protected Map<String, ProcedureEntity> getProcedures() {
        return PROCEDURES;
    }

    public AquariusHelper getAquariusHelper() {
        return aquariusHelper;
    }

    @Override
    public AbstractProxyHelper getProxyHelper() {
        return getAquariusHelper();
    }

    @Override
    public String getConnectorName() {
        return AquariusGetObservationDao.class.getName();
    }

    @Override
    public <
            T> T unproxy(T entity) {
        return (T) Hibernate.unproxy(entity);
    }

    protected void checkGradesAndQualifier(AquariusConnector connector) {
        LOGGER.debug("Check for Grades and Qualifier");
        try {
            if (!getAquariusHelper().isSetUseGradesFromFile()) {

                GradeListServiceResponse grades = connector.getGradeList();
                if (grades != null) {
                    getAquariusHelper().setGrades(grades.getGrades());
                }
            }
        } catch (Exception e) {
            LOGGER.error("Error while querying grades!", e);
        }
        try {
            QualifierListServiceResponse qualifiers = connector.getQualifierList();
            if (qualifiers != null) {
                getAquariusHelper().setQualifiers(qualifiers.getQualifiers());
            }
        } catch (Exception e) {
            LOGGER.error("Error while querying qualifiers!", e);
        }
    }

    protected Map<String, LocationDataServiceResponse> getLocations(AquariusConnector connector)
            throws OwsExceptionReport {
        return getLocations(getAquariusHelper().getLocationDescriptionListRequest(), connector);
    }

    private Map<String, LocationDataServiceResponse> getLocations(LocationDescriptionListServiceRequest request,
            AquariusConnector connector) throws OwsExceptionReport {
        for (String location : connector.getLocationDescriptions(request)) {
            getLocation(location, connector);
        }
        return Collections.unmodifiableMap(LOCATIONS);
    }

    protected Set<String> getLocationIds(AquariusConnector connector) throws OwsExceptionReport {
        if (getAquariusHelper().hasConfiguredLocations()) {
            return getAquariusHelper().getConfiguredLocations();
        }
        return connector.getLocationDescriptions(getAquariusHelper().getLocationDescriptionListRequest());
    }

    protected LocationDataServiceResponse getLocation(String locationIdentifier, AquariusConnector connector)
            throws OwsExceptionReport {
        if (!this.LOCATIONS.containsKey(locationIdentifier)) {
            LOGGER.debug("Querying location '{}'!", locationIdentifier);
            AbstractAquariusHarvester.LOCATIONS.put(locationIdentifier,
                    connector.getLocation(getAquariusHelper().getLocationData(locationIdentifier)));
        }
        return this.LOCATIONS.get(locationIdentifier);
    }

    protected Map<String, ParameterMetadata> getParameterList(AquariusConnector connector) throws OwsExceptionReport {
        LOGGER.debug("Query parameters");
        Map<String, ParameterMetadata> paramMap = new HashMap<>();
        ParameterListServiceResponse params = connector.getParameterList();
        if (params.getParameters() != null) {
            for (ParameterMetadata param : params.getParameters()) {
                paramMap.put(param.getIdentifier(), param);
            }
        }
        AbstractAquariusHarvester.PARAMETERS.putAll(paramMap);
        return Collections.unmodifiableMap(PARAMETERS);
    }

    protected Map<String, UnitMetadata> getUnitList(AquariusConnector connector) throws OwsExceptionReport {
        LOGGER.debug("Query units");
        Map<String, UnitMetadata> unitMap = new HashMap<>();
        UnitListServiceResponse us = connector.getUnitList();
        if (us.getUnits() != null) {
            for (UnitMetadata unit : us.getUnits()) {
                unitMap.put(unit.getIdentifier(), unit);
            }
        }
        AbstractAquariusHarvester.UNITS.putAll(unitMap);
        return Collections.unmodifiableMap(UNITS);
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
        for (TimeSeriesDescription timeSeries : connector.getTimeSeriesDescriptions(getAquariusHelper()
                .getGetTimeSeriesDescriptionListRequest().setLocationIdentifier(locationIdentifier))) {
            set.add(timeSeries);
            getAquariusHelper().addDataset(timeSeries);
        }
        return set;
    }

    protected void harvestDatasets(LocationDataServiceResponse location, TimeSeriesDescription timeSeries,
            FeatureEntity feature, ProcedureEntity procedure, PlatformEntity platform, ServiceEntity service,
            AquariusConnector connector) {
        if (feature.isSetGeometry()) {
            try {
                if (getAquariusHelper().checkForData(timeSeries)) {
                    OfferingEntity offering =
                            createOffering(OFFERINGS, timeSeries, procedure, service, getAquariusHelper());
                    DatasetEntity dataset = createDataset(procedure, offering, feature, platform, timeSeries,
                            PARAMETERS.get(timeSeries.getParameter()), UNITS.get(timeSeries.getUnit()), service);
                    if (dataset != null) {
                        getAquariusHelper().addLocation(location);
                        getAquariusHelper().addParameter(PARAMETERS.get(timeSeries.getParameter()));
                        PhenomenonEntity phen =
                                createPhenomenon(PARAMETERS.get(timeSeries.getParameter()), PHENOMEON, service);
                        dataset.setPhenomenon(phen);
                        dataset.setCategory(createCategory(phen, CATEGORIES, service));
                        TimeSeriesDataServiceResponse firstTimeSeriesData =
                                connector.getTimeSeriesDataFirstPoint(timeSeries.getUniqueId());
                        addParameter(dataset, timeSeries, firstTimeSeriesData);
                        DatasetEntity insertDataset = getCRUDRepository().insertDataset(dataset);
                        updateFirstLastObservation(insertDataset, firstTimeSeriesData, timeSeries, connector);
                    }
                }
            } catch (Exception e) {
                LOGGER.error(String.format("Error harvesting timeseries '%s'!", timeSeries.getUniqueId()), e);
            }
        }
    }

    protected boolean checkLocation(String identifier, Map<String, LocationDataServiceResponse> locations) {
        if (locations.containsKey(identifier)) {
            LocationDataServiceResponse location = locations.get(identifier);
            return checkLocation(location);
        }
        return false;
    }

    protected boolean checkLocation(LocationDataServiceResponse location) {
        return location != null && location.getLatitude() != null && location.getLongitude() != null;
    }

    protected void updateFirstLastObservation(DatasetEntity dataset, TimeSeriesDescription timeSeries,
            AquariusConnector connector) throws OwsExceptionReport {
        TimeSeriesDataServiceResponse firstTimeSeriesData =
                connector.getTimeSeriesDataFirstPoint(timeSeries.getUniqueId());
        updateFirstLastObservation(dataset, firstTimeSeriesData, timeSeries, connector);
    }

    private void updateFirstLastObservation(DatasetEntity dataset, TimeSeriesDataServiceResponse firstTimeSeriesData,
            TimeSeriesDescription timeSeries, AquariusConnector connector) throws OwsExceptionReport {
        TimeSeriesDataServiceResponse lastTimeSeriesData =
                connector.getTimeSeriesDataLastPoint(timeSeries.getUniqueId());
        updateDataset(dataset, firstTimeSeriesData, lastTimeSeriesData);
    }

    protected void updateDataset(DatasetEntity entity, TimeSeriesDataServiceResponse firstTimeSeriesData,
            TimeSeriesDataServiceResponse lastTimeSeriesDataLast) {
        if (firstTimeSeriesData != null) {
            insertData(entity, getAquariusHelper().applyChecker(firstTimeSeriesData).getFirstPoint());
        }
        if (lastTimeSeriesDataLast != null) {
            insertData(entity, getAquariusHelper().applyChecker(lastTimeSeriesDataLast).getLastPoint());
        }
    }

    protected DataEntity<?> insertData(DatasetEntity dataset, Point point) {
        return insertData(dataset, createDataEntity(dataset, point, (Long) null, aquariusHelper.isSetApplyRounding()));
    }

    protected DataEntity<?> insertData(DatasetEntity dataset, DataEntity<?> data) {
        return getCRUDRepository().insertData(dataset, data);
    }

    protected void addParameter(DatasetEntity dataset, TimeSeriesDescription timeSeries,
            TimeSeriesDataServiceResponse firstTimeSeriesData) throws OwsExceptionReport {
        if (check(timeSeries.getComputationIdentifier()) && firstTimeSeriesData != null
                && check(firstTimeSeriesData.getInterpolationTypes())) {
            ParameterEntity<?> param = createInterpolationParam(dataset,
                    firstTimeSeriesData.getInterpolationTypes().get(0), timeSeries.getComputationIdentifier());
            if (param != null) {
                dataset.addParameter(param);
            }
        }
        if (check(timeSeries.getComputationPeriodIdentifier())) {
            ParameterEntity<?> param = createAggregationParam(dataset, timeSeries.getComputationPeriodIdentifier());
            if (param != null) {
                dataset.addParameter(param);
            }
        }
    }

    protected boolean check(String value) {
        return value != null && !value.isEmpty();
    }

    protected boolean check(Collection<?> value) {
        return value != null && !value.isEmpty();
    }

    private ParameterEntity<?> createInterpolationParam(DatasetEntity dataset, InterpolationType interpolationType,
            String computationIdentifier) {
        switch (AquariusConstants.InterpolationTypes.getFrom(interpolationType.getType())) {
            case InstantaneousValues:
                return createParameter(dataset, WaterMLConstants.INTERPOLATION_TYPE,
                        WaterMLConstants.InterpolationType.Continuous.name());
            case DiscreteValues:
                return createParameter(dataset, WaterMLConstants.INTERPOLATION_TYPE,
                        WaterMLConstants.InterpolationType.Discontinuous.name());
            case InstantaneousTotals:
                return createParameter(dataset, WaterMLConstants.INTERPOLATION_TYPE,
                        WaterMLConstants.InterpolationType.InstantTotal.name());
            case PrecedingTotals:
                return createParameter(dataset, WaterMLConstants.INTERPOLATION_TYPE,
                        WaterMLConstants.InterpolationType.TotalPrec.name());
            case PrecedingConstant:
                return createParameter(dataset, WaterMLConstants.INTERPOLATION_TYPE,
                        getPreceding(computationIdentifier));
            case SucceedingConstant:
                return createParameter(dataset, WaterMLConstants.INTERPOLATION_TYPE,
                        getSucceeding(computationIdentifier));
            case Default:
            default:
                return null;
        }
    }

    private String getPreceding(String computationIdentifier) {
        switch (AquariusConstants.ComputationIdentifiers.getFrom(computationIdentifier)) {
            case Min:
                return WaterMLConstants.InterpolationType.MinPrec.name();
            case Max:
                return WaterMLConstants.InterpolationType.MaxPrec.name();
            case Mean:
                return WaterMLConstants.InterpolationType.AveragePrec.name();
            case Default:
            default:
                return WaterMLConstants.InterpolationType.ConstPrec.name();
        }
    }

    private String getSucceeding(String computationIdentifier) {
        switch (AquariusConstants.ComputationIdentifiers.getFrom(computationIdentifier)) {
            case Min:
                return WaterMLConstants.InterpolationType.MinSucc.name();
            case Max:
                return WaterMLConstants.InterpolationType.MaxSucc.name();
            case Mean:
                return WaterMLConstants.InterpolationType.AverageSucc.name();
            case Default:
            default:
                return WaterMLConstants.InterpolationType.ConstSucc.name();
        }
    }

    private ParameterEntity<?> createAggregationParam(DatasetEntity dataset, String computationPeriodIdentifier) {
        switch (computationPeriodIdentifier) {
            case "Daily":
                return createParameter(dataset, WaterMLConstants.EN_AGGREGATION_DURATION, "P1D");
            case "Hourly":
                return createParameter(dataset, WaterMLConstants.EN_AGGREGATION_DURATION, "PT1H");
            case "Monthly":
                return createParameter(dataset, WaterMLConstants.EN_AGGREGATION_DURATION, "P1M");
            case "Points":
            case UNKNOWN:
            default:
                return null;
        }
    }

    private ParameterEntity<?> createParameter(DatasetEntity dataset, String name, String value) {
        return createParameter(dataset, WaterMLConstants.NS_WML_20, name, value);
    }

}
