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
package org.n52.sos.i18n;

import org.n52.sos.util.StringHelper;

/**
 * Extended {@link I18NLanguageObject} for the procedure with additional
 * shortname and longname
 * 
 * @author Carsten Hollmann <c.hollmann@52north.org>
 * @since 4.1.0
 * 
 */
public class I18NProcedureLanguageObject extends I18NLanguageObject {

    private String shortname;

    private String longname;

    /**
     * constructor
     * 
     * @param language
     *            the language
     * @param name
     *            the name in the language
     */
    public I18NProcedureLanguageObject(String language, String name) {
        super(language, name);
    }

    /**
     * constructor
     * 
     * @param language
     *            the language
     * @param name
     *            the name in the language
     * @param description
     *            the description in the language
     */
    public I18NProcedureLanguageObject(String language, String name, String description) {
        super(language, name, description);
    }

    /**
     * constructor
     * 
     * @param language
     *            the language
     * @param name
     *            the name in the language
     * @param description
     *            the description in the language
     * @param shortname
     *            the shortname in the language
     * @param longname
     *            the longname in the language
     */
    public I18NProcedureLanguageObject(String language, String name, String description, String shortname,
            String longname) {
        super(language, name, description);
        setShortname(shortname);
        setLongname(longname);
    }

    /**
     * Get the shortname in the language
     * 
     * @return the shortname in the language
     */
    public String getShortname() {
        return shortname;
    }

    /**
     * * Set the shortname in the language
     * 
     * @param shortname
     *            the shortname to set
     * @return this
     */
    public I18NLanguageObject setShortname(String shortname) {
        this.shortname = shortname;
        return this;
    }

    /**
     * Check if the shortname is set
     * 
     * @return <code>true</code>, if the shortname is set
     */
    public boolean isSetShortname() {
        return StringHelper.isNotEmpty(getShortname());
    }

    /**
     * Get the longname in the language
     * 
     * @return the longname in the language
     */
    public String getLongname() {
        return longname;
    }

    /**
     * * Set the longname in the language
     * 
     * @param longname
     *            the longname to set
     * @return this
     */
    public I18NLanguageObject setLongname(String longname) {
        this.longname = longname;
        return this;
    }

    /**
     * Check if the longname is set
     * 
     * @return <code>true</code>, if the longname is set
     */
    public boolean isSetLongname() {
        return StringHelper.isNotEmpty(getLongname());
    }

}
