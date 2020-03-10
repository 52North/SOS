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
package org.n52.sos.decode.kvp.v2;

import java.util.Map;

import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Before;
import org.junit.Test;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Polygon;
import org.locationtech.jts.geom.PrecisionModel;
import org.locationtech.jts.io.WKTWriter;
import org.n52.shetland.ogc.filter.ComparisonFilter;
import org.n52.shetland.ogc.filter.FilterConstants;
import org.n52.shetland.ogc.ows.OWSConstants;
import org.n52.shetland.ogc.sos.Sos2Constants;
import org.n52.shetland.ogc.sos.request.GetObservationRequest;
import org.n52.shetland.ogc.swe.simpleType.SweText;
import org.n52.sos.decode.kvp.KvpTest;
import org.n52.svalbard.decode.exception.DecodingException;

import com.google.common.collect.Maps;

/**
 * @author <a href="mailto:e.h.juerrens@52north.org">Eike Hinderk
 *         J&uuml;rrens</a>
 * @since 4.0.0
 *
 */
public class GetObservationKvpDecoderv20Test implements KvpTest {

    private static final String EPSG_4852 = "4852";

    private static final String MY_OBSERVATION = "MyObservation";

    private static final String GER = "ger";

    private GetObservationKvpDecoderv20 decoder;

    private Polygon polygon;

    private GeometryFactory geometryFactory;

    private String wktGeometry;

    @Before
    public void setUp() {
        this.decoder = new GetObservationKvpDecoderv20();
        this.geometryFactory = new GeometryFactory(new PrecisionModel(PrecisionModel.FLOATING_SINGLE), 4326);
        this.polygon = this.geometryFactory
                .createPolygon(new Coordinate[] { new Coordinate(-15.46, 77.98), new Coordinate(-93.51, 38.27),
                    new Coordinate(47.10, -1.05), new Coordinate(58.71, 70.61), new Coordinate(-15.46, 77.98) });
        this.wktGeometry = new WKTWriter().write(polygon).replaceFirst(" ", "").replaceAll(", ", ",");
    }

    @Test
    public void should_decode_extension_parameter_MergeObservationsIntoDataArray() throws DecodingException {
        final Map<String, String> mapTrue = getDefaultMap();
        mapTrue.put(Sos2Constants.Extensions.MergeObservationsIntoDataArray.name(), "true");
        final GetObservationRequest requestTrue = decoder.decode(mapTrue);

        final Map<String, String> mapFalse = getDefaultMap();
        mapFalse.put(Sos2Constants.Extensions.MergeObservationsIntoDataArray.name(), "false");
        final GetObservationRequest requestFalse = decoder.decode(mapFalse);

     MatcherAssert.assertThat(requestTrue.getExtensions().getBooleanExtension(
                Sos2Constants.Extensions.MergeObservationsIntoDataArray.name()), Matchers.is(Boolean.TRUE));

     MatcherAssert.assertThat(requestFalse.getExtensions().getBooleanExtension(
                Sos2Constants.Extensions.MergeObservationsIntoDataArray.name()), Matchers.is(Boolean.FALSE));
    }

    @Test
    public void should_decode_extension_parameter_language() throws DecodingException {
        final Map<String, String> map = getDefaultMap();
        map.put(OWSConstants.AdditionalRequestParams.language.name(), GER);
        final GetObservationRequest request = decoder.decode(map);

     MatcherAssert.assertThat(request.getExtensions().containsExtension(OWSConstants.AdditionalRequestParams.language),
                Matchers.is(Boolean.TRUE));
     MatcherAssert.assertThat(request.getExtensions().getExtension(OWSConstants.AdditionalRequestParams.language.name())
                .get().getValue(), Matchers.instanceOf(SweText.class));
     MatcherAssert.assertThat(((SweText) request.getExtensions()
                .getExtension(OWSConstants.AdditionalRequestParams.language.name()).get().getValue()).getStringValue(),
                Matchers.is(GER));
    }

    @Test
    public void should_decode_extension_parameter_crs() throws DecodingException {
        final Map<String, String> map = getDefaultMap();
        map.put(OWSConstants.AdditionalRequestParams.crs.name(), EPSG_4852);
        final GetObservationRequest request = decoder.decode(map);

     MatcherAssert.assertThat(request.getExtensions().containsExtension(OWSConstants.AdditionalRequestParams.crs),
                Matchers.is(Boolean.TRUE));
     MatcherAssert.assertThat(
                request.getExtensions().getExtension(OWSConstants.AdditionalRequestParams.crs.name()).get().getValue(),
                Matchers.instanceOf(SweText.class));
     MatcherAssert.assertThat(((SweText) request.getExtensions()
                .getExtension(OWSConstants.AdditionalRequestParams.crs.name()).get().getValue()).getValue(),
                Matchers.is(EPSG_4852));
    }

