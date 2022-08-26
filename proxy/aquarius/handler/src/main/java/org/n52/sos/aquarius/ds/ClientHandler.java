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

import org.n52.faroe.ConfigurationError;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.aquaticinformatics.aquarius.sdk.timeseries.AquariusClient;
import com.aquaticinformatics.aquarius.sdk.timeseries.servicemodels.Publish.GetKeepAlive;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import net.servicestack.client.WebServiceException;

@SuppressFBWarnings({ "EI_EXPOSE_REP", "EI_EXPOSE_REP2" })
public class ClientHandler {

    private static final Logger LOGGER = LoggerFactory.getLogger(ClientHandler.class);

    private static final int MAX_ATTEMPTS = 3;

    private AquariusClient client;

    private AquariusConnection connection;

    public ClientHandler(AquariusConnection connection) {
            this.connection = connection;
        }

    public synchronized void establishConnection(int attempt) {
        establishConnection(connection, attempt);
    }

    private synchronized void establishConnection(AquariusConnection connection, int attempt) {
        try {
            this.client = AquariusClient.createConnectedClient(connection.getHost(), connection.getUsername(),
                    connection.getPassword());
        } catch (Exception ae) {
            if (attempt < MAX_ATTEMPTS) {
                establishConnection(attempt + 1);
            }
            throw new ConfigurationError(
                    String.format("Error when establishing a connection to Aquarius for (%s, %s, %s)",
                            connection.getHost(), connection.getUsername(), connection.getPassword()),
                    ae);
        }
    }

    public synchronized void delete() throws Exception {
        if (this.client != null) {
            try {
                this.client.close();
            } catch (WebServiceException e) {
                if (e.getStatusCode() == 401) {
                    LOGGER.debug("Closing connection returns 401 - Unauthorized!");
                } else {
                    throw e;
                }
            }
        }
    }

    public synchronized void keepAlive() {
        this.client.Publish.get(new GetKeepAlive());
    }

    public synchronized AquariusClient getClient() {
        if (client == null) {
            establishConnection(0);
        }
        return client;
    }

}
