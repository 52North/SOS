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
import java.util.Map;
import java.util.Set;

import org.apache.xmlbeans.XmlObject;
import org.n52.sos.convert.RequestResponseModifier;
import org.n52.sos.convert.RequestResponseModifierFacilitator;
import org.n52.sos.convert.RequestResponseModifierKeyType;
import org.n52.sos.encode.ObservationEncoder;
import org.n52.sos.ogc.om.OmObservation;
import org.n52.sos.ogc.ows.OwsExceptionReport;
import org.n52.sos.ogc.sos.Sos1Constants;
import org.n52.sos.ogc.sos.Sos2Constants;
import org.n52.sos.ogc.sos.SosConstants;
import org.n52.sos.request.AbstractServiceRequest;
import org.n52.sos.request.GetObservationRequest;
import org.n52.sos.request.InsertObservationRequest;
import org.n52.sos.response.AbstractServiceResponse;
import org.n52.sos.response.GetObservationResponse;
import org.n52.sos.response.InsertObservationResponse;
import org.n52.sos.service.Configurator;
import org.n52.sos.service.profile.Profile;
import org.n52.sos.util.CodingHelper;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

public class SplitMergeObservations implements
        RequestResponseModifier<AbstractServiceRequest<?>, AbstractServiceResponse> {

    private static final Set<RequestResponseModifierKeyType> REQUEST_RESPONSE_MODIFIER_KEY_TYPES = getKeyTypes();

    /**
     * Get the keys
     * 
     * @return Set of keys
     */
    private static Set<RequestResponseModifierKeyType> getKeyTypes() {
        Set<String> services = Sets.newHashSet(SosConstants.SOS);
        Set<String> versions = Sets.newHashSet(Sos1Constants.SERVICEVERSION, Sos2Constants.SERVICEVERSION);
        Map<AbstractServiceRequest<?>, AbstractServiceResponse> requestResponseMap = Maps.newHashMap();

        requestResponseMap.put(new GetObservationRequest(), new GetObservationResponse());
        requestResponseMap.put(new InsertObservationRequest(), new InsertObservationResponse());
        Set<RequestResponseModifierKeyType> keys = Sets.newHashSet();
        for (String service : services) {
            for (String version : versions) {
                for (AbstractServiceRequest<?> request : requestResponseMap.keySet()) {
                    keys.add(new RequestResponseModifierKeyType(service, version, request));
                    keys.add(new RequestResponseModifierKeyType(service, version, request, requestResponseMap
                            .get(request)));
                }
            }
        }
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
        if (request instanceof GetObservationRequest && response instanceof GetObservationResponse) {
            return mergeObservations((GetObservationRequest)request, (GetObservationResponse) response); 
        }
        if (response instanceof GetObservationResponse) {
            return mergeObservations((GetObservationResponse) response);
        }
        return response;
    }
    
    private AbstractServiceResponse mergeObservations(GetObservationRequest request, GetObservationResponse response) throws OwsExceptionReport {
        boolean checkForMergeObservationsInResponse = checkForMergeObservationsInResponse(request);
        request.setMergeObservationValues(checkForMergeObservationsInResponse);
        boolean checkEncoderForMergeObservations = checkEncoderForMergeObservations(response);
        if (checkForMergeObservationsInResponse || checkEncoderForMergeObservations) {
            if (!response.hasStreamingData()) {
                mergeObservationsWithSameConstellation(response);
            }
            response.setMergeObservations(true);
        }        
        return response;
    }
    
    private void mergeObservationsWithSameConstellation(GetObservationResponse response) {
        // TODO merge all observations with the same observationContellation
        // FIXME Failed to set the observation type to sweArrayObservation for
        // the merged Observations
        // (proc, obsProp, foi)
        if (response.getObservationCollection() != null) {
            final List<OmObservation> mergedObservations = new LinkedList<OmObservation>();
            int obsIdCounter = 1;
            for (final OmObservation sosObservation : response.getObservationCollection()) {
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
            response.setObservationCollection(mergedObservations);
        }
    }

    private boolean checkEncoderForMergeObservations(GetObservationResponse response) throws OwsExceptionReport {
        if (response.isSetResponseFormat()) {
            ObservationEncoder<XmlObject, OmObservation> encoder =
                    (ObservationEncoder<XmlObject, OmObservation>) CodingHelper.getEncoder(
                            response.getResponseFormat(), new OmObservation());
            if (encoder.shouldObservationsWithSameXBeMerged()) {
                return true;
            }
        }
        return false;
    }

    private AbstractServiceResponse mergeObservations(GetObservationResponse response) throws OwsExceptionReport {
        boolean checkEncoderForMergeObservations = checkEncoderForMergeObservations(response);
        if (checkEncoderForMergeObservations && !response.hasStreamingData()) {
            if (!response.hasStreamingData()) {
                mergeObservationsWithSameConstellation(response);
            }
            response.setMergeObservations(checkEncoderForMergeObservations);
        }
        return response;
    }
    
    private boolean checkForMergeObservationsInResponse(GetObservationRequest sosRequest) {
        if (getActiveProfile().isMergeValues() || isSetExtensionMergeObservationsToSweDataArray(sosRequest)) {
            return true;
        }
        return false;
    }

    private boolean isSetExtensionMergeObservationsToSweDataArray(final GetObservationRequest sosRequest) {
        return sosRequest.isSetExtensions()
                && sosRequest.getExtensions().isBooleanExtensionSet(
                        Sos2Constants.Extensions.MergeObservationsIntoDataArray.name());
    }
    
    protected Profile getActiveProfile() {
        return Configurator.getInstance().getProfileHandler().getActiveProfile();
    }

    @Override
    public RequestResponseModifierFacilitator getFacilitator() {
        // TODO Auto-generated method stub
        return new RequestResponseModifierFacilitator().setMerger(true).setSplitter(true);
    }

}
