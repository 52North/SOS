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

import java.util.Iterator;
import java.util.Locale;
import java.util.ServiceConfigurationError;
import java.util.ServiceLoader;

import org.n52.sos.ds.ConnectionProviderIdentificator;
import org.n52.sos.ds.DatasourceDaoIdentifier;
import org.n52.sos.exception.ConfigurationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.n52.sos.exception.ConfigurationException;

/**
 * Producer that loads a single instance of <code>T</code> with a
 * {@link ServiceLoader}.
 *
 * @param <T>
 *            the type to produce
 *            <p/>
 * @author Christian Autermann <c.autermann@52north.org>
 * @since 4.0.0
 */
public class SingletonServiceLoader<T> implements Producer<T> {

    private static final Logger LOG = LoggerFactory.getLogger(SingletonServiceLoader.class);

    private final Class<? extends T> clazz;

    private final boolean failIfNotFound;

    private final ServiceLoader<? extends T> serviceLoader;

    private T implementation;

    private T defaultImplementation;

    public SingletonServiceLoader(Class<? extends T> c, boolean failIfNotFound) {
        this(c, failIfNotFound, null);
    }

    public SingletonServiceLoader(Class<? extends T> c, boolean failIfNotFound, T defaultImplementation) {
        this.clazz = c;
        this.failIfNotFound = failIfNotFound;
        this.serviceLoader = ServiceLoader.load(c);
        this.defaultImplementation = defaultImplementation;
    }

    @Override
    public final T get() throws ConfigurationException {
        if (implementation == null) {
            Iterator<? extends T> iter = serviceLoader.iterator();
            while (iter.hasNext() && implementation == null) {
                try {
                    implementation = iter.next();
                } catch (ServiceConfigurationError sce) {
                    LOG.warn(String.format("Implementation for %s could be loaded!", clazz), sce);
                }
            }
            if (implementation == null && defaultImplementation != null) {
                implementation = defaultImplementation;
            }
            if (implementation == null) {
                String message = String.format("No implementation for %s could be loaded!", clazz);
                if (failIfNotFound) {
                    throw new ConfigurationException(message);
                } else {
                    LOG.warn(message);
                }
            } else {
                processImplementation(implementation);
                LOG.info("Implementation for {} successfully loaded: {}", clazz, implementation);
            }
        }

        return implementation;
    }

    @Override
    public T get(Locale language) {
        // No language support
        return get();
    }

    @Override
    public final T get(String datasourceIdentifier) {
        if (implementation == null) {
            Iterator<? extends T> iter = serviceLoader.iterator();
            T currentImplementation = null;
            while (iter.hasNext() && implementation == null) {
                try {
                    currentImplementation = iter.next();
                } catch (ServiceConfigurationError sce) {
                    LOG.warn(String.format("Implementation for %s could be loaded!", clazz), sce);
                }
                if (currentImplementation instanceof ConnectionProviderIdentificator) {
                    if (datasourceIdentifier.equalsIgnoreCase(
                            ((ConnectionProviderIdentificator) currentImplementation).getConnectionProviderIdentifier())) {
                        implementation = currentImplementation;
                    }
                }
                if (currentImplementation instanceof DatasourceDaoIdentifier) {
                    if (datasourceIdentifier.equalsIgnoreCase(
                            ((DatasourceDaoIdentifier) currentImplementation).getDatasourceDaoIdentifier())) {
                        implementation = currentImplementation;
                    }
                }
            }
            if (implementation == null && defaultImplementation != null) {
                implementation = defaultImplementation;
            }
            if (implementation == null) {
                String message = String.format("No implementation for %s could be loaded!", clazz);
                if (failIfNotFound) {
                    throw new ConfigurationException(message);
                } else {
                    LOG.warn(message);
                }
            } else {
                processImplementation(implementation);
                LOG.info("Implementation for {} successfully loaded: {}", clazz, implementation);
            }
        }

        return implementation;
    }

    /**
     * Classes extending this class may overwrite the default (empty)
     * implementation.
     * <p/>
     *
     * @param implementation
     *            the loaded implementation
     *            <p/>
     * @throws ConfigurationException
     *             if the processing fails
     */
    protected void processImplementation(T implementation) throws ConfigurationException {
    }

}
