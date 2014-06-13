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
 * - Apache License, version 2.0
 * - Apache Software License, version 1.0
 * - GNU Lesser General Public License, version 3
 * - Mozilla Public License, versions 1.0, 1.1 and 2.0
 * - Common Development and Distribution License (CDDL), version 1.0
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
package org.n52.sos.web;

import java.util.Locale;

import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;

import org.n52.sos.i18n.LocaleHelper;
import org.n52.sos.i18n.LocalizedString;
import org.n52.sos.i18n.MultilingualString;
import org.n52.sos.i18n.metadata.AbstractI18NMetadata;
import org.n52.sos.i18n.metadata.I18NFeatureMetadata;
import org.n52.sos.i18n.metadata.I18NObservablePropertyMetadata;
import org.n52.sos.i18n.metadata.I18NOfferingMetadata;
import org.n52.sos.i18n.metadata.I18NProcedureMetadata;

public class I18NJsonEncoder {

    private static final String DESCRIPTION = "description";
    private static final String NAME = "name";
    private static final String ID = "id";
    private static final String LONG_NAME = "longName";
    private static final String SHORT_NAME = "shortName";
    private static final String TYPE = "type";
    private static final String TYPE_FEATURE = "feature";
    private static final String TYPE_OFFERING = "offering";
    private static final String TYPE_PROCEDURE = "procedure";
    private static final String TYPE_OBSERVABLE_PROPERTY = "observableProperty";
    private static final String I18N = "i18n";

    private JSONObject encodeInternal(AbstractI18NMetadata i18n)
            throws JSONException {
        return new JSONObject()
                .put(ID, i18n.getIdentifier())
                .put(NAME, encodeMultilingualString(i18n.getName()))
                .put(DESCRIPTION, encodeMultilingualString(i18n.getDescription()));
    }

    public JSONObject encodeMultilingualString(MultilingualString mls)
            throws JSONException {
        JSONObject json = new JSONObject();
        for (LocalizedString ls : mls) {
            json.put(LocaleHelper.toString(ls.getLang()), ls.getText());
        }
        return json;
    }

    public JSONObject encode(I18NFeatureMetadata i18n)
            throws JSONException {
        return encodeInternal(i18n).put(TYPE, TYPE_FEATURE);
    }

    public JSONObject encode(I18NObservablePropertyMetadata i18n)
            throws JSONException {
        return encodeInternal(i18n).put(TYPE, TYPE_OBSERVABLE_PROPERTY);
    }

    public JSONObject encode(I18NOfferingMetadata i18n)
            throws JSONException {
        return encodeInternal(i18n).put(TYPE, TYPE_OFFERING);
    }

    public JSONObject encode(I18NProcedureMetadata i18n)
            throws JSONException {
        return encodeInternal(i18n)
                .put(SHORT_NAME, i18n.getShortName())
                .put(LONG_NAME, i18n.getLongName())
                .put(TYPE, TYPE_PROCEDURE);
    }

    public JSONObject encode(Iterable<? extends AbstractI18NMetadata> i18ns)
            throws JSONException {
        JSONArray array = new JSONArray();
        for (AbstractI18NMetadata i18n : i18ns) {
            array.put(encode(i18n));
        }
        return new JSONObject().put(I18N, array);
    }

    public JSONObject encode(AbstractI18NMetadata i18n)
            throws JSONException {
        if (i18n instanceof I18NFeatureMetadata) {
            return encode((I18NFeatureMetadata) i18n);
        } else if (i18n instanceof I18NOfferingMetadata) {
            return encode((I18NOfferingMetadata) i18n);
        } else if (i18n instanceof I18NProcedureMetadata) {
            return encode((I18NProcedureMetadata) i18n);
        } else if (i18n instanceof I18NObservablePropertyMetadata) {
            return encode((I18NObservablePropertyMetadata) i18n);
        } else {
            throw new JSONException("Unknown type: " + i18n);
        }
    }

    public MultilingualString decodeMultilingualString(JSONObject json)
            throws JSONException {
        MultilingualString mls = new MultilingualString();
        decodeMultilingualString(json, mls);
        return mls;
    }

    private void decodeMultilingualString(JSONObject json,
                                          MultilingualString mls)
            throws JSONException {
        JSONArray keys = json.names();
        int length = keys.length();
        for (int i = 0; i < length; ++i) {
            String key = keys.getString(i);
            Locale locale = LocaleHelper.fromString(key);
            mls.addLocalization(locale, json.getString(key));
        }
    }

    public AbstractI18NMetadata decode(JSONObject s)
            throws JSONException {
        String type = s.getString(TYPE);
        String id = s.getString(ID);
        final AbstractI18NMetadata i18n;
        if (type.equals(TYPE_FEATURE)) {
            i18n = new I18NFeatureMetadata(id);
        } else if (type.equals(TYPE_OBSERVABLE_PROPERTY)) {
            i18n = new I18NObservablePropertyMetadata(id);
        } else if (type.equals(TYPE_OFFERING)) {
            i18n = new I18NOfferingMetadata(id);
        } else if (type.equals(TYPE_PROCEDURE)) {
            I18NProcedureMetadata pi18n = new I18NProcedureMetadata(id);
            decodeMultilingualString(s.optJSONObject(LONG_NAME), pi18n
                                     .getLongName());
            decodeMultilingualString(s.optJSONObject(SHORT_NAME), pi18n
                                     .getShortName());
            i18n = pi18n;
        } else {
            throw new JSONException("Unknown type: " + type);
        }
        decodeMultilingualString(s.optJSONObject(NAME), i18n.getName());
        decodeMultilingualString(s.optJSONObject(DESCRIPTION), i18n
                                 .getDescription());
        return i18n;
    }

}
