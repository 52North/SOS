/*
 * Copyright (C) 2012-2022 52°North Initiative for Geospatial Open Source
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
package org.n52.sos.service;

import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Enumeration;
import java.util.Set;

import org.n52.faroe.annotation.Configurable;
import org.n52.faroe.annotation.Setting;
import org.n52.janmayen.lifecycle.Destroyable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Sets;

/**
 * TODO JavaDoc
 *
 * @author Christian Autermann
 */
@Configurable
public class DriverCleanupListener implements Destroyable {

    public static final String DEREGISTER_JDBC_DRIVER = "service.jdbc.deregister";

    private static final Logger LOGGER = LoggerFactory.getLogger(DriverCleanupListener.class);

    private boolean deregisterJDBCDriver;

    private final Set<String> providedDrivers = Sets.newHashSet();

    public void addDriverClass(String name) {
        if (name != null && !name.isEmpty()) {
            synchronized (this.providedDrivers) {
                this.providedDrivers.add(name);
            }
        }
    }

    @Override
    public void destroy() {
        if (this.deregisterJDBCDriver) {
            cleanupDrivers(this.providedDrivers);
        } else {
            LOGGER.debug("Deregistering of JDBC driver(s) is disabled!");
        }
    }

    @Setting(DEREGISTER_JDBC_DRIVER)
    public void setDeregisterJDBCDriver(boolean deregisterJDBCDriver) {
        this.deregisterJDBCDriver = deregisterJDBCDriver;
    }

    protected void cleanupDrivers(Set<String> provided) {
        LOGGER.debug("Deregistering JDBC driver is enabled!");
        Enumeration<Driver> drivers = DriverManager.getDrivers();
        while (drivers.hasMoreElements()) {
            deregisterDriver(drivers.nextElement(), provided);
        }
    }

    private void deregisterDriver(Driver driver, Set<String> provided) {
        synchronized (this.providedDrivers) {
            if (provided.contains(driver.getClass().getName())) {
                LOGGER.debug("JDBC driver {} is marked to do not deregister", driver);
            } else {
                deregisterDriver(driver);
            }
        }

    }

    private void deregisterDriver(Driver driver) {
        try {
            DriverManager.deregisterDriver(driver);
            LOGGER.info("Deregistering JDBC driver: {}", driver);
        } catch (SQLException e) {
            LOGGER.error("Error deregistering driver " + driver, e);
        }
    }

}
