/**
 * Copyright (C) 2012-2014 52°North Initiative for Geospatial Open Source
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
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.xmlbeans.XmlObject;
import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Disjunction;
import org.hibernate.criterion.HibernateCriterionHelper;
import org.hibernate.criterion.Junction;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.n52.sos.coding.CodingRepository;
import org.n52.sos.convert.ConverterException;
import org.n52.sos.ds.AbstractGetObservationDAO;
import org.n52.sos.ds.HibernateDatasourceConstants;
import org.n52.sos.ds.hibernate.dao.AbstractSpatialFilteringProfileDAO;
import org.n52.sos.ds.hibernate.dao.DaoFactory;
import org.n52.sos.ds.hibernate.dao.FeatureOfInterestDAO;
import org.n52.sos.ds.hibernate.dao.HibernateSqlQueryConstants;
import org.n52.sos.ds.hibernate.dao.ObservationConstellationDAO;
import org.n52.sos.ds.hibernate.dao.ObservationDAO;
import org.n52.sos.ds.hibernate.dao.SpatialFilteringProfileDAO;
import org.n52.sos.ds.hibernate.dao.series.SeriesDAO;
import org.n52.sos.ds.hibernate.dao.series.SeriesObservationDAO;
import org.n52.sos.ds.hibernate.entities.AbstractObservation;
import org.n52.sos.ds.hibernate.entities.AbstractSpatialFilteringProfile;
import org.n52.sos.ds.hibernate.entities.FeatureOfInterest;
import org.n52.sos.ds.hibernate.entities.ObservableProperty;
import org.n52.sos.ds.hibernate.entities.Observation;
import org.n52.sos.ds.hibernate.entities.ObservationConstellation;
import org.n52.sos.ds.hibernate.entities.Offering;
import org.n52.sos.ds.hibernate.entities.Procedure;
import org.n52.sos.ds.hibernate.entities.SpatialFilteringProfile;
import org.n52.sos.ds.hibernate.entities.series.Series;
import org.n52.sos.ds.hibernate.entities.series.SeriesObservation;
import org.n52.sos.ds.hibernate.util.HibernateGetObservationHelper;
import org.n52.sos.ds.hibernate.util.HibernateHelper;
import org.n52.sos.ds.hibernate.util.QueryHelper;
import org.n52.sos.ds.hibernate.util.TemporalRestrictions;
import org.n52.sos.ds.hibernate.util.observation.HibernateObservationUtilities;
import org.n52.sos.ds.hibernate.values.HibernateChunkStreamingValue;
import org.n52.sos.ds.hibernate.values.HibernateScrollableStreamingValue;
import org.n52.sos.ds.hibernate.values.HibernateStreamingConfiguration;
import org.n52.sos.ds.hibernate.values.HibernateStreamingValue;
import org.n52.sos.ds.hibernate.values.series.HibernateChunkSeriesStreamingValue;
import org.n52.sos.ds.hibernate.values.series.HibernateScrollableSeriesStreamingValue;
import org.n52.sos.ds.hibernate.values.series.HibernateSeriesStreamingValue;
import org.n52.sos.encode.Encoder;
import org.n52.sos.encode.ObservationEncoder;
import org.n52.sos.encode.XmlEncoderKey;
import org.n52.sos.exception.CodedException;
import org.n52.sos.exception.ows.NoApplicableCodeException;
import org.n52.sos.exception.ows.OptionNotSupportedException;
import org.n52.sos.exception.ows.concrete.MissingObservedPropertyParameterException;
import org.n52.sos.exception.ows.concrete.NotYetSupportedException;
import org.n52.sos.exception.sos.ResponseExceedsSizeLimitException;
import org.n52.sos.ogc.filter.BinaryLogicFilter;
import org.n52.sos.ogc.filter.ComparisonFilter;
import org.n52.sos.ogc.filter.Filter;
import org.n52.sos.ogc.filter.FilterConstants;
import org.n52.sos.ogc.filter.FilterConstants.ComparisonOperator;
import org.n52.sos.ogc.filter.TemporalFilter;
import org.n52.sos.ogc.gml.GmlConstants;
import org.n52.sos.ogc.om.OmConstants;
import org.n52.sos.ogc.om.OmObservation;
import org.n52.sos.ogc.om.OmObservationConstellation;
import org.n52.sos.ogc.ows.OwsExceptionReport;
import org.n52.sos.ogc.sos.ConformanceClasses;
import org.n52.sos.ogc.sos.Sos1Constants;
import org.n52.sos.ogc.sos.Sos2Constants;
import org.n52.sos.ogc.sos.SosConstants;
import org.n52.sos.ogc.sos.SosConstants.SosIndeterminateTime;
import org.n52.sos.request.GetObservationRequest;
import org.n52.sos.response.GetObservationResponse;
import org.n52.sos.service.ServiceConfiguration;
import org.n52.sos.util.CollectionHelper;
import org.n52.sos.util.Constants;
import org.n52.sos.util.http.HTTPStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

/**
 * Implementation of the abstract class AbstractGetObservationDAO
 * 
 * @since 4.0.0
 */
