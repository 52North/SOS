/**
 * Copyright (C) 2012-2020 52°North Initiative for Geospatial Open Source
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
package org.n52.sos.encode.streaming.sos.v2;

import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import javax.xml.stream.XMLStreamException;

import org.apache.xmlbeans.XmlException;
import org.apache.xmlbeans.XmlObject;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.n52.sos.coding.CodingRepository;
import org.n52.sos.config.SettingsManager;
import org.n52.sos.encode.EncodingValues;
import org.n52.sos.ogc.ows.OwsExceptionReport;
import org.n52.sos.ogc.sos.Sos2Constants;
import org.n52.sos.ogc.sos.SosConstants;
import org.n52.sos.ogc.swes.SwesExtensions;
import org.n52.sos.response.GetFeatureOfInterestResponse;
import org.n52.sos.service.Configurator;
import org.n52.sos.service.profile.DefaultProfileHandler;

import net.opengis.sos.x20.GetFeatureOfInterestResponseDocument;
import net.opengis.sos.x20.GetObservationResponseDocument;

public class GetFeatureOfInterestXmlStreamWriterTest extends AbstractMetadataTest {

private GetFeatureOfInterestXmlStreamWriter encoder = new  GetFeatureOfInterestXmlStreamWriter();
    
    @BeforeClass
    public final static void init() {
        CodingRepository.getInstance();
        Configurator configurator = mock(Configurator.class);
        when(configurator.getProfileHandler()).thenReturn(new DefaultProfileHandler());
        Configurator.setInstance(configurator);
    }

    @AfterClass
    public static void cleanUp() {
        SettingsManager.getInstance().cleanup();
    }
    
    @Test
    public void testMetadataEncoding() throws XmlException, XMLStreamException, IOException, OwsExceptionReport {
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            encoder.write(createResponse(), baos, new EncodingValues());
            XmlObject encode = XmlObject.Factory.parse(new String(baos.toByteArray()));
            assertThat(encode, instanceOf(GetFeatureOfInterestResponseDocument.class));
            GetFeatureOfInterestResponseDocument gord = (GetFeatureOfInterestResponseDocument) encode;
            assertThat(gord.getGetFeatureOfInterestResponse() != null, is(true));
            checkMetadataResponse(gord.getGetFeatureOfInterestResponse().getExtensionArray());
        }
    }
    
    private GetFeatureOfInterestResponse createResponse() {
        GetFeatureOfInterestResponse response = new GetFeatureOfInterestResponse();
        response.setService(SosConstants.SOS);
        response.setVersion(Sos2Constants.SERVICEVERSION);
        
        SwesExtensions swesExtensions = new SwesExtensions();
        swesExtensions.addSwesExtension(createExtension());
        response.setExtensions(swesExtensions);
        return response;
    }
    
}
