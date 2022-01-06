/*
 * Copyright (C) 2012-2022 52°North Spatial Information Research GmbH
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
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.n52.series.db.beans.DataEntity;
import org.n52.series.db.beans.DatasetEntity;
import org.n52.series.db.beans.OfferingEntity;
import org.n52.shetland.util.CollectionHelper;
import org.n52.sos.ds.hibernate.dao.observation.AbstractObservationTimeDAO;

/**
 * Hibernate data access class for series observation times
 *
 * @since 4.0.0
 *
 */
public class SeriesObservationTimeDAO extends AbstractObservationTimeDAO {

    private static final String DS_OFF_PREFIX = "ds.off";

    /**
     * Create criteria for series
     *
     * @param clazz
     *            Class to query
     * @param series
     *            Series to get values for
     * @param session
     *            Hibernate session
     * @return Criteria for series
     */
    private Criteria createCriteriaFor(Class<?> clazz, DatasetEntity series, Session session) {
        final Criteria criteria =
                session.createCriteria(clazz).add(Restrictions.eq(DataEntity.PROPERTY_DELETED, false))
                        .setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY);
        criteria.add(Restrictions.eq(DataEntity.PROPERTY_DATASET, series));
        return criteria;
    }

    /**
     * Create criteria to get min/max time values for a series
     *
     * @param series
     *            Series to get time values for
     * @param offerings
     *            the offerings
     * @param session
     *            Hibernate session
     * @return Criteria for time values
     */
    public Criteria getMinMaxTimeCriteriaForSeriesGetDataAvailabilityDAO(DatasetEntity series,
            Collection<String> offerings, Session session) {
        Criteria criteria = createCriteriaFor(DataEntity.class, series, session);
        criteria.setProjection(
                Projections.projectionList().add(Projections.min(DataEntity.PROPERTY_SAMPLING_TIME_START))
                        .add(Projections.max(DataEntity.PROPERTY_SAMPLING_TIME_END)));
        return criteria;
    }

    public Criteria getOfferingMinMaxTimeCriteriaForSeriesGetDataAvailabilityDAO(DatasetEntity series,
            Collection<String> offerings, Session session) {
        Criteria criteria = createCriteriaFor(DataEntity.class, series, session);
        Criteria datasetCriteria = criteria.createCriteria(DataEntity.PROPERTY_DATASET, "ds");
        if (CollectionHelper.isNotEmpty(offerings)) {
            datasetCriteria.createCriteria(DatasetEntity.PROPERTY_OFFERING, DS_OFF_PREFIX)
                    .add(Restrictions.in(OfferingEntity.IDENTIFIER, offerings));
        } else {
            datasetCriteria.createAlias(DatasetEntity.PROPERTY_OFFERING, DS_OFF_PREFIX);
        }
        criteria.setProjection(
                Projections.projectionList().add(Projections.groupProperty("ds.off." + OfferingEntity.IDENTIFIER))
                        .add(Projections.min(DataEntity.PROPERTY_SAMPLING_TIME_START))
                        .add(Projections.max(DataEntity.PROPERTY_SAMPLING_TIME_END)));
        return criteria;
    }

    @Override
    protected Class<?> getObservationTimeClass() {
        return DataEntity.class;
    }

}
