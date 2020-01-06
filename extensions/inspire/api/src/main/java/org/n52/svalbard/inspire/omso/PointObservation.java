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

import org.n52.sos.exception.ows.concrete.InvalidSridException;
import org.n52.sos.ogc.gml.ReferenceType;
import org.n52.sos.ogc.om.AbstractObservationValue;
import org.n52.sos.ogc.om.ObservationValue;
import org.n52.sos.ogc.om.OmObservation;
import org.n52.sos.ogc.om.PointValuePair;
import org.n52.sos.ogc.om.SingleObservationValue;
import org.n52.sos.ogc.om.StreamingValue;
import org.n52.sos.ogc.om.features.samplingFeatures.AbstractSamplingFeature;
import org.n52.sos.ogc.om.values.CvDiscretePointCoverage;
import org.n52.sos.ogc.om.values.GeometryValue;

import com.google.common.base.Strings;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.Point;

public class PointObservation extends AbstractInspireObservation {

    private static final long serialVersionUID = 2388069533262527383L;

    /**
     * constructor
     */
    public PointObservation() {
        super();
    }
    
    /**
     * constructor
     * 
     * @param observation
     *            {@link OmObservation} to convert
     */
    public PointObservation(OmObservation observation) {
        super(observation);
        getObservationConstellation().setObservationType(InspireOMSOConstants.OBS_TYPE_POINT_OBSERVATION);
        if (!checkForFeatureGeometry(observation) && observation.isSetSpatialFilteringProfileParameter()) {
            try {
                ((AbstractSamplingFeature)getObservationConstellation().getFeatureOfInterest()).setGeometry(getGeometryFromSamplingGeometry(observation));
            } catch (InvalidSridException e) {
                // TODO
            }
        }
    }
    
    @Override
    public OmObservation cloneTemplate() {
        if (getObservationConstellation().getFeatureOfInterest() instanceof AbstractSamplingFeature){
            ((AbstractSamplingFeature)getObservationConstellation().getFeatureOfInterest()).setEncode(true);
        }
        return cloneTemplate(new PointObservation());
    }
    
    @Override
    public void setValue(ObservationValue<?> value) {
        if (value instanceof StreamingValue<?>) {
            super.setValue(value);
        } else if (value.getValue() instanceof CvDiscretePointCoverage) {
            super.setValue(value);
        } else {
            CvDiscretePointCoverage cvDiscretePointCoverage = new CvDiscretePointCoverage(getObservationID());
            cvDiscretePointCoverage.setRangeType(new ReferenceType(getObservationConstellation().getObservablePropertyIdentifier()));
            cvDiscretePointCoverage.setUnit(((AbstractObservationValue<?>) value).getUnit());
            Geometry geometry = null;
            String domainExtent = "";
            if (isSetSpatialFilteringProfileParameter() && getSpatialFilteringProfileParameter().getValue() instanceof GeometryValue) {
                GeometryValue geometryValue = (GeometryValue)getSpatialFilteringProfileParameter().getValue();
                geometry = getSpatialFilteringProfileParameter().getValue().getValue();
                domainExtent = geometryValue.getGmlId();
            } else if (Strings.isNullOrEmpty(domainExtent) && checkForFeatureGeometry(this)) {
                geometry = getGeometryFromFeature(this);
                domainExtent = getObservationConstellation().getFeatureOfInterest().getGmlId();
            }
            if (geometry != null) {
                cvDiscretePointCoverage.setDomainExtent("#" + geometry.getGeometryType() + "_" + domainExtent);
                Point point = null;
                if (geometry instanceof Point) {
                    point = (Point)geometry;
                } else {
                    point = geometry.getCentroid();
                }
                cvDiscretePointCoverage.setValue(new PointValuePair(point, value.getValue()));
            }
            super.setValue(new SingleObservationValue<>(value.getPhenomenonTime(), cvDiscretePointCoverage));
        }
    }

}
