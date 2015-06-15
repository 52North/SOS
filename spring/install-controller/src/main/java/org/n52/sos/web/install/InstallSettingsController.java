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
package org.n52.sos.web.install;

import java.io.File;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import org.n52.sos.config.SettingDefinition;
import org.n52.sos.config.SettingType;
import org.n52.sos.config.SettingValue;
import org.n52.sos.config.SettingsManager;
import org.n52.sos.exception.ConfigurationException;
import org.n52.sos.web.ControllerConstants;
import org.n52.sos.web.install.InstallConstants.Step;

/**
 * @since 4.0.0
 *
 */
@Controller
@RequestMapping(ControllerConstants.Paths.INSTALL_SETTINGS)
public class InstallSettingsController extends AbstractProcessingInstallationController {
    private static final Logger LOG = LoggerFactory.getLogger(InstallSettingsController.class);

    @Override
    protected Step getStep() {
        return Step.SETTINGS;
    }

    @Override
    protected void process(Map<String, String> parameters,
                           InstallationConfiguration c)
            throws InstallationSettingsError {
        logSettings(parameters);
        SettingsManager sm = getSettingsManager(c);
        for (SettingDefinition<?, ?> def : sm.getSettingDefinitions()) {
            SettingValue<?> val = null;
            if (parameters.containsKey(def.getKey())) {
                val = createSettingValue(sm, def, parameters.get(def.getKey()), c);
            } else if (def.getType() == SettingType.BOOLEAN) {
                val = createSettingValue(sm, def, String.valueOf(false), c);
            }
            if (val == null) {
                LOG.warn("No value for setting {}. Ignoring.", def.getKey());
            } else {
                checkFileSetting(def, val, c);
                c.setSetting(def, val);
            }
        }
    }

    protected void logSettings(Map<String, String> parameters) {
        if (LOG.isDebugEnabled()) {
            StringBuilder sb = new StringBuilder();
            sb.append("Processing parameters:\n").append("{\n");
            for (Map.Entry<String, String> e : parameters.entrySet()) {
                sb.append("\t").append(e.getKey()).append(": ").append(e.getValue()).append("\n");
            }
            sb.append("}");
            LOG.debug(sb.toString());
        }
    }

    private SettingsManager getSettingsManager(InstallationConfiguration c)
            throws InstallationSettingsError {
        SettingsManager sm;
        try {
            sm = SettingsManager.getInstance();
        } catch (ConfigurationException ex) {
            throw new InstallationSettingsError(c, String.format(ErrorMessages.COULD_NOT_INSTANTIATE_SETTINGS_MANAGER,
                                                                 ex.getMessage()), ex);
        }
        return sm;
    }

    protected SettingValue<?> createSettingValue(SettingsManager sm, SettingDefinition<?, ?> def, String stringValue,
            InstallationConfiguration c) throws InstallationSettingsError {
        try {
            return sm.getSettingFactory().newSettingValue(def, stringValue);
        } catch (Exception e) {
            throw new InstallationSettingsError(c, String.format(ErrorMessages.COULD_NOT_VALIDATE_PARAMETER, def.getTitle(), stringValue));
        }
    }

    @SuppressWarnings("unchecked")
    protected void checkFileSetting(SettingDefinition<?, ?> def, SettingValue<?> val, InstallationConfiguration c)
            throws InstallationSettingsError {
        if (val.getValue() instanceof File) {
            SettingValue<File> fileSetting = (SettingValue<File>) val;
            File f = fileSetting.getValue();
            if (!f.exists() && !def.isOptional()) {
                throw new InstallationSettingsError(c, String.format(ErrorMessages.COULD_NOT_FIND_FILE,
                        f.getAbsolutePath()));
            }
        }
    }
}
