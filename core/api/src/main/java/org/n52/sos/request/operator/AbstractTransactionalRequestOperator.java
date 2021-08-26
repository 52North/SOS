/*
 * Copyright (C) 2012-2021 52°North Spatial Information Research GmbH
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


import javax.inject.Inject;

import org.n52.faroe.ConfigurationError;
import org.n52.iceland.request.handler.OperationHandler;
import org.n52.shetland.ogc.ows.exception.NoApplicableCodeException;
import org.n52.shetland.ogc.ows.exception.OwsExceptionReport;
import org.n52.shetland.ogc.ows.service.OwsServiceRequest;
import org.n52.shetland.ogc.ows.service.OwsServiceResponse;
import org.n52.sos.service.TransactionalSecurityConfiguration;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

/**
 * @since 4.0.0
 *
 * @param <D> an implementation of {@link OperationHandler}
 * @param <Q> an implementation of {@link OwsServiceRequest}
 * @param <A> an implementation of {@link OwsServiceResponse}
 */
@SuppressFBWarnings({"EI_EXPOSE_REP", "EI_EXPOSE_REP2"})
public abstract class AbstractTransactionalRequestOperator<D extends OperationHandler,
                                                            Q extends OwsServiceRequest,
                                                            A extends OwsServiceResponse>
        extends AbstractRequestOperator<D, Q, A> {

    public static final String ADD_OBSERVATION_IDENTIFIER_FOR_STA = "service.addObservationIdentifierForSTA";

    private static final boolean TRANSACTIONAL_ACTIVATION_STATE = false;

    private TransactionalSecurityConfiguration transactionalSecurityConfiguration;

    public AbstractTransactionalRequestOperator(String service, String version, String operationName,
                                                Class<Q> requestType) {
        super(service, version, operationName, TRANSACTIONAL_ACTIVATION_STATE, requestType);
    }

    public AbstractTransactionalRequestOperator(String service, String version, Enum<?> operationName,
                                                Class<Q> requestType) {
        this(service, version, operationName.name(), requestType);
    }

    @Inject
    public void setTransactionalSecurityConfiguration(TransactionalSecurityConfiguration config) {
        this.transactionalSecurityConfiguration = config;
    }

    public TransactionalSecurityConfiguration getTransactionalSecurityConfiguration() {
        return transactionalSecurityConfiguration;
    }

    @Override
    public OwsServiceResponse receiveRequest(OwsServiceRequest request)
            throws OwsExceptionReport {
        try {
            new TransactionalRequestChecker(getTransactionalSecurityConfiguration())
                    .check(request.getRequestContext());
        } catch (ConfigurationError ce) {
            throw new NoApplicableCodeException().causedBy(ce);
        }
        return super.receiveRequest(request);
    }

}
