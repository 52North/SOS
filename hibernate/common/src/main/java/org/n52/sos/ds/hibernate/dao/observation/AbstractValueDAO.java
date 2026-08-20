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
package org.n52.sos.ds.hibernate.dao.observation;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Expression;
import jakarta.persistence.criteria.From;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Path;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;

import org.hibernate.Session;
import org.n52.series.db.beans.DataEntity;
import org.n52.shetland.ogc.filter.TemporalFilter;
import org.n52.shetland.ogc.gml.time.IndeterminateValue;
import org.n52.shetland.ogc.ows.exception.OwsExceptionReport;
import org.n52.shetland.ogc.sos.ExtendedIndeterminateTime;
import org.n52.shetland.ogc.sos.request.AbstractObservationRequest;
import org.n52.shetland.ogc.sos.request.GetObservationRequest;
import org.n52.sos.ds.hibernate.dao.DaoFactory;
import org.n52.sos.ds.hibernate.dao.TimeCreator;
import org.n52.sos.ds.hibernate.util.ResultFilterClasses;
import org.n52.sos.ds.hibernate.util.ResultFilterRestrictions;
import org.n52.sos.ds.hibernate.util.ResultFilterRestrictions.SubQueryIdentifier;
import org.n52.sos.ds.hibernate.util.SosTemporalRestrictions;
import org.n52.sos.ds.hibernate.util.SpatialRestrictions;
import org.n52.sos.exception.ows.concrete.UnsupportedOperatorException;
import org.n52.sos.exception.ows.concrete.UnsupportedTimeException;
import org.n52.sos.exception.ows.concrete.UnsupportedValueReferenceException;
import org.n52.sos.util.GeometryHandler;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

/**
 * Abstract DAO class for querying {@link DataEntity}
 *
 * @author <a href="mailto:c.hollmann@52north.org">Carsten Hollmann</a>
 * @since 4.1.0
 *
 */
@SuppressFBWarnings({"EI_EXPOSE_REP2"})
public abstract class AbstractValueDAO extends TimeCreator {

    private DaoFactory daoFactory;

    public AbstractValueDAO(DaoFactory daoFactory) {
        this.daoFactory = daoFactory;
    }

    /**
     * Check if a Spatial Filtering Profile filter is requested and build the restricting {@link Predicate}
     *
     * @param cb
     *            CriteriaBuilder
     * @param root
     *            Root of the value query
     * @param request
     *            GetObservationRequest request
     *
     * @return Predicate, or {@code null} if no Spatial Filtering Profile filter was requested
     *
     * @throws OwsExceptionReport
     *             If Spatial Filtering Profile is not supported or an error occurs.
     */
    protected Predicate checkAndAddSpatialFilteringProfileCriterion(CriteriaBuilder cb, Path<?> root,
            GetObservationRequest request) throws OwsExceptionReport {
        if (request.hasSpatialFilteringProfileSpatialFilter()) {
            return SpatialRestrictions.filter(cb, root.get(DataEntity.PROPERTY_GEOMETRY_ENTITY),
                    request.getSpatialFilter().getOperator(),
                    getGeometryHandler().switchCoordinateAxisFromToDatasourceIfNeeded(
                            request.getSpatialFilter().getGeometry()));
        }
        return null;
    }

    protected Predicate checkAndAddResultFilterCriterion(CriteriaBuilder cb, CriteriaQuery<?> query, Root<?> root,
            GetObservationRequest request, SubQueryIdentifier identifier, Session session)
            throws OwsExceptionReport {
        if (request.hasResultFilter()) {
            return ResultFilterRestrictions.getResultFilterExpression(cb, query, root, request.getResultFilter(),
                    getResultFilterClasses(), DataEntity.PROPERTY_ID, identifier);
        }
        return null;
    }

    protected ResultFilterClasses getResultFilterClasses() {
        return new ResultFilterClasses(getValuedObservationFactory().numericClass(),
                getValuedObservationFactory().countClass(), getValuedObservationFactory().textClass(),
                getValuedObservationFactory().categoryClass(), getValuedObservationFactory().complexClass(),
                getValuedObservationFactory().profileClass());
    }

    /**
     * Build the restricting {@link Predicate} for the requested temporal filters
     *
     * @param cb
     *            CriteriaBuilder
     * @param root
     *            Path to filter on
     * @param temporalFilters
     *            Requested temporal filters, may be {@code null} or empty
     *
     * @return Predicate, or {@code null} if no temporal filter was requested
     */
    protected Predicate temporalFilterPredicate(CriteriaBuilder cb, Path<?> root,
            List<TemporalFilter> temporalFilters)
        throws UnsupportedOperatorException, UnsupportedTimeException, UnsupportedValueReferenceException {
        if (temporalFilters != null && !temporalFilters.isEmpty()) {
            return SosTemporalRestrictions.filter(cb, root, temporalFilters);
        }
        return null;
    }

