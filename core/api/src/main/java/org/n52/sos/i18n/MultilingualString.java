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
package org.n52.sos.i18n;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.n52.sos.service.ServiceConfiguration;

import com.google.common.base.Objects;
import com.google.common.base.Objects.ToStringHelper;
import com.google.common.base.Optional;
import com.google.common.collect.Maps;

public class MultilingualString implements Iterable<LocalizedString>, Serializable {
    private static final long serialVersionUID = -1120455418520277338L;
    private final Map<Locale, LocalizedString> localizations = Maps.newHashMap();

    public MultilingualString addLocalization(Locale lang, String value) {
        return addLocalization(new LocalizedString(lang, value));
    }

    public MultilingualString addLocalization(LocalizedString value) {
        this.localizations.put(value.getLang(), value);
        return this;
    }

    public Optional<LocalizedString> getLocalization(Locale lang) {
        return Optional.fromNullable(getLocalizations().get(lang));
    }

    public Optional<LocalizedString> getLocalizationOrDefault(Locale lang) {
        Optional<LocalizedString> localization = getLocalization(lang);
        if (localization.isPresent()) {
            return localization;
        }
        return getDefaultLocalization();
    }

    public Optional<LocalizedString> getDefaultLocalization() {
        return getLocalization(getDefaultLocale());
    }

     public MultilingualString filter(Locale locale) {
        if (locale == null) {
            return isShowAllLocales() ? this : only(getDefaultLocale());
        } else {
            return hasLocale(locale) ? only(locale) : only(getDefaultLocale());
        }
    }

    public Set<Locale> getLocales() {
        return Collections.unmodifiableSet(getLocalizations().keySet());
    }

    public boolean hasLocale(Locale locale) {
        return getLocales().contains(locale);
    }

    @Override
    public Iterator<LocalizedString> iterator() {
        return getLocalizations().values().iterator();
    }

    public int size() {
        return getLocalizations().size();
    }

    public boolean isEmpty() {
        return getLocalizations().isEmpty();
    }

    @Override
    public String toString() {
        ToStringHelper h = Objects.toStringHelper(this).omitNullValues();
        for (Entry<Locale, LocalizedString> e : getLocalizations().entrySet()) {
            h.add(e.getKey().toString(), e.getValue().getText());
        }
        return h.toString();
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getLocalizations());
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof MultilingualString) {
            MultilingualString that = (MultilingualString) obj;
            return Objects.equal(this.getLocalizations(),
                                 that.getLocalizations());
        }
        return false;
    }

    private Map<Locale, LocalizedString> getLocalizations() {
        return Collections.unmodifiableMap(this.localizations);
    }

    public MultilingualString only(Locale... locale) {
        return only(Arrays.asList(locale));
    }

    public MultilingualString only(Iterable<Locale> locales) {
        MultilingualString mls = new MultilingualString();
        for (Locale locale : locales) {
            Optional<LocalizedString> localization = getLocalization(locale);
            if (localization.isPresent()) {
                mls.addLocalization(localization.get());
            }
        }
        return mls;
    }

    protected boolean isShowAllLocales() {
        return ServiceConfiguration.getInstance().isShowAllLanguageValues();
    }

    protected Locale getDefaultLocale() {
        return ServiceConfiguration.getInstance().getDefaultLanguage();
    }
}
