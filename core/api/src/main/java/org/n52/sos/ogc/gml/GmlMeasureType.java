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

import org.n52.sos.util.StringHelper;

/**
 * Class represents a GML conform MeasureType element
 * 
 * @since 4.0.0
 * 
 */
public class GmlMeasureType {

    /**
     * Measured value
     */
    private Double value;

    /**
     * Unit of measure
     */
    private String unit;

    /**
     * constructor
     * 
     * @param value
     *            Measured value
     */
    public GmlMeasureType(Double value) {
        this(value, null);
    }

    /**
     * constructor
     * 
     * @param value
     *            Measured value
     * @param unit
     *            Unit of measure
     */
    public GmlMeasureType(Double value, String unit) {
        this.value = value;
        this.unit = unit;
    }

    /**
     * @param value
     *            Measured value to set
     */
    public void setValue(Double value) {
        this.value = value;
    }

    /**
     * @return Measured value
     */
    public Double getValue() {
        return value;
    }

    /**
     * Set unit of measure
     * 
     * @param unit
     *            Unit of measure to set
     */
    public void setUnit(String unit) {
        this.unit = unit;
    }

    /**
     * Get unit of measure
     * 
     * @return Unit of measure
     */
    public String getUnit() {
        return unit;
    }

    @Override
    public String toString() {
        return String.format("GmlMeasureType [value=%s, unit=%s]", getValue(), getUnit());
    }

    /**
     * Check whether measured value is set
     * 
     * @return <code>true</code>, if measured value is set
     */
    public boolean isSetValue() {
        return value != null;
    }

    /**
     * Check whether unit of measure is set
     * 
     * @return <code>true</code>, if unit of measure is set
     */
    public boolean isSetUnit() {
        return StringHelper.isNotEmpty(getUnit());
    }

}
