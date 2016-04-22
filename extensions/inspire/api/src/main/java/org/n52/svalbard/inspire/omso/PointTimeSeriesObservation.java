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

import org.n52.sos.exception.ows.concrete.InvalidSridException;
import org.n52.sos.ogc.om.NamedValue;
import org.n52.sos.ogc.om.OmObservation;
import org.n52.sos.ogc.om.features.samplingFeatures.SamplingFeature;

import com.google.common.collect.Sets;

public class PointTimeSeriesObservation extends AbstractInspireObservation {

    private static final long serialVersionUID = -6453048922030316456L;
    
    public PointTimeSeriesObservation() {
        super();
    }
    
    public PointTimeSeriesObservation(OmObservation observation) {
        super(observation);
        if (!checkForFeatureGeometry(observation) && observation.isSetSpatialFilteringProfileParameter()) {
            try {
                ((SamplingFeature)getObservationConstellation().getFeatureOfInterest()).setGeometry(getGeometryFromSamplingGeometry(observation));
            } catch (InvalidSridException e) {
                // TODO
            }
        }
        observation.setParameter(Sets.<NamedValue<?>>newHashSet());
        getObservationConstellation().setObservationType(InspireOMSOConstants.OBS_TYPE_POINT_TIME_SERIES_OBSERVATION);
    }
    
    @Override
    public OmObservation cloneTemplate() {
        if (getObservationConstellation().getFeatureOfInterest() instanceof SamplingFeature){
            ((SamplingFeature)getObservationConstellation().getFeatureOfInterest()).setEncode(true);
        }
        return cloneTemplate(new PointTimeSeriesObservation());
    }
}
