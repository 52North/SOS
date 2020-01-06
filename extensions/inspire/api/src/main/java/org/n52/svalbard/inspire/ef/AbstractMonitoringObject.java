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

import org.n52.sos.ogc.gml.AbstractFeature;
import org.n52.sos.ogc.gml.AbstractGeometry;
import org.n52.sos.ogc.gml.ReferenceType;
import org.n52.sos.util.CollectionHelper;
import org.n52.sos.w3c.xlink.AttributeSimpleAttrs;
import org.n52.sos.w3c.xlink.SimpleAttrs;
import org.n52.svalbard.inspire.base.Identifier;
import org.n52.svalbard.inspire.base2.LegislationCitation;
import org.n52.svalbard.inspire.base2.RelatedParty;

import com.google.common.base.Strings;
import com.google.common.collect.Sets;
import com.vividsolutions.jts.geom.Geometry;

public abstract class AbstractMonitoringObject extends AbstractFeature implements AttributeSimpleAttrs {

    private static final long serialVersionUID = -5682405837481118193L;
    
    private SimpleAttrs simpleAttrs;
    
    
    /**
     * 1..1 inspireId, super.identifier
     */
    /**
     * 0..* name, super.name
     */
    
    /**
     * 0..1
     */
    private String additionalDescription;
    
    /**
     * 1..*
     */
    private Set<ReferenceType> mediaMonitored = Sets.newHashSet();
    
    /**
     * 0..*
     */
    private Set<LegislationCitation> legalBackground = Sets.newHashSet();
    
    /**
     * 0..1
     */
    private RelatedParty responsibleParty;
    
    /**
     * 0..1
     */
    private AbstractGeometry geometry;
    
    /**
     * 0..*
     */
    private Set<String> onlineResource = Sets.newHashSet();
    
    /**
     * 0..*
     */
    private Set<ReferenceType> purpose = Sets.newHashSet();

    /**
     * 0..*
     */
    private Set<ObservingCapability> observingCapability = Sets.newHashSet();
    
    /**
     * 0..1
     */
    private Hierarchy broader;
    
    /**
     * 0..*
     */
    private Set<Hierarchy> narrower = Sets.newHashSet();
    
    /**
     * 0..*
     */
    private Set<AbstractMonitoringObject> supersedes = Sets.newHashSet();
    
    /**
     * 0..*
     */
    private Set<AbstractMonitoringObject> supersededBy = Sets.newHashSet();
    
    public AbstractMonitoringObject(SimpleAttrs simpleAttrs) {
        this.simpleAttrs = simpleAttrs;
    }
    
    public AbstractMonitoringObject(Identifier inspireId, ReferenceType mediaMonitored) {
        this(inspireId, Sets.newHashSet(mediaMonitored));
    }
    
    public AbstractMonitoringObject(Identifier inspireId, Set<ReferenceType> mediaMonitored) {
        super(inspireId);
        this.mediaMonitored.addAll(mediaMonitored);
    }

    @Override
    public void setSimpleAttrs(SimpleAttrs simpleAttrs) {
       this.simpleAttrs = simpleAttrs;
    }

    @Override
    public SimpleAttrs getSimpleAttrs() {
        return simpleAttrs;
    }

    @Override
    public boolean isSetSimpleAttrs() {
        return getSimpleAttrs() != null && getSimpleAttrs().isSetHref();
    }
    
    public Identifier getInspireId() {
       return (Identifier) getIdentifierCodeWithAuthority();
    }

    /**
     * @return the additionalDescription
     */
    public String getAdditionalDescription() {
        return additionalDescription;
    }

    /**
     * @param additionalDescription the additionalDescription to set
     */
    public void setAdditionalDescription(String additionalDescription) {
        this.additionalDescription = additionalDescription;
    }
    
    public boolean isSetAdditionalDescription() {
        return !Strings.isNullOrEmpty(getAdditionalDescription());
    }

    /**
     * @return the mediaMonitored
     */
    public Set<ReferenceType> getMediaMonitored() {
        return mediaMonitored;
    }

    /**
     * @param mediaMonitored the mediaMonitored to add
     */
    public void addMediaMonitored(Set<ReferenceType> mediaMonitored) {
        this.mediaMonitored.addAll(mediaMonitored);
    }
    
    /**
     * @param mediaMonitored the mediaMonitored to add
     */
    public void addMediaMonitored(ReferenceType mediaMonitored) {
        this.mediaMonitored.add(mediaMonitored);
    }

    /**
     * @return the legalBackground
     */
    public Set<LegislationCitation> getLegalBackground() {
        return legalBackground;
    }

