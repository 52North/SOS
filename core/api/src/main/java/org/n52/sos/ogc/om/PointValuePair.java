/**
 * Copyright (C) 2012-2020 52°North Initiative for Geospatial Open Source
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
package org.n52.sos.ogc.om;

import org.n52.sos.ogc.om.values.Value;

import com.vividsolutions.jts.geom.Point;

public class PointValuePair implements Comparable<PointValuePair> {
    /**
     * Point value pair point
     */
    private Point point;

    /**
     * Point value pair value
     */
    private Value<?> value;

    /**
     * Constructor
     * 
     * @param point
     *            Point value pair point
     * @param value
     *            Point value pair value
     */
    public PointValuePair(Point point, Value<?> value) {
        this.point = point;
        this.value = value;
    }

    /**
     * Get point value pair point
     * 
     * @return Point value pair point
     */
    public Point getPoint() {
        return point;
    }

    /**
     * Get point value pair value
     * 
     * @return Point value pair value
     */
    public Value<?> getValue() {
        return value;
    }

    /**
     * Set point value pair point
     * 
     * @param point
     *            Point value pair point to set
     */
    public void setPoint(Point point) {
        this.point = point;
    }

    /**
     * Set point value pair value
     * 
     * @param value
     *            Point value pair value to set
     */
    public void setValue(Value<?> value) {
        this.value = value;
    }
    
    public boolean isSetValue() {
        return getValue() != null && getValue().isSetValue();
    }
    
    public boolean isSetPoint() {
        return getPoint() != null && !getPoint().isEmpty();
    }

    public boolean isEmpty() {
        return isSetPoint() && isSetValue();
    }

    @Override
    public int compareTo(PointValuePair o) {
        return point.compareTo(o.point);
    }
}
