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

import org.apache.commons.lang.ArrayUtils;
import org.n52.sos.exception.ows.concrete.InvalidSridException;
import org.n52.sos.ogc.gml.AbstractFeature;
import org.n52.sos.ogc.gml.CodeWithAuthority;
import org.n52.sos.ogc.om.AbstractObservationValue;
import org.n52.sos.ogc.om.MultiObservationValues;
import org.n52.sos.ogc.om.ObservationValue;
import org.n52.sos.ogc.om.OmObservation;
import org.n52.sos.ogc.om.SingleObservationValue;
import org.n52.sos.ogc.om.StreamingValue;
import org.n52.sos.ogc.om.TimeLocationValueTriple;
import org.n52.sos.ogc.om.features.SfConstants;
import org.n52.sos.ogc.om.features.samplingFeatures.AbstractSamplingFeature;
import org.n52.sos.ogc.om.features.samplingFeatures.SamplingFeature;
import org.n52.sos.ogc.om.values.TLVTValue;
import org.n52.sos.ogc.om.values.TVPValue;
import org.n52.sos.util.JavaHelper;

import com.google.common.collect.Lists;
import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.LineString;
import com.vividsolutions.jts.geom.Point;

public class TrajectoryObservation extends AbstractInspireObservation {

    private static final long serialVersionUID = 5660393848737321598L;

    /**
     * constructor
     */
    public TrajectoryObservation() {
        super();
    }

    /**
     * constructor
     * 
     * @param observation
     *            {@link OmObservation} to convert
     */
    public TrajectoryObservation(OmObservation observation) {
        super(observation);
        getObservationConstellation().setObservationType(InspireOMSOConstants.OBS_TYPE_TRAJECTORY_OBSERVATION);
        SamplingFeature sf = new SamplingFeature(getObservationConstellation().getFeatureOfInterest().getIdentifierCodeWithAuthority());
        sf.setFeatureType(SfConstants.SAMPLING_FEAT_TYPE_SF_SAMPLING_CURVE);
        getObservationConstellation().setFeatureOfInterest(sf);
        if (isSetSpatialFilteringProfileParameter()) {
            removeSpatialFilteringProfileParameter();
        }
        if (!isSetObservationID()) {
            setObservationID(JavaHelper.generateID(toString()));
        }
    }

    @Override
    public OmObservation cloneTemplate() {
        SamplingFeature sf = new SamplingFeature(new CodeWithAuthority(""));
        sf.setFeatureType(SfConstants.SAMPLING_FEAT_TYPE_SF_SAMPLING_CURVE);
        getObservationConstellation().setFeatureOfInterest(sf);
        if (isSetSpatialFilteringProfileParameter()) {
            removeSpatialFilteringProfileParameter();
        }
        return cloneTemplate(new TrajectoryObservation());
    }

    @SuppressWarnings("rawtypes")
    @Override
    public void setValue(ObservationValue<?> value) {
        if (value instanceof StreamingValue || value.getValue() instanceof TLVTValue) {
            super.setValue(value);
        } else {
            Geometry geometry = null;
            if (isSetSpatialFilteringProfileParameter()) {
                geometry = getSpatialFilteringProfileParameter().getValue().getValue();
            } else {
                if (getObservationConstellation().getFeatureOfInterest() instanceof AbstractSamplingFeature
                        && ((AbstractSamplingFeature) getObservationConstellation().getFeatureOfInterest()).isSetGeometry()) {
                    geometry = ((AbstractSamplingFeature) getObservationConstellation().getFeatureOfInterest()).getGeometry();
                }
            }
            TLVTValue tlvpValue = convertSingleValueToMultiValue((SingleObservationValue<?>) value, geometry);
            if (!tlvpValue.isSetUnit() && ((AbstractObservationValue<?>) value).isSetUnit()) {
                tlvpValue.setUnit(((AbstractObservationValue<?>) value).getUnit());
            }
            final MultiObservationValues<List<TimeLocationValueTriple>> multiValue =
                    new MultiObservationValues<List<TimeLocationValueTriple>>();
            multiValue.setValue(tlvpValue);
            if (!multiValue.isSetObservationID()) {
                if (value instanceof AbstractObservationValue
                        && ((AbstractObservationValue) value).isSetObservationID()) {
                    multiValue.setObservationID(((AbstractObservationValue) value).getObservationID());
                } else if (isSetObservationID()) {
                    multiValue.setObservationID(getObservationID());
                }
            }
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
    protected boolean mergeValues(ObservationValue<?> observationValue) {
        if (observationValue.getValue() instanceof TLVTValue) {
            TLVTValue tlvtValue = (TLVTValue) observationValue.getValue();
            List<TimeLocationValueTriple> valuesToMerge = tlvtValue.getValue();
            // List<TimeLocationValueTriple> valuesToMerge =
            // (List<TimeLocationValueTriple>)((TLVTValue)observationValue.getValue()).getValue();
            ((TLVTValue) getValue().getValue()).addValues(valuesToMerge);
            checkForFeature(valuesToMerge);
            return true;
        } else {
           return super.mergeValues(observationValue);
        }
    }

    /**
     * Create geometry for featureOfInterest from
     * {@link TimeLocationValueTriple}s
     * 
     * @param values
     *            The {@link TimeLocationValueTriple}s to check for
     *            featureOfInterest
     */
    private void checkForFeature(List<TimeLocationValueTriple> values) {
        AbstractFeature featureOfInterest = getObservationConstellation().getFeatureOfInterest();
        if (featureOfInterest instanceof AbstractSamplingFeature) {
            AbstractSamplingFeature sf = (AbstractSamplingFeature) featureOfInterest;
            Coordinate[] coords = getCoordinates(values);
            int srid = 0;
            if (sf.isSetGeometry()) {
                srid = sf.getGeometry().getSRID();
                coords = (Coordinate[]) ArrayUtils.addAll(sf.getGeometry().getCoordinates(), coords);
            } else {
                TimeLocationValueTriple next = values.iterator().next();
                if (next.isSetLocation()) {
                    srid = next.getLocation().getSRID();
                }
            }
            try {
                if (coords.length == 1) {
                    Point point = new GeometryFactory().createPoint(coords[0]);
                    point.setSRID(srid);
                    sf.setGeometry(point);
                } else if (coords.length > 1) {
                    LineString lineString = new GeometryFactory().createLineString(coords);
                    lineString.setSRID(srid);
                    sf.setGeometry(lineString);
                }
            } catch (InvalidSridException e) {
                // TODO
            }
        }
    }

    /**
     * Get {@link Coordinate}s from the {@link TimeLocationValueTriple}s
     * 
     * @param values
     *            The {@link TimeLocationValueTriple}s to get {@link Coordinate}
     *            s from
     * @return The coordinates
     */
    private Coordinate[] getCoordinates(List<TimeLocationValueTriple> values) {
        List<Coordinate> coords = Lists.newArrayList();
        for (TimeLocationValueTriple timeLocationValueTriple : values) {
            if (timeLocationValueTriple.isSetLocation()) {
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
        final TimeLocationValueTriple timeLocationValueTriple =
                new TimeLocationValueTriple(singleValue.getPhenomenonTime(), singleValue.getValue(), geom);
        tlvpValue.addValue(timeLocationValueTriple);
        return tlvpValue;
    }
}
