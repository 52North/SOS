/*
 * Copyright (C) 2012-2021 52°North Initiative for Geospatial Open Source
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
package org.n52.sos.service;

import org.n52.faroe.ConfigurationError;
import org.n52.faroe.annotation.Configurable;
import org.n52.faroe.annotation.Setting;
import org.n52.janmayen.net.IPAddress;
import org.n52.janmayen.net.IPAddressRange;
import org.n52.shetland.util.CollectionHelper;

import com.google.common.base.Strings;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.ImmutableSet.Builder;

/**
 * @author <a href="mailto:shane@axiomalaska.com">Shane StClair</a>
 *
 * @since 4.0.0
 */
@Configurable
public class TransactionalSecurityConfiguration implements TransactionalSecuritySettings {

    private boolean transactionalActive;

    /**
     * List of CIDR encoded or raw IP ranges allowed to make transactional
     * requests
     */
    private ImmutableSet<IPAddressRange> transactionalAllowedIps = ImmutableSet.of();

    /**
     * Authorization token required for transactional requests
     */
    private String transactionalToken;

    private ImmutableSet<IPAddress> allowedProxies =  ImmutableSet.of();

    /**
     * @return the transactionalActive
     */
    public boolean isTransactionalActive() {
        return transactionalActive;
    }

    /**
     * @param transactionalActive
     *            the transactionalActive to set
     */
    @Setting(TRANSACTIONAL_ACTIVE)
    public void setTransactionalActive(final boolean transactionalActive) {
        this.transactionalActive = transactionalActive;
    }

    /**
     * @return List of CIDR encoded or raw IP ranges allowed to make
     *         transactional requests
     */
    public ImmutableSet<IPAddressRange> getAllowedAddresses() {
        return transactionalAllowedIps;
    }

    @Setting(TRANSACTIONAL_ALLOWED_IPS)
    public void setTransactionalAllowedIps(final String txAllowedIps) throws ConfigurationError {
        if (!Strings.isNullOrEmpty(txAllowedIps)) {
            Builder<IPAddressRange> builder = ImmutableSet.builder();
            for (final String splitted : txAllowedIps.split(",")) {
                String trimmed = splitted.trim();
                try {
                    builder.add(new IPAddressRange(trimmed));
                } catch (final IllegalArgumentException e) {
                    throw new ConfigurationError(
                            "Transactional allowed address is not a valid CIDR range or IP address", e);
                }
            }
            transactionalAllowedIps = builder.build();
        } else {
            transactionalAllowedIps = ImmutableSet.of();
        }
    }

    @Setting(ALLOWED_PROXIES)
    public void setAllowedProxies(final String proxies) {
        if (!Strings.isNullOrEmpty(proxies)) {
            final Builder<IPAddress> builder = ImmutableSet.builder();
            for (final String splitted : proxies.split(",")) {
                try {
                    builder.add(new IPAddress(splitted.trim()));
                } catch (final IllegalArgumentException e) {
                    throw new ConfigurationError(
                            "Allowed proxy address is not a valid IP address", e);
                }
            }
            allowedProxies = builder.build();
        } else {
            allowedProxies = ImmutableSet.of(new IPAddress("127.0.0.1"));
        }
    }

    public ImmutableSet<IPAddress> getAllowedProxies() {
        return allowedProxies;
    }

    /**
     * @return Authorization token for transactional requests
     */
    public String getTransactionalToken() {
        return transactionalToken;
    }

    @Setting(TRANSACTIONAL_TOKEN)
    public void setTransactionalToken(final String txToken) {
        transactionalToken = txToken;
    }

    /**
     * @return true if allowed IPs or token is defined
     */
    public boolean isSetTransactionalSecurityActive() {
        return transactionalActive;
    }

    /**
     * @return true if allowed IPs defined
     */
    public boolean isSetTransactionalAllowedIps() {
        return CollectionHelper.isNotEmpty(getAllowedAddresses());
    }

    /**
     * @return true if token is defined
     */
    public boolean isSetTransactionalToken() {
        return !Strings.isNullOrEmpty(getTransactionalToken());
    }

}
