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
package org.n52.sos.ogc.om;

import org.n52.sos.util.StringHelper;

/**
 * class represents a phenomenon of an observation
 * 
 * @since 4.0.0
 */
public class OmObservableProperty extends AbstractPhenomenon {
    /**
     * serial number
     */
    private static final long serialVersionUID = -1820718860701876580L;

    /** unit of the values of the phenomenons observations */
    private String unit;

    /** valueType in the database of the phenomenons observation values */
    private String valueType;

    /**
     * constructor
     * 
     * @param identifier
     *            observableProperty identifier
     */
    public OmObservableProperty(String identifier) {
        super(identifier);
    }

    /**
     * constructor
     * 
     * @param identifier
     *            id of the observableProperty
     * @param description
     *            description of the observableProperty
     * @param unit
     *            unit of the observation values according to this
     *            observableProperty
     * @param valueType
     *            database valType of the observation values according to this
     *            observableProperty
     */
    public OmObservableProperty(String identifier, String description, String unit, String valueType) {
        super(identifier, description);
        this.unit = unit;
        this.valueType = valueType;
    }

    /**
     * Get unit of measurement
     * 
     * @return Returns the unit.
     */
    public String getUnit() {
        return unit;
    }

    /**
     * Set unit of measurement
     * 
     * @param unit
     *            The unit to set.
     */
    public void setUnit(String unit) {
        this.unit = unit;
    }

    /**
     * Get value type
     * 
     * @return Returns the valueType.
     */
    public String getValueType() {
        return valueType;
    }

    /**
     * Set value type
     * 
     * @param valueType
     *            The valueType to set.
     */
    public void setValueType(String valueType) {
        this.valueType = valueType;
    }

    /**
     * Check whether unit of measure is set
     * 
     * @return <code>true</code>, if unit of measure is set
     */
    public boolean isSetUnit() {
        return StringHelper.isNotEmpty(getUnit());
    }
}
