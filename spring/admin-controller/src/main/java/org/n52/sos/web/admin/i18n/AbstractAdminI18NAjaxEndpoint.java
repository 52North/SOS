/**
 * Copyright (C) 2012-2014 52°North Initiative for Geospatial Open Source
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
package org.n52.sos.web.admin.i18n;

import java.util.Arrays;
import java.util.Collection;
import java.util.Set;

import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;
import org.n52.sos.cache.ContentCache;
import org.n52.sos.exception.NoSuchOfferingException;
import org.n52.sos.exception.ows.concrete.NoImplementationFoundException;
import org.n52.sos.i18n.I18NDAORepository;
import org.n52.sos.i18n.I18NLanguageObject;
import org.n52.sos.i18n.I18NObject;
import org.n52.sos.i18n.request.GetI18NObjectRequest;
import org.n52.sos.ogc.ows.OwsExceptionReport;
import org.n52.sos.service.Configurator;
import org.n52.sos.util.CollectionHelper;
import org.n52.sos.web.AbstractController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import com.google.common.collect.Sets;

/**
 * @author Carsten Hollmann <c.hollmann@52north.org>
 * @since 4.1.0
 *
 */
public abstract class AbstractAdminI18NAjaxEndpoint extends AbstractController {

    private static final Logger LOGGER = LoggerFactory.getLogger(AbstractAdminI18NAjaxEndpoint.class);

    protected static final String OBJECT_ID = "objectId";

    protected static final String LANGUAGE_OBJECT = "languageObject";

    protected static final String LANGUAGE = "language";

    protected static final String NAME = "name";

    protected static final String DESCRIPTION = "description";
    
    @ResponseBody
    @ResponseStatus(HttpStatus.OK)
    @RequestMapping(method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public abstract String get() throws NoImplementationFoundException, OwsExceptionReport, JSONException;

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @RequestMapping(value="/insert", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
    public abstract void insert(@RequestBody final String i18nObjectJson) throws NoImplementationFoundException, OwsExceptionReport, JSONException, NoSuchOfferingException;


    protected String get(GetI18NObjectRequest request) throws OwsExceptionReport, JSONException {
        Collection<I18NObject> i18nObjects = I18NDAORepository.getInstance().getDAO().getI18NObjects(request);
        return toJson(i18nObjects).toString();
    }
    
    protected ContentCache getCache() {
        return Configurator.getInstance().getCache();
    }
    
    protected void check(final String identifier, final Collection<String> cache) throws NoSuchOfferingException {
        LOGGER.trace("check('{}')", identifier);
        LOGGER.trace("Valid identifier in Cache: {}",Arrays.toString(cache.toArray()));
        if (!cache.contains(identifier)) {
                throw new NoSuchOfferingException(identifier);
        }
    }
    protected JSONObject toJson(final Collection<I18NObject> i18NObjects) throws JSONException {
        final JSONObject jsonI18NObject = new JSONObject();
        if (CollectionHelper.isNotEmpty(i18NObjects)) {
            for (final I18NObject e : i18NObjects) {
                jsonI18NObject.put(e.getObjectIdentifier(), toJson(e));
            }
        }
        return jsonI18NObject;
    }

    protected JSONObject toJson(final I18NObject i18NObject) throws JSONException {
        return new JSONObject().put(OBJECT_ID, i18NObject.getObjectIdentifier()).put(LANGUAGE_OBJECT,
                toJson(i18NObject.getI18NValues()));
    }
    
    protected I18NObject toObject(final I18NObject i18NObject, final JSONObject jsonObject) {
        
        return i18NObject;
    }

    private Collection<JSONObject> toJson(Set<I18NLanguageObject> i18nValues) throws JSONException {
        final Set<JSONObject> jsonObjects = Sets.newHashSetWithExpectedSize(i18nValues.size());
        if (CollectionHelper.isNotEmpty(i18nValues)) {
            for (final I18NLanguageObject e : i18nValues) {
                jsonObjects.add(
                        new JSONObject()
                        .put(LANGUAGE, e.getLanguage())
                        .put(NAME, e.getName())
                        .put(DESCRIPTION, e.getDescription()));
            }
        }
        return jsonObjects;
    }
    
}
