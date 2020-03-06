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
package org.n52.sos.ds.hibernate.dao.observation.ereporting;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.criterion.Subqueries;
import org.n52.sos.aqd.AqdConstants;
import org.n52.sos.aqd.AqdConstants.AssessmentType;
import org.n52.sos.aqd.AqdHelper;
import org.n52.sos.aqd.AqdSamplingPoint;
import org.n52.sos.aqd.ReportObligationType;
import org.n52.sos.ds.hibernate.dao.DaoFactory;
import org.n52.sos.ds.hibernate.dao.ereporting.EReportingDaoHelper;
import org.n52.sos.ds.hibernate.dao.ereporting.EReportingObservationContext;
import org.n52.sos.ds.hibernate.dao.ereporting.EReportingSamplingPointDAO;
import org.n52.sos.ds.hibernate.dao.observation.ObservationContext;
import org.n52.sos.ds.hibernate.dao.observation.ObservationFactory;
import org.n52.sos.ds.hibernate.dao.observation.series.AbstractSeriesDAO;
import org.n52.sos.ds.hibernate.dao.observation.series.AbstractSeriesObservationDAO;
import org.n52.sos.ds.hibernate.entities.ereporting.EReportingAssessmentType;
import org.n52.sos.ds.hibernate.entities.ereporting.EReportingSamplingPoint;
import org.n52.sos.ds.hibernate.entities.observation.Observation;
import org.n52.sos.ds.hibernate.entities.observation.ereporting.EReportingSeries;
import org.n52.sos.ds.hibernate.entities.observation.series.AbstractSeriesObservation;
import org.n52.sos.ds.hibernate.entities.observation.series.Series;
import org.n52.sos.ds.hibernate.entities.observation.series.SeriesObservation;
import org.n52.sos.ds.hibernate.util.QueryHelper;
import org.n52.sos.exception.CodedException;
import org.n52.sos.exception.ows.OptionNotSupportedException;
import org.n52.sos.ogc.gml.CodeType;
import org.n52.sos.ogc.gml.ReferenceType;
import org.n52.sos.ogc.om.NamedValue;
import org.n52.sos.ogc.om.OmObservation;
import org.n52.sos.ogc.om.values.HrefAttributeValue;
import org.n52.sos.ogc.om.values.ReferenceValue;
import org.n52.sos.ogc.om.values.Value;
import org.n52.sos.ogc.ows.OwsExceptionReport;
import org.n52.sos.ogc.sos.SosConstants.SosIndeterminateTime;
import org.n52.sos.request.GetObservationRequest;
import org.n52.sos.util.CollectionHelper;
import org.n52.sos.w3c.xlink.W3CHrefAttribute;

import com.google.common.base.Strings;
import com.google.common.collect.Lists;

public class EReportingObservationDAO extends AbstractSeriesObservationDAO {

