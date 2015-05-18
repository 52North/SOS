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
package org.n52.iceland.ogc.om;

import org.n52.iceland.util.StringHelper;

/**
 * class represents a phenomenon of an observation
 * 
 * @since 4.0.0
 */
public class OmObservableProperty extends AbstractPhenomenon {
    /**
     * serial number
     */
    private static final long serialVersionUID = -1820718860701876580L;

    /** unit of the values of the phenomenons observations */
    private String unit;

    /** valueType in the database of the phenomenons observation values */
    private String valueType;

    /**
     * constructor
     * 
     * @param identifier
     *            observableProperty identifier
     */
    public OmObservableProperty(String identifier) {
        super(identifier);
    }

    /**
     * constructor
     * 
     * @param identifier
     *            id of the observableProperty
     * @param description
     *            description of the observableProperty
     * @param unit
     *            unit of the observation values according to this
     *            observableProperty
     * @param valueType
     *            database valType of the observation values according to this
     *            observableProperty
     */
    public OmObservableProperty(String identifier, String description, String unit, String valueType) {
        super(identifier, description);
        this.unit = unit;
        this.valueType = valueType;
    }

    /**
     * Get unit of measurement
     * 
     * @return Returns the unit.
     */
    public String getUnit() {
        return unit;
    }

    /**
     * Set unit of measurement
     * 
     * @param unit
     *            The unit to set.
     */
    public void setUnit(String unit) {
        this.unit = unit;
    }

    /**
     * Get value type
     * 
     * @return Returns the valueType.
     */
    public String getValueType() {
        return valueType;
    }

    /**
     * Set value type
     * 
     * @param valueType
     *            The valueType to set.
     */
    public void setValueType(String valueType) {
        this.valueType = valueType;
    }

    /**
     * Check whether unit of measure is set
     * 
     * @return <code>true</code>, if unit of measure is set
     */
    public boolean isSetUnit() {
        return StringHelper.isNotEmpty(getUnit());
    }
}
