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

import java.net.URI;
import java.util.Set;

import org.n52.sos.ogc.gml.AbstractFeature;
import org.n52.sos.ogc.gml.time.Time;
import org.n52.sos.util.CollectionHelper;
import org.n52.sos.w3c.xlink.AttributeSimpleAttrs;
import org.n52.sos.w3c.xlink.SimpleAttrs;
import org.n52.svalbard.inspire.base.Identifier;
import org.n52.svalbard.inspire.base2.RelatedParty;

import com.google.common.collect.Sets;

public class EnvironmentalMonitoringActivity extends AbstractFeature implements AttributeSimpleAttrs {

    private static final long serialVersionUID = -5702172788560838642L;

    private SimpleAttrs simpleAttrs;
    
    /**
     * 1..1
     */
    private Time activityTime;
    
    /**
     * 1..1
     */
    private String activityConditions;
    
    /**
     * 0..1
     */
    private Object boundingBox;
    
    /**
     * 1..1
     */
    private RelatedParty responsibleParty;
    
    /**
     * 1..1
     */
    private Identifier inspireId;
    
    /**
     * 0..*
     */
    private Set<URI> onlineResource = Sets.newHashSet();
    
    /**
     * 0..*
     */
    private Set<EnvironmentalMonitoringProgramme> setUpFor = Sets.newHashSet();
    
    /**
     * 0..*
     */
    private Set<AbstractMonitoringFeature> uses = Sets.newHashSet();

    public EnvironmentalMonitoringActivity(SimpleAttrs simpleAttrs) {
        this.simpleAttrs = simpleAttrs;
    }
    
    public EnvironmentalMonitoringActivity(Time activityTime, String activityConditions, RelatedParty responsibleParty,
            Identifier inspireId) {
        this.activityTime = activityTime;
        this.activityConditions = activityConditions;
        this.responsibleParty = responsibleParty;
        this.inspireId = inspireId;
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

    /**
     * @return the activityTime
     */
    public Time getActivityTime() {
        return activityTime;
    }

    /**
     * @return the activityConditions
     */
    public String getActivityConditions() {
        return activityConditions;
    }

    /**
     * @return the boundingBox
     */
    public Object getBoundingBox() {
        return boundingBox;
    }

    /**
     * @param boundingBox the boundingBox to set
     */
    public void setBoundingBox(Object boundingBox) {
        this.boundingBox = boundingBox;
    }
    
    public boolean isSetBoundingBox() {
        return getBoundingBox() != null;
    }

    /**
     * @return the responsibleParty
     */
    public RelatedParty getResponsibleParty() {
        return responsibleParty;
    }

    /**
     * @return the inspireId
     */
    public Identifier getInspireId() {
        return inspireId;
    }

    /**
     * @return the onlineResource
     */
    public Set<URI> getOnlineResource() {
        return onlineResource;
    }

    /**
     * @param onlineResource the onlineResource to set
     */
    public void setOnlineResource(Set<URI> onlineResource) {
        this.onlineResource.clear();
        this.onlineResource = onlineResource;
    }
    
    public boolean isSetOnlineResource() {
        return CollectionHelper.isNotEmpty(getOnlineResource());
    }

    /**
     * @return the setUpFor
     */
    public Set<EnvironmentalMonitoringProgramme> getSetUpFor() {
        return setUpFor;
    }

    /**
     * @param setUpFor the setUpFor to set
     */
    public void setSetUpFor(Set<EnvironmentalMonitoringProgramme> setUpFor) {
        this.setUpFor.clear();
        this.setUpFor = setUpFor;
    }
    
    public boolean isSetUpFor() {
        return CollectionHelper.isNotEmpty(getSetUpFor());
    }

    /**
     * @return the uses
     */
    public Set<AbstractMonitoringFeature> getUses() {
        return uses;
    }

    /**
     * @param uses the uses to set
     */
    public void setUses(Set<AbstractMonitoringFeature> uses) {
        this.uses.clear();
        this.uses = uses;
    }

    public boolean isSetUses() {
        return CollectionHelper.isNotEmpty(getUses());
    }
    
}