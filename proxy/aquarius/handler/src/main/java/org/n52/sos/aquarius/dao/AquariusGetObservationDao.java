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
package org.n52.sos.aquarius.dao;

import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import javax.inject.Inject;

import org.hibernate.Session;
import org.joda.time.DateTime;
import org.joda.time.Interval;
import org.n52.faroe.annotation.Setting;
import org.n52.iceland.ds.ConnectionProviderException;
import org.n52.iceland.exception.ows.concrete.NotYetSupportedException;
import org.n52.iceland.i18n.I18NSettings;
import org.n52.iceland.request.handler.OperationHandlerRepository;
import org.n52.io.request.IoParameters;
import org.n52.sensorweb.server.db.assembler.value.ValueConnector;
import org.n52.sensorweb.server.db.old.dao.DbQuery;
import org.n52.series.db.beans.DataEntity;
import org.n52.series.db.beans.DatasetEntity;
import org.n52.series.db.beans.QuantityDataEntity;
import org.n52.series.db.beans.UnitEntity;
import org.n52.series.db.old.DataAccessException;
import org.n52.series.db.old.HibernateSessionStore;
import org.n52.series.db.old.dao.DatasetDao;
import org.n52.shetland.ogc.filter.TemporalFilter;
import org.n52.shetland.ogc.gml.time.IndeterminateValue;
import org.n52.shetland.ogc.gml.time.TimeInstant;
import org.n52.shetland.ogc.gml.time.TimePeriod;
import org.n52.shetland.ogc.om.ObservationStream;
import org.n52.shetland.ogc.om.OmObservation;
import org.n52.shetland.ogc.ows.exception.CodedException;
import org.n52.shetland.ogc.ows.exception.NoApplicableCodeException;
import org.n52.shetland.ogc.ows.exception.OwsExceptionReport;
import org.n52.shetland.ogc.sos.ExtendedIndeterminateTime;
import org.n52.shetland.ogc.sos.request.GetObservationRequest;
import org.n52.shetland.ogc.sos.response.GetObservationResponse;
import org.n52.sos.aquarius.ds.AccessorConnector;
import org.n52.sos.aquarius.ds.AquariusHelper;
import org.n52.sos.aquarius.pojo.Location;
import org.n52.sos.aquarius.pojo.TimeSeriesDescription;
import org.n52.sos.aquarius.pojo.data.Point;
import org.n52.sos.aquarius.requests.AbstractGetTimeSeriesData;
import org.n52.sos.ds.ApiQueryHelper;
import org.n52.sos.ds.dao.GetObservationDao;
import org.n52.svalbard.encode.Encoder;
import org.n52.svalbard.encode.EncoderRepository;
import org.n52.svalbard.encode.ObservationEncoder;
import org.n52.svalbard.encode.XmlEncoderKey;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