public class GetObservationDAO extends AbstractGetObservationDAO {

    private static final Logger LOGGER = LoggerFactory.getLogger(GetObservationDAO.class);

    private final HibernateSessionHolder sessionHolder = new HibernateSessionHolder();

    /**
     * constructor
     */
    public GetObservationDAO() {
        super(SosConstants.SOS);
    }
    
    @Override
    public String getDatasourceDaoIdentifier() {
        return HibernateDatasourceConstants.ORM_DATASOURCE_DAO_IDENTIFIER;
    }
    @Override
    public GetObservationResponse getObservation(final GetObservationRequest sosRequest) throws OwsExceptionReport {
        if (sosRequest.getVersion().equals(Sos1Constants.SERVICEVERSION)
                && sosRequest.getObservedProperties().isEmpty()) {
            throw new MissingObservedPropertyParameterException();
        }
        if (sosRequest.isSetResultFilter()) {
            throw new NotYetSupportedException("result filtering");
        }
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
            if (HibernateStreamingConfiguration.getInstance().isForceDatasourceStreaming()
                    && CollectionHelper.isEmpty(sosRequest.getFirstLatestTemporalFilter())) {
                // TODO
                if (HibernateHelper.isEntitySupported(Series.class, session)) {
                    sosResponse.setObservationCollection(querySeriesObservationForStreaming(sosRequest, session));
                } else {
                    sosResponse.setObservationCollection(queryObservationForStreaming(sosRequest, session));
                }
            } else {
                // check if series mapping is supported
                if (HibernateHelper.isEntitySupported(Series.class, session)) {
                    sosResponse.setObservationCollection(querySeriesObservation(sosRequest, session));
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
                    sosResponse.setObservationCollection(queryObservation(sosRequest, session));
                    // }
                }

            }
        } catch (final HibernateException he) {
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
    public Set<String> getConformanceClasses() {
        try {
            Session session = sessionHolder.getSession();
            if (ServiceConfiguration.getInstance().isStrictSpatialFilteringProfile()) {
                return Sets.newHashSet(ConformanceClasses.SOS_V2_SPATIAL_FILTERING_PROFILE);
            }
            sessionHolder.returnSession(session);
        } catch (OwsExceptionReport owse) {
            LOGGER.error("Error while getting Spatial Filtering Profile conformance class!", owse);
        }
        return super.getConformanceClasses();
    }

    /**
     * Query observations from database depending on requested filters
     * 
     * @param request
     *            GetObservation request
     * @param session
     *            Hibernate session
     * @return List of internal Observation objects
     * @throws OwsExceptionReport
     *             If an error occurs during requesting
     * @throws ConverterException
     *             If an error occurs during converting
     */
    // TODO move this and associated methods to ObservationDAO
    protected List<OmObservation> queryObservation(final GetObservationRequest request, final Session session)
            throws OwsExceptionReport, ConverterException {
        if (request.isSetResultFilter()) {
            throw new NotYetSupportedException("result filtering");
        }

        final long start = System.currentTimeMillis();
        // get valid featureOfInterest identifier
        final Set<String> features = QueryHelper.getFeatures(request, session);
        if (features != null && features.isEmpty()) {
            return new ArrayList<OmObservation>();
        }
        // temporal filters
        final List<SosIndeterminateTime> sosIndeterminateTimeFilters = request.getFirstLatestTemporalFilter();
        final Criterion filterCriterion = HibernateGetObservationHelper.getTemporalFilterCriterion(request);

        // final List<OmObservation> result = new LinkedList<OmObservation>();
        Collection<AbstractObservation> observations = Lists.newArrayList();
        // query with temporal filter
        if (filterCriterion != null) {
            observations = new ObservationDAO().getObservationsFor(request, features, filterCriterion, session);
        }
        // query with first/latest value filter
        else if (CollectionHelper.isNotEmpty(sosIndeterminateTimeFilters)) {
            for (SosIndeterminateTime sosIndeterminateTime : sosIndeterminateTimeFilters) {
                if (ServiceConfiguration.getInstance().isOverallExtrema()) {
                    observations =
                            new ObservationDAO().getObservationsFor(request, features, sosIndeterminateTime, session);
                } else {
                    for (ObservationConstellation oc : HibernateGetObservationHelper
                            .getAndCheckObservationConstellationSize(request, session)) {
                        for (String feature : HibernateGetObservationHelper.getAndCheckFeatureOfInterest(oc, features,
                                session)) {
                            observations.addAll(new ObservationDAO().getObservationsFor(oc, Sets.newHashSet(feature),
                                    request, sosIndeterminateTime, session));
                        }
                    }
                }
            }
        }
        // query without temporal or indeterminate filters
        else {
            observations = new ObservationDAO().getObservationsFor(request, features, session);
        }

        int metadataObservationsCount = 0;

        List<OmObservation> result =
                HibernateGetObservationHelper.toSosObservation(observations, request.getVersion(),
                        request.getResultModel(), session);
        Set<OmObservationConstellation> timeSeries = Sets.newHashSet();
        if (getConfigurator().getProfileHandler().getActiveProfile().isShowMetadataOfEmptyObservations()
                || ServiceConfiguration.getInstance().getMaxNumberOfReturnedTimeSeries() > 0) {
            for (OmObservation omObservation : result) {
                timeSeries.add(omObservation.getObservationConstellation());
            }
        }
        if (getConfigurator().getProfileHandler().getActiveProfile().isShowMetadataOfEmptyObservations()) {
            // create a map of series to check by id, so we don't need to fetch
            // each observation's series from the database
            for (ObservationConstellation oc : HibernateGetObservationHelper.getAndCheckObservationConstellationSize(
                    request, session)) {
                final List<String> featureIds =
                        HibernateGetObservationHelper.getAndCheckFeatureOfInterest(oc, features, session);
                for (OmObservation omObservation : HibernateObservationUtilities
                        .createSosObservationFromObservationConstellation(oc, featureIds, request.getVersion(),
                                session)) {
                    if (!timeSeries.contains(omObservation.getObservationConstellation())) {
                        result.add(omObservation);
                        timeSeries.add(omObservation.getObservationConstellation());
                    }
                }
            }
        }

        HibernateGetObservationHelper
                .checkMaxNumberOfReturnedSeriesSize(timeSeries.size() + metadataObservationsCount);
        HibernateGetObservationHelper.checkMaxNumberOfReturnedValues(observations.size());
        LOGGER.debug("Time to query observations needed {} ms!", (System.currentTimeMillis() - start));
        return result;
    }

    /**
     * Query observation if the series mapping is supported.
     * 
     * @param request
     *            GetObservation request
     * @param session
     *            Hibernate session
     * @return List of internal Observations
     * @throws OwsExceptionReport
     *             If an error occurs.
     * @throws ConverterException
     *             If an error occurs during sensor description creation.
     */
    protected List<OmObservation> querySeriesObservation(GetObservationRequest request, Session session)
            throws OwsExceptionReport, ConverterException {
        if (request.isSetResultFilter()) {
            throw new NotYetSupportedException("result filtering");
        }

        final long start = System.currentTimeMillis();
        // get valid featureOfInterest identifier
        final Set<String> features = QueryHelper.getFeatures(request, session);
        if (features != null && features.isEmpty()) {
            return new ArrayList<OmObservation>();
        }
        // temporal filters
        final List<SosIndeterminateTime> sosIndeterminateTimeFilters = request.getFirstLatestTemporalFilter();
        final Criterion filterCriterion = HibernateGetObservationHelper.getTemporalFilterCriterion(request);

        final List<OmObservation> result = new LinkedList<OmObservation>();
        Collection<SeriesObservation> seriesObservations = Lists.newArrayList();

        // query with temporal filter
        if (filterCriterion != null) {
            seriesObservations =
                    new SeriesObservationDAO().getSeriesObservationsFor(request, features, filterCriterion, session);
        }
        // query with first/latest value filter
        else if (CollectionHelper.isNotEmpty(sosIndeterminateTimeFilters)) {
            for (SosIndeterminateTime sosIndeterminateTime : sosIndeterminateTimeFilters) {
                if (ServiceConfiguration.getInstance().isOverallExtrema()) {
                    seriesObservations =
                            new SeriesObservationDAO().getSeriesObservationsFor(request, features,
                                    sosIndeterminateTime, session);
                } else {
                    for (Series series : new SeriesDAO().getSeries(request, features, session)) {
                        seriesObservations.addAll(new SeriesObservationDAO().getSeriesObservationsFor(series, request,
                                sosIndeterminateTime, session));
                    }
                }
            }
        }
        // query without temporal or indeterminate filters
        else {
            seriesObservations = new SeriesObservationDAO().getSeriesObservationsFor(request, features, session);
        }

        // if active profile demands observation metadata for series without
        // matching observations,
        // a "result" observation without values is created.
        // TODO does this apply for indeterminate time first/latest filters?
        // Yes.
        int metadataObservationsCount = 0;
        if (getConfigurator().getProfileHandler().getActiveProfile().isShowMetadataOfEmptyObservations()) {
            // create a map of series to check by id, so we don't need to fetch
            // each observation's series from the database
            Map<Long, Series> seriesToCheckMap = Maps.newHashMap();
            for (Series series : new SeriesDAO().getSeries(request, features, session)) {
                seriesToCheckMap.put(series.getSeriesId(), series);
            }

            // check observations and remove any series found from the map
            for (SeriesObservation seriesObs : seriesObservations) {
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
                result.addAll(HibernateObservationUtilities.createSosObservationFromSeries(series,
                        request.getVersion(), session));
            }
        }
        HibernateGetObservationHelper
                .checkMaxNumberOfReturnedTimeSeries(seriesObservations, metadataObservationsCount);
        HibernateGetObservationHelper.checkMaxNumberOfReturnedValues(seriesObservations.size());

        LOGGER.debug("Time to query observations needs {} ms!", (System.currentTimeMillis() - start));
        Collection<AbstractObservation> abstractObservations = Lists.newArrayList();
        abstractObservations.addAll(seriesObservations);
        result.addAll(HibernateGetObservationHelper.toSosObservation(abstractObservations, request.getVersion(),
                request.getResultModel(), session));
        return result;
    }

    /**
     * Query the observations for streaming datasource
     * 
     * @param request
     *            The GetObservation request
     * @param session
     *            Hibernate Session
     * @return List of internal observations
     * @throws OwsExceptionReport
     *             If an error occurs.
     * @throws ConverterException
     *             If an error occurs during sensor description creation.
     */
    protected List<OmObservation> queryObservationForStreaming(GetObservationRequest request, final Session session)
            throws OwsExceptionReport, ConverterException {
        final long start = System.currentTimeMillis();
        final List<OmObservation> result = new LinkedList<OmObservation>();
        // get valid featureOfInterest identifier
        final Set<String> features = QueryHelper.getFeatures(request, session);
        if (features != null && features.isEmpty()) {
            return result;
        }
        Criterion temporalFilterCriterion = HibernateGetObservationHelper.getTemporalFilterCriterion(request);
        for (ObservationConstellation oc : HibernateGetObservationHelper.getAndCheckObservationConstellationSize(
                request, session)) {
            final List<String> featureIds =
                    HibernateGetObservationHelper.getAndCheckFeatureOfInterest(oc, features, session);
            for (OmObservation observationTemplate : HibernateObservationUtilities
                    .createSosObservationFromObservationConstellation(oc, featureIds, request.getVersion(), session)) {
                FeatureOfInterest featureOfInterest =
                        new FeatureOfInterestDAO().getFeatureOfInterest(observationTemplate
                                .getObservationConstellation().getFeatureOfInterest().getIdentifier().getValue(),
                                session);
                HibernateStreamingValue streamingValue =
                        getStreamingValue(request, oc.getProcedure().getProcedureId(), oc.getObservableProperty()
                                .getObservablePropertyId(), featureOfInterest.getFeatureOfInterestId());
                streamingValue.setTemporalFilterCriterion(temporalFilterCriterion);
                streamingValue.setObservationTemplate(observationTemplate);
                observationTemplate.setValue(streamingValue);
                result.add(observationTemplate);
            }
        }
        LOGGER.debug("Time to query observations needs {} ms!", (System.currentTimeMillis() - start));
        return result;
    }

    /**
     * Query the series observations for streaming datasource
     * 
     * @param request
     *            The GetObservation request
     * @param session
     *            Hibernate Session
     * @return List of internal observations
     * @throws OwsExceptionReport
     *             If an error occurs.
     * @throws ConverterException
     *             If an error occurs during sensor description creation.
     */
    protected List<OmObservation> querySeriesObservationForStreaming(GetObservationRequest request,
            final Session session) throws OwsExceptionReport, ConverterException {
        final long start = System.currentTimeMillis();
        final List<OmObservation> result = new LinkedList<OmObservation>();
        // get valid featureOfInterest identifier
        final Set<String> features = QueryHelper.getFeatures(request, session);
        if (features != null && features.isEmpty()) {
            return result;
        }
        Criterion temporalFilterCriterion = HibernateGetObservationHelper.getTemporalFilterCriterion(request);
        for (Series series : new SeriesDAO().getSeries(request, features, session)) {
            Collection<? extends OmObservation> createSosObservationFromSeries =
                    HibernateObservationUtilities
                            .createSosObservationFromSeries(series, request.getVersion(), session);
            OmObservation observationTemplate = createSosObservationFromSeries.iterator().next();
            HibernateSeriesStreamingValue streamingValue = getSeriesStreamingValue(request, series.getSeriesId());
            streamingValue.setTemporalFilterCriterion(temporalFilterCriterion);
            streamingValue.setObservationTemplate(observationTemplate);
            observationTemplate.setValue(streamingValue);
            result.add(observationTemplate);
        }
        LOGGER.debug("Time to query observations needs {} ms!", (System.currentTimeMillis() - start));
        return result;
    }

    /**
     * Get the series streaming observation value for the observations
     * 
     * @param request
     *            GetObservation request
     * @param seriesId
     *            Series id
     * @return Streaming observation value
     */
    private HibernateSeriesStreamingValue getSeriesStreamingValue(GetObservationRequest request, long seriesId) {
        if (HibernateStreamingConfiguration.getInstance().isChunkDatasourceStreaming()) {
            return new HibernateChunkSeriesStreamingValue(request, seriesId);
        } else {
            return new HibernateScrollableSeriesStreamingValue(request, seriesId);
        }
    }

    /**
     * Get the streaming observation value for the observations
     * 
     * @param request
     *            GetObservation request
     * @param procedure
     *            Procedure id
     * @param observableProperty
     *            ObservableProperty id
     * @param feature
     *            FeatureOfInterest id
     * @return Streaming observation value
     */
    private HibernateStreamingValue getStreamingValue(GetObservationRequest request, long procedure,
            long observableProperty, long feature) {
        if (HibernateStreamingConfiguration.getInstance().isChunkDatasourceStreaming()) {
            return new HibernateChunkStreamingValue(request, procedure, observableProperty, feature);
        } else {
            return new HibernateScrollableStreamingValue(request, procedure, observableProperty, feature);
        }
    }

    /**
     * @see {@link HibernateGetObservationHelper#getAndCheckObservationConstellationSize}
     */
    @Deprecated
    private List<ObservationConstellation> getAndCheckObservationConstellationSize(GetObservationRequest request,
            Session session) throws CodedException {
        List<ObservationConstellation> observationConstellations = getObservationConstellations(session, request);
        checkMaxNumberOfReturnedSeriesSize(observationConstellations.size());
        return observationConstellations;
    }

    /**
     * @see {@link HibernateGetObservationHelper#getAndCheckObservationConstellationSize}
     */
    @Deprecated
    private void checkMaxNumberOfReturnedTimeSeries(Collection<SeriesObservation> seriesObservations,
            int metadataObservationsCount) throws CodedException {
        if (Integer.MAX_VALUE != ServiceConfiguration.getInstance().getMaxNumberOfReturnedTimeSeries()) {
            Set<Long> seriesIds = Sets.newHashSet();
            for (SeriesObservation seriesObs : seriesObservations) {
                seriesIds.add(seriesObs.getSeries().getSeriesId());
            }
            checkMaxNumberOfReturnedSeriesSize(seriesIds.size() + metadataObservationsCount);
        }
    }

    /**
     * @see {@link HibernateGetObservationHelper#getAndCheckObservationConstellationSize}
     */
    @Deprecated
    private void checkMaxNumberOfReturnedSeriesSize(int size) throws CodedException {
        // FIXME refactor profile handling
        if (size > ServiceConfiguration.getInstance().getMaxNumberOfReturnedTimeSeries()) {
            throw new ResponseExceedsSizeLimitException().at("maxNumberOfReturnedTimeSeries");
        }
    }

    /**
     * @see {@link HibernateGetObservationHelper#getAndCheckObservationConstellationSize}
     */
    @Deprecated
    private void checkMaxNumberOfReturnedValues(int size) throws CodedException {
        // FIXME refactor profile handling
        if (size > ServiceConfiguration.getInstance().getMaxNumberOfReturnedValues()) {
            throw new ResponseExceedsSizeLimitException().at("maxNumberOfReturnedValues");
        }
    }

    /**
     * Get and check featureOfInterest identifiers for Hydrology-Profile
     * 
     * @param observationConstellation
     *            ObservationConstellation
     * @param featureIdentifier
     *            FeatureOfInterest identifiers
     * @param session
     *            Hibernate session
     * @return Checked featureOfInterest identifiers
     * @throws CodedException
     *             If an error occurs
     *
     * @see {@link HibernateGetObservationHelper#getAndCheckObservationConstellationSize}
     */
    @Deprecated
    private List<String> getAndCheckFeatureOfInterest(final ObservationConstellation observationConstellation,
            final Set<String> featureIdentifier, final Session session) throws CodedException {
        final List<String> featuresForConstellation =
                new FeatureOfInterestDAO().getFeatureOfInterestIdentifiersForObservationConstellation(
                        observationConstellation, session);
        if (featureIdentifier == null) {
            return featuresForConstellation;
        } else {
            return CollectionHelper.conjunctCollections(featuresForConstellation, featureIdentifier);
        }
    }

    /**
     * Convert observation entities to internal observations
     * 
     * @param observations
     *            Observation entities
     * @param version
     *            Service version
     * @param resultModel
     *            Requested result model
     * @param session
     *            Hibernate session
     * @return Internal observation objects
     * @throws OwsExceptionReport
     *             If an error occurs
     * @throws ConverterException
     *             If an error occurs during the conversion
     * @see {@link HibernateGetObservationHelper#toSosObservation}
     */
    @Deprecated
    protected List<OmObservation> toSosObservation(final Collection<AbstractObservation> observations,
            final String version, final String resultModel, final Session session) throws OwsExceptionReport,
            ConverterException {
        if (!observations.isEmpty()) {
            Map<Long, AbstractSpatialFilteringProfile> spatialFilteringProfile = Maps.newHashMap();
            AbstractSpatialFilteringProfileDAO<?> spatialFilteringProfileDAO =
                    DaoFactory.getInstance().getSpatialFilteringProfileDAO(session);
            if (spatialFilteringProfileDAO != null) {
                spatialFilteringProfile =
                        spatialFilteringProfileDAO.getSpatialFilertingProfiles(
                                HibernateObservationUtilities.getObservationIds(observations), session);
            }
            final long startProcess = System.currentTimeMillis();
            final List<OmObservation> sosObservations =
                    HibernateObservationUtilities.createSosObservationsFromObservations(
                            new HashSet<AbstractObservation>(observations), spatialFilteringProfile, version,
                            resultModel, session);
            LOGGER.debug("Time to process observations needs {} ms!", (System.currentTimeMillis() - startProcess));
            return sosObservations;
        } else {
            return Collections.emptyList();
        }
    }

    /**
     * Add a result filter to the Criteria
     * 
     * @param c
     *            Hibernate criteria
     * @param resultFilter
     *            Result filter to add
     * @throws CodedException
     *             If the requested filter is not supported!
     * 
     * @see {@link HibernateGetObservationHelper#addResultFilterToCriteria}
     */
    @Deprecated
    @SuppressWarnings("rawtypes")
    private void addResultFilterToCriteria(Criteria c, Filter resultFilter) throws CodedException {
        if (resultFilter instanceof ComparisonFilter) {
            c.add(getCriterionForComparisonFilter((ComparisonFilter) resultFilter));
        } else if (resultFilter instanceof BinaryLogicFilter) {
            BinaryLogicFilter binaryLogicFilter = (BinaryLogicFilter) resultFilter;
            Junction junction = null;
            if (FilterConstants.BinaryLogicOperator.And.equals(binaryLogicFilter.getOperator())) {
                junction = Restrictions.conjunction();
            } else if (FilterConstants.BinaryLogicOperator.Or.equals(binaryLogicFilter.getOperator())) {
                junction = Restrictions.disjunction();
            } else {
                throw new NoApplicableCodeException()
                        .withMessage("The requested binary logic filter operator is invalid!");
            }
            for (Filter<?> filterPredicate : binaryLogicFilter.getFilterPredicates()) {
                if (!(filterPredicate instanceof ComparisonFilter)) {
                    throw new NoApplicableCodeException().withMessage("The requested result filter is not supported!");
                }
                junction.add(getCriterionForComparisonFilter((ComparisonFilter) filterPredicate));
            }
            c.add(junction);
        } else {
            throw new NoApplicableCodeException().withMessage("The requested result filter is not supported!");
        }
    }

    /**
     * Get the Hibernate Criterion for the requested result filter
     * 
     * @param resultFilter
     *            Requested result filter
     * @return Hibernate Criterion
     * @throws CodedException
     *             If the requested result filter is not supported
     * @see {@link HibernateGetObservationHelper#getCriterionForComparisonFilter}
     */
    @Deprecated
    private Criterion getCriterionForComparisonFilter(ComparisonFilter resultFilter) throws CodedException {
        if (ComparisonOperator.PropertyIsLike.equals(resultFilter.getOperator())) {
            checkValueReferenceForResultFilter(resultFilter.getValueReference());
            if (resultFilter.isSetEscapeString()) {
                return HibernateCriterionHelper.getLikeExpression(Observation.DESCRIPTION,
                        checkValueForWildcardSingleCharAndEscape(resultFilter), MatchMode.ANYWHERE,
                        Constants.DOLLAR_CHAR, true);
            } else {
                return Restrictions.like(Observation.DESCRIPTION,
                        checkValueForWildcardSingleCharAndEscape(resultFilter), MatchMode.ANYWHERE);
            }
        } else {
            throw new NoApplicableCodeException().withMessage(
                    "The requested comparison filter {} is not supported! Only {} is supported!", resultFilter
                            .getOperator().name(), ComparisonOperator.PropertyIsLike.name());
        }
    }

    /**
     * Check if the default SQL values for wildcard, single char or escape are
     * used. If not replace the characters from the result filter with the
     * default values.
     * 
     * @param resultFilter
     *            Requested result filter
     * @return Modified request string with default character.
     * @see {@link HibernateGetObservationHelper#checkValueForWildcardSingleCharAndEscape}
     */
    @Deprecated
    private String checkValueForWildcardSingleCharAndEscape(ComparisonFilter resultFilter) {
        String value = resultFilter.getValue();
        if (resultFilter.isSetSingleChar() && !resultFilter.getSingleChar().equals(Constants.PERCENT_STRING)) {
            value = value.replace(resultFilter.getSingleChar(), Constants.UNDERSCORE_STRING);
        }
        if (resultFilter.isSetWildCard() && !resultFilter.getWildCard().equals(Constants.UNDERSCORE_STRING)) {
            value = value.replace(resultFilter.getWildCard(), Constants.UNDERSCORE_STRING);
        }
        if (resultFilter.isSetEscapeString() && !resultFilter.getEscapeString().equals(Constants.DOLLAR_STRING)) {
            value = value.replace(resultFilter.getWildCard(), Constants.UNDERSCORE_STRING);
        }
        return value;
    }

    /**
     * Check if the requested value reference is supported.
     * 
     * @param valueReference
     *            Requested value reference
     * @throws CodedException
     *             If the requested value reference is not supported.
     * @see {@link HibernateGetObservationHelper#checkValueReferenceForResultFilter}
     */
    @Deprecated
    private void checkValueReferenceForResultFilter(String valueReference) throws CodedException {
        if (Strings.isNullOrEmpty(valueReference)) {
            throw new NoApplicableCodeException().withMessage(
                    "The requested valueReference is missing! The valueReference should be %s/%s!",
                    OmConstants.VALUE_REF_OM_OBSERVATION, GmlConstants.VALUE_REF_GML_DESCRIPTION);
        } else if (!valueReference.startsWith(OmConstants.VALUE_REF_OM_OBSERVATION)
                && !valueReference.contains(GmlConstants.VALUE_REF_GML_DESCRIPTION))
            throw new NoApplicableCodeException().withMessage(
                    "The requested valueReference is not supported! Currently only %s/%s is supported",
                    OmConstants.VALUE_REF_OM_OBSERVATION, GmlConstants.VALUE_REF_GML_DESCRIPTION);
    }

    /**
     * Get ObervationConstellation from requested parameters
     * 
     * @param session
     *            Hibernate session
     * @param request
     *            GetObservation request
     * @return Resulting ObservationConstellation entities
     * @see {@link HibernateGetObservationHelper#getObservationConstellations}
     */
    @Deprecated
    protected List<ObservationConstellation> getObservationConstellations(final Session session,
            final GetObservationRequest request) {
        return new ObservationConstellationDAO().getObservationConstellations(request.getProcedures(),
                request.getObservedProperties(), request.getOfferings(), session);
    }

    /**
     * Get Hibernate Criterion from requested temporal filters
     * 
     * @param request
     *            GetObservation request
     * @return Hibernate Criterion from requested temporal filters
     * @throws OwsExceptionReport
     *             If a temporal filter is not supported
     * @see {@link HibernateGetObservationHelper#getTemporalFilterCriterion}
     */
    @Deprecated
    protected Criterion getTemporalFilterCriterion(final GetObservationRequest request) throws OwsExceptionReport {

        final List<TemporalFilter> filters = request.getNotFirstLatestTemporalFilter();
        if (request.hasTemporalFilters() && CollectionHelper.isNotEmpty(filters)) {
            return TemporalRestrictions.filter(filters);
        } else {
            return null;
        }
    }

    /**
     * @see {@link HibernateGetObservationHelper#checkEncoderForMergeObservationValues}
     */
    @Deprecated
    private boolean checkEncoderForMergeObservationValues(String responseFormat) {
        Encoder<XmlObject, OmObservation> encoder =
                CodingRepository.getInstance().getEncoder(new XmlEncoderKey(responseFormat, OmObservation.class));
        if (encoder == null && encoder instanceof ObservationEncoder) {
            return ((ObservationEncoder<?, OmObservation>) encoder).shouldObservationsWithSameXBeMerged();
        }
        return false;
    }

    /**
     * Create Hibernate Criteria for Observation entity and add restrictions for
     * ObservationConstellation parameter
     * 
     * @param session
     *            Hibernate session
     * @param oc
     *            ObservationConstellation with parameter for restrictions
     * @return Hibernate Criteria for Observation entity
     */
    @Deprecated
    protected Criteria createObservationCriteria(final Session session, final ObservationConstellation oc) {
        final Criteria c =
                session.createCriteria(Observation.class).setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY)
                        .add(Restrictions.eq(Observation.DELETED, false));
        c.createCriteria(Observation.OBSERVABLE_PROPERTY).add(
                Restrictions.eq(ObservableProperty.ID, oc.getObservableProperty().getObservablePropertyId()));
        c.createCriteria(Observation.PROCEDURE).add(Restrictions.eq(Procedure.ID, oc.getProcedure().getProcedureId()));
        c.createCriteria(Observation.OFFERINGS).add(Restrictions.eq(Offering.ID, oc.getOffering().getOfferingId()));
        return c;
    }

