/**
 * Copyright (C) 2012-2015 52°North Initiative for Geospatial Open Source
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
package org.n52.sos.ds.hibernate.dao.series;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Restrictions;
import org.n52.sos.ds.hibernate.dao.AbstractValueDAO;
import org.n52.sos.ds.hibernate.entities.Offering;
import org.n52.sos.ds.hibernate.entities.series.Series;
import org.n52.sos.ds.hibernate.entities.series.values.SeriesValueTime;
import org.n52.sos.ds.hibernate.util.HibernateHelper;
import org.n52.sos.ogc.ows.OwsExceptionReport;
import org.n52.sos.ogc.sos.SosConstants.SosIndeterminateTime;
import org.n52.sos.request.GetObservationRequest;
import org.n52.sos.util.CollectionHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Implementation of {@link AbstractValueDAO} for series concept to query only
 * time information
 * 
 * @author Carsten Hollmann <c.hollmann@52north.org>
 * @since 4.1.0
 *
 */
public class SeriesValueTimeDAO extends AbstractValueDAO {

    private static final Logger LOGGER = LoggerFactory.getLogger(SeriesValueTimeDAO.class);

    /**
     * Query the minimum {@link SeriesValueTime} for parameter
     * 
     * @param request
     *            {@link GetObservationRequest}
     * @param series
     *            Datasource series id
     * @param temporalFilterCriterion
     *            Temporal filter {@link Criterion}
     * @param session
     *            Hibernate Session
     * @return Resulting minimum {@link SeriesValueTime}
     * @throws OwsExceptionReport
     *             If an error occurs when executing the query
     */
    public SeriesValueTime getMinSeriesValueFor(GetObservationRequest request, long series,
            Criterion temporalFilterCriterion, Session session) throws OwsExceptionReport {
        return (SeriesValueTime) getSeriesValueCriteriaFor(request, series, temporalFilterCriterion,
                SosIndeterminateTime.first, session).uniqueResult();
    }

    /**
     * Query the maximum {@link SeriesValueTime} for parameter
     * 
     * @param request
     *            {@link GetObservationRequest}
     * @param series
     *            Datasource series id
     * @param temporalFilterCriterion
     *            Temporal filter {@link Criterion}
     * @param session
     *            Hibernate Session
     * @return Resulting maximum {@link SeriesValueTime}
     * @throws OwsExceptionReport
     *             If an error occurs when executing the query
     */
    public SeriesValueTime getMaxSeriesValueFor(GetObservationRequest request, long series,
            Criterion temporalFilterCriterion, Session session) throws OwsExceptionReport {
        return (SeriesValueTime) getSeriesValueCriteriaFor(request, series, temporalFilterCriterion,
                SosIndeterminateTime.latest, session).uniqueResult();
    }

    /**
     * Query the minimum {@link SeriesValueTime} for parameter
     * 
     * @param request
     *            {@link GetObservationRequest}
     * @param series
     *            Datasource series id
     * @param temporalFilterCriterion
     *            Temporal filter {@link Criterion}
     * @param session
     *            Hibernate Session
     * @return Resulting minimum {@link SeriesValueTime}
     * @throws OwsExceptionReport
     *             If an error occurs when executing the query
     */
    public SeriesValueTime getMinSeriesValueFor(GetObservationRequest request, long series, Session session)
            throws OwsExceptionReport {
        return (SeriesValueTime) getSeriesValueCriteriaFor(request, series, null, SosIndeterminateTime.first, session)
                .uniqueResult();
    }

    /**
     * Query the maximum {@link SeriesValueTime} for parameter
     * 
     * @param request
     *            {@link GetObservationRequest}
     * @param series
     *            Datasource series id
     * @param temporalFilterCriterion
     *            Temporal filter {@link Criterion}
     * @param session
     *            Hibernate Session
     * @return Resulting maximum {@link SeriesValueTime}
     * @throws OwsExceptionReport
     *             If an error occurs when executing the query
     */
    public SeriesValueTime getMaxSeriesValueFor(GetObservationRequest request, long series, Session session)
            throws OwsExceptionReport {
        return (SeriesValueTime) getSeriesValueCriteriaFor(request, series, null, SosIndeterminateTime.latest, session)
                .uniqueResult();
    }

    /**
     * Create {@link Criteria} for parameter
     * 
     * @param request
     *            {@link GetObservationRequest}
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
    private Criteria getSeriesValueCriteriaFor(GetObservationRequest request, long series,
            Criterion temporalFilterCriterion, SosIndeterminateTime sosIndeterminateTime, Session session)
            throws OwsExceptionReport {
        final Criteria c =
                getDefaultObservationCriteria(SeriesValueTime.class, session).createAlias(SeriesValueTime.SERIES, "s");
        checkAndAddSpatialFilteringProfileCriterion(c, request, session);

        c.add(Restrictions.eq("s." + Series.ID, series));

        if (CollectionHelper.isNotEmpty(request.getOfferings())) {
            c.createCriteria(SeriesValueTime.OFFERINGS).add(
                    Restrictions.in(Offering.IDENTIFIER, request.getOfferings()));
        }

        String logArgs = "request, series, offerings";
        if (temporalFilterCriterion != null) {
            logArgs += ", filterCriterion";
            c.add(temporalFilterCriterion);
        }
        if (sosIndeterminateTime != null) {
            logArgs += ", sosIndeterminateTime";
            addIndeterminateTimeRestriction(c, sosIndeterminateTime);
        }
        LOGGER.debug("QUERY getSeriesObservationFor({}): {}", logArgs, HibernateHelper.getSqlString(c));
        return c;
    }

    /**
     * Get default {@link Criteria} for {@link Class}
     * 
     * @param clazz
     *            {@link Class} to get default {@link Criteria} for
     * @param session
     *            Hibernate Session
     * @return Default {@link Criteria}
     */
    public Criteria getDefaultObservationCriteria(Class<?> clazz, Session session) {
        return session.createCriteria(clazz).add(Restrictions.eq(SeriesValueTime.DELETED, false))
                .setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY);
    }

}
