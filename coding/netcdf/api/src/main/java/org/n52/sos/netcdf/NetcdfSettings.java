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
package org.n52.sos.netcdf;

import java.util.Collections;
import java.util.Set;

import org.n52.sos.config.SettingDefinition;
import org.n52.sos.config.SettingDefinitionGroup;
import org.n52.sos.config.SettingDefinitionProvider;
import org.n52.sos.config.settings.BooleanSettingDefinition;
import org.n52.sos.config.settings.ChoiceSettingDefinition;
import org.n52.sos.config.settings.IntegerSettingDefinition;
import org.n52.sos.config.settings.NumericSettingDefinition;
import org.n52.sos.config.settings.StringSettingDefinition;
import org.n52.sos.iso.CodeList.CiRoleCodes;
import org.n52.sos.util.Constants;

import ucar.nc2.NetcdfFileWriter;

import com.axiomalaska.cf4j.CFStandardNames;
import com.google.common.collect.ImmutableSet;

/**
 * Implementation of {@link SettingDefinitionProvider} for netCDF encoding
 * 
 * @author <a href="mailto:c.hollmann@52north.org">Carsten Hollmann</a>
 * @since 4.4.0
 *
 */
public class NetcdfSettings implements SettingDefinitionProvider {
    
    public static final String NETCDF_VERSION = "netcdf.version";

    public static final String NETCDF_CHUNK_SIZE_TIME = "netcdf.chunk.size";

    public static final String NETCDF_FILL_VALUE = "netcdf.fillValue";

    public static final String NETCDF_HEIGHT_DEPTH = "netcdf.heightDepth";

    public static final String NETCDF_VARIABLE_TYPE = "netcdf.varibale.type";

    public static final String NETCDF_VARIABLE_UPPER_CASE = "netcdf.varibale.upperCase";

    public static final String NETCDF_PUBLISHER = "netcdf.publisher";

    public static final String NETCDF_CONTRIBUTOR = "netcdf.contributor";
    
    public static final String NETCDF_PHEN_LATITUDE = "netcdf.phenomenon.latitude";

    public static final String NETCDF_PHEN_LONGITUDE = "netcdf.phenomenon.longitude";

    public static final String NETCDF_PHEN_Z = "netcdf.phenomenon.z";
    
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
            setDescription("Set the NetCDF version for the encoding. "
                    + "Notice: NetCDF 4 requires the installation of the native netCDF4 c library."
                    + "Inforamtion about the installation of the native netCDF4 c library can be found here: "
                    + "<a href=\"https://www.unidata.ucar.edu/software/thredds/v4.3/netcdf-java/reference/netcdf4Clibrary.html\" target=\"_blank\"/>").
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
            setOrder(ORDER_3),
            
            new ChoiceSettingDefinition().
            addOption(Double.class.getSimpleName(), "DOUBLE").
            addOption(Float.class.getSimpleName(), "FLOAT").
            setGroup(SETTINGS_GROUP).
            setKey(NETCDF_VARIABLE_TYPE).
            setTitle("Set the variable type").
            setDefaultValue(Double.class.getSimpleName()).
            setDescription("Define the variable type for latitude, longitude, height/depth, values.").
            setOptional(false).
            setOrder(ORDER_4),
            
            new BooleanSettingDefinition().
            setGroup(SETTINGS_GROUP).
            setKey(NETCDF_VARIABLE_UPPER_CASE).
            setTitle("Use UPPER_CASE variable/dimension names").
            setDefaultValue(false).
            setDescription("Set TRUE if the variable/dimension names should be UPPER_CASE").
            setOptional(false).
            setOrder(ORDER_5),
            
            getCiRoleCodeChoiceSettingDefinition().
            setGroup(SETTINGS_GROUP).
            setKey(NETCDF_PUBLISHER).
            setTitle("Set the publisher CI_RoleCode definition").
            setDefaultValue(CiRoleCodes.CI_RoleCode_publisher.name()).
            setDescription("Define the publisher CI_RoleCode definition.").
            setOptional(false).
            setOrder(ORDER_6),
            
