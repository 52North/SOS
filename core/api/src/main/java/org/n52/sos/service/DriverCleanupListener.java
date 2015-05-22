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
package org.n52.sos.service;

import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Enumeration;
import java.util.Set;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.n52.iceland.config.SettingsManager;
import org.n52.iceland.config.annotation.Setting;
import org.n52.iceland.service.Configurator;
import org.n52.iceland.service.ServiceSettings;
import org.n52.iceland.lifecycle.Constructable;
import org.n52.iceland.lifecycle.Destroyable;

/**
 * TODO JavaDoc
 *
 * @author Christian Autermann
 */
public class DriverCleanupListener implements Constructable, Destroyable {

    private static final Logger LOGGER = LoggerFactory
            .getLogger(DriverCleanupListener.class);

    private Configurator configurator;
    private SettingsManager settingsManager;
    private boolean deregisterJDBCDriver;

    @Override
    public void init() {
        this.settingsManager.configure(this);
    }

    @Override
    public void destroy() {
        if (this.deregisterJDBCDriver) {
            cleanupDrivers(configurator.getProvidedJdbcDriver());
        } else {
            LOGGER.debug("Deregistering of JDBC driver(s) is disabled!");
        }
    }

    @Inject
    public void setConfigurator(Configurator configurator) {
        this.configurator = configurator;
    }

    @Inject
    public void setSettingsManager(SettingsManager settingsManager) {
        this.settingsManager = settingsManager;
    }

    @Setting(ServiceSettings.DEREGISTER_JDBC_DRIVER)
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
        if (provided.contains(driver.getClass().getName())) {
            LOGGER.debug("JDBC driver {} is marked to do not deregister", driver);
        } else {
            deregisterDriver(driver);
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
