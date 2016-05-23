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
package org.n52.sos.i18n.json;

import org.n52.sos.exception.JSONException;

import java.util.Iterator;
import java.util.Locale;
import java.util.Map.Entry;

import org.n52.sos.i18n.LocaleHelper;
import org.n52.sos.i18n.LocalizedString;
import org.n52.sos.i18n.MultilingualString;
import org.n52.sos.i18n.metadata.AbstractI18NMetadata;
import org.n52.sos.i18n.metadata.I18NFeatureMetadata;
import org.n52.sos.i18n.metadata.I18NObservablePropertyMetadata;
import org.n52.sos.i18n.metadata.I18NOfferingMetadata;
import org.n52.sos.i18n.metadata.I18NProcedureMetadata;
import org.n52.sos.util.JSONUtils;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;

/**
 * TODO JavaDoc
 *
 * @author Christian Autermann
 */
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
    private final JsonNodeFactory nodeFactory = JSONUtils.nodeFactory();

    private ObjectNode encodeInternal(AbstractI18NMetadata i18n) {
        ObjectNode node = nodeFactory.objectNode();
        node.put(ID, i18n.getIdentifier());
        node.put(NAME, encode(i18n.getName()));
        node.put(DESCRIPTION, encode(i18n.getDescription()));
        return node;
    }

    public ObjectNode encode(MultilingualString mls) {
        ObjectNode json = nodeFactory.objectNode();
        if (mls != null) {
            for (LocalizedString ls : mls) {
                json.put(LocaleHelper.toString(ls.getLang()), ls.getText());
            }
        }
        return json;
    }

    public ObjectNode encode(I18NFeatureMetadata i18n) {
        return encodeInternal(i18n).put(TYPE, TYPE_FEATURE);
    }

    public ObjectNode encode(I18NObservablePropertyMetadata i18n) {
        return encodeInternal(i18n).put(TYPE, TYPE_OBSERVABLE_PROPERTY);
    }

    public ObjectNode encode(I18NOfferingMetadata i18n) {
        return encodeInternal(i18n).put(TYPE, TYPE_OFFERING);
    }

    public ObjectNode encode(I18NProcedureMetadata i18n) {
        ObjectNode node = encodeInternal(i18n);
        node.put(SHORT_NAME, encode(i18n.getShortName()));
        node.put(LONG_NAME, encode(i18n.getLongName()));
        node.put(TYPE, TYPE_PROCEDURE);
        return node;
    }

    public ObjectNode encode(Iterable<? extends AbstractI18NMetadata> i18ns)
            throws JSONException {
        ObjectNode node = nodeFactory.objectNode();
        ArrayNode array = node.putArray(I18N);
        for (AbstractI18NMetadata i18n : i18ns) {
            array.add(I18NJsonEncoder.this.encode(i18n));
        }
        return node;
    }

    public ObjectNode encode(AbstractI18NMetadata i18n)
            throws JSONException {
        if (i18n instanceof I18NFeatureMetadata) {
            return I18NJsonEncoder.this
                    .encode((I18NFeatureMetadata) i18n);
        } else if (i18n instanceof I18NOfferingMetadata) {
            return I18NJsonEncoder.this
                    .encode((I18NOfferingMetadata) i18n);
        } else if (i18n instanceof I18NProcedureMetadata) {
            return I18NJsonEncoder.this
                    .encode((I18NProcedureMetadata) i18n);
        } else if (i18n instanceof I18NObservablePropertyMetadata) {
            return I18NJsonEncoder.this
                    .encode((I18NObservablePropertyMetadata) i18n);
        } else {
            throw new JSONException("Unknown type: " + i18n);
        }
    }

    public MultilingualString decodeMultilingualString(JsonNode json) {
        MultilingualString mls = new MultilingualString();
        decodeMultilingualString(json, mls);
        return mls;
    }

    private void decodeMultilingualString(JsonNode json,
                                          MultilingualString mls) {
        Iterator<Entry<String, JsonNode>> it = json.fields();
        while (it.hasNext()) {
            Entry<String, JsonNode> e = it.next();
            Locale locale = LocaleHelper.fromString(e.getKey());
            mls.addLocalization(locale, e.getValue().asText());
        }
    }

    public AbstractI18NMetadata decodeI18NMetadata(JsonNode s)
            throws JSONException {
        String type = s.path(TYPE).asText();
        String id = s.path(ID).asText();
        final AbstractI18NMetadata i18n;
        if (type.equals(TYPE_FEATURE)) {
            i18n = new I18NFeatureMetadata(id);
        } else if (type.equals(TYPE_OBSERVABLE_PROPERTY)) {
            i18n = new I18NObservablePropertyMetadata(id);
        } else if (type.equals(TYPE_OFFERING)) {
            i18n = new I18NOfferingMetadata(id);
        } else if (type.equals(TYPE_PROCEDURE)) {
            I18NProcedureMetadata pi18n = new I18NProcedureMetadata(id);
            decodeMultilingualString(s.path(LONG_NAME), pi18n.getLongName());
            decodeMultilingualString(s.path(SHORT_NAME), pi18n.getShortName());
            i18n = pi18n;
        } else {
            throw new JSONException("Unknown type: " + type);
        }
        decodeMultilingualString(s.path(NAME), i18n.getName());
        decodeMultilingualString(s.path(DESCRIPTION), i18n.getDescription());
        return i18n;
    }
}
