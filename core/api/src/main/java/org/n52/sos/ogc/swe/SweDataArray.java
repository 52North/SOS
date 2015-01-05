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
package org.n52.sos.ogc.swe;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import org.n52.sos.ogc.swe.SweConstants.SweDataComponentType;
import org.n52.sos.ogc.swe.encoding.SweAbstractEncoding;
import org.n52.sos.ogc.swe.simpleType.SweCount;

/**
 * SOS internal representation of SWE dataArray TODO document that this
 * implementation supports only simple types in swe:elementType.swe:DataRecord
 * in TWiki
 * 
 * @since 4.0.0
 */
public class SweDataArray extends SweAbstractDataComponent {

    /**
     * swe:values<br />
     * Each list entry represents one block, a list of tokens.<br />
     * Atm, this implementation using java.lang.String to represent each token.
     */
    private List<List<String>> values;

    /**
     * swe:elementType
     */
    private SweAbstractDataComponent elementType;

    /**
     * 
     */
    private SweAbstractEncoding encoding;

    private SweCount elementCount;

    /**
     * @return the values
     */
    public List<List<String>> getValues() {
        return values;
    }

    /**
     * 
     * @param values
     *            the values to set
     * @return This SweDataArray
     */
    public SweDataArray setValues(final List<List<String>> values) {
        this.values = values;
        return this;
    }

    /**
     * @return the elementType
     */
    public SweAbstractDataComponent getElementType() {
        return elementType;
    }

    /**
     * @param elementType
     *            the elementType to set
     * @return This SweDataArray
     */
    public SweDataArray setElementType(final SweAbstractDataComponent elementType) {
        this.elementType = elementType;
        return this;
    }

    public SweCount getElementCount() {
        SweCount elementCount = new SweCount();
        if (isSetValues()) {
            elementCount.setValue(values.size());
        } else if (isSetElementCount()) {
            elementCount = this.elementCount;
        } else {
            elementCount.setValue(0);
        }
        return elementCount;
    }

    public SweAbstractEncoding getEncoding() {
        return encoding;
    }

    public SweDataArray setEncoding(final SweAbstractEncoding encoding) {
        this.encoding = encoding;
        return this;
    }

    /**
     * @return <tt>true</tt>, if the values field is set properly
     */
    public boolean isSetValues() {
        if (values != null && !values.isEmpty()) {
            if (values.size() == 1) {
                final List<String> list = values.get(0);
                return list != null && !list.isEmpty();
            }
            return true;
        }
        return false;
    }

    /**
     * Adds the given block - a {@link List}<{@link String}> - add the end of
     * the current list of blocks
     * 
     * @param blockOfTokensToAddAtTheEnd
     * @return <tt>true</tt> (as specified by {@link Collection#add}) <br />
     *         <tt>false</tt> if block could not be added
     */
    public boolean add(final List<String> blockOfTokensToAddAtTheEnd) {
        if (values == null) {
            values = new LinkedList<List<String>>();
        }
        return values.add(blockOfTokensToAddAtTheEnd);
    }
    
    public boolean addAll(List<List<String>> newValues) {
        if (values == null) {
            values = newValues;
        }
        return values.addAll(newValues);
    }

    @Override
    public int hashCode() {
        final int prime = 23;
        int hash = 7;
        hash = prime * hash + super.hashCode();
        hash = prime * hash + (getValues() != null ? getValues().hashCode() : 0);
        hash = prime * hash + (getElementType() != null ? getElementType().hashCode() : 0);
        hash = prime * hash + (getEncoding() != null ? getEncoding().hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(final Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final SweDataArray other = (SweDataArray) obj;
        if (getValues() != other.getValues() && (getValues() == null || !getValues().equals(other.getValues()))) {
            return false;
        }
        if (getElementType() != other.getElementType()
                && (getElementType() == null || !getElementType().equals(other.getElementType()))) {
            return false;
        }
        if (getEncoding() != other.getEncoding()
                && (getEncoding() == null || !getEncoding().equals(other.getEncoding()))) {
            return false;
        }
        return super.equals(obj);
    }

    public boolean isSetElementTyp() {
        return elementType != null;
    }

    public boolean isSetEncoding() {
        return encoding != null;
    }

    public SweDataArray setElementCount(final SweCount elementCount) {
        this.elementCount = elementCount;
        return this;
    }

    public boolean isSetElementCount() {
        return elementCount != null || isSetValues();
    }

    public boolean isEmpty() {
        return isSetElementTyp() && isSetEncoding() && isSetValues();
    }

    @Override
    public SweDataComponentType getDataComponentType() {
        return SweDataComponentType.DataArray;
    }
}