    @Test
    public void should_decode_extension_parameter_resultType() throws DecodingException {
        final Map<String, String> map = getDefaultMap();
        map.put("resultType", MY_OBSERVATION);
        final GetObservationRequest request = decoder.decode(map);

     MatcherAssert.assertThat(request.isSetResultModel(), Matchers.is(Boolean.TRUE));
     MatcherAssert.assertThat(request.getResultModel(), Matchers.is(MY_OBSERVATION));
    }

    @Test
    public void should_decode_extension_resultFilter() throws DecodingException {
        final Map<String, String> map = getDefaultMap();
        map.put(FILTER, "om:result eq '10.5'");
        final GetObservationRequest request = decoder.decode(map);

     MatcherAssert.assertThat(request.hasResultFilter(), Matchers.is(true));
     MatcherAssert.assertThat(request.getResultFilter().getValueReference(), Matchers.is(OM_RESULT));
     MatcherAssert.assertThat(request.getResultFilter(), Matchers.instanceOf(ComparisonFilter.class));
        ComparisonFilter filter = (ComparisonFilter) request.getResultFilter();
     MatcherAssert.assertThat(filter.getOperator().name(),
                Matchers.is(FilterConstants.ComparisonOperator.PropertyIsEqualTo.name()));
     MatcherAssert.assertThat(filter.getValue(), Matchers.is("10.5"));
    }

    @Test
    public void should_decode_extension_resultFilter_between() throws DecodingException {
        final Map<String, String> map = getDefaultMap();
        map.put(FILTER, "om:result ge '10.0' and om:result le '20.0'");
        final GetObservationRequest request = decoder.decode(map);

     MatcherAssert.assertThat(request.hasResultFilter(), Matchers.is(true));
     MatcherAssert.assertThat(request.getResultFilter().getValueReference(), Matchers.is(OM_RESULT));
     MatcherAssert.assertThat(request.getResultFilter(), Matchers.instanceOf(ComparisonFilter.class));
        ComparisonFilter filter = (ComparisonFilter) request.getResultFilter();
     MatcherAssert.assertThat(filter.getOperator().name(),
                Matchers.is(FilterConstants.ComparisonOperator.PropertyIsBetween.name()));
     MatcherAssert.assertThat(filter.getValue(), Matchers.is("10.0"));
     MatcherAssert.assertThat(filter.getValueUpper(), Matchers.is("20.0"));
    }

    @Test
    public void should_decode_extension_spatialFilter() throws DecodingException {
        final Map<String, String> map = getDefaultMap();
        map.put(FILTER,
                String.format(
                        "geo.intersects(http://www.opengis.net/req/omxml/2.0/data/samplingGeometry,'SRID=%s;%s')",
                        polygon.getSRID(), wktGeometry));
        final GetObservationRequest request = decoder.decode(map);

     MatcherAssert.assertThat(request.hasResultFilter(), Matchers.is(false));
     MatcherAssert.assertThat(request.isSetSpatialFilter(), Matchers.is(true));
     MatcherAssert.assertThat(request.hasSpatialFilteringProfileSpatialFilter(), Matchers.is(true));
     MatcherAssert.assertThat(request.getSpatialFilter().getOperator().name(),
                Matchers.is(FilterConstants.SpatialOperator.BBOX.name()));
    }

    @Test
    public void should_decode_extension_resultFilter_spatialFilter() throws DecodingException {
        final Map<String, String> map = getDefaultMap();
        map.put(FILTER, String.format("result eq '10.5' and geo.intersects(featureOfInterest,'SRID=%s;%s')",
                polygon.getSRID(), wktGeometry));
        final GetObservationRequest request = decoder.decode(map);

     MatcherAssert.assertThat(request.hasResultFilter(), Matchers.is(true));
    }

    private Map<String, String> getDefaultMap() {
        Map<String, String> map = Maps.newHashMap();
        map.put("service", "SOS");
        map.put("version", "2.0.0");
        map.put("request", "GetObservation");
        return map;
    }

}
