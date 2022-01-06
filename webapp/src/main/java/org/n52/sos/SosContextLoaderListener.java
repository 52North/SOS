/*
 * Copyright (C) 2012-2022 52°North Spatial Information Research GmbH
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
package org.n52.sos;


import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;

import org.n52.iceland.service.DatabaseSettingsHandler;
import org.n52.sos.ds.HibernateDatasourceConstants;
import org.n52.sos.ds.HibernateDatasourceConstants.DatabaseExtension;
import org.springframework.core.env.AbstractEnvironment;
import org.springframework.web.context.ConfigurableWebApplicationContext;
import org.springframework.web.context.ContextLoaderListener;

/**
 * {@link ContextLoaderListener} enhancement to set active profile depending on
 * database concept and add setting config location to 'contextConfigLocation'.
 *
 * @author Carsten Hollmann
 * @since 5.1.1
 *
 */
public class SosContextLoaderListener extends ContextLoaderListener {

    public static final String SETTINGS_LOCATION_PARAM = "settingsConfigLocation";
    private boolean configured;

    @Override
    public void contextInitialized(ServletContextEvent event) {
        ServletContext sc = event.getServletContext();
        DatabaseSettingsHandler handler = new DatabaseSettingsHandler();
        handler.setServletContext(sc);
        this.configured = handler.exists();
        if (configured) {
            setProfile(handler, sc);
        }
        initWebApplicationContext(event.getServletContext());
    }

    /**
     * Add profile to the context depending on the database concept
     *
     * @param handler
     *            the datasoure properties handler
     * @param sc
     *            the context
     */
    private void setProfile(DatabaseSettingsHandler handler, ServletContext sc) {
        String concept = handler.getAll()
                .getProperty(HibernateDatasourceConstants.DATABASE_CONCEPT_KEY);
        String extension = handler.getAll()
                .getProperty(HibernateDatasourceConstants.DATABASE_EXTENSION_KEY, DatabaseExtension.DATASOURCE.name());
        String profiles = handler.getAll()
                .getProperty(HibernateDatasourceConstants.SPRING_PROFILE_KEY, DatabaseExtension.DATASOURCE.name());
        sc.setInitParameter(
                AbstractEnvironment.ACTIVE_PROFILES_PROPERTY_NAME,
                String.join(",", concept.toLowerCase(), extension.toLowerCase(), profiles.toLowerCase()));
    }

    @Override
    protected void customizeContext(ServletContext sc, ConfigurableWebApplicationContext wac) {
        if (configured) {
            // add setting config location to 'contextConfigLocation' parameter
            String settingLocation = sc.getInitParameter(SETTINGS_LOCATION_PARAM);
            if (settingLocation != null && !settingLocation.isEmpty()) {
                wac.setConfigLocation(
                        new StringBuffer(sc.getInitParameter(CONFIG_LOCATION_PARAM)).append(System.lineSeparator())
                                .append(settingLocation)
                                .toString());
            }
        }
        super.customizeContext(sc, wac);
    }
}
