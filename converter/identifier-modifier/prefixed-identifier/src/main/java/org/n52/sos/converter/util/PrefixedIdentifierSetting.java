/**
 * Copyright (C) 2012-2020 52°North Initiative for Geospatial Open Source
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

/**
 * This {@link SettingDefinitionProvider} is for identifier prefixes which are
 * dynamically added/removed to/from the identifier in the responses/requests.
 * 
 * @author <a href="mailto:c.hollmann@52north.org">Carsten Hollmann</a>
 * @since 4.4.0
 *
 */
public class PrefixedIdentifierSetting implements SettingDefinitionProvider {
    
    public static final String GLOBAL_PREFIX_KEY = "sos.prefix.global";
    
    public static final String OFFERING_PREFIX_KEY = "sos.prefix.offering";
    
    public static final String PROCEDURE_PREFIX_KEY = "sos.prefix.procedure";
    
    public static final String OBSERVABLE_PROPERTY_PREFIX_KEY = "sos.prefix.obervableProperty";
    
    public static final String FEATURE_OF_INTEREST_PREFIX_KEY = "sos.prefix.featureOfInterest";

    public static final SettingDefinitionGroup GROUP = 
            new SettingDefinitionGroup()
            .setTitle("PrefixIdentifier")
            .setDescription("Identifier prefixes are added/removed to/from the indetifier in the responses/requests.</br>"
                    + "The should end with a separator, e.g. '/' for URLs or ':' for URNs or '.'.")
            .setOrder(ORDER_10);
    
    public static final StringSettingDefinition GLOBAL_PREFIX_DEFINITION =
            new StringSettingDefinition()
                    .setGroup(GROUP)
                    .setOrder(ORDER_0)
                    .setKey(GLOBAL_PREFIX_KEY)
                    .setOptional(true)
                    .setDefaultValue("")
                    .setTitle("The global prefix")
                    .setDescription(
                            "This prefix is used to add/remove a prefix to/from the each identifier");
    
    public static final StringSettingDefinition OFFERING_PREFIX_DEFINITION =
        new StringSettingDefinition()
                .setGroup(GROUP)
                .setOrder(ORDER_1)
                .setKey(OFFERING_PREFIX_KEY)
                .setOptional(true)
                .setDefaultValue("")
                .setTitle("The offering prefix")
                .setDescription(
                        "This prefix is used to add/remove a prefix to/from the offering identifier");
    
    public static final StringSettingDefinition PROCEDURE_PREFIX_DEFINITION =
        new StringSettingDefinition()
                .setGroup(GROUP)
                .setOrder(ORDER_2)
                .setKey(PROCEDURE_PREFIX_KEY)
                .setOptional(true)
                .setDefaultValue("")
                .setTitle("The procedure prefix")
                .setDescription(
                        "This prefix is used to add/remove a prefix to/from the procedure identifier");
    
    public static final StringSettingDefinition OBSERVABLE_PROPERTY_PREFIX_DEFINITION =
        new StringSettingDefinition()
                .setGroup(GROUP)
                .setOrder(ORDER_3)
                .setKey(OBSERVABLE_PROPERTY_PREFIX_KEY)
                .setOptional(true)
                .setDefaultValue("")
                .setTitle("The obserProp prefix")
                .setDescription(
                        "This prefix is used to add/remove a prefix to/from the observedProperty identifier");
    
    public static final StringSettingDefinition FEATURE_OF_INTEREST_PREFIX_DEFINITION =
        new StringSettingDefinition()
                .setGroup(GROUP)
                .setOrder(ORDER_4)
                .setKey(FEATURE_OF_INTEREST_PREFIX_KEY)
                .setOptional(true)
                .setDefaultValue("")
                .setTitle("The feature prefix")
                .setDescription(
                        "This prefix is used to add/remove a prefix to/from the featureOfInterest identifier");

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
