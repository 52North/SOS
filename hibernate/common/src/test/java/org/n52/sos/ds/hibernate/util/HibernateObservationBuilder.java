/*
 * Copyright (C) 2012-2023 52°North Spatial Information Research GmbH
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

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Root;

import org.hibernate.Session;
import org.joda.time.DateTime;
import org.n52.series.db.beans.BooleanDataEntity;
import org.n52.series.db.beans.CategoryEntity;
import org.n52.series.db.beans.CodespaceEntity;
import org.n52.series.db.beans.DataEntity;
import org.n52.series.db.beans.DatasetEntity;
import org.n52.series.db.beans.FeatureEntity;
import org.n52.series.db.beans.FormatEntity;
import org.n52.series.db.beans.OfferingEntity;
import org.n52.series.db.beans.PhenomenonEntity;
import org.n52.series.db.beans.PlatformEntity;
import org.n52.series.db.beans.ProcedureEntity;
import org.n52.series.db.beans.ProcedureHistoryEntity;
import org.n52.series.db.beans.UnitEntity;
import org.n52.series.db.beans.AssessmentTypeEntity;
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

    public static final String CATEGORY = "Category";

    public static final String PLATFORM = "Platform";

    public static final String PROCEDURE_DESCRIPTION_FORMAT = "ProcedureDescriptionFormat";

    public static final String FEATURE_OF_INTEREST_TYPE = "FeatureOfInterestType";

    public static final String OBSERVATION_TYPE = "ObservationType";

    public static final String EREPORTING_SAMPLING_POINT = "samplingPoint";

    public static final String EREPORTING_ASSESSMENT_TYPE = "assessmentType";

    public static final String XML_TOKEN = "<xml/>";

    public static final String PROCEDURE = "Procedure";

    private final Session session;

    private final DaoFactory daoFactory;

    public HibernateObservationBuilder(Session session, DaoFactory daoFactory) {
        this.session = session;
        this.daoFactory = daoFactory;
    }

    public DataEntity<?> createObservation(DataEntity<?> observation, String id, Date phenomenonTimeStart,
            Date phenomenonTimeEnd, Date resultTime, Date validTimeStart, Date validTimeEnd)
            throws OwsExceptionReport {
        observation.setDeleted(false);
        observation.setIdentifier(id);
        observation.setSamplingTimeStart(phenomenonTimeStart);
        observation.setSamplingTimeEnd(phenomenonTimeEnd);
        observation.setResultTime(resultTime);
        observation.setValidTimeStart(validTimeStart);
        observation.setValidTimeEnd(validTimeEnd);
        observation.setIdentifierCodespace(getCodespace());
        session.persist(observation);
        session.flush();
        return observation;
    }

    public List<DataEntity<?>> createObservation(String id, DateTime phenomenonTimeStart, DateTime phenomenonTimeEnd,
            DateTime resultTime, DateTime validTimeStart, DateTime validTimeEnd) throws OwsExceptionReport {
        List<DataEntity<?>> observations = Lists.newArrayList();
        for (OfferingEntity offering : getOfferings()) {
            observations.add(createObservation(createObservation(offering), offering.getIdentifier() + "/" + id,
                    phenomenonTimeStart != null ? phenomenonTimeStart.toDate() : null,
                    phenomenonTimeEnd != null ? phenomenonTimeEnd.toDate() : null,
                    resultTime != null ? resultTime.toDate() : null,
                    validTimeStart != null ? validTimeStart.toDate() : null,
                    validTimeEnd != null ? validTimeEnd.toDate() : null));
        }
        return observations;
    }

    public List<DataEntity<?>> createObservation(String id, DateTime s, DateTime e) throws OwsExceptionReport {
        return createObservation(id, s, e, s, s, e);
    }

    public List<DataEntity<?>> createObservation(String id, DateTime s) throws OwsExceptionReport {
        return createObservation(id, s, s, s, s, s);
    }

    public List<DataEntity<?>> createObservation(Enum<?> id, DateTime phenomenonTimeStart, DateTime phenomenonTimeEnd,
            DateTime resultTime, DateTime validTimeStart, DateTime validTimeEnd) throws OwsExceptionReport {
        return createObservation(id.name(), phenomenonTimeStart, phenomenonTimeEnd, resultTime, validTimeStart,
                validTimeEnd);
    }

    public List<DataEntity<?>> createObservation(Enum<?> id, DateTime begin, DateTime end) throws OwsExceptionReport {
        return createObservation(id.name(), begin, end);
    }

    public List<DataEntity<?>> createObservation(Enum<?> id, DateTime time) throws OwsExceptionReport {
        return createObservation(id.name(), time);
    }

    protected DataEntity<?> createObservation(OfferingEntity offering) throws OwsExceptionReport {
        AbstractObservationDAO observationDAO = daoFactory.getObservationDAO();
        ObservationFactory observationFactory = observationDAO.getObservationFactory();
        BooleanDataEntity observation = observationFactory.truth();
        observation.setValue(true);
        observation.setDataset(getSeries(offering, observation));
        if (observation.hasEreportingProfile()) {
            observation.getEreportingProfile().setValidation(1);
            observation.getEreportingProfile().setVerification(1);
        }
        return observation;
    }

    protected List<OfferingEntity> getOfferings() {
        return Lists.newArrayList(getOffering1(), getOffering2());
    }

    /**
     * Look up the single entity of the given class whose property equals the given value.
     *
     * @param <T>
     *            Entity type
     * @param clazz
     *            Entity class to query
     * @param property
     *            Property to match on
     * @param value
     *            Value the property has to equal
     *
     * @return The matching entity, or <code>null</code> if there is none
     */
    private <T> T getUnique(Class<T> clazz, String property, Object value) {
        CriteriaBuilder cb = session.getCriteriaBuilder();
        CriteriaQuery<T> query = cb.createQuery(clazz);
        Root<T> root = query.from(clazz);
        query.where(cb.equal(root.get(property), value));
        return session.createQuery(query).uniqueResult();
    }

    protected FeatureEntity getFeatureOfInterest() {
        FeatureEntity featureOfInterest =
                getUnique(FeatureEntity.class, FeatureEntity.IDENTIFIER, FEATURE_OF_INTEREST);
        if (featureOfInterest == null) {
            featureOfInterest = new FeatureEntity();
            featureOfInterest.setIdentifierCodespace(getCodespace());
            featureOfInterest.setXml(XML_TOKEN);
            featureOfInterest.setFeatureType(getFeatureOfInterestType());
            featureOfInterest.setChildren(null);
            featureOfInterest.setParents(null);
            featureOfInterest.setIdentifier(FEATURE_OF_INTEREST);
            featureOfInterest.setName(FEATURE_OF_INTEREST);
            session.persist(featureOfInterest);
            session.flush();
            return featureOfInterest;
        }
        return featureOfInterest;
    }

    protected PlatformEntity getPlatform(boolean eReporting) {
        if (eReporting) {
            PlatformEntity platform =
                    getUnique(PlatformEntity.class, PlatformEntity.IDENTIFIER, EREPORTING_SAMPLING_POINT);
            if (platform == null) {
                platform = new PlatformEntity();
                platform.setIdentifier(EREPORTING_SAMPLING_POINT);
                platform.setAssessmentType(getEReportingAssessmentType());
                session.persist(platform);
                session.flush();
                session.refresh(platform);
            }
            return platform;
        }
        PlatformEntity platform = getUnique(PlatformEntity.class, PlatformEntity.IDENTIFIER, PLATFORM);
        if (platform == null) {
            platform = new PlatformEntity();
            platform.setIdentifier(PLATFORM);
            platform.setName(PLATFORM);
            session.persist(platform);
            session.flush();
        }
        return platform;
    }

    protected PhenomenonEntity getObservableProperty() {
        PhenomenonEntity observableProperty =
                getUnique(PhenomenonEntity.class, PhenomenonEntity.IDENTIFIER, OBSERVABLE_PROPERTY);
        if (observableProperty == null) {
            observableProperty = new PhenomenonEntity();
            observableProperty.setDescription(OBSERVABLE_PROPERTY);
            observableProperty.setIdentifier(OBSERVABLE_PROPERTY);
            session.persist(observableProperty);
            session.flush();
        }
        return observableProperty;
    }

    protected CategoryEntity getCategory() {
        CategoryEntity category = getUnique(CategoryEntity.class, CategoryEntity.IDENTIFIER, CATEGORY);
        if (category == null) {
            category = new CategoryEntity();
            category.setDescription(CATEGORY);
            category.setIdentifier(CATEGORY);
            session.persist(category);
            session.flush();
        }
        return category;
    }

    protected OfferingEntity getOffering1() {
        OfferingEntity offering = getUnique(OfferingEntity.class, OfferingEntity.IDENTIFIER, OFFERING_1);
        if (offering == null) {
            OfferingEntity tOffering = new OfferingEntity();
            tOffering.setFeatureTypes(Collections.singleton(getFeatureOfInterestType()));
            tOffering.setIdentifier(OFFERING_1);
            tOffering.setName(OFFERING_1);
            tOffering.setObservationTypes(Collections.singleton(getObservationType()));
            tOffering.setRelatedFeatures(null);
            session.persist(tOffering);
            session.flush();
            return tOffering;
        }
        return offering;
    }

    protected OfferingEntity getOffering2() {
        OfferingEntity offering = getUnique(OfferingEntity.class, OfferingEntity.IDENTIFIER, OFFERING_2);
        if (offering == null) {
            OfferingEntity tOffering = new OfferingEntity();
            tOffering.setFeatureTypes(Collections.singleton(getFeatureOfInterestType()));
            tOffering.setIdentifier(OFFERING_2);
            tOffering.setName(OFFERING_2);
            tOffering.setObservationTypes(Collections.singleton(getObservationType()));
            tOffering.setRelatedFeatures(null);
            session.persist(tOffering);
            session.flush();
            return tOffering;
        }
        return offering;
    }

    protected UnitEntity getUnit() {
        UnitEntity unit = getUnique(UnitEntity.class, UnitEntity.IDENTIFIER, UNIT);
        if (unit == null) {
            unit = new UnitEntity();
            unit.setUnit(UNIT);
            session.persist(unit);
            session.flush();
        }
        return unit;
    }

    protected CodespaceEntity getCodespace() {
        CodespaceEntity codespace = getUnique(CodespaceEntity.class, CodespaceEntity.PROPERTY_NAME, CODESPACE);
        if (codespace == null) {
            codespace = new CodespaceEntity();
            codespace.setName(CODESPACE);
            session.persist(codespace);
            session.flush();
        }
        return codespace;
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    protected DatasetEntity getSeries(OfferingEntity offering, DataEntity o) throws OwsExceptionReport {
        AbstractObservationDAO observationDAO = daoFactory.getObservationDAO();

        SeriesObservationFactory observationFactory =
                (SeriesObservationFactory) observationDAO.getObservationFactory();

        CriteriaBuilder cb = session.getCriteriaBuilder();
        CriteriaQuery query = cb.createQuery(observationFactory.seriesClass());
        Root root = query.from(observationFactory.seriesClass());
        query.where(cb.equal(root.get(DatasetEntity.PROPERTY_FEATURE), getFeatureOfInterest()),
                cb.equal(root.get(DatasetEntity.PROPERTY_PHENOMENON), getObservableProperty()),
                cb.equal(root.get(DatasetEntity.PROPERTY_PROCEDURE), getProcedure()),
                cb.equal(root.get(DatasetEntity.PROPERTY_OFFERING), offering));
        DatasetEntity series = (DatasetEntity) session.createQuery(query).uniqueResult();
        if (series == null) {
            series = (DatasetEntity) daoFactory.getSeriesDAO().getDatasetFactory().visit(o);
            series.setObservableProperty(getObservableProperty());
            series.setProcedure(getProcedure());
            series.setCategory(getCategory());
            series.setPlatform(getPlatform(series.hasEreportingProfile()));
            series.setFeature(getFeatureOfInterest());
            series.setOffering(offering);
            series.setDeleted(false);
            series.setPublished(true);

            session.persist(series);
            session.flush();
            session.refresh(series);
        } else if (series.isDeleted()) {
            series.setDeleted(false);
            session.merge(series);
            session.flush();
            session.refresh(series);
        }
        return series;
    }

    protected PlatformEntity getEReportingSamplingPoint() {

        PlatformEntity platform =
                getUnique(PlatformEntity.class, PlatformEntity.IDENTIFIER, EREPORTING_SAMPLING_POINT);
        if (platform == null) {
            platform = new PlatformEntity();
            platform.setIdentifier(EREPORTING_SAMPLING_POINT);
            platform.setAssessmentType(getEReportingAssessmentType());
            session.persist(platform);
            session.flush();
            session.refresh(platform);
        }
        return platform;

    }

    public AssessmentTypeEntity getEReportingAssessmentType() {
        AssessmentTypeEntity assessmentType = getUnique(AssessmentTypeEntity.class,
                AssessmentTypeEntity.PROPERTY_ASSESSMENT_TYPE, EREPORTING_ASSESSMENT_TYPE);
        if (assessmentType == null) {
            assessmentType = new AssessmentTypeEntity();
            assessmentType.setAssessmentType(EREPORTING_ASSESSMENT_TYPE);
            assessmentType.setUri(EREPORTING_ASSESSMENT_TYPE);
            session.persist(assessmentType);
            session.flush();
            session.refresh(assessmentType);
        }

        return assessmentType;
    }

    protected ProcedureEntity getProcedure() {
        ProcedureEntity procedure = getUnique(ProcedureEntity.class, ProcedureEntity.IDENTIFIER, PROCEDURE);
        if (procedure == null) {
            ProcedureEntity tProcedure = new ProcedureEntity();
            tProcedure.setDeleted(false);
            tProcedure.setIdentifier(PROCEDURE);
            tProcedure.setFormat(getProcedureDescriptionFormat());
            tProcedure.setChildren(null);
            tProcedure.setParents(null);
            session.save(tProcedure);
            session.flush();
            tProcedure.setProcedureHistory(Collections.singleton(getValidProcedureTime()));
            //TODO: .merge() requires procedureHistory to be mutable
            session.update(tProcedure);
            session.flush();
            return tProcedure;
        }
        return procedure;
    }

    protected ProcedureHistoryEntity getValidProcedureTime() {
        ProcedureHistoryEntity validProcedureTime = getUnique(ProcedureHistoryEntity.class,
                ProcedureHistoryEntity.PROCEDURE, getProcedure());
        if (validProcedureTime == null) {
            validProcedureTime = new ProcedureHistoryEntity();
            validProcedureTime.setXml(XML_TOKEN);
            validProcedureTime.setEndTime(null);
            validProcedureTime.setStartTime(new Date());
            validProcedureTime.setProcedure(getProcedure());
            validProcedureTime.setFormat(getProcedureDescriptionFormat());
            session.persist(validProcedureTime);
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
        FormatEntity observationType = getUnique(FormatEntity.class, FormatEntity.FORMAT, format);
        if (observationType == null) {
            observationType = new FormatEntity();
            observationType.setFormat(format);
            session.persist(observationType);
            session.flush();
            session.refresh(observationType);
        }
        return observationType;
    }
}
