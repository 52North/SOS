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
package org.n52.sos.ds.hibernate.entities.i18n;

import org.n52.sos.ds.hibernate.entities.Codespace;
import org.n52.sos.util.StringHelper;

/**
 * Insertion object for {@link AbstractFeatureI18N}
 * 
 * @author Carsten Hollmann <c.hollmann@52north.org>
 * @since 4.0.0
 * 
 */
public class I18NInsertionObject {

    private Codespace codespace;

    private String name;

    private String description;

    /**
     * Constructor
     * 
     * @param codespace
     *            Codespace the values relates to
     */
    public I18NInsertionObject(Codespace codespace) {
        setCodespace(codespace);
    }

    /**
     * Constructor
     * 
     * @param codespace
     *            Codespace the values relates to
     * @param name
     *            The language specific name
     */
    public I18NInsertionObject(Codespace codespace, String name) {
        setCodespace(codespace);
        setName(name);
    }

    /**
     * Constructor
     * 
     * @param codespace
     *            Codespace the values relates to
     * @param name
     *            The language specific name
     * @param description
     *            The language specific description
     */
    public I18NInsertionObject(Codespace codespace, String name, String description) {
        setCodespace(codespace);
        setName(name);
        setDescription(description);
    }

    /**
     * Get the Codespace
     * 
     * @return the codespace
     */
    public Codespace getCodespace() {
        return codespace;
    }

    /**
     * Set the Codespace
     * 
     * @param codespace
     *            the codespace to set
     */
    public I18NInsertionObject setCodespace(Codespace codespace) {
        this.codespace = codespace;
        return this;
    }

    /**
     * Set the language specific name
     * 
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * Get the language specific name
     * 
     * @param name
     *            the name to set
     * @return this object
     */
    public I18NInsertionObject setName(String name) {
        this.name = name;
        return this;
    }

    /**
     * Check if the language specific name is set
     * 
     * @return <code>true</code>, if the language specific name is set
     */
    public boolean isSetName() {
        return StringHelper.isNotEmpty(getName());
    }

    /**
     * Get the language specific description
     * 
     * @return the description
     */
    public String getDescription() {
        return description;
    }

    /**
     * Set the language specific description
     * 
     * @param description
     *            the description to set
     * @return this object
     */
    public I18NInsertionObject setDescription(String description) {
        this.description = description;
        return this;
    }

    /**
     * Check if the language specific description is set
     * 
     * @return <code>true</code>, if the language specific description is set
     */
    public boolean isSetDescription() {
        return StringHelper.isNotEmpty(getDescription());
    }

    /**
     * Check if the object is not empty is set
     * 
     * @return <code>true</code>, if language specific name or language specific
     *         description is set
     */
    public boolean isNotEmpty() {
        return isSetName() || isSetDescription();
    }
}
