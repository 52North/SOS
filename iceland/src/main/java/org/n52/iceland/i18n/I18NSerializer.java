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

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.Iterator;


public class I18NSerializer {
    private static final String TUPLE_SEPERATOR = "&";
    private static final String TOKEN_SEPERATOR = "=";
    private static final String UTF8 = "UTF-8";

    public String encode(MultilingualString string) {
        Iterator<LocalizedString> it = string.iterator();
        StringBuilder builder = new StringBuilder();
        if (it.hasNext()) {
            encode(builder, it.next());
            while (it.hasNext()) {
                builder.append(TUPLE_SEPERATOR);
                encode(builder, it.next());
            }
        }
        return builder.toString();
    }

    private StringBuilder encode(StringBuilder builder, LocalizedString loc) {
        return builder.append(LocaleHelper.toString(loc.getLang()))
                .append(TOKEN_SEPERATOR)
                .append(encodeText(loc.getText()));
    }

    public MultilingualString decode(String string) {
        MultilingualString mls = new MultilingualString();
        for (String s : string.split(TUPLE_SEPERATOR)) {
            String[] kvp = s.split(TOKEN_SEPERATOR);
            mls.addLocalization(LocaleHelper.fromString(kvp[0]), decodeText(kvp[1]));
        }
        return mls;
    }

    private static String decodeText(String text) {
        try {
            return URLDecoder.decode(text, UTF8);
        } catch (UnsupportedEncodingException ex) {
            throw new RuntimeException(ex);
        }
    }

    private static String encodeText(String text) {
        try {
            return URLEncoder.encode(text, UTF8);
        } catch (UnsupportedEncodingException ex) {
            throw new RuntimeException(ex);
        }
    }
}
