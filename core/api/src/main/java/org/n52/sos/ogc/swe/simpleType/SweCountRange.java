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

import org.n52.sos.ogc.ows.OwsExceptionReport;
import org.n52.sos.ogc.swe.RangeValue;
import org.n52.sos.ogc.swe.SweConstants.SweDataComponentType;
import org.n52.sos.w3c.xlink.Referenceable;
import org.n52.sos.ogc.swe.SweDataComponentVisitor;
import org.n52.sos.ogc.swe.VoidSweDataComponentVisitor;

/**
 * @since 4.0.0
 *
 */
public class SweCountRange extends SweAbstractSimpleType<RangeValue<Integer>> {

    private RangeValue<Integer> value;
    private Referenceable<SweAllowedValues> constraint;

    @Override
    public RangeValue<Integer> getValue() {
        return value;
    }

    @Override
    public SweCountRange setValue(final RangeValue<Integer> value) {
        this.value = value;
        return this;
    }

    @Override
    public boolean isSetValue() {
        return value != null;
    }

    @Override
    public String getStringValue() {
        return value.toString();
    }
    
    /**
     * @return the constraint
     */
    public Referenceable<SweAllowedValues> getConstraint() {
        return constraint;
    }

    /**
     * @param constraint the constraint to set
     */
    public void setConstraint(SweAllowedValues constraint) {
        this.constraint = Referenceable.of(constraint);
    }
    
    public boolean isSetContstraint() {
        return getConstraint() != null && !getConstraint().isAbsent();
    }
    
    /**
     * @param constraint the constraint to set
     */
    public void setConstraint(Referenceable<SweAllowedValues> constraint) {
        this.constraint = constraint;
    }

    @Override
    public SweDataComponentType getDataComponentType() {
        return SweDataComponentType.CountRange;
    }

    @Override
    public <T> T accept(SweDataComponentVisitor<T> visitor)
            throws OwsExceptionReport {
        return visitor.visit(this);
    }

    @Override
    public void accept(VoidSweDataComponentVisitor visitor)
            throws OwsExceptionReport {
        visitor.visit(this);
    }
    
    @SuppressWarnings("unchecked")
    @Override
    public SweCountRange clone() {
        SweCountRange clone = new SweCountRange();
        copyValueTo(clone);
        if (isSetQuality()) {
            clone.setQuality(cloneQuality());
        }
        if (isSetValue()) {
            clone.setValue((RangeValue<Integer>)getValue().clone());
        }
        if (isSetContstraint()) {
            clone.setConstraint(getConstraint());
        }
        return clone;
    }

}
