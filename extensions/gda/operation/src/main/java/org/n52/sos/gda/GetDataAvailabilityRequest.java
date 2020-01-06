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
package org.n52.sos.gda;

import java.util.LinkedList;
import java.util.List;

import org.n52.sos.gda.v20.GetDataAvailabilityV20Response;
import org.n52.sos.ogc.filter.ComparisonFilter;
import org.n52.sos.ogc.filter.Filter;
import org.n52.sos.ogc.filter.SpatialFilter;
import org.n52.sos.ogc.ows.OwsExceptionReport;
import org.n52.sos.ogc.sos.ResultFilter;
import org.n52.sos.ogc.sos.ResultFilterConstants;
import org.n52.sos.ogc.sos.SosSpatialFilter;
import org.n52.sos.ogc.sos.SosSpatialFilterConstants;
import org.n52.sos.request.AbstractServiceRequest;
import org.n52.sos.util.CollectionHelper;
import org.n52.sos.util.StringHelper;

import com.google.common.base.Strings;

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

    private List<String> offerings = new LinkedList<String>();
    
    private String responseFormat;

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
    public GetDataAvailabilityRequest addProcedure(String procedure) {
        this.procedures.add(procedure);
        return this;
    }

    /**
     * Add a {@code observedProperty} to the request.
     * 
     * @param observedProperty
     *            the {@code observedProperty}
     */
    public GetDataAvailabilityRequest addObservedProperty(String observedProperty) {
        this.observedProperties.add(observedProperty);
        return this;
    }

    /**
     * Add a {@code featureOfInterest} to the request.
     * 
     * @param featureOfInterest
     *            the {@code featureOfInterest}
     */
    public GetDataAvailabilityRequest addFeatureOfInterest(String featureOfInterest) {
        this.featuresOfInterest.add(featureOfInterest);
        return this;
    }

    public GetDataAvailabilityRequest setFeatureOfInterest(List<String> featuresOfInterest) {
        this.featuresOfInterest = featuresOfInterest;
        return this;
    }

    /**
     * Add a {@code offering} to the request.
     * 
     * @param offering
     *            the {@code offering}
     */
    public GetDataAvailabilityRequest addOffering(String offering) {
        this.offerings.add(offering);
        return this;
    }

    public GetDataAvailabilityRequest setOfferings(List<String> offerings) {
        this.offerings = offerings;
        return this;
    }

    public boolean isSetProcedures() {
        return CollectionHelper.isNotEmpty(getProcedures());
    }

    public GetDataAvailabilityRequest setProcedure(List<String> procedures) {
        this.procedures = procedures;
        return this;
    }
    
    public boolean isSetProcedure() {
        return CollectionHelper.isNotEmpty(getProcedures());
    }

    public GetDataAvailabilityRequest setProcedures(List<String> procedures) {
        this.procedures = procedures;
        return this;
    }

    public boolean isSetObservedProperties() {
        return CollectionHelper.isNotEmpty(getObservedProperties());
    }

    public GetDataAvailabilityRequest setObservedProperty(List<String> observedProperties) {
        this.observedProperties = observedProperties;
        return this;
    }

    public boolean isSetFeaturesOfInterest() {
        return CollectionHelper.isNotEmpty(getFeaturesOfInterest());
    }

    public boolean isSetOfferings() {
        return CollectionHelper.isNotEmpty(getOfferings());
    }

    @Override
    public GetDataAvailabilityResponse getResponse() throws OwsExceptionReport {
        GetDataAvailabilityResponse gdaResponse = null;
        
        if ((isSetResponseFormat() && GetDataAvailabilityConstants.NS_GDA_20.equals(getResponseFormat()))
                || GetDataAvailabilityConstants.NS_GDA_20.equals(getNamespace())) {
            gdaResponse = new GetDataAvailabilityV20Response();
        } else {
            gdaResponse = new GetDataAvailabilityResponse();
        }
        if (isSetResponseFormat()) {
            gdaResponse.setResponseFormat(getResponseFormat());
        }
        gdaResponse.set(this);
        return gdaResponse;
    }

    /**
     * @return the responseFormat
     */
    public String getResponseFormat() {
        return responseFormat;
    }

    /**
     * @param responseFormat the responseFormat to set
     */
    public GetDataAvailabilityRequest setResponseFormat(String responseFormat) {
        this.responseFormat = responseFormat;
        return this;
    }
    
    public boolean isSetResponseFormat() {
        return !Strings.isNullOrEmpty(getResponseFormat());
    }

    public GetDataAvailabilityRequest setNamespace(String namspace) {
        if (StringHelper.isNotEmpty(namspace)) {
            this.namspace = namspace;
        }
        return this;
    }

    public String getNamespace() {
        return this.namspace;
    }

    public boolean hasResultFilter() {
        return isSetExtensions() && hasExtension(ResultFilterConstants.RESULT_FILTER)
                && getExtension(ResultFilterConstants.RESULT_FILTER) instanceof ResultFilter;
    }
    
    public Filter<?> getResultFilter() {
        if (hasResultFilter()) {
            return ((ResultFilter)getExtension(ResultFilterConstants.RESULT_FILTER)).getValue();
        }
        return null;
    }

    public GetDataAvailabilityRequest setResultFilter(ComparisonFilter filter) {
        addExtension(new ResultFilter(filter));
        return this;
    }
    
    public boolean hasSpatialFilter() {
        return isSetExtensions() && hasExtension(SosSpatialFilterConstants.SPATIAL_FILTER)
                && getExtension(SosSpatialFilterConstants.SPATIAL_FILTER) instanceof SosSpatialFilter;
    }
    
    public SpatialFilter getSpatialFilter() {
        if (hasSpatialFilter()) {
            return ((SosSpatialFilter)getExtension(SosSpatialFilterConstants.SPATIAL_FILTER)).getValue();
        }
        return null;
    }

    public GetDataAvailabilityRequest setSpatialFilter(SpatialFilter filter) {
        addExtension(new SosSpatialFilter(filter));
        return this;
    }
}
