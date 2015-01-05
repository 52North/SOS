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

import org.hibernate.HibernateException;
import org.hibernate.ScrollableResults;
import org.n52.sos.ds.hibernate.entities.series.values.SeriesValue;
import org.n52.sos.exception.CodedException;
import org.n52.sos.exception.ows.NoApplicableCodeException;
import org.n52.sos.ogc.om.OmObservation;
import org.n52.sos.ogc.om.TimeValuePair;
import org.n52.sos.ogc.ows.OwsExceptionReport;
import org.n52.sos.request.GetObservationRequest;
import org.n52.sos.util.http.HTTPStatus;

/**
 * Hibernate series streaming value implementation for {@link ScrollableResults}
 * 
 * @author Carsten Hollmann <c.hollmann@52north.org>
 * @since 4.0.2
 *
 */
public class HibernateScrollableSeriesStreamingValue extends HibernateSeriesStreamingValue {

    private static final long serialVersionUID = -6439122088572009613L;

    private ScrollableResults scrollableResult;

    /**
     * constructor
     * 
     * @param request
     *            {@link GetObservationRequest}
     * @param series
     *            Datasource series id
     * @throws CodedException 
     */
    public HibernateScrollableSeriesStreamingValue(GetObservationRequest request, long series) throws CodedException {
        super(request, series);
    }

    @Override
    public boolean hasNextValue() throws OwsExceptionReport {
        boolean next = false;
        if (scrollableResult == null) {
            getNextResults();
            if (scrollableResult != null) {
                next = scrollableResult.next();
            }
        } else {
            next = scrollableResult.next();
        }
        if (!next) {
            sessionHolder.returnSession(session);
        }
        return next;
    }

    @Override
    public SeriesValue nextEntity() throws OwsExceptionReport {
        checkMaxNumberOfReturnedValues(1);
        return (SeriesValue) scrollableResult.get()[0];
    }

    @Override
    public TimeValuePair nextValue() throws OwsExceptionReport {
        try {
            SeriesValue resultObject = nextEntity();
            TimeValuePair value = resultObject.createTimeValuePairFrom();
            session.evict(resultObject);
            return value;
        } catch (final HibernateException he) {
            sessionHolder.returnSession(session);
            throw new NoApplicableCodeException().causedBy(he).withMessage("Error while querying observation data!")
                    .setStatus(HTTPStatus.INTERNAL_SERVER_ERROR);
        }
    }
    
    @Override
    public OmObservation nextSingleObservation() throws OwsExceptionReport {
        try {
            OmObservation observation = observationTemplate.cloneTemplate();
            SeriesValue resultObject = nextEntity();
            resultObject.addValuesToObservation(observation, getResponseFormat());
            checkForModifications(observation);
            session.evict(resultObject);
            return observation;
        } catch (final HibernateException he) {
            sessionHolder.returnSession(session);
            throw new NoApplicableCodeException().causedBy(he).withMessage("Error while querying observation data!")
                    .setStatus(HTTPStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Get the next results from database
     * 
     * @throws OwsExceptionReport
     *             If an error occurs when querying the next results
     */
    private void getNextResults() throws OwsExceptionReport {
        if (session == null) {
            session = sessionHolder.getSession();
        }
        try {
            // query with temporal filter
            if (temporalFilterCriterion != null) {
                setScrollableResult(seriesValueDAO.getStreamingSeriesValuesFor(request, series,
                        temporalFilterCriterion, session));
            }
            // query without temporal or indeterminate filters
            else {
                setScrollableResult(seriesValueDAO.getStreamingSeriesValuesFor(request, series, session));
            }
        } catch (final HibernateException he) {
            sessionHolder.returnSession(session);
            throw new NoApplicableCodeException().causedBy(he).withMessage("Error while querying observation data!")
                    .setStatus(HTTPStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Set the queried {@link ScrollableResults} to local variable
     * 
     * @param scrollableResult
     *            Queried {@link ScrollableResults}
     */
    private void setScrollableResult(ScrollableResults scrollableResult) {
        this.scrollableResult = scrollableResult;
    }

}
