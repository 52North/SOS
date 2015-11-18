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
package org.n52.sos.ogc.om;

public class ObservationMergeIndicator {
    
    private boolean procedure = true;
    
    private boolean observableProperty = true;
    
    private boolean featureOfInterest = true;
    
    private boolean offerings = true;
    
    private boolean phenomenonTime = false;
    
    private boolean samplingGeometry = false;
    
    
    /**
     * @return the procedure
     */
    public boolean isProcedure() {
        return procedure;
    }

    /**
     * @param procedure the procedure to set
     */
    public ObservationMergeIndicator setProcedure(boolean procedure) {
        this.procedure = procedure;
        return this;
    }

    /**
     * @return the observableProperty
     */
    public boolean isObservableProperty() {
        return observableProperty;
    }

    /**
     * @param observableProperty the observableProperty to set
     */
    public ObservationMergeIndicator setObservableProperty(boolean observableProperty) {
        this.observableProperty = observableProperty;
        return this;
    }

    /**
     * @return the featureOfInterest
     */
    public boolean isFeatureOfInterest() {
        return featureOfInterest;
    }

    /**
     * @param featureOfInterest the featureOfInterest to set
     */
    public ObservationMergeIndicator setFeatureOfInterest(boolean featureOfInterest) {
        this.featureOfInterest = featureOfInterest;
        return this;
    }
    
    /**
     * @return the offerings
     */
    public boolean isOfferings() {
        return offerings;
    }

    /**
     * @param offerings the offerings to set
     */
    public void setOfferings(boolean offerings) {
        this.offerings = offerings;
    }

    public boolean sameObservationConstellation() {
        return isProcedure() && isObservableProperty() && isFeatureOfInterest() && isOfferings();
    }

    /**
     * @return the phenomenonTime
     */
    public boolean isPhenomenonTime() {
        return phenomenonTime;
    }

    /**
     * @param phenomenonTime the phenomenonTime to set
     */
    public void setPhenomenonTime(boolean phenomenonTime) {
        this.phenomenonTime = phenomenonTime;
    }

    /**
     * @return the samplingGeometry
     */
    public boolean isSamplingGeometry() {
        return samplingGeometry;
    }

    /**
     * @param samplingGeometry the samplingGeometry to set
     */
    public void setSamplingGeometry(boolean samplingGeometry) {
        this.samplingGeometry = samplingGeometry;
    }
    

}
