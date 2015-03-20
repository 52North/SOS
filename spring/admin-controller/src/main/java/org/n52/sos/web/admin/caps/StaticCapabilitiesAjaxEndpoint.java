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
import java.sql.SQLException;

import net.opengis.sos.x20.CapabilitiesDocument;

import org.apache.xmlbeans.XmlException;
import org.apache.xmlbeans.XmlObject;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import org.n52.sos.exception.ConfigurationException;
import org.n52.sos.exception.NoSuchExtensionException;
import org.n52.sos.exception.NoSuchIdentifierException;
import org.n52.sos.ogc.ows.OwsExceptionReport;
import org.n52.sos.ogc.ows.StaticCapabilities;
import org.n52.sos.util.JSONUtils;
import org.n52.sos.web.ControllerConstants;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;


@Controller
@RequestMapping(ControllerConstants.Paths.STATIC_CAPABILITIES_AJAX_ENDPOINT)
public class StaticCapabilitiesAjaxEndpoint extends AbstractAdminCapabiltiesAjaxEndpoint {

    private static final String CURRENT = "current";
    private static final String STATIC_CAPABILITIES = "caps";

    @ResponseBody
    @ResponseStatus(HttpStatus.OK)
    @RequestMapping(method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public String getStaticCapabilities() throws SQLException, OwsExceptionReport {
        ObjectNode response = JSONUtils.nodeFactory().objectNode();
        String current = getSelectedStaticCapabilities();
        ObjectNode staticCapabilities = response.putObject(STATIC_CAPABILITIES);
        for (StaticCapabilities sc : getDao().getStaticCapabilities().values()) {
            staticCapabilities.put(sc.getIdentifier(), sc.getDocument());
        }
        if (current != null && !current.isEmpty()) {
            response.put(CURRENT, current);
        }
        return response.toString();
    }

    @ResponseBody
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @RequestMapping(method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
    public void setCurrentCapabilities(@RequestBody String json) throws SQLException,
                                                                        ConfigurationException,
                                                                        OwsExceptionReport,
                                                                        NoSuchExtensionException,
                                                                        IOException {
        String id = null;
        JsonNode node = JSONUtils.loadString(json);
        if (node.path(CURRENT).isTextual()) {
            id = node.path(CURRENT).asText();
        }
        setSelectedStaticCapabilities(id);
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @RequestMapping(value="/{identifier}", method = RequestMethod.POST, consumes = MediaType.APPLICATION_XML_VALUE)
    public void saveStaticCapabilities(
            @PathVariable("identifier") String identifier,
            @RequestBody String document) throws XmlException, OwsExceptionReport {
        XmlObject xo = XmlObject.Factory.parse(document);
        if (!(xo instanceof CapabilitiesDocument)) {
            throw new XmlException("Not a Capabilities document!");
        }
        getDao().saveStaticCapabilities(identifier.trim(), document);
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @RequestMapping(value="/{identifier}", method = RequestMethod.DELETE)
    public void deleteStaticCapabilities(
            @PathVariable("identifier") String identifier) throws SQLException, ConfigurationException,
                                                                  NoSuchIdentifierException, OwsExceptionReport {
        if (getSelectedStaticCapabilities() != null && getSelectedStaticCapabilities().equals(identifier)) {
            setSelectedStaticCapabilities(null);
        }
        getDao().deleteStaticCapabilities(identifier.trim());
    }
}
