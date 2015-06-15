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
package org.n52.sos.ogc.om;

import org.n52.sos.ogc.gml.ReferenceType;
import org.n52.sos.ogc.om.values.Value;

/**
 * Class representing a O&M conform NamedValue
 * 
 * @since 4.0.0
 * 
 * @param <T>
 *            value type
 */
public class NamedValue<T> {

    /**
     * Value name
     */
    private ReferenceType name;

    /**
     * Value
     */
    private Value<T> value;

    /**
     * Get value name
     * 
     * @return Value name
     */
    public ReferenceType getName() {
        return name;
    }

    /**
     * Set value name
     * 
     * @param name
     *            Value name to set
     */
    public void setName(ReferenceType name) {
        this.name = name;
    }

    /**
     * Get value
     * 
     * @return Value
     */
    public Value<T> getValue() {
        return value;
    }

    /**
     * Set value
     * 
     * @param value
     *            Value to set
     */
    public void setValue(Value<T> value) {
        this.value = value;
    }

    /**
     * Check whether value name is set
     * 
     * @return <code>true</code>, if value name is set
     */
    public boolean isSetName() {
        return name != null && name.isSetHref();
    }

    /**
     * Check whether value is set
     * 
     * @return <code>true</code>, if value is set
     */
    public boolean isSetValue() {
        return value != null && value.isSetValue();
    }
}
