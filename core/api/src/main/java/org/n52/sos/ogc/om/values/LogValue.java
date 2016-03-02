/**
 * Copyright (C) 2012-2016 52°North Initiative for Geospatial Open Source
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
package org.n52.sos.ogc.om.values;

import org.n52.sos.ogc.swe.DataRecord;
import org.n52.sos.ogc.swe.simpleType.SweQuantity;

/**
 * Represents the GroundWaterML 2.0 LogValue
 * 
 * @author <a href="mailto:c.hollmann@52north.org">Carsten Hollmann</a>
 * @since 4.4.0
 *
 */
public class LogValue {

    private SweQuantity fromDepth;
    private SweQuantity toDepth;
    private DataRecord value;
    private Value<?> simpleValue;
    
    /**
     * constructor
     */
    public LogValue() {
        super();
    }
    
    /**
     * constructor
     * 
     * @param fromDepth
     *            the fromDepth value
     * @param toDepth
     *            the toDepth value
     * @param value
     *            the values
     */
    public LogValue(SweQuantity fromDepth, SweQuantity toDepth, DataRecord value) {
        super();
        this.fromDepth = fromDepth;
        this.toDepth = toDepth;
        this.value = value;
    }

    /**
     * @return the fromDepth
     */
    public SweQuantity getFromDepth() {
        return fromDepth;
    }

    /**
     * @param fromDepth
     *            the fromDepth to set
     */
    public LogValue setFromDepth(SweQuantity fromDepth) {
        this.fromDepth = fromDepth;
        return this;
    }
    
    public boolean isSetFromDepth() {
        return getFromDepth() != null;
    }

    /**
     * @return the toDepth
     */
    public SweQuantity getToDepth() {
        return toDepth;
    }

    /**
     * @param toDepth
     *            the toDepth to set
     */
    public LogValue setToDepth(SweQuantity toDepth) {
        this.toDepth = toDepth;
        return this;
    }
    
    public boolean isSetToDepth() {
        return getToDepth() != null;
    }

    /**
     * @return the value
     */
    public DataRecord getValue() {
        return value;
    }

    /**
     * @param value
     *            the value to set
     */
    public LogValue setValue(DataRecord value) {
        this.value = value;
        return this;
    }
    
    public boolean isSetValue() {
        return getValue() != null;
    }

    /**
     * @return the simpleValue
     */
    public Value<?> getSimpleValue() {
        return simpleValue;
    }

    /**
     * @param simpleValue the simpleValue to set
     */
    public void setSimpleValue(Value<?> simpleValue) {
        this.simpleValue = simpleValue;
    }

}