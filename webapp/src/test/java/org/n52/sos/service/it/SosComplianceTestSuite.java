/*
 * Copyright (C) 2012-2022 52°North Spatial Information Research GmbH
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

import org.junit.Ignore;
import org.junit.Rule;
import org.junit.runner.RunWith;

import org.n52.iceland.service.Service;

/**
 * TODO JavaDoc
 *
 * @author <a href="mailto:c.autermann@52north.org">Christian Autermann</a>
 *
 * @since 4.0.0
 */
@Ignore
@RunWith(ComplianceSuiteRunner.class)
public class SosComplianceTestSuite extends MockHttpExecutor
        implements ComplianceSuite {

    private final H2Database datasource = new H2Database();

    public SosComplianceTestSuite() {
        super(Service.class);
    }

    @Rule
    public H2Database getDatasource() {
        return datasource;
    }

    @Override
    public Client kvp() {
        return get("/kvp");
    }

    @Override
    public Client pox() {
        return post("/pox");
    }

    @Override
    public Client soap() {
        return post("/soap");
    }

    @Override
    public RequestExecutor getExecutor() {
        return this;
    }

    @Override
    public Class<?>[] getTests() {
        return new Class<?>[] {

            org.n52.sos.service.it.functional.ComplexObservationTest.class,
            org.n52.sos.service.it.functional.ContentNegotiationEndpointTest.class,
            org.n52.sos.service.it.functional.DescribeSensorProcedureDescriptionFormatTest.class,
            org.n52.sos.service.it.functional.ObservationOmEncodingsTest.class,
            org.n52.sos.service.it.functional.ObservationJsonEncodingsTest.class,
            org.n52.sos.service.it.functional.ObservationWmlEncodingsTest.class,
//            disabled due to travis build error
//            org.n52.sos.service.it.functional.ObservationNetCDFEncodingsTest.class,

            org.n52.sos.service.it.v2.kvp.DeleteObservationTest.class,
            org.n52.sos.service.it.v2.kvp.DeleteSensorTest.class,
            org.n52.sos.service.it.v2.kvp.DescribeSensorTest.class,
            org.n52.sos.service.it.v2.kvp.GetCapabilitiesTest.class,
            org.n52.sos.service.it.v2.kvp.GetDataAvailabilityTest.class,
            org.n52.sos.service.it.v2.kvp.GetFeatureOfInterestTest.class,
            org.n52.sos.service.it.v2.kvp.GetObservationByIdTest.class,
            org.n52.sos.service.it.v2.kvp.GetObservationTest.class,
            org.n52.sos.service.it.v2.kvp.GetResultTemplateTest.class,
            org.n52.sos.service.it.v2.kvp.GetResultTest.class,

            org.n52.sos.service.it.v2.soap.DeleteObservationTest.class,
            org.n52.sos.service.it.v2.soap.DeleteSensorTest.class,
            org.n52.sos.service.it.v2.soap.DescribeSensorTest.class,
            org.n52.sos.service.it.v2.soap.GetCapabilitiesTest.class,
            org.n52.sos.service.it.v2.soap.GetDataAvailabilityTest.class,
            org.n52.sos.service.it.v2.soap.GetFeatureOfInterestTest.class,
            org.n52.sos.service.it.v2.soap.GetObservationByIdTest.class,
            org.n52.sos.service.it.v2.soap.GetObservationTest.class,
            org.n52.sos.service.it.v2.soap.GetResultTemplateTest.class,
            org.n52.sos.service.it.v2.soap.GetResultTest.class,
            org.n52.sos.service.it.v2.soap.InsertObservationTest.class,
            org.n52.sos.service.it.v2.soap.InsertResultTemplateTest.class,
            org.n52.sos.service.it.v2.soap.InsertResultTest.class,
            org.n52.sos.service.it.v2.soap.InsertSensorTest.class,
            org.n52.sos.service.it.v2.soap.UpdateSensorDescriptionTest.class,
        };
    }

}
