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
package org.n52.sos.ds.hibernate.util;

import java.util.Collections;
import java.util.Date;
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;
import org.joda.time.DateTime;
import org.n52.sos.ds.hibernate.dao.DaoFactory;
import org.n52.sos.ds.hibernate.dao.observation.AbstractObservationDAO;
import org.n52.sos.ds.hibernate.dao.observation.ObservationFactory;
import org.n52.sos.ds.hibernate.dao.observation.series.SeriesObservationFactory;
import org.n52.sos.ds.hibernate.entities.Codespace;
import org.n52.sos.ds.hibernate.entities.FeatureOfInterestType;
import org.n52.sos.ds.hibernate.entities.ObservableProperty;
import org.n52.sos.ds.hibernate.entities.ObservationType;
import org.n52.sos.ds.hibernate.entities.Offering;
import org.n52.sos.ds.hibernate.entities.Procedure;
import org.n52.sos.ds.hibernate.entities.ProcedureDescriptionFormat;
import org.n52.sos.ds.hibernate.entities.TOffering;
import org.n52.sos.ds.hibernate.entities.TProcedure;
import org.n52.sos.ds.hibernate.entities.Unit;
import org.n52.sos.ds.hibernate.entities.ValidProcedureTime;
import org.n52.sos.ds.hibernate.entities.ereporting.EReportingAssessmentType;
import org.n52.sos.ds.hibernate.entities.ereporting.EReportingSamplingPoint;
import org.n52.sos.ds.hibernate.entities.feature.FeatureOfInterest;
import org.n52.sos.ds.hibernate.entities.observation.Observation;
import org.n52.sos.ds.hibernate.entities.observation.ereporting.AbstractEReportingObservation;
import org.n52.sos.ds.hibernate.entities.observation.ereporting.EReportingSeries;
import org.n52.sos.ds.hibernate.entities.observation.full.BooleanObservation;
import org.n52.sos.ds.hibernate.entities.observation.legacy.AbstractLegacyObservation;
import org.n52.sos.ds.hibernate.entities.observation.series.AbstractSeriesObservation;
import org.n52.sos.ds.hibernate.entities.observation.series.Series;
import org.n52.sos.ogc.ows.OwsExceptionReport;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;


public class HibernateObservationBuilder {
    public static final String CODESPACE = "Codespace";
    public static final String UNIT = "Unit";
    public static final String OFFERING_1 = "Offering1";
    public static final String OFFERING_2 = "Offering2";
    public static final String FEATURE_OF_INTEREST = "FeatureOfInterest";
    public static final String OBSERVABLE_PROPERTY = "ObservableProperty";
    public static final String PROCEDURE_DESCRIPTION_FORMAT = "ProcedureDescriptionFormat";
    public static final String FEATURE_OF_INTEREST_TYPE = "FeatureOfInterestType";
    public static final String OBSERVATION_TYPE = "ObservationType";
    public static final String EREPORTING_SAMPLING_POINT = "samplingPoint";
    public static final String EREPORTING_ASSESSMENT_TYPE = "assessmentType";

    private final Session session;

    public HibernateObservationBuilder(Session session) {
        this.session = session;
    }

    public Observation<?> createObservation(Observation<?> observation, String id, Date phenomenonTimeStart, Date phenomenonTimeEnd, Date resultTime,
            Date validTimeStart, Date validTimeEnd) throws OwsExceptionReport {
        observation.setDeleted(false);
        observation.setIdentifier(id);
        observation.setPhenomenonTimeStart(phenomenonTimeStart);
        observation.setPhenomenonTimeEnd(phenomenonTimeEnd);
        observation.setResultTime(resultTime);
        observation.setValidTimeStart(validTimeStart);
        observation.setValidTimeEnd(validTimeEnd);
        observation.setOfferings(Sets.newHashSet(getOffering1(), getOffering2()));
        observation.setUnit(getUnit());
        observation.setCodespace(getCodespace());
        session.save(observation);
        session.flush();
        return observation;
    }

    public List<Observation<?>> createObservation(String id, DateTime phenomenonTimeStart, DateTime phenomenonTimeEnd,
            DateTime resultTime, DateTime validTimeStart, DateTime validTimeEnd) throws OwsExceptionReport {
        List<Observation<?>> observations = Lists.newArrayList(); 
        for (Offering offering : getOfferings()) {
             observations.add(createObservation(createObservation(offering), id, phenomenonTimeStart != null ? phenomenonTimeStart.toDate() : null,
                    phenomenonTimeEnd != null ? phenomenonTimeEnd.toDate() : null,
                    resultTime != null ? resultTime.toDate() : null, validTimeStart != null ? validTimeStart.toDate()
                            : null, validTimeEnd != null ? validTimeEnd.toDate() : null));
        }
        return observations;
    }

