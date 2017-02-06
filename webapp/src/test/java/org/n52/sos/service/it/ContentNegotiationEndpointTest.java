/**
 * Copyright (C) 2012-2016 52°North Initiative for Geospatial Open Source
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
package org.n52.sos.service.it;

import static org.hamcrest.Matchers.is;
import static org.n52.sos.service.it.util.XPath.hasXPath;

import java.io.IOException;

import net.opengis.sos.x20.GetCapabilitiesDocument;
import net.opengis.sos.x20.GetCapabilitiesType;

import org.apache.xmlbeans.XmlObject;
import org.junit.Assume;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ErrorCollector;
import org.w3.x2003.x05.soapEnvelope.EnvelopeDocument;
import org.n52.sos.binding.BindingRepository;
import org.n52.sos.ogc.ows.OWSConstants;
import org.n52.sos.ogc.sos.Sos2Constants;
import org.n52.sos.ogc.sos.SosConstants;
import org.n52.sos.service.it.v2.XPaths;
import org.n52.sos.util.http.HTTPStatus;
import org.n52.sos.util.http.MediaTypes;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;

/**
 * TODO JavaDoc
 *
 * @author Christian Autermann
 */
public class ContentNegotiationEndpointTest extends AbstractComplianceSuiteTest {
    private static final String APPLICATION_SOAP_XML
            = MediaTypes.APPLICATION_SOAP_XML.toString();
    private static final String APPLICATION_XML
            = MediaTypes.APPLICATION_XML.toString();
    private static final String APPLICATION_JSON
            = MediaTypes.APPLICATION_JSON.toString();
    private static final String SERVICE
            = OWSConstants.RequestParams.service.toString();
    private static final String REQUEST
            = OWSConstants.RequestParams.request.toString();
    private static final String GET_CAPABILITIES
            = SosConstants.Operations.GetCapabilities.toString();
    private static final String SERVICE_VERSION = Sos2Constants.SERVICEVERSION;
    private static final String SERVICE_TYPE = SosConstants.SOS;
    @Rule
    public final ErrorCollector errors = new ErrorCollector();

    private XmlObject envelope(final XmlObject r) {
        final EnvelopeDocument envDoc = EnvelopeDocument.Factory.newInstance();
        envDoc.addNewEnvelope().addNewBody().set(r);
        return envDoc;
    }

    private GetCapabilitiesDocument validGetCapabilitiesDocument() {
        GetCapabilitiesDocument document = GetCapabilitiesDocument.Factory
                .newInstance();
        GetCapabilitiesType gc = document.addNewGetCapabilities2();
        gc.addNewAcceptVersions().addNewVersion()
                .setStringValue(SERVICE_VERSION);
        gc.setService(SERVICE_TYPE);
        return document;
    }

    private String validPoxRequest() {
        return validGetCapabilitiesDocument().xmlText();
    }

    private String validSoapRequest() {
        return envelope(validGetCapabilitiesDocument()).xmlText();
    }

    private String validJsonRequest()
            throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        ObjectNode request = JsonNodeFactory.instance.objectNode();
        request.put(REQUEST, GET_CAPABILITIES);
        request.put(SERVICE, SERVICE_TYPE);
        String a = mapper.writer().writeValueAsString(request);
        return a;
    }

    @Test
    public void testKvpRequest() {
        validate(get(null).accept(APPLICATION_XML)
                .query(REQUEST, GET_CAPABILITIES)
                .query(SERVICE, SERVICE_TYPE).response());
    }

    @Test
    public void testPoxRequest() {
        validate(post(null)
                .contentType(APPLICATION_XML)
                .accept(APPLICATION_XML)
                .entity(validPoxRequest())
                .response());
    }

    @Test
    public void testPoxRequestContentTypeMismatch() {
        validate(post("/pox")
                .contentType(APPLICATION_JSON)
                .accept(APPLICATION_XML)
                .entity(validPoxRequest())
                .response());
    }

    @Test
    public void testPoxRequestWithEncoding() {
        validate(post(null)
                .contentType(APPLICATION_XML + ";charset=UTF-8")
                .accept(APPLICATION_XML)
                .entity(validPoxRequest())
                .response());
    }

    @Test
    public void testSoapRequest() {
        validate(post(null)
                .contentType(APPLICATION_SOAP_XML)
                .accept(APPLICATION_XML)
                .entity(validSoapRequest())
                .response());
    }

    @Test
    public void testSoapRequestContentTypeMismatch() {
        validate(post("/soap")
                .contentType(APPLICATION_JSON)
                .accept(APPLICATION_XML)
                .entity(validSoapRequest())
                .response());
    }

    @Test
    public void testSoapRequestWithEncoding() {
        validate(post(null)
                .contentType(APPLICATION_SOAP_XML + ";charset=UTF-8")
                .accept(APPLICATION_XML)
                .entity(validSoapRequest())
                .response());
    }

    private boolean isJsonBindingSupported() {
        return BindingRepository.getInstance().isBindingSupported(MediaTypes.APPLICATION_JSON);
    }

    @Test
    public void testJsonRequest()
            throws JsonProcessingException, IOException {
        Assume.assumeTrue(isJsonBindingSupported());
        validate(post(null)
                .contentType(APPLICATION_JSON)
                .accept(APPLICATION_XML)
                .entity(validJsonRequest())
                .response());
    }

    @Test
    public void testJsonRequestContentTypeMismatch()
            throws JsonProcessingException, IOException {
        Assume.assumeTrue(isJsonBindingSupported());
        validate(post("/json")
                .contentType(APPLICATION_XML)
                .accept(APPLICATION_XML)
                .entity(validJsonRequest())
                .response());
    }

    @Test
    public void testJsonRequestWithEncoding()
            throws JsonProcessingException, IOException {
        Assume.assumeTrue(isJsonBindingSupported());
        validate(post(null)
                .contentType(APPLICATION_JSON + ";charset=UTF-8")
                .accept(APPLICATION_XML)
                .entity(validJsonRequest())
                .response());
    }

    @Test
    public void unknownMediaType() {
        Response response = post(null)
                .contentType("some/thing")
                .accept(APPLICATION_XML)
                .entity(validPoxRequest())
                .response();
        errors.checkThat(response.getStatus(),
                         is(HTTPStatus.UNSUPPORTED_MEDIA_TYPE
                .getCode()));
    }

    @Test
    public void invalidMediaType() {
        Response response = post(null)
                .contentType("some/thing/wrong")
                .accept(APPLICATION_XML)
                .entity(validPoxRequest())
                .response();
        errors.checkThat(response.getStatus(),
                         is(HTTPStatus.BAD_REQUEST.getCode()));
    }

    private void validate(Response response) {
        errors.checkThat(response.getStatus(), is(HTTPStatus.OK.getCode()));
        errors.checkThat(response.asNode(), hasXPath(XPaths.CAPABILITIES));
    }
}
