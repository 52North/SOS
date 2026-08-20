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

import java.util.EnumSet;
import java.util.List;
import java.util.Set;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;

import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.n52.iceland.i18n.I18NDAORepository;
import org.n52.series.db.beans.DataEntity;
import org.n52.shetland.ogc.filter.FilterConstants;
import org.n52.shetland.ogc.filter.TemporalFilter;
import org.n52.shetland.ogc.gml.time.Time;
import org.n52.shetland.ogc.ows.exception.OwsExceptionReport;
import org.n52.shetland.ogc.sos.Sos2Constants;
import org.n52.sos.ds.hibernate.ExtendedHibernateTestCase;
import org.n52.sos.ds.hibernate.H2Configuration;
import org.n52.sos.ds.hibernate.dao.DaoFactory;

/**
 * @author <a href="mailto:c.autermann@52north.org">Christian Autermann</a>
 *
 * @since 4.0.0
 */
public abstract class TemporalRestrictionTest extends ExtendedHibernateTestCase
        implements TemporalRestrictionTestConstants {
    private Time filter;

    @After
    public void cleanup() throws OwsExceptionReport {
        Session session = null;
        Transaction transaction = null;
        try {
            session = getSession();
            transaction = getTransaction(session);
            CriteriaBuilder cb = session.getCriteriaBuilder();
            CriteriaQuery<? extends DataEntity> query = cb.createQuery(getObservationClass());
            query.from(getObservationClass());
            for (DataEntity o : session.createQuery(query).getResultList()) {
                session.delete(o);
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

    @Before
    public void createScenario() throws OwsExceptionReport {
        Session session = getSession();
        HibernateMetadataCache.init(session);
        try {
            this.filter = createScenario(session);
        } finally {
            returnSession(session);
        }
    }

    @AfterClass
    public static void cleanUp() {
        H2Configuration.recreate();
    }

    protected abstract Time createScenario(Session session) throws OwsExceptionReport;

    protected HibernateObservationBuilder getBuilder(Session session) throws OwsExceptionReport {
        I18NDAORepository i18NDAORepository = new I18NDAORepository();
        DaoFactory daoFactory = new DaoFactory();
        daoFactory.setI18NDAORepository(i18NDAORepository);
        return new HibernateObservationBuilder(session, daoFactory);
    }

    private Set<Identifier> filter(TemporalFilter tf, Session session)
            throws OwsExceptionReport {
        CriteriaBuilder cb = session.getCriteriaBuilder();
        CriteriaQuery<String> query = cb.createQuery(String.class);
        Root<? extends DataEntity> root = query.from(getObservationClass());
        Predicate p = SosTemporalRestrictions.filter(cb, root, tf);
        query.select(root.get(DataEntity.IDENTIFIER))
                .distinct(true)
                .where(p);
        List<String> list = session.createQuery(query).getResultList();
        Set<Identifier> s = EnumSet.noneOf(Identifier.class);
        for (String id : list) {
            if (id.contains("/")) {
                s.add(TemporalRestrictionTest.Identifier.valueOf(id.substring(id.indexOf("/") + 1)));
            } else {
                s.add(TemporalRestrictionTest.Identifier.valueOf(id));
            }
        }
        return s;
    }

    protected Set<Identifier> filterPhenomenonTime(Session session, FilterConstants.TimeOperator r) throws OwsExceptionReport {
        return filter(new TemporalFilter(r, filter, Sos2Constants.PHENOMENON_TIME_VALUE_REFERENCE), session);
    }

    protected Set<Identifier> filterResultTime(Session session, FilterConstants.TimeOperator r) throws OwsExceptionReport {
        return filter(new TemporalFilter(r, filter, Sos2Constants.RESULT_TIME_VALUE_REFERENCE), session);
    }

    public enum Identifier {
        PP_AFTER_ID,
        PP_MEETS_ID,
        PP_OVERLAPS_ID,
        PP_ENDED_BY_ID,
        PP_CONTAINS_ID,
        PP_EQUALS_ID,
        PP_BEGUN_BY_ID,
        PP_OVERLAPPED_BY_ID,
        PP_MET_BY_ID,
        PP_BEFORE_ID,
        PP_BEGINS_ID,
        PP_ENDS_ID,
        PP_DURING_ID,
        IP_BEFORE_ID,
        IP_BEGINS_ID,
        IP_DURING_ID,
        IP_ENDS_ID,
        IP_AFTER_ID,
        PI_CONTAINS_ID,
        PI_BEFORE_ID,
        PI_AFTER_ID,
        PI_ENDED_BY_ID,
        PI_BEGUN_BY_ID,
        II_AFTER_ID,
        II_EQUALS_ID,
        II_BEFORE_ID;
    }
}
