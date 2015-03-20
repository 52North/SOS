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

import java.util.Locale;
import java.util.StringTokenizer;

import org.n52.sos.request.AbstractServiceRequest;
import org.n52.sos.service.ServiceConfiguration;

import com.google.common.base.Function;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;

public class LocaleHelper {
    private static final Function<Locale, String> encoder = new Encoder();
    private static final Function<String, Locale> decoder = new Decoder();

    public static String toString(Locale locale) {
        return encoder.apply(locale);
    }

    public static Locale fromString(String locale) {
        return decoder.apply(locale);
    }

    public static Locale fromRequest(AbstractServiceRequest<?> locale) {
        return fromString(locale.getRequestedLanguage());
    }

    public static Function<Locale, String> toStringFunction() {
        return encoder;
    }

    public static Function<String, Locale> fromStringFunction() {
        return decoder;
    }

    private static class Decoder implements Function<String, Locale> {
        private final LoadingCache<String, Locale> cache = CacheBuilder
                .newBuilder().build(new CacheLoaderImpl());

        private Locale parseLocaleString(String locale) {
            StringTokenizer tokenizer = new StringTokenizer(locale, "-_ #");
            int length = tokenizer.countTokens();
            String[] tokens = new String[length];
            for (int i = 0; i < length; ++i) {
                tokens[i] = tokenizer.nextToken();
            }
            String language = (length > 0 ? tokens[0] : "");
            String country = (length > 1 ? tokens[1] : "");
            String variant = (length > 2 ? tokens[2] : "");
            return new Locale(language, country, variant);
        }

        @Override
        public Locale apply(String locale) {
            return this.cache.getUnchecked(locale);
        }

        private class CacheLoaderImpl extends CacheLoader<String, Locale> {
            @Override
            public Locale load(String locale) {
                if (locale == null || locale.isEmpty()) {
                    return ServiceConfiguration.getInstance()
                            .getDefaultLanguage();
                } else {
                    return parseLocaleString(locale.trim());
                }
            }
        }
    }

    private static class Encoder implements Function<Locale, String> {
        @Override
        public String apply(Locale input) {
            String country = input.getISO3Country();
            String language = input.getISO3Language();
            StringBuilder sb = new StringBuilder(language);
            if (!country.isEmpty()) {
                sb.append("-").append(country);
            }
            return sb.toString();
        }
    }

}
