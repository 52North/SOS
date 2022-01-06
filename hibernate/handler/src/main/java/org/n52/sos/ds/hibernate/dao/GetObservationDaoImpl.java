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
package org.n52.sos.ds.hibernate.dao;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.inject.Inject;

import org.apache.xmlbeans.XmlObject;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.criterion.Criterion;
import org.n52.faroe.annotation.Configurable;
import org.n52.faroe.annotation.Setting;
import org.n52.iceland.convert.ConverterException;
import org.n52.iceland.ds.ConnectionProvider;
import org.n52.iceland.exception.ows.concrete.NotYetSupportedException;
import org.n52.janmayen.http.HTTPStatus;
import org.n52.series.db.beans.DataEntity;
import org.n52.series.db.beans.DatasetEntity;
import org.n52.shetland.ogc.gml.time.IndeterminateValue;
import org.n52.shetland.ogc.om.ObservationStream;
import org.n52.shetland.ogc.om.OmObservation;
import org.n52.shetland.ogc.ows.exception.NoApplicableCodeException;
import org.n52.shetland.ogc.ows.exception.OwsExceptionReport;
import org.n52.shetland.ogc.sos.ExtendedIndeterminateTime;
import org.n52.shetland.ogc.sos.request.GetObservationRequest;
import org.n52.shetland.ogc.sos.response.GetObservationResponse;
import org.n52.shetland.ogc.sos.response.GlobalObservationResponseValues;
import org.n52.sos.ds.hibernate.HibernateSessionHolder;
import org.n52.sos.ds.hibernate.dao.observation.series.AbstractSeriesDAO;
import org.n52.sos.ds.hibernate.util.ObservationTimeExtrema;
import org.n52.sos.ds.hibernate.util.observation.HibernateObservationUtilities;
import org.n52.sos.ds.hibernate.util.observation.HibernateOmObservationCreatorContext;
import org.n52.sos.ds.hibernate.values.dataset.HibernateChunkSeriesStreamingValue;
import org.n52.sos.ds.hibernate.values.dataset.HibernateSeriesStreamingValue;
import org.n52.sos.service.profile.ProfileHandler;
import org.n52.svalbard.encode.Encoder;
import org.n52.svalbard.encode.ObservationEncoder;
import org.n52.svalbard.encode.XmlEncoderKey;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

@Configurable
public class GetObservationDaoImpl extends AbstractObservationDao implements org.n52.sos.ds.dao.GetObservationDao {
    private static final Logger LOGGER = LoggerFactory.getLogger(GetObservationDaoImpl.class);

    private static final String LOG_TIME_TO_QUERY = "Time to query observations needs {} ms!";

    private HibernateSessionHolder sessionHolder;

    private ProfileHandler profileHandler;

    private DaoFactory daoFactory;

    private HibernateOmObservationCreatorContext observationCreatorContext;

    private boolean overallExtrema;

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
    public void setOmObservationCreatorContext(HibernateOmObservationCreatorContext observationCreatorContext) {
        this.observationCreatorContext = observationCreatorContext;
    }

    @Setting("profile.hydrology.overallExtrema")
    public void setOverallExtrema(boolean overallExtrema) {
        this.overallExtrema = overallExtrema;
    }

    @Override
    public GetObservationResponse queryObservationData(GetObservationRequest request, GetObservationResponse response)
            throws OwsExceptionReport {
        Session session = null;
        try {
            session = sessionHolder.getSession();
            return getObservations(request, response, session);
        } catch (HibernateException he) {
            throw new NoApplicableCodeException().causedBy(he).withMessage("Error while querying observation data!")
                    .setStatus(HTTPStatus.INTERNAL_SERVER_ERROR);
        } finally {
            sessionHolder.returnSession(session);
        }
    }

    @Override
    public GetObservationResponse queryObservationData(GetObservationRequest request, GetObservationResponse response,
            Object connection) throws OwsExceptionReport {
        if (checkConnection(connection)) {
            return getObservations(request, response, HibernateSessionHolder.getSession(connection));
        }
        return queryObservationData(request, response);
    }

