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

   
    public static final String EREPORTING_NAMESPACE = "eReporting.namespace";

    public static final String EREPORTING_OBSERVATION_PREFIX = "eReporting.observation.prefix";
    
    public static final String EREPORTING_OFFERING_PREFIX_KEY = "eReporting.offering.prefix";
    
    public static final String EREPORTING_PROCEDURE_PREFIX_KEY = "eReporting.procedure.prefix";
    
//    public static final String EREPORTING_OBSERVABLE_PROPERTY_PREFIX_KEY = "eReporting.obervableProperty.prefix";
    
    public static final String EREPORTING_FEATURE_OF_INTEREST_PREFIX_KEY = "eReporting.featureOfInterest.prefix";
    
    public static final String EREPORTING_SAMPLING_POINT_PREFIX_KEY = "eReporting.samplingPoint.prefix";
    
    public static final String EREPORTING_STATION_PREFIX_KEY = "eReporting.station.prefix";
    
    public static final String EREPORTING_NETWORK_PREFIX_KEY = "eReporting.network.prefix";
    
    public static final String EREPORTING_VALIDITY_FLAGS = "eReporting.flags.validity";
    
    public static final String EREPORTING_VERIFICATION_FLAGS = "eReporting.flags.verification";
    
    public static final SettingDefinitionGroup GROUP = 
            new SettingDefinitionGroup()
            .setTitle("eReporting")
            .setOrder(6)
            .setDescription("Setting to define AQD e-Reporting related settings.");

    public static final StringSettingDefinition EREPORTING_NAMESPACE_DEFINITION = 
            new StringSettingDefinition()
            .setGroup(GROUP)
            .setOrder(ORDER_0)
            .setKey(EREPORTING_NAMESPACE)
            .setDefaultValue("")
            .setOptional(true)
            .setTitle("AQD e-Reporting namespace/global prefix")
                    .setDescription(
                            "AQD e-Reporting namespace is used as global prefix for localId of inspireIds. Should end with '/' (http) or ':' (urn)");

    public static final StringSettingDefinition EREPORTING_OFFERING_PREFIX_DEFINITION =
            new StringSettingDefinition()
                    .setGroup(GROUP)
                    .setOrder(ORDER_1)
                    .setKey(EREPORTING_OFFERING_PREFIX_KEY)
                    .setOptional(true)
                    .setDefaultValue("")
                    .setTitle("Offering prefix")
                    .setDescription(
                            "This prefix is used to add a prefix to the offering identifier");
        
    public static final StringSettingDefinition EREPORTING_PROCEDURE_PREFIX_DEFINITION =
        new StringSettingDefinition()
                .setGroup(GROUP)
                .setOrder(ORDER_2)
                .setKey(EREPORTING_PROCEDURE_PREFIX_KEY)
                .setOptional(true)
                .setDefaultValue("")
                .setTitle("AQD e-Reporting Sampling Point Process/Procedure prefix")
                .setDescription(
                        "This prefix is used to add a prefix to the AQD e-Reporting Sampling Point Process/Procedure identifier, 'SPP.' for AQD e-Reporting");
    
