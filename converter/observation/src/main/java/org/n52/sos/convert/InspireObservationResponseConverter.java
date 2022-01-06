/*
 * Copyright (C) 2012-2022 52°North Spatial Information Research GmbH
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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.NoSuchElementException;
import java.util.Set;

import javax.inject.Inject;

import org.n52.faroe.annotation.Configurable;
import org.n52.faroe.annotation.Setting;
import org.n52.iceland.cache.ContentCacheController;
import org.n52.iceland.convert.RequestResponseModifier;
import org.n52.iceland.convert.RequestResponseModifierFacilitator;
import org.n52.iceland.convert.RequestResponseModifierKey;
import org.n52.shetland.inspire.omso.InspireOMSOConstants;
import org.n52.shetland.inspire.omso.MultiPointObservation;
import org.n52.shetland.inspire.omso.PointObservation;
import org.n52.shetland.inspire.omso.PointTimeSeriesObservation;
import org.n52.shetland.inspire.omso.ProfileObservation;
import org.n52.shetland.inspire.omso.TrajectoryObservation;
import org.n52.shetland.ogc.om.ObservationMergeIndicator;
import org.n52.shetland.ogc.om.ObservationStream;
import org.n52.shetland.ogc.om.OmObservation;
import org.n52.shetland.ogc.om.StreamingValue;
import org.n52.shetland.ogc.ows.exception.CodedException;
import org.n52.shetland.ogc.ows.exception.InvalidParameterValueException;
import org.n52.shetland.ogc.ows.exception.OwsExceptionReport;
import org.n52.shetland.ogc.ows.service.OwsServiceRequest;
import org.n52.shetland.ogc.ows.service.OwsServiceResponse;
import org.n52.shetland.ogc.sos.Sos1Constants;
import org.n52.shetland.ogc.sos.Sos2Constants;
import org.n52.shetland.ogc.sos.SosConstants;
import org.n52.shetland.ogc.sos.request.AbstractObservationRequest;
import org.n52.shetland.ogc.sos.request.GetObservationByIdRequest;
import org.n52.shetland.ogc.sos.request.GetObservationRequest;
import org.n52.shetland.ogc.sos.response.AbstractObservationResponse;
import org.n52.shetland.ogc.sos.response.GetObservationByIdResponse;
import org.n52.shetland.ogc.sos.response.GetObservationResponse;
import org.n52.shetland.util.CollectionHelper;
import org.n52.sos.cache.SosContentCache;
import org.n52.sos.service.SosSettings;

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
public class InspireObservationResponseConverter extends AbstractRequestResponseModifier {

    private static final Set<RequestResponseModifierKey> REQUEST_RESPONSE_MODIFIER_KEY_TYPES = getKeyTypes();

    private boolean includeResultTimeForMerging;

    private ContentCacheController contentCacheController;

    @Setting(SosSettings.INCLUDE_RESULT_TIME_FOR_MERGING)
    public void setIncludeResultTimeForMerging(boolean includeResultTimeForMerging) {
        this.includeResultTimeForMerging = includeResultTimeForMerging;
    }

    @Inject
    public void setContentCacheController(ContentCacheController ctrl) {
        this.contentCacheController = ctrl;
    }

    public ContentCacheController getContentCacheController() {
        return contentCacheController;
    }

    private static Set<RequestResponseModifierKey> getKeyTypes() {
        Set<String> services = Sets.newHashSet(SosConstants.SOS);
        Set<String> versions = Sets.newHashSet(Sos1Constants.SERVICEVERSION, Sos2Constants.SERVICEVERSION);
        Map<AbstractObservationRequest, AbstractObservationResponse> requestResponseMap = Maps.newHashMap();
        requestResponseMap.put(new GetObservationRequest(), new GetObservationResponse());
        requestResponseMap.put(new GetObservationByIdRequest(), new GetObservationByIdResponse());
        Set<RequestResponseModifierKey> keys = Sets.newHashSet();
        for (String service : services) {
            for (String version : versions) {
                for (Entry<AbstractObservationRequest, AbstractObservationResponse> entry : requestResponseMap
                        .entrySet()) {
                    keys.add(new RequestResponseModifierKey(service, version, entry.getKey()));
                    keys.add(new RequestResponseModifierKey(service, version, entry.getKey(),
                            requestResponseMap.get(entry.getKey())));
                }
            }
        }
        return keys;
    }

    @Override
    public Set<RequestResponseModifierKey> getKeys() {
        return Collections.unmodifiableSet(REQUEST_RESPONSE_MODIFIER_KEY_TYPES);
    }

    @Override
    public OwsServiceRequest modifyRequest(OwsServiceRequest request) throws OwsExceptionReport {
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
    public OwsServiceResponse modifyResponse(OwsServiceRequest request, OwsServiceResponse response)
            throws OwsExceptionReport {
        if (response instanceof AbstractObservationResponse) {
            AbstractObservationResponse resp = (AbstractObservationResponse) response;
            if (InspireOMSOConstants.NS_OMSO_30.equals(resp.getResponseFormat())
                    && resp.getObservationCollection().hasNext()) {
                // if (resp.hasStreamingData()) {
                checkData(request, resp);
                // } else {
                // checkForNonStreamingData(request, resp);
                // }
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
    private void checkData(OwsServiceRequest request, AbstractObservationResponse response) throws OwsExceptionReport {
        Map<String, List<OmObservation>> map = Maps.newHashMap();
        while (response.getObservationCollection().hasNext()) {
            OmObservation omObservation = response.getObservationCollection().next();
            if (omObservation.getValue() instanceof StreamingValue<?>) {
                if (checkRequestedObservationTypeForOffering(omObservation, request)) {
                    String observationType = checkForObservationTypeForStreaming(omObservation, request);
                    while (((StreamingValue<?>) omObservation.getValue()).hasNext()) {
                        OmObservation observation = ((StreamingValue<?>) omObservation.getValue()).next();
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
        }
        response.setObservationCollection(mergeObservations(map));
    }

    /**
     * Merge the observations depending on their observationType
     *
     * @param map
     *            {@link Map} with observationType key
     * @return Merged observations
     * @throws OwsExceptionReport
     *             If an error occurs
     */
    private ObservationStream mergeObservations(Map<String, List<OmObservation>> map) throws OwsExceptionReport {
        List<OmObservation> mergedObservations = Lists.newArrayList();
        for (Entry<String, List<OmObservation>> entry : map.entrySet()) {
            switch (entry.getKey()) {
                case InspireOMSOConstants.OBS_TYPE_PROFILE_OBSERVATION:
                    mergedObservations.addAll(mergeProfileObservation(ObservationStream.of(map.get(entry.getKey()))));
                    break;
                case InspireOMSOConstants.OBS_TYPE_TRAJECTORY_OBSERVATION:
                    mergedObservations.addAll(mergeTrajectoryObservation(
                            ObservationStream.of(map.get(entry.getKey()))));
                    break;
                case InspireOMSOConstants.OBS_TYPE_MULTI_POINT_OBSERVATION:
                    mergedObservations.addAll(mergeMultiPointObservation(
                            ObservationStream.of(map.get(entry.getKey()))));
                    break;
                case InspireOMSOConstants.OBS_TYPE_POINT_TIME_SERIES_OBSERVATION:
                    mergedObservations
                            .addAll(mergePointTimeSeriesObservation(ObservationStream.of(map.get(entry.getKey()))));
                    break;
                default:
                    mergedObservations.addAll(map.get(entry.getKey()));
                    break;
            }
        }
        return ObservationStream.of(mergedObservations);
    }

    /**
     * Merge observation of observationType
     * {@link InspireOMSOConstants#OBS_TYPE_POINT_TIME_SERIES_OBSERVATION}
     *
     * @param observations
     *            {@link OmObservation}s to merge
     * @return Merged observations
     * @throws OwsExceptionReport
     *             If an error occurs
     */
    private Collection<? extends OmObservation> mergePointTimeSeriesObservation(ObservationStream observations)
            throws OwsExceptionReport {
        return toList(observations.merge(
                ObservationMergeIndicator.sameObservationConstellation().setResultTime(includeResultTimeForMerging)));
    }

    /**
     ** Merge observation of observationType
     * {@link InspireOMSOConstants#OBS_TYPE_MULTI_POINT_OBSERVATION}
     *
     * @param observations
     *            {@link OmObservation}s to merge
     * @return Merged observations
     * @throws OwsExceptionReport
     *             If an error occurs
     */
    private Collection<? extends OmObservation> mergeMultiPointObservation(ObservationStream observations)
            throws OwsExceptionReport {
        ObservationMergeIndicator observationMergeIndicator = new ObservationMergeIndicator();
        observationMergeIndicator.setObservableProperty(true).setProcedure(true).setPhenomenonTime(true);
        return toList(observations.merge(observationMergeIndicator));
    }

    /**
     * Merge observation of observationType
     * {@link InspireOMSOConstants#OBS_TYPE_PROFILE_OBSERVATION}
     *
     * @param observations
     *            {@link OmObservation}s to merge
     * @return Merged observations
     * @throws OwsExceptionReport
     *             If an error occurs
     */
    private Collection<? extends OmObservation> mergeProfileObservation(ObservationStream observations)
            throws OwsExceptionReport {
        ObservationMergeIndicator observationMergeIndicator = new ObservationMergeIndicator();
        observationMergeIndicator.setObservableProperty(true).setProcedure(true).setFeatureOfInterest(true)
                .setPhenomenonTime(true).setOfferings(true);
        return toList(observations.merge(observationMergeIndicator));
    }

    /**
     * Merge observation of observationType
     * {@link InspireOMSOConstants#OBS_TYPE_TRAJECTORY_OBSERVATION}
     *
     * @param observations
     *            {@link OmObservation}s to merge
     * @return Merged observations
     * @throws OwsExceptionReport
     *             If an error occurs
     */
    private Collection<? extends OmObservation> mergeTrajectoryObservation(ObservationStream observations)
            throws OwsExceptionReport {
        ObservationMergeIndicator observationMergeIndicator = new ObservationMergeIndicator();
        observationMergeIndicator.setObservableProperty(true).setProcedure(true).setFeatureOfInterest(true)
                .setOfferings(true);
        return toList(observations.merge(observationMergeIndicator));
    }

    private Collection<? extends OmObservation> toList(ObservationStream stream)
            throws NoSuchElementException, OwsExceptionReport {
        List<OmObservation> observations = new ArrayList<>();
        while (stream.hasNext()) {
            observations.add(stream.next());
        }
        return observations;
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
     *             If an error occurs
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
    private String checkForObservationTypeForStreaming(OmObservation observation, OwsServiceRequest request) {
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
        for (String offering : observation.getObservationConstellation().getOfferings()) {
            if (((SosContentCache) getContentCacheController().getCache()).getAllObservationTypesForOffering(offering)
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
        for (String offering : observation.getObservationConstellation().getOfferings()) {
            if (((SosContentCache) getContentCacheController().getCache()).getAllObservationTypesForOffering(offering)
                    .contains(observationType)) {
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
    private boolean checkRequestedObservationTypeForOffering(OmObservation observation, OwsServiceRequest request) {
        if (request instanceof AbstractObservationRequest
                && ((AbstractObservationRequest) request).isSetResultModel()) {
            String observationType = ((AbstractObservationRequest) request).getResultModel();
            return checkForObservationType(observation, observationType);
        }
        return true;
    }

    private boolean checkForObservationTypeForMerging(OmObservation observation, OwsServiceRequest request) {
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
