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
import org.n52.sos.ogc.swe.SweAbstractDataComponent;
import org.n52.sos.ogc.swe.SweConstants.SweDataComponentType;
import org.n52.sos.w3c.xlink.Referenceable;
import org.n52.sos.ogc.swe.SweDataComponentVisitor;
import org.n52.sos.ogc.swe.VoidSweDataComponentVisitor;

public class SweCategoryRange extends SweAbstractUomType<RangeValue<String>> implements SweQuality {
    
    /**
     * value
     */
    private RangeValue<String> value;
    private Referenceable<SweAllowedTokens> constraint;


    @Override
    public RangeValue<String> getValue() {
        return value;
    }

    @Override
    public String getStringValue() {
        return value.toString();
    }

    @Override
    public boolean isSetValue() {
        return value != null;
    }

    @Override
    public SweAbstractSimpleType<RangeValue<String>> setValue(RangeValue<String> value) {
        this.value = value;
        return this;
    }
    
    /**
     * @return the constraint
     */
    public Referenceable<SweAllowedTokens> getConstraint() {
        return constraint;
    }

    /**
     * @param constraint the constraint to set
     */
    public void setConstraint(SweAllowedTokens constraint) {
        this.constraint = Referenceable.of(constraint);
    }
    
    public boolean isSetContstraint() {
        return getConstraint() != null && !getConstraint().isAbsent();
    }
    
    /**
     * @param constraint the constraint to set
     */
    public void setConstraint(Referenceable<SweAllowedTokens> constraint) {
        this.constraint = constraint;
    }

    @Override
    public SweDataComponentType getDataComponentType() {
        return SweDataComponentType.CategoryRange;
    }

    @Override
    public <T> T accept(SweDataComponentVisitor<T> visitor) throws OwsExceptionReport {
        return visitor.visit(this);
    }

    @Override
    public void accept(VoidSweDataComponentVisitor visitor) throws OwsExceptionReport {
        visitor.visit(this);
    }

    @SuppressWarnings("unchecked")
    @Override
    public SweAbstractDataComponent clone() throws CloneNotSupportedException {
        SweCategoryRange clone = new SweCategoryRange();
        copyValueTo(clone);
        if (isSetQuality()) {
            clone.setQuality(cloneQuality());
        }
        if (isSetValue()) {
            clone.setValue((RangeValue<String>)getValue().clone());
        }
        if (isSetContstraint()) {
            clone.setConstraint(getConstraint());
        }
        return clone;
    }

    @Override
    public int hashCode() {
        final int prime = 97;
        int hash = 7;
        hash = prime * hash + super.hashCode();
        return hash;
    }

    @Override
    public boolean equals(final Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        return super.equals(obj);
    }
}
