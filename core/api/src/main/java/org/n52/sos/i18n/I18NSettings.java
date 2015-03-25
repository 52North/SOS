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
package org.n52.sos.i18n;

import java.util.Collections;
import java.util.Set;

import org.n52.sos.config.SettingDefinition;
import org.n52.sos.config.SettingDefinitionGroup;
import org.n52.sos.config.SettingDefinitionProvider;
import org.n52.sos.config.settings.BooleanSettingDefinition;
import org.n52.sos.config.settings.ChoiceSettingDefinition;
import org.n52.sos.config.settings.StringSettingDefinition;

import com.google.common.collect.ImmutableSet;

/**
 * SettingDefinitionProvider for I18N
 *
 * @author Carsten Hollmann <c.hollmann@52north.org>
 * @since 4.1.0
 *
 */
public class I18NSettings implements SettingDefinitionProvider {

    public static final String I18N_DEFAULT_LANGUAGE = "i18n.defaultLanguage";

    public static final String I18N_SHOW_ALL_LANGUAGE_VALUES
            = "i18n.showAllLanguageValues";

    public static final SettingDefinitionGroup GROUP
            = new SettingDefinitionGroup().setTitle("I18N")
            .setOrder(ORDER_10).setShwoInDefaultSettings(false);

    public static final StringSettingDefinition I18N_DEFAULT_LANGUAGE_DEFINITION
            = new StringSettingDefinition()
            .setGroup(GROUP).setOrder(ORDER_1).setKey(I18N_DEFAULT_LANGUAGE)
            .setDefaultValue("eng")
            .setTitle("I18N default language")
            .setDescription("Set the I18N default language for this service");

    public static final BooleanSettingDefinition SHOW_ALL_LANGUAGE_VLAUES
            = new BooleanSettingDefinition()
            .setGroup(GROUP)
            .setOrder(ORDER_2)
            .setKey(I18N_SHOW_ALL_LANGUAGE_VALUES)
            .setDefaultValue(false)
            .setTitle("I18N show all language values")
            .setDescription(
                    "Show all language specific values if no language is queried or the queried language is not supported!");

    private static final Set<SettingDefinition<?, ?>> DEFINITIONS = ImmutableSet
            .<SettingDefinition<?, ?>>of(
                    I18N_DEFAULT_LANGUAGE_DEFINITION, SHOW_ALL_LANGUAGE_VLAUES);

    @Override
    public Set<SettingDefinition<?, ?>> getSettingDefinitions() {
        return Collections.unmodifiableSet(DEFINITIONS);
    }
}
