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

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.n52.sos.ogc.swe.simpleType.SweAbstractSimpleType;

import com.google.common.collect.Sets;

/**
 * @author <a href="mailto:e.h.juerrens@52north.org">Eike Hinderk
 *         J&uuml;rrens</a>
 * 
 * @since 4.0.0
 */
public abstract class SweAbstractDataRecord extends SweAbstractDataComponent implements DataRecord {
    private List<SweField> fields;

    /**
     *
     */
    public SweAbstractDataRecord() {
        super();
    }

    @Override
    public List<SweField> getFields() {
        return fields;
    }

    @Override
    public SweAbstractDataRecord setFields(final List<SweField> fields) {
        this.fields = fields;
        return this;
    }

    @Override
    public SweAbstractDataRecord addField(final SweField field) {
        if (fields == null) {
            fields = new LinkedList<SweField>();
        }
        fields.add(field);
        return this;
    }

    @Override
    public boolean isSetFields() {
        return fields != null && !fields.isEmpty();
    }

    @Override
    public int getFieldIndexByIdentifier(final String fieldNameOrElementDefinition) {
        int index = 0;
        if (isSetFields()) {
            for (final SweField sweField : fields) {
                if (isElementDefinition(fieldNameOrElementDefinition, sweField)
                        || isFieldName(fieldNameOrElementDefinition, sweField)) {
                    return index;
                }
                index++;
            }
        }
        return -1;
    }

    boolean isFieldName(final String fieldNameOrElementDefinition, final SweField sweField) {
        return sweField.isSetName()
                && sweField.getName().getValue().equalsIgnoreCase(fieldNameOrElementDefinition);
    }

    boolean isElementDefinition(final String fieldNameOrElementDefinition, final SweField sweField) {
        return sweField.getElement() != null && sweField.getElement().isSetDefinition()
                && sweField.getElement().getDefinition().equalsIgnoreCase(fieldNameOrElementDefinition);
    }

    @Override
    public boolean equals(final Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final SweDataRecord other = (SweDataRecord) obj;
        if (getFields() != other.getFields() && (getFields() == null || !getFields().equals(other.getFields()))) {
            return false;
        }
        return super.equals(obj);
    }

    @Override
    public int hashCode() {
        final int prime = 42;
        int hash = 7;
        hash = prime * hash + super.hashCode();
        hash = prime * hash + (getFields() != null ? getFields().hashCode() : 0);
        return hash;
    }
    
    @SuppressWarnings("rawtypes")
    @Override
    public Set<SweAbstractSimpleType<?>> getSweAbstractSimpleTypeFromFields(Class clazz) {
        if (isSetFields()) {
            Set<SweAbstractSimpleType<?>> set = Sets.newHashSet();
            for (SweField field : getFields()) {
                SweAbstractDataComponent element = field.getElement();
                if (!element.isSetName() && field.isSetName()) {
                    element.setName(field.getName());
                }
                if (element.getClass() == clazz) {
                    set.add((SweAbstractSimpleType<?>)element);
                }
            }
            return set;
        }
        return Collections.emptySet();
    }
}
