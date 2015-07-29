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
package org.n52.sos.statistics.api;

import org.n52.iceland.config.SettingDefinitionGroup;

/**
 * Keys for the {@link SettingDefinitionGroup} for the elasticsearch
 * configuration. MUST match with the corresponding xml file.
 */
public class ElasticsearchSettingsKeys {

    public static final String LOGGING_ENABLED = "statistics.elasticsearch.is_logging_enabled";
    public static final String CLUSTER_NAME = "statistics.elasticsearch.cluster_name";
    public static final String INDEX_NAME = "statistics.elasticsearch.index_name";
    public static final String TYPE_NAME = "statistics.elasticsearch.type_name";
    public static final String UUID = "statistics.elasticsearch.uuid";
    public static final String CLUSTER_NODES = "statistics.elasticsearch.cluster_nodes";

    // lanMode vs transportclient mode vs embedded elasticsearch server
    public static final String CONNECTION_MODE = "statistics.elasticsearch.connection_mode";
    public static final String CONNECTION_MODE_NODE = "statistics.elasticsearch.connection_mode.node";
    public static final String CONNECTION_MODE_TRANSPORT_CLIENT = "statistics.elasticsearch.connection_mode.transport_client";
    public static final String CONNECTION_MODE_EMBEDDED_SERVER = "statistics.elasticsearch.connection_mode.embedded_server";

    public static final String KIBANA_CONFIG_PATH = "statistics.elasticsearch.kibana_config_file";
    public static final String KIBANA_CONFIG_ENABLE = "statistics.elasticsearch.kibana_config_enable";
}
