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
package org.n52.iceland.ogc.ows;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.n52.iceland.config.SettingsManager;
import org.n52.iceland.ds.ConnectionProviderException;
import org.n52.iceland.exception.ConfigurationException;
import org.n52.iceland.service.AbstractServiceCommunicationObject;
import org.n52.iceland.service.operator.ServiceOperatorKey;
import org.n52.iceland.util.AbstractConfiguringServiceLoaderRepository;
import org.n52.iceland.util.Activatable;
import org.n52.iceland.util.CollectionHelper;
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