    private GetObservationResponse getObservations(GetObservationRequest request, GetObservationResponse response,
            Session session) throws OwsExceptionReport {
        List<OmObservation> observations = new ArrayList<>();
        try {
            if (!request.hasFirstLatestTemporalFilter()) {
                observations.addAll(querySeriesObservationForStreaming(request, response, session));
            } else {
                observations.addAll(querySeriesObservation(request, session));
            }
            response.setObservationCollection(ObservationStream.of(observations));
            return response;
        } catch (ConverterException ce) {
            throw new NoApplicableCodeException().causedBy(ce).withMessage("Error while processing observation data!")
                    .setStatus(HTTPStatus.INTERNAL_SERVER_ERROR);
        }
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
    private List<OmObservation> querySeriesObservation(GetObservationRequest request, Session session)
            throws OwsExceptionReport, ConverterException {
        if (request.isSetResultFilter()) {
            throw new NotYetSupportedException("result filtering");
        }
        Locale requestedLocale = getRequestedLocale(request);
        String pdf = getProcedureDescriptionFormat(request.getResponseFormat());
        final long start = System.currentTimeMillis();
        List<String> features = request.getFeatureIdentifiers();

        Collection<DataEntity<?>> seriesObservations = Lists.newArrayList();
        AbstractSeriesDAO seriesDAO = daoFactory.getSeriesDAO();
        for (IndeterminateValue sosIndeterminateTime : request.getFirstLatestTemporalFilter()) {
            for (DatasetEntity series : getSeries(seriesDAO, request, features, sosIndeterminateTime, session)) {
                if (sosIndeterminateTime.equals(ExtendedIndeterminateTime.FIRST)) {
                    seriesObservations.add(series.getFirstObservation());
                } else if (sosIndeterminateTime.equals(ExtendedIndeterminateTime.LATEST)) {
                    seriesObservations.add(series.getLastObservation());
                }
            }
        }

        final List<OmObservation> result = new LinkedList<>();
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
            for (DatasetEntity series : seriesToCheckMap.values()) {
                HibernateObservationUtilities.createSosObservationFromSeries(series, request, requestedLocale, pdf,
                        observationCreatorContext, session).forEachRemaining(result::add);
            }
        }

        LOGGER.debug(LOG_TIME_TO_QUERY, System.currentTimeMillis() - start);
        toSosObservation(new ArrayList<>(seriesObservations), request, requestedLocale, pdf, observationCreatorContext,
                session).forEachRemaining(result::add);
        return result;
    }

    private List<DatasetEntity> getSeries(AbstractSeriesDAO seriesDAO, GetObservationRequest request,
            List<String> features, IndeterminateValue sosIndeterminateTime, Session session) throws OwsExceptionReport {
        if (!overallExtrema) {
            return seriesDAO.getSeries(request, features, session);
        }
        Date first = null;
        Date last = null;
        List<DatasetEntity> list = new LinkedList<>();
        for (DatasetEntity dataset : seriesDAO.getSeries(request, features, session)) {
            if (sosIndeterminateTime.equals(ExtendedIndeterminateTime.FIRST)) {
                if (first == null) {
                    first = dataset.getFirstValueAt();
                    list.add(dataset);
                } else if (dataset.getFirstValueAt().equals(first)) {
                    list.add(dataset);
                } else if (dataset.getFirstValueAt().before(first)) {
                    list.clear();
                    first = dataset.getFirstValueAt();
                    list.add(dataset);
                }
            } else if (sosIndeterminateTime.equals(ExtendedIndeterminateTime.LATEST)) {
                if (last == null) {
                    last = dataset.getLastValueAt();
                    list.add(dataset);
                } else if (dataset.getLastValueAt().equals(last)) {
                    list.add(dataset);
                } else if (dataset.getLastValueAt().after(last)) {
                    list.clear();
                    last = dataset.getLastValueAt();
                    list.add(dataset);
                }
            }
        }
        return list;
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
    private List<OmObservation> querySeriesObservationForStreaming(GetObservationRequest request,
            GetObservationResponse response, Session session) throws OwsExceptionReport, ConverterException {
        final long start = System.currentTimeMillis();
        final List<OmObservation> result = new LinkedList<OmObservation>();
        List<String> features = request.getFeatureIdentifiers();
        Criterion temporalFilterCriterion = getTemporalFilterCriterion(request);
        List<DatasetEntity> serieses = daoFactory.getSeriesDAO().getSeries(request, features, session);
        checkMaxNumberOfReturnedSeriesSize(serieses.size());
        int maxNumberOfValuesPerSeries = getMaxNumberOfValuesPerSeries(serieses.size());
        for (DatasetEntity series : serieses) {
            ObservationStream createSosObservationFromSeries = series.hasEreportingProfile()
                    ? HibernateObservationUtilities.createSosObservationFromEReportingSeries(series, request,
                            getRequestedLocale(request), getProcedureDescriptionFormat(request.getResponseFormat()),
                            observationCreatorContext, session)
                    : HibernateObservationUtilities.createSosObservationFromSeries(series, request,
                            getRequestedLocale(request), getProcedureDescriptionFormat(request.getResponseFormat()),
                            observationCreatorContext, session);
            OmObservation observationTemplate = createSosObservationFromSeries.next();
            HibernateSeriesStreamingValue streamingValue =
                    new HibernateChunkSeriesStreamingValue(sessionHolder.getConnectionProvider(), daoFactory, request,
                            series, getChunkSize());
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
        LOGGER.debug(LOG_TIME_TO_QUERY, System.currentTimeMillis() - start);
        return result;
    }

    private String getProcedureDescriptionFormat(String responseFormat) {
        Encoder<XmlObject, OmObservation> encoder = getEncoder(new XmlEncoderKey(responseFormat, OmObservation.class));
        if (encoder != null && encoder instanceof ObservationEncoder) {
            return ((ObservationEncoder) encoder).getProcedureEncodingNamspace();
        }
        return null;
    }

}
