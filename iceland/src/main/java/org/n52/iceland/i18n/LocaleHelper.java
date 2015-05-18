/**
 * Copyright 2015 52°North Initiative for Geospatial Open Source
 * Software GmbH
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.n52.iceland.i18n;

import java.util.Locale;
import java.util.StringTokenizer;

import org.n52.iceland.request.AbstractServiceRequest;
import org.n52.iceland.service.ServiceConfiguration;

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