    public List<Observation<?>> createObservation(String id, DateTime s, DateTime e) throws OwsExceptionReport {
        return createObservation(id, s, e, s, s, e);
    }

    public List<Observation<?>> createObservation(String id, DateTime s) throws OwsExceptionReport {
        return createObservation(id, s, s, s, s, s);
    }

    public List<Observation<?>> createObservation(Enum<?> id, DateTime phenomenonTimeStart, DateTime phenomenonTimeEnd,
            DateTime resultTime, DateTime validTimeStart, DateTime validTimeEnd) throws OwsExceptionReport {
        return createObservation(id.name(), phenomenonTimeStart, phenomenonTimeEnd, resultTime, validTimeStart,
                validTimeEnd);
    }

    public List<Observation<?>> createObservation(Enum<?> id, DateTime begin, DateTime end) throws OwsExceptionReport {
        return createObservation(id.name(), begin, end);
    }

    public List<Observation<?>> createObservation(Enum<?> id, DateTime time) throws OwsExceptionReport {
        return createObservation(id.name(), time);
    }

    protected Observation<?> createObservation(Offering offering) throws OwsExceptionReport {
        AbstractObservationDAO observationDAO = DaoFactory.getInstance().getObservationDAO();
        ObservationFactory observationFactory = observationDAO.getObservationFactory();
        BooleanObservation observation = observationFactory.truth();
        observation.setValue(true);
        if (observation instanceof AbstractSeriesObservation) {
            AbstractSeriesObservation<?> seriesBooleanObservation = (AbstractSeriesObservation<?>) observation;
            seriesBooleanObservation.setSeries(getSeries(offering));
            if (observation instanceof AbstractEReportingObservation) {
                AbstractEReportingObservation<?> abstractEReportingObservation
                        = (AbstractEReportingObservation) observation;
                abstractEReportingObservation.setValidation(1);
                abstractEReportingObservation.setVerification(1);
            }
        } else {
            AbstractLegacyObservation<?> booleanObservation = (AbstractLegacyObservation<?>) observation;
            booleanObservation.setFeatureOfInterest(getFeatureOfInterest());
            booleanObservation.setProcedure(getProcedure());
            booleanObservation.setObservableProperty(getObservableProperty());
        }
        return observation;
    }

    protected List<Offering> getOfferings() {
       return Lists.newArrayList(getOffering1(), getOffering2());
    }

    protected FeatureOfInterest getFeatureOfInterest() {
        FeatureOfInterest featureOfInterest =
                (FeatureOfInterest) session.createCriteria(FeatureOfInterest.class)
                        .add(Restrictions.eq(FeatureOfInterest.IDENTIFIER, FEATURE_OF_INTEREST)).uniqueResult();
        if (featureOfInterest == null) {
            featureOfInterest = new FeatureOfInterest();
            featureOfInterest.setCodespace(getCodespace());
            featureOfInterest.setDescriptionXml("<xml/>");
            featureOfInterest.setFeatureOfInterestType(getFeatureOfInterestType());
            featureOfInterest.setChilds(null);
            featureOfInterest.setParents(null);
            featureOfInterest.setIdentifier(FEATURE_OF_INTEREST);
            featureOfInterest.setName(FEATURE_OF_INTEREST);
            session.save(featureOfInterest);
            session.flush();
            return featureOfInterest;
        }
        return featureOfInterest;
    }

    protected ObservableProperty getObservableProperty() {
        ObservableProperty observableProperty =
                (ObservableProperty) session.createCriteria(ObservableProperty.class)
                        .add(Restrictions.eq(ObservableProperty.IDENTIFIER, OBSERVABLE_PROPERTY)).uniqueResult();
        if (observableProperty == null) {
            observableProperty = new ObservableProperty();
            observableProperty.setDescription(OBSERVABLE_PROPERTY);
            observableProperty.setIdentifier(OBSERVABLE_PROPERTY);
            session.save(observableProperty);
            session.flush();
        }
        return observableProperty;
    }

