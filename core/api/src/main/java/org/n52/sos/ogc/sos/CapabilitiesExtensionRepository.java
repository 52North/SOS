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
package org.n52.sos.ogc.sos;

import java.util.List;
import java.util.Map;
import java.util.Set;

import org.n52.sos.exception.ConfigurationException;
import org.n52.sos.ogc.ows.OwsExceptionReport;
import org.n52.sos.request.operator.RequestOperatorKey;
import org.n52.sos.request.operator.RequestOperatorRepository;
import org.n52.sos.util.AbstractConfiguringServiceLoaderRepository;
import org.n52.sos.util.CollectionHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

/**
 * Repository for {@link CapabilitiesExtension} implementations
 * 
 * @since 4.0.0
 * 
 */
public class CapabilitiesExtensionRepository extends
        AbstractConfiguringServiceLoaderRepository<CapabilitiesExtensionProvider> {
    private static final Logger LOG = LoggerFactory.getLogger(CapabilitiesExtensionRepository.class);

    private static class LazyHolder {
		private static final CapabilitiesExtensionRepository INSTANCE = new CapabilitiesExtensionRepository();
		
		private LazyHolder() {};
	}


    /**
     * Implemented {@link CapabilitiesExtensionProvider}
     */
    private final Map<CapabilitiesExtensionKey, List<CapabilitiesExtensionProvider>> providers = Maps.newHashMap();

    public static CapabilitiesExtensionRepository getInstance() {
        return LazyHolder.INSTANCE;
    }

    /**
     * Load implemented Capabilities extension provider
     * 
     * @throws ConfigurationException
     *             If no Capabilities extension provider is implemented
     */
    private CapabilitiesExtensionRepository() throws ConfigurationException {
        super(CapabilitiesExtensionProvider.class, false);
        load(false);
    }

    /**
     * Load the implemented Capabilities extension provider and add them to a
     * map with operation name as key
     * 
     * @param implementations
     *            the loaded implementations
     */
    @Override
    protected void processConfiguredImplementations(final Set<CapabilitiesExtensionProvider> implementations) {
        providers.clear();
        for (final CapabilitiesExtensionProvider provider : implementations) {
            if (provider.hasRelatedOperation()) {
                if (checkIfRelatedOperationIsActivated(provider)) {
                    LOG.info("Registered CapabilitiesExtensionProvider for {}", provider.getCapabilitiesExtensionKey());
                    addCapabilitiesExtensionProvider(provider);
                }
            } else {
                LOG.info("Registered CapabilitiesExtensionProvider for {}", provider.getCapabilitiesExtensionKey());
                addCapabilitiesExtensionProvider(provider);
            }
        }
    }

    public List<CapabilitiesExtensionProvider> getCapabilitiesExtensionProvider(
            final CapabilitiesExtensionKey serviceOperatorIdentifier) throws OwsExceptionReport {
        return getAllValidCapabilitiesExtensionProvider(providers.get(serviceOperatorIdentifier));
    }

    /**
     * Get the implemented {@link CapabilitiesExtensionProvider} for service and
     * version
     * 
     * @param service
     *            Specific service
     * @param version
     *            Specific version
     * 
     * @return the implemented Capabilities extension provider
     * 
     * @throws OwsExceptionReport
     */
    public List<CapabilitiesExtensionProvider> getCapabilitiesExtensionProvider(final String service, final String version)
            throws OwsExceptionReport {
        return getCapabilitiesExtensionProvider(new CapabilitiesExtensionKey(service, version));
    }

    /**
     * Get all valid {@link CapabilitiesExtensionProvider}
     * 
     * @param list
     *            Loaded CapabilitiesExtensionProvider
     * 
     * @return Valid CapabilitiesExtensionProvider
     */
    private List<CapabilitiesExtensionProvider> getAllValidCapabilitiesExtensionProvider(
            final List<CapabilitiesExtensionProvider> list) {
        final List<CapabilitiesExtensionProvider> valid = Lists.newLinkedList();
        if (CollectionHelper.isNotEmpty(list)) {
            for (final CapabilitiesExtensionProvider provider : list) {
                if (provider.hasRelatedOperation()) {
                    if (checkIfRelatedOperationIsActivated(provider)) {
                        valid.add(provider);
                    }
                } else {
                    valid.add(provider);
                }
            }
        }
        return valid;
    }

    /**
     * Add a loaded {@link CapabilitiesExtensionProvider} to the local map
     * 
     * @param provider
     *            Loaded CapabilitiesExtensionProvider
     */
    private void addCapabilitiesExtensionProvider(final CapabilitiesExtensionProvider provider) {
        final List<CapabilitiesExtensionProvider> extensions = Lists.newLinkedList();
        extensions.add(provider);
        if (providers.containsKey(provider.getCapabilitiesExtensionKey())) {
            extensions.addAll(providers.get(provider.getCapabilitiesExtensionKey()));
        }
        providers.put(provider.getCapabilitiesExtensionKey(), extensions);
    }

    /**
     * Check if the related operation for the loaded
     * {@link CapabilitiesExtensionProvider} is active
     * 
     * @param cep
     *            CapabilitiesExtensionProvider to check
     * 
     * @return <code>true</code>, if related operation is active
     */
    private boolean checkIfRelatedOperationIsActivated(final CapabilitiesExtensionProvider cep) {
        final CapabilitiesExtensionKey cek = cep.getCapabilitiesExtensionKey();
        final RequestOperatorKey rok = new RequestOperatorKey(cek.getService(), cek.getVersion(), cep.getRelatedOperation());
        return RequestOperatorRepository.getInstance().getActiveRequestOperatorKeys().contains(rok);
    }
}
