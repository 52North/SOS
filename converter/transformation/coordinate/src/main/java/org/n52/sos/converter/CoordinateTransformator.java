/*
 * Copyright (C) 2012-2023 52°North Spatial Information Research GmbH
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

import static java.util.stream.Collectors.toList;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Stream;

import javax.inject.Inject;

import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.Envelope;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Point;
import org.locationtech.jts.geom.PrecisionModel;
import org.n52.faroe.annotation.Setting;
import org.n52.iceland.convert.RequestResponseModifier;
import org.n52.iceland.convert.RequestResponseModifierFacilitator;
import org.n52.iceland.convert.RequestResponseModifierKey;
import org.n52.janmayen.lifecycle.Constructable;
import org.n52.shetland.ogc.filter.SpatialFilter;
import org.n52.shetland.ogc.gml.AbstractFeature;
import org.n52.shetland.ogc.om.MultiObservationValues;
import org.n52.shetland.ogc.om.NamedValue;
import org.n52.shetland.ogc.om.OmObservation;
import org.n52.shetland.ogc.om.PointValuePair;
import org.n52.shetland.ogc.om.SingleObservationValue;
import org.n52.shetland.ogc.om.TimeLocationValueTriple;
import org.n52.shetland.ogc.om.features.FeatureCollection;
import org.n52.shetland.ogc.om.features.samplingFeatures.AbstractSamplingFeature;
import org.n52.shetland.ogc.om.features.samplingFeatures.SamplingFeature;
import org.n52.shetland.ogc.om.values.CvDiscretePointCoverage;
import org.n52.shetland.ogc.om.values.MultiPointCoverage;
import org.n52.shetland.ogc.om.values.TLVTValue;
import org.n52.shetland.ogc.ows.OWSConstants;
import org.n52.shetland.ogc.ows.exception.InvalidParameterValueException;
import org.n52.shetland.ogc.ows.exception.MissingParameterValueException;
import org.n52.shetland.ogc.ows.exception.NoApplicableCodeException;
import org.n52.shetland.ogc.ows.exception.OwsExceptionReport;
import org.n52.shetland.ogc.ows.service.GetCapabilitiesRequest;
import org.n52.shetland.ogc.ows.service.GetCapabilitiesResponse;
import org.n52.shetland.ogc.ows.service.OwsServiceRequest;
import org.n52.shetland.ogc.ows.service.OwsServiceResponse;
import org.n52.shetland.ogc.sensorML.AbstractComponent;
import org.n52.shetland.ogc.sensorML.AbstractProcess;
import org.n52.shetland.ogc.sensorML.AbstractSensorML;
import org.n52.shetland.ogc.sensorML.SensorML;
import org.n52.shetland.ogc.sensorML.SensorMLConstants;
import org.n52.shetland.ogc.sensorML.System;
import org.n52.shetland.ogc.sensorML.elements.SmlCapabilities;
import org.n52.shetland.ogc.sensorML.elements.SmlComponent;
import org.n52.shetland.ogc.sensorML.elements.SmlLocation;
import org.n52.shetland.ogc.sensorML.elements.SmlPosition;
import org.n52.shetland.ogc.sensorML.v20.AbstractPhysicalProcess;
import org.n52.shetland.ogc.sos.Sos1Constants;
import org.n52.shetland.ogc.sos.Sos2Constants;
import org.n52.shetland.ogc.sos.SosCapabilities;
import org.n52.shetland.ogc.sos.SosConstants;
import org.n52.shetland.ogc.sos.SosObservationOffering;
import org.n52.shetland.ogc.sos.SosProcedureDescription;
import org.n52.shetland.ogc.sos.request.DescribeSensorRequest;
import org.n52.shetland.ogc.sos.request.GetFeatureOfInterestRequest;
import org.n52.shetland.ogc.sos.request.GetObservationByIdRequest;
import org.n52.shetland.ogc.sos.request.GetObservationRequest;
import org.n52.shetland.ogc.sos.request.GetResultRequest;
import org.n52.shetland.ogc.sos.request.InsertObservationRequest;
import org.n52.shetland.ogc.sos.request.InsertResultTemplateRequest;
import org.n52.shetland.ogc.sos.request.SrsNameRequest;
import org.n52.shetland.ogc.sos.response.AbstractStreaming;
import org.n52.shetland.ogc.sos.response.DescribeSensorResponse;
import org.n52.shetland.ogc.sos.response.GetFeatureOfInterestResponse;
import org.n52.shetland.ogc.sos.response.GetObservationByIdResponse;
import org.n52.shetland.ogc.sos.response.GetObservationResponse;
import org.n52.shetland.ogc.sos.response.GetResultResponse;
import org.n52.shetland.ogc.sos.response.InsertObservationResponse;
import org.n52.shetland.ogc.sos.response.InsertResultTemplateResponse;
import org.n52.shetland.ogc.swe.CoordinateSettingsProvider;
import org.n52.shetland.ogc.swe.SweConstants;
import org.n52.shetland.ogc.swe.SweConstants.SweCoordinateNames;
import org.n52.shetland.ogc.swe.SweCoordinate;
import org.n52.shetland.ogc.swe.SweEnvelope;
import org.n52.shetland.ogc.swe.SweField;
import org.n52.shetland.ogc.swe.SweVector;
import org.n52.shetland.ogc.swe.simpleType.SweCount;
import org.n52.shetland.ogc.swe.simpleType.SweQuantity;
import org.n52.shetland.ogc.swe.simpleType.SweText;
import org.n52.shetland.util.CollectionHelper;
import org.n52.shetland.util.JavaHelper;
import org.n52.shetland.util.ReferencedEnvelope;
import org.n52.sos.service.ProcedureDescriptionSettings;
import org.n52.sos.util.GeometryHandler;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Strings;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

/**
 * Class that transforms geometries in the requests to the stored EPSG code and
 * transforms geometries in the responses to the default response or requested
 * EPSG code.
 *
 * @author <a href="mailto:c.hollmann@52north.org">Carsten Hollmann</a>
 *
 * @since 4.1.0
 *
 */
