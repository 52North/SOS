/**
 * Copyright (C) 2012-2015 52°North Initiative for Geospatial Open Source
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
package org.n52.sos.ogc.sos;

import java.util.Collection;
import java.util.Map;
import java.util.Set;

import org.n52.sos.ogc.gml.AbstractFeature;
import org.n52.sos.ogc.gml.time.Time;
import org.n52.sos.ogc.om.AbstractPhenomenon;
import org.n52.sos.util.CollectionHelper;

import com.google.common.base.Objects;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

/**
 * @since 4.0.0
 *
 */
public abstract class SosProcedureDescription extends AbstractFeature {
    private static final long serialVersionUID = 1144253800787127139L;
    private String sensorDescriptionXmlString;
    private String descriptionFormat;
    private final Map<String, AbstractFeature> featuresOfInterestMap = Maps.newHashMap();
    private final Map<String, AbstractPhenomenon> phenomenonMap = Maps.newHashMap();
    private final Set<SosOffering> offerings = Sets.newLinkedHashSet();
    private final Set<String> featuresOfInterest = Sets.newLinkedHashSet();
    private final Set<String> parentProcedures = Sets.newLinkedHashSet();
    private final Set<SosProcedureDescription> childProcedures = Sets.newLinkedHashSet();
    private Time validTime;

    @Override
    public SosProcedureDescription setIdentifier(String identifier) {
        super.setIdentifier(identifier);
        return this;
    }
    
    public Set<SosOffering> getOfferings() {
        return offerings;
    }

    public SosProcedureDescription addOfferings(Collection<SosOffering> offerings) {
        this.offerings.addAll(offerings);
        return this;
    }

    public SosProcedureDescription addOffering(SosOffering offering) {
        if (offering != null) {
            this.offerings.add(offering);
        }
        return this;
    }

    public boolean isSetOfferings() {
        return offerings != null && !offerings.isEmpty();
    }

    public String getSensorDescriptionXmlString() {
        return sensorDescriptionXmlString;
    }

    public SosProcedureDescription setSensorDescriptionXmlString(String sensorDescriptionXmlString) {
        this.sensorDescriptionXmlString = sensorDescriptionXmlString;
        return this;
    }

    public boolean isSetSensorDescriptionXmlString() {
        return sensorDescriptionXmlString != null &&
               !sensorDescriptionXmlString.isEmpty();
    }

    public String getDescriptionFormat() {
        return descriptionFormat;
    }

    public SosProcedureDescription setDescriptionFormat(String descriptionFormat) {
        this.descriptionFormat = descriptionFormat;
        return this;
    }
    
    public SosProcedureDescription setFeaturesOfInterest(Collection<String> features) {
    	getFeaturesOfInterest().clear();
        getFeaturesOfInterest().addAll(features);
        return this;
    }

    public SosProcedureDescription addFeaturesOfInterest(Collection<String> features) {
        getFeaturesOfInterest().addAll(features);
        return this;
    }
    
    public SosProcedureDescription addFeatureOfInterest(String featureIdentifier) {
        getFeaturesOfInterest().add(featureIdentifier);
        return this;
    }
    
    public Set<String> getFeaturesOfInterest() {
        return featuresOfInterest;
    }

    public boolean isSetFeaturesOfInterest() {
        return CollectionHelper.isNotEmpty(getFeaturesOfInterest());
    }
    
    public SosProcedureDescription setFeaturesOfInterest(Map<String, AbstractFeature> featuresOfInterestMap) {
    	this.featuresOfInterestMap.clear();
    	this.featuresOfInterestMap.putAll(featuresOfInterestMap);
        return this;
    }
    
    public SosProcedureDescription addFeaturesOfInterest(Map<String, AbstractFeature> featuresOfInterestMap) {
        getFeaturesOfInterestMap().putAll(featuresOfInterestMap);
        return this;
    }
    
    public SosProcedureDescription addFeatureOfInterest(AbstractFeature feature) {
        getFeaturesOfInterestMap().put(feature.getIdentifier(), feature);
        return this;
    }
    
    public Map<String, AbstractFeature> getFeaturesOfInterestMap() {
        return featuresOfInterestMap;
    }

    public boolean isSetFeaturesOfInterestMap() {
        return CollectionHelper.isNotEmpty(getFeaturesOfInterestMap());
    }
    
    public boolean hasAbstractFeatureFor(String identifier) {
        return isSetFeaturesOfInterestMap() && getFeaturesOfInterestMap().containsKey(identifier);
    }
    
    public AbstractFeature getAbstractFeatureFor(String identifier) {
        return getFeaturesOfInterestMap().get(identifier);
    }

