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
package org.n52.sos.web.install;

import java.util.HashMap;
import java.util.Map;

import org.n52.sos.config.SettingDefinition;
import org.n52.sos.config.SettingValue;
import org.n52.sos.ds.Datasource;

/**
 * TODO JavaDoc
 * 
 * @author Christian Autermann <c.autermann@52north.org>
 * 
 * @since 4.0.0
 */
public class InstallationConfiguration {

    private Map<SettingDefinition<?, ?>, SettingValue<?>> settings =
            new HashMap<SettingDefinition<?, ?>, SettingValue<?>>();

    private Map<String, Object> databaseSettings = new HashMap<String, Object>();

    private String username;

    private String password;

    private Datasource datasource;

    private boolean createSchema = false;

    private boolean dropSchema = false;
    
    private boolean forceUpdateSchema = false;

    public InstallationConfiguration() {
    }

    public Map<SettingDefinition<?, ?>, SettingValue<?>> getSettings() {
        return settings;
    }

    public InstallationConfiguration setSettings(Map<SettingDefinition<?, ?>, SettingValue<?>> settings) {
        this.settings = settings;
        return this;
    }

    public Map<String, Object> getDatabaseSettings() {
        return databaseSettings;
    }

    public Datasource getDatasource() {
        return datasource;
    }

    public void setDatasource(Datasource datasource) {
        this.datasource = datasource;
    }

    public InstallationConfiguration setDatabaseSettings(Map<String, Object> databaseSettings) {
        this.databaseSettings = databaseSettings;
        return this;
    }

    public String getUsername() {
        return username;
    }

    public InstallationConfiguration setUsername(String username) {
        this.username = username;
        return this;
    }

    public String getPassword() {
        return password;
    }

    public InstallationConfiguration setPassword(String password) {
        this.password = password;
        return this;
    }

    public Object getDatabaseSetting(String k) {
        return databaseSettings.get(k);
    }

    public boolean getBooleanDatabaseSetting(String k) {
        return ((Boolean) getDatabaseSetting(k)).booleanValue();
    }

    public boolean hasDatabaseSetting(String k) {
        return databaseSettings.containsKey(k);
    }

    public InstallationConfiguration setDatabaseSetting(String k, Object v) {
        databaseSettings.put(k, v);
        return this;
    }

    public SettingValue<?> getSetting(SettingDefinition<?, ?> k) {
        return settings.get(k);
    }

    public InstallationConfiguration setSetting(SettingDefinition<?, ?> k, SettingValue<?> v) {
        settings.put(k, v);
        return this;
    }

    public boolean isCreateSchema() {
        return createSchema;
    }

    public void setCreateSchema(boolean createSchema) {
        this.createSchema = createSchema;
    }

    public boolean isDropSchema() {
        return dropSchema;
    }

    public void setDropSchema(boolean dropSchema) {
        this.dropSchema = dropSchema;
    }

    public boolean isForceUpdateSchema() {
        return forceUpdateSchema;
    }
    
    public void setForceUpdateSchema(boolean forceUpdateSchema) {
        this.forceUpdateSchema = forceUpdateSchema;
    }
    
}
