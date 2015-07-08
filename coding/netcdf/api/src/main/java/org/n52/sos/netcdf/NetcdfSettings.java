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
package org.n52.sos.netcdf;

import java.util.Collections;
import java.util.Set;

import org.n52.sos.config.SettingDefinition;
import org.n52.sos.config.SettingDefinitionGroup;
import org.n52.sos.config.SettingDefinitionProvider;
import org.n52.sos.config.settings.ChoiceSettingDefinition;
import org.n52.sos.config.settings.IntegerSettingDefinition;
import org.n52.sos.config.settings.NumericSettingDefinition;
import org.n52.sos.config.settings.StringSettingDefinition;

import ucar.nc2.NetcdfFileWriter;

import com.axiomalaska.cf4j.CFStandardNames;
import com.google.common.collect.ImmutableSet;

public class NetcdfSettings implements SettingDefinitionProvider {
    
    public static final String NETCDF_VERSION = "netcdf.version";
    public static final String NETCDF_CHUNK_SIZE_TIME = "netcdf.chunk.size";
    public static final String NETCDF_FILL_VALUE = "netcdf.fillValue";
    public static final String NETCDF_HEIGHT_DEPTH = "netcdf.heightDepth";
    
    private static final SettingDefinitionGroup SETTINGS_GROUP = new SettingDefinitionGroup()
    .setTitle("netCDF").setDescription("Define netCDF specific parameter").setOrder(10.5f);
    
    private static final Set<SettingDefinition<?, ?>> DEFINITIONS = ImmutableSet.<SettingDefinition<?, ?>> of(
            new ChoiceSettingDefinition().
            addOption(NetcdfFileWriter.Version.netcdf3.name(), "NetCDF 3 version").
            addOption(NetcdfFileWriter.Version.netcdf4.name(), "NetCDF 4 version").
            setGroup(SETTINGS_GROUP).
            setKey(NETCDF_VERSION).
            setTitle("NetCDF version").
            setDefaultValue(NetcdfFileWriter.Version.netcdf4.name()).
            setDescription("Set the NetCDF version for the encoding").
            setOptional(false).
            setOrder(ORDER_0),
            
            new IntegerSettingDefinition().
            setGroup(SETTINGS_GROUP).
            setKey(NETCDF_CHUNK_SIZE_TIME).
            setTitle("NetCDF chunk size time").
            setDefaultValue(1000).
            setDescription("Set the chunk size for time variable of NetCDF 4 encoding").
            setOptional(false).
            setOrder(ORDER_1),
            
            // TODO should be a NumericSettingDefinition()
            new NumericSettingDefinition().
            setGroup(SETTINGS_GROUP).
            setKey(NETCDF_FILL_VALUE).
            setTitle("NetCDF _FillValue").
            setDefaultValue(-9999.9).
            setDescription("Set the netCDF _FillValue").
            setOptional(false).
            setOrder(ORDER_2),
            
            new ChoiceSettingDefinition().
            addOption(CFStandardNames.HEIGHT.getName(), "height").
            addOption(CFStandardNames.DEPTH.getName(), "depth").
            setGroup(SETTINGS_GROUP).
            setKey(NETCDF_HEIGHT_DEPTH).
            setTitle("Vertical variable height or depth").
            setDefaultValue(CFStandardNames.HEIGHT.getName()).
            setDescription("Define which vertical variable should be use, height or depth").
            setOptional(false).
            setOrder(ORDER_3)
            
            );

    @Override
    public Set<SettingDefinition<?, ?>> getSettingDefinitions() {
        return Collections.unmodifiableSet(DEFINITIONS);
    }

}
