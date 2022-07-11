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

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Map;
import java.util.Set;

import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.n52.faroe.ConfigurationError;
import org.n52.faroe.settings.StringSettingDefinition;

public interface AquariusDatasource extends ProxyDatasource {
    String CONGIF_ERROR = "An error occurs during instantiation of the Aquarius connection!";

    String SPRING_PROFILE = "aquarius";

    String AQUARIUS_PATH_DEFAULT_VALUE = "/AQUARIUS/Publish/v2/";

    String CONNECTOR_KEY = "proxy.aquarius.connector";

    String PROXY_USERNAME_TITLE = "Proxy Service User name";

    String PROXY_USERNAME_DESCRIPTION = "Your proxy server user name. The default value for Aquarius is \"user\".";

    String AQUARIUS_USERNAME_DEFAULT_VALUE = "user";

    String PROXY_PASSWORD_TITLE = "Proxy Service Password";

    String PROXY_PASSWORD_DESCRIPTION = "Your proxy server password. The default value is \"password\".";

    String AQUARIUS_PASSWORD_DEFAULT_VALUE = "passsword";

    String AQUARIUS_HOST_DESCRIPTION =
            "Set this to the IP/net location of Aquarius data server. The default value for Aquarius is "
                    + "\"https://aquarius.aquaticinformatics.com\".";

    String AQUARIUS_HOST_DEFAULT_VALUE = "https://aquarius.aquaticinformatics.com";

    @Override
    default Set<String> getSpringProfiles() {
        Set<String> springProfiles = ProxyDatasource.super.getSpringProfiles();
        springProfiles.add(SPRING_PROFILE);
        return springProfiles;
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
        sd.setOrder(6);
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
        sd.setOrder(8);
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
        sd.setOrder(9);
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
        sd.setOrder(7);
        sd.setKey(PROXY_PASSWORD_KEY);
        sd.setTitle(PROXY_PASSWORD_TITLE);
        return sd;
    }

    default StringSettingDefinition add(StringSettingDefinition setting, String defaultValue, String description) {
        setting.setDescription(description);
        setting.setDefaultValue(defaultValue);
        return setting;
    }

    default void validateConnection(Map<String, Object> settings) {
        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
            String host = (String) settings.get(PROXY_HOST_KEY);
            String path = AQUARIUS_PATH_DEFAULT_VALUE;
            String username = (String) settings.get(PROXY_USERNAME_KEY);
            String password = (String) settings.get(PROXY_PASSWORD_KEY);
            String url = host + path + "session";
            URIBuilder uriBuilder = new URIBuilder(url);
            uriBuilder.addParameter("Username", username);
            uriBuilder.addParameter("EncryptedPassword", password);
            URI uri = uriBuilder.build();
            HttpPost post = new HttpPost(uri);
            CloseableHttpResponse response = httpClient.execute(post);
            if (response != null && response.getEntity() != null && response.getStatusLine()
                    .getStatusCode() == 200) {
                String content = EntityUtils.toString(response.getEntity(), "UTF-8");
                if (content != null && !content.isEmpty()) {
                    HttpDelete httpDelete = new HttpDelete(url);
                    httpClient.execute(httpDelete);
                } else {
                    throw new ConfigurationError(CONGIF_ERROR);
                }
            } else {
                throw new ConfigurationError(CONGIF_ERROR);
            }
        } catch (IOException | URISyntaxException e) {
            throw new ConfigurationError(CONGIF_ERROR, e);
        }
    }

    @Override
    default String getProxyHostDescription() {
        return AQUARIUS_HOST_DESCRIPTION;
    }

    @Override
    default String getProxyHostDefaultValue() {
        return AQUARIUS_HOST_DEFAULT_VALUE;
    }

    @Override
    default String getProxyPathDefaultValue() {
        return AQUARIUS_PATH_DEFAULT_VALUE;
    }

    @Override
    default String getProxyUsernameDefaultValue() {
        return AQUARIUS_USERNAME_DEFAULT_VALUE;
    }

    @Override
    default String getProxyPasswordDefaultValue() {
        return AQUARIUS_PASSWORD_DEFAULT_VALUE;
    }

}
