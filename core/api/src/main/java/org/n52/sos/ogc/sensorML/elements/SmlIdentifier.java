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

import org.n52.sos.util.StringHelper;

/**
 * SOS internal representation of SensorML identifier
 * 
 * @since 4.0.0
 */
public class SmlIdentifier {

    private String name;

    private String definition;

    private String value;
    
    private String label;

    /**
     * constructor
     * 
     * @param name
     *            Identifier name
     * @param definition
     *            Identifier definition
     * @param value
     *            Identifier value
     */
    public SmlIdentifier(final String name, final String definition, final String value) {
        super();
        this.name = name;
        this.label = name;
        this.definition = definition;
        this.value = value;
    }
    

    /**
     * @return the Identifier name
     */
    public String getName() {
        return name;
    }

    /**
     * @param name
     *            Identifier name
     */
    public void setName(final String name) {
        this.name = name;
        this.label = name;
    }
    
    
    /**
     * @return the Identifier label
     */
    public String getLabel() {
        return name;
    }

    /**
     * @param name
     *            Identifier name
     */
    public void setLabel(final String label) {
        this.label = label;
        this.name = label;
    }

    /**
     * @return the Identifier definition
     */
    public String getDefinition() {
        return definition;
    }

    /**
     * @param definition
     *            Identifier definition
     */
    public void setDefinition(final String definition) {
        this.definition = definition;
    }

    /**
     * @return the Identifier value
     */
    public String getValue() {
        return value;
    }

    /**
     * @param value
     *            Identifier value
     */
    public void setValue(final String value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return String.format("SosSMLIdentifier [name=%s, label=%s, definition=%s, value=%s]", name, label, definition, value);
    }

    public boolean isSetName() {
        return StringHelper.isNotEmpty(name);
    }
    
    public boolean isSetLabel() {
        return StringHelper.isNotEmpty(label);
    }

    public boolean isSetValue() {
        return StringHelper.isNotEmpty(value);
    }

    public boolean isSetDefinition() {
        return StringHelper.isNotEmpty(definition);
    }
}
