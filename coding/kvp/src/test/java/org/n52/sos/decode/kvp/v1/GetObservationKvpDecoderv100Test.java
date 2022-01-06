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
package org.n52.sos.decode.kvp.v1;

import java.util.HashMap;
import java.util.Map;

import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.Polygon;
import org.n52.janmayen.http.MediaType;
import org.n52.shetland.ogc.OGCConstants;
import org.n52.shetland.ogc.filter.FilterConstants.SpatialOperator;
import org.n52.shetland.ogc.filter.TemporalFilter;
import org.n52.shetland.ogc.gml.time.TimePeriod;
import org.n52.shetland.ogc.ows.OWSConstants.RequestParams;
import org.n52.shetland.ogc.ows.exception.OwsExceptionReport;
import org.n52.shetland.ogc.sos.Sos1Constants;
import org.n52.shetland.ogc.sos.SosConstants;
import org.n52.shetland.ogc.sos.request.GetObservationRequest;
import org.n52.sos.decode.kvp.v2.DeleteSensorKvpDecoderv20;
import org.n52.svalbard.decode.exception.DecodingException;

/**
 * @author <a href="mailto:shane@axiomalaska.com">Shane StClair</a>
 * @since 4.0.0
 */
public class GetObservationKvpDecoderv100Test extends DeleteSensorKvpDecoderv20 {
    private static final String OFFERING = "testOffering";

    private static final String PROCEDURE = "testProcedure";

    private static final String OBSERVED_PROPERTY = "testObservedProperty";

    private static final String RESPONSE_FORMAT = "text/xml;subtype=\"some/fake/subytpe\"";

    private static final String START_TIME = "2012-11-19T14:00:00+01:00";

    private static final String END_TIME = "2012-11-19T14:15:00+01:00";

    private static final String TIME_PERIOD = "PT15M";

    private static final String EVENT_TIME_START_TIME_END_TIME = "om:phenomenonTime," + START_TIME + "/" + END_TIME;

    private static final String EVENT_TIME_START_TIME_PERIOD = "om:phenomenonTime," + START_TIME + "/" + TIME_PERIOD;

    private static final String EVENT_TIME_PERIOD_END_TIME = "om:phenomenonTime," + TIME_PERIOD + "/" + END_TIME;

    private static final String SPATIAL_FILTER_VALUE_REFERENCE = "om:featureOfInterest/*/sams:shape";

    private static final String SPATIAL_FILTER =
            SPATIAL_FILTER_VALUE_REFERENCE + ",0.0,0.0,60.0,60.0,urn:ogc:def:crs:EPSG::4326";

    private static final String ADDITIONAL_PARAMETER = "additionalParameter";

    private static final String EMPTY_STRING = "";

    private static final int EPSG_WGS84 = 4326;

    private GetObservationKvpDecoderv100 decoder;

    @Before
    public void setUp() {
        this.decoder = new GetObservationKvpDecoderv100();
        this.decoder.setSrsUrlNamePrefix(OGCConstants.URN_DEF_CRS_EPSG);
        this.decoder.setSrsUrnNamePrefix(OGCConstants.URL_DEF_CRS_EPSG);
        this.decoder.setStorageEPSG(EPSG_WGS84);
    }

    @Test
    public void basic() throws DecodingException {
        GetObservationRequest req = decoder.decode(createMap(SosConstants.SOS, Sos1Constants.SERVICEVERSION, OFFERING,
                PROCEDURE, OBSERVED_PROPERTY, RESPONSE_FORMAT));
        MatcherAssert.assertThat(req, CoreMatchers.is(CoreMatchers.notNullValue()));
        MatcherAssert.assertThat(req.getOperationName(),
                CoreMatchers.is(SosConstants.Operations.GetObservation.name()));
        MatcherAssert.assertThat(req.getService(), CoreMatchers.is(SosConstants.SOS));
        MatcherAssert.assertThat(req.getVersion(), CoreMatchers.is(Sos1Constants.SERVICEVERSION));
        MatcherAssert.assertThat(req.getOfferings().size(), CoreMatchers.is(1));
        MatcherAssert.assertThat(req.getOfferings().get(0), CoreMatchers.is(OFFERING));
        MatcherAssert.assertThat(req.getProcedures().size(), CoreMatchers.is(1));
        MatcherAssert.assertThat(req.getProcedures().get(0), CoreMatchers.is(PROCEDURE));
        MatcherAssert.assertThat(req.getObservedProperties().size(), CoreMatchers.is(1));
        MatcherAssert.assertThat(req.getObservedProperties().get(0), CoreMatchers.is(OBSERVED_PROPERTY));
        MatcherAssert.assertThat(MediaType.parse(req.getResponseFormat()),
                CoreMatchers.is(MediaType.parse(RESPONSE_FORMAT)));
    }

