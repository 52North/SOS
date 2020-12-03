/*
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
package org.n52.series.db.da.sos;

import javax.inject.Inject;

import org.hibernate.Session;
import org.n52.iceland.ds.ConnectionProvider;
import org.n52.iceland.ds.ConnectionProviderException;
import org.n52.series.db.old.HibernateSessionStore;
import org.n52.shetland.ogc.ows.exception.CodedException;
import org.n52.shetland.ogc.ows.exception.NoApplicableCodeException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SOSHibernateSessionHolder implements HibernateSessionStore {

    private static final Logger LOGGER = LoggerFactory.getLogger(SOSHibernateSessionHolder.class);

    private static final String DATASOURCE_PROPERTIES = "/datasource.properties";

    private ConnectionProvider connectionProvider;

    @Inject
    public void setConnectionProvider(ConnectionProvider connectionProvider) {
        this.connectionProvider = connectionProvider;
    }

    private static void throwNewDatabaseConnectionException() {
        throw new RuntimeException("Could not establish database connection.");
    }

    @Override
    public Session getSession() {
        try {
            return getSession(getConnectionProvider().getConnection());
        } catch (CodedException | ConnectionProviderException cpe) {
            throwNewDatabaseConnectionException();
        }
        return null;
    }

    public static Session getSession(Object connection) throws CodedException {
        if (connection == null) {
            throw new NoApplicableCodeException().withMessage("The parameter connection is null!");
        }
        if (!(connection instanceof Session)) {
            throw new NoApplicableCodeException()
                    .withMessage("The parameter connection is not an Hibernate Session!");
        }
        return (Session) connection;
    }

    @Override
    public void returnSession(Session session) {
        getConnectionProvider().returnConnection(session);
    }

    @Override
    public void shutdown() {

    }

    public ConnectionProvider getConnectionProvider() {
        return this.connectionProvider;
    }

}