@SuppressFBWarnings({"EI_EXPOSE_REP2"})
public class CoordinateTransformator implements RequestResponseModifier, Constructable {

    private static final Set<RequestResponseModifierKey> REQUEST_RESPONSE_MODIFIER_KEY_TYPES = getKeyTypes();

    private static final int NOT_SET_EPSG = -1;

    private Set<String> northingNames = Collections.emptySet();

    private Set<String> eastingNames = Collections.emptySet();

    private Set<String> altitudeNames = Collections.emptySet();

    private ProcedureDescriptionSettings procedureSettings;

    private GeometryHandler geometryHandler;

    @Inject
    public void setProcedureDescriptionSettings(ProcedureDescriptionSettings procedureSettings) {
        this.procedureSettings = procedureSettings;
    }

    @Inject
    public void setGeometryHandler(GeometryHandler geometryHandler) {
        this.geometryHandler = geometryHandler;
    }

    /**
     * Check all geometries in the response {@link OmObservation} and transform
     * to requested or default response EPSG code if necessary
     *
     * @param omObservation
     *            Response {@link OmObservation}
     * @param targetCRS
     *            Target EPSG code
     * @param target3DCRS
     *            Target 3D EPSG code
     * @throws OwsExceptionReport
     *             If the transformation fails
     */
    private void checkResponseObservation(OmObservation omObservation, int targetCRS, int target3DCRS)
            throws OwsExceptionReport {
        if (omObservation.getObservationConstellation().getFeatureOfInterest() instanceof AbstractSamplingFeature) {
            checkResponseGeometryOfSamplingFeature(
                    (AbstractSamplingFeature) omObservation.getObservationConstellation().getFeatureOfInterest(),
                    targetCRS, target3DCRS);
        }
        if (omObservation.isSetParameter()) {
            checkOmParameterForGeometry(omObservation.getParameter(), false);
        }
        if (omObservation.getValue() instanceof AbstractStreaming) {
            ((AbstractStreaming) omObservation.getValue()).add(OWSConstants.AdditionalRequestParams.crs,
                    Lists.newArrayList(targetCRS, target3DCRS));
        } else if (omObservation.getValue() instanceof MultiObservationValues) {
            if (((MultiObservationValues) omObservation.getValue()).getValue() instanceof TLVTValue) {
                checkTLVTValueForGeometry((TLVTValue) ((MultiObservationValues) omObservation.getValue()).getValue(),
                        targetCRS);
            }
        } else if (omObservation.getValue() instanceof SingleObservationValue) {
            SingleObservationValue singleValue = (SingleObservationValue) omObservation.getValue();
            if (singleValue.getValue() instanceof CvDiscretePointCoverage) {
                checkCvDiscretePointCoverageForGeometry((CvDiscretePointCoverage) singleValue.getValue(), targetCRS);
            } else if (((SingleObservationValue) omObservation.getValue()).getValue() instanceof MultiPointCoverage) {
                checkMultiPointCoverageForGeometry((MultiPointCoverage) singleValue.getValue(), targetCRS);
            }
        }
    }

