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
package org.n52.sos.ds;

import java.util.Collections;
import java.util.Set;

import org.n52.sos.config.SettingDefinition;
import org.n52.sos.config.SettingDefinitionGroup;
import org.n52.sos.config.SettingDefinitionProvider;
import org.n52.sos.config.settings.BooleanSettingDefinition;
import org.n52.sos.config.settings.IntegerSettingDefinition;
import org.n52.sos.config.settings.StringSettingDefinition;

import com.google.common.collect.ImmutableSet;

/**
 * TODO JavaDoc
 * 
 * @author Christian Autermann <c.autermann@52north.org>
 * 
 * @since 4.0.0
 */
public class FeatureQuerySettingsProvider implements SettingDefinitionProvider {

    public static final String DATASOURCE_NORTHING_FIRST = "misc.datasourceNorthingFirst";

    public static final String EPSG_CODES_WITH_NORTHING_FIRST = "misc.switchCoordinatesForEpsgCodes";
    
    public static final String STORAGE_EPSG = "service.defaultEpsg";

    public static final String STORAGE_3D_EPSG = "service.default3DEpsg";

    @Deprecated
    public static final String DEFAULT_EPSG = STORAGE_EPSG;

    @Deprecated
    public static final String DEFAULT_3D_EPSG = STORAGE_3D_EPSG;
    
    public static final String DEFAULT_RESPONSE_EPSG = "service.defaultResponseEpsg";

    public static final String DEFAULT_RESPONSE_3D_EPSG = "service.defaultRespopnse3DEpsg";

    public static final String SPATIAL_DATASOURCE = "service.SpatialDatasource";
    
    public static final String SUPPORTED_CRS_KEY = "service.supportedCrs";
    
    public static final String AUTHORITY = "service.crsAuthority";
    
    public static final SettingDefinitionGroup GROUP = new SettingDefinitionGroup().setTitle("CRS")
            .setOrder(ORDER_4);

    public static final BooleanSettingDefinition DATASOURCE_NORTHING_FIRST_DEFINITION = new BooleanSettingDefinition()
            .setGroup(GROUP).setOrder(ORDER_1).setKey(DATASOURCE_NORTHING_FIRST).setDefaultValue(false)
            .setTitle("Are the geometries stored in datasource with northing first")
            .setDescription("Indicates if the geometries stored in the datasource with northing first axis order");

    public static final BooleanSettingDefinition SPATIAL_DATASOURCE_DEFINITION =
    new BooleanSettingDefinition()
            .setGroup(GROUP)
            .setOrder(ORDER_2)
            .setKey(SPATIAL_DATASOURCE)
            .setDefaultValue(true)
            .setTitle("Is datasource spatial enabled")
            .setDescription(
                    "The underlying datasource supports spatial queries and geometry data types. If not, the SOS only supports Get... operations and only BBOX spatial filtering.");

    public static final StringSettingDefinition AUTHORITY_DEFINITION = new StringSettingDefinition()
            .setGroup(GROUP)
            .setOrder(ORDER_3)
            .setKey(AUTHORITY)
            .setDefaultValue("EPSG")
            .setTitle("CRS authority")
            .setDescription("Set the CRS authority for this service, e.g. EPSG!");
    
    public static final IntegerSettingDefinition STORAGE_EPSG_DEFINITION = new IntegerSettingDefinition()
            .setGroup(GROUP)
            .setOrder(ORDER_4)
            .setKey(STORAGE_EPSG)
            .setDefaultValue(4326)
            .setTitle("Storage EPSG Code")
            .setDescription("The EPSG code in which the geometries are stored.");

    public static final IntegerSettingDefinition STORAGE_3D_EPSG_DEFINITION = new IntegerSettingDefinition()
            .setGroup(GROUP)
            .setOrder(ORDER_5)
            .setKey(STORAGE_3D_EPSG)
            .setDefaultValue(4979)
            .setTitle("Storage 3D EPSG Code")
            .setDescription("The 3D EPSG code in which the geometries are stored.");

    public static final IntegerSettingDefinition DEFAULT_RESPONSE_EPSG_DEFINITION = new IntegerSettingDefinition()
            .setGroup(GROUP)
            .setOrder(ORDER_6)
            .setKey(DEFAULT_RESPONSE_EPSG)
            .setDefaultValue(4326)
            .setTitle("Default response EPSG Code")
            .setDescription("The default EPSG code in which the geometries are returned.");

    public static final IntegerSettingDefinition DEFAULT_RESPONSE_3D_EPSG_DEFINITION = new IntegerSettingDefinition()
            .setGroup(GROUP)
            .setOrder(ORDER_7)
            .setKey(DEFAULT_RESPONSE_3D_EPSG)
            .setDefaultValue(4979)
            .setTitle("Default response 3D EPSG Code")
            .setDescription("The default 3D EPSG code in which the geometries are returned.");

    public static final StringSettingDefinition SUPPORTED_CRS_DEFINITION = new StringSettingDefinition()
            .setGroup(GROUP)
            .setOrder(ORDER_8)
            .setKey(SUPPORTED_CRS_KEY)
            .setDefaultValue("4326,31466,31467,4258")
            .setTitle("Supported crs")
            .setDescription("Set the supported crs for this service as ',' separated list! If empty, this tool supported CRS are used!");

    public static final StringSettingDefinition EPSG_CODES_WITH_REVERSED_AXIS_ORDER_DEFINITION =
            new StringSettingDefinition()
                    .setGroup(GROUP)
                    .setOrder(ORDER_9)
                    .setKey(EPSG_CODES_WITH_NORTHING_FIRST)
                    .setOptional(false)
                    .setDefaultValue(
                            "2044-2045;2081-2083;2085-2086;2093;2096-2098;2105-2132;2169-2170;2176-2180;"
                                    + "2193;2200;2206-2212;2319;2320-2462;2523-2549;2551-2735;2738-2758;2935-2941;"
                                    + "2953;3006-3030;3034-3035;3058-3059;3068;3114-3118;3126-3138;3300-3301;3328-3335;"
                                    + "3346;3350-3352;3366;3416;4001-4999;20004-20032;20064-20092;21413-21423;21473-21483;"
                                    + "21896-21899;22171;22181-22187;22191-22197;25884;27205-27232;27391-27398;27492;"
                                    + "28402-28432;28462-28492;30161-30179;30800;31251-31259;31275-31279;31281-31290;31466-31700")
                    .setTitle("EPSG Codes with Switched Coordinates")
                    .setDescription(
                            "A list of all EPSG codes with northing first coordinate axis order. The SOS transforms the axis order if the underlying datasource uses a differnent order"
                                    + "for example from lat/lon to lon/lat, or from x/y to y/x.");

    private static final Set<SettingDefinition<?, ?>> DEFINITIONS = ImmutableSet.<SettingDefinition<?, ?>> of(
            DATASOURCE_NORTHING_FIRST_DEFINITION, EPSG_CODES_WITH_REVERSED_AXIS_ORDER_DEFINITION,
            AUTHORITY_DEFINITION, STORAGE_EPSG_DEFINITION, STORAGE_3D_EPSG_DEFINITION, SPATIAL_DATASOURCE_DEFINITION, 
            SUPPORTED_CRS_DEFINITION, DEFAULT_RESPONSE_EPSG_DEFINITION, DEFAULT_RESPONSE_3D_EPSG_DEFINITION);

    @Override
    public Set<SettingDefinition<?, ?>> getSettingDefinitions() {
        return Collections.unmodifiableSet(DEFINITIONS);
    }
}
