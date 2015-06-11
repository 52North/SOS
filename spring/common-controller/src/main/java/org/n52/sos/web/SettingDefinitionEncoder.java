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

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.n52.sos.config.SettingDefinition;
import org.n52.sos.config.SettingDefinitionGroup;
import org.n52.sos.config.SettingType;
import org.n52.sos.config.SettingValue;
import org.n52.sos.config.settings.ChoiceSettingDefinition;
import org.n52.sos.config.settings.IntegerSettingDefinition;
import org.n52.sos.ds.Datasource;
import org.n52.sos.exception.JSONException;
import org.n52.sos.i18n.MultilingualString;
import org.n52.sos.i18n.json.I18NJsonEncoder;
import org.n52.sos.ogc.gml.time.TimeInstant;
import org.n52.sos.util.DateTimeHelper;
import org.n52.sos.util.JSONUtils;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;


@SuppressWarnings({ "rawtypes", "unchecked" })
public class SettingDefinitionEncoder {
    private final JsonNodeFactory nodeFactory = JSONUtils.nodeFactory();

    public Map<SettingDefinitionGroup, Set<SettingDefinition>> sortByGroup(Set<SettingDefinition<?, ?>> defs) {

        Map<SettingDefinitionGroup, Set<SettingDefinition>> map = new HashMap<>();
        for (SettingDefinition def : defs) {
            SettingDefinitionGroup group = def.hasGroup() ? def.getGroup() : Datasource.ADVANCED_GROUP;
            Set<SettingDefinition> groupDefs = map.get(group);
            if (groupDefs == null) {
                groupDefs = new HashSet<>();
                map.put(group, groupDefs);
            }
            groupDefs.add(def);
        }
        return map;
    }

    public ObjectNode encode(Map<SettingDefinitionGroup, Set<SettingDefinition>> grouped) throws JSONException {
        ObjectNode json = nodeFactory.objectNode();
        ArrayNode sections = json.putArray(JSONConstants.SECTIONS_KEY);
        List<SettingDefinitionGroup> sortedGroups = new ArrayList<>(grouped.keySet());
        Collections.sort(sortedGroups);
        for (SettingDefinitionGroup group : sortedGroups) {
            ObjectNode jgroup = sections.addObject();
            jgroup.put(JSONConstants.TITLE_KEY, group.getTitle());
            jgroup.put(JSONConstants.DESCRIPTION_KEY, group.getDescription());
            jgroup.put(JSONConstants.SETTINGS_KEY, encode(grouped.get(group)));
        }
        return json;
    }

    public ObjectNode encode(Set<SettingDefinition> settings) throws JSONException {
        ObjectNode j = nodeFactory.objectNode();
        List<SettingDefinition> sorted = new ArrayList<>(settings);
        Collections.sort(sorted);
        for (SettingDefinition def : sorted) {
            j.put(def.getKey(), encode(def));
        }
        return j;
    }

    public ObjectNode encode(SettingDefinition def) throws JSONException {
        ObjectNode j = nodeFactory.objectNode();
        j.put(JSONConstants.TITLE_KEY, def.getTitle());
        j.put(JSONConstants.DESCRIPTION_KEY, def.getDescription());
        j.put(JSONConstants.TYPE_KEY, getType(def)).put(JSONConstants.REQUIRED_KEY, !def.isOptional());
        j.put(JSONConstants.DEFAULT, def.hasDefaultValue() ? encodeDefaultValue(def): null);

        if (def.getType() == SettingType.INTEGER && def instanceof IntegerSettingDefinition) {
            IntegerSettingDefinition iDef = (IntegerSettingDefinition) def;
            if (iDef.hasMinimum()) {
                j.put(JSONConstants.MINIMUM_KEY, iDef.getMinimum());
                j.put(JSONConstants.MINIMUM_EXCLUSIVE_KEY, iDef.isExclusiveMinimum());
            }
            if (iDef.hasMaximum()) {
                j.put(JSONConstants.MAXIMUM_KEY, iDef.getMaximum());
                j.put(JSONConstants.MAXIMUM_EXCLUSIVE_KEY, iDef.isExclusiveMaximum());
            }
        }
        if (def.getType() == SettingType.CHOICE && def instanceof ChoiceSettingDefinition) {
            ChoiceSettingDefinition cDef = (ChoiceSettingDefinition) def;
            ObjectNode options = j.putObject(JSONConstants.OPTIONS_KEY);
            for (Entry<String, String> option : cDef.getOptions().entrySet()) {
                options.put(option.getKey(), option.getValue());
            }
        }
        return j;
    }

    private String getType(SettingDefinition def) {
        switch (def.getType()) {
        case INTEGER:
            return JSONConstants.INTEGER_TYPE;
        case NUMERIC:
            return JSONConstants.NUMBER_TYPE;
        case BOOLEAN:
            return JSONConstants.BOOLEAN_TYPE;
        case TIMEINSTANT:
        case FILE:
        case STRING:
        case URI:
            return JSONConstants.STRING_TYPE;
        case MULTILINGUAL_STRING:
            return JSONConstants.MULTILINGUAL_TYPE;
        case CHOICE:
            return JSONConstants.CHOICE_TYPE;
        default:
            throw new IllegalArgumentException(String.format("Unknown Type %s", def.getType()));
        }
    }

    private JsonNode encodeDefaultValue(SettingDefinition def) throws JSONException {
        if (def == null) {
            return nodeFactory.nullNode();
        }
        return encodeValue(def.getType(), def.getDefaultValue());
    }

    public JsonNode encodeValue(SettingValue def) throws JSONException {
        if (def == null) {
            return nodeFactory.nullNode();
        }
        return encodeValue(def.getType(), def.getValue());
    }

    public JsonNode encodeValue(SettingType type, Object value)
            throws IllegalArgumentException {
        if (value == null) {
            return nodeFactory.nullNode();
        }
        switch (type) {
            case TIMEINSTANT:
                return nodeFactory.textNode(DateTimeHelper.format((TimeInstant) value));
            case FILE:
            case URI:
            case CHOICE:
            case STRING:
                return nodeFactory.textNode(String.valueOf(value));
            case BOOLEAN:
                return nodeFactory.booleanNode((Boolean) value);
            case INTEGER:
                return nodeFactory.numberNode((Integer) value);
            case NUMERIC:
                return nodeFactory.numberNode((Double) value);
            case MULTILINGUAL_STRING:
                return new I18NJsonEncoder().encode((MultilingualString) value);
            default:
                throw new IllegalArgumentException(String.format("Unknown Type %s", type));
        }
    }
}
