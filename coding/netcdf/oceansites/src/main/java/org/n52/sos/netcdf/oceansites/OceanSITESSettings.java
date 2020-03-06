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
package org.n52.sos.netcdf.oceansites;

import java.util.Collections;
import java.util.Set;

import org.n52.sos.config.SettingDefinition;
import org.n52.sos.config.SettingDefinitionGroup;
import org.n52.sos.config.SettingDefinitionProvider;
import org.n52.sos.config.settings.ChoiceSettingDefinition;
import org.n52.sos.config.settings.StringSettingDefinition;
import org.n52.sos.iso.CodeList.CiRoleCodes;
import org.n52.sos.netcdf.NetcdfSettings;

import com.google.common.collect.ImmutableSet;

/**
 * Implementation of {@link SettingDefinitionProvider} for OceanSITES netCDF
 * encoding settings.
 * 
 * @author <a href="mailto:c.hollmann@52north.org">Carsten Hollmann</a>
 * @since 4.4.0
 *
 */
public class OceanSITESSettings implements SettingDefinitionProvider {

    public static final String OCEANSITES_SITE_DEFINITON = "oceansites.definition.site";

    public static final String OCEANSITES_PLATFORM_DEFINITION = "oceansites.definition.platform";

    public static final String OCEANSITES_DATA_MODE = "oceansites.dataMode";

    public static final String OCEANSITES_DATA_MODE_DEFINITION = "oceansites.definition.dataMode";

    public static final String OCEANSITES_LICENSE = "oceansites.license";

    public static final String OCEANSITES_CITATION = "oceansites.citation";

    public static final String OCEANSITES_ACKNOWLEDGEMENT = "oceansites.acknowledgement";

    public static final String OCEANSITES_PROJECT = "oceansites.project";

    public static final String OCEANSITES_PROJECT_DEFINITION = "oceansites.definition.project";

    public static final String OCEANSITES_WMO_PLATFORM_CODE_DEFINITION = "oceansites.definition.wmo.platformCode";

    public static final String OCEANSITES_ARRAY_DEFINITION = "oceansites.definition.array";

    public static final String OCEANSITES_NETWORK_DEFINITION = "oceansites.definition.network";

    public static final String OCEANSITES_FORMAT_VERSION = "oceansites.formatVersion";

    public static final String OCEANSITES_PRINCIPAL_INVESTIGATOR = "oceansites.principalInvestigator";

    public static final String OCEANSITES_REFERENCES = "oceansites.references";
    
    public static final String OCEANSITES_AREA_DEFINITION = "oceansites.area";

    private static final SettingDefinitionGroup SETTINGS_GROUP = new SettingDefinitionGroup()
            .setTitle("OceanSITES netCDF").setDescription("Define OceanSITES specific parameter").setOrder(10.22f);