    public void eventTime_startTime_endTime() throws OwsExceptionReport, DecodingException {
        Map<String, String> map = createMap(SosConstants.SOS, Sos1Constants.SERVICEVERSION, OFFERING, PROCEDURE,
                OBSERVED_PROPERTY, RESPONSE_FORMAT);
        map.put(Sos1Constants.GetObservationParams.eventTime.name(), EVENT_TIME_START_TIME_END_TIME);
        GetObservationRequest req = decoder.decode(map);
        MatcherAssert.assertThat(req.getTemporalFilters().size(), CoreMatchers.is(1));
        TemporalFilter temporalFilter = req.getTemporalFilters().get(0);
        MatcherAssert.assertThat(temporalFilter.getTime(), CoreMatchers.instanceOf(TimePeriod.class));
        TimePeriod timePeriod = (TimePeriod) temporalFilter.getTime();
        MatcherAssert.assertThat(timePeriod.getStart().getMillis(),
                CoreMatchers.is(new DateTime(START_TIME).getMillis()));
        MatcherAssert.assertThat(timePeriod.getEnd().getMillis(),
                CoreMatchers.is(new DateTime(END_TIME).plusMillis(999).getMillis()));
        MatcherAssert.assertThat(timePeriod.getStartIndet(), CoreMatchers.nullValue());
        MatcherAssert.assertThat(timePeriod.getEndIndet(), CoreMatchers.nullValue());
    }

    @Test
    public void eventTime_startTime_period() throws OwsExceptionReport, DecodingException {
        Map<String, String> map = createMap(SosConstants.SOS, Sos1Constants.SERVICEVERSION, OFFERING, PROCEDURE,
                OBSERVED_PROPERTY, RESPONSE_FORMAT);
        map.put(Sos1Constants.GetObservationParams.eventTime.name(), EVENT_TIME_START_TIME_PERIOD);
        GetObservationRequest req = decoder.decode(map);
        MatcherAssert.assertThat(req.getTemporalFilters().size(), CoreMatchers.is(1));
        TemporalFilter temporalFilter = req.getTemporalFilters().get(0);
        MatcherAssert.assertThat(temporalFilter.getTime(), CoreMatchers.instanceOf(TimePeriod.class));
        TimePeriod timePeriod = (TimePeriod) temporalFilter.getTime();
        MatcherAssert.assertThat(timePeriod.getStart().getMillis(),
                CoreMatchers.is(new DateTime(START_TIME).getMillis()));
        MatcherAssert.assertThat(timePeriod.getEnd().getMillis(), CoreMatchers.is(new DateTime(END_TIME).getMillis()));
        MatcherAssert.assertThat(timePeriod.getStartIndet(), CoreMatchers.nullValue());
        MatcherAssert.assertThat(timePeriod.getEndIndet(), CoreMatchers.nullValue());
    }

