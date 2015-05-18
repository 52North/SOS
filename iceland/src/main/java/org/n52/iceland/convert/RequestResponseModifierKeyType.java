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
package org.n52.iceland.convert;

import java.util.Set;

import org.n52.iceland.request.AbstractServiceRequest;
import org.n52.iceland.response.AbstractServiceResponse;
import org.n52.iceland.service.AbstractServiceCommunicationObject;
import org.n52.iceland.util.Constants;
import org.n52.iceland.util.StringHelper;

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
