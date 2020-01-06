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
package org.n52.sos.request;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.n52.sos.ogc.filter.ComparisonFilter;
import org.n52.sos.ogc.filter.Filter;
import org.n52.sos.ogc.filter.SpatialFilter;
import org.n52.sos.ogc.filter.TemporalFilter;
import org.n52.sos.ogc.gml.time.TimeInstant;
import org.n52.sos.ogc.ows.OwsExceptionReport;
import org.n52.sos.ogc.sos.ResultFilter;
import org.n52.sos.ogc.sos.ResultFilterConstants;
import org.n52.sos.ogc.sos.Sos2Constants;
import org.n52.sos.ogc.sos.SosConstants;
import org.n52.sos.ogc.sos.SosConstants.SosIndeterminateTime;
import org.n52.sos.ogc.sos.SosSpatialFilter;
import org.n52.sos.ogc.sos.SosSpatialFilterConstants;
import org.n52.sos.ogc.swes.SwesExtension;
import org.n52.sos.ogc.swes.SwesExtensions;
import org.n52.sos.response.AbstractObservationResponse;
import org.n52.sos.response.GetObservationResponse;
import org.n52.sos.util.CollectionHelper;
import org.n52.sos.util.StringHelper;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

/**
 * SOS GetObservation request
 * 
 * @since 4.0.0
 */
public class GetObservationRequest extends AbstractObservationRequest implements SpatialFeatureQueryRequest {

    /**
     * Request as String
     */
    private String requestString;

    /**
     * Offerings list
     */
    private List<String> offerings = Lists.newLinkedList();

    /**
     * Temporal filters list
     */
    private List<TemporalFilter> temporalFilters = Lists.newLinkedList();

    /**
     * Procedures list
     */
    private List<String> procedures = Lists.newLinkedList();

    /**
     * ObservedProperties list
     */
    private List<String> observedProperties = Lists.newLinkedList();

    /**
     * FOI identifiers list
     */
    private List<String> featureIdentifiers = Lists.newLinkedList();

    /**
     * Spatial filters list
     */
    private SpatialFilter spatialFilter;

    private Map<String, String> namespaces = Maps.newHashMap();

