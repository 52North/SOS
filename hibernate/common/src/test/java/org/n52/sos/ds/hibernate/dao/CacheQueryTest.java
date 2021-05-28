/*
 * Copyright (C) 2012-2021 52°North Spatial Information Research GmbH
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

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.criterion.Restrictions;
import org.joda.time.DateTime;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.n52.iceland.ds.ConnectionProviderException;
import org.n52.iceland.i18n.I18NDAORepository;
import org.n52.series.db.beans.DataEntity;
import org.n52.shetland.ogc.ows.exception.CodedException;
import org.n52.shetland.ogc.ows.exception.OwsExceptionReport;
import org.n52.sos.ds.hibernate.ExtendedHibernateTestCase;
import org.n52.sos.ds.hibernate.util.HibernateHelper;
import org.n52.sos.ds.hibernate.util.HibernateObservationBuilder;
import org.n52.sos.ds.hibernate.util.ScrollableIterable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @since 4.0.0
 */
// Don't execute during normal builds. This test should be used for manual query
// performance evaluation.
public class CacheQueryTest extends ExtendedHibernateTestCase {
    private static final Logger LOGGER = LoggerFactory.getLogger(CacheQueryTest.class);

    private static final String LOG_TEMPLATE = "Creating test observation {} of {}";

    @Test
    public void runtimeComparisonObservationQueries() throws ConnectionProviderException, OwsExceptionReport {
        // run the query types in random order
        List<QueryType> queryTypes = Arrays.asList(QueryType.values());

        // run each query once and discard the result to warm up hibernate
        getFoiForOfferingObservationInfoTime();
        getFoiForOfferingObservationTime();
        getFoiForOfferingHqlTime();

        // note: performance tests on multiple types of queries seem to be
        // affected by each other (first run is slower,
        // subsequent queries are very fast).

        Map<QueryType, Long> resultTimes = new HashMap<>();

        final int runs = 100;
        for (int i = 0; i < runs; i++) {
            Collections.shuffle(queryTypes);
            for (QueryType qt : queryTypes) {
                LOGGER.info("Running foiForOffering query: " + qt.name());
                switch (qt) {
                    case OBSERVATION_INFO:
                        addToResultTimeMap(resultTimes, qt, getFoiForOfferingObservationInfoTime());
                        break;
                    case OBSERVATION:
                        addToResultTimeMap(resultTimes, qt, getFoiForOfferingObservationTime());
                        break;
                    case HQL:
                        addToResultTimeMap(resultTimes, qt, getFoiForOfferingHqlTime());
                        break;
                    default:
                        break;
                }
            }
        }

        long observationInfoTime = resultTimes.get(QueryType.OBSERVATION_INFO) / runs;
        long observationTime = resultTimes.get(QueryType.OBSERVATION) / runs;
        long hqlTime = resultTimes.get(QueryType.HQL) / runs;

        LOGGER.info("foi for offering, new way {} ms, the arithmetic mean of {} runs", observationInfoTime, runs);
        LOGGER.info("foi for offering, old way {} ms, the arithmetic mean of {} runs", observationTime, runs);
        LOGGER.info("foi for offering, hql way {} ms, the arithmetic mean of {} runs", hqlTime, runs);

        // note: this will fail for very low numbers of observations (old way is
        // faster)
        Assert.assertTrue("Old way is faster", observationInfoTime < observationTime);

        // note: hql is faster!
        // Assert.assertTrue("HQL is faster", observationInfoTime < hqlTime);
    }

    private void addToResultTimeMap(Map<QueryType, Long> resultTimes, QueryType qt,
            long foiForOfferingObservationInfoTime) {
        if (resultTimes.containsKey(qt)) {
            resultTimes.put(qt, foiForOfferingObservationInfoTime + resultTimes.get(qt));
        }
        resultTimes.put(qt, foiForOfferingObservationInfoTime);

    }

