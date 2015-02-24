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
import java.util.Set;

import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;
import org.n52.sos.config.SettingDefinition;
import org.n52.sos.config.SettingDefinitionGroup;
import org.n52.sos.config.SettingType;
import org.n52.sos.config.settings.IntegerSettingDefinition;
import org.n52.sos.ds.Datasource;

/**
 * TODO JavaDoc
 * 
 * @author Christian Autermann <c.autermann@52north.org>
 * 
 * @since 4.0.0
 */
/*
 * this class contains unnecessary raw types as the OpenJDK 1.6.0 comiler will
 * fail on wrong incompatiple type errors
 */
@SuppressWarnings({ "rawtypes", "unchecked" })
public class SettingDefinitionEncoder {
    public Map<SettingDefinitionGroup, Set<SettingDefinition>> sortByGroup(Set<SettingDefinition<?, ?>> defs) {
        Map<SettingDefinitionGroup, Set<SettingDefinition>> map =
                new HashMap<SettingDefinitionGroup, Set<SettingDefinition>>();
        for (SettingDefinition def : defs) {
            SettingDefinitionGroup group = def.hasGroup() ? def.getGroup() : Datasource.ADVANCED_GROUP;
            Set<SettingDefinition> groupDefs = map.get(group);
            if (groupDefs == null) {
                groupDefs = new HashSet<SettingDefinition>();
                map.put(group, groupDefs);
            }
            groupDefs.add(def);
        }
        return map;
    }

    public JSONObject encode(Map<SettingDefinitionGroup, Set<SettingDefinition>> grouped) throws JSONException {
        JSONArray sections = new JSONArray();
        List<SettingDefinitionGroup> sortedGroups = new ArrayList<SettingDefinitionGroup>(grouped.keySet());
        Collections.sort(sortedGroups);
        for (SettingDefinitionGroup group : sortedGroups) {
            sections.put(new JSONObject().put(JSONConstants.TITLE_KEY, group.getTitle())
                    .put(JSONConstants.DESCRIPTION_KEY, group.getDescription())
                    .put(JSONConstants.SETTINGS_KEY, encode(grouped.get(group))));
        }
        return new JSONObject().put(JSONConstants.SECTIONS_KEY, sections);
    }

    public JSONObject encode(Set<SettingDefinition> settings) throws JSONException {
        JSONObject j = new JSONObject();
        List<SettingDefinition> sorted = new ArrayList<SettingDefinition>(settings);
        Collections.sort(sorted);
        for (SettingDefinition def : sorted) {
            j.put(def.getKey(), encode(def));
        }
        return j;
    }

    public JSONObject encode(SettingDefinition def) throws JSONException {
        JSONObject j =
                new JSONObject().put(JSONConstants.TITLE_KEY, def.getTitle())
                        .put(JSONConstants.DESCRIPTION_KEY, def.getDescription())
                        .put(JSONConstants.TYPE_KEY, getType(def)).put(JSONConstants.REQUIRED_KEY, !def.isOptional())
                        .put(JSONConstants.DEFAULT, def.hasDefaultValue() ? encodeValue(def) : null);

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
        case FILE:
        case STRING:
        case URI:
            return JSONConstants.STRING_TYPE;
        default:
            throw new IllegalArgumentException(String.format("Unknown Type %s", def.getType()));
        }
    }

    private Object encodeValue(SettingDefinition def) {
        switch (def.getType()) {
        case FILE:
        case URI:
            return def.getDefaultValue().toString();
        case BOOLEAN:
        case INTEGER:
        case NUMERIC:
        case STRING:
            return def.getDefaultValue();
        default:
            throw new IllegalArgumentException(String.format("Unknown Type %s", def.getType()));
        }
    }
}
