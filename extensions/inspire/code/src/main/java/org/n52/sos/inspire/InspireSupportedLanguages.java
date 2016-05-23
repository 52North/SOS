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
package org.n52.sos.inspire;

import java.util.Collection;
import java.util.Set;

import org.n52.sos.util.CollectionHelper;

import com.google.common.collect.Sets;

/**
 * Service internal representation of INSPIRE supported languages
 * 
 * @author Carsten Hollmann <c.hollmann@52north.org>
 * @since 4.1.0
 * 
 */
public class InspireSupportedLanguages implements InspireObject {

    /* element DefaultLanguage 1..1 */
    private InspireLanguageISO6392B defaultLanguage;

    /* element SupportedLanguage 0..* */
    private Set<InspireLanguageISO6392B> supportedLanguages = Sets.newHashSet();

    /**
     * constructor
     * 
     * @param defaultLanguage
     *            the mandatory default language
     */
    public InspireSupportedLanguages(InspireLanguageISO6392B defaultLanguage) {
        setDefaultLanguage(defaultLanguage);
    }

    /**
     * Get the default language
     * 
     * @return the defaultLanguage
     */
    public InspireLanguageISO6392B getDefaultLanguage() {
        return defaultLanguage;
    }

    /**
     * Set the default language
     * 
     * @param defaultLanguage
     *            the defaultLanguage to set
     */
    private void setDefaultLanguage(InspireLanguageISO6392B defaultLanguage) {
        this.defaultLanguage = defaultLanguage;
    }

    /**
     * Get the supported languages
     * 
     * @return the supportedLanguages
     */
    public Set<InspireLanguageISO6392B> getSupportedLanguages() {
        return supportedLanguages;
    }

    /**
     * Set the supported languages, clears the existing collection
     * 
     * @param supportedLanguages
     *            the supportedLanguages to set
     */
    public void setSupportedLanguages(Collection<InspireLanguageISO6392B> supportedLanguages) {
        getSupportedLanguages().clear();
        if (CollectionHelper.isNotEmpty(supportedLanguages)) {
            getSupportedLanguages().addAll(supportedLanguages);
        }

    }

    /**
     * Add the supported language
     * 
     * @param supportedLanguage
     *            the supported language to add
     * @return this
     */
    public InspireSupportedLanguages addSupportedLanguage(InspireLanguageISO6392B supportedLanguage) {
        getSupportedLanguages().add(supportedLanguage);
        return this;
    }

    /**
     * Check if supported languages are set
     * 
     * @return <code>true</code>, if supported languages are set
     */
    public boolean isSetSupportedLanguages() {
        return CollectionHelper.isNotEmpty(getSupportedLanguages());
    }

    @Override
    public String toString() {
        return String.format("%s %n[%n defaultLanguage=%s,%n supportedLanguages=%s%n]", this.getClass()
                .getSimpleName(), getDefaultLanguage(), CollectionHelper.collectionToString(getSupportedLanguages()));
    }
}
