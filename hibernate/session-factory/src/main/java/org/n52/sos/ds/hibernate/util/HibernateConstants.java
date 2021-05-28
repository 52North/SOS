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
package org.n52.sos.ds.hibernate.util;

import org.hibernate.cfg.AvailableSettings;

/**
 * @since 4.0.0
 *
 */
public interface HibernateConstants {

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

    String HBM2DDL_NONE = "none";

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

    /*
     * Default: 0
     *
     * Seconds. If set, if an application checks out but then fails to check-in
     * [i.e. close()] a Connection within the specified period of time, the pool
     * will unceremoniously destroy() the Connection. This permits applications
     * with occasional Connection leaks to survive, rather than eventually
     * exhausting the Connection pool. And that's a shame. Zero means no
     * timeout, applications are expected to close() their own Connections.
     * Obviously, if a non-zero value is set, it should be to a value longer
     * than any Connection should reasonably be checked-out. Otherwise, the pool
     * will occasionally kill Connections in active use, which is bad. This is
     * basically a bad idea, but it's a commonly requested feature. Fix your
     * $%!@% applications so they don't leak Connections! Use this temporarily
     * in combination with debugUnreturnedConnectionStackTraces to figure out
     * where Connections are being checked-out that don't make it back into the
     * pool!
     */
    String C3P0_UNRETURNED_CONNECTION_TIMEOUT = "hibernate.c3p0.unreturnedConnectionTimeout";

    /*
     * Default: caller
     *
     * Must be one of caller, library, or none. Determines how
     * the contextClassLoader (see java.lang.Thread) of c3p0-spawned Threads is
     * determined. If caller, c3p0-spawned Threads (helper threads,
     * java.util.Timer threads) inherit their contextClassLoader from the client
     * Thread that provokes initialization of the pool. If library, the
     * contextClassLoader will be the class that loaded c3p0 classes. If none,
     * no contextClassLoader will be set (the property will be null), which in
     * practice means the system ClassLoader will be used. The default setting
     * of caller is sometimes a problem when client applications will be hot
     * redeployed by an app-server. When c3p0's Threads hold a reference to a
     * contextClassLoader from the first client that hits them, it may be
     * impossible to garbage collect a ClassLoader associated with that client
     * when it is undeployed in a running VM. Setting this to library can
     * resolve these issues.
     */
    String C3P0_CONTEXT_CLASS_LOADER_SOURCE = "hibernate.c3p0.contextClassLoaderSource";

    /*
     * Default: false
     *
     * If true, c3p0-spawned Threads will have the
     * java.security.AccessControlContext associated with c3p0 library classes.
     * By default, c3p0-spawned Threads (helper threads, java.util.Timer
     * threads) inherit their AccessControlContext from the client Thread that
     * provokes initialization of the pool. This can sometimes be a problem,
     * especially in application servers that support hot redeployment of client
     * apps. If c3p0's Threads hold a reference to an AccessControlContext from
     * the first client that hits them, it may be impossible to garbage collect
     * a ClassLoader associated with that client when it is undeployed in a
     * running VM. Also, it is possible client Threads might lack sufficient
     * permission to perform operations that c3p0 requires. Setting this to true
     * can resolve these issues.
     */
    String C3P0_PRIVILEGE_SPAWNED_THREAD = "hibernate.c3p0.privilegeSpawnedThreads";

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

    int LIMIT_EXPRESSION_DEPTH = 1000;

    String FUNC_EXTENT = "extent";

    String JDBC_TIME_ZONE = AvailableSettings.JDBC_TIME_ZONE;

    String SPRING_PROFILE = "hibernate";

}
