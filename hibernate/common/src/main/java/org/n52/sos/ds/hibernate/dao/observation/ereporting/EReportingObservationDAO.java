/**
 * Copyright (C) 2012-2014 52°North Initiative for Geospatial Open Source
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

import java.util.Collection;
import java.util.List;
import java.util.Set;

import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.criterion.Criterion;

import org.n52.sos.aqd.AqdConstants;
import org.n52.sos.aqd.AqdConstants.AssessmentType;
import org.n52.sos.aqd.AqdSamplingPoint;
import org.n52.sos.ds.hibernate.dao.DaoFactory;
import org.n52.sos.ds.hibernate.dao.ereporting.EReportingSamplingPointDAO;
import org.n52.sos.ds.hibernate.dao.ereporting.EReportingSeriesIdentifiers;
import org.n52.sos.ds.hibernate.dao.observation.series.AbstractSeriesDAO;
import org.n52.sos.ds.hibernate.dao.observation.series.AbstractSeriesObservationDAO;
import org.n52.sos.ds.hibernate.entities.ObservationConstellation;
import org.n52.sos.ds.hibernate.entities.ereporting.EReportingSamplingPoint;
import org.n52.sos.ds.hibernate.entities.observation.AbstractObservation;
import org.n52.sos.ds.hibernate.entities.observation.full.BlobObservation;
import org.n52.sos.ds.hibernate.entities.observation.full.BooleanObservation;
import org.n52.sos.ds.hibernate.entities.observation.full.CategoryObservation;
import org.n52.sos.ds.hibernate.entities.observation.full.ComplexObservation;
import org.n52.sos.ds.hibernate.entities.observation.ContextualReferencedObservation;
import org.n52.sos.ds.hibernate.entities.observation.full.CountObservation;
import org.n52.sos.ds.hibernate.entities.observation.full.GeometryObservation;
import org.n52.sos.ds.hibernate.entities.observation.full.NumericObservation;
import org.n52.sos.ds.hibernate.entities.observation.Observation;
import org.n52.sos.ds.hibernate.entities.observation.full.SweDataArrayObservation;
import org.n52.sos.ds.hibernate.entities.observation.TemporalReferencedObservation;
import org.n52.sos.ds.hibernate.entities.observation.full.TextObservation;
import org.n52.sos.ds.hibernate.entities.observation.ereporting.AbstractEReportingObservation;
import org.n52.sos.ds.hibernate.entities.observation.ereporting.ContextualReferencedEReportingObservation;
import org.n52.sos.ds.hibernate.entities.observation.ereporting.full.EReportingBlobObservation;
import org.n52.sos.ds.hibernate.entities.observation.ereporting.full.EReportingBooleanObservation;
import org.n52.sos.ds.hibernate.entities.observation.ereporting.full.EReportingCategoryObservation;
import org.n52.sos.ds.hibernate.entities.observation.ereporting.full.EReportingComplexObservation;
import org.n52.sos.ds.hibernate.entities.observation.ereporting.full.EReportingCountObservation;
import org.n52.sos.ds.hibernate.entities.observation.ereporting.full.EReportingGeometryObservation;
import org.n52.sos.ds.hibernate.entities.observation.ereporting.full.EReportingNumericObservation;
import org.n52.sos.ds.hibernate.entities.observation.ereporting.EReportingSweDataArrayObservation;
import org.n52.sos.ds.hibernate.entities.observation.ereporting.full.EReportingTextObservation;
import org.n52.sos.ds.hibernate.entities.observation.ereporting.TemporalReferencedEReportingObservation;
import org.n52.sos.ds.hibernate.entities.observation.series.AbstractSeriesObservation;
import org.n52.sos.ds.hibernate.entities.observation.series.Series;
import org.n52.sos.ds.hibernate.entities.observation.series.SeriesObservation;
import org.n52.sos.exception.CodedException;
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
import org.n52.sos.w3c.xlink.W3CHrefAttribute;

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

    @SuppressWarnings("unchecked")
    @Override
    protected List<SeriesObservation<?>> getSeriesObservationsFor(GetObservationRequest request,
            Collection<String> features, Criterion filterCriterion, SosIndeterminateTime sosIndeterminateTime,
            Session session) throws HibernateException, OwsExceptionReport {
        return getSeriesObservationCriteriaFor(request, features, filterCriterion, sosIndeterminateTime, session)
                .list();
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<SeriesObservation<?>> getSeriesObservationsFor(Series series, GetObservationRequest request,
            SosIndeterminateTime sosIndeterminateTime, Session session) throws OwsExceptionReport {
        return getSeriesObservationCriteriaFor(series, request, sosIndeterminateTime, session).list();
    }

    @Override
    protected void addObservationIdentifiersToObservation(ObservationIdentifiers observationIdentifiers,
            Observation<?> observation, Session session) throws CodedException {
        EReportingSeriesIdentifiers identifiers = new EReportingSeriesIdentifiers();
        identifiers.setFeatureOfInterest(observationIdentifiers.getFeatureOfInterest());
        identifiers.setObservableProperty(observationIdentifiers.getObservableProperty());
        identifiers.setProcedure(observationIdentifiers.getProcedure());
        if (observationIdentifiers instanceof EReportingObservationIdentifiers) {
            identifiers.setSamplingPoint(((EReportingObservationIdentifiers) observationIdentifiers)
                    .getSamplingPoint());
        }
        AbstractSeriesDAO seriesDAO = DaoFactory.getInstance().getSeriesDAO();
        Series series = seriesDAO.getOrInsertSeries(identifiers, session);
        ((AbstractSeriesObservation) observation).setSeries(series);
        seriesDAO.updateSeriesWithFirstLatestValues(series, observation, session);
    }

    @Override
    protected ObservationIdentifiers createObservationIdentifiers(
            Set<ObservationConstellation> hObservationConstellations) {
        EReportingObservationIdentifiers observationIdentifiers = new EReportingObservationIdentifiers();
        return observationIdentifiers;
    }

    @Override
    protected ObservationIdentifiers addAdditionalObjectsToObservationIdentifiers(
            ObservationIdentifiers observationIdentifiers, OmObservation sosObservation, Session session) {
        if (observationIdentifiers instanceof EReportingObservationIdentifiers) {
            if (sosObservation.isSetParameter()) {
                AqdSamplingPoint samplingPoint = new AqdSamplingPoint();
                List<NamedValue<?>> remove = Lists.newArrayList();
                for (NamedValue<?> namedValue : sosObservation.getParameter()) {
                    if (checkForSamplingPoint(namedValue.getName())) {
                        addSamplingPointParameterValuesToAqdSamplingPoint(samplingPoint, namedValue.getValue());
                        remove.add(namedValue);
                    } else if (checkForAssessmentType(namedValue.getName())) {
                        addAssessmentTypeParameterValuesToAqdSamplingPoint(samplingPoint, namedValue.getValue());
                        remove.add(namedValue);
                    }
                }
                sosObservation.getParameter().removeAll(remove);
                ((EReportingObservationIdentifiers) observationIdentifiers)
                        .setSamplingPoint(new EReportingSamplingPointDAO().getOrInsert(samplingPoint, session));
            }
        }
        return observationIdentifiers;
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
            samplingPoint.setAssessmentType(AssessmentType.fromConceptURI(((ReferenceValue) value).getValue().getHref()));
        } else if (value instanceof HrefAttributeValue) {
            samplingPoint.setAssessmentType(AssessmentType.fromConceptURI(((HrefAttributeValue) value).getValue().getHref()));
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
    @SuppressWarnings("rawtypes")
    protected Class<? extends AbstractObservation> getObservationClass() {
        return AbstractEReportingObservation.class;
    }

    @Override
    protected Class<? extends ContextualReferencedObservation> getObservationInfoClass() {
        return ContextualReferencedEReportingObservation.class;
    }

    @Override
    protected Class<? extends TemporalReferencedObservation> getObservationTimeClass() {
        return TemporalReferencedEReportingObservation.class;
    }

    @Override
    protected Class<? extends BlobObservation> getBlobObservationClass() {
        return EReportingBlobObservation.class;
    }

    @Override
    protected Class<? extends BooleanObservation> getBooleanObservationClass() {
        return EReportingBooleanObservation.class;
    }

    @Override
    protected Class<? extends CategoryObservation> getCategoryObservationClass() {
        return EReportingCategoryObservation.class;
    }

    @Override
    protected Class<? extends CountObservation> getCountObservationClass() {
        return EReportingCountObservation.class;
    }

    @Override
    protected Class<? extends GeometryObservation> getGeometryObservationClass() {
        return EReportingGeometryObservation.class;
    }

    @Override
    protected Class<? extends NumericObservation> getNumericObservationClass() {
        return EReportingNumericObservation.class;
    }

    @Override
    protected Class<? extends SweDataArrayObservation> getSweDataArrayObservationClass() {
        return EReportingSweDataArrayObservation.class;
    }

    @Override
    protected Class<? extends TextObservation> getTextObservationClass() {
        return EReportingTextObservation.class;
    }

    @Override
    protected Class<? extends ComplexObservation> getComplexObservationClass() {
        return EReportingComplexObservation.class;
    }

    protected class EReportingObservationIdentifiers extends ObservationIdentifiers {

        private EReportingSamplingPoint samplingPoint;

        /**
         * @return the samplingPoint
         */
        public EReportingSamplingPoint getSamplingPoint() {
            return samplingPoint;
        }

        /**
         * @param samplingPoint
         *            the samplingPoint to set
         */
        public void setSamplingPoint(EReportingSamplingPoint samplingPoint) {
            this.samplingPoint = samplingPoint;
        }

    }

}
