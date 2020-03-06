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
package org.n52.sos.ds.hibernate.entities.feature.inspire;

import org.n52.sos.ds.hibernate.entities.feature.FeatureOfInterest;
import org.n52.sos.ds.hibernate.entities.feature.FeatureVisitor;
import org.n52.sos.ds.hibernate.entities.feature.GeometryVisitor;
import org.n52.sos.ogc.gml.AbstractFeature;
import org.n52.sos.ogc.ows.OwsExceptionReport;

import com.vividsolutions.jts.geom.Geometry;

public class EnvironmentalMonitoringFacility extends FeatureOfInterest {

    private static final long serialVersionUID = -4612931300484622090L;

    private MediaMonitored mediaMonitored;
    
    private String measurementRegime;
    
    private boolean mobile;

    /**
     * @return the mediaMonitored
     */
    public MediaMonitored getMediaMonitored() {
        return mediaMonitored;
    }

    /**
     * @param mediaMonitored the mediaMonitored to set
     */
    public void setMediaMonitored(MediaMonitored mediaMonitored) {
        this.mediaMonitored = mediaMonitored;
    }

    /**
     * @return the measurementRegime
     */
    public String getMeasurementRegime() {
        return measurementRegime;
    }

    /**
     * @param measurementRegime the measurementRegime to set
     */
    public void setMeasurementRegime(String measurementRegime) {
        this.measurementRegime = measurementRegime;
    }

    /**
     * @return the mobile
     */
    public boolean isMobile() {
        return mobile;
    }

    /**
     * @param mobile the mobile to set
     */
    public void setMobile(boolean mobile) {
        this.mobile = mobile;
    }
    
    @Override
    public AbstractFeature accept(FeatureVisitor visitor) throws OwsExceptionReport {
        return visitor.visit(this);
    }

    @Override
    public Geometry accept(GeometryVisitor visitor) throws OwsExceptionReport {
        return visitor.visit(this);
    }
}
