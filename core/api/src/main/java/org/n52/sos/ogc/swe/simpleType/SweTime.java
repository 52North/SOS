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

import org.joda.time.DateTime;

import org.n52.sos.ogc.ows.OwsExceptionReport;
import org.n52.sos.ogc.swe.SweConstants.SweDataComponentType;
import org.n52.sos.ogc.swe.SweDataComponentVisitor;
import org.n52.sos.ogc.swe.VoidSweDataComponentVisitor;
import org.n52.sos.util.DateTimeHelper;
import org.n52.sos.w3c.xlink.Referenceable;

/**
 * SOS internal representation of SWE simpleType time
 *
 * @since 4.0.0
 */
public class SweTime extends SweAbstractUomType<DateTime> {

    /**
     * value
     */
    private DateTime value;
    private Referenceable<SweAllowedTimes> constraint;

    @Override
    public DateTime getValue() {
        return value;
    }

    @Override
    public SweTime setValue(final DateTime value) {
        this.value = value;
        return this;
    }

    @Override
    public String getStringValue() {
        if (isSetValue()) {
            return DateTimeHelper.formatDateTime2IsoString(value);
        }
        return null;
    }

    @Override
    public boolean isSetValue() {
        return value != null;
    }
    
    /**
     * @return the constraint
     */
    public Referenceable<SweAllowedTimes> getConstraint() {
        return constraint;
    }

    /**
     * @param constraint the constraint to set
     */
    public void setConstraint(SweAllowedTimes constraint) {
        this.constraint = Referenceable.of(constraint);
    }
    
    public boolean isSetContstraint() {
        return getConstraint() != null && !getConstraint().isAbsent();
    }
    
    /**
     * @param constraint the constraint to set
     */
    public void setConstraint(Referenceable<SweAllowedTimes> constraint) {
        this.constraint = constraint;
    }

    @Override
    public SweDataComponentType getDataComponentType() {
        return SweDataComponentType.Time;
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
    public SweTime clone() {
        SweTime clone = new SweTime();
        copyValueTo(clone);
        if (isSetValue()) {
            clone.setValue(getValue());
        }
        if (isSetContstraint()) {
            clone.setConstraint(getConstraint());
        }
        return clone;
    }
}