    @Test
    public void eventTime_period_endTime() throws OwsExceptionReport, DecodingException {
        Map<String, String> map = createMap(SosConstants.SOS, Sos1Constants.SERVICEVERSION, OFFERING, PROCEDURE,
                OBSERVED_PROPERTY, RESPONSE_FORMAT);
        map.put(Sos1Constants.GetObservationParams.eventTime.name(), EVENT_TIME_PERIOD_END_TIME);
        GetObservationRequest req = decoder.decode(map);
        MatcherAssert.assertThat(req.getTemporalFilters().size(), CoreMatchers.is(1));
        TemporalFilter temporalFilter = req.getTemporalFilters().get(0);
        MatcherAssert.assertThat(temporalFilter.getTime(), CoreMatchers.instanceOf(TimePeriod.class));
        TimePeriod timePeriod = (TimePeriod) temporalFilter.getTime();
        MatcherAssert.assertThat(timePeriod.getStart().getMillis(),
                CoreMatchers.is(new DateTime(START_TIME).getMillis()));
        MatcherAssert.assertThat(timePeriod.getEnd().getMillis(),
                CoreMatchers.is(new DateTime(END_TIME).plusMillis(999).getMillis()));
        MatcherAssert.assertThat(timePeriod.getStartIndet(), CoreMatchers.nullValue());
        MatcherAssert.assertThat(timePeriod.getEndIndet(), CoreMatchers.nullValue());
    }

    @Test
    public void spatialFilter() throws DecodingException {
        Map<String, String> map = createMap(SosConstants.SOS, Sos1Constants.SERVICEVERSION, OFFERING, PROCEDURE,
                OBSERVED_PROPERTY, RESPONSE_FORMAT);
        map.put(SosConstants.GetObservationParams.featureOfInterest.name(), SPATIAL_FILTER);
        GetObservationRequest req = decoder.decode(map);
        MatcherAssert.assertThat(req.getSpatialFilter().getSrid(), CoreMatchers.is(EPSG_WGS84));
        MatcherAssert.assertThat(req.getSpatialFilter().getOperator(), CoreMatchers.is(SpatialOperator.BBOX));
        MatcherAssert.assertThat(req.getSpatialFilter().getValueReference(),
                CoreMatchers.is(SPATIAL_FILTER_VALUE_REFERENCE));
        MatcherAssert.assertThat(req.getSpatialFilter().getGeometry().getSRID(), CoreMatchers.is(EPSG_WGS84));
        MatcherAssert.assertThat(req.getSpatialFilter().getGeometry().isGeometry(), CoreMatchers.is(true));
        Geometry geometry = req.getSpatialFilter().getGeometry().getGeometry().get();
        MatcherAssert.assertThat(geometry, CoreMatchers.instanceOf(Polygon.class));
        Polygon polygon = (Polygon) geometry;
        MatcherAssert.assertThat(polygon.getExteriorRing().getPointN(0).getX(), CoreMatchers.is(0.0));
        MatcherAssert.assertThat(polygon.getExteriorRing().getPointN(0).getY(), CoreMatchers.is(0.0));
        MatcherAssert.assertThat(polygon.getExteriorRing().getPointN(2).getX(), CoreMatchers.is(60.0));
        MatcherAssert.assertThat(polygon.getExteriorRing().getPointN(2).getY(), CoreMatchers.is(60.0));
    }

    @Test(expected = DecodingException.class)
    public void missingResponse() throws DecodingException {
        decoder.decode(createMap(SosConstants.SOS, Sos1Constants.SERVICEVERSION, OFFERING, PROCEDURE,
                OBSERVED_PROPERTY, EMPTY_STRING));
    }

    @Test(expected = DecodingException.class)
    public void additionalParameter() throws DecodingException {
        final Map<String, String> map = createMap(SosConstants.SOS, Sos1Constants.SERVICEVERSION, OFFERING, PROCEDURE,
                OBSERVED_PROPERTY, RESPONSE_FORMAT);
        map.put(ADDITIONAL_PARAMETER, ADDITIONAL_PARAMETER);
        decoder.decode(map);
    }

    private Map<String, String> createMap(String service, String version, String offering, String procedure,
                                          String observedProperty, String responseFormat) {
        Map<String, String> map = new HashMap<>(7);
        map.put(RequestParams.service.name(), service);
        map.put(RequestParams.request.name(), SosConstants.Operations.GetObservation.name());
        map.put(RequestParams.version.name(), version);
        map.put(SosConstants.GetObservationParams.offering.name(), offering);
        map.put(SosConstants.GetObservationParams.procedure.name(), procedure);
        map.put(SosConstants.GetObservationParams.observedProperty.name(), observedProperty);
        map.put(SosConstants.GetObservationParams.responseFormat.name(), responseFormat);
        return map;
    }
}
