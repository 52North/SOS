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

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.apache.xmlbeans.XmlError;
import org.apache.xmlbeans.XmlException;
import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.XmlOptions;
import org.apache.xmlbeans.XmlValidationError;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import org.n52.sos.util.JSONUtils;
import org.n52.sos.util.XmlHelper;
import org.n52.sos.util.XmlHelper.LaxValidationCase;
import org.n52.sos.web.ControllerConstants;

import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

@Controller
@RequestMapping(ControllerConstants.Paths.VALIDATION_AJAX_ENDPOINT)
public class ValidationAjaxEndpoint extends AbstractAdminCapabiltiesAjaxEndpoint {

	private static final Logger LOGGER = LoggerFactory.getLogger(XmlHelper.class);

    @ResponseBody
    @ResponseStatus(HttpStatus.OK)
    @RequestMapping(method = RequestMethod.POST, consumes = MediaType.APPLICATION_XML_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public String validate(@RequestBody String xml) {
    	LOGGER.trace("Starting validation");
        ObjectNode result = JSONUtils.nodeFactory().objectNode();
        ArrayNode resultErrors = result.putArray(ERRORS_PROPERTY);
        LinkedList<XmlError> xmlErrors = new LinkedList<XmlError>();
        XmlOptions options = new XmlOptions().setErrorListener(xmlErrors).setLoadLineNumbers(XmlOptions.LOAD_LINE_NUMBERS_END_ELEMENT);
        try {
            XmlObject x = XmlObject.Factory.parse(xml, options);
            result.put(VALID_PROPERTY, x.validate(options));
        } catch (XmlException ex) {
            resultErrors.add("Could not parse XML document: "+ ex.getMessage());
        }
        /*
         * TODO Re-do error handling in XMLHelper and remove the next block. Use XMLHelper.validate and not the xmlbeans version
         * BLOCK taken from XMLHelper -> needs re-do because the current XMLHelper.validate()
         * does not return errors and does not provide any means to access errors after validation
         */
        // START of BLOCK
        final Iterator<XmlError> iter = xmlErrors.iterator();
        final List<XmlError> shouldPassErrors = new LinkedList<XmlError>();
        final List<XmlError> errors = new LinkedList<XmlError>();
        while (iter.hasNext()) {
            final XmlError error = iter.next();
            boolean shouldPass = false;
            if (error instanceof XmlValidationError) {
                for (final LaxValidationCase lvc : LaxValidationCase.values()) {
                    if (lvc.shouldPass((XmlValidationError) error)) {
                        shouldPass = true;
                        LOGGER.debug("Lax validation case found for XML validation error: {}", error);
                        break;
                    }
                }
            }
            if (shouldPass) {
                shouldPassErrors.add(error);
            } else {
                errors.add(error);
            }
        }
        if (errors.size() > 0) {
        	for (XmlError e : errors) {
                resultErrors.add(e.toString());
            }
        } else if (errors.size() == 0) {
        	result.put(VALID_PROPERTY, true);
        }
        // END of BLOCK
        // uncomment next lines if BLOCK is removed
//        for (XmlError e : xmlErrors) {
//            resultErrors.put(e.toString());
//        }
        LOGGER.trace("Finishing validation");
        return result.toString();
    }
}
