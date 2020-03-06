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
package org.n52.sos.convert;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.n52.sos.cache.ContentCache;
import org.n52.sos.config.annotation.Configurable;
import org.n52.sos.config.annotation.Setting;
import org.n52.sos.exception.CodedException;
import org.n52.sos.exception.ows.InvalidParameterValueException;
import org.n52.sos.ogc.om.ObservationMergeIndicator;
import org.n52.sos.ogc.om.ObservationMerger;
import org.n52.sos.ogc.om.OmObservation;
import org.n52.sos.ogc.om.StreamingObservation;
import org.n52.sos.ogc.om.StreamingValue;
import org.n52.sos.ogc.ows.OwsExceptionReport;
import org.n52.sos.ogc.sos.Sos1Constants;
import org.n52.sos.ogc.sos.Sos2Constants;
import org.n52.sos.ogc.sos.SosConstants;
import org.n52.sos.request.AbstractObservationRequest;
import org.n52.sos.request.AbstractServiceRequest;
import org.n52.sos.request.GetObservationByIdRequest;
import org.n52.sos.request.GetObservationRequest;
import org.n52.sos.response.AbstractObservationResponse;
import org.n52.sos.response.AbstractServiceResponse;
import org.n52.sos.response.GetObservationByIdResponse;
import org.n52.sos.response.GetObservationResponse;
import org.n52.sos.service.Configurator;
import org.n52.sos.service.ServiceSettings;
import org.n52.sos.util.CollectionHelper;
import org.n52.svalbard.inspire.omso.InspireOMSOConstants;
import org.n52.svalbard.inspire.omso.MultiPointObservation;
import org.n52.svalbard.inspire.omso.PointObservation;
import org.n52.svalbard.inspire.omso.PointTimeSeriesObservation;
import org.n52.svalbard.inspire.omso.ProfileObservation;
import org.n52.svalbard.inspire.omso.TrajectoryObservation;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

/**
 * Implementation if {@link RequestResponseModifier} to convert default
 * observations into INSPIRE OM Specialised Observations.
 * 
 * @author <a href="mailto:c.hollmann@52north.org">Carsten Hollmann</a>
 * @since 4.4.0
 *
 */
