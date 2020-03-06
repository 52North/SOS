/**
 * Copyright (C) 2012-2020 52°North Initiative for Geospatial Open Source
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

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

import org.n52.sos.convert.ConverterException;
import org.n52.sos.ds.AbstractGetObservationByIdDAO;
import org.n52.sos.ds.HibernateDatasourceConstants;
import org.n52.sos.ds.hibernate.dao.DaoFactory;
import org.n52.sos.ds.hibernate.dao.observation.series.SeriesObservationDAO;
import org.n52.sos.ds.hibernate.entities.observation.AbstractObservation;
import org.n52.sos.ds.hibernate.entities.observation.Observation;
import org.n52.sos.ds.hibernate.entities.observation.series.Series;
import org.n52.sos.ds.hibernate.util.HibernateGetObservationHelper;
import org.n52.sos.ds.hibernate.util.HibernateHelper;
import org.n52.sos.ds.hibernate.util.observation.HibernateObservationUtilities;
import org.n52.sos.ds.hibernate.values.HibernateStreamingConfiguration;
import org.n52.sos.ds.hibernate.values.series.HibernateChunkSeriesStreamingValue;
import org.n52.sos.ds.hibernate.values.series.HibernateScrollableSeriesStreamingValue;
import org.n52.sos.ds.hibernate.values.series.HibernateSeriesStreamingValue;
import org.n52.sos.exception.CodedException;
import org.n52.sos.exception.ows.NoApplicableCodeException;
import org.n52.sos.i18n.LocaleHelper;
import org.n52.sos.ogc.om.OmObservation;
import org.n52.sos.ogc.ows.OwsExceptionReport;
import org.n52.sos.ogc.sos.SosConstants;
import org.n52.sos.request.GetObservationByIdRequest;
import org.n52.sos.response.GetObservationByIdResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Implementation of the abstract class AbstractGetObservationByIdDAO
 *
 * @since 4.0.0
 *
 */
public class GetObservationByIdDAO extends AbstractGetObservationByIdDAO {

    private static final Logger LOGGER = LoggerFactory.getLogger(GetObservationByIdDAO.class);

    private final HibernateSessionHolder sessionHolder = new HibernateSessionHolder();

    /**
     * constructor
     */
    public GetObservationByIdDAO() {
        super(SosConstants.SOS);
    }

    @Override
    public String getDatasourceDaoIdentifier() {
        return HibernateDatasourceConstants.ORM_DATASOURCE_DAO_IDENTIFIER;
    }
    
    @Override
    public boolean isSupported() {
        return true;
    }

    @Override
    public GetObservationByIdResponse getObservationById(GetObservationByIdRequest request) throws OwsExceptionReport {
        Session session = null;
        try {
            session = sessionHolder.getSession();
            List<OmObservation> omObservations = Lists.newArrayList();
            if (DaoFactory.getInstance().isSeriesDAO()) {
                omObservations.addAll(querySeriesObservation(request, session));
            }
            List<Observation<?>> observations = Lists.newArrayList();
            observations.addAll(queryObservation(request, session));
            omObservations.addAll(HibernateObservationUtilities.createSosObservationsFromObservations(
                    checkObservations(observations, request), request, LocaleHelper.fromRequest(request), session));
            GetObservationByIdResponse response = new GetObservationByIdResponse();
            
            response.setService(request.getService());
            response.setVersion(request.getVersion());
            response.setResponseFormat(request.getResponseFormat());
            response.setResultModel(request.getResultModel());
            response.setObservationCollection(omObservations);
            return response;

        } catch (HibernateException he) {
            throw new NoApplicableCodeException().causedBy(he).withMessage("Error while querying observation data!");
        } catch (ConverterException ce) {
            throw new NoApplicableCodeException().causedBy(ce).withMessage("Error while processing observation data!");
        } finally {
            sessionHolder.returnSession(session);
        }
    }

    private List<OmObservation> querySeriesObservation(GetObservationByIdRequest request,
            Session session) throws OwsExceptionReport, ConverterException {
        List<OmObservation> observations = Lists.newArrayList();
        if (HibernateStreamingConfiguration.getInstance().isForceDatasourceStreaming()) {
            observations.addAll(querySeriesObservationForStreaming(request, session));
        } else {
            observations.addAll(querySeriesObservationForNonStreaming(request, session));
        }
        return observations;
    }