    /**
     * Create Hibernate Criteria for Observation entity and add restrictions for
     * ObservationConstellation parameter and featureOfInterest identifier
     * 
     * @param session
     *            Hibernate session
     * @param oc
     *            ObservationConstellation with parameter for restrictions
     * @param features
     *            FeatureOfInterest identifier for restrictions
     * @return Hibernate Criteria for Observation entity
     */
    @Deprecated
    protected Criteria createObservationCriteria(final Session session, final ObservationConstellation oc,
            final Set<String> features) {
        Criteria criteria = createObservationCriteria(session, oc);
        if (CollectionHelper.isNotEmpty(features)) {
            criteria.createCriteria(Observation.FEATURE_OF_INTEREST).add(
                    Restrictions.in(FeatureOfInterest.IDENTIFIER, features));
        }
        return criteria;
    }

    /**
     * Create Hibernate Criteria from requested parameters
     * 
     * @param session
     *            Hibernate session
     * @param request
     *            GetObservation request
     * @param features
     *            FeatureOfInterest identifiers
     * @return Hibernate Criteria
     * @throws HibernateException
     *             If an error occurs during Criteria creation
     * @throws OwsExceptionReport
     *             If an requested parameter is not supported
     */
    @Deprecated
    protected Criteria createTemporalFilterLessCriteria(final Session session, final GetObservationRequest request,
            final Set<String> features) throws HibernateException, OwsExceptionReport {

        final Criteria c =
                DaoFactory.getInstance().getObservationDAO(session)
                        .getObservationClassCriteriaForResultModel(request.getResultModel(), session);

        if (request.hasSpatialFilteringProfileSpatialFilter()) {
            if (!HibernateHelper.isEntitySupported(SpatialFilteringProfile.class, session)) {
                throw new OptionNotSupportedException().at(Sos2Constants.GetObservationParams.spatialFilter)
                        .withMessage("The SOS 2.0 Spatial Filtering Profile is not supported by this service!");
            }
            Set<Long> observationIds =
                    new SpatialFilteringProfileDAO().getObservationIdsForSpatialFilter(request.getSpatialFilter(),
                            session);
            if (CollectionHelper.isEmpty(observationIds)) {
                c.add(Restrictions.eq(Observation.ID, Long.MIN_VALUE));
            } else if (CollectionHelper.isNotEmpty(observationIds)) {
                Disjunction disjunction = Restrictions.disjunction();
                for (List<Long> list : HibernateHelper.getValidSizedLists(observationIds)) {
                    disjunction.add(Restrictions.in(Observation.ID, list));
                }
                c.add(disjunction);
            }
        }

        if (request.isSetOffering()) {
            c.createCriteria(Observation.OFFERINGS).add(Restrictions.in(Offering.IDENTIFIER, request.getOfferings()));
        }
        if (request.isSetObservableProperty()) {
            c.createCriteria(Observation.OBSERVABLE_PROPERTY).add(
                    Restrictions.in(ObservableProperty.IDENTIFIER, request.getObservedProperties()));
        }
        if (request.isSetProcedure()) {
            c.createCriteria(Observation.PROCEDURE)
                    .add(Restrictions.in(Procedure.IDENTIFIER, request.getProcedures()));
        }
        if (features != null) {
            c.createCriteria(Observation.FEATURE_OF_INTEREST).add(
                    Restrictions.in(FeatureOfInterest.IDENTIFIER, features));
        }

        if (request.isSetResultFilter()) {
            HibernateGetObservationHelper.addResultFilterToCriteria(c, request.getResultFilter());
        }
        return c;
    }

