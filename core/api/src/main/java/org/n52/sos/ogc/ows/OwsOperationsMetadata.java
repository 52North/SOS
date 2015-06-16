/**
 * Copyright (C) 2012-2015 52°North Initiative for Geospatial Open Source
 * Software GmbH
 *
 * This program is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License version 2 as published
 * by the Free Software Foundation.
 *
 * If the program is linked with libraries which are licensed under one of
 * the following licenses, the combination of the program with the linked
 * library is not considered a "derivative work" of the program:
 *
 *     - Apache License, version 2.0
 *     - Apache Software License, version 1.0
 *     - GNU Lesser General Public License, version 3
 *     - Mozilla Public License, versions 1.0, 1.1 and 2.0
 *     - Common Development and Distribution License (CDDL), version 1.0
 *
 * Therefore the distribution of the program linked with libraries licensed
 * under the aforementioned licenses, is permitted by the copyright holders
 * if the distribution is compliant with both the GNU General Public
 * License version 2 and the aforementioned licenses.
 *
 * This program is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General
 * Public License for more details.
 */
package org.n52.sos.ogc.ows;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.SortedMap;
import java.util.SortedSet;
import java.util.TreeMap;
import java.util.TreeSet;

import org.n52.sos.util.CollectionHelper;

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
