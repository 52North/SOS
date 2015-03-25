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
package org.n52.sos.web.admin;


import javax.servlet.UnavailableException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import org.n52.sos.exception.JSONException;
import org.n52.sos.ogc.ows.CompositeOwsException;
import org.n52.sos.ogc.ows.OwsExceptionReport;
import org.n52.sos.service.Configurator;
import org.n52.sos.util.JSONUtils;
import org.n52.sos.web.ControllerConstants;

/**
 * @since 4.0.0
 *
 */
@Controller
public class AdminReloadCacheController extends AbstractAdminController {
    private static final Logger LOG = LoggerFactory.getLogger(AdminReloadCacheController.class);

    @RequestMapping(value = ControllerConstants.Paths.ADMIN_RELOAD_CAPABILITIES_CACHE, method = RequestMethod.POST)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void reload() throws OwsExceptionReport, UnavailableException {
        checkConfiguratorAvailability();
        if (!cacheIsLoading()) {
            LOG.debug("Reloading Capabilitities Cache");
            updateCache();
        }
        // TODO display other message here because the WebUI is displaying information about successful cache update start but nothing is happening
    }

    @ResponseBody
    @RequestMapping(value = ControllerConstants.Paths.ADMIN_CACHE_LOADING, method = RequestMethod.GET, produces = "application/json; charset=UTF-8")
    public String getCacheLoadingStatus() throws JSONException, UnavailableException {
        checkConfiguratorAvailability();
        return JSONUtils.print(JSONUtils.nodeFactory().objectNode().put("loading", cacheIsLoading()));
    }

    private void checkConfiguratorAvailability() throws UnavailableException {
        if (Configurator.getInstance() == null) {
            throw new UnavailableException("configurator is not available");
        }
    }

    @ResponseBody
    @ExceptionHandler(CompositeOwsException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public String errorWhileRefreshing(CompositeOwsException e) {
        return e.getMessage();
    }

    @ResponseBody
    @ExceptionHandler(UnavailableException.class)
    @ResponseStatus(HttpStatus.SERVICE_UNAVAILABLE)
    public String noConfigurator(UnavailableException e) {
        return e.getMessage();
    }
}
