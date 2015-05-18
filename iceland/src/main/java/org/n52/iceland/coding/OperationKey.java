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
package org.n52.iceland.coding;

import org.n52.iceland.util.Comparables;

import com.google.common.base.MoreObjects;
import com.google.common.base.Objects;

/**
 * TODO JavaDoc
 * 
 * @author Christian Autermann <c.autermann@52north.org>
 * 
 * @since 4.0.0
 */
public class OperationKey implements Comparable<OperationKey> {
    private final String service;

    private final String version;

    private final String operation;

    public OperationKey(String service, String version, String operation) {
        this.service = service;
        this.version = version;
        this.operation = operation;
    }

    public OperationKey(String service, String version, Enum<?> operation) {
        this(service, version, operation.name());
    }

    public OperationKey(OperationKey key) {
        this(key.getService(), key.getVersion(), key.getOperation());
    }

    public String getService() {
        return service;
    }

    public String getVersion() {
        return version;
    }

    public String getOperation() {
        return operation;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj != null && getClass() == obj.getClass()) {
            final OperationKey o = (OperationKey) obj;
            return Objects.equal(getService(), o.getService()) && Objects.equal(getVersion(), o.getVersion())
                    && Objects.equal(getOperation(), o.getOperation());
        }
        return false;
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(getClass()).add("service", getService()).add("version", getVersion())
                .add("operation", getOperation()).toString();
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getClass().getName(), getService(), getVersion(), getOperation());
    }

    public int getSimilarity(OperationKey key) {
        return this.equals(key) ? 0 : -1;
    }

    @Override
    public int compareTo(OperationKey other) {
        return Comparables.chain(other).compare(getService(), other.getService())
                .compare(getVersion(), other.getVersion()).compare(getOperation(), other.getOperation()).result();
    }
}
