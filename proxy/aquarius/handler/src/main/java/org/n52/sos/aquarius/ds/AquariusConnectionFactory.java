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
package org.n52.sos.aquarius.ds;

import javax.inject.Inject;

import org.n52.faroe.ConfigurationError;
import org.n52.iceland.ds.ConnectionProviderException;
import org.n52.janmayen.lifecycle.Constructable;
import org.n52.sensorweb.server.helgoland.adapters.config.DataSourceConfiguration;
import org.n52.sos.aquarius.adapters.config.AquariusConfigurationProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

@SuppressFBWarnings({ "EI_EXPOSE_REP2" })
public class AquariusConnectionFactory implements Constructable {

    private static final Logger LOGGER = LoggerFactory.getLogger(AquariusConnectionFactory.class);

    private AquariusConnection connection;

    private AquariusConfigurationProvider configurationProvider;

    private AquariusHelper aquariusHelper;

    @Inject
    public void setAquariusHelper(AquariusHelper aquariusHelper) {
        this.aquariusHelper = aquariusHelper;
    }

    @Inject
    public void setAquariusConfigurationProvider(AquariusConfigurationProvider configurationProvider) {
        this.configurationProvider = configurationProvider;
    }

    public AquariusConnector getConnection() throws ConnectionProviderException {
        try {
            if (connection == null) {
                return null;
            }
            return new AquariusConnector(connection, aquariusHelper);
        } catch (Exception e) {
            throw new ConnectionProviderException("Error while getting connection!", e);
        }
    }

    @Override
    public void init() {
        try {
            LOGGER.debug("Instantiating session factory");
            DataSourceConfiguration dataSourceConfiguration = this.configurationProvider.getDataSourceConfiguration();
            String host = dataSourceConfiguration.getUrl();
            String user = dataSourceConfiguration.getUsername();
            String password = dataSourceConfiguration.getPassword();
            LOGGER.debug("Server: {}", host);
            this.connection = new AquariusConnection(user, password, host);
        } catch (SecurityException | IllegalArgumentException e) {
            throw new ConfigurationError("An error occurs during instantiation of the AquariusConnector connection!",
                    e);
        }
    }

}
