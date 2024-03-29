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
package org.n52.sos.ds.datasource;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import org.n52.faroe.SettingDefinition;

import com.google.common.collect.Sets;

public abstract class AbstractAquariusH2Datasource extends AbstractH2ProxyDatasource implements AquariusDatasource {

    private static final long serialVersionUID = 1L;

    private String usernameDefault;

    private String usernameDescription;

    private String passwordDefault;

    private String passwordDescription;

//    private String restPathDefault;
//
//    private String restPathDescription;

    private String hostDefault;

    private String hostDescription;

    public AbstractAquariusH2Datasource() {
        super();
        setServiceUsernameDefault(getProxyUsernameDefaultValue());
        setServiceUsernameDescription(getProxyUsernameDescription());
        setServicePasswordDefault(getProxyPasswordDefaultValue());
        setServicePasswordDescription(getProxyPasswordDescription());
        setServiceHostDefault(getProxyHostDefaultValue());
        setServiceHostDescription(getProxyHostDescription());
//        setServicePathDefault(getProxyPathDefaultValue());
//        setServicePathDescription(getProxyPathDescription());
    }

    @Override
    public Set<String> getSpringProfiles() {
        return AquariusDatasource.super.getSpringProfiles();
    }

    @Override
    public Set<SettingDefinition<?>> getSettingDefinitions() {
        return Sets.<SettingDefinition<?>> newHashSet(
                createServiceUsernameDefinition(usernameDefault, usernameDescription),
                createServicePasswordDefinition(passwordDefault, passwordDescription),
                createServiceHostDefinition(hostDefault, hostDescription));
//                createServicePathDefinition(restPathDefault, restPathDescription));
    }

    @Override
    public Set<SettingDefinition<?>> getChangableSettingDefinitions(Properties current) {
        Map<String, Object> settings = parseDatasourceProperties(current);
        return Sets.<SettingDefinition<?>> newHashSet(
                createServiceUsernameDefinition((String) settings.get(PROXY_USERNAME_KEY), usernameDescription),
                createServicePasswordDefinition((String) settings.get(PROXY_PASSWORD_KEY), passwordDescription),
                createServiceHostDefinition((String) settings.get(PROXY_HOST_KEY), hostDescription));
//                createServicePathDefinition((String) settings.get(PROXY_PATH_KEY), restPathDescription));
    }

    @Override
    public Properties getDatasourceProperties(Properties current, Map<String, Object> changed) {
        return getDatasourceProperties(mergeProperties(current, changed));
    }

    @Override
    public Properties getDatasourceProperties(Map<String, Object> settings) {
        Properties p = super.getDatasourceProperties(settings);
        p.put(PROXY_USERNAME_KEY, settings.get(PROXY_USERNAME_KEY));
        p.put(PROXY_PASSWORD_KEY, settings.get(PROXY_PASSWORD_KEY));
        return p;
    }

    @Override
    public Map<String, Object> parseDatasourceProperties(Properties current) {
        final Map<String, Object> settings = new HashMap<String, Object>(current.size());
        settings.put(PROXY_USERNAME_KEY, current.getProperty(PROXY_USERNAME_KEY));
        settings.put(PROXY_PASSWORD_KEY, current.getProperty(PROXY_PASSWORD_KEY));
        if (current.containsKey(PROXY_HOST_KEY)) {
            settings.put(PROXY_HOST_KEY, current.getProperty(PROXY_HOST_KEY));
        }
        if (current.containsKey(PROXY_PATH_KEY)) {
            settings.put(PROXY_PATH_KEY, current.getProperty(PROXY_PATH_KEY));
        }
        return settings;
    }

    @Override
    public void validateConnection(Map<String, Object> settings) {
        AquariusDatasource.super.validateConnection(settings);
    }

    @Override
    public void validateConnection(Properties current, Map<String, Object> changed) {
        validateConnection(mergeProperties(current, changed));
    }

    /**
     * @param usernameDefault
     *            the usernameDefault to set
     */
    public void setServiceUsernameDefault(String usernameDefault) {
        this.usernameDefault = usernameDefault;
    }

    /**
     * @param usernameDescription
     *            the usernameDescription to set
     */
    public void setServiceUsernameDescription(String usernameDescription) {
        this.usernameDescription = usernameDescription;
    }

    /**
     * @param passwordDefault
     *            the passwordDefault to set
     */
    public void setServicePasswordDefault(String passwordDefault) {
        this.passwordDefault = passwordDefault;
    }

    /**
     * @param passwordDescription
     *            the passwordDescription to set
     */
    public void setServicePasswordDescription(String passwordDescription) {
        this.passwordDescription = passwordDescription;
    }

    /**
     * @param hostDefault
     *            the hostDefault to set
     */
    public void setServiceHostDefault(String hostDefault) {
        this.hostDefault = hostDefault;
    }

    /**
     * @param hostDescription
     *            the hostDescription to set
     */
    public void setServiceHostDescription(String hostDescription) {
        this.hostDescription = hostDescription;
    }


//    public void setServicePathDescription(String restPathDescription) {
//        this.restPathDescription = restPathDescription;
//    }
//
//    public void setServicePathDefault(String restPathDefaultValue) {
//        this.restPathDefault = restPathDefaultValue;
//    }

}
