/*
 * Copyright (C) 2012-2017 52°North Initiative for Geospatial Open Source
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
package org.n52.sos.request.operator;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import org.n52.shetland.ogc.om.OmConstants;
import org.n52.shetland.ogc.ows.exception.CompositeOwsException;
import org.n52.shetland.ogc.ows.exception.NoApplicableCodeException;
import org.n52.shetland.ogc.ows.exception.OwsExceptionReport;
import org.n52.shetland.ogc.ows.service.OwsServiceRequest;
import org.n52.shetland.ogc.ows.service.OwsServiceResponse;
import org.n52.shetland.ogc.sos.Sos2Constants;
import org.n52.shetland.ogc.sos.SosConstants;
import org.n52.shetland.ogc.sos.request.GetObservationRequest;
import org.n52.shetland.ogc.sos.response.GetObservationResponse;
import org.n52.shetland.ogc.sta.StaConstants;
import org.n52.shetland.ogc.sta.request.StaGetObservationsRequest;
import org.n52.shetland.ogc.sta.response.StaGetObservationsResponse;
import org.n52.sos.ds.hibernate.StaGetObservationDAO;

/**
 *
 * @author <a href="mailto:m.kiesow@52north.org">Martin Kiesow</a>
 */
public class StaGetObservationRequestOperator extends StaAbstractGetRequestOperator<StaGetObservationDAO, StaGetObservationsRequest, StaGetObservationsResponse> {

    public StaGetObservationRequestOperator() {
        super(StaConstants.SERVICE_NAME, StaConstants.VERSION_1_0, SosConstants.Operations.GetObservation.name(), StaGetObservationsRequest.class);
        //super(StaConstants.SERVICE_NAME, StaConstants.VERSION_1_0, StaConstants.Operation.GET_OBSERVATIONS.name(), StaGetObservationsRequest.class);
        //super(SosConstants.SOS, Sos2Constants.SERVICEVERSION, SosConstants.Operations.GetObservation.name(), StaGetObservationsRequest.class);
    }

