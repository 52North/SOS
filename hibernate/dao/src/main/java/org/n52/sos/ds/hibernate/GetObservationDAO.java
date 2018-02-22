/*
 * Copyright (C) 2012-2018 52°North Initiative for Geospatial Open Source
 * Software GmbH
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
package org.n52.sos.ds.hibernate;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import javax.inject.Inject;

import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.criterion.Criterion;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.n52.faroe.ConfigurationError;
import org.n52.faroe.annotation.Configurable;
import org.n52.faroe.annotation.Setting;
import org.n52.iceland.convert.ConverterException;
import org.n52.iceland.ds.ConnectionProvider;
import org.n52.iceland.exception.ows.concrete.NotYetSupportedException;
import org.n52.iceland.ogc.ows.OwsServiceMetadataRepository;
import org.n52.iceland.service.MiscSettings;
import org.n52.iceland.util.LocalizedProducer;
import org.n52.janmayen.http.HTTPStatus;
import org.n52.shetland.ogc.gml.time.IndeterminateValue;
import org.n52.shetland.ogc.om.ObservationStream;
import org.n52.shetland.ogc.om.OmObservation;
import org.n52.shetland.ogc.om.OmObservationConstellation;
import org.n52.shetland.ogc.ows.OwsServiceProvider;
import org.n52.shetland.ogc.ows.exception.CodedException;
import org.n52.shetland.ogc.ows.exception.NoApplicableCodeException;
import org.n52.shetland.ogc.ows.exception.OwsExceptionReport;
import org.n52.shetland.ogc.sos.Sos1Constants;
import org.n52.shetland.ogc.sos.Sos2Constants;
import org.n52.shetland.ogc.sos.SosConstants;
import org.n52.shetland.ogc.sos.request.GetObservationRequest;
import org.n52.shetland.ogc.sos.response.GetObservationResponse;
import org.n52.shetland.util.CollectionHelper;
import org.n52.sos.ds.AbstractGetObservationHandler;
import org.n52.sos.ds.FeatureQueryHandler;
import org.n52.sos.ds.hibernate.dao.DaoFactory;
import org.n52.sos.ds.hibernate.dao.FeatureOfInterestDAO;
import org.n52.sos.ds.hibernate.dao.observation.AbstractObservationDAO;
import org.n52.sos.ds.hibernate.dao.observation.legacy.LegacyObservationDAO;
import org.n52.sos.ds.hibernate.dao.observation.series.AbstractSeriesDAO;
import org.n52.sos.ds.hibernate.dao.observation.series.AbstractSeriesObservationDAO;
import org.n52.sos.ds.hibernate.entities.EntitiyHelper;
import org.n52.sos.ds.hibernate.entities.ObservationConstellation;
import org.n52.sos.ds.hibernate.entities.feature.AbstractFeatureOfInterest;
import org.n52.sos.ds.hibernate.entities.observation.Observation;
import org.n52.sos.ds.hibernate.entities.observation.series.Series;
import org.n52.sos.ds.hibernate.entities.observation.series.SeriesObservation;
import org.n52.sos.ds.hibernate.util.HibernateGetObservationHelper;
import org.n52.sos.ds.hibernate.util.ObservationTimeExtrema;
import org.n52.sos.ds.hibernate.util.QueryHelper;
import org.n52.sos.ds.hibernate.util.observation.HibernateObservationUtilities;
import org.n52.sos.ds.hibernate.values.HibernateChunkStreamingValue;
import org.n52.sos.ds.hibernate.values.HibernateScrollableStreamingValue;
import org.n52.sos.ds.hibernate.values.HibernateStreamingConfiguration;
import org.n52.sos.ds.hibernate.values.HibernateStreamingSettings;
import org.n52.sos.ds.hibernate.values.HibernateStreamingValue;
import org.n52.sos.ds.hibernate.values.series.HibernateChunkSeriesStreamingValue;
import org.n52.sos.ds.hibernate.values.series.HibernateScrollableSeriesStreamingValue;
import org.n52.sos.ds.hibernate.values.series.HibernateSeriesStreamingValue;
import org.n52.sos.exception.ows.concrete.MissingObservedPropertyParameterException;
import org.n52.svalbard.ConformanceClasses;
import org.n52.svalbard.encode.Encoder;
import org.n52.svalbard.encode.EncoderRepository;
import org.n52.svalbard.encode.ObservationEncoder;
import org.n52.svalbard.encode.XmlEncoderKey;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

/**
 * Implementation of the abstract class AbstractGetObservationHandler
 *
 * @since 4.0.0
 */
