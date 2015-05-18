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
package org.n52.iceland.service.operator;

import org.n52.iceland.util.Comparables;

import com.google.common.base.Objects;

/**
 * @since 4.0.0
 * 
 */
public class ServiceOperatorKey implements Comparable<ServiceOperatorKey> {
    private final String service;

    private final String version;

    public ServiceOperatorKey(String service, String version) {
        this.service = service;
        this.version = version;
    }

    public String getService() {
        return service;
    }

    public boolean hasService() {
        return getService() != null;
    }

    public String getVersion() {
        return version;
    }

    public boolean hasVersion() {
        return getVersion() != null;
    }

    @Override
    public int compareTo(ServiceOperatorKey other) {
        return Comparables.chain(other).compare(getService(), other.getService())
                .compare(getVersion(), other.getVersion()).result();
    }

    @Override
    public boolean equals(Object o) {
        if (o != null && o.getClass() == getClass()) {
            ServiceOperatorKey other = (ServiceOperatorKey) o;
            return Objects.equal(getService(), other.getService()) && Objects.equal(getVersion(), other.getVersion());
        }
        return false;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getService(), getVersion());
    }

    @Override
    public String toString() {
        return String.format("ServiceOperatorKeyType[service=%s, version=%s]", getService(), getVersion());
    }
}
