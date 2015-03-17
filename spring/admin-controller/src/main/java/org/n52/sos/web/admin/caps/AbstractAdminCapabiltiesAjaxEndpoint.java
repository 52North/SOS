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
package org.n52.sos.web.admin.caps;

import org.apache.xmlbeans.XmlException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import org.n52.sos.cache.ContentCache;
import org.n52.sos.config.CapabilitiesExtensionManager;
import org.n52.sos.exception.ConfigurationException;
import org.n52.sos.exception.JSONException;
import org.n52.sos.exception.NoSuchExtensionException;
import org.n52.sos.exception.NoSuchOfferingException;
import org.n52.sos.ogc.ows.OwsExceptionReport;
import org.n52.sos.service.Configurator;
import org.n52.sos.web.AbstractController;

public class AbstractAdminCapabiltiesAjaxEndpoint extends AbstractController {
    private static final Logger log = LoggerFactory.getLogger(AbstractAdminCapabiltiesAjaxEndpoint.class);
    protected static final String OFFERING = "offeringId";
    protected static final String IDENTIFIER = "identifier";
    protected static final String DISABLED_PROPERTY = "disabled";
    protected static final String EXTENSION_PROPERTY = "extensionContent";
    protected static final String IDENTIFIER_PROPERTY = "identifier";
    protected static final String ERRORS_PROPERTY = "errors";
    protected static final String VALID_PROPERTY = "valid";

    protected CapabilitiesExtensionManager getDao() {
        return getSettingsManager();
    }

    protected ContentCache getCache() {
        return Configurator.getInstance().getCache();
    }

    @ResponseBody
    @ExceptionHandler(XmlException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public String error(final XmlException e) {
        return e.getMessage();
    }

    @ResponseBody
    @ExceptionHandler(NoSuchOfferingException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public String error(final NoSuchOfferingException e) {
        return String.format("Offering '%s' not found!", e.getIdentifier());
    }

    @ResponseBody
    @ExceptionHandler(NoSuchExtensionException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public String error(final NoSuchExtensionException e) {
        return String.format("Extension '%s' not found!", e.getIdentifier());
    }


    @ResponseBody
    @ExceptionHandler(JSONException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public String error(final JSONException e) {
        return e.getMessage();
    }

    @ResponseBody
    @ExceptionHandler(ConfigurationException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public String error(final ConfigurationException e) {
        return e.getMessage();
    }

    @ResponseBody
    @ExceptionHandler(OwsExceptionReport.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public String error(final OwsExceptionReport e) {
        return e.getMessage();
    }

    protected String getSelectedStaticCapabilities() throws OwsExceptionReport {
        return getSettingsManager().getActiveStaticCapabilities();
    }

    protected void setSelectedStaticCapabilities(String id) throws ConfigurationException,
                                                                   OwsExceptionReport,
                                                                   NoSuchExtensionException {
        final String current = getSelectedStaticCapabilities();
        id = (id == null || id.trim().isEmpty()) ? null : id;
        boolean change = false;
        if (current == null) {
            if (id == null) {
                log.debug("Staying with dynamic capabilities.");
            } else {
                log.debug("Choosing static capabilities '{}'", id);
                change = true;
            }
        } else if (id == null) {
            log.debug("Reverting to dynamic capabilities.");
            change = true;
        } else {
            log.debug("Switching static capabilities from '{}' to '{}'", current, id);
            change = true;
        }

        if (change) {
            getDao().setActiveStaticCapabilities(id);
        }
    }

    protected void showDynamicCapabilities() throws ConfigurationException, OwsExceptionReport, NoSuchExtensionException {
        setSelectedStaticCapabilities(null);
    }
}
