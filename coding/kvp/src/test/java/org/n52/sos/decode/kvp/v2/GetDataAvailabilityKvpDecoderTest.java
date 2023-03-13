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
import org.n52.shetland.ogc.sos.gda.GetDataAvailabilityRequest;
import org.n52.sos.decode.kvp.KvpTest;
import org.n52.svalbard.decode.exception.DecodingException;

import com.google.common.collect.Maps;

/**
 * @since 4.4.2
 *
 */
public class GetDataAvailabilityKvpDecoderTest implements KvpTest {
    private GetDataAvailabilityKvpDecoder decoder;
    private Polygon polygon;
    private GeometryFactory geometryFactory;
    private String wktGeometry;

    @Before
    public void setup() {
        this.decoder = new GetDataAvailabilityKvpDecoder();
        this.geometryFactory = new GeometryFactory(new PrecisionModel(PrecisionModel.FLOATING_SINGLE), 4326);
        this.polygon = this.geometryFactory.createPolygon(new Coordinate[] {
            new Coordinate(-15.46, 77.98),
            new Coordinate(-93.51, 38.27),
            new Coordinate(47.10, -1.05),
            new Coordinate(58.71, 70.61),
            new Coordinate(-15.46, 77.98)
        });
        this.wktGeometry = new WKTWriter().write(polygon).replaceFirst(" ", "").replaceAll(", ", ",");
    }

    @Test
    public void should_decode_extension_resultFilter() throws DecodingException {
        final Map<String, String> map = getDefaultMap();
        map.put(FILTER, "om:result eq '10.5'");
        final GetDataAvailabilityRequest request = decoder.decode(map);

        MatcherAssert.assertThat(request.hasResultFilter(), Matchers.is(true));
        MatcherAssert.assertThat(request.getResultFilter().getValueReference(), Matchers.is(OM_RESULT));
        MatcherAssert.assertThat(request.getResultFilter() instanceof ComparisonFilter, Matchers.is(true));
        ComparisonFilter filter = (ComparisonFilter) request.getResultFilter();
        MatcherAssert.assertThat(filter.getOperator().name(),
                Matchers.is(FilterConstants.ComparisonOperator.PropertyIsEqualTo.name()));
        MatcherAssert.assertThat(filter.getValue(), Matchers.is("10.5"));
    }

    @Test
    public void should_decode_extension_resultFilter_between() throws DecodingException {
        final Map<String, String> map = getDefaultMap();
        map.put(FILTER, "om:result ge '10.0' and om:result le '20.0'");
        final GetDataAvailabilityRequest request = decoder.decode(map);

        MatcherAssert.assertThat(request.hasResultFilter(), Matchers.is(true));
        MatcherAssert.assertThat(request.getResultFilter().getValueReference(), Matchers.is(OM_RESULT));
        MatcherAssert.assertThat(request.getResultFilter() instanceof ComparisonFilter, Matchers.is(true));
        ComparisonFilter filter = (ComparisonFilter) request.getResultFilter();
        MatcherAssert.assertThat(filter.getOperator().name(),
                Matchers.is(FilterConstants.ComparisonOperator.PropertyIsBetween.name()));
        MatcherAssert.assertThat(filter.getValue(), Matchers.is("10.0"));
        MatcherAssert.assertThat(filter.getValueUpper(), Matchers.is("20.0"));
    }

    @Test
    public void should_decode_extension_spatialFilter() throws DecodingException {
        final Map<String, String> map = getDefaultMap();
        map.put(FILTER, String
                .format("geo.intersects(http://www.opengis.net/req/omxml/2.0/data/samplingGeometry,'SRID=%s;%s')",
                        polygon.getSRID(), wktGeometry));
        final GetDataAvailabilityRequest request = decoder.decode(map);

        MatcherAssert.assertThat(request.hasResultFilter(), Matchers.is(false));
        MatcherAssert.assertThat(request.hasSpatialFilter(), Matchers.is(true));
        MatcherAssert.assertThat(request.hasSpatialFilteringProfileSpatialFilter(), Matchers.is(true));
        MatcherAssert.assertThat(request.getSpatialFilter().getOperator().name(),
                Matchers.is(FilterConstants.SpatialOperator.BBOX.name()));
    }

    @Test
    public void should_decode_extension_resultFilter_spatialFilter() throws DecodingException {
        final Map<String, String> map = getDefaultMap();
        map.put(FILTER, String
                .format("result eq '10.5' and geo.intersects(featureOfInterest,'SRID=%s;%s')",
                        polygon.getSRID(), wktGeometry));
        final GetDataAvailabilityRequest request = decoder.decode(map);

        MatcherAssert.assertThat(request.hasResultFilter(), Matchers.is(true));
    }

    private Map<String, String> getDefaultMap() {
        Map<String, String> map = Maps.newHashMap();
        map.put("service", "SOS");
        map.put("version", "2.0.0");
        map.put("request", "GetDataAvailability");
        return map;
    }
}
