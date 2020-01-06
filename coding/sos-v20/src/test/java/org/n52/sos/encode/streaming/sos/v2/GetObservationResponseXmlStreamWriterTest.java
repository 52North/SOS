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
import org.joda.time.DateTime;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.n52.sos.coding.CodingRepository;
import org.n52.sos.config.SettingsManager;
import org.n52.sos.encode.EncodingValues;
import org.n52.sos.ogc.gml.CodeWithAuthority;
import org.n52.sos.ogc.gml.time.TimeInstant;
import org.n52.sos.ogc.om.OmConstants;
import org.n52.sos.ogc.om.OmObservableProperty;
import org.n52.sos.ogc.om.OmObservation;
import org.n52.sos.ogc.om.OmObservationConstellation;
import org.n52.sos.ogc.om.SingleObservationValue;
import org.n52.sos.ogc.om.features.samplingFeatures.SamplingFeature;
import org.n52.sos.ogc.om.values.QuantityValue;
import org.n52.sos.ogc.ows.OwsExceptionReport;
import org.n52.sos.ogc.sensorML.SensorMLConstants;
import org.n52.sos.ogc.sos.Sos2Constants;
import org.n52.sos.ogc.sos.SosConstants;
import org.n52.sos.ogc.sos.SosProcedureDescriptionUnknowType;
import org.n52.sos.ogc.swes.SwesExtensions;
import org.n52.sos.response.GetObservationResponse;
import org.n52.sos.service.Configurator;
import org.n52.sos.service.profile.DefaultProfileHandler;

import com.google.common.collect.Lists;

import net.opengis.sos.x20.GetObservationResponseDocument;

public class GetObservationResponseXmlStreamWriterTest extends AbstractMetadataTest {
    private GetObservationResponseXmlStreamWriter encoder = new  GetObservationResponseXmlStreamWriter();
    
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
            assertThat(encode, instanceOf(GetObservationResponseDocument.class));
            GetObservationResponseDocument gord = (GetObservationResponseDocument) encode;
            assertThat(gord.getGetObservationResponse() != null, is(true));
            checkMetadataResponse(gord.getGetObservationResponse().getExtensionArray());
        }
    }
    
    private GetObservationResponse createResponse() {
        GetObservationResponse response = new GetObservationResponse();
        response.setService(SosConstants.SOS);
        response.setVersion(Sos2Constants.SERVICEVERSION);
        response.setResponseFormat(OmConstants.NS_OM_2);
        OmObservation obs = new OmObservation();

        OmObservationConstellation obsConst = new OmObservationConstellation();
        obsConst.setProcedure(new SosProcedureDescriptionUnknowType("procedure", SensorMLConstants.NS_SML, null));
        OmObservableProperty omObservableProperty = new OmObservableProperty("observable_property");
        omObservableProperty.setUnit("°C");
        obsConst.setObservableProperty(omObservableProperty);
        obsConst.setFeatureOfInterest(new SamplingFeature(new CodeWithAuthority("feature")));
        obsConst.setObservationType( OmConstants.OBS_TYPE_MEASUREMENT);
        obsConst.addOffering("offering");
        obs.setObservationConstellation(obsConst);

        obs.setResultTime(new TimeInstant(DateTime.now()));
        SingleObservationValue<Double> obsVal = new SingleObservationValue<Double>();
        obsVal.setPhenomenonTime(new TimeInstant(DateTime.now()));
        obsVal.setValue(new QuantityValue(Double.valueOf("52.7"), "°C"));
        obs.setValue(obsVal);
        response.setObservationCollection(Lists.newArrayList(obs));
        
        SwesExtensions swesExtensions = new SwesExtensions();
        swesExtensions.addSwesExtension(createExtension());
        response.setExtensions(swesExtensions);
        return response;
    }
}
