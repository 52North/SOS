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
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.SortedMap;
import java.util.SortedSet;
import java.util.TreeMap;
import java.util.TreeSet;

import org.n52.iceland.util.CollectionHelper;

import com.google.common.collect.Lists;

/**
 * @since 4.0.0
 * 
 */
public class OwsOperationsMetadata {
    private SortedSet<OwsOperation> operations;

    private SortedMap<String, List<OwsParameterValue>> commonValues;

    private OwsExtendedCapabilities extendedCapabilities;

    public SortedSet<OwsOperation> getOperations() {
        return Collections.unmodifiableSortedSet(operations);
    }

    public void setOperations(Collection<OwsOperation> operations) {
        this.operations = operations == null ? null : new TreeSet<OwsOperation>(operations);
    }

    public SortedMap<String, List<OwsParameterValue>> getCommonValues() {
        return Collections.unmodifiableSortedMap(commonValues);
    }

    public void addOperation(OwsOperation operation) {
        if (operations == null) {
            operations = new TreeSet<OwsOperation>();
        }
        operations.add(operation);
    }

    public void addCommonValue(String parameterName, OwsParameterValue value) {
        if (commonValues == null) {
            commonValues = new TreeMap<String, List<OwsParameterValue>>();
        }
        List<OwsParameterValue> values = commonValues.get(parameterName);
        if (values == null) {
            values = new LinkedList<OwsParameterValue>();
            commonValues.put(parameterName, values);
        }
        values.add(value);
    }
    
    public void overrideCommonValue(String parameterName, OwsParameterValue value) {
        if (commonValues == null) {
            commonValues = new TreeMap<String, List<OwsParameterValue>>();
        }
        List<OwsParameterValue> values = Lists.newLinkedList();
        values.add(value);
        commonValues.put(parameterName, values);
    }

    public boolean isSetCommonValues() {
        return !CollectionHelper.isEmpty(getCommonValues());
    }

    public boolean isSetOperations() {
        return !CollectionHelper.isEmpty(getOperations());
    }

    public boolean isEmpty() {
        return CollectionHelper.isEmpty(getOperations());
    }

    /**
     * @return the extendedCapabilities
     */
    public OwsExtendedCapabilities getExtendedCapabilities() {
        return extendedCapabilities;
    }

    /**
     * @param extendedCapabilities
     *            the extendedCapabilities to set
     */
    public void setExtendedCapabilities(OwsExtendedCapabilities extendedCapabilities) {
        this.extendedCapabilities = extendedCapabilities;
    }

    public boolean isSetExtendedCapabilities() {
        return getExtendedCapabilities() != null;
    }
}
