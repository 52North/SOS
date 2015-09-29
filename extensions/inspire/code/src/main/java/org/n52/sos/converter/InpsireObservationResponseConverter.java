package org.n52.sos.converter;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.hibernate.internal.util.collections.CollectionHelper;
import org.n52.sos.convert.RequestResponseModifier;
import org.n52.sos.convert.RequestResponseModifierFacilitator;
import org.n52.sos.convert.RequestResponseModifierKeyType;
import org.n52.sos.inspire.omso.InspireOMSOConstants;
import org.n52.sos.inspire.omso.MultiPointObservation;
import org.n52.sos.inspire.omso.PointObservation;
import org.n52.sos.inspire.omso.PointTimeSeriesObservation;
import org.n52.sos.inspire.omso.ProfileObservation;
import org.n52.sos.inspire.omso.TrajectoryObservation;
import org.n52.sos.ogc.om.OmObservation;
import org.n52.sos.ogc.om.PointValuePair;
import org.n52.sos.ogc.om.SingleObservationValue;
import org.n52.sos.ogc.om.StreamingValue;
import org.n52.sos.ogc.om.features.samplingFeatures.SamplingFeature;
import org.n52.sos.ogc.om.values.CvDiscretePointCoverage;
import org.n52.sos.ogc.ows.OwsExceptionReport;
import org.n52.sos.ogc.sos.Sos1Constants;
import org.n52.sos.ogc.sos.Sos2Constants;
import org.n52.sos.ogc.sos.SosConstants;
import org.n52.sos.request.AbstractServiceRequest;
import org.n52.sos.request.GetObservationRequest;
import org.n52.sos.response.GetObservationResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.Point;

public class InpsireObservationResponseConverter implements RequestResponseModifier<GetObservationRequest, GetObservationResponse> {

    private static final Logger LOGGER = LoggerFactory.getLogger(InpsireObservationResponseConverter.class);

    private static final Set<RequestResponseModifierKeyType> REQUEST_RESPONSE_MODIFIER_KEY_TYPES = getKeyTypes();

