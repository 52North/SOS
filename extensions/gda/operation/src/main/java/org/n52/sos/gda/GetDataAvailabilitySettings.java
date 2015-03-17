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
package org.n52.sos.gda;

import java.util.Collections;
import java.util.Set;

import org.n52.sos.config.SettingDefinition;
import org.n52.sos.config.SettingDefinitionProvider;
import org.n52.sos.config.settings.BooleanSettingDefinition;
import org.n52.sos.service.MiscSettings;

import com.google.common.collect.ImmutableSet;

public class GetDataAvailabilitySettings implements SettingDefinitionProvider {


    public static final String FORCE_GDA_VALUE_COUNT = "operation.gda.forceValueCount";
    
    public static final BooleanSettingDefinition FORCE_GDA_VALUE_COUNT_DEFINITION = new BooleanSettingDefinition()
                .setGroup(MiscSettings.GROUP)
                .setOrder(ORDER_17)
                .setKey(FORCE_GDA_VALUE_COUNT)
                .setDefaultValue(false)
                .setTitle("Should the SOS include value count in GetDataAvailability response?")
                .setDescription(
                        "Should the SOS include the value count for each timeseries in the GetDataAvailability response?");
    
    private static final Set<SettingDefinition<?, ?>> DEFINITIONS = ImmutableSet.<SettingDefinition<?, ?>> of(FORCE_GDA_VALUE_COUNT_DEFINITION);
    
    @Override
    public Set<SettingDefinition<?, ?>> getSettingDefinitions() {
        return Collections.unmodifiableSet(DEFINITIONS);
    }

}
