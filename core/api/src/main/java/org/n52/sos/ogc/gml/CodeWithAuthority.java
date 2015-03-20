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

/**
 * Class represents a GML conform CodeWithAuthority element
 * 
 * @since 4.0.0
 * 
 */
import java.io.Serializable;

import org.n52.sos.util.Constants;
import org.n52.sos.util.StringHelper;

import com.google.common.base.Objects;

public class CodeWithAuthority implements Comparable<CodeWithAuthority>, Serializable {

    private static final long serialVersionUID = 9001214766142377426L;

    /**
     * value/identifier
     */
    private String value;

    /**
     * code space
     */
    private String codeSpace = Constants.EMPTY_STRING;

    /**
     * constructor
     * 
     * @param value
     *            Value/identifier
     */
    public CodeWithAuthority(String value) {
        this.value = value;
    }

    /**
     * constructor
     * 
     * @param value
     *            Value/identifier
     * @param codeSpace
     *            Code space
     */
    public CodeWithAuthority(String value, String codeSpace) {
        this.value = value;
        this.codeSpace = codeSpace;
    }

    /**
     * Get value
     * 
     * @return Value
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
     * Set value and return this CodeWithAuthority object
     * 
     * @param value
     *            Value to set
     * @return This CodeWithAuthority object
     */
    public CodeWithAuthority setValue(String value) {
        this.value = value;
        return this;
    }

    /**
     * Set code space and return this CodeWithAuthority object
     * 
     * @param codeSpace
     *            Code space to set
     * @return This CodeWithAuthority object
     */
    public CodeWithAuthority setCodeSpace(String codeSpace) {
        this.codeSpace = codeSpace;
        return this;
    }

    /**
     * Check whether value is set
     * 
     * @return <code>true</code> if value is set
     */
    public boolean isSetValue() {
        return StringHelper.isNotEmpty(getValue());
    }

    /**
     * Check whether code space is set
     * 
     * @return <code>true</code> if code space is set
     */
    public boolean isSetCodeSpace() {
        return StringHelper.isNotEmpty(getCodeSpace());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getCodeSpace(), getValue());
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (!(obj instanceof CodeWithAuthority)) {
            return false;
        }
        CodeWithAuthority other = (CodeWithAuthority) obj;
        if (getCodeSpace() == null) {
            if (other.getCodeSpace() != null) {
                return false;
            }
        } else if (!getCodeSpace().equals(other.getCodeSpace())) {
            return false;
        }
        if (getValue() == null) {
            if (other.getValue() != null) {
                return false;
            }
        } else if (!getValue().equals(other.getValue())) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return String.format("CodeWithAuthority [value=%s, codeSpace=%s]", getValue(), getCodeSpace());
    }

    @Override
    public int compareTo(CodeWithAuthority o) {
        if (isSetValue() && o.isSetValue()) {
            if (isSetCodeSpace() && o.isSetCodeSpace()) {
                if (getValue().equals(o.getValue()) && getCodeSpace().equals(o.getCodeSpace())) {
                    return 0;
                }
            }
            if (getValue().equals(o.getValue())) {
                return 0;
            }
        }
        return 1;
    }
}
