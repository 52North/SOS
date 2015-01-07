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
package org.n52.sos.converter;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.n52.sos.aqd.AqdConstants;
import org.n52.sos.convert.RequestResponseModifier;
import org.n52.sos.convert.RequestResponseModifierFacilitator;
import org.n52.sos.convert.RequestResponseModifierKeyType;
import org.n52.sos.ogc.om.OmObservation;
import org.n52.sos.ogc.ows.OwsExceptionReport;
import org.n52.sos.ogc.swe.SweDataArray;
import org.n52.sos.request.AbstractServiceRequest;
import org.n52.sos.request.GetObservationRequest;
import org.n52.sos.request.InsertObservationRequest;
import org.n52.sos.response.AbstractServiceResponse;
import org.n52.sos.response.GetObservationResponse;

import com.google.common.collect.Sets;

public class AqdSplitMergeObservations implements
        RequestResponseModifier<AbstractServiceRequest<?>, AbstractServiceResponse> {

    private static final Set<RequestResponseModifierKeyType> REQUEST_RESPONSE_MODIFIER_KEY_TYPES = getKeyTypes();

    /**
     * Get the keys
     * 
     * @return Set of keys
     */
    private static Set<RequestResponseModifierKeyType> getKeyTypes() {
        Set<RequestResponseModifierKeyType> keys = Sets.newHashSet();
        keys.add(new RequestResponseModifierKeyType(AqdConstants.AQD, AqdConstants.VERSION,
                new GetObservationRequest()));
        keys.add(new RequestResponseModifierKeyType(AqdConstants.AQD, AqdConstants.VERSION,
                new GetObservationRequest(), new GetObservationResponse()));
        return keys;
    }

    @Override
    public Set<RequestResponseModifierKeyType> getRequestResponseModifierKeyTypes() {
        return Collections.unmodifiableSet(REQUEST_RESPONSE_MODIFIER_KEY_TYPES);
    }

    @Override
    public AbstractServiceRequest<?> modifyRequest(AbstractServiceRequest<?> request) throws OwsExceptionReport {
        if (request instanceof InsertObservationRequest) {
            // TODO
        }
        return request;
    }

    @Override
    public AbstractServiceResponse modifyResponse(AbstractServiceRequest<?> request, AbstractServiceResponse response)
            throws OwsExceptionReport {
        if (response instanceof GetObservationResponse) {
            return mergeObservations((GetObservationResponse) response);
        }
        return response;
    }

    private AbstractServiceResponse mergeObservations(GetObservationResponse response) throws OwsExceptionReport {
        response.setMergeObservations(true);
        if (!response.hasStreamingData()) {
            response.setObservationCollection(mergeObservations(response.getObservationCollection()));
        }
        return response;
    }

    private List<OmObservation> mergeObservations(List<OmObservation> observationCollection) {
        if (observationCollection != null) {
            final List<OmObservation> mergedObservations = new LinkedList<OmObservation>();
            int obsIdCounter = 1;
            for (final OmObservation sosObservation : observationCollection) {
                if (mergedObservations.isEmpty()) {
                    sosObservation.setObservationID(Integer.toString(obsIdCounter++));
                    mergedObservations.add(sosObservation);
                } else {
                    boolean combined = false;
                    for (final OmObservation combinedSosObs : mergedObservations) {
                        if (combinedSosObs.checkForMerge(sosObservation)) {
                            combinedSosObs.setResultTime(null);
                            mergeObservationValues(combinedSosObs, sosObservation);
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
        return observationCollection;
    }

    private void mergeObservationValues(OmObservation combinedSosObs, OmObservation sosObservation) {
        mergeValues(combinedSosObs, sosObservation);
        mergeResultTimes(combinedSosObs, sosObservation);
    }

    private void mergeValues(OmObservation combinedSosObs, OmObservation sosObservation) {
        SweDataArray combinedValue = (SweDataArray) combinedSosObs.getValue().getValue().getValue();
        SweDataArray value = (SweDataArray) sosObservation.getValue().getValue().getValue();
        if (value.isSetValues()) {
            combinedValue.addAll(value.getValues());
        }
    }

    /**
     * Merge result time with passed observation result time
     * 
     * @param sosObservation
     *            Observation to merge
     * @param sosObservation2
     */
    private void mergeResultTimes(final OmObservation combinedSosObs, OmObservation sosObservation) {
        if (combinedSosObs.isSetResultTime() && sosObservation.isSetResultTime()) {
            if (combinedSosObs.getResultTime().getValue().isBefore(sosObservation.getResultTime().getValue())) {
                combinedSosObs.setResultTime(sosObservation.getResultTime());
            }
        } else if (!combinedSosObs.isSetResultTime() && sosObservation.isSetResultTime()) {
            combinedSosObs.setResultTime(sosObservation.getResultTime());
        }
    }
    
    @Override
    public RequestResponseModifierFacilitator getFacilitator() {
        return new RequestResponseModifierFacilitator().setMerger(true).setSplitter(true);
    }

}
