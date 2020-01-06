/*
 * Copyright (C) 2012-2020 52°North Initiative for Geospatial Open Source
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
package org.n52.sos.ds;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Locale;
import java.util.Optional;

import org.n52.janmayen.i18n.LocaleHelper;
import org.n52.series.db.beans.Describable;
import org.n52.series.db.beans.DescribableEntity;
import org.n52.series.db.beans.i18n.I18nEntity;
import org.n52.shetland.ogc.gml.AbstractFeature;
import org.n52.shetland.ogc.gml.CodeType;
import org.n52.shetland.ogc.ows.exception.NoApplicableCodeException;
import org.n52.shetland.ogc.ows.exception.OwsExceptionReport;

public interface I18nNameDescriptionAdder {

    default void addNameAndDescription(DescribableEntity entity, AbstractFeature feature, Locale requestedLocale,
            Locale defaultLocale, boolean showAllLanguageValues) throws OwsExceptionReport {
        if (entity.hasTranslations()) {
            if (requestedLocale != null) {
                // specific locale was requested
                Optional<I18nEntity<? extends Describable>> translation = getTranslation(entity, requestedLocale);
                if (translation.isPresent()) {
                    if (translation.get().hasName()) {
                        feature.addName(new CodeType(translation.get().getName(),
                                URI.create(LocaleHelper.encode(requestedLocale))));
                    } else {
                        feature.addName(entity.getName());
                    }
                    if (translation.get().hasDescription()) {
                        feature.setDescription(translation.get().getDescription());
                    } else {
                        feature.setDescription(entity.getDescription());
                    }
                } else {
                    feature.addName(entity.getName());
                    feature.setDescription(entity.getDescription());
                }
            } else {
                Optional<I18nEntity<? extends Describable>> translation =
                        defaultLocale != null ? getTranslation(entity, defaultLocale) : Optional.empty();
                if (showAllLanguageValues) {
                    // load all names
                    for (I18nEntity<? extends Describable> t : entity.getTranslations()) {
                        feature.addName(new CodeType(t.getName(), URI.create(t.getLocale())));
                    }
                } else {
                    if (translation.isPresent()) {
                        if (translation.get().hasName()) {
                            feature.addName(new CodeType(translation.get().getName(),
                                    URI.create(LocaleHelper.encode(defaultLocale))));
                        } else {
                            feature.addName(entity.getName());
                        }
                    } else {
                        feature.addName(entity.getName());
                    }
                }
                // choose always the description in the default locale
                if (translation.isPresent()) {
                    if (translation.get().hasDescription()) {
                        feature.setDescription(translation.get().getDescription());
                    } else {
                        feature.setDescription(entity.getDescription());
                    }
                } else {
                    feature.setDescription(entity.getDescription());
                }
            }
        } else {
            feature.addName(entity.getName());
            feature.setDescription(entity.getDescription());
        }
    }

    default Optional<I18nEntity<? extends Describable>> getTranslation(DescribableEntity entity,
            Locale requestedLocale) {
        I18nEntity<? extends Describable> translation = entity.getTranslation(LocaleHelper.encode(requestedLocale));
        if (translation != null) {
            return Optional.of(translation);
        }
        for (Locale locale : LocaleHelper.getEquivalents(requestedLocale)) {
            I18nEntity<? extends Describable> t = entity.getTranslation(LocaleHelper.encode(locale));
            if (t != null) {
                return Optional.of(t);
            }
        }
        return Optional.empty();
    }

    default CodeType getName(DescribableEntity entity) throws OwsExceptionReport {
        if (entity.isSetName()) {
            CodeType name = new CodeType(entity.getName());
            if (entity.isSetNameCodespace()) {
                try {
                    name.setCodeSpace(new URI(entity.getNameCodespace().getName()));
                } catch (URISyntaxException e) {
                    throw new NoApplicableCodeException().causedBy(e).withMessage("Error while creating URI from '{}'",
                            entity.getNameCodespace().getName());
                }
            }
            return name;
        }
        return null;
    }

    default String getDescription(DescribableEntity entity) {
        if (entity.isSetDescription()) {
            return entity.getDescription();
        }
        return null;
    }
}
