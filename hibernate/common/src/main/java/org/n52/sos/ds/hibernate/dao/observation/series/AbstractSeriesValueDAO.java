/*
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
package org.n52.sos.ds.hibernate.dao.observation.series;

import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.n52.series.db.beans.DataEntity;
import org.n52.shetland.ogc.ows.exception.OwsExceptionReport;
import org.n52.shetland.ogc.sos.request.AbstractObservationRequest;
import org.n52.shetland.ogc.sos.request.GetObservationRequest;
import org.n52.sos.ds.hibernate.dao.DaoFactory;
import org.n52.sos.ds.hibernate.dao.observation.AbstractValueDAO;
import org.n52.sos.ds.hibernate.util.HibernateHelper;
import org.n52.sos.ds.hibernate.util.ResultFilterRestrictions;
import org.n52.sos.ds.hibernate.util.ResultFilterRestrictions.SubQueryIdentifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Abstract value data access object class for {@link SeriesValue}
 *
 * @author <a href="mailto:c.hollmann@52north.org">Carsten Hollmann</a>
 * @since 4.3.0
 *
 */
public abstract class AbstractSeriesValueDAO extends AbstractValueDAO {

    private static final Logger LOGGER = LoggerFactory.getLogger(AbstractSeriesValueDAO.class);

    private static final String QUERY_STREAMING_SERIES_VALUE = "QUERY getStreamingSeriesValuesFor({}): {}";

    public AbstractSeriesValueDAO(DaoFactory daoFactory) {
        super(daoFactory);
    }

    protected Class<?> getSeriesValueClass() {
        return DataEntity.class;
    }

    /**
     * Query streaming value for parameter as chunk {@link List}
     *
     * @param request
     *            {@link AbstractObservationRequest}
     * @param series
     *            Datasource series id
     * @param temporalFilterCriterion
     *            Temporal filter {@link Criterion}
     * @param chunkSize
     *            chunk size
     * @param currentRow
     *            Start row
     * @param session
     *            Hibernate Session
     * @return Resulting chunk {@link List}
     * @throws OwsExceptionReport
     *             If an error occurs when querying
     */
    @SuppressWarnings("unchecked")
    public List<DataEntity<?>> getStreamingSeriesValuesFor(AbstractObservationRequest request, long series,
            Criterion temporalFilterCriterion, int chunkSize, int currentRow, Session session)
            throws OwsExceptionReport {
        StringBuilder logArgs = new StringBuilder();
        Criteria c = getSeriesValueCriteriaFor(request, series, temporalFilterCriterion, session, logArgs);
        addChunkValuesToCriteria(c, chunkSize, currentRow, request, logArgs);
        LOGGER.trace(QUERY_STREAMING_SERIES_VALUE, logArgs.toString(), HibernateHelper.getSqlString(c));
        return (List<DataEntity<?>>) c.list();
    }

    /**
     * Query streaming value for parameter as chunk {@link List}
     *
     * @param request
     *            {@link AbstractObservationRequest}
     * @param series
     *            Datasource series id
     * @param temporalFilterCriterion
     *            Temporal filter {@link Criterion}
     * @param chunkSize
     *            chunk size
     * @param currentRow
     *            Start row
     * @param session
     *            Hibernate Session
     * @return Resulting chunk {@link List}
     * @throws OwsExceptionReport
     *             If an error occurs when querying
     */
    @SuppressWarnings("unchecked")
    public List<DataEntity<?>> getStreamingSeriesValuesFor(AbstractObservationRequest request, Set<Long> series,
            Criterion temporalFilterCriterion, int chunkSize, int currentRow, Session session)
            throws OwsExceptionReport {
        if (request instanceof GetObservationRequest && ((GetObservationRequest) request).hasResultFilter()) {
            StringBuilder logArgs = new StringBuilder();
            List<DataEntity<?>> list = new LinkedList<>();
            for (SubQueryIdentifier identifier : ResultFilterRestrictions
                    .getSubQueryIdentifier(getResultFilterClasses())) {
                Criteria c = getSeriesValueCriteriaFor(request, series, temporalFilterCriterion, session, logArgs);
                addChunkValuesToCriteria(c, chunkSize, currentRow, request, logArgs);
                checkAndAddResultFilterCriterion(c, (GetObservationRequest) request, identifier, session, logArgs);
                LOGGER.trace(QUERY_STREAMING_SERIES_VALUE, logArgs.toString(),
                        HibernateHelper.getSqlString(c));
                list.addAll(c.list());
            }
            return list;
        } else {
            StringBuilder logArgs = new StringBuilder();
            Criteria c = getSeriesValueCriteriaFor(request, series, temporalFilterCriterion, session, logArgs);
            addChunkValuesToCriteria(c, chunkSize, currentRow, request, logArgs);
            LOGGER.trace(QUERY_STREAMING_SERIES_VALUE, logArgs.toString(),
                    HibernateHelper.getSqlString(c));
            return (List<DataEntity<?>>) c.list();
        }
    }

