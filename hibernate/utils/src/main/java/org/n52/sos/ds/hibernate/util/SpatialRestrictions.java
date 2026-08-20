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
package org.n52.sos.ds.hibernate.util;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.Path;
import jakarta.persistence.criteria.Predicate;

import org.locationtech.jts.geom.Geometry;
import org.n52.shetland.ogc.filter.FilterConstants.SpatialOperator;
import org.n52.shetland.ogc.filter.SpatialFilter;
import org.n52.shetland.ogc.ows.exception.OwsExceptionReport;
import org.n52.sos.exception.ows.concrete.UnsupportedOperatorException;

/**
 * Builds spatial {@link Predicate Predicates} via the {@code st_*} functions
 * that both supported dialects (PostGIS, H2GIS) register in Hibernate 6,
 * mirroring the approach taken by dao-impl's
 * {@code FeatureQuerySpecifications#matchesSpatially()}.
 *
 * @author <a href="mailto:c.autermann@52north.org">Christian Autermann</a>
 *
 * @since 4.0.0
 */
public final class SpatialRestrictions {
    private static final double DWITHIN_DISTANCE = 10;

    private SpatialRestrictions() {
    }

    /**
     * Get the spatial restriction for the supplied filter.
     *
     * @param cb       the criteria builder
     * @param property the geometry path to apply the filter to
     * @param filter   the filter
     *
     * @return the predicate
     *
     * @throws OwsExceptionReport if the spatial filter is not supported
     */
    public static Predicate filter(CriteriaBuilder cb, Path<Geometry> property, SpatialFilter filter)
            throws OwsExceptionReport {
        return filter(cb, property, filter.getOperator(), filter.getGeometry().toGeometry());
    }

    /**
     * Get spatial filter restrictions.
     *
     * @param cb       the criteria builder
     * @param property the geometry path to apply the filter to
     * @param operator Spatial filter
     * @param geometry the geometry
     *
     * @return the predicate
     *
     * @throws OwsExceptionReport If the spatial filter is not supported
     */
    public static Predicate filter(CriteriaBuilder cb, Path<Geometry> property, SpatialOperator operator,
            Geometry geometry) throws OwsExceptionReport {
        switch (operator) {
            case BBOX:
                return within(cb, property, geometry);
            case Contains:
                return contains(cb, property, geometry);
            case Crosses:
                return crosses(cb, property, geometry);
            case Disjoint:
                return disjoint(cb, property, geometry);
            case DWithin:
                return distanceWithin(cb, property, geometry, DWITHIN_DISTANCE);
            case Equals:
                return eq(cb, property, geometry);
            case Intersects:
                return intersects(cb, property, geometry);
            case Overlaps:
                return overlaps(cb, property, geometry);
            case Touches:
                return touches(cb, property, geometry);
            case Within:
                return within(cb, property, geometry);
            case Beyond:
            default:
                throw new UnsupportedOperatorException(operator);
        }
    }

    public static Predicate eq(CriteriaBuilder cb, Path<Geometry> property, Geometry value) {
        return spatialFunction(cb, "st_equals", property, value);
    }

    public static Predicate within(CriteriaBuilder cb, Path<Geometry> property, Geometry value) {
        return spatialFunction(cb, "st_within", property, value);
    }

    public static Predicate contains(CriteriaBuilder cb, Path<Geometry> property, Geometry value) {
        return spatialFunction(cb, "st_contains", property, value);
    }

    public static Predicate crosses(CriteriaBuilder cb, Path<Geometry> property, Geometry value) {
        return spatialFunction(cb, "st_crosses", property, value);
    }

    public static Predicate disjoint(CriteriaBuilder cb, Path<Geometry> property, Geometry value) {
        return spatialFunction(cb, "st_disjoint", property, value);
    }

    public static Predicate intersects(CriteriaBuilder cb, Path<Geometry> property, Geometry value) {
        return spatialFunction(cb, "st_intersects", property, value);
    }

    public static Predicate overlaps(CriteriaBuilder cb, Path<Geometry> property, Geometry value) {
        return spatialFunction(cb, "st_overlaps", property, value);
    }

    public static Predicate touches(CriteriaBuilder cb, Path<Geometry> property, Geometry value) {
        return spatialFunction(cb, "st_touches", property, value);
    }

    public static Predicate distanceWithin(CriteriaBuilder cb, Path<Geometry> property, Geometry value,
            double distance) {
        return cb.equal(cb.function("st_dwithin", Boolean.class, property, cb.literal(value), cb.literal(distance)),
                true);
    }

    private static Predicate spatialFunction(CriteriaBuilder cb, String function, Path<Geometry> property,
            Geometry value) {
        return cb.equal(cb.function(function, Boolean.class, property, cb.literal(value)), true);
    }

}