/**
 * Copyright 2015 52°North Initiative for Geospatial Open Source
 * Software GmbH
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.n52.iceland.ogc.om;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.n52.iceland.exception.CodedException;
import org.n52.iceland.exception.sos.ResponseExceedsSizeLimitException;
import org.n52.iceland.ogc.om.values.Value;
import org.n52.iceland.ogc.ows.OWSConstants.AdditionalRequestParams;
import org.n52.iceland.ogc.ows.OwsExceptionReport;
import org.n52.iceland.service.Configurator;
import org.n52.iceland.service.ServiceConfiguration;
import org.n52.iceland.util.CollectionHelper;

import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

public abstract class AbstractStreaming extends AbstractObservationValue<Value<OmObservation>> {

    private static final long serialVersionUID = -4290319005184152231L;

    private Map<AdditionalRequestParams, Object> additionalRequestParams = Maps.newHashMap();

    private String responseFormat;
    
    private int maxNumberOfValues = Integer.MIN_VALUE;
    
    private int currentNumberOfValues = 0;

    public abstract boolean hasNextValue() throws OwsExceptionReport;

    public abstract OmObservation nextSingleObservation() throws OwsExceptionReport;
    

    /**
     * Check and modify observation for Spatial Filtering Profile and requested
     * crs
     * 
     * @param observation
     *            {@link OmObservation} to check
     * @throws OwsExceptionReport
     *             If an error occurs when modifying the {@link OmObservation}
     */
    protected abstract void checkForModifications(OmObservation observation) throws OwsExceptionReport;

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
