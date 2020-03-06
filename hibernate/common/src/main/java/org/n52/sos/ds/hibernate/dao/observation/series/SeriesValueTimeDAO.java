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
package org.n52.sos.ds.hibernate.dao.observation.series;

import java.util.Collection;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Criterion;
import org.n52.sos.ds.hibernate.dao.observation.ValuedObservationFactory;
import org.n52.sos.ds.hibernate.entities.observation.series.Series;
import org.n52.sos.ds.hibernate.entities.observation.series.TemporalReferencedSeriesObservation;
import org.n52.sos.ds.hibernate.util.ObservationTimeExtrema;
import org.n52.sos.exception.CodedException;
import org.n52.sos.ogc.ows.OwsExceptionReport;
import org.n52.sos.request.GetObservationRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Implementation of {@link AbstractSeriesValueTimeDAO} for series concept to
 * query only time information
 *
 * @author Carsten Hollmann <c.hollmann@52north.org>
 * @since 4.1.0
 *
 */
public class SeriesValueTimeDAO extends AbstractSeriesValueTimeDAO {

    private static final Logger LOGGER = LoggerFactory.getLogger(SeriesValueTimeDAO.class);

    @Override
        protected void addSpecificRestrictions(Criteria c, GetObservationRequest request, StringBuilder logArgs) throws CodedException {
            // nothing  to add
    }

    @Override
    protected Class<?> getSeriesValueTimeClass() {
        return TemporalReferencedSeriesObservation.class;
    }

    @Override
    public ObservationTimeExtrema getTimeExtremaForSeries(Collection<Series> series, Criterion temporalFilter,
            Session session) throws OwsExceptionReport {
        return new ObservationTimeExtrema();
    }

    @Override
    public ObservationTimeExtrema getTimeExtremaForSeriesIds(Collection<Long> series, Criterion temporalFilter,
            Session session) throws OwsExceptionReport {
        return new ObservationTimeExtrema();
    }

