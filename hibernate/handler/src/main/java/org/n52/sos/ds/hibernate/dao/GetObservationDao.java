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
package org.n52.sos.ds.hibernate.dao;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import javax.inject.Inject;

import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.criterion.Criterion;
import org.n52.faroe.annotation.Configurable;
import org.n52.faroe.annotation.Setting;
import org.n52.iceland.convert.ConverterException;
import org.n52.iceland.ds.ConnectionProvider;
import org.n52.iceland.exception.ows.concrete.NotYetSupportedException;
import org.n52.iceland.i18n.I18NSettings;
import org.n52.iceland.service.MiscSettings;
import org.n52.janmayen.http.HTTPStatus;
import org.n52.series.db.beans.DataEntity;
import org.n52.series.db.beans.DatasetEntity;
import org.n52.shetland.ogc.gml.time.IndeterminateValue;
import org.n52.shetland.ogc.om.ObservationStream;
import org.n52.shetland.ogc.om.OmObservation;
import org.n52.shetland.ogc.ows.exception.NoApplicableCodeException;
import org.n52.shetland.ogc.ows.exception.OwsExceptionReport;
import org.n52.shetland.ogc.sos.request.GetObservationRequest;
import org.n52.shetland.ogc.sos.response.GetObservationResponse;
import org.n52.shetland.ogc.sos.response.GlobalObservationResponseValues;
import org.n52.shetland.util.CollectionHelper;
import org.n52.sos.ds.hibernate.HibernateSessionHolder;
import org.n52.sos.ds.hibernate.dao.observation.series.AbstractSeriesDAO;
import org.n52.sos.ds.hibernate.dao.observation.series.AbstractSeriesObservationDAO;
import org.n52.sos.ds.hibernate.util.HibernateGetObservationHelper;
import org.n52.sos.ds.hibernate.util.ObservationTimeExtrema;
import org.n52.sos.ds.hibernate.util.observation.HibernateObservationUtilities;
import org.n52.sos.ds.hibernate.util.observation.OmObservationCreatorContext;
import org.n52.sos.ds.hibernate.values.series.HibernateChunkSeriesStreamingValue;
import org.n52.sos.ds.hibernate.values.series.HibernateSeriesStreamingValue;
import org.n52.sos.service.profile.ProfileHandler;
import org.n52.svalbard.encode.Encoder;
import org.n52.svalbard.encode.EncoderRepository;
import org.n52.svalbard.encode.ObservationEncoder;
import org.n52.svalbard.encode.XmlEncoderKey;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

