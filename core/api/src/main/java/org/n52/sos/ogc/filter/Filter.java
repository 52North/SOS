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
package org.n52.sos.ogc.filter;

import org.n52.sos.util.StringHelper;

/**
 * SOS classf or FES FilterPredicates
 * 
 * @author Carsten Hollmann <c.hollmann@52north.org>
 * @since 4.0.0
 * 
 * @param <T>
 *            operator type, e.g.
 *            {@link org.n52.sos.ogc.filter.FilterConstants.TimeOperator},
 *            {@link org.n52.sos.ogc.filter.FilterConstants.SpatialOperator}
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
