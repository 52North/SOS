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
package org.n52.sos.util;

import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.ServiceConfigurationError;
import java.util.ServiceLoader;
import java.util.Set;

import org.n52.sos.ds.ConnectionProvider;
import org.n52.sos.ds.ConnectionProviderIdentificator;
import org.n52.sos.ds.DatasourceDaoIdentifier;
import org.n52.sos.ds.OperationDAO;
import org.n52.sos.exception.ConfigurationException;
import org.n52.sos.service.Configurator;
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
