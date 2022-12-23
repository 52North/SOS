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
package org.n52.sos.web;

import org.n52.sensorweb.server.helgoland.adapters.config.DataSourceConfiguration;
import org.n52.sensorweb.server.helgoland.adapters.web.HttpClient;
import org.n52.sensorweb.server.helgoland.adapters.web.SimpleHttpClient;
import org.n52.sos.ds.datasource.ProxyDatasource;

public interface HttpClientCreator {

    default HttpClient getClient(DataSourceConfiguration config) {
        SimpleHttpClient simpleHttpClient = new SimpleHttpClient();
        if (isProxyDefined(config)) {
            simpleHttpClient.setProxyHost(config.getProperties().get(ProxyDatasource.PROXY_PROXY_HOST_KEY));
            String portValue = config.getProperties().get(ProxyDatasource.PROXY_PROXY_PORT_KEY);
            if (portValue != null && !portValue.isEmpty()) {
                simpleHttpClient.setProxyPort(portValue);
            }
        }
        return simpleHttpClient;
    }

    default boolean isProxyDefined(DataSourceConfiguration config) {
        return config.hasProperties() && config.getProperties().containsKey(ProxyDatasource.PROXY_PROXY_HOST_KEY);
    }
}
