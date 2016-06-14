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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.n52.sos.ogc.UoM;
import org.n52.sos.ogc.gml.time.Time;
import org.n52.sos.ogc.gml.time.TimePeriod;
import org.n52.sos.ogc.om.TimeLocationValueTriple;
import org.n52.sos.ogc.om.values.visitor.ValueVisitor;
import org.n52.sos.ogc.om.values.visitor.VoidValueVisitor;
import org.n52.sos.ogc.ows.OwsExceptionReport;
import org.n52.sos.util.CollectionHelper;

/**
 * {@link MultiValue} representing a time location value triple for observations
 *
 * @since 4.4.0
 *
 */
public class TLVTValue implements MultiValue<List<TimeLocationValueTriple>> {

    /**
     * serial number
     */
    private static final long serialVersionUID = 6367772653326892723L;

    /**
     * Mesurement values
     */
    private List<TimeLocationValueTriple> value = new ArrayList<TimeLocationValueTriple>(0);

    /**
     * Unit of measure
     */
    private UoM unit;

    @Override
    public TLVTValue setValue(List<TimeLocationValueTriple> value) {
        this.value.clear();
        this.value.addAll(value);
        return this;
    }

    @Override
    public List<TimeLocationValueTriple> getValue() {
        Collections.sort(value);
        return value;
    }

    /**
     * Add time value pair value
     *
     * @param value
     *            Time value pair value to add
     */
    public void addValue(TimeLocationValueTriple value) {
        this.value.add(value);
    }

    /**
     * Add time value pair values
     *
     * @param values
     *            Time value pair values to add
     */
    public void addValues(List<TimeLocationValueTriple> values) {
        this.value.addAll(values);
    }

    @Override
    public void setUnit(String unit) {
        this.unit = new UoM(unit);
    }

    @Override
    public String getUnit() {
        if (isSetUnit()) {
            return unit.getUom();
        }
        return null;
    }

    @Override
    public UoM getUnitObject() {
        return this.unit;
    }

    @Override
    public void setUnit(UoM unit) {
        this.unit = unit;
    }

    @Override
    public boolean isSetUnit() {
        return getUnitObject() != null && !getUnitObject().isEmpty();
    }

    @Override
    public Time getPhenomenonTime() {
        TimePeriod timePeriod = new TimePeriod();
        if (isSetValue()) {
            for (TimeLocationValueTriple timeValuePair : getValue()) {
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
    public <X> X accept(ValueVisitor<X> visitor) throws OwsExceptionReport {
        return visitor.visit(this);
    }

    @Override
    public void accept(VoidValueVisitor visitor) throws OwsExceptionReport {
        visitor.visit(this);
    }
}
