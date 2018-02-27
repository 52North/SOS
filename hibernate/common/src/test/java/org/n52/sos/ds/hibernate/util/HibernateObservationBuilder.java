/*
 * Copyright (C) 2012-2018 52°North Initiative for Geospatial Open Source
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
import org.n52.series.db.beans.CodespaceEntity;
import org.n52.series.db.beans.DataEntity;
import org.n52.series.db.beans.DatasetEntity;
import org.n52.series.db.beans.FeatureEntity;
import org.n52.series.db.beans.FormatEntity;
import org.n52.series.db.beans.OfferingEntity;
import org.n52.series.db.beans.PhenomenonEntity;
import org.n52.series.db.beans.ProcedureEntity;
import org.n52.series.db.beans.ProcedureHistoryEntity;
import org.n52.series.db.beans.UnitEntity;
import org.n52.series.db.beans.data.Data;
import org.n52.series.db.beans.data.Data.BooleanData;
import org.n52.series.db.beans.ereporting.EReportingAssessmentTypeEntity;
import org.n52.series.db.beans.ereporting.EReportingDataEntity;
import org.n52.series.db.beans.ereporting.EReportingDatasetEntity;
import org.n52.series.db.beans.ereporting.EReportingSamplingPointEntity;
import org.n52.shetland.ogc.ows.exception.OwsExceptionReport;
import org.n52.sos.ds.hibernate.dao.DaoFactory;
import org.n52.sos.ds.hibernate.dao.observation.AbstractObservationDAO;
import org.n52.sos.ds.hibernate.dao.observation.ObservationFactory;
import org.n52.sos.ds.hibernate.dao.observation.series.SeriesObservationFactory;

import com.google.common.collect.Lists;


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
    private final DaoFactory daoFactory;

    public HibernateObservationBuilder(Session session, DaoFactory daoFactory) {
        this.session = session;
        this.daoFactory = daoFactory;
    }

    public Data<?> createObservation(Data<?> observation, String id, Date phenomenonTimeStart, Date phenomenonTimeEnd, Date resultTime,
            Date validTimeStart, Date validTimeEnd) throws OwsExceptionReport {
        observation.setDeleted(false);
        observation.setIdentifier(id);
        observation.setPhenomenonTimeStart(phenomenonTimeStart);
        observation.setPhenomenonTimeEnd(phenomenonTimeEnd);
        observation.setResultTime(resultTime);
        observation.setValidTimeStart(validTimeStart);
        observation.setValidTimeEnd(validTimeEnd);
        observation.setIdentifierCodespace(getCodespace());
        session.save(observation);
        session.flush();
        return observation;
    }

    public List<Data<?>> createObservation(String id, DateTime phenomenonTimeStart, DateTime phenomenonTimeEnd,
            DateTime resultTime, DateTime validTimeStart, DateTime validTimeEnd) throws OwsExceptionReport {
        List<Data<?>> observations = Lists.newArrayList();
        for (OfferingEntity offering : getOfferings()) {
             observations.add(createObservation(createObservation(offering), offering.getIdentifier() + "/" + id, phenomenonTimeStart != null ? phenomenonTimeStart.toDate() : null,
                    phenomenonTimeEnd != null ? phenomenonTimeEnd.toDate() : null,
                    resultTime != null ? resultTime.toDate() : null, validTimeStart != null ? validTimeStart.toDate()
                            : null, validTimeEnd != null ? validTimeEnd.toDate() : null));
        }
        return observations;
    }

    public List<Data<?>> createObservation(String id, DateTime s, DateTime e) throws OwsExceptionReport {
        return createObservation(id, s, e, s, s, e);
    }

    public List<Data<?>> createObservation(String id, DateTime s) throws OwsExceptionReport {
        return createObservation(id, s, s, s, s, s);
    }

    public List<Data<?>> createObservation(Enum<?> id, DateTime phenomenonTimeStart, DateTime phenomenonTimeEnd,
            DateTime resultTime, DateTime validTimeStart, DateTime validTimeEnd) throws OwsExceptionReport {
        return createObservation(id.name(), phenomenonTimeStart, phenomenonTimeEnd, resultTime, validTimeStart,
                validTimeEnd);
    }

    public List<Data<?>> createObservation(Enum<?> id, DateTime begin, DateTime end) throws OwsExceptionReport {
        return createObservation(id.name(), begin, end);
    }

    public List<Data<?>> createObservation(Enum<?> id, DateTime time) throws OwsExceptionReport {
        return createObservation(id.name(), time);
    }

    protected Data<?> createObservation(OfferingEntity offering) throws OwsExceptionReport {
        AbstractObservationDAO observationDAO = daoFactory.getObservationDAO();
        ObservationFactory observationFactory = observationDAO.getObservationFactory();
        BooleanData observation = observationFactory.truth();
        observation.setValue(true);
        observation.setDataset(getSeries(offering, observation));
        if (observation instanceof EReportingDataEntity) {
            EReportingDataEntity abstractEReportingObservation =
                    (EReportingDataEntity) observation;
            abstractEReportingObservation.setValidation(1);
            abstractEReportingObservation.setVerification(1);
        }
        return observation;
    }

    protected List<OfferingEntity> getOfferings() {
       return Lists.newArrayList(getOffering1(), getOffering2());
    }

    protected FeatureEntity getFeatureOfInterest() {
        FeatureEntity featureOfInterest =
                (FeatureEntity) session.createCriteria(FeatureEntity.class)
                        .add(Restrictions.eq(FeatureEntity.IDENTIFIER, FEATURE_OF_INTEREST)).uniqueResult();
        if (featureOfInterest == null) {
            featureOfInterest = new FeatureEntity();
            featureOfInterest.setIdentifierCodespace(getCodespace());
            featureOfInterest.setXml("<xml/>");
            featureOfInterest.setFeatureType(getFeatureOfInterestType());
            featureOfInterest.setChildren(null);
            featureOfInterest.setParents(null);
            featureOfInterest.setIdentifier(FEATURE_OF_INTEREST);
            featureOfInterest.setName(FEATURE_OF_INTEREST);
            session.save(featureOfInterest);
            session.flush();
            return featureOfInterest;
        }
        return featureOfInterest;
    }

    protected PhenomenonEntity getObservableProperty() {
        PhenomenonEntity observableProperty =
                (PhenomenonEntity) session.createCriteria(PhenomenonEntity.class)
                        .add(Restrictions.eq(PhenomenonEntity.IDENTIFIER, OBSERVABLE_PROPERTY)).uniqueResult();
        if (observableProperty == null) {
            observableProperty = new PhenomenonEntity();
            observableProperty.setDescription(OBSERVABLE_PROPERTY);
            observableProperty.setIdentifier(OBSERVABLE_PROPERTY);
            session.save(observableProperty);
            session.flush();
        }
        return observableProperty;
    }

    protected OfferingEntity getOffering1() {
        OfferingEntity offering =
                (OfferingEntity) session.createCriteria(OfferingEntity.class)
                        .add(Restrictions.eq(OfferingEntity.IDENTIFIER, OFFERING_1)).uniqueResult();
        if (offering == null) {
            OfferingEntity tOffering = new OfferingEntity();
            tOffering.setFeatureTypes(Collections.singleton(getFeatureOfInterestType()));
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

    protected OfferingEntity getOffering2() {
        OfferingEntity offering =
                (OfferingEntity) session.createCriteria(OfferingEntity.class)
                        .add(Restrictions.eq(OfferingEntity.IDENTIFIER, OFFERING_2)).uniqueResult();
        if (offering == null) {
            OfferingEntity tOffering = new OfferingEntity();
            tOffering.setFeatureTypes(Collections.singleton(getFeatureOfInterestType()));
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

    protected UnitEntity getUnit() {
        UnitEntity unit = (UnitEntity) session.createCriteria(UnitEntity.class).add(Restrictions.eq(UnitEntity.IDENTIFIER, UNIT)).uniqueResult();
        if (unit == null) {
            unit = new UnitEntity();
            unit.setUnit(UNIT);
            session.save(unit);
            session.flush();
        }
        return unit;
    }

    protected CodespaceEntity getCodespace() {
        CodespaceEntity codespace =
                (CodespaceEntity) session.createCriteria(CodespaceEntity.class)
                        .add(Restrictions.eq(CodespaceEntity.PROPERTY_NAME, CODESPACE)).uniqueResult();
        if (codespace == null) {
            codespace = new CodespaceEntity();
            codespace.setName(CODESPACE);
            session.save(codespace);
            session.flush();
        }
        return codespace;
    }

    protected DatasetEntity getSeries(OfferingEntity offering, Data o) throws OwsExceptionReport {
        AbstractObservationDAO observationDAO = daoFactory.getObservationDAO();

        SeriesObservationFactory observationFactory = (SeriesObservationFactory) observationDAO.getObservationFactory();

        Criteria criteria =
                session.createCriteria(observationFactory.seriesClass()).setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY)
                        .add(Restrictions.eq(DatasetEntity.PROPERTY_FEATURE, getFeatureOfInterest()))
                        .add(Restrictions.eq(DatasetEntity.PROPERTY_PHENOMENON, getObservableProperty()))
                        .add(Restrictions.eq( DatasetEntity.PROPERTY_PROCEDURE, getProcedure()))
                        .add(Restrictions.eq( DatasetEntity.PROPERTY_OFFERING, offering));
        DatasetEntity series = (DatasetEntity) criteria.uniqueResult();
        if (series == null) {
            series = (DatasetEntity) daoFactory.getSeriesDAO().getDatasetFactory().visit(o);
            series.setObservableProperty(getObservableProperty());
            series.setProcedure(getProcedure());
            series.setFeature(getFeatureOfInterest());
            series.setOffering(offering);
            series.setDeleted(false);
            series.setPublished(true);

            if (series instanceof EReportingDatasetEntity) {
                EReportingDatasetEntity eReportingSeries = (EReportingDatasetEntity) series;
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

    protected EReportingSamplingPointEntity getEReportingSamplingPoint() {

        EReportingSamplingPointEntity assessmentType
                = (EReportingSamplingPointEntity) session
                .createCriteria(EReportingSamplingPointEntity.class)
                .setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY)
                .add(Restrictions.eq(EReportingSamplingPointEntity.IDENTIFIER, EREPORTING_SAMPLING_POINT))
                .uniqueResult();
        if (assessmentType == null) {
            assessmentType = new EReportingSamplingPointEntity();
            assessmentType.setIdentifier(EREPORTING_SAMPLING_POINT);
            assessmentType.setAssessmentType(getEReportingAssessmentType());
            session.save(assessmentType);
            session.flush();
            session.refresh(assessmentType);
        }
        return assessmentType;

    }

    public EReportingAssessmentTypeEntity getEReportingAssessmentType() {
        EReportingAssessmentTypeEntity assessmentType
                = (EReportingAssessmentTypeEntity) session
                .createCriteria(EReportingAssessmentTypeEntity.class)
                .setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY)
                .add(Restrictions.eq(EReportingAssessmentTypeEntity.ASSESSMENT_TYPE, EREPORTING_ASSESSMENT_TYPE))
                .uniqueResult();
        if (assessmentType == null) {
            assessmentType = new EReportingAssessmentTypeEntity();
            assessmentType.setAssessmentType(EREPORTING_ASSESSMENT_TYPE);
            assessmentType.setUri(EREPORTING_ASSESSMENT_TYPE);
            session.save(assessmentType);
            session.flush();
            session.refresh(assessmentType);
        }

        return assessmentType;
    }

    protected ProcedureEntity getProcedure() {
        ProcedureEntity procedure =
                (ProcedureEntity) session.createCriteria(ProcedureEntity.class)
                        .add(Restrictions.eq(ProcedureEntity.IDENTIFIER, "Procedure")).uniqueResult();
        if (procedure == null) {
            ProcedureEntity tProcedure = new ProcedureEntity();
            tProcedure.setDeleted(false);
            tProcedure.setIdentifier("Procedure");
            tProcedure.setFormat(getProcedureDescriptionFormat());
            tProcedure.setChildren(null);
            tProcedure.setParents(null);
            session.save(tProcedure);
            session.flush();
            tProcedure.setProcedureHistory(Collections.singleton(getValidProcedureTime()));
            session.update(tProcedure);
            session.flush();
            return tProcedure;
        }
        return procedure;
    }

    protected ProcedureHistoryEntity getValidProcedureTime() {
        ProcedureHistoryEntity validProcedureTime =
                (ProcedureHistoryEntity) session.createCriteria(ProcedureHistoryEntity.class)
                        .add(Restrictions.eq(ProcedureHistoryEntity.PROCEDURE, getProcedure())).uniqueResult();
        if (validProcedureTime == null) {
            validProcedureTime = new ProcedureHistoryEntity();
            validProcedureTime.setXml("<xml/>");
            validProcedureTime.setEndTime(null);
            validProcedureTime.setStartTime(new Date());
            validProcedureTime.setProcedure(getProcedure());
            validProcedureTime.setFormat(getProcedureDescriptionFormat());
            session.save(validProcedureTime);
            session.flush();
        }
        return validProcedureTime;
    }

    protected FormatEntity getProcedureDescriptionFormat() {
        return getFormat(PROCEDURE_DESCRIPTION_FORMAT);
    }

    protected FormatEntity getFeatureOfInterestType() {
        return getFormat(FEATURE_OF_INTEREST_TYPE);
    }

    protected FormatEntity getObservationType() {
        return getFormat(OBSERVATION_TYPE);
    }

    protected FormatEntity getFormat(String format) {
        FormatEntity observationType =
                (FormatEntity) session.createCriteria(FormatEntity.class)
                        .add(Restrictions.eq(FormatEntity.FORMAT, format)).uniqueResult();
        if (observationType == null) {
            observationType = new FormatEntity();
            observationType.setFormat(format);
            session.save(observationType);
            session.flush();
            session.refresh(observationType);
        }
        return observationType;
    }
}
