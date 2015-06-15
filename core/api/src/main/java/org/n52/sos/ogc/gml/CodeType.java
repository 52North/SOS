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
package org.n52.sos.ogc.gml;

import org.n52.sos.util.Constants;
import org.n52.sos.util.StringHelper;

import com.google.common.base.Objects;

/**
 * Class represents a GML conform CodeType element
 *
 * @since 4.0.0
 *
 */
public class CodeType {

    /**
     * Value/identifier
     */
    private String value;

    /**
     * Code space
     */
    private String codeSpace;

    /**
     * constructor
     *
     * @param value
     *            Value/identifier
     */
    public CodeType(final String value) {
        this.value = value;
    }

    public CodeType(final String value, final String codespace) {
        setValue(value);
        setCodeSpace(codespace);
    }

    /**
     * Get value
     *
     * @return Value to set
     */
    public String getValue() {
        return value;
    }

    /**
     * Get code space
     *
     * @return Code space
     */
    public String getCodeSpace() {
        return codeSpace;
    }

    /**
     * Set value and return this CodeType object
     *
     * @param value
     *            Value to set
     * @return This CodeType object
     */
    public CodeType setValue(final String value) {
        this.value = value;
        return this;
    }

    /**
     * Set code space and return this CodeType object
     *
     * @param codeSpace
     *            Code space to set
     * @return This CodeType object
     */
    public CodeType setCodeSpace(final String codeSpace) {
        this.codeSpace = codeSpace;
        return this;
    }

    /**
     * Check whether value is set
     *
     * @return <code>true</code>, if value is set
     */
    public boolean isSetValue() {
        return StringHelper.isNotEmpty(getValue());
    }

    /**
     * Check whether code space is set
     *
     * @return <code>true</code>, if code space is set
     */
    public boolean isSetCodeSpace() {
        return StringHelper.isNotEmpty(getCodeSpace());
    }

    @Override
    public String toString() {
        return Objects.toStringHelper(this)
                .add("value", getValue())
                .add("codeSpace", getCodeSpace())
                .toString();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof CodeType) {
            CodeType that = (CodeType) obj;
            return Objects.equal(getValue(), that.getValue()) &&
                   Objects.equal(getCodeSpace(), that.getCodeSpace());
        }
        return false;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getValue(), getCodeSpace());
    }

}
