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
package org.n52.sos.encode.sos.v2;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

import java.io.IOException;

import org.apache.xmlbeans.XmlObject;
import org.custommonkey.xmlunit.Diff;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.n52.sos.exception.ows.concrete.XmlDecodingException;
import org.n52.sos.ogc.ows.OwsExceptionReport;
import org.n52.sos.response.GetCapabilitiesResponse;
import org.xml.sax.SAXException;


public class GetCapabilitiesResponseEncoderTest {

	private GetCapabilitiesResponseEncoder encoder;
	
	@Before
	public void setUp(){
		encoder = new GetCapabilitiesResponseEncoder();
	}

	@Test public void
	should_create_static_capabilities()
			throws OwsExceptionReport, SAXException, IOException {
		XmlObject encodedResponse = encoder.encode(minimalCapabilities());
		
		Diff d = new Diff (encodedResponse.xmlText(), minimalCapabilities().getXmlString());
		
		assertThat(d.identical(), is(true));
		assertThat(d.similar(), is(true));
	}
	
	@Rule
	public ExpectedException expectedEx = ExpectedException.none();

	@Test public void
	should_throw_Exception_when_static_content_is_invalid()
		throws OwsExceptionReport {
		expectedEx.expect(XmlDecodingException.class);
		expectedEx.expectMessage("Error while decoding Static Capabilities:\nBAD XML STRING");
		
		encoder.encode(badCapabilities());
	}
	
	private GetCapabilitiesResponse minimalCapabilities() {
		GetCapabilitiesResponse response = new GetCapabilitiesResponse();
		response.setService("SOS");
		response.setVersion("2.0.0");
		response.setXmlString("<sos:Capabilities version=\"2.0.0\" xmlns:sos=\"http://www.opengis.net/sos/2.0\" " + 
				"xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" " + 
				"xsi:schemaLocation=\"http://www.opengis.net/sos/2.0 http://schemas.opengis.net/sos/2.0/sosGetCapabilities.xsd\"/>");
		return response;
	}
	
	private GetCapabilitiesResponse badCapabilities() {
		GetCapabilitiesResponse response = new GetCapabilitiesResponse();
		response.setService("SOS");
		response.setVersion("2.0.0");
		response.setXmlString("BAD XML STRING");
		return response;
	}
}
