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
import org.n52.sos.ogc.sos.Sos2Constants;
import org.n52.sos.ogc.sos.SosConstants;
import org.n52.sos.response.GetResultResponse;
import org.n52.sos.util.CollectionHelper;
import org.n52.sos.util.StringHelper;

/**
 * @since 4.0.0
 * 
 */
public class GetResultRequest extends AbstractServiceRequest<GetResultResponse> implements SpatialFeatureQueryRequest {

    private final String operationName = SosConstants.Operations.GetResult.name();

    /**
     * Identifier for the observation template
     */
    private String observationTemplateIdentifier;

    private String offering;

    private String observedProperty;

    private List<String> featureIdentifiers;

    private List<TemporalFilter> temporalFilter;

    private SpatialFilter spatialFilter;

    private Map<String, String> namespaces;

    public GetResultRequest() {
        super();
    }

    @Override
    public String getOperationName() {
        return operationName;
    }

    /**
     * Get observation template identifier
     * 
     * @return observation template identifier
     */
    public String getObservationTemplateIdentifier() {
        return observationTemplateIdentifier;
    }

    /**
     * Set observation template identifier
     * 
     * @param observationTemplateIdentifier
     *            observation template identifier
     */
    public void setObservationTemplateIdentifier(String observationTemplateIdentifier) {
        this.observationTemplateIdentifier = observationTemplateIdentifier;
    }

    public boolean isSetObservationTemplateIdentifier() {
        return StringHelper.isNotEmpty(getObservationTemplateIdentifier());
    }

    public String getOffering() {
        return offering;
    }

    public void setOffering(String offering) {
        this.offering = offering;
    }

    public boolean isSetOffering() {
        return StringHelper.isNotEmpty(getOffering());
    }

    public String getObservedProperty() {
        return observedProperty;
    }

    public void setObservedProperty(String observedProperty) {
        this.observedProperty = observedProperty;
    }

    public boolean isSetObservedProperty() {
        return StringHelper.isNotEmpty(getObservedProperty());
    }

    /**
     * Get FOI identifiers
     * 
     * @return FOI identifiers
     */
    @Override
    public List<String> getFeatureIdentifiers() {
        return featureIdentifiers;
    }

    /**
     * Set FOI identifiers
     * 
     * @param featureIdentifiers
     *            FOI identifiers
     */
    @Override
    public void setFeatureIdentifiers(List<String> featureIdentifiers) {
        this.featureIdentifiers = featureIdentifiers;
    }

    @Override
    public boolean isSetFeatureOfInterest() {
        return CollectionHelper.isNotEmpty(getFeatureIdentifiers());
    }

    public List<TemporalFilter> getTemporalFilter() {
        return temporalFilter;
    }

    public void setTemporalFilter(List<TemporalFilter> temporalFilters) {
        this.temporalFilter = temporalFilters;
    }

    public boolean hasTemporalFilter() {
        return CollectionHelper.isNotEmpty(getTemporalFilter());
    }

    @Override
    public SpatialFilter getSpatialFilter() {
        return spatialFilter;
    }

    @Override
    public void setSpatialFilter(SpatialFilter spatialFilter) {
        this.spatialFilter = spatialFilter;
    }

    @Override
    public boolean isSetSpatialFilter() {
        return getSpatialFilter() != null;
    }

    public Map<String, String> getNamespaces() {
        return namespaces;
    }

    public void setNamespaces(Map<String, String> namespaces) {
        this.namespaces = namespaces;
    }

    public boolean isSetNamespaces() {
        return CollectionHelper.isNotEmpty(getNamespaces());
    }

    @Override
    public boolean hasSpatialFilteringProfileSpatialFilter() {
        return isSetSpatialFilter()
                && getSpatialFilter().getValueReference().equals(
                        Sos2Constants.VALUE_REFERENCE_SPATIAL_FILTERING_PROFILE);
    }

    @Override
    public GetResultResponse getResponse() throws OwsExceptionReport {
        return (GetResultResponse) new GetResultResponse().set(this);
    }

}
