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
package org.n52.sos.encode.sos.v2;

import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.LinkedList;
import java.util.List;

import org.apache.xmlbeans.XmlException;
import org.apache.xmlbeans.XmlObject;
import org.joda.time.DateTime;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.n52.sos.coding.CodingRepository;
import org.n52.sos.config.SettingsManager;
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
import org.n52.sos.ogc.swe.SweDataArray;
import org.n52.sos.ogc.swe.SweField;
import org.n52.sos.ogc.swe.SweSimpleDataRecord;
import org.n52.sos.ogc.swe.simpleType.SweCount;
import org.n52.sos.ogc.swes.SwesExtension;
import org.n52.sos.ogc.swes.SwesExtensionImpl;
import org.n52.sos.ogc.swes.SwesExtensions;
import org.n52.sos.response.GetObservationResponse;
import org.n52.sos.service.Configurator;
import org.n52.sos.service.profile.DefaultProfileHandler;
import org.n52.sos.util.SweHelper;

import com.google.common.collect.Lists;

import net.opengis.sos.x20.GetObservationResponseDocument;
import net.opengis.swe.x20.DataArrayPropertyType;
import net.opengis.swe.x20.DataArrayType;
import net.opengis.swe.x20.DataRecordType;

public class GetObservationResponseEncoderTest {

    private GetObservationResponseEncoder encoder = new GetObservationResponseEncoder();
    
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
    public void testMetadataEncoding() throws OwsExceptionReport, XmlException {
        XmlObject encode = encoder.encode(createResponse());
        assertThat(encode, instanceOf(GetObservationResponseDocument.class));
        GetObservationResponseDocument gord = (GetObservationResponseDocument) encode;
        assertThat(gord.getGetObservationResponse() != null, is(true));
        assertThat(gord.getGetObservationResponse().getExtensionArray() != null, is(true));
        assertThat(gord.getGetObservationResponse().getExtensionArray().length, is(1));
        XmlObject parse = XmlObject.Factory.parse(gord.getGetObservationResponse().getExtensionArray(0).xmlText());
        assertThat(parse, instanceOf(DataArrayPropertyType.class));
        DataArrayPropertyType dad = (DataArrayPropertyType) parse;
        assertThat(dad.getDataArray1(), instanceOf(DataArrayType.class));
        DataArrayType dat = (DataArrayType) dad.getDataArray1();
        assertThat(dat.getElementType().isSetAbstractDataComponent(), is (true));
        assertThat(dat.getElementType().getAbstractDataComponent(), instanceOf(DataRecordType.class));
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
    
    private SwesExtension<SweDataArray> createExtension() {
        SweDataArray sweDataArray = new SweDataArray();
        sweDataArray.setElementCount(new SweCount().setValue(2));
        sweDataArray.setEncoding(new SweHelper().createTextEncoding(";", ",", "."));

        SweSimpleDataRecord dataRecord = new SweSimpleDataRecord();
        dataRecord.setDefinition("Components");
        dataRecord.addField(new SweField("test_id"));
        dataRecord.addField(new SweField("test_code"));
        dataRecord.addField(new SweField("test_desc"));
        sweDataArray.setElementType(dataRecord);

        LinkedList<List<String>> values = new LinkedList<List<String>>();
        List<String> blockOfTokens_1 = new LinkedList<>();
        blockOfTokens_1.add("1");
        blockOfTokens_1.add("code_1");
        blockOfTokens_1.add("desc_1");
        values.add(blockOfTokens_1);
        List<String> blockOfTokens_2 = new LinkedList<>();
        blockOfTokens_2.add("2");
        blockOfTokens_2.add("code_2");
        blockOfTokens_2.add("desc_2");
        values.add(blockOfTokens_2);

        sweDataArray.setValues(values);
        return new SwesExtensionImpl<SweDataArray>().setValue(sweDataArray).setIdentifier("test")
                .setDefinition("test");
    }
}