public class AquariusGetObservationDao extends AbstractAquariusDao
        implements GetObservationDao, ValueConnector, ApiQueryHelper {
    private static final Logger LOGGER = LoggerFactory.getLogger(AquariusGetObservationDao.class);

    private OperationHandlerRepository operationHandlerRepository;

    private AquariusObservationHelper aquariusObservationHelper;

    private HibernateSessionStore sessionStore;

    private EncoderRepository encoderRepository;

    private Locale defaultLanguage;

    private AquariusHelper aquariusHelper;

    @Inject
    public void setConnectionProvider(HibernateSessionStore sessionStore) {
        this.sessionStore = sessionStore;
    }

    @Inject
    public void setOperationHandlerRepository(OperationHandlerRepository operationHandlerRepository) {
        this.operationHandlerRepository = operationHandlerRepository;
    }

    @Inject
    public void setAquariusObservationHelper(AquariusObservationHelper niwaObservationHelper) {
        this.aquariusObservationHelper = niwaObservationHelper;
    }

    @Inject
    public void setEncoderRepository(EncoderRepository encoderRepository) {
        this.encoderRepository = encoderRepository;
    }

    @Inject
    public void setAquariusHelper(AquariusHelper aquariusHelper) {
        this.aquariusHelper = aquariusHelper;
    }

    @Setting(I18NSettings.I18N_DEFAULT_LANGUAGE)
    public void setDefaultLanguage(String defaultLanguage) {
        this.defaultLanguage = new Locale(defaultLanguage);
    }

    @Override
    public GetObservationResponse queryObservationData(GetObservationRequest request, GetObservationResponse response)
            throws OwsExceptionReport {
        try {
            return queryObservationData(request, response, getConnector(null));
        } catch (ConnectionProviderException e) {
            throw new NoApplicableCodeException().causedBy(e);
        }
    }

    @Override
    public GetObservationResponse queryObservationData(GetObservationRequest request, GetObservationResponse response,
            Object connection) throws OwsExceptionReport {
        Session session = null;
        try {
            session = sessionStore.getSession();
            response.setObservationCollection(
                    ObservationStream.of(querySeriesObservation(request, getConnector(connection), session)));
            return response;
        } catch (ConnectionProviderException e) {
            throw new NoApplicableCodeException().causedBy(e);
        } finally {
            sessionStore.returnSession(session);
        }
    }

    /**
     * Query observation if the series mapping is supported.
     *
     * @param request
     *            GetObservation request
     * @param connection Aquarius connection
     * @param session
     *            Hibernate session
     * @return List of internal Observations
     * @throws OwsExceptionReport
     *             If an error occurs.
     */
    protected List<OmObservation> querySeriesObservation(GetObservationRequest request, AccessorConnector connection,
            Session session) throws OwsExceptionReport {
        if (request.isSetResultFilter()) {
            throw new NotYetSupportedException("result filtering");
        }
        final long start = System.currentTimeMillis();
        final List<OmObservation> result = new LinkedList<>();
        Map<String, Location> locations = new HashMap<>();
        Map<String, TimeSeriesDescription> timeSeries = new HashMap<>();
        getLocationsAndDataSets(request, locations, timeSeries, connection, session);
        Map<TimeSeriesDescription, List<Point>> observationMap = new HashMap<>();
        for (Entry<String, TimeSeriesDescription> dataSet : timeSeries.entrySet()) {
            List<Point> observations;
            if (observationMap.containsKey(dataSet.getValue())) {
                observations = observationMap.get(dataSet.getValue());
            } else {
                observations = new LinkedList<>();
            }
            if (request.hasTemporalFilters()) {
                // query with temporal filter
                for (IndeterminateValue temporalFilter : request.getFirstLatestTemporalFilter()) {
                    observations.addAll(queryForTemporalFilter(dataSet.getValue(), temporalFilter, connection));
                }
                for (TemporalFilter temporalFilter : request.getNotFirstLatestTemporalFilter()) {
                    if (temporalFilter != null) {
                        observations.addAll(queryForTemporalFilter(dataSet.getValue(), temporalFilter, connection));
                    }
                }
            } else {
                observations
                        .addAll(connection.getTimeSeriesData(aquariusHelper.getTimeSeriesDataRequest(dataSet.getValue()
                                .getUniqueId())));
            }
            if (!observations.isEmpty()) {
                observationMap.put(dataSet.getValue(), observations);
            }
        }
        LOGGER.debug("Time to query observations needs {} ms!", System.currentTimeMillis() - start);
        result.addAll(aquariusObservationHelper.toSosObservation(observationMap, locations, request,
                operationHandlerRepository, getProcedureDescriptionFormat(request.getResponseFormat()), connection));
        LOGGER.debug("Time to query and process observations needs {} ms!", System.currentTimeMillis() - start);
        return result;
    }

    private void getLocationsAndDataSets(GetObservationRequest request, Map<String, Location> locations,
            Map<String, TimeSeriesDescription> dataSetDOs, AccessorConnector connection, Session session)
            throws OwsExceptionReport {
        List<DatasetEntity> datasets = getDataSets(request, session);
        for (DatasetEntity datasetEntity : datasets) {
            dataSetDOs.putAll(queryDataSet(datasetEntity, connection));
            if (!locations.containsKey(datasetEntity.getFeature()
                    .getIdentifier())) {
                Location location = queryLocation(datasetEntity.getFeature()
                        .getIdentifier(), connection);
                locations.put(location.getIdentifier(), location);
                aquariusHelper.addLocation(location);
            }
        }
    }

    private List<DatasetEntity> getDataSets(GetObservationRequest request, Session session) throws CodedException {
        try {
            return new DatasetDao<>(session).getAllInstances(createDbQuery(request));
        } catch (DataAccessException e) {
            throw new NoApplicableCodeException().causedBy(e)
                    .withMessage("Error while querying datasets for observation!");
        }
    }

    private DbQuery createDbQuery(GetObservationRequest request) {
        Map<String, String> map = Maps.newHashMap();
        if (request.isSetFeatureOfInterest()) {
            map.put(IoParameters.FEATURES, listToString(request.getFeatureIdentifiers()));
        }
        if (request.isSetProcedure()) {
            map.put(IoParameters.PROCEDURES, listToString(request.getProcedures()));
        }
        if (request.isSetObservableProperty()) {
            map.put(IoParameters.PHENOMENA, listToString(request.getObservedProperties()));
        }
        if (request.isSetOffering()) {
            map.put(IoParameters.OFFERINGS, listToString(request.getOfferings()));
        }
        map.put(IoParameters.MATCH_DOMAIN_IDS, "true");
        return new DbQuery(IoParameters.createFromSingleValueMap(map));
    }

    private Map<String, TimeSeriesDescription> queryDataSet(DatasetEntity datasetEntity, AccessorConnector connection)
            throws OwsExceptionReport {
        Map<String, TimeSeriesDescription> dataSetDOs = new HashMap<>();
        if (aquariusHelper.hasDataset(datasetEntity.getIdentifier())) {
            TimeSeriesDescription timeSeries = aquariusHelper.getDataset(datasetEntity.getIdentifier());
            dataSetDOs.put(timeSeries.getIdentifier(), timeSeries);
        } else {
            for (TimeSeriesDescription timeSeries : connection
                    .getTimeSeriesDescriptions(aquariusHelper.getGetTimeSeriesDescriptionListRequest()
                            .setLocationIdentifier(datasetEntity.getFeature()
                                    .getIdentifier()))) {
                if (timeSeries.getIdentifier()
                        .equals(datasetEntity.getOffering()
                                .getIdentifier())) {
                    dataSetDOs.put(timeSeries.getUniqueId(), timeSeries);
                    aquariusHelper.addDataset(timeSeries);
                }
            }
        }
        return dataSetDOs;
    }

    private Location queryLocation(String identifier, AccessorConnector connection) throws OwsExceptionReport {
        if (aquariusHelper.hasLocation(identifier)) {
            return aquariusHelper.getLocation(identifier);
        }
        return connection.getLocation(identifier);
    }

    private Collection<Point> queryForTemporalFilter(TimeSeriesDescription timeSeries,
            IndeterminateValue temporalFilter, AccessorConnector connection) throws OwsExceptionReport {
        List<Point> values = new LinkedList<>();
        if (ExtendedIndeterminateTime.FIRST.equals(temporalFilter)) {
            Point point = connection.getTimeSeriesDataFirstPoint(timeSeries.getUniqueId());
            if (point != null) {
                values.add(point);
            }
        } else if (ExtendedIndeterminateTime.LATEST.equals(temporalFilter)) {
            Point point = connection.getTimeSeriesDataLastPoint(timeSeries.getUniqueId());
            if (point != null) {
                values.add(point);
            }
        }
        return values;
    }

    private List<Point> queryForTemporalFilter(TimeSeriesDescription timeSeries, TemporalFilter temporalFilter,
            AccessorConnector connection) throws OwsExceptionReport {
        switch (temporalFilter.getOperator()) {
            case TM_During:
                if (temporalFilter.getTime() instanceof TimePeriod) {
                    AbstractGetTimeSeriesData request =
                            aquariusHelper.getTimeSeriesDataRequest(timeSeries.getUniqueId());
                    request.setQueryFrom(((TimePeriod) temporalFilter.getTime()).getStart());
                    request.setQueryTo(((TimePeriod) temporalFilter.getTime()).getEnd());
                    return connection.getTimeSeriesData(request);
                }
                break;
            case TM_Equals:
                if (temporalFilter.getTime() instanceof TimeInstant) {
                    AbstractGetTimeSeriesData request =
                            aquariusHelper.getTimeSeriesDataRequest(timeSeries.getUniqueId());
                    request.setQueryFrom(((TimeInstant) temporalFilter.getTime()).getValue());
                    request.setQueryTo(((TimeInstant) temporalFilter.getTime()).getValue());
                    return connection.getTimeSeriesData(request);
                }
                break;
            default:
                break;
        }
        return null;
    }

    private String getProcedureDescriptionFormat(String responseFormat) {
        Encoder<Object, Object> encoder =
                encoderRepository.getEncoder(new XmlEncoderKey(responseFormat, OmObservation.class));
        if (encoder != null && encoder instanceof ObservationEncoder) {
            return ((ObservationEncoder) encoder).getProcedureEncodingNamspace();
        }
        return null;
    }

    @Override
    public Locale getDefaultLanguage() {
        return defaultLanguage;
    }

    @Override
    public List<DataEntity<?>> getObservations(DatasetEntity entitiy, DbQuery parameters) {
        List<DataEntity<?>> measurements = Lists.newArrayList();
        try {
            AccessorConnector connection = getConnector(null);
            Date start = null;
            Date end = null;
            if (parameters.getTimespan() != null) {
                Interval interval = parameters.getTimespan()
                        .toInterval();
                start = interval.getStart()
                        .toDate();
                end = interval.getEnd()
                        .toDate();
                measurements.addAll(getData(start, end, entitiy, connection));
            }
        } catch (OwsExceptionReport | ConnectionProviderException e) {
            LOGGER.error("Error while querying observations", e);
        }
        return measurements;
    }

    @Override
    public UnitEntity getUom(DatasetEntity seriesEntity) {
        return seriesEntity.getUnit();
    }

    @Override
    public Optional<DataEntity<?>> getFirstObservation(DatasetEntity entity) {
        try {
            if (entity.getFirstObservation() != null && entity.getFirstObservation()
                    .getValue() != null) {
                if (entity.getFirstObservation() instanceof QuantityDataEntity) {
                    return Optional.of((QuantityDataEntity) checkTimeStart(entity.getFirstObservation()));
                }
            } else {
                AccessorConnector connection = getConnector(null);
                return Optional.of(convertTimeSeriesDataPoint(
                        connection.getTimeSeriesDataFirstPoint(entity.getDomain()), entity.getId()));
            }
        } catch (OwsExceptionReport | ConnectionProviderException e) {
            LOGGER.error("Error while querying first observation", e);
        }
        return Optional.<DataEntity<?>> empty();
    }

    @Override
    public Optional<DataEntity<?>> getLastObservation(DatasetEntity entity) {
        try {
            if (entity.getLastObservation() != null && entity.getLastObservation()
                    .getValue() != null) {
                if (entity.getLastObservation() instanceof QuantityDataEntity) {
                    return Optional.of((QuantityDataEntity) checkTimeStart(entity.getLastObservation()));
                }
            } else {
                AccessorConnector connection = getConnector(null);
                return Optional.of(convertTimeSeriesDataPoint(
                        connection.getTimeSeriesDataLastPoint(entity.getIdentifier()), entity.getId()));
            }
        } catch (OwsExceptionReport | ConnectionProviderException e) {
            LOGGER.error("Error while querying last observation", e);
        }
        return Optional.<DataEntity<?>> empty();
    }

    private DataEntity<?> checkTimeStart(DataEntity<?> entity) {
        if (entity.getSamplingTimeStart() == null) {
            entity.setSamplingTimeStart(entity.getSamplingTimeEnd());
        }
        return entity;
    }

    private List<QuantityDataEntity> getData(Date start, Date end, DatasetEntity series,
            AccessorConnector accessorConnector) throws OwsExceptionReport {
        AbstractGetTimeSeriesData request = aquariusHelper.getTimeSeriesDataRequest(series.getIdentifier());
        request.setQueryFrom(new DateTime(start));
        request.setQueryTo(new DateTime(end));
        return convertTimeSeriesData(accessorConnector.getTimeSeriesData(request), series.getId());
    }

    private List<QuantityDataEntity> convertTimeSeriesData(List<Point> timeSeriesDataPoints, Long datasetId) {
        List<QuantityDataEntity> measurements = new LinkedList<QuantityDataEntity>();
        for (Point timeSeriesDataPoint : timeSeriesDataPoints) {
            measurements.add(convertTimeSeriesDataPoint(timeSeriesDataPoint, datasetId));
        }
        return measurements;
    }

    private QuantityDataEntity convertTimeSeriesDataPoint(Point point, Long datasetId) {
        QuantityDataEntity mde = createQuantityDataEntity(datasetId);
        mde.setDatasetId(datasetId);
        Date date = new DateTime(point.getTimestamp()).toDate();
        mde.setSamplingTimeStart(date);
        mde.setSamplingTimeEnd(date);
        mde.setValue(point.getValue()
                .getNumericAsBigDecimal());
        return mde;
    }

    private QuantityDataEntity createQuantityDataEntity(Long datasetId) {
        QuantityDataEntity mde = new QuantityDataEntity();
        mde.setDatasetId(datasetId);
        return mde;
    }

    @SuppressWarnings("rawtypes")
    private Set<TimeSeriesDescription> checkForRequestedObservableProperties(DatasetEntity series,
            Set<TimeSeriesDescription> measures) {
        return measures.stream()
                .filter(m -> series.getPhenomenon()
                        .getIdentifier()
                        .contains(m.getParameter()))
                .collect(Collectors.toSet());
    }
}
