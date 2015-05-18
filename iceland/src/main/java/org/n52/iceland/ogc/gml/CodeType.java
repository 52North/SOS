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
package org.n52.iceland.ogc.gml;

import org.n52.iceland.util.StringHelper;

import com.google.common.base.MoreObjects;
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
        return MoreObjects.toStringHelper(this)
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
