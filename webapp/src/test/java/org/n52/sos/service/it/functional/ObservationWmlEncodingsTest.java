/*
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
package org.n52.sos.service.it.functional;

import java.io.IOException;

import org.junit.Test;
import org.n52.shetland.ogc.om.series.wml.WaterMLConstants;
import org.n52.shetland.ogc.sos.Sos1Constants;
import org.n52.shetland.ogc.sos.Sos2Constants;

import net.opengis.ows.x11.ExceptionReportDocument;
import net.opengis.sos.x20.GetObservationResponseDocument;

public class ObservationWmlEncodingsTest extends AbstractObservationEncodingsTest {

    @Test
    public void testSos2GetObsWmlUrl() throws IOException {
        testGetObsXmlResponse(Sos2Constants.SERVICEVERSION, WaterMLConstants.NS_WML_20,
                GetObservationResponseDocument.class);
    }

    @Test
    public void testSos2GetObsWmlDrUrl() throws IOException {
        testGetObsXmlResponse(Sos2Constants.SERVICEVERSION, WaterMLConstants.NS_WML_20_DR,
                GetObservationResponseDocument.class);
    }

    @Test
    public void testSos1GetObsWmlMimeType() throws IOException {
        // WML not implemented for SOS 1.0.0, expect an ExceptionDocument
        testGetObsXmlResponse(Sos1Constants.SERVICEVERSION, WaterMLConstants.WML_CONTENT_TYPE.toString(),
                ExceptionReportDocument.class);
    }

    @Test
    public void testSos1GetObsWmlDrMimeType() throws IOException {
        // WML not implemented for SOS 1.0.0, expect an ExceptionDocument
        testGetObsXmlResponse(Sos1Constants.SERVICEVERSION, WaterMLConstants.WML_DR_CONTENT_TYPE.toString(),
                ExceptionReportDocument.class);
    }


}
