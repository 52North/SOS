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

import java.util.Collection;
import java.util.List;

import org.n52.sos.ogc.gml.CodeType;
import org.n52.sos.ogc.swe.SweConstants.SweDataComponentType;
import org.n52.sos.util.CollectionHelper;
import org.n52.sos.util.StringHelper;

import com.google.common.base.Objects;
import com.google.common.collect.Lists;

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
     * optional: gml:name [0..*] (SweCommon 1.0.1)
     */
    private List<CodeType> names = Lists.newArrayList();

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
        if (StringHelper.isNotEmpty(label)) {
            return label;
        } else if (isSetNames()) {
            return getName().getValue();
        }
        return null;
    }

    public CodeType getName() {
        if (isSetNames()) {
            return getNames().iterator().next();
        } else if (StringHelper.isNotEmpty(label)) {
            return new CodeType(getLabel());
        }
        return null;
    }

    public List<CodeType> getNames() {
        return names;
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

    public SweAbstractDataComponent addName(final String name) {
        getNames().add(new CodeType(name));
        return this;
    }

    public SweAbstractDataComponent addName(final CodeType name) {
        getNames().add(name);
        return this;
    }

    public SweAbstractDataComponent addName(final Collection<CodeType> names) {
        getNames().addAll(names);
        return this;
    }

    public SweAbstractDataComponent setName(final String name) {
        getNames().clear();
        getNames().add(new CodeType(name));
        return this;
    }

    public SweAbstractDataComponent setName(final CodeType name) {
        getNames().clear();
        getNames().add(name);
        return this;
    }

    public SweAbstractDataComponent setName(final Collection<CodeType> names) {
        getNames().clear();
        getNames().addAll(names);
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
        return StringHelper.isNotEmpty(getLabel());
    }

    public boolean isSetName() {
        return getName() != null && getName().isSetValue();
    }

    public boolean isSetNames() {
        return CollectionHelper.isNotEmpty(getNames());
    }

    public boolean isSetIdentifier() {
        return identifier != null && !identifier.isEmpty();
    }

    public boolean isSetXml() {
        return xml != null && !xml.isEmpty();
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(31, 7, getDefinition(), getDescription(), getIdentifier());
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

    /**
     * Copies all values from this {@link SweAbstractDataComponent} to the
     * passed
     * 
     * @param copy
     *            {@link SweAbstractDataComponent} to copy values to
     * @return 
     */
    public SweAbstractDataComponent copyValueTo(SweAbstractDataComponent copy) {
        copy.setDefinition(definition);
        copy.setDescription(description);
        copy.setIdentifier(identifier);
        copy.setLabel(label);
        copy.setName(names);
        return copy;
    }

}
