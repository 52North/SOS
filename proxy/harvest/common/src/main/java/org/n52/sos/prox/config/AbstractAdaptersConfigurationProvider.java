/*
 * Copyright (C) 2012-2023 52°North Spatial Information Research GmbH
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
package org.n52.sos.prox.config;

import java.util.Collections;
import java.util.List;
import java.util.Properties;

import javax.inject.Inject;

import org.n52.bjornoya.schedule.DefaultJobConfiguration;
import org.n52.bjornoya.schedule.JobConfiguration;
import org.n52.bjornoya.schedule.JobConfiguration.JobType;
import org.n52.iceland.service.DatasourceSettingsHandler;
import org.n52.sensorweb.server.helgoland.adapters.config.ConfigurationProvider;
import org.n52.sensorweb.server.helgoland.adapters.config.Credentials;
import org.n52.sensorweb.server.helgoland.adapters.config.DataSourceConfiguration;
import org.n52.sos.ds.datasource.ProxyDatasource;

public abstract class AbstractAdaptersConfigurationProvider implements ConfigurationProvider {

    @Inject
    private DatasourceSettingsHandler datasourceSettingsHandler;

    @Inject
    private DefaultJobConfiguration defaultJobConfiguration;

    @Override
    public List<DataSourceConfiguration> getDataSources() {
        return Collections.singletonList(getDataSourceConfiguration());
    }

    public DataSourceConfiguration getDataSourceConfiguration() {
        DataSourceConfiguration config = new DataSourceConfiguration();
        config.setItemName(getItemName());
        config.setConnector(getConnector());
        config.setUrl(getUrl());
        config.setType(getType());
        addValues(config);
        config.addJobs(getFullHarvestJob());
        config.addJobs(getTemporalHarvestJob());
        return config;
    }

    protected abstract String getGroup();

    protected abstract String getName();

    protected abstract String getType();

    protected abstract String getConnector();

    protected abstract String getItemName();

    protected String getRestPath() {
        return getProperties().getProperty(ProxyDatasource.PROXY_PATH_KEY, "");
    }

    private String getUrl() {
        return getProperties().getProperty(ProxyDatasource.PROXY_HOST_KEY);
    }

    private DataSourceConfiguration addValues(DataSourceConfiguration config) {
        if (getProperties().containsKey(ProxyDatasource.PROXY_USERNAME_KEY)
                || getProperties().containsKey(ProxyDatasource.PROXY_PASSWORD_KEY)) {
            Credentials credentials = config.isSetCredentials() ? config.getCredentials() : new Credentials();

            if (getProperties().containsKey(ProxyDatasource.PROXY_USERNAME_KEY)) {
                credentials.setUsername(getProperties().getProperty(ProxyDatasource.PROXY_USERNAME_KEY));
            }
            if (getProperties().containsKey(ProxyDatasource.PROXY_PASSWORD_KEY)) {
                credentials.setPassword(getProperties().getProperty(ProxyDatasource.PROXY_PASSWORD_KEY));
            }
            config.setCredentials(credentials);
        }
        if (isProxyDefined()) {
            config.addProperty(ProxyDatasource.PROXY_PROXY_HOST_KEY,
                    getProperties().getProperty(ProxyDatasource.PROXY_PROXY_HOST_KEY));
            String portValue = getProperties().getProperty(ProxyDatasource.PROXY_PROXY_PORT_KEY);
            if (portValue != null && !portValue.isEmpty()) {
                config.addProperty(ProxyDatasource.PROXY_PROXY_PORT_KEY, portValue);
            }
        }
        return config;
    }

    private JobConfiguration addValues(JobConfiguration config) {
        return config.setGroup(getGroup());
    }

    private boolean isProxyDefined() {
        return getProperties().containsKey(ProxyDatasource.PROXY_PROXY_HOST_KEY);
    }

    private Properties getProperties() {
        return this.datasourceSettingsHandler.getAll();
    }

    private JobConfiguration getFullHarvestJob() {
        return addValues(defaultJobConfiguration.getFullJobConfiguration(getJobName(JobType.full)));
    }

    private String getJobName(JobType jobType) {
        return getName() + " - " + jobType.name();
    }

    private JobConfiguration getTemporalHarvestJob() {
        return addValues(defaultJobConfiguration.getTemporalJobConfiguration(getJobName(JobType.temporal)));
    }
}
