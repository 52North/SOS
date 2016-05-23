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

/**
 * 
 * Interface for setting definitions that can be used within the Service.
 * Defined settings will be presented in the administrator and installer view.
 * <p/>
 * 
 * @see SettingDefinitionProvider
 * @see SettingDefinitionGroup
 * @see SettingsManager
 * @see org.n52.sos.config.settings.FileSettingDefinition
 * @see org.n52.sos.config.settings.BooleanSettingDefinition
 * @see org.n52.sos.config.settings.IntegerSettingDefinition
 * @see org.n52.sos.config.settings.NumericSettingDefinition
 * @see org.n52.sos.config.settings.StringSettingDefinition
 * @see org.n52.sos.config.settings.UriSettingDefinition <p/>
 * @param <S>
 *            The type of the implementing class
 * @param <T>
 *            The type of the value
 *            <p/>
 * @author Christian Autermann <c.autermann@52north.org>
 * @since 4.0.0
 */
public interface SettingDefinition<S extends SettingDefinition<S, T>, T> extends Ordered<S>, Cloneable {
    /**
     * @return the unique key of this definition
     */
    String getKey();

    /**
     * @return the title of this definition
     */
    String getTitle();

    /**
     * @return the description of this definition
     */
    String getDescription();

    /**
     * @return wether this setting is optional or required.
     */
    boolean isOptional();

    /**
     * @return the default value (or null if there is none)
     */
    T getDefaultValue();

    /**
     * @return the group of this definition
     */
    SettingDefinitionGroup getGroup();

    /**
     * @return if this definition has a non empty title
     */
    boolean hasTitle();

    /**
     * @return if this definition has a non empty description
     */
    boolean hasDescription();

    /**
     * @return if this definition has a default value
     */
    boolean hasDefaultValue();

    /**
     * @return if this definition has a group
     */
    boolean hasGroup();

    /**
     * Sets the unique identifier of this setting definition, which can be
     * referenced by configurable classes.
     * 
     * @param key
     *            the <b>unique</b> key
     * 
     * @return this (for method chaining)
     */
    S setKey(String key);

    /**
     * Sets the title of this setting definition, which will be presented to the
     * user.
     * 
     * @param title
     *            the title
     * 
     * @return this (for method chaining)
     */
    S setTitle(String title);

    /**
     * Sets the description of this setting definition, which should further
     * describe the purpose of this setting. Can contain XHTML markup.
     * 
     * @param description
     *            the description
     * 
     * @return this (for method chaining)
     */
    S setDescription(String description);

    /**
     * Sets whether this setting is optional or can be null. By default all
     * settings are required.
     * 
     * @param optional
     *            if this setting is optional
     * 
     * @return this (for method chaining)
     */
    S setOptional(boolean optional);

    /**
     * Sets the default value of this setting. All required settings should have
     * a default setting to allow a smoother integration of new settings in old
     * configurations.
     * 
     * @param defaultValue
     *            the default value
     * 
     * @return this (for method chaining)
     */
    S setDefaultValue(T defaultValue);

    /**
     * Sets the group of this definition. If no group is set, the setting will
     * be moved to a default group.
     * 
     * @param group
     *            the group
     * 
     * @return this (for method chaining)
     */
    S setGroup(SettingDefinitionGroup group);

    /**
     * @return the type of the value of this definition
     */
    SettingType getType();
}
