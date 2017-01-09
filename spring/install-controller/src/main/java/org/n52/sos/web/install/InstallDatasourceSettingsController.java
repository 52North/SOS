/*
 * Copyright (C) 2012-2017 52°North Initiative for Geospatial Open Source
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
package org.n52.sos.web.install;

import java.io.File;
import java.net.URI;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.inject.Inject;
import javax.servlet.http.HttpSession;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import org.n52.iceland.config.SettingDefinition;
import org.n52.iceland.config.json.JsonSettingsEncoder;
import org.n52.iceland.ds.Datasource;
import org.n52.iceland.exception.JSONException;
import org.n52.iceland.util.JSONUtils;
import org.n52.sos.web.common.ControllerConstants;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.common.collect.Lists;

/**
 * @author Christian Autermann <c.autermann@52north.org>
 *
 * @since 4.0.0
 */
@Controller
@RequestMapping(ControllerConstants.Paths.INSTALL_DATASOURCE_DIALECTS)
public class InstallDatasourceSettingsController extends AbstractInstallController {

    @Inject
    private Collection<Datasource> datasources;

    @ResponseBody
    @RequestMapping(method = RequestMethod.GET, produces = "application/json; charset=UTF-8")
    public String get(HttpSession session) throws JSONException {
        InstallationConfiguration c = getSettings(session);
        Map<String, Datasource> datasources = getDatasources();
        return JSONUtils.print(encode(c, datasources));
    }

    private JsonNode encode(InstallationConfiguration c, Map<String, Datasource> dialects) throws JSONException {
        ObjectNode node = JSONUtils.nodeFactory().objectNode();
        JsonSettingsEncoder enc = getSettingsEncoder();
        List<String> orderedDialects = getOrderedDialects(dialects.keySet());
        for (String dialect : orderedDialects) {
            boolean selected = false;
            if (c.getDatasource() != null && c.getDatasource().getDialectName().equals(dialect)) {
                selected = true;
            }
            Datasource d = dialects.get(dialect);
            Set<SettingDefinition<?, ?>> defs = d.getSettingDefinitions();
            if (selected) {
                for (SettingDefinition<?, ?> def : defs) {
                    setDefaultValue(c, def);
                }
            }
            JsonNode settings = enc.encode(enc.sortByGroup(defs));
            ObjectNode jsonObject = node.putObject(dialect);
            jsonObject.set("settings", settings);
            jsonObject.put("needsSchema", d.needsSchema());
            jsonObject.put("selected", selected);
        }
        return node;
    }

    protected Map<String, Datasource> getDatasources() {
        Map<String, Datasource> dialects = new HashMap<>(this.datasources.size());
        for (Datasource dd : this.datasources) {
            dialects.put(dd.getDialectName(), dd);
        }
        return dialects;
    }

    @SuppressWarnings("unchecked")
    protected void setDefaultValue(InstallationConfiguration c, SettingDefinition<?, ?> def) {
        Object val = c.getDatabaseSetting(def.getKey());
        if (val != null) {
            switch (def.getType()) {
            case BOOLEAN:
                SettingDefinition<?, Boolean> bsd = (SettingDefinition<?, Boolean>) def;
                bsd.setDefaultValue((Boolean) val);
                break;
            case FILE:
                SettingDefinition<?, File> fsd = (SettingDefinition<?, File>) def;
                fsd.setDefaultValue((File) val);
                break;
            case INTEGER:
                SettingDefinition<?, Integer> isd = (SettingDefinition<?, Integer>) def;
                isd.setDefaultValue((Integer) val);
                break;
            case NUMERIC:
                SettingDefinition<?, Double> dsd = (SettingDefinition<?, Double>) def;
                dsd.setDefaultValue((Double) val);
                break;
            case STRING:
                SettingDefinition<?, String> ssd = (SettingDefinition<?, String>) def;
                ssd.setDefaultValue((String) val);
                break;
            case URI:
                SettingDefinition<?, URI> usd = (SettingDefinition<?, URI>) def;
                usd.setDefaultValue((URI) val);
                break;
            default:
                break;
            }
        }
    }

    protected List<String> getOrderedDialects(Set<String> dialectKeys) {
        List<String> orderedDialectKeys = Lists.newArrayList(dialectKeys);
        Collections.sort(orderedDialectKeys);
        return orderedDialectKeys;
    }
}
