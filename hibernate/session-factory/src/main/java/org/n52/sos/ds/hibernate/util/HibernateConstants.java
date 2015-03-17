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

import org.hibernate.cfg.AvailableSettings;

/**
 * @since 4.0.0
 *
 */
public interface HibernateConstants {

    /**
     * @deprecated no entity has this relation
     * TODO Eike: move to according entity class
     */
    @Deprecated
    public static final String PARAMETER_OFFERING_EXTENSION_ID = "offeringExtensionId";
    String DIALECT = "hibernate.dialect";

    String DRIVER_CLASS = AvailableSettings.DRIVER;

    String CONNECTION_PROVIDER_CLASS = AvailableSettings.CONNECTION_PROVIDER;

    String CONNECTION_URL = AvailableSettings.URL;

    String CONNECTION_USERNAME = AvailableSettings.USER;

    String CONNECTION_PASSWORD = AvailableSettings.PASS;

    String DEFAULT_CATALOG = AvailableSettings.DEFAULT_CATALOG;

    String DEFAULT_SCHEMA = AvailableSettings.DEFAULT_SCHEMA;

    String HBM2DDL_AUTO = AvailableSettings.HBM2DDL_AUTO;

    String CONNECTION_POOL_SIZE = AvailableSettings.POOL_SIZE;

    String CONNECTION_RELEASE_MODE = AvailableSettings.RELEASE_CONNECTIONS;

    String CURRENT_SESSION_CONTEXT = AvailableSettings.CURRENT_SESSION_CONTEXT_CLASS;

    String CONNECTION_RELEASE_MODE_AFTER_TRANSACTION = "after_transaction";

    String CONNECTION_RELEASE_MODE_AFTER_STATEMENT = "after_statement";

    String CONNECTION_RELEASE_MODE_ON_CLOSE = "on_close";

    String CONNECTION_RELEASE_MODE_AUTO = "auto";

    String THREAD_LOCAL_SESSION_CONTEXT = "thread";

    String HBM2DDL_UPDATE = "update";

    String HBM2DDL_VALIDATE = "validate";

    String HBM2DDL_CREATE = "create";

    String HBM2DDL_CREATE_DROP = "create-drop";

    String C3P0_MIN_SIZE = AvailableSettings.C3P0_MIN_SIZE;

    String C3P0_MAX_SIZE = AvailableSettings.C3P0_MAX_SIZE;

    String C3P0_IDLE_TEST_PERIOD = AvailableSettings.C3P0_IDLE_TEST_PERIOD;

    String C3P0_ACQUIRE_INCREMENT = AvailableSettings.C3P0_ACQUIRE_INCREMENT;

    String C3P0_TIMEOUT = AvailableSettings.C3P0_TIMEOUT;

    String C3P0_MAX_STATEMENTS = AvailableSettings.C3P0_MAX_STATEMENTS;

    String C3P0_PREFERRED_TEST_QUERY = "hibernate.c3p0.preferredTestQuery";

    String JDBC_BATCH_SIZE = AvailableSettings.STATEMENT_BATCH_SIZE;

    //FIXME Not a valid property, remove?
    @Deprecated
    String CONNECTION_AUTO_RECONNECT = "hibernate.connection.autoReconnect";

    //FIXME Not a valid property, remove?
    @Deprecated
    String CONNECTION_AUTO_RECONNECT_FOR_POOLS = "hibernate.connection.autoReconnectForPools";

    //FIXME Not a valid property, remove?
    @Deprecated
    String CONNECTION_TEST_ON_BORROW = "hibernate.connection.testOnBorrow";

    String MAX_FETCH_DEPTH = AvailableSettings.MAX_FETCH_DEPTH;

    String CONNECION_FINDER = org.hibernate.spatial.HibernateSpatialConfiguration.AvailableSettings.CONNECTION_FINDER;

    int LIMIT_EXPRESSION_DEPTH = 1000;

    String FUNC_EXTENT = "extent";
}
