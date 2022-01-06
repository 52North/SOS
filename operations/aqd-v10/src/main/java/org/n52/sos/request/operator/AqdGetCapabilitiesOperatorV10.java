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
package org.n52.sos.request.operator;

import java.net.URI;
import java.util.Arrays;
import java.util.Collection;
import java.util.SortedSet;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.n52.iceland.exception.ows.concrete.InvalidAcceptVersionsParameterException;
import org.n52.janmayen.function.Functions;
import org.n52.janmayen.http.HTTPMethods;
import org.n52.shetland.aqd.AqdConstants;
import org.n52.shetland.aqd.ReportObligationType;
import org.n52.shetland.ogc.ows.OWSConstants;
import org.n52.shetland.ogc.ows.OwsAllowedValues;
import org.n52.shetland.ogc.ows.OwsDCP;
import org.n52.shetland.ogc.ows.OwsDomain;
import org.n52.shetland.ogc.ows.OwsOperation;
import org.n52.shetland.ogc.ows.OwsOperationsMetadata;
import org.n52.shetland.ogc.ows.OwsRequestMethod;
import org.n52.shetland.ogc.ows.OwsServiceIdentification;
import org.n52.shetland.ogc.ows.OwsValue;
import org.n52.shetland.ogc.ows.exception.CompositeOwsException;
import org.n52.shetland.ogc.ows.exception.OwsExceptionReport;
import org.n52.shetland.ogc.ows.service.GetCapabilitiesRequest;
import org.n52.shetland.ogc.ows.service.GetCapabilitiesResponse;
import org.n52.shetland.ogc.sos.SosCapabilities;
import org.n52.shetland.ogc.sos.SosConstants;
import org.n52.shetland.ogc.sos.SosObservationOffering;
import org.n52.sos.ds.AbstractGetCapabilitiesHandler;

public class AqdGetCapabilitiesOperatorV10 extends
        AbstractAqdRequestOperator<AbstractGetCapabilitiesHandler, GetCapabilitiesRequest, GetCapabilitiesResponse> {

    private static final String OPERATION_NAME = SosConstants.Operations.GetCapabilities.name();

    public AqdGetCapabilitiesOperatorV10() {
        super(OPERATION_NAME, GetCapabilitiesRequest.class);
    }

    @Override
    public GetCapabilitiesResponse receive(GetCapabilitiesRequest request) throws OwsExceptionReport {
        return modifyCapabilities((GetCapabilitiesResponse) changeResponseServiceVersion(
                getOperationHandler().getCapabilities((GetCapabilitiesRequest) changeRequestServiceVersion(request))));
    }

    private GetCapabilitiesResponse modifyCapabilities(GetCapabilitiesResponse response) {
        SosCapabilities capabilities = (SosCapabilities) response.getCapabilities();
        capabilities.setVersion(AqdConstants.VERSION);
        capabilities.setService(AqdConstants.AQD);
        capabilities.getServiceIdentification().ifPresent(this::modifyServiceIdentification);
        capabilities.getOperationsMetadata().ifPresent(this::modifyOperationsMetadata);
        capabilities.getContents().ifPresent(this::modifyContents);
        return response;
    }

    private void addFlowParameter(OwsOperation operation) {
        operation.addParameter(new OwsDomain(AqdConstants.EXTENSION_FLOW, new OwsAllowedValues(
                Arrays.stream(ReportObligationType.values()).map(ReportObligationType::name).map(OwsValue::new))));
    }

    private void removeJSONEndpoint(OwsOperation operation) {
        operation.getDCP().stream().filter(OwsDCP::isHTTP).map(OwsDCP::asHTTP)
                .forEach(http -> http.removeRequestMethodIf(this::isJsonEndpoint));
    }

    private boolean isJsonEndpoint(OwsRequestMethod method) {
        return method.getHttpMethod().equals(HTTPMethods.POST)
                && method.getHref().map(URI::getPath).map(path -> path.endsWith("/json")).orElse(false);
    }

    @Override
    protected void checkParameters(GetCapabilitiesRequest request) throws OwsExceptionReport {
        final CompositeOwsException exceptions = new CompositeOwsException();
        if (request.isSetAcceptVersions()) {
            boolean contains = false;
            for (String version : request.getAcceptVersions()) {
                if (AqdConstants.VERSION.equals(version)) {
                    contains = true;
                }
            }
            if (!contains) {
                exceptions.add(new InvalidAcceptVersionsParameterException(request.getAcceptVersions()));
            }
        }
        checkExtensions(request, exceptions);
        exceptions.throwIfNotEmpty();
    }

    private void setAcceptVersionsParameter(OwsOperation operation) {
        operation.getParameters().stream().filter(this::isAcceptVersions)
                .forEach(d -> d.setPossibleValues(new OwsAllowedValues(new OwsValue(AqdConstants.VERSION))));
    }

    private void setResponseFormat(OwsOperation operation) {
        operation.getParameters().stream().filter(this::isResponseFormat)
                .forEach(d -> d.setPossibleValues(new OwsAllowedValues(new OwsValue(AqdConstants.NS_AQD))));
    }

    private boolean isGetObservation(OwsOperation operation) {
        return operation.getName().equals(SosConstants.Operations.GetObservation.name());
    }

    private boolean isGetCapabilities(OwsOperation operation) {
        return operation.getName().equals(SosConstants.Operations.GetCapabilities.name());
    }

    private boolean isAcceptVersions(OwsDomain d) {
        return d.getName().equals(OWSConstants.GetCapabilitiesParams.AcceptVersions.name());
    }

    private boolean isResponseFormat(OwsDomain d) {
        return d.getName().equals(SosConstants.GetObservationParams.responseFormat.name());
    }

    private void modifyServiceIdentification(OwsServiceIdentification serviceIdentification) {
        serviceIdentification.setServiceTypeVersion(Arrays.asList(AqdConstants.VERSION));
    }

    private void modifyOperationsMetadata(OwsOperationsMetadata operationsMetadata) {

        modifyCommonParameters(operationsMetadata.getParameters());

        SortedSet<OwsOperation> operations = operationsMetadata.getOperations();
        operationsMetadata.setOperations(Stream.concat(
                operations.stream().filter(this::isGetCapabilities)
                        .map(Functions.mutate(this::setAcceptVersionsParameter)),
                operations.stream().filter(this::isGetObservation).map(Functions.mutate(this::setResponseFormat)))
                .map(Functions.mutate(this::removeJSONEndpoint)).map(Functions.mutate(this::addFlowParameter))
                .collect(Collectors.toList()));
    }

    private void modifyContents(Collection<SosObservationOffering> contents) {
        contents.forEach(this::modifyObservationOffering);
    }

    private void modifyObservationOffering(SosObservationOffering offering) {
        offering.setResponseFormats(Arrays.asList(AqdConstants.NS_AQD));
    }

    private void modifyCommonParameters(SortedSet<OwsDomain> parameters) {
        parameters.stream().filter(d -> d.getName().equals(OWSConstants.RequestParams.service.name()))
                .forEach(d -> d.setPossibleValues(new OwsAllowedValues(new OwsValue(AqdConstants.AQD))));

        parameters.stream().filter(d -> d.getName().equals(OWSConstants.RequestParams.version.name()))
                .forEach(d -> d.setPossibleValues(new OwsAllowedValues(new OwsValue(AqdConstants.VERSION))));
    }

}