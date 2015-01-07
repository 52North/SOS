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
package org.n52.sos.ds.hibernate.dao.ereporting;

import java.util.Collection;
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;
import org.n52.sos.aqd.AqdConstants;
import org.n52.sos.aqd.AqdHelper;
import org.n52.sos.aqd.ReportObligationType;
import org.n52.sos.ds.hibernate.dao.series.AbstractSeriesDAO;
import org.n52.sos.ds.hibernate.dao.series.SeriesIdentifiers;
import org.n52.sos.ds.hibernate.entities.ereporting.EReportingAssessmentType;
import org.n52.sos.ds.hibernate.entities.ereporting.EReportingSamplingPoint;
import org.n52.sos.ds.hibernate.entities.ereporting.EReportingSeries;
import org.n52.sos.ds.hibernate.entities.series.Series;
import org.n52.sos.exception.CodedException;
import org.n52.sos.exception.ows.OptionNotSupportedException;
import org.n52.sos.request.GetObservationRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class EReportingSeriesDAO extends AbstractSeriesDAO {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(EReportingSeriesDAO.class);

    @Override
    protected Class<?> getSeriesClass() {
        return EReportingSeries.class;
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<Series> getSeries(GetObservationRequest request, Collection<String> features, Session session) throws CodedException {
        return getSeriesCriteria(request, features, session).list();
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<Series> getSeries(String observedProperty, Collection<String> features, Session session) {
        return getSeriesCriteria(observedProperty, features, session).list();
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<Series> getSeries(Collection<String> procedures, Collection<String> observedProperties,
            Collection<String> features, Session session) {
        return getSeriesCriteria(procedures, observedProperties, features, session).list();
    }

    @Override
    public EReportingSeries getSeriesFor(String procedure, String observableProperty, String featureOfInterest,
            Session session) {
        return (EReportingSeries) getSeriesCriteriaFor(procedure, observableProperty, featureOfInterest, session).uniqueResult();
    }

    @Override
    public EReportingSeries getOrInsertSeries(SeriesIdentifiers identifiers, Session session) throws CodedException {
        return (EReportingSeries) super.getOrInsert(identifiers, session);
    }
    
    /**
     * Add EReportingSamplingPoint restriction to Hibernate Criteria
     * 
     * @param c
     *            Hibernate Criteria to add restriction
     * @param samplingPoint
     *            EReportingSamplingPoint identifier to add
     */
    public void addEReportingSamplingPointToCriteria(Criteria c, String samplingPoint) {
        c.createCriteria(EReportingSeries.SAMPLING_POINT).add(Restrictions.eq(EReportingSamplingPoint.IDENTIFIER, samplingPoint));

    }
    
    /**
     * Add EReportingSamplingPoint restriction to Hibernate Criteria
     * 
     * @param c
     *            Hibernate Criteria to add restriction
     * @param samplingPoint
     *            EReportingSamplingPoint to add
     */
    public void addEReportingSamplingPointToCriteria(Criteria c, EReportingSamplingPoint samplingPoint) {
        c.add(Restrictions.eq(EReportingSeries.SAMPLING_POINT, samplingPoint));
    }

    /**
     * Add EReportingSamplingPoint restriction to Hibernate Criteria
     * 
     * @param c
     *            Hibernate Criteria to add restriction
     * @param samplingPoints
     *            EReportingSamplingPoint identifiers to add
     */
    public void addEReportingSamplingPointToCriteria(Criteria c, Collection<String> samplingPoints) {
        c.createCriteria(EReportingSeries.SAMPLING_POINT).add(Restrictions.in(EReportingSamplingPoint.IDENTIFIER, samplingPoints));
    }

    @Override
    protected void addSpecificRestrictions(Criteria c, GetObservationRequest request) throws CodedException {
        if (request.isSetResponseFormat() && AqdConstants.NS_AQD.equals(request.getResponseFormat())) {
            ReportObligationType flow = AqdHelper.getInstance().getFlow(request.getExtensions());
            if (ReportObligationType.E1A.equals(flow) || ReportObligationType.E2A.equals(flow)) {
                addAssessmentType(c, AqdConstants.AssessmentType.Fixed.name());
            } else if (ReportObligationType.E1B.equals(flow)) {
                addAssessmentType(c, AqdConstants.AssessmentType.Model.name());
            } else {
                throw new OptionNotSupportedException().withMessage("The requested e-Reporting flow %s is not supported!", flow.name());
            }
        }
    }
    
    private void addAssessmentType(Criteria c, String assessmentType) {
        c.createCriteria(EReportingSeries.SAMPLING_POINT).createCriteria(EReportingSamplingPoint.ASSESSMENTTYPE).
        add(Restrictions.ilike(EReportingAssessmentType.ASSESSMENT_TYPE, assessmentType));
    }
    
}
