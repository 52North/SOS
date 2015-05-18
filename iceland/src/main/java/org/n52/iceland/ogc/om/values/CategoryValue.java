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
package org.n52.iceland.ogc.om.values;

import org.n52.iceland.util.StringHelper;

/**
 * Category measurement representation for observation
 * 
 * @since 4.0.0
 * 
 */
public class CategoryValue implements Value<String> {
    /**
     * serial number
     */
    private static final long serialVersionUID = 8364802854103009470L;

    /**
     * Measurement value
     */
    private String value;

    /**
     * Unit of measure
     */
    private String unit;

    /**
     * constructor
     * 
     * @param value
     *            Measurement value
     */
    public CategoryValue(String value) {
        this(value, null);
    }

    /**
     * * constructor
     * 
     * @param value
     *            Measurement value
     * @param unit
     *            Unit of measure
     */
    public CategoryValue(String value, String unit) {
        this.value = value;
        this.unit = unit;
    }

    @Override
    public void setValue(String value) {
        this.value = value;
    }

    @Override
    public String getValue() {
        return value;
    }

    @Override
    public void setUnit(String unit) {
        this.unit = unit;
    }

    @Override
    public String getUnit() {
        return unit;
    }

    @Override
    public String toString() {
        return String.format("CategoryValue [value=%s, unit=%s]", getValue(), getUnit());
    }

    @Override
    public boolean isSetValue() {
        return StringHelper.isNotEmpty(getValue());
    }

    @Override
    public boolean isSetUnit() {
        return StringHelper.isNotEmpty(getUnit());
    }

}
