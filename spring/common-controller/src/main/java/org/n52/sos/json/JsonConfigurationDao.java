/*
 * Copyright (C) 2012-2022 52°North Initiative for Geospatial Open Source
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
package org.n52.sos.json;

import org.n52.faroe.ConfigurationError;
import org.n52.faroe.json.AbstractJsonDao;
import org.n52.janmayen.Json;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

public class JsonConfigurationDao extends AbstractJsonDao {

    public ObjectNode getConfig() {
        return getConfiguration();
    }

    public String getConfigString() {
        return Json.print(getConfiguration());
    }

    public void writeConfig(ObjectNode configuration) {
        if (configuration != null) {
            configuration().set(configuration);
            configuration().writeNow();
        }
    }

    public void writeConfig(String configuration) {
        if (configuration == null || configuration.isEmpty()) {
            throw new ConfigurationError("The configuration string is null or empty!");
        }
        JsonNode node = Json.loadString(configuration);
        if (!node.isObject()) {
            throw new ConfigurationError("The configuration is not a JSON object!");
        }
        writeConfig((ObjectNode) node);
    }
}
