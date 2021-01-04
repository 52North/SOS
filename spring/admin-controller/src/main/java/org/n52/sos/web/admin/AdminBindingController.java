/*
 * Copyright (C) 2012-2021 52°North Initiative for Geospatial Open Source
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

import java.io.IOException;
import java.util.Map;
import java.util.Map.Entry;

import javax.inject.Inject;

import org.n52.iceland.binding.Binding;
import org.n52.iceland.binding.BindingKey;
import org.n52.iceland.binding.BindingRepository;
import org.n52.iceland.binding.MediaTypeBindingKey;
import org.n52.iceland.binding.PathBindingKey;
import org.n52.iceland.ds.ConnectionProviderException;
import org.n52.iceland.exception.JSONException;
import org.n52.janmayen.Json;
import org.n52.janmayen.http.MediaType;
import org.n52.sos.web.common.ControllerConstants;
import org.n52.sos.web.common.JSONConstants;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

/**
 * @author <a href="mailto:c.autermann@52north.org">Christian Autermann</a>
 * @since 4.0.0
 */
@Controller
public class AdminBindingController extends AbstractAdminController {

    @Inject
    private BindingRepository bindingRepository;

    @ResponseBody
    @ExceptionHandler(JSONException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public String onJSONException(JSONException e) {
        return e.getMessage();
    }

    @ResponseBody
    @ExceptionHandler(ConnectionProviderException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public String onConnectionProviderException(ConnectionProviderException e) {
        return e.getMessage();
    }

    @RequestMapping(value = ControllerConstants.Paths.ADMIN_BINDINGS, method = RequestMethod.GET)
    public String view() {
        return ControllerConstants.Views.ADMIN_BINDINGS;
    }

    @ResponseBody
    @RequestMapping(value = ControllerConstants.Paths.ADMIN_BINDINGS_JSON_ENDPOINT,
                    method = RequestMethod.GET,
                    produces = ControllerConstants.MEDIA_TYPE_APPLICATION_JSON)
    public String getAll() {
        ObjectNode node = Json.nodeFactory().objectNode();
        node.set(JSONConstants.BINDINGS_KEY, getBindings());
        return Json.print(node);
    }

    protected ArrayNode getBindings() {
        Map<MediaType, Binding> bindings = bindingRepository.getAllBindingsByMediaType();
        ArrayNode a = Json.nodeFactory().arrayNode();
        for (Entry<MediaType, Binding> e : bindings.entrySet()) {
            MediaType mediaType = e.getKey();
            a.addObject().put(JSONConstants.BINDING_KEY, mediaType.toString()).put(JSONConstants.ACTIVE_KEY,
                    this.bindingRepository.isActive(new MediaTypeBindingKey(mediaType)));
        }
        return a;
    }

    @ResponseBody
    @RequestMapping(value = ControllerConstants.Paths.ADMIN_BINDINGS_JSON_ENDPOINT,
                    method = RequestMethod.POST,
                    consumes = ControllerConstants.MEDIA_TYPE_APPLICATION_JSON)
    public void change(@RequestBody String request) throws IOException {
        JsonNode json = Json.loadString(request);
        if (json.has(JSONConstants.BINDING_KEY)) {
            BindingKey key = getKey(json.path(JSONConstants.BINDING_KEY).asText());
            this.bindingRepository.setActive(key, json.path(JSONConstants.ACTIVE_KEY).asBoolean());
        } else {
            throw new JSONException("Invalid JSON");
        }
    }

    private BindingKey getKey(String keyString) {
        try {
            return new MediaTypeBindingKey(MediaType.parse(keyString));
        } catch (IllegalArgumentException e) {
            // nothing to do!!!
        }
        return new PathBindingKey(keyString);
    }
}
