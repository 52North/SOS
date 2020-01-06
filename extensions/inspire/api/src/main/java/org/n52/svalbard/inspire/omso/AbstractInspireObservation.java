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

import org.n52.sos.ogc.om.OmObservation;
import org.n52.sos.ogc.om.features.samplingFeatures.AbstractSamplingFeature;
import org.n52.sos.ogc.om.values.GeometryValue;

import com.vividsolutions.jts.geom.Geometry;

/**
 * Abstract class for INSPIRE OM Specialised Observations
 * 
 * @author <a href="mailto:c.hollmann@52north.org">Carsten Hollmann</a>
 * @since 4.4.0
 *
 */
public abstract class AbstractInspireObservation extends OmObservation {

    private static final long serialVersionUID = 3681367197554559966L;

    /**
     * constructor
     */
    public AbstractInspireObservation() {
    }

    /**
     * constructor
     * 
     * @param observation
     *            {@link OmObservation} to convert
     */
    public AbstractInspireObservation(OmObservation observation) {
        this();
        observation.copyTo(this);
        if (getObservationConstellation().getFeatureOfInterest() instanceof AbstractSamplingFeature) {
            AbstractSamplingFeature sf = (AbstractSamplingFeature) getObservationConstellation().getFeatureOfInterest();
            sf.setEncode(true);
        }
    }

    /**
     * Check if the {@link OmObservation} has a featureOfInterest with geometry
     * value
     * 
     * @param observation
     *            {@link OmObservation} to check
     * @return <code>true</code>, if the {@link OmObservation} has a
     *         featureOfInterest with geometry value
     */
    protected boolean checkForFeatureGeometry(OmObservation observation) {
        if (observation.getObservationConstellation().isSetFeatureOfInterest() && observation.getObservationConstellation().getFeatureOfInterest() instanceof AbstractSamplingFeature) {
            return ((AbstractSamplingFeature) observation.getObservationConstellation().getFeatureOfInterest())
                    .isSetGeometry();
        }
        return false;
    }

    /**
     * Get the geometry value from the featureOfInterest of the
     * {@link OmObservation}
     * 
     * @param observation
     *            The {@link OmObservation} to get the geometry from
     * @return The geometry
     */
    protected Geometry getGeometryFromFeature(OmObservation observation) {
        return ((AbstractSamplingFeature) observation.getObservationConstellation().getFeatureOfInterest()).getGeometry();
    }

    /**
     * Get the geometry value from the samplingGeometry (om:parameter) of the
     * {@link OmObservation}
     * 
     * @param observation
     *            The {@link OmObservation} to get the geometry from
     * @return The geometry
     */
    protected Geometry getGeometryFromSamplingGeometry(OmObservation observation) {
        return ((GeometryValue) getSpatialFilteringProfileParameter().getValue()).getValue();
    }
}
