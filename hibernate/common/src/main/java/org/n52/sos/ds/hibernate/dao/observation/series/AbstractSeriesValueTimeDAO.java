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

import java.util.Collection;
import java.util.Set;
import java.util.stream.Collectors;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.ProjectionList;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.n52.series.db.beans.DataEntity;
import org.n52.series.db.beans.DatasetEntity;
import org.n52.shetland.ogc.gml.time.IndeterminateValue;
import org.n52.shetland.ogc.ows.exception.OwsExceptionReport;
import org.n52.shetland.ogc.sos.ExtendedIndeterminateTime;
import org.n52.shetland.ogc.sos.request.AbstractObservationRequest;
import org.n52.shetland.ogc.sos.request.GetObservationRequest;
import org.n52.shetland.util.DateTimeHelper;
import org.n52.sos.ds.hibernate.dao.DaoFactory;
import org.n52.sos.ds.hibernate.dao.observation.AbstractValueTimeDAO;
import org.n52.sos.ds.hibernate.util.HibernateHelper;
import org.n52.sos.ds.hibernate.util.ObservationTimeExtrema;
import org.n52.sos.ds.hibernate.util.QueryHelper;
import org.n52.sos.ds.hibernate.util.ResultFilterRestrictions;
import org.n52.sos.ds.hibernate.util.ResultFilterRestrictions.SubQueryIdentifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Abstract value time data access object class for {@link DataEntity}
 *
 * @author <a href="mailto:c.hollmann@52north.org">Carsten Hollmann</a>
 * @since 4.3.0
 *
 */
public abstract class AbstractSeriesValueTimeDAO extends AbstractValueTimeDAO {

    private static final Logger LOGGER = LoggerFactory.getLogger(AbstractSeriesValueTimeDAO.class);

    private static final String LOG_ARGS_REQUEST_SERIES = "request, series";

    private static final String LOG_QUERY_TIME_EXTREMA =
            "QUERY getTimeExtremaForSeries(request, series, temporalFilter): {}";

    private static final String LOG_QUERY_DATA_ENTITY = "QUERY getDataEntityFor({}): {}";

    public AbstractSeriesValueTimeDAO(DaoFactory daoFactory) {
        super(daoFactory);
    }

    /**
     * Get the concrete {@link DataEntity} class.
     *
     * @return The concrete {@link DataEntity} class
     */
    protected Class<?> getSeriesValueTimeClass() {
        return DataEntity.class;
    }

    /**
     * Get {@link ObservationTimeExtrema} for a {@link DataEntity} with temporal
     * filter.
     *
     * @param request
     *            {@link AbstractObservationRequest} request
     * @param series
     *            {@link DataEntity} to get time extrema for
     * @param temporalFilterCriterion
     *            Temporal filter
     * @param session
     *            Hibernate session
     * @return Time extrema for {@link DataEntity}
     * @throws OwsExceptionReport
     *             If an error occurs
     */
    public ObservationTimeExtrema getTimeExtremaForSeries(AbstractObservationRequest request, long series,
            Criterion temporalFilterCriterion, Session session) throws OwsExceptionReport {
        if (request instanceof GetObservationRequest && ((GetObservationRequest) request).hasResultFilter()) {
            ObservationTimeExtrema ote = new ObservationTimeExtrema();
            for (SubQueryIdentifier identifier : ResultFilterRestrictions
                    .getSubQueryIdentifier(getResultFilterClasses())) {
                Criteria c = getSeriesValueCriteriaFor(request, series, temporalFilterCriterion, null, session);
                checkAndAddResultFilterCriterion(c, (GetObservationRequest) request, identifier, session,
                        new StringBuilder());
                addMinMaxTimeProjection(c);
                ote.expand(parseMinMaxTime((Object[]) c.uniqueResult()));
            }
            return ote;
        } else {
            Criteria c = getSeriesValueCriteriaFor(request, series, temporalFilterCriterion, null, session);
            addMinMaxTimeProjection(c);
            LOGGER.trace(LOG_QUERY_TIME_EXTREMA,
                    HibernateHelper.getSqlString(c));
            return parseMinMaxTime((Object[]) c.uniqueResult());
        }
    }

