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

import java.util.List;
import java.util.Map;

import org.n52.sos.ogc.filter.SpatialFilter;
import org.n52.sos.ogc.filter.TemporalFilter;
import org.n52.sos.ogc.ows.OwsExceptionReport;
import org.n52.sos.ogc.sos.SosConstants;
import org.n52.sos.response.GetFeatureOfInterestResponse;
import org.n52.sos.util.CollectionHelper;

/**
 * Sos GetFeatureOfInterst request
 * 
 * @since 4.0.0
 */
public class GetFeatureOfInterestRequest extends AbstractServiceRequest<GetFeatureOfInterestResponse> {

    /**
     * FOI identifiers list
     */
    private List<String> featureIdentifiers;

    /**
     * FOI observedProperties list
     */
    private List<String> observedProperties;

    /**
     * FOI procedures list
     */
    private List<String> procedures;

    /**
     * FOI spatial filters list
     */
    private List<SpatialFilter> spatialFilters;

    /**
     * FOI temporal filters list
     */
    private List<TemporalFilter> temporalFilters;

    private Map<String, String> namespaces;

    /*
     * (non-Javadoc)
     * 
     * @see org.n52.sos.request.AbstractSosRequest#getOperationName()
     */
    @Override
    public String getOperationName() {
        return SosConstants.Operations.GetFeatureOfInterest.name();
    }

    /**
     * Get temporal filters
     * 
     * @return temporal filters
     */
    public List<TemporalFilter> getTemporalFilters() {
        return temporalFilters;
    }

    /**
     * Set temporal filters
     * 
     * @param temporalFilters
     *            temporal filters
     */
    public void setTemporalFilters(List<TemporalFilter> temporalFilters) {
        this.temporalFilters = temporalFilters;
    }

    /**
     * Get FOI identifiers
     * 
     * @return FOI identifiers
     */
    public List<String> getFeatureIdentifiers() {
        return featureIdentifiers;
    }

    /**
     * Set FOI identifiers
     * 
     * @param featureIDs
     *            FOI identifiers
     */
    public void setFeatureIdentifiers(List<String> featureIDs) {
        this.featureIdentifiers = featureIDs;
    }

    /**
     * Get FOI observedProperties
     * 
     * @return FOI observedProperties
     */
    public List<String> getObservedProperties() {
        return observedProperties;
    }

    /**
     * Set FOI observedProperties
     * 
     * @param observedProperties
     *            FOI observedProperties
     */
    public void setObservedProperties(List<String> observedProperties) {
        this.observedProperties = observedProperties;
    }

    /**
     * Get FOI procedures
     * 
     * @return FOI procedures
     */
    public List<String> getProcedures() {
        return procedures;
    }

    /**
     * Set FOI procedures
     * 
     * @param procedures
     *            FOI procedures
     */
    public void setProcedures(List<String> procedures) {
        this.procedures = procedures;
    }

    /**
     * Get spatial filters
     * 
     * @return spatial filters
     */
    public List<SpatialFilter> getSpatialFilters() {
        return spatialFilters;
    }

    /**
     * Set spatial filters
     * 
     * @param spatialFilters
     *            spatial filters
     */
    public void setSpatialFilters(List<SpatialFilter> spatialFilters) {
        this.spatialFilters = spatialFilters;
    }

    public void setNamespaces(Map<String, String> namespaces) {
        this.namespaces = namespaces;
    }

    public Map<String, String> getNamespaces() {
        return namespaces;
    }

    public boolean isSetFeatureOfInterestIdentifiers() {
        return CollectionHelper.isNotEmpty(getFeatureIdentifiers());
    }

    public boolean isSetTemporalFilters() {
        return CollectionHelper.isNotEmpty(getTemporalFilters());
    }

    public boolean isSetSpatialFilters() {
        return CollectionHelper.isNotEmpty(getSpatialFilters());
    }

    public boolean isSetObservableProperties() {
        return CollectionHelper.isNotEmpty(getObservedProperties());
    }

    public boolean isSetProcedures() {
        return CollectionHelper.isNotEmpty(getProcedures());
    }

    public boolean isSetNamespaces() {
        return CollectionHelper.isNotEmpty(namespaces);
    }

    public boolean containsOnlyFeatureParameter() {
        return !isSetObservableProperties() && !isSetProcedures() && !isSetTemporalFilters();
    }

    public boolean hasNoParameter() {
        return !isSetObservableProperties() && !isSetProcedures() && !isSetTemporalFilters() && !isSetFeatureOfInterestIdentifiers();
    }

    @Override
    public GetFeatureOfInterestResponse getResponse() throws OwsExceptionReport {
        return (GetFeatureOfInterestResponse) new GetFeatureOfInterestResponse().set(this);
    }

}
