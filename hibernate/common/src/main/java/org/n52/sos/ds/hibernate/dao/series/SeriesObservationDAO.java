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
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Criterion;
import org.n52.sos.ds.hibernate.entities.series.Series;
import org.n52.sos.ds.hibernate.entities.series.SeriesBlobObservation;
import org.n52.sos.ds.hibernate.entities.series.SeriesBooleanObservation;
import org.n52.sos.ds.hibernate.entities.series.SeriesCategoryObservation;
import org.n52.sos.ds.hibernate.entities.series.SeriesCountObservation;
import org.n52.sos.ds.hibernate.entities.series.SeriesGeometryObservation;
import org.n52.sos.ds.hibernate.entities.series.SeriesNumericObservation;
import org.n52.sos.ds.hibernate.entities.series.SeriesObservation;
import org.n52.sos.ds.hibernate.entities.series.SeriesObservationInfo;
import org.n52.sos.ds.hibernate.entities.series.SeriesObservationTime;
import org.n52.sos.ds.hibernate.entities.series.SeriesSweDataArrayObservation;
import org.n52.sos.ds.hibernate.entities.series.SeriesTextObservation;
import org.n52.sos.exception.CodedException;
import org.n52.sos.ogc.ows.OwsExceptionReport;
import org.n52.sos.ogc.sos.SosConstants.SosIndeterminateTime;
import org.n52.sos.request.GetObservationRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Hibernate data access class for series observations
 * 
 * @since 4.0.0
 * 
 */
public class SeriesObservationDAO extends AbstractSeriesObservationDAO {

    private static final Logger LOGGER = LoggerFactory.getLogger(SeriesObservationDAO.class);

    /**
     * Query series observation for series and offerings
     * 
     * @param series
     *            Series to get values for
     * @param offerings
     *            Offerings to get values for
     * @param session
     *            Hibernate session
     * @return Series observations that fit
     */
    @SuppressWarnings("unchecked")
    public List<SeriesObservation> getSeriesObservationFor(Series series, List<String> offerings, Session session) {
        return getSeriesObservationCriteriaFor(series, offerings, session).list();
    }

    /**
     * Query series obserations for series, temporal filter, and offerings
     * 
     * @param series
     *            Series to get values for
     * @param offerings
     *            Offerings to get values for
     * @param filterCriterion
     * @param session
     *            Hibernate session
     * @return Series observations that fit
     */
    @SuppressWarnings("unchecked")
    public List<SeriesObservation> getSeriesObservationFor(Series series, List<String> offerings,
            Criterion filterCriterion, Session session) {
        return getSeriesObservationCriteriaFor(series, offerings, filterCriterion, session).list();
    }

    /**
     * Query first/latest series obserations for series (and offerings)
     * 
     * @param series
     *            Series to get values for
     * @param offerings
     *            Offerings to get values for
     * @param sosIndeterminateTime
     * @param session
     *            Hibernate session
     * @return Series observations that fit
     */
    @SuppressWarnings("unchecked")
    public List<SeriesObservation> getSeriesObservationForSosIndeterminateTimeFilter(Series series,
            List<String> offerings, SosIndeterminateTime sosIndeterminateTime, Session session) {
        return getSeriesObservationCriteriaForSosIndeterminateTimeFilter(series, offerings, sosIndeterminateTime, session).list();
    }

    /**
     * Query series observations for GetObservation request and features
     * 
     * @param request
     *            GetObservation request
     * @param features
     *            Collection of feature identifiers resolved from the request
     * @param session
     *            Hibernate session
     * @return Series observations that fit
     * @throws OwsExceptionReport
     */
    public List<SeriesObservation> getSeriesObservationsFor(GetObservationRequest request,
            Collection<String> features, Session session) throws OwsExceptionReport {
        return getSeriesObservationsFor(request, features, null, null, session);
    }

