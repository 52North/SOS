/*
 * Copyright (C) 2012-2023 52°North Spatial Information Research GmbH
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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Path;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import jakarta.persistence.criteria.Selection;

import org.hibernate.Session;
import org.n52.series.db.beans.DataEntity;
import org.n52.series.db.beans.DatasetEntity;
import org.n52.shetland.ogc.filter.TemporalFilter;
import org.n52.shetland.ogc.gml.time.IndeterminateValue;
import org.n52.shetland.ogc.ows.exception.OwsExceptionReport;
import org.n52.shetland.ogc.sos.request.AbstractObservationRequest;
import org.n52.shetland.ogc.sos.request.GetObservationRequest;
import org.n52.shetland.util.DateTimeHelper;
import org.n52.sos.ds.hibernate.dao.DaoFactory;
import org.n52.sos.ds.hibernate.dao.observation.AbstractValueTimeDAO;
import org.n52.sos.ds.hibernate.util.ObservationTimeExtrema;
import org.n52.sos.ds.hibernate.util.QueryHelper;
import org.n52.sos.ds.hibernate.util.ResultFilterRestrictions;
import org.n52.sos.ds.hibernate.util.ResultFilterRestrictions.SubQueryIdentifier;

/**
 * Abstract value time data access object class for {@link DataEntity}
 *
 * @author <a href="mailto:c.hollmann@52north.org">Carsten Hollmann</a>
 * @since 4.3.0
 *
 */
public abstract class AbstractSeriesValueTimeDAO extends AbstractValueTimeDAO {

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
     * Get {@link ObservationTimeExtrema} for a {@link DataEntity} with temporal filter.
     *
     * @param request
     *            {@link AbstractObservationRequest} request
     * @param series
     *            {@link DataEntity} to get time extrema for
     * @param temporalFilters
     *            Requested temporal filters
     * @param session
     *            Hibernate session
     * @return Time extrema for {@link DataEntity}
     * @throws OwsExceptionReport
     *             If an error occurs
     */
    public ObservationTimeExtrema getTimeExtremaForSeries(AbstractObservationRequest request, long series,
            List<TemporalFilter> temporalFilters, Session session) throws OwsExceptionReport {
        if (request instanceof GetObservationRequest observationRequest && observationRequest.hasResultFilter()) {
            ObservationTimeExtrema ote = new ObservationTimeExtrema();
            for (SubQueryIdentifier identifier : ResultFilterRestrictions
                    .getSubQueryIdentifier(getResultFilterClasses())) {
                ote.expand(
                        queryMinMaxTime(session, request, series, temporalFilters, observationRequest, identifier));
            }
            return ote;
        } else {
            return queryMinMaxTime(session, request, series, temporalFilters, null, null);
        }
    }


    @Override
    public ObservationTimeExtrema getTimeExtremaForSeries(Collection<DatasetEntity> series,
            List<TemporalFilter> temporalFilters, Session session) throws OwsExceptionReport {
        return getTimeExtremaForSeriesIds(series.stream().map(DatasetEntity::getId).collect(Collectors.toSet()),
                temporalFilters, session);
    }

    @Override
    public ObservationTimeExtrema getTimeExtremaForSeriesIds(Collection<Long> series,
            List<TemporalFilter> temporalFilters, Session session) throws OwsExceptionReport {
        CriteriaBuilder cb = session.getCriteriaBuilder();
        CriteriaQuery<Object[]> query = cb.createQuery(Object[].class);
        Root<?> root = query.from(getSeriesValueTimeClass());
        List<Predicate> predicates = defaultValuePredicates(cb, root);
        predicates.add(QueryHelper.getPredicateForObjects(cb, root.get(DataEntity.PROPERTY_DATASET_ID), series));
        Predicate temporalFilter = temporalFilterPredicate(cb, root, temporalFilters);
        if (temporalFilter != null) {
            predicates.add(temporalFilter);
        }
        query.multiselect(phenomenonTimeSelections(cb, root)).where(predicates.toArray(new Predicate[0]));
        return parseMinMaxPhenomenonTime(session.createQuery(query).uniqueResult());
    }

