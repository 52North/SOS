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
package org.n52.iceland.ogc.filter;

import org.n52.iceland.ogc.filter.FilterConstants.SortOrder;

/**
 * SOS class for FES SortProperty element
 * 
 * @author Carsten Hollmann <c.hollmann@52north.org>
 * 
 * @since 4.0.0
 *
 */
public class FesSortProperty {

    private String valueReference;

    private SortOrder sortOrder;

    public FesSortProperty(String valueReference) {
        setValueReference(valueReference);
    }

    /**
     * @return the valueReference
     */
    public String getValueReference() {
        return valueReference;
    }

    /**
     * @param valueReference
     *            the valueReference to set
     */
    public FesSortProperty setValueReference(String valueReference) {
        this.valueReference = valueReference;
        return this;
    }

    /**
     * @return the sortOrder
     */
    public SortOrder getSortOrder() {
        return sortOrder;
    }

    /**
     * @param sortOrder
     *            the sortOrder to set
     */
    public FesSortProperty setSortOrder(SortOrder sortOrder) {
        this.sortOrder = sortOrder;
        return this;
    }

    public boolean isSetSortOrder() {
        return getSortOrder() != null;
    }
}
