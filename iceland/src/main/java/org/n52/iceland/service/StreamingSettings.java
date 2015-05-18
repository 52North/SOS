/**
 * Copyright 2015 52°North Initiative for Geospatial Open Source
 * Software GmbH
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.n52.iceland.service;

import java.util.Collections;
import java.util.Set;

import org.n52.iceland.config.SettingDefinition;
import org.n52.iceland.config.SettingDefinitionGroup;
import org.n52.iceland.config.SettingDefinitionProvider;
import org.n52.iceland.config.settings.BooleanSettingDefinition;

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
