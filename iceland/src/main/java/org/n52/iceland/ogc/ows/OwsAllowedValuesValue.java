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

import java.util.Collection;
import java.util.Set;

import com.google.common.collect.Sets;

/**
 * Class represents an OWS AllowedValues value element
 * 
 * @author Carsten Hollmann <c.hollmann@52north.org>
 * 
 * @since 4.0.0
 * 
 */
public class OwsAllowedValuesValue implements OwsAllowedValues {

    private Set<String> values = Sets.newHashSet();

    /**
     * constructor
     * 
     * @param value
     */
    public OwsAllowedValuesValue(String value) {
        addValue(value);
    }

    /**
     * constructor
     * 
     * @param values
     */
    public OwsAllowedValuesValue(Collection<String> values) {
        setValues(values);
    }

    /**
     * @return the values
     */
    public Set<String> getValues() {
        return values;
    }

    /**
     * @param value
     *            the value to add
     */
    public void addValue(String value) {
        getValues().add(value);
    }

    /**
     * @param values
     *            the values to add
     */
    public void addValues(Collection<String> values) {
        getValues().addAll(values);
    }

    /**
     * @param values
     *            the values to set
     */
    private void setValues(Collection<String> values) {
        this.values = Sets.newHashSet(values);
    }
}
