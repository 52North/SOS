/**
 * Copyright (C) 2012-2014 52°North Initiative for Geospatial Open Source
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

import org.n52.sos.ogc.om.values.visitor.ThrowingValueVisitor;
import org.n52.sos.ogc.om.values.visitor.ThrowingVoidValueVisitor;
import org.n52.sos.ogc.om.values.visitor.ValueVisitor;
import org.n52.sos.ogc.om.values.visitor.VoidValueVisitor;
import org.n52.sos.ogc.swe.SweDataRecord;

import com.google.common.base.Objects;

public class ComplexValue implements Value<SweDataRecord> {
    private static final long serialVersionUID = 7864029515468084800L;
    private SweDataRecord value;
    private String unit;

    public ComplexValue() {
        this(null);
    }

    public ComplexValue(SweDataRecord value) {
        this.value = value;
    }

    @Override
    public void setValue(SweDataRecord value) {
        this.value = value;
    }

    @Override
    public SweDataRecord getValue() {
        return this.value;
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
    public boolean isSetValue() {
        return this.value != null;
    }

    @Override
    public boolean isSetUnit() {
        return this.unit != null && !this.unit.isEmpty();
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
    public <X> X accept(ValueVisitor<X> visitor) {
        return visitor.visit(this);
    }

    @Override
    public void accept(VoidValueVisitor visitor) {
        visitor.visit(this);
    }

    @Override
    public <X, T extends Exception> X accept(ThrowingValueVisitor<X, T> visitor) throws T {
        return visitor.visit(this);
    }

    @Override
    public <T extends Exception> void accept(ThrowingVoidValueVisitor<T> visitor) throws T {
        visitor.visit(this);
    }
}
