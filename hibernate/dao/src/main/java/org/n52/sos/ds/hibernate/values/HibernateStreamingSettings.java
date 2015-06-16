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
package org.n52.sos.ds.hibernate.values;

import java.util.Collections;
import java.util.Set;

import org.n52.sos.config.SettingDefinition;
import org.n52.sos.config.SettingDefinitionProvider;
import org.n52.sos.config.settings.BooleanSettingDefinition;
import org.n52.sos.config.settings.IntegerSettingDefinition;
import org.n52.sos.service.StreamingSettings;

import com.google.common.collect.Sets;

/**
 * {@link SettingDefinitionProvider} class for streaming datasource settings
 * 
 * @author Carsten Hollmann <c.hollmann@52north.org>
 * @since 4.1.0
 *
 */
public class HibernateStreamingSettings implements SettingDefinitionProvider {

    public static final String FORCE_DATASOURCE_STREAMING = "service.streaming.datasource";

    public static final String DATASOURCE_STREAMING_APPROACH = "service.streaming.datasource.approach";

    public static final String CHUNK_SIZE = "service.streaming.datasource.chunkSize";

    public static final BooleanSettingDefinition FORCE_DATASOURCE_STREAMING_DEFINITION =
            new BooleanSettingDefinition()
                    .setGroup(StreamingSettings.GROUP)
                    .setOrder(ORDER_1)
                    .setKey(FORCE_DATASOURCE_STREAMING)
                    .setDefaultValue(HibernateStreamingConfiguration.DEFAULT_STREAMING_DATASOURCE)
                    .setTitle("Should this service stream datasource values (currently only GetObservation) to encoder?")
                    .setDescription(
                            "Whether the service should stream datasource values (currently only GetObservation) to encoder if it is supported by the datasource! This reduces the memory usage.");

    public static final BooleanSettingDefinition DATASOURCE_STREAMING_APPROACH_DEFINITION =
            new BooleanSettingDefinition()
                    .setGroup(StreamingSettings.GROUP)
                    .setOrder(ORDER_2)
                    .setKey(DATASOURCE_STREAMING_APPROACH)
                    .setDefaultValue(HibernateStreamingConfiguration.DEFAULT_CHUNK_STREAMING_DATASOURCE)
                    .setTitle(
                            "Should this service query the streaming datasource values (currently only GetObservation) as chunk of x (true) ora as scrollable values?")
                    .setDescription(
                            "Whether the service should query the streaming stream datasource values (currently only GetObservation) as chunk of x (true) or as scrollable values.");

    public static final IntegerSettingDefinition CHUNK_SIZE_DEFINITION =
            new IntegerSettingDefinition()
                    .setGroup(StreamingSettings.GROUP)
                    .setKey(CHUNK_SIZE)
                    .setDefaultValue(HibernateStreamingConfiguration.DEFAULT_CHUNK_SIZE)
                    .setTitle(String.format("Number of chunk size.", HibernateStreamingConfiguration.DEFAULT_CHUNK_SIZE))
                    .setDescription(
                            "Number of chunk size, only relevant if scrollable datasource streaming is set to 'true'. If define a number <= 0, the whole values are queried at once!")
                    .setOrder(ORDER_3);

    private static final Set<SettingDefinition<?, ?>> DEFINITIONS = Sets.<SettingDefinition<?, ?>> newHashSet(
            FORCE_DATASOURCE_STREAMING_DEFINITION, DATASOURCE_STREAMING_APPROACH_DEFINITION, CHUNK_SIZE_DEFINITION);

    @Override
    public Set<SettingDefinition<?, ?>> getSettingDefinitions() {
        return Collections.unmodifiableSet(DEFINITIONS);
    }

}
