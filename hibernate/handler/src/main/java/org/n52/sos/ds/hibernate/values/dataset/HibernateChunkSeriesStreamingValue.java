/*
 * Copyright (C) 2012-2021 52°North Spatial Information Research GmbH
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
package org.n52.sos.ds.hibernate.values.dataset;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.n52.iceland.ds.ConnectionProvider;
import org.n52.janmayen.http.HTTPStatus;
import org.n52.series.db.beans.DataEntity;
import org.n52.series.db.beans.DatasetEntity;
import org.n52.series.db.beans.TrajectoryDataEntity;
import org.n52.series.db.beans.dataset.DatasetType;
import org.n52.series.db.beans.dataset.ObservationType;
import org.n52.shetland.ogc.om.OmObservation;
import org.n52.shetland.ogc.om.TimeValuePair;
import org.n52.shetland.ogc.ows.exception.CodedException;
import org.n52.shetland.ogc.ows.exception.NoApplicableCodeException;
import org.n52.shetland.ogc.ows.exception.OwsExceptionReport;
import org.n52.shetland.ogc.sos.request.AbstractObservationRequest;
import org.n52.shetland.util.CollectionHelper;
import org.n52.sos.ds.hibernate.dao.DaoFactory;

/**
 * Hibernate dataset streaming value implementation for chunk results
 *
 * @author <a href="mailto:c.hollmann@52north.org">Carsten Hollmann</a>
 * @since 4.0.2
 *
 */
public class HibernateChunkSeriesStreamingValue extends HibernateSeriesStreamingValue {

    private static final String ERROR_LOG = "Error while querying observation data!";

    private Iterator<DataEntity<?>> seriesValuesResult;

    private int chunkSize;

    private int currentRow;

    private boolean noChunk;

    private int currentResultSize;

    /**
     * constructor
     *
     * @param connectionProvider
     *            the connection provider
     * @param daoFactory
     *            the DAO factory
     * @param request
     *            {@link AbstractObservationRequest}
     * @param dataset
     *            Datasource dataset id
     * @param chunkSize
     *            size of the chunk
     * @throws CodedException
     *             If an error occurs
     */
    public HibernateChunkSeriesStreamingValue(ConnectionProvider connectionProvider, DaoFactory daoFactory,
            AbstractObservationRequest request, DatasetEntity dataset, int chunkSize) throws OwsExceptionReport {
        super(connectionProvider, daoFactory, request, dataset);
        this.chunkSize = chunkSize;
    }

    @Override
    public boolean hasNext() throws OwsExceptionReport {
        boolean next = false;
        if ((seriesValuesResult == null || !seriesValuesResult.hasNext()) && getSession().isOpen()) {
            if (!noChunk) {
                getNextResults();
                if (chunkSize <= 0 || currentResultSize < chunkSize) {
                    noChunk = true;
                }
            }
        }
        if (seriesValuesResult != null) {
            next = seriesValuesResult.hasNext();
        }
        if (!next) {
            returnSession(getSession());
        }

        return next;
    }

    @Override
    public DataEntity<?> nextEntity() throws OwsExceptionReport {
        return (DataEntity<?>) seriesValuesResult.next();
    }

    @Override
    public TimeValuePair nextValue() throws OwsExceptionReport {
        try {
            if (hasNext()) {
                DataEntity<?> resultObject = seriesValuesResult.next();
                TimeValuePair value = getDaoFactory().getObservationHelper().createTimeValuePairFrom(resultObject);
                getSession().evict(resultObject);
                return value;
            }
            return null;
        } catch (final HibernateException he) {
            returnSession(getSession());
            throw new NoApplicableCodeException().causedBy(he)
                    .withMessage(ERROR_LOG)
                    .setStatus(HTTPStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Override
    public OmObservation next() throws OwsExceptionReport {
        try {
            if (hasNext()) {
                OmObservation observation = getObservationTemplate().cloneTemplate();
                DataEntity<?> resultObject = seriesValuesResult.next();
                getObservationHelper().addValuesToObservation(resultObject, observation, getResponseFormat());
                checkForModifications(observation);
                getSession().evict(resultObject);
                return observation;
            }
            return null;
        } catch (final HibernateException he) {
            returnSession(getSession());
            throw new NoApplicableCodeException().causedBy(he)
                    .withMessage(ERROR_LOG)
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
        Session session = null;
        try {
            session = getSession();
            // query with temporal filter
            Collection<DataEntity<?>> resutltValues = new ArrayList<>();
            if (temporalFilterCriterion != null) {
                resutltValues.addAll(seriesValueDAO.getStreamingSeriesValuesFor(request, series,
                        temporalFilterCriterion, chunkSize, currentRow, session));
            } else {
                // query without temporal or indeterminate filters
                resutltValues.addAll(seriesValueDAO.getStreamingSeriesValuesFor(request, series, chunkSize, currentRow,
                        getSession()));
            }
            currentRow += chunkSize;
            if (DatasetType.trajectory.equals(dataset.getDatasetType())
                    || ObservationType.trajectory.equals(dataset.getObservationType())) {
                List<DataEntity<?>> list = new LinkedList<>();
                for (DataEntity<?> dataEntity : resutltValues) {
                    if (dataEntity instanceof TrajectoryDataEntity) {
                        list.addAll(((TrajectoryDataEntity) dataEntity).getValue());
                    } else {
                        list.add(dataEntity);
                    }
                }
                resutltValues = list;
            }
            checkMaxNumberOfReturnedValues(resutltValues.size());
            setSeriesValuesResult(resutltValues);
        } catch (final HibernateException he) {
            returnSession(session);
            throw new NoApplicableCodeException().causedBy(he)
                    .withMessage(ERROR_LOG)
                    .setStatus(HTTPStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Check the queried {@link DataEntity}s for null and set them as iterator
     * to local variable.
     *
     * @param seriesValuesResult
     *            Queried {@link DataEntity}s
     */
    private void setSeriesValuesResult(Collection<DataEntity<?>> seriesValuesResult) {
        if (CollectionHelper.isNotEmpty(seriesValuesResult)) {
            this.currentResultSize = seriesValuesResult.size();
            this.seriesValuesResult = seriesValuesResult.iterator();
        }

    }

}
