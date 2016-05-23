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
package org.n52.sos.web;

import java.io.File;
import java.net.URI;
import java.util.Map;
import java.util.Map.Entry;
import java.util.SortedMap;
import java.util.TreeMap;

import javax.servlet.ServletContext;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import org.n52.sos.config.SettingDefinition;
import org.n52.sos.config.SettingValue;
import org.n52.sos.config.SettingsManager;
import org.n52.sos.exception.ConfigurationException;
import org.n52.sos.exception.JSONException;
import org.n52.sos.i18n.MultilingualString;
import org.n52.sos.i18n.json.I18NJsonEncoder;
import org.n52.sos.ogc.gml.time.Time;
import org.n52.sos.service.DatabaseSettingsHandler;
import org.n52.sos.util.DateTimeHelper;
import org.n52.sos.util.JSONUtils;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

/**
 * @since 4.0.0
 *
 */
@Controller
public class AbstractController {

    @Autowired
    private ServletContext context;

    private SettingsManager sm;

    public ServletContext getContext() {
        return this.context;
    }

    public void setContext(ServletContext context) {
        this.context = context;
    }

    public String getBasePath() {
        return getContext().getRealPath("/");
    }

    public MetaDataHandler getMetaDataHandler() {
        return MetaDataHandler.getInstance(getContext());
    }

    public DatabaseSettingsHandler getDatabaseSettingsHandler() {
        return DatabaseSettingsHandler.getInstance(getContext());
    }

    protected Boolean parseBoolean(Map<String, String> parameters, String name) {
        return parseBoolean(parameters.get(name));
    }

    protected Boolean parseBoolean(String s) {
        if (s != null && !s.trim().isEmpty()) {
            s = s.trim();
            if (s.equals("true") || s.equals("yes") || s.equals("on")) {
                return Boolean.TRUE;
            }
            if (s.equals("false") || s.equals("no") || s.equals("off")) {
                return Boolean.FALSE;
            }
        }
        return Boolean.FALSE;
    }

    protected SettingsManager getSettingsManager() throws ConfigurationException {
        return (sm == null) ? sm = SettingsManager.getInstance() : sm;
    }

    protected ObjectNode toJSONValueMap(Map<SettingDefinition<?, ?>, SettingValue<?>> settings)
            throws ConfigurationException, JSONException {
        SortedMap<String, JsonNode> map = new TreeMap<String, JsonNode>();
        SettingDefinitionEncoder encoder = new SettingDefinitionEncoder();
        for (Entry<SettingDefinition<?, ?>, SettingValue<?>> e : settings.entrySet()) {
        	if (e.getValue() != null) {
        		map.put(e.getKey().getKey(), encoder.encodeValue(e.getValue()));
        	} else {
        		map.put(e.getKey().getKey(), encoder.encodeValue(e.getKey().getType(), e.getKey().getDefaultValue()));
        	}
        }
        ObjectNode node = JSONUtils.nodeFactory().objectNode();
        node.putAll(map);
        return node;
    }

    private Object encodeValue(SettingValue<?> v) {
        if (v == null || v.getValue() == null) {
            return null;
        }
        switch (v.getType()) {
        case INTEGER:
        case NUMERIC:
        case STRING:
        case BOOLEAN:
            return v.getValue();
        case FILE:
            return ((File) v.getValue()).getPath();
        case URI:
            return ((URI) v.getValue()).toString();
        case TIMEINSTANT:
             return DateTimeHelper.format((Time)v.getValue());
        case MULTILINGUAL_STRING:
            return JSONUtils.print(new I18NJsonEncoder().encode((MultilingualString)v.getValue()));
        default:
            throw new IllegalArgumentException(String.format("Type %s is not supported!", v.getType()));
        }
    }
}
