/**
 * Copyright (C) 2012-2015 52°North Initiative for Geospatial Open Source
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
package org.n52.sos.ds.hibernate.values.series;

import java.util.Set;

import org.hibernate.HibernateException;
import org.n52.sos.ds.hibernate.dao.AbstractObservationDAO;
import org.n52.sos.ds.hibernate.dao.DaoFactory;
import org.n52.sos.ds.hibernate.dao.series.AbstractSeriesObservationDAO;
import org.n52.sos.ds.hibernate.entities.AbstractObservation;
import org.n52.sos.ds.hibernate.entities.series.SeriesObservation;
import org.n52.sos.ds.hibernate.values.AbstractHibernateStreamingObservation;
import org.n52.sos.exception.ows.NoApplicableCodeException;
import org.n52.sos.ogc.ows.OwsExceptionReport;
import org.n52.sos.request.GetObservationRequest;
import org.n52.sos.util.http.HTTPStatus;

/**
 * Streaming observation class for series concept
 * 
 * @author Carsten Hollmann <c.hollmann@52north.org>
 * @since 4.0.2
 *
 */
public class HibernateSeriesStreamingObservation extends AbstractHibernateStreamingObservation {

    private static final long serialVersionUID = 201732114914686926L;

    private final AbstractSeriesObservationDAO seriesObservationDAO;

    private Set<Long> seriesIDs;

    /**
     * constructor
     * 
     * @param request
     *            {@link GetObservationRequest}
     * @throws OwsExceptionReport 
     */
    public HibernateSeriesStreamingObservation(GetObservationRequest request) throws OwsExceptionReport {
        super(request);
        AbstractObservationDAO observationDAO = DaoFactory.getInstance().getObservationDAO();
        if (observationDAO instanceof AbstractSeriesObservationDAO) {
            seriesObservationDAO = (AbstractSeriesObservationDAO) observationDAO;
        } else {
            throw new NoApplicableCodeException().withMessage("The required '%s' implementation is no supported!",
                    AbstractObservationDAO.class.getName());
        }
    }

    @Override
    protected AbstractObservation checkShowMetadtaOfEmptyObservations(AbstractObservation abstractObservation) {
        if (showMetadataOfEmptyObservation) {
            if (abstractObservation instanceof SeriesObservation) {
                seriesIDs.add(((SeriesObservation) abstractObservation).getSeries().getSeriesId());
            }
        }
        return abstractObservation;
    }

    @Override
    protected void getNextScrollableResults() throws OwsExceptionReport {
        if (session == null) {
            session = sessionHolder.getSession();
        }
        try {
            if (observationNotQueried) {
                // query with temporal filter
                if (temporalFilterCriterion != null) {
                    setResult(seriesObservationDAO.getStreamingSeriesObservationsFor(request, features,
                            temporalFilterCriterion, session));
                }
                // query without temporal or indeterminate filters
                else {
                    setResult(seriesObservationDAO.getStreamingSeriesObservationsFor(request, features, session));
                }
                observationNotQueried = false;
            }
            if (!observationNotQueried && showMetadataOfEmptyObservation) {
                if (temporalFilterCriterion != null) {
                    setResult(seriesObservationDAO.getSeriesNotMatchingSeries(seriesIDs, request, features,
                            temporalFilterCriterion, session));
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

}
