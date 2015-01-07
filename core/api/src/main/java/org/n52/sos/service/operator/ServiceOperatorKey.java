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
package org.n52.sos.service.operator;

import org.n52.sos.util.Comparables;

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
