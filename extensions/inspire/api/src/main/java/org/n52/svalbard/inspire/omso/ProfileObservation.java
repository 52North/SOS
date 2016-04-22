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

import org.n52.sos.ogc.om.ObservationValue;
import org.n52.sos.ogc.om.OmObservation;
import org.n52.sos.ogc.om.SingleObservationValue;
import org.n52.sos.ogc.om.StreamingValue;
import org.n52.sos.ogc.om.features.samplingFeatures.SamplingFeature;
import org.n52.sos.ogc.om.values.RectifiedGridCoverage;
import org.n52.sos.ogc.om.values.ReverencableGridCoverage;

public class ProfileObservation extends AbstractInspireObservation {

    private static final long serialVersionUID = -4114937024428256032L;

    public ProfileObservation() {
        super();
    }
    
    public ProfileObservation(OmObservation observation) {
        super(observation);
        getObservationConstellation().setObservationType(InspireOMSOConstants.OBS_TYPE_PROFILE_OBSERVATION);
    }
    
    @Override
    public OmObservation cloneTemplate() {
        if (getObservationConstellation().getFeatureOfInterest() instanceof SamplingFeature){
            ((SamplingFeature)getObservationConstellation().getFeatureOfInterest()).setEncode(true);
        }
        return cloneTemplate(new ProfileObservation());
    }
    
    @Override
    public void setValue(ObservationValue<?> value) {
        if (value instanceof StreamingValue<?>) {
            super.setValue(value);
        } else if (value.getValue() instanceof RectifiedGridCoverage || value.getValue() instanceof ReverencableGridCoverage) {
            super.setValue(value);
        } else {
            double heightDepth = 0;
            if (isSetHeightDepthParameter()) {
                heightDepth = getHeightDepthParameter().getValue().getValue();
            }
            RectifiedGridCoverage rectifiedGridCoverage = new RectifiedGridCoverage(getObservationID());
            rectifiedGridCoverage.setUnit(value.getValue().getUnit());
            rectifiedGridCoverage.addValue(heightDepth, value.getValue());
            super.setValue(new SingleObservationValue<>(value.getPhenomenonTime(), rectifiedGridCoverage));
        }
    }
    
    @Override
    protected void mergeValues(ObservationValue<?> observationValue) {
      if (observationValue.getValue() instanceof RectifiedGridCoverage) {
          ((RectifiedGridCoverage)getValue().getValue()).addValue(((RectifiedGridCoverage)observationValue.getValue()).getValue());
//      } else if (observationValue.getValue() instanceof ReverencableGridCoverage) {
//          ((ReverencableGridCoverage)getValue()).addValue(((ReverencableGridCoverage)observationValue).getValue());
      } else {
          super.mergeValues(observationValue);
      }
    }
}
