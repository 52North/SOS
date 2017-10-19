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

import java.util.Arrays;
import java.util.List;
import org.n52.shetland.ogc.ows.exception.CompositeOwsException;
import org.n52.shetland.ogc.ows.exception.OwsExceptionReport;
import org.n52.shetland.ogc.ows.service.OwsServiceRequest;
import org.n52.shetland.ogc.ows.service.OwsServiceResponse;
import org.n52.shetland.ogc.sos.request.GetObservationRequest;
import org.n52.shetland.ogc.sos.response.GetObservationResponse;
import org.n52.shetland.ogc.sta.StaConstants;
import org.n52.sos.ds.AbstractGetObservationHandler;
import org.n52.shetland.ogc.sta.request.StaGetDatastreamsRequest;

/**
 *
 * @author <a href="mailto:m.kiesow@52north.org">Martin Kiesow</a>
 */
public class StaGetDatastreamRequestOperator extends StaAbstractGetRequestOperator<AbstractGetObservationHandler, StaGetDatastreamsRequest, GetObservationResponse> {

    public StaGetDatastreamRequestOperator() {
        super(StaConstants.SERVICE_NAME, StaConstants.VERSION_1_0, StaConstants.Operation.GET_DATASTREAMS.name(), StaGetDatastreamsRequest.class);
    }

    @Override
    protected OwsServiceResponse receive(OwsServiceRequest request) throws OwsExceptionReport {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    //@Override
    protected GetObservationResponse receive(StaGetDatastreamsRequest request) throws OwsExceptionReport {

        // create SOS GetObservationRequest from GetDatastreamsRequest
        GetObservationRequest sosRequest = new GetObservationRequest();

        List<StaConstants.PathSegment> path = request.getPath();

        for (int i = path.size(); i >= 0; i--) {
            StaConstants.PathSegment segment = path.get(i);
            StaConstants.PathComponent component = segment.getComponent();

            if (component instanceof StaConstants.EntityPathComponent) {
                // TODO if this part is used to determine the querryable entity: set the first entity and ignore other entities without IDs

                // get separate SOS IDs from Datastream ID
                String id = segment.getId();
                if (id != null && !id.equals("")) {

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
                }

            } else if (component instanceof StaConstants.Parameter) {

                switch ((StaConstants.Parameter) component) {
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
                }

            } else if (component instanceof StaConstants.Option) {
                throw new UnsupportedOperationException("REST path options are not supported yet.");

            } else {
                // TODO throw exception
            }
        }

//        sosRequest.setFeatureIdentifiers(request.get);
//        sosRequest.setObservedProperties(parseStringOrStringList(node.path(OBSERVED_PROPERTY)));
//        sosRequest.setOfferings(parseStringOrStringList(node.path(OFFERING)));
//        sosRequest.setProcedures(parseStringOrStringList(node.path(PROCEDURE)));
//        sosRequest.setResponseFormat(node.path(RESPONSE_FORMAT).textValue());
//        sosRequest.setResponseMode(node.path(RESPONSE_MODE).textValue());
//        sosRequest.setResultModel(node.path(RESULT_MODEL).textValue());
//        sosRequest.setResultFilter(parseComparisonFilter(node.path(RESULT_FILTER)));
//        sosRequest.setSpatialFilter(parseSpatialFilter(node.path(SPATIAL_FILTER)));
//        sosRequest.setTemporalFilters(parseTemporalFilters(node.path(TEMPORAL_FILTER)));
//        // TODO whats that for?
//        sosRequest.setRequestString(Json.print(node));
//
//
//        final GetObservationResponse response = getOperationHandler().getObservation(sosRequest);
        final GetObservationResponse response = null;

//        setObservationResponseResponseFormatAndContentType(request, response);
        // request.setQuerryOptions ...not supported yet

        return response;
    }


//  @Override
//    protected void checkParameters(OwsServiceRequest request) throws OwsExceptionReport {

    protected void checkParameters(StaGetDatastreamsRequest request) throws OwsExceptionReport {
//        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
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

    @Override
    protected void checkParameters(OwsServiceRequest request) throws OwsExceptionReport {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}
