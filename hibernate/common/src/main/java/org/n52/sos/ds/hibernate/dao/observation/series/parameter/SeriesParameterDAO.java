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
package org.n52.sos.ds.hibernate.dao.observation.series.parameter;

import java.util.Collections;
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;
import org.n52.sos.ds.hibernate.entities.observation.series.Series;
import org.n52.sos.ds.hibernate.entities.parameter.Parameter;
import org.n52.sos.ds.hibernate.entities.parameter.series.SeriesParameter;
import org.n52.sos.ds.hibernate.util.HibernateHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SeriesParameterDAO {

    private static final Logger LOGGER = LoggerFactory.getLogger(SeriesParameterDAO.class);
    
    public List<Parameter> getSeriesParameter(Series series, Session session) {
        return getSeriesParameter(series.getSeriesId(), session);
    }

    private List<Parameter> getSeriesParameter(long series, Session session) {
        if (HibernateHelper.isEntitySupported(SeriesParameter.class)) {
            Criteria c = getDefaultCriteria(session);
            c.add(Restrictions.eq(SeriesParameter.SERIES_ID, series));
            LOGGER.trace("QUERY getSeriesParameter(): {series}", HibernateHelper.getSqlString(c));
            return c.list();
        }
        return Collections.EMPTY_LIST;
    }

    private Criteria getDefaultCriteria(Session session) {
        return session.createCriteria(SeriesParameter.class)
                .setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY);
    }

        
}
