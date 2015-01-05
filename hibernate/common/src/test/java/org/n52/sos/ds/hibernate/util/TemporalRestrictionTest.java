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

import static org.n52.sos.ds.hibernate.util.TemporalRestrictionTest.Identifier.valueOf;

import java.util.EnumSet;
import java.util.List;
import java.util.Set;

import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.criterion.Projections;
import org.junit.After;
import org.junit.Before;
import org.n52.sos.ds.hibernate.ExtendedHibernateTestCase;
import org.n52.sos.ds.hibernate.entities.AbstractObservation;
import org.n52.sos.ogc.gml.time.Time;
import org.n52.sos.ogc.ows.OwsExceptionReport;

/**
 * @author Christian Autermann <c.autermann@52north.org>
 * 
 * @since 4.0.0
 */
public abstract class TemporalRestrictionTest extends ExtendedHibernateTestCase {
    private Time filter;

    @After
    public void cleanup() throws OwsExceptionReport {
        Session session = null;
        Transaction transaction = null;
        try {
            session = getSession();
            transaction = session.beginTransaction();
            ScrollableIterable<AbstractObservation> i =
                    ScrollableIterable.fromCriteria(session.createCriteria(getObservationClass(session)));
            for (AbstractObservation o : i) {
                session.delete(o);
            }
            i.close();
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
        try {
            this.filter = createScenario(session);
        } finally {
            returnSession(session);
        }
    }

    protected abstract Time createScenario(Session session) throws OwsExceptionReport;

    protected HibernateObservationBuilder getBuilder(Session session) throws OwsExceptionReport {
        return new HibernateObservationBuilder(session);
    }

    @SuppressWarnings("unchecked")
    private Set<Identifier> filter(TimePrimitiveFieldDescriptor d, TemporalRestriction r, Time time, Session session)
            throws OwsExceptionReport {
        List<String> list =
                session.createCriteria(getObservationClass(session)).add(r.get(d, time))
                        .setProjection(Projections.distinct(Projections.property(AbstractObservation.IDENTIFIER))).list();
        Set<Identifier> s = EnumSet.noneOf(Identifier.class);
        for (String id : list) {
            s.add(valueOf(id));
        }
        return s;
    }

    protected Set<Identifier> filterPhenomenonTime(Session session, TemporalRestriction r) throws OwsExceptionReport {
        return filter(TemporalRestrictions.PHENOMENON_TIME_FIELDS, r, filter, session);
    }

    protected Set<Identifier> filterResultTime(Session session, TemporalRestriction r) throws OwsExceptionReport {
        return filter(TemporalRestrictions.RESULT_TIME_FIELDS, r, filter, session);
    }

    public enum Identifier {
        PP_AFTER_ID, PP_MEETS_ID, PP_OVERLAPS_ID, PP_ENDED_BY_ID, PP_CONTAINS_ID, PP_EQUALS_ID, PP_BEGUN_BY_ID, PP_OVERLAPPED_BY_ID, PP_MET_BY_ID, PP_BEFORE_ID, PP_BEGINS_ID, PP_ENDS_ID, PP_DURING_ID, IP_BEFORE_ID, IP_BEGINS_ID, IP_DURING_ID, IP_ENDS_ID, IP_AFTER_ID, PI_CONTAINS_ID, PI_BEFORE_ID, PI_AFTER_ID, PI_ENDED_BY_ID, PI_BEGUN_BY_ID, II_AFTER_ID, II_EQUALS_ID, II_BEFORE_ID;
    }
}
