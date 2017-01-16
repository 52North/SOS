/*
 * Copyright (C) 2012-2017 52°North Initiative for Geospatial Open Source
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

import java.net.URISyntaxException;
import java.util.Locale;

import org.n52.shetland.ogc.ows.service.OwsServiceRequest;
import org.n52.iceland.service.ServiceConfiguration;
import org.n52.janmayen.i18n.LocaleHelper;
import org.n52.janmayen.i18n.LocalizedString;
import org.n52.janmayen.i18n.MultilingualString;
import org.n52.shetland.ogc.ows.exception.CodedException;
import org.n52.shetland.ogc.ows.exception.NoApplicableCodeException;
import org.n52.sos.cache.SosContentCache;
import org.n52.shetland.ogc.sos.SosOffering;
import org.n52.sos.service.Configurator;

import com.google.common.base.Optional;

/**
 * Helper class for I18N support
 *
 * @author <a href="mailto:c.hollmann@52north.org">Carsten Hollmann</a>
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
     * @throws CodedException
     */
    @Deprecated
    public static void addOfferingNames(SosOffering sosOffering, OwsServiceRequest request) throws CodedException {
        addOfferingNames(sosOffering, LocaleHelper.decode(request.getRequestedLanguage()));
    }

    public static void addOfferingNames(SosOffering sosOffering, OwsServiceRequest request,SosContentCache cache, Locale defaultLocale, boolean showAllLanguages) throws CodedException {
        addOfferingNames(cache, sosOffering, LocaleHelper.decode(request.getRequestedLanguage()), defaultLocale, showAllLanguages);
    }

    /**
     * Add offering names to {@link SosOffering} for the specific language or
     * the configured default language
     *
     * @param offering
     *            {@link SosOffering} to add names
     * @param requestedLocale
     *            the specific language
     * @throws CodedException
     */
    @Deprecated
    public static void addOfferingNames(SosOffering offering, Locale requestedLocale) throws CodedException {
        addOfferingNames(getCache(), offering, requestedLocale, getDefaultLanguage(),
                        isShowAllLanguageValues());
    }

    public static void addOfferingNames(SosContentCache cache, SosOffering offering, Locale requestedLocale, Locale defaultLocale, boolean showAllLanguages) throws CodedException {
        String identifier = offering.getIdentifier();
        try {
            if (requestedLocale != null && cache.hasI18NNamesForOffering(identifier, requestedLocale)) {
                offering.addName(cache.getI18nNameForOffering(identifier, requestedLocale).asCodeType());
            } else {
                if (showAllLanguages) {
                    MultilingualString names = cache.getI18nNamesForOffering(identifier);
                    if (names != null) {
                        for (LocalizedString name : names) {
                            offering.addName(name.asCodeType());
                        }
                    }
                } else {
                    LocalizedString name = cache.getI18nNameForOffering(identifier, defaultLocale);
                    if (name != null) {
                        offering.addName(name.asCodeType());
                    }
                }
            }
            if (!offering.isSetName()) {
                offering.addName(cache.getNameForOffering(identifier));
            }
        } catch (URISyntaxException urise) {
            throw new NoApplicableCodeException().causedBy(urise).withMessage("Error while creating offering name!");
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
    @Deprecated
    public static void addOfferingDescription(SosOffering sosOffering, OwsServiceRequest request) {
        addOfferingDescription(sosOffering, LocaleHelper.decode(request.getRequestedLanguage()));
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
    @Deprecated
    public static void addOfferingDescription(SosOffering offering, Locale locale) {
        addOfferingDescription(offering, locale, getDefaultLanguage(), getCache());
    }

    public static void addOfferingDescription(SosOffering offering, Locale locale, Locale defaultLocale, SosContentCache cache) {
        MultilingualString descriptions = cache
                .getI18nDescriptionsForOffering(offering.getIdentifier());
        if (descriptions != null) {
            Optional<LocalizedString> description = descriptions
                    .getLocalizationOrDefault(locale, defaultLocale);
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
    @Deprecated
    protected static SosContentCache getCache() {
        return Configurator.getInstance().getCache();
    }

    /**
     * Get the configure default language
     *
     * @return Default language
     */
    @Deprecated
    protected static Locale getDefaultLanguage() {
        return ServiceConfiguration.getInstance().getDefaultLanguage();
    }

    @Deprecated
    protected static boolean isShowAllLanguageValues() {
        return ServiceConfiguration.getInstance().isShowAllLanguageValues();
    }

    /**
     * private constructor
     */
    private I18NHelper() {

    }

}
