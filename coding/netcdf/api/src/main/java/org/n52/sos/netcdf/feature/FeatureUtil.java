/*
 * Copyright (C) 2012-2021 52°North Initiative for Geospatial Open Source
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
package org.n52.sos.netcdf.feature;

import java.util.HashSet;
import java.util.Set;

import org.n52.shetland.ogc.om.features.samplingFeatures.AbstractSamplingFeature;

import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.LineString;
import org.locationtech.jts.geom.Point;

/**
 * Utility class for features in netCDF encoding
 *
 * @author <a href="mailto:shane@axiomdatascience.com">Shane StClair</a>
 * @author <a href="mailto:c.hollmann@52north.org">Carsten Hollmann</a>
 * @since 4.4.0
 *
 */
public class FeatureUtil {
    public static Set<Point> getFeaturePoints(Set<AbstractSamplingFeature> features) {
        Set<Point> featurePoints = new HashSet<Point>();

        for (AbstractSamplingFeature feature : features) {
            featurePoints.addAll(getFeaturePoints(feature));
        }

        return featurePoints;
    }

    public static Set<Point> getFeaturePoints(AbstractSamplingFeature feature) {
        Set<Point> points = new HashSet<Point>();
        if (feature != null && feature.isSetGeometry()) {
            return getPoints(feature.getGeometry());
        }
        return points;
    }

    public static Set<Point> getPoints(Geometry geom) {
        Set<Point> points = new HashSet<Point>();
        if (geom != null) {
            if (geom instanceof Point) {
                points.add((Point) geom);
            } else if (geom instanceof LineString) {
                LineString lineString = (LineString) geom;
                for (int i = 0; i < lineString.getNumPoints(); i++) {
                    Point point = lineString.getPointN(i);
                    point.setSRID(lineString.getSRID());
                    points.add(point);
                }
            }
        }
        return points;
    }

    public static Set<Double> getFeatureHeights(AbstractSamplingFeature feature) {
        return getHeights(getFeaturePoints(feature));
    }

    public static Set<Double> getHeights(Set<Point> points) {
        Set<Double> heights = new HashSet<Double>();
        for (Point point : points) {
            if (!Double.isNaN(point.getCoordinate().getZ())) {
                heights.add(point.getCoordinate().getZ());
            } else {
                heights.add(0.0);
            }
        }
        return heights;
    }

    public static Point clonePoint2d(Point point) {
        if (point == null) {
            return null;
        }
        if (Double.isNaN(point.getCoordinate().getZ())) {
            return point;
        }
        Point clonedPoint = (Point) point.copy();
        clonedPoint.getCoordinate().setZ(Double.NaN);
        return clonedPoint;
    }

    public static boolean equal2d(Point a, Point b) {
        return a.getX() == b.getX() && a.getY() == b.getY();
    }
}