    private long getFoiForOfferingObservationInfoTime() throws OwsExceptionReport {
        // new way using ObservationInfo class (excludes value table joins)
        Session session = getSession();
        long start = System.currentTimeMillis();
        Criteria c = session.createCriteria(getContextualReferencedObservationClass())
                .add(Restrictions.eq(DataEntity.PROPERTY_DELETED, false));
        // c.createCriteria(DataEntity.FEATURE_OF_INTEREST).setProjection(
        // Projections.distinct(Projections.property(AbstractFeatureEntity.IDENTIFIER)));
        // c.createCriteria(DataEntity.OFFERINGS).add(
        // Restrictions.eq(Offering.IDENTIFIER,
        // HibernateObservationBuilder.OFFERING_1));
        c.list();
        long time = System.currentTimeMillis() - start;
        LOGGER.debug("QUERY get featureOfInterest identifiers for offering new way: {}",
                HibernateHelper.getSqlString(c));
        returnSession(session);
        return time;
    }

    private long getFoiForOfferingObservationTime() throws OwsExceptionReport {
        // old way using full Observation class (includes value table joins)
        Session session = getSession();
        long start = System.currentTimeMillis();
        final Criteria c =
                session.createCriteria(getObservationClass()).add(Restrictions.eq(DataEntity.PROPERTY_DELETED, false));
        // c.createCriteria(DataEntity.FEATURE_OF_INTEREST).setProjection(
        // Projections.distinct(Projections.property(AbstractFeatureEntity.IDENTIFIER)));
        // c.createCriteria(DataEntity.OFFERINGS).add(
        // Restrictions.eq(Offering.IDENTIFIER,
        // HibernateObservationBuilder.OFFERING_1));
        c.list();
        long time = System.currentTimeMillis() - start;
        LOGGER.debug("QUERY get featureOfInterest identifiers for offering old way: {}",
                HibernateHelper.getSqlString(c));
        returnSession(session);
        return time;
    }

    private long getFoiForOfferingHqlTime() {
        // hql method
        Session session = getSession();
        long start = System.currentTimeMillis();
        // Query<?> query =
        // session.createQuery(
        // "select distinct foi." + FeatureOfInterest.IDENTIFIER + " from
        // Observation o" + " join o."
        // + AbstractObservation.OFFERINGS + " offs " + " join o." +
        // AbstractObservation.FEATURE_OF_INTEREST
        // + " foi" + " where o.deleted = 'F' and offs." + Offering.IDENTIFIER +
        // " = :offering")
        // .setParameter("offering", HibernateObservationBuilder.OFFERING_1);
        // query.list();
        long time = System.currentTimeMillis() - start;
        // LOGGER.debug("QUERY get featureOfInterest identifiers for offering
        // HQL way: {}",
        // HibernateHelper.getSqlString(query, session));
        return time;
    }

    @BeforeClass
    public static void fillObservations() throws OwsExceptionReport {
        Session session = getSession();
        Transaction transaction = null;
        try {
            transaction = session.beginTransaction();
            I18NDAORepository i18NDAORepository = new I18NDAORepository();
            DaoFactory daoFactory = new DaoFactory();
            daoFactory.setI18NDAORepository(i18NDAORepository);
            HibernateObservationBuilder b = new HibernateObservationBuilder(session, daoFactory);
            DateTime begin = new DateTime();
            int numObs = 10000;
            for (int i = 0; i < numObs; ++i) {
                if (i % 50 == 0) {
                    LOGGER.debug(LOG_TEMPLATE, i, numObs);
                    session.flush();
                    session.clear();
                }
                b.createObservation(String.valueOf(i), begin.plusHours(i));
            }
            LOGGER.debug(LOG_TEMPLATE, numObs, numObs);
            session.flush();
            transaction.commit();
        } catch (HibernateException | CodedException he) {
            if (transaction != null) {
                transaction.rollback();
            }
            throw he;
        } finally {
            returnSession(session);
        }
    }

    @AfterClass
    public static void clearObservations() throws OwsExceptionReport {
        Session session = null;
        Transaction transaction = null;
        try {
            session = getSession();
            transaction = session.beginTransaction();
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
        // SettingsManager.getInstance().cleanup();
    }

    private enum QueryType {
        OBSERVATION_INFO, OBSERVATION, HQL
    }
}
