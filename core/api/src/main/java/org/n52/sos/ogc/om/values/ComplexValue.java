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
package org.n52.sos.ogc.om.values;

import org.n52.sos.ogc.UoM;
import org.n52.sos.ogc.om.values.visitor.ValueVisitor;
import org.n52.sos.ogc.om.values.visitor.VoidValueVisitor;
import org.n52.sos.ogc.ows.OwsExceptionReport;
import org.n52.sos.ogc.swe.SweAbstractDataRecord;

import com.google.common.base.Objects;

public class ComplexValue implements Value<SweAbstractDataRecord> {
    private static final long serialVersionUID = 7864029515468084800L;
    private SweAbstractDataRecord value;
    private UoM unit;

    public ComplexValue() {
        this(null);
    }

    public ComplexValue(SweAbstractDataRecord value) {
        this.value = value;
    }

    @Override
    public ComplexValue setValue(SweAbstractDataRecord value) {
        this.value = value;
        return this;
    }

    @Override
    public SweAbstractDataRecord getValue() {
        return this.value;
    }

    @Override
    public boolean isSetValue() {
        return this.value != null;
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
    public String toString() {
        return Objects.toStringHelper(this)
                .add("value", this.value)
                .add("unit", this.unit)
                .toString();
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(this.value, this.unit);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof ComplexValue) {
            ComplexValue that = (ComplexValue) obj;
            return Objects.equal(this.getValue(), that.getValue()) &&
                   Objects.equal(this.getUnit(), that.getUnit());
        }
        return false;
    }

    @Override
    public <X> X accept(ValueVisitor<X> visitor)
            throws OwsExceptionReport {
        return visitor.visit(this);
    }

    @Override
    public void accept(VoidValueVisitor visitor)
            throws OwsExceptionReport {
        visitor.visit(this);
    }
}
