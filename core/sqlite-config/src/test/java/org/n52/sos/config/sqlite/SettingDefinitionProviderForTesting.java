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
package org.n52.sos.config.sqlite;

import java.util.Set;

import org.n52.sos.config.SettingDefinition;
import org.n52.sos.config.SettingDefinitionProvider;
import org.n52.sos.config.settings.BooleanSettingDefinition;
import org.n52.sos.config.settings.ChoiceSettingDefinition;
import org.n52.sos.config.settings.FileSettingDefinition;
import org.n52.sos.config.settings.IntegerSettingDefinition;
import org.n52.sos.config.settings.MultilingualStringSettingDefinition;
import org.n52.sos.config.settings.NumericSettingDefinition;
import org.n52.sos.config.settings.StringSettingDefinition;
import org.n52.sos.config.settings.UriSettingDefinition;

import com.google.common.collect.Sets;

/**
 * TODO JavaDoc
 *
 * @author Christian Autermann <c.autermann@52north.org>
 */
public class SettingDefinitionProviderForTesting implements
        SettingDefinitionProvider {

    public static final String URI_SETTING = "uri_setting";
    public static final String DOUBLE_SETTING = "double_setting";
    public static final String INTEGER_SETTING = "integer_setting";
    public static final String FILE_SETTING = "file_setting";
    public static final String STRING_SETTING = "string_setting";
    public static final String BOOLEAN_SETTING = "boolean_setting";
    public static final String CHOICE_SETTING = "choice_setting";
    public static final String LOCALIZED_STRING_SETTING
            = "localized_string_setting";

    @Override
    public Set<SettingDefinition<?, ?>> getSettingDefinitions() {
        return Sets.<SettingDefinition<?, ?>>newHashSet(
                new BooleanSettingDefinition().setKey(BOOLEAN_SETTING),
                new NumericSettingDefinition().setKey(DOUBLE_SETTING),
                new IntegerSettingDefinition().setKey(INTEGER_SETTING),
                new UriSettingDefinition().setKey(URI_SETTING),
                new FileSettingDefinition().setKey(FILE_SETTING),
                new StringSettingDefinition().setKey(STRING_SETTING),
                new ChoiceSettingDefinition().setKey(CHOICE_SETTING),
                new MultilingualStringSettingDefinition().setKey(LOCALIZED_STRING_SETTING));
    }
}
