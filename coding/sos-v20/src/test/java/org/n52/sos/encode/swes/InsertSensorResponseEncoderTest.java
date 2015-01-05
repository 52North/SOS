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
package org.n52.sos.encode.swes;

import static java.lang.Boolean.FALSE;
import static java.lang.Boolean.TRUE;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.Assert.assertThat;

import java.io.ByteArrayOutputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import net.opengis.swes.x20.InsertSensorResponseDocument;

import org.apache.xmlbeans.XmlObject;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.n52.sos.coding.CodingRepository;
import org.n52.sos.config.SettingsManager;
import org.n52.sos.encode.EncoderKey;
import org.n52.sos.encode.OperationEncoderKey;
import org.n52.sos.encode.XmlEncoderKey;
import org.n52.sos.exception.ows.concrete.UnsupportedEncoderInputException;
import org.n52.sos.ogc.ows.OwsExceptionReport;
import org.n52.sos.ogc.sos.Sos2Constants;
import org.n52.sos.ogc.sos.SosConstants;
import org.n52.sos.ogc.swes.SwesConstants;
import org.n52.sos.response.InsertSensorResponse;
import org.n52.sos.util.http.MediaTypes;
import org.n52.sos.w3c.SchemaLocation;

import com.google.common.collect.Maps;

/**
 * TODO JavaDoc
 * 
 * @author Christian Autermann <c.autermann@52north.org>
 * 
 * @since 4.0.0
 */
public class InsertSensorResponseEncoderTest {
    @BeforeClass
    public static void initDecoders() {
        CodingRepository.getInstance();
    }

    @AfterClass
    public static void cleanUp() {
        SettingsManager.getInstance().cleanup();
    }

    @Test
    public void should_return_correct_encoder_keys() {
        Set<EncoderKey> returnedKeySet = new InsertSensorResponseEncoder().getEncoderKeyType();
        assertThat(returnedKeySet.size(), is(3));
        assertThat(returnedKeySet, hasItem(new XmlEncoderKey(SwesConstants.NS_SWES_20, InsertSensorResponse.class)));
        assertThat(returnedKeySet, hasItem(new OperationEncoderKey(SosConstants.SOS, Sos2Constants.SERVICEVERSION,
                Sos2Constants.Operations.InsertSensor, MediaTypes.TEXT_XML)));
        assertThat(returnedKeySet, hasItem(new OperationEncoderKey(SosConstants.SOS, Sos2Constants.SERVICEVERSION,
                Sos2Constants.Operations.InsertSensor, MediaTypes.APPLICATION_XML)));
    }

    @Test
    public void should_return_emptyMap_for_supportedTypes() {
        assertThat(new InsertSensorResponseEncoder().getSupportedTypes(), is(not(nullValue())));
        assertThat(new InsertSensorResponseEncoder().getSupportedTypes().isEmpty(), is(TRUE));
    }

    @Test
    public void should_return_emptySet_for_conformanceClasses() {
        assertThat(new InsertSensorResponseEncoder().getConformanceClasses(), is(not(nullValue())));
        assertThat(new InsertSensorResponseEncoder().getConformanceClasses().isEmpty(), is(TRUE));
    }

    @Test
    public void should_add_own_prefix_to_prefixMap() {
        Map<String, String> prefixMap = Maps.newHashMap();
        new InsertSensorResponseEncoder().addNamespacePrefixToMap(prefixMap);
        assertThat(prefixMap.isEmpty(), is(FALSE));
        assertThat(prefixMap.containsKey(SwesConstants.NS_SWES_20), is(TRUE));
        assertThat(prefixMap.containsValue(SwesConstants.NS_SWES_PREFIX), is(TRUE));
    }

    @Test
    public void should_not_fail_if_prefixMap_is_null() {
        new InsertSensorResponseEncoder().addNamespacePrefixToMap(null);
    }

    @Test
    public void should_return_contentType_xml() {
        assertThat(new InsertSensorResponseEncoder().getContentType(), is(MediaTypes.TEXT_XML));
    }

    @Test
    public void should_return_correct_schema_location() {
        assertThat(new InsertSensorResponseEncoder().getSchemaLocations().size(), is(1));
        SchemaLocation schemLoc = new InsertSensorResponseEncoder().getSchemaLocations().iterator().next();
        assertThat(schemLoc.getNamespace(), is("http://www.opengis.net/swes/2.0"));
        assertThat(schemLoc.getSchemaFileUrl(), is("http://schemas.opengis.net/swes/2.0/swes.xsd"));
    }

    @Test(expected = UnsupportedEncoderInputException.class)
    public void should_return_exception_if_received_null() throws OwsExceptionReport {
        new InsertSensorResponseEncoder().encode(null);
        new InsertSensorResponseEncoder().encode(null, new ByteArrayOutputStream());
        new InsertSensorResponseEncoder().encode(null, new HashMap<SosConstants.HelperValues, String>());
    }

    @Test
    public void should_encode_InsertSensor_response() throws OwsExceptionReport {
        String assignedOffering = "assignedOffering";
        String assignedProcedure = "assignedProcedure";
        InsertSensorResponse response = new InsertSensorResponse();
        response.setAssignedOffering(assignedOffering);
        response.setAssignedProcedure(assignedProcedure);

        final XmlObject encodedResponse = new InsertSensorResponseEncoder().encode(response);

        assertThat(encodedResponse, is(instanceOf(InsertSensorResponseDocument.class)));
        InsertSensorResponseDocument doc = (InsertSensorResponseDocument) encodedResponse;
        assertThat(doc.isNil(), is(FALSE));
        assertThat(doc.getInsertSensorResponse().getAssignedOffering(), is(assignedOffering));
        assertThat(doc.getInsertSensorResponse().getAssignedProcedure(), is(assignedProcedure));
        assertThat(doc.validate(), is(TRUE));
    }
}
