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
package org.n52.svalbard.inspire.omso;

import java.util.List;

import org.n52.sos.exception.ows.concrete.InvalidSridException;
import org.n52.sos.ogc.gml.AbstractFeature;
import org.n52.sos.ogc.om.ObservationValue;
import org.n52.sos.ogc.om.OmObservation;
import org.n52.sos.ogc.om.SingleObservationValue;
import org.n52.sos.ogc.om.StreamingValue;
import org.n52.sos.ogc.om.features.SfConstants;
import org.n52.sos.ogc.om.features.samplingFeatures.AbstractSamplingFeature;
import org.n52.sos.ogc.om.values.ProfileLevel;
import org.n52.sos.ogc.om.values.ProfileValue;
import org.n52.sos.ogc.om.values.QuantityRangeValue;
import org.n52.sos.ogc.om.values.RectifiedGridCoverage;
import org.n52.sos.ogc.om.values.ReferencableGridCoverage;
import org.n52.sos.util.CollectionHelper;

import com.google.common.collect.Lists;
import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.LineString;

public class ProfileObservation extends AbstractInspireObservation {

    private static final long serialVersionUID = -4114937024428256032L;

    /**
     * constructor
     */
    public ProfileObservation() {
        super();
    }
    
    /**
     * constructor
     * 
     * @param observation
     *            {@link OmObservation} to convert
     */
    public ProfileObservation(OmObservation observation) {
        super(observation);
        getObservationConstellation().setObservationType(InspireOMSOConstants.OBS_TYPE_PROFILE_OBSERVATION);
    }
    
    @Override
    public OmObservation cloneTemplate() {
        if (getObservationConstellation().getFeatureOfInterest() instanceof AbstractSamplingFeature){
            ((AbstractSamplingFeature)getObservationConstellation().getFeatureOfInterest()).setEncode(true);
        }
        return cloneTemplate(new ProfileObservation());
    }
    
    @Override
    public void setValue(ObservationValue<?> value) {
        if (value instanceof StreamingValue<?>) {
            super.setValue(value);
        } else if (value.getValue() instanceof RectifiedGridCoverage || value.getValue() instanceof ReferencableGridCoverage) {
            super.setValue(value);
        } else if (value.getValue() instanceof ProfileValue) {
            ProfileValue profile = (ProfileValue) value.getValue();
            RectifiedGridCoverage rectifiedGridCoverage = new RectifiedGridCoverage(getObservationID());
            rectifiedGridCoverage.setUnit(value.getValue().getUnit());
            rectifiedGridCoverage.setRangeParameters(getObservationConstellation().getObservablePropertyIdentifier());
            List<Coordinate> coordinates = Lists.newArrayList();
            int srid = 0;
            for (ProfileLevel level : profile.getValue()) {
                if (level.isSetLevelEnd()) {
                    rectifiedGridCoverage.addValue(new QuantityRangeValue(level.getLevelStart().getValue(),
                            level.getLevelEnd().getValue(), level.getLevelStart().getUnit()), level.getSimpleValue());
                } else {
                    rectifiedGridCoverage.addValue(level.getLevelStart().getValue(), level.getSimpleValue());
                }
                if (level.isSetLocation()) {
                    Coordinate coordinate = level.getLocation().getCoordinate();
                    coordinate.z = level.getLevelStart().getValue();
                    coordinates.add(coordinate);
                    if (srid == 0) {
                        srid = level.getLocation().getSRID();
                    }
                }
            }
            if (CollectionHelper.isNotEmpty(coordinates)) {
                setFeatureGeometry(coordinates, srid);
            }
            super.setValue(new SingleObservationValue<>(value.getPhenomenonTime(), rectifiedGridCoverage));
        } else {
            double heightDepth = 0;
            if (isSetHeightDepthParameter()) {
                heightDepth = getHeightDepthParameter().getValue().getValue();
                removeParameter(getHeightDepthParameter());
            }
            RectifiedGridCoverage rectifiedGridCoverage = new RectifiedGridCoverage(getObservationID());
            rectifiedGridCoverage.setUnit(value.getValue().getUnit());
            rectifiedGridCoverage.addValue(heightDepth, value.getValue());
            super.setValue(new SingleObservationValue<>(value.getPhenomenonTime(), rectifiedGridCoverage));
            
        }
    }
    
    private void setFeatureGeometry(List<Coordinate> coordinates, int srid) {
        AbstractFeature featureOfInterest = getObservationConstellation().getFeatureOfInterest();
        if (featureOfInterest instanceof AbstractSamplingFeature) {
            AbstractSamplingFeature sf = (AbstractSamplingFeature) featureOfInterest;
            Coordinate[] coords = coordinates.toArray(new Coordinate[0]);
            try {
                LineString lineString = new GeometryFactory().createLineString(coords);
                lineString.setSRID(srid);
                sf.setGeometry(lineString);
                sf.setFeatureType(SfConstants.SAMPLING_FEAT_TYPE_SF_SAMPLING_CURVE);
            } catch (InvalidSridException e) {
                // TODO
            }
        }
    }

    @Override
    protected boolean mergeValues(ObservationValue<?> observationValue) {
        if (observationValue.getValue() instanceof RectifiedGridCoverage) {
            ((RectifiedGridCoverage) getValue().getValue())
                    .addValue(((RectifiedGridCoverage) observationValue.getValue()).getValue());
            // } else if (observationValue.getValue() instanceof
            // ReverencableGridCoverage) {
            // ((ReverencableGridCoverage)getValue()).addValue(((ReverencableGridCoverage)observationValue).getValue());

            if (getObservationConstellation().getFeatureOfInterest() instanceof AbstractSamplingFeature) {
                if (((AbstractSamplingFeature) getObservationConstellation().getFeatureOfInterest()).isSetGeometry()) {
                    // TODO check for SamplingCurve and Depht/Height
                }
            }
            return true;
        } else {
            return super.mergeValues(observationValue);
        }
    }
}
