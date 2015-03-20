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
package org.n52.sos.request;

import java.util.Collections;
import java.util.List;

import org.n52.sos.exception.ows.concrete.MissingServiceParameterException;
import org.n52.sos.exception.ows.concrete.MissingVersionParameterException;
import org.n52.sos.ogc.ows.OWSConstants;
import org.n52.sos.ogc.ows.OwsExceptionReport;
import org.n52.sos.ogc.swe.simpleType.SweText;
import org.n52.sos.ogc.swes.SwesConstants.HasSwesExtension;
import org.n52.sos.ogc.swes.SwesExtension;
import org.n52.sos.ogc.swes.SwesExtensions;
import org.n52.sos.response.AbstractServiceResponse;
import org.n52.sos.service.AbstractServiceCommunicationObject;
import org.n52.sos.service.operator.ServiceOperatorKey;
import org.n52.sos.util.Constants;
import org.n52.sos.util.StringHelper;

/**
 * abstract super class for all service request classes
 * 
 * @since 4.0.0
 * 
 */
@SuppressWarnings("rawtypes")
public abstract class AbstractServiceRequest<T extends AbstractServiceResponse> extends AbstractServiceCommunicationObject implements HasSwesExtension<AbstractServiceRequest> {
    private List<ServiceOperatorKey> serviceOperatorKeyTypes;

    private SwesExtensions extensions;

    private RequestContext requestContext;

    public List<ServiceOperatorKey> getServiceOperatorKeyType() throws OwsExceptionReport {
        if (serviceOperatorKeyTypes == null) {
            checkServiceAndVersionParameter();
            serviceOperatorKeyTypes = Collections.singletonList(new ServiceOperatorKey(getService(), getVersion()));
        }
        return Collections.unmodifiableList(serviceOperatorKeyTypes);
    }

    private void checkServiceAndVersionParameter() throws OwsExceptionReport {
        if (!isSetService()) {
            throw new MissingServiceParameterException();
        }
        if (!isSetVersion()) {
            throw new MissingVersionParameterException();
        }
    }

    @Override
    public SwesExtensions getExtensions() {
        return extensions;
    }

    @Override
    public AbstractServiceRequest setExtensions(final SwesExtensions extensions) {
        this.extensions = extensions;
        return this;
    }
    
    @Override
    public AbstractServiceRequest addExtensions(final SwesExtensions extensions) {
        if (getExtensions() == null) {
            setExtensions(extensions);
        } else {
            getExtensions().addSwesExtension(extensions.getExtensions());
        }
        return this;
    }

    @Override
    public AbstractServiceRequest addExtension(final SwesExtension extension) {
        if (getExtensions() == null) {
            setExtensions(new SwesExtensions());
        }
        getExtensions().addSwesExtension(extension);
        return this;
    }

    @Override
    public boolean isSetExtensions() {
        return extensions != null && !extensions.isEmpty();
    }

    public String getRequestedLanguage() {
        if (isSetExtensions()) {
            if (getExtensions().containsExtension(OWSConstants.AdditionalRequestParams.language)) {
                Object value = getExtensions().getExtension(OWSConstants.AdditionalRequestParams.language).getValue();
                if (value instanceof SweText) {
                    return ((SweText) value).getValue();
                } else if (value instanceof Integer) {
                    return (String) getExtensions().getExtension(OWSConstants.AdditionalRequestParams.language).getValue();
                }
            }
        }
        return Constants.EMPTY_STRING;
    }
    
    public boolean isSetRequestedLanguage() {
        return StringHelper.isNotEmpty(getRequestedLanguage());
    }

    public RequestContext getRequestContext() {
        return requestContext;
    }

    public AbstractServiceRequest setRequestContext(final RequestContext requestContext) {
        this.requestContext = requestContext;
        return this;
    }

    public boolean isSetRequestContext() {
        return requestContext != null;
    }
    
    public abstract T getResponse() throws OwsExceptionReport;

    @Override
    public String toString() {
        return String.format("%s[service=%s, version=%s, operation=%s]", getClass().getName(), getService(),
                getVersion(), getOperationName());
    }
}
