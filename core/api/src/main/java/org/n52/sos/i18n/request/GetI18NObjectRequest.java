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
package org.n52.sos.i18n.request;

import org.n52.sos.i18n.I18NObject;
import org.n52.sos.util.StringHelper;

/**
 * Request to get the I18N data from the datasource into the admin GUI
 * 
 * @author Carsten Hollmann <c.hollmann@52north.org>
 * @since 4.1.0
 * 
 */
public class GetI18NObjectRequest {

    private String objectIdentifier;

    private String language;

    private Class<? extends I18NObject> clazz;

    /**
     * constructor
     * 
     * @param clazz
     *            Class to get the data for
     */
    public GetI18NObjectRequest(Class<? extends I18NObject> clazz) {
        setType(clazz);
    }

    /**
     * Get the object identifier to get the information for
     * 
     * @return the objectIdentifier to get the information for
     */
    public String getObjectIdentifier() {
        return objectIdentifier;
    }

    /**
     * Set the object identifier to get the information for
     * 
     * @param objectIdentifier
     *            the objectId to set
     */
    public GetI18NObjectRequest setObjectIdentifier(String objectIdentifier) {
        this.objectIdentifier = objectIdentifier;
        return this;
    }

    /**
     * Check if the object identifier is set
     * 
     * @return <code>true</code>, if the object identifier is set
     */
    public boolean isSetObjectIdentifier() {
        return StringHelper.isNotEmpty(getObjectIdentifier());
    }

    /**
     * Get the requested language to get data for
     * 
     * @return the language to get data for
     */
    public String getLanguage() {
        return language;
    }

    /**
     * Set the language to get the information for
     * 
     * @param language
     *            the language to set
     */
    public GetI18NObjectRequest setLanguage(String language) {
        this.language = language;
        return this;
    }

    /**
     * Check if the language is set
     * 
     * @return <code>true</code>, if the language is set
     */
    public boolean isSetLanguage() {
        return StringHelper.isNotEmpty(getLanguage());
    }

    /**
     * Get the Class to indicate which table should be queried
     * 
     * @return the type
     */
    public Class<? extends I18NObject> getType() {
        return clazz;
    }

    /**
     * Set the Class to indicate which table should be queried
     * 
     * @param type
     *            the type to set
     */
    private void setType(Class<? extends I18NObject> clazz) {
        this.clazz = clazz;
    }

}