    @Override
    protected ValuedObservationFactory getValuedObservationFactory() {
        return SeriesValuedObervationFactory.getInstance();
    }

//    /**
//     * Query the minimum {@link TemporalReferencedSeriesObservation} for parameter
//     *
//     * @param request
//     *            {@link GetObservationRequest}
//     * @param series
//     *            Datasource series id
//     * @param temporalFilterCriterion
//     *            Temporal filter {@link Criterion}
//     * @param session
//     *            Hibernate Session
//     * @return Resulting minimum {@link TemporalReferencedSeriesObservation}
//     * @throws OwsExceptionReport
//     *             If an error occurs when executing the query
//     */
//    public TemporalReferencedSeriesObservation getMinSeriesValueFor(GetObservationRequest request, long series,
//            Criterion temporalFilterCriterion, Session session) throws OwsExceptionReport {
//        return (TemporalReferencedSeriesObservation) getSeriesValueCriteriaFor(request, series, temporalFilterCriterion,
//                SosConstants.SosIndeterminateTime.first, session).uniqueResult();
//    }
//    
//    /**
//     * Query the maximum {@link TemporalReferencedSeriesObservation} for parameter
//     *
//     * @param request
//     *            {@link GetObservationRequest}
//     * @param series
//     *            Datasource series id
//     * @param temporalFilterCriterion
//     *            Temporal filter {@link Criterion}
//     * @param session
//     *            Hibernate Session
//     * @return Resulting maximum {@link TemporalReferencedSeriesObservation}
//     * @throws OwsExceptionReport
//     *             If an error occurs when executing the query
//     */
//    public TemporalReferencedSeriesObservation getMaxSeriesValueFor(GetObservationRequest request, long series,
//            Criterion temporalFilterCriterion, Session session) throws OwsExceptionReport {
//        return (TemporalReferencedSeriesObservation) getSeriesValueCriteriaFor(request, series, temporalFilterCriterion,
//                SosConstants.SosIndeterminateTime.latest, session).uniqueResult();
//    }
//
//    /**
//     * Query the minimum {@link TemporalReferencedSeriesObservation} for parameter
//     *
//     * @param request
//     *            {@link GetObservationRequest}
//     * @param series
//     *            Datasource series id
//     * @param session
//     *            Hibernate Session
//     * @return Resulting minimum {@link TemporalReferencedSeriesObservation}
//     * @throws OwsExceptionReport
//     *             If an error occurs when executing the query
//     */
//    public TemporalReferencedSeriesObservation getMinSeriesValueFor(GetObservationRequest request, long series, Session session)
//            throws OwsExceptionReport {
//        return (TemporalReferencedSeriesObservation) getSeriesValueCriteriaFor(request, series, null, SosConstants.SosIndeterminateTime.first, session)
//                .uniqueResult();
//    }
//
//    /**
//     * Query the maximum {@link TemporalReferencedSeriesObservation} for parameter
//     *
//     * @param request
//     *            {@link GetObservationRequest}
//     * @param series
//     *            Datasource series id
//     * @param session
//     *            Hibernate Session
//     * @return Resulting maximum {@link TemporalReferencedSeriesObservation}
//     * @throws OwsExceptionReport
//     *             If an error occurs when executing the query
//     */
//    public TemporalReferencedSeriesObservation getMaxSeriesValueFor(GetObservationRequest request, long series, Session session)
//            throws OwsExceptionReport {
//        return (TemporalReferencedSeriesObservation) getSeriesValueCriteriaFor(request, series, null, SosConstants.SosIndeterminateTime.latest, session)
//                .uniqueResult();
//    }
//
//    /**
//     * Create {@link Criteria} for parameter
//     *
//     * @param request
//     *            {@link GetObservationRequest}
//     * @param series
//     *            Datasource series id
//     * @param temporalFilterCriterion
//     *            Temporal filter {@link Criterion}
//     * @param sosIndeterminateTime
//     *            first/latest indicator
//     * @param session
//     *            Hibernate Session
//     * @return Resulting {@link Criteria}
//     * @throws OwsExceptionReport
//     *             If an error occurs when adding Spatial Filtering Profile
//     *             restrictions
//     */
//    protected Criteria getSeriesValueCriteriaFor(GetObservationRequest request, long series,
//            Criterion temporalFilterCriterion, SosConstants.SosIndeterminateTime sosIndeterminateTime, Session session)
//            throws OwsExceptionReport {
//        final Criteria c =
//                getDefaultObservationCriteria(TemporalReferencedSeriesObservation.class, session).createAlias(TemporalReferencedSeriesObservation.SERIES, "s");
//        checkAndAddSpatialFilteringProfileCriterion(c, request, session);
//
//        c.add(Restrictions.eq("s." + Series.ID, series));
//
//        if (CollectionHelper.isNotEmpty(request.getOfferings())) {
//            c.createCriteria(TemporalReferencedSeriesObservation.OFFERINGS).add(
//                    Restrictions.in(Offering.IDENTIFIER, request.getOfferings()));
//        }
//
//        String logArgs = "request, series, offerings";
//        if (temporalFilterCriterion != null) {
//            logArgs += ", filterCriterion";
//            c.add(temporalFilterCriterion);
//        }
//        if (sosIndeterminateTime != null) {
//            logArgs += ", sosIndeterminateTime";
//            addIndeterminateTimeRestriction(c, sosIndeterminateTime);
//        }
//        LOGGER.debug("QUERY getSeriesObservationFor({}): {}", logArgs, HibernateHelper.getSqlString(c));
//        return c;
//    }
//
//    /**
//     * Get default {@link Criteria} for {@link Class}
//     *
//     * @param clazz
//     *            {@link Class} to get default {@link Criteria} for
//     * @param session
//     *            Hibernate Session
//     * @return Default {@link Criteria}
//     */
//    public Criteria getDefaultObservationCriteria(Class<?> clazz, Session session) {
//        return session.createCriteria(clazz).add(Restrictions.eq(TemporalReferencedSeriesObservation.DELETED, false))
//                .setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY);
//    }

}
