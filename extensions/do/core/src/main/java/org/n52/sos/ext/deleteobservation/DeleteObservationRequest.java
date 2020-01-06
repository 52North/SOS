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
package org.n52.sos.ext.deleteobservation;

import java.util.Collection;
import java.util.Set;

import org.n52.sos.ogc.filter.TemporalFilter;
import org.n52.sos.ogc.ows.OwsExceptionReport;
import org.n52.sos.request.AbstractServiceRequest;
import org.n52.sos.request.ResponseFormat;
import org.n52.sos.util.CollectionHelper;

import com.google.common.base.Strings;
import com.google.common.collect.Sets;

/**
 * @author <a href="mailto:e.h.juerrens@52north.org">Eike Hinderk
 *         J&uuml;rrens</a>
 * 
 * @since 1.0.0
 */
public class DeleteObservationRequest extends AbstractServiceRequest<DeleteObservationResponse>
        implements ResponseFormat {

    private final String operationName = DeleteObservationConstants.Operations.DeleteObservation.name();

    private Set<String> observationIdentifiers = Sets.newHashSet();

    private Set<String> procedures = Sets.newHashSet();

    private Set<String> observedProperties = Sets.newHashSet();

    private Set<String> features = Sets.newHashSet();

    private Set<String> offerings = Sets.newHashSet();

    private Set<TemporalFilter> temporalFilters = Sets.newLinkedHashSet();

    private String responseFormat;

    public DeleteObservationRequest(String responseFormat) {
        this.responseFormat = responseFormat;
    }

    @Override
    public String getOperationName() {
        return operationName;
    }

    public Set<String> getObservationIdentifiers() {
        return observationIdentifiers;
    }

    public void setObservationIdentifiers(Collection<String> observationIdentifier) {
        this.observationIdentifiers.clear();
        if (observationIdentifier != null) {
            this.observationIdentifiers.addAll(observationIdentifier);
        }
    }

    public void addObservationIdentifier(String observationIdentifier) {
        this.observationIdentifiers.add(observationIdentifier);
    }

    public boolean isSetObservationIdentifiers() {
        return CollectionHelper.isNotEmpty(getObservationIdentifiers());
    }

    /**
     * @return the procedures
     */
    public Set<String> getProcedures() {
        return procedures;
    }

    /**
     * @param procedures
     *            the procedures to set
     */
    public void setProcedures(Collection<String> procedures) {
        this.procedures.clear();
        if (procedures != null) {
            this.procedures.addAll(procedures);
        }
    }

    public void addProcedure(String procedure) {
        this.procedures.add(procedure);
    }

    public boolean isSetprocedures() {
        return CollectionHelper.isNotEmpty(getProcedures());
    }

    /**
     * @return the observedProperties
     */
    public Set<String> getObservedProperties() {
        return observedProperties;
    }

    /**
     * @param observedProperties
     *            the observedProperties to set
     */
    public void setObservedProperties(Collection<String> observedProperties) {
        this.observedProperties.clear();
        if (observedProperties != null) {
            this.observedProperties.addAll(observedProperties);
        }
    }

    public void addObservedProperty(String observedProperty) {
        this.observedProperties.add(observedProperty);
    }

    public boolean isSetObservedProperty() {
        return CollectionHelper.isNotEmpty(getObservedProperties());
    }

    /**
     * @return the featureOfInterest
     */
    public Set<String> getFeatureIdentifiers() {
        return features;
    }

    /**
     * @param featureOfInterest
     *            the featureOfInterest to set
     */
    public void setFeatureIdentifiers(Collection<String> featureOfInterest) {
        this.features.clear();
        if (featureOfInterest != null) {
            this.features.addAll(featureOfInterest);
        }
    }

    public void addFeatureIdentifier(String featureOfInterest) {
        this.features.add(featureOfInterest);
    }

    public boolean isSetFeatureIdentifiers() {
        return CollectionHelper.isNotEmpty(getFeatureIdentifiers());
    }

    /**
     * @return the offerings
     */
    public Set<String> getOfferings() {
        return offerings;
    }

    /**
     * @param offerings
     *            the offerings to set
     */
    public void setOfferings(Collection<String> offerings) {
        this.offerings.clear();
        if (offerings != null) {
            this.offerings.addAll(offerings);
        }
    }

    public void addOffering(String offering) {
        this.offerings.add(offering);
    }

    public boolean isSetOfferings() {
        return CollectionHelper.isNotEmpty(getOfferings());
    }

    /**
     * @return the temporalFilters
     */
    public Set<TemporalFilter> getTemporalFilters() {
        return temporalFilters;
    }

    /**
     * @param temporalFilters
     *            the temporalFilters to set
     */
    public void setTemporalFilters(Collection<TemporalFilter> temporalFilters) {
        this.temporalFilters.clear();
        if (temporalFilters != null) {
            this.temporalFilters.addAll(temporalFilters);
        }
    }

    public void addTemporalFilter(TemporalFilter temporalFilter) {
        this.temporalFilters.add(temporalFilter);
    }

    public boolean isSetTemporalFilters() {
        return CollectionHelper.isNotEmpty(getTemporalFilters());
    }

    @Override
    public String getResponseFormat() {
        return responseFormat;
    }

    @Override
    public void setResponseFormat(String responseFormat) {
        this.responseFormat = responseFormat;
    }

    @Override
    public boolean isSetResponseFormat() {
        return !Strings.isNullOrEmpty(getResponseFormat());
    }

    @Override
    public String toString() {
        return String.format(
                "DeleteObservationRequest [service=%s, version=%s, observationIdentifier=%s, operationName=%s]",
                getService(), getVersion(), observationIdentifiers, operationName);
    }

    @Override
    public DeleteObservationResponse getResponse() throws OwsExceptionReport {
        return (DeleteObservationResponse) new DeleteObservationResponse(this.getResponseFormat()).set(this);
    }

}