    private static final Set<SettingDefinition<?, ?>> DEFINITIONS =
            ImmutableSet
                    .<SettingDefinition<?, ?>> of(
                            new StringSettingDefinition()
                                    .setGroup(SETTINGS_GROUP)
                                    .setKey(OCEANSITES_LICENSE)
                                    .setTitle("OceanSITES license")
                                    .setDefaultValue(OceanSITESConstants.LICENSE_DEFAULT_TEXT)
                                    .setDescription(
                                            "A statement describing the data distribution policy; "
                                                    + "it may be a project- or DAC-specific statement, but must allow "
                                                    + "free use of data. OceanSITES has adopted the CLIVAR data policy, which "
                                                    + "explicitly calls for free and unrestricted data exchange. Details at:"
                                                    + "<a href=\"http://www.clivar.org/data/data_policy.php\" target=\"_blank\"/>")
                                    .setOptional(true).setOrder(ORDER_0),

                            new StringSettingDefinition()
                                    .setGroup(SETTINGS_GROUP)
                                    .setKey(OCEANSITES_CITATION)
                                    .setTitle("OceanSITES citation")
                                    .setDefaultValue(OceanSITESConstants.CITATION_DEFAULT_TEXT)
                                    .setDescription(
                                            "The citation to be used in publications using the dataset; "
                                                    + "should include a reference to OceanSITES but may contain any other "
                                                    + "text deemed appropriate by the PI and DAC.").setOptional(true)
                                    .setOrder(ORDER_1),

                            new StringSettingDefinition()
                                    .setGroup(SETTINGS_GROUP)
                                    .setKey(OCEANSITES_ACKNOWLEDGEMENT)
                                    .setTitle("OceanSITES acknowledgement")
                                    .setDefaultValue(OceanSITESConstants.ACKNOWLEDGEMENT_DEFAULT)
                                    .setDescription(
                                            "A place to acknowledge various types of support for the project that produced this data.")
                                    .setOptional(true).setOrder(ORDER_2),

                            new StringSettingDefinition().setGroup(SETTINGS_GROUP).setKey(OCEANSITES_PROJECT)
                                    .setTitle("OceanSITES project")
                                    .setDefaultValue(OceanSITESConstants.PROJECT_DEFAULT)
                                    .setDescription("The scientific project that produced the data.")
                                    .setOptional(true).setOrder(ORDER_3),

                            new StringSettingDefinition().setGroup(SETTINGS_GROUP)
                                    .setKey(OCEANSITES_PROJECT_DEFINITION).setTitle("OceanSITES project definition")
                                    .setDefaultValue(OceanSITESConstants.PROJECT_DEFINITION)
                                    .setDescription("The project SensorML identifier definition").setOptional(true)
                                    .setOrder(ORDER_4),

                            new StringSettingDefinition().setGroup(SETTINGS_GROUP).setKey(OCEANSITES_ARRAY_DEFINITION)
                                    .setTitle("OceanSITES array definition")
                                    .setDefaultValue(OceanSITESConstants.ARRAY_DEFINITION)
                                    .setDescription("The array SensorML identifier definition").setOptional(true)
                                    .setOrder(ORDER_5),

                            new StringSettingDefinition().setGroup(SETTINGS_GROUP)
                                    .setKey(OCEANSITES_NETWORK_DEFINITION).setTitle("OceanSITES network definition")
                                    .setDefaultValue(OceanSITESConstants.NETWORK_DEFINITION)
                                    .setDescription("The network definition from sensor desription (SensorML identification)").setOptional(true)
                                    .setOrder(ORDER_6),

                            new StringSettingDefinition().setGroup(SETTINGS_GROUP)
                                    .setKey(OCEANSITES_WMO_PLATFORM_CODE_DEFINITION)
                                    .setTitle("OceanSITES WMO platform code definition")
                                    .setDefaultValue(OceanSITESConstants.WMO_PLATFORM_CODE_DEFINITION)
                                    .setDescription("The WMO platform code SensorML identifier definition.").setOptional(true)
                                    .setOrder(ORDER_7),

                            new StringSettingDefinition().setGroup(SETTINGS_GROUP).setKey(OCEANSITES_SITE_DEFINITON)
                                    .setTitle("OceanSITES site definition")
                                    .setDefaultValue(OceanSITESConstants.SITE_CODE_DEFINITION)
                                    .setDescription("The site code SensorML identifier definition.").setOptional(false)
                                    .setOrder(ORDER_8),

                            new StringSettingDefinition().setGroup(SETTINGS_GROUP)
                                    .setKey(OCEANSITES_PLATFORM_DEFINITION)
                                    .setTitle("OceanSITES platform code definition")
                                    .setDefaultValue(OceanSITESConstants.PLATFORM_CODE_DEFINITION)
                                    .setDescription("The platform code SensorML identifier definition.").setOptional(false)
                                    .setOrder(ORDER_9),

                            new StringSettingDefinition().setGroup(SETTINGS_GROUP)
                                    .setKey(OCEANSITES_DATA_MODE_DEFINITION)
                                    .setTitle("OceanSITES data mode definition")
                                    .setDefaultValue(OceanSITESConstants.DATA_MODE_DEFINITION)
                                    .setDescription("The data mode SensorML classifier definition.").setOptional(true)
                                    .setOrder(ORDER_10),

                            new ChoiceSettingDefinition().addOption(OceanSITESConstants.DataMode.D.name())
                                    .addOption(OceanSITESConstants.DataMode.M.name())
                                    .addOption(OceanSITESConstants.DataMode.P.name())
                                    .addOption(OceanSITESConstants.DataMode.R.name()).setGroup(SETTINGS_GROUP)
                                    .setKey(OCEANSITES_DATA_MODE).setTitle("OceanSITES data mode")
                                    .setDefaultValue(OceanSITESConstants.DataMode.R.toString())
                                    .setDescription("The data mode.").setOptional(true).setOrder(ORDER_11),

                            new StringSettingDefinition().setGroup(SETTINGS_GROUP).setKey(OCEANSITES_FORMAT_VERSION)
                                    .setTitle("OceanSITES data mode definition")
                                    .setDefaultValue(OceanSITESConstants.FORMAT_VERSION_DEFAULT_TEXT)
                                    .setDescription("The data mode SensorML identifier definition.").setOptional(false)
                                    .setOrder(ORDER_12),

                            NetcdfSettings.getCiRoleCodeChoiceSettingDefinition().setGroup(SETTINGS_GROUP)
                                    .setKey(OCEANSITES_PRINCIPAL_INVESTIGATOR)
                                    .setTitle("Set the principalInvestigator CI_RoleCode definition")
                                    .setDefaultValue(CiRoleCodes.CI_RoleCode_principalInvestigator.name())
                                    .setDescription("Define the principalInvestigator CI_RoleCode definition.")
                                    .setOptional(false).setOrder(ORDER_13),

                            new StringSettingDefinition()
                                    .setGroup(SETTINGS_GROUP)
                                    .setKey(OCEANSITES_REFERENCES)
                                    .setTitle("OceanSITES references")
                                    .setDefaultValue(OceanSITESConstants.REFERENCES_DEFAULT_TEXT)
                                    .setDescription(
                                            "Published or web-based references that describe the data or "
                                                    + "methods used to produce it. Include a reference to OceanSITES "
                                                    + "and a project-specific reference if appropriate.")
                                    .setOptional(true).setOrder(ORDER_14),
                                    
                             new StringSettingDefinition().setGroup(SETTINGS_GROUP).setKey(OCEANSITES_AREA_DEFINITION)
                                    .setTitle("OceanSITES data mode definition")
                                    .setDefaultValue(OceanSITESConstants.AREA_DEFINITION)
                                    .setDescription("The area SensorML identifier definition.").setOptional(true)
                                    .setOrder(ORDER_15)

                    );

    @Override
    public Set<SettingDefinition<?, ?>> getSettingDefinitions() {
        return Collections.unmodifiableSet(DEFINITIONS);
    }

}
