/**
 * Copyright (C) 2012-2017 52°North Initiative for Geospatial Open Source
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
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.n52.sos.ds.hibernate.dao.observation.ObservationContext;
import org.n52.sos.ds.hibernate.entities.observation.series.Series;
import org.n52.sos.exception.CodedException;
import org.n52.sos.ogc.ows.OwsExceptionReport;
import org.n52.sos.request.GetObservationByIdRequest;
import org.n52.sos.request.GetObservationRequest;

/**
 * Hibernate data access class for series
 *
 * @since 4.0.0
 *
 */
public class SeriesDAO extends AbstractSeriesDAO {

    @Override
    @SuppressWarnings("unchecked")
    public List<Series> getSeries(GetObservationRequest request, Collection<String> features, Session session) throws OwsExceptionReport {
        return getSeriesCriteria(request, features, session).list();
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<Series> getSeries(GetObservationByIdRequest request, Session session) throws OwsExceptionReport {
        return getSeriesCriteria(request, session).list();
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<Series> getSeries(Collection<String> procedures, Collection<String> observedProperties,
            Collection<String> features, Session session) {
        return getSeriesCriteria(procedures, observedProperties, features, session).list();
    }
    
    @Override
    @SuppressWarnings("unchecked")
    public List<Series> getSeries(Collection<String> procedures, Collection<String> observedProperties,
            Collection<String> features, Collection<String> offerings, Session session) {
        return getSeriesCriteria(procedures, observedProperties, features, offerings, session).list();
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<Series> getSeries(String observedProperty, Collection<String> features, Session session) {
        return getSeriesCriteria(observedProperty, features, session).list();
    }
    
    @Override
    @SuppressWarnings("unchecked")
    public List<Series> getSeries(String procedure, String observedProperty, String offering, Collection<String> features, Session session) {
        return getSeriesCriteria(procedure, observedProperty, offering, features, session).list();
    }

    @Override
    public Series getSeriesFor(String procedure, String observableProperty, String featureOfInterest, Session session) {
        return (Series) getSeriesCriteriaFor(procedure, observableProperty, featureOfInterest, session).uniqueResult();
    }

    @Override
    public Series getOrInsertSeries(ObservationContext identifiers, final Session session) throws CodedException {
        return getOrInsert(identifiers, session);
    }

    @Override
    public Class <?>getSeriesClass() {
        return Series.class;
    }

    @Override
    protected void addSpecificRestrictions(Criteria c, GetObservationRequest request) {
        // nothing to add
    }


}
