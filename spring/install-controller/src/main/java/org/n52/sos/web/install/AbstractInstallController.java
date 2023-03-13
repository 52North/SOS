/*
 * Copyright (C) 2012-2023 52°North Spatial Information Research GmbH
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

import java.util.Iterator;
import java.util.Map.Entry;

import javax.inject.Inject;
import javax.servlet.http.HttpSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.n52.faroe.SettingDefinition;
import org.n52.faroe.SettingValue;
import org.n52.faroe.SettingsService;
import org.n52.sos.web.common.AbstractController;

/**
 * TODO JavaDoc
 *
 * @author Christian Autermann
 */
public class AbstractInstallController extends AbstractController {
    private static final Logger LOG = LoggerFactory.getLogger(AbstractInstallController.class);
    private static final String INSTALLATION_CONFIGURATION = "installation_configuration";

    @Inject
    private SettingsService settingsManager;

    public void setSettings(HttpSession session, InstallationConfiguration settings) {
        session.setAttribute(INSTALLATION_CONFIGURATION, settings);
    }

    public InstallationConfiguration getSettings(HttpSession s) {
        InstallationConfiguration c = (InstallationConfiguration) s
                .getAttribute(INSTALLATION_CONFIGURATION);
        if (c == null) {
            c = new InstallationConfiguration();
            // try to read default settings from existing configuration
            try {
                c.setSettings(this.settingsManager.getSettings());
                // remove null values (in case new settings have been added
                // since configuration was generated)
                Iterator<Entry<SettingDefinition<?>, SettingValue<?>>> iterator
                        = c.getSettings().entrySet().iterator();
                while (iterator.hasNext()) {
                    Entry<SettingDefinition<?>, SettingValue<?>> setting
                            = iterator.next();
                    if (setting.getValue() == null) {
                        iterator.remove();
                    }
                }
            } catch (Exception ex) {
                LOG.warn("Couldn't read existing settings", ex);
            }
            setSettings(s, c);
        }
        return c;
    }

}
