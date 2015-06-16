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
package org.n52.sos.service;

import java.util.Collections;
import java.util.Set;

import org.n52.sos.config.SettingDefinition;
import org.n52.sos.config.SettingDefinitionGroup;
import org.n52.sos.config.SettingDefinitionProvider;
import org.n52.sos.config.settings.BooleanSettingDefinition;
import org.n52.sos.config.settings.StringSettingDefinition;

import com.google.common.collect.Sets;

/**
 * @author Shane StClair <shane@axiomalaska.com>
 * 
 * @since 4.0.0
 */
public class TransactionalSecuritySettings implements SettingDefinitionProvider {

    public static final String TRANSACTIONAL_ACTIVE = "service.security.transactional.active";

    public static final String TRANSACTIONAL_ALLOWED_IPS = "service.transactionalAllowedIps";

    public static final String TRANSACTIONAL_TOKEN = "service.transactionalToken";

    public static final String ALLOWED_PROXIES = "service.transactionalAllowedProxies";

    public static final SettingDefinitionGroup TX_SEC_GROUP =
            new SettingDefinitionGroup()
                    .setTitle("Transactional Security")
                    .setDescription(
                            "Simple security settings to restrict access to transactional methods (InsertSensor, InsertObservation, etc.)."
                                    + " Users requiring more control over security should use "
                                    + "<a href=\"http://52north.org/communities/security/wss/2.2/\">52&deg;North <abbr title=\"Web Security Service\">WSS</abbr></a>.")
                    .setOrder(1);

    public static final BooleanSettingDefinition TRANSACTIONAL_SECURITY_ACTIVE_DEFINITION =
            new BooleanSettingDefinition()
                    .setGroup(TX_SEC_GROUP)
                    .setOrder(ORDER_0)
                    .setKey(TRANSACTIONAL_ACTIVE)
                    .setTitle("Transactional security active")
                    .setOptional(true)
                    .setDescription(
                            "Activate/Deactivate transactional security support. If true, allowed IPs or token should be defined!"
                            + " If allowed IPs and/or authorization token are defined, then incoming transactional requests are checked against them.")
                    .setDefaultValue(true);

    public static final StringSettingDefinition TRANSACTIONAL_ALLOWED_IPS_DEFINITION = new StringSettingDefinition()
            .setGroup(TX_SEC_GROUP)
            .setOrder(ORDER_1)
            .setKey(TRANSACTIONAL_ALLOWED_IPS)
            .setTitle("Transactional Allowed IPs")
            .setOptional(true)
            .setDefaultValue("127.0.0.1")
            .setDescription(
                    "Comma separated ranges of IPs that should be allowed to make transactional requests. " +
                    "Use CIDR notation or raw IP addresses (e.g. <code>127.0.0.1,192.168.0.0/16</code>). " +
                    "Subnet notation is also supported (e.g. <code>192.168.0.0/255.255.0.0</code>). Leading zeros are not allowed.");

    public static final StringSettingDefinition ALLOWED_PROXY_DEFINITITION =
            new StringSettingDefinition()
                    .setGroup(TX_SEC_GROUP)
                    .setOrder(ORDER_2)
                    .setKey(ALLOWED_PROXIES)
                    .setTitle("Allowed Proxy IPs")
                    .setOptional(true)
                    .setDefaultValue("127.0.0.1")
                    .setDescription("Comma seperated list of allowed proxy IP addresses. These will be used to authorize allowed transactional IP addresses behind proxy servers.");

    public static final StringSettingDefinition TRANSACTIONAL_TOKEN_DEFINITION =
            new StringSettingDefinition()
                    .setGroup(TX_SEC_GROUP)
                    .setOrder(ORDER_3)
                    .setKey(TRANSACTIONAL_TOKEN)
                    .setTitle("Transactional authorization token")
                    .setOptional(true)
                    .setDefaultValue("")
                    .setDescription(
                            "Authorization token to require for transactional requests. Specified in the HTTP Authorization header (Authorization: {token}).");

    private static final Set<SettingDefinition<?, ?>> DEFINITIONS = Sets.<SettingDefinition<?, ?>> newHashSet(
            TRANSACTIONAL_SECURITY_ACTIVE_DEFINITION,
            TRANSACTIONAL_ALLOWED_IPS_DEFINITION,
            TRANSACTIONAL_TOKEN_DEFINITION,
            ALLOWED_PROXY_DEFINITITION);

    @Override
    public Set<SettingDefinition<?, ?>> getSettingDefinitions() {
        return Collections.unmodifiableSet(DEFINITIONS);
    }
}
