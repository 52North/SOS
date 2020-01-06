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
import org.n52.sos.ogc.swe.simpleType.SweQuantity;

/**
 * Quantity measurement representation for observation
 *
 * @since 4.0.0
 *
 */
public class QuantityValue extends SweQuantity implements QuantityValued<Double, QuantityValue> {
    /**
     * serial number
     */
    private static final long serialVersionUID = -1422892416601346312L;

    /**
     * constructor
     *
     * @param value
     *              Measurement value
     */
    public QuantityValue(Double value) {
        super();
        super.setValue(value);
    }

    /**
     * constructor
     *
     * @param value
     *              Measurement value
     * @param unit
     *              Unit of measure
     */
    public QuantityValue(Double value, String unit) {
        super(value, unit);
    }
    
    /**
     * constructor
     *
     * @param value
     *              Measurement value
     * @param unit
     *              Unit of measure
     */
    public QuantityValue(Double value, UoM unit) {
        super(value, unit);
    }

    @Override
    public QuantityValue setValue(final Double value) {
        super.setValue(value);
        return this;
    }

    @Override
    public void setUnit(String unit) {
        super.setUom(unit);
    }

    @Override
    public String getUnit() {
        return super.getUom();
    }

    @Override
    public UoM getUnitObject() {
        return super.getUomObject();
    }

    @Override
    public void setUnit(UoM unit) {
       super.setUom(unit);
    }

    @Override
    public boolean isSetUnit() {
        return super.isSetUom();
    }

    @Override
    public String toString() {
        return String
                .format("QuantityValue [value=%s, unit=%s]", getValue(), getUnit());
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

    @Override
    public int compareTo(QuantityValue o) {
        if (o == null) {
            throw new NullPointerException();
        }
        if (getValue() == null ^ o.getValue() == null) {
            return (getValue() == null) ? -1 : 1;
        }
        if (getValue() == null && o.getValue() == null) {
            return 0;
        }
        return getValue().compareTo(o.getValue());
    }
}
