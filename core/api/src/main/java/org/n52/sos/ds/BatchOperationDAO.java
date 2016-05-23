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
package org.n52.sos.ds;

import org.n52.sos.exception.ows.concrete.InvalidAcceptVersionsParameterException;
import org.n52.sos.exception.ows.concrete.InvalidServiceOrVersionException;
import org.n52.sos.exception.ows.concrete.InvalidServiceParameterException;
import org.n52.sos.exception.ows.concrete.MissingServiceParameterException;
import org.n52.sos.exception.ows.concrete.MissingVersionParameterException;
import org.n52.sos.exception.ows.concrete.VersionNotSupportedException;
import org.n52.sos.ogc.ows.CompositeOwsException;
import org.n52.sos.ogc.ows.OwsExceptionReport;
import org.n52.sos.ogc.ows.OwsOperation;
import org.n52.sos.ogc.sos.SosConstants;
import org.n52.sos.request.AbstractServiceRequest;
import org.n52.sos.request.BatchRequest;
import org.n52.sos.request.GetCapabilitiesRequest;
import org.n52.sos.response.BatchResponse;
import org.n52.sos.service.operator.ServiceOperator;
import org.n52.sos.service.operator.ServiceOperatorKey;
import org.n52.sos.service.operator.ServiceOperatorRepository;
import org.n52.sos.util.BatchConstants;

/**
 * TODO JavaDoc
 * 
 * @author Christian Autermann <c.autermann@52north.org>
 * 
 * @since 4.0.0
 */
public class BatchOperationDAO extends AbstractOperationDAO {
    public BatchOperationDAO() {
        super(SosConstants.SOS, BatchConstants.OPERATION_NAME);
    }

    public BatchResponse executeRequests(BatchRequest request) throws OwsExceptionReport {
        BatchResponse response = new BatchResponse();
        response.setService(request.getService());
        response.setVersion(request.getVersion());
        for (AbstractServiceRequest<?> r : request) {
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

    protected ServiceOperator getServiceOperator(AbstractServiceRequest<?> request) throws OwsExceptionReport {
        checkServiceOperatorKeys(request);
        for (ServiceOperatorKey sokt : request.getServiceOperatorKeyType()) {
            ServiceOperator so = ServiceOperatorRepository.getInstance().getServiceOperator(sokt);
            if (so != null) {
                return so;
            }
        }
        // no operator found
        if (request instanceof GetCapabilitiesRequest) {
            throw new InvalidAcceptVersionsParameterException(((GetCapabilitiesRequest) request).getAcceptVersions());
        } else {
            throw new InvalidServiceOrVersionException(request.getService(), request.getVersion());
        }
    }

    protected void checkServiceOperatorKeys(AbstractServiceRequest<?> request) throws OwsExceptionReport {
        CompositeOwsException exceptions = new CompositeOwsException();
        for (ServiceOperatorKey sokt : request.getServiceOperatorKeyType()) {
            checkService(sokt, exceptions);
            if (request instanceof GetCapabilitiesRequest) {
                checkAcceptVersions(request, exceptions);
            } else {
                checkVersion(sokt, exceptions);
            }
        }
        exceptions.throwIfNotEmpty();
    }

    protected boolean isVersionSupported(String service, String version) {
        return ServiceOperatorRepository.getInstance().isVersionSupported(service, version);
    }

    @Override
    protected void setOperationsMetadata(OwsOperation operation, String service, String version)
            throws OwsExceptionReport {
        /* nothing to do here */
    }

    private void checkAcceptVersions(AbstractServiceRequest<?> request, CompositeOwsException exceptions) {
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

    private void checkVersion(ServiceOperatorKey sokt, CompositeOwsException exceptions) {
        if (sokt.hasVersion()) {
            if (sokt.getVersion().isEmpty()) {
                exceptions.add(new MissingVersionParameterException());
            } else if (!isVersionSupported(sokt.getService(), sokt.getVersion())) {
                exceptions.add(new VersionNotSupportedException());
            }
        }
    }

    private void checkService(ServiceOperatorKey sokt, CompositeOwsException exceptions) {
        if (sokt.hasService()) {
            if (sokt.getService().isEmpty()) {
                exceptions.add(new MissingServiceParameterException());
            } else if (!ServiceOperatorRepository.getInstance().isServiceSupported(sokt.getService())) {
                exceptions.add(new InvalidServiceParameterException(sokt.getService()));
            }
        }
    }
    
    @Override
    public String getDatasourceDaoIdentifier() {
        return IDEPENDET_IDENTIFIER;
    }
}
