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
package org.n52.sos.ds.hibernate.dao.observation.ereporting;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.criterion.Subqueries;
import org.n52.series.db.beans.DataEntity;
import org.n52.series.db.beans.DatasetEntity;
import org.n52.series.db.beans.ereporting.EReportingAssessmentTypeEntity;
import org.n52.series.db.beans.ereporting.EReportingProfileDataEntity;
import org.n52.series.db.beans.ereporting.EReportingSamplingPointEntity;
import org.n52.shetland.aqd.AqdConstants;
import org.n52.shetland.aqd.AqdConstants.AssessmentType;
import org.n52.shetland.aqd.AqdSamplingPoint;
import org.n52.shetland.aqd.ReportObligationType;
import org.n52.shetland.aqd.ReportObligations;
import org.n52.shetland.ogc.gml.AbstractFeature;
import org.n52.shetland.ogc.gml.CodeType;
import org.n52.shetland.ogc.gml.ReferenceType;
import org.n52.shetland.ogc.gml.time.IndeterminateValue;
import org.n52.shetland.ogc.om.NamedValue;
import org.n52.shetland.ogc.om.OmObservation;
import org.n52.shetland.ogc.om.values.HrefAttributeValue;
import org.n52.shetland.ogc.om.values.ReferenceValue;
import org.n52.shetland.ogc.om.values.TextValue;
import org.n52.shetland.ogc.om.values.Value;
import org.n52.shetland.ogc.ows.exception.OptionNotSupportedException;
import org.n52.shetland.ogc.ows.exception.OwsExceptionReport;
import org.n52.shetland.ogc.sos.request.GetObservationRequest;
import org.n52.shetland.util.CollectionHelper;
import org.n52.shetland.w3c.xlink.W3CHrefAttribute;
import org.n52.sos.ds.hibernate.dao.DaoFactory;
import org.n52.sos.ds.hibernate.dao.ereporting.EReportingDaoHelper;
import org.n52.sos.ds.hibernate.dao.ereporting.EReportingObservationContext;
import org.n52.sos.ds.hibernate.dao.ereporting.EReportingSamplingPointDAO;
import org.n52.sos.ds.hibernate.dao.observation.ObservationContext;
import org.n52.sos.ds.hibernate.dao.observation.ObservationFactory;
import org.n52.sos.ds.hibernate.dao.observation.series.AbstractSeriesObservationDAO;
import org.n52.sos.ds.hibernate.util.QueryHelper;

import com.google.common.base.Strings;
import com.google.common.collect.Lists;

public class EReportingObservationDAO extends AbstractSeriesObservationDAO implements EReportingDaoHelper {

    private static final String LOG_TEMPLATE = "The requested e-Reporting flow %s is not supported!";

    private final Set<Integer> verificationFlags;

    private final Set<Integer> validityFlags;

    public EReportingObservationDAO(Set<Integer> verificationFlags, Set<Integer> validityFlags,
            DaoFactory daoFactory) {
        super(daoFactory);
        this.verificationFlags =
                verificationFlags != null ? new LinkedHashSet<>(verificationFlags) : new LinkedHashSet<>();
        this.validityFlags = validityFlags != null ? new LinkedHashSet<>(validityFlags) : new LinkedHashSet<>();
    }

    @Override
    public Set<Integer> getVerificationFlags() {
        return Collections.unmodifiableSet(verificationFlags);
    }

