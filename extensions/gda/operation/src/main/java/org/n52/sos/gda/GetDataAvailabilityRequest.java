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
package org.n52.sos.gda;

import java.util.LinkedList;
import java.util.List;

import org.n52.sos.ogc.ows.OwsExceptionReport;
import org.n52.sos.request.AbstractServiceRequest;
import org.n52.sos.util.CollectionHelper;
import org.n52.sos.util.StringHelper;

/**
 * A request to obtain the {@code DataAvailabilites} of the SOS.
 * 
 * @author Christian Autermann
 * 
 * @since 4.0.0
 */
public class GetDataAvailabilityRequest extends AbstractServiceRequest<GetDataAvailabilityResponse> {

    private List<String> procedures = new LinkedList<String>();

    private List<String> observedProperties = new LinkedList<String>();

    private List<String> featuresOfInterest = new LinkedList<String>();
    
    private List<String> offerings =  new LinkedList<String>();
    
    private String namspace = GetDataAvailabilityConstants.NS_GDA;

    @Override
    public String getOperationName() {
        return GetDataAvailabilityConstants.OPERATION_NAME;
    }

    /**
     * @return the requested {@code procedures}.
     */
    public List<String> getProcedures() {
        return procedures;
    }

    /**
     * @return the requested {@code observedProperties}.
     */
    public List<String> getObservedProperties() {
        return observedProperties;
    }

    /**
     * @return the requested {@code featuresOfInterest}.
     */
    public List<String> getFeaturesOfInterest() {
        return featuresOfInterest;
    }
    
    /**
     * @return the requested {@code offerings}.
     */
    public List<String> getOfferings() {
        return offerings;
    }

    /**
     * Add a {@code procedure} to the request.
     * 
     * @param procedure
     *            the {@code procedure}
     */
    public void addProcedure(String procedure) {
        this.procedures.add(procedure);
    }

    /**
     * Add a {@code observedProperty} to the request.
     * 
     * @param observedProperty
     *            the {@code observedProperty}
     */
    public void addObservedProperty(String observedProperty) {
        this.observedProperties.add(observedProperty);
    }

    /**
     * Add a {@code featureOfInterest} to the request.
     * 
     * @param featureOfInterest
     *            the {@code featureOfInterest}
     */
    public void addFeatureOfInterest(String featureOfInterest) {
        this.featuresOfInterest.add(featureOfInterest);
    }
    
    public void setFeatureOfInterest(
			List<String> featuresOfInterest) {
		this.featuresOfInterest = featuresOfInterest;
	}

	/**
     * Add a {@code offering} to the request.
     * 
     * @param offering
     *            the {@code offering}
     */
    public void addOffering(String offering) {
        this.offerings.add(offering);
    }

    public void setOffering(List<String> offerings) {
    	this.offerings = offerings;
	}

	public boolean isSetProcedures() {
        return CollectionHelper.isNotEmpty(getProcedures());
    }

    public void setProcedure(List<String> procedures) {
		this.procedures = procedures;
	}

	public boolean isSetObservedProperties() {
        return CollectionHelper.isNotEmpty(getObservedProperties());
    }

    public void setObservedProperty(
			List<String> observedProperties) {
		this.observedProperties = observedProperties;
	}

	public boolean isSetFeaturesOfInterest() {
        return CollectionHelper.isNotEmpty(getFeaturesOfInterest());
    }
    
    public boolean isSetOfferings() {
        return CollectionHelper.isNotEmpty(getOfferings());
    }

    @Override
    public GetDataAvailabilityResponse getResponse() throws OwsExceptionReport {
        return (GetDataAvailabilityResponse) new GetDataAvailabilityResponse().set(this);
    }

    public void setNamespace(String namspace) {
        if (StringHelper.isNotEmpty(namspace)) {
            this.namspace = namspace;
        }
    }
    
    public String getNamespace() {
        return this.namspace;
    }
    
}
