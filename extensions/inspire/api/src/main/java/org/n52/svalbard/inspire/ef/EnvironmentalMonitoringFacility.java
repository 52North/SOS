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
package org.n52.svalbard.inspire.ef;

import java.util.Set;

import org.n52.sos.ogc.gml.ReferenceType;
import org.n52.sos.ogc.om.features.samplingFeatures.FeatureOfInterestVisitor;
import org.n52.sos.ogc.om.values.visitor.ValueVisitor;
import org.n52.sos.ogc.ows.OwsExceptionReport;
import org.n52.sos.util.CollectionHelper;
import org.n52.sos.util.JavaHelper;
import org.n52.sos.w3c.xlink.SimpleAttrs;
import org.n52.svalbard.inspire.base.Identifier;

import com.google.common.collect.Sets;
import com.vividsolutions.jts.geom.Point;

public class EnvironmentalMonitoringFacility extends AbstractMonitoringFeature {

    private static final long serialVersionUID = 3316596272258742521L;

    /**
     * 0..1
     */
    private Point representativePoint;

    /**
     * 1..1, nillable
     */
    private ReferenceType measurementRegime;

    /**
     * 1..1, nillable
     */
    private Boolean mobile;

    /**
     * 0..*
     */
    private Set<ReferenceType> resultAcquisitionSource = Sets.newHashSet();

    /**
     * 0..1
     */
    private ReferenceType specialisedEMFType;

    /**
     * 1..*, nillable
     */
    private Set<OperationalActivityPeriod> operationalActivityPeriod = Sets.newHashSet();

    /**
     * 0..*
     */
    private Set<AnyDomainLink> relatedTo = Sets.newHashSet();

    /**
     * 0..*
     */
    private Set<NetworkFacility> belongsTo = Sets.newHashSet();
    
    
    private boolean wasEncoded = false;

    public EnvironmentalMonitoringFacility(SimpleAttrs simpleAttrs) {
        super(simpleAttrs);
    }
    
    public EnvironmentalMonitoringFacility(Identifier inspireId, ReferenceType mediaMonitored) {
        super(inspireId, mediaMonitored);
    }
    
    public EnvironmentalMonitoringFacility(Identifier inspireId, Set<ReferenceType> mediaMonitored) {
        super(inspireId, mediaMonitored);
    }
    
    public EnvironmentalMonitoringFacility(Identifier inspireId, ReferenceType mediaMonitored,
            ReferenceType measurementRegime, boolean mobile, OperationalActivityPeriod operationalActivityPeriod) {
        super(inspireId, mediaMonitored);
        this.measurementRegime = measurementRegime;
        this.mobile = mobile;
        this.operationalActivityPeriod.add(operationalActivityPeriod);
        setDefaultElementEncoding(InspireEfConstants.NS_EF);
    }

    public EnvironmentalMonitoringFacility(Identifier inspireId, Set<ReferenceType> mediaMonitored,
            ReferenceType measurementRegime, boolean mobile,
            Set<OperationalActivityPeriod> operationalActivityPeriod) {
        super(inspireId, mediaMonitored);
        this.measurementRegime = measurementRegime;
        this.mobile = mobile;
        this.operationalActivityPeriod.addAll(operationalActivityPeriod);
        setDefaultElementEncoding(InspireEfConstants.NS_EF);
    }
    
    @Override
    public boolean isSetGmlID() {
        return super.isSetGmlID() && wasEncoded;
    }
    
    @Override
    public String getGmlId() {
        if (!super.isSetGmlID()) {
            final StringBuilder builder = new StringBuilder();
            builder.append("emf");
            builder.append(JavaHelper.generateID(getIdentifierCodeWithAuthority().getValue()));
            setGmlId(builder.toString());
        }
        return super.getGmlId();
    }

    /**
     * @return the representativePoint
     */
    public Point getRepresentativePoint() {
        return representativePoint;
    }

    /**
     * @param representativePoint
     *            the representativePoint to set
     */
    public void setRepresentativePoint(Point representativePoint) {
        this.representativePoint = representativePoint;
    }
    
    public boolean isSetRepresentativePoint() {
        return getRepresentativePoint() != null && !getRepresentativePoint().isEmpty();
    }

    /**
     * @return the measurementRegime
     */
    public ReferenceType getMeasurementRegime() {
        return measurementRegime;
    }
    
    /**
     * @param measurementRegime the measurementRegime to set
     */
    public void setMeasurementRegime(ReferenceType measurementRegime) {
        this.measurementRegime = measurementRegime;
    }

    public boolean isSetMeasurementRegime() {
        return getMeasurementRegime() != null && getMeasurementRegime().isSetHref();
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
    public void setMobile(Boolean mobile) {
        this.mobile = mobile;
    }

    public boolean isSetMobile() {
        return mobile != null;
    }

    /**
     * @return the resultAcquisitionSource
     */
    public Set<ReferenceType> getResultAcquisitionSource() {
        return resultAcquisitionSource;
    }

    /**
     * @param resultAcquisitionSource
     *            the resultAcquisitionSource to set
     */
    public void setResultAcquisitionSource(Set<ReferenceType> resultAcquisitionSource) {
        this.resultAcquisitionSource.clear();
        this.resultAcquisitionSource = resultAcquisitionSource;
    }
    
    public boolean isSetResultAcquisitionSource() {
        return CollectionHelper.isNotEmpty(getResultAcquisitionSource());
    }

    /**
     * @return the specialisedEMFType
     */
    public ReferenceType getSpecialisedEMFType() {
        return specialisedEMFType;
    }

    /**
     * @param specialisedEMFType
     *            the specialisedEMFType to set
     */
    public void setSpecialisedEMFType(ReferenceType specialisedEMFType) {
        this.specialisedEMFType = specialisedEMFType;
    }
    
    public boolean isSetSpecialisedEMFType() {
        return getSpecialisedEMFType() != null;
    }

    /**
     * @return the operationalActivityPeriod
     */
    public Set<OperationalActivityPeriod> getOperationalActivityPeriod() {
        return operationalActivityPeriod;
    }
    
    /**
     * @param operationalActivityPeriod the operationalActivityPeriod to set
     */
    public void setOperationalActivityPeriod(Set<OperationalActivityPeriod> operationalActivityPeriod) {
        this.operationalActivityPeriod = operationalActivityPeriod;
    }

    public boolean isSetOperationalActivityPeriod() {
        return CollectionHelper.isNotEmpty(getOperationalActivityPeriod());
    }

    /**
     * @return the relatedTo
     */
    public Set<AnyDomainLink> getRelatedTo() {
        return relatedTo;
    }

    /**
     * @param relatedTo
     *            the relatedTo to set
     */
    public void setRelatedTo(Set<AnyDomainLink> relatedTo) {
        this.relatedTo.clear();
        this.relatedTo = relatedTo;
    }
    
    public boolean isSetRelatedTo() {
        return CollectionHelper.isNotEmpty(getRelatedTo());
    }

    /**
     * @return the belongsTo
     */
    public Set<NetworkFacility> getBelongsTo() {
        return belongsTo;
    }

    /**
     * @param belongsTo
     *            the belongsTo to set
     */
    public void setBelongsTo(Set<NetworkFacility> belongsTo) {
        this.belongsTo.clear();
        this.belongsTo = belongsTo;
    }
    
    public boolean isSetBelongsTo() {
        return CollectionHelper.isNotEmpty(getBelongsTo());
    }
    
    public void wasEncoded() {
        this.wasEncoded  = true;
    }

}
