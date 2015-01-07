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
package org.n52.sos.ogc;

import org.n52.sos.service.operator.ServiceOperatorKey;

import com.google.common.base.Objects;
import com.google.common.base.Preconditions;
import com.google.common.collect.ComparisonChain;

/**
 * Abstract class for comparable keys with parameters service, version and
 * domain
 * 
 * @author Carsten Hollmann <c.hollmann@52north.org>
 * @since 4.1.0
 * 
 * @param <T>
 *            implementation of this class
 */
public abstract class AbstractComparableServiceVersionDomainKey<T extends AbstractComparableServiceVersionDomainKey<T>>
        implements Comparable<T> {
    private ServiceOperatorKey sok;

    private String domain;

    /**
     * constructor
     * 
     * @param sok
     *            the {@link ServiceOperatorKey} to set
     * @param domain
     *            the domain to set
     */
    public AbstractComparableServiceVersionDomainKey(ServiceOperatorKey sok, String domain) {
        setServiceOperatorKey(sok);
        setDomain(domain);
    }

    /**
     * constructor
     * 
     * @param service
     *            the service to set
     * @param version
     *            the version to set
     * @param domain
     *            the domain to set
     */
    public AbstractComparableServiceVersionDomainKey(String service, String version, String domain) {
        this(new ServiceOperatorKey(service, version), domain);
    }

    /**
     * Set the {@link ServiceOperatorKey} to set
     * 
     * @param sok
     *            the {@link ServiceOperatorKey} to set
     */
    private void setServiceOperatorKey(ServiceOperatorKey sok) {
        this.sok = sok;
    }

    /**
     * Get the {@link ServiceOperatorKey}
     * 
     * @return the {@link ServiceOperatorKey}
     */
    public ServiceOperatorKey getServiceOperatorKey() {
        return sok;
    }

    /**
     * Get the service
     * 
     * @return the service
     */
    public String getService() {
        return sok == null ? null : sok.getService();
    }

    /**
     * Get the version
     * 
     * @return the version
     */
    public String getVersion() {
        return sok == null ? null : sok.getVersion();
    }

    /**
     * Get the domain
     * 
     * @return the domain
     */
    public String getDomain() {
        return domain;
    }

    /**
     * Set the domain
     * 
     * @param domain
     *            the domain to set
     */
    private void setDomain(String domain) {
        this.domain = domain;
    }

    @Override
    public int compareTo(T o) {
        Preconditions.checkNotNull(o);
        return ComparisonChain.start().compare(getServiceOperatorKey(), o.getServiceOperatorKey())
                .compare(getDomain(), o.getDomain()).result();
    }

    @SuppressWarnings("unchecked")
    @Override
    public boolean equals(Object obj) {
        if (obj != null && obj.getClass() == getClass()) {
            T o = (T) obj;
            return Objects.equal(getServiceOperatorKey(), o.getServiceOperatorKey())
                    && Objects.equal(getDomain(), o.getDomain());
        }
        return false;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getServiceOperatorKey(), getDomain());
    }

    @Override
    public String toString() {
        return String.format("%s[serviceOperatorKeyType=%s, domain=%s]", getClass().getSimpleName(),
                getServiceOperatorKey(), getDomain());
    }
}
