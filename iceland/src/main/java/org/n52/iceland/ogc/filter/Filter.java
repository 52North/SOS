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

import org.n52.iceland.util.StringHelper;

/**
 * SOS classf or FES FilterPredicates
 * 
 * @author Carsten Hollmann <c.hollmann@52north.org>
 * @since 4.0.0
 * 
 * @param <T>
 *            operator type, e.g.
 *            {@link org.n52.iceland.ogc.filter.FilterConstants.TimeOperator},
 *            {@link org.n52.iceland.ogc.filter.FilterConstants.SpatialOperator}
 */
public abstract class Filter<T> implements AbstractSelectionClause {

    /**
     * Value reference
     */
    private String valueReference;

    /**
     * constructor
     */
    public Filter() {
    }

    /**
     * @param valueReference
     */
    public Filter(String valueReference) {
        super();
        this.valueReference = valueReference;
    }

    /**
     * Get value reference
     * 
     * @return value reference
     */
    public String getValueReference() {
        return valueReference;
    }

    /**
     * Set value reference
     * 
     * @param valueReference
     *            value reference
     * @return This filter
     */
    public Filter<T> setValueReference(String valueReference) {
        this.valueReference = valueReference;
        return this;
    }

    /**
     * Check if valueReference is set
     * 
     * @return <code>true</code>, if valueReference is set
     */
    public boolean hasValueReference() {
        return StringHelper.isNotEmpty(getValueReference());
    }

    /**
     * Get filter operator
     * 
     * @return filter operator
     */
    public abstract T getOperator();

    /**
     * Set filter operator
     * 
     * @param operator
     *            filter operator
     */
    public abstract Filter<T> setOperator(T operator);

    /**
     * Check if operator is set
     * 
     * @return <code>true</code>, if operator is set
     */
    public boolean isSetOperator() {
        return getOperator() != null;
    }
}
