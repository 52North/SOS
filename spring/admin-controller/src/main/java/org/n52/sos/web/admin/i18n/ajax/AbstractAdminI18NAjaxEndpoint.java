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
package org.n52.sos.web.admin.i18n.ajax;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import org.n52.sos.cache.ContentCache;
import org.n52.sos.cache.ContentCacheUpdate;
import org.n52.sos.ds.I18NDAO;
import org.n52.sos.exception.JSONException;
import org.n52.sos.exception.NoSuchIdentifierException;
import org.n52.sos.exception.ows.concrete.NoImplementationFoundException;
import org.n52.sos.i18n.I18NDAORepository;
import org.n52.sos.i18n.json.I18NJsonEncoder;
import org.n52.sos.i18n.metadata.AbstractI18NMetadata;
import org.n52.sos.ogc.ows.OwsExceptionReport;
import org.n52.sos.service.Configurator;
import org.n52.sos.util.JSONUtils;
import org.n52.sos.web.AbstractController;
import org.n52.sos.web.ControllerConstants;

import com.google.common.base.Optional;

public abstract class AbstractAdminI18NAjaxEndpoint<T extends AbstractI18NMetadata> extends AbstractController {

    private static final Logger LOGGER = LoggerFactory
            .getLogger(AbstractAdminI18NAjaxEndpoint.class);
    private final I18NJsonEncoder encoder = new I18NJsonEncoder();

    @ResponseBody
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(NoImplementationFoundException.class)
    public String onError(NoImplementationFoundException e) {
        return "The operation is not supported by this SOS";
    }

    @ResponseBody
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(OwsExceptionReport.class)
    public String onError(OwsExceptionReport e) {
        return "The operation failed: " + e.getMessage();
    }

    @ResponseBody
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(JSONException.class)
    public String onError(JSONException e) {
        return "Could not decode JSON object: " + e.getMessage();
    }

    @ResponseBody
    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler(NoSuchIdentifierException.class)
    public String onError(NoSuchIdentifierException e) {
        return String.format("The identifier %s is unknown!", e.getIdentifier());
    }

    @ResponseBody
    @ResponseStatus(HttpStatus.OK)
    @RequestMapping(method = RequestMethod.GET,
                    produces = ControllerConstants.MEDIA_TYPE_APPLICATION_JSON)
    public String get()
            throws NoImplementationFoundException, JSONException,
                   OwsExceptionReport {
        Iterable<T> i18n = getDao().getMetadata();
        return JSONUtils.print(encoder.encode(i18n));
    }

    @ResponseBody
    @ResponseStatus(HttpStatus.OK)
    @RequestMapping(params = "id",
                    method = RequestMethod.GET,
                    produces = ControllerConstants.MEDIA_TYPE_APPLICATION_JSON)
    public String get(@RequestParam("id") String id)
            throws NoImplementationFoundException,
                   JSONException,
                   NoSuchIdentifierException,
                   OwsExceptionReport {
        LOGGER.debug("Getting I18N for {}", id);
        checkIdentifier(id);
        T i18n = getDao().getMetadata(id);
        if (i18n == null) {
            i18n = create(id);
        }
        return JSONUtils.print(encoder.encode(i18n));
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @RequestMapping(method = RequestMethod.POST,
                    consumes = MediaType.APPLICATION_JSON_VALUE)
    public void update(@RequestBody String json)
            throws NoImplementationFoundException,
                   JSONException,
                   NoSuchIdentifierException,
                   OwsExceptionReport,
                   IOException {

        @SuppressWarnings("unchecked")
        T i18n = (T) encoder.decodeI18NMetadata(JSONUtils.loadString(json));
        LOGGER.debug("Updating I18N for {}", i18n.getIdentifier());
        checkIdentifier(i18n.getIdentifier());
        LOGGER.debug("Saving I18N: {}", i18n);
        getDao().saveMetadata(i18n);
        ContentCacheUpdate update = getContentCacheUpdate(i18n);
        Configurator.getInstance().getCacheController().update(update);
    }

    private void checkIdentifier(String id)
            throws NoSuchIdentifierException {
        ContentCache cache = Configurator.getInstance().getCache();
        if (!isValid(cache, id)) {
            throw new NoSuchIdentifierException(id);
        }
    }

    private I18NDAO<T> getDao()
            throws NoImplementationFoundException {
        I18NDAO<T> dao = I18NDAORepository.getInstance().getDAO(getType());
        if (dao == null) {
            throw new NoImplementationFoundException(I18NDAO.class);
        }
        return dao;
    }

    protected abstract Class<T> getType();

    protected abstract boolean isValid(ContentCache cache, String id);

    protected abstract T create(String id);

    protected ContentCacheUpdate getContentCacheUpdate(final T i18n) {
        return new ContentCacheUpdate() {
            @Override public void execute() {
                // ignore no longer available locales to skip a complete update
                getCache().addSupportedLanguage(i18n.getLocales());
            }
        };
    }

}
