/*
 * Copyright (C) 2012-2020 52°North Initiative for Geospatial Open Source
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
package org.n52.sos.web.common;

import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.toSet;

import java.util.Collections;
import java.util.Map;
import java.util.Set;

import javax.inject.Inject;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import org.n52.faroe.ConfigurationError;
import org.n52.faroe.SettingDefinition;
import org.n52.faroe.SettingDefinitionGroup;
import org.n52.faroe.SettingsService;
import org.n52.iceland.exception.JSONException;
import org.n52.janmayen.Json;
import org.n52.shetland.util.CollectionHelper;
import org.n52.shetland.util.StringHelper;

import com.google.common.base.Strings;

/**
 * TODO JavaDoc
 *
 * @author <a href="mailto:c.autermann@52north.org">Christian Autermann</a>
 *
 * @since 4.0.0
 */
@Controller
public class SettingDefinitonController extends AbstractController {
    private static final SettingDefinitionGroup DEFAULT_GROUP;

    static {
        DEFAULT_GROUP = new SettingDefinitionGroup();
        DEFAULT_GROUP.setTitle("Settings");
    }

    @Inject
    private SettingsService settingsManager;

    @ResponseBody
    @RequestMapping(value = ControllerConstants.Paths.SETTING_DEFINITIONS, method = RequestMethod.GET, produces
                    = ControllerConstants.MEDIA_TYPE_APPLICATION_JSON)
    public String get(@RequestParam(value = "showAll", defaultValue = "true") boolean showAll,
                      @RequestParam(value = "only", required = false) String only,
                      @RequestParam(value = "exclude", required = false) String exclude)
            throws ConfigurationError, JSONException {
        Set<SettingDefinition<?>> defs = this.settingsManager.getSettingDefinitions();
        Map<SettingDefinitionGroup, Set<SettingDefinition<?>>> grouped;
        if (!Strings.isNullOrEmpty(only) || !Strings.isNullOrEmpty(exclude)) {
            grouped = sortByGroup(defs, false,
                    !Strings.isNullOrEmpty(only) ? StringHelper.splitToSet(only) : Collections.emptySet(),
                    !Strings.isNullOrEmpty(exclude) ? StringHelper.splitToSet(exclude) : Collections.emptySet());
        } else {
            grouped = sortByGroup(defs, showAll, Collections.emptySet(), Collections.emptySet());
        }
        return Json.print(getSettingsEncoder().encode(grouped));
    }

    protected Map<SettingDefinitionGroup, Set<SettingDefinition<?>>> sortByGroup(
            Set<SettingDefinition<?>> defs, boolean showAll, Set<String> only, Set<String> exclude) {
        return defs.stream()
                .filter(def -> checkGroup(def, showAll, only, exclude))
                .collect(groupingBy(this::getGroup, toSet()));
    }

    protected boolean checkGroup(SettingDefinition<?> def, boolean showAll, Set<String> only, Set<String> exclude) {
        if (!showAll) {
            SettingDefinitionGroup group = getGroup(def);
            if (CollectionHelper.isEmpty(only) && CollectionHelper.isEmpty(exclude)) {
                return group.isShowInDefaultSettings();
            }
            return CollectionHelper.isNotEmpty(only) ? only.contains(group.getTitle())
                    : CollectionHelper.isNotEmpty(exclude) ? !exclude.contains(group.getTitle()) : true;
        }
        return true;
    }

    protected SettingDefinitionGroup getGroup(SettingDefinition<?> def) {
        return def.getGroup(DEFAULT_GROUP);
    }

}
