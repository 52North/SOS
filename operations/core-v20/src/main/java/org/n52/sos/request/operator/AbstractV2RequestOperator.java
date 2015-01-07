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

import java.util.Map;

import org.n52.sos.ds.OperationDAO;
import org.n52.sos.exception.ows.MissingParameterValueException;
import org.n52.sos.ogc.ows.CompositeOwsException;
import org.n52.sos.ogc.ows.OwsExceptionReport;
import org.n52.sos.ogc.sos.Sos1Constants;
import org.n52.sos.ogc.sos.Sos2Constants;
import org.n52.sos.ogc.sos.SosConstants;
import org.n52.sos.request.AbstractServiceRequest;
import org.n52.sos.request.DescribeSensorRequest;
import org.n52.sos.response.AbstractServiceResponse;
import org.n52.sos.util.SosHelper;

import com.google.common.base.Strings;

/**
 * @param <D>
 *            The OperationDAO implementation class
 * @param <Q>
 *            the request type
 * @param <A>
 *            the response type
 * 
 * @author Christian Autermann <c.autermann@52north.org>
 * @since 4.0.0
 */
public abstract class AbstractV2RequestOperator<D extends OperationDAO, Q extends AbstractServiceRequest<?>, A extends AbstractServiceResponse>
        extends AbstractRequestOperator<D, Q, A> implements WSDLAwareRequestOperator {
    public AbstractV2RequestOperator(String operationName, Class<Q> requestType) {
        super(SosConstants.SOS, Sos2Constants.SERVICEVERSION, operationName, requestType);
    }

    @Override
    public Map<String, String> getAdditionalSchemaImports() {
        return null;
    }

    @Override
    public Map<String, String> getAdditionalPrefixes() {
        return null;
    }
    
    protected void checkExtensions(final AbstractServiceRequest<?> request, final CompositeOwsException exceptions) {
        if (request.isSetExtensions()) {
            // currently nothing to check
        }
    }
}
