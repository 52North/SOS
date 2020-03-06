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
package org.n52.sos.ogc.swe.simpleType;

import java.math.BigInteger;
import java.util.List;

import org.n52.sos.ogc.swe.RangeValue;
import org.n52.sos.ogc.swes.AbstractSWES;
import org.n52.sos.util.CollectionHelper;

import com.google.common.collect.Lists;

public class SweAllowedValues extends AbstractSWES {

    private static final long serialVersionUID = 7657978405846926210L;
    private List<Double> value = Lists.newArrayList();
    private List<RangeValue<Double>> interval = Lists.newArrayList();
    private BigInteger significantFigures;

    /**
     * @return the value
     */
    public List<Double> getValue() {
        return value;
    }

    /**
     * @param value
     *            the value to set
     */
    public void setValue(List<Double> value) {
        this.value.clear();
        this.value.addAll(value);
    }
    
    public void addValue(Double value) {
        this.value.add(value);
    }
    
    public void addValue(double value) {
        this.value.add(value);
    }
    
    public boolean isSetValue() {
        return CollectionHelper.isNotEmpty(getValue());
    }

    /**
     * @return the interval
     */
    public List<RangeValue<Double>> getInterval() {
        return interval;
    }

    /**
     * @param interval
     *            the interval to set
     */
    public void setInterval(List<RangeValue<Double>> interval) {
        this.interval.clear();
        this.interval.addAll(interval);
    }
    
    /**
     * @param interval
     *            the interval to add
     */
    public void addInterval(RangeValue<Double> interval) {
        this.interval.add(interval);
    }
    
    public boolean isSetInterval() {
        return CollectionHelper.isNotEmpty(getInterval());
    }

    /**
     * @return the significantFigures
     */
    public BigInteger getSignificantFigures() {
        return significantFigures;
    }

    /**
     * @param significantFigures
     *            the significantFigures to set
     */
    public void setSignificantFigures(BigInteger significantFigures) {
        this.significantFigures = significantFigures;
    }
    
    public boolean isSetSignificantFigures() {
        return getSignificantFigures() != null;
    }

}
