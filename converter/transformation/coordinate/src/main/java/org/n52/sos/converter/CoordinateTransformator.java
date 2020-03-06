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
package org.n52.sos.converter;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.n52.sos.convert.AbstractRequestResponseModifier;
import org.n52.sos.convert.RequestResponseModifierKeyType;
import org.n52.sos.exception.ows.InvalidParameterValueException;
import org.n52.sos.exception.ows.MissingParameterValueException;
import org.n52.sos.exception.ows.NoApplicableCodeException;
import org.n52.sos.ogc.filter.SpatialFilter;
import org.n52.sos.ogc.gml.AbstractFeature;
import org.n52.sos.ogc.om.AbstractStreaming;
import org.n52.sos.ogc.om.MultiObservationValues;
import org.n52.sos.ogc.om.NamedValue;
import org.n52.sos.ogc.om.OmObservation;
import org.n52.sos.ogc.om.PointValuePair;
import org.n52.sos.ogc.om.SingleObservationValue;
import org.n52.sos.ogc.om.TimeLocationValueTriple;
import org.n52.sos.ogc.om.features.FeatureCollection;
import org.n52.sos.ogc.om.features.samplingFeatures.AbstractSamplingFeature;
import org.n52.sos.ogc.om.features.samplingFeatures.SamplingFeature;
import org.n52.sos.ogc.om.values.CvDiscretePointCoverage;
import org.n52.sos.ogc.om.values.MultiPointCoverage;
import org.n52.sos.ogc.om.values.TLVTValue;
import org.n52.sos.ogc.ows.OWSConstants;
import org.n52.sos.ogc.ows.OwsExceptionReport;
import org.n52.sos.ogc.sensorML.AbstractComponent;
import org.n52.sos.ogc.sensorML.AbstractProcess;
import org.n52.sos.ogc.sensorML.AbstractSensorML;
import org.n52.sos.ogc.sensorML.SensorML;
import org.n52.sos.ogc.sensorML.SensorMLConstants;
import org.n52.sos.ogc.sensorML.System;
import org.n52.sos.ogc.sensorML.elements.SmlCapabilities;
import org.n52.sos.ogc.sensorML.elements.SmlComponent;
import org.n52.sos.ogc.sensorML.elements.SmlLocation;
import org.n52.sos.ogc.sensorML.elements.SmlPosition;
import org.n52.sos.ogc.sensorML.v20.AbstractPhysicalProcess;
import org.n52.sos.ogc.sos.Sos1Constants;
import org.n52.sos.ogc.sos.Sos2Constants;
import org.n52.sos.ogc.sos.SosConstants;
import org.n52.sos.ogc.sos.SosEnvelope;
import org.n52.sos.ogc.sos.SosObservationOffering;
import org.n52.sos.ogc.sos.SosProcedureDescription;
import org.n52.sos.ogc.swe.CoordinateHelper;
import org.n52.sos.ogc.swe.SweConstants;
import org.n52.sos.ogc.swe.SweConstants.AltitudeSweCoordinateName;
import org.n52.sos.ogc.swe.SweConstants.EastingSweCoordinateName;
import org.n52.sos.ogc.swe.SweConstants.NorthingSweCoordinateName;
import org.n52.sos.ogc.swe.SweConstants.SweCoordinateName;
import org.n52.sos.ogc.swe.SweCoordinate;
import org.n52.sos.ogc.swe.SweEnvelope;
import org.n52.sos.ogc.swe.SweField;
import org.n52.sos.ogc.swe.SweVector;
import org.n52.sos.ogc.swe.simpleType.SweCount;
import org.n52.sos.ogc.swe.simpleType.SweQuantity;
import org.n52.sos.ogc.swe.simpleType.SweText;
import org.n52.sos.request.AbstractServiceRequest;
import org.n52.sos.request.DescribeSensorRequest;
import org.n52.sos.request.GetCapabilitiesRequest;
import org.n52.sos.request.GetFeatureOfInterestRequest;
import org.n52.sos.request.GetObservationByIdRequest;
import org.n52.sos.request.GetObservationRequest;
import org.n52.sos.request.GetResultRequest;
import org.n52.sos.request.InsertObservationRequest;
import org.n52.sos.request.InsertResultTemplateRequest;
import org.n52.sos.request.SrsNameRequest;
import org.n52.sos.response.AbstractServiceResponse;
import org.n52.sos.response.DescribeSensorResponse;
import org.n52.sos.response.GetCapabilitiesResponse;
import org.n52.sos.response.GetFeatureOfInterestResponse;
import org.n52.sos.response.GetObservationByIdResponse;
import org.n52.sos.response.GetObservationResponse;
import org.n52.sos.response.GetResultResponse;
import org.n52.sos.response.InsertObservationResponse;
import org.n52.sos.response.InsertResultTemplateResponse;
import org.n52.sos.service.ProcedureDescriptionSettings;
import org.n52.sos.util.CollectionHelper;
import org.n52.sos.util.Constants;
import org.n52.sos.util.EpsgConstants;
import org.n52.sos.util.GeometryHandler;
import org.n52.sos.util.JTSHelper;
import org.n52.sos.util.StringHelper;
import org.n52.sos.util.SweHelper;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Joiner;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.vividsolutions.jts.geom.Envelope;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.Point;

