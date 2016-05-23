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
package org.n52.sos.config;

import java.io.File;
import java.net.URI;

import org.n52.sos.config.settings.BooleanSettingDefinition;
import org.n52.sos.config.settings.ChoiceSettingDefinition;
import org.n52.sos.config.settings.FileSettingDefinition;
import org.n52.sos.config.settings.IntegerSettingDefinition;
import org.n52.sos.config.settings.MultilingualStringSettingDefinition;
import org.n52.sos.config.settings.NumericSettingDefinition;
import org.n52.sos.config.settings.StringSettingDefinition;
import org.n52.sos.config.settings.TimeInstantSettingDefinition;
import org.n52.sos.config.settings.UriSettingDefinition;
import org.n52.sos.ogc.gml.time.TimeInstant;
import org.n52.sos.i18n.MultilingualString;

/**
 * Factory to construct implementation specific {@link SettingValue}s.
 * <p/>
 *
 * @author Christian Autermann <c.autermann@52north.org>
 * @since 4.0.0
 */
public interface SettingValueFactory {

    /**
     * Constructs a new {@code Boolean} setting value from the supplied
     * definition and string value.
     * <p/>
     *
     * @param setting
     *            the setting definition
     * @param stringValue
     *            the value as string
     *            <p/>
     * @return the implementation specific {@code SettingValue}
     */
    SettingValue<Boolean> newBooleanSettingValue(BooleanSettingDefinition setting, String stringValue);

    /**
     * Constructs a new {@code Integer} setting value from the supplied
     * definition and string value.
     * <p/>
     *
     * @param setting
     *            the setting definition
     * @param stringValue
     *            the value as string
     *            <p/>
     * @return the implementation specific {@code SettingValue}
     */
    SettingValue<Integer> newIntegerSettingValue(IntegerSettingDefinition setting, String stringValue);

    /**
     * Constructs a new {@code String} setting value from the supplied
     * definition and string value.
     * <p/>
     *
     * @param setting
     *            the setting definition
     * @param stringValue
     *            the value as string
     *            <p/>
     * @return the implementation specific {@code SettingValue}
     */
    SettingValue<String> newStringSettingValue(StringSettingDefinition setting, String stringValue);

    /**
     * Constructs a new {@code File} setting value from the supplied definition
     * and string value.
     * <p/>
     *
     * @param setting
     *            the setting definition
     * @param stringValue
     *            the value as string
     *            <p/>
     * @return the implementation specific {@code SettingValue}
     */
    SettingValue<File> newFileSettingValue(FileSettingDefinition setting, String stringValue);

    /**
     * Constructs a new {@code URI} setting value from the supplied definition
     * and string value.
     * <p/>
     *
     * @param setting
     *            the setting definition
     * @param stringValue
     *            the value as string
     *            <p/>
     * @return the implementation specific {@code SettingValue}
     */
    SettingValue<URI> newUriSettingValue(UriSettingDefinition setting, String stringValue);

    /**
     * Constructs a new {@code Double} setting value from the supplied
     * definition and string value.
     * <p/>
     *
     * @param setting
     *            the setting definition
     * @param stringValue
     *            the value as string
     *            <p/>
     * @return the implementation specific {@code SettingValue}
     */
    SettingValue<Double> newNumericSettingValue(NumericSettingDefinition setting, String stringValue);

    /**
     * Constructs a new {@code TimeInstant} setting value from the supplied
     * definition and string value.
     * <p/>
     *
     * @param setting
     *            the setting definition
     * @param stringValue
     *            the value as string
     *            <p/>
     * @return the implementation specific {@code SettingValue}
     */
    SettingValue<TimeInstant> newTimeInstantSettingValue(TimeInstantSettingDefinition setting, String stringValue);

     /**
     * Constructs a new {@code MultilingualString} setting value from the supplied
     * definition and string value.
     * <p/>
     *
     * @param setting
     *            the setting definition
     * @param stringValue
     *            the value as string
     *            <p/>
     * @return the implementation specific {@code SettingValue}
     */
    SettingValue<MultilingualString> newMultiLingualStringValue(MultilingualStringSettingDefinition setting, String stringValue);

    /**
     * Constructs a new {@code String} setting value from the supplied
     * definition and string value.
     * <p/>
     *
     * @param setting
     *            the setting definition
     * @param stringValue
     *            the value as string
     *            <p/>
     * @return the implementation specific {@code SettingValue}
     */
    SettingValue<String> newChoiceSettingValue(ChoiceSettingDefinition setting, String stringValue);

    /**
     * Constructs a new generic setting value from the supplied definition and
     * string value.
     * <p/>
     *
     * @param setting
     *            the setting definition
     * @param stringValue
     *            the value as string
     *            <p/>
     * @return the implementation specific {@code SettingValue}
     */
    SettingValue<?> newSettingValue(SettingDefinition<?, ?> setting, String stringValue);
}
