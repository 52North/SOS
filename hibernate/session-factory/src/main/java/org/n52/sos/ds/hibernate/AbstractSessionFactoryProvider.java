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
package org.n52.sos.ds.hibernate;

import java.util.concurrent.locks.ReentrantLock;

import org.hibernate.HibernateException;
import org.hibernate.SessionFactory;
import org.hibernate.internal.SessionFactoryImpl;
import org.hibernate.service.spi.Stoppable;
import org.n52.sos.ds.ConnectionProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @since 4.0.0
 * 
 */
public abstract class AbstractSessionFactoryProvider implements ConnectionProvider {

    private static final Logger LOG = LoggerFactory.getLogger(AbstractSessionFactoryProvider.class);

    private final ReentrantLock lock = new ReentrantLock();

    /*
     * (non-Javadoc)
     * 
     * @see org.n52.sos.ds.ConnectionProvider#cleanup()
     */
    @Override
    public void cleanup() {
        lock.lock();
        SessionFactory sessionFactory = getSessionFactory();
        try {
            if (getSessionFactory() != null) {
                try {
                    if (SessionFactoryImpl.class.isInstance(sessionFactory)
                            && Stoppable.class.isInstance(((SessionFactoryImpl) sessionFactory)
                                    .getConnectionProvider())) {
                        ((Stoppable) ((SessionFactoryImpl) sessionFactory).getConnectionProvider()).stop();
                    }
                    sessionFactory.close();
                    LOG.info("Connection provider closed successfully!");
                } catch (HibernateException he) {
                    LOG.error("Error while closing connection provider!", he);
                }
            }
        } finally {
            sessionFactory = null;
            lock.unlock();
        }
    }

    protected abstract SessionFactory getSessionFactory();

}
