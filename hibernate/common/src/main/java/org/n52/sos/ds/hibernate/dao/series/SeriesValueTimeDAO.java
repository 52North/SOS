/**
 * Copyright (C) 2012-2016 52°North Initiative for Geospatial Open Source
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
import org.hibernate.criterion.Criterion;
import org.n52.sos.ds.hibernate.entities.series.Series;
import org.n52.sos.ds.hibernate.entities.series.values.SeriesValueTime;
import org.n52.sos.ds.hibernate.util.ObservationTimeExtrema;
import org.n52.sos.exception.CodedException;
import org.n52.sos.request.GetObservationRequest;

/**
 * Implementation of {@link AbstractSeriesValueTimeDAO} for series concept to
 * query only time information
 * 
 * @author Carsten Hollmann <c.hollmann@52north.org>
 * @since 4.1.0
 *
 */
public class SeriesValueTimeDAO extends AbstractSeriesValueTimeDAO {

    @Override
    protected Class<?> getSeriesValueTimeClass() {
        return SeriesValueTime.class;
    }
    
    @Override
    protected void addSpecificRestrictions(Criteria c, GetObservationRequest request) throws CodedException {
        // nothing  to add
    }

    @Override
    public ObservationTimeExtrema getTimeExtremaForSeries(Collection<Series> series, Criterion temporalFilter,
            Session session) {
        return new ObservationTimeExtrema();
    }

    @Override
    public ObservationTimeExtrema getTimeExtremaForSeriesIds(Collection<Long> series, Criterion temporalFilter,
            Session session) {
        return new ObservationTimeExtrema();
    }

}
