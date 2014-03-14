/**
 * Copyright (C) 2012-2014 52°North Initiative for Geospatial Open Source
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

import org.n52.sos.ogc.swe.SweConstants.SweDataComponentType;

/**
 * @since 4.0.0
 * 
 */
public abstract class SweAbstractDataComponent {

    private String definition;

    /**
     * optional: swe:description[0..1]
     */
    private String description;

    /**
     * optional: swe:label [0..1]
     */
    private String label;

    /**
     * optional: swe:identifier [0..1]
     */
    private String identifier;

    /**
     * pre-set XML representation
     */
    private String xml;

    public String getDefinition() {
        return definition;
    }

    public String getDescription() {
        return description;
    }

    public String getLabel() {
        return label;
    }

    public String getIdentifier() {
        return identifier;
    }

    public SweAbstractDataComponent setDefinition(final String definition) {
        this.definition = definition;
        return this;
    }

    public SweAbstractDataComponent setDescription(final String description) {
        this.description = description;
        return this;
    }

    public SweAbstractDataComponent setLabel(final String label) {
        this.label = label;
        return this;
    }

    public SweAbstractDataComponent setIdentifier(final String identifier) {
        this.identifier = identifier;
        return this;
    }

    public String getXml() {
        return xml;
    }

    public SweAbstractDataComponent setXml(final String xml) {
        this.xml = xml;
        return this;
    }

    public boolean isSetDefinition() {
        return definition != null && !definition.isEmpty();
    }

    public boolean isSetDescription() {
        return description != null && !description.isEmpty();
    }

    public boolean isSetLabel() {
        return label != null && !label.isEmpty();
    }

    public boolean isSetIdentifier() {
        return identifier != null && !identifier.isEmpty();
    }

    public boolean isSetXml() {
        return xml != null && !xml.isEmpty();
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int hash = 7;
        hash = prime * hash + (getDefinition() != null ? getDefinition().hashCode() : 0);
        hash = prime * hash + (getDescription() != null ? getDescription().hashCode() : 0);
        hash = prime * hash + (getIdentifier() != null ? getIdentifier().hashCode() : 0);
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
        final SweAbstractDataComponent other = (SweAbstractDataComponent) obj;
        if ((getDefinition() == null) ? (other.getDefinition() != null) : !getDefinition().equals(
                other.getDefinition())) {
            return false;
        }
        if ((getDescription() == null) ? (other.getDescription() != null) : !getDescription().equals(
                other.getDescription())) {
            return false;
        }
        if ((getIdentifier() == null) ? (other.getIdentifier() != null) : !getIdentifier().equals(
                other.getIdentifier())) {
            return false;
        }
        return true;
    }

    public abstract SweDataComponentType getDataComponentType();

}
