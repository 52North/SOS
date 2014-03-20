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
 * Insertion object for {@link I18NProcedure}
 * 
 * @author Carsten Hollmann <c.hollmann@52north.org>
 * @since 4.0.0
 * 
 */
public class I18NProcedureInsertionObject extends I18NInsertionObject {

    private String shortname;

    private String longname;

    /**
     * Constructor
     * 
     * @param codespace
     *            Codespace the values relates to
     */
    public I18NProcedureInsertionObject(Codespace codespace) {
        super(codespace);
    }

    /**
     * Constructor
     * 
     * @param codespace
     *            Codespace the values relates to
     * @param name
     *            The language specific name
     */
    public I18NProcedureInsertionObject(Codespace codespace, String name) {
        super(codespace, name);
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
    public I18NProcedureInsertionObject(Codespace codespace, String name, String description) {
        super(codespace, name, description);
    }

    /**
     * Get the language specific shortname
     * 
     * @return the shortname
     */
    public String getShortname() {
        return shortname;
    }

    /**
     * Set the language specific shortname
     * 
     * @param shortname
     *            the shortname to set
     */
    public void setShortname(String shortname) {
        this.shortname = shortname;
    }

    /**
     * Check if the language specific shortname is set
     * 
     * @return <code>true</code>, if the language specific shortname is set
     */
    public boolean isSetShortname() {
        return StringHelper.isNotEmpty(getShortname());
    }

    /**
     * Get the language specific longname
     * 
     * @return the longname
     */
    public String getLongname() {
        return longname;
    }

    /**
     * Set the language specific longname
     * 
     * @param longname
     *            the longname to set
     */
    public void setLongname(String longname) {
        this.longname = longname;
    }

    /**
     * Check if the language specific longname is set
     * 
     * @return <code>true</code>, if the language specific longname is set
     */
    public boolean isSetLongname() {
        return StringHelper.isNotEmpty(getLongname());
    }

    /**
     * Check if the object is not empty is set
     * 
     * @return <code>true</code>, if language specific name or language specific
     *         description or language specific shortname or language specific
     *         longname is set
     */
    public boolean isNotEmpty() {
        return super.isNotEmpty() || isSetSpecificValues();
    }

    /**
     * Check if object specific values are set
     * 
     * @return <code>true</code>, if language specific shortname or language
     *         specific longname is set
     */
    public boolean isSetSpecificValues() {
        return isSetShortname() || isSetLongname();
    }

}
