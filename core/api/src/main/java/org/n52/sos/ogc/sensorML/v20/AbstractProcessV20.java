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
package org.n52.sos.ogc.sensorML.v20;

import org.n52.sos.ogc.gml.ReferenceType;
import org.n52.sos.ogc.sensorML.AbstractProcess;
import org.n52.sos.ogc.sensorML.SensorML20Constants;
import org.n52.sos.ogc.sensorML.SensorMLConstants;

/**
 * Class that represents SensorML 2.0 AbstractProcess
 * 
 * @author Carsten Hollmann <c.hollmann@52north.org>
 * @since 4.2.0
 *
 */
public class AbstractProcessV20 extends AbstractProcess {

    private static final long serialVersionUID = 1L;

    private AbstractSettings configuration;

    private SmlFeatureOfInterest featureOfInterest;

    private AbstractModes modes;

    private String definition;

    public AbstractProcessV20 setSmlFeatureOfInterest(SmlFeatureOfInterest featureOfInterest) {
        this.featureOfInterest = featureOfInterest;
        if (featureOfInterest.isSetFeatures()) {
            if (featureOfInterest.isSetFeaturesOfInterest()) {
                addFeaturesOfInterest(featureOfInterest.getFeaturesOfInterest());
            }
            if (featureOfInterest.isSetFeaturesOfInterestMap()) {
                addFeaturesOfInterest(featureOfInterest.getFeaturesOfInterestMap());
            }
        }
        return this;
    }

    public SmlFeatureOfInterest getSmlFeatureOfInterest() {
        if (featureOfInterest == null && (isSetFeaturesOfInterest() || isSetFeaturesOfInterestMap())) {
            featureOfInterest = new SmlFeatureOfInterest();
            featureOfInterest.setDefinition(SensorMLConstants.FEATURE_OF_INTEREST_FIELD_DEFINITION);
            featureOfInterest.setLabel(SensorMLConstants.ELEMENT_NAME_FEATURES_OF_INTEREST);
        }
        if (isSetFeaturesOfInterest()) {
            featureOfInterest.addFeaturesOfInterest(getFeaturesOfInterest());
        }
        if (isSetFeaturesOfInterestMap()) {
            featureOfInterest.addFeaturesOfInterest(getFeaturesOfInterestMap());
        }
        return featureOfInterest;
    }

    public boolean isSetSmlFeatureOfInterest() {
        return (featureOfInterest != null && featureOfInterest.isSetFeatures()) || isSetFeatures();
    }

    @Override
    public ReferenceType getTypeOf() {
        return super.getTypeOf();
    }

    @Override
    public void setTypeOf(ReferenceType typeOf) {
        super.setTypeOf(typeOf);
    }

    @Override
    public boolean isSetTypeOf() {
        return super.isSetTypeOf();
    }

    /**
     * @return the configuration
     */
    public AbstractSettings getConfiguration() {
        return configuration;
    }

    /**
     * @param configuration the configuration to set
     */
    public void setConfiguration(AbstractSettings configuration) {
        this.configuration = configuration;
    }

    /**
     * @return the modes
     */
    public AbstractModes getModes() {
        return modes;
    }

    /**
     * @param modes the modes to set
     */
    public void setModes(AbstractModes modes) {
        this.modes = modes;
    }

    /**
     * @return the definition
     */
    public String getDefinition() {
        return definition;
    }

    /**
     * @param definition the definition to set
     */
    public void setDefinition(String definition) {
        this.definition = definition;
    }
    
    @Override
    public String getDescriptionFormat() {
        return SensorML20Constants.NS_SML_20;
    }
    
    @Override
    public String getDefaultElementEncoding() {
        return SensorML20Constants.NS_SML_20;
    }

}
