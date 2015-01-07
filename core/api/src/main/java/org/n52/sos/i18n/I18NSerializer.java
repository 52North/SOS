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