    private List<Observation<?>> checkObservations(List<Observation<?>> queryObservation, GetObservationByIdRequest request) {
        if (!request.isCheckForDuplicity()) {
            return queryObservation;
        }
        List<Observation<?>> checkedObservations = Lists.newArrayList();
        Set<String> identifiers = Sets.newHashSet();
        for (Observation<?> observation : queryObservation) {
            if (!identifiers.contains(observation.getIdentifier())) {
                identifiers.add(observation.getIdentifier());
                checkedObservations.add(observation);
            }
        }
        return checkedObservations;
    }

    /**
     * Query observations for observation identifiers
     *
     * @param request
     *            GetObservationById request
     * @param session
     *            Hibernate session
     * @return Resulting observations
     * @throws CodedException
     *             If an error occurs during querying the database
     */
    @SuppressWarnings("unchecked")
    private List<Observation<?>> queryObservation(GetObservationByIdRequest request, Session session)
            throws OwsExceptionReport {
        Criteria c =
                DaoFactory.getInstance().getObservationDAO()
                        .getObservationClassCriteriaForResultModel(request.getResultModel(), session);
        c.add(Restrictions.in(AbstractObservation.IDENTIFIER, request.getObservationIdentifier()));
        LOGGER.debug("QUERY queryObservation(request): {}", HibernateHelper.getSqlString(c));
        return c.list();
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
    protected List<OmObservation> querySeriesObservationForStreaming(GetObservationByIdRequest request,
            final Session session) throws OwsExceptionReport, ConverterException {
        final long start = System.currentTimeMillis();
        final List<OmObservation> result = new LinkedList<OmObservation>();
        // get valid featureOfInterest identifier
        List<Series> serieses = DaoFactory.getInstance().getSeriesDAO().getSeries(request, session);
        HibernateGetObservationHelper.checkMaxNumberOfReturnedSeriesSize(serieses.size());
        for (Series series : serieses) {
            Collection<? extends OmObservation> createSosObservationFromSeries =
                    HibernateObservationUtilities
                            .createSosObservationFromSeries(series, request, session);
            OmObservation observationTemplate = createSosObservationFromSeries.iterator().next();
            HibernateSeriesStreamingValue streamingValue = getSeriesStreamingValue(request, series.getSeriesId());
            streamingValue.setResponseFormat(request.getResponseFormat());
            streamingValue.setObservationTemplate(observationTemplate);
            observationTemplate.setValue(streamingValue);
            result.add(observationTemplate);
        }
        LOGGER.debug("Time to query observations needs {} ms!", (System.currentTimeMillis() - start));
        return result;
    }

    protected List<OmObservation> querySeriesObservationForNonStreaming(
            GetObservationByIdRequest request, Session session) throws OwsExceptionReport, ConverterException {
        final long start = System.currentTimeMillis();
        Collection<Observation<?>> seriesObservations = Lists.newArrayList();
        List<Series> serieses = DaoFactory.getInstance().getSeriesDAO().getSeries(request, session);
        HibernateGetObservationHelper.checkMaxNumberOfReturnedSeriesSize(serieses.size());
        SeriesObservationDAO observationDAO = (SeriesObservationDAO)DaoFactory.getInstance().getObservationDAO();
        for (Series series : serieses) {
           seriesObservations.addAll(observationDAO.getSeriesObservationFor(series, null, session));
        }
        final List<OmObservation> result = new LinkedList<OmObservation>();
        result.addAll(HibernateGetObservationHelper.toSosObservation(seriesObservations, request, session));
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
     * @throws CodedException 
     */
    private HibernateSeriesStreamingValue getSeriesStreamingValue(GetObservationByIdRequest request, long seriesId) throws CodedException {
        if (HibernateStreamingConfiguration.getInstance().isChunkDatasourceStreaming()) {
            return new HibernateChunkSeriesStreamingValue(request, seriesId, request.isCheckForDuplicity());
        } else {
            return new HibernateScrollableSeriesStreamingValue(request, seriesId, request.isCheckForDuplicity());
        }
    }

}
