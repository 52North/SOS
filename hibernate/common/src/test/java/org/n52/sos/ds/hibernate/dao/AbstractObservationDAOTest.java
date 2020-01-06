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
package org.n52.sos.ds.hibernate.dao;

import java.util.Collection;
import java.util.List;
import java.util.Set;

import org.hibernate.Criteria;
import org.hibernate.ScrollableResults;
import org.hibernate.Session;
import org.hibernate.criterion.Criterion;
import org.joda.time.DateTime;
import org.junit.Assert;
import org.junit.Test;
import org.n52.sos.ds.hibernate.dao.observation.AbstractObservationDAO;
import org.n52.sos.ds.hibernate.dao.observation.ObservationContext;
import org.n52.sos.ds.hibernate.dao.observation.ObservationFactory;
import org.n52.sos.ds.hibernate.entities.ObservationConstellation;
import org.n52.sos.ds.hibernate.entities.observation.Observation;
import org.n52.sos.ds.hibernate.entities.observation.series.full.SeriesNumericObservation;
import org.n52.sos.exception.CodedException;
import org.n52.sos.exception.ows.InvalidParameterValueException;
import org.n52.sos.exception.ows.MissingParameterValueException;
import org.n52.sos.exception.ows.NoApplicableCodeException;
import org.n52.sos.ogc.gml.time.Time.TimeIndeterminateValue;
import org.n52.sos.ogc.gml.time.TimeInstant;
import org.n52.sos.ogc.om.OmObservation;
import org.n52.sos.ogc.ows.OwsExceptionReport;

import com.vividsolutions.jts.geom.Envelope;
import com.vividsolutions.jts.geom.Geometry;

public class AbstractObservationDAOTest {


    TestObservationDAO dao = new TestObservationDAO();


    /*
     * PhenomenonTime with TimeInstant
     */

    @Test
    public void add_phenomenonTime_instant_value() throws OwsExceptionReport {
        Observation<?> observation = new SeriesNumericObservation();
        TimeInstant phenomenonTime = new TimeInstant(new DateTime());
        dao.addPhenomenonTimeToObservation(observation, phenomenonTime);
        Assert.assertTrue(observation.getPhenomenonTimeStart() != null);
        Assert.assertTrue(observation.getPhenomenonTimeEnd() != null);
    }

    @Test
    public void add_phenomenonTime_instant_timeIndeterminatePosition_now() throws OwsExceptionReport {
        Observation<?> observation = new SeriesNumericObservation();
        TimeInstant phenomenonTime = new TimeInstant(TimeIndeterminateValue.now);
        dao.addPhenomenonTimeToObservation(observation, phenomenonTime);
        Assert.assertTrue(observation.getPhenomenonTimeStart() != null);
        Assert.assertTrue(observation.getPhenomenonTimeEnd() != null);
    }

    @Test(expected=InvalidParameterValueException.class)
    public void add_phenomenonTime_instant_timeIndeterminatePosition_other() throws OwsExceptionReport {
        Observation<?> observation = new SeriesNumericObservation();
        TimeInstant phenomenonTime = new TimeInstant(TimeIndeterminateValue.unknown);
        dao.addPhenomenonTimeToObservation(observation, phenomenonTime);
    }

    @Test(expected=MissingParameterValueException.class)
    public void add_phenomenonTime_instant_timeIndeterminatePosition_missing() throws OwsExceptionReport {
        Observation<?> observation = new SeriesNumericObservation();
        TimeInstant phenomenonTime = new TimeInstant();
        dao.addPhenomenonTimeToObservation(observation, phenomenonTime);
    }

    /*
     * ResultTime
     */

    @Test
    public void add_resultTime_value() throws CodedException {
        Observation<?> observation = new SeriesNumericObservation();
        TimeInstant resultTime = new TimeInstant(new DateTime());
        dao.addResultTimeToObservation(observation, resultTime, null);
        Assert.assertTrue(observation.getResultTime() != null);
    }

    @Test
    public void add_resultTime_timeIndeterminatePosition_now() throws CodedException {
        Observation<?> observation = new SeriesNumericObservation();
        TimeInstant resultTime = new TimeInstant(TimeIndeterminateValue.now);
        dao.addResultTimeToObservation(observation, resultTime, null);
        Assert.assertTrue(observation.getResultTime() != null);
    }

    @Test(expected=InvalidParameterValueException.class)
    public void add_resultTime_timeIndeterminatePosition_other() throws CodedException {
        Observation<?> observation = new SeriesNumericObservation();
        TimeInstant resultTime = new TimeInstant(TimeIndeterminateValue.unknown);
        dao.addResultTimeToObservation(observation, resultTime, null);
    }

