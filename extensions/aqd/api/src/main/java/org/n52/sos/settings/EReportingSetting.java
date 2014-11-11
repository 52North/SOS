/**
 * Copyright (C) 2012-2014 52°North Initiative for Geospatial Open Source
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
package org.n52.sos.settings;

import java.util.Set;

import org.n52.sos.config.SettingDefinition;
import org.n52.sos.config.SettingDefinitionGroup;
import org.n52.sos.config.SettingDefinitionProvider;
import org.n52.sos.config.settings.StringSettingDefinition;

import com.google.common.collect.ImmutableSet;

/**
 * Setting definition provider for AQD e-Reporting definitions
 * 
 * @author Carsten Hollmann <c.hollmann@52north.org>
 * @since 4.3.0
 *
 */
public class EReportingSetting implements SettingDefinitionProvider {

    public static final SettingDefinitionGroup GROUP = new SettingDefinitionGroup().setTitle("eReporting").setOrder(6);

    public static final String EREPORTING_NAMESPACE = "eReporting.namespace";

    public static final String EREPORTING_OBSERVATION_PREFIX = "eReporting.observation.prefix";

    public static final StringSettingDefinition EREPORTING_NAMESPACE_DEFINITION = new StringSettingDefinition()
            .setGroup(GROUP).setOrder(ORDER_0).setKey(EREPORTING_NAMESPACE).setDefaultValue("").setOptional(true)
            .setTitle("AQD e-Reporting namespace").setDescription("AQD e-Reporting namespace used as prefix for ids");

    public static final StringSettingDefinition EREPORTING_OBSERVATION_PREFIX_DEFINITION =
            new StringSettingDefinition().setGroup(GROUP).setOrder(ORDER_0).setKey(EREPORTING_OBSERVATION_PREFIX)
                    .setDefaultValue("").setOptional(true).setTitle("AQD e-Reporting observation prefix")
                    .setDescription("AQD e-Reporting observation prefix used for observation gml:id");

    @Override
    public Set<SettingDefinition<?, ?>> getSettingDefinitions() {
        return ImmutableSet.<SettingDefinition<?, ?>> of(EREPORTING_NAMESPACE_DEFINITION,
                EREPORTING_OBSERVATION_PREFIX_DEFINITION);
    }

}
