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
package org.n52.iceland.ogc.ows;

import org.n52.iceland.util.StringHelper;

/**
 * Class represents an OWS DomaintType element
 * 
 * @author Carsten Hollmann <c.hollmann@52north.org>
 * 
 * @since 4.0.0
 * 
 */
public class OwsDomainType {

    private String name;

    private OwsPossibleValues value;

    private String defaultValue;

    /**
     * constructor
     * 
     * @param name
     * @param possibleValues
     */
    public OwsDomainType(String name, OwsPossibleValues possibleValues) {
        setName(name);
        setValue(possibleValues);
    }

    /**
     * constructor
     * 
     * @param name
     * @param possibleValues
     * @param defaultValue
     */
    public OwsDomainType(String name, OwsPossibleValues possibleValues, String defaultValue) {
        setName(name);
        setValue(possibleValues);
        setDefaultValue(defaultValue);
    }

    /**
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * @param name
     *            the name to set
     */
    private void setName(String name) {
        this.name = name;
    }

    /**
     * @return the value
     */
    public OwsPossibleValues getValue() {
        return value;
    }

    /**
     * @param value
     *            the value to set
     */
    private void setValue(OwsPossibleValues value) {
        this.value = value;
    }

    /**
     * @return the defaultValue
     */
    public String getDefaultValue() {
        return defaultValue;
    }

    /**
     * @param defaultValue
     *            the defaultValue to set
     */
    public void setDefaultValue(String defaultValue) {
        this.defaultValue = defaultValue;
    }

    /**
     * @return
     */
    public boolean isSetDefaultValue() {
        return StringHelper.isNotEmpty(getDefaultValue());
    }
}
