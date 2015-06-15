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
import java.util.Collection;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import org.n52.sos.ds.ConnectionProviderException;
import org.n52.sos.exception.ConfigurationException;
import org.n52.sos.exception.JSONException;
import org.n52.sos.ogc.ows.OwsExtendedCapabilitiesKey;
import org.n52.sos.ogc.ows.OwsExtendedCapabilitiesRepository;
import org.n52.sos.ogc.swes.OfferingExtensionKey;
import org.n52.sos.ogc.swes.OfferingExtensionRepository;
import org.n52.sos.service.operator.ServiceOperatorKey;
import org.n52.sos.util.JSONUtils;
import org.n52.sos.web.ControllerConstants;
import org.n52.sos.web.JSONConstants;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

/**
 * Admin controller for extensions
 * @author Carsten Hollmann <c.hollmann@52north.org>
 * @since 4.1.0
 *
 */
@Controller
public class AdminExtensionController extends AbstractAdminController {
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

    @RequestMapping(value = ControllerConstants.Paths.ADMIN_EXTENSIONS, method = RequestMethod.GET)
    public String view() throws ConnectionProviderException {
        return ControllerConstants.Views.ADMIN_EXTENSIONS;
    }

    @ResponseBody
    @RequestMapping(value = ControllerConstants.Paths.ADMIN_EXTENSIONS_JSON_ENDPOINT, method = RequestMethod.GET, produces = ControllerConstants.MEDIA_TYPE_APPLICATION_JSON)
    public String getAll() throws JSONException, ConnectionProviderException {
        ObjectNode node = JSONUtils.nodeFactory().objectNode();
        node.put(JSONConstants.EXTENDED_CAPABILITIES_EXTENSION_KEY, getExtendedCapabilitiesExtensions());
        node.put(JSONConstants.OFFERING_EXTENSION_EXTENSION_KEY, getOfferingExtensionExtensions());
        return JSONUtils.print(node);
    }

    @ResponseBody
    @RequestMapping(value = ControllerConstants.Paths.ADMIN_EXTENSIONS_JSON_ENDPOINT, method = RequestMethod.POST, consumes = ControllerConstants.MEDIA_TYPE_APPLICATION_JSON)
    public void change(@RequestBody String request) throws JSONException, ConnectionProviderException, IOException {
        JsonNode json = JSONUtils.loadString(request);

        if (json.has(JSONConstants.EXTENDED_CAPABILITIES_DOMAIN_KEY)) {
            ServiceOperatorKey sokt =
                    new ServiceOperatorKey(json.path(JSONConstants.SERVICE_KEY).asText(),
                            json.path(JSONConstants.VERSION_KEY).asText());
            OwsExtendedCapabilitiesKey oeckt = new OwsExtendedCapabilitiesKey(sokt, json.path(JSONConstants.EXTENDED_CAPABILITIES_DOMAIN_KEY).asText());
            if (json.path(JSONConstants.ACTIVE_KEY).asBoolean()) {
                for (OwsExtendedCapabilitiesKey key : OwsExtendedCapabilitiesRepository.getInstance().getAllExtendedCapabilitiesProviders().keySet()) {
                    if (key.getServiceOperatorKey().equals(sokt)) {
                        getSettingsManager().setActive(key, false);
                    }
                }
            }
            getSettingsManager().setActive(oeckt, json.path(JSONConstants.ACTIVE_KEY).asBoolean());
        } else if (json.has(JSONConstants.OFFERING_EXTENSION_DOMAIN_KEY)) {
            ServiceOperatorKey sokt =
                    new ServiceOperatorKey(json.path(JSONConstants.SERVICE_KEY).asText(),
                            json.path(JSONConstants.VERSION_KEY).asText());
            OfferingExtensionKey oekt =
                    new OfferingExtensionKey(sokt,
                            json.path(JSONConstants.OFFERING_EXTENSION_DOMAIN_KEY).asText());
            getSettingsManager().setActive(oekt, json.path(JSONConstants.ACTIVE_KEY).asBoolean());
        } else {
            throw new JSONException("Invalid JSON");
        }
    }

    protected ArrayNode getExtendedCapabilitiesExtensions() throws ConnectionProviderException, ConfigurationException,
            JSONException {
        ArrayNode jeces = JSONUtils.nodeFactory().arrayNode();
        final Map<ServiceOperatorKey, Collection<String>> oes =
                OwsExtendedCapabilitiesRepository.getInstance().getAllDomains();
        for (ServiceOperatorKey sokt : oes.keySet()) {
            for (String name : oes.get(sokt)) {
                OwsExtendedCapabilitiesKey oeckt = new OwsExtendedCapabilitiesKey(sokt, name);
                jeces.addObject()
                        .put(JSONConstants.SERVICE_KEY, oeckt.getService())
                        .put(JSONConstants.VERSION_KEY, oeckt.getVersion())
                        .put(JSONConstants.EXTENDED_CAPABILITIES_DOMAIN_KEY, oeckt.getDomain())
                        .put(JSONConstants.ACTIVE_KEY, getSettingsManager().isActive(oeckt));
            }
        }
        return jeces;
    }

    protected ArrayNode getOfferingExtensionExtensions() throws JSONException, ConnectionProviderException,
            ConfigurationException {
        ArrayNode joes = JSONUtils.nodeFactory().arrayNode();
        final Map<ServiceOperatorKey, Collection<String>> oes =
                OfferingExtensionRepository.getInstance().getAllDomains();
        for (ServiceOperatorKey sokt : oes.keySet()) {
            for (String name : oes.get(sokt)) {
                OfferingExtensionKey oekt = new OfferingExtensionKey(sokt, name);
                joes.addObject()
                        .put(JSONConstants.SERVICE_KEY, oekt.getService())
                        .put(JSONConstants.VERSION_KEY, oekt.getVersion())
                        .put(JSONConstants.OFFERING_EXTENSION_DOMAIN_KEY, oekt.getDomain())
                        .put(JSONConstants.ACTIVE_KEY, getSettingsManager().isActive(oekt));
            }
        }
        return joes;
    }

}
