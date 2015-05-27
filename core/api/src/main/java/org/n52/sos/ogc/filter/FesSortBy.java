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

import java.util.List;

import com.google.common.collect.Lists;

/**
 * SOS class for FES SortBy element
 * 
 * @author Carsten Hollmann <c.hollmann@52north.org>
 * 
 * @since 4.0.0
 *
 */
public class FesSortBy implements AbstractSortingClause {

    private List<FesSortProperty> sortProperties = Lists.newArrayList();

    
    public FesSortBy(FesSortProperty sortProperty) {
        super();
        addSortProperty(sortProperty);
    }
    
    public FesSortBy(List<FesSortProperty> sortProperties) {
        super();
        setSortProperties(sortProperties);
    }

    /**
     * @return the sortProperties
     */
    public List<FesSortProperty> getSortProperties() {
        return sortProperties;
    }

    public FesSortBy addSortProperty(FesSortProperty sortProperty) {
       getSortProperties().add(sortProperty);
       return this;
    }

    public FesSortBy addSortProperties(List<FesSortProperty> sortProperties) {
        getSortProperties().addAll(sortProperties);
        return this;
    }

    /**
     * @param sortProperties
     *            the sortProperties to set
     */
    private void setSortProperties(List<FesSortProperty> sortProperties) {
        this.sortProperties = sortProperties;
    }

}
