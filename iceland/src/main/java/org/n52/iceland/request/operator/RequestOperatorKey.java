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
package org.n52.iceland.request.operator;

import org.n52.iceland.service.operator.ServiceOperatorKey;

import com.google.common.base.Objects;
import com.google.common.base.Preconditions;
import com.google.common.collect.ComparisonChain;

/**
 * @since 4.0.0
 * 
 */
public class RequestOperatorKey implements Comparable<RequestOperatorKey> {
    private final ServiceOperatorKey sok;

    private final String operationName;
    
    private final boolean defaultActive;

    public RequestOperatorKey(ServiceOperatorKey sok, String operationName) {
        this(sok, operationName, true);
    }
    
    public RequestOperatorKey(ServiceOperatorKey sok, String operationName, boolean defaultActive) {
        this.sok = sok;
        this.operationName = operationName;
        this.defaultActive = defaultActive;
    }

    public RequestOperatorKey(String service, String version, String operationName) {
        this(new ServiceOperatorKey(service, version), operationName, true);
    }
    
    public RequestOperatorKey(String service, String version, String operationName, boolean defaultActive) {
        this(new ServiceOperatorKey(service, version), operationName, defaultActive);
    }

    public ServiceOperatorKey getServiceOperatorKey() {
        return sok;
    }

    public String getService() {
        return sok == null ? null : sok.getService();
    }

    public String getVersion() {
        return sok == null ? null : sok.getVersion();
    }

    public String getOperationName() {
        return operationName;
    }
    
    /**
     * @return the defaultActive
     */
    public boolean isDefaultActive() {
        return defaultActive;
    }

    @Override
    public int compareTo(RequestOperatorKey o) {
        Preconditions.checkNotNull(o);
        return ComparisonChain.start().compare(getServiceOperatorKey(), o.getServiceOperatorKey())
                .compare(getOperationName(), o.getOperationName()).result();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj != null && obj.getClass() == getClass()) {
            RequestOperatorKey o = (RequestOperatorKey) obj;
            return Objects.equal(getServiceOperatorKey(), o.getServiceOperatorKey())
                    && Objects.equal(getOperationName(), o.getOperationName());
        }
        return false;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getServiceOperatorKey(), getOperationName());
    }

    @Override
    public String toString() {
        return String.format("%s[serviceOperatorKeyType=%s, operationName=%s, defaultActive=%b]", getClass().getSimpleName(),
                getServiceOperatorKey(), getOperationName(), isDefaultActive());
    }
}
