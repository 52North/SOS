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
package org.n52.iceland.request.operator;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.n52.iceland.config.SettingsManager;
import org.n52.iceland.ds.ConnectionProviderException;
import org.n52.iceland.ds.OperationHandlerRepository;
import org.n52.iceland.exception.ConfigurationException;
import org.n52.iceland.service.operator.ServiceOperatorKey;
import org.n52.iceland.util.AbstractConfiguringServiceLoaderRepository;
import org.n52.iceland.util.Activatable;
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
        OperationHandlerRepository.getInstance().update();
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
