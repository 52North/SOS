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
package org.n52.iceland.encode;

import org.n52.iceland.service.operator.ServiceOperatorKey;

import com.google.common.base.Objects;

/**
 * @author Christian Autermann <c.autermann@52north.org>
 * 
 * @since 4.0.0
 */
public class ResponseFormatKey {
    private ServiceOperatorKey serviceOperatorKeyType;

    private String responseFormat;

    public ResponseFormatKey(ServiceOperatorKey serviceOperatorKeyType, String responseFormat) {
        this.serviceOperatorKeyType = serviceOperatorKeyType;
        this.responseFormat = responseFormat;
    }

    public ResponseFormatKey() {
        this(null, null);
    }

    public ServiceOperatorKey getServiceOperatorKeyType() {
        return serviceOperatorKeyType;
    }

    public void setServiceOperatorKeyType(ServiceOperatorKey serviceOperatorKeyType) {
        this.serviceOperatorKeyType = serviceOperatorKeyType;
    }

    public String getResponseFormat() {
        return responseFormat;
    }

    public void setResponseFormat(String responseFormat) {
        this.responseFormat = responseFormat;
    }

    public String getService() {
        return getServiceOperatorKeyType() != null ? getServiceOperatorKeyType().getService() : null;
    }

    public String getVersion() {
        return getServiceOperatorKeyType() != null ? getServiceOperatorKeyType().getVersion() : null;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getServiceOperatorKeyType(), getResponseFormat());
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof ResponseFormatKey) {
            ResponseFormatKey o = (ResponseFormatKey) obj;
            return Objects.equal(getServiceOperatorKeyType(), o.getServiceOperatorKeyType())
                    && Objects.equal(getResponseFormat(), o.getResponseFormat());
        }
        return false;
    }

    @Override
    public String toString() {
        return String.format("%s[serviceOperatorKeyType=%s, responseFormat=%s]", getClass().getSimpleName(),
                getServiceOperatorKeyType(), getResponseFormat());
    }
}
