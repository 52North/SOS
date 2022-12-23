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
package org.n52.sos.ds.datasource;

import java.util.Set;

import org.n52.faroe.settings.StringSettingDefinition;
import org.n52.iceland.ds.Datasource;

import com.google.common.collect.Sets;

public interface ProxyDatasource extends Datasource {

    String CONGIF_ERROR = "An error occurs during instantiation of the connection!";

    String SPRING_PROFILE = "proxy";

    String PROXY_HOST_KEY = "proxy.host";

    String PROXY_HOST_DEFAULT_VALUE = "http://localhost";

    String PROXY_HOST_TITLE = "Proxy Service host";

    String PROXY_HOST_DESCRIPTION = "Set this to the IP/net location of proxy data server.";

    String PROXY_PATH_KEY = "proxy.path";

    String PROXY_PATH_DEFAULT_VALUE = "/path";

    String PROXY_PATH_TITLE = "Proxy Service Path";

    String PROXY_PATH_DESCRIPTION = "Set this to the path of the REST API.";

    String PROXY_USERNAME_KEY = "proxy.username";

    String PROXY_USERNAME_DEFAULT_VALUE = "user";

    String PROXY_USERNAME_TITLE = "User name";

    String PROXY_USERNAME_DESCRIPTION = "Your proxy server user name. The default value for is \"user\".";

    String PROXY_PASSWORD_DEFAULT_VALUE = "password";

    String PROXY_PASSWORD_KEY = "proxy.password";

    String PROXY_PASSWORD_TITLE = "Password";

    String PROXY_PASSWORD_DESCRIPTION = "Your proxy server password. The default value is \"password\".";

    String PROXY_PROXY_HOST_KEY = "proxy.proxy.host";

    String PROXY_PROXY_HOST_TITLE = "Proxy host the proxy service is running behind";

    String PROXY_PROXY_PORT_KEY = "proxy.proxy.port";

    String PROXY_PROXY_PORT_TITLE = "Proxy port the proxy service is running behind";

    String PROXY_PROXY_SSL_KEY = "proxy.proxy.ssl.ignore.hostname";

    String PROXY_PROXY_SSL_TITLE = "Ignore SSL hostname validation";

    @Override
    default Set<String> getSpringProfiles() {
        return Sets.newHashSet(SPRING_PROFILE);
    }

    default StringSettingDefinition createServiceUsernameDefinition(String defaultValue, String description) {
        StringSettingDefinition sd = createServiceUsernameDefinition();
        return add(sd, defaultValue, description);
    }

    /**
     * Create settings definition for username
     *
     * @return Username settings definition
     */
    default StringSettingDefinition createServiceUsernameDefinition() {
        StringSettingDefinition sd = new StringSettingDefinition();
        sd.setGroup(BASE_GROUP);
        sd.setOrder(7);
        sd.setKey(PROXY_USERNAME_KEY);
        sd.setTitle(PROXY_USERNAME_TITLE);
        return sd;
    }

    default StringSettingDefinition createServiceHostDefinition(String defaultValue, String description) {
        StringSettingDefinition sd = createServiceHostDefinition();
        return add(sd, defaultValue, description);
    }

    /**
     * Create settings definition for host
     *
     * @return Host settings definition
     */
    default StringSettingDefinition createServiceHostDefinition() {
        StringSettingDefinition sd = new StringSettingDefinition();
        sd.setGroup(BASE_GROUP);
        sd.setOrder(5);
        sd.setKey(PROXY_HOST_KEY);
        sd.setTitle(PROXY_HOST_TITLE);
        return sd;
    }

    default StringSettingDefinition createServicePathDefinition(String defaultValue, String description) {
        StringSettingDefinition sd = createServicePathDefinition();
        return add(sd, defaultValue, description);
    }

    /**
     * Create settings definition for REST path
     *
     * @return REST path settings definition
     */
    default StringSettingDefinition createServicePathDefinition() {
        StringSettingDefinition sd = new StringSettingDefinition();
        sd.setGroup(BASE_GROUP);
        sd.setOrder(6);
        sd.setKey(PROXY_PATH_KEY);
        sd.setTitle(PROXY_PATH_TITLE);
        return sd;
    }

    default StringSettingDefinition createServicePasswordDefinition(String defaultValue, String description) {
        StringSettingDefinition sd = createServicePasswordDefinition();
        return add(sd, defaultValue, description);
    }

    default StringSettingDefinition createServicePasswordDefinition() {
        StringSettingDefinition sd = new StringSettingDefinition();
        sd.setGroup(BASE_GROUP);
        sd.setOrder(8);
        sd.setKey(PROXY_PASSWORD_KEY);
        sd.setTitle(PROXY_PASSWORD_TITLE);
        return sd;
    }

    default StringSettingDefinition add(StringSettingDefinition setting, String defaultValue, String description) {
        setting.setDescription(description);
        setting.setDefaultValue(defaultValue);
        return setting;
    }

    default String getProxyHostDescription() {
        return PROXY_HOST_DESCRIPTION;
    }

    default String getProxyHostDefaultValue() {
        return PROXY_HOST_DEFAULT_VALUE;
    }

    default String getProxyPathDescription() {
        return PROXY_PATH_DESCRIPTION;
    }

    default String getProxyPathDefaultValue() {
        return PROXY_PATH_DEFAULT_VALUE;
    }

    default String getProxyUsernameDescription() {
        return PROXY_USERNAME_DESCRIPTION;
    }

    default String getProxyUsernameDefaultValue() {
        return PROXY_USERNAME_DEFAULT_VALUE;
    }

    default String getProxyPasswordDescription() {
        return PROXY_PASSWORD_DESCRIPTION;
    }

    default String getProxyPasswordDefaultValue() {
        return PROXY_PASSWORD_DEFAULT_VALUE;
    }

}
