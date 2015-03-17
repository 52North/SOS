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

import org.n52.sos.ogc.filter.FilterConstants.SpatialOperator;

import com.vividsolutions.jts.geom.Geometry;

/**
 * Spatial filter class
 * 
 * @since 4.0.0
 * 
 */
public class SpatialFilter extends Filter<SpatialOperator> {

    /**
     * Spatial filter operator
     */
    private SpatialOperator operator;

    /**
     * Filter geometry
     */
    private Geometry geometry;

    /**
     * default constructor
     */
    public SpatialFilter() {
        super();
    }

    /**
     * constructor
     * 
     * @param operatorp
     *            Spatial operator
     * @param geomWKTp
     *            Filter geometry
     * @param valueReferencep
     *            Filter valueReference
     */
    public SpatialFilter(SpatialOperator operatorp, Geometry geomWKTp, String valueReferencep) {
        super(valueReferencep);
        this.operator = operatorp;
        this.geometry = geomWKTp;
    }

    @Override
    public SpatialOperator getOperator() {
        return operator;
    }

    @Override
    public SpatialFilter setOperator(SpatialOperator operator) {
        this.operator = operator;
        return this;
    }

    /**
     * Get SRID
     * 
     * @return SRID
     */
    public int getSrid() {
        return geometry.getSRID();
    }

    /**
     * Get filter geometry
     * 
     * @return filter geometry
     */
    public Geometry getGeometry() {
        return geometry;
    }

    /**
     * Set filter geometry
     * 
     * @param geometry
     *            filter geometry
     * @return This filter
     */
    public SpatialFilter setGeometry(Geometry geometry) {
        this.geometry = geometry;
       return this;
    }

    @Override
    public String toString() {
        return "Spatial filter: " + operator + " " + geometry;
    }

}
