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
package org.n52.sos.ogc.swe.simpleType;

import java.util.Collection;

import org.n52.sos.ogc.swe.SweConstants.SweDataComponentType;

/**
 * SOS internal representation of SWE simpleType quantity
 * 
 * @author Carsten Hollmann
 * @since 4.0.0
 */
public class SweQuantity extends SweAbstractUomType<Double> implements SweQuality {

    /**
     * axis ID
     */
    private String axisID;

    /**
     * value
     */
    private Double value;

    /**
     * constructor
     */
    public SweQuantity() {
    }

    /**
     * Get axis ID
     * 
     * @return the axisID
     */
    public String getAxisID() {
        return axisID;
    }

    /**
     * set axis ID
     * 
     * @param axisID
     *            the axisID to set
     * @return This SweQuantity
     */
    public SweQuantity setAxisID(final String axisID) {
        this.axisID = axisID;
        return this;
    }

    @Override
    public Double getValue() {
        return value;
    }

    @Override
    public SweQuantity setValue(final Double value) {
        this.value = value;
        return this;
    }

    @Override
    public int hashCode() {
        final int prime = 97;
        int hash = 7;
        hash = prime * hash + super.hashCode();
        hash = prime * hash + (axisID != null ? axisID.hashCode() : 0);
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
        final SweQuantity other = (SweQuantity) obj;
        if ((getAxisID() == null) ? (other.getAxisID() != null) : !getAxisID().equals(other.getAxisID())) {
            return false;
        }
        return super.equals(obj);
    }

    @Override
    public String getStringValue() {
        if (isSetValue()) {
            return Double.toString(value.intValue());
        }
        return null;
    }

    @Override
    public boolean isSetValue() {
        return value != null;
    }

    public boolean isSetAxisID() {
        return axisID != null && !axisID.isEmpty();
    }

    @Override
    public SweDataComponentType getDataComponentType() {
        return SweDataComponentType.Quantity;
    }

    @Override
    public SweQuantity setUom(String uom) {
        return (SweQuantity) super.setUom(uom);
    }

    @Override
    public SweQuantity setQuality(Collection<SweQuality> quality) {
        return (SweQuantity) super.setQuality(quality);
    }
}
