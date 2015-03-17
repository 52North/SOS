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
package org.n52.sos.util;

import org.n52.sos.i18n.LocaleHelper;

import java.util.Locale;
import java.util.Map;

import org.n52.sos.cache.ContentCache;
import org.n52.sos.i18n.LocalizedString;
import org.n52.sos.i18n.MultilingualString;
import org.n52.sos.ogc.sos.SosOffering;
import org.n52.sos.request.AbstractServiceRequest;
import org.n52.sos.service.Configurator;
import org.n52.sos.service.ServiceConfiguration;

import com.google.common.base.Optional;

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
        addOfferingNames(sosOffering, LocaleHelper.fromString(request.getRequestedLanguage()));
    }

    /**
     * Add offering names to {@link SosOffering} for the specific language or
     * the configured default language
     *
     * @param offering
     *            {@link SosOffering} to add names
     * @param requestedLocale
     *            the specific language
     */
    public static void addOfferingNames(SosOffering offering, Locale requestedLocale) {
        String identifier = offering.getIdentifier();
        Locale defaultLanguage = getDefaultLanguage();
        if (requestedLocale != null && getCache().hasI18NNamesForOffering(identifier, requestedLocale)) {
            offering.addName(getCache().getI18nNameForOffering(identifier, requestedLocale).asCodeType());
        } else {
            if (ServiceConfiguration.getInstance().isShowAllLanguageValues()) {
                MultilingualString names = getCache().getI18nNamesForOffering(identifier);
                if (names != null) {
                    for (LocalizedString name : names) {
                        offering.addName(name.asCodeType());
                    }
                }
            } else {
                LocalizedString name = getCache()
                        .getI18nNameForOffering(identifier, defaultLanguage);
                if (name != null) {
                    offering.addName(name.asCodeType());
                }
            }
        }
        if (!offering.isSetName()) {
            offering.addName(getCache().getNameForOffering(identifier));
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
        addOfferingDescription(sosOffering, LocaleHelper.fromString(request.getRequestedLanguage()));
    }

    /**
     * Add offering description to {@link SosOffering} for the specific language
     * or the configured default language
     *
     * @param offering
     *            {@link SosOffering} to add description
     * @param locale
     *            the specific language
     */
    public static void addOfferingDescription(SosOffering offering, Locale locale) {
        MultilingualString descriptions = getCache()
                .getI18nDescriptionsForOffering(offering.getIdentifier());
        if (descriptions != null) {
            Optional<LocalizedString> description = descriptions
                    .getLocalizationOrDefault(locale);
            if (description.isPresent()) {
                offering.setDescription(description.get().getText());
            }
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
    protected static Locale getDefaultLanguage() {
        return ServiceConfiguration.getInstance().getDefaultLanguage();
    }

    /**
     * private constructor
     */
    private I18NHelper() {

    }

}