    /**
     * @param legalBackground the legalBackground to set
     */
    public void setLegalBackground(Set<LegislationCitation> legalBackground) {
        this.legalBackground.clear();
        this.legalBackground = legalBackground;
    }
    
    public boolean isSetLegalBackground() {
        return CollectionHelper.isNotEmpty(getLegalBackground());
    }

    /**
     * @return the responsibleParty
     */
    public RelatedParty getResponsibleParty() {
        return responsibleParty;
    }

    /**
     * @param responsibleParty the responsibleParty to set
     */
    public void setResponsibleParty(RelatedParty responsibleParty) {
        this.responsibleParty = responsibleParty;
    }
    
    public boolean isSetResponsibleParty() {
        return getResponsibleParty() != null;
    }

    /**
     * @return the geometry
     */
    public AbstractGeometry getGeometry() {
        return geometry;
    }

    /**
     * @param geometry the geometry to set
     */
    public void setGeometry(AbstractGeometry geometry) {
        this.geometry = geometry;
    }
    
    /**
     * @param geometry the geometry to set
     */
    public void setGeometry(Geometry geometry) {
        this.geometry = new AbstractGeometry().setGeometry(geometry);
    }
    
    public boolean isSetGeometry() {
        return getGeometry() != null && getGeometry().isSetGeometry();
    }
    

    /**
     * @return the onlineResource
     */
    public Set<String> getOnlineResource() {
        return onlineResource;
    }

    /**
     * @param onlineResource the onlineResource to set
     */
    public void setOnlineResource(Set<String> onlineResource) {
        this.onlineResource.clear();
        this.onlineResource = onlineResource;
    }

   public boolean isSetOnlineResources() {
       return CollectionHelper.isNotEmpty(getOnlineResource());
   }
    
    /**
     * @return the purpose
     */
    public Set<ReferenceType> getPurpose() {
        return purpose;
    }

    /**
     * @param purpose the purpose to set
     */
    public void setPurpose(Set<ReferenceType> purpose) {
        this.purpose.clear();
        this.purpose = purpose;
    }
    
    public boolean isSetPurpose() {
        return CollectionHelper.isNotEmpty(getPurpose());
    }

    /**
     * @return the observingCapability
     */
    public Set<ObservingCapability> getObservingCapability() {
        return observingCapability;
    }

    /**
     * @param observingCapability the observingCapability to set
     */
    public void setObservingCapability(Set<ObservingCapability> observingCapability) {
        this.observingCapability.clear();
        this.observingCapability = observingCapability;
    }
    
    /**
     * @param observingCapability the observingCapability to add
     */
    public void addObservingCapability(ObservingCapability observingCapability) {
        this.observingCapability.add(observingCapability);
    }
    
    public boolean isSetObservingCapability() {
        return CollectionHelper.isNotEmpty(getObservingCapability());
    }

    /**
     * @return the broader
     */
    public Hierarchy getBroader() {
        return broader;
    }

    /**
     * @param broader the broader to set
     */
    public void setBroader(Hierarchy broader) {
        this.broader = broader;
    }
    
    public boolean isSetBroader() {
        return getBroader() != null;
    }

    /**
     * @return the narrower
     */
    public Set<Hierarchy> getNarrower() {
        return narrower;
    }

    /**
     * @param narrower the narrower to set
     */
    public void setNarrower(Set<Hierarchy> narrower) {
        this.narrower.clear();
        this.narrower = narrower;
    }

    
    public boolean isSetNarrower() {
        return CollectionHelper.isNotEmpty(getObservingCapability());
    }
    /**
     * @return the supersedes
     */
    public Set<AbstractMonitoringObject> getSupersedes() {
        return supersedes;
    }

    /**
     * @param supersedes the supersedes to set
     */
    public void setSupersedes(Set<AbstractMonitoringObject> supersedes) {
        this.supersedes.clear();
        this.supersedes = supersedes;
    }

    public boolean isSetSupersedes() {
        return CollectionHelper.isNotEmpty(getSupersedes());
    }
    
    /**
     * @return the supersededBy
     */
    public Set<AbstractMonitoringObject> getSupersededBy() {
        return supersededBy;
    }

    /**
     * @param supersededBy the supersededBy to set
     */
    public void setSupersededBy(Set<AbstractMonitoringObject> supersededBy) {
        this.supersededBy.clear();
        this.supersededBy = supersededBy;
    }
    
    public boolean isSetSupersededBy() {
        return CollectionHelper.isNotEmpty(getSupersededBy());
    }
    
}
