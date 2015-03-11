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
package org.n52.sos.util.net;

import java.util.List;

import org.n52.sos.util.Constants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Objects;
import com.google.common.base.Optional;
import com.google.common.base.Preconditions;
import com.google.common.base.Strings;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;

/**
 * Representation of a proxy chain as found in HTTP {@code X-Forwarded-For}
 * header.
 *
 * @author Christian Autermann <c.autermann@52north.org>
 */
public class ProxyChain {
    private static final Logger LOG = LoggerFactory.getLogger(ProxyChain.class);
    private final ImmutableList<IPAddress> proxies;
    private final IPAddress origin;

    /**
     * Creates a new chain from a origin (the original client) and all
     * intermediate proxies.
     *
     * @param origin  the origin
     * @param proxies the proxies
     */
    public ProxyChain(IPAddress origin, List<IPAddress> proxies) {
        Preconditions.checkArgument(origin != null && proxies != null);
        this.proxies = ImmutableList.copyOf(proxies);
        this.origin = origin;
    }

    /**
     * Creates a new chain from a list of addresses as found in the
     * {@code X-Forwarded-For} header. The list has to have at least one member.
     *
     * @param chain the chain
     */
    public ProxyChain(List<IPAddress> chain) {
        Preconditions.checkArgument(chain != null && !chain.isEmpty());
        this.origin = chain.get(0);
        this.proxies = ImmutableList.copyOf(chain.subList(1, chain.size()));
    }

    /**
     * Get the origin of the request (the clients address).
     *
     * @return the origin
     */
    public IPAddress getOrigin() {
        return origin;
    }

    /**
     * Get a list of all intermediate proxy servers.
     *
     * @return the proxies
     */
    public ImmutableList<IPAddress> getProxies() {
        return proxies;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getOrigin(), getProxies());
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof ProxyChain) {
            ProxyChain other = (ProxyChain) obj;
            return Objects.equal(getOrigin(), other.getOrigin()) &&
                   Objects.equal(getProxies(), other.getProxies());

        }
        return false;
    }

    @Override
    public String toString() {
        return Objects.toStringHelper(this).omitNullValues()
                .add("origin", getOrigin())
                .add("proxies", getProxies())
                .toString();
    }

    /**
     * Creates a Proxy chain from the {@code X-Forwarded-For} HTTP header.
     *
     * @param header the {@code X-Forwarded-For} header
     *
     * @return a {@code ProxyChain} if the header is present, non empty and well
     *         formed.
     */
    public static Optional<ProxyChain> fromForwardedForHeader(String header) {
        try {
            if (Strings.emptyToNull(header) != null) {
                String[] split = header.split(",");
                List<IPAddress> chain = Lists
                        .newArrayListWithExpectedSize(split.length);
                for (String splitted : split) {
                    chain.add(getIPAddress(splitted));
                }
                return Optional.of(new ProxyChain(chain));
            }
        } catch (IllegalArgumentException e) {
            LOG.warn("Ignoring invalid IP address in X-Forwared-For header: " +
                     header, e);
        }
        return Optional.absent();
    }
    
    @VisibleForTesting
    static IPAddress getIPAddress(String address) {
        return new IPAddress(address.split(Constants.COLON_STRING)[0].trim());
    }
}
