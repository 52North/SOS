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
package org.n52.sos.ds.hibernate.util;

import java.util.Map;

import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.joda.time.DateTime;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.Test;
import org.n52.iceland.ds.ConnectionProviderException;
import org.n52.iceland.i18n.I18NDAORepository;
import org.n52.series.db.beans.DataEntity;
import org.n52.shetland.ogc.gml.time.TimePeriod;
import org.n52.shetland.ogc.ows.exception.OwsExceptionReport;
import org.n52.shetland.util.DateTimeHelper;
import org.n52.sos.ds.hibernate.ExtendedHibernateTestCase;
import org.n52.sos.ds.hibernate.H2Configuration;
import org.n52.sos.ds.hibernate.dao.DaoFactory;
import org.n52.sos.ds.hibernate.dao.OfferingDAO;
import org.n52.sos.ds.hibernate.dao.observation.AbstractObservationDAO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author <a href="mailto:e.h.juerrens@52north.org">Eike Hinderk
 *         J&uuml;rrens</a>
 * @author CarstenHollmann
 *
 * @since 4.0.0
 */
public class ObservationDAOTest extends ExtendedHibernateTestCase {

    private static final Logger LOGGER = LoggerFactory.getLogger(ObservationDAOTest.class);

    private AbstractObservationDAO observationDAO;

    private OfferingDAO offeringDAO;

    @Before
    public void fillObservations() throws OwsExceptionReport {
        Session session = getSession();
        HibernateMetadataCache.init(session);
        Transaction transaction = null;
        try {
            I18NDAORepository i18NDAORepository = new I18NDAORepository();
            DaoFactory daoFactory = new DaoFactory();
            daoFactory.setI18NDAORepository(i18NDAORepository);
            observationDAO = daoFactory.getObservationDAO();
            offeringDAO = daoFactory.getOfferingDAO();
            transaction = getTransaction(session);
            HibernateObservationBuilder b;
            b = new HibernateObservationBuilder(session, daoFactory);
            DateTime begin = DateTimeHelper.parseIsoString2DateTime("2019-10-01T12:00:00.000Z");
            for (int i = 0; i < 50; ++i) {
                b.createObservation(String.valueOf(i), begin.plusHours(i));
            }
            session.flush();
            transaction.commit();
        } catch (HibernateException he) {
            if (transaction != null) {
                transaction.rollback();
            }
            throw he;
        } finally {
            returnSession(session);
        }
    }

    @After
    public void clearObservations() throws OwsExceptionReport {
        Session session = null;
        Transaction transaction = null;
        try {
            session = getSession();
            transaction = getTransaction(session);
            try (ScrollableIterable<DataEntity<?>> i =
                    ScrollableIterable.fromCriteria(session.createCriteria(getObservationClass()))) {
                for (DataEntity<?> o : i) {
                    session.delete(o);
                }
            }
            session.flush();
            transaction.commit();
        } catch (HibernateException he) {
            if (transaction != null) {
                transaction.rollback();
            }
            throw he;
        } finally {
            returnSession(session);
        }
    }

    @AfterClass
    public static void cleanUp() {
        H2Configuration.recreate();
    }

    @Test
    public void getGlobalTemporalBoundingBoxWithNullReturnsNull() {
        assertThat("global temporal bounding box", observationDAO.getGlobalTemporalBoundingBox(null), is(nullValue()));
    }

    @Test
    public void getGlobalTemporalBoundingBoxEndBeforeStartOrEqual() throws ConnectionProviderException {
        Session session = getSession();
        try {
            TimePeriod temporalBBox = observationDAO.getGlobalTemporalBoundingBox(session);
            assertThat(temporalBBox, is(notNullValue()));
            assertThat(temporalBBox.getStart(), is(notNullValue()));
            assertThat(temporalBBox.getEnd(), is(notNullValue()));
            timePeriodStartIsBeforeEndOrEqual(temporalBBox);
        } finally {
            returnSession(session);
        }
    }

    private void timePeriodStartIsBeforeEndOrEqual(TimePeriod temporalBBox) {
        boolean startBeforeEndOrEqual = temporalBBox.getStart().isEqual(temporalBBox.getEnd())
                || temporalBBox.getStart().isBefore(temporalBBox.getEnd());
        assertThat("start is before end or equal", startBeforeEndOrEqual, is(true));
    }

    @Test
    public void getTemporalBoundingBoxForOfferingsWithNullReturnsEmptyList() throws OwsExceptionReport {
        Map<String, TimePeriod> emptyMap = offeringDAO.getTemporalBoundingBoxesForOfferings(null);
        assertThat("empty map", is(notNullValue()));
        assertThat("map is empty", emptyMap.isEmpty(), is(true));
    }

    @Test
    public void getTemporalBoundingBoxForOfferingsContainsNoNullElements()
            throws ConnectionProviderException, OwsExceptionReport {
        Session session = getSession();
        try {
            Map<String, TimePeriod> tempBBoxMap = offeringDAO.getTemporalBoundingBoxesForOfferings(session);
            assertThat("bbox map is empty", tempBBoxMap.isEmpty(), is(false));
            for (String offeringId : tempBBoxMap.keySet()) {
                assertThat("offering id", offeringId, is(not(nullValue())));
                TimePeriod offeringBBox = tempBBoxMap.get(offeringId);
                assertThat("offering temp bbox", offeringBBox, is(not(nullValue())));
                assertThat("offering temporal bbox start", offeringBBox.getStart(), is(not(nullValue())));
                assertThat("offering temporal bbox end", offeringBBox.getEnd(), is(not(nullValue())));
                timePeriodStartIsBeforeEndOrEqual(offeringBBox);
            }
        } finally {
            returnSession(session);
        }
    }

}
