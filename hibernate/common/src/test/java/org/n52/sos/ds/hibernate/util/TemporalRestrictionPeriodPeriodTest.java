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

import static org.n52.sos.ds.hibernate.util.TemporalRestrictionTest.Identifier.PP_AFTER_ID;
import static org.n52.sos.ds.hibernate.util.TemporalRestrictionTest.Identifier.PP_BEFORE_ID;
import static org.n52.sos.ds.hibernate.util.TemporalRestrictionTest.Identifier.PP_BEGINS_ID;
import static org.n52.sos.ds.hibernate.util.TemporalRestrictionTest.Identifier.PP_BEGUN_BY_ID;
import static org.n52.sos.ds.hibernate.util.TemporalRestrictionTest.Identifier.PP_CONTAINS_ID;
import static org.n52.sos.ds.hibernate.util.TemporalRestrictionTest.Identifier.PP_DURING_ID;
import static org.n52.sos.ds.hibernate.util.TemporalRestrictionTest.Identifier.PP_ENDED_BY_ID;
import static org.n52.sos.ds.hibernate.util.TemporalRestrictionTest.Identifier.PP_ENDS_ID;
import static org.n52.sos.ds.hibernate.util.TemporalRestrictionTest.Identifier.PP_EQUALS_ID;
import static org.n52.sos.ds.hibernate.util.TemporalRestrictionTest.Identifier.PP_MEETS_ID;
import static org.n52.sos.ds.hibernate.util.TemporalRestrictionTest.Identifier.PP_MET_BY_ID;
import static org.n52.sos.ds.hibernate.util.TemporalRestrictionTest.Identifier.PP_OVERLAPPED_BY_ID;
import static org.n52.sos.ds.hibernate.util.TemporalRestrictionTest.Identifier.PP_OVERLAPS_ID;

import java.util.Set;

import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.junit.Test;
import org.n52.sos.ds.hibernate.util.TemporalRestriction.AfterRestriction;
import org.n52.sos.ds.hibernate.util.TemporalRestriction.BeforeRestriction;
import org.n52.sos.ds.hibernate.util.TemporalRestriction.BeginsRestriction;
import org.n52.sos.ds.hibernate.util.TemporalRestriction.BegunByRestriction;
import org.n52.sos.ds.hibernate.util.TemporalRestriction.ContainsRestriction;
import org.n52.sos.ds.hibernate.util.TemporalRestriction.DuringRestriction;
import org.n52.sos.ds.hibernate.util.TemporalRestriction.EndedByRestriction;
import org.n52.sos.ds.hibernate.util.TemporalRestriction.EndsRestriction;
import org.n52.sos.ds.hibernate.util.TemporalRestriction.MeetsRestriction;
import org.n52.sos.ds.hibernate.util.TemporalRestriction.MetByRestriction;
import org.n52.sos.ds.hibernate.util.TemporalRestriction.OverlappedByRestriction;
import org.n52.sos.ds.hibernate.util.TemporalRestriction.OverlapsRestriction;
import org.n52.sos.ds.hibernate.util.TemporalRestriction.TEqualsRestriction;
import org.n52.sos.exception.ows.concrete.UnsupportedTimeException;
import org.n52.sos.ogc.gml.time.TimePeriod;
import org.n52.sos.ogc.ows.OwsExceptionReport;

/**
 * TODO JavaDoc
 * 
 * @author Christian Autermann <c.autermann@52north.org>
 * 
 * @since 4.0.0
 */
public class TemporalRestrictionPeriodPeriodTest extends TemporalRestrictionTest {
    @Override
    protected TimePeriod createScenario(Session session) throws OwsExceptionReport {
        Transaction transaction = null;
        try {
            DateTime ref = new DateTime(DateTimeZone.UTC).minusDays(1);
            transaction = session.beginTransaction();
            HibernateObservationBuilder b = getBuilder(session);
            b.createObservation(PP_AFTER_ID, ref.plus(3), ref.plus(4));
            b.createObservation(PP_MET_BY_ID, ref.plus(2), ref.plus(3));
            b.createObservation(PP_OVERLAPPED_BY_ID, ref.plus(1), ref.plus(3));
            b.createObservation(PP_ENDS_ID, ref.plus(1), ref.plus(2));
            b.createObservation(PP_ENDED_BY_ID, ref.minus(3), ref.plus(2));
            b.createObservation(PP_DURING_ID, ref.minus(1), ref.plus(1));
            b.createObservation(PP_EQUALS_ID, ref.minus(2), ref.plus(2));
            b.createObservation(PP_CONTAINS_ID, ref.minus(3), ref.plus(3));
            b.createObservation(PP_BEGUN_BY_ID, ref.minus(2), ref.plus(3));
            b.createObservation(PP_BEGINS_ID, ref.minus(2), ref.minus(1));
            b.createObservation(PP_OVERLAPS_ID, ref.minus(3), ref.minus(1));
            b.createObservation(PP_MEETS_ID, ref.minus(3), ref.minus(2));
            b.createObservation(PP_BEFORE_ID, ref.minus(4), ref.minus(3));
            session.flush();
            transaction.commit();
            return new TimePeriod(ref.minus(2), ref.plus(2));
        } catch (HibernateException he) {
            if (transaction != null) {
                transaction.rollback();
            }
            throw he;
        }
    }

