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
package org.n52.sos.ogc.om.values;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.n52.sos.ogc.gml.time.Time;
import org.n52.sos.ogc.gml.time.TimePeriod;
import org.n52.sos.ogc.om.TimeValuePair;
import org.n52.sos.util.CollectionHelper;
import org.n52.sos.util.StringHelper;

/**
 * Multi value representing a time value pairs for observations
 * 
 * @since 4.0.0
 * 
 */
public class TVPValue implements MultiValue<List<TimeValuePair>> {

    /**
     * serial number
     */
    private static final long serialVersionUID = -5156098026027119423L;

    /**
     * Mesurement values
     */
    private List<TimeValuePair> value = new ArrayList<TimeValuePair>(0);

    /**
     * Unit of measure
     */
    private String unit;

    @Override
    public void setValue(List<TimeValuePair> value) {
        this.value = value;
    }

    @Override
    public List<TimeValuePair> getValue() {
        Collections.sort(value);
        return value;
    }

    /**
     * Add time value pair value
     * 
     * @param value
     *            Time value pair value to add
     */
    public void addValue(TimeValuePair value) {
        this.value.add(value);
    }

    /**
     * Add time value pair values
     * 
     * @param values
     *            Time value pair values to add
     */
    public void addValues(List<TimeValuePair> values) {
        this.value.addAll(values);
    }

    @Override
    public void setUnit(String unit) {
        this.unit = unit;
    }

    @Override
    public String getUnit() {
        return this.unit;
    }

    @Override
    public Time getPhenomenonTime() {
        TimePeriod timePeriod = new TimePeriod();
        if (isSetValue()) {
            for (TimeValuePair timeValuePair : getValue()) {
                timePeriod.extendToContain(timeValuePair.getTime());
            }
        }
        return timePeriod;
    }

    @Override
    public boolean isSetValue() {
        return CollectionHelper.isNotEmpty(getValue());
    }

    @Override
    public boolean isSetUnit() {
        return StringHelper.isNotEmpty(getUnit());
    }
}
