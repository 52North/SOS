/**
 * Copyright (C) 2012-2016 52°North Initiative for Geospatial Open Source
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
package org.n52.svalbard.inspire.omso;

import java.util.List;

import org.apache.commons.lang.ArrayUtils;
import org.n52.sos.exception.ows.concrete.InvalidSridException;
import org.n52.sos.ogc.gml.AbstractFeature;
import org.n52.sos.ogc.om.AbstractObservationValue;
import org.n52.sos.ogc.om.MultiObservationValues;
import org.n52.sos.ogc.om.ObservationValue;
import org.n52.sos.ogc.om.OmObservation;
import org.n52.sos.ogc.om.SingleObservationValue;
import org.n52.sos.ogc.om.StreamingValue;
import org.n52.sos.ogc.om.TimeLocationValueTriple;
import org.n52.sos.ogc.om.features.SfConstants;
import org.n52.sos.ogc.om.features.samplingFeatures.SamplingFeature;
import org.n52.sos.ogc.om.values.TLVTValue;
import org.n52.sos.ogc.om.values.TVPValue;
import org.omg.CORBA.portable.StreamableValue;

import com.google.common.collect.Lists;
import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.LineString;
import com.vividsolutions.jts.geom.Point;

public class TrajectoryObservation extends OmObservation {

    private static final long serialVersionUID = 5660393848737321598L;

    public TrajectoryObservation() {
    }
    
    public TrajectoryObservation(OmObservation observation) {
        observation.copyTo(this);
        if (getObservationConstellation().getFeatureOfInterest() instanceof SamplingFeature){
            SamplingFeature sf = (SamplingFeature)getObservationConstellation().getFeatureOfInterest();
            sf.setEncode(true);
            sf.setFeatureType(SfConstants.FT_SAMPLINGCURVE);
        }
        if (isSetSpatialFilteringProfileParameter()) {
            removeSpatialFilteringProfileParameter();
        }
    }

    @Override
    public OmObservation cloneTemplate() {
        if (getObservationConstellation().getFeatureOfInterest() instanceof SamplingFeature){
            SamplingFeature sf = (SamplingFeature)getObservationConstellation().getFeatureOfInterest();
            sf.setEncode(true);
            sf.setFeatureType(SfConstants.FT_SAMPLINGCURVE);
        }
        if (isSetSpatialFilteringProfileParameter()) {
            removeSpatialFilteringProfileParameter();
        }
        return cloneTemplate(new TrajectoryObservation());
    }
    
    @Override
    public void setValue(ObservationValue<?> value) {
        if (value instanceof StreamingValue || value.getValue() instanceof TLVTValue) {
            super.setValue(value);
        } else {
            Geometry geometry = null;
            if (isSetSpatialFilteringProfileParameter()) {
                geometry = getSpatialFilteringProfileParameter().getValue().getValue();
            } else {
                if (getObservationConstellation().getFeatureOfInterest() instanceof SamplingFeature && ((SamplingFeature)getObservationConstellation().getFeatureOfInterest()).isSetGeometry()) {
                    geometry = ((SamplingFeature)getObservationConstellation().getFeatureOfInterest()).getGeometry();
                }
            }
            TLVTValue tlvpValue = convertSingleValueToMultiValue((SingleObservationValue<?>)value, geometry);
            tlvpValue.setUnit(((AbstractObservationValue<?>) value).getUnit());
            final MultiObservationValues<List<TimeLocationValueTriple>> multiValue = new MultiObservationValues<List<TimeLocationValueTriple>>();
            multiValue.setValue(tlvpValue);
            super.setValue(multiValue);
        }
    }
    
    @Override
    public void mergeWithObservation(OmObservation observation) {
        if (observation instanceof TrajectoryObservation) {
            mergeValues(observation.getValue());
        } else {
            super.mergeWithObservation(observation);
        }
    }
    
    @Override
    protected void mergeValues(ObservationValue<?> observationValue) {
        if (observationValue.getValue() instanceof TLVTValue) {
            TLVTValue tlvtValue = (TLVTValue)observationValue.getValue();
            List<TimeLocationValueTriple> valuesToMerge = tlvtValue.getValue();
//            List<TimeLocationValueTriple> valuesToMerge = (List<TimeLocationValueTriple>)((TLVTValue)observationValue.getValue()).getValue();
            ((TLVTValue)getValue().getValue()).addValues(valuesToMerge);
            checkForFeature(valuesToMerge);
        } else {
            super.mergeValues(observationValue);
        }
    }
    
    private void checkForFeature(List<TimeLocationValueTriple> valuesToMerge) {
        AbstractFeature featureOfInterest = getObservationConstellation().getFeatureOfInterest();
        if (featureOfInterest instanceof SamplingFeature) {
            SamplingFeature sf = (SamplingFeature)featureOfInterest;
            Coordinate[] coords = getCoordinates(valuesToMerge);
            int srid = 0;
            if (sf.isSetGeometry()) {
                srid = sf.getGeometry().getSRID();
                coords = (Coordinate[])ArrayUtils.addAll(sf.getGeometry().getCoordinates(), coords);
            } else {
                TimeLocationValueTriple next = valuesToMerge.iterator().next();
                if (next.isSetLocation()) {
                    srid = next.getLocation().getSRID();
                }
            }
            try {
                if (coords.length == 1) {
                    Point point = new GeometryFactory().createPoint(coords[0]);
                    point.setSRID(srid);
                    sf.setGeometry(point);
                } else if (coords.length > 1){
                    LineString lineString = new GeometryFactory().createLineString(coords);
                    lineString.setSRID(srid);
                    sf.setGeometry(lineString);
                }
            } catch (InvalidSridException e) {
                // TODO
            }
        }
    }

    private Coordinate[] getCoordinates(List<TimeLocationValueTriple> valuesToMerge) {
        List<Coordinate> coords = Lists.newArrayList();
        for (TimeLocationValueTriple timeLocationValueTriple : valuesToMerge) {
            if (timeLocationValueTriple.isSetLocation()){
                coords.add(timeLocationValueTriple.getLocation().getCoordinate());
            }
        }
        return coords.toArray(new Coordinate[0]);
    }

    /**
     * Convert {@link SingleObservationValue} to {@link TVPValue}
     * 
     * @param singleValue
     *            Single observation value
     * @return Converted TVPValue value
     */
    private TLVTValue convertSingleValueToMultiValue(final SingleObservationValue<?> singleValue, Geometry geom) {
        final TLVTValue tlvpValue = new TLVTValue();
        tlvpValue.setUnit(singleValue.getValue().getUnit());
        final TimeLocationValueTriple timeLocationValueTriple = new TimeLocationValueTriple(singleValue.getPhenomenonTime(), singleValue.getValue(), geom);
        tlvpValue.addValue(timeLocationValueTriple);
        return tlvpValue;
    }
}
