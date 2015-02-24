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

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.codehaus.jettison.json.JSONException;
import org.n52.sos.config.SettingDefinition;
import org.n52.sos.config.SettingDefinitionGroup;
import org.n52.sos.exception.ConfigurationException;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

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
@Controller
@SuppressWarnings({ "rawtypes", "unchecked" })
public class SettingDefinitonController extends AbstractController {

    private static final SettingDefinitionGroup DEFAULT_SETTINGS_GROUP = new SettingDefinitionGroup()
            .setTitle("Settings");

    private SettingDefinitionEncoder encoder = new SettingDefinitionEncoder();

    @ResponseBody
    @RequestMapping(value = ControllerConstants.Paths.SETTING_DEFINITIONS, method = RequestMethod.GET, produces = ControllerConstants.MEDIA_TYPE_APPLICATION_JSON)
    public String get() throws ConfigurationException, JSONException {
        Set<SettingDefinition<?, ?>> defs = getSettingsManager().getSettingDefinitions();
        Map<SettingDefinitionGroup, Set<SettingDefinition>> grouped = sortByGroup(defs);
        return getEncoder().encode(grouped).toString(4);
    }

    protected Map<SettingDefinitionGroup, Set<SettingDefinition>> sortByGroup(Set<SettingDefinition<?, ?>> defs) {
        Map<SettingDefinitionGroup, Set<SettingDefinition>> map =
                new HashMap<SettingDefinitionGroup, Set<SettingDefinition>>();
        for (SettingDefinition def : defs) {
            SettingDefinitionGroup group = def.hasGroup() ? def.getGroup() : DEFAULT_SETTINGS_GROUP;
            Set<SettingDefinition> groupDefs = map.get(group);
            if (groupDefs == null) {
                groupDefs = new HashSet<SettingDefinition>();
                map.put(group, groupDefs);
            }
            groupDefs.add(def);
        }
        return map;
    }

    protected SettingDefinitionEncoder getEncoder() {
        return encoder;
    }

}
