/**
 * Copyright (C) 2012-2020 52°North Initiative for Geospatial Open Source
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
package org.n52.sos.ogc.sensorML;

import org.n52.sos.util.StringHelper;

/**
 * Abtract class represents SensorML 2.0 TermType
 * 
 * @author Carsten Hollmann <c.hollmann@52north.org>
 * @since 4.4.0
 *
 */
public abstract class Term {

    private String name;

    private String label;

    private String definition;

    /**
     * Classifier codeSpace href
     */
    private String codeSpace;

    private String value;

    public Term() {

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
     * @return the Classifier definition
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
     * @return the Classifier codeSpace href
     */
    public String getCodeSpace() {
        return codeSpace;
    }

    /**
     * @param codeSpace
     *            href Classifier codeSpace href
     */
    public void setCodeSpace(final String codeSpace) {
        this.codeSpace = codeSpace;
    }

    /**
     * @return the value
     */
    public String getValue() {
        return value;
    }

    /**
     * @param value
     *            the value to set
     */
    public void setValue(final String value) {
        this.value = value;
    }

    /**
     * @return <code>true</code>, if the name is set AND not empty
     */
    public boolean isSetName() {
        return StringHelper.isNotEmpty(name);
    }

    /**
     * @return <code>true</code>, if the label is set AND not empty
     */
    public boolean isSetLabel() {
        return StringHelper.isNotEmpty(label);
    }

    /**
     * @return <code>true</code>, if the codeSpace is set AND not empty
     */
    public boolean isSetCodeSpace() {
        return StringHelper.isNotEmpty(codeSpace);
    }

    /**
     * @return <code>true</code>, if the codeSpace is set AND not empty
     */
    public boolean isSetDefinition() {
        return StringHelper.isNotEmpty(definition);
    }

    /**
     * @return <code>true</code>, if the value is set AND not empty
     */
    public boolean isSetValue() {
        return StringHelper.isNotEmpty(value);
    }

}