//        public static final StringSettingDefinition EREPORTING_OBSERVABLE_PROPERTY_PREFIX_DEFINITION =
//            new StringSettingDefinition()
//                    .setGroup(GROUP)
//                    .setOrder(ORDER_3)
//                    .setKey(EREPORTING_OBSERVABLE_PROPERTY_PREFIX_KEY)
//                    .setOptional(true)
//                    .setDefaultValue("")
//                    .setTitle("ObservableProperty prefix")
//                    .setDescription(
//                            "The observableProperty prefix is used to add a prefix to the observableProperty identifier");
        
    public static final StringSettingDefinition EREPORTING_FEATURE_OF_INTEREST_PREFIX_DEFINITION =
        new StringSettingDefinition()
                .setGroup(GROUP)
                .setOrder(ORDER_4)
                .setKey(EREPORTING_FEATURE_OF_INTEREST_PREFIX_KEY)
                .setOptional(true)
                .setDefaultValue("")
                .setTitle("AQD e-Reporting Sample/FeatureOfInterest prefix")
                .setDescription(
                        "This prefix is used to add a prefix to the AQD e-Reporting Sample/FeatureOfInterest identifier, e.g. 'SAM.' for AQD e-Reporting");
    
    public static final StringSettingDefinition EREPORTING_SAMPLING_POINT_PREFIX_DEFINITION =
            new StringSettingDefinition()
                    .setGroup(GROUP)
                    .setOrder(ORDER_5)
                    .setKey(EREPORTING_SAMPLING_POINT_PREFIX_KEY)
                    .setOptional(true)
                    .setDefaultValue("")
                    .setTitle("AQD e-Reporting Sampling Point prefix")
                    .setDescription(
                            "This prefix is used to add a prefix to the Sampling Point identifier, e.g 'SPO.' for AQD e-Reporting");
    
    public static final StringSettingDefinition EREPORTING_STATION_PREFIX_DEFINITION =
            new StringSettingDefinition()
                    .setGroup(GROUP)
                    .setOrder(ORDER_6)
                    .setKey(EREPORTING_STATION_PREFIX_KEY)
                    .setOptional(true)
                    .setDefaultValue("")
                    .setTitle("AQD e-Reporting Station prefix")
                    .setDescription(
                            "This prefix is used to add a prefix to the Station identifier, e.g. 'STA.' for AQD e-Reporting");
    
    public static final StringSettingDefinition EREPORTING_NETWORK_PREFIX_DEFINITION =
            new StringSettingDefinition()
                    .setGroup(GROUP)
                    .setOrder(ORDER_7)
                    .setKey(EREPORTING_NETWORK_PREFIX_KEY)
                    .setOptional(true)
                    .setDefaultValue("")
                    .setTitle("AQD e-Reporting Network prefix")
                    .setDescription(
                            "This prefix is used to add a prefix to the Network identifier, e.g. 'NET.' for AQD e-Reporting");
    

    public static final StringSettingDefinition EREPORTING_OBSERVATION_PREFIX_DEFINITION =
            new StringSettingDefinition()
                .setGroup(GROUP)
                .setOrder(ORDER_10)
                .setKey(EREPORTING_OBSERVATION_PREFIX)
                .setDefaultValue("")
                .setOptional(true)
                .setTitle("AQD e-Reporting observation prefix")
                .setDescription("AQD e-Reporting observation prefix used for observation gml:id, e.g. 'OBS.' for AQD e-Reporting");
    
    public static final StringSettingDefinition EREPORTING_VALIDITY_FLAGS_DEFINITION =
            new StringSettingDefinition()
                .setGroup(GROUP)
                .setOrder(ORDER_11)
                .setKey(EREPORTING_VALIDITY_FLAGS)
                .setDefaultValue("1,2,3")
                .setOptional(true)
                .setTitle("Validity flag for validated data (E1 flows)")
                .setDescription("Comma separated list of validity flags (int) that indentify validated data. Conjunction (AND) with verification flags!");
    
    public static final StringSettingDefinition EREPORTING_VERIFICATION_FLAGS_DEFINITION =
            new StringSettingDefinition()
                .setGroup(GROUP)
                .setOrder(ORDER_12)
                .setKey(EREPORTING_VERIFICATION_FLAGS)
                .setDefaultValue("1")
                .setOptional(true)
                .setTitle("Verification flag for validated data (E1 flows)")
                .setDescription("Comma separated list of verificatio flags (int) that indentify validated data. Conjunction (AND) with validity flags!");

    @Override
    public Set<SettingDefinition<?, ?>> getSettingDefinitions() {
        return ImmutableSet.<SettingDefinition<?, ?>> of(EREPORTING_NAMESPACE_DEFINITION,
                EREPORTING_OFFERING_PREFIX_DEFINITION,
                EREPORTING_PROCEDURE_PREFIX_DEFINITION,
//                EREPORTING_OBSERVABLE_PROPERTY_PREFIX_DEFINITION,
                EREPORTING_FEATURE_OF_INTEREST_PREFIX_DEFINITION,
                EREPORTING_SAMPLING_POINT_PREFIX_DEFINITION,
                EREPORTING_STATION_PREFIX_DEFINITION,
                EREPORTING_NETWORK_PREFIX_DEFINITION,
                EREPORTING_OBSERVATION_PREFIX_DEFINITION,
                EREPORTING_VALIDITY_FLAGS_DEFINITION,
                EREPORTING_VERIFICATION_FLAGS_DEFINITION);
    }

}