    private ObservationTimeExtrema parseMinMaxPhenomenonTime(Object[] result) {
        ObservationTimeExtrema ote = new ObservationTimeExtrema();
        if (result != null) {
            ote.setMinPhenomenonTime(DateTimeHelper.makeDateTime(result[0]));
            ote.setMaxPhenomenonTime(DateTimeHelper.makeDateTime(result[1]));
        }
        return ote;
    }

    private List<Selection<?>> phenomenonTimeSelections(CriteriaBuilder cb, Path<?> root) {
        List<Selection<?>> selections = new ArrayList<>();
        selections.add(cb.least(root.<Date>get(DataEntity.PROPERTY_SAMPLING_TIME_START)));
        selections.add(cb.greatest(root.<Date>get(DataEntity.PROPERTY_SAMPLING_TIME_END)));
        return selections;
    }

    private List<Selection<?>> minMaxTimeSelections(CriteriaBuilder cb, Path<?> root) {
        List<Selection<?>> selections = new ArrayList<>();
        selections.add(cb.least(root.<Date>get(DataEntity.PROPERTY_SAMPLING_TIME_START)));
        selections.add(cb.greatest(root.<Date>get(DataEntity.PROPERTY_SAMPLING_TIME_END)));
        selections.add(cb.greatest(root.<Date>get(DataEntity.PROPERTY_RESULT_TIME)));
        selections.add(cb.least(root.<Date>get(DataEntity.PROPERTY_VALID_TIME_START)));
        selections.add(cb.greatest(root.<Date>get(DataEntity.PROPERTY_VALID_TIME_END)));
        return selections;
    }

    private ObservationTimeExtrema parseMinMaxTime(Object[] result) {
        ObservationTimeExtrema ote = new ObservationTimeExtrema();
        if (result != null) {
            ote.setMinPhenomenonTime(DateTimeHelper.makeDateTime(result[0]));
            ote.setMaxPhenomenonTime(DateTimeHelper.makeDateTime(result[1]));
            ote.setMaxResultTime(DateTimeHelper.makeDateTime(result[2]));
            ote.setMinValidTime(DateTimeHelper.makeDateTime(result[3]));
            ote.setMaxValidTime(DateTimeHelper.makeDateTime(result[4]));
        }
        return ote;
    }

    /**
     * Query min/max time for a single dataset id, optionally restricted by a result filter sub-query
     *
     * @param session
     *            Hibernate session
     * @param request
     *            {@link AbstractObservationRequest}
     * @param series
     *            Datasource series id
     * @param temporalFilters
     *            Requested temporal filters
     * @param resultFilterRequest
     *            {@link GetObservationRequest} to take the result filter from, or {@code null} if none applies
     * @param identifier
     *            Result filter sub-query identifier, or {@code null} if none applies
     * @return Resulting time extrema
     * @throws OwsExceptionReport
     *             If an error occurs when adding Spatial Filtering Profile restrictions
     */
    private ObservationTimeExtrema queryMinMaxTime(Session session, AbstractObservationRequest request, long series,
            List<TemporalFilter> temporalFilters, GetObservationRequest resultFilterRequest,
            SubQueryIdentifier identifier) throws OwsExceptionReport {
        CriteriaBuilder cb = session.getCriteriaBuilder();
        CriteriaQuery<Object[]> query = cb.createQuery(Object[].class);
        Root<?> root = query.from(getSeriesValueTimeClass());
        List<Predicate> predicates = getSeriesValuePredicatesFor(cb, query, root, request, series, temporalFilters,
                null, session);
        if (resultFilterRequest != null && identifier != null) {
            Predicate resultFilter =
                    checkAndAddResultFilterCriterion(cb, query, root, resultFilterRequest, identifier, session);
            if (resultFilter != null) {
                predicates.add(resultFilter);
            }
        }
        query.multiselect(minMaxTimeSelections(cb, root)).where(predicates.toArray(new Predicate[0]));
        return parseMinMaxTime(session.createQuery(query).uniqueResult());
    }

