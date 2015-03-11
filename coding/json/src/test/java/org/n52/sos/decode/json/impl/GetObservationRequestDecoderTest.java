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
package org.n52.sos.decode.json.impl;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.Assert.assertThat;

import java.io.IOException;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ErrorCollector;
import org.n52.sos.ConfiguredSettingsManager;
import org.n52.sos.ogc.filter.FilterConstants.SpatialOperator;
import org.n52.sos.ogc.filter.FilterConstants.TimeOperator;
import org.n52.sos.ogc.gml.time.TimePeriod;
import org.n52.sos.ogc.ows.OwsExceptionReport;
import org.n52.sos.request.GetObservationRequest;
import org.n52.sos.util.Constants;

import com.fasterxml.jackson.databind.JsonNode;
import com.github.fge.jackson.JsonLoader;
import com.vividsolutions.jts.geom.Point;

/**
 * TODO JavaDoc
 * 
 * @author Christian Autermann <c.autermann@52north.org>
 * 
 * @since 4.0.0
 */
public class GetObservationRequestDecoderTest {
    @ClassRule
    public static final ConfiguredSettingsManager csm = new ConfiguredSettingsManager();

    private GetObservationRequestDecoder decoder;

    @Rule
    public final ErrorCollector errors = new ErrorCollector();

    @Before
    public void before() {
        this.decoder = new GetObservationRequestDecoder();
    }

    @Test
    public void hasRequest() throws IOException, OwsExceptionReport {
        assertThat(loadSingle().getOperationName(), is(equalTo("GetObservation")));
    }

    @Test
    public void hasVersion() throws IOException, OwsExceptionReport {
        assertThat(loadSingle().getVersion(), is(equalTo("2.0.0")));
    }

    @Test
    public void hasService() throws IOException, OwsExceptionReport {
        assertThat(loadSingle().getService(), is(equalTo("SOS")));
    }

    @Test
    public void hasProcedure() throws IOException, OwsExceptionReport {
        final GetObservationRequest req = loadSingle();
        assertThat(req.getProcedures(), is(notNullValue()));
        assertThat(req.getProcedures(), hasSize(1));
        assertThat(req.getProcedures().get(0), is(notNullValue()));
        assertThat(req.getProcedures().get(0), is(equalTo("procedure1")));
    }

    @Test
    public void hasProcedures() throws IOException, OwsExceptionReport {
        final GetObservationRequest req = loadMultiple();
        assertThat(req.getProcedures(), is(notNullValue()));
        assertThat(req.getProcedures(), hasSize(2));
        assertThat(req.getProcedures().get(0), is(notNullValue()));
        assertThat(req.getProcedures().get(0), is(equalTo("procedure1")));
        assertThat(req.getProcedures().get(1), is(notNullValue()));
        assertThat(req.getProcedures().get(1), is(equalTo("procedure2")));

    }

    @Test
    public void hasOffering() throws IOException, OwsExceptionReport {
        final GetObservationRequest req = loadSingle();
        assertThat(req.getOfferings(), is(notNullValue()));
        assertThat(req.getOfferings(), hasSize(1));
        assertThat(req.getOfferings().get(0), is(notNullValue()));
        assertThat(req.getOfferings().get(0), is(equalTo("offering1")));
    }

    @Test
    public void hasOfferings() throws IOException, OwsExceptionReport {
        final GetObservationRequest req = loadMultiple();
        assertThat(req.getOfferings(), is(notNullValue()));
        assertThat(req.getOfferings(), hasSize(2));
        assertThat(req.getOfferings().get(0), is(notNullValue()));
        assertThat(req.getOfferings().get(0), is(equalTo("offering1")));
        assertThat(req.getOfferings().get(1), is(notNullValue()));
        assertThat(req.getOfferings().get(1), is(equalTo("offering2")));

    }

    @Test
    public void hasObservedProperty() throws IOException, OwsExceptionReport {
        final GetObservationRequest req = loadSingle();
        assertThat(req.getObservedProperties(), is(notNullValue()));
        assertThat(req.getObservedProperties(), hasSize(1));
        assertThat(req.getObservedProperties().get(0), is(notNullValue()));
        assertThat(req.getObservedProperties().get(0), is(equalTo("observedProperty1")));
    }

