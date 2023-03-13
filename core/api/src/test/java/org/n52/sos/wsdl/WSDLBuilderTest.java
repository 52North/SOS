/*
 * Copyright (C) 2012-2023 52°North Spatial Information Research GmbH
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
package org.n52.sos.wsdl;

import java.net.URI;

import org.apache.xmlbeans.XmlOptions;
import org.junit.BeforeClass;
import org.junit.Test;
import org.n52.svalbard.encode.EncoderRepository;
import org.n52.svalbard.encode.OwsEncoderv110;
import org.n52.svalbard.encode.SosRequestEncoderv20;
import org.n52.svalbard.encode.SwesExtensionEncoderv20;
import org.n52.svalbard.encode.WsdlEncoderv11;

import com.google.common.collect.Lists;

public class WSDLBuilderTest {

    private static EncoderRepository encoderRepository;

    @BeforeClass
    public static void setup() {
        encoderRepository = new EncoderRepository();
        WsdlEncoderv11 wsdlEncoderv11 = new WsdlEncoderv11();
        wsdlEncoderv11.setXmlOptions(XmlOptions::new);
        encoderRepository.setEncoders(Lists.newArrayList(wsdlEncoderv11, new SosRequestEncoderv20(),
                new SwesExtensionEncoderv20(), new OwsEncoderv110()));
        encoderRepository.init();
    }

    @Test
    public void test() {
        URI url = URI.create("http://localhost:8080/52n-sos-webapp/service");
        WSDLBuilder b = new WSDLBuilder(encoderRepository).setSoapEndpoint(url)
                .setKvpEndpoint(url)
                .setPoxEndpoint(url);
        for (Metadata o : new Metadata[] { Metadatas.DELETE_SENSOR, Metadatas.DESCRIBE_SENSOR,
            Metadatas.GET_CAPABILITIES, Metadatas.GET_FEATURE_OF_INTEREST, Metadatas.GET_OBSERVATION,
            Metadatas.GET_OBSERVATION_BY_ID, Metadatas.GET_RESULT, Metadatas.GET_RESULT_TEMPLATE,
            Metadatas.INSERT_OBSERVATION, Metadatas.INSERT_RESULT, Metadatas.INSERT_RESULT_TEMPLATE,
            Metadatas.INSERT_SENSOR, Metadatas.UPDATE_SENSOR_DESCRIPTION }) {
            b.addPoxOperation(o);
            b.addKvpOperation(o);
            b.addSoapOperation(o);
        }
        b.build();
//        System.out.println(b.build());
    }

}
