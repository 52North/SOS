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
import java.util.LinkedList;
import java.util.List;
import java.util.Properties;
import java.util.Set;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.n52.sos.config.SettingsManager;
import org.n52.sos.exception.ConfigurationException;
import org.n52.sos.util.Cleanupable;
import org.n52.sos.util.GeometryHandler;

import com.google.common.collect.Sets;

/**
 * @since 4.0.0
 *
 */
public class SosContextListener implements ServletContextListener {

    private static final Logger LOG = LoggerFactory.getLogger(SosContextListener.class);

    private static String path = null;
    private static final List<Runnable> hooks = new LinkedList<>();

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        setPath(sce.getServletContext().getRealPath("/"));
        if (Configurator.getInstance() == null) {
            instantiateConfigurator(sce.getServletContext());
            instantiateGeometryHandler();
        } else {
            LOG.error("Configurator already instantiated.");
        }
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        Set<String> providedJdbcDriver = null;
        if (Configurator.getInstance() != null) {
            providedJdbcDriver = Sets.newHashSet(
                    Configurator.getInstance().getProvidedJdbcDriver());
        }

        synchronized(hooks) {
            for (Runnable hook :hooks) {
                try {
                    hook.run();
                } catch (Throwable t) {
                    LOG.error("Error running shutdown hook", t);
                }
            }
        }

        cleanupConfigurator();
        cleanupSettingsManager();
        cleanupGeometryHandler();
        if (providedJdbcDriver != null) {
            cleanupDrivers(providedJdbcDriver);
        }

    }

    protected void cleanupDrivers(Set<String> providedJdbcDriver) {
        if (ServiceConfiguration.getInstance().isDeregisterJdbcDriver()) {
            LOG.debug("Deregistering JDBC driver is enabled!");
            Enumeration<Driver> drivers = DriverManager.getDrivers();
            while (drivers.hasMoreElements()) {
                Driver driver = drivers.nextElement();
                if (!providedJdbcDriver.contains(driver.getClass().getName())) {
                    try {
                        DriverManager.deregisterDriver(driver);
                        LOG.info("Deregistering JDBC driver: {}", driver);
                    } catch (SQLException e) {
                        LOG.error("Error deregistering driver " + driver, e);
                    }
                } else {
                    LOG.debug("JDBC driver {} is marked to do not deregister", driver);
                }
            }
        } else {
            LOG.debug("Deregistering of JDBC driver(s) is disabled!");
        }
    }

    protected void cleanupSettingsManager() {
        try {
            if (SettingsManager.getInstance() != null) {
                SettingsManager.getInstance().cleanup();
            }
        } catch (Throwable ex) {
            LOG.error("Error while SettingsManager clean up", ex);
        }
    }

    protected void cleanupConfigurator() {
        try {
            if (Configurator.getInstance() != null) {
                Configurator.getInstance().cleanup();
            }
        } catch (Throwable ex) {
            LOG.error("Error while Configurator clean up", ex);
        }
    }
    protected void cleanupGeometryHandler() {
        try {
            if (GeometryHandler.getInstance() != null) {
                GeometryHandler.getInstance().cleanup();
            }
        } catch (Throwable ex) {
            LOG.error("Error while GeometryHandler clean up", ex);
        }
    }
    protected void instantiateConfigurator(ServletContext context) {
        DatabaseSettingsHandler dbsh = DatabaseSettingsHandler.getInstance(context);
        if (dbsh.exists()) {
            LOG.debug("Initialising Configurator ({},{})", dbsh.getPath(), getPath());
            try {
                instantiateConfigurator(dbsh.getAll());
            } catch (ConfigurationException ex) {
                LOG.error("Error reading database properties", ex);
            }
        } else {
            LOG.warn("Can not initialize Configurator; config file is not present: {}", dbsh.getPath());
        }
    }

    protected void instantiateConfigurator(Properties p) {
        try {
            Configurator.createInstance(p, getPath());
        } catch (ConfigurationException ce) {
            String message = "Configurator initialization failed!";
            LOG.error(message, ce);
            throw new RuntimeException(message, ce);
        }
    }
    
    /**
     * Instantiate the {@link GeometryHandler} to avoid exceptions during the
     * shutdown process.
     */
    protected void instantiateGeometryHandler() {
        GeometryHandler.getInstance();
    }

    public static String getPath() {
        return SosContextListener.path;
    }

    public static void setPath(String path) {
        SosContextListener.path = path;
    }

    public static boolean hasPath() {
        return SosContextListener.path != null;
    }

    public static void registerShutdownHook(Runnable runnable) {
        if (runnable != null) {
            synchronized(hooks) {
                hooks.add(runnable);
            }
        }
    }

    public static void registerShutdownHook(final Cleanupable cleanupable) {
        if (cleanupable != null) {
            registerShutdownHook(new Runnable() {
                @Override
                public void run() {
                    cleanupable.cleanup();
                }
            });
        }
    }
}
