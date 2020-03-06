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
package org.n52.sos.ds.hibernate.entities.feature;

import java.util.Date;

import org.n52.sos.ds.hibernate.entities.Unit;
import org.n52.sos.ogc.gml.AbstractFeature;
import org.n52.sos.ogc.ows.OwsExceptionReport;

import com.google.common.base.Strings;
import com.vividsolutions.jts.geom.Geometry;

public class Specimen extends FeatureOfInterest {

    private static final long serialVersionUID = -6274017088296908033L;
    
    private String materialClass;
    private Date samplingTimeStart;
    private Date samplingTimeEnd;
    private String samplingMethod;
    private Double size;
    private Unit sizeUnit;
    private String currentLocation;
    private String  specimenType;
    
    /**
     * @return the materialClass
     */
    public String getMaterialClass() {
        return materialClass;
    }

    /**
     * @param materialClass the materialClass to set
     */
    public void setMaterialClass(String materialClass) {
        this.materialClass = materialClass;
    }

    /**
     * @return the samplingTimeStart
     */
    public Date getSamplingTimeStart() {
        return samplingTimeStart;
    }

    /**
     * @param samplingTimeStart the samplingTimeStart to set
     */
    public void setSamplingTimeStart(Date samplingTimeStart) {
        this.samplingTimeStart = samplingTimeStart;
    }
    
    /**
     * @return the samplingTimeEnd
     */
    public Date getSamplingTimeEnd() {
        return samplingTimeEnd;
    }

    /**
     * @param samplingTimeEnd the samplingTimeEnd to set
     */
    public void setSamplingTimeEnd(Date samplingTimeEnd) {
        this.samplingTimeEnd = samplingTimeEnd;
    }

    /**
     * @return the samplingMethod
     */
    public String getSamplingMethod() {
        return samplingMethod;
    }
    
    /**
     * @param samplingMethod the samplingMethod to set
     */
    public void setSamplingMethod(String samplingMethod) {
        this.samplingMethod = samplingMethod;
    }
    
    public boolean isSetSamplingMethod() {
        return !Strings.isNullOrEmpty(getSamplingMethod());
    }

    /**
     * @return the size
     */
    public Double getSize() {
        return size;
    }

    /**
     * @param size the size to set
     */
    public void setSize(Double size) {
        this.size = size;
    }
    
    public boolean isSetSize() {
        return getSize() != null;
    }
    
    public Unit getSizeUnit() {
        return sizeUnit;
    }

    public void setSizeUnit(final Unit sizeUnit) {
        this.sizeUnit = sizeUnit;
    }

    public boolean isSetSizeUnit() {
        return getSizeUnit() != null && getSizeUnit().isSetUnit();
    }

    /**
     * @return the currentLocation
     */
    public String getCurrentLocation() {
        return currentLocation;
    }

    /**
     * @param currentLocation the currentLocation to set
     */
    public void setCurrentLocation(String currentLocation) {
        this.currentLocation = currentLocation;
    }
    
    public boolean isSetCurrentLocation() {
        return !Strings.isNullOrEmpty(getCurrentLocation());
    }

    /**
     * @return the specimenType
     */
    public String getSpecimenType() {
        return specimenType;
    }

    /**
     * @param specimenType the specimenType to set
     */
    public void setSpecimenType(String specimenType) {
        this.specimenType = specimenType;
    }

    public boolean isSetSpecimenType() {
        return !Strings.isNullOrEmpty(getSpecimenType());
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
