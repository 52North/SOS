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
package org.n52.sos.ogc.sensorML.v20;

import java.util.Collection;
import java.util.Map;
import java.util.Set;

import org.n52.sos.ogc.gml.AbstractFeature;
import org.n52.sos.ogc.swe.SweAbstractDataComponent;
import org.n52.sos.ogc.swe.SweConstants.SweDataComponentType;
import org.n52.sos.util.CollectionHelper;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

/**
 * Class that represents SensorML 2.0 FeatrureOfInterest
 * 
 * @author Carsten Hollmann <c.hollmann@52north.org>
 * @since 4.2.0
 *
 */
public class SmlFeatureOfInterest extends SweAbstractDataComponent {
    
    private final Set<String> featuresOfInterest = Sets.newLinkedHashSet();
    
    private final Map<String, AbstractFeature> featuresOfInterestMap = Maps.newHashMap();
    
    @Override
    public SweDataComponentType getDataComponentType() {
        return null;
    }
    
    
    public SmlFeatureOfInterest addFeaturesOfInterest(Collection<String> features) {
        getFeaturesOfInterest().addAll(features);
        return this;
    }
    
    public SmlFeatureOfInterest addFeatureOfInterest(String featureIdentifier) {
        getFeaturesOfInterest().add(featureIdentifier);
        return this;
    }
    
    public Set<String> getFeaturesOfInterest() {
        return featuresOfInterest;
    }

    public boolean isSetFeaturesOfInterest() {
        return CollectionHelper.isNotEmpty(getFeaturesOfInterest()) || CollectionHelper.isNotEmpty(getFeaturesOfInterestMap());
    }
    
    public SmlFeatureOfInterest addFeaturesOfInterest(Map<String, AbstractFeature> featuresOfInterestMap) {
        getFeaturesOfInterestMap().putAll(featuresOfInterestMap);
        return this;
    }
    
    public SmlFeatureOfInterest addFeatureOfInterest(AbstractFeature feature) {
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
    
    public boolean isSetFeatures() {
        return isSetFeaturesOfInterest() || isSetFeaturesOfInterestMap();
    }
}
