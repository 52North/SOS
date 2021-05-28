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

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.elasticsearch.common.geo.builders.CoordinatesBuilder;
import org.elasticsearch.common.geo.builders.PointBuilder;
import org.elasticsearch.common.geo.builders.PolygonBuilder;
import org.elasticsearch.common.geo.builders.ShapeBuilder;
import org.locationtech.jts.geom.Coordinate;
import org.n52.iceland.statistics.api.parameters.ObjectEsParameterFactory;
import org.n52.shetland.ogc.filter.SpatialFilter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class SpatialFilterEsModel extends AbstractElasticsearchModel {

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

    /**
     *
     * Transform the geomtry to Elatisearch geo_shape type.
     *
     * If other the 4326 SRID coordinates are present it needed to be
     * transformed before conver in to Elasticsearch geo_shape
     *
     */
    @Override
    protected Map<String, Object> getAsMap() {
        if (spatialFilter == null || spatialFilter.getGeometry() == null) {
            return null;
        }

        try {
            switch (spatialFilter.getSrid()) {
                case 4326:
                    transform4326(spatialFilter);
                    break;

                default:
                    throw new IllegalArgumentException("Unsupported SRID coordination system "
                            + spatialFilter.getSrid());
            }
            return dataMap;
        } catch (Throwable e) {
            logger.warn(e.getMessage(), e);
        }
        return null;
    }

    private void transform4326(SpatialFilter spatialFilter) {
        switch (spatialFilter.getOperator()) {
            case BBOX:
                createBbox(spatialFilter);
                break;
            case Equals:
                createEquals(spatialFilter);
                break;
            default:
                throw new IllegalArgumentException("Unsupported operator " + spatialFilter.getOperator().toString());
        }

    }

    private void createEquals(SpatialFilter filter) {
        Coordinate[] points = filter.getGeometry().getCoordinates();
        if (points.length != 1) {
            throw new IllegalArgumentException(
                    "Invalid number of coordinates in geometry. It should be a point. Got " + points.length);
        }
        PointBuilder point = PointBuilder.newPoint(points[0].x, points[0].y);
        createSpatialFilter(filter, point);
    }

    /**
     * A closed polygon whose first and last point must match, thus requiring n
     * + 1 vertices to create an n-sided polygon and a minimum of 4 vertices.
     *
     * @param filter
     *            the spatial filter
     */
    private void createBbox(SpatialFilter filter) {
        CoordinatesBuilder coordinates = new CoordinatesBuilder();
        for (Coordinate coord : filter.getGeometry().getCoordinates()) {
            coordinates.coordinate(coord.getX(), coord.getY());
        }
        createSpatialFilter(filter, new PolygonBuilder(coordinates));

    }

    private void createSpatialFilter(SpatialFilter filter, ShapeBuilder builder) {
        if (filter.getOperator() != null) {
            put(ObjectEsParameterFactory.SPATIAL_FILTER_OPERATOR, filter.getOperator().toString());
        }
        put(ObjectEsParameterFactory.SPATIAL_FILTER_SHAPE, builder);
        put(ObjectEsParameterFactory.SPATIAL_FILTER_VALUE_REF, filter.getValueReference());
    }

}
