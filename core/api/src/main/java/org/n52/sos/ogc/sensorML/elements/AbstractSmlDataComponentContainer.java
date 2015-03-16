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
package org.n52.sos.ogc.sensorML.elements;

import java.util.Collections;
import java.util.Set;

import org.n52.sos.ogc.swe.DataRecord;
import org.n52.sos.ogc.swe.SweAbstractDataComponent;
import org.n52.sos.ogc.swe.SweField;
import org.n52.sos.ogc.swe.SweSimpleDataRecord;
import org.n52.sos.ogc.swe.simpleType.SweAbstractSimpleType;
import org.n52.sos.util.CollectionHelper;

import com.google.common.collect.Sets;

/**
 * Abstract container class for SensorML data components.
 * 
 * @author Carsten Hollmann <c.hollmann@52north.org>
 * @since 4.0.0
 *
 * @param <T> Implemented class
 */
public class AbstractSmlDataComponentContainer<T> {

    private String name;

    private String typeDefinition;

    private DataRecord dataRecord;

    private Set<SweAbstractDataComponent> abstractDataComponents = Sets.newHashSet();

    public AbstractSmlDataComponentContainer() {
    }

    public AbstractSmlDataComponentContainer(DataRecord dataRecord) {
        this.dataRecord = dataRecord;
    }

    public AbstractSmlDataComponentContainer(Set<SweAbstractDataComponent> abstractDataComponents) {
        this.abstractDataComponents = abstractDataComponents;
    }

    public String getName() {
        return name;
    }

    @SuppressWarnings("unchecked")
    public T setName(String name) {
        this.name = name;
        return (T) this;
    }

    public boolean isSetName() {
        return name != null && !name.isEmpty();
    }

    /**
     * @return the typeDefinition
     */
    public String getTypeDefinition() {
        return typeDefinition;
    }

    /**
     * @param typeDefinition
     *            the typeDefinition to set
     */
    public void setTypeDefinition(String typeDefinition) {
        this.typeDefinition = typeDefinition;
    }

    public boolean isSetTypeDefinition() {
        return typeDefinition != null && !typeDefinition.isEmpty();
    }

    /**
     * @return the dataRecord
     */
    public DataRecord getDataRecord() {
        if (!isSetAbstractDataRecord() && isSetDataComponents()) {
            SweSimpleDataRecord sdr = new SweSimpleDataRecord();
            int counter = 1;
            for (SweAbstractDataComponent element : abstractDataComponents) {
                String name = "field_" + counter++;
                if (element.isSetName()) {
                    name = element.getName().getValue();
                }
                SweField field = new SweField(name, element);
                sdr.addField(field);
            }
            return sdr;
        }
        return dataRecord;
    }

    /**
     * @param dataRecord
     *            the dataRecord to set
     */
    @SuppressWarnings("unchecked")
    public T setDataRecord(DataRecord dataRecord) {
        this.dataRecord = dataRecord;
        return (T) this;
    }

    public boolean isSetAbstractDataRecord() {
        return isSetDataRecord() || isSetDataComponents();
    }

    private boolean isSetDataRecord() {
        return dataRecord != null;
    }

    public Set<SweAbstractDataComponent> getAbstractDataComponents() {
        if (!isSetDataComponents() && isSetAbstractDataRecord()) {
            Set<SweAbstractDataComponent> components = Sets.newHashSet();
            for (SweField field : getDataRecord().getFields()) {
                components.add(field.getElement());
            }
            return components;
        }
        return abstractDataComponents;
    }

    @SuppressWarnings("unchecked")
    public T setAbstractDataComponents(Set<SweAbstractDataComponent> abstractDataComponents) {
        this.abstractDataComponents = abstractDataComponents;
        return (T) this;
    }

    @SuppressWarnings("unchecked")
    public T addAbstractDataComponents(Set<SweAbstractDataComponent> abstractDataComponents) {
        this.abstractDataComponents.addAll(abstractDataComponents);
        return (T) this;
    }

    @SuppressWarnings("unchecked")
    public T addAbstractDataComponents(SweAbstractDataComponent abstractDataComponent) {
        this.abstractDataComponents.add(abstractDataComponent);
        return (T) this;
    }

    public boolean isSetAbstractDataComponents() {
        return isSetDataComponents() || isSetDataRecord();
    }

    private boolean isSetDataComponents() {
        return CollectionHelper.isNotEmpty(abstractDataComponents);
    }

    @SuppressWarnings("rawtypes")
    public Set<SweAbstractSimpleType<?>> getSweAbstractSimpleTypeFromFields(Class clazz) {
        if (isSetAbstractDataRecord()) {
            return getDataRecord().getSweAbstractSimpleTypeFromFields(clazz);
        }
        return Collections.emptySet();
    }
}
