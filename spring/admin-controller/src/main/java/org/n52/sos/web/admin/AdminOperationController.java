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

import java.io.IOException;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import org.n52.sos.ds.ConnectionProviderException;
import org.n52.sos.exception.JSONException;
import org.n52.sos.request.operator.RequestOperatorKey;
import org.n52.sos.request.operator.RequestOperatorRepository;
import org.n52.sos.service.operator.ServiceOperatorKey;
import org.n52.sos.util.JSONUtils;
import org.n52.sos.web.ControllerConstants;
import org.n52.sos.web.JSONConstants;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

/**
 * @author Christian Autermann <c.autermann@52north.org>
 * @since 4.0.0
 */
@Controller
public class AdminOperationController extends AbstractAdminController {

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

    @RequestMapping(value = ControllerConstants.Paths.ADMIN_OPERATIONS, method = RequestMethod.GET)
    public String view() throws ConnectionProviderException {
        return ControllerConstants.Views.ADMIN_OPERATIONS;
    }

    @ResponseBody
    @RequestMapping(value = ControllerConstants.Paths.ADMIN_OPERATIONS_JSON_ENDPOINT, method = RequestMethod.GET, produces = ControllerConstants.MEDIA_TYPE_APPLICATION_JSON)
    public String getAll() throws JSONException, ConnectionProviderException {
        ObjectNode json = JSONUtils.nodeFactory().objectNode();
        ArrayNode array = json.putArray(JSONConstants.OPERATIONS_KEY);
        for (RequestOperatorKey key : RequestOperatorRepository.getInstance().getAllRequestOperatorKeys()) {
            array.addObject()
                    .put(JSONConstants.SERVICE_KEY, key.getServiceOperatorKey().getService())
                    .put(JSONConstants.VERSION_KEY, key.getServiceOperatorKey().getVersion())
                    .put(JSONConstants.OPERATION_KEY, key.getOperationName())
                    .put(JSONConstants.ACTIVE_KEY, getSettingsManager().isActive(key));
        }

        return JSONUtils.print(json);
    }

    @ResponseBody
    @RequestMapping(value = ControllerConstants.Paths.ADMIN_OPERATIONS_JSON_ENDPOINT, method = RequestMethod.POST, consumes = ControllerConstants.MEDIA_TYPE_APPLICATION_JSON)
    public void change(@RequestBody String request) throws JSONException, ConnectionProviderException, IOException {
        JsonNode json = JSONUtils.loadString(request);
        ServiceOperatorKey sokt =
                new ServiceOperatorKey(json.path(JSONConstants.SERVICE_KEY).asText(),
                        json.path(JSONConstants.VERSION_KEY).asText());
        RequestOperatorKey rokt = new RequestOperatorKey(sokt, json.path(JSONConstants.OPERATION_KEY).asText());
        getSettingsManager().setActive(rokt, json.path(JSONConstants.ACTIVE_KEY).asBoolean());
    }
}
