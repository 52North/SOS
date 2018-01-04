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
package org.n52.sos.ds.hibernate.values.series;

import java.util.Collection;
import java.util.Iterator;

import org.hibernate.HibernateException;

import org.n52.iceland.ds.ConnectionProvider;
import org.n52.janmayen.http.HTTPStatus;
import org.n52.shetland.ogc.om.OmObservation;
import org.n52.shetland.ogc.om.TimeValuePair;
import org.n52.shetland.ogc.ows.exception.CodedException;
import org.n52.shetland.ogc.ows.exception.NoApplicableCodeException;
import org.n52.shetland.ogc.ows.exception.OwsExceptionReport;
import org.n52.shetland.ogc.sos.request.GetObservationRequest;
import org.n52.shetland.util.CollectionHelper;
import org.n52.sos.ds.hibernate.dao.DaoFactory;
import org.n52.sos.ds.hibernate.entities.observation.legacy.AbstractValuedLegacyObservation;
import org.n52.sos.ds.hibernate.values.HibernateStreamingConfiguration;

/**
 * Hibernate series streaming value implementation for chunk results
 *
 * @author <a href="mailto:c.hollmann@52north.org">Carsten Hollmann</a>
 * @since 4.0.2
 *
 */
public class HibernateChunkSeriesStreamingValue extends HibernateSeriesStreamingValue {

    private Iterator<AbstractValuedLegacyObservation<?>> seriesValuesResult;

    private int chunkSize;

    private int currentRow;

    private boolean noChunk = false;
    
    private int valueCounter = 0;

    private int currentResultSize = 0;

    /**
     * constructor
     *
     * @param connectionProvider the connection provider
     * @param daoFactory the DAO factory
     * @param request
     *            {@link GetObservationRequest}
     * @param series
     *            Datasource series id
     * @param duplicated 
     * @throws CodedException
     */
    public HibernateChunkSeriesStreamingValue(ConnectionProvider connectionProvider, DaoFactory daoFactory, GetObservationRequest request, long series, boolean duplicated) throws OwsExceptionReport {
        super(connectionProvider, daoFactory, request, series, duplicated);
        this.chunkSize = HibernateStreamingConfiguration.getInstance().getChunkSize();
    }

    @Override
    public boolean hasNext() throws OwsExceptionReport {
        boolean next = false;
        if (seriesValuesResult == null || !seriesValuesResult.hasNext()) {
            if (!noChunk && (valueCounter == 0 || valueCounter == chunkSize)) {
                getNextResults();
                if (chunkSize <= 0 || (valueCounter != 0 && valueCounter < chunkSize)) {
                    noChunk = true;
                } else {
                    valueCounter = 0;
                }
            }
        }
        if (seriesValuesResult != null) {
            next = seriesValuesResult.hasNext();
        }
        if (!next) {
            sessionHolder.returnSession(session);
        }


        return next;
    }

    @Override
    public AbstractValuedLegacyObservation<?> nextEntity() throws OwsExceptionReport {
        AbstractValuedLegacyObservation<?> resultObject = (AbstractValuedLegacyObservation<?>) getNextValue();;
        if (checkValue(resultObject)) {
            return resultObject;
        }
        session.evict(resultObject);
        return null;
    }

    @Override
    public TimeValuePair nextValue() throws OwsExceptionReport {
        try {
            if (hasNext()) {
                AbstractValuedLegacyObservation<?> resultObject = getNextValue();
                TimeValuePair value = null;
                if (checkValue(resultObject)) {
                    value = resultObject.createTimeValuePairFrom();
                }
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
    public OmObservation next(boolean withIdentifierNameDesription) throws OwsExceptionReport {
        try {
            if (hasNext()) {
                OmObservation observation = getObservationTemplate().cloneTemplate();
                AbstractValuedLegacyObservation<?> resultObject = getNextValue();
                if (checkValue(resultObject)) {
                    observation = observationTemplate.cloneTemplate(withIdentifierNameDesription);
                    resultObject.addValuesToObservation(observation, getResponseFormat());
                    checkForModifications(observation);
                }
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
    
    private AbstractValuedLegacyObservation<?> getNextValue() {
        valueCounter++;
        return seriesValuesResult.next();
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
            Collection<AbstractValuedLegacyObservation<?>> seriesValuesResult;
            if (temporalFilterCriterion != null) {
                seriesValuesResult =
                        seriesValueDAO.getStreamingSeriesValuesFor(request, series, temporalFilterCriterion,
                                chunkSize, currentRow, session);
            }
            // query without temporal or indeterminate filters
            else {
                seriesValuesResult =
                        seriesValueDAO.getStreamingSeriesValuesFor(request, series, chunkSize, currentRow, session);
            }
            currentRow += chunkSize;
            checkMaxNumberOfReturnedValues(seriesValuesResult.size());
            setSeriesValuesResult(seriesValuesResult);
        } catch (final HibernateException he) {
            sessionHolder.returnSession(session);
            throw new NoApplicableCodeException().causedBy(he).withMessage("Error while querying observation data!")
                    .setStatus(HTTPStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Check the queried {@link AbstractValuedLegacyObservation}s for null and set them as
     * iterator to local variable.
     *
     * @param seriesValuesResult
     *            Queried {@link AbstractValuedLegacyObservation}s
     */
    private void setSeriesValuesResult(Collection<AbstractValuedLegacyObservation<?>> seriesValuesResult) {
        if (CollectionHelper.isNotEmpty(seriesValuesResult)) {
            this.seriesValuesResult = seriesValuesResult.iterator();
        }
    }

}
