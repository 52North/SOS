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
package org.n52.sos.web.install;

import java.io.IOException;
import java.util.Iterator;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import org.n52.faroe.ConfigurationError;
import org.n52.faroe.SettingDefinition;
import org.n52.faroe.SettingsService;
import org.n52.faroe.settings.MultilingualStringSettingDefinition;
import org.n52.janmayen.Json;
import org.n52.sos.web.common.ControllerConstants;

import com.fasterxml.jackson.databind.JsonNode;

/**
 * @since 4.0.0
 *
 */
@Controller
@RequestMapping(ControllerConstants.Paths.INSTALL_LOAD_CONFIGURATION)
public class InstallLoadSettingsController extends AbstractInstallController {

    private static final Logger LOG = LoggerFactory.getLogger(InstallLoadSettingsController.class);

    @Inject
    private SettingsService settingsManager;

    @ResponseStatus(HttpStatus.OK)
    @RequestMapping(method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
    public void post(@RequestBody String config, HttpServletRequest req) throws ConfigurationError, IOException {
        final HttpSession session = req.getSession();
        InstallationConfiguration c = getSettings(session);
        JsonNode settings = Json.loadString(config);
        Iterator<String> i = settings.fieldNames();
        while (i.hasNext()) {
            String value;
            String key = i.next();
            if (settings.path(key).isContainerNode()) {
                value = Json.print(settings.path(key));
            } else {
                value = settings.path(key).asText();
            }
            // skip null values
            if (value == null || value.equals("null")) {
                LOG.warn("Value for setting with key {} is null", key);
                continue;
            }
            SettingDefinition<?> def = settingsManager.getDefinitionByKey(key);
            if (def == null) {
                LOG.warn("No definition for setting with key {}", key);
                continue;
            }
            if (def instanceof MultilingualStringSettingDefinition) {
                c.setSetting(def, settingsManager.getSettingFactory()
                        .newMultiLingualStringSettingValue((MultilingualStringSettingDefinition) def, value));
            } else {
                c.setSetting(def, settingsManager.getSettingFactory().newSettingValue(def, value));
            }
        }
        setSettings(session, c);
    }

    @ResponseBody
    @ExceptionHandler(ConfigurationError.class)
    public String onConfigurationError(ConfigurationError e) {
        return e.getMessage();
    }
}
