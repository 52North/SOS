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

import java.util.Set;

import org.n52.sos.ogc.filter.FilterConstants.BinaryLogicOperator;

import com.google.common.collect.Sets;

/**
 * SOS class for binary logic filters "AND" and "OR"
 * 
 * @since 4.0.0
 * 
 */
public class BinaryLogicFilter extends Filter<BinaryLogicOperator> {

    private BinaryLogicOperator operator;

    private Set<Filter<?>> filterPredicates = Sets.newHashSet();
    
    /**
     * constructor
     * 
     * @param operator
     *            Binary logic filter operator
     */
    public BinaryLogicFilter(BinaryLogicOperator operator) {
        super();
        this.operator = operator;
    }

    /**
     * constructor
     * 
     * @param operator
     *            Binary logic filter operator
     * @param filterOne
     *            First filter
     * @param filterTwo
     *            Second filter
     */
    public BinaryLogicFilter(BinaryLogicOperator operator, Filter<?> filterOne, Filter<?> filterTwo) {
        super();
        this.operator = operator;
        filterPredicates.add(filterOne);
        filterPredicates.add(filterTwo);
    }

    @Override
    public BinaryLogicOperator getOperator() {
        return operator;
    }

    @Override
    public Filter<BinaryLogicOperator> setOperator(BinaryLogicOperator operator) {
        this.operator = operator;
        return this;
    }

    /**
     * @return the filterPredicates
     */
    public Set<Filter<?>> getFilterPredicates() {
        return filterPredicates;
    }

    /**
     * @param filterPredicate
     *            the filterPredicate to add
     */
    public BinaryLogicFilter addFilterPredicates(Filter<?> filterPredicate) {
        this.filterPredicates.add(filterPredicate);
        return this;
    }

    /**
     * @param filterPredicates
     *            the filterPredicates to add
     */
    public BinaryLogicFilter addFilterPredicates(Set<Filter<?>> filterPredicates) {
        this.filterPredicates.addAll(filterPredicates);
        return this;
        
    }

}