    protected Offering getOffering1() {
        Offering offering =
                (Offering) session.createCriteria(Offering.class)
                        .add(Restrictions.eq(Offering.IDENTIFIER, OFFERING_1)).uniqueResult();
        if (offering == null) {
            TOffering tOffering = new TOffering();
            tOffering.setFeatureOfInterestTypes(Collections.singleton(getFeatureOfInterestType()));
            tOffering.setIdentifier(OFFERING_1);
            tOffering.setName(OFFERING_1);
            tOffering.setObservationTypes(Collections.singleton(getObservationType()));
            tOffering.setRelatedFeatures(null);
            session.save(tOffering);
            session.flush();
            return tOffering;
        }
        return offering;
    }

    protected Offering getOffering2() {
        Offering offering =
                (Offering) session.createCriteria(Offering.class)
                        .add(Restrictions.eq(Offering.IDENTIFIER, OFFERING_2)).uniqueResult();
        if (offering == null) {
            TOffering tOffering = new TOffering();
            tOffering.setFeatureOfInterestTypes(Collections.singleton(getFeatureOfInterestType()));
            tOffering.setIdentifier(OFFERING_2);
            tOffering.setName(OFFERING_2);
            tOffering.setObservationTypes(Collections.singleton(getObservationType()));
            tOffering.setRelatedFeatures(null);
            session.save(tOffering);
            session.flush();
            return tOffering;
        }
        return offering;
    }

    protected Unit getUnit() {
        Unit unit = (Unit) session.createCriteria(Unit.class).add(Restrictions.eq(Unit.UNIT, UNIT)).uniqueResult();
        if (unit == null) {
            unit = new Unit();
            unit.setUnit(UNIT);
            session.save(unit);
            session.flush();
        }
        return unit;
    }

    protected Codespace getCodespace() {
        Codespace codespace =
                (Codespace) session.createCriteria(Codespace.class)
                        .add(Restrictions.eq(Codespace.CODESPACE, CODESPACE)).uniqueResult();
        if (codespace == null) {
            codespace = new Codespace();
            codespace.setCodespace(CODESPACE);
            session.save(codespace);
            session.flush();
        }
        return codespace;
    }

