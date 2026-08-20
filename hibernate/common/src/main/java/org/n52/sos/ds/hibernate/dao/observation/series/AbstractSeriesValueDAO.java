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

import java.util.LinkedList;
import java.util.List;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Path;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;

import org.hibernate.Session;
import org.n52.series.db.beans.DataEntity;
import org.n52.shetland.ogc.ows.exception.OwsExceptionReport;
import org.n52.shetland.ogc.sos.request.GetObservationRequest;
import org.n52.sos.ds.hibernate.dao.DaoFactory;
import org.n52.sos.ds.hibernate.dao.observation.AbstractValueDAO;
import org.n52.sos.ds.hibernate.util.ResultFilterRestrictions;
import org.n52.sos.ds.hibernate.util.ResultFilterRestrictions.SubQueryIdentifier;

/**
 * Abstract value data access object class for {@link DataEntity}
 *
 * @author <a href="mailto:c.hollmann@52north.org">Carsten Hollmann</a>
 * @since 4.3.0
 *
 */
public abstract class AbstractSeriesValueDAO extends AbstractValueDAO {

    public AbstractSeriesValueDAO(DaoFactory daoFactory) {
        super(daoFactory);
    }

    protected Class<?> getSeriesValueClass() {
        return DataEntity.class;
    }

    /**
     * Query streaming value for parameter as chunk {@link List}
     *
     * @param ctx
     *            {@link ValueQueryContext}
     * @throws OwsExceptionReport
     *             If an error occurs when querying
     */
    public List<DataEntity<?>> getStreamingSeriesValuesFor(ValueQueryContext ctx) throws OwsExceptionReport {
        if (ctx.getRequest() instanceof GetObservationRequest getObsReq && getObsReq.hasResultFilter()) {
            List<DataEntity<?>> list = new LinkedList<>();
            for (SubQueryIdentifier identifier : ResultFilterRestrictions
                    .getSubQueryIdentifier(getResultFilterClasses())) {
                list.addAll(querySeriesValuesFor(ctx, getObsReq, identifier));
            }
            return list;
        } else {
            return querySeriesValuesFor(ctx, null, null);
        }
    }

    /**
     * Build and execute the query for parameter, optionally restricted by a result filter sub-query
     *
     * @param ctx
     *            {@link ValueQueryContext}
     * @param resultFilterRequest
     *            {@link GetObservationRequest} to take the result filter from, or {@code null} if none applies
     * @param identifier
     *            Result filter sub-query identifier, or {@code null} if none applies
     * @return Resulting {@link DataEntity}s
     * @throws OwsExceptionReport
     *             If an error occurs when adding Spatial Filtering Profile restrictions
     */
    @SuppressWarnings("unchecked")
    private List<DataEntity<?>> querySeriesValuesFor(ValueQueryContext ctx, GetObservationRequest resultFilterRequest,
            SubQueryIdentifier identifier) throws OwsExceptionReport {
        Session session = ctx.getSession();
        CriteriaBuilder cb = session.getCriteriaBuilder();
        CriteriaQuery<?> query = cb.createQuery(getSeriesValueClass());
        Root<?> root = query.from(getSeriesValueClass());
        List<Predicate> predicates = getSeriesValuePredicatesFor(cb, root, ctx);
        if (resultFilterRequest != null && identifier != null) {
            Predicate resultFilter =
                    checkAndAddResultFilterCriterion(cb, query, root, resultFilterRequest, identifier, session);
            if (resultFilter != null) {
                predicates.add(resultFilter);
            }
        }
        fetchDefaultAssociations(root);
        query.where(predicates.toArray(new Predicate[0]))
                .orderBy(cb.asc(root.get(getOrderColumn(ctx.getRequest()))));
        var typedQuery = session.createQuery(query).setReadOnly(true);
        if (ctx.getChunkSize() > 0) {
            typedQuery.setMaxResults(ctx.getChunkSize()).setFirstResult(ctx.getCurrentRow());
        }
        return (List<DataEntity<?>>) typedQuery.list();
    }

    /**
     * Build the restricting {@link Predicate}s for the parameter, independent of any result filter
     *
     * @param cb
     *            CriteriaBuilder
     * @param root
     *            Root of the value query
     * @param ctx
     *            {@link ValueQueryContext}
     * @return Mutable list of predicates
     * @throws OwsExceptionReport
     *             If an error occurs when adding Spatial Filtering Profile restrictions
     */
    private List<Predicate> getSeriesValuePredicatesFor(CriteriaBuilder cb, Path<?> root, ValueQueryContext ctx)
            throws OwsExceptionReport {
        List<Predicate> predicates = defaultValuePredicates(cb, root);
        predicates.add(cb.equal(root.get(DataEntity.PROPERTY_DATASET_ID), ctx.getDatasetId()));
        if (ctx.getRequest() instanceof GetObservationRequest getObsReq) {
            Predicate spatialFilter = checkAndAddSpatialFilteringProfileCriterion(cb, root, getObsReq);
            if (spatialFilter != null) {
                predicates.add(spatialFilter);
            }
            Predicate temporalFilter = temporalFilterPredicate(cb, root, ctx.getTemporalFilters());
            if (temporalFilter != null) {
                predicates.add(temporalFilter);
            }
            predicates.addAll(specificPredicates(cb, root, getObsReq));
        }
        return predicates;
    }

}