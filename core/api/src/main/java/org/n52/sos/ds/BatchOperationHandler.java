/*
 * Copyright (C) 2012-2021 52°North Initiative for Geospatial Open Source
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
package org.n52.sos.ds;

import java.util.Objects;
import java.util.Set;

import javax.inject.Inject;

import org.n52.iceland.exception.ows.concrete.InvalidAcceptVersionsParameterException;
import org.n52.iceland.exception.ows.concrete.InvalidServiceOrVersionException;
import org.n52.iceland.exception.ows.concrete.InvalidServiceParameterException;
import org.n52.iceland.exception.ows.concrete.VersionNotSupportedException;
import org.n52.iceland.service.operator.ServiceOperator;
import org.n52.iceland.service.operator.ServiceOperatorRepository;
import org.n52.janmayen.Comparables;
import org.n52.shetland.ogc.ows.exception.CompositeOwsException;
import org.n52.shetland.ogc.ows.exception.MissingServiceParameterException;
import org.n52.shetland.ogc.ows.exception.MissingVersionParameterException;
import org.n52.shetland.ogc.ows.exception.OwsExceptionReport;
import org.n52.shetland.ogc.ows.service.GetCapabilitiesRequest;
import org.n52.shetland.ogc.ows.service.OwsServiceKey;
import org.n52.shetland.ogc.ows.service.OwsServiceRequest;
import org.n52.shetland.ogc.sos.BatchConstants;
import org.n52.shetland.ogc.sos.SosConstants;
import org.n52.shetland.ogc.sos.request.BatchRequest;
import org.n52.shetland.ogc.sos.response.BatchResponse;

/**
 * TODO JavaDoc
 *
 * Renamed, in version 4.x called BatchOperationDAO
 *
 * @author <a href="mailto:c.autermann@52north.org">Christian Autermann</a>
 *
 * @since 5.0.0
 */
public class BatchOperationHandler extends AbstractSosOperationHandler {

    private ServiceOperatorRepository serviceOperatorRepository;

    public BatchOperationHandler() {
        super(SosConstants.SOS, BatchConstants.OPERATION_NAME);
    }

    @Inject
    public void setServiceOperatorRepository(ServiceOperatorRepository serviceOperatorRepository) {
        this.serviceOperatorRepository = serviceOperatorRepository;
    }

    public BatchResponse executeRequests(BatchRequest request) throws OwsExceptionReport {
        BatchResponse response = new BatchResponse();
        response.setService(request.getService());
        response.setVersion(request.getVersion());
        for (OwsServiceRequest r : request) {
            try {
                response.add(getServiceOperator(r).receiveRequest(r));
            } catch (OwsExceptionReport e) {
                response.add(e.setVersion(r.getVersion() != null ? r.getVersion() : request.getVersion()));
                if (request.isStopAtFailure()) {
                    break;
                }
            }
        }
        return response;
    }

    protected ServiceOperator getServiceOperator(OwsServiceRequest request) throws OwsExceptionReport {
        String service = request.getService();
        String version = request.getVersion();
        if (request instanceof GetCapabilitiesRequest) {
            GetCapabilitiesRequest gcr = (GetCapabilitiesRequest) request;
            if (gcr.isSetAcceptVersions()) {
                return gcr.getAcceptVersions().stream().map(v -> new OwsServiceKey(service, v))
                        .map(getServiceOperatorRepository()::getServiceOperator).filter(Objects::nonNull).findFirst()
                        .orElseThrow(() -> new InvalidServiceOrVersionException(service, version));
            } else {
                Set<String> supportedVersions = getServiceOperatorRepository().getSupportedVersions(service);
                String newest = supportedVersions.stream().max(Comparables.version())
                        .orElseThrow(() -> new InvalidServiceParameterException(service));
                return getServiceOperatorRepository().getServiceOperator(new OwsServiceKey(service, newest));
            }
        } else {
            return getServiceOperatorRepository().getServiceOperator(new OwsServiceKey(service, version));
        }
    }

    protected ServiceOperatorRepository getServiceOperatorRepository() {
        return this.serviceOperatorRepository;
    }

    protected void checkServiceOperatorKeys(OwsServiceRequest request) throws OwsExceptionReport {
        CompositeOwsException exceptions = new CompositeOwsException();
        OwsServiceKey sokt = new OwsServiceKey(request.getService(), request.getVersion());
        checkService(sokt, exceptions);
        if (request instanceof GetCapabilitiesRequest) {
            checkAcceptVersions(request, exceptions);
        } else {
            checkVersion(sokt, exceptions);
        }
        exceptions.throwIfNotEmpty();
    }

    protected boolean isVersionSupported(String service, String version) {
        return getServiceOperatorRepository().isVersionSupported(service, version);
    }

    private void checkAcceptVersions(OwsServiceRequest request, CompositeOwsException exceptions) {
        GetCapabilitiesRequest gcr = (GetCapabilitiesRequest) request;
        if (gcr.isSetAcceptVersions()) {
            boolean hasSupportedVersion = false;
            for (String version : gcr.getAcceptVersions()) {
                if (isVersionSupported(gcr.getService(), version)) {
                    hasSupportedVersion = true;
                }
            }
            if (!hasSupportedVersion) {
                exceptions.add(new InvalidAcceptVersionsParameterException(gcr.getAcceptVersions()));
            }
        }
    }

    private void checkVersion(OwsServiceKey sokt, CompositeOwsException exceptions) {
        if (sokt.hasVersion()) {
            if (sokt.getVersion().isEmpty()) {
                exceptions.add(new MissingVersionParameterException());
            } else if (!isVersionSupported(sokt.getService(), sokt.getVersion())) {
                exceptions.add(new VersionNotSupportedException());
            }
        }
    }

    private void checkService(OwsServiceKey sokt, CompositeOwsException exceptions) {
        if (sokt.hasService()) {
            if (sokt.getService().isEmpty()) {
                exceptions.add(new MissingServiceParameterException());
            } else if (!getServiceOperatorRepository().isServiceSupported(sokt.getService())) {
                exceptions.add(new InvalidServiceParameterException(sokt.getService()));
            }
        }
    }

    @Override
    public boolean isSupported() {
        return true;
    }
}
