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
package org.n52.sos.web.admin;

import javax.inject.Inject;

import org.n52.faroe.ConfigurationError;
import org.n52.faroe.SettingsService;
import org.n52.iceland.cache.ContentCacheController;
import org.n52.iceland.cache.ContentCachePersistenceStrategy;
import org.n52.iceland.ds.ConnectionProviderException;
import org.n52.sos.context.ContextSwitcher;
import org.n52.sos.web.common.ControllerConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.View;
import org.springframework.web.servlet.view.RedirectView;

/**
 * @since 4.0.0
 *
 */
@Controller
@RequestMapping(ControllerConstants.Paths.ADMIN_RESET)
public class AdminResetController extends AbstractReloadContextController {
    private static final Logger LOG = LoggerFactory.getLogger(AdminResetController.class);

    @Inject
    private ContextSwitcher contextSwitcher;

    @Inject
    private SettingsService settingsManager;

    @Inject
    private ContentCacheController contentCacheController;

    @RequestMapping(method = RequestMethod.GET)
    public String get() {
        return ControllerConstants.Views.ADMIN_RESET;
    }

    @RequestMapping(method = RequestMethod.POST)
    public View post() throws ConfigurationError, ConnectionProviderException {
        LOG.debug("Resetting Service.");

        getDatabaseSettingsHandler().delete();
        this.settingsManager.deleteAll();

        ContentCachePersistenceStrategy persistenceStrategy
                = contentCacheController.getContentCachePersistenceStrategy();

        // delete a cache file if present
        if (persistenceStrategy != null) {
            persistenceStrategy.remove();
        }

        reloadContext();

        return new RedirectView(ControllerConstants.Paths.ROOT, true);
    }
}
