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

import org.n52.iceland.ogc.filter.FilterConstants.SpatialOperator;

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
