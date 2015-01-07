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

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.n52.sos.config.SettingsManager;
import org.n52.sos.ds.ConnectionProviderException;
import org.n52.sos.ds.OperationDAORepository;
import org.n52.sos.exception.ConfigurationException;
import org.n52.sos.service.operator.ServiceOperatorKey;
import org.n52.sos.util.AbstractConfiguringServiceLoaderRepository;
import org.n52.sos.util.Activatable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Christian Autermann <c.autermann@52north.org>
 * 
 * @since 4.0.0
 */
public class RequestOperatorRepository extends AbstractConfiguringServiceLoaderRepository<RequestOperator> {
    private static final Logger LOG = LoggerFactory.getLogger(RequestOperatorRepository.class);

    private static class LazyHolder {
    	private static final RequestOperatorRepository INSTANCE = new RequestOperatorRepository();
    	
    	private LazyHolder() {};
    }

    private final Map<RequestOperatorKey, Activatable<RequestOperator>> requestOperators =
            new HashMap<RequestOperatorKey, Activatable<RequestOperator>>(0);

    public static RequestOperatorRepository getInstance() {
        return LazyHolder.INSTANCE;
    }

    /**
     * private constructor for singleton
     * 
     * @throws ConfigurationException
     */
    private RequestOperatorRepository() throws ConfigurationException {
        super(RequestOperator.class, false);
        load(false);
    }

    @Override
    protected void processConfiguredImplementations(final Set<RequestOperator> requestOperators)
            throws ConfigurationException {
        this.requestOperators.clear();
        for (final RequestOperator op : requestOperators) {
            try {
                LOG.info("Registered IRequestOperator for {}", op.getRequestOperatorKeyType());
                final boolean active = SettingsManager.getInstance().isActive(op.getRequestOperatorKeyType());
                this.requestOperators
                        .put(op.getRequestOperatorKeyType(), new Activatable<RequestOperator>(op, active));
            } catch (final ConnectionProviderException cpe) {
                throw new ConfigurationException("Error while checking RequestOperator", cpe);
            }
        }
    }

    @Override
    public void update() throws ConfigurationException {
        OperationDAORepository.getInstance().update();
        super.update();
    }

    public RequestOperator getRequestOperator(final RequestOperatorKey key) {
        final Activatable<RequestOperator> a = requestOperators.get(key);
        return a == null ? null : a.get();
    }

    public RequestOperator getRequestOperator(final ServiceOperatorKey sok, final String operationName) {
        return getRequestOperator(new RequestOperatorKey(sok, operationName));
    }

    public void setActive(final RequestOperatorKey rokt, final boolean active) {
        if (requestOperators.get(rokt) != null) {
            requestOperators.get(rokt).setActive(active);
        }
    }

    public Set<RequestOperatorKey> getActiveRequestOperatorKeys() {
        return Activatable.filter(requestOperators).keySet();
    }

    public Set<RequestOperatorKey> getAllRequestOperatorKeys() {
        return Collections.unmodifiableSet(requestOperators.keySet());
    }
}