    /**
     * Query streaming value for parameter as chunk {@link List}
     *
     * @param request
     *            {@link AbstractObservationRequest}
     * @param series
     *            Datasource series ids
     * @param chunkSize
     *            Chunk size
     * @param currentRow
     *            Start row
     * @param session
     *            Hibernate Session
     * @return Resulting chunk {@link List}
     * @throws OwsExceptionReport
     *             If an error occurs when querying
     */
    @SuppressWarnings("unchecked")
    public List<DataEntity<?>> getStreamingSeriesValuesFor(AbstractObservationRequest request, Set<Long> series,
            int chunkSize, int currentRow, Session session) throws OwsExceptionReport {
        if (request instanceof GetObservationRequest && ((GetObservationRequest) request).hasResultFilter()) {
            List<DataEntity<?>> list = new LinkedList<>();
            for (SubQueryIdentifier identifier : ResultFilterRestrictions
                    .getSubQueryIdentifier(getResultFilterClasses())) {
                StringBuilder logArgs = new StringBuilder();
                Criteria c = getSeriesValueCriteriaFor(request, series, null, session, logArgs);
                addChunkValuesToCriteria(c, chunkSize, currentRow, request, logArgs);
                checkAndAddResultFilterCriterion(c, (GetObservationRequest) request, identifier, session, logArgs);
                LOGGER.trace(QUERY_STREAMING_SERIES_VALUE, logArgs.toString(),
                        HibernateHelper.getSqlString(c));
                list.addAll(c.list());
            }
            return list;
        } else {
            StringBuilder logArgs = new StringBuilder();
            Criteria c = getSeriesValueCriteriaFor(request, series, null, session, logArgs);
            addChunkValuesToCriteria(c, chunkSize, currentRow, request, logArgs);
            LOGGER.trace(QUERY_STREAMING_SERIES_VALUE, logArgs.toString(),
                    HibernateHelper.getSqlString(c));
            return (List<DataEntity<?>>) c.list();
        }
    }

    /**
     * Query streaming value for parameter as chunk {@link List}
     *
     * @param request
     *            {@link AbstractObservationRequest}
     * @param series
     *            Datasource series id
     * @param chunkSize
     *            Chunk size
     * @param currentRow
     *            Start row
     * @param session
     *            Hibernate Session
     * @return Resulting chunk {@link List}
     * @throws OwsExceptionReport
     *             If an error occurs when querying
     */
    @SuppressWarnings("unchecked")
    public List<DataEntity<?>> getStreamingSeriesValuesFor(AbstractObservationRequest request, long series,
            int chunkSize, int currentRow, Session session) throws OwsExceptionReport {
        if (request instanceof GetObservationRequest && ((GetObservationRequest) request).hasResultFilter()) {
            StringBuilder logArgs = new StringBuilder();
            List<DataEntity<?>> list = new LinkedList<>();
            for (SubQueryIdentifier identifier : ResultFilterRestrictions
                    .getSubQueryIdentifier(getResultFilterClasses())) {
                Criteria c = getSeriesValueCriteriaFor(request, series, null, session, logArgs);
                addChunkValuesToCriteria(c, chunkSize, currentRow, request, logArgs);
                checkAndAddResultFilterCriterion(c, (GetObservationRequest) request, identifier, session, logArgs);
                LOGGER.trace(QUERY_STREAMING_SERIES_VALUE, logArgs.toString(),
                        HibernateHelper.getSqlString(c));
                list.addAll(c.list());
            }
            return list;
        } else {
            StringBuilder logArgs = new StringBuilder();
            Criteria c = getSeriesValueCriteriaFor(request, series, null, session, logArgs);
            addChunkValuesToCriteria(c, chunkSize, currentRow, request, logArgs);
            LOGGER.trace(QUERY_STREAMING_SERIES_VALUE, logArgs.toString(),
                    HibernateHelper.getSqlString(c));
            return (List<DataEntity<?>>) c.list();
        }
    }