    @Test
    public void afterPeriodPhenomenonTime() throws OwsExceptionReport {
        Session session = getSession();
        try {
            Set<Identifier> filtered = filterPhenomenonTime(session, new AfterRestriction());
            assertThat(filtered, is(notNullValue()));
            assertThat(filtered, hasItem(PP_AFTER_ID));
            assertThat(filtered, hasSize(1));
        } finally {
            returnSession(session);
        }
    }

    @Test
    public void testAfterResultTime() throws OwsExceptionReport {
        Session session = getSession();
        try {
            Set<Identifier> filtered = filterResultTime(session, new AfterRestriction());
            assertThat(filtered, is(notNullValue()));
            assertThat(filtered, hasItem(PP_AFTER_ID));
            assertThat(filtered, hasSize(1));
        } finally {
            returnSession(session);
        }
    }

    @Test
    public void testBeforePhenomenonTime() throws OwsExceptionReport {
        Session session = getSession();
        try {
            Set<Identifier> filtered = filterPhenomenonTime(session, new BeforeRestriction());
            assertThat(filtered, is(notNullValue()));
            assertThat(filtered, hasItem(PP_BEFORE_ID));
            assertThat(filtered, hasSize(1));
        } finally {
            returnSession(session);
        }
    }

    @Test
    public void testBeforeResultTime() throws OwsExceptionReport {
        Session session = getSession();
        try {
            Set<Identifier> filtered = filterResultTime(session, new BeforeRestriction());
            assertThat(filtered, is(notNullValue()));
            assertThat(filtered, hasItems(PP_BEFORE_ID, PP_MEETS_ID, PP_CONTAINS_ID, PP_ENDED_BY_ID, PP_OVERLAPS_ID));
            assertThat(filtered, hasSize(5));
        } finally {
            returnSession(session);
        }
    }

    @Test
    public void testEqualsPhenomenonTime() throws OwsExceptionReport {
        Session session = getSession();
        try {
            Set<Identifier> filtered = filterPhenomenonTime(session, new TEqualsRestriction());
            assertThat(filtered, is(notNullValue()));
            assertThat(filtered, hasItem(PP_EQUALS_ID));
            assertThat(filtered, hasSize(1));
        } finally {
            returnSession(session);
        }
    }

    @Test(expected = UnsupportedTimeException.class)
    public void testEqualsResultTime() throws OwsExceptionReport {
        Session session = getSession();
        try {
            filterResultTime(session, new TEqualsRestriction());
        } finally {
            returnSession(session);
        }
    }

    @Test
    public void testContainsPhenomenonTime() throws OwsExceptionReport {
        Session session = getSession();
        try {
            Set<Identifier> filtered = filterPhenomenonTime(session, new ContainsRestriction());
            assertThat(filtered, is(notNullValue()));
            assertThat(filtered, hasItem(PP_CONTAINS_ID));
            assertThat(filtered, hasSize(1));
        } finally {
            returnSession(session);
        }
    }

    @Test(expected = UnsupportedTimeException.class)
    public void testContainsResultTime() throws OwsExceptionReport {
        Session session = getSession();
        try {
            filterResultTime(session, new ContainsRestriction());
        } finally {
            returnSession(session);
        }
    }

    @Test
    public void testDuringPhenomenonTime() throws OwsExceptionReport {
        Session session = getSession();
        try {
            Set<Identifier> filtered = filterPhenomenonTime(session, new DuringRestriction());
            assertThat(filtered, is(notNullValue()));
            assertThat(filtered, hasItem(PP_DURING_ID));
            assertThat(filtered, hasSize(1));
        } finally {
            returnSession(session);
        }
    }

    @Test
    public void testDuringResultTime() throws OwsExceptionReport {
        Session session = getSession();
        try {
            Set<Identifier> filtered = filterResultTime(session, new DuringRestriction());
            assertThat(filtered, is(notNullValue()));
            assertThat(filtered, hasItems(PP_OVERLAPPED_BY_ID, PP_ENDS_ID, PP_DURING_ID));
            assertThat(filtered, hasSize(3));
        } finally {
            returnSession(session);
        }
    }

    @Test
    public void testBeginsPhenomenonTime() throws OwsExceptionReport {
        Session session = getSession();
        try {
            Set<Identifier> filtered = filterPhenomenonTime(session, new BeginsRestriction());
            assertThat(filtered, is(notNullValue()));
            assertThat(filtered, hasItem(PP_BEGINS_ID));
            assertThat(filtered, hasSize(1));
        } finally {
            returnSession(session);
        }
    }

    @Test
    public void testBeginsResultTime() throws OwsExceptionReport {
        Session session = getSession();
        try {
            Set<Identifier> filtered = filterResultTime(session, new BeginsRestriction());
            assertThat(filtered, is(notNullValue()));
            assertThat(filtered, hasItems(PP_BEGINS_ID, PP_EQUALS_ID, PP_BEGUN_BY_ID));
            assertThat(filtered, hasSize(3));
        } finally {
            returnSession(session);
        }
    }

