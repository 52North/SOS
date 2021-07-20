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
package org.n52.sos.aquarius.ds;

import java.net.URISyntaxException;
import java.util.Timer;
import java.util.TimerTask;

import javax.inject.Inject;

import org.n52.faroe.ConfigurationError;
import org.n52.iceland.ds.ConnectionProviderException;
import org.n52.iceland.service.DatabaseSettingsHandler;
import org.n52.janmayen.lifecycle.Constructable;
import org.n52.janmayen.lifecycle.Destroyable;
import org.n52.shetland.ogc.ows.exception.OwsExceptionReport;
import org.n52.sos.aquarius.AquariusConstants;
import org.n52.sos.ds.datasource.AbstractAquariusH2Datasource;
import org.n52.sos.web.HttpClientHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AquariusConnectionFactory implements Constructable, Destroyable {

    private static final Logger LOGGER = LoggerFactory.getLogger(AquariusConnectionFactory.class);

    private AquariusConnection connection;

    private Timer timer = new Timer("session-keepalive", true);

    private DatabaseSettingsHandler databaseSettingsHandler;

    private HttpClientHandler httpClientHandler;

    private SessionHandler sessionHandler;

    private AquariusHelper aquariusHelper;

    @Inject
    public void setAquariusHelper(AquariusHelper aquariusHelper) {
        this.aquariusHelper = aquariusHelper;
    }

    @Inject
    public void setDatabaseSettingsHandler(DatabaseSettingsHandler databaseSettingsHandler) {
        this.databaseSettingsHandler = databaseSettingsHandler;
    }

    @Inject
    public void setHttpClientHandle(HttpClientHandler httpClientHandler) {
        this.httpClientHandler = httpClientHandler;
    }

    public AquariusConnector getConnection() throws ConnectionProviderException {
        try {
            if (connection == null) {
                return null;
            }
            return new AquariusConnector(sessionHandler, httpClientHandler, aquariusHelper);
        } catch (Exception e) {
            throw new ConnectionProviderException("Error while getting connection!", e);
        }
    }

    private String getRestPath() {
        return AquariusConstants.AQUARIUS_PATH;
    }

    @Override
    public void init() {
        try {
            LOGGER.debug("Instantiating session factory");
            String host = this.databaseSettingsHandler.getAll()
                    .getProperty(AbstractAquariusH2Datasource.PROXY_HOST_KEY);
            String user = this.databaseSettingsHandler.getAll()
                    .getProperty(AbstractAquariusH2Datasource.PROXY_USERNAME_KEY);
            String password = this.databaseSettingsHandler.getAll()
                    .getProperty(AbstractAquariusH2Datasource.PROXY_PASSWORD_KEY);
            LOGGER.debug("Server: {}", host);
            this.connection = new AquariusConnection(user, password, host, getRestPath());
            this.sessionHandler = new SessionHandler(httpClientHandler, connection);
            timer.scheduleAtFixedRate(new KeepAliveTask(), 1800000, 1800000);
        } catch (SecurityException | IllegalArgumentException e) {
            throw new ConfigurationError("An error occurs during instantiation of the AquariusConnector connection!",
                    e);
        }
    }

    @Override
    public void destroy() {
        try {
            timer.cancel();
            if (sessionHandler != null) {
                sessionHandler.delete(sessionHandler.getSession());
            }
        } catch (OwsExceptionReport e) {
            LOGGER.error("Error while destroying class!", e);
        }
    }

    public class KeepAliveTask extends TimerTask {

        @Override
        public void run() {
            try {
                sessionHandler.keepAlive(sessionHandler.getSession());
            } catch (OwsExceptionReport | URISyntaxException e) {
                LOGGER.debug("Error while executing keepalive", e);
            }
            LOGGER.debug("KeepAlive was executed!");
        }

    }

}
