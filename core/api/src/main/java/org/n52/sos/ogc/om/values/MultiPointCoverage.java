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
package org.n52.sos.ogc.om.values;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.n52.sos.ogc.gml.time.Time;
import org.n52.sos.ogc.om.PointValuePair;
import org.n52.sos.ogc.om.values.MultiPointCoverage.PointValueLists;
import org.n52.sos.ogc.om.values.visitor.ValueVisitor;
import org.n52.sos.ogc.om.values.visitor.VoidValueVisitor;
import org.n52.sos.ogc.ows.OwsExceptionReport;
import org.n52.sos.util.CollectionHelper;
import org.n52.sos.util.StringHelper;

import com.google.common.collect.Lists;
import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.Point;
import com.vividsolutions.jts.geom.Polygon;
import com.vividsolutions.jts.geom.PrecisionModel;

public class MultiPointCoverage implements MultiValue<List<PointValuePair>> {

    private static final long serialVersionUID = -2848924351209857414L;
    
    /**
     * Mesurement values
     */
    private List<PointValuePair> value = new ArrayList<PointValuePair>(0);

    /**
     * Unit of measure
     */
    private String unit;

    @Override
    public List<PointValuePair> getValue() {
        Collections.sort(value);
        return value;
    }
    
    public PointValueLists getPointValue() {
       return new PointValueLists(getValue());
    }

    @Override
    public void setValue(List<PointValuePair> value) {
        this.value.clear();
        this.value.addAll(value);
    }

    /**
     * Add time value pair value
     *
     * @param value
     *            Time value pair value to add
     */
    public void addValue(PointValuePair value) {
        this.value.add(value);
    }

    /**
     * Add time value pair values
     *
     * @param values
     *            Time value pair values to add
     */
    public void addValues(List<PointValuePair> values) {
        this.value.addAll(values);
    }

    @Override
    public void setUnit(String unit) {
        this.unit = unit;
    }

    @Override
    public String getUnit() {
        return this.unit;
    }

    @Override
    public boolean isSetValue() {
        return CollectionHelper.isNotEmpty(getValue());
    }

    @Override
    public boolean isSetUnit() {
        return StringHelper.isNotEmpty(getUnit());
    }

    @Override
    public Time getPhenomenonTime() {
        return null;
    }

    public Polygon getExtent() {
        if (isSetValue()) {
            int srid = -1;
            List<Coordinate> coordinates = Lists.newLinkedList();
            for (PointValuePair pointValuePair : getValue()) {
                Point point = pointValuePair.getPoint();
                coordinates.add(point.getCoordinate());
                if (point.getSRID() != srid) {
                    srid = point.getSRID();
                 }
            }
            GeometryFactory geometryFactory;
            if (srid > 0) {
                geometryFactory = new GeometryFactory(new PrecisionModel(PrecisionModel.FLOATING), srid);
            } else {
                geometryFactory = new GeometryFactory(new PrecisionModel(PrecisionModel.FLOATING));
            }
            return geometryFactory.createPolygon(coordinates.toArray(new Coordinate[coordinates.size()]));
        }
        return null;
    }

    @Override
    public <X> X accept(ValueVisitor<X> visitor) throws OwsExceptionReport {
        return visitor.visit(this);
    }

    @Override
    public void accept(VoidValueVisitor visitor) throws OwsExceptionReport {
        visitor.visit(this);
    }

    public class PointValueLists {
        
        private List<Point> points;
        
        private List<Value<?>> values;
        
        public PointValueLists(List<PointValuePair> pointValuePairs) {
            points = Lists.newArrayListWithCapacity(pointValuePairs.size());
            values = Lists.newArrayListWithCapacity(pointValuePairs.size());
            fillListsWithValues(pointValuePairs);
        }

        private void fillListsWithValues(List<PointValuePair> pointValuePairs) {
           for (PointValuePair pointValuePair : pointValuePairs) {
               points.add(pointValuePair.getPoint());
               values.add(pointValuePair.getValue());
           }
        }

        /**
         * @return the points
         */
        public List<Point> getPoints() {
            return points;
        }

        /**
         * @return the values
         */
        public List<Value<?>> getValues() {
            return values;
        }
    
    }

}
