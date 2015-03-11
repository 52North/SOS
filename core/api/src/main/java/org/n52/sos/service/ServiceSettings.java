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

import static org.n52.sos.config.SettingDefinitionProvider.ORDER_0;

import java.util.Collections;
import java.util.Set;

import org.n52.sos.config.SettingDefinition;
import org.n52.sos.config.SettingDefinitionGroup;
import org.n52.sos.config.SettingDefinitionProvider;
import org.n52.sos.config.settings.BooleanSettingDefinition;
import org.n52.sos.config.settings.StringSettingDefinition;
import org.n52.sos.config.settings.UriSettingDefinition;

import com.google.common.collect.Sets;

/**
 * @author Christian Autermann <c.autermann@52north.org>
 *
 * @since 4.0.0
 */
public class ServiceSettings implements SettingDefinitionProvider {

    public static final String SERVICE_URL = "service.sosUrl";

    // public static final String SUPPORTS_QUALITY = "service.supportsQuality";
    public static final String SENSOR_DIRECTORY = "service.sensorDirectory";

    public static final String USE_DEFAULT_PREFIXES = "service.useDefaultPrefixes";

    public static final String ENCODE_FULL_CHILDREN_IN_DESCRIBE_SENSOR = "service.encodeFullChildrenInDescribeSensor";

    public static final String MAX_GET_OBSERVATION_RESULTS = "service.maxGetObservationResults";

    public static final String DEREGISTER_JDBC_DRIVER = "service.jdbc.deregister";

    public static final String ADD_OUTPUTS_TO_SENSOR_ML = "service.addOutputsToSensorML";

    public static final String STRICT_SPATIAL_FILTERING_PROFILE = "service.strictSpatialFilteringProfile";

    public static final String  VALIDATE_RESPONSE = "service.response.validate";

    public static final SettingDefinitionGroup GROUP = new SettingDefinitionGroup().setTitle("Service").setOrder(2);

    public static final UriSettingDefinition SERVICE_URL_DEFINITION = new UriSettingDefinition()
            .setGroup(GROUP)
            .setOrder(ORDER_0)
            .setKey(SERVICE_URL)
            .setTitle("SOS URL")
            .setDescription(
                    "The endpoint URL of this sos which will be shown in the GetCapabilities response "
                            + "(e.g. <code>http://localhost:8080/52nSOS/sos</code> or <code>http://localhost:8080/52nSOS/service</code>)."
                            + " The path to a specific binding (like <code>/soap</code>) will appended to this URL."
                            + " For detailed information, please read the <a href=\"https://wiki.52north.org/bin/view/SensorWeb/SensorObservationServiceIVDocumentation\">documentation</a>");

//    public static final IntegerSettingDefinition MAX_GET_OBSERVATION_RESULTS_DEFINITION =
//            new IntegerSettingDefinition()
//                    .setGroup(GROUP)
//                    .setOrder(ORDER_4)
//                    .setKey(MAX_GET_OBSERVATION_RESULTS)
//                    .setDefaultValue(0)
//                    .setTitle("Maximum number of observations")
//                    .setDescription(
//                            "Maximum number of observation in GetObservation responses. "
//                                    + "Set to <code>0</code> (zero) for unlimited number of observations.");

    // TODO quality is not yet supported
    // public static final BooleanSettingDefinition SUPPORTS_QUALITY_DEFINITION
    // = new BooleanSettingDefinition()
    // .setGroup(GROUP)
    // .setOrder(5)
    // .setKey(SUPPORTS_QUALITY)
    // .setDefaultValue(true)
    // .setTitle("Supports quality")
    // .setDescription("Support quality information in observations.");

    public static final StringSettingDefinition SENSOR_DIRECTORY_DEFINITION =
            new StringSettingDefinition()
                    .setGroup(GROUP)
                    .setOrder(ORDER_7)
                    .setKey(SENSOR_DIRECTORY)
                    .setDefaultValue("/sensors")
                    .setOptional(true)
                    .setTitle("Sensor Directory")
                    .setDescription(
                            "The path to a directory with the sensor descriptions in SensorML format. "
                                    + "It can be either an absolute path (like <code>/home/user/sosconfig/sensors</code>) "
                                    + "or a path relative to the web application classes directory (e.g. <code>WEB-INF/classes/sensors</code>).");