    @Test
    public void testBegunByPhenomenonTime() throws OwsExceptionReport {
        Session session = getSession();
        try {
            Set<Identifier> filtered = filterPhenomenonTime(session, new BegunByRestriction());
            assertThat(filtered, is(notNullValue()));
            assertThat(filtered, hasItem(PP_BEGUN_BY_ID));
            assertThat(filtered, hasSize(1));
        } finally {
            returnSession(session);
        }
    }

    @Test(expected = UnsupportedTimeException.class)
    public void testBegunByResultTime() throws OwsExceptionReport {
        Session session = getSession();
        try {
            filterResultTime(session, new BegunByRestriction());
        } finally {
            returnSession(session);
        }
    }

    @Test
    public void testEndsPhenomenonTime() throws OwsExceptionReport {
        Session session = getSession();
        try {
            Set<Identifier> filtered = filterPhenomenonTime(session, new EndsRestriction());
            assertThat(filtered, is(notNullValue()));
            assertThat(filtered, hasItem(PP_ENDS_ID));
            assertThat(filtered, hasSize(1));
        } finally {
            returnSession(session);
        }
    }

    @Test
    public void testEndsResultTime() throws OwsExceptionReport {
        Session session = getSession();
        try {
            Set<Identifier> filtered = filterResultTime(session, new EndsRestriction());
            assertThat(filtered, is(notNullValue()));
            assertThat(filtered, hasItem(PP_MET_BY_ID));
            assertThat(filtered, hasSize(1));
        } finally {
            returnSession(session);
        }
    }

    @Test
    public void testEndedByPhenomenonTime() throws OwsExceptionReport {
        Session session = getSession();
        try {
            Set<Identifier> filtered = filterPhenomenonTime(session, new EndedByRestriction());
            assertThat(filtered, is(notNullValue()));
            assertThat(filtered, hasItem(PP_ENDED_BY_ID));
            assertThat(filtered, hasSize(1));
        } finally {
            returnSession(session);
        }
    }

    @Test(expected = UnsupportedTimeException.class)
    public void testEndedByResultTime() throws OwsExceptionReport {
        Session session = getSession();
        try {
            filterResultTime(session, new EndedByRestriction());
        } finally {
            returnSession(session);
        }
    }

    @Test
    public void testOverlapsPhenomenonTime() throws OwsExceptionReport {
        Session session = getSession();
        try {
            Set<Identifier> filtered = filterPhenomenonTime(session, new OverlapsRestriction());
            assertThat(filtered, is(notNullValue()));
            assertThat(filtered, hasItem(PP_OVERLAPS_ID));
            assertThat(filtered, hasSize(1));
        } finally {
            returnSession(session);
        }
    }

    @Test(expected = UnsupportedTimeException.class)
    public void testOverlapsResultTime() throws OwsExceptionReport {
        Session session = getSession();
        try {
            filterResultTime(session, new OverlapsRestriction());
        } finally {
            returnSession(session);
        }
    }

    @Test
    public void testOverlappedByPhenomenonTime() throws OwsExceptionReport {
        Session session = getSession();
        try {
            Set<Identifier> filtered = filterPhenomenonTime(session, new OverlappedByRestriction());
            assertThat(filtered, is(notNullValue()));
            assertThat(filtered, hasItem(PP_OVERLAPPED_BY_ID));
            assertThat(filtered, hasSize(1));
        } finally {
            returnSession(session);
        }
    }

    @Test(expected = UnsupportedTimeException.class)
    public void testOverlappedByResultTime() throws OwsExceptionReport {
        Session session = getSession();
        try {
            filterResultTime(session, new OverlappedByRestriction());
        } finally {
            returnSession(session);
        }
    }

    @Test
    public void testMeetsPhenomenonTime() throws OwsExceptionReport {
        Session session = getSession();
        try {
            Set<Identifier> filtered = filterPhenomenonTime(session, new MeetsRestriction());
            assertThat(filtered, is(notNullValue()));
            assertThat(filtered, hasItem(PP_MEETS_ID));
            assertThat(filtered, hasSize(1));
        } finally {
            returnSession(session);
        }
    }

    @Test(expected = UnsupportedTimeException.class)
    public void testMeetsResultTime() throws OwsExceptionReport {
        Session session = getSession();
        try {
            filterResultTime(session, new MeetsRestriction());
        } finally {
            returnSession(session);
        }
    }

    @Test
    public void testMetByPhenomenonTime() throws OwsExceptionReport {
        Session session = getSession();
        try {
            Set<Identifier> filtered = filterPhenomenonTime(session, new MetByRestriction());
            assertThat(filtered, is(notNullValue()));
            assertThat(filtered, hasItem(PP_MET_BY_ID));
            assertThat(filtered, hasSize(1));
        } finally {
            returnSession(session);
        }
    }

    @Test(expected = UnsupportedTimeException.class)
    public void testMetByResultTime() throws OwsExceptionReport {
        Session session = getSession();
        try {
            filterResultTime(session, new MetByRestriction());
        } finally {
            returnSession(session);
        }
    }
}