    /**
     * Get {@link Criteria} for parameter
     *
     * @param request
     *            {@link AbstractObservationRequest}
     * @param series
     *            Datasource series id
     * @param temporalFilterCriterion
     *            Temporal filter {@link Criterion}
     * @param session
     *            Hibernate Session
     * @param logArgs log arguments
     * @return Resulting {@link Criteria}
     * @throws OwsExceptionReport
     *             If an error occurs when adding Spatial Filtering Profile
     *             restrictions
     */
    private Criteria getSeriesValueCriteriaFor(AbstractObservationRequest request, long series,
            Criterion temporalFilterCriterion, Session session, StringBuilder logArgs) throws OwsExceptionReport {
        final Criteria c = getDefaultSeriesValueCriteriaFor(request, temporalFilterCriterion, session, logArgs);
        c.add(Restrictions.eq(DataEntity.PROPERTY_DATASET_ID, series));
        return c.setReadOnly(true);
    }

    /**
     * Get {@link Criteria} for parameter
     *
     * @param request
     *            {@link AbstractObservationRequest}
     * @param series
     *            Datasource series ids
     * @param temporalFilterCriterion
     *            Temporal filter {@link Criterion}
     * @param session
     *            Hibernate Session
     * @param logArgs log arguments
     * @return Resulting {@link Criteria}
     * @throws OwsExceptionReport
     *             If an error occurs when adding Spatial Filtering Profile
     *             restrictions
     */
    private Criteria getSeriesValueCriteriaFor(AbstractObservationRequest request, Set<Long> series,
            Criterion temporalFilterCriterion, Session session, StringBuilder logArgs) throws OwsExceptionReport {
        final Criteria c = getDefaultSeriesValueCriteriaFor(request, temporalFilterCriterion, session, logArgs);
        c.add(Restrictions.in(DataEntity.PROPERTY_DATASET_ID, series));
        return c.setReadOnly(true);
    }

    private Criteria getDefaultSeriesValueCriteriaFor(AbstractObservationRequest request,
            Criterion temporalFilterCriterion, Session session, StringBuilder logArgs) throws OwsExceptionReport {
        final Criteria c = getDefaultObservationCriteria(session);
        c.addOrder(Order.asc(getOrderColumn(request)));
        logArgs.append("request, series");
        if (request instanceof GetObservationRequest) {
            GetObservationRequest getObsReq = (GetObservationRequest) request;
            checkAndAddSpatialFilteringProfileCriterion(c, getObsReq, session, logArgs);

            if (temporalFilterCriterion != null) {
                logArgs.append(", filterCriterion");
                c.add(temporalFilterCriterion);
            }
            addSpecificRestrictions(c, getObsReq, logArgs);
        }
        return c.setReadOnly(true);
    }

    /**
     * Get default {@link Criteria} for {@link Class}
     *
     * @param session
     *            Hibernate Session
     * @return Default {@link Criteria}
     */
    protected Criteria getDefaultObservationCriteria(Session session) {
        return getDefaultCriteria(getSeriesValueClass(), session);
        // return
        // session.createCriteria(getSeriesValueClass()).add(Restrictions.eq(AbstractValuedSeriesObservation.DELETED,
        // false))
        // .setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY);
    }

}
