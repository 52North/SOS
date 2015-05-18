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
package org.n52.iceland.ogc.swe.simpleType;

import static com.google.common.base.Preconditions.checkNotNull;

import org.n52.iceland.ogc.swe.SweConstants.SweDataComponentType;

/**
 * SOS internal representation of SWE simpleType text
 * 
 * @author Carsten Hollmann
 * @since 4.0.0
 */
public class SweText extends SweAbstractSimpleType<String> implements Comparable<SweText>, SweQuality {

    /**
     * value
     */
    private String value;

    /**
     * constructor
     */
    public SweText() {
    }

    @Override
    public String getValue() {
        return value;
    }

    @Override
    public SweText setValue(final String value) {
        this.value = value;
        return this;
    }

    @Override
    public String getStringValue() {
        return value;
    }

    @Override
    public boolean isSetValue() {
        return value != null && !value.isEmpty();
    }

    @Override
    public SweDataComponentType getDataComponentType() {
        return SweDataComponentType.Text;
    }

    @Override
    public int compareTo(SweText o) {
        return checkNotNull(o) == this ? 0
                : getValue() == o.getValue() ? 0
                    : getValue() == null ? -1
                       : o.getValue() == null ? 1
                          : getValue().compareTo(o.getValue());
    }
}
