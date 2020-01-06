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
package org.n52.sos.service.it.functional;

import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.Assert.assertThat;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.zip.ZipInputStream;

import org.apache.commons.io.IOUtils;
import org.apache.xmlbeans.XmlObject;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.junit.After;
import org.junit.Assume;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ErrorCollector;
import org.n52.sos.ds.hibernate.H2Configuration;
import org.n52.sos.netcdf.NetcdfConstants;
import org.n52.sos.ogc.om.OmConstants;
import org.n52.sos.ogc.om.OmObservation;
import org.n52.sos.ogc.om.values.QuantityValue;
import org.n52.sos.ogc.ows.OWSConstants;
import org.n52.sos.ogc.ows.OwsExceptionReport;
import org.n52.sos.ogc.series.wml.WaterMLConstants;
import org.n52.sos.ogc.sos.Sos1Constants;
import org.n52.sos.ogc.sos.Sos2Constants;
import org.n52.sos.ogc.sos.SosConstants;
import org.n52.sos.request.operator.RequestOperatorKey;
import org.n52.sos.request.operator.RequestOperatorRepository;
import org.n52.sos.service.Configurator;
import org.n52.sos.service.it.Response;
import org.n52.sos.service.operator.ServiceOperatorKey;
import org.n52.sos.util.http.MediaTypes;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Lists;
import com.sun.jna.Native;

import net.opengis.om.x10.ObservationCollectionDocument;
import net.opengis.ows.x11.ExceptionReportDocument;
import net.opengis.sos.x20.GetObservationResponseDocument;
import net.opengis.sos.x20.InsertObservationResponseDocument;
import net.opengis.swes.x20.InsertSensorResponseDocument;
import ucar.nc2.dataset.NetcdfDataset;
import ucar.nc2.jni.netcdf.Nc4prototypes;

public abstract class AbstractObservationEncodingsTest extends AbstractObservationTest {
    protected static final String FEATURE_OF_INTEREST = "featureOfInterest";
    protected static final String PROCEDURE = "procedure";
    protected static final String OFFERING = "offering";
    protected static final String OBSERVABLE_PROPERTY = "http://example.tld/phenomenon/quantity";
    protected static final String UNIT = "unit";

    @Rule
    public final ErrorCollector errors = new ErrorCollector();

    @Before
    public void before() throws OwsExceptionReport {
        ServiceOperatorKey sok = new ServiceOperatorKey(SosConstants.SOS, Sos2Constants.SERVICEVERSION);
        RequestOperatorRepository.getInstance().setActive(new RequestOperatorKey(sok, Sos2Constants.Operations.InsertSensor.name()), true);
        RequestOperatorRepository.getInstance().setActive(new RequestOperatorKey(sok, SosConstants.Operations.InsertObservation.name()), true);

        assertThat(pox().entity(createInsertSensorRequest(PROCEDURE, OFFERING, OBSERVABLE_PROPERTY).xmlText(getXmlOptions())).response().asXmlObject(),
                is(instanceOf(InsertSensorResponseDocument.class)));

        //create 20 observations to insert
        List<OmObservation> observations = Lists.newArrayList();
        for (int i = 0; i < 20; i++) {
            observations.add(createObservation(OmConstants.OBS_TYPE_MEASUREMENT, PROCEDURE, OFFERING,
                    createObservableProperty(OBSERVABLE_PROPERTY), createFeature(FEATURE_OF_INTEREST, createRandomPoint4326()),
                    new DateTime(DateTimeZone.UTC).minusHours(i), new QuantityValue(Math.random() * 10 + 50, UNIT)));
        }

        assertThat(pox().entity(createInsertObservationRequest(observations, OFFERING).xmlText(getXmlOptions())).response().asXmlObject(),
                is(instanceOf(InsertObservationResponseDocument.class)));
    }

    @After
    public void after() throws OwsExceptionReport {
        H2Configuration.truncate();
        Configurator.getInstance().getCacheController().update();
    }

    protected void testGetObsXmlResponse(String serviceVersion, String responseFormat, Class<?> expectedResponseClass)
            throws IOException {
        XmlObject responseDoc = sendGetObsKvp(serviceVersion, responseFormat).asXmlObject();
        assertThat(responseDoc, is(instanceOf(expectedResponseClass)));
    }

    protected Response sendGetObsKvp(String serviceVersion, String responseFormat) {
        return getExecutor().kvp()
                .query(OWSConstants.RequestParams.service, SosConstants.SOS)
                .query(OWSConstants.RequestParams.version, serviceVersion)
                .query(OWSConstants.RequestParams.request, SosConstants.Operations.GetObservation)
                .query(SosConstants.GetObservationParams.procedure, PROCEDURE)
                .query(SosConstants.GetObservationParams.offering, OFFERING)
                .query(SosConstants.GetObservationParams.observedProperty, OBSERVABLE_PROPERTY)
                .query(SosConstants.GetObservationParams.responseFormat, responseFormat)
                .response();
    }

}
