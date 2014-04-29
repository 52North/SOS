/**
 * Copyright (C) 2012-2014 52°North Initiative for Geospatial Open Source
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

import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.ScrollMode;
import org.hibernate.ScrollableResults;
import org.hibernate.Session;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.n52.sos.ds.hibernate.dao.AbstractValueDAO;
import org.n52.sos.ds.hibernate.entities.Offering;
import org.n52.sos.ds.hibernate.entities.series.Series;
import org.n52.sos.ds.hibernate.entities.series.SeriesObservation;
import org.n52.sos.ds.hibernate.entities.series.values.SeriesValue;
import org.n52.sos.ds.hibernate.util.HibernateHelper;
import org.n52.sos.ogc.ows.OwsExceptionReport;
import org.n52.sos.request.GetObservationRequest;
import org.n52.sos.util.CollectionHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SeriesValueDAO extends AbstractValueDAO {

    private static final Logger LOGGER = LoggerFactory.getLogger(SeriesValueDAO.class);

    public ScrollableResults getStreamingSeriesValuesFor(GetObservationRequest request, long series,
            Criterion temporalFilterCriterion, Session session) throws HibernateException, OwsExceptionReport {
        return getSeriesValueCriteriaFor(request, series, temporalFilterCriterion, session).scroll(
                ScrollMode.FORWARD_ONLY);
    }

    public ScrollableResults getStreamingSeriesValuesFor(GetObservationRequest request, long series, Session session)
            throws HibernateException, OwsExceptionReport {
        return getSeriesValueCriteriaFor(request, series, null, session).scroll(ScrollMode.FORWARD_ONLY);
    }

    @SuppressWarnings("unchecked")
    public List<SeriesValue> getStreamingSeriesValuesFor(GetObservationRequest request, long series,
            Criterion temporalFilterCriterion, int chunkSize, int currentRow, Session session) throws OwsExceptionReport {
        Criteria c = getSeriesValueCriteriaFor(request, series, temporalFilterCriterion, session);
        addChunkValuesToCriteria(c, chunkSize, currentRow);
        LOGGER.debug("QUERY getStreamingSeriesValuesFor(): {}", HibernateHelper.getSqlString(c));
        return (List<SeriesValue>) c.list();
    }

    @SuppressWarnings("unchecked")
    public List<SeriesValue> getStreamingSeriesValuesFor(GetObservationRequest request, long series, int chunkSize,
            int currentRow, Session session) throws OwsExceptionReport {
        Criteria c = getSeriesValueCriteriaFor(request, series, null, session);
        addChunkValuesToCriteria(c, chunkSize, currentRow);
        LOGGER.debug("QUERY getStreamingSeriesValuesFor(): {}", HibernateHelper.getSqlString(c));
        return (List<SeriesValue>) c.list();
    }

    private void addChunkValuesToCriteria(Criteria c, int chunkSize, int currentRow) {
        c.addOrder(Order.asc(SeriesValue.PHENOMENON_TIME_START));
        c.setMaxResults(chunkSize).setFirstResult(currentRow);
    }

    private Criteria getSeriesValueCriteriaFor(GetObservationRequest request, long series,
            Criterion temporalFilterCriterion, Session session) throws OwsExceptionReport {
        final Criteria c =
                getDefaultObservationCriteria(SeriesValue.class, session).createAlias(SeriesObservation.SERIES, "s");

        checkAndAddSpatialFilteringProfileCriterion(c, request, session);

        c.add(Restrictions.eq("s." + Series.ID, series));

        if (CollectionHelper.isNotEmpty(request.getOfferings())) {
            c.createCriteria(SeriesObservation.OFFERINGS).add(
                    Restrictions.in(Offering.IDENTIFIER, request.getOfferings()));
        }

        String logArgs = "request, series, offerings";
        if (temporalFilterCriterion != null) {
            logArgs += ", filterCriterion";
            c.add(temporalFilterCriterion);
        }
        LOGGER.debug("QUERY getStreamingSeriesValuesFor({}): {}", logArgs, HibernateHelper.getSqlString(c));
        return c.setReadOnly(true);
    }

    public Criteria getDefaultObservationCriteria(Class<?> clazz, Session session) {
        return session.createCriteria(clazz).add(Restrictions.eq(SeriesValue.DELETED, false))
                .setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY);
    }

}
