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
package org.n52.iceland.util;

import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.ServiceConfigurationError;
import java.util.ServiceLoader;
import java.util.Set;

import org.n52.iceland.ds.ConnectionProvider;
import org.n52.iceland.ds.ConnectionProviderIdentificator;
import org.n52.iceland.exception.ConfigurationException;
import org.n52.iceland.service.Configurator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
 * Abstract class to encapsulate the loading of implementations that are
 * registered with the ServiceLoader interface.
 * 
 * @param <T>
 *            the type that should be loaded
 * 
 * 
 * @author Christian Autermann <c.autermann@52north.org>
 * @since 4.0.0
 */
public abstract class AbstractServiceLoaderRepository<T> {
    private static final Logger LOG = LoggerFactory.getLogger(AbstractServiceLoaderRepository.class);

    private final ServiceLoader<T> serviceLoader;

    private final Class<T> type;

    private final boolean failIfEmpty;

    protected AbstractServiceLoaderRepository(Class<T> type, boolean failIfEmpty) {
        LOG.debug("Loading Implementations for {}", type);
        this.type = type;
        this.failIfEmpty = failIfEmpty;
        this.serviceLoader = ServiceLoader.load(this.type);
        LOG.debug("Implementations for {} loaded succesfull!", this.type);
    }

    public void update() throws ConfigurationException {
        LOG.debug("Reloading Implementations for {}", this.type);
        load(true);
        LOG.debug("Implementations for {} reloaded succesfull!", this.type);
    }

    protected final void load(boolean reload) throws ConfigurationException {
        processImplementations(getImplementations(reload));
    }

    private Set<T> getImplementations(boolean reload) throws ConfigurationException {
        if (reload) {
            this.serviceLoader.reload();
        }
        LinkedList<T> implementations = new LinkedList<T>();
        Iterator<T> iter = this.serviceLoader.iterator();
        while (iter.hasNext()) {
            try {
                T t = iter.next();
                LOG.debug("Found implementation: {}", t);
                implementations.add(t);
            } catch (ServiceConfigurationError e) {
                // TODO add more details like which class with qualified name
                LOG.warn(
                        String.format("An implementation for %s could not be loaded! Exception message: ", this.type),
                        e);
            }
        }
        if (this.failIfEmpty && implementations.isEmpty()) {
            String exceptionText = String.format("No implementations for %s is found!", this.type);
            LOG.error(exceptionText);
            throw new ConfigurationException(exceptionText);
        }
        LOG.debug("Found {} implementations for {}", implementations.size(), this.type);
        return new HashSet<T>(implementations);
    }

    protected boolean checkConnectionProviderIdentifications(ConnectionProviderIdentificator connectionProviderIdentificator) {
        ConnectionProvider dataConnectionProvider = getConfigurator().getDataConnectionProvider();
        ConnectionProvider featureConnectionProvider = getConfigurator().getFeatureConnectionProvider();
        if (dataConnectionProvider.getConnectionProviderIdentifier().equalsIgnoreCase(
                connectionProviderIdentificator.getConnectionProviderIdentifier())
                || featureConnectionProvider.getConnectionProviderIdentifier().equalsIgnoreCase(
                        connectionProviderIdentificator.getConnectionProviderIdentifier())) {
            return true;
        }
        return false;
    }
    
    private Configurator getConfigurator() {
        return Configurator.getInstance();
    }

    protected abstract void processImplementations(Set<T> implementations) throws ConfigurationException;
}
