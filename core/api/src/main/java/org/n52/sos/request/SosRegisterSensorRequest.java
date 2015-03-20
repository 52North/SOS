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
package org.n52.sos.request;

import java.util.Collection;

import org.n52.sos.ogc.gml.AbstractFeature;
import org.n52.sos.ogc.om.AbstractPhenomenon;
import org.n52.sos.ogc.ows.OwsExceptionReport;
import org.n52.sos.ogc.sos.Sos1Constants;
import org.n52.sos.response.AbstractServiceResponse;
import org.n52.sos.response.DummyResponse;

/**
 * SOS RegisterSensor request
 * 
 * @since 4.0.0
 */
public class SosRegisterSensorRequest extends AbstractServiceRequest<AbstractServiceResponse> {

    /**
     * RegisterSensor operation name
     */
    private final String operationName = Sos1Constants.Operations.RegisterSensor.name();

    /**
     * SOS Sensor system
     */
    // private SensorSystem system;

    /**
     * observableProperties collection
     */
    private Collection<AbstractPhenomenon> observableProperties;

    /**
     * featureOfInterest collection
     */
    private Collection<AbstractFeature> featuresOfInterest;

    /**
     * Sensor description
     */
    private String sensorDescription;

    /**
     * constructor
     * 
     * @param system
     *            SOS sensor system
     * @param sosComponents
     *            observableProperties
     * @param sensorDescription
     *            Sensor description
     * @param featuresOfInterest
     *            featuresOfInterst
     */
    /**
     * constructor
     * 
     * @param observableProperties
     *            Observable properties
     * @param sensorDescription
     *            Sensor description as String
     * @param featuresOfInterest
     *            FeatureOfInterest
     */
    public SosRegisterSensorRequest(Collection<AbstractPhenomenon> observableProperties, String sensorDescription,
            Collection<AbstractFeature> featuresOfInterest) {
        // this.system = system;
        this.observableProperties = observableProperties;
        this.sensorDescription = sensorDescription;
        this.featuresOfInterest = featuresOfInterest;
    }

    /**
     * Get observableProperties
     * 
     * @return observableProperties
     */
    public Collection<AbstractPhenomenon> getObservableProperties() {
        return observableProperties;
    }

    /**
     * Set observableProperties
     * 
     * @param observableProperties
     *            observableProperties
     */
    public void setObservableProperties(Collection<AbstractPhenomenon> observableProperties) {
        this.observableProperties = observableProperties;
    }

    /**
     * Get sensor description
     * 
     * @return sensor description
     */
    public String getSensorDescription() {
        return sensorDescription;
    }

    /**
     * Set sensor description
     * 
     * @param sensorDescription
     *            sensor description
     */
    public void setSensorDescription(String sensorDescription) {
        this.sensorDescription = sensorDescription;
    }

    // /**
    // * Get SOS sensor system
    // *
    // * @return SOS sensor system
    // */
    // public SensorSystem getSystem() {
    // return system;
    // }
    //
    // /**
    // * Set SOS sensor system
    // *
    // * @param system
    // * SOS sensor system
    // */
    // public void setSystem(SensorSystem system) {
    // this.system = system;
    // }

    /**
     * Get featuresOfInterst
     * 
     * @return featuresOfInterst
     */
    public Collection<AbstractFeature> getFeaturesOfInterest() {
        return featuresOfInterest;
    }

    /**
     * Set featuresOfInterst
     * 
     * @param featuresOfInterest
     *            featuresOfInterst
     */
    public void setFeaturesOfInterest(Collection<AbstractFeature> featuresOfInterest) {
        this.featuresOfInterest = featuresOfInterest;
    }

    @Override
    public String getOperationName() {
        return operationName;
    }
    
    @Override
    public AbstractServiceResponse getResponse() throws OwsExceptionReport {
        return (AbstractServiceResponse) new DummyResponse().setOperationName(getOperationName()).set(this).setVersion(getVersion());
    }

}
