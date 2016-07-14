/**
 * Copyright (C) 2012-2016 52°North Initiative for Geospatial Open Source
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

import java.util.Collections;
import java.util.Set;

import org.n52.sos.config.SettingDefinition;
import org.n52.sos.config.SettingDefinitionGroup;
import org.n52.sos.config.SettingDefinitionProvider;
import org.n52.sos.config.settings.BooleanSettingDefinition;
import org.n52.sos.config.settings.FileSettingDefinition;
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

    public static final String MAX_GET_OBSERVATION_RESULTS = "service.maxGetObservationResults";

    public static final String DEREGISTER_JDBC_DRIVER = "service.jdbc.deregister";

    public static final String STRICT_SPATIAL_FILTERING_PROFILE = "service.strictSpatialFilteringProfile";

    public static final String  VALIDATE_RESPONSE = "service.response.validate";

    public static final String EXPOSE_CHILD_OBSERVABLE_PROPERTIES = "service.exposeChildObservableProperties";
    
    public static final String UPDATE_FEATURE_GEOMETRY = "service.updateFeatureGeometry";
    
    public static final String CACHE_FILE_FOLDER = "service.cacheFileFolder";

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

    public static final BooleanSettingDefinition DEREGISTER_JDBC_DRIVER_DEFINITION =
            new BooleanSettingDefinition()
                    .setGroup(GROUP)
                    .setOrder(ORDER_13)
                    .setKey(DEREGISTER_JDBC_DRIVER)
                    .setDefaultValue(true)
                    .setTitle("Deregister JDBC driver")
                    .setDescription(
                            "Should the service deregister all used JDBC driver (SQLite, PostgreSQL or H2) during shutdown process.");


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
                            "Whether the SOS should validate the XML response when the debug mode is disabled.");

     public static final BooleanSettingDefinition EXPOSE_CHILD_OBSERVABLE_PROPERTIES_DEFINITION =
            new BooleanSettingDefinition()
                    .setGroup(GROUP)
                    .setOrder(ORDER_17)
                    .setKey(EXPOSE_CHILD_OBSERVABLE_PROPERTIES)
                    .setDefaultValue(false)
                    .setTitle("Should this SOS expose child observable properties?")
                    .setDescription(
                            "Whether the SOS should expose the children of composite phenomenons (e.g. in complex observations) instead of their parents.");
     
     public static final BooleanSettingDefinition UPDATE_FEATURE_GEOMETRY_DEFINITION =
             new BooleanSettingDefinition()
                     .setGroup(GROUP)
                     .setOrder(ORDER_18)
                     .setKey(UPDATE_FEATURE_GEOMETRY)
                     .setDefaultValue(false)
                     .setTitle("Should this SOS expand the featureOfInterest geometry with the samplingGeometry?")
                     .setDescription(
                             "Whether the SOS should expand the featureOfInterest geometry with the samplingGeometry from the inserted observation.");
     
     public static final FileSettingDefinition CACHE_FILE_FOLDER_DEFILINION = new FileSettingDefinition()
             .setGroup(GROUP)
             .setOrder(ORDER_19)
             .setKey(CACHE_FILE_FOLDER)
             .setTitle("Cache file folder")
             .setOptional(true)
             .setDescription(
                     "The path to a folder where the cache file should be stored. Default is the webapp folder. If you define a path, then grant the necessary rights to write to the tomcat user!!!");

    private static final Set<SettingDefinition<?, ?>> DEFINITIONS = Sets.<SettingDefinition<?, ?>> newHashSet(
            SERVICE_URL_DEFINITION,
            SENSOR_DIRECTORY_DEFINITION,
            DEREGISTER_JDBC_DRIVER_DEFINITION,
            STRICT_SPATIAL_FILTERING_PROFILE_DEFINITION,
            VALIDATE_RESPONSE_DEFINITION,
            EXPOSE_CHILD_OBSERVABLE_PROPERTIES_DEFINITION,
            UPDATE_FEATURE_GEOMETRY_DEFINITION,
            CACHE_FILE_FOLDER_DEFILINION);

    @Override
    public Set<SettingDefinition<?, ?>> getSettingDefinitions() {
        return Collections.unmodifiableSet(DEFINITIONS);
    }
}