/**
 * Class that transforms geometries in the requests to the stored EPSG code and
 * transforms geometries in the responses to the default response or requested
 * EPSG code.
 * 
 * @author Carsten Hollmann <c.hollmann@52north.org>
 * 
 * @since 4.1.0
 * 
 */
public class CoordinateTransformator extends
        AbstractRequestResponseModifier<AbstractServiceRequest<?>, AbstractServiceResponse> {

    private final SweHelper helper = new SweHelper();
    private static final Set<RequestResponseModifierKeyType> REQUEST_RESPONSE_MODIFIER_KEY_TYPES = getKeyTypes();

    private static Set<RequestResponseModifierKeyType> getKeyTypes() {
        Set<String> services = Sets.newHashSet(SosConstants.SOS);
        Set<String> versions = Sets.newHashSet(Sos1Constants.SERVICEVERSION, Sos2Constants.SERVICEVERSION);
        Map<AbstractServiceRequest<?>, AbstractServiceResponse> requestResponseMap = Maps.newHashMap();
        requestResponseMap.put(new GetCapabilitiesRequest(), new GetCapabilitiesResponse());
        requestResponseMap.put(new GetObservationRequest(), new GetObservationResponse());
        requestResponseMap.put(new GetObservationByIdRequest(), new GetObservationByIdResponse());
        requestResponseMap.put(new GetFeatureOfInterestRequest(), new GetFeatureOfInterestResponse());
        requestResponseMap.put(new InsertObservationRequest(), new InsertObservationResponse());
        requestResponseMap.put(new InsertResultTemplateRequest(), new InsertResultTemplateResponse());
        requestResponseMap.put(new GetResultRequest(), new GetResultResponse());
        requestResponseMap.put(new DescribeSensorRequest(), new DescribeSensorResponse());
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
        checkRequestIfCrsIsSetAndSupported(request);
        if (request instanceof GetFeatureOfInterestRequest) {
            return modifyGetFeatureOfInterestRequest((GetFeatureOfInterestRequest) request);
        } else if (request instanceof GetObservationRequest) {
            return modifyGetObservationRequest((GetObservationRequest) request);
        } else if (request instanceof GetResultRequest) {
            return modifyGetResultRequest((GetResultRequest) request);
        } else if (request instanceof InsertObservationRequest) {
            return modifyInsertObservationRequest((InsertObservationRequest) request);
        } else if (request instanceof InsertResultTemplateRequest) {
            return modifyInsertResultTemplateRequest((InsertResultTemplateRequest) request);
        }
        return request;
    }

    @Override
    public AbstractServiceResponse modifyResponse(AbstractServiceRequest<?> request, AbstractServiceResponse response)
            throws OwsExceptionReport {
        if (request instanceof GetFeatureOfInterestRequest && response instanceof GetFeatureOfInterestResponse) {
            return modifyGetFeatureOfInterestResponse((GetFeatureOfInterestRequest) request,
                    (GetFeatureOfInterestResponse) response);
        } else if (request instanceof GetObservationRequest && response instanceof GetObservationResponse) {
            return modifyGetObservationResponse((GetObservationRequest) request, (GetObservationResponse) response);
        } else if (request instanceof GetObservationByIdRequest && response instanceof GetObservationByIdResponse) {
            return modifyGetObservationByIdResponse((GetObservationByIdRequest) request,
                    (GetObservationByIdResponse) response);
        } else if (request instanceof GetCapabilitiesRequest && response instanceof GetCapabilitiesResponse) {
            return modifyGetCapabilitiesResponse((GetCapabilitiesRequest) request, (GetCapabilitiesResponse) response);
        } else if (request instanceof DescribeSensorRequest && response instanceof DescribeSensorResponse) {
            return modifyDescribeSensorResponse((DescribeSensorRequest) request, (DescribeSensorResponse) response);
        }
        return response;
    }

    /**
     * Modify the GetFeatureOfInterest request
     * 
     * @param request
     *            the GetFeatureOfInterest request
     * @return Modified the GetFeatureOfInterest request
     * @throws OwsExceptionReport
     *             If the transformation fails
     */
    private AbstractServiceRequest<?> modifyGetFeatureOfInterestRequest(GetFeatureOfInterestRequest request)
            throws OwsExceptionReport {
        if (request.isSetSpatialFilters()) {
            preProcessSpatialFilters(request.getSpatialFilters());
        }
        return request;
    }

    /**
     * Modify the GetObservation request
     * 
     * @param request
     *            the GetObservation request
     * @return Modified the GetObservation request
     * @throws OwsExceptionReport
     *             If the transformation fails
     */
    private AbstractServiceRequest<?> modifyGetObservationRequest(GetObservationRequest request)
            throws OwsExceptionReport {
        if (request.isSetSpatialFilter()) {
            preProcessSpatialFilter(request.getSpatialFilter());
        }
        return request;
    }

    /**
     * Modify the GetResult request
     * 
     * @param request
     *            the GetResult request
     * @return Modified the GetResult request
     * @throws OwsExceptionReport
     *             If the transformation fails
     */
    private AbstractServiceRequest<?> modifyGetResultRequest(GetResultRequest request) throws OwsExceptionReport {
        if (request.isSetSpatialFilter()) {
            preProcessSpatialFilter(request.getSpatialFilter());
        }
        return request;
    }

    /**
     * Modify the InsertObservation request
     * 
     * @param request
     *            the InsertObservation request
     * @return Modified the InsertObservation request
     * @throws OwsExceptionReport
     *             If the transformation fails
     */
    private AbstractServiceRequest<?> modifyInsertObservationRequest(InsertObservationRequest request)
            throws OwsExceptionReport {
        if (request.isSetObservation()) {
            checkRequestedObservations(request.getObservations());
        }
        return request;
    }

    /**
     * Modify the InsertResultTemplate request
     * 
     * @param request
     *            the InsertResultTemplate request
     * @return Modified the InsertResultTemplate request
     * @throws OwsExceptionReport
     *             If the transformation fails
     */
    private AbstractServiceRequest<?> modifyInsertResultTemplateRequest(InsertResultTemplateRequest request)
            throws OwsExceptionReport {
        if (request.getObservationTemplate().getFeatureOfInterest() instanceof AbstractSamplingFeature) {
            checkRequestedGeometryOfSamplingFeature((AbstractSamplingFeature) request.getObservationTemplate()
                    .getFeatureOfInterest());
        }
        return request;
    }

    /**
     * Modify the GetFeatureOfInterest response
     * 
     * @param request
     *            the GetFeatureOfInterest request
     * @return Modified the GetFeatureOfInterest request
     * @param response
     *            the GetFeatureOfInterest response
     * @return Modified the GetFeatureOfInterest response
     * @throws OwsExceptionReport
     *             If the transformation fails
     */
    private AbstractServiceResponse modifyGetFeatureOfInterestResponse(GetFeatureOfInterestRequest request,
            GetFeatureOfInterestResponse response) throws OwsExceptionReport {
        processAbstractFeature(response.getAbstractFeature(), getRequestedCrs(request), getRequested3DCrs(request));
        return response;
    }

    /**
     * Modify the GetObservation response
     * 
     * @param request
     *            the GetObservation request
     * @return Modified the GetObservation request
     * @param response
     *            the GetObservation response
     * @return Modified the GetObservation response
     * @throws OwsExceptionReport
     *             If the transformation fails
     */
    private AbstractServiceResponse modifyGetObservationResponse(GetObservationRequest request,
            GetObservationResponse response) throws OwsExceptionReport {
        response.setResponseFormat(request.getResponseFormat());
        checkResponseObservations(response.getObservationCollection(), getRequestedCrs(request), getRequested3DCrs(request));
        return response;
    }

    /**
     * Modify the GetObservationById response
     * 
     * @param request
     *            the GetObservationById request
     * @return Modified the GetObservationById request
     * @param response
     *            the GetObservationById response
     * @return Modified the GetObservationById response
     * @throws OwsExceptionReport
     *             If the transformation fails
     */
    private AbstractServiceResponse modifyGetObservationByIdResponse(GetObservationByIdRequest request,
            GetObservationByIdResponse response) throws OwsExceptionReport {
        checkResponseObservations(response.getObservationCollection(), getRequestedCrs(request), getRequested3DCrs(request));
        return response;
    }

    /**
     * Modify the GetCapabilities response
     * 
     * @param request
     *            the GetCapabilities request
     * @return Modified the GetCapabilities request
     * @param response
     *            the GetCapabilities response
     * @return Modified the GetCapabilities response
     * @throws OwsExceptionReport
     *             If an error occurs
     */
    private AbstractServiceResponse modifyGetCapabilitiesResponse(GetCapabilitiesRequest request,
            GetCapabilitiesResponse response) throws OwsExceptionReport {
        if (response.getCapabilities().isSetContents()) {
            for (SosObservationOffering sosObservationOffering : response.getCapabilities().getContents()) {
                if (sosObservationOffering.isSetObservedArea()) {
                    SosEnvelope observedArea = sosObservationOffering.getObservedArea();
                    int targetSrid = getRequestedCrs(request);
                    Envelope transformEnvelope =
                            getGeomtryHandler().transformEnvelope(observedArea.getEnvelope(), observedArea.getSrid(),
                                    targetSrid);
                    observedArea.setEnvelope(transformEnvelope);
                    observedArea.setSrid(targetSrid);
                    sosObservationOffering.setObservedArea(observedArea);
                }
            }
        }
        return response;
    }

    /**
     * Modify the DescribeSensor response
     * 
     * @param request
     *            the DescribeSensor request
     * @return Modified the DescribeSensor request
     * @param response
     *            the DescribeSensor response
     * @return Modified the DescribeSensor response
     * @throws OwsExceptionReport
     *             If the transformation fails
     */
    private AbstractServiceResponse modifyDescribeSensorResponse(DescribeSensorRequest request,
            DescribeSensorResponse response) throws NumberFormatException, OwsExceptionReport {
        int requestedCrs = getRequestedCrs(request);
        if (response.isSetProcedureDescriptions()) {
            for (SosProcedureDescription description : response.getProcedureDescriptions()) {
                if (description instanceof AbstractSensorML) {
                    checkAbstractSensorML((AbstractSensorML) description, requestedCrs);
                }
            }
        }
        return response;
    }

    /**
     * Check the {@link AbstractSensorML} if modifications are required
     * 
     * @param abstractSensorML
     *            {@link AbstractSensorML} to check
     * @param targetCrs
     *            Target EPSG code Target EPSG code
     * @throws OwsExceptionReport
     *             If the transformation fails
     */
    private void checkAbstractSensorML(AbstractSensorML abstractSensorML, int targetCrs) throws OwsExceptionReport {
        if (abstractSensorML instanceof SensorML) {
            checkCapabilitiesForObservedAreaAndTransform(abstractSensorML, targetCrs);
            if (((SensorML) abstractSensorML).isSetMembers()) {
                for (AbstractProcess member : ((SensorML) abstractSensorML).getMembers()) {
                    checkCapabilitiesForObservedAreaAndTransform(member, targetCrs);
                    checkAbstractProcess(member, targetCrs);
                }
            }
        } else {
            checkAbstractProcess(abstractSensorML, targetCrs);
        }
    }

    /**
     * Check the {@link AbstractProcess} if modifications are required
     * 
     * @param abstractProcess
     *            {@link AbstractProcess} to check
     * @param targetCrs
     *            Target EPSG code
     * @throws OwsExceptionReport
     *             If the transformation fails
     */
    private void checkAbstractProcess(AbstractSensorML abstractSensorML, int targetCrs) throws OwsExceptionReport {
        if (abstractSensorML instanceof AbstractComponent) {
            AbstractComponent abstractComponent = (AbstractComponent) abstractSensorML;
            if (abstractComponent.isSetPosition()) {
                transformPosition(abstractComponent.getPosition(), targetCrs);
            } else if (abstractComponent.isSetLocation()) {
                transformLocation(abstractComponent.getLocation(), targetCrs);
            }
            if (abstractComponent instanceof System && ((System) abstractComponent).isSetComponents()) {
                for (SmlComponent component : ((System) abstractComponent).getComponents()) {
                    if (component.isSetProcess()) {
                        checkAbstractSensorML(component.getProcess(), targetCrs);
                    }
                }
            }
        } else if (abstractSensorML instanceof AbstractPhysicalProcess) {
            AbstractPhysicalProcess process = (AbstractPhysicalProcess) abstractSensorML;
            if (process.isSetPosition()) {
                transformPosition(process.getPosition(), targetCrs);
            }
        }
    }

    /**
     * Check the {@link SmlPosition} if modifications are required
     * 
     * @param position
     *            {@link SmlPosition} to check
     * @param targetCrs
     *            Target EPSG code
     * @throws OwsExceptionReport
     *             If the transformation fails
     */
    private void transformPosition(SmlPosition position, int targetCrs) throws OwsExceptionReport {
        int sourceCrs = targetCrs;
        if (position.isSetReferenceFrame() && checkReferenceFrame(position.getReferenceFrame())) {
            sourceCrs = getCrsFromString(position.getReferenceFrame());
        } else if (position.isSetVector() && position.getVector().isSetReferenceFrame()
                && checkReferenceFrame(position.getVector().getReferenceFrame())) {
            sourceCrs = getCrsFromString(position.getVector().getReferenceFrame());
        }
        if (targetCrs != sourceCrs) {
            if (position.isSetPosition()) {
                position.setPosition(transformSweCoordinates(position.getPosition(), sourceCrs, targetCrs));
                position.setReferenceFrame(transformReferenceFrame(position.getReferenceFrame(), sourceCrs, targetCrs));
            } else if (position.isSetVector()) {
                SweVector vector = position.getVector();
                vector.setCoordinates(transformSweCoordinates(vector.getCoordinates(), sourceCrs, targetCrs));
                vector.setReferenceFrame(transformReferenceFrame(vector.getReferenceFrame(), sourceCrs, targetCrs));
            }
        }
    }

    @VisibleForTesting
    protected String transformReferenceFrame(String referenceFrame, int sourceCrs, int targetCrs) {
        if (sourceCrs > 0 && targetCrs > 0) {
            return referenceFrame.replace(Integer.toString(sourceCrs), Integer.toString(targetCrs));
        }
        return referenceFrame;
    }

    /**
     * Transform coordinates
     * 
     * @param position
     *            {@link SweCoordinate}s to transform
     * @param sourceCrs
     *            Source CRS
     * @param targetCrs
     *            Target CRS
     * @return Transformed {@link SweCoordinate}s
     * @throws OwsExceptionReport
     *             if an error occurs
     */
    private List<SweCoordinate<?>> transformSweCoordinates(List<SweCoordinate<?>> position, int sourceCrs,
            int targetCrs) throws OwsExceptionReport {
        SweCoordinate<?> altitude = null;
        Object easting = null;
        Object northing = null;
        String eastingName = SweCoordinateName.easting.name();
        String northingName = SweCoordinateName.northing.name();
        for (SweCoordinate<?> coordinate : position) {
            if (checkAltitudeName(coordinate.getName())) {
                altitude = coordinate;
            } else if (checkNorthingName(coordinate.getName())) {
                northingName = coordinate.getName();
                northing = coordinate.getValue().getValue();
            } else if (checkEastingName(coordinate.getName())) {
                eastingName = coordinate.getName();
                easting = coordinate.getValue().getValue();
            }
        }
        if (easting != null && northing != null) {
            List<Object> coordinates = Lists.newArrayListWithExpectedSize(Constants.INT_2);
            if (getGeomtryHandler().isNorthingFirstEpsgCode(sourceCrs)) {
                coordinates.add(northing);
                coordinates.add(easting);
            } else {
                coordinates.add(easting);
                coordinates.add(northing);
            }
            Geometry geom =
                    getGeomtryHandler().transform(
                            JTSHelper.createGeometryFromWKT(JTSHelper.createWKTPointFromCoordinateString(Joiner.on(
                                    Constants.SPACE_STRING).join(coordinates)), sourceCrs), targetCrs);
            double x, y;
            if (getGeomtryHandler().isNorthingFirstEpsgCode(targetCrs)) {
                x = geom.getCoordinate().y;
                y = geom.getCoordinate().x;
            } else {
                x = geom.getCoordinate().x;
                y = geom.getCoordinate().y;
            }
            SweQuantity yq =
                    helper.createSweQuantity(y, SweConstants.Y_AXIS, ProcedureDescriptionSettings.getInstance()
                            .getLatLongUom());
            SweQuantity xq =
                    helper.createSweQuantity(x, SweConstants.X_AXIS, ProcedureDescriptionSettings.getInstance()
                            .getLatLongUom());
            ArrayList<SweCoordinate<?>> newPosition =
                    Lists.<SweCoordinate<?>> newArrayList(new SweCoordinate<Double>(northingName, yq),
                            new SweCoordinate<Double>(eastingName, xq));
            if (altitude != null) {
                newPosition.add(altitude);
            }
            return newPosition;
        }
        return position;
    }

    /**
     * Check the {@link SmlLocation} if modifications are required
     * 
     * @param location
     *            {@link SmlLocation} to check
     * @param targetCrs
     *            Target EPSG code
     * @throws OwsExceptionReport
     *             If the transformation fails
     */
    private void transformLocation(SmlLocation location, int targetCrs) throws OwsExceptionReport {
        location.setPoint((Point) getGeomtryHandler().transform(location.getPoint(), targetCrs));
    }
    
    /**
     * Check if the name is a defined altitude name
     * 
     * @param name
     *            Name to check
     * @return <code>true</code>, if the name is an altitude name
     */
    @VisibleForTesting
    protected boolean checkAltitudeName(String name) {
        return AltitudeSweCoordinateName.isAltitudeSweCoordinateName(name)
                || CoordinateHelper.getInstance().hasAltitudeName(name);
    }

    /**
     * Check if the name is a defined northing name
     * 
     * @param name
     *            Name to check
     * @return <code>true</code>, if the name is a northing name
     */
    @VisibleForTesting
    protected boolean checkNorthingName(String name) {
        return NorthingSweCoordinateName.isNorthingSweCoordinateName(name)
                || CoordinateHelper.getInstance().hasNorthingName(name);
    }

    /**
     * Check if the name is a defined easting name
     * 
     * @param name
     *            Name to check
     * @return <code>true</code>, if the name is an easting name
     */
    @VisibleForTesting
    protected boolean checkEastingName(String name) {
        return EastingSweCoordinateName.isEastingSweCoordinateName(name)
                || CoordinateHelper.getInstance().hasEastingName(name);
    }

    /**
     * Check if the {@link AbstractSensorML} contains {@link SmlCapabilities}
     * with observed area
     * 
     * @param abstractSensorML
     *            {@link AbstractSensorML} to check
     * @param targetCrs
     *            Target EPSG code
     * @throws OwsExceptionReport
     *             If the transformation fails
     */
    private void checkCapabilitiesForObservedAreaAndTransform(AbstractSensorML abstractSensorML, int targetCrs)
            throws OwsExceptionReport {
        if (abstractSensorML.isSetCapabilities()) {
            for (SmlCapabilities capabilities : abstractSensorML.getCapabilities()) {
                if (SensorMLConstants.ELEMENT_NAME_OBSERVED_BBOX.equals(capabilities.getName())) {
                    if (capabilities.isSetAbstractDataRecord() && capabilities.getDataRecord().isSetFields()) {
                        for (SweField field : capabilities.getDataRecord().getFields()) {
                            if (SensorMLConstants.ELEMENT_NAME_OBSERVED_BBOX.equals(field.getName().getValue())
                                    && field.getElement() instanceof SweEnvelope
                                    && !Integer.toString(targetCrs).equals(
                                            ((SweEnvelope) field.getElement()).getReferenceFrame())) {
                                SweEnvelope envelope = (SweEnvelope) field.getElement();
                                int sourceCrs = getCrsFromString(envelope.getReferenceFrame());
                                Envelope transformEnvelope =
                                        getGeomtryHandler().transformEnvelope(envelope.toEnvelope(),
                                        		sourceCrs, targetCrs);
                                SweEnvelope newEnvelope =
                                        new SweEnvelope(new SosEnvelope(transformEnvelope, targetCrs),
                                                ProcedureDescriptionSettings.getInstance().getLatLongUom());
                                newEnvelope.setReferenceFrame(transformReferenceFrame(envelope.getReferenceFrame(), sourceCrs, targetCrs));
                                field.setElement(newEnvelope);
                            }
                        }
                    }
                }
            }
        }
    }

    /**
     * Check if the the CRS parameter is contained in the request and supported
     * 
     * @param request
     *            Request to check
     * @throws OwsExceptionReport
     *             If an error occurs or the requested RS is not supported
     */
    private void checkRequestIfCrsIsSetAndSupported(AbstractServiceRequest<?> request) throws OwsExceptionReport {
        int crsFrom = getCrsFrom(request);
        if (EpsgConstants.NOT_SET_EPSG != crsFrom) {
            String requestedCrs = Integer.toString(crsFrom);
            if (!getGeomtryHandler().getSupportedCRS().contains(requestedCrs)) {
                throw new InvalidParameterValueException(OWSConstants.AdditionalRequestParams.crs, requestedCrs);
            }
        }
    }

    /**
     * Get the CRS from the request or if the CRS parameter is not set, return
     * the {@link EpsgConstants.NOT_SET_EPSG}.
     * 
     * @param request
     *            the request to check
     * @return the requested CRS or {@link EpsgConstants.NOT_SET_EPSG}
     * @throws OwsExceptionReport
     *             If an error occurs when parsing the request
     */
    private int getCrsFrom(AbstractServiceRequest<?> request) throws OwsExceptionReport {
        if (request.isSetExtensions()) {
            if (request.getExtensions().containsExtension(OWSConstants.AdditionalRequestParams.crs)) {
                return getCrs(request.getExtensions().getExtension(OWSConstants.AdditionalRequestParams.crs)
                        .getValue());
            }
        } else if (request instanceof SrsNameRequest && ((SrsNameRequest) request).isSetSrsName()) {
            return getCrs(((SrsNameRequest) request).getSrsName());
        }
        return EpsgConstants.NOT_SET_EPSG;
    }

    /**
     * Get the target EPSG code. If set, the request CRS, else the default
     * response EPSG code.
     * 
     * @param request
     *            the request to get CRS from
     * @return Requested, if set, or the default response EPSG code
     * @throws OwsExceptionReport
     */
    private int getRequestedCrs(AbstractServiceRequest<?> request) throws OwsExceptionReport {
        int crsFrom = getCrsFrom(request);
        if (crsFrom != EpsgConstants.NOT_SET_EPSG) {
            return crsFrom;
        }
        return getGeomtryHandler().getDefaultResponseEPSG();
    }
    
    private int getRequested3DCrs(AbstractServiceRequest<?> request) throws OwsExceptionReport {
        int crsFrom = getCrsFrom(request);
        if (crsFrom != EpsgConstants.NOT_SET_EPSG) {
            return crsFrom;
        }
        return getGeomtryHandler().getDefaultResponse3DEPSG();
    }

    /**
     * Get the EPSG code as integer from value
     * 
     * @param value
     *            EPSG code
     * @return integer representation of EPSG code
     * @throws OwsExceptionReport
     *             If an error occurs
     */
    private int getCrs(Object value) throws OwsExceptionReport {
        if (value instanceof SweCount) {
            return ((SweCount) value).getValue();
        } else if (value instanceof Integer) {
            return (Integer) value;
        } else if (value instanceof SweText) {
            return getCrsFromString(((SweText) value).getValue());
        } else if (value instanceof String) {
            return getCrsFromString((String) value);
        }
        return EpsgConstants.NOT_SET_EPSG;
    }

    /**
     * Get EPSG code as integer from String value
     * 
     * @param crs
     *            String EPSG code
     * @return integer representation of EPSG code
     * @throws OwsExceptionReport
     *             If an error occurs when parsing
     */
    @VisibleForTesting
    protected int getCrsFromString(String crs) throws OwsExceptionReport {
        if (StringHelper.isNotEmpty(crs) && !"NOT_SET".equalsIgnoreCase(crs)) {
        	int lastIndex = 0;
			if (crs.startsWith(Constants.HTTP)) {
				lastIndex = crs.lastIndexOf(Constants.SLASH_STRING);
			} else if (crs.indexOf(Constants.COLON_STRING) != -1) {
				lastIndex = crs.lastIndexOf(Constants.COLON_STRING);
			}
            try {
                return lastIndex == 0 ? Integer.valueOf(crs) : Integer.valueOf(crs.substring(lastIndex + 1));
            } catch (final NumberFormatException nfe) {
                String parameter =
                        new StringBuilder().append(SosConstants.GetObservationParams.srsName.name())
                                .append(Constants.SLASH_CHAR).append(OWSConstants.AdditionalRequestParams.crs.name())
                                .toString();
                throw new NoApplicableCodeException()
                        .causedBy(nfe)
                        .at(parameter)
                        .withMessage(
                                "Error while parsing '%s' parameter! Parameter has to contain EPSG code number", parameter);
            }
        }
        throw new MissingParameterValueException(OWSConstants.AdditionalRequestParams.crs);
    }

    private boolean checkReferenceFrame(String referenceFrame) {
        char[] charArray = referenceFrame.toCharArray();
        for (int i = 0; i < charArray.length; i++) {
            if (Character.isDigit(referenceFrame.toCharArray()[i])){
                return true;
            }
        } 
        return false;
    }

    /**
     * Check if the spatial filters geometries EPSG code are the same as the
     * stored EPSG code. If not a coordinate transformation is performed.
     * 
     * @param spatialFilters
     *            Spatial filters to check
     * @throws OwsExceptionReport
     *             If the transformation fails
     */
    private void preProcessSpatialFilters(List<SpatialFilter> spatialFilters) throws OwsExceptionReport {
        for (SpatialFilter spatialFilter : spatialFilters) {
            preProcessSpatialFilter(spatialFilter);
        }
    }

    /**
     * Check if the spatial filter geometry EPSG code is the same as the stored
     * EPSG code. If not a coordinate transformation is performed.
     * 
     * @param spatialFilter
     *            Spatial filter to check
     * @throws OwsExceptionReport
     *             If the transformation fails
     */
    private void preProcessSpatialFilter(SpatialFilter spatialFilter) throws OwsExceptionReport {
        spatialFilter.setGeometry(getGeomtryHandler().transformToStorageEpsg(spatialFilter.getGeometry()));

    }

    /**
     * Check all geometries in the requested {@link OmObservation}s and
     * transform to storage EPSG code if necessary
     * 
     * @param observations
     *            Requested {@link OmObservation}s
     * @throws OwsExceptionReport
     *             If the transformation fails
     */
    private void checkRequestedObservations(List<OmObservation> observations) throws OwsExceptionReport {
        if (CollectionHelper.isNotEmpty(observations)) {
            for (OmObservation omObservation : observations) {
                if (omObservation.getObservationConstellation().getFeatureOfInterest() instanceof AbstractSamplingFeature) {
                    AbstractSamplingFeature samplingFeature =
                            (AbstractSamplingFeature) omObservation.getObservationConstellation().getFeatureOfInterest();
                    checkRequestedGeometryOfSamplingFeature(samplingFeature);
                }
                if (omObservation.isSetParameter()) {
                    checkOmParameterForGeometry(omObservation.getParameter(), true);
                }
            }
        }
    }

    /**
     * Check all geometries in the response {@link OmObservation}s and transform
     * to requested or default response EPSG code if necessary
     * 
     * @param observations
     *            Response {@link OmObservation}s
     * @param target3DCRS 
     * @param targetCrs
     *            Target EPSG code
     * @throws OwsExceptionReport
     *             If the transformation fails
     */
    private void checkResponseObservations(List<OmObservation> observations, int targetCRS, int target3DCRS) throws OwsExceptionReport {
        if (CollectionHelper.isNotEmpty(observations)) {
            for (OmObservation omObservation : observations) {
                if (omObservation.getObservationConstellation().getFeatureOfInterest() instanceof AbstractSamplingFeature)
                    checkResponseGeometryOfSamplingFeature((AbstractSamplingFeature) omObservation
                            .getObservationConstellation().getFeatureOfInterest(), targetCRS, target3DCRS);
                if (omObservation.isSetParameter()) {
                    checkOmParameterForGeometry(omObservation.getParameter(), false);
                }
                if (omObservation.getValue() instanceof AbstractStreaming) {
                    ((AbstractStreaming) omObservation.getValue()).add(OWSConstants.AdditionalRequestParams.crs,
                            Lists.newArrayList(targetCRS, target3DCRS));
                } else if (omObservation.getValue() instanceof MultiObservationValues) {
                    if (((MultiObservationValues)omObservation.getValue()).getValue() instanceof TLVTValue) {
                        checkTLVTValueForGeometry((TLVTValue)((MultiObservationValues)omObservation.getValue()).getValue(), targetCRS);
                    }
                } else if (omObservation.getValue() instanceof SingleObservationValue) {
                    SingleObservationValue singleValue = (SingleObservationValue)omObservation.getValue();
                    if (singleValue.getValue() instanceof CvDiscretePointCoverage) {
                        checkCvDiscretePointCoverageForGeometry((CvDiscretePointCoverage)singleValue.getValue(),targetCRS);
                    } else if (((SingleObservationValue)omObservation.getValue()).getValue() instanceof MultiPointCoverage) {
                        checkMultiPointCoverageForGeometry((MultiPointCoverage)singleValue.getValue(),targetCRS);
                    }
                }
            }
        }
    }

    private void checkMultiPointCoverageForGeometry(MultiPointCoverage value, int targetCRS) throws OwsExceptionReport {
        for (PointValuePair pvp : value.getValue()) {
            pvp.setPoint((Point)getGeomtryHandler().transform(pvp.getPoint(), targetCRS));
        }
    }

    private void checkCvDiscretePointCoverageForGeometry(CvDiscretePointCoverage value, int targetCRS) throws OwsExceptionReport {
        value.getValue().setPoint((Point)getGeomtryHandler().transform(value.getValue().getPoint(), targetCRS));
    }

    private void checkTLVTValueForGeometry(TLVTValue value, int targetCRS) throws OwsExceptionReport {
        for (TimeLocationValueTriple tlvt : value.getValue()) {
            tlvt.setLocation(getGeomtryHandler().transform(tlvt.getLocation(), targetCRS));
        }
    }

    /**
     * Check and transform the {@link SamplingFeature} geometry to storage EPSG
     * code if necessary
     * 
     * @param samplingFeature
     *            the {@link SamplingFeature}
     * @throws OwsExceptionReport
     *             If the transformation fails
     */
    private void checkRequestedGeometryOfSamplingFeature(AbstractSamplingFeature samplingFeature) throws OwsExceptionReport {
        if (samplingFeature.isSetGeometry()) {
            samplingFeature.setGeometry(getGeomtryHandler().transformToStorageEpsg(samplingFeature.getGeometry()));
        }
    }

    /**
     * Check and transform the {@link SamplingFeature} geometry to requested or
     * default response EPSG code if necessary
     * 
     * @param samplingFeature
     *            the {@link SamplingFeature}
     * @param targetCrs
     *            Target EPSG code
     * @throws OwsExceptionReport
     *             If the transformation fails
     */
    private void checkResponseGeometryOfSamplingFeature(AbstractSamplingFeature samplingFeature, int targetCRS, int target3DCRS)
            throws OwsExceptionReport {
        if (samplingFeature.isSetGeometry()) {
            if (Double.isNaN(samplingFeature.getGeometry().getCoordinate().z)) {
                if (samplingFeature.getGeometry().getSRID() != targetCRS) {
                    samplingFeature.setGeometry(getGeomtryHandler().transform(samplingFeature.getGeometry(), targetCRS));
                }
            } else {
                if (samplingFeature.getGeometry().getSRID() != target3DCRS) {
                    samplingFeature.setGeometry(getGeomtryHandler().transform(samplingFeature.getGeometry(), target3DCRS));
                }
            }
        }
    }

    /**
     * Check all geometries in the response {@link AbstractFeature}s and
     * transform to requested or default response EPSG code if necessary
     * 
     * @param feature
     *            the response {@link AbstractFeature}s
     * @param targetCrs
     *            Target EPSG code
     * @throws OwsExceptionReport
     *             If the transformation fails
     */
    private void processAbstractFeature(AbstractFeature feature, int targetCRS, int target3DCRS) throws OwsExceptionReport {
        if (feature != null) {
            if (feature instanceof FeatureCollection) {
                FeatureCollection featureCollection = (FeatureCollection) feature;
                for (AbstractFeature abstractFeature : featureCollection.getMembers().values()) {
                    if (abstractFeature instanceof AbstractSamplingFeature) {
                        checkResponseGeometryOfSamplingFeature((AbstractSamplingFeature) abstractFeature, targetCRS, target3DCRS);
                    }
                }
            } else if (feature instanceof AbstractSamplingFeature) {
                checkResponseGeometryOfSamplingFeature((AbstractSamplingFeature) feature, targetCRS, target3DCRS);
            }
        }
    }

    /**
     * Check if the O&M parameter contains a geometry and transform to target
     * EPSG code, e.g. SOS 2.0 Spatial Filtering Profile
     * 
     * @param parameters
     *            O&M parameter to check
     * @param targetCrs
     *            Target EPSG code
     * @throws OwsExceptionReport
     *             If the transformation fails
     */
    @SuppressWarnings("unchecked")
    private void checkOmParameterForGeometry(Collection<NamedValue<?>> parameters, boolean request)
            throws OwsExceptionReport {
        for (NamedValue<?> namedValue : parameters) {
            if (Sos2Constants.HREF_PARAMETER_SPATIAL_FILTERING_PROFILE.equals(namedValue.getName().getHref())) {
                NamedValue<Geometry> spatialFilteringProfileParameter = (NamedValue<Geometry>) namedValue;
                if (request) {
                    spatialFilteringProfileParameter.getValue().setValue(
                            getGeomtryHandler().transformToStorageEpsg(spatialFilteringProfileParameter.getValue().getValue()));
                } else {
                    spatialFilteringProfileParameter.getValue().setValue(
                            getGeomtryHandler().transform(spatialFilteringProfileParameter.getValue().getValue(),
                                    !Double.isNaN(spatialFilteringProfileParameter.getValue().getValue().getCoordinate().z)
                                        ? getGeomtryHandler().getStorage3DEPSG()
                                        : getGeomtryHandler().getStorageEPSG()));
                }
            }
        }
    }

    private GeometryHandler getGeomtryHandler() {
        return GeometryHandler.getInstance();
    }

}
