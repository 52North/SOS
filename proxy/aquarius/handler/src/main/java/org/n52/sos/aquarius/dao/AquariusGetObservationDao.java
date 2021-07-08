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
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;

import javax.inject.Inject;

import org.hibernate.Hibernate;
import org.hibernate.Session;
import org.joda.time.DateTime;
import org.joda.time.Interval;
import org.n52.iceland.convert.ConverterException;
import org.n52.iceland.ds.ConnectionProviderException;
import org.n52.iceland.exception.ows.concrete.NotYetSupportedException;
import org.n52.io.request.IoParameters;
import org.n52.janmayen.http.HTTPStatus;
import org.n52.sensorweb.server.db.assembler.value.ValueConnector;
import org.n52.sensorweb.server.db.old.dao.DbQuery;
import org.n52.series.db.beans.DataEntity;
import org.n52.series.db.beans.DatasetEntity;
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
import org.n52.sos.aquarius.ds.AquariusTimeHelper;
import org.n52.sos.aquarius.harvest.AquariusEntityBuilder;
import org.n52.sos.aquarius.pojo.TimeSeriesData;
import org.n52.sos.aquarius.pojo.data.Point;
import org.n52.sos.aquarius.requests.AbstractGetTimeSeriesData;
import org.n52.sos.ds.ApiQueryHelper;
import org.n52.sos.ds.dao.GetObservationDao;
import org.n52.sos.ds.observation.DatasetOmObservationCreator;
import org.n52.sos.ds.observation.ObservationHelper;
import org.n52.sos.ds.observation.OmObservationCreatorContext;
import org.n52.sos.proxy.Counter;
import org.n52.svalbard.encode.Encoder;
import org.n52.svalbard.encode.EncoderRepository;
import org.n52.svalbard.encode.ObservationEncoder;
import org.n52.svalbard.encode.XmlEncoderKey;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