    @Override
    public Set<Integer> getValidityFlags() {
        return Collections.unmodifiableSet(validityFlags);
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<DataEntity<?>> getSeriesObservationFor(DatasetEntity series, List<String> offerings, Session session) {
        return getSeriesObservationCriteriaFor(series, offerings, session).list();
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<DataEntity<?>> getSeriesObservationFor(DatasetEntity series, List<String> offerings,
            Criterion filterCriterion, Session session) {
        return getSeriesObservationCriteriaFor(series, offerings, filterCriterion, session).list();
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<DataEntity<?>> getSeriesObservationForExtendedIndeterminateTimeFilter(DatasetEntity series,
            List<String> offerings, IndeterminateValue sosIndeterminateTime, Session session) {
        return getSeriesObservationCriteriaForIndeterminateTimeFilter(series, offerings, sosIndeterminateTime, session)
                .list();
    }

    @Override
    public List<DataEntity<?>> getSeriesObservationsFor(GetObservationRequest request, Collection<String> features,
            Session session) throws OwsExceptionReport {
        return getSeriesObservationsFor(request, features, null, null, session);
    }

    @Override
    public List<DataEntity<?>> getSeriesObservationsFor(GetObservationRequest request, Collection<String> features,
            Criterion filterCriterion, Session session) throws OwsExceptionReport {
        return getSeriesObservationsFor(request, features, filterCriterion, null, session);
    }

    @Override
    public List<DataEntity<?>> getSeriesObservationsFor(GetObservationRequest request, Collection<String> features,
            IndeterminateValue sosIndeterminateTime, Session session) throws OwsExceptionReport {
        return getSeriesObservationsFor(request, features, null, sosIndeterminateTime, session);
    }

    @Override
    protected List<DataEntity<?>> getSeriesObservationsFor(GetObservationRequest request, Collection<String> features,
            Criterion filterCriterion, IndeterminateValue sosIndeterminateTime, Session session)
            throws HibernateException, OwsExceptionReport {
        if (CollectionHelper.isNotEmpty(features)) {
            List<DataEntity<?>> observations = new ArrayList<>();
            for (List<String> ids : QueryHelper.getListsForIdentifiers(features)) {
                observations.addAll(
                        getSeriesObservationCriteriaFor(request, ids, filterCriterion, sosIndeterminateTime, session));
            }
            return observations;
        } else {
            return getSeriesObservationCriteriaFor(request, features, filterCriterion, sosIndeterminateTime, session);
        }
    }

    @Override
    public List<DataEntity<?>> getSeriesObservationsFor(DatasetEntity series, GetObservationRequest request,
            IndeterminateValue sosIndeterminateTime, Session session) throws OwsExceptionReport {
        return getSeriesObservationCriteriaFor(series, request, sosIndeterminateTime, session);
    }

    @Override
    protected void addSpecificRestrictions(Criteria c, GetObservationRequest request, StringBuilder logArgs)
            throws OwsExceptionReport {
        if (request.isSetResponseFormat() && AqdConstants.NS_AQD.equals(request.getResponseFormat())) {
            ReportObligationType flow = ReportObligations.getFlow(request.getExtensions());
            if (flow == null) {
                throw new OptionNotSupportedException().withMessage(LOG_TEMPLATE, "null");
            } else {
                switch (flow) {
                    case E1A:
                    case E2A:
                        addAssessmentType(c, AqdConstants.AssessmentType.Fixed.name());
                        break;
                    case E1B:
                        addAssessmentType(c, AqdConstants.AssessmentType.Model.name());
                        break;
                    default:
                        throw new OptionNotSupportedException().withMessage(LOG_TEMPLATE, flow.name());
                }
            }
            // add quality restrictions
            addValidityAndVerificationRestrictions(c, request, logArgs);
        }
    }

    private void addAssessmentType(Criteria c, String assessmentType) {
        final DetachedCriteria detachedCriteria = DetachedCriteria.forClass(DatasetEntity.class);
        detachedCriteria.add(Restrictions.eq(DatasetEntity.PROPERTY_DELETED, false));
        detachedCriteria.createCriteria(getSamplingPointAssociationPath())
                .createCriteria(EReportingSamplingPointEntity.ASSESSMENTTYPE)
                .add(Restrictions.ilike(EReportingAssessmentTypeEntity.ASSESSMENT_TYPE, assessmentType));
        detachedCriteria.setProjection(Projections.distinct(Projections.property(DatasetEntity.PROPERTY_ID)));
        c.add(Subqueries.propertyIn(DatasetEntity.PROPERTY_ID, detachedCriteria));
    }

    @Override
    protected ObservationContext createObservationContext() {
        return new EReportingObservationContext();
    }

    @Override
    protected ObservationContext fillObservationContext(ObservationContext ctx, OmObservation omObservation,
            Session session) {
        if (ctx instanceof EReportingObservationContext) {
            EReportingObservationContext ectx = (EReportingObservationContext) ctx;
            AqdSamplingPoint samplingPoint = null;
            if (omObservation.isSetParameter()) {
                samplingPoint = new AqdSamplingPoint();
                List<NamedValue<?>> remove = Lists.newArrayList();
                for (NamedValue<?> namedValue : omObservation.getParameter()) {
                    if (checkForSamplingPoint(namedValue.getName())) {
                        addSamplingPointParameterValuesToAqdSamplingPoint(samplingPoint, namedValue.getValue());
                        remove.add(namedValue);
                    } else if (checkForAssessmentType(namedValue.getName())) {
                        addAssessmentTypeParameterValuesToAqdSamplingPoint(samplingPoint, namedValue.getValue());
                        remove.add(namedValue);
                    }
                }
                omObservation.getParameter()
                        .removeAll(remove);
                EReportingSamplingPointDAO dao = new EReportingSamplingPointDAO(getDaoFactory());
                ectx.setSamplingPoint(dao.getOrInsert(samplingPoint, session));
            }
            if (samplingPoint == null && omObservation.getObservationConstellation()
                    .isSetFeatureOfInterest()) {
                samplingPoint = new AqdSamplingPoint();
                AbstractFeature featureOfInterest = omObservation.getObservationConstellation()
                        .getFeatureOfInterest();
                addSamplingPointParameterValuesToAqdSamplingPoint(samplingPoint,
                        new ReferenceValue(new ReferenceType(
                                featureOfInterest.getIdentifier(), featureOfInterest.isSetName()
                                        ? featureOfInterest.getFirstName()
                                                .getValue()
                                        : "")));
                EReportingSamplingPointDAO dao = new EReportingSamplingPointDAO(getDaoFactory());
                ectx.setSamplingPoint(dao.getOrInsert(samplingPoint, session));
            }
            if (samplingPoint != null && samplingPoint.getAssessmentType() == null) {
                addAssessmentTypeParameterValuesToAqdSamplingPoint(samplingPoint,
                        new TextValue(AssessmentType.Fixed.name()));
            }
        }
        return ctx;
    }

    @Override
    protected DatasetEntity addObservationContextToObservation(ObservationContext ctx, DataEntity<?> observation,
            Session session) throws OwsExceptionReport {
        observation.setEreportingProfile(new EReportingProfileDataEntity());
        return super.addObservationContextToObservation(ctx, observation, session);
    }

    @Override
    protected Criteria addAdditionalObservationIdentification(Criteria c, OmObservation observation) {
        String identifier = getSamplingPointIdentifier(observation);
        if (!Strings.isNullOrEmpty(identifier)) {
            c.createCriteria(getSamplingPointAssociationPath())
                    .add(Restrictions.eq(EReportingSamplingPointEntity.IDENTIFIER, identifier));
        }
        return c;
    }

    private String getSamplingPointIdentifier(OmObservation observation) {
        if (observation.isSetParameter()) {
            for (NamedValue<?> namedValue : observation.getParameter()) {
                Value<?> value = namedValue.getValue();
                if (value instanceof ReferenceValue) {
                    return ((ReferenceValue) value).getValue()
                            .getHref();
                } else if (value instanceof HrefAttributeValue) {
                    return ((HrefAttributeValue) value).getValue()
                            .getHref();
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
        return name.isSetHref() && AqdConstants.ProcessParameter.SamplingPoint.getConceptURI()
                .equals(name.getHref());
    }

    private boolean checkForAssessmentType(ReferenceType name) {
        return name.isSetHref() && AqdConstants.ProcessParameter.AssessmentType.getConceptURI()
                .equals(name.getHref());
    }

    @Override
    public ObservationFactory getObservationFactory() {
        return EReportingObservationFactory.getInstance();
    }

}