    /**
     * Query series observations for GetObservation request, features, and a
     * filter criterion (typically a temporal filter)
     * 
     * @param request
     *            GetObservation request
     * @param features
     *            Collection of feature identifiers resolved from the request
     * @param filterCriterion
     *            Criterion to apply to criteria query (typically a temporal
     *            filter)
     * @param session
     *            Hibernate session
     * @return Series observations that fit
     * @throws OwsExceptionReport
     */
    public List<SeriesObservation> getSeriesObservationsFor(GetObservationRequest request,
            Collection<String> features, Criterion filterCriterion, Session session) throws OwsExceptionReport {
        return getSeriesObservationsFor(request, features, filterCriterion, null, session);
    }

    /**
     * Query series observations for GetObservation request, features, and an
     * indeterminate time (first/latest)
     * 
     * @param request
     *            GetObservation request
     * @param features
     *            Collection of feature identifiers resolved from the request
     * @param sosIndeterminateTime
     *            Indeterminate time to use in a temporal filter (first/latest)
     * @param session
     *            Hibernate session
     * @return Series observations that fit
     * @throws OwsExceptionReport
     */
    public List<SeriesObservation> getSeriesObservationsFor(GetObservationRequest request,
            Collection<String> features, SosIndeterminateTime sosIndeterminateTime, Session session)
            throws OwsExceptionReport {
        return getSeriesObservationsFor(request, features, null, sosIndeterminateTime, session);
    }

    /**
     * Query series observations for GetObservation request, features, and
     * filter criterion (typically a temporal filter) or an indeterminate time
     * (first/latest). This method is private and accepts all possible arguments
     * for request-based getSeriesObservationFor. Other public methods overload
     * this method with sensible combinations of arguments.
     * 
     * @param request
     *            GetObservation request
     * @param features
     *            Collection of feature identifiers resolved from the request
     * @param filterCriterion
     *            Criterion to apply to criteria query (typically a temporal
     *            filter)
     * @param sosIndeterminateTime
     *            Indeterminate time to use in a temporal filter (first/latest)
     * @param session
     * @return Series observations that fit
     * @throws OwsExceptionReport
     */
    @SuppressWarnings("unchecked")
    protected List<SeriesObservation> getSeriesObservationsFor(GetObservationRequest request, Collection<String> features,
            Criterion filterCriterion, SosIndeterminateTime sosIndeterminateTime, Session session) throws OwsExceptionReport {
        return getSeriesObservationCriteriaFor(request, features, filterCriterion, sosIndeterminateTime, session).list();
    }

    @SuppressWarnings("unchecked")
    public List<SeriesObservation> getSeriesObservationsFor(Series series, GetObservationRequest request,
            SosIndeterminateTime sosIndeterminateTime, Session session) throws OwsExceptionReport {
        return getSeriesObservationCriteriaFor(series, request, sosIndeterminateTime, session).list();

    }

    @Override
    protected void addSpecificRestrictions(Criteria c, GetObservationRequest request) throws CodedException {
       // nothing to add
    }

    @Override
    protected Class<?> getObservationClass() {
        return SeriesObservation.class;
    }

    @Override
    protected Class<?> getObservationTimeClass() {
        return SeriesObservationTime.class;
    }

    @Override
    protected Class<?> getObservationInfoClass() {
        return SeriesObservationInfo.class;
    }

    @Override
    protected Class<?> getBlobObservationClass() {
        return SeriesBlobObservation.class;
    }

    @Override
    protected Class<?> getBooleanObservationClass() {
        return SeriesBooleanObservation.class;
    }

    @Override
    protected Class<?> getCategoryObservationClass() {
        return SeriesCategoryObservation.class;
    }

    @Override
    protected Class<?> getCountObservationClass() {
        return SeriesCountObservation.class;
    }

    @Override
    protected Class<?> getGeometryObservationClass() {
        return SeriesGeometryObservation.class;
    }

    @Override
    protected Class<?> getNumericObservationClass() {
        return SeriesNumericObservation.class;
    }

    @Override
    protected Class<?> getSweDataArrayObservationClass() {
        return SeriesSweDataArrayObservation.class;
    }

    @Override
    protected Class<?> getTextObservationClass() {
        return SeriesTextObservation.class;
    }

}
