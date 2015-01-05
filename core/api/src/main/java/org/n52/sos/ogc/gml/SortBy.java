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
package org.n52.sos.ogc.gml;

import org.n52.sos.ogc.gml.GmlConstants.SortingOrder;

/**
 * class represents the gml:sortByType
 * 
 * @since 4.0.0
 */
public class SortBy {

    /** name of the property, by which should be sorted */
    private String property;

    /**
     * order of the sorting (currently only ascending (ASC) or descending (DESC)
     */
    private SortingOrder order;

    /**
     * constructor
     * 
     * @param propertyp
     *            name of property, by which should be sorted
     * @param orderp
     *            sorting order (currently only ascending ('ASC') or descending
     *            ('DESC')
     */
    public SortBy(String propertyp, SortingOrder orderp) {
        this.property = propertyp;
        this.order = orderp;
    }

    /**
     * default constructor
     */
    public SortBy() {
    }

    /**
     * 
     * @return Returns String representation with values of this object
     */
    public String toString() {
        return String.format("Sort by [property=%s, order=%]", getProperty(), getOrder());
    }

    /**
     * Get order
     * 
     * @return the order
     */
    public SortingOrder getOrder() {
        return order;
    }

    /**
     * Set ordering
     * 
     * @param order
     *            the order to set
     */
    public void setOrder(SortingOrder order) {
        this.order = order;
    }

    /**
     * Get property
     * 
     * @return the property
     */
    public String getProperty() {
        return property;
    }

    /**
     * Set property
     * 
     * @param property
     *            the property to set
     */
    public void setProperty(String property) {
        this.property = property;
    }
}
