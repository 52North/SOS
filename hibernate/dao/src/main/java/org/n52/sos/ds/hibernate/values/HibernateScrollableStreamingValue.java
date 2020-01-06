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
package org.n52.sos.ds.hibernate.values;

import org.hibernate.HibernateException;
import org.hibernate.ScrollableResults;
import org.n52.sos.ds.hibernate.entities.observation.legacy.AbstractValuedLegacyObservation;
import org.n52.sos.exception.ows.NoApplicableCodeException;
import org.n52.sos.ogc.om.OmObservation;
import org.n52.sos.ogc.om.TimeValuePair;
import org.n52.sos.ogc.ows.OwsExceptionReport;
import org.n52.sos.request.GetObservationRequest;
import org.n52.sos.util.http.HTTPStatus;

/**
 * Hibernate streaming value implementation for {@link ScrollableResults}
 *
 * @author Carsten Hollmann <c.hollmann@52north.org>
 * @since 4.1.0
 */
public class HibernateScrollableStreamingValue extends HibernateStreamingValue {

    private static final long serialVersionUID = -1113871324524260053L;

    private ScrollableResults scrollableResult;

    /**
     * constructor
     *
     * @param request
     *            {@link GetObservationRequest}
     * @param procedure
     *            Datasource procedure id
     * @param observableProperty
     *            Datasource observableProperty id
     * @param featureOfInterest
     *            Datasource featureOfInterest id
     */
    public HibernateScrollableStreamingValue(GetObservationRequest request, long procedure, long observableProperty,
            long featureOfInterest) {
        super(request, procedure, observableProperty, featureOfInterest);
    }

    @Override
    public boolean hasNextValue() throws OwsExceptionReport {
        boolean next = false;
        if (scrollableResult == null) {
            getNextResults();
            next = scrollableResult != null;
        } else {
            next = scrollableResult.next();
        }
        if (!next) {
            sessionHolder.returnSession(getSession());
        }
        return next;
    }

    @Override
    public AbstractValuedLegacyObservation<?> nextEntity() throws OwsExceptionReport {
        return (AbstractValuedLegacyObservation<?>) scrollableResult.get()[0];
    }

    @Override
    public TimeValuePair nextValue() throws OwsExceptionReport {
        try {
            AbstractValuedLegacyObservation<?> resultObject = nextEntity();
            TimeValuePair value = createTimeValuePairFrom(resultObject);
            getSession().evict(resultObject);
            return value;
        } catch (final HibernateException he) {
            sessionHolder.returnSession(getSession());
            throw new NoApplicableCodeException().causedBy(he).withMessage("Error while querying observation data!")
                    .setStatus(HTTPStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Override
    public OmObservation nextSingleObservation(boolean withIdentifierNameDesription) throws OwsExceptionReport {
        try {
            OmObservation observation = observationTemplate.cloneTemplate(withIdentifierNameDesription);
            AbstractValuedLegacyObservation<?> resultObject = nextEntity();
            resultObject.addValuesToObservation(observation, getResponseFormat());
//            addValuesToObservation(observation, resultObject);
//            if (resultObject.hasSamplingGeometry()) {
//                observation.addParameter(createSpatialFilteringProfileParameter(resultObject.getSamplingGeometry()));
//            }
            checkForModifications(observation);
            getSession().evict(resultObject);
            return observation;
        } catch (final HibernateException he) {
            sessionHolder.returnSession(getSession());
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
        try {
            if (request instanceof GetObservationRequest) {
                GetObservationRequest getObsReq = (GetObservationRequest)request;
                // query with temporal filter
                if (temporalFilterCriterion != null) {
                    setScrollableResult(valueDAO.getStreamingValuesFor(getObsReq, procedure, observableProperty,
                            featureOfInterest, temporalFilterCriterion, getSession()));
                }
                // query without temporal or indeterminate filters
                else {
                    setScrollableResult(valueDAO.getStreamingValuesFor(getObsReq, procedure, observableProperty,
                            featureOfInterest, getSession()));
                }
            }
        } catch (final HibernateException he) {
            sessionHolder.returnSession(getSession());
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
