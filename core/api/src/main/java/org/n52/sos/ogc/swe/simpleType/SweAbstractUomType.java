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

/**
 * @author <a href="mailto:e.h.juerrens@52north.org">Eike Hinderk
 *         J&uuml;rrens</a>
 * 
 * @since 4.0.0
 */
public abstract class SweAbstractUomType<T> extends SweAbstractSimpleType<T> {

    /**
     * unit of measurement
     */
    private String uom;

    /**
     * Get unit of measurement
     * 
     * @return the uom
     */
    public String getUom() {
        return uom;
    }

    /**
     * Set unit of measurement
     * 
     * @param uom
     *            the uom to set
     * @return This SweAbstractUomType
     */
    public SweAbstractUomType<T> setUom(final String uom) {
        this.uom = uom;
        return this;
    }

    /**
     * 
     * @return <tt>true</tt>, if the uom is set and not an empty string,<br>
     *         <tt>false</tt>, else.
     */
    public boolean isSetUom() {
        return uom != null && !uom.isEmpty();
    }

    @Override
    public String toString() {
        return String.format("%s [simpleType=%s, value=%s, uom=%s, quality=%s]", getClass().getSimpleName(),
                getDataComponentType(), getValue(), getUom(), getQuality());
    }
}
