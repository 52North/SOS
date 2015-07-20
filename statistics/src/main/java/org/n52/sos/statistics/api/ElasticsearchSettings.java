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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import javax.inject.Inject;

import org.n52.iceland.config.SettingType;
import org.n52.iceland.config.SettingsService;
import org.n52.iceland.config.annotation.Configurable;
import org.n52.iceland.config.annotation.Setting;
import org.n52.iceland.config.json.JsonSettingValue;
import org.n52.iceland.exception.ConfigurationError;
import org.n52.iceland.util.Validation;
import org.n52.sos.statistics.api.mappings.MetadataDataMapping;

@Configurable
public class ElasticsearchSettings {

    // private static final Logger logger =
    // LoggerFactory.getLogger(ElasticsearchSettings.class);

    @Inject
    private SettingsService settingsService;

    /**
     * Is statistics collection enable
     */
    private boolean loggingEnabled;

    /**
     * In LAN mode the clustername to join to.
     */
    private String clusterName;

    /**
     * Is the connection type Remote or LAN
     */
    private boolean nodeConnectionMode = true;

    /**
     * The ElasticSearch indexId of the date to be persisted under
     */
    private String indexId;

    /**
     * TypeId of the date to be persisted under
     */
    private String typeId = "ogc-type";

    /**
     * List of the nodes to try to connect to during startup
     */
    private List<String> clusterNodes;

    /**
     * Unique id of the running instance.
     */
    private String uuid;

    /**
     * Enables the kibana configuration importing into elasticsearch. Controls
     * the {@link this#kibanaConfPath} process
     */
    private boolean kibanaConfigEnable;

    /**
     * Path to the configuration file for the preconfigured kibana settings.
     */
    private String kibanaConfPath;

    // Getter Setters

    public boolean isLoggingEnabled() {
        return loggingEnabled;
    }

    @Setting(ElasticsearchSettingsKeys.LOGGING_ENABLED)
    public void setLoggingEnabled(boolean loggingEnabled) {
        this.loggingEnabled = loggingEnabled;
    }

    public String getIndexId() {
        return indexId;
    }

    @Setting(ElasticsearchSettingsKeys.INDEX_NAME)
    public void setIndexId(String indexId) {
        Validation.notNullOrEmpty(ElasticsearchSettingsKeys.INDEX_NAME, indexId);
        this.indexId = indexId;
    }

    public String getTypeId() {
        return typeId;
    }

    @Setting(ElasticsearchSettingsKeys.TYPE_NAME)
    public void setTypeId(String typeId) {
        Validation.notNullOrEmpty(ElasticsearchSettingsKeys.TYPE_NAME, typeId);
        if (typeId.equals(MetadataDataMapping.METADATA_TYPE_NAME)) {
            throw new ConfigurationError("The %s is reserved. Choose another one.", typeId);
        }
        this.typeId = typeId;
    }

    public String getUuid() {
        return uuid;
    }

    @Setting(ElasticsearchSettingsKeys.UUID)
    public void setUuid(String uuid) {
        if (uuid == null || uuid.trim().isEmpty()) {
            uuid = UUID.randomUUID().toString();
            saveStringValueToConfigFile(ElasticsearchSettingsKeys.UUID, uuid);
        }
        this.uuid = uuid;
    }

    public String getClusterName() {
        return clusterName;
    }

    @Setting(ElasticsearchSettingsKeys.CLUSTER_NAME)
    public void setClusterName(String clusterName) {
        Validation.notNullOrEmpty(ElasticsearchSettingsKeys.CLUSTER_NAME, clusterName);
        this.clusterName = clusterName;
    }

    public List<String> getClusterNodes() {
        return clusterNodes;
    }

    /**
     * this variable must not be null and format of host[:port] comma separated
     * if multiple values are given
     * 
     * @param clusterNodes
     *            list of the clusterNodes
     */
    @Setting(ElasticsearchSettingsKeys.CLUSTER_NODES)
    public void setClusterNodes(String clusterNodes) {
        this.clusterNodes = new ArrayList<>();
        Validation.notNullOrEmpty(ElasticsearchSettingsKeys.CLUSTER_NODES, clusterNodes);
        if (clusterNodes.contains(",")) {
            Arrays.asList(clusterNodes.split(",")).stream().peek(this::checkClustorNodeFormat).forEach(this.clusterNodes::add);
        } else {
            checkClustorNodeFormat(clusterNodes);
            this.clusterNodes.add(clusterNodes);
        }
    }

    private boolean checkClustorNodeFormat(String node) throws ConfigurationError {
        if (node.contains(":")) {
            String[] split = node.split(":");
            if (split.length != 2) {
                throw new ConfigurationError("Illegal format expected host[:port]", node);
            }
            try {
                Integer.valueOf(split[1]);
            } catch (NumberFormatException e) {
                throw new ConfigurationError("Illegal value for port", e);
            }
        }
        return true;
    }

    public boolean isNodeConnectionMode() {
        return nodeConnectionMode;
    }

    /**
     * Connection type to the Elasticsearch cluster. NodeClient or
     * TransportClient are supported.
     * 
     * @param choice
     *            {@link ElasticsearchSettingsKeys#CONNECTION_MODE_NODE} or
     *            {@link ElasticsearchSettingsKeys#CONNECTION_MODE_TRANSPORT_CLIENT}
     */
    @Setting(ElasticsearchSettingsKeys.CONNECTION_MODE)
    public void setNodeConnectionMode(String choice) {
        this.nodeConnectionMode = choice.equalsIgnoreCase(ElasticsearchSettingsKeys.CONNECTION_MODE_NODE);
    }

    private void saveStringValueToConfigFile(String key,
            String value) {
        JsonSettingValue<String> newValue = new JsonSettingValue<String>(SettingType.STRING, key, value);
        settingsService.changeSetting(newValue);

    }

    public String getKibanaConfPath() {
        return kibanaConfPath;
    }

    @Setting(ElasticsearchSettingsKeys.KIBANA_CONFIG_PATH)
    public void setKibanaConfPath(String kibanaConfPath) {
        this.kibanaConfPath = kibanaConfPath;
    }

    public boolean isKibanaConfigEnable() {
        return kibanaConfigEnable;
    }

    @Setting(ElasticsearchSettingsKeys.KIBANA_CONFIG_ENABLE)
    public void setKibanaConfigEnable(boolean kibanaConfigEnable) {
        this.kibanaConfigEnable = kibanaConfigEnable;
    }

    @Override
    public String toString() {
        return "ElasticsearchSettings [loggingEnabled=" + loggingEnabled + ", clusterName=" + clusterName + ", nodeConnectionMode="
                + nodeConnectionMode + ", indexId=" + indexId + ", typeId=" + typeId + ", clusterNodes=" + clusterNodes + ", uuid=" + uuid + "]";
    }
}
