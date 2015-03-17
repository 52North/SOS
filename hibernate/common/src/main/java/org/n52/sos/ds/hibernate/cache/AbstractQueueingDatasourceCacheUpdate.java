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
package org.n52.sos.ds.hibernate.cache;


import org.n52.sos.ds.ConnectionProvider;
import org.n52.sos.ds.ConnectionProviderException;
import org.n52.sos.ds.hibernate.ThreadLocalSessionFactory;
import org.n52.sos.service.Configurator;
import org.n52.sos.util.CompositeParallelAction;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.n52.sos.ogc.ows.OwsExceptionReport;

/**
 * @author Christian Autermann <c.autermann@52north.org>
 * @author Shane StClair <shane@axiomalaska.com>
 *
 * @since 4.0.0
 */
public abstract class AbstractQueueingDatasourceCacheUpdate<T extends AbstractThreadableDatasourceCacheUpdate>
        extends AbstractDatasourceCacheUpdate {
    private static final Logger LOGGER = LoggerFactory.getLogger(AbstractQueueingDatasourceCacheUpdate.class);

    private final int threads;

    private final String threadGroupName;

    private final ConnectionProvider connectionProvider = Configurator.getInstance().getDataConnectionProvider();

    private final ThreadLocalSessionFactory sessionFactory = new ThreadLocalSessionFactory(connectionProvider);

    public AbstractQueueingDatasourceCacheUpdate(int threads, String threadGroupName) {
        this.threads = threads;
        this.threadGroupName = threadGroupName;
    }

    protected abstract T[] getUpdatesToExecute() throws OwsExceptionReport;

    @Override
    public void execute() {
        LOGGER.debug("AbstractQueueingDatasourceCacheUpdate init");
        T[] updatesToExecute;
        try {
            updatesToExecute = getUpdatesToExecute();
        } catch (OwsExceptionReport ex) {
            getErrors().add(ex);
            return;
        }
        CompositeParallelAction<AbstractThreadableDatasourceCacheUpdate> compositeParallelAction =
                new CompositeParallelAction<AbstractThreadableDatasourceCacheUpdate>(threads, threadGroupName, updatesToExecute) {
            @Override
            protected void pre(AbstractThreadableDatasourceCacheUpdate action) {
                action.setCache(getCache());
                action.setErrors(getErrors());
                action.setSessionFactory(sessionFactory);
            }

            @Override
            protected void post(AbstractThreadableDatasourceCacheUpdate action) {
                action.getSession().clear();
            }
        };
        //execute multiple threads
        compositeParallelAction.execute();

        try {
            sessionFactory.close();
        } catch (ConnectionProviderException cpe) {
            LOGGER.error("Error while closing SessionFactory", cpe);
        }
    }
}