@Configurable
public class InspireObservationResponseConverter
        extends AbstractRequestResponseModifier<AbstractServiceRequest<?>, AbstractServiceResponse> {

    private static final Set<RequestResponseModifierKeyType> REQUEST_RESPONSE_MODIFIER_KEY_TYPES = getKeyTypes();
    
    private boolean includeResultTimeForMerging = false; 
    
    @Setting(ServiceSettings.INCLUDE_RESULT_TIME_FOR_MERGING)
    public void setIncludeResultTimeForMerging(boolean includeResultTimeForMerging) {
        this.includeResultTimeForMerging = includeResultTimeForMerging;
    }

    private static Set<RequestResponseModifierKeyType> getKeyTypes() {
        Set<String> services = Sets.newHashSet(SosConstants.SOS);
        Set<String> versions = Sets.newHashSet(Sos1Constants.SERVICEVERSION, Sos2Constants.SERVICEVERSION);
        Map<AbstractObservationRequest, AbstractObservationResponse> requestResponseMap = Maps.newHashMap();
        requestResponseMap.put(new GetObservationRequest(), new GetObservationResponse());
        requestResponseMap.put(new GetObservationByIdRequest(), new GetObservationByIdResponse());
        Set<RequestResponseModifierKeyType> keys = Sets.newHashSet();
        for (String service : services) {
            for (String version : versions) {
                for (AbstractServiceRequest<?> request : requestResponseMap.keySet()) {
                    keys.add(new RequestResponseModifierKeyType(service, version, request));
                    keys.add(new RequestResponseModifierKeyType(service, version, request,
                            requestResponseMap.get(request)));
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
        if (request instanceof AbstractObservationRequest) {
            AbstractObservationRequest req = (AbstractObservationRequest) request;
            if (req.isSetResponseFormat() && InspireOMSOConstants.NS_OMSO_30.equals(req.getResponseFormat())) {
                if (req.isSetResultModel()) {
                    checkRequestedResultType(req.getResultModel());
                }
            }
        }
        return request;
    }

    @Override
    public AbstractServiceResponse modifyResponse(AbstractServiceRequest<?> request, AbstractServiceResponse response)
            throws OwsExceptionReport {
        if (response instanceof AbstractObservationResponse) {
            AbstractObservationResponse resp = (AbstractObservationResponse) response;
            if (InspireOMSOConstants.NS_OMSO_30.equals(resp.getResponseFormat())
                    && CollectionHelper.isNotEmpty(resp.getObservationCollection())) {
                if (resp.hasStreamingData()) {
                    checkForStreamingData(request, resp);
                } else {
                    checkForNonStreamingData(request, resp);
                }
            }
        }
        return response;
    }

    /**
     * Check the {@link AbstractObservationResponse} with {@link StreamingValue}
     * 
     * @param request
     *            The request
     * @param response
     *            The response
     * @throws OwsExceptionReport
     *             If an error occurs
     */
    private void checkForStreamingData(AbstractServiceRequest<?> request, AbstractObservationResponse response)
            throws OwsExceptionReport {
        Map<String, List<OmObservation>> map = Maps.newHashMap();
        for (OmObservation omObservation : response.getObservationCollection()) {
            if (omObservation.getValue() instanceof StreamingValue<?>) {
                if (checkRequestedObservationTypeForOffering(omObservation, request)) {
                    boolean withIdentifierNameDesription = checkForObservationTypeForMerging(omObservation, request);
                    String observationType = checkForObservationTypeForStreaming(omObservation, request);
                    List<OmObservation> observations = ((StreamingValue<?>) omObservation.getValue()).getObservation(withIdentifierNameDesription);
                    if (CollectionHelper.isNotEmpty(observations)) {
                        for (OmObservation observation : observations) {
                            if (InspireOMSOConstants.OBS_TYPE_PROFILE_OBSERVATION.equals(observationType)) {
                                putOrAdd(map, InspireOMSOConstants.OBS_TYPE_PROFILE_OBSERVATION,
                                        convertToProfileObservations(observation));
                            } else if (InspireOMSOConstants.OBS_TYPE_TRAJECTORY_OBSERVATION.equals(observationType)) {
                                putOrAdd(map, InspireOMSOConstants.OBS_TYPE_TRAJECTORY_OBSERVATION,
                                        convertToTrajectoryObservations(observation));
                            } else if (InspireOMSOConstants.OBS_TYPE_MULTI_POINT_OBSERVATION.equals(observationType)) {
                                putOrAdd(map, InspireOMSOConstants.OBS_TYPE_MULTI_POINT_OBSERVATION,
                                        convertToMultiPointObservations(observation));
                            } else if (InspireOMSOConstants.OBS_TYPE_POINT_TIME_SERIES_OBSERVATION
                                    .equals(observationType)) {
                                putOrAdd(map, InspireOMSOConstants.OBS_TYPE_POINT_TIME_SERIES_OBSERVATION,
                                        convertToPointTimeSeriesObservations(observation));
                            } else if (InspireOMSOConstants.OBS_TYPE_POINT_OBSERVATION.equals(observationType)) {
                                putOrAdd(map, InspireOMSOConstants.OBS_TYPE_POINT_OBSERVATION,
                                        convertToPointObservations(observation));
                            }
                        }
                    }
                }
            } else if (omObservation.getValue() instanceof StreamingObservation) {
                // TODO
            }
        }
        response.setObservationCollection(mergeObservations(map));
    }

    /**
     * Check the {@link AbstractObservationResponse} with default values
     * 
     * @param request
     *            The request
     * @param response
     *            The response
     * @throws CodedException 
     */
    private void checkForNonStreamingData(AbstractServiceRequest<?> request, AbstractObservationResponse response) throws CodedException {
        Map<String, List<OmObservation>> map = Maps.newHashMap();
        for (OmObservation observation : response.getObservationCollection()) {
            if (checkRequestedObservationTypeForOffering(observation, request)) {
                String observationType = checkForObservationTypeForStreaming(observation, request);
                if (InspireOMSOConstants.OBS_TYPE_PROFILE_OBSERVATION.equals(observationType)) {
                    putOrAdd(map, InspireOMSOConstants.OBS_TYPE_PROFILE_OBSERVATION,
                            convertToProfileObservations(observation));
                } else if (InspireOMSOConstants.OBS_TYPE_TRAJECTORY_OBSERVATION.equals(observationType)) {
                    putOrAdd(map, InspireOMSOConstants.OBS_TYPE_TRAJECTORY_OBSERVATION,
                            convertToTrajectoryObservations(observation));
                } else if (InspireOMSOConstants.OBS_TYPE_MULTI_POINT_OBSERVATION.equals(observationType)) {
                    putOrAdd(map, InspireOMSOConstants.OBS_TYPE_MULTI_POINT_OBSERVATION,
                            convertToMultiPointObservations(observation));
                } else if (InspireOMSOConstants.OBS_TYPE_POINT_TIME_SERIES_OBSERVATION
                        .equals(observationType)) {
                    putOrAdd(map, InspireOMSOConstants.OBS_TYPE_POINT_TIME_SERIES_OBSERVATION,
                            convertToPointTimeSeriesObservations(observation));
                } else if (InspireOMSOConstants.OBS_TYPE_POINT_OBSERVATION.equals(observationType)) {
                    putOrAdd(map, InspireOMSOConstants.OBS_TYPE_POINT_OBSERVATION,
                            convertToPointObservations(observation));
                }
            }
        }
        response.setObservationCollection(mergeObservations(map));
    }

    /**
     * Merge the observations depending on their observationType
     * 
     * @param map
     *            {@link Map} with observationType key
     * @return Merged observations
     */
    private List<OmObservation> mergeObservations(Map<String, List<OmObservation>> map) {
        List<OmObservation> mergedObservations = Lists.newArrayList();
        for (String key : map.keySet()) {
            switch (key) {
            case InspireOMSOConstants.OBS_TYPE_PROFILE_OBSERVATION:
                mergedObservations.addAll(mergeProfileObservation(map.get(key)));
                break;
            case InspireOMSOConstants.OBS_TYPE_TRAJECTORY_OBSERVATION:
                mergedObservations.addAll(mergeTrajectoryObservation(map.get(key)));
                break;
            case InspireOMSOConstants.OBS_TYPE_MULTI_POINT_OBSERVATION:
                mergedObservations.addAll(mergeMultiPointObservation(map.get(key)));
                break;
            case InspireOMSOConstants.OBS_TYPE_POINT_TIME_SERIES_OBSERVATION:
                mergedObservations.addAll(mergePointTimeSeriesObservation(map.get(key)));
                break;
            default:
                mergedObservations.addAll(map.get(key));
                break;
            }
        }
        return mergedObservations;
    }

    /**
     * Merge observation of observationType
     * {@link InspireOMSOConstants#OBS_TYPE_POINT_TIME_SERIES_OBSERVATION}
     * 
     * @param observations
     *            {@link OmObservation}s to merge
     * @return Merged observations
     */
    private Collection<? extends OmObservation> mergePointTimeSeriesObservation(List<OmObservation> observations) {
        return new ObservationMerger().mergeObservations(observations,
                ObservationMergeIndicator.defaultObservationMergerIndicator().setResultTime(includeResultTimeForMerging));
    }

    /**
     ** Merge observation of observationType
     * {@link InspireOMSOConstants#OBS_TYPE_MULTI_POINT_OBSERVATION}
     * 
     * @param observations
     *            {@link OmObservation}s to merge
     * @return Merged observations
     */
    private Collection<? extends OmObservation> mergeMultiPointObservation(List<OmObservation> observations) {
        ObservationMergeIndicator observationMergeIndicator = new ObservationMergeIndicator();
        observationMergeIndicator.setObservableProperty(true).setProcedure(true).setPhenomenonTime(true);
        return new ObservationMerger().mergeObservations(observations, observationMergeIndicator);
    }

    /**
     * Merge observation of observationType
     * {@link InspireOMSOConstants#OBS_TYPE_PROFILE_OBSERVATION}
     * 
     * @param observations
     *            {@link OmObservation}s to merge
     * @return Merged observations
     */
    private Collection<? extends OmObservation> mergeProfileObservation(List<OmObservation> observations) {
        ObservationMergeIndicator observationMergeIndicator = new ObservationMergeIndicator();
        observationMergeIndicator.setObservableProperty(true).setProcedure(true).setFeatureOfInterest(true)
                .setPhenomenonTime(true).setOfferings(true);
        return new ObservationMerger().mergeObservations(observations, observationMergeIndicator);
    }

    /**
     * Merge observation of observationType
     * {@link InspireOMSOConstants#OBS_TYPE_TRAJECTORY_OBSERVATION}
     * 
     * @param observations
     *            {@link OmObservation}s to merge
     * @return Merged observations
     */
    private Collection<? extends OmObservation> mergeTrajectoryObservation(List<OmObservation> observations) {
        ObservationMergeIndicator observationMergeIndicator = new ObservationMergeIndicator();
        observationMergeIndicator.setObservableProperty(true).setProcedure(true).setFeatureOfInterest(true)
                .setOfferings(true);
        return new ObservationMerger().mergeObservations(observations, observationMergeIndicator);
    }

    /**
     * Convert observation to {@link PointObservation}
     * 
     * @param observation
     *            {@link OmObservation} to convert
     * @return Converted observation
     */
    private List<OmObservation> convertToPointObservations(OmObservation observation) {
        return Lists.<OmObservation> newArrayList(new PointObservation(observation));
    }

    /**
     * Convert observation to {@link PointTimeSeriesObservation}
     * 
     * @param observation
     *            {@link OmObservation} to convert
     * @return Converted observation
     */
    private List<OmObservation> convertToPointTimeSeriesObservations(OmObservation observation) {
        return Lists.<OmObservation> newArrayList(new PointTimeSeriesObservation(observation));
    }

    /**
     * Convert observation to {@link MultiPointObservation}
     * 
     * @param observation
     *            {@link OmObservation} to convert
     * @return Converted observation
     * @throws CodedException
     */
    private List<OmObservation> convertToMultiPointObservations(OmObservation observation) throws CodedException {
        return Lists.<OmObservation> newArrayList(new MultiPointObservation(observation));
    }

    /**
     * Convert observation to {@link ProfileObservation}
     * 
     * @param observation
     *            {@link OmObservation} to convert
     * @return Converted observation
     */
    private List<OmObservation> convertToProfileObservations(OmObservation observation) {
        return Lists.<OmObservation> newArrayList(new ProfileObservation(observation));
    }

    /**
     * Convert observation to {@link TrajectoryObservation}
     * 
     * @param observation
     *            {@link OmObservation} to convert
     * @return Converted observation
     */
    private List<OmObservation> convertToTrajectoryObservations(OmObservation observation) {
        return Lists.<OmObservation> newArrayList(new TrajectoryObservation(observation));
    }

    /**
     * Check {@link OmObservation} for requested resultType or default
     * observationType
     * 
     * @param observation
     *            {@link OmObservation} to check
     * @param request
     *            {@link GetObservationRequest} to check
     * @return Observation type for {@link OmObservation}
     */
    private String checkForObservationTypeForStreaming(OmObservation observation, AbstractServiceRequest<?> request) {
        if (request instanceof AbstractObservationRequest
                && ((AbstractObservationRequest) request).isSetResultModel()) {
            String requestedObservationType = ((AbstractObservationRequest) request).getResultModel();
            if (checkForObservationType(observation, requestedObservationType)) {
                if (InspireOMSOConstants.OBS_TYPE_POINT_TIME_SERIES_OBSERVATION.equals(requestedObservationType)) {
                    return InspireOMSOConstants.OBS_TYPE_POINT_TIME_SERIES_OBSERVATION;
                } else if (InspireOMSOConstants.OBS_TYPE_MULTI_POINT_OBSERVATION.equals(requestedObservationType)) {
                    return InspireOMSOConstants.OBS_TYPE_MULTI_POINT_OBSERVATION;
                } else if (InspireOMSOConstants.OBS_TYPE_PROFILE_OBSERVATION.equals(requestedObservationType)
                        && observation.isSetHeightDepthParameter()) {
                    return InspireOMSOConstants.OBS_TYPE_PROFILE_OBSERVATION;
                } else if (InspireOMSOConstants.OBS_TYPE_TRAJECTORY_OBSERVATION.equals(requestedObservationType)
                        && observation.isSetSpatialFilteringProfileParameter() && checkForTrajectory(observation)) {
                    return InspireOMSOConstants.OBS_TYPE_TRAJECTORY_OBSERVATION;
                }
            }
        } else {
            if (checkForObservationType(observation, InspireOMSOConstants.OBS_TYPE_TRAJECTORY_OBSERVATION)) {
                return InspireOMSOConstants.OBS_TYPE_TRAJECTORY_OBSERVATION;
            } else if (checkForObservationType(observation, InspireOMSOConstants.OBS_TYPE_PROFILE_OBSERVATION)) {
                return InspireOMSOConstants.OBS_TYPE_PROFILE_OBSERVATION;
            } else if (checkForObservationType(observation,
                    InspireOMSOConstants.OBS_TYPE_POINT_TIME_SERIES_OBSERVATION)) {
                return InspireOMSOConstants.OBS_TYPE_POINT_TIME_SERIES_OBSERVATION;
            } else if (checkForObservationType(observation, InspireOMSOConstants.OBS_TYPE_MULTI_POINT_OBSERVATION)) {
                return InspireOMSOConstants.OBS_TYPE_MULTI_POINT_OBSERVATION;
            }
        }
        // TODO default setting
        return InspireOMSOConstants.OBS_TYPE_POINT_OBSERVATION;
    }

    /**
     * Check for {@link TrajectoryObservation}
     * 
     * @param observation
     *            {@link OmObservation} to check
     * @return <code>true</code>, if observationType of observation is
     *         {@link InspireOMSOConstants#OBS_TYPE_TRAJECTORY_OBSERVATION}
     */
    private boolean checkForTrajectory(OmObservation observation) {
        ContentCache cache = Configurator.getInstance().getCache();
        for (String offering : observation.getObservationConstellation().getOfferings()) {
            if (cache.getAllObservationTypesForOffering(offering)
                    .contains(InspireOMSOConstants.OBS_TYPE_TRAJECTORY_OBSERVATION)) {
                return true;
            }
        }
        return checkForObservationType(observation, InspireOMSOConstants.OBS_TYPE_TRAJECTORY_OBSERVATION);
    }

    /**
     * Check if observationType is valid for observation
     * 
     * @param observation
     *            {@link OmObservation} to check
     * @param observationType
     *            The observationType to check
     * @return <code>true</code>, if observationType is valid for observation
     */
    private boolean checkForObservationType(OmObservation observation, String observationType) {
        ContentCache cache = Configurator.getInstance().getCache();
        for (String offering : observation.getObservationConstellation().getOfferings()) {
            if (cache.getAllObservationTypesForOffering(offering).contains(observationType)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Check if the requested observationType is valid for the offering
     * 
     * @param observation
     *            {@link OmObservation} to check offering for
     * @param request
     *            The request with requested observationType
     * @return <code>true</code>, if no observationType is requested or
     *         observationType is valid for offering of {@link OmObservation}
     */
    private boolean checkRequestedObservationTypeForOffering(OmObservation observation,
            AbstractServiceRequest<?> request) {
        if (request instanceof AbstractObservationRequest
                && ((AbstractObservationRequest) request).isSetResultModel()) {
            String observationType = ((AbstractObservationRequest) request).getResultModel();
            return checkForObservationType(observation, observationType);
        }
        return true;
    }

    private boolean checkForObservationTypeForMerging(OmObservation observation, AbstractServiceRequest<?> request) {
        String observationType = checkForObservationTypeForStreaming(observation, request);
        return InspireOMSOConstants.OBS_TYPE_POINT_TIME_SERIES_OBSERVATION.equals(observationType)
                || InspireOMSOConstants.OBS_TYPE_TRAJECTORY_OBSERVATION.equals(observationType);
    }

    /**
     * Check if the requested resultType is valid
     * 
     * @param resultType
     *            Requested resultType
     * @throws CodedException
     *             If the requested resultType is not valid
     */
    private void checkRequestedResultType(String resultType) throws CodedException {
        if (!getValidResultTypes().contains(resultType)) {
            throw new InvalidParameterValueException().at("resultType").withMessage(
                    "The requested resultType '%s' is not valid for the responseFormat '%s'", resultType,
                    InspireOMSOConstants.NS_OMSO_30);
        }
    }

    /**
     * @return The valid resultTypes
     */
    private Set<String> getValidResultTypes() {
        return Sets.newHashSet(InspireOMSOConstants.OBS_TYPE_POINT_OBSERVATION,
                InspireOMSOConstants.OBS_TYPE_POINT_TIME_SERIES_OBSERVATION,
                InspireOMSOConstants.OBS_TYPE_MULTI_POINT_OBSERVATION,
                InspireOMSOConstants.OBS_TYPE_PROFILE_OBSERVATION,
                InspireOMSOConstants.OBS_TYPE_TRAJECTORY_OBSERVATION);
    }

    /**
     * Put/add {@link OmObservation}s to the map
     * 
     * @param map
     *            The map to put/add
     * @param type
     *            The key to put/add values for
     * @param observations
     *            The values to add
     */
    private void putOrAdd(Map<String, List<OmObservation>> map, String type, List<OmObservation> observations) {
        if (CollectionHelper.isNotEmpty(observations)) {
            if (map.containsKey(type)) {
                map.get(type).addAll(observations);
            } else {
                map.put(type, observations);
            }
        }
    }

    @Override
    public RequestResponseModifierFacilitator getFacilitator() {
        return super.getFacilitator().setMerger(true);
    }

}
