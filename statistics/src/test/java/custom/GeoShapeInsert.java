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
package custom;

import java.util.HashMap;
import java.util.Map;

import javax.inject.Inject;

import org.junit.Test;

import org.n52.iceland.statistics.api.interfaces.datahandler.IStatisticsDataHandler;
import org.n52.shetland.ogc.filter.FilterConstants.SpatialOperator;
import org.n52.shetland.ogc.filter.SpatialFilter;
import org.n52.shetland.util.JTSHelper;
import org.n52.sos.statistics.sos.SosDataMapping;
import org.n52.sos.statistics.sos.models.SpatialFilterEsModel;

import org.locationtech.jts.geom.Geometry;

import basetest.SpringBaseTest;

public class GeoShapeInsert extends SpringBaseTest {

    @Inject
    private IStatisticsDataHandler handler;

    @Test
    public void insert() throws Exception {
        Geometry geom = JTSHelper.createGeometryFromWKT("POLYGON ((30 10, 40 40, 20 40, 10 20, 30 10))", 4326);
        SpatialFilter filter = new SpatialFilter(SpatialOperator.BBOX, geom, "value-ref");

        Map<String, Object> map = SpatialFilterEsModel.convert(filter);

        Map<String, Object> root = new HashMap<>();
        root.put(SosDataMapping.GO_SPATIAL_FILTER.getName(), map);
        root.put(SosDataMapping.GO_FEATURE_OF_INTERESTS.getName(), "feature of interest");

        handler.persist(root);
    }
}
