/**
 * Copyright (C) 2012-2017 52°North Initiative for Geospatial Open Source
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

import org.n52.sos.ogc.UoM;
import org.n52.sos.ogc.ows.OwsExceptionReport;
import org.n52.sos.ogc.swe.SweConstants.SweDataComponentType;
import org.n52.sos.w3c.xlink.Referenceable;
import org.n52.sos.ogc.swe.SweDataComponentVisitor;
import org.n52.sos.ogc.swe.VoidSweDataComponentVisitor;

/**
 * @author <a href="mailto:e.h.juerrens@52north.org">Eike Hinderk
 *         J&uuml;rrens</a>
 * @since 4.0.0
 */
public class SweCategory extends SweAbstractUomType<String> implements SweQuality {

    private String value;
    private Referenceable<SweAllowedTokens> constraint;
    
    public SweCategory() {
    }

    public SweCategory(String value, String uom) {
        this.value = value;
        setUom(uom);
    }
    
    public SweCategory(String value, UoM uom) {
        this.value = value;
        setUom(uom);
    }
    
    @Override
    public String getValue() {
        return value;
    }

    @Override
    public SweCategory setValue(final String value) {
        this.value = value;
        return this;
    }

    public SweCategory setCodeSpace(final String codeSpace) {
        setUom(codeSpace);
        return this;
    }

    public String getCodeSpace() {
        return getUom();
    }

    public boolean isSetCodeSpace() {
        return isSetUom();
    }

    @Override
    public String toString() {
        return String.format("SosSweCategory [quality=%s, value=%s, codeSpace=%s, simpleType=%s]", getQuality(),
                value, getUom(), getDataComponentType());
    }

    @Override
    public boolean isSetValue() {
        return value != null && !value.isEmpty();
    }

    @Override
    public String getStringValue() {
        return value;
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
        return SweDataComponentType.Category;
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
    
    @Override
    public SweCategory clone() {
        SweCategory clone = new SweCategory();
        copyValueTo(clone);
        if (isSetQuality()) {
            clone.setQuality(cloneQuality());
        }
        if (isSetValue()) {
            clone.setValue(getValue());
        }
        if (isSetContstraint()) {
            clone.setConstraint(getConstraint());
        }
        return clone;
    }

}
