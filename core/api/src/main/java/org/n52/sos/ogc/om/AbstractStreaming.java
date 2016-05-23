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

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.n52.sos.exception.CodedException;
import org.n52.sos.exception.sos.ResponseExceedsSizeLimitException;
import org.n52.sos.ogc.om.values.Value;
import org.n52.sos.ogc.ows.OWSConstants.AdditionalRequestParams;
import org.n52.sos.ogc.ows.OwsExceptionReport;
import org.n52.sos.ogc.sos.Sos2Constants;
import org.n52.sos.service.Configurator;
import org.n52.sos.service.ServiceConfiguration;
import org.n52.sos.util.CollectionHelper;
import org.n52.sos.util.GeometryHandler;

import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.vividsolutions.jts.geom.Geometry;

public abstract class AbstractStreaming extends AbstractObservationValue<Value<OmObservation>> {

    private static final long serialVersionUID = -4290319005184152231L;

    private Map<AdditionalRequestParams, Object> additionalRequestParams = Maps.newHashMap();

    private String responseFormat;
    
    private int maxNumberOfValues = Integer.MIN_VALUE;
    
    private int currentNumberOfValues = 0;

    public abstract boolean hasNextValue() throws OwsExceptionReport;

    public abstract OmObservation nextSingleObservation() throws OwsExceptionReport;
    

    public Collection<OmObservation> mergeObservation() throws OwsExceptionReport {
        List<OmObservation> observations = getObservation();
        // TODO merge all observations with the same observationContellation
        // FIXME Failed to set the observation type to sweArrayObservation for
        // the merged Observations
        // (proc, obsProp, foi)
        if (CollectionHelper.isNotEmpty(observations)) {
            final List<OmObservation> mergedObservations = new LinkedList<OmObservation>();
            int obsIdCounter = 1;
            for (final OmObservation sosObservation : observations) {
                if (mergedObservations.isEmpty()) {
                    sosObservation.setObservationID(Integer.toString(obsIdCounter++));
                    mergedObservations.add(sosObservation);
                } else {
                    boolean combined = false;
                    for (final OmObservation combinedSosObs : mergedObservations) {
                        if (combinedSosObs.checkForMerge(sosObservation)) {
                            combinedSosObs.setResultTime(null);
                            combinedSosObs.mergeWithObservation(sosObservation);
                            combined = true;
                            break;
                        }
                    }
                    if (!combined) {
                        mergedObservations.add(sosObservation);
                    }
                }
            }
            return mergedObservations;
        }
        return observations;
    }

    public List<OmObservation> getObservation() throws OwsExceptionReport {
        List<OmObservation> observations = Lists.newArrayList();
        do {
            observations.add(nextSingleObservation());
        } while (hasNextValue());
        return observations;
    }

    public void add(AdditionalRequestParams parameter, Object object) {
        additionalRequestParams.put(parameter, object);
    }

    public boolean contains(AdditionalRequestParams parameter) {
        return additionalRequestParams.containsKey(parameter);
    }

    public boolean isSetAdditionalRequestParams() {
        return CollectionHelper.isNotEmpty(additionalRequestParams);
    }

    protected Object getAdditionalRequestParams(AdditionalRequestParams parameter) {
        return additionalRequestParams.get(parameter);
    }

    /**
     * Check and modify observation for Spatial Filtering Profile and requested
     * crs
     * 
     * @param observation
     *            {@link OmObservation} to check
     * @throws OwsExceptionReport
     *             If an error occurs when modifying the {@link OmObservation}
     */
    @SuppressWarnings("unchecked")
    protected void checkForModifications(OmObservation observation) throws OwsExceptionReport {
        if (isSetAdditionalRequestParams() && contains(AdditionalRequestParams.crs)) {
            Object additionalRequestParam = getAdditionalRequestParams(AdditionalRequestParams.crs);
            int targetCRS = -1;
            if (additionalRequestParam instanceof Integer) {
                targetCRS = (Integer) additionalRequestParam;
            } else if (additionalRequestParam instanceof String) {
                targetCRS = Integer.parseInt((String) additionalRequestParam);
            }
            if (observation.isSetParameter()) {
                for (NamedValue<?> namedValue : observation.getParameter()) {
                    if (Sos2Constants.HREF_PARAMETER_SPATIAL_FILTERING_PROFILE.equals(namedValue.getName().getHref())) {
                        NamedValue<Geometry> spatialFilteringProfileParameter = (NamedValue<Geometry>) namedValue;
                        spatialFilteringProfileParameter.getValue().setValue(
                                GeometryHandler.getInstance().transform(
                                        spatialFilteringProfileParameter.getValue().getValue(), targetCRS));
                    }
                }
            }
        }
    }

    @Override
    public boolean isSetValue() {
        return true;
    }
    
    public void setResponseFormat(String responseFormat) {
        this.responseFormat = responseFormat;
    }
    
    public String getResponseFormat() {
        if (Strings.isNullOrEmpty(responseFormat)) {
            this.responseFormat = Configurator.getInstance().getProfileHandler().getActiveProfile().getObservationResponseFormat();
        }
        return responseFormat;
    }

    /**
     * @return the maxNumberOfValues
     */
    public int getMaxNumberOfValues() {
        return maxNumberOfValues;
    }

    /**
     * @param maxNumberOfValues the maxNumberOfValues to set
     */
    public void setMaxNumberOfValues(int maxNumberOfValues) {
        this.maxNumberOfValues = maxNumberOfValues;
    }
    
    /**
     * Check if the max number of returned values is exceeded
     *
     * @param size
     *            Max number count
     * @throws CodedException
     *             If the size limit is exceeded
     */
    protected void checkMaxNumberOfReturnedValues(int size) throws CodedException {
        if (ServiceConfiguration.getInstance().getMaxNumberOfReturnedValues() > 0) {
            currentNumberOfValues += size;
            if (currentNumberOfValues > getMaxNumberOfValues()) {
                throw new ResponseExceedsSizeLimitException().at("maxNumberOfReturnedValues");
            }
        }
    }

}
