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
package org.n52.sos.inspire.omso;

import java.util.List;

import org.n52.sos.ogc.om.AbstractObservationValue;
import org.n52.sos.ogc.om.MultiObservationValues;
import org.n52.sos.ogc.om.ObservationValue;
import org.n52.sos.ogc.om.OmObservation;
import org.n52.sos.ogc.om.PointValuePair;
import org.n52.sos.ogc.om.SingleObservationValue;
import org.n52.sos.ogc.om.features.samplingFeatures.SamplingFeature;
import org.n52.sos.ogc.om.values.CvDiscretePointCoverage;
import org.n52.sos.ogc.om.values.MultiPointCoverage;

import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.Point;

public class MultiPointObservation extends OmObservation {

    private static final long serialVersionUID = -2792198498699052095L;

    public MultiPointObservation() {
    }
    
    public MultiPointObservation(OmObservation observation) {
        observation.copyTo(this);
    }
    
    @Override
    public OmObservation cloneTemplate() {
        if (getObservationConstellation().getFeatureOfInterest() instanceof SamplingFeature){
            ((SamplingFeature)getObservationConstellation().getFeatureOfInterest()).setEncode(true);
        }
        return cloneTemplate(new MultiPointObservation());
    }
    
    @Override
    public void setValue(ObservationValue<?> value) {
        if (value.getValue() instanceof MultiPointCoverage) {
            super.setValue(value);
        } else {
            MultiPointCoverage multiPointCoverage = new MultiPointCoverage();
            multiPointCoverage.setUnit(((AbstractObservationValue<?>) value).getUnit());
            Point point = null;
            if (isSetSpatialFilteringProfileParameter()) {
                Geometry geometry = getSpatialFilteringProfileParameter().getValue().getValue();
                point = geometry.getInteriorPoint();
            } else {
                if (getObservationConstellation().getFeatureOfInterest() instanceof SamplingFeature && ((SamplingFeature)getObservationConstellation().getFeatureOfInterest()).isSetGeometry()) {
                    Geometry geometry = ((SamplingFeature)getObservationConstellation().getFeatureOfInterest()).getGeometry();
                    point = geometry.getInteriorPoint();
                }
            }
            multiPointCoverage.addValue(new PointValuePair(point, value.getValue()));
            MultiObservationValues<List<PointValuePair>> multiObservationValues = new MultiObservationValues<>();
            multiObservationValues.setValue(multiPointCoverage);
            super.setValue(multiObservationValues);
        }
    }
    
    @Override
    public void mergeWithObservation(OmObservation sosObservation) {
        // TODO Auto-generated method stub
        super.mergeWithObservation(sosObservation);
    }
    
    @Override
    protected void mergeValues(ObservationValue<?> observationValue) {
        // if SampPoint
        // How to get FOI-Geom????
        
        super.mergeValues(observationValue);
    }
}
