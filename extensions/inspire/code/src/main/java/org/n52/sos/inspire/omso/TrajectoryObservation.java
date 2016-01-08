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
package org.n52.sos.inspire.omso;

import java.util.List;

import org.n52.sos.ogc.om.AbstractObservationValue;
import org.n52.sos.ogc.om.MultiObservationValues;
import org.n52.sos.ogc.om.ObservationValue;
import org.n52.sos.ogc.om.OmObservation;
import org.n52.sos.ogc.om.SingleObservationValue;
import org.n52.sos.ogc.om.TimeLocationValueTriple;
import org.n52.sos.ogc.om.features.samplingFeatures.SamplingFeature;
import org.n52.sos.ogc.om.values.TLVTValue;
import org.n52.sos.ogc.om.values.TVPValue;

import com.vividsolutions.jts.geom.Geometry;

public class TrajectoryObservation extends OmObservation {

    private static final long serialVersionUID = 5660393848737321598L;

    public TrajectoryObservation() {
    }
    
    public TrajectoryObservation(OmObservation observation) {
        observation.copyTo(this);
    }

    @Override
    public OmObservation cloneTemplate() {
        if (getObservationConstellation().getFeatureOfInterest() instanceof SamplingFeature){
            ((SamplingFeature)getObservationConstellation().getFeatureOfInterest()).setEncode(true);
        }
        return cloneTemplate(new TrajectoryObservation());
    }
    
    @Override
    public void setValue(ObservationValue<?> value) {
        if (value.getValue() instanceof TLVTValue) {
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
    public void mergeWithObservation(OmObservation sosObservation) {
        super.mergeWithObservation(sosObservation);
    }
    
    @Override
    protected void mergeValues(ObservationValue<?> observationValue) {
        if (observationValue.getValue() instanceof TLVTValue) {
            List<TimeLocationValueTriple> valuesToMerge = ((TLVTValue)observationValue.getValue()).getValue();
            ((TLVTValue)getValue().getValue()).addValues(valuesToMerge);
            
        } else {
            super.mergeValues(observationValue);
        }
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
