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
package org.n52.sos.decode.kvp.v2;

import static java.lang.Boolean.FALSE;
import static java.lang.Boolean.TRUE;
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.n52.sos.ogc.filter.ComparisonFilter;
import org.n52.sos.ogc.filter.FilterConstants;
import org.n52.sos.ogc.ows.OWSConstants;
import org.n52.sos.ogc.ows.OwsExceptionReport;
import org.n52.sos.ogc.sos.Sos2Constants;
import org.n52.sos.ogc.swe.simpleType.SweText;
import org.n52.sos.request.GetObservationRequest;

import com.google.common.collect.Maps;
import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.Polygon;
import com.vividsolutions.jts.geom.PrecisionModel;
import com.vividsolutions.jts.io.WKTWriter;

/**
 * @author <a href="mailto:e.h.juerrens@52north.org">Eike Hinderk
 *         J&uuml;rrens</a>
 * @since 4.0.0
 * 
 */
public class GetObservationKvpDecoderv20Test {
    
    private GetObservationKvpDecoderv20 decoder;
    private Polygon polygon;
    private GeometryFactory geometryFactory;
    private String wktGeometry;

    @Before
    public void setup() {
        this.decoder = new GetObservationKvpDecoderv20();
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
    public void should_decode_extension_parameter_MergeObservationsIntoDataArray() throws OwsExceptionReport {
        final Map<String, String> mapTrue = getDefaultMap();
        mapTrue.put(Sos2Constants.Extensions.MergeObservationsIntoDataArray.name(), "true");
        final GetObservationRequest requestTrue = decoder.decode(mapTrue);

        final Map<String, String> mapFalse = getDefaultMap();
        mapFalse.put(Sos2Constants.Extensions.MergeObservationsIntoDataArray.name(), "false");
        final GetObservationRequest requestFalse = decoder.decode(mapFalse);

        assertThat(requestTrue.isSetExtensions(), is(TRUE));
        assertThat(requestTrue.getExtensions()
                .isBooleanExtensionSet(Sos2Constants.Extensions.MergeObservationsIntoDataArray.name()), is(TRUE));

        assertThat(requestFalse.isSetExtensions(), is(TRUE));
        assertThat(requestFalse.getExtensions()
                .isBooleanExtensionSet(Sos2Constants.Extensions.MergeObservationsIntoDataArray.name()), is(FALSE));
    }

    @Test
    public void should_decode_extension_parameter_language() throws OwsExceptionReport {
        final Map<String, String> map = getDefaultMap();
        map.put(OWSConstants.AdditionalRequestParams.language.name(), "ger");
        final GetObservationRequest request = decoder.decode(map);

        assertThat(request.isSetExtensions(), is(TRUE));
        assertThat(request.getExtensions().containsExtension(OWSConstants.AdditionalRequestParams.language),
                is(TRUE));
        assertThat(request.getExtensions().getExtension(OWSConstants.AdditionalRequestParams.language.name())
                .getValue(), instanceOf(SweText.class));
        assertThat(((SweText) request.getExtensions()
                .getExtension(OWSConstants.AdditionalRequestParams.language.name()).getValue()).getStringValue(),
                is("ger"));
    }

    @Test
    public void should_decode_extension_parameter_crs() throws OwsExceptionReport {
        final Map<String, String> map = getDefaultMap();
        map.put(OWSConstants.AdditionalRequestParams.crs.name(), "4852");
        final GetObservationRequest request = decoder.decode(map);

        assertThat(request.isSetExtensions(), is(TRUE));
        assertThat(request.getExtensions().containsExtension(OWSConstants.AdditionalRequestParams.crs), is(TRUE));
        assertThat(
                request.getExtensions().getExtension(OWSConstants.AdditionalRequestParams.crs.name()).getValue(),
                instanceOf(SweText.class));
        assertThat(((SweText) request.getExtensions()
                .getExtension(OWSConstants.AdditionalRequestParams.crs.name()).getValue()).getValue(), is("4852"));
    }

    @Test
    public void should_decode_extension_parameter_resultType() throws OwsExceptionReport {
        final Map<String, String> map = getDefaultMap();
        map.put("resultType", "MyObservation");
        final GetObservationRequest request = decoder.decode(map);

        assertThat(request.isSetResultModel(), is(TRUE));
        assertThat(request.getResultModel(), is("MyObservation"));
    }
    
    
    @Test
    public void should_decode_extension_resultFilter() throws OwsExceptionReport {
        final Map<String, String> map = getDefaultMap();
        map.put("$filter", "om:result eq 10.5");
        final GetObservationRequest request = decoder.decode(map);

        assertThat(request.hasResultFilter(), is(true));
        assertThat(request.getResultFilter().getValueReference(), is("om:result"));
        assertThat(request.getResultFilter() instanceof ComparisonFilter, is(true));
        assertThat(((ComparisonFilter) request.getResultFilter()).getOperator().name(), is(FilterConstants.ComparisonOperator.PropertyIsEqualTo.name())); 
        assertThat(((ComparisonFilter) request.getResultFilter()).getValue(), is("10.5")); 
    }
    
    @Test
    public void should_decode_extension_resultFilter_between() throws OwsExceptionReport {
        final Map<String, String> map = getDefaultMap();
        map.put("$filter", "om:result ge 10.0 and om:result le 20.0");
        final GetObservationRequest request = decoder.decode(map);
        
        assertThat(request.hasResultFilter(), is(true));
        assertThat(request.getResultFilter().getValueReference(), is("om:result"));
        assertThat(request.getResultFilter() instanceof ComparisonFilter, is(true));
        assertThat(((ComparisonFilter) request.getResultFilter()).getOperator().name(), is(FilterConstants.ComparisonOperator.PropertyIsBetween.name())); 
        assertThat(((ComparisonFilter) request.getResultFilter()).getValue(), is("10.0")); 
        assertThat(((ComparisonFilter) request.getResultFilter()).getValueUpper(), is("20.0")); 
    }
    
    @Test
    public void should_decode_extension_spatialFilter() throws OwsExceptionReport {
        final Map<String, String> map = getDefaultMap();
        map.put("$filter", String
                .format("geo.intersects(http://www.opengis.net/req/omxml/2.0/data/samplingGeometry,'SRID=%s;%s')",
                        polygon.getSRID(), wktGeometry));
        final GetObservationRequest request = decoder.decode(map);
        
        assertThat(request.hasResultFilter(), is(false));
        assertThat(request.isSetSpatialFilter(), is(true));
        assertThat(request.hasSpatialFilteringProfileSpatialFilter(), is(true));
        assertThat(request.getSpatialFilter().getOperator().name(), is(FilterConstants.SpatialOperator.BBOX.name())); 
    }
    
    @Test
    public void should_decode_extension_resultFilter_spatialFilter() throws OwsExceptionReport {
        final Map<String, String> map = getDefaultMap();
        map.put("$filter", String
                .format("result eq 10.5 and geo.intersects(featureOfInterest,'SRID=%s;%s')",
                        polygon.getSRID(), wktGeometry));
        final GetObservationRequest request = decoder.decode(map);
        
        assertThat(request.hasResultFilter(), is(true));
    }
    
    private Map<String, String> getDefaultMap() {
        Map<String, String> map = Maps.newHashMap();
        map.put("service", "SOS");
        map.put("version", "2.0.0");
        map.put("request", "GetObservation");
        return map;
    }

}