            getCiRoleCodeChoiceSettingDefinition().
            setGroup(SETTINGS_GROUP).
            setKey(NETCDF_CONTRIBUTOR).
            setTitle("Set the contributor CI_RoleCode definition").
            setDefaultValue(CiRoleCodes.CI_RoleCode_principalInvestigator.name()).
            setDescription("Define the contributor CI_RoleCode definition.").
            setOptional(false).
            setOrder(ORDER_7),
            
            new StringSettingDefinition().
            setGroup(SETTINGS_GROUP).
            setKey(NETCDF_PHEN_LATITUDE).
            setTitle("Set latitude phenomenon identifier").
            setDefaultValue(Constants.EMPTY_STRING).
            setDescription("Define the phenomenon identifier for latitude values. Multiple values as comma separated list.").
            setOptional(true).
            setOrder(ORDER_8),
            
            new StringSettingDefinition().
            setGroup(SETTINGS_GROUP).
            setKey(NETCDF_PHEN_LONGITUDE).
            setTitle("Set longitude phenomenon identifier").
            setDefaultValue(Constants.EMPTY_STRING).
            setDescription("Define the phenomenon identifier for longitude values. Multiple values as comma separated list.").
            setOptional(true).
            setOrder(ORDER_9),
            
            new StringSettingDefinition().
            setGroup(SETTINGS_GROUP).
            setKey(NETCDF_PHEN_Z).
            setTitle("Set height/depth phenomenon identifier").
            setDefaultValue(Constants.EMPTY_STRING).
            setDescription("Define the phenomenon identifier for height/depth values. Multiple values as comma separated list.").
            setOptional(true).
            setOrder(ORDER_10)
            
            );

    @Override
    public Set<SettingDefinition<?, ?>> getSettingDefinitions() {
        return Collections.unmodifiableSet(DEFINITIONS);
    }
    
    public static ChoiceSettingDefinition getCiRoleCodeChoiceSettingDefinition() {
        return new ChoiceSettingDefinition().
        addOption(CiRoleCodes.CI_RoleCode_author.name(), CiRoleCodes.CI_RoleCode_author.getIdentifier()).
        addOption(CiRoleCodes.CI_RoleCode_custodian.name(), CiRoleCodes.CI_RoleCode_custodian.getIdentifier()).
        addOption(CiRoleCodes.CI_RoleCode_distributor.name(), CiRoleCodes.CI_RoleCode_distributor.getIdentifier()).
        addOption(CiRoleCodes.CI_RoleCode_originator.name(), CiRoleCodes.CI_RoleCode_originator.getIdentifier()).
        addOption(CiRoleCodes.CI_RoleCode_owner.name(), CiRoleCodes.CI_RoleCode_owner.getIdentifier()).
        addOption(CiRoleCodes.CI_RoleCode_pointOfContact.name(), CiRoleCodes.CI_RoleCode_pointOfContact.getIdentifier()).
        addOption(CiRoleCodes.CI_RoleCode_principalInvestigator.name(), CiRoleCodes.CI_RoleCode_principalInvestigator.getIdentifier()).
        addOption(CiRoleCodes.CI_RoleCode_processor.name(), CiRoleCodes.CI_RoleCode_processor.getIdentifier()).
        addOption(CiRoleCodes.CI_RoleCode_publisher.name(), CiRoleCodes.CI_RoleCode_publisher.getIdentifier()).
        addOption(CiRoleCodes.CI_RoleCode_resourceProvider.name(), CiRoleCodes.CI_RoleCode_resourceProvider.getIdentifier()).
        addOption(CiRoleCodes.CI_RoleCode_user.name(), CiRoleCodes.CI_RoleCode_user.getIdentifier());
    }
}
