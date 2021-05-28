/*
 * Copyright (C) 2012-2021 52°North Spatial Information Research GmbH
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
package org.n52.sos.statistics.sos.models;

import java.util.Map;

import org.junit.Assert;
import org.junit.Test;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.io.ParseException;
import org.n52.iceland.statistics.api.parameters.ObjectEsParameterFactory;
import org.n52.shetland.ogc.filter.FilterConstants.SpatialOperator;
import org.n52.shetland.ogc.filter.SpatialFilter;
import org.n52.shetland.ogc.ows.exception.OwsExceptionReport;
import org.n52.shetland.util.JTSHelper;
import org.n52.svalbard.decode.exception.DecodingException;

public class SpatialFilterEsModelTest {

    private static final String VAL_REF = "value-ref";
    private static final String WKT_POLYGON = "POLYGON ((30 10, 40 40, 20 40, 10 20, 30 10))";

    @Test
    public void createBBOXGeometryAndConvert() throws OwsExceptionReport, DecodingException, ParseException {
        Geometry geom = JTSHelper.createGeometryFromWKT(WKT_POLYGON, 4326);
        SpatialFilter filter = new SpatialFilter(SpatialOperator.BBOX, geom, VAL_REF);

        Map<String, Object> map = SpatialFilterEsModel.convert(filter);

        Assert.assertEquals(VAL_REF, map.get(ObjectEsParameterFactory.SPATIAL_FILTER_VALUE_REF.getName()));
        Assert.assertEquals(SpatialOperator.BBOX.toString(),
                map.get(ObjectEsParameterFactory.SPATIAL_FILTER_OPERATOR.getName()));
        Assert.assertNotNull(map.get(ObjectEsParameterFactory.SPATIAL_FILTER_SHAPE.getName()));
    }

    @Test
    public void createInvalidOperatorTypeGeometry() throws OwsExceptionReport, DecodingException, ParseException {
        Geometry geom = JTSHelper.createGeometryFromWKT(WKT_POLYGON, 4326);
        SpatialFilter filter = new SpatialFilter(SpatialOperator.Crosses, geom, VAL_REF);

        Map<String, Object> map = SpatialFilterEsModel.convert(filter);

        Assert.assertNull(map);
    }

    @Test(expected = IllegalArgumentException.class)
    public void createInvalidGeometry() throws OwsExceptionReport, DecodingException, ParseException {
        Geometry geom = JTSHelper.createGeometryFromWKT("POLYGON ((40 40, 20 40, 10 20, 30 10))", 4326);
        SpatialFilter filter = new SpatialFilter(SpatialOperator.Crosses, geom, VAL_REF);

        Map<String, Object> map = SpatialFilterEsModel.convert(filter);

        Assert.assertNull(map);
    }

    @Test
    public void returnNullMapIfNull() {
        Assert.assertNull(SpatialFilterEsModel.convert((SpatialFilter) null));
    }

    @Test
    public void createInvalidSridGeometry() throws OwsExceptionReport, DecodingException, ParseException {
        Geometry geom = JTSHelper.createGeometryFromWKT(WKT_POLYGON, 9999);
        SpatialFilter filter = new SpatialFilter(SpatialOperator.BBOX, geom, VAL_REF);

        Map<String, Object> map = SpatialFilterEsModel.convert(filter);

        Assert.assertNull(map);
    }

    @Test
    public void createPointGeometry() throws OwsExceptionReport, DecodingException, ParseException {
        Geometry geom = JTSHelper.createGeometryFromWKT("POINT (40 40)", 4326);
        SpatialFilter filter = new SpatialFilter(SpatialOperator.Equals, geom, VAL_REF);

        Map<String, Object> map = SpatialFilterEsModel.convert(filter);

        Assert.assertNotNull(map);
    }

    @Test
    public void createPointButItisPologyonGeometry() throws OwsExceptionReport, DecodingException, ParseException {
        Geometry geom = JTSHelper.createGeometryFromWKT(WKT_POLYGON, 4326);
        SpatialFilter filter = new SpatialFilter(SpatialOperator.Equals, geom, VAL_REF);

        Map<String, Object> map = SpatialFilterEsModel.convert(filter);

        Assert.assertNull(map);
    }
}
