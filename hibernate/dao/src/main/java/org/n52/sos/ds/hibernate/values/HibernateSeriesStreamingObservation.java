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
package org.n52.sos.ds.hibernate.values;

import java.util.Set;

import org.hibernate.HibernateException;
import org.hibernate.ScrollableResults;
import org.hibernate.Session;
import org.hibernate.criterion.Criterion;
import org.n52.sos.convert.ConverterException;
import org.n52.sos.ds.hibernate.HibernateSessionHolder;
import org.n52.sos.ds.hibernate.dao.series.SeriesObservationDAO;
import org.n52.sos.ds.hibernate.entities.AbstractObservation;
import org.n52.sos.ds.hibernate.entities.series.Series;
import org.n52.sos.ds.hibernate.entities.series.SeriesObservation;
import org.n52.sos.ds.hibernate.util.HibernateGetObservationHelper;
import org.n52.sos.ds.hibernate.util.observation.HibernateObservationUtilities;
import org.n52.sos.exception.ows.NoApplicableCodeException;
import org.n52.sos.ogc.om.OmObservation;
import org.n52.sos.ogc.om.StreamingObservation;
import org.n52.sos.ogc.ows.OwsExceptionReport;
import org.n52.sos.request.GetObservationRequest;
import org.n52.sos.service.Configurator;
import org.n52.sos.util.http.HTTPStatus;

public class HibernateSeriesStreamingObservation extends StreamingObservation {
    
    private static final long serialVersionUID = 201732114914686926L;
    
    private final HibernateSessionHolder sessionHolder = new HibernateSessionHolder();
    
    private final SeriesObservationDAO seriesObservationDAO = new SeriesObservationDAO();
    
    private Session session;
    
    private ScrollableResults result;
    
    private GetObservationRequest request;

    private Set<String> features;

    private Criterion temporalFilterCriterion;
    
    private Set<Long> seriesIDs;
    
    private boolean showMetadataOfEmptyObservation = false;
    
    private boolean observationNotQueried = true;
    
    public HibernateSeriesStreamingObservation(GetObservationRequest request) {
        this.request = request;
        showMetadataOfEmptyObservation = Configurator.getInstance().getProfileHandler().getActiveProfile().isShowMetadataOfEmptyObservations();
    }

    @Override
    public boolean hasNextSingleObservation() throws OwsExceptionReport {
        boolean next = false;
        if (result == null ) {
            getNextScrollableResults();
            if (result != null ) {
                next = result.next();
            }
        } else {
            next = result.next();
            if (!next) {
                getNextScrollableResults();
                if (result != null) {
                    next = result.next();
                }
            }
        }
        if (!next) {
            sessionHolder.returnSession(session);
        }
        return next;
    }

    @Override
    public OmObservation nextSingleObservation() throws OwsExceptionReport {
        try {
            OmObservation observation;
            Object resultObject = result.get()[0];
            if (resultObject instanceof SeriesObservation) {
                observation = HibernateGetObservationHelper.toSosObservation(checkShowMetadtaOfEmptyObservations((SeriesObservation)result.get()[0]), request.getVersion(), request.getResultModel(), session);
            } else if (resultObject instanceof Series) {
                observation = HibernateObservationUtilities.createSosObservationFromSeries((Series)resultObject, request.getVersion(), session).iterator().next();
            } else {
                throw new NoApplicableCodeException().withMessage("The object {} is not supported", resultObject.getClass().getName()); 
            }
            session.evict(resultObject);
            return observation;
        } catch (final HibernateException he) {
            sessionHolder.returnSession(session);
            throw new NoApplicableCodeException().causedBy(he).withMessage("Error while querying observation data!")
                    .setStatus(HTTPStatus.INTERNAL_SERVER_ERROR);
        } catch (ConverterException ce) {
            sessionHolder.returnSession(session);
            throw new NoApplicableCodeException().causedBy(ce).withMessage("Error while processing observation data!")
                    .setStatus(HTTPStatus.INTERNAL_SERVER_ERROR);
        }
    }
    

    private AbstractObservation checkShowMetadtaOfEmptyObservations(SeriesObservation seriesObservation) {
        if (showMetadataOfEmptyObservation) {
            seriesIDs.add(seriesObservation.getSeries().getSeriesId());
        }
        return seriesObservation;
    }

    private void getNextScrollableResults() throws OwsExceptionReport {
        if (session == null) {
             session = sessionHolder.getSession();
        }
        try {
            if (observationNotQueried) {
                // query with temporal filter
                if (temporalFilterCriterion != null) {
                    setResult(seriesObservationDAO.getStreamingSeriesObservationsFor(request, features, temporalFilterCriterion, session));
                }
                // query without temporal or indeterminate filters
                else {
                    setResult(seriesObservationDAO.getStreamingSeriesObservationsFor(request, features, session));
                }
                observationNotQueried = false;
            }
            if (!observationNotQueried && showMetadataOfEmptyObservation) {
                if (temporalFilterCriterion != null) {
                    setResult(seriesObservationDAO.getSeriesNotMatchingSeries(seriesIDs ,request, features, temporalFilterCriterion, session));
                }
                // query without temporal or indeterminate filters
                else {
                    setResult(seriesObservationDAO.getSeriesNotMatchingSeries(seriesIDs, request, features, session));
                }
                
            }
        } catch (final HibernateException he) {
            sessionHolder.returnSession(session);
            throw new NoApplicableCodeException().causedBy(he).withMessage("Error while querying observation data!")
                    .setStatus(HTTPStatus.INTERNAL_SERVER_ERROR);
        }
    }

    private void setResult(ScrollableResults result) {
        this.result = result;
    }
    
    public void setValidFeatures(Set<String> features) {
        this.features = features;
    }

    public void setTemporalFilterCriterion(Criterion temporalFilterCriterion) {
        this.temporalFilterCriterion = temporalFilterCriterion;
    }


}
