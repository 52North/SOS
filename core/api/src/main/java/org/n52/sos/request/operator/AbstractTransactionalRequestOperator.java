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
package org.n52.sos.request.operator;


import org.n52.sos.ds.OperationDAO;
import org.n52.sos.exception.ConfigurationException;
import org.n52.sos.exception.ows.NoApplicableCodeException;
import org.n52.sos.ogc.ows.OwsExceptionReport;
import org.n52.sos.request.AbstractServiceRequest;
import org.n52.sos.response.AbstractServiceResponse;
import org.n52.sos.service.TransactionalSecurityConfiguration;

/**
 * @since 4.0.0
 *
 * @param <D>
 * @param <Q>
 * @param <A>
 */
public abstract class AbstractTransactionalRequestOperator<D extends OperationDAO, Q extends AbstractServiceRequest<?>, A extends AbstractServiceResponse>
        extends AbstractRequestOperator<D, Q, A> {
	
    private static final boolean TRANSACTIONAL_ACTIVATION_STATE = false;

	public AbstractTransactionalRequestOperator(String service,
                                                String version,
                                                String operationName,
                                                Class<Q> requestType) {
        super(service, version, operationName, TRANSACTIONAL_ACTIVATION_STATE, requestType);
    }

    @Override
    public AbstractServiceResponse receiveRequest(AbstractServiceRequest<?> request)
            throws OwsExceptionReport {
        try {
            new TransactionalRequestChecker(getConfig())
                    .check(request.getRequestContext());
        } catch (ConfigurationException ce) {
            throw new NoApplicableCodeException().causedBy(ce);
        }
        return super.receiveRequest(request);
    }

    private TransactionalSecurityConfiguration getConfig() {
        return TransactionalSecurityConfiguration.getInstance();
    }
}
