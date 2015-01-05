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
package org.n52.sos.config.settings;

import org.n52.sos.config.SettingDefinition;
import org.n52.sos.config.SettingType;

/**
 * {@link SettingDefinition} for {@code Integer}s.
 * <p/>
 * 
 * @since 4.0.0
 * @author Christian Autermann <c.autermann@52north.org>
 */
public class IntegerSettingDefinition extends AbstractSettingDefinition<IntegerSettingDefinition, Integer> {

    private Integer minimum;

    private Integer maximum;

    private boolean exclusiveMaximum = false;

    private boolean exclusiveMinimum = false;

    /**
     * Constructs a new {@code IntegerSettingDefinition}.
     */
    public IntegerSettingDefinition() {
        super(SettingType.INTEGER);
    }

    /**
     * Get the value of minimum.
     * <p/>
     * 
     * @return the value of minimum
     */
    public Integer getMinimum() {
        return minimum;
    }

    /**
     * @return whether a minimum value is set
     */
    public boolean hasMinimum() {
        return getMinimum() != null;
    }

    /**
     * Set the value of minimum.
     * <p/>
     * 
     * @param minimum
     *            new value of minimum
     *            <p/>
     * @return this
     */
    public IntegerSettingDefinition setMinimum(Integer minimum) {
        this.minimum = minimum;
        return this;
    }

    /**
     * Get the value of maximum.
     * <p/>
     * 
     * @return the value of maximum
     */
    public Integer getMaximum() {
        return maximum;
    }

    /**
     * @return whether a maximum value is set
     */
    public boolean hasMaximum() {
        return getMaximum() != null;
    }

    /**
     * Set the value of maximum.
     * <p/>
     * 
     * @param maximum
     *            new value of maximum
     *            <p/>
     * @return this
     */
    public IntegerSettingDefinition setMaximum(Integer maximum) {
        this.maximum = maximum;
        return this;
    }

    /**
     * Get the value of exclusiveMaximum.
     * <p/>
     * 
     * @return the value of exclusiveMaximum
     */
    public boolean isExclusiveMaximum() {
        return exclusiveMaximum;
    }

    /**
     * Set the value of exclusiveMaximum.
     * <p/>
     * 
     * @param exclusiveMaximum
     *            new value of exclusiveMaximum
     *            <p/>
     * @return this
     */
    public IntegerSettingDefinition setExclusiveMaximum(boolean exclusiveMaximum) {
        this.exclusiveMaximum = exclusiveMaximum;
        return this;
    }

    /**
     * Get the value of exclusiveMinimum.
     * <p/>
     * 
     * @return the value of exclusiveMinimum
     */
    public boolean isExclusiveMinimum() {
        return exclusiveMinimum;
    }

    /**
     * Set the value of exclusiveMinimum.
     * <p/>
     * 
     * @param exclusiveMinimum
     *            new value of exclusiveMinimum
     *            <p/>
     * @return this
     */
    public IntegerSettingDefinition setExclusiveMinimum(boolean exclusiveMinimum) {
        this.exclusiveMinimum = exclusiveMinimum;
        return this;
    }
}
