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

import java.io.IOException;
import java.util.Map;

import org.apache.xmlbeans.XmlException;
import org.apache.xmlbeans.XmlObject;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import org.n52.sos.exception.NoSuchExtensionException;
import org.n52.sos.exception.NoSuchIdentifierException;
import org.n52.sos.exception.NoSuchOfferingException;
import org.n52.sos.ogc.ows.StringBasedCapabilitiesExtension;
import org.n52.sos.ogc.sos.Sos1Constants;
import org.n52.sos.ogc.sos.Sos2Constants;
import org.n52.sos.ogc.sos.SosConstants;
import org.n52.sos.util.JSONUtils;
import org.n52.sos.web.ControllerConstants;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

@Controller
@RequestMapping(ControllerConstants.Paths.CAPABILITIES_EXTENSION_AJAX_ENDPOINT)
public class CapabilitiesExtensionAjaxEndpoint extends AbstractAdminCapabiltiesAjaxEndpoint {
    @ResponseBody
    @ResponseStatus(HttpStatus.OK)
    @RequestMapping(method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public String getCapabilitiesExtensions() {
        ObjectNode response = JSONUtils.nodeFactory().objectNode();
        Map<String, StringBasedCapabilitiesExtension> capabilitiesExtensions = getDao()
                .getActiveCapabilitiesExtensions();
        for (String id : capabilitiesExtensions.keySet()) {
            response.put(id, toJson(capabilitiesExtensions.get(id)));
        }
        return response.toString();
    }

    private JsonNode toJson(StringBasedCapabilitiesExtension capabilitiesExtension) {
        return JSONUtils.nodeFactory().objectNode()
                .put(IDENTIFIER_PROPERTY, capabilitiesExtension.getSectionName())
                .put(DISABLED_PROPERTY, capabilitiesExtension.isDisabled())
                .put(EXTENSION_PROPERTY, capabilitiesExtension.getExtension());

    }

    @ResponseBody
    @ResponseStatus(HttpStatus.OK)
    @RequestMapping(value="/{identifier}", method = RequestMethod.GET, consumes = MediaType.APPLICATION_JSON_VALUE)
    public String getCapabilitiesExtension(@PathVariable("identifier") String identifier) throws NoSuchIdentifierException {
        StringBasedCapabilitiesExtension ce = getDao().getActiveCapabilitiesExtensions().get(identifier);
        if (ce == null) {
            throw new NoSuchIdentifierException(identifier);
        }
        return toJson(ce).toString();
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @RequestMapping(value="/{identifier}", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
    public void setCapabilitiesExtensionSettings(
            @PathVariable("identifier") String identifier,
            @RequestBody String settings) throws NoSuchExtensionException, NoSuchOfferingException, IOException {
        JsonNode request = JSONUtils.loadString(settings);

        if (request.has(DISABLED_PROPERTY)) {
            getDao().disableCapabilitiesExtension(identifier, request.path(DISABLED_PROPERTY).asBoolean());
        }
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @RequestMapping(value="/{identifier}", method = RequestMethod.POST, consumes = MediaType.APPLICATION_XML_VALUE)
    @SuppressWarnings("unchecked")
    public void saveCapabilitiesExtension(
            @PathVariable("identifier") String identifier,
            @RequestBody String extension) throws XmlException, InvalidIdentifierException {

        if (contains(identifier)) {
            throw new InvalidIdentifierException(identifier);
        }

        XmlObject.Factory.parse(extension);
        getDao().saveCapabilitiesExtension(identifier, extension);
    }

    public boolean contains(String name) {
        for (SosConstants.CapabilitiesSections s : SosConstants.CapabilitiesSections.values()) {
            if (s.name().equals(name)) {
                return true;
            }
        }
        for (Sos1Constants.CapabilitiesSections s : Sos1Constants.CapabilitiesSections.values()) {
            if (s.name().equals(name)) {
                return true;
            }
        }
        for (Sos2Constants.CapabilitiesSections s : Sos2Constants.CapabilitiesSections.values()) {
            if (s.name().equals(name)) {
                return true;
            }
        }
        return false;
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @RequestMapping(value="/{identifier}", method = RequestMethod.DELETE)
    public void deleteCapabilitiesExtension(
            @PathVariable("identifier") String identifier) throws NoSuchExtensionException {
        getDao().deleteCapabiltiesExtension(identifier);
    }

    @ResponseBody
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(InvalidIdentifierException.class)
    public String onError(InvalidIdentifierException e) {
        return String.format("The identifier %s is invalid", e.getMessage());
    }

    public static final class InvalidIdentifierException extends Exception {
        private static final long serialVersionUID = -1250321592096950412L;

        public InvalidIdentifierException(String message) {
            super(message);
        }
    }
}
