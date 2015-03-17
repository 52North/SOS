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
package org.n52.sos.ogc.swe;

import org.n52.sos.ogc.swe.simpleType.SweAbstractSimpleType;
import org.n52.sos.ogc.swe.simpleType.SweAbstractUomType;

/**
 * SOS internal representation of SWE coordinates
 * 
 * @param <T>
 * @since 4.0.0
 */
public class SweCoordinate<T> {

    /**
     * Coordinate name
     */
    private String name;

    /**
     * Coordinate value TODO is this assignment to generic? maybe, we switch to
     * {@link SweAbstractUomType}?
     */
    private SweAbstractSimpleType<T> value;

    /**
     * constructor
     * 
     * @param name
     *            Coordinate name
     * @param value
     *            Coordinate value
     */
    public SweCoordinate(final String name, final SweAbstractSimpleType<T> value) {
        super();
        this.name = name;
        this.value = value;
    }

    /**
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * @param name
     *            the name to set
     */
    public void setName(final String name) {
        this.name = name;
    }

    /**
     * @return the value
     */
    public SweAbstractSimpleType<T> getValue() {
        return value;
    }

    /**
     * @param value
     *            the value to set
     */
    public void setValue(final SweAbstractSimpleType<T> value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return String.format("SosSweCoordinate[name=%s, value=%s]", getName(), getValue());
    }
}
