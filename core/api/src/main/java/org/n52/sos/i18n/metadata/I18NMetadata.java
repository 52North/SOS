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

import java.util.Collection;
import java.util.Set;

import org.n52.sos.util.CollectionHelper;

import com.google.common.collect.Sets;

/**
 * Abstract I18N object class
 * 
 * @author Carsten Hollmann <c.hollmann@52north.org>
 * @since 4.1.0
 * 
 */
public abstract class I18NObject {

    private String objectIdentifier;

    private Set<I18NLanguageObject> i18nLanugageValues = Sets.newHashSet();

    /**
     * constructor
     * 
     * @param objectIdentifier
     *            The identifier of this object
     */
    public I18NObject(String objectIdentifier) {
        setObjectIdentifier(objectIdentifier);
    }

    /**
     * constructor
     * 
     * @param objectIdentifier
     *            The identifier of this object
     * @param i18NLanguageObject
     *            A language object for this identifier
     */
    public I18NObject(String objectIdentifier, I18NLanguageObject i18NLanguageObject) {
        setObjectIdentifier(objectIdentifier);
        addI18NValue(i18NLanguageObject);
    }

    /**
     * The the identifier of this object
     * 
     * @return The object identifier
     */
    public String getObjectIdentifier() {
        return objectIdentifier;
    }

    /**
     * Set the obejct identifier
     * 
     * @param objectIdentifier
     *            the identifier to set
     */
    private void setObjectIdentifier(String objectIdentifier) {
        this.objectIdentifier = objectIdentifier;
    }

    /**
     * Get the language object for the specified language
     * 
     * @param language
     *            The language to get the object for
     * @return The language object for the specified language or null if not
     *         present
     */
    public I18NLanguageObject getI18NValueFor(String language) {
        for (I18NLanguageObject value : getI18NValues()) {
            if (value.getLanguage().equals(language)) {
                return value;
            }
        }
        return null;
    }

    /**
     * Get all lanugage obejcts
     * 
     * @return All language objects
     */
    public Set<I18NLanguageObject> getI18NValues() {
        return i18nLanugageValues;
    }

    /**
     * Set language objects, clear the exiting collection
     * 
     * @param i18nLanguageObjects
     *            Language obejcts to set
     */
    public void setI18NValue(Collection<I18NLanguageObject> i18nLanguageObjects) {
        getI18NValues().clear();
        getI18NValues().addAll(i18nLanguageObjects);
    }

    /**
     * Add a language obejct
     * 
     * @param i18nLanguageObject
     *            the language obejct to add
     */
    public void addI18NValue(I18NLanguageObject i18nLanguageObject) {
        getI18NValues().add(i18nLanguageObject);
    }

    /**
     * Add the language obejcts
     * 
     * @param i18nLanguageObjects
     *            the language obejcts to add
     */
    public void addI18NValue(Collection<I18NLanguageObject> i18nLanguageObjects) {
        getI18NValues().addAll(i18nLanguageObjects);
    }

    /**
     * Check if a language object is present for the specific language
     * 
     * @param language
     *            the language to check if a language object is present
     * @return <code>true</code>, if a language object is present
     */
    public boolean hasI18NValuesFor(String language) {
        for (I18NLanguageObject value : getI18NValues()) {
            if (value.getLanguage().equals(language)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Check if this object has I18N values
     * 
     * @return <code>true</code>, if this object has I18N values
     */
    public boolean hasI18NValues() {
        return CollectionHelper.isNotEmpty(getI18NValues());
    }
}
