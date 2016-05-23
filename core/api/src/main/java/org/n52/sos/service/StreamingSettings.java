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

import com.google.common.collect.Sets;

public class StreamingSettings implements SettingDefinitionProvider {

    public static final String FORCE_STREAMING_ENCODING = "service.streaming.encoding";

    public static final SettingDefinitionGroup GROUP = new SettingDefinitionGroup().setTitle("Streaming").setOrder(5);

    public static final BooleanSettingDefinition FORCE_STREAMING_ENCODING_DEFINITION = new BooleanSettingDefinition()
            .setGroup(GROUP)
            .setOrder(ORDER_0)
            .setKey(FORCE_STREAMING_ENCODING)
            .setDefaultValue(false)
            .setTitle("Should this service stream the XML responses?")
            .setDescription(
                    "Whether the service should stream the XML response! If true, the responses are not validated!");

    private static final Set<SettingDefinition<?, ?>> DEFINITIONS = Sets.<SettingDefinition<?, ?>> newHashSet(
            FORCE_STREAMING_ENCODING_DEFINITION);

    @Override
    public Set<SettingDefinition<?, ?>> getSettingDefinitions() {
        return Collections.unmodifiableSet(DEFINITIONS);
    }

}