    public SosProcedureDescription setParentProcedures(Collection<String> parentProcedures) {
    	this.parentProcedures.clear();
        this.parentProcedures.addAll(parentProcedures);
        return this;
    }
    
    public SosProcedureDescription addParentProcedures(Collection<String> parentProcedures) {
        this.parentProcedures.addAll(parentProcedures);
        return this;
    }

    public SosProcedureDescription addParentProcedure(String parentProcedureIdentifier) {
        this.parentProcedures.add(parentProcedureIdentifier);
        return this;
    }

    public Set<String> getParentProcedures() {
        return parentProcedures;
    }

    public boolean isSetParentProcedures() {
        return parentProcedures != null && !parentProcedures.isEmpty();
    }

    public SosProcedureDescription addChildProcedures(Collection<SosProcedureDescription> childProcedures) {
        if (childProcedures != null) {
            this.childProcedures.addAll(childProcedures);
        }
        return this;
    }

    public SosProcedureDescription addChildProcedure(SosProcedureDescription childProcedure) {
        this.childProcedures.add(childProcedure);
        return this;
    }

    public Set<SosProcedureDescription> getChildProcedures() {
        return childProcedures;
    }

    public boolean isSetChildProcedures() {
        return getChildProcedures() != null && !getChildProcedures().isEmpty();
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getIdentifierCodeWithAuthority());
    }

    @Override
    public boolean equals(final Object obj) {
        if (obj != null && getClass().equals(obj.getClass())) {
            final SosProcedureDescription other = (SosProcedureDescription) obj;
            return Objects.equal(getIdentifierCodeWithAuthority(), other.getIdentifierCodeWithAuthority());

        }
        return false;
    }

    @Override
    public String toString() {
        return "SosProcedureDescription [identifier=" + getIdentifier() + "]";
    }

    public SosProcedureDescription setValidTime(Time validTime) {
        this.validTime = validTime;
        return this;
    }

    public boolean isSetValidTime() {
        return getValidTime() != null;
    }

    public Time getValidTime() {
        return this.validTime;
    }

    public SosProcedureDescription addPhenomenon(AbstractPhenomenon phenomenon) {
        getPhenomenon().put(phenomenon.getIdentifier(), phenomenon);
        return this;
    }
    
    public SosProcedureDescription setPhenomenon(Map<String, AbstractPhenomenon> phenomenons) {
    	getPhenomenon().clear();
        getPhenomenon().putAll(phenomenons);
        return this;
    }
    
    public SosProcedureDescription addPhenomenon(Map<String, AbstractPhenomenon> phenomenons) {
        getPhenomenon().putAll(phenomenons);
        return this;
    }
    
    public Map<String, AbstractPhenomenon> getPhenomenon() {
        return phenomenonMap;
    }
    
    public boolean isSetPhenomenon() {
        return CollectionHelper.isNotEmpty(getPhenomenon());
    }
    
    public boolean hasPhenomenonFor(String identifier) {
        return isSetPhenomenon() && getPhenomenon().containsKey(identifier);
    }
    
    public AbstractPhenomenon getPhenomenonFor(String identifer) {
        return getPhenomenon().get(identifer);
    }
    
    
    /**
     * Copies all values from this object to the copyOf object except XML description and description format
     * @param copyOf {@link SosProcedureDescription} to copy values to
     */
    public void copyTo(SosProcedureDescription copyOf) {
        super.copyTo(copyOf);
        copyOf.setValidTime(getValidTime());
        copyOf.setFeatureOfInterest(getFeaturesOfInterest());
        copyOf.setFeatureOfInterestMap(getFeaturesOfInterestMap());
        copyOf.setOffetrings(getOfferings());
        copyOf.setParentProcedures(getParentProcedures());
        copyOf.setChildProcedures(getChildProcedures());
    }
    
    public boolean isSetFeatures() {
        return isSetFeaturesOfInterest() || isSetFeaturesOfInterestMap();
    }

    private void setFeatureOfInterest(Set<String> featuresOfInterest) {
        this.featuresOfInterest.addAll(featuresOfInterest);
    }

    private void setFeatureOfInterestMap(Map<String, AbstractFeature> featuresOfInterestMap) {
        this.featuresOfInterestMap.putAll(featuresOfInterestMap);
    }

    private void setOffetrings(Set<SosOffering> offerings) {
        this.offerings.addAll(offerings);
    }

    private void setParentProcedures(Set<String> parentProcedures) {
        this.parentProcedures.addAll(parentProcedures);
    }

    private void setChildProcedures(Set<SosProcedureDescription> childProcedures) {
        this.childProcedures.addAll(childProcedures);
    }
}
