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
package org.n52.sos.decode.kvp.v1;

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertThat;

import java.util.HashMap;
import java.util.Map;

import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;
import org.n52.sos.decode.kvp.v2.DeleteSensorKvpDecoderv20;
import org.n52.sos.ogc.filter.TemporalFilter;
import org.n52.sos.ogc.gml.time.TimePeriod;
import org.n52.sos.ogc.ows.OWSConstants.RequestParams;
import org.n52.sos.ogc.ows.OwsExceptionReport;
import org.n52.sos.ogc.sos.Sos1Constants;
import org.n52.sos.ogc.sos.SosConstants;
import org.n52.sos.request.GetObservationRequest;
import org.n52.sos.util.Constants;
import org.n52.sos.util.http.MediaType;

/**
 * @author Shane StClair <shane@axiomalaska.com>
 * @since 4.0.0
 */
public class GetObservationKvpDecoderv100Test extends DeleteSensorKvpDecoderv20 {
    private static final String OFFERING = "testOffering";

    private static final String PROCEDURE = "testProcedure";

    private static final String OBSERVED_PROPERTY = "testObservedProperty";

    private static final String RESPONSE_FORMAT = "text/xml;subtype=\"some/fake/subytpe\"";

    private static final String START_TIME = "2012-11-19T14:00:00+01:00";

    private static final String END_TIME = "2012-11-19T14:15:00+01:00";

    private static final String EVENT_TIME = "om:phenomenonTime," + START_TIME + "/" + END_TIME;

    private static final String SPATIAL_FILTER_VALUE_REFERENCE = "om:featureOfInterest/*/sams:shape";

    private static final String SPATIAL_FILTER = SPATIAL_FILTER_VALUE_REFERENCE
            + ",0.0,0.0,60.0,60.0,urn:ogc:def:crs:EPSG::" + Constants.EPSG_WGS84;

    private static final String ADDITIONAL_PARAMETER = "additionalParameter";

    private static final String EMPTY_STRING = "";

    private GetObservationKvpDecoderv100 decoder;

    @Before
    public void setUp() {
        this.decoder = new GetObservationKvpDecoderv100();
    }

    @Test
    public void basic() throws OwsExceptionReport {
        GetObservationRequest req =
                decoder.decode(createMap(SosConstants.SOS, Sos1Constants.SERVICEVERSION, OFFERING, PROCEDURE,
                        OBSERVED_PROPERTY, RESPONSE_FORMAT));
        assertThat(req, is(notNullValue()));
        assertThat(req.getOperationName(), is(SosConstants.Operations.GetObservation.name()));
        assertThat(req.getService(), is(SosConstants.SOS));
        assertThat(req.getVersion(), is(Sos1Constants.SERVICEVERSION));
        assertThat(req.getOfferings().size(), is(1));
        assertThat(req.getOfferings().get(0), is(OFFERING));
        assertThat(req.getProcedures().size(), is(1));
        assertThat(req.getProcedures().get(0), is(PROCEDURE));
        assertThat(req.getObservedProperties().size(), is(1));
        assertThat(req.getObservedProperties().get(0), is(OBSERVED_PROPERTY));
        assertThat(MediaType.parse(req.getResponseFormat()), is(MediaType.parse(RESPONSE_FORMAT)));
    }

    @Test
    public void eventTime() throws OwsExceptionReport {
        Map<String, String> map =
                createMap(SosConstants.SOS, Sos1Constants.SERVICEVERSION, OFFERING, PROCEDURE, OBSERVED_PROPERTY,
                        RESPONSE_FORMAT);
        map.put(Sos1Constants.GetObservationParams.eventTime.name(), EVENT_TIME);
        GetObservationRequest req = decoder.decode(map);
        assertThat(req.getTemporalFilters().size(), is(1));
        TemporalFilter temporalFilter = req.getTemporalFilters().get(0);
        assertThat(temporalFilter.getTime(), instanceOf(TimePeriod.class));
        TimePeriod timePeriod = (TimePeriod) temporalFilter.getTime();
        assertThat(timePeriod.getStart().getMillis(), is(new DateTime(START_TIME).getMillis()));
        assertThat(timePeriod.getEnd().getMillis(), is(new DateTime(END_TIME).plusMillis(999).getMillis()));
        assertThat(timePeriod.getStartIndet(), nullValue());
        assertThat(timePeriod.getEndIndet(), nullValue());
    }

