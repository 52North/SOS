/**
 * Copyright (C) 2012-2016 52°North Initiative for Geospatial Open Source
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

import java.util.Collection;
import java.util.Iterator;

import org.hibernate.HibernateException;
import org.n52.sos.ds.hibernate.entities.observation.ValuedObservation;
import org.n52.sos.ds.hibernate.entities.observation.legacy.AbstractValuedLegacyObservation;
import org.n52.sos.exception.ows.NoApplicableCodeException;
import org.n52.sos.ogc.om.OmObservation;
import org.n52.sos.ogc.om.TimeValuePair;
import org.n52.sos.ogc.ows.OwsExceptionReport;
import org.n52.sos.request.GetObservationRequest;
import org.n52.sos.util.CollectionHelper;
import org.n52.sos.util.http.HTTPStatus;

/**
 * Hibernate streaming value implementation for chunk results
 *
 * @author Carsten Hollmann <c.hollmann@52north.org>
 * @since 4.1.0
 *
 */
public class HibernateChunkStreamingValue extends HibernateStreamingValue {

    private static final long serialVersionUID = -4898252375907510691L;

    private Iterator<ValuedObservation<?>> valuesResult;

    private int chunkSize;

    private int currentRow;

    private boolean noChunk = false;

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
    public HibernateChunkStreamingValue(GetObservationRequest request, long procedure, long observableProperty,
            long featureOfInterest) {
        super(request, procedure, observableProperty, featureOfInterest);
        this.chunkSize = HibernateStreamingConfiguration.getInstance().getChunkSize();
    }

    @Override
    public boolean hasNextValue() throws OwsExceptionReport {
        boolean next = false;
        if (valuesResult == null || !valuesResult.hasNext()) {
            if (!noChunk) {
                getNextResults();
                if (chunkSize <= 0) {
                    noChunk = true;
                }
            }
        }
        if (valuesResult != null) {
            next = valuesResult.hasNext();
        }
        if (!next) {
            sessionHolder.returnSession(session);
        }

        return next;
    }

    @Override
    public AbstractValuedLegacyObservation<?> nextEntity() throws OwsExceptionReport {
        return (AbstractValuedLegacyObservation<?>) valuesResult.next();
    }

    @Override
    public TimeValuePair nextValue() throws OwsExceptionReport {
        try {
            if (hasNextValue()) {
                ValuedObservation<?> resultObject = nextEntity();
                TimeValuePair value = createTimeValuePairFrom(resultObject);
                session.evict(resultObject);
                return value;
            }
            return null;
        } catch (final HibernateException he) {
            sessionHolder.returnSession(session);
            throw new NoApplicableCodeException().causedBy(he).withMessage("Error while querying observation data!")
                    .setStatus(HTTPStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Override
    public OmObservation nextSingleObservation() throws OwsExceptionReport {
        try {
            if (hasNextValue()) {
                OmObservation observation = observationTemplate.cloneTemplate();
                AbstractValuedLegacyObservation<?> resultObject = nextEntity();
                resultObject.addValuesToObservation(observation, getResponseFormat());
//                addValuesToObservation(observation, resultObject);
                checkForModifications(observation);
                session.evict(resultObject);
                return observation;
            }
            return null;
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
            final Collection<ValuedObservation<?>> valuesResult;
            if (temporalFilterCriterion != null) {
                valuesResult =
                        valueDAO.getStreamingValuesFor(request, procedure, observableProperty, featureOfInterest,
                                temporalFilterCriterion, chunkSize, currentRow, session);
            }
            // query without temporal or indeterminate filters
            else {
                valuesResult =
                        valueDAO.getStreamingValuesFor(request, procedure, observableProperty, featureOfInterest,
                                chunkSize, currentRow, session);
            }
            currentRow += chunkSize;
            setObservationValuesResult(valuesResult);
        } catch (final HibernateException he) {
            sessionHolder.returnSession(session);
            throw new NoApplicableCodeException().causedBy(he).withMessage("Error while querying observation data!")
                    .setStatus(HTTPStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Check the queried {@link AbstractValuedLegacyObservation}s for null and
     * set them as iterator to local variable.
     *
     * @param valuesResult
     *            Queried {@link AbstractValuedLegacyObservation}s
     */
    private void setObservationValuesResult(Collection<ValuedObservation<?>> valuesResult) {
        if (CollectionHelper.isNotEmpty(valuesResult)) {
            this.valuesResult = valuesResult.iterator();
        }

    }

}