    protected Series getSeries(Offering offering) throws OwsExceptionReport {
        AbstractObservationDAO observationDAO = DaoFactory.getInstance().getObservationDAO();

        SeriesObservationFactory observationFactory = (SeriesObservationFactory) observationDAO.getObservationFactory();

        Criteria criteria =
                session.createCriteria(observationFactory.seriesClass()).setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY)
                        .add(Restrictions.eq(Series.FEATURE_OF_INTEREST, getFeatureOfInterest()))
                        .add(Restrictions.eq(Series.OBSERVABLE_PROPERTY, getObservableProperty()))
                        .add(Restrictions.eq(Series.PROCEDURE, getProcedure()))
                        .add(Restrictions.eq(Series.OFFERING, offering));
        Series series = (Series) criteria.uniqueResult();
        if (series == null) {
            series = observationFactory.series();
            series.setObservableProperty(getObservableProperty());
            series.setProcedure(getProcedure());
            series.setFeatureOfInterest(getFeatureOfInterest());
            series.setOffering(offering);
            series.setDeleted(false);
            series.setPublished(true);

            if (series instanceof EReportingSeries) {
                EReportingSeries eReportingSeries = (EReportingSeries) series;
                eReportingSeries.setSamplingPoint(getEReportingSamplingPoint());
            }

            session.save(series);
            session.flush();
            session.refresh(series);
        } else if (series.isDeleted()) {
            series.setDeleted(false);
            session.update(series);
            session.flush();
            session.refresh(series);
        }
        return series;
    }

    protected EReportingSamplingPoint getEReportingSamplingPoint() {

        EReportingSamplingPoint assessmentType
                = (EReportingSamplingPoint) session
                .createCriteria(EReportingSamplingPoint.class)
                .setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY)
                .add(Restrictions.eq(EReportingSamplingPoint.IDENTIFIER, EREPORTING_SAMPLING_POINT))
                .uniqueResult();
        if (assessmentType == null) {
            assessmentType = new EReportingSamplingPoint();
            assessmentType.setIdentifier(EREPORTING_SAMPLING_POINT);
            assessmentType.setAssessmentType(getEReportingAssessmentType());
            session.save(assessmentType);
            session.flush();
            session.refresh(assessmentType);
        }
        return assessmentType;

    }

    public EReportingAssessmentType getEReportingAssessmentType() {
        EReportingAssessmentType assessmentType
                = (EReportingAssessmentType) session
                .createCriteria(EReportingAssessmentType.class)
                .setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY)
                .add(Restrictions.eq(EReportingAssessmentType.ASSESSMENT_TYPE, EREPORTING_ASSESSMENT_TYPE))
                .uniqueResult();
        if (assessmentType == null) {
            assessmentType = new EReportingAssessmentType();
            assessmentType.setAssessmentType(EREPORTING_ASSESSMENT_TYPE);
            assessmentType.setUri(EREPORTING_ASSESSMENT_TYPE);
            session.save(assessmentType);
            session.flush();
            session.refresh(assessmentType);
        }

        return assessmentType;
    }

    protected Procedure getProcedure() {
        Procedure procedure =
                (Procedure) session.createCriteria(Procedure.class)
                        .add(Restrictions.eq(Procedure.IDENTIFIER, "Procedure")).uniqueResult();
        if (procedure == null) {
            TProcedure tProcedure = new TProcedure();
            tProcedure.setDeleted(false);
            tProcedure.setIdentifier("Procedure");
            tProcedure.setGeom(null);
            tProcedure.setProcedureDescriptionFormat(getProcedureDescriptionFormat());
            tProcedure.setChilds(null);
            tProcedure.setParents(null);
            session.save(tProcedure);
            session.flush();
            tProcedure.setValidProcedureTimes(Collections.singleton(getValidProcedureTime()));
            session.update(tProcedure);
            session.flush();
            return tProcedure;
        }
        return procedure;
    }

    protected ValidProcedureTime getValidProcedureTime() {
        ValidProcedureTime validProcedureTime =
                (ValidProcedureTime) session.createCriteria(ValidProcedureTime.class)
                        .add(Restrictions.eq(ValidProcedureTime.PROCEDURE, getProcedure())).uniqueResult();
        if (validProcedureTime == null) {
            validProcedureTime = new ValidProcedureTime();
            validProcedureTime.setDescriptionXml("<xml/>");
            validProcedureTime.setEndTime(null);
            validProcedureTime.setStartTime(new Date());
            validProcedureTime.setProcedure(getProcedure());
            validProcedureTime.setProcedureDescriptionFormat(getProcedureDescriptionFormat());
            session.save(validProcedureTime);
            session.flush();
        }
        return validProcedureTime;
    }

    protected ProcedureDescriptionFormat getProcedureDescriptionFormat() {
        ProcedureDescriptionFormat procedureDescriptionFormat =
                (ProcedureDescriptionFormat) session
                        .createCriteria(ProcedureDescriptionFormat.class)
                        .add(Restrictions.eq(ProcedureDescriptionFormat.PROCEDURE_DESCRIPTION_FORMAT,
                                PROCEDURE_DESCRIPTION_FORMAT)).uniqueResult();
        if (procedureDescriptionFormat == null) {
            procedureDescriptionFormat = new ProcedureDescriptionFormat();
            procedureDescriptionFormat.setProcedureDescriptionFormat(PROCEDURE_DESCRIPTION_FORMAT);
            session.save(procedureDescriptionFormat);
            session.flush();
        }
        return procedureDescriptionFormat;
    }

    protected FeatureOfInterestType getFeatureOfInterestType() {
        FeatureOfInterestType featureOfInterestType =
                (FeatureOfInterestType) session
                        .createCriteria(FeatureOfInterestType.class)
                        .add(Restrictions.eq(FeatureOfInterestType.FEATURE_OF_INTEREST_TYPE, FEATURE_OF_INTEREST_TYPE))
                        .uniqueResult();
        if (featureOfInterestType == null) {
            featureOfInterestType = new FeatureOfInterestType();
            featureOfInterestType.setFeatureOfInterestType(FEATURE_OF_INTEREST_TYPE);
            session.save(featureOfInterestType);
            session.flush();
        }
        return featureOfInterestType;
    }

    protected ObservationType getObservationType() {
        ObservationType observationType =
                (ObservationType) session.createCriteria(ObservationType.class)
                        .add(Restrictions.eq(ObservationType.OBSERVATION_TYPE, OBSERVATION_TYPE)).uniqueResult();
        if (observationType == null) {
            observationType = new ObservationType();
            observationType.setObservationType(OBSERVATION_TYPE);
            session.save(observationType);
            session.flush();
            session.refresh(observationType);
        }
        return observationType;
    }
}
