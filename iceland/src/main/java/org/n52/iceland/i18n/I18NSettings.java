/**
 * Copyright 2015 52°North Initiative for Geospatial Open Source
 * Software GmbH
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.n52.iceland.i18n;

import java.util.Collections;
import java.util.Set;

import org.n52.iceland.config.SettingDefinition;
import org.n52.iceland.config.SettingDefinitionGroup;
import org.n52.iceland.config.SettingDefinitionProvider;
import org.n52.iceland.config.settings.BooleanSettingDefinition;
import org.n52.iceland.config.settings.StringSettingDefinition;

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
