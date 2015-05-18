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
package org.n52.iceland.ogc;

import org.n52.iceland.service.operator.ServiceOperatorKey;

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