@Configurable
public class GetObservationDAO extends AbstractGetObservationHandler {

    private static final Logger LOGGER = LoggerFactory.getLogger(GetObservationDAO.class);

    private HibernateSessionHolder sessionHolder;
    private FeatureQueryHandler featureQueryHandler;
    private OwsServiceMetadataRepository serviceMetadataRepository;
    private EncoderRepository encoderRepository;
    private DaoFactory daoFactory;
    private final EntitiyHelper entitiyHelper = new EntitiyHelper();
    private boolean overallExtrema = true;
    private int maxNumberOfReturnedTimeSeries = -1;
    private boolean streamingDatasource = HibernateStreamingConfiguration.DEFAULT_STREAMING_DATASOURCE;
    private boolean chunkDatasourceStreaming = HibernateStreamingConfiguration.DEFAULT_CHUNK_STREAMING_DATASOURCE;

    public GetObservationDAO() {
        super(SosConstants.SOS);
    }

    @Inject
    public void setDaoFactory(DaoFactory daoFactory) {
        this.daoFactory = daoFactory;
    }

    @Inject
    public void setEncoderRepository(EncoderRepository encoderRepository) {
        this.encoderRepository = encoderRepository;
    }

    @Inject
    public void setServiceMetadataRepository(OwsServiceMetadataRepository repo) {
        this.serviceMetadataRepository = repo;
    }

    @Inject
    public void setConnectionProvider(ConnectionProvider connectionProvider) {
        this.sessionHolder = new HibernateSessionHolder(connectionProvider);
    }

    protected ConnectionProvider getConnectionProvider() {
        return this.sessionHolder.getConnectionProvider();
    }

    @Inject
    public void setFeatureQueryHandler(FeatureQueryHandler featureQueryHandler) {
        this.featureQueryHandler = featureQueryHandler;
    }

    @Override
    public GetObservationResponse getObservation(final GetObservationRequest sosRequest) throws OwsExceptionReport {
        if (sosRequest.getVersion().equals(Sos1Constants.SERVICEVERSION) &&
                 sosRequest.getObservedProperties().isEmpty()) {
            throw new MissingObservedPropertyParameterException();
        }
//        if (sosRequest.isSetResultFilter()) {
//            throw new NotYetSupportedException("result filtering");
//        }
        final GetObservationResponse sosResponse = new GetObservationResponse();
        sosResponse.setService(sosRequest.getService());
        sosResponse.setVersion(sosRequest.getVersion());
        sosResponse.setResponseFormat(sosRequest.getResponseFormat());
        if (sosRequest.isSetResultModel()) {
            sosResponse.setResultModel(sosRequest.getResultModel());
        }
        Session session = null;
        try {
            session = sessionHolder.getSession();
            if (isForceDatasourceStreaming() && CollectionHelper.isEmpty(sosRequest.getFirstLatestTemporalFilter())) {
                // TODO
                if (isSeriesSupported()) {
                    sosResponse.setObservationCollection(querySeriesObservationForStreaming(sosRequest, sosResponse, session));
                } else {
                    sosResponse.setObservationCollection(queryObservationForStreaming(sosRequest, session));
                }
            } else {
                AbstractObservationDAO observationDAO = daoFactory.getObservationDAO();
                // check if series mapping is supported
                if (observationDAO instanceof AbstractSeriesObservationDAO) {
                    sosResponse
                            .setObservationCollection(querySeriesObservation(sosRequest, (AbstractSeriesObservationDAO) observationDAO, session));
                } else {
                    // if
                    // (getConfigurator().getProfileHandler().getActiveProfile().isShowMetadataOfEmptyObservations())
                    // {
                    // // TODO Hydro-Profile adds result observation metadata
                    // // to
                    // // response
                    // sosResponse.setObservationCollection(queryObservationHydro(sosRequest,
                    // session));
                    // } else {
                    sosResponse
                            .setObservationCollection(queryObservation(sosRequest, (LegacyObservationDAO) observationDAO, session));
                    // }
                }

            }
        } catch (HibernateException he) {
            throw new NoApplicableCodeException().causedBy(he).withMessage("Error while querying observation data!")
                    .setStatus(HTTPStatus.INTERNAL_SERVER_ERROR);
        } catch (ConverterException ce) {
            throw new NoApplicableCodeException().causedBy(ce).withMessage("Error while processing observation data!")
                    .setStatus(HTTPStatus.INTERNAL_SERVER_ERROR);
        } finally {
            sessionHolder.returnSession(session);
        }
        return sosResponse;
    }