    // TODO: This fails without a Configurator loaded. Research how to set this
    // up for a test.
    // @Test
    // public void spatialFilter() throws OwsExceptionReport {
    // Map<String, String> map = createMap(SosConstants.SOS,
    // Sos1Constants.SERVICEVERSION, OFFERING, PROCEDURE,
    // OBSERVED_PROPERTY, RESPONSE_FORMAT);
    // map.put(SosConstants.GetObservationParams.featureOfInterest.name(),
    // SPATIAL_FILTER);
    // GetObservationRequest req = decoder.decode(map);
    // assertThat(req.getSpatialFilter().getSrid(), is(Constants.EPSG_WGS84));
    // assertThat(req.getSpatialFilter().getOperator(),
    // is(SpatialOperator.BBOX));
    // assertThat(req.getSpatialFilter().getValueReference(),
    // is(SPATIAL_FILTER_VALUE_REFERENCE));
    // assertThat(req.getSpatialFilter().getGeometry().getSRID(), is(Constants.EPSG_WGS84));
    // assertThat(req.getSpatialFilter().getGeometry(),
    // instanceOf(Polygon.class));
    // Polygon polygon = (Polygon) req.getSpatialFilter().getGeometry();
    // assertThat(polygon.getExteriorRing().getPointN(0).getX(), is(0.0));
    // assertThat(polygon.getExteriorRing().getPointN(0).getY(), is(0.0));
    // assertThat(polygon.getExteriorRing().getPointN(2).getX(), is(60.0));
    // assertThat(polygon.getExteriorRing().getPointN(2).getY(), is(60.0));
    // }

    @Test(expected = OwsExceptionReport.class)
    public void missingService() throws OwsExceptionReport {
        decoder.decode(createMap(EMPTY_STRING, Sos1Constants.SERVICEVERSION, EMPTY_STRING, PROCEDURE,
                OBSERVED_PROPERTY, RESPONSE_FORMAT));
    }

    @Test(expected = OwsExceptionReport.class)
    public void missingVersion() throws OwsExceptionReport {
        decoder.decode(createMap(SosConstants.SOS, EMPTY_STRING, OFFERING, PROCEDURE, OBSERVED_PROPERTY,
                RESPONSE_FORMAT));
    }

    @Test(expected = OwsExceptionReport.class)
    public void missingOffering() throws OwsExceptionReport {
        decoder.decode(createMap(SosConstants.SOS, Sos1Constants.SERVICEVERSION, EMPTY_STRING, PROCEDURE,
                OBSERVED_PROPERTY, RESPONSE_FORMAT));
    }

    @Test(expected = OwsExceptionReport.class)
    public void missingProcedure() throws OwsExceptionReport {
        decoder.decode(createMap(SosConstants.SOS, Sos1Constants.SERVICEVERSION, OFFERING, EMPTY_STRING,
                OBSERVED_PROPERTY, RESPONSE_FORMAT));
    }

    @Test(expected = OwsExceptionReport.class)
    public void missingObservedProperty() throws OwsExceptionReport {
        decoder.decode(createMap(SosConstants.SOS, Sos1Constants.SERVICEVERSION, OFFERING, PROCEDURE, EMPTY_STRING,
                RESPONSE_FORMAT));
    }

    @Test(expected = OwsExceptionReport.class)
    public void missingResponse() throws OwsExceptionReport {
        decoder.decode(createMap(SosConstants.SOS, Sos1Constants.SERVICEVERSION, OFFERING, PROCEDURE,
                OBSERVED_PROPERTY, EMPTY_STRING));
    }

    @Test(expected = OwsExceptionReport.class)
    public void additionalParameter() throws OwsExceptionReport {
        final Map<String, String> map =
                createMap(SosConstants.SOS, Sos1Constants.SERVICEVERSION, OFFERING, PROCEDURE, OBSERVED_PROPERTY,
                        RESPONSE_FORMAT);
        map.put(ADDITIONAL_PARAMETER, ADDITIONAL_PARAMETER);
        decoder.decode(map);
    }

    private Map<String, String> createMap(String service, String version, String offering, String procedure,
            String observedProperty, String responseFormat) {
        Map<String, String> map = new HashMap<String, String>(1);
        map.put(RequestParams.service.name(), service);
        map.put(RequestParams.request.name(), SosConstants.Operations.DescribeSensor.name());
        map.put(RequestParams.version.name(), version);
        map.put(SosConstants.GetObservationParams.offering.name(), offering);
        map.put(SosConstants.GetObservationParams.procedure.name(), procedure);
        map.put(SosConstants.GetObservationParams.observedProperty.name(), observedProperty);
        map.put(SosConstants.GetObservationParams.responseFormat.name(), responseFormat);
        return map;
    }
}
