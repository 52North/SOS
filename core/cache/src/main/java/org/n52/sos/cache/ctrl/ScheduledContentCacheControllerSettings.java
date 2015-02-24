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
package org.n52.sos.cache.ctrl;

import java.util.Collections;
import java.util.Set;

import org.n52.sos.config.SettingDefinition;
import org.n52.sos.config.SettingDefinitionProvider;
import org.n52.sos.config.settings.IntegerSettingDefinition;
import org.n52.sos.service.ServiceSettings;

/**
 * Settings for the {@link AbstractSchedulingContentCacheController}.
 * 
 * @author Christian Autermann <c.autermann@52north.org>
 * 
 * @since 4.0.0
 */
public class ScheduledContentCacheControllerSettings implements SettingDefinitionProvider {
    public static final String CAPABILITIES_CACHE_UPDATE_INTERVAL = "service.capabilitiesCacheUpdateInterval";

    public static final IntegerSettingDefinition CACHE_UPDATE_INTERVAL_DEFINITION = new IntegerSettingDefinition()
            .setGroup(ServiceSettings.GROUP)
            .setOrder(6)
            .setKey(CAPABILITIES_CACHE_UPDATE_INTERVAL)
            .setDefaultValue(120)
            .setMinimum(0)
            .setTitle("Content cache update interval")
            .setDescription(
                    "The update interval of the content cache in minutes. Set this to lower value if "
                            + "your database is externally modified frequently. Set to 0 to disable scheduled "
                            + "cache updates.");

    @Override
    public Set<SettingDefinition<?, ?>> getSettingDefinitions() {
        return Collections.<SettingDefinition<?, ?>> singleton(CACHE_UPDATE_INTERVAL_DEFINITION);
    }
}
