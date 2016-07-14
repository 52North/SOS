/**
 * Copyright (C) 2012-2016 52°North Initiative for Geospatial Open Source
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
package org.n52.sos.coding;

import org.n52.sos.util.Comparables;

import com.google.common.base.Objects;

public class VersionedOperationKey implements Comparable<VersionedOperationKey> {

    private final String service;

    private final String version;

    private final String operation;
    
    private final String operationVersion;

    public VersionedOperationKey(String service, String version, String operation, String operationVersion) {
        this.service = service;
        this.version = version;
        this.operation = operation;
        this.operationVersion = operationVersion;
    }

    public VersionedOperationKey(String service, String version, Enum<?> operation, String operationVersion) {
        this(service, version, operation.name(), operationVersion);
    }

    public VersionedOperationKey(OperationKey key, String operationVersion) {
        this(key.getService(), key.getVersion(), key.getOperation(), operationVersion);
    }
    
    public VersionedOperationKey(VersionedOperationKey key) {
        this(key.getService(), key.getVersion(), key.getOperation(), key.getOperationVersion());
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

    public String getOperationVersion() {
        return operationVersion;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj != null && getClass() == obj.getClass()) {
            final VersionedOperationKey o = (VersionedOperationKey) obj;
            return Objects.equal(getService(), o.getService()) 
                    && Objects.equal(getVersion(), o.getVersion())
                    && Objects.equal(getOperation(), o.getOperation())
                    && Objects.equal(getOperationVersion(), o.getOperationVersion());
        }
        return false;
    }

    @Override
    public String toString() {
        return Objects.toStringHelper(getClass()).add("service", getService()).add("version", getVersion())
                .add("operation", getOperation()).add("operationVersion", getOperationVersion()).toString();
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getClass().getName(), getService(), getVersion(), getOperation(), getOperationVersion());
    }

    public int getSimilarity(OperationKey key) {
        return this.equals(key) ? 0 : -1;
    }

    @Override
    public int compareTo(VersionedOperationKey other) {
        return Comparables.chain(other)
                .compare(getService(), other.getService())
                .compare(getVersion(), other.getVersion())
                .compare(getOperation(), other.getOperation())
                .compare(getOperationVersion(), other.getOperationVersion()).result();
    }
}