    /**
     * Get the keys
     * 
     * @return Set of keys
     */
    private static Set<RequestResponseModifierKeyType> getKeyTypes() {
        Set<String> services = Sets.newHashSet(SosConstants.SOS);
        Set<String> versions = Sets.newHashSet(Sos1Constants.SERVICEVERSION, Sos2Constants.SERVICEVERSION);
        Map<GetObservationRequest, GetObservationResponse> requestResponseMap = Maps.newHashMap();

        requestResponseMap.put(new GetObservationRequest(), new GetObservationResponse());
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
    public GetObservationRequest modifyRequest(GetObservationRequest request) throws OwsExceptionReport {
        // TODO check for response format and change if required
        return request;
    }

    @Override
    public GetObservationResponse modifyResponse(GetObservationRequest request, GetObservationResponse response)
            throws OwsExceptionReport {
        // TODO How to identify INSPIRE-Obs and ObsType
//        if (InspireOMSOConstants.NS_OMSO_30.startsWith(request.getResponseFormat())) {
        if (CollectionHelper.isNotEmpty(response.getObservationCollection())) {
            if (InspireOMSOConstants.NS_OMSO_30.equals(request.getResponseFormat())) {
                String observationType = "";
                if (InspireOMSOConstants.OBS_TYPE_POINT_OBSERVATION.equals(observationType)) {
                    return convertToPointObservations(response);
                } else if (InspireOMSOConstants.OBS_TYPE_POINT_TIME_SERIES_OBSERVATION.equals(observationType)) {
                    return convertToPointTimeSeriesObservations(response);
                } else if (InspireOMSOConstants.OBS_TYPE_MULTI_POINT_OBSERVATION.equals(observationType)) {
                    return convertToMultipointObservations(response);
                } else if (InspireOMSOConstants.OBS_TYPE_PROFILE_OBSERVATION.equals(observationType)) {
                    return convertToProfileObservations(response);
                } else if (InspireOMSOConstants.OBS_TYPE_TRAJECTORY_OBSERVATION.equals(observationType)) {
                    return convertToTrajectoryObservations(response);
                } 
            }
        }
        return response;
    }

    private GetObservationResponse convertToPointObservations(GetObservationResponse response) {
        List<OmObservation> observations = Lists.newArrayListWithCapacity(response.getObservationCollection().size());
        for (OmObservation omObservation : response.getObservationCollection()) {
            PointObservation pointObservation = new PointObservation(omObservation);
            if (response.hasStreamingData()) {
                if (pointObservation.getValue() instanceof StreamingValue<?>) {
                    StreamingValue<?> sv = (StreamingValue<?>)pointObservation.getValue();
                    sv.setObservationTemplate(new PointObservation(sv.getObservationTemplate()));
                }
            } else {
                Geometry geometry = null;
                if (pointObservation.isSetSpatialFilteringProfileParameter()) {
                    geometry = pointObservation.getSpatialFilteringProfileParameter().getValue().getValue();
                } else if (checkForFeatureGeometry(pointObservation)) {
                    geometry = getGeometryFromFeature(pointObservation);
                }
                Point p = null;
                if (geometry != null) {
                    if (geometry instanceof Point) {
                        p = (Point)geometry;
                    } else {
                        p = geometry.getCentroid();
                    }
                }
                CvDiscretePointCoverage cvDiscretePointCoverage = new CvDiscretePointCoverage();
                cvDiscretePointCoverage.setValue(new PointValuePair(p, pointObservation.getValue().getValue()));
                SingleObservationValue<?> singleObservationValue = new SingleObservationValue<>(cvDiscretePointCoverage);
                pointObservation.setValue(singleObservationValue);
            }
            observations.add(pointObservation);
        }
        response.setObservationCollection(observations);
        return response;
    }

    private GetObservationResponse convertToPointTimeSeriesObservations(GetObservationResponse response) {
        List<OmObservation> observations = Lists.newArrayList();
        for (OmObservation omObservation : response.getObservationCollection()) {
            PointTimeSeriesObservation pointTimeSeriesObservation = new PointTimeSeriesObservation(omObservation);
            if (response.hasStreamingData()) {
                if (pointTimeSeriesObservation.getValue() instanceof StreamingValue<?>) {
                    StreamingValue<?> sv = (StreamingValue<?>)pointTimeSeriesObservation.getValue();
                    sv.setObservationTemplate(new PointTimeSeriesObservation(sv.getObservationTemplate()));
                }
            } else {
             // TODO Merge for same constellation, FOI = Point, convert value to TVPValue???
            }
            observations.add(pointTimeSeriesObservation);
        }
        response.setObservationCollection(observations);
        return response;
    }

    private GetObservationResponse convertToMultipointObservations(GetObservationResponse response) {
        List<OmObservation> observations = Lists.newArrayList();
        Map<String, MultiPointObservation> mergedObservations = Maps.newHashMap();
        for (OmObservation omObservation : response.getObservationCollection()) {
            String observedProperty = omObservation.getObservationConstellation().getObservableProperty().getIdentifier();
            if (mergedObservations.containsKey(observedProperty)) {
                MultiPointObservation multiPointObservation = mergedObservations.get(observedProperty);
                // TODO Merge StreamingValue to current
            } else {
                MultiPointObservation multiPointObservation = new MultiPointObservation(omObservation);
                if (response.hasStreamingData()) {
                    if (multiPointObservation.getValue() instanceof StreamingValue<?>) {
                        StreamingValue<?> sv = (StreamingValue<?>)multiPointObservation.getValue();
                        sv.setObservationTemplate(new MultiPointObservation(sv.getObservationTemplate()));
                        // TODO Merge StreamingValue for same observedProperty(/Procedure)
                    }
                } else {
                 // TODO Merge same observedProperty(/Procedure) at same time, FOI = Surface, convert value to MultiPointCoverage
                }
                mergedObservations.put(observedProperty, multiPointObservation);
                observations.add(multiPointObservation);
            }
        }
        response.setObservationCollection(observations);
        return response;
    }

    private GetObservationResponse convertToProfileObservations(GetObservationResponse response) {
        List<OmObservation> observations = Lists.newArrayList();
        for (OmObservation omObservation : response.getObservationCollection()) {
            ProfileObservation profileObservation = new ProfileObservation(omObservation);
            if (response.hasStreamingData()) {
                if (profileObservation.getValue() instanceof StreamingValue<?>) {
                    StreamingValue<?> sv = (StreamingValue<?>)profileObservation.getValue();
                    sv.setObservationTemplate(new ProfileObservation(sv.getObservationTemplate()));
                }
            } else {
             // TODO Merge same constellation different depth same time, FOI = Curve, convert value to Rectified-/ReferencableGridCoverage
            }
            observations.add(profileObservation);
        }
        response.setObservationCollection(observations);
        return response;
    }

    private GetObservationResponse convertToTrajectoryObservations(GetObservationResponse response) {
        List<OmObservation> observations = Lists.newArrayList();
        for (OmObservation omObservation : response.getObservationCollection()) {
            TrajectoryObservation trajectoryObservation = new TrajectoryObservation(omObservation);
            if (response.hasStreamingData()) {
                if (trajectoryObservation.getValue() instanceof StreamingValue<?>) {
                    StreamingValue<?> sv = (StreamingValue<?>)trajectoryObservation.getValue();
                    sv.setObservationTemplate(new TrajectoryObservation(sv.getObservationTemplate()));
                }
            } else {
             // TODO Merge same constellation different samplingGeometry, FOI = Curve, convert value to ???
            }
            observations.add(trajectoryObservation);
        }
        response.setObservationCollection(observations);
        return response;
    }

    private boolean checkForFeatureGeometry(OmObservation observation) {
        if (observation.getObservationConstellation().getFeatureOfInterest() instanceof SamplingFeature) {
            return ((SamplingFeature)observation.getObservationConstellation().getFeatureOfInterest()).isSetGeometry();
        }
        return false;
    }

    private Geometry getGeometryFromFeature(OmObservation observation) {
        return ((SamplingFeature)observation.getObservationConstellation().getFeatureOfInterest()).getGeometry();
    }

    @Override
    public RequestResponseModifierFacilitator getFacilitator() {
        return new RequestResponseModifierFacilitator().setMerger(true);
    }

}