    @SuppressWarnings("unchecked")
    @Override
    public List<SeriesObservation<?>> getSeriesObservationFor(Series series, List<String> offerings, Session session) {
        return getSeriesObservationCriteriaFor(series, offerings, session).list();
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<SeriesObservation<?>> getSeriesObservationFor(Series series, List<String> offerings,
            Criterion filterCriterion, Session session) {
        return getSeriesObservationCriteriaFor(series, offerings, filterCriterion, session).list();
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<SeriesObservation<?>> getSeriesObservationForSosIndeterminateTimeFilter(Series series,
            List<String> offerings, SosIndeterminateTime sosIndeterminateTime, Session session) {
        return getSeriesObservationCriteriaForSosIndeterminateTimeFilter(series, offerings, sosIndeterminateTime,
                session).list();
    }

    @Override
    public List<SeriesObservation<?>> getSeriesObservationsFor(GetObservationRequest request,
            Collection<String> features, Session session) throws OwsExceptionReport {
        return getSeriesObservationsFor(request, features, null, null, session);
    }

    @Override
    public List<SeriesObservation<?>> getSeriesObservationsFor(GetObservationRequest request,
            Collection<String> features, Criterion filterCriterion, Session session) throws OwsExceptionReport {
        return getSeriesObservationsFor(request, features, filterCriterion, null, session);
    }

    @Override
    public List<SeriesObservation<?>> getSeriesObservationsFor(GetObservationRequest request,
            Collection<String> features, SosIndeterminateTime sosIndeterminateTime, Session session)
            throws OwsExceptionReport {
        return getSeriesObservationsFor(request, features, null, sosIndeterminateTime, session);
    }

    @Override
    protected List<SeriesObservation<?>> getSeriesObservationsFor(GetObservationRequest request,
            Collection<String> features, Criterion filterCriterion, SosIndeterminateTime sosIndeterminateTime,
            Session session) throws HibernateException, OwsExceptionReport {
        if (CollectionHelper.isNotEmpty(features)) {
            List<SeriesObservation<?>> observations = new ArrayList<>();
            for (List<String> ids : QueryHelper.getListsForIdentifiers(features)) {
                observations.addAll(getSeriesObservationCriteriaFor(request, ids, filterCriterion, sosIndeterminateTime, session));
            }
            return observations;
        } else {
            return getSeriesObservationCriteriaFor(request, features, filterCriterion, sosIndeterminateTime, session);
        }
    }

    @Override
    public List<SeriesObservation<?>> getSeriesObservationsFor(Series series, GetObservationRequest request,
            SosIndeterminateTime sosIndeterminateTime, Session session) throws OwsExceptionReport {
        return getSeriesObservationCriteriaFor(series, request, sosIndeterminateTime, session);
    }

    @Override
    protected void addSpecificRestrictions(Criteria c, GetObservationRequest request)
            throws CodedException {
        if (request.isSetResponseFormat() && AqdConstants.NS_AQD.equals(request.getResponseFormat())) {
            ReportObligationType flow = AqdHelper.getInstance().getFlow(request.getExtensions());
            if (ReportObligationType.E1A.equals(flow) || ReportObligationType.E2A.equals(flow)) {
                addAssessmentType(c, AqdConstants.AssessmentType.Fixed.name());
            } else if (ReportObligationType.E1B.equals(flow)) {
                addAssessmentType(c, AqdConstants.AssessmentType.Model.name());
            } else {
                throw new OptionNotSupportedException().withMessage("The requested e-Reporting flow %s is not supported!",
                        flow.name());
            }
         // add quality restrictions
            EReportingDaoHelper.addValidityAndVerificationRestrictions(c, request);
        }
    }

    private void addAssessmentType(Criteria c, String assessmentType) {
        final DetachedCriteria detachedCriteria = DetachedCriteria.forClass(EReportingSeries.class);
        detachedCriteria.add(Restrictions.eq(Series.DELETED, false));
        detachedCriteria.add(Restrictions.eq(Series.PUBLISHED, true));
        detachedCriteria.createCriteria(EReportingSeries.SAMPLING_POINT).createCriteria(EReportingSamplingPoint.ASSESSMENTTYPE).
        add(Restrictions.ilike(EReportingAssessmentType.ASSESSMENT_TYPE, assessmentType));
        detachedCriteria.setProjection(Projections.distinct(Projections.property(Series.ID)));
        c.add(Subqueries.propertyIn(Series.ID, detachedCriteria));
    }

    @Override
    protected void addObservationContextToObservation(ObservationContext ctx,
            Observation<?> observation, Session session) throws CodedException {
        AbstractSeriesDAO seriesDAO = DaoFactory.getInstance().getSeriesDAO();
        Series series = seriesDAO.getOrInsertSeries(ctx, session);
        ((AbstractSeriesObservation) observation).setSeries(series);
        seriesDAO.updateSeriesWithFirstLatestValues(series, observation, session);
    }

    @Override
    protected ObservationContext createObservationContext() {
        return new EReportingObservationContext();
    }

    @Override
    protected ObservationContext fillObservationContext(
            ObservationContext ctx, OmObservation sosObservation, Session session) {
        if (ctx instanceof EReportingObservationContext) {
            boolean samplingPointAdded = false;
            boolean assessmentTypeAdded = false;
            AqdSamplingPoint samplingPoint = new AqdSamplingPoint();
            if (sosObservation.isSetParameter()) {
                List<NamedValue<?>> remove = Lists.newArrayList();
                for (NamedValue<?> namedValue : sosObservation.getParameter()) {
                    if (checkForSamplingPoint(namedValue.getName())) {
                        addSamplingPointParameterValuesToAqdSamplingPoint(samplingPoint, namedValue.getValue());
                        remove.add(namedValue);
                        samplingPointAdded = true;
                    } else if (checkForAssessmentType(namedValue.getName())) {
                        addAssessmentTypeParameterValuesToAqdSamplingPoint(samplingPoint, namedValue.getValue());
                        remove.add(namedValue);
                        assessmentTypeAdded = true;
                    }
                }
                sosObservation.getParameter().removeAll(remove);
                
            }
            if (!samplingPointAdded) {
                addSamplingPointParameterValuesToAqdSamplingPoint(samplingPoint, new ReferenceValue(new ReferenceType(ctx.getFeatureOfInterest().getIdentifier(), ctx.getFeatureOfInterest().getName())));
            }
            if (!assessmentTypeAdded) {
                addAssessmentTypeParameterValuesToAqdSamplingPoint(samplingPoint, new ReferenceValue(new ReferenceType(AssessmentType.Fixed.getConceptURI())));
            }
            ((EReportingObservationContext) ctx).setSamplingPoint(new EReportingSamplingPointDAO().getOrInsert(samplingPoint, session));
        }
        return ctx;
    }

    @Override
    protected Criteria addAdditionalObservationIdentification(Criteria c, OmObservation observation) {
        String identifier = getSamplingPointIdentifier(observation);
        if (!Strings.isNullOrEmpty(identifier)) {
            c.createCriteria(EReportingSeries.SAMPLING_POINT)
            .add(Restrictions.eq(EReportingSamplingPoint.IDENTIFIER, identifier));
        }
        return c;
    }


    private String getSamplingPointIdentifier(OmObservation observation) {
        if (observation.isSetParameter()) {
            for (NamedValue<?> namedValue : observation.getParameter()) {
                Value<?> value = namedValue.getValue();
                if (value instanceof ReferenceValue) {
                    return ((ReferenceValue) value).getValue().getHref();
                } else if (value instanceof HrefAttributeValue) {
                    return ((HrefAttributeValue) value).getValue().getHref();
                }
            }
        }
        return null;
    }

    private AqdSamplingPoint addSamplingPointParameterValuesToAqdSamplingPoint(AqdSamplingPoint samplingPoint,
            Value<?> value) {
        if (value instanceof ReferenceValue) {
            ReferenceType referenceType = ((ReferenceValue) value).getValue();
            samplingPoint.setIdentifier(referenceType.getHref());
            if (referenceType.isSetTitle()) {
                samplingPoint.setName(new CodeType(referenceType.getTitle()));
            }
        } else if (value instanceof HrefAttributeValue) {
            W3CHrefAttribute hrefAttribute = ((HrefAttributeValue) value).getValue();
            samplingPoint.setIdentifier(hrefAttribute.getHref());
        }
        return samplingPoint;
    }

    private AqdSamplingPoint addAssessmentTypeParameterValuesToAqdSamplingPoint(AqdSamplingPoint samplingPoint,
            Value<?> value) {
        if (value instanceof ReferenceValue) {
            samplingPoint.setAssessmentType(AssessmentType.fromConceptURI(((ReferenceValue) value).getValue()
                    .getHref()));
        } else if (value instanceof HrefAttributeValue) {
            samplingPoint.setAssessmentType(AssessmentType.fromConceptURI(((HrefAttributeValue) value).getValue()
                    .getHref()));
        }
        return samplingPoint;
    }

    private boolean checkForSamplingPoint(ReferenceType name) {
        return name.isSetHref() && AqdConstants.ProcessParameter.SamplingPoint.getConceptURI().equals(name.getHref());
    }

    private boolean checkForAssessmentType(ReferenceType name) {
        return name.isSetHref() && AqdConstants.ProcessParameter.AssessmentType.getConceptURI().equals(name.getHref());
    }

    @Override
    public ObservationFactory getObservationFactory() {
        return EReportingObservationFactory.getInstance();
    }
}
