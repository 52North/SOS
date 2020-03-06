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
package org.n52.sos.ogc.gml;

/**
 * Internal representation of the OGC GML CoordinateSystemAxis.
 * 
 * @author Carsten Hollmann <c.hollmann@52north.org>
 * @since 4.4.0
 *
 */
public class CoordinateSystemAxis extends IdentifiedObject {

    private static final long serialVersionUID = 2044040407272459804L;
    /* 1..1 */
    private CodeType axisAbbrev;
    /* 1..1 */
    private CodeWithAuthority axisDirection;
    /* 0..1 */
    private Double minimumValue;
    /* 0..1 */
    private Double maximumValue;
    /* 0..1 */
    private CodeWithAuthority rangeMeaning;
    /* 1..1 */
    private String uom;

    public CoordinateSystemAxis(CodeWithAuthority identifier, CodeType axisAbbrev, CodeWithAuthority axisDirection, String uom) {
        super(identifier);
        this.axisAbbrev = axisAbbrev;
        this.axisDirection = axisDirection;
        this.uom = uom;
    }

    /**
     * @return the axisAbbrev
     */
    public CodeType getAxisAbbrev() {
        return axisAbbrev;
    }

    /**
     * @return the axisDirection
     */
    public CodeWithAuthority getAxisDirection() {
        return axisDirection;
    }

    /**
     * @return the minimumValue
     */
    public Double getMinimumValue() {
        return minimumValue;
    }

    /**
     * @param minimumValue
     *            the minimumValue to set
     */
    public CoordinateSystemAxis setMinimumValue(Double minimumValue) {
        this.minimumValue = minimumValue;
        return this;
    }
    
    public boolean isSetMinimumValue() {
        return getMinimumValue() != null;
    }

    /**
     * @return the maximumValue
     */
    public Double getMaximumValue() {
        return maximumValue;
    }

    /**
     * @param maximumValue
     *            the maximumValue to set
     */
    public CoordinateSystemAxis setMaximumValue(Double maximumValue) {
        this.maximumValue = maximumValue;
        return this;
    }
    
    public boolean isSetMaximumValue() {
        return getMaximumValue() != null;
    }

    /**
     * @return the rangeMeaning
     */
    public CodeWithAuthority getRangeMeaning() {
        return rangeMeaning;
    }

    /**
     * @param rangeMeaning
     *            the rangeMeaning to set
     */
    public CoordinateSystemAxis setRangeMeaning(CodeWithAuthority rangeMeaning) {
        this.rangeMeaning = rangeMeaning;
        return this;
    }
    
    public boolean isSetRangeMeaning() {
        return getRangeMeaning() != null;
    }

    /**
     * @return the uom
     */
    public String getUom() {
        return uom;
    }

}