    /**
     * Build the restricting {@link Predicate}s for a single dataset id, including the optional two-phase
     * indeterminate time restriction. Note: mirrors the legacy behaviour where the result filter (with a
     * {@code null} sub-query identifier) is applied here in addition to being re-applied per result-filter
     * class by the {@code queryMinMaxTime}/{@code getStreamingSeriesValuesFor}-style callers when a result
     * filter is present; preserved as-is rather than changed as part of this port.
     *
     * @param cb
     *            CriteriaBuilder
     * @param query
     *            Query being built
     * @param root
     *            Root of the value query
     * @param request
     *            {@link AbstractObservationRequest}
     * @param series
     *            Datasource series id
     * @param temporalFilters
     *            Requested temporal filters
     * @param sosIndeterminateTime
     *            first/latest indicator, or {@code null}
     * @param session
     *            Hibernate Session
     * @return Mutable list of predicates
     * @throws OwsExceptionReport
     *             If an error occurs when adding Spatial Filtering Profile restrictions
     */
    private List<Predicate> getSeriesValuePredicatesFor(CriteriaBuilder cb, CriteriaQuery<?> query, Root<?> root,
            AbstractObservationRequest request, long series, List<TemporalFilter> temporalFilters,
            IndeterminateValue sosIndeterminateTime, Session session) throws OwsExceptionReport {
        List<Predicate> predicates = defaultValuePredicates(cb, root);
        predicates.add(cb.equal(root.get(DataEntity.PROPERTY_DATASET_ID), series));
        if (request instanceof GetObservationRequest getObsReq) {
            Predicate spatialFilter = checkAndAddSpatialFilteringProfileCriterion(cb, root, getObsReq);
            if (spatialFilter != null) {
                predicates.add(spatialFilter);
            }
            Predicate resultFilter = checkAndAddResultFilterCriterion(cb, query, root, getObsReq, null, session);
            if (resultFilter != null) {
                predicates.add(resultFilter);
            }
        }
        Predicate temporalFilter = temporalFilterPredicate(cb, root, temporalFilters);
        if (temporalFilter != null) {
            predicates.add(temporalFilter);
        }
        if (sosIndeterminateTime != null) {
            Date extremaTime =
                    queryIndeterminateTimeExtrema(session, request, series, temporalFilters, sosIndeterminateTime);
            predicates.add(getIndeterminateTimePredicate(cb, root, sosIndeterminateTime, extremaTime));
        }
        if (request instanceof GetObservationRequest getObsReq) {
            predicates.addAll(specificPredicates(cb, root, getObsReq));
        }
        return predicates;
    }

    private Date queryIndeterminateTimeExtrema(Session session, AbstractObservationRequest request, long series,
            List<TemporalFilter> temporalFilters, IndeterminateValue sosIndeterminateTime)
            throws OwsExceptionReport {
        CriteriaBuilder cb = session.getCriteriaBuilder();
        CriteriaQuery<Date> query = cb.createQuery(Date.class);
        Root<?> root = query.from(getSeriesValueTimeClass());
        List<Predicate> predicates = defaultValuePredicates(cb, root);
        predicates.add(cb.equal(root.get(DataEntity.PROPERTY_DATASET_ID), series));
        if (request instanceof GetObservationRequest getObsReq) {
            Predicate spatialFilter = checkAndAddSpatialFilteringProfileCriterion(cb, root, getObsReq);
            if (spatialFilter != null) {
                predicates.add(spatialFilter);
            }
            Predicate resultFilter = checkAndAddResultFilterCriterion(cb, query, root, getObsReq, null, session);
            if (resultFilter != null) {
                predicates.add(resultFilter);
            }
        }
        Predicate temporalFilter = temporalFilterPredicate(cb, root, temporalFilters);
        if (temporalFilter != null) {
            predicates.add(temporalFilter);
        }
        query.select(getIndeterminateTimeExtremaExpression(cb, root, sosIndeterminateTime))
                .where(predicates.toArray(new Predicate[0]));
        return session.createQuery(query).uniqueResult();
    }

}