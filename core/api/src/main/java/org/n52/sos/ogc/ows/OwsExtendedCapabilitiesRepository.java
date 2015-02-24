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
package org.n52.sos.ogc.ows;

import java.util.Map;
import java.util.Set;

import org.n52.sos.exception.ConfigurationException;
import org.n52.sos.util.AbstractConfiguringServiceLoaderRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Maps;

/**
 * Repository for {@link OwsExtendedCapabilities}. Loads all implemented
 * {@link OwsExtendedCapabilitiesProvider} and adds the provided
 * {@link OwsExtendedCapabilities} to this repository.
 * 
 * @since 4.0.0
 * 
 */
public class OwsExtendedCapabilitiesRepository extends
        AbstractConfiguringServiceLoaderRepository<OwsExtendedCapabilitiesProvider> {

    private static final Logger LOGGER = LoggerFactory.getLogger(OwsExtendedCapabilitiesRepository.class);

    private static class LazyHolder {
		private static final OwsExtendedCapabilitiesRepository INSTANCE = new OwsExtendedCapabilitiesRepository();
		
		private LazyHolder() {};
	}


    private final Map<String, OwsExtendedCapabilities> extendedCapabilities = Maps.newHashMap();

    /**
     * For singleton use
     * 
     * @return The single instance
     */
    public static OwsExtendedCapabilitiesRepository getInstance() {
        return LazyHolder.INSTANCE;
    }

    /**
     * Load implemented {@link OwsExtendedCapabilities}
     * 
     * @throws ConfigurationException
     *             If no Capabilities extension providerr is implemented
     */
    private OwsExtendedCapabilitiesRepository() throws ConfigurationException {
        super(OwsExtendedCapabilitiesProvider.class, false);
        load(false);
    }

    @Override
    protected void processConfiguredImplementations(final Set<OwsExtendedCapabilitiesProvider> implementations)
            throws ConfigurationException {
        extendedCapabilities.clear();
        for (final OwsExtendedCapabilitiesProvider owsExtendedCapabilitiesProvider : implementations) {
            for (final OwsExtendedCapabilities owsExtendedCapabilities : owsExtendedCapabilitiesProvider
                    .getOwsExtendedCapabilities()) {
                if (hasExtendedCapabilitiesFor(owsExtendedCapabilities.getService())) {
                    LOGGER.warn(
                            "The OwsExtendedCapabilitiesRepository still contains an OwsExtendedCapabilities implementation for service '{}'",
                            owsExtendedCapabilities.getService());
                } else {
                    extendedCapabilities.put(owsExtendedCapabilities.getService(), owsExtendedCapabilities);
                }
            }
        }
    }

    /**
     * Get map with all loaded OwsExtendedCapabilities implementations
     * 
     * @return Map with loaded OwsExtendedCapabilities implementations for
     *         services
     */
    public Map<String, OwsExtendedCapabilities> getExtendedCapabilities() {
        return extendedCapabilities;
    }

    /**
     * Get the loaded OwsExtendedCapabilities implementation for the specific
     * service
     * 
     * @param service
     *            The related service
     * @return loaded OwsExtendedCapabilities implementation
     */
    public OwsExtendedCapabilities getExtendedCapabilities(final String service) {
        return getExtendedCapabilities().get(service);
    }

    /**
     * Check if a loaded OwsExtendedCapabilities implementation is loaded for
     * the specific service
     * 
     * @param service
     *            The related service to check for
     * @return <code>true</code>, if a OwsExtendedCapabilities implementation is
     *         loaded for the specific service
     */
    public boolean hasExtendedCapabilitiesFor(final String service) {
        return getExtendedCapabilities().containsKey(service);
    }

}
