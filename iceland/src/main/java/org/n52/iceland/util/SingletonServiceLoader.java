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

import java.util.Iterator;
import java.util.Locale;
import java.util.ServiceConfigurationError;
import java.util.ServiceLoader;

import org.n52.iceland.ds.ConnectionProviderIdentificator;
import org.n52.iceland.ds.DatasourceDaoIdentifier;
import org.n52.iceland.exception.ConfigurationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