    /*
     * (non-Javadoc)
     * 
     * @see org.n52.sos.request.AbstractSosRequest#getOperationName()
     */
    @Override
    public String getOperationName() {
        return SosConstants.Operations.GetObservation.name();
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

    /**
     * Get observableProperties
     * 
     * @return observableProperties
     */
    public List<String> getObservedProperties() {
        return observedProperties;
    }

    /**
     * Set observedProperties
     * 
     * @param observedProperties
     *            observedProperties
     */
    public void setObservedProperties(List<String> observedProperties) {
        this.observedProperties = observedProperties;
    }

    /**
     * Get offerings
     * 
     * @return offerings
     */
    public List<String> getOfferings() {
        return offerings;
    }

    /**
     * Set offerings
     * 
     * @param offerings
     *            offerings
     */
    public void setOfferings(List<String> offerings) {
        this.offerings = offerings;
    }

    /**
     * Get procedures
     * 
     * @return procedures
     */
    public List<String> getProcedures() {
        return procedures;
    }

    /**
     * Set procedures
     * 
     * @param procedures
     *            procedures
     */
    public void setProcedures(List<String> procedures) {
        this.procedures = procedures;
    }

    /**
     * Get result filters
     * 
     * @return result filters
     */
    @Deprecated
    public Filter<?> getResult() {
        return getResultFilter();
    }

    /**
     * Set result filters
     * 
     * @param result
     *            result filters
     */
    @Deprecated
    public void setResult(ComparisonFilter result) {
        this.setResultFilter(result);
    }

    /**
     * Check if a result filter is set
     * 
     * @return <code>true</code>, if a result filter is set
     */
    @Deprecated
    public boolean isSetResultFilter() {
        return getResultFilter() != null;
    }

    /**
     * 
     * Get request as String
     * 
     * @return request as String
     */
    public String getRequestString() {
        return requestString;
    }

    /**
     * Set request as String
     * 
     * @param requestString
     *            request as String
     */
    public void setRequestString(String requestString) {
        this.requestString = requestString;
    }

    /**
     * Get spatial filter
     * 
     * @return spatial filter
     */
    @Override
    public SpatialFilter getSpatialFilter() {
        if (hasExtension(SosSpatialFilterConstants.SPATIAL_FILTER)) {
            return ((SosSpatialFilter) getExtension(SosSpatialFilterConstants.SPATIAL_FILTER)).getValue();
        } else
            return spatialFilter;
    }

    /**
     * Set spatial filter
     * 
     * @param resultSpatialFilter
     *            spatial filter
     */
    @Override
    public void setSpatialFilter(SpatialFilter resultSpatialFilter) {
        this.spatialFilter = resultSpatialFilter;
    }

    /**
     * Create a copy of this request with defined observableProperties
     * 
     * @param obsProps
     *            defined observableProperties
     * @return SOS GetObservation request copy
     */
    public GetObservationRequest copyOf(List<String> obsProps) {
        GetObservationRequest res = new GetObservationRequest();
        super.copyOf(res);
        res.setTemporalFilters(this.temporalFilters);
        res.setObservedProperties(obsProps);
        res.setOfferings(this.offerings);
        res.setProcedures(this.procedures);
        res.setResponseFormat(getResponseFormat());
        res.setResponseMode(getResponseMode());
        res.setSpatialFilter(this.spatialFilter);
        res.setResultModel(getResultModel());
        res.setFeatureIdentifiers(this.featureIdentifiers);
        res.setService(this.getService());
        res.setRequestString(this.requestString);
        return res;

    }

    public void setNamespaces(Map<String, String> namespaces) {
        this.namespaces = namespaces;
    }

    public Map<String, String> getNamespaces() {
        return namespaces;
    }

    public boolean isSetOffering() {
        if (offerings != null && !offerings.isEmpty()) {
            return true;
        }
        return false;
    }

    public boolean isSetObservableProperty() {
        if (observedProperties != null && !observedProperties.isEmpty()) {
            return true;
        }
        return false;
    }

    public boolean isSetProcedure() {
        if (procedures != null && !procedures.isEmpty()) {
            return true;
        }
        return false;
    }

    @Override
    public boolean isSetFeatureOfInterest() {
        if (featureIdentifiers != null && !featureIdentifiers.isEmpty()) {
            return true;
        }
        return false;
    }

    public boolean isSetTemporalFilter() {
        if (temporalFilters != null && !temporalFilters.isEmpty()) {
            return true;
        }
        return false;
    }

    @Override
    public boolean isSetSpatialFilter() {
        if (spatialFilter != null || hasExtension(SosSpatialFilterConstants.SPATIAL_FILTER)) {
            return true;
        }
        return false;
    }

    public boolean hasFirstLatestTemporalFilter() {
        for (TemporalFilter temporalFilter : temporalFilters) {
            if (temporalFilter.getTime() instanceof TimeInstant) {
                TimeInstant ti = (TimeInstant) temporalFilter.getTime();
                if (ti.isSetSosIndeterminateTime()) {
                    return true;
                }
            }
        }
        return false;
    }

    public List<SosIndeterminateTime> getFirstLatestTemporalFilter() {
        List<SosIndeterminateTime> tf = new LinkedList<SosIndeterminateTime>();
        for (TemporalFilter temporalFilter : temporalFilters) {
            if (temporalFilter.getTime() instanceof TimeInstant) {
                TimeInstant ti = (TimeInstant) temporalFilter.getTime();
                if (ti.isSetSosIndeterminateTime()) {
                    tf.add(ti.getSosIndeterminateTime());
                }
            }
        }
        return tf;
    }

    public List<TemporalFilter> getNotFirstLatestTemporalFilter() {
        List<TemporalFilter> tf = new LinkedList<TemporalFilter>();
        for (TemporalFilter temporalFilter : temporalFilters) {
            if (temporalFilter.getTime() instanceof TimeInstant) {
                TimeInstant ti = (TimeInstant) temporalFilter.getTime();
                if (!ti.isSetSosIndeterminateTime()) {
                    tf.add(temporalFilter);
                }
            } else {
                tf.add(temporalFilter);
            }
        }
        return tf;
    }

    public boolean hasTemporalFilters() {
        return temporalFilters != null && !temporalFilters.isEmpty();
    }

    public boolean isEmpty() {
        return !isSetOffering() && !isSetObservableProperty() && !isSetProcedure() && !isSetFeatureOfInterest()
                && !isSetTemporalFilter() && !isSetSpatialFilter();
    }

    @Override
    public boolean hasSpatialFilteringProfileSpatialFilter() {
        return isSetSpatialFilter() 
                && (getSpatialFilter().getValueReference().equals(Sos2Constants.VALUE_REFERENCE_SPATIAL_FILTERING_PROFILE)
                        || (hasExtension(SosSpatialFilterConstants.SPATIAL_FILTER) 
                                && ((SosSpatialFilter) getExtension(SosSpatialFilterConstants.SPATIAL_FILTER)).getValue()
                                .getValueReference().equals(Sos2Constants.VALUE_REFERENCE_SPATIAL_FILTERING_PROFILE)));
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

    public GetObservationRequest setResultFilter(Filter<?> filter) {
        addExtension(new ResultFilter(filter));
        return this;
    }

    public boolean isSetRequestString() {
        return StringHelper.isNotEmpty(getRequestString());
    }

    public boolean isSetResult() {
        return getResult() != null;
    }

    public boolean isSetNamespaces() {
        return CollectionHelper.isNotEmpty(getNamespaces());
    }

    @Override
    public AbstractObservationResponse getResponse() throws OwsExceptionReport {
        return (GetObservationResponse) new GetObservationResponse().set(this);
    }


    /**
     * Check if the {@link SwesExtensions} contains {@link Filter}
     * 
     * @return <code>true</code>, if the {@link SwesExtensions} contains
     *         {@link Filter}
     */
    public boolean isSetFesFilterExtension() {
        if (isSetExtensions()) {
            for (SwesExtension<?> extension : getExtensions().getExtensions()) {
                if (isFesFilterExtension(extension)) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Get all {@link SwesExtensions} with {@link Filter}
     * 
     * @return All {@link SwesExtensions} with {@link Filter}
     */
    public Set<SwesExtension<?>> getFesFilterExtensions() {
        Set<SwesExtension<?>> set = Sets.newHashSet();
        if (isSetExtensions()) {
            for (SwesExtension<?> extension : getExtensions().getExtensions()) {
                if (isFesFilterExtension(extension)) {
                    set.add(extension);
                }
            }
        }
        return set;
    }

    private boolean isFesFilterExtension(SwesExtension<?> extension) {
        return !((extension instanceof ResultFilter) 
                || (extension instanceof SpatialFilter)
                || (extension instanceof SosSpatialFilter)) 
                && extension.getValue() instanceof Filter<?>;
    }
}