@Configurable
public class GetObservationDao
        implements org.n52.sos.ds.dao.GetObservationDao {
    private static final Logger LOGGER = LoggerFactory.getLogger(GetObservationDao.class);

    private HibernateSessionHolder sessionHolder;
    private ProfileHandler profileHandler;
    private EncoderRepository encoderRepository;
    private DaoFactory daoFactory;
    private OmObservationCreatorContext observationCreatorContext;
    private boolean overallExtrema;
    private Locale defaultLanguage;

    @Inject
    public void setEncoderRepository(EncoderRepository encoderRepository) {
        this.encoderRepository = encoderRepository;
    }

    @Inject
    public void setDaoFactory(DaoFactory daoFactory) {
        this.daoFactory = daoFactory;
    }

    @Inject
    public void setConnectionProvider(ConnectionProvider connectionProvider) {
        this.sessionHolder = new HibernateSessionHolder(connectionProvider);
    }

    @Inject
    public void setProfileHandler(ProfileHandler profileHandler) {
        this.profileHandler = profileHandler;
    }

    @Inject
    public void setOmObservationCreatorContext(OmObservationCreatorContext observationCreatorContext) {
        this.observationCreatorContext = observationCreatorContext;
    }

    @Setting(MiscSettings.RETURN_OVERALL_EXTREMA_FOR_FIRST_LATEST)
    public void setOverallExtrema(boolean overallExtrema) {
        this.overallExtrema = overallExtrema;
    }

    @Setting(I18NSettings.I18N_DEFAULT_LANGUAGE)
    public void setDefaultLanguage(String defaultLanguage) {
        this.defaultLanguage = new Locale(defaultLanguage);
    }

    @Override
    public GetObservationResponse queryObservationData(GetObservationRequest request, GetObservationResponse response)
            throws OwsExceptionReport {
        Session session = null;
        try {
            session = sessionHolder.getSession();
            List<OmObservation> observations = new ArrayList<>();
            if (!request.hasFirstLatestTemporalFilter()) {
                observations.addAll(querySeriesObservationForStreaming(request, response, session));
            } else {
                observations.addAll(querySeriesObservation(request, session));
            }
            response.setObservationCollection(ObservationStream.of(observations));
            return response;
        } catch (HibernateException he) {
            throw new NoApplicableCodeException().causedBy(he).withMessage("Error while querying observation data!")
                    .setStatus(HTTPStatus.INTERNAL_SERVER_ERROR);
        } catch (ConverterException ce) {
            throw new NoApplicableCodeException().causedBy(ce).withMessage("Error while processing observation data!")
                    .setStatus(HTTPStatus.INTERNAL_SERVER_ERROR);
        } finally {
            sessionHolder.returnSession(session);
        }

    }

    /**
     * Query observation if the series mapping is supported.
     *
     * @param request
     *            GetObservation request
     * @param observationDAO
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
        AbstractSeriesObservationDAO observationDAO = daoFactory.getObservationDAO();
        Locale requestedLocale = getRequestedLocale(request);
        String pdf = getProcedureDescriptionFormat(request.getResponseFormat());
        final long start = System.currentTimeMillis();
        List<String> features = request.getFeatureIdentifiers();
        // temporal filters
        final List<IndeterminateValue> extendedIndeterminateTimeFilters = request.getFirstLatestTemporalFilter();
        final Criterion filterCriterion = HibernateGetObservationHelper.getTemporalFilterCriterion(request);

        final List<OmObservation> result = new LinkedList<>();
        Collection<DataEntity<?>> seriesObservations = Lists.newArrayList();

        AbstractSeriesDAO seriesDAO = daoFactory.getSeriesDAO();

        // query with temporal filter
        if (filterCriterion != null) {
            seriesObservations = checkObservationsForDuplicity(
                    observationDAO.getSeriesObservationsFor(request, features, filterCriterion, session), request);
        }
        // query with first/latest value filter
        else if (CollectionHelper.isNotEmpty(extendedIndeterminateTimeFilters)) {
            for (IndeterminateValue sosIndeterminateTime : extendedIndeterminateTimeFilters) {
                if (overallExtrema) {
                    seriesObservations =
                            observationDAO.getSeriesObservationsFor(request, features, sosIndeterminateTime, session);
                } else {
                    for (DatasetEntity series : seriesDAO.getSeries(request, features, session)) {
                        seriesObservations.addAll(observationDAO.getSeriesObservationsFor(series, request,
                                sosIndeterminateTime, session));

                    }
                    seriesObservations = checkObservationsForDuplicity(
                            observationDAO.getSeriesObservationsFor(request, features, session), request);
                }
            }
        }
        // query without temporal or indeterminate filters
        else {
            seriesObservations = checkObservationsForDuplicity(
                    observationDAO.getSeriesObservationsFor(request, features, session), request);
        }

        // if active profile demands observation metadata for series without
        // matching observations,
        // a "result" observation without values is created.
        // TODO does this apply for indeterminate time first/latest filters?
        // Yes.
        int metadataObservationsCount = 0;
        if (profileHandler.getActiveProfile().isShowMetadataOfEmptyObservations()) {
            // create a map of series to check by id, so we don't need to fetch
            // each observation's series from the database
            Map<Long, DatasetEntity> seriesToCheckMap = Maps.newHashMap();
            for (DatasetEntity series : seriesDAO.getSeries(request, features, session)) {
                seriesToCheckMap.put(series.getId(), series);
            }

            // check observations and remove any series found from the map
            for (DataEntity<?> seriesObs : seriesObservations) {
                long seriesId = seriesObs.getDataset().getId();
                if (seriesToCheckMap.containsKey(seriesId)) {
                    seriesToCheckMap.remove(seriesId);
                }
            }
            // now we're left with the series without matching observations in
            // the check map,
            // add "result" observations for them
            metadataObservationsCount = seriesToCheckMap.size();
            for (DatasetEntity series : seriesToCheckMap.values()) {
                HibernateObservationUtilities.createSosObservationFromSeries(series, request, requestedLocale, pdf,
                        observationCreatorContext, session).forEachRemaining(result::add);
            }
        }
        HibernateGetObservationHelper.checkMaxNumberOfReturnedTimeSeries(seriesObservations,
                metadataObservationsCount);
        HibernateGetObservationHelper.checkMaxNumberOfReturnedValues(seriesObservations.size());

        LOGGER.debug("Time to query observations needs {} ms!", (System.currentTimeMillis() - start));
        Collection<DataEntity<?>> abstractObservations = Lists.newArrayList();
        abstractObservations.addAll(seriesObservations);
        HibernateGetObservationHelper.toSosObservation(new ArrayList<>(seriesObservations), request, requestedLocale,
                pdf, observationCreatorContext, session).forEachRemaining(result::add);
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
            GetObservationResponse response, Session session) throws OwsExceptionReport, ConverterException {
        final long start = System.currentTimeMillis();
        final List<OmObservation> result = new LinkedList<OmObservation>();
        List<String> features = request.getFeatureIdentifiers();
        Criterion temporalFilterCriterion = HibernateGetObservationHelper.getTemporalFilterCriterion(request);
        List<DatasetEntity> serieses = daoFactory.getSeriesDAO().getSeries(request, features, session);
        HibernateGetObservationHelper.checkMaxNumberOfReturnedSeriesSize(serieses.size());
        int maxNumberOfValuesPerSeries = HibernateGetObservationHelper.getMaxNumberOfValuesPerSeries(serieses.size());
        checkSeriesOfferings(serieses, request);
        Collection<DatasetEntity> duplicated = checkAndGetDuplicatedtSeries(serieses, request);
        for (DatasetEntity series : serieses) {
            ObservationStream createSosObservationFromSeries =
                    HibernateObservationUtilities.createSosObservationFromSeries(series, request,
                            getRequestedLocale(request), getProcedureDescriptionFormat(request.getResponseFormat()),
                            observationCreatorContext, session);
            OmObservation observationTemplate = createSosObservationFromSeries.next();
            HibernateSeriesStreamingValue streamingValue =
                    new HibernateChunkSeriesStreamingValue(sessionHolder.getConnectionProvider(), daoFactory, request,
                            series.getId(), duplicated.contains(series));
            streamingValue.setResponseFormat(request.getResponseFormat());
            streamingValue.setTemporalFilterCriterion(temporalFilterCriterion);
            streamingValue.setObservationTemplate(observationTemplate);
            streamingValue.setMaxNumberOfValues(maxNumberOfValuesPerSeries);
            observationTemplate.setValue(streamingValue);
            result.add(observationTemplate);
        }

        ObservationTimeExtrema timeExtrema =
                daoFactory.getValueTimeDAO().getTimeExtremaForSeries(serieses, temporalFilterCriterion, session);
        if (timeExtrema.isSetPhenomenonTimes()) {
            response.setGlobalObservationValues(
                    new GlobalObservationResponseValues().setPhenomenonTime(timeExtrema.getPhenomenonTime()));
        }
        LOGGER.debug("Time to query observations needs {} ms!", (System.currentTimeMillis() - start));
        return result;
    }

    private void checkSeriesOfferings(List<DatasetEntity> serieses, GetObservationRequest request) {
        boolean allSeriesWithOfferings = true;
        for (DatasetEntity series : serieses) {
            allSeriesWithOfferings = !series.isSetOffering() ? false : allSeriesWithOfferings;
        }
        if (allSeriesWithOfferings) {
            request.setOfferings(Lists.<String> newArrayList());
        }
    }

    private Collection<DatasetEntity> checkAndGetDuplicatedtSeries(List<DatasetEntity> serieses, GetObservationRequest request) {
        if (!request.isCheckForDuplicity()) {
            return Sets.newHashSet();
        }
        Set<DatasetEntity> single = Sets.newHashSet();
        Set<DatasetEntity> duplicated = Sets.newHashSet();
        for (DatasetEntity series : serieses) {
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

    private boolean isDuplicatedSeries(DatasetEntity series, Set<DatasetEntity> serieses) {
        for (DatasetEntity s : serieses) {
            if (hasSameObservationIdentifier(series, s)) {
                return true;
            }
        }
        return false;
    }

    public boolean hasSameObservationIdentifier(DatasetEntity toCheck, DatasetEntity s) {
        return toCheck.getFeature().equals(s.getFeature()) && toCheck.getProcedure().equals(s.getProcedure())
                && toCheck.getObservableProperty().equals(s.getObservableProperty());
    }

    private Collection<DataEntity<?>> checkObservationsForDuplicity(
            Collection<DataEntity<?>> seriesObservations, GetObservationRequest request) {
        if (!request.isCheckForDuplicity()) {
            return seriesObservations;
        }
        Collection<DataEntity<?>> checked = Lists.newArrayList();
        Set<DatasetEntity> serieses = Sets.newHashSet();
        Set<DatasetEntity> duplicated = Sets.newHashSet();
        for (DataEntity<?> seriesObservation : seriesObservations) {
            if (serieses.isEmpty()) {
                serieses.add(seriesObservation.getDataset());
            } else {
                if (!serieses.contains(seriesObservation.getDataset()) && !duplicated.contains(seriesObservation)
                        && isDuplicatedSeries(seriesObservation.getDataset(), serieses)) {
                    duplicated.add(seriesObservation.getDataset());
                }
            }

            if (serieses.contains(seriesObservation.getDataset()) || (duplicated.contains(seriesObservation.getDataset())
                    && seriesObservation.getDataset().getOffering() != null)) {
                checked.add(seriesObservation);
            }
        }
        return checked;
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
}
