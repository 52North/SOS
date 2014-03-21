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
package org.n52.sos.util;

import java.util.Map;

import org.n52.sos.cache.ContentCache;
import org.n52.sos.ogc.sos.SosOffering;
import org.n52.sos.request.AbstractServiceRequest;
import org.n52.sos.service.Configurator;
import org.n52.sos.service.ServiceConfiguration;

/**
 * Helper class for I18N support
 * 
 * @author Carsten Hollmann <c.hollmann@52north.org>
 * @since 4.1.0
 * 
 */
public class I18NHelper {

    /**
     * Add offering names to {@link SosOffering} for the requested language
     * 
     * @param sosOffering
     *            {@link SosOffering} to add names
     * @param request
     *            Request with language
     */
    public static void addOfferingNames(SosOffering sosOffering, AbstractServiceRequest<?> request) {
        addOfferingNames(sosOffering, request.getRequestedLanguage());
    }

    /**
     * Add offering names to {@link SosOffering} for the specific language or
     * the configured default language
     * 
     * @param offering
     *            {@link SosOffering} to add names
     * @param i18n
     *            the specific language
     */
    public static void addOfferingNames(SosOffering offering, String i18n) {
        if (StringHelper.isNotEmpty(i18n) && getCache().hasI18NNamesForOffering(offering.getIdentifier(), i18n)) {
            offering.addName(getCache().getI18nNameForOffering(offering.getIdentifier(), i18n), i18n);
        } else {
            if (ServiceConfiguration.getInstance().isShowAllLanguageValues()) {
                Map<String, String> i18nNames = getCache().getI18nNamesForOffering(offering.getIdentifier());
                if (i18nNames != null) {
                    for (String codespace : i18nNames.keySet()) {
                        offering.addName(i18nNames.get(codespace), codespace);
                    }
                }
            } else {
                offering.addName(getCache().getI18nNameForOffering(offering.getIdentifier(), getDefaultLanguage()),
                        getDefaultLanguage());
            }
        }
        if (!offering.isSetName()) {
            offering.addName(getCache().getNameForOffering(offering.getIdentifier()));
        }
    }

    /**
     * Add offering description to {@link SosOffering} for the requested
     * language
     * 
     * @param sosOffering
     *            {@link SosOffering} to add description
     * @param request
     *            Request with language
     */
    public static void addOfferingDescription(SosOffering sosOffering, AbstractServiceRequest<?> request) {
        addOfferingDescription(sosOffering, request.getRequestedLanguage());
    }

    /**
     * Add offering description to {@link SosOffering} for the specific language
     * or the configured default language
     * 
     * @param offering
     *            {@link SosOffering} to add description
     * @param i18n
     *            the specific language
     */
    public static void addOfferingDescription(SosOffering sosOffering, String i18n) {
        if (StringHelper.isNotEmpty(i18n)
                && getCache().hasI18NDescriptionForOffering(sosOffering.getIdentifier(), i18n)) {
            sosOffering.setDescription(getCache().getI18nDescriptionForOffering(sosOffering.getIdentifier(), i18n));
        }
        if (!sosOffering.isSetDescription()) {
            sosOffering.setDescription(getCache().getI18nDescriptionForOffering(sosOffering.getIdentifier(),
                    getDefaultLanguage()));
        }
    }

    /**
     * Get the current cache
     * 
     * @return Current cache
     */
    protected static ContentCache getCache() {
        return Configurator.getInstance().getCache();
    }

    /**
     * Get the configure default language
     * 
     * @return Default language
     */
    protected static String getDefaultLanguage() {
        return ServiceConfiguration.getInstance().getDefaultLanguage();
    }

    /**
     * private constructor
     */
    private I18NHelper() {

    }

}
