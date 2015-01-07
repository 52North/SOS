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

import org.n52.sos.ogc.gml.CodeType;
import org.n52.sos.ogc.swe.SweConstants.SweDataComponentType;

/**
 * SOS internal representation of SWE field
 * 
 * @since 4.0.0
 */
public class SweField extends SweAbstractDataComponent {

    /**
     * field element
     */
    private SweAbstractDataComponent element;

    /**
     * constructor
     * 
     * @param name
     *            Field name
     * @param element
     *            Field element
     */
    public SweField(final String name, final SweAbstractDataComponent element) {
        super();
        setName(name);
        this.element = element;
    }
    
    public SweField(final CodeType name, final SweAbstractDataComponent element) {
        super();
        setName(name);
        this.element = element;
    }

//    /**
//     * @return the name
//     */
//    public String getName() {
//        return name;
//    }
//
//    /**
//     * @param name
//     *            the name to set
//     * @return This SweField
//     */
//    public SweField setName(final String name) {
//        this.name = name;
//        return this;
//    }

    /**
     * @return the element
     */
    public SweAbstractDataComponent getElement() {
        return element;
    }

    /**
     * @param element
     *            the element to set
     * @return This SweField
     */
    public SweField setElement(final SweAbstractDataComponent element) {
        this.element = element;
        return this;
    }

    @Override
    public int hashCode() {
        final int prime = 67;
        int hash = 3;
        hash = prime * hash + super.hashCode();
        hash = prime * hash + (getName() != null ? getName().hashCode() : 0);
        hash = prime * hash + (getElement() != null ? getElement().hashCode() : 0);
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
        final SweField other = (SweField) obj;
        if ((getName() == null) ? (other.getName() != null) : !getName().equals(other.getName())) {
            return false;
        }
        if (getElement() != other.getElement() && (getElement() == null || !getElement().equals(other.getElement()))) {
            return false;
        }
        return super.equals(obj);
    }

    @Override
    public String toString() {
        return String.format("SosSweField[name=%s, element=%s]", getName(), getElement());
    }

    @Override
    public SweDataComponentType getDataComponentType() {
        return SweDataComponentType.Field;
    }
}
