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
package org.n52.sos.ds.hibernate.util;

import java.util.Collections;
import java.util.Date;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;
import org.joda.time.DateTime;

import org.n52.sos.ds.hibernate.dao.AbstractObservationDAO;
import org.n52.sos.ds.hibernate.dao.DaoFactory;
import org.n52.sos.ds.hibernate.dao.series.SeriesObservationDAO;
import org.n52.sos.ds.hibernate.entities.AbstractObservation;
import org.n52.sos.ds.hibernate.entities.BooleanObservation;
import org.n52.sos.ds.hibernate.entities.Codespace;
import org.n52.sos.ds.hibernate.entities.FeatureOfInterest;
import org.n52.sos.ds.hibernate.entities.FeatureOfInterestType;
import org.n52.sos.ds.hibernate.entities.ObservableProperty;
import org.n52.sos.ds.hibernate.entities.ObservationType;
import org.n52.sos.ds.hibernate.entities.Offering;
import org.n52.sos.ds.hibernate.entities.Procedure;
import org.n52.sos.ds.hibernate.entities.ProcedureDescriptionFormat;
import org.n52.sos.ds.hibernate.entities.TFeatureOfInterest;
import org.n52.sos.ds.hibernate.entities.TOffering;
import org.n52.sos.ds.hibernate.entities.TProcedure;
import org.n52.sos.ds.hibernate.entities.Unit;
import org.n52.sos.ds.hibernate.entities.ValidProcedureTime;
import org.n52.sos.ds.hibernate.entities.series.Series;
import org.n52.sos.ds.hibernate.entities.series.SeriesBooleanObservation;
import org.n52.sos.ogc.om.values.BooleanValue;
import org.n52.sos.ogc.ows.OwsExceptionReport;

import com.google.common.collect.Sets;

/**
 * @author Christian Autermann <c.autermann@52north.org>
 *
 * @since 4.0.0
 */
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

    private final Session session;

    public HibernateObservationBuilder(Session session) {
        this.session = session;
    }

    public AbstractObservation createObservation(String id, Date phenomenonTimeStart, Date phenomenonTimeEnd, Date resultTime,
            Date validTimeStart, Date validTimeEnd) throws OwsExceptionReport {
        AbstractObservationDAO observationDAO = DaoFactory.getInstance().getObservationDAO();
        AbstractObservation observation = observationDAO.createObservationFromValue(new BooleanValue(true), session);
        if (observationDAO instanceof SeriesObservationDAO) {
            SeriesBooleanObservation seriesBooleanObservation = (SeriesBooleanObservation)observation;
            seriesBooleanObservation.setSeries(getSeries());
            seriesBooleanObservation.setValue(true);
        } else {
            BooleanObservation booleanObservation = (BooleanObservation)observation;
            booleanObservation.setFeatureOfInterest(getFeatureOfInterest());
            booleanObservation.setProcedure(getProcedure());
            booleanObservation.setObservableProperty(getObservableProperty());
            booleanObservation.setValue(true);
        }
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

    public AbstractObservation createObservation(String id, DateTime phenomenonTimeStart, DateTime phenomenonTimeEnd,
            DateTime resultTime, DateTime validTimeStart, DateTime validTimeEnd) throws OwsExceptionReport {
        return createObservation(id, phenomenonTimeStart != null ? phenomenonTimeStart.toDate() : null,
                phenomenonTimeEnd != null ? phenomenonTimeEnd.toDate() : null,
                resultTime != null ? resultTime.toDate() : null, validTimeStart != null ? validTimeStart.toDate()
                        : null, validTimeEnd != null ? validTimeEnd.toDate() : null);
    }

    public AbstractObservation createObservation(String id, DateTime begin, DateTime end) throws OwsExceptionReport {
        Date s = begin != null ? begin.toDate() : null;
        Date e = end != null ? end.toDate() : null;
        return createObservation(id, s, e, s, s, e);
    }

    public AbstractObservation createObservation(String id, DateTime position) throws OwsExceptionReport {
        Date s = position != null ? position.toDate() : null;
        return createObservation(id, s, s, s, s, s);
    }

    public AbstractObservation createObservation(Enum<?> id, Date phenomenonTimeStart, Date phenomenonTimeEnd,
            Date resultTime, Date validTimeStart, Date validTimeEnd) throws OwsExceptionReport {
        return createObservation(id.name(), phenomenonTimeStart, phenomenonTimeEnd, resultTime, validTimeStart,
                validTimeEnd);
    }

    public AbstractObservation createObservation(Enum<?> id, DateTime phenomenonTimeStart, DateTime phenomenonTimeEnd,
            DateTime resultTime, DateTime validTimeStart, DateTime validTimeEnd) throws OwsExceptionReport {
        return createObservation(id.name(), phenomenonTimeStart, phenomenonTimeEnd, resultTime, validTimeStart,
                validTimeEnd);
    }

    public AbstractObservation createObservation(Enum<?> id, DateTime begin, DateTime end) throws OwsExceptionReport {
        return createObservation(id.name(), begin, end);
    }

    public AbstractObservation createObservation(Enum<?> id, DateTime time) throws OwsExceptionReport {
        return createObservation(id.name(), time);
    }

    protected FeatureOfInterest getFeatureOfInterest() {
        FeatureOfInterest featureOfInterest =
                (FeatureOfInterest) session.createCriteria(FeatureOfInterest.class)
                        .add(Restrictions.eq(FeatureOfInterest.IDENTIFIER, FEATURE_OF_INTEREST)).uniqueResult();
        if (featureOfInterest == null) {
            TFeatureOfInterest tFeatureOfInterest = new TFeatureOfInterest();
            tFeatureOfInterest.setCodespace(getCodespace());
            tFeatureOfInterest.setDescriptionXml("<xml/>");
            tFeatureOfInterest.setFeatureOfInterestType(getFeatureOfInterestType());
            tFeatureOfInterest.setChilds(null);
            tFeatureOfInterest.setParents(null);
            tFeatureOfInterest.setIdentifier(FEATURE_OF_INTEREST);
            tFeatureOfInterest.setName(FEATURE_OF_INTEREST);
            session.save(tFeatureOfInterest);
            session.flush();
            return tFeatureOfInterest;
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

    protected Series getSeries() {
        Criteria criteria =
                session.createCriteria(Series.class).setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY)
                        .add(Restrictions.eq(Series.FEATURE_OF_INTEREST, getFeatureOfInterest()))
                        .add(Restrictions.eq(Series.OBSERVABLE_PROPERTY, getObservableProperty()))
                        .add(Restrictions.eq(Series.PROCEDURE, getProcedure()));
        Series series = (Series) criteria.uniqueResult();
        if (series == null) {
            series = new Series();
            series.setObservableProperty(getObservableProperty());
            series.setProcedure(getProcedure());
            series.setFeatureOfInterest(getFeatureOfInterest());
            series.setDeleted(false);
            series.setPublished(true);
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
