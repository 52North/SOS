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

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.apache.xmlbeans.XmlException;
import org.apache.xmlbeans.XmlObject;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;
import org.n52.sos.exception.NoSuchExtensionException;
import org.n52.sos.exception.NoSuchOfferingException;
import org.n52.sos.ogc.ows.OfferingExtension;
import org.n52.sos.ogc.ows.OwsExceptionReport;
import org.n52.sos.web.ControllerConstants;
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

import com.google.common.collect.Lists;

@Controller
@RequestMapping(ControllerConstants.Paths.OFFERING_EXTENSIONS_AJAX_ENDPOINT)
public class OfferingExtensionAjaxEndpoint extends AbstractAdminCapabiltiesAjaxEndpoint {

	private static final Logger LOGGER = LoggerFactory.getLogger(OfferingExtensionAjaxEndpoint.class);

	private JSONObject toJson(final Collection<OfferingExtension> extensionsForOffering) throws JSONException {
		final JSONObject jsonOffering = new JSONObject();
		if (extensionsForOffering != null) {
			for (final OfferingExtension e : extensionsForOffering) {
				jsonOffering.put(e.getIdentifier(), toJson(e));
			}
		}
		return jsonOffering;
	}

	private JSONObject toJson(final OfferingExtension extensionForOffering) throws JSONException {
		return new JSONObject()
		.put(IDENTIFIER_PROPERTY, extensionForOffering.getIdentifier())
		.put(DISABLED_PROPERTY, extensionForOffering.isDisabled())
		.put(EXTENSION_PROPERTY, extensionForOffering.getExtension())
		.put(OFFERING, extensionForOffering.getOfferingName());
	}

	@ResponseBody
	@ResponseStatus(HttpStatus.OK)
	@RequestMapping(method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public String getOfferingExtensions() throws JSONException, OwsExceptionReport {
		final Map<String, List<OfferingExtension>> offeringExtensions = getDao().getOfferingExtensions();
		final List<String> offerings = Lists.newArrayList(getCache().getOfferings());
		Collections.sort(offerings);
		final JSONObject response = new JSONObject();
		for (final String offering : offerings) {
			response.put(offering, toJson(offeringExtensions.get(offering)));
		}
		return response.toString();
	}

	private void checkOffering(final String offering) throws NoSuchOfferingException {
		LOGGER.trace("checkOffering('{}')",offering);
		LOGGER.trace("Offerings im Cache: {}",Arrays.toString(getCache().getOfferings().toArray()));
		if (!getCache().getOfferings().contains(offering)) {
			throw new NoSuchOfferingException(offering);
		}
	}

	@ResponseStatus(HttpStatus.NO_CONTENT)
	@RequestMapping(value="/save", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
	public void saveOfferingExtension(@RequestBody final String extensionJson) 
			throws XmlException, NoSuchOfferingException, OwsExceptionReport, JSONException {
		final JSONObject request = new JSONObject(extensionJson);

		final String offeringId = request.optString(OFFERING);
		final String extensionId = request.optString(IDENTIFIER);
		final String extensionContent = request.optString(EXTENSION_PROPERTY);
		checkOffering(offeringId);
		XmlObject.Factory.parse(extensionContent);
		getDao().saveOfferingExtension(offeringId, extensionId, extensionContent);
	}

	@ResponseStatus(HttpStatus.NO_CONTENT)
	@RequestMapping(value="/edit", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
	public void setOfferingExtensionSettings(
			@RequestBody final String settings) throws JSONException, NoSuchExtensionException, NoSuchOfferingException,
			OwsExceptionReport {
		final JSONObject request = new JSONObject(settings);
		final String offeringId = request.optString(OFFERING);
		final String extensionId = request.optString(IDENTIFIER);

		final Object disabled = request.opt(DISABLED_PROPERTY);
		if (disabled != null && disabled != JSONObject.NULL) {
			getDao().disableOfferingExtension(offeringId, extensionId, ((Boolean) disabled).booleanValue());
		}
	}

	@ResponseStatus(HttpStatus.NO_CONTENT)
	@RequestMapping(value="/delete", method = RequestMethod.DELETE, consumes = MediaType.APPLICATION_JSON_VALUE)
	public void deleteOfferingExtension(
			@RequestBody final String requestJson) throws NoSuchExtensionException, NoSuchOfferingException,
			OwsExceptionReport, JSONException {
		final JSONObject request = new JSONObject(requestJson);
		final String offeringId = request.optString(OFFERING);
		final String extensionId = request.optString(IDENTIFIER);
		getDao().deleteOfferingExtension(offeringId, extensionId);
	}
}
