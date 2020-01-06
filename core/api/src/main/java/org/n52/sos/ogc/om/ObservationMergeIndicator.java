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
package org.n52.sos.ogc.om;

public class ObservationMergeIndicator {

    private boolean procedure = false;

    private boolean observableProperty = false;

    private boolean featureOfInterest = false;

    private boolean offerings = false;

    private boolean phenomenonTime = false;
    
    private boolean resultTime = false;

    private boolean samplingGeometry = false;
    
    private boolean observationType = true;

    public ObservationMergeIndicator() {
    }

    public static ObservationMergeIndicator defaultObservationMergerIndicator() {
        return new ObservationMergeIndicator().setFeatureOfInterest(true).setObservableProperty(true)
                .setProcedure(true).setOfferings(true);
    }

    /**
     * @return the procedure
     */
    public boolean isProcedure() {
        return procedure;
    }

    /**
     * @param procedure
     *            the procedure to set
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
     * @param observableProperty
     *            the observableProperty to set
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
     * @param featureOfInterest
     *            the featureOfInterest to set
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
     * @param offerings
     *            the offerings to set
     */
    public ObservationMergeIndicator setOfferings(boolean offerings) {
        this.offerings = offerings;
        return this;
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
     * @param phenomenonTime
     *            the phenomenonTime to set
     */
    public ObservationMergeIndicator setPhenomenonTime(boolean phenomenonTime) {
        this.phenomenonTime = phenomenonTime;
        return this;
    }
    
    /**
     * @return the resultTime
     */
    public boolean isSetResultTime() {
        return resultTime;
    }

    /**
     * @param resultTime
     *            the resultTime to set
     */
    public ObservationMergeIndicator setResultTime(boolean resultTime) {
        this.resultTime = resultTime;
        return this;
    }

    /**
     * @return the samplingGeometry
     */
    public boolean isSamplingGeometry() {
        return samplingGeometry;
    }

    /**
     * @param samplingGeometry
     *            the samplingGeometry to set
     */
    public ObservationMergeIndicator setSamplingGeometry(boolean samplingGeometry) {
        this.samplingGeometry = samplingGeometry;
        return this;
    }

    public boolean isCheckObservationType() {
        return observationType;
    }
    
    public ObservationMergeIndicator setObservationType(boolean observationType) {
        this.observationType = observationType;
        return this;
    }

}
