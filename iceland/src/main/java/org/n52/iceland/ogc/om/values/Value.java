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

import java.io.Serializable;

/**
 * Interface for measurement value representation for observation
 * 
 * @since 4.0.0
 * 
 * @param <T>
 *            specific value type
 */
public interface Value<T> extends Serializable {

    /**
     * Set the measurment value
     * 
     * @param value
     *            Value to set
     */
    void setValue(T value);

    /**
     * Get the measurement value
     * 
     * @return Measurement value
     */
    T getValue();

    /**
     * Set the unit of measure
     * 
     * @param unit
     *            Unit of measure
     */
    void setUnit(String unit);

    /**
     * Get the unit of measure
     * 
     * @return Unit of measure
     */
    String getUnit();

    /**
     * Check whether the value is set
     * 
     * @return <code>true</code>, if value is set
     */
    boolean isSetValue();

    /**
     * Check whether the unit of measure is set
     * 
     * @return <code>true</code>, if unit of measure is set
     */
    boolean isSetUnit();

}
