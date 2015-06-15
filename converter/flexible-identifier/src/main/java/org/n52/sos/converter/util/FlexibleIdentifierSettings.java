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
import org.n52.sos.config.settings.BooleanSettingDefinition;

import com.google.common.collect.ImmutableSet;

public class FlexibleIdentifierSettings implements SettingDefinitionProvider {

	public static final String RETURN_HUMAN_READABLE_IDENTIFIER_KEY = "sos.flexibleIdentifier";
	
	public static final String INCLUDE_OFFERING_KEY = "sos.flexibleIdentifier.offering";
	
	public static final String INCLUDE_PROCEDURE_KEY = "sos.flexibleIdentifier.procedure";
	
	public static final String INCLUDE_OBSERVABLE_PROPERTY_KEY = "sos.flexibleIdentifier.obervableProperty";
	
	public static final String INCLUDE_FEATURE_OF_INTEREST_KEY = "sos.flexibleIdentifier.featureOfInterest";

	public static final SettingDefinitionGroup GROUP = new SettingDefinitionGroup().setTitle("FlexibleIdentifier")
	            .setOrder(ORDER_10);
	
	public static final BooleanSettingDefinition RETURN_HUMAN_READABLE_IDENTIFIER_DEFINITION =
            new BooleanSettingDefinition()
                    .setGroup(GROUP)
                    .setOrder(ORDER_0)
                    .setKey(RETURN_HUMAN_READABLE_IDENTIFIER_KEY)
                    .setDefaultValue(false)
                    .setTitle("Should the SOS return human readable identifier?")
                    .setDescription(
                            "Should the SOS return human readable identifier (gml:name as gml:identifier)?");
	
	public static final BooleanSettingDefinition INCLUDE_OFFERING_DEFINITION =
            new BooleanSettingDefinition()
                    .setGroup(GROUP)
                    .setOrder(ORDER_1)
                    .setKey(INCLUDE_OFFERING_KEY)
                    .setDefaultValue(true)
                    .setTitle("Should the SOS return human readable identifier for offering?")
                    .setDescription(
                            "Should the SOS return human readable identifier for offering (gml:name as gml:identifier)?");
	
	public static final BooleanSettingDefinition INCLUDE_PROCEDURE_DEFINITION =
            new BooleanSettingDefinition()
                    .setGroup(GROUP)
                    .setOrder(ORDER_2)
                    .setKey(INCLUDE_PROCEDURE_KEY)
                    .setDefaultValue(true)
                    .setTitle("Should the SOS return human readable identifier for procedure?")
                    .setDescription(
                            "Should the SOS return human readable identifier for procedure (gml:name as gml:identifier)?");
	
	public static final BooleanSettingDefinition INCLUDE_OBSERVABLE_PROPERTY_DEFINITION =
            new BooleanSettingDefinition()
                    .setGroup(GROUP)
                    .setOrder(ORDER_3)
                    .setKey(INCLUDE_OBSERVABLE_PROPERTY_KEY)
                    .setDefaultValue(true)
                    .setTitle("Should the SOS return human readable identifier for observableProperty?")
                    .setDescription(
                            "Should the SOS return human readable identifier for observableProperty (gml:name as gml:identifier)?");
	
	public static final BooleanSettingDefinition INCLUDE_FEATURE_OF_INTEREST_DEFINITION =
            new BooleanSettingDefinition()
                    .setGroup(GROUP)
                    .setOrder(ORDER_4)
                    .setKey(INCLUDE_FEATURE_OF_INTEREST_KEY)
                    .setDefaultValue(true)
                    .setTitle("Should the SOS return human readable identifier for featureOfInterest?")
                    .setDescription(
                            "Should the SOS return human readable identifier for featureOfInterest (gml:name as gml:identifier)?");

	private static final Set<SettingDefinition<?, ?>> DEFINITIONS = ImmutableSet
			.<SettingDefinition<?, ?>> of(
					RETURN_HUMAN_READABLE_IDENTIFIER_DEFINITION,
					INCLUDE_OFFERING_DEFINITION, INCLUDE_PROCEDURE_DEFINITION,
					INCLUDE_OBSERVABLE_PROPERTY_DEFINITION,
					INCLUDE_FEATURE_OF_INTEREST_DEFINITION);

	@Override
	    public Set<SettingDefinition<?, ?>> getSettingDefinitions() {
	        return Collections.unmodifiableSet(DEFINITIONS);
	    }
}
