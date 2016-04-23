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

import org.n52.sos.exception.CodedException;
import org.n52.sos.exception.ows.NoApplicableCodeException;
import org.n52.sos.exception.ows.concrete.InvalidSridException;
import org.n52.sos.ogc.gml.CodeWithAuthority;
import org.n52.sos.ogc.om.AbstractObservationValue;
import org.n52.sos.ogc.om.ObservationValue;
import org.n52.sos.ogc.om.OmObservation;
import org.n52.sos.ogc.om.PointValuePair;
import org.n52.sos.ogc.om.SingleObservationValue;
import org.n52.sos.ogc.om.features.SfConstants;
import org.n52.sos.ogc.om.features.samplingFeatures.SamplingFeature;
import org.n52.sos.ogc.om.values.MultiPointCoverage;
import org.n52.sos.util.CollectionHelper;
import org.n52.sos.util.GeometryHandler;
import org.n52.sos.util.JTSHelper;

import com.vividsolutions.jts.geom.Envelope;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.Point;

public class MultiPointObservation extends AbstractInspireObservation {

    private static final long serialVersionUID = -2792198498699052095L;

    public MultiPointObservation() {
        super();
    }
    
    public MultiPointObservation(OmObservation observation) throws CodedException {
        super(observation);
        getObservationConstellation().setObservationType(InspireOMSOConstants.OBS_TYPE_MULTI_POINT_OBSERVATION);
        if (getValue().getValue() instanceof MultiPointCoverage) {
            SamplingFeature samplingFeature = new SamplingFeature(new CodeWithAuthority(""));
            samplingFeature.setFeatureType(SfConstants.SAMPLING_FEAT_TYPE_SF_SAMPLING_SURFACE);
            samplingFeature.setEncode(true);
            try {
                samplingFeature.setGeometry(getEnvelope(((MultiPointCoverage)getValue().getValue()).getValue()));
            } catch (InvalidSridException e) {
                throw new NoApplicableCodeException().causedBy(e);
            }
            getObservationConstellation().setFeatureOfInterest(samplingFeature);
        }
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
            MultiPointCoverage multiPointCoverage = new MultiPointCoverage(getObservationID());
            multiPointCoverage.setUnit(((AbstractObservationValue<?>) value).getUnit());
            multiPointCoverage.addValue(new PointValuePair(getPoint(), value.getValue()));
            super.setValue(new SingleObservationValue<>(value.getPhenomenonTime(), multiPointCoverage));
        }
    }
    
    @Override
    protected void mergeValues(ObservationValue<?> observationValue) {
        if (observationValue.getValue() instanceof MultiPointCoverage) {
            List<PointValuePair> valuesToMerge = ((MultiPointCoverage)observationValue.getValue()).getValue();
            ((MultiPointCoverage)getValue()).addValues(valuesToMerge);
            if (getObservationConstellation().getFeatureOfInterest() instanceof SamplingFeature) {
                if (((SamplingFeature)getObservationConstellation().getFeatureOfInterest()).isSetGeometry()) {
                    try {
                        ((SamplingFeature)getObservationConstellation().getFeatureOfInterest()).setGeometry(getEnvelope(valuesToMerge));
                    } catch (InvalidSridException e) {
                        // TODO
                    }
                }
            }
        } else {
            super.mergeValues(observationValue);
        }
    }

    private Point getPoint() {
        Point point = null;
        if (isSetSpatialFilteringProfileParameter()) {
            Geometry geometry = getSpatialFilteringProfileParameter().getValue().getValue();
            point = geometry.getInteriorPoint();
            point.setSRID(geometry.getSRID());
        } else {
            if (getObservationConstellation().getFeatureOfInterest() instanceof SamplingFeature && ((SamplingFeature)getObservationConstellation().getFeatureOfInterest()).isSetGeometry()) {
                Geometry geometry = ((SamplingFeature)getObservationConstellation().getFeatureOfInterest()).getGeometry();
                point = geometry.getInteriorPoint();
                point.setSRID(geometry.getSRID());
            }
        }
        return point;
    }

    private Geometry getEnvelope(List<PointValuePair> pointValuePairs) {
        Envelope envelope = new Envelope();
        GeometryFactory factory = null;
        int srid = GeometryHandler.getInstance().getStorageEPSG();
        if (CollectionHelper.isNotEmpty(pointValuePairs)) {
            for (PointValuePair pointValuePair : pointValuePairs) {
                if (factory == null && pointValuePair.getPoint() != null) {
                    factory = pointValuePair.getPoint().getFactory();
                }
                if (pointValuePair.getPoint().getSRID() > 0) {
                    srid = pointValuePair.getPoint().getSRID();
                }
                envelope.expandToInclude(pointValuePair.getPoint().getEnvelopeInternal());
            }
        } else {
            if (isSetSpatialFilteringProfileParameter()) {
                Geometry geometry = getSpatialFilteringProfileParameter().getValue().getValue();
                if (factory == null && geometry != null) {
                    factory = geometry.getFactory();
                }
                if (geometry.getSRID() > 0) {
                    srid = geometry.getSRID();
                }
                envelope.expandToInclude(geometry.getEnvelopeInternal());
            } else {
                if (getObservationConstellation().getFeatureOfInterest() instanceof SamplingFeature && ((SamplingFeature)getObservationConstellation().getFeatureOfInterest()).isSetGeometry()) {
                    Geometry geometry = ((SamplingFeature)getObservationConstellation().getFeatureOfInterest()).getGeometry();
                    if (factory == null && geometry != null) {
                        factory = geometry.getFactory();
                    }
                    if (geometry.getSRID() > 0) {
                        srid = geometry.getSRID();
                    }
                    envelope.expandToInclude(geometry.getEnvelopeInternal());
                }
            }
        }
        if (factory == null) {
            JTSHelper.getGeometryFactoryForSRID(GeometryHandler.getInstance().getStorageEPSG());
        }
        Geometry geometry = factory.toGeometry(envelope);
        geometry.setSRID(srid);
        return geometry;
    }
}
