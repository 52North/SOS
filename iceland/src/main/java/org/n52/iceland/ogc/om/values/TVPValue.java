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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.n52.iceland.ogc.gml.time.Time;
import org.n52.iceland.ogc.gml.time.TimePeriod;
import org.n52.iceland.ogc.om.TimeValuePair;
import org.n52.iceland.util.CollectionHelper;
import org.n52.iceland.util.StringHelper;

/**
 * Multi value representing a time value pairs for observations
 * 
 * @since 4.0.0
 * 
 */
public class TVPValue implements MultiValue<List<TimeValuePair>> {

    /**
     * serial number
     */
    private static final long serialVersionUID = -5156098026027119423L;

    /**
     * Mesurement values
     */
    private List<TimeValuePair> value = new ArrayList<TimeValuePair>(0);

    /**
     * Unit of measure
     */
    private String unit;

    @Override
    public void setValue(List<TimeValuePair> value) {
        this.value = value;
    }

    @Override
    public List<TimeValuePair> getValue() {
        Collections.sort(value);
        return value;
    }

    /**
     * Add time value pair value
     * 
     * @param value
     *            Time value pair value to add
     */
    public void addValue(TimeValuePair value) {
        this.value.add(value);
    }

    /**
     * Add time value pair values
     * 
     * @param values
     *            Time value pair values to add
     */
    public void addValues(List<TimeValuePair> values) {
        this.value.addAll(values);
    }

    @Override
    public void setUnit(String unit) {
        this.unit = unit;
    }

    @Override
    public String getUnit() {
        return this.unit;
    }

    @Override
    public Time getPhenomenonTime() {
        TimePeriod timePeriod = new TimePeriod();
        if (isSetValue()) {
            for (TimeValuePair timeValuePair : getValue()) {
                timePeriod.extendToContain(timeValuePair.getTime());
            }
        }
        return timePeriod;
    }

    @Override
    public boolean isSetValue() {
        return CollectionHelper.isNotEmpty(getValue());
    }

    @Override
    public boolean isSetUnit() {
        return StringHelper.isNotEmpty(getUnit());
    }
}