    /**
     * Get result order for {@link SosIndeterminateTime}
     * 
     * @param indetTime
     *            SosIndeterminateTime value
     * @return Hibernate result Order
     */
    @Deprecated
    protected Order getOrder(final SosIndeterminateTime indetTime) {
        if (indetTime.equals(SosIndeterminateTime.first)) {
            return Order.asc(Observation.PHENOMENON_TIME_START);
        } else if (indetTime.equals(SosIndeterminateTime.latest)) {
            return Order.desc(Observation.PHENOMENON_TIME_END);
        }
        return null;
    }

    /**
     * Execute database query for Hydrology-Profile
     * 
     * @param criteria
     *            Hibernate Criteria to execute
     * @return List of resulting observations
     */
    @Deprecated
    @SuppressWarnings("unchecked")
    private List<Observation> executeQueryObservationHydro(Criteria criteria) {
        LOGGER.debug("QUERY queryObservationHydro(): {}", HibernateHelper.getSqlString(criteria));
        return criteria.list();
    }

    /**
     * Get latest observation time Hibernate Criterion with named query and for
     * ObservationConstellation and featureOfInterest identifier
     * 
     * @param oc
     *            ObservationConstellation with parameter for restrictions
     * @param featureIdentifier
     *            FeatureOfInterest identifier for restrictions
     * @param session
     *            Hibernate session
     * @return Hibernate Criterion for latest observation time
     */
    @Deprecated
    private Criterion getLatestObservationTimeCriterion(ObservationConstellation oc, String featureIdentifier,
            Session session) {
        Query namedQuery = session.getNamedQuery(ObservationDAO.SQL_QUERY_GET_LATEST_OBSERVATION_TIME);
        namedQuery.setParameter(HibernateSqlQueryConstants.FEATURE, featureIdentifier);
        namedQuery.setParameter(HibernateSqlQueryConstants.OBSERVABLE_PROPERTY, oc.getObservableProperty()
                .getIdentifier());
        namedQuery.setParameter(HibernateSqlQueryConstants.PROCEDURE, oc.getProcedure().getIdentifier());
        namedQuery.setParameter(HibernateSqlQueryConstants.OFFERING, oc.getOffering().getIdentifier());
        return Restrictions.eq(Observation.PHENOMENON_TIME_START, namedQuery.uniqueResult());
    }