    @Override
    public Set<String> getConformanceClasses(String service, String version) {
        if (SosConstants.SOS.equals(service) && Sos2Constants.SERVICEVERSION.equals(version)) {
        HashSet<String> set = Sets.newHashSet(ResultFilterConstants.CONFORMANCE_CLASS_RF);
            if (isSpatialFilteringProfile()) {
            set.add(ConformanceClasses.SOS_V2_SPATIAL_FILTERING_PROFILE);
        }
        return set;
    }
    
    @Override
    public boolean isSupported() {
        return true;
    }

    @Override
    public boolean isSupported() {
        return true;
    }

    /**
     * Query observations from database depending on requested filters
     *
     * @param request GetObservation request
     * @param observationDAO
     * @param session Hibernate session
     *
     * @return List of internal Observation objects
     *
     * @throws OwsExceptionReport If an error occurs during requesting
     * @throws ConverterException If an error occurs during converting
     */
    // TODO move this and associated methods to ObservationDAO
    protected ObservationStream queryObservation(final GetObservationRequest request,
                                                   LegacyObservationDAO observationDAO, final Session session)
            throws OwsExceptionReport, ConverterException {

        final long start = System.currentTimeMillis();
        // get valid featureOfInterest identifier
        final Set<String> features = getFeatures(request, session);
        if (features != null && features.isEmpty()) {
            return ObservationStream.empty();
        }
        // temporal filters
        final List<IndeterminateValue> sosIndeterminateTimeFilters = request.getFirstLatestTemporalFilter();
        final Criterion filterCriterion = HibernateGetObservationHelper.getTemporalFilterCriterion(request);

        // final List<OmObservation> result = new LinkedList<OmObservation>();
        Collection<Observation<?>> observations = Lists.newArrayList();
        // query with temporal filter
        if (filterCriterion != null) {
            observations = observationDAO.getObservationsFor(request, features, filterCriterion, session);
        } // query with first/latest value filter
        else if (CollectionHelper.isNotEmpty(sosIndeterminateTimeFilters)) {
            for (IndeterminateValue sosIndeterminateTime : sosIndeterminateTimeFilters) {
                if (isOverallExtrema()) {
                    observations = observationDAO.getObservationsFor(request, features, sosIndeterminateTime, session);
                } else {
                    for (ObservationConstellation oc : HibernateGetObservationHelper.getAndCheckObservationConstellationSize(request, daoFactory, session)) {
                        for (String feature : HibernateGetObservationHelper.getAndCheckFeatureOfInterest(oc, features, daoFactory, session)) {
                            observations.addAll(observationDAO.getObservationsFor(oc, Sets.newHashSet(feature), request, sosIndeterminateTime, session));
                        }
                    }
                }
            }
        } // query without temporal or indeterminate filters
        else {
            observations = observationDAO.getObservationsFor(request, features, session);
        }

        LocalizedProducer<OwsServiceProvider> serviceProviderFactory
                = this.serviceMetadataRepository.getServiceProviderFactory(request.getService());
        String procedureDescriptionFormat = getProcedureDescriptionFormat(request.getResponseFormat());
        Locale requestedLocale = getRequestedLocale(request);

        List<OmObservation> result = HibernateGetObservationHelper.toSosObservation(observations, request, serviceProviderFactory, requestedLocale, procedureDescriptionFormat, daoFactory, session).collect(LinkedList::new);
        Set<OmObservationConstellation> timeSeries = Sets.newHashSet();
        if (isShowMetadataOfEmptyObservations() || getMaxNumberOfReturnedTimeSeries() > 0) {
            result.stream().map(OmObservation::getObservationConstellation).forEach(timeSeries::add);
        }
        if (isShowMetadataOfEmptyObservations()) {
            // create a map of series to check by id, so we don't need to fetch
            // each observation's series from the database
            for (ObservationConstellation oc : HibernateGetObservationHelper.getAndCheckObservationConstellationSize(request, daoFactory, session)) {
                final List<String> featureIds = HibernateGetObservationHelper.getAndCheckFeatureOfInterest(oc, features, daoFactory, session);
                ObservationStream templateObservations = HibernateObservationUtilities.createSosObservationFromObservationConstellation(oc, featureIds, request, serviceProviderFactory, requestedLocale, procedureDescriptionFormat, daoFactory, session);
                while(templateObservations.hasNext()) {
                    OmObservation omObservation = templateObservations.next();
                    if (!timeSeries.contains(omObservation.getObservationConstellation())) {
                        result.add(omObservation);
                        timeSeries.add(omObservation.getObservationConstellation());
                    }
                }
            }
        }

        HibernateGetObservationHelper.checkMaxNumberOfReturnedSeriesSize(timeSeries.size());
        HibernateGetObservationHelper.checkMaxNumberOfReturnedValues(observations.size());
        LOGGER.debug("Time to query observations needed {} ms!", (System.currentTimeMillis() - start));
        return ObservationStream.of(result);
    }


    /**
     * Query observation if the series mapping is supported.
     *
     * @param request GetObservation request
     * @param observationDAO
     * @param session Hibernate session
     *
     * @return List of internal Observations
     *
     * @throws OwsExceptionReport If an error occurs.
     * @throws ConverterException If an error occurs during sensor description creation.
     */
    protected ObservationStream querySeriesObservation(GetObservationRequest request,
                                                         AbstractSeriesObservationDAO observationDAO, Session session)
            throws OwsExceptionReport, ConverterException {
        final long start = System.currentTimeMillis();
        // get valid featureOfInterest identifier
        final Set<String> features = getFeatures(request, session);
        if (features != null && features.isEmpty()) {
            return ObservationStream.empty();
        }
        // temporal filters
        final List<IndeterminateValue> sosIndeterminateTimeFilters = request.getFirstLatestTemporalFilter();
        final Criterion filterCriterion = HibernateGetObservationHelper.getTemporalFilterCriterion(request);

        final List<OmObservation> result = new LinkedList<>();
        Collection<SeriesObservation<?>> seriesObservations = Lists.newArrayList();

        AbstractSeriesDAO seriesDAO = daoFactory.getSeriesDAO();

        // query with temporal filter
        if (filterCriterion != null) {
            seriesObservations = observationDAO.getSeriesObservationsFor(request, features, filterCriterion, session);
        } // query with first/latest value filter
        else if (CollectionHelper.isNotEmpty(sosIndeterminateTimeFilters)) {
            for (IndeterminateValue sosIndeterminateTime : sosIndeterminateTimeFilters) {
                if (isOverallExtrema()) {
                    seriesObservations = observationDAO.getSeriesObservationsFor(request, features,
                                                                                 sosIndeterminateTime, session);
                } else {
                    for (Series series : seriesDAO.getSeries(request, features, session)) {
                        seriesObservations.addAll(observationDAO.getSeriesObservationsFor(series, request,
                                                                                          sosIndeterminateTime, session));
                        
                    }
                    seriesObservations = checkObservationsForDuplicity(seriesObservations, request);
                }
            }
        } // query without temporal or indeterminate filters
        else {
            seriesObservations = checkObservationsForDuplicity(observationDAO.getSeriesObservationsFor(request, features, session), request);
        }

        // if active profile demands observation metadata for series without
        // matching observations,
        // a "result" observation without values is created.
        // TODO does this apply for indeterminate time first/latest filters?
        // Yes.
        int metadataObservationsCount = 0;
        LocalizedProducer<OwsServiceProvider> serviceProviderFactory
                = this.serviceMetadataRepository.getServiceProviderFactory(request
                        .getService());
        Locale requestedLocale = getRequestedLocale(request);
        String procedureDescriptionFormat
                = getProcedureDescriptionFormat(request
                        .getResponseFormat());
        if (isShowMetadataOfEmptyObservations()) {
            // create a map of series to check by id, so we don't need to fetch
            // each observation's series from the database
            Map<Long, Series> seriesToCheckMap = Maps.newHashMap();
            for (Series series : seriesDAO.getSeries(request, features, session)) {
                seriesToCheckMap.put(series.getSeriesId(), series);
            }

            // check observations and remove any series found from the map
            for (SeriesObservation<?> seriesObs : seriesObservations) {
                long seriesId = seriesObs.getSeries().getSeriesId();
                if (seriesToCheckMap.containsKey(seriesId)) {
                    seriesToCheckMap.remove(seriesId);
                }
            }
            // now we're left with the series without matching observations in
            // the check map,
            // add "result" observations for them
            metadataObservationsCount = seriesToCheckMap.size();
            for (Series series : seriesToCheckMap.values()) {
                HibernateObservationUtilities.createSosObservationFromSeries(series, request, serviceProviderFactory, requestedLocale, procedureDescriptionFormat, daoFactory, session)
                        .forEachRemaining(result::add);
            }
        }
        HibernateGetObservationHelper.checkMaxNumberOfReturnedTimeSeries(seriesObservations, metadataObservationsCount);
        HibernateGetObservationHelper.checkMaxNumberOfReturnedValues(seriesObservations.size());

        LOGGER.debug("Time to query observations needs {} ms!", (System.currentTimeMillis() - start));
        HibernateGetObservationHelper.toSosObservation(new ArrayList<>(seriesObservations), request, serviceProviderFactory, requestedLocale, procedureDescriptionFormat, daoFactory, session)
                .forEachRemaining(result::add);
        return ObservationStream.of(result);
    }

    /**
     * Query the observations for streaming datasource
     *
     * @param request The GetObservation request
     * @param session Hibernate Session
     *
     * @return List of internal observations
     *
     * @throws OwsExceptionReport If an error occurs.
     * @throws ConverterException If an error occurs during sensor description creation.
     */
    protected ObservationStream queryObservationForStreaming(GetObservationRequest request, final Session session)
            throws OwsExceptionReport, ConverterException {
        final long start = System.currentTimeMillis();
        final List<OmObservation> result = new LinkedList<>();
        // get valid featureOfInterest identifier
        final Set<String> features = getFeatures(request, session);
        if (features != null && features.isEmpty()) {
            return ObservationStream.empty();
        }
        Criterion temporalFilterCriterion = HibernateGetObservationHelper.getTemporalFilterCriterion(request);
        List<ObservationConstellation> observationConstellations = HibernateGetObservationHelper.getAndCheckObservationConstellationSize(request, daoFactory, session);
        HibernateGetObservationHelper.checkMaxNumberOfReturnedSeriesSize(observationConstellations.size());
        int maxNumberOfValuesPerSeries = HibernateGetObservationHelper.getMaxNumberOfValuesPerSeries(observationConstellations.size());
        LocalizedProducer<OwsServiceProvider> serviceProvider = this.serviceMetadataRepository.getServiceProviderFactory(request.getService());
        Locale requestedLocale = getRequestedLocale(request);
        String pdf = getProcedureDescriptionFormat(request.getResponseFormat());
        for (ObservationConstellation oc : observationConstellations) {
            List<String> featureIds = HibernateGetObservationHelper.getAndCheckFeatureOfInterest(oc, features, daoFactory, session);



            HibernateObservationUtilities.createSosObservationFromObservationConstellation(oc, featureIds, request, serviceProvider, requestedLocale, pdf, daoFactory, session)
                    .modify(observationTemplate -> {
                        FeatureOfInterest featureOfInterest = new FeatureOfInterestDAO(daoFactory)
                                .get(observationTemplate.getObservationConstellation()
                                        .getFeatureOfInterest().getIdentifier(), session);
                        HibernateStreamingValue streamingValue
                                = getStreamingValue(request, oc.getProcedure().getProcedureId(), oc
                                                    .getObservableProperty()
                                                    .getObservablePropertyId(), featureOfInterest
                                                            .getFeatureOfInterestId());
                        streamingValue.setResponseFormat(request.getResponseFormat());
                        streamingValue.setTemporalFilterCriterion(temporalFilterCriterion);
                        streamingValue.setObservationTemplate(observationTemplate);
                        streamingValue.setMaxNumberOfValues(maxNumberOfValuesPerSeries);
                        observationTemplate.setValue(streamingValue);
                    }).forEachRemaining(result::add);
        }
        LOGGER.debug("Time to query observations needs {} ms!", (System.currentTimeMillis() - start));
        return ObservationStream.of(result);
    }

    /**
     * Query the series observations for streaming datasource
     *
     * @param request The GetObservation request
     * @param session Hibernate Session
     *
     * @return List of internal observations
     *
     * @throws OwsExceptionReport If an error occurs.
     * @throws ConverterException If an error occurs during sensor description creation.
     */
    protected ObservationStream querySeriesObservationForStreaming(GetObservationRequest request,
                                                                     final Session session) throws OwsExceptionReport,
                                                                                                   ConverterException {
        final long start = System.currentTimeMillis();
        final List<OmObservation> result = new LinkedList<>();
        // get valid featureOfInterest identifier
        final Set<String> features = getFeatures(request, session);
        if (features != null && features.isEmpty()) {
            return ObservationStream.empty();
        }
        Criterion temporalFilterCriterion = HibernateGetObservationHelper.getTemporalFilterCriterion(request);
        List<Series> serieses = daoFactory.getSeriesDAO().getSeries(request, features, session);
        HibernateGetObservationHelper.checkMaxNumberOfReturnedSeriesSize(serieses.size());
        int maxNumberOfValuesPerSeries = HibernateGetObservationHelper.getMaxNumberOfValuesPerSeries(serieses.size());
        checkSeriesOfferings(serieses, request);
        Collection<Series> duplicated = checkAndGetDuplicatedtSeries(serieses, request);
        for (Series series : serieses) {
            Locale locale = getRequestedLocale(request);
            String service = request.getService();
            LocalizedProducer<OwsServiceProvider> serviceProviderFactory
                    = this.serviceMetadataRepository.getServiceProviderFactory(service);
            ObservationStream createSosObservationFromSeries = HibernateObservationUtilities
                    .createSosObservationFromSeries(series, request, serviceProviderFactory, locale, getProcedureDescriptionFormat(request.getResponseFormat()), daoFactory, session);
            OmObservation observationTemplate = createSosObservationFromSeries.next();
            HibernateSeriesStreamingValue streamingValue = getSeriesStreamingValue(request, series.getSeriesId(), duplicated.contains(series));
            streamingValue.setResponseFormat(request.getResponseFormat());
            streamingValue.setTemporalFilterCriterion(temporalFilterCriterion);
            streamingValue.setObservationTemplate(observationTemplate);
            streamingValue.setMaxNumberOfValues(maxNumberOfValuesPerSeries);
            observationTemplate.setValue(streamingValue);
            result.add(observationTemplate);
        }
        // query global response values
        
        ObservationTimeExtrema timeExtrema = DaoFactory.getInstance().getValueTimeDAO().getTimeExtremaForSeries(serieses, temporalFilterCriterion, session);
        if (timeExtrema.isSetPhenomenonTimes()) {
            response.setGlobalValues(response.new GlobalGetObservationValues().setPhenomenonTime(timeExtrema.getPhenomenonTime()));
        }
        LOGGER.debug("Time to query observations needs {} ms!", (System.currentTimeMillis() - start));
        return ObservationStream.of(result);
    }

    private Set<String> getFeatures(GetObservationRequest request, Session session) throws OwsExceptionReport {
        return QueryHelper.getFeatures(this.featureQueryHandler, request, session);
    }

    /**
     * Get the series streaming observation value for the observations
     *
     * @param request GetObservation request
     * @param seriesId Series id
     *
     * @param duplicated 
     * @return Streaming observation value
     *
     * @throws CodedException
     */
    private HibernateSeriesStreamingValue getSeriesStreamingValue(GetObservationRequest request, long seriesId) throws
            OwsExceptionReport {
        ConnectionProvider connectionProvider = getConnectionProvider();
        if (isChunkDatasourceStreaming()) {
            return new HibernateChunkSeriesStreamingValue(connectionProvider, daoFactory, request, seriesId, duplicated);
        } else {
            return new HibernateScrollableSeriesStreamingValue(connectionProvider, daoFactory, request, seriesId, duplicated);
        }
    }

    /**
     * Get the streaming observation value for the observations
     *
     * @param request GetObservation request
     * @param procedure Procedure id
     * @param observableProperty ObservableProperty id
     * @param feature FeatureOfInterest id
     *
     * @return Streaming observation value
     */
    private HibernateStreamingValue getStreamingValue(GetObservationRequest request, long procedure,
                                                      long observableProperty, long feature) {
        ConnectionProvider connectionProvider = getConnectionProvider();
        if (isChunkDatasourceStreaming()) {
            return new HibernateChunkStreamingValue(connectionProvider, daoFactory, request, procedure, observableProperty, feature);
        } else {
            return new HibernateScrollableStreamingValue(connectionProvider, daoFactory, request, procedure, observableProperty, feature);
        }
    }

    private boolean isSpatialFilteringProfile() {
        return false;
    }

    private String getProcedureDescriptionFormat(String responseFormat) {
        Encoder<Object, Object> encoder = encoderRepository.getEncoder(new XmlEncoderKey(responseFormat, OmObservation.class));
        if (encoder != null && encoder instanceof ObservationEncoder) {
            return ((ObservationEncoder)encoder).getProcedureEncodingNamspace();
        }
        return null;
    }

    private boolean isShowMetadataOfEmptyObservations() throws ConfigurationError {
        return getActiveProfile().isShowMetadataOfEmptyObservations();
    }

    private boolean isSeriesSupported() {
        return entitiyHelper.isSeriesSupported();
    }

    @Setting(MiscSettings.RETURN_OVERALL_EXTREMA_FOR_FIRST_LATEST)
    public void setOverallExtrema(boolean overallExtrema) {
        this.overallExtrema = overallExtrema;
    }

    protected boolean isOverallExtrema() {
        return overallExtrema;
    }

    @Setting(MiscSettings.HYDRO_MAX_NUMBER_OF_RETURNED_TIME_SERIES)
    public void setMaxNumberOfReturnedTimeSeries(Integer value) {
        this.maxNumberOfReturnedTimeSeries = value;
    }

    protected int getMaxNumberOfReturnedTimeSeries() {
        return maxNumberOfReturnedTimeSeries;
    }

    /**
     * Set the indicator to force streaming datasource
     *
     * @param streamingDatasource Value to set
     */
    @Setting(HibernateStreamingSettings.FORCE_DATASOURCE_STREAMING)
    public void setForceDatasourceStreaming(boolean streamingDatasource) {
        this.streamingDatasource = streamingDatasource;
    }

    /**
     * Check if streaming values should be used
     *
     * @return <code>true</code>, if datasource streaming is activated
     */
    protected boolean isForceDatasourceStreaming() {
        return streamingDatasource;
    }

    /**
     * Set the indicator to use chunk or scrollable streaming
     *
     * @param chunkDatasourceStreaming Value to set
     */
    @Setting(HibernateStreamingSettings.DATASOURCE_STREAMING_APPROACH)
    public void setChunkDatasourceStreaming(boolean chunkDatasourceStreaming) {
        this.chunkDatasourceStreaming = chunkDatasourceStreaming;
    }

    /**
     * Check for streaming mode to use
     *
     * @return <code>true</code>, if chunk streaming should be used <code>false</code>, if scrollable should be used
     */
    protected boolean isChunkDatasourceStreaming() {
        return chunkDatasourceStreaming;
    }

    private void checkSeriesOfferings(List<Series> serieses, GetObservationRequest request) {
        boolean allSeriesWithOfferings = true;
        for (Series series : serieses) {
            allSeriesWithOfferings = !series.isSetOffering() ?  false : allSeriesWithOfferings;
        }
        if (allSeriesWithOfferings) {
            request.setOfferings(Lists.<String>newArrayList());
        }
    }

    private Collection<Series> checkAndGetDuplicatedtSeries(List<Series> serieses, GetObservationRequest request) {
        if (!request.isCheckForDuplicity()) {
            return Sets.newHashSet();
        }
        Set<Series> single = Sets.newHashSet();
        Set<Series> duplicated = Sets.newHashSet();
        for (Series series : serieses) {
            if (!single.isEmpty()) {
                if (isDuplicatedSeries(series, single)) {
                    duplicated.add(series);
                }
            } else {
                single.add(series);
            }
        }
        return duplicated;
    }
    
    private boolean isDuplicatedSeries(Series series, Set<Series> serieses) {
        for (Series s : serieses) {
            if (series.hasSameObservationIdentifier(s)) {
                return true;
            }
        }
        return false;
    }
    
    private Collection<SeriesObservation<?>> checkObservationsForDuplicity(Collection<SeriesObservation<?>> seriesObservations, GetObservationRequest request) {
        if (!request.isCheckForDuplicity()) {
            return seriesObservations;
        }
        Collection<SeriesObservation<?>> checked = Lists.newArrayList();
        Set<Series> serieses = Sets.newHashSet();
        Set<Series> duplicated = Sets.newHashSet();
        for (SeriesObservation<?> seriesObservation : seriesObservations) {
            if (serieses.isEmpty()) {
                serieses.add(seriesObservation.getSeries());
            } else {
                if (!serieses.contains(seriesObservation.getSeries()) && !duplicated.contains(seriesObservation)
                        && isDuplicatedSeries(seriesObservation.getSeries(), serieses)) {
                    duplicated.add(seriesObservation.getSeries());
                } else {
                    serieses.add(seriesObservation.getSeries());
                }
            }

            if (serieses.contains(seriesObservation.getSeries()) || (duplicated.contains(seriesObservation.getSeries())
                    && seriesObservation.getOfferings().size() == 1)) {
                checked.add(seriesObservation);
            }
        }
        return checked;
    }

}
