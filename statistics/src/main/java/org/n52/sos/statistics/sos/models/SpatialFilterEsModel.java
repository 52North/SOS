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
package org.n52.sos.statistics.sos.models;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.elasticsearch.common.geo.builders.PolygonBuilder;
import org.n52.iceland.ogc.filter.FilterConstants.SpatialOperator;
import org.n52.sos.ogc.filter.SpatialFilter;
import org.n52.sos.statistics.sos.SosDataMapping;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.vividsolutions.jts.geom.Coordinate;

public class SpatialFilterEsModel extends AbstractElasticsearchModel {

    private static final Logger logger = LoggerFactory.getLogger(SpatialFilterEsModel.class);

    private final SpatialFilter spatialFilter;

    private SpatialFilterEsModel(SpatialFilter filters) {
        this.spatialFilter = filters;
    }

    public static Map<String, Object> convert(SpatialFilter filter) {
        return new SpatialFilterEsModel(filter).getAsMap();
    }

    public static List<Map<String, Object>> convert(Collection<SpatialFilter> filters) {
        if (filters == null || filters.isEmpty()) {
            return null;
        }
        return filters.stream().map(SpatialFilterEsModel::convert).collect(Collectors.toList());
    }

    @Override
    protected Map<String, Object> getAsMap() {
        if (spatialFilter == null || spatialFilter.getGeometry() == null) {
            return null;
        }

        // only bbox is allowed here or empty
        if (spatialFilter.getOperator() != null && spatialFilter.getOperator() != SpatialOperator.BBOX) {
            logger.debug("SpatialFilter operator is not allowed here {}", spatialFilter.getOperator());
            return null;
        }

        try {
            switch (spatialFilter.getSrid()) {
            case 4326:
                createBBOX(spatialFilter);
                break;

            default:
                logger.debug("Unsupported SRID coordination system {}", spatialFilter.getSrid());
                return null;
            }
            return dataMap;
        } catch (Throwable e) {
            logger.warn(e.getMessage(), e);
        }
        return null;
    }

    private void createBBOX(SpatialFilter filter) {
        PolygonBuilder polygon = PolygonBuilder.newPolygon();
        for (Coordinate coord : filter.getGeometry().getCoordinates()) {
            polygon.point(coord);
        }

        if (filter.getOperator() != null) {
            put(SosDataMapping.SPATIAL_FILTER_OPERATOR, filter.getOperator().toString());
        }
        put(SosDataMapping.SPATIAL_FILTER_SHAPE, polygon);
        put(SosDataMapping.SPATIAL_FILTER_VALUE_REF, filter.getValueReference());

    }

}
