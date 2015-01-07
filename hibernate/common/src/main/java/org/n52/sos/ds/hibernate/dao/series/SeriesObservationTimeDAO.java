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

import java.util.Collection;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.n52.sos.ds.hibernate.dao.AbstractObservationTimeDAO;
import org.n52.sos.ds.hibernate.entities.Offering;
import org.n52.sos.ds.hibernate.entities.series.Series;
import org.n52.sos.ds.hibernate.entities.series.SeriesObservationTime;
import org.n52.sos.ds.hibernate.entities.values.ObservationValueTime;
import org.n52.sos.util.CollectionHelper;

/**
 * Hibernate data access class for series observation timess
 * 
 * @since 4.0.0
 * 
 */
public class SeriesObservationTimeDAO extends AbstractObservationTimeDAO {

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
	private Criteria createCriteriaFor(Class<?> clazz, Series series,
			Session session) {
		final Criteria criteria = session.createCriteria(clazz)
				.add(Restrictions.eq(ObservationValueTime.DELETED, false))
				.setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY);
		criteria.add(Restrictions.eq(SeriesObservationTime.SERIES, series));
		return criteria;
	}

	/**
	 * Create criteria to get min/max time values for a series
	 * 
	 * @param series
	 *            Series to get time values for
	 * @param session
	 *            Hibernate session
	 * @return Criteria for time values
	 */
	public Criteria getMinMaxTimeCriteriaForSeriesGetDataAvailabilityDAO(
			Series series, Collection<String> offerings, Session session) {
		Criteria criteria = createCriteriaFor(getObservationTimeClass(),
				series, session);
		if (CollectionHelper.isNotEmpty(offerings)) {
			criteria.createCriteria(SeriesObservationTime.OFFERINGS).add(
					Restrictions.in(Offering.IDENTIFIER, offerings));
		}
		criteria.setProjection(Projections
				.projectionList()
				.add(Projections
						.min(SeriesObservationTime.PHENOMENON_TIME_START))
				.add(Projections.max(SeriesObservationTime.PHENOMENON_TIME_END)));
		return criteria;
	}

	@Override
	protected Class<?> getObservationTimeClass() {
		return SeriesObservationTime.class;
	}

}
