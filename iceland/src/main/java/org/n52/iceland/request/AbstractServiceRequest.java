/**
 * Copyright 2015 52°North Initiative for Geospatial Open Source
 * Software GmbH
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.n52.iceland.request;

import java.util.Collections;
import java.util.List;

import org.n52.iceland.exception.ows.concrete.MissingServiceParameterException;
import org.n52.iceland.exception.ows.concrete.MissingVersionParameterException;
import org.n52.iceland.ogc.ows.OWSConstants;
import org.n52.iceland.ogc.ows.OwsExceptionReport;
import org.n52.iceland.ogc.swe.simpleType.SweText;
import org.n52.iceland.ogc.swes.SwesConstants.HasSwesExtension;
import org.n52.iceland.ogc.swes.SwesExtension;
import org.n52.iceland.ogc.swes.SwesExtensions;
import org.n52.iceland.response.AbstractServiceResponse;
import org.n52.iceland.service.AbstractServiceCommunicationObject;
import org.n52.iceland.service.operator.ServiceOperatorKey;
import org.n52.iceland.util.Constants;
import org.n52.iceland.util.StringHelper;

/**
 * abstract super class for all service request classes
 * 
 * @since 4.0.0
 * 
 */
@SuppressWarnings("rawtypes")
public abstract class AbstractServiceRequest<T extends AbstractServiceResponse> extends AbstractServiceCommunicationObject implements HasSwesExtension<AbstractServiceRequest> {
    private List<ServiceOperatorKey> serviceOperatorKeyTypes;

    private RequestContext requestContext;
    
    private SwesExtensions extensions;

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
    
    
    public boolean isSetRequestedLanguage() {
        return StringHelper.isNotEmpty(getRequestedLanguage());
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

    @Override
    public String toString() {
        return String.format("%s[service=%s, version=%s, operation=%s]", getClass().getName(), getService(),
                getVersion(), getOperationName());
    }
}
