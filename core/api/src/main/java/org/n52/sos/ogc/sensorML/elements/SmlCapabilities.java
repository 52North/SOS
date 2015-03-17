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
import org.n52.sos.ogc.swe.simpleType.SweAbstractSimpleType;

/**
 * SOS internal representation of SensorML capabilities
 * 
 * @since 4.0.0
 */
public class SmlCapabilities {

    private String name;
    private DataRecord dataRecord;

    /**
     * default constructor
     */
    public SmlCapabilities() {
        this(null, null);
    }

    /**
     * constructor
     * 
     * @param name
     *            Type
     * @param dataRecord
     *            DataRecord
     */
    public SmlCapabilities(String name, DataRecord dataRecord) {
        this.name = name;
        this.dataRecord = dataRecord;
    }

    public String getName() {
        return name;
    }

    public SmlCapabilities setName(String name) {
        this.name = name;
        return this;
    }

    /**
     * @return the dataRecord
     */
    public DataRecord getDataRecord() {
        return dataRecord;
    }

    /**
     * @param dataRecord
     *            the dataRecord to set
     * @return this
     */
    public SmlCapabilities setDataRecord(DataRecord dataRecord) {
        this.dataRecord = dataRecord;
        return this;
    }

    public boolean isSetAbstractDataRecord() {
        return getDataRecord() != null;
    }

    public boolean isSetName() {
        return name != null && !name.isEmpty();
    }
    
    @SuppressWarnings("rawtypes")
    public Set<SweAbstractSimpleType<?>> getSweAbstractSimpleTypeFromFields(Class clazz) {
        if (isSetAbstractDataRecord()) {
            return getDataRecord().getSweAbstractSimpleTypeFromFields(clazz);
        }
        return Collections.emptySet();
    }

}
