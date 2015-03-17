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

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.n52.sos.config.SettingsManager;
import org.n52.sos.ds.ConnectionProviderException;
import org.n52.sos.exception.ConfigurationException;
import org.n52.sos.service.AbstractServiceCommunicationObject;
import org.n52.sos.service.operator.ServiceOperatorKey;
import org.n52.sos.util.AbstractConfiguringServiceLoaderRepository;
import org.n52.sos.util.Activatable;
import org.n52.sos.util.CollectionHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

/**
 * Repository for {@link OwsExtendedCapabilities}. Loads all implemented
 * {@link OwsExtendedCapabilitiesProvider} and adds to this repository.
 * 
 * @since 4.0.0
 * 
 */
public class OwsExtendedCapabilitiesRepository extends
        AbstractConfiguringServiceLoaderRepository<OwsExtendedCapabilitiesProvider> {

    private static final Logger LOGGER = LoggerFactory.getLogger(OwsExtendedCapabilitiesRepository.class);

    /**
     * Lazy holder class for the {@link OwsExtendedCapabilitiesRepository}
     * 
     * @author Carsten Hollmann <c.hollmann@52north.org>
     * @since 4.1.0
     * 
     */
    private static class LazyHolder {
        private static final OwsExtendedCapabilitiesRepository INSTANCE = new OwsExtendedCapabilitiesRepository();

        private LazyHolder() {
        };
    }

    private final Map<OwsExtendedCapabilitiesKey, Activatable<OwsExtendedCapabilitiesProvider>> extendedCapabilitiesProvider =
            new HashMap<OwsExtendedCapabilitiesKey, Activatable<OwsExtendedCapabilitiesProvider>>(0);

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
    protected void processConfiguredImplementations(
            final Set<OwsExtendedCapabilitiesProvider> extendedCapabilitiesProviders) throws ConfigurationException {
        this.extendedCapabilitiesProvider.clear();
        final SettingsManager sm = SettingsManager.getInstance();
        Set<ServiceOperatorKey> activeSokts = Sets.newHashSet();
        for (final OwsExtendedCapabilitiesProvider ecp : extendedCapabilitiesProviders) {
            for (OwsExtendedCapabilitiesKey key : ecp.getExtendedCapabilitiesKeyType()) {
                try {
                    LOGGER.info("Registered OwsExtendedCapabilitiesProvider for {}", key);
                    if (sm.isActive(key)) {
                        if (activeSokts.contains(key.getServiceOperatorKey())) {
                            sm.setActive(key, false, false);
                        } else {
                            activeSokts.add(key.getServiceOperatorKey());
                        }
                    }
                    this.extendedCapabilitiesProvider.put(key, Activatable.from(ecp, sm.isActive(key)));
                } catch (final ConnectionProviderException ex) {
                    throw new ConfigurationException("Could not check status of Binding", ex);
                }
            }
        }
    }

    /**
     * Get map of all, active and inactive,
     * {@link OwsExtendedCapabilitiesProvider}s
     * 
     * @return the map with all {@link OwsExtendedCapabilitiesProvider}s
     */
    public Map<OwsExtendedCapabilitiesKey, OwsExtendedCapabilitiesProvider> getAllExtendedCapabilitiesProviders() {
        return Activatable.unfiltered(extendedCapabilitiesProvider);
    }

    /**
     * Get map of all active {@link OwsExtendedCapabilitiesProvider}s
     * 
     * @return the map with all active {@link OwsExtendedCapabilitiesProvider}s
     */
    public Map<OwsExtendedCapabilitiesKey, OwsExtendedCapabilitiesProvider> getExtendedCapabilitiesProviders() {
        return Activatable.filter(extendedCapabilitiesProvider);
    }

    /**
     * Get the loaded {@link OwsExtendedCapabilitiesProvider} implementation for
     * the specific service and version
     * 
     * @param serviceCommunicationObject
     *            The {@link AbstractServiceCommunicationObject} with service
     *            and version
     * @return loaded {@link OwsExtendedCapabilitiesProvider} implementation
     */
    public OwsExtendedCapabilitiesProvider getExtendedCapabilitiesProvider(
            AbstractServiceCommunicationObject serviceCommunicationObject) {
        for (String name : getDomains()) {
            OwsExtendedCapabilitiesProvider provider =
                    getExtendedCapabilitiesProvider(new OwsExtendedCapabilitiesKey(
                            serviceCommunicationObject.getService(), serviceCommunicationObject.getVersion(), name));
            if (provider != null) {
                return provider;
            }
        }
        return null;
    }

    /**
     * Get the loaded {@link OwsExtendedCapabilitiesProvider} implementation for
     * the specific {@link OwsExtendedCapabilitiesKey}
     * 
     * @param key
     *            The related {@link OwsExtendedCapabilitiesKey}
     * @return loaded {@link OwsExtendedCapabilitiesProvider} implementation
     */
    public OwsExtendedCapabilitiesProvider getExtendedCapabilitiesProvider(OwsExtendedCapabilitiesKey key) {
        return getExtendedCapabilitiesProviders().get(key);
//      final Activatable<OwsExtendedCapabilitiesProvider> provider = extendedCapabilitiesProvider.get(key);
//      return provider == null ? null : provider.get();
    }

    /**
     * Check if a {@link OwsExtendedCapabilitiesProvider} implementation is
     * loaded for the specific {@link AbstractServiceCommunicationObject}
     * 
     * @param serviceCommunicationObject
     *            The {@link AbstractServiceCommunicationObject} with service
     *            and version
     * @return <code>true</code>, if a {@link OwsExtendedCapabilitiesProvider}
     *         implementation is loaded for the specific
     *         {@link AbstractServiceCommunicationObject}
     */
    public boolean hasExtendedCapabilitiesProvider(AbstractServiceCommunicationObject serviceCommunicationObject) {
        boolean hasProvider = false;
        for (String name : getDomains()) {
            hasProvider =
                    hasExtendedCapabilitiesProvider(new OwsExtendedCapabilitiesKey(
                            serviceCommunicationObject.getService(), serviceCommunicationObject.getVersion(), name));
            if (hasProvider) {
                return hasProvider;
            }
        }
        return false;
    }

    /**
     * Check if a {@link OwsExtendedCapabilitiesProvider} implementation is
     * loaded for the specific {@link OwsExtendedCapabilitiesKey}
     * 
     * @param key
     *            The related {@link OwsExtendedCapabilitiesKey} to check for
     * @return <code>true</code>, if a {@link OwsExtendedCapabilitiesProvider}
     *         implementation is loaded for the specific
     *         {@link OwsExtendedCapabilitiesKey}
     */
    public boolean hasExtendedCapabilitiesProvider(final OwsExtendedCapabilitiesKey oeckt) {
        return getExtendedCapabilitiesProviders().containsKey(oeckt);
    }

    /**
     * Change the status of the {@link OwsExtendedCapabilitiesProvider} which
     * relates to the requested {@link OwsExtendedCapabilitiesKey}
     * 
     * @param oeckt
     *            the {@link OwsExtendedCapabilitiesKey} to change the status
     *            for
     * @param active
     *            the new status
     */
    public void setActive(final OwsExtendedCapabilitiesKey oeckt, final boolean active) {
        if (getAllExtendedCapabilitiesProviders().containsKey(oeckt)) {
            if (active) {
                for (OwsExtendedCapabilitiesKey key : getAllExtendedCapabilitiesProviders().keySet()) {
                    if (key.getService().equals(oeckt.getService()) && key.getVersion().equals(oeckt.getVersion())) {
                        extendedCapabilitiesProvider.get(key).setActive(false);
                    }
                }
            }
            extendedCapabilitiesProvider.get(oeckt).setActive(active);
        }
    }

    /**
     * Get map with {@link ServiceOperatorKey} and linked domain values
     * 
     * @return the map with {@link ServiceOperatorKey} and linked domain values
     */
    public Map<ServiceOperatorKey, Collection<String>> getAllDomains() {
        Map<ServiceOperatorKey, Collection<String>> domains = Maps.newHashMap();
        for (OwsExtendedCapabilitiesKey key : getAllExtendedCapabilitiesProviders().keySet()) {
            CollectionHelper.addToCollectionMap(key.getServiceOperatorKey(), key.getDomain(), domains);
        }
        return domains;
    }

    /**
     * Get all domain values from {@link OwsExtendedCapabilitiesKey}
     * 
     * @return the domain values
     */
    private Set<String> getDomains() {
        Set<String> domains = Sets.newHashSet();
        for (OwsExtendedCapabilitiesKey key : getExtendedCapabilitiesProviders().keySet()) {
            domains.add(key.getDomain());
        }
        return domains;
    }

}
