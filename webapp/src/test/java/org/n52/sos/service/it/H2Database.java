/**
 * Copyright (C) 2012-2016 52°North Initiative for Geospatial Open Source
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
package org.n52.sos.service.it;

import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.junit.rules.ExternalResource;
import org.n52.sos.ds.hibernate.H2Configuration;
import org.n52.sos.ds.hibernate.entities.ObservationType;
import org.n52.sos.ds.hibernate.util.ScrollableIterable;
import org.n52.sos.ogc.ows.OwsExceptionReport;

/**
 * @author Christian Autermann <c.autermann@52north.org>
 * 
 * @since 4.0.0
 */
public class H2Database extends ExternalResource {
    private final String[] defaultObservationTypes = {
            "http://www.opengis.net/def/observationType/OGC-OM/2.0/OM_CountObservation",
            "http://www.opengis.net/def/observationType/OGC-OM/2.0/OM_Measurement",
            "http://www.opengis.net/def/observationType/OGC-OM/2.0/OM_SWEArrayObservation",
            "http://www.opengis.net/def/observationType/OGC-OM/2.0/OM_TruthObservation",
            "http://www.opengis.net/def/observationType/OGC-OM/2.0/OM_CategoryObservation",
            "http://www.opengis.net/def/observationType/OGC-OM/2.0/OM_TextObservation" };

    @Override
    protected void before() throws Throwable {
        H2Configuration.assertInitialized();
    }

    @Override
    protected void after() {
        H2Configuration.recreate();
    }

    /**
     * Removes all entries of entity {@link ObservationType} from the database.
     * 
     * @throws OwsExceptionReport
     */
    protected void removeObservationTypes() throws OwsExceptionReport {
        Session session = null;
        Transaction transaction = null;
        try {
            session = getSession();
            transaction = session.beginTransaction();
            final ScrollableIterable<ObservationType> i =
                    ScrollableIterable.fromCriteria(session.createCriteria(ObservationType.class));
            for (final ObservationType o : i) {
                session.delete(o);
            }
            i.close();
            session.flush();
            transaction.commit();
        } catch (final HibernateException he) {
            if (transaction != null) {
                transaction.rollback();
            }
            throw he;
        } finally {
            returnSession(session);
        }
    }

    /**
     * Add some default entries of entity {@link ObservationType} to the test
     * database.
     * 
     * @throws OwsExceptionReport
     * @see {@link #defaultObservationTypes}
     */
    protected void addObservationTypes() throws OwsExceptionReport {
        Session session = null;
        Transaction transaction = null;
        try {
            session = getSession();
            transaction = session.beginTransaction();
            for (int i = 0; i < defaultObservationTypes.length; i++) {
                final ObservationType ot = new ObservationType();
                ot.setObservationTypeId(i);
                ot.setObservationType(defaultObservationTypes[i]);
                session.save(ot);
            }
            session.flush();
            transaction.commit();
        } catch (final HibernateException he) {
            if (transaction != null) {
                transaction.rollback();
            }
            throw he;
        } finally {
            returnSession(session);
        }
    }

    public void recreate() {
        H2Configuration.recreate();
    }

    public void truncate() {
        H2Configuration.truncate();
    }

    public Session getSession() {
        return H2Configuration.getSession();
    }

    public void returnSession(Session session) {
        H2Configuration.returnSession(session);
    }
}