    /**
     *
     * Get the keys
     *
     * @return Set of keys
     */
    private static Set<RequestResponseModifierKey> getKeyTypes() {
        ImmutableMap.Builder<OwsServiceRequest, OwsServiceResponse> mapBuilder = ImmutableMap.builder();
        ImmutableSet.Builder<RequestResponseModifierKey> keysBuilder = ImmutableSet.builder();
        mapBuilder.put(new GetCapabilitiesRequest(SosConstants.SOS), new GetCapabilitiesResponse());
        mapBuilder.put(new GetObservationRequest(), new GetObservationResponse());
        mapBuilder.put(new GetObservationByIdRequest(), new GetObservationByIdResponse());
        mapBuilder.put(new GetFeatureOfInterestRequest(), new GetFeatureOfInterestResponse());
        mapBuilder.put(new InsertObservationRequest(), new InsertObservationResponse());
        mapBuilder.put(new InsertResultTemplateRequest(), new InsertResultTemplateResponse());
        mapBuilder.put(new GetResultRequest(), new GetResultResponse());
        mapBuilder.put(new DescribeSensorRequest(), new DescribeSensorResponse());
        List<String> services = Arrays.asList(SosConstants.SOS);
        List<String> versions = Arrays.asList(Sos1Constants.SERVICEVERSION, Sos2Constants.SERVICEVERSION);
        mapBuilder.build().forEach((request, response) -> {
            services.forEach(service -> {
                versions.forEach(version -> {
                    keysBuilder.add(new RequestResponseModifierKey(service, version, request),
                            new RequestResponseModifierKey(service, version, request, response));
                });
            });
        });
        return keysBuilder.build();
    }

    @Override
    public Set<RequestResponseModifierKey> getKeys() {
        return Collections.unmodifiableSet(REQUEST_RESPONSE_MODIFIER_KEY_TYPES);
    }