    /**
     * Get {@link ObservationTimeExtrema} for a dataset id with temporal
     * filter.
     *
     * @param request
     *            {@link AbstractObservationRequest} request
     * @param series
     *            {@link Set} of dataset ids to get time extrema for
     * @param temporalFilterCriterion
     *            Temporal filter
     * @param session
     *            Hibernate session
     * @return Time extrema for dataset ids
     * @throws OwsExceptionReport
     *             If an error occurs
     */
    public ObservationTimeExtrema getTimeExtremaForSeries(AbstractObservationRequest request, Set<Long> series,
            Criterion temporalFilterCriterion, Session session) throws OwsExceptionReport {
        if (request instanceof GetObservationRequest && ((GetObservationRequest) request).hasResultFilter()) {
            ObservationTimeExtrema ote = new ObservationTimeExtrema();
            for (SubQueryIdentifier identifier : ResultFilterRestrictions
                    .getSubQueryIdentifier(getResultFilterClasses())) {
                Criteria c = getSeriesValueCriteriaFor(request, series, temporalFilterCriterion, null, session);
                checkAndAddResultFilterCriterion(c, (GetObservationRequest) request, identifier, session,
                        new StringBuilder());
                addMinMaxTimeProjection(c);
                ote.expand(parseMinMaxTime((Object[]) c.uniqueResult()));
            }
            return ote;
        } else {
            Criteria c = getSeriesValueCriteriaFor(request, series, temporalFilterCriterion, null, session);
            addMinMaxTimeProjection(c);
            LOGGER.trace(LOG_QUERY_TIME_EXTREMA,
                    HibernateHelper.getSqlString(c));
            return parseMinMaxTime((Object[]) c.uniqueResult());
        }
    }

    /**
     * Get {@link ObservationTimeExtrema} for a dataset.
     *
     * @param request
     *            {@link AbstractObservationRequest} request
     * @param series
     *            Dataset id to get time extrema for
     * @param session
     *            Hibernate session
     * @return Time extrema for dataset
     * @throws OwsExceptionReport
     *             If an error occurs
     */
    public ObservationTimeExtrema getTimeExtremaForSeries(AbstractObservationRequest request, long series,
            Session session) throws OwsExceptionReport {
        return getTimeExtremaForSeries(request, series, null, session);
    }

    @Override
    public ObservationTimeExtrema getTimeExtremaForSeries(Collection<DatasetEntity> series,
            Criterion temporalFilterCriterion, Session session) throws OwsExceptionReport {
        Criteria c = getSeriesValueCriteriaFor(series, temporalFilterCriterion, null, session);
        addPhenomenonTimeProjection(c);
        LOGGER.trace("QUERY getTimeExtremaForSeries(series, temporalFilter): {}", HibernateHelper.getSqlString(c));
        return parseMinMaxPhenomenonTime((Object[]) c.uniqueResult());
    }

    /**
     * Query the minimum {@link DataEntity} for parameter
     *
     * @param request
     *            {@link AbstractObservationRequest}
     * @param series
     *            Datasource series id
     * @param temporalFilterCriterion
     *            Temporal filter {@link Criterion}
     * @param session
     *            Hibernate Session
     * @return Resulting minimum {@link DataEntity}
     * @throws OwsExceptionReport
     *             If an error occurs when executing the query
     */
    public DataEntity getMinSeriesValueFor(AbstractObservationRequest request, long series,
            Criterion temporalFilterCriterion, Session session) throws OwsExceptionReport {
        return (DataEntity) getSeriesValueCriteriaFor(request, series, temporalFilterCriterion,
                ExtendedIndeterminateTime.FIRST, session).uniqueResult();
    }

    /**
     * Query the minimum {@link DataEntity} for parameter
     *
     * @param request
     *            {@link AbstractObservationRequest}
     * @param series
     *            Datasource series id
     * @param session
     *            Hibernate Session
     * @return Resulting minimum {@link DataEntity}
     * @throws OwsExceptionReport
     *             If an error occurs when executing the query
     */
    public DataEntity getMinSeriesValueFor(AbstractObservationRequest request, long series, Session session)
            throws OwsExceptionReport {
        return (DataEntity) getSeriesValueCriteriaFor(request, series, null, ExtendedIndeterminateTime.FIRST, session)
                .uniqueResult();
    }

