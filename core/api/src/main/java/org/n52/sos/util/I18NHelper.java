/*
 * Copyright (C) 2012-2023 52°North Spatial Information Research GmbH
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

import java.net.URI;
import java.util.Locale;
import java.util.Optional;
import java.util.stream.Stream;

import org.n52.janmayen.i18n.LocaleHelper;
import org.n52.janmayen.i18n.LocalizedString;
import org.n52.janmayen.i18n.MultilingualString;
import org.n52.shetland.ogc.gml.CodeType;
import org.n52.shetland.ogc.sos.SosOffering;
import org.n52.sos.cache.SosContentCache;

/**
 * Helper interface for I18N support
 *
 * @author <a href="mailto:c.hollmann@52north.org">Carsten Hollmann</a>
 * @since 4.1.0
 *
 */
public interface I18NHelper {

    default void addOfferingNames(SosContentCache cache, SosOffering offering, Locale requestedLocale,
            Locale defaultLocale, boolean showAllLanguages) {
        String identifier = offering.getIdentifier();

        if (requestedLocale != null) {
            checkForNames(cache, offering, requestedLocale);
        } else {
            if (showAllLanguages) {
                Optional.ofNullable(cache.getI18nNamesForOffering(identifier)).map(MultilingualString::stream)
                        .orElseGet(Stream::empty).map(CodeType::new).forEach(offering::addName);
            } else {
                Optional.ofNullable(cache.getI18nNameForOffering(identifier, defaultLocale)).map(CodeType::new)
                        .ifPresent(offering::addName);
            }
        }
        if (!offering.isSetName()) {
            offering.addName(cache.getNameForOffering(identifier));
        }
    }

    default void checkForNames(SosContentCache cache, SosOffering offering, Locale requestedLocale) {
        if (!checkForLocaleName(cache, offering, requestedLocale, requestedLocale)) {
            for (Locale locale : LocaleHelper.getEquivalents(requestedLocale)) {
                if (!checkForLocaleName(cache, offering, locale, requestedLocale)) {
                    continue;
                }
            }
        }
    }

    default boolean checkForLocaleName(SosContentCache cache, SosOffering offering, Locale locale,
            Locale requestedLocale) {
        if (cache.hasI18NNamesForOffering(offering.getIdentifier(), locale)) {
            LocalizedString i18nNameForOffering = cache.getI18nNameForOffering(offering.getIdentifier(), locale);
            offering.setName(
                    new CodeType(i18nNameForOffering.getText(), URI.create(LocaleHelper.encode(requestedLocale))));
            return true;
        }
        return false;
    }

    default void addOfferingDescription(SosOffering offering, Locale locale, Locale defaultLocale,
            SosContentCache cache) {
        if (!checkForLocaleDescription(cache, offering, locale)) {
            for (Locale l : LocaleHelper.getEquivalents(locale)) {
                if (!checkForLocaleDescription(cache, offering, l)) {
                    continue;
                }
            }
        }
        // Optional.ofNullable(cache.getI18nDescriptionsForOffering(offering.getIdentifier()))
        // .flatMap(d -> d.getLocalizationOrDefault(locale,
        // defaultLocale)).map(LocalizedString::getText)
        // .ifPresent(offering::setDescription);
    }

    default boolean checkForLocaleDescription(SosContentCache cache, SosOffering offering, Locale locale) {
        if (cache.hasI18NDescriptionForOffering(offering.getIdentifier(), locale)) {
            LocalizedString i18nDescriptionForOffering =
                    cache.getI18nDescriptionForOffering(offering.getIdentifier(), locale);
            offering.setDescription(i18nDescriptionForOffering.getText());
            return true;
        }
        return false;
    }

}
