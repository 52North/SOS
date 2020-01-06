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
package org.n52.sos.ogc.om.features.samplingFeatures;

import java.util.List;

import org.n52.sos.exception.ows.concrete.InvalidSridException;
import org.n52.sos.ogc.gml.CodeWithAuthority;
import org.n52.sos.ogc.gml.ReferenceType;
import org.n52.sos.ogc.gml.time.Time;
import org.n52.sos.ogc.om.features.SfConstants;
import org.n52.sos.ogc.om.values.QuantityValue;
import org.n52.sos.ogc.ows.OwsExceptionReport;
import org.n52.sos.w3c.xlink.Referenceable;

import com.google.common.collect.Lists;
import com.vividsolutions.jts.geom.Geometry;

public class SfSpecimen extends AbstractSamplingFeature {
    
    private static final long serialVersionUID = -2500483279808128025L;
    /*
     * 1..1
     */
    private ReferenceType materialClass;
    /*
     * 1..1
     */
    private Time samplingTime;
    /*
     * 0..1
     */
    private Referenceable<SfProcess> samplingMethod;
    /*
     * 0..1 samplingLocation use geometry
     */
    /*
     * 0..*
     */
    private List<PreparationStep> processingDetails = Lists.newArrayList();
    /*
     * 0..1
     */
    private QuantityValue size;
    /*
     * 0..1
     */
    private Referenceable<SpecLocation> currentLocation;
    /*
     * 0..1
     */
    private ReferenceType  specimenType;
    
    public SfSpecimen(CodeWithAuthority featureIdentifier) {
        this(featureIdentifier, null);
    }
    
    public SfSpecimen(final CodeWithAuthority featureIdentifier, final String gmlId) {
        super(featureIdentifier, gmlId);
        setDefaultElementEncoding(SfConstants.NS_SPEC);
    }

    @Override
    public String getFeatureType() {
        return SfConstants.SAMPLING_FEAT_TYPE_SF_SPECIMEN;
    }

    /**
     * @return the materialClass
     */
    public ReferenceType getMaterialClass() {
        return materialClass;
    }

    /**
     * @param materialClass the materialClass to set
     */
    public void setMaterialClass(ReferenceType materialClass) {
        this.materialClass = materialClass;
    }

    /**
     * @return the samplingTime
     */
    public Time getSamplingTime() {
        return samplingTime;
    }

    /**
     * @param samplingTime the samplingTime to set
     */
    public void setSamplingTime(Time samplingTime) {
        this.samplingTime = samplingTime;
    }

    /**
     * @return the samplingMethod
     */
    public Referenceable<SfProcess> getSamplingMethod() {
        return samplingMethod;
    }
    
    /**
     * @param samplingMethod the samplingMethod to set
     */
    public void setSamplingMethod(SfProcess samplingMethod) {
        this.samplingMethod = Referenceable.of(samplingMethod);
    }

    /**
     * @param samplingMethod the samplingMethod to set
     */
    public void setSamplingMethod(Referenceable<SfProcess> samplingMethod) {
        this.samplingMethod = samplingMethod;
    }
    
    public boolean isSetSamplingMethod() {
        return getSamplingMethod() != null && !getSamplingMethod().isAbsent();
    }

    /**
     * @return the samplingLocation
     */
    public Geometry getSamplingLocation() {
        return getGeometry();
    }

    /**
     * @param samplingLocation the samplingLocation to set
     * @throws InvalidSridException 
     */
    public void setSamplingLocation(Geometry samplingLocation) throws InvalidSridException {
        setGeometry(samplingLocation);
    }

    public boolean isSetSamplingLocation() {
        return super.isSetGeometry();
    }
    
    /**
     * @return the processingDetails
     */
    public List<PreparationStep> getProcessingDetails() {
        return processingDetails;
    }

    /**
     * @param processingDetails the processingDetails to set
     */
    public void setProcessingDetails(List<PreparationStep> processingDetails) {
        this.processingDetails.clear();
        this.processingDetails.addAll(processingDetails);
    }
    
    public void addProcessingDetails(PreparationStep processingDetails) {
        this.processingDetails.add(processingDetails);
    }
    
    public void addProcessingDetails(List<PreparationStep> processingDetails) {
        this.processingDetails.addAll(processingDetails);
    }

    public boolean isSetProcessingDetails() {
        return !getProcessingDetails().isEmpty();
    }
    
    /**
     * @return the size
     */
    public QuantityValue getSize() {
        return size;
    }

    /**
     * @param size the size to set
     */
    public void setSize(QuantityValue size) {
        this.size = size;
    }
    
    public boolean isSetSize() {
        return getSize() != null;
    }

    /**
     * @return the currentLocation
     */
    public Referenceable<SpecLocation> getCurrentLocation() {
        return currentLocation;
    }

    /**
     * @param currentLocation the currentLocation to set
     */
    public void setCurrentLocation(Referenceable<SpecLocation> currentLocation) {
        this.currentLocation = currentLocation;
    }
    
    public boolean isSetCurrentLocation() {
        return getCurrentLocation() != null && !getCurrentLocation().isAbsent();
    }

    /**
     * @return the specimenType
     */
    public ReferenceType getSpecimenType() {
        return specimenType;
    }

    /**
     * @param specimenType the specimenType to set
     */
    public void setSpecimenType(ReferenceType specimenType) {
        this.specimenType = specimenType;
    }

    public boolean isSetSpecimenType() {
        return getSpecimenType() != null;
    }
    
    @Override
    public <X> X accept(FeatureOfInterestVisitor<X> visitor) throws OwsExceptionReport {
        return visitor.visit(this);
    }
    
    @Override
    public String toString() {
        return String
                .format("SfSpecimen [name=%s, description=%s, xmlDescription=%s, geometry=%s, featureType=%s, url=%s, sampledFeatures=%s, parameters=%s, encode=%b, relatedSamplingFeatures=%s]",
                        getName(), getDescription(), getXmlDescription(), getGeometry(), getFeatureType(), getUrl(),
                        getSampledFeatures(), getParameters(), isEncode(), getRelatedSamplingFeatures());
    }
}