    @Test(expected=NoApplicableCodeException.class)
    public void add_resultTime_timeIndeterminatePosition_missing() throws CodedException {
        Observation<?> observation = new SeriesNumericObservation();
        TimeInstant resultTime = new TimeInstant();
        dao.addResultTimeToObservation(observation, resultTime, null);
    }

    @Test
    public void add_resultTime_value_from_phenomenonTime() throws CodedException {
        Observation<?> observation = new SeriesNumericObservation();
        TimeInstant resultTime = new TimeInstant();
        resultTime.setGmlId("#phenomenonTime");
        TimeInstant phenomeonTime = new TimeInstant(new DateTime());
        dao.addResultTimeToObservation(observation, resultTime, phenomeonTime);
        Assert.assertTrue(observation.getResultTime() != null);
    }

    @Test
    public void add_resultTime_from_phenomenonTime_timeIndeterminatePosition_now() throws CodedException {
        Observation<?> observation = new SeriesNumericObservation();
        TimeInstant resultTime = new TimeInstant();
        resultTime.setGmlId("#phenomenonTime");
        TimeInstant phenomeonTime = new TimeInstant(TimeIndeterminateValue.now);
        dao.addResultTimeToObservation(observation, resultTime, phenomeonTime);
        Assert.assertTrue(observation.getResultTime() != null);
    }

    @Test(expected=InvalidParameterValueException.class)
    public void add_resultTime_from_phenomenonTime_timeIndeterminatePosition_other() throws CodedException {
        Observation<?> observation = new SeriesNumericObservation();
        TimeInstant resultTime = new TimeInstant();
        resultTime.setGmlId("#phenomenonTime");
        TimeInstant phenomeonTime = new TimeInstant(TimeIndeterminateValue.unknown);
        dao.addResultTimeToObservation(observation, resultTime, phenomeonTime);
    }

    @Test(expected=NoApplicableCodeException.class)
    public void add_resultTime_from_phenomenonTime_timeIndeterminatePosition_missing() throws CodedException {
        Observation<?> observation = new SeriesNumericObservation();
        TimeInstant resultTime = new TimeInstant();
        resultTime.setGmlId("#phenomenonTime");
        TimeInstant phenomeonTime = new TimeInstant();
        dao.addResultTimeToObservation(observation, resultTime, phenomeonTime);
    }

    public class TestObservationDAO extends AbstractObservationDAO {

        @Override
        protected void addObservationContextToObservation(ObservationContext ctx,
                Observation<?> observation, Session session) throws CodedException {
        }

        @Override
        public Criteria getObservationInfoCriteriaForFeatureOfInterestAndProcedure(String feature, String procedure,
                Session session) {
            return null;
        }

        @Override
        public Criteria getObservationInfoCriteriaForFeatureOfInterestAndOffering(String feature, String offering,
                Session session) {
            return null;
        }

        @Override
        public Criteria getObservationCriteriaForProcedure(String procedure, Session session) throws CodedException {
            return null;
        }

        @Override
        public Criteria getObservationCriteriaForObservableProperty(String observableProperty, Session session)
                throws CodedException {
            return null;
        }

        @Override
        public Criteria getObservationCriteriaForFeatureOfInterest(String featureOfInterest, Session session)
                throws CodedException {
            return null;
        }

        @Override
        public Criteria getObservationCriteriaFor(String procedure, String observableProperty, Session session)
                throws CodedException {
            return null;
        }

        @Override
        public Criteria getObservationCriteriaFor(String procedure, String observableProperty,
                String featureOfInterest, Session session) throws CodedException {
            return null;
        }

        @Override
        public Collection<String> getObservationIdentifiers(String procedureIdentifier, Session session) {
            return null;
        }

        @Override
        public List<Geometry> getSamplingGeometries(String feature, Session session) throws OwsExceptionReport {
            return null;
        }

        @Override
        public Long getSamplingGeometriesCount(String feature, Session session) throws OwsExceptionReport {
            return null;
        }

        @Override
        public Envelope getBboxFromSamplingGeometries(String feature, Session session) throws OwsExceptionReport {
            return null;
        }

        @Override
        public ScrollableResults getObservations(Set<String> procedure, Set<String> observableProperty,
                Set<String> featureOfInterest, Set<String> offering, Criterion filterCriterion, Session session) {
            return null;
        }

        @Override
        public Criteria getTemoralReferencedObservationCriteriaFor(OmObservation observation, ObservationConstellation observationConstellation, Session session)
                throws CodedException {
            return null;
        }

        @Override
        public ObservationFactory getObservationFactory() {
            return null;
        }

        @Override
        public String addProcedureAlias(Criteria criteria) {
            return null;
        }

        @Override
        protected Criteria addAdditionalObservationIdentification(Criteria c, OmObservation sosObservation) {
            return null;
        }

    }

}