    @Test
    public void hasObservedProperties() throws IOException, OwsExceptionReport {
        final GetObservationRequest req = loadMultiple();
        assertThat(req.getObservedProperties(), is(notNullValue()));
        assertThat(req.getObservedProperties(), hasSize(2));
        assertThat(req.getObservedProperties().get(0), is(notNullValue()));
        assertThat(req.getObservedProperties().get(0), is(equalTo("observedProperty1")));
        assertThat(req.getObservedProperties().get(1), is(notNullValue()));
        assertThat(req.getObservedProperties().get(1), is(equalTo("observedProperty2")));
    }

    @Test
    public void hasFeatureOfInterest() throws IOException, OwsExceptionReport {
        final GetObservationRequest req = loadSingle();
        assertThat(req.getFeatureIdentifiers(), is(notNullValue()));
        assertThat(req.getFeatureIdentifiers(), hasSize(1));
        assertThat(req.getFeatureIdentifiers().get(0), is(notNullValue()));
        assertThat(req.getFeatureIdentifiers().get(0), is(equalTo("featureOfInterest1")));
    }

    @Test
    public void hasFeaturesOfInterest() throws IOException, OwsExceptionReport {
        final GetObservationRequest req = loadMultiple();
        assertThat(req.getFeatureIdentifiers(), is(notNullValue()));
        assertThat(req.getFeatureIdentifiers(), hasSize(2));
        assertThat(req.getFeatureIdentifiers().get(0), is(notNullValue()));
        assertThat(req.getFeatureIdentifiers().get(0), is(equalTo("featureOfInterest1")));
        assertThat(req.getFeatureIdentifiers().get(1), is(notNullValue()));
        assertThat(req.getFeatureIdentifiers().get(1), is(equalTo("featureOfInterest2")));
    }

    @Test
    public void hasSpatialFilter() throws IOException, OwsExceptionReport {
        final GetObservationRequest req = loadSingle();
        assertThat(req.getSpatialFilter(), is(notNullValue()));
        assertThat(req.getSpatialFilter().getOperator(), is(SpatialOperator.Equals));
        assertThat(req.getSpatialFilter().getValueReference(),
                is("om:featureOfInterest/sams:SF_SpatialSamplingFeature/sams:shape"));
        assertThat(req.getSpatialFilter().getGeometry(), is(notNullValue()));
        assertThat(req.getSpatialFilter().getGeometry(), is(instanceOf(Point.class)));
        assertThat(req.getSpatialFilter().getGeometry().getSRID(), is(Constants.EPSG_WGS84));
        assertThat(req.getSpatialFilter().getGeometry().getCoordinate().x, is(51.0));
        assertThat(req.getSpatialFilter().getGeometry().getCoordinate().y, is(8.0));
        assertThat(Double.isNaN(req.getSpatialFilter().getGeometry().getCoordinate().z), is(true));
    }

    @Test
    public void hasNoTemporalFilters() throws IOException, OwsExceptionReport {
        final GetObservationRequest req = loadNoFilters();
        assertThat(req.getTemporalFilters(), is(notNullValue()));
        assertThat(req.getTemporalFilters(), hasSize(0));
    }

    @Test
    public void hasSingleTemporalFilter() throws IOException, OwsExceptionReport {
        final GetObservationRequest req = loadSingle();
        assertThat(req.getTemporalFilters(), is(notNullValue()));
        assertThat(req.getTemporalFilters(), hasSize(1));
        assertThat(req.getTemporalFilters().get(0), is(notNullValue()));
        assertThat(req.getTemporalFilters().get(0).getOperator(), is(TimeOperator.TM_Equals));
        assertThat(req.getTemporalFilters().get(0).getValueReference(), is("om:phenomenonTime"));
        assertThat(req.getTemporalFilters().get(0).getTime(), is(instanceOf(TimePeriod.class)));
        final TimePeriod time = (TimePeriod) req.getTemporalFilters().get(0).getTime();
        assertThat(time.getStart(),
                is(equalTo(new DateTime(2013, 01, 01, 00, 00, 00, 00, DateTimeZone.forOffsetHours(2)))));
        assertThat(time.getEnd(),
                is(equalTo(new DateTime(2013, 01, 01, 01, 00, 00, 00, DateTimeZone.forOffsetHours(2)))));
    }