    public static final BooleanSettingDefinition USE_DEFAULT_PREFIXES_DEFINITION = new BooleanSettingDefinition()
            .setGroup(GROUP).setOrder(ORDER_11).setKey(USE_DEFAULT_PREFIXES).setDefaultValue(false).setOptional(true)
            .setTitle("Use default prefixes for offering, procedure, features")
            .setDescription("Use default prefixes for offering, procedure, features.");

    public static final BooleanSettingDefinition ENCODE_FULL_CHILDREN_IN_DESCRIBE_SENSOR_DEFINITION =
            new BooleanSettingDefinition()
                    .setGroup(GROUP)
                    .setOrder(ORDER_12)
                    .setKey(ENCODE_FULL_CHILDREN_IN_DESCRIBE_SENSOR)
                    .setDefaultValue(true)
                    .setTitle("Encode full for child procedure SensorML in parent DescribeSensor responses")
                    .setDescription(
                            "Whether to encode full SensorML for each child procedures in a DescribeSensor response for a parent procedure.");

    public static final BooleanSettingDefinition DEREGISTER_JDBC_DRIVER_DEFINITION =
            new BooleanSettingDefinition()
                    .setGroup(GROUP)
                    .setOrder(ORDER_13)
                    .setKey(DEREGISTER_JDBC_DRIVER)
                    .setDefaultValue(true)
                    .setTitle("Deregister JDBC driver")
                    .setDescription(
                            "Should the service deregister all used JDBC driver (SQLite, PostgreSQL or H2) during shutdown process.");

    public static final BooleanSettingDefinition ADD_OUTPUTS_TO_SENSOR_ML_DEFINITION =
            new BooleanSettingDefinition()
                    .setGroup(GROUP)
                    .setOrder(ORDER_14)
                    .setKey(ADD_OUTPUTS_TO_SENSOR_ML)
                    .setDefaultValue(true)
                    .setTitle("Add outputs to DescribeSensor SensorML responses")
                    .setDescription(
                            "Whether to query example observations and dynamically add outputs to DescribeSensor SensorML responses.");

    public static final BooleanSettingDefinition STRICT_SPATIAL_FILTERING_PROFILE_DEFINITION =
            new BooleanSettingDefinition()
                    .setGroup(GROUP)
                    .setOrder(ORDER_15)
                    .setKey(STRICT_SPATIAL_FILTERING_PROFILE)
                    .setDefaultValue(false)
                    .setTitle("Should this SOS support strict Spatial Filtering Profile?")
                    .setDescription(
                            "Whether the SOS should support strict SOS 2.0 Spatial Filtering Profile. That means each observation should contain a om:parameter with sampling geometry. Else the SOS allows observations without om:parameter with sampling geometry!");

    public static final BooleanSettingDefinition VALIDATE_RESPONSE_DEFINITION =
            new BooleanSettingDefinition()
                    .setGroup(GROUP)
                    .setOrder(ORDER_16)
                    .setKey(VALIDATE_RESPONSE)
                    .setDefaultValue(false)
                    .setTitle("Should this SOS validate the XML response in non debug mode?")
                    .setDescription(
                            "Whether the SOS should validate the XML response when the debug mode is disables!");

    private static final Set<SettingDefinition<?, ?>> DEFINITIONS = Sets.<SettingDefinition<?, ?>> newHashSet(
            SERVICE_URL_DEFINITION,
//            MAX_GET_OBSERVATION_RESULTS_DEFINITION,
            // SUPPORTS_QUALITY_DEFINITION,
            SENSOR_DIRECTORY_DEFINITION, USE_DEFAULT_PREFIXES_DEFINITION,
            ENCODE_FULL_CHILDREN_IN_DESCRIBE_SENSOR_DEFINITION, DEREGISTER_JDBC_DRIVER_DEFINITION,
            ADD_OUTPUTS_TO_SENSOR_ML_DEFINITION, STRICT_SPATIAL_FILTERING_PROFILE_DEFINITION,
            VALIDATE_RESPONSE_DEFINITION);

    @Override
    public Set<SettingDefinition<?, ?>> getSettingDefinitions() {
        return Collections.unmodifiableSet(DEFINITIONS);
    }
}
