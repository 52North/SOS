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
import java.util.Set;

import javax.inject.Inject;

import org.n52.iceland.coding.encode.ResponseFormatKey;
import org.n52.iceland.ds.ConnectionProviderException;
import org.n52.iceland.exception.JSONException;
import org.n52.janmayen.Json;
import org.n52.shetland.ogc.ows.service.OwsServiceKey;
import org.n52.sos.coding.encode.ProcedureDescriptionFormatKey;
import org.n52.sos.coding.encode.ProcedureDescriptionFormatRepository;
import org.n52.sos.coding.encode.ResponseFormatRepository;
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
public class AdminEncodingController extends AbstractAdminController {

    @Inject
    private ResponseFormatRepository responseFormatRepository;

    @Inject
    private ProcedureDescriptionFormatRepository procedureDescriptionFormatRepository;

    @ResponseBody
    @ExceptionHandler(ConnectionProviderException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public String onConnectionProviderException(ConnectionProviderException e) {
        return e.getMessage();
    }

    @RequestMapping(value = ControllerConstants.Paths.ADMIN_ENCODINGS, method = RequestMethod.GET)
    public String view() {
        return ControllerConstants.Views.ADMIN_ENCODINGS;
    }

    @ResponseBody
    @RequestMapping(value = ControllerConstants.Paths.ADMIN_ENCODINGS_JSON_ENDPOINT,
                    method = RequestMethod.GET,
                    produces = ControllerConstants.MEDIA_TYPE_APPLICATION_JSON)
    public String getAll() {
        ObjectNode node = Json.nodeFactory().objectNode();
        node.set(JSONConstants.OBSERVATION_ENCODINGS_KEY, getObservationEncodings());
        node.set(JSONConstants.PROCEDURE_ENCODINGS_KEY, getProcedureEncodings());
        return Json.print(node);
    }

    @ResponseBody
    @RequestMapping(value = ControllerConstants.Paths.ADMIN_ENCODINGS_JSON_ENDPOINT,
                    method = RequestMethod.POST,
                    consumes = ControllerConstants.MEDIA_TYPE_APPLICATION_JSON)
    public void change(@RequestBody String request) throws IOException {
        JsonNode json = Json.loadString(request);

        if (json.has(JSONConstants.RESPONSE_FORMAT_KEY)) {
            OwsServiceKey sokt = new OwsServiceKey(json.path(JSONConstants.SERVICE_KEY).asText(),
                    json.path(JSONConstants.VERSION_KEY).asText());
            ResponseFormatKey rfkt =
                    new ResponseFormatKey(sokt, json.path(JSONConstants.RESPONSE_FORMAT_KEY).asText());
            this.responseFormatRepository.setActive(rfkt, json.path(JSONConstants.ACTIVE_KEY).asBoolean());
        } else if (json.has(JSONConstants.PROCEDURE_DESCRIPTION_FORMAT_KEY)) {
            OwsServiceKey sokt = new OwsServiceKey(json.path(JSONConstants.SERVICE_KEY).asText(),
                    json.path(JSONConstants.VERSION_KEY).asText());
            ProcedureDescriptionFormatKey pdfkt = new ProcedureDescriptionFormatKey(sokt,
                    json.path(JSONConstants.PROCEDURE_DESCRIPTION_FORMAT_KEY).asText());
            this.procedureDescriptionFormatRepository.setActive(pdfkt,
                    json.path(JSONConstants.ACTIVE_KEY).asBoolean());
        } else {
            throw new JSONException("Invalid JSON");
        }
    }

    protected ArrayNode getObservationEncodings() {
        ArrayNode joes = Json.nodeFactory().arrayNode();
        final Map<OwsServiceKey, Set<String>> oes = this.responseFormatRepository.getAllSupportedResponseFormats();
        for (Entry<OwsServiceKey, Set<String>> entry : oes.entrySet()) {
            for (String responseFormat : entry.getValue()) {
                ResponseFormatKey rfkt = new ResponseFormatKey(entry.getKey(), responseFormat);
                joes.addObject().put(JSONConstants.SERVICE_KEY, rfkt.getService())
                        .put(JSONConstants.VERSION_KEY, rfkt.getVersion())
                        .put(JSONConstants.RESPONSE_FORMAT_KEY, rfkt.getResponseFormat())
                        .put(JSONConstants.ACTIVE_KEY, this.responseFormatRepository.isActive(rfkt));
            }
        }
        return joes;
    }

    protected ArrayNode getProcedureEncodings() {
        ArrayNode jpes = Json.nodeFactory().arrayNode();
        final Map<OwsServiceKey, Set<String>> oes =
                this.procedureDescriptionFormatRepository.getAllProcedureDescriptionFormats();
        for (Entry<OwsServiceKey, Set<String>> entry : oes.entrySet()) {
            for (String procedureDescriptionFormat : entry.getValue()) {
                ProcedureDescriptionFormatKey rfkt =
                        new ProcedureDescriptionFormatKey(entry.getKey(), procedureDescriptionFormat);
                jpes.addObject().put(JSONConstants.SERVICE_KEY, rfkt.getService())
                        .put(JSONConstants.VERSION_KEY, rfkt.getVersion())
                        .put(JSONConstants.PROCEDURE_DESCRIPTION_FORMAT_KEY, rfkt.getProcedureDescriptionFormat())
                        .put(JSONConstants.ACTIVE_KEY, this.procedureDescriptionFormatRepository.isActive(rfkt));
            }
        }
        return jpes;
    }
}
