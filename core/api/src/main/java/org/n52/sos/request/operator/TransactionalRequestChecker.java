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

import java.util.Set;

import org.n52.sos.exception.ows.NoApplicableCodeException;
import org.n52.sos.ogc.ows.OwsExceptionReport;
import org.n52.sos.request.RequestContext;
import org.n52.sos.service.TransactionalSecurityConfiguration;
import org.n52.sos.util.http.HTTPStatus;
import org.n52.sos.util.net.IPAddress;
import org.n52.sos.util.net.IPAddressRange;
import org.n52.sos.util.net.ProxyChain;

import com.google.common.base.Predicate;
import com.google.common.base.Predicates;
import com.google.common.collect.ImmutableSet;

/**
 * TODO JavaDoc
 *
 * @author Christian Autermann <c.autermann@52north.org>
 */
public class TransactionalRequestChecker {
    private Predicate<RequestContext> predicate;

    @SuppressWarnings("unchecked")
    public TransactionalRequestChecker(TransactionalSecurityConfiguration config) {
        this.predicate = Predicates.and(createIpAdressPredicate(config),
                                        createTokenPredicate(config));
    }

    public void add(Predicate<RequestContext> p) {
        this.predicate = Predicates.and(this.predicate, p);
    }

    public void check(RequestContext rc) throws OwsExceptionReport {
        if (!predicate.apply(rc)) {
            throw new NoApplicableCodeException()
                    .withMessage("Not authorized for transactional operations!")
                    .setStatus(HTTPStatus.UNAUTHORIZED);

        }
    }

    private Predicate<RequestContext> createTokenPredicate(
            TransactionalSecurityConfiguration config) {
        if (!config.isTransactionalActive() ||
            !config.isSetTransactionalToken()) {
            return Predicates.alwaysTrue();
        } else {
            return new TokenPredicate(config.getTransactionalToken());
        }
    }

    private Predicate<RequestContext> createIpAdressPredicate(
            TransactionalSecurityConfiguration config) {
        if (!config.isTransactionalActive() ||
            !config.isSetTransactionalAllowedIps()) {
            return Predicates.alwaysTrue();
        } else {
            return new IpPredicate(config.getAllowedAddresses(),
                                   config.getAllowedProxies());
        }
    }

    private static class TokenPredicate implements Predicate<RequestContext> {
        private final String token;

        TokenPredicate(String token) {
            this.token = token;
        }

        @Override
        public boolean apply(RequestContext ctx) {
            return ctx.getToken().isPresent() &&
                   ctx.getToken().get().equals(this.token);
        }
    }

    private static class IpPredicate implements Predicate<RequestContext> {
        private final ImmutableSet<IPAddressRange> allowedAddresses;
        private final ImmutableSet<IPAddress> allowedProxies;

        IpPredicate(Set<IPAddressRange> allowedAddresses,
                    Set<IPAddress> allowedProxies) {
            this.allowedAddresses = ImmutableSet.copyOf(allowedAddresses);
            this.allowedProxies = ImmutableSet.copyOf(allowedProxies);
        }

        @Override
        public boolean apply(RequestContext ctx) {
            if (ctx.getIPAddress().isPresent()) {
                final IPAddress address;
                if (ctx.getForwardedForChain().isPresent()) {
                    if (!this.allowedProxies.contains(ctx.getIPAddress().get())) {
                        return false;
                    }
                    ProxyChain chain = ctx.getForwardedForChain().get();
                    for (IPAddress proxy : chain.getProxies()) {
                        if (!this.allowedProxies.contains(proxy)) {
                            return false;
                        }
                    }
                    address = chain.getOrigin();
                } else {
                    address = ctx.getIPAddress().get();
                }
                for (IPAddressRange range : this.allowedAddresses) {
                    if (range.contains(address)) {
                        return true;
                    }
                }
            }
            return false;
        }
    }
}