    @Test
    public void hasMultipleTemporalFilters() throws IOException, OwsExceptionReport {
        final GetObservationRequest req = loadMultiple();
        assertThat(req.getTemporalFilters(), is(notNullValue()));
        assertThat(req.getTemporalFilters(), hasSize(2));
        assertThat(req.getTemporalFilters().get(0), is(notNullValue()));
        assertThat(req.getTemporalFilters().get(0).getOperator(), is(TimeOperator.TM_Equals));
        assertThat(req.getTemporalFilters().get(0).getValueReference(), is("om:phenomenonTime"));
        assertThat(req.getTemporalFilters().get(0).getTime(), is(instanceOf(TimePeriod.class)));
        final TimePeriod time1 = (TimePeriod) req.getTemporalFilters().get(0).getTime();
        assertThat(time1.getStart(),
                is(equalTo(new DateTime(2013, 01, 01, 00, 00, 00, 00, DateTimeZone.forOffsetHours(2)))));
        assertThat(time1.getEnd(),
                is(equalTo(new DateTime(2013, 01, 01, 01, 00, 00, 00, DateTimeZone.forOffsetHours(2)))));
        assertThat(req.getTemporalFilters().get(1), is(notNullValue()));
        assertThat(req.getTemporalFilters().get(1).getOperator(), is(TimeOperator.TM_Equals));
        assertThat(req.getTemporalFilters().get(1).getValueReference(), is("om:phenomenonTime"));
        assertThat(req.getTemporalFilters().get(1).getTime(), is(instanceOf(TimePeriod.class)));
        final TimePeriod time2 = (TimePeriod) req.getTemporalFilters().get(1).getTime();
        assertThat(time2.getStart(),
                is(equalTo(new DateTime(2013, 01, 01, 20, 00, 00, 00, DateTimeZone.forOffsetHours(2)))));
        assertThat(time2.getEnd(),
                is(equalTo(new DateTime(2013, 01, 01, 22, 00, 00, 00, DateTimeZone.forOffsetHours(2)))));
    }
    
    @Test
    public void hasMergeObservationsIntoDataArrayExtension() throws IOException, OwsExceptionReport {
        final GetObservationRequest req = loadMergeIntoArray();
        assertThat(req.getExtensions(), is(notNullValue()));
        assertThat(req.getExtensions().isBooleanExtensionSet("MergeObservationsIntoDataArray"), is(true));
   }

    protected GetObservationRequest loadSingle() throws OwsExceptionReport, IOException {
        final JsonNode json = JsonLoader.fromResource("/examples/sos/GetObservationRequest-single.json");
        final GetObservationRequest req = decoder.decodeJSON(json, true);
        assertThat(req, is(notNullValue()));
        return req;
    }

    protected GetObservationRequest loadMultiple() throws OwsExceptionReport, IOException {
        final JsonNode json = JsonLoader.fromResource("/examples/sos/GetObservationRequest-multiple.json");
        final GetObservationRequest req = decoder.decodeJSON(json, true);
        assertThat(req, is(notNullValue()));
        return req;
    }

    protected GetObservationRequest loadNoFilters() throws OwsExceptionReport, IOException {
        final JsonNode json = JsonLoader.fromResource("/examples/sos/GetObservationRequest-no-filters.json");
        final GetObservationRequest req = decoder.decodeJSON(json, true);
        assertThat(req, is(notNullValue()));
        return req;
    }
    
    protected GetObservationRequest loadMergeIntoArray() throws OwsExceptionReport, IOException {
        final JsonNode json = JsonLoader.fromResource("/examples/sos/GetObservationRequest-merge-into-array.json");
        final GetObservationRequest req = decoder.decodeJSON(json, true);
        assertThat(req, is(notNullValue()));
        return req;
    }
}