    /**
     * Get the min/max expression for {@link IndeterminateValue} value
     *
     * @param cb
     *            CriteriaBuilder
     * @param root
     *            Root of the value query
     * @param indetTime
     *            Value to get the expression for
     *
     * @return Expression to use to determine indeterminate time extrema
     */
    protected Expression<Date> getIndeterminateTimeExtremaExpression(CriteriaBuilder cb, Path<?> root,
            IndeterminateValue indetTime) {
        if (indetTime.equals(ExtendedIndeterminateTime.FIRST)) {
            return cb.least(root.<Date>get(DataEntity.PROPERTY_SAMPLING_TIME_START));
        } else if (indetTime.equals(ExtendedIndeterminateTime.LATEST)) {
            return cb.greatest(root.<Date>get(DataEntity.PROPERTY_SAMPLING_TIME_END));
        }
        return null;
    }

    /**
     * Get the AbstractValue property to filter on for an {@link IndeterminateValue}
     *
     * @param indetTime
     *            Value to get property for
     * @return String property to filter on
     */
    protected String getIndeterminateTimeFilterProperty(IndeterminateValue indetTime) {
        if (indetTime.equals(ExtendedIndeterminateTime.FIRST)) {
            return DataEntity.PROPERTY_SAMPLING_TIME_START;
        } else if (indetTime.equals(ExtendedIndeterminateTime.LATEST)) {
            return DataEntity.PROPERTY_SAMPLING_TIME_END;
        }
        return null;
    }

    /**
     * Get a {@link Predicate} restricting values to the given indeterminate time extrema (max for latest, min
     * for first). Note: use this in addition to all other applicable restrictions so that it filters within
     * that same set.
     *
     * @param cb
     *            CriteriaBuilder
     * @param root
     *            Root of the value query
     * @param indetTime
     *            Indeterminate time restriction to add
     * @param extremaTime
     *            Precomputed indeterminate time extrema
     *
     * @return Predicate
     */
    protected Predicate getIndeterminateTimePredicate(CriteriaBuilder cb, Path<?> root,
            IndeterminateValue indetTime, Date extremaTime) {
        return cb.equal(root.get(getIndeterminateTimeFilterProperty(indetTime)), extremaTime);
    }

    protected String getOrderColumn(AbstractObservationRequest request) {
        if (request instanceof GetObservationRequest observationRequest) {
            if (observationRequest.isSetTemporalFilter()) {
                TemporalFilter filter = observationRequest.getTemporalFilters().iterator().next();
                if (filter.getValueReference().contains(DataEntity.PROPERTY_RESULT_TIME)) {
                    return DataEntity.PROPERTY_RESULT_TIME;
                }
            }
        }
        return DataEntity.PROPERTY_SAMPLING_TIME_START;
    }

    /**
     * Build the default restricting {@link Predicate}s applicable to every value query
     *
     * @param cb
     *            CriteriaBuilder
     * @param root
     *            Root of the value query
     *
     * @return Mutable list of default predicates
     */
    protected List<Predicate> defaultValuePredicates(CriteriaBuilder cb, Path<?> root) {
        List<Predicate> predicates = new ArrayList<>();
        predicates.add(cb.isFalse(root.<Boolean>get(DataEntity.PROPERTY_DELETED)));
        if (!daoFactory.isIncludeChildObservableProperties()) {
            predicates.add(cb.isNull(root.get(DataEntity.PROPERTY_PARENT)));
        } else {
            predicates.add(cb.or(cb.isNotNull(root.get(DataEntity.PROPERTY_PARENT)),
                    cb.and(cb.isNull(root.get(DataEntity.PROPERTY_PARENT)),
                            cb.equal(cb.size(root.get(DataEntity.PROPERTY_VALUE)), 0))));
        }
        return predicates;
    }

    protected void fetchDefaultAssociations(From<?, ?> root) {
        root.fetch(DataEntity.PROPERTY_PARAMETERS, JoinType.LEFT);
    }

    protected abstract List<Predicate> specificPredicates(CriteriaBuilder cb, Path<?> root,
            GetObservationRequest request) throws OwsExceptionReport;

    protected abstract ValuedObservationFactory getValuedObservationFactory();

    public GeometryHandler getGeometryHandler() {
        return daoFactory.getGeometryHandler();
    }

    protected DaoFactory getDaoFactory() {
        return daoFactory;
    }
}