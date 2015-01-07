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
package org.n52.sos.converter.util;

import java.util.Collections;
import java.util.Set;

import org.n52.sos.config.SettingDefinition;
import org.n52.sos.config.SettingDefinitionGroup;
import org.n52.sos.config.SettingDefinitionProvider;
import org.n52.sos.config.settings.StringSettingDefinition;

import com.google.common.collect.ImmutableSet;

public class PrefixedIdentifierSetting implements SettingDefinitionProvider {
    // TODO Add this to org.n52.sos.config.SettingDefinitionProvider
    
    public static final String GLOBAL_PREFIX_KEY = "sos.prefix.global";
    
    public static final String OFFERING_PREFIX_KEY = "sos.prefix.offering";
    
    public static final String PROCEDURE_PREFIX_KEY = "sos.prefix.procedure";
    
    public static final String OBSERVABLE_PROPERTY_PREFIX_KEY = "sos.prefix.obervableProperty";
    
    public static final String FEATURE_OF_INTEREST_PREFIX_KEY = "sos.prefix.featureOfInterest";

    public static final SettingDefinitionGroup GROUP = new SettingDefinitionGroup().setTitle("Identifier Prefix")
                .setOrder(ORDER_10);
    
    public static final StringSettingDefinition GLOBAL_PREFIX_DEFINITION =
            new StringSettingDefinition()
                    .setGroup(GROUP)
                    .setOrder(ORDER_0)
                    .setKey(GLOBAL_PREFIX_KEY)
                    .setOptional(true)
                    .setDefaultValue("")
                    .setTitle("global prefix")
                    .setDescription(
                            "global prefix");
    
    public static final StringSettingDefinition OFFERING_PREFIX_DEFINITION =
        new StringSettingDefinition()
                .setGroup(GROUP)
                .setOrder(ORDER_1)
                .setKey(OFFERING_PREFIX_KEY)
                .setOptional(true)
                .setDefaultValue("")
                .setTitle("offering prefix")
                .setDescription(
                        "offering prefix");
    
    public static final StringSettingDefinition PROCEDURE_PREFIX_DEFINITION =
        new StringSettingDefinition()
                .setGroup(GROUP)
                .setOrder(ORDER_2)
                .setKey(PROCEDURE_PREFIX_KEY)
                .setOptional(true)
                .setDefaultValue("")
                .setTitle("procedure prefix")
                .setDescription(
                        "procedure prefix");
    
    public static final StringSettingDefinition OBSERVABLE_PROPERTY_PREFIX_DEFINITION =
        new StringSettingDefinition()
                .setGroup(GROUP)
                .setOrder(ORDER_3)
                .setKey(OBSERVABLE_PROPERTY_PREFIX_KEY)
                .setOptional(true)
                .setDefaultValue("")
                .setTitle("obserProp prefix")
                .setDescription(
                        "obserProp prefix");
    
    public static final StringSettingDefinition FEATURE_OF_INTEREST_PREFIX_DEFINITION =
        new StringSettingDefinition()
                .setGroup(GROUP)
                .setOrder(ORDER_4)
                .setKey(FEATURE_OF_INTEREST_PREFIX_KEY)
                .setOptional(true)
                .setDefaultValue("")
                .setTitle("feature prefix")
                .setDescription(
                        "feature prefix");

    private static final Set<SettingDefinition<?, ?>> DEFINITIONS = ImmutableSet
                    .<SettingDefinition<?, ?>> of(
                                    GLOBAL_PREFIX_DEFINITION,
                                    OFFERING_PREFIX_DEFINITION, PROCEDURE_PREFIX_DEFINITION,
                                    OBSERVABLE_PROPERTY_PREFIX_DEFINITION,
                                    FEATURE_OF_INTEREST_PREFIX_DEFINITION);

    @Override
        public Set<SettingDefinition<?, ?>> getSettingDefinitions() {
            return Collections.unmodifiableSet(DEFINITIONS);
        }

}
