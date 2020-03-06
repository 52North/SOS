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
package org.n52.sos.uvf;

import java.util.Set;

import org.n52.schetland.uvf.UVFConstants;
import org.n52.sos.config.SettingDefinition;
import org.n52.sos.config.SettingDefinitionGroup;
import org.n52.sos.config.SettingDefinitionProvider;
import org.n52.sos.config.settings.ChoiceSettingDefinition;
import org.n52.sos.config.settings.StringSettingDefinition;

import com.google.common.collect.ImmutableSet;

public class UVFSettings implements SettingDefinitionProvider {
    
    
    public static final String DEFAULT_CRS_SETTING_KEY = "uvf.default.crs";
    public static final String UVF_TIME_ZONE_SETTING_KEY = "uvf.timeZone";
    public static final String UVF_LINE_ENDING_KEY = "uvf.lineEnding";
    
    public static final SettingDefinitionGroup GROUP = 
            new SettingDefinitionGroup()
                            .setTitle("UVF Encoding")
                        .setOrder(ORDER_10);

    private static final StringSettingDefinition DEFAULT_CRS_SETTING_DEFINITION = 
            new StringSettingDefinition()
            .setGroup(GROUP)
            .setOrder(1)
            .setKey(DEFAULT_CRS_SETTING_KEY)
            .setDefaultValue("31466")
            .setTitle("The default CRS EPSG code used in UVF response")
            .setDescription(String.format("The default CRS EPSG code that is used if no swe extension is present in "
                    + "the request that specifies one. Allowed values are: <tt>%s</tt>.", UVFConstants.ALLOWED_CRS));

    private static final StringSettingDefinition UVF_TIME_ZONE__SETTING_DEFINITION = 
            new StringSettingDefinition()
            .setGroup(GROUP)
            .setOrder(2)
            .setKey(UVF_TIME_ZONE_SETTING_KEY)
            .setDefaultValue("CET")
            .setTitle("Returned Time zone for the UVF encoding")
            .setDescription("Define the time zone in which the time should be encoded in the UVF responce"
                    + "Valid values are see <a href=\"http://docs.oracle.com/javase/8/docs/api/java/util/TimeZone.html\" target=\"_blank\">Java TimeZone</a>."
                    + " Default is CET.");
    
    public static final ChoiceSettingDefinition UVF_LINE_ENDING_DEFINITION =
            new ChoiceSettingDefinition()
            .setGroup(GROUP)
            .setOrder(3)
            .setKey(UVF_LINE_ENDING_KEY)
            .setDefaultValue(UVFConstants.LineEnding.Unix.name())
            .setTitle("The line ending that should be used for UVF")
            .addOption(UVFConstants.LineEnding.Windows.name(), UVFConstants.LineEnding.Windows.name())
            .addOption(UVFConstants.LineEnding.Unix.name(), UVFConstants.LineEnding.Unix.name())
            .addOption(UVFConstants.LineEnding.Mac.name(), UVFConstants.LineEnding.Mac.name());
    
    @Override
    public Set<SettingDefinition<?, ?>> getSettingDefinitions() {
       return ImmutableSet.<SettingDefinition<?,?>>of(DEFAULT_CRS_SETTING_DEFINITION, UVF_TIME_ZONE__SETTING_DEFINITION, UVF_LINE_ENDING_DEFINITION);
    }

}