public class AquariusGetObservationDao extends AbstractAquariusDao
        implements GetObservationDao, ValueConnector, ApiQueryHelper, AquariusTimeHelper, AquariusEntityBuilder {
    private static final Logger LOGGER = LoggerFactory.getLogger(AquariusGetObservationDao.class);

    private ObservationHelper observationHelper;

    private HibernateSessionStore sessionStore;

    private EncoderRepository encoderRepository;

    private AquariusHelper aquariusHelper;

    private OmObservationCreatorContext observationCreatorContext;

    @Inject
    public void setConnectionProvider(HibernateSessionStore sessionStore) {
        this.sessionStore = sessionStore;
    }

    @Inject
    public void setObservationHelper(ObservationHelper observationHelper) {
        this.observationHelper = observationHelper;
    }

    @Inject
    public void setEncoderRepository(EncoderRepository encoderRepository) {
        this.encoderRepository = encoderRepository;
    }

    @Inject
    public void setAquariusHelper(AquariusHelper aquariusHelper) {
        this.aquariusHelper = aquariusHelper;
    }

    @Inject
    public void setOmObservationCreatorContext(OmObservationCreatorContext observationCreatorContext) {
        this.observationCreatorContext = observationCreatorContext;
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
     * @param connection
     *            Aquarius connection
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
        Locale requestedLocale = getRequestedLocale(request);
        String pdf = getProcedureDescriptionFormat(request.getResponseFormat());

        try {
            List<DatasetEntity> datasets = getDataSets(request, session);
            Counter counter = new Counter();
            for (DatasetEntity dataset : datasets) {
                String identifier = dataset.getIdentifier();
                Collection<TimeSeriesData> data = Lists.newArrayList();
                if (request.hasTemporalFilters()) {
                    // query with temporal filter
                    for (IndeterminateValue temporalFilter : request.getFirstLatestTemporalFilter()) {
                        data.add(queryForTemporalFilter(identifier, temporalFilter, connection));
                    }
                    for (TemporalFilter temporalFilter : request.getNotFirstLatestTemporalFilter()) {
                        if (temporalFilter != null) {
                            data.add(queryForTemporalFilter(identifier, temporalFilter, connection));
                        }
                    }
                } else {
                    data.add(connection.getTimeSeriesData(aquariusHelper.getTimeSeriesDataRequest(identifier)));
                }
                if (!data.isEmpty() || (data.isEmpty() && getProfileHandler().getActiveProfile()
                        .isShowMetadataOfEmptyObservations())) {
                    AquariusStreamingValue streamingValue = new AquariusStreamingValue(observationHelper);
                    List<DataEntity<?>> dataEntities = new LinkedList<>();
                    for (TimeSeriesData timeSeriesData : data) {
                        dataEntities.addAll(convertTimeSeriesData(timeSeriesData, dataset, counter));
                    }
                    streamingValue.setResultValues(dataEntities);
                    ObservationStream observationStream = new DatasetOmObservationCreator(dataset, request,
                            requestedLocale, pdf, observationCreatorContext, session).create();
                    OmObservation observationTemplate = observationStream.next();
                    observationTemplate.setValue(streamingValue);
                    streamingValue.setObservationTemplate(observationTemplate);
                    result.add(observationTemplate);
                }
            }
        } catch (ConverterException ce) {
            throw new NoApplicableCodeException().causedBy(ce)
                    .withMessage("Error while processing observation data!")
                    .setStatus(HTTPStatus.INTERNAL_SERVER_ERROR);
        }
        LOGGER.debug("Time to query and process observations needs {} ms!", System.currentTimeMillis() - start);
        return result;
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

    private TimeSeriesData queryForTemporalFilter(String identifier, IndeterminateValue temporalFilter,
            AccessorConnector connection) throws OwsExceptionReport {
        if (ExtendedIndeterminateTime.FIRST.equals(temporalFilter)) {
            return connection.getTimeSeriesDataFirstPoint(identifier);
        } else if (ExtendedIndeterminateTime.LATEST.equals(temporalFilter)) {
            return connection.getTimeSeriesDataLastPoint(identifier);
        }
        return null;
    }

    private TimeSeriesData queryForTemporalFilter(String identifier, TemporalFilter temporalFilter,
            AccessorConnector connection) throws OwsExceptionReport {
        switch (temporalFilter.getOperator()) {
            case TM_During:
                if (temporalFilter.getTime() instanceof TimePeriod) {
                    AbstractGetTimeSeriesData request = aquariusHelper.getTimeSeriesDataRequest(identifier);
                    request.setQueryFrom(((TimePeriod) temporalFilter.getTime()).getStart());
                    request.setQueryTo(((TimePeriod) temporalFilter.getTime()).getEnd());
                    return connection.getTimeSeriesData(request);
                }
                break;
            case TM_Equals:
                if (temporalFilter.getTime() instanceof TimeInstant) {
                    AbstractGetTimeSeriesData request = aquariusHelper.getTimeSeriesDataRequest(identifier);
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
    public Optional<DataEntity<?>> getFirstObservation(DatasetEntity dataset) {
        try {
            if (dataset.getFirstObservation() != null) {
                return Optional.ofNullable(
                        checkTimeStart(Hibernate.unproxy(dataset.getFirstObservation(), DataEntity.class)));
            } else {
                AccessorConnector connection = getConnector(null);
                TimeSeriesData timeSeriesData = connection.getTimeSeriesDataFirstPoint(dataset.getIdentifier());
                return Optional.of(createDataEntity(dataset, aquariusHelper.applyQualifierChecker(timeSeriesData)
                        .getFirstPoint(), new Counter()));
            }
        } catch (OwsExceptionReport | ConnectionProviderException e) {
            LOGGER.error("Error while querying first observation", e);
        }
        return Optional.<DataEntity<?>> empty();
    }

    @Override
    public Optional<DataEntity<?>> getLastObservation(DatasetEntity dataset) {
        try {
            if (dataset.getLastObservation() != null) {
                return Optional
                        .ofNullable(checkTimeStart(Hibernate.unproxy(dataset.getLastObservation(), DataEntity.class)));
            } else {
                AccessorConnector connection = getConnector(null);
                TimeSeriesData timeSeriesData = connection.getTimeSeriesDataLastPoint(dataset.getIdentifier());
                return Optional.of(createDataEntity(dataset, aquariusHelper.applyQualifierChecker(timeSeriesData)
                        .getLastPoint(), new Counter()));
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

    private List<DataEntity<?>> getData(Date start, Date end, DatasetEntity series,
            AccessorConnector accessorConnector) throws OwsExceptionReport {
        AbstractGetTimeSeriesData request = aquariusHelper.getTimeSeriesDataRequest(series.getIdentifier());
        request.setQueryFrom(new DateTime(start));
        request.setQueryTo(new DateTime(end));
        return convertTimeSeriesData(accessorConnector.getTimeSeriesData(request), series, new Counter());
    }

    private List<DataEntity<?>> convertTimeSeriesData(TimeSeriesData timeSeriesData, DatasetEntity dataset,
            Counter counter) {
        return convertData(aquariusHelper.applyQualifierChecker(timeSeriesData)
                .getPoints(), dataset, counter);
    }

    private List<DataEntity<?>> convertData(List<Point> points, DatasetEntity dataset, Counter counter) {
        List<DataEntity<?>> measurements = new LinkedList<>();
        for (Point point : points) {
            if (point != null) {
                measurements.add(createDataEntity(dataset, point, counter));
            }
        }
        return measurements;
    }

}
