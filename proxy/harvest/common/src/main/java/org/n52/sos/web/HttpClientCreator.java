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