    @Override
    public OwsServiceRequest modifyRequest(OwsServiceRequest request) throws OwsExceptionReport {
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
    public OwsServiceResponse modifyResponse(OwsServiceRequest request, OwsServiceResponse response)
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
    private OwsServiceRequest modifyGetFeatureOfInterestRequest(GetFeatureOfInterestRequest request)
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
    private OwsServiceRequest modifyGetObservationRequest(GetObservationRequest request) throws OwsExceptionReport {
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
    private OwsServiceRequest modifyGetResultRequest(GetResultRequest request) throws OwsExceptionReport {
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
    private OwsServiceRequest modifyInsertObservationRequest(InsertObservationRequest request)
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
    private OwsServiceRequest modifyInsertResultTemplateRequest(InsertResultTemplateRequest request)
            throws OwsExceptionReport {
        if (request.getObservationTemplate().getFeatureOfInterest() instanceof AbstractSamplingFeature) {
            checkRequestedGeometryOfSamplingFeature(
                    (AbstractSamplingFeature) request.getObservationTemplate().getFeatureOfInterest());
        }
        return request;
    }

    /**
     * Modify the GetFeatureOfInterest response
     *
     * @param request
     *            the GetFeatureOfInterest request
     * @param response
     *            the GetFeatureOfInterest response
     * @return Modified the GetFeatureOfInterest response
     * @throws OwsExceptionReport
     *             If the transformation fails
     */
    private OwsServiceResponse modifyGetFeatureOfInterestResponse(GetFeatureOfInterestRequest request,
            GetFeatureOfInterestResponse response) throws OwsExceptionReport {
        processAbstractFeature(response.getAbstractFeature(), getRequestedCrs(request), getRequested3DCrs(request));
        return response;
    }

    /**
     * Modify the GetObservation response
     *
     * @param request
     *            the GetObservation request
     * @param response
     *            the GetObservation response
     * @return Modified the GetObservation response
     * @throws OwsExceptionReport
     *             If the transformation fails
     */
    private OwsServiceResponse modifyGetObservationResponse(GetObservationRequest request,
            GetObservationResponse response) throws OwsExceptionReport {
        response.setResponseFormat(request.getResponseFormat());
        int crs = getRequestedCrs(request);
        int crs3D = getRequested3DCrs(request);
        response.setObservationCollection(
                response.getObservationCollection().modify(o -> checkResponseObservation(o, crs, crs3D)));
        return response;
    }

    /**
     * Modify the GetObservationById response
     *
     * @param request
     *            the GetObservationById request
     * @param response
     *            the GetObservationById response
     * @return Modified the GetObservationById response
     * @throws OwsExceptionReport
     *             If the transformation fails
     */
    private OwsServiceResponse modifyGetObservationByIdResponse(GetObservationByIdRequest request,
            GetObservationByIdResponse response) throws OwsExceptionReport {
        int crs = getRequestedCrs(request);
        int crs3D = getRequested3DCrs(request);
        response.setObservationCollection(
                response.getObservationCollection().modify(o -> checkResponseObservation(o, crs, crs3D)));
        return response;
    }

    /**
     * Modify the GetCapabilities response
     *
     * @param request
     *            the GetCapabilities request
     * @param response
     *            the GetCapabilities response
     * @return Modified the GetCapabilities response
     * @throws OwsExceptionReport
     *             If an error occurs
     */
    private OwsServiceResponse modifyGetCapabilitiesResponse(GetCapabilitiesRequest request,
            GetCapabilitiesResponse response) throws OwsExceptionReport {
        if (!response.isStatic() && response.getCapabilities() instanceof SosCapabilities
                && ((SosCapabilities) response.getCapabilities()).getContents().isPresent()) {
            SosCapabilities sosCapabilities = (SosCapabilities) response.getCapabilities();
            if (sosCapabilities.getContents().isPresent()) {
                for (SosObservationOffering sosObservationOffering : sosCapabilities.getContents().get()) {
                    if (sosObservationOffering.isSetObservedArea()) {
                        ReferencedEnvelope observedArea = sosObservationOffering.getObservedArea();
                        int targetSrid = getRequestedCrs(request);
                        Envelope transformEnvelope = getGeomtryHandler().transformEnvelope(observedArea.getEnvelope(),
                                observedArea.getSrid(), targetSrid);
                        observedArea.setEnvelope(transformEnvelope);
                        observedArea.setSrid(targetSrid);
                        sosObservationOffering.setObservedArea(observedArea);
                    }
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
     * @param response
     *            the DescribeSensor response
     * @return Modified the DescribeSensor response
     * @throws OwsExceptionReport
     *             If the transformation fails
     */
    private OwsServiceResponse modifyDescribeSensorResponse(DescribeSensorRequest request,
            DescribeSensorResponse response) throws NumberFormatException, OwsExceptionReport {
        int requestedCrs = getRequestedCrs(request);
        if (response.isSetProcedureDescriptions()) {
            for (SosProcedureDescription<?> description : response.getProcedureDescriptions()) {
                if (description.getProcedureDescription() instanceof AbstractSensorML) {
                    checkAbstractSensorML((AbstractSensorML) description.getProcedureDescription(), requestedCrs);
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
     * @param abstractSensorML
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
                position.setReferenceFrame(
                        transformReferenceFrame(position.getReferenceFrame(), sourceCrs, targetCrs));
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
    private List<? extends SweCoordinate<? extends Number>> transformSweCoordinates(
            List<? extends SweCoordinate<? extends Number>> position, int sourceCrs, int targetCrs)
            throws OwsExceptionReport {
        SweCoordinate<? extends Number> altitude = null;
        Number easting = null;
        Number northing = null;
        String eastingName = SweCoordinateNames.EASTING;
        String northingName = SweCoordinateNames.NORTHING;
        for (SweCoordinate<? extends Number> coordinate : position) {
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

            double x;
            double y;
            if (getGeomtryHandler().isNorthingFirstEpsgCode(sourceCrs)) {
                y = northing.doubleValue();
                x = easting.doubleValue();
            } else {
                y = easting.doubleValue();
                x = northing.doubleValue();
            }



            GeometryFactory factory = new GeometryFactory(new PrecisionModel(PrecisionModel.FLOATING), sourceCrs);
            Coordinate coordinate = null;
            if (getGeomtryHandler().is3dCrs(sourceCrs)) {
                double z = 0.0;
                if (altitude != null && altitude.getValue() != null && altitude.getValue().getValue() != null) {
                    z = altitude.getValue().getValue().doubleValue();
                }
                coordinate = getGeomtryHandler().transform(factory.createPoint(new Coordinate(x, y, z)), targetCrs)
                        .getCoordinate();
            } else {
                coordinate = getGeomtryHandler().transform(factory.createPoint(new Coordinate(x, y)), targetCrs)
                        .getCoordinate();
            }

            if (getGeomtryHandler().isNorthingFirstEpsgCode(targetCrs)) {
                x = coordinate.y;
                y = coordinate.x;
            } else {
                x = coordinate.x;
                y = coordinate.y;
            }
            return Stream
                    .of(new SweCoordinate<>(northingName,
                            createSweQuantity(y, SweConstants.Y_AXIS, procedureSettings.getLatLongUom())),
                            new SweCoordinate<>(eastingName,
                                    createSweQuantity(x, SweConstants.X_AXIS, procedureSettings.getLatLongUom())),
                            altitude)
                    .filter(Objects::nonNull).collect(toList());
        }
        return position;
    }

    /**
     * Create a {@link SweQuantity} from parameter
     *
     * @param value
     *            the {@link SweQuantity} value
     * @param axis
     *            the {@link SweQuantity} axis id
     * @param uom
     *            the {@link SweQuantity} unit of measure
     * @return the {@link SweQuantity} from parameter
     */
    private SweQuantity createSweQuantity(Object value, String axis, String uom) {
        return new SweQuantity().setAxisID(axis).setUom(uom).setValue(JavaHelper.asDouble(value));
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
        return SweCoordinateNames.isZ(name) || hasAltitudeName(name);
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
        return SweCoordinateNames.isY(name) || hasNorthingName(name);
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
        return SweCoordinateNames.isX(name) || hasEastingName(name);
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
                                    && field.getElement() instanceof SweEnvelope && !Integer.toString(targetCrs)
                                            .equals(((SweEnvelope) field.getElement()).getReferenceFrame())) {
                                SweEnvelope envelope = (SweEnvelope) field.getElement();
                                int sourceCrs = getCrsFromString(envelope.getReferenceFrame());
                                boolean northingFirst = getGeomtryHandler().isNorthingFirstEpsgCode(sourceCrs);
                                Envelope transformEnvelope = getGeomtryHandler()
                                        .transformEnvelope(envelope.toEnvelope(), sourceCrs, targetCrs);
                                SweEnvelope newEnvelope =
                                        new SweEnvelope(new ReferencedEnvelope(transformEnvelope, targetCrs),
                                                procedureSettings.getLatLongUom(), northingFirst);
                                newEnvelope.setReferenceFrame(
                                        transformReferenceFrame(envelope.getReferenceFrame(), sourceCrs, targetCrs));
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
    private void checkRequestIfCrsIsSetAndSupported(OwsServiceRequest request) throws OwsExceptionReport {
        int crsFrom = getCrsFrom(request);
        if (NOT_SET_EPSG != crsFrom) {
            String requestedCrs = Integer.toString(crsFrom);
            if (!getGeomtryHandler().getSupportedCRS().contains(requestedCrs)) {
                throw new InvalidParameterValueException(OWSConstants.AdditionalRequestParams.crs, requestedCrs);
            }
        }
    }

    /**
     * Get the CRS from the request or if the CRS parameter is not set, return
     * the {@link #NOT_SET_EPSG}.
     *
     * @param request
     *            the request to check
     * @return the requested CRS or {@link #NOT_SET_EPSG}
     * @throws OwsExceptionReport
     *             If an error occurs when parsing the request
     */
    private int getCrsFrom(OwsServiceRequest request) throws OwsExceptionReport {
        Optional<?> crsExtension = request.getExtension(OWSConstants.AdditionalRequestParams.crs)
                .map(extension -> extension.getValue());

        if (crsExtension.isPresent()) {
            return getCrs(crsExtension.get());
        } else if (request instanceof SrsNameRequest && ((SrsNameRequest) request).isSetSrsName()) {
            return getCrs(((SrsNameRequest) request).getSrsName());
        }
        return NOT_SET_EPSG;
    }

    /**
     * Get the target EPSG code. If set, the request CRS, else the default
     * response EPSG code.
     *
     * @param request
     *            the request to get CRS from
     * @return Requested, if set, or the default response EPSG code
     * @throws OwsExceptionReport If an error occurs
     */
    private int getRequestedCrs(OwsServiceRequest request) throws OwsExceptionReport {
        int crsFrom = getCrsFrom(request);
        if (crsFrom != NOT_SET_EPSG) {
            return crsFrom;
        }
        return getGeomtryHandler().getDefaultResponseEPSG();
    }

    private int getRequested3DCrs(OwsServiceRequest request) throws OwsExceptionReport {
        int crsFrom = getCrsFrom(request);
        if (crsFrom != NOT_SET_EPSG) {
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
        return NOT_SET_EPSG;
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
        if (!Strings.isNullOrEmpty(crs) && !"NOT_SET".equalsIgnoreCase(crs)) {
            int lastIndex = 0;
            if (crs.startsWith("http")) {
                lastIndex = crs.lastIndexOf('/');
            } else if (crs.indexOf(':') != -1) {
                lastIndex = crs.lastIndexOf(':');
            }
            try {
                return lastIndex == 0 ? Integer.valueOf(crs) : Integer.valueOf(crs.substring(lastIndex + 1));
            } catch (final NumberFormatException nfe) {
                String parameter = new StringBuilder().append(SosConstants.GetObservationParams.srsName.name())
                        .append('/').append(OWSConstants.AdditionalRequestParams.crs.name()).toString();
                throw new NoApplicableCodeException().causedBy(nfe).at(parameter).withMessage(
                        "Error while parsing '%s' parameter! Parameter has to contain EPSG code number", parameter);
            }
        }
        throw new MissingParameterValueException(OWSConstants.AdditionalRequestParams.crs);
    }

    private boolean checkReferenceFrame(String referenceFrame) {
        char[] charArray = referenceFrame.toCharArray();
        for (int i = 0; i < charArray.length; i++) {
            if (Character.isDigit(referenceFrame.toCharArray()[i])) {
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
                if (omObservation.getObservationConstellation()
                        .getFeatureOfInterest() instanceof AbstractSamplingFeature) {
                    AbstractSamplingFeature samplingFeature = (AbstractSamplingFeature) omObservation
                            .getObservationConstellation().getFeatureOfInterest();
                    checkRequestedGeometryOfSamplingFeature(samplingFeature);
                }
                if (omObservation.isSetParameter()) {
                    checkOmParameterForGeometry(omObservation.getParameter(), true);
                }
            }
        }
    }

    private void checkMultiPointCoverageForGeometry(MultiPointCoverage value, int targetCRS)
            throws OwsExceptionReport {
        for (PointValuePair pvp : value.getValue()) {
            pvp.setPoint((Point) getGeomtryHandler().transform(pvp.getPoint(), targetCRS));
        }
    }

    private void checkCvDiscretePointCoverageForGeometry(CvDiscretePointCoverage value, int targetCRS)
            throws OwsExceptionReport {
        value.getValue().setPoint((Point) getGeomtryHandler().transform(value.getValue().getPoint(), targetCRS));
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
    private void checkRequestedGeometryOfSamplingFeature(AbstractSamplingFeature samplingFeature)
            throws OwsExceptionReport {
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
     * @param targetCRS
     *            Target EPSG code
     * @throws OwsExceptionReport
     *             If the transformation fails
     */
    private void checkResponseGeometryOfSamplingFeature(AbstractSamplingFeature samplingFeature, int targetCRS,
            int target3DCRS) throws OwsExceptionReport {
        if (samplingFeature.isSetGeometry()) {
            if (Double.isNaN(samplingFeature.getGeometry().getCoordinate().z)) {
                if (samplingFeature.getGeometry().getSRID() != targetCRS) {
                    samplingFeature
                            .setGeometry(getGeomtryHandler().transform(samplingFeature.getGeometry(), targetCRS));
                }
            } else {
                if (samplingFeature.getGeometry().getSRID() != target3DCRS) {
                    samplingFeature
                            .setGeometry(getGeomtryHandler().transform(samplingFeature.getGeometry(), target3DCRS));
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
     * @param targetCRS
     *            Target EPSG code
     * @throws OwsExceptionReport
     *             If the transformation fails
     */
    private void processAbstractFeature(AbstractFeature feature, int targetCRS, int target3DCRS)
            throws OwsExceptionReport {
        if (feature != null) {
            if (feature instanceof FeatureCollection) {
                FeatureCollection featureCollection = (FeatureCollection) feature;
                for (AbstractFeature abstractFeature : featureCollection.getMembers().values()) {
                    if (abstractFeature instanceof AbstractSamplingFeature) {
                        checkResponseGeometryOfSamplingFeature((AbstractSamplingFeature) abstractFeature, targetCRS,
                                target3DCRS);
                    }
                }
            } else if (feature instanceof AbstractSamplingFeature) {
                checkResponseGeometryOfSamplingFeature((AbstractSamplingFeature) feature, targetCRS, target3DCRS);
            }
        }
    }

    /**
     * Checks if the O&M parameter contains a geometry and transform to target
     * EPSG code, e.g. SOS 2.0 Spatial Filtering Profile
     *
     * @param parameters
     *            O&M parameter to check
     * @param request
     *            If the parameter is transformed for a request or a response.
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
                    spatialFilteringProfileParameter.getValue().setValue(getGeomtryHandler()
                            .transformToStorageEpsg(spatialFilteringProfileParameter.getValue().getValue()));
                } else {
                    spatialFilteringProfileParameter.getValue().setValue(getGeomtryHandler().transform(
                            spatialFilteringProfileParameter.getValue().getValue(),
                            !Double.isNaN(spatialFilteringProfileParameter.getValue().getValue().getCoordinate().z)
                                    ? getGeomtryHandler().getStorage3DEPSG()
                                    : getGeomtryHandler().getStorageEPSG()));
                }
            }
        }
    }

    private GeometryHandler getGeomtryHandler() {
        return geometryHandler;
    }

    /**
     * @return the northingNames
     */
    public Set<String> getNorthingNames() {
        return Collections.unmodifiableSet(northingNames);
    }

    /**
     * @param northingNames
     *            the northingNames to set
     */
    @Setting(CoordinateSettingsProvider.NORTHING_COORDINATE_NAME)
    public void setNorthingNames(String northingNames) {
        if (!Strings.isNullOrEmpty(northingNames)) {
            this.northingNames = CollectionHelper.csvStringToSet(northingNames);
        }
    }

    /**
     * Check if northing names contains name
     *
     * @param name
     *            Name to check
     * @return <code>true</code>, if the name is defined.
     */
    public boolean hasNorthingName(String name) {
        return check(getNorthingNames(), name);
    }

    /**
     * @return the eastingNames
     */
    public Set<String> getEastingNames() {
        return Collections.unmodifiableSet(eastingNames);
    }

    /**
     * @param eastingNames
     *            the eastingNames to set
     */
    @Setting(CoordinateSettingsProvider.EASTING_COORDINATE_NAME)
    public void setEastingNames(String eastingNames) {
        if (!Strings.isNullOrEmpty(eastingNames)) {
            this.eastingNames = CollectionHelper.csvStringToSet(eastingNames);
        }
    }

    /**
     * Check if easting names contains name
     *
     * @param name
     *            Name to check
     * @return <code>true</code>, if the name is defined.
     */
    public boolean hasEastingName(String name) {
        return check(getEastingNames(), name);
    }

    /**
     * @return the altitudeNames
     */
    public Set<String> getAltitudeNames() {
        return Collections.unmodifiableSet(altitudeNames);
    }

    /**
     * @param altitudeNames
     *            the altitudeNames to set
     */
    @Setting(CoordinateSettingsProvider.ALTITUDE_COORDINATE_NAME)
    public void setAltitudeNames(String altitudeNames) {
        if (!Strings.isNullOrEmpty(altitudeNames)) {
            this.altitudeNames = CollectionHelper.csvStringToSet(altitudeNames);
        }
    }

    /**
     * Check if altitude names contains name
     *
     * @param name
     *            Name to check
     * @return <code>true</code>, if the name is defined.
     */
    public boolean hasAltitudeName(String name) {
        return check(getAltitudeNames(), name);
    }

    private boolean check(Set<String> set, String name) {
        for (String string : set) {
            if (string.equalsIgnoreCase(name)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public RequestResponseModifierFacilitator getFacilitator() {
        return new RequestResponseModifierFacilitator();
    }

    @Override
    public void init() {
    }

}