    /**
     * Query the maximum {@link DataEntity} for parameter
     *
     * @param request
     *            {@link AbstractObservationRequest}
     * @param series
     *            Datasource series id
     * @param temporalFilterCriterion
     *            Temporal filter {@link Criterion}
     * @param session
     *            Hibernate Session
     * @return Resulting maximum {@link DataEntity}
     * @throws OwsExceptionReport
     *             If an error occurs when executing the query
     */
    public DataEntity getMaxSeriesValueFor(AbstractObservationRequest request, long series,
            Criterion temporalFilterCriterion, Session session) throws OwsExceptionReport {
        return (DataEntity) getSeriesValueCriteriaFor(request, series, temporalFilterCriterion,
                ExtendedIndeterminateTime.LATEST, session).uniqueResult();
    }

    /**
     * Query the maximum {@link DataEntity} for parameter
     *
     * @param request
     *            {@link AbstractObservationRequest}
     * @param series
     *            Datasource series id
     * @param session
     *            Hibernate Session
     * @return Resulting maximum {@link DataEntity}
     * @throws OwsExceptionReport
     *             If an error occurs when executing the query
     */
    public DataEntity getMaxSeriesValueFor(AbstractObservationRequest request, long series, Session session)
            throws OwsExceptionReport {
        return (DataEntity) getSeriesValueCriteriaFor(request, series, null, ExtendedIndeterminateTime.LATEST, session)
                .uniqueResult();
    }

    @Override
    public ObservationTimeExtrema getTimeExtremaForSeriesIds(Collection<Long> series,
            Criterion temporalFilterCriterion, Session session) throws OwsExceptionReport {
        Criteria c = getSeriesValueCriteriaForSeriesIds(series, temporalFilterCriterion, null, session);
        addPhenomenonTimeProjection(c);
        LOGGER.trace("QUERY getTimeExtremaForSeriesIds(series, temporalFilter): {}", HibernateHelper.getSqlString(c));
        return parseMinMaxPhenomenonTime((Object[]) c.uniqueResult());
    }

    private ObservationTimeExtrema parseMinMaxPhenomenonTime(Object[] result) {
        ObservationTimeExtrema ote = new ObservationTimeExtrema();
        if (result != null) {
            ote.setMinPhenomenonTime(DateTimeHelper.makeDateTime(result[0]));
            ote.setMaxPhenomenonTime(DateTimeHelper.makeDateTime(result[1]));
        }
        return ote;
    }

    private void addPhenomenonTimeProjection(Criteria c) {
        ProjectionList projectionList = Projections.projectionList();
        projectionList.add(Projections.min(DataEntity.PROPERTY_SAMPLING_TIME_START));
        projectionList.add(Projections.max(DataEntity.PROPERTY_SAMPLING_TIME_END));
        c.setProjection(projectionList);
    }

    /**
     * Get default {@link Criteria} for {@link Class}
     *
     * @param session
     *            Hibernate Session
     * @return Default {@link Criteria}
     */
    protected Criteria getDefaultObservationCriteria(Session session) {
        return getDefaultCriteria(getSeriesValueTimeClass(), session);
        // return
        // session.createCriteria().add(Restrictions.eq(DataEntity.DELETED,
        // false))
        // .setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY);
    }

    private void addMinMaxTimeProjection(Criteria c) {
        ProjectionList projectionList = Projections.projectionList();
        projectionList.add(Projections.min(DataEntity.PROPERTY_SAMPLING_TIME_START));
        projectionList.add(Projections.max(DataEntity.PROPERTY_SAMPLING_TIME_END));
        projectionList.add(Projections.max(DataEntity.PROPERTY_RESULT_TIME));
        if (HibernateHelper.isColumnSupported(getSeriesValueTimeClass(), DataEntity.PROPERTY_VALID_TIME_START)
                && HibernateHelper.isColumnSupported(getSeriesValueTimeClass(), DataEntity.PROPERTY_VALID_TIME_END)) {
            projectionList.add(Projections.min(DataEntity.PROPERTY_VALID_TIME_START));
            projectionList.add(Projections.max(DataEntity.PROPERTY_VALID_TIME_END));
        }
        c.setProjection(projectionList);
    }

    private ObservationTimeExtrema parseMinMaxTime(Object[] result) {
        ObservationTimeExtrema ote = new ObservationTimeExtrema();
        if (result != null) {
            ote.setMinPhenomenonTime(DateTimeHelper.makeDateTime(result[0]));
            ote.setMaxPhenomenonTime(DateTimeHelper.makeDateTime(result[1]));
            ote.setMaxResultTime(DateTimeHelper.makeDateTime(result[2]));
            if (result.length == 5) {
                ote.setMinValidTime(DateTimeHelper.makeDateTime(result[3]));
                ote.setMaxValidTime(DateTimeHelper.makeDateTime(result[4]));
            }
        }
        return ote;
    }