    /**
     * Get first observation time Hibernate Criterion with named query and for
     * ObservationConstellation and featureOfInterest identifier
     * 
     * @param oc
     *            ObservationConstellation with parameter for restrictions
     * @param featureIdentifier
     *            FeatureOfInterest identifier for restrictions
     * @param session
     *            Hibernate session
     * @return Hibernate Criterion for first observation time
     */
    @Deprecated
    private Criterion getFirstObservationTimeCriterion(ObservationConstellation oc, String featureIdentifier,
            Session session) {
        Query namedQuery = session.getNamedQuery(ObservationDAO.SQL_QUERY_GET_FIRST_OBSERVATION_TIME);
        namedQuery.setParameter(HibernateSqlQueryConstants.FEATURE, featureIdentifier);
        namedQuery.setParameter(HibernateSqlQueryConstants.OBSERVABLE_PROPERTY, oc.getObservableProperty()
                .getIdentifier());
        namedQuery.setParameter(HibernateSqlQueryConstants.PROCEDURE, oc.getProcedure().getIdentifier());
        namedQuery.setParameter(HibernateSqlQueryConstants.OFFERING, oc.getOffering().getIdentifier());
        return Restrictions.eq(Observation.PHENOMENON_TIME_START, namedQuery.uniqueResult());
    }

}
