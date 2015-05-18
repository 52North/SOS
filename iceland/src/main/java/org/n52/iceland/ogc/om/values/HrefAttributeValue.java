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
import org.n52.iceland.w3c.xlink.W3CHrefAttribute;

public class HrefAttributeValue implements Value<W3CHrefAttribute> {

    private static final long serialVersionUID = 1762674768718660098L;

    private W3CHrefAttribute value;

    /**
     * Unit of measure
     */
    private String unit;

    public HrefAttributeValue() {
    }

    public HrefAttributeValue(W3CHrefAttribute value) {
        setValue(value);
    }

    @Override
    public void setValue(W3CHrefAttribute value) {
       this.value = value;
    }

    @Override
    public W3CHrefAttribute getValue() {
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
    public boolean isSetValue() {
        return getValue() != null && getValue().isSetHref();
    }


    @Override
    public boolean isSetUnit() {
        return StringHelper.isNotEmpty(getUnit());
    }
    
    @Override
    public String toString() {
        return String.format("HrefAttributeValue [value=%s, unit=%s]", getValue(), getUnit());
    }

}
