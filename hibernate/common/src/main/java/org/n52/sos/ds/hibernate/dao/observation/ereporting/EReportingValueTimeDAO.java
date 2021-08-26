/*
 * Copyright (C) 2012-2021 52°North Spatial Information Research GmbH
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
package org.n52.sos.ds.hibernate.dao.observation.ereporting;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.ProjectionList;
import org.hibernate.criterion.Projections;
import org.n52.series.db.beans.DataEntity;
import org.n52.series.db.beans.DatasetEntity;
import org.n52.shetland.ogc.ows.exception.OwsExceptionReport;
import org.n52.shetland.ogc.sos.request.GetObservationRequest;
import org.n52.shetland.util.DateTimeHelper;
import org.n52.sos.ds.hibernate.dao.DaoFactory;
import org.n52.sos.ds.hibernate.dao.ereporting.EReportingDaoHelper;
import org.n52.sos.ds.hibernate.dao.observation.ValuedObservationFactory;
import org.n52.sos.ds.hibernate.dao.observation.series.AbstractSeriesValueTimeDAO;
import org.n52.sos.ds.hibernate.util.HibernateHelper;
import org.n52.sos.ds.hibernate.util.ObservationTimeExtrema;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class EReportingValueTimeDAO extends AbstractSeriesValueTimeDAO implements EReportingDaoHelper {
    private static final Logger LOGGER = LoggerFactory.getLogger(EReportingValueTimeDAO.class);

    private final Set<Integer> verificationFlags;

    private final Set<Integer> validityFlags;

    public EReportingValueTimeDAO(Set<Integer> verificationFlags, Set<Integer> validityFlags, DaoFactory daoFactory) {
        super(daoFactory);
        this.verificationFlags =
                verificationFlags != null ? new LinkedHashSet<>(verificationFlags) : new LinkedHashSet<>();
        this.validityFlags = validityFlags != null ? new LinkedHashSet<>(validityFlags) : new LinkedHashSet<>();
    }

    @Override
    protected void addSpecificRestrictions(Criteria c, GetObservationRequest request, StringBuilder logArgs)
            throws OwsExceptionReport {
        // add quality restrictions
        addValidityAndVerificationRestrictions(c, request, logArgs);
    }

    @Override
    public ObservationTimeExtrema getTimeExtremaForSeries(Collection<DatasetEntity> series,
            Criterion temporalFilterCriterion, Session session) throws OwsExceptionReport {
        Criteria c = getSeriesValueCriteriaFor(series, temporalFilterCriterion, null, session);
        addPhenomenonTimeProjection(c);
        LOGGER.trace("QUERY getTimeExtremaForSeries(series, temporalFilter): {}", HibernateHelper.getSqlString(c));
        return parseMinMaxPhenomenonTime((Object[]) c.uniqueResult());
    }

    @Override
    public ObservationTimeExtrema getTimeExtremaForSeriesIds(Collection<Long> series,
            Criterion temporalFilterCriterion, Session session) throws OwsExceptionReport {
        Criteria c = getSeriesValueCriteriaForSeriesIds(series, temporalFilterCriterion, null, session);
        addPhenomenonTimeProjection(c);
        LOGGER.trace("QUERY getTimeExtremaForSeriesIds(series, temporalFilter): {}", HibernateHelper.getSqlString(c));
        return parseMinMaxPhenomenonTime((Object[]) c.uniqueResult());
    }

    private ObservationTimeExtrema parseMinMaxPhenomenonTime(Object[] result) {
        ObservationTimeExtrema ote = new ObservationTimeExtrema();
        if (result != null) {
            ote.setMinPhenomenonTime(DateTimeHelper.makeDateTime(result[0]));
            ote.setMaxPhenomenonTime(DateTimeHelper.makeDateTime(result[1]));
        }
        return ote;
    }

    @Override
    public Set<Integer> getVerificationFlags() {
        return Collections.unmodifiableSet(verificationFlags);
    }

    @Override
    public Set<Integer> getValidityFlags() {
        return Collections.unmodifiableSet(validityFlags);
    }

    private void addPhenomenonTimeProjection(Criteria c) {
        ProjectionList projectionList = Projections.projectionList();
        projectionList.add(Projections.min(DataEntity.PROPERTY_SAMPLING_TIME_START));
        projectionList.add(Projections.max(DataEntity.PROPERTY_SAMPLING_TIME_END));
        c.setProjection(projectionList);
    }

    @Override
    protected ValuedObservationFactory getValuedObservationFactory() {
        return EReportingValuedObservationFactory.getInstance();
    }

}