    @Override
    protected OwsServiceResponse receive(OwsServiceRequest request) throws OwsExceptionReport {
    //protected StaGetObservationsResponse receive(StaGetObservationsRequest request) throws OwsExceptionReport {

        // create SOS GetObservationRequest from GetDatastreamsRequest
        GetObservationRequest sosRequest;
        //sosRequest = (GetObservationRequest) createSosRequest((StaGetObservationsRequest) request);

        // iterate path to determineQueryableEntityIndex(request);
        // create specific SOS request, e.g. GetObservationRequest for GetDatastreamsRequest
        // iterate path to add sos request parameters
            // depending on sos request type, add parameters from , e.g. procedure to GetObservationRequest
            // handle parameters
        // iterate and handle query options

        StaGetObservationsRequest staRequest = (StaGetObservationsRequest) request;
        List<StaConstants.PathSegment> path = staRequest.getPath();

        // create specific SOS request, e.g. GetObservationRequest for GetDatastreamsRequest
        sosRequest = new GetObservationRequest();

        // iterate path to add sos request parameters
            // depending on sos request type, add parameters from , e.g. procedure to GetObservationRequest
            // handle parameters
        StaConstants.PathComponent queryComponent = null;
        for (int i = path.size() - 1; i >= 0; i--) {
            StaConstants.PathSegment segment = path.get(i);
            StaConstants.PathComponent component = segment.getComponent();
            String id = segment.getId();

            if (component instanceof StaConstants.EntityPathComponent) {

                try {
                    // set the main resource (once)
                    if (queryComponent == null) {
                        queryComponent = component;

                        // ignore ID, as this is no GetObservationById request

                    } else if (id != null && !id.equals("")) {

                        // don't ask for a main resource's ID first, as this is no GetObservationById resutst
                        if (component.equals(StaConstants.EntitySet.Datastreams)) {

                            // set procedure, offering, observed property and feature of interest from datastream

                            // procedure
                            String procedure = id.replaceFirst("_op.*$", "");
                            if (procedure.startsWith("pr") && procedure.length() > 2
                                    && sosRequest.getProcedures().isEmpty()) {
                                sosRequest.setProcedures(Arrays.asList(procedure.substring(2)));
                            }

                            // observed property
                            String observedProperty = id.replaceFirst("^pr.*_", "").replaceFirst("_of.*$", "");
                            if (observedProperty.startsWith("op") && observedProperty.length() > 2
                                    && sosRequest.getObservedProperties().isEmpty()) {
                                sosRequest.setObservedProperties(Arrays.asList(observedProperty.substring(2)));
                            }

                            // offering
                            String offerings = id.replaceFirst("^pr.*_op.*_", "").replaceFirst("_fi.*$", "");
                            if (offerings.startsWith("of") && offerings.length() > 2
                                    && sosRequest.getOfferings().isEmpty()) {

                                List<String> of = Arrays.asList(offerings.substring(2).split("_"));
                                if (!of.isEmpty()) {

                                    sosRequest.setOfferings(of);
                                }
                            }

                            // feature of interest
                            String[] split = id.split("_");
                            String featureOfInterest = split[split.length - 1];
                            if (featureOfInterest.startsWith("fi") && featureOfInterest.length() > 2
                                    && sosRequest.getFeatureIdentifiers().isEmpty()) {
                                sosRequest.setFeatureIdentifiers(Arrays.asList(featureOfInterest.substring(2)));
                            }

                        } else if (component.equals(StaConstants.EntitySet.FeaturesOfInterest)) {

                            if (sosRequest.isSetFeatureOfInterest()) {
                                throw new IOException("The resource path contains contradicting information in " + segment.toString());
                            } else {
                                sosRequest.setFeatureIdentifiers(Arrays.asList(id));
                            }
                        } else if (component.equals(StaConstants.EntitySet.ObservedProperties)) {

                            if (sosRequest.isSetObservableProperty()) {
                                throw new IOException("The resource path contains contradicting information in " + segment.toString());
                            } else {
                                sosRequest.setObservedProperties(Arrays.asList(id));
                            }

                        } else if (component.equals(StaConstants.EntitySet.Sensors)
                                || component.equals(StaConstants.EntitySet.Things)) {

                            if (sosRequest.isSetProcedure()) {
                                throw new IOException("The resource path contains contradicting information in " + segment.toString());
                            } else {
                                sosRequest.setProcedures(Arrays.asList(id));
                            }
                        } else {}
                    }
                } catch (IOException ioe) {
                    throw new NoApplicableCodeException().withMessage("Error while reading request! Message: %s", ioe.getMessage());
                }

            } else if (component instanceof StaConstants.Parameter) {
                throw new UnsupportedOperationException("STA REST path parameters are not supported yet.");

                // TODO set request parameters (name, UoM, spatial/temporal filters)
                /* switch ((StaConstants.Parameter) component) {
                    case name:
                        break;
                    case description:
                        break;
                    case observationType:
                        break;
                    case unitOfMeasurement:
                        break;
                    case observedArea:
                        break;
                    case phenomenonTime:
                        break;
                    case resultTime:
                        break;
                    default:
                        break;
                } */

            } else if (component instanceof StaConstants.Option) {
                throw new UnsupportedOperationException("STA REST path options are not supported yet.");

            } else {
                // TODO throw exception
            }
        }

        sosRequest.setService(StaConstants.SERVICE_NAME);
        sosRequest.setVersion(StaConstants.VERSION_1_0);

        sosRequest.setResponseFormat(OmConstants.RESPONSE_FORMAT_OM_2);

//        sosRequest.setResponseMode(node.path(RESPONSE_MODE).textValue());
//        sosRequest.setResultModel(node.path(RESULT_MODEL).textValue());
//        sosRequest.setResultFilter(parseComparisonFilter(node.path(RESULT_FILTER)));
//        sosRequest.setSpatialFilter(parseSpatialFilter(node.path(SPATIAL_FILTER)));
//        sosRequest.setTemporalFilters(parseTemporalFilters(node.path(TEMPORAL_FILTER)));

//        // TODO whats that for?
//        sosRequest.setRequestString(Json.print(node));

        final GetObservationResponse response = ((StaGetObservationDAO) getOperationHandler()).getObservation(sosRequest);

        setObservationResponseResponseFormatAndContentType(sosRequest, response);

        // TODO convert SOS response to STA response

        return response;
    }

    @Override
    protected void checkParameters(OwsServiceRequest request) throws OwsExceptionReport {
        //protected void checkParameters(StaGetObservationsRequest request) throws OwsExceptionReport {
        final CompositeOwsException exceptions = new CompositeOwsException();

        try {
            checkServiceParameter(request.getService());
        } catch (OwsExceptionReport owse) {
            exceptions.add(owse);
        }
        try {
            checkSingleVersionParameter(request);
        } catch (OwsExceptionReport owse) {
            exceptions.add(owse);
        }

        // TODO add checks for STA parameters, these were removed:
        // offeringId, observedProperties, procedureIds, featureOfInterestIdentifiers, spatialFilter, isSetTemporalFilter, responseFormat
        // empty, even in SOS: checkExtensions(sosRequest, exceptions);

        exceptions.throwIfNotEmpty();

        // TODO do we have to abort requests without filters, because the result might get huge? (cf. SosGetObservationOperatorV20)
    }
}
