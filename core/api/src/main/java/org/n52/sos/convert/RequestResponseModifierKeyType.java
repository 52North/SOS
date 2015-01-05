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
package org.n52.sos.convert;

import java.util.Set;

import org.n52.sos.request.AbstractServiceRequest;
import org.n52.sos.response.AbstractServiceResponse;
import org.n52.sos.service.AbstractServiceCommunicationObject;
import org.n52.sos.util.Constants;
import org.n52.sos.util.StringHelper;

import com.google.common.base.Objects;
import com.google.common.collect.Sets;

public class RequestResponseModifierKeyType implements Comparable<RequestResponseModifierKeyType> {

    private String service = Constants.EMPTY_STRING;

    private String version = Constants.EMPTY_STRING;

    private AbstractServiceRequest<?> request;

    private AbstractServiceResponse response;

    public RequestResponseModifierKeyType(String service, String version, AbstractServiceRequest<?> request) {
         this(service, version, request, null);
    }

    public RequestResponseModifierKeyType(String service, String version, AbstractServiceRequest<?> request,
            AbstractServiceResponse response) {
        super();
        setService(service);
        setVersion(version);
        setRequest(request);
        setResponse(response);
    }


    /**
     * @return the service
     */
    public String getService() {
        return this.service;
    }

    private void setService(String service) {
        this.service = service;
    }

    /**
     * @return the version
     */
    public String getVersion() {
        return this.version;
    }

    private void setVersion(String version) {
        this.version = version;
    }

    public boolean isSetService() {
        return StringHelper.isNotEmpty(getService());
    }

    public boolean isSetVersion() {
        return StringHelper.isNotEmpty(getVersion());
    }

    /**
     * @return the request
     */
    public AbstractServiceRequest<?> getRequest() {
        return request;
    }

    /**
     * @param request
     *            the request to set
     */
    private void setRequest(AbstractServiceRequest<?> request) {
        this.request = request;
    }

    public boolean isSetRequest() {
        return getRequest() != null;
    }

    /**
     * @return the response
     */
    public AbstractServiceResponse getResponse() {
        return response;
    }

    /**
     * @param response
     *            the response to set
     */
    private void setResponse(AbstractServiceResponse response) {
        this.response = response;
    }

    public boolean isSetResponse() {
        return getResponse() != null;
    }

    @Override
    public String toString() {
        return String.format("%s[service=%s, service=%s, request=%s, response=%s]", getClass().getSimpleName(),
                getService(), getVersion(), isSetRequest() ? getRequest().getClass().getSimpleName()
                        : Constants.EMPTY_STRING, isSetResponse() ? getResponse().getClass().getSimpleName()
                        : Constants.EMPTY_STRING);
    }

    @Override
    public int compareTo(RequestResponseModifierKeyType o) {
        if (o instanceof RequestResponseModifierKeyType) {
            if (checkCompareToParameter(getService(), o.getService())
                    && checkCompareToParameter(getVersion(), o.getVersion())
                    && checkParameter(getRequest(), o.getRequest()) && checkParameter(getResponse(), o.getResponse())) {
                return 0;
            }
            return 1;
        }
        return -1;
    }

    private boolean checkCompareToParameter(String localParameter, String parameterToCheck) {
        if (localParameter == null || (localParameter == null && parameterToCheck == null)) {
            return true;
        }
        return localParameter != null && parameterToCheck != null && localParameter.equals(parameterToCheck);
    }

    private boolean checkParameter(AbstractServiceCommunicationObject object,
            AbstractServiceCommunicationObject objectToCheck) {
        if (object == null && objectToCheck == null) {
            return true;
        }
        return object != null && objectToCheck != null && object.getClass() == objectToCheck.getClass();
    }

    @Override
    public boolean equals(Object o) {
        if (o != null && o.getClass() == getClass()) {
            RequestResponseModifierKeyType other = (RequestResponseModifierKeyType) o;
            Set<Boolean> equal = Sets.newHashSet();
            if (isSetService()) {
                equal.add(Objects.equal(getService(), other.getService()));
            }
            if (isSetVersion()) {
                equal.add(Objects.equal(getVersion(), other.getVersion()));
            }
            equal.add(checkParameter(getRequest(), other.getRequest()));
            equal.add(checkResponseForEquals(other.getResponse()));
            if (equal.size() == 1) {
                return equal.iterator().next();
            }
        }
        return false;
    }

    private boolean checkResponseForEquals(AbstractServiceResponse toCheck) {
        if (toCheck != null && getRequest() != null) {
            return getResponse().getClass() == toCheck.getClass();
        }
        return true;
    }

    @Override
    public int hashCode() {
        int hashCode = Objects.hashCode(3, 79, getService(), getVersion(), getRequest().getClass());
        if (isSetResponse()) {
            return Objects.hashCode(hashCode, getResponse().getClass());
        }
        return hashCode;
    }

}
