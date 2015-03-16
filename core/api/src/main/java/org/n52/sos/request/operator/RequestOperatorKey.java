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

import org.n52.sos.service.operator.ServiceOperatorKey;

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