    /**
     * Create {@link Criteria} for parameter
     *
     * @param request
     *            {@link AbstractObservationRequest}
     * @param series
     *            Datasource series id
     * @param temporalFilterCriterion
     *            Temporal filter {@link Criterion}
     * @param sosIndeterminateTime
     *            first/latest indicator
     * @param session
     *            Hibernate Session
     * @return Resulting {@link Criteria}
     * @throws OwsExceptionReport
     *             If an error occurs when adding Spatial Filtering Profile
     *             restrictions
     */
    private Criteria getSeriesValueCriteriaFor(AbstractObservationRequest request, long series,
            Criterion temporalFilterCriterion, IndeterminateValue sosIndeterminateTime, Session session)
            throws OwsExceptionReport {
        final Criteria c = getDefaultObservationCriteria(session);
        c.add(Restrictions.eq(DataEntity.PROPERTY_DATASET_ID, series));
        StringBuilder logArgs = new StringBuilder(LOG_ARGS_REQUEST_SERIES);
        if (request instanceof GetObservationRequest) {
            GetObservationRequest getObsReq = (GetObservationRequest) request;
            checkAndAddSpatialFilteringProfileCriterion(c, getObsReq, session, logArgs);
            checkAndAddResultFilterCriterion(c, getObsReq, null, session, logArgs);
            addTemporalFilterCriterion(c, temporalFilterCriterion, logArgs);
            addIndeterminateTimeRestriction(c, sosIndeterminateTime, logArgs);
            addSpecificRestrictions(c, getObsReq, logArgs);
        }
        LOGGER.trace(LOG_QUERY_DATA_ENTITY, logArgs.toString(), HibernateHelper.getSqlString(c));
        return c;
    }

    private Criteria getSeriesValueCriteriaFor(AbstractObservationRequest request, Set<Long> series,
            Criterion temporalFilterCriterion, IndeterminateValue sosIndeterminateTime, Session session)
            throws OwsExceptionReport {
        final Criteria c = getDefaultObservationCriteria(session).createAlias(DataEntity.PROPERTY_DATASET, "s");
        c.add(Restrictions.in(DataEntity.PROPERTY_DATASET_ID, series));
        StringBuilder logArgs = new StringBuilder(LOG_ARGS_REQUEST_SERIES);
        if (request instanceof GetObservationRequest) {
            GetObservationRequest getObsReq = (GetObservationRequest) request;
            checkAndAddSpatialFilteringProfileCriterion(c, getObsReq, session, logArgs);

            addTemporalFilterCriterion(c, temporalFilterCriterion, logArgs);
            addIndeterminateTimeRestriction(c, sosIndeterminateTime, logArgs);
            addSpecificRestrictions(c, getObsReq, logArgs);
        }
        LOGGER.trace("QUERY getSeriesValueCriteriaFor({}): {}", logArgs.toString(), HibernateHelper.getSqlString(c));
        return c;
    }

    protected Criteria getSeriesValueCriteriaFor(Collection<DatasetEntity> series, Criterion temporalFilterCriterion,
            IndeterminateValue sosIndeterminateTime, Session session) throws OwsExceptionReport {
        return getSeriesValueCriteriaForSeriesIds(
                series.stream().map(DatasetEntity::getId).collect(Collectors.toSet()), temporalFilterCriterion,
                sosIndeterminateTime, session);
    }

    protected Criteria getSeriesValueCriteriaForSeriesIds(Collection<Long> series, Criterion temporalFilterCriterion,
            IndeterminateValue sosIndeterminateTime, Session session) throws OwsExceptionReport {
        final Criteria c = getDefaultObservationCriteria(session);

        c.add(QueryHelper.getCriterionForObjects(DataEntity.PROPERTY_DATASET_ID, series));

        StringBuilder logArgs = new StringBuilder(LOG_ARGS_REQUEST_SERIES);
        addTemporalFilterCriterion(c, temporalFilterCriterion, logArgs);
        addIndeterminateTimeRestriction(c, sosIndeterminateTime, logArgs);
        LOGGER.trace(LOG_QUERY_DATA_ENTITY, logArgs.toString(), HibernateHelper.getSqlString(c));
        return c;
    }

}
