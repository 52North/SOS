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
package org.n52.iceland.ogc.swes;

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
 * Repository for {@link OfferingExtensionProvider} implementations
 * 
 * @since 4.1.0
 * 
 */
public class OfferingExtensionRepository extends AbstractConfiguringServiceLoaderRepository<OfferingExtensionProvider> {

    private static final Logger LOGGER = LoggerFactory.getLogger(OfferingExtensionRepository.class);

    /**
     * Lazy holder class for the {@link OfferingExtensionRepository}
     * 
     * @author Carsten Hollmann <c.hollmann@52north.org>
     * @since 4.1.0
     * 
     */
    private static class LazyHolder {
        private static final OfferingExtensionRepository INSTANCE = new OfferingExtensionRepository();

        private LazyHolder() {
        };
    }

    private final Map<OfferingExtensionKey, Activatable<OfferingExtensionProvider>> offeringExtensionProviders =
            new HashMap<OfferingExtensionKey, Activatable<OfferingExtensionProvider>>(0);

    /**
     * For singleton use
     * 
     * @return The single instance
     */
    public static OfferingExtensionRepository getInstance() {
        return LazyHolder.INSTANCE;
    }

    /**
     * Load implemented {@link OfferingExtensionProvider}
     * 
     * @throws ConfigurationException
     *             If no {@link OfferingExtensionProvider} is implemented
     */
    private OfferingExtensionRepository() throws ConfigurationException {
        super(OfferingExtensionProvider.class, false);
        load(false);
    }

    @Override
    protected void processConfiguredImplementations(final Set<OfferingExtensionProvider> offeringExtensionProviders)
            throws ConfigurationException {
        this.offeringExtensionProviders.clear();
        SettingsManager sm = SettingsManager.getInstance();
        for (final OfferingExtensionProvider oep : offeringExtensionProviders) {
            for (OfferingExtensionKey key : oep.getOfferingExtensionKeyTypes()) {
                try {
                    LOGGER.info("Registered OfferingExtensionProvider for {}", key);
                    this.offeringExtensionProviders.put(key, Activatable.from(oep, sm.isActive(key)));
                } catch (final ConnectionProviderException cpe) {
                    throw new ConfigurationException("Error while checking RequestOperator", cpe);
                }
            }
        }
    }

    /**
     * Get map of all, active and inactive, {@link OfferingExtensionProvider}s
     * 
     * @return the map with all {@link OfferingExtensionProvider}s
     */
    public Map<OfferingExtensionKey, OfferingExtensionProvider> getAllOfferingExtensionProviders() {
        return Activatable.unfiltered(offeringExtensionProviders);
    }

    /**
     * Get map of all active {@link OfferingExtensionProvider}s
     * 
     * @return the map with all active {@link OfferingExtensionProvider}s
     */
    public Map<OfferingExtensionKey, OfferingExtensionProvider> getOfferingExtensionProviders() {
        return Activatable.filter(offeringExtensionProviders);
    }

    /**
     * Get the loaded {@link OfferingExtensionProvider} implementation for the
     * specific service and version
     * 
     * @param serviceCommunicationObject
     *            The {@link AbstractServiceCommunicationObject} with service
     *            and version
     * @return loaded {@link OfferingExtensionProvider} implementation
     */
    public Set<OfferingExtensionProvider> getOfferingExtensionProvider(
            AbstractServiceCommunicationObject serviceCommunicationObject) {
        Set<OfferingExtensionProvider> providers = Sets.newHashSet();
        for (String name : getDomains()) {
            OfferingExtensionProvider provider =
                    getOfferingExtensionProvider(new OfferingExtensionKey(serviceCommunicationObject.getService(),
                            serviceCommunicationObject.getVersion(), name));
            if (provider != null) {
                providers.add(provider);
            }
        }
        return providers;
    }

    /**
     * Get the loaded {@link OfferingExtensionProvider} implementation for the
     * specific {@link OfferingExtensionKey}
     * 
     * @param key
     *            The related {@link OfferingExtensionKey}
     * @return loaded {@link OfferingExtensionProvider} implementation
     */
    public OfferingExtensionProvider getOfferingExtensionProvider(OfferingExtensionKey key) {
        return getOfferingExtensionProviders().get(key);
    }

    /**
     * Check if a {@link OfferingExtensionProvider} implementation is loaded for
     * the specific {@link OfferingExtensionKey}
     * 
     * @param key
     *            The related {@link OfferingExtensionKey} to check for
     * @return <code>true</code>, if a {@link OfferingExtensionProvider}
     *         implementation is loaded for the specific service
     */
    public boolean hasOfferingExtensionProviderFor(OfferingExtensionKey key) {
        return getOfferingExtensionProviders().containsKey(key);
    }

    /**
     * Check if a provider is available for the requested service and version
     * 
     * @param serviceCommunicationObject
     *            request object with service and version
     * @return <code>true</code>, if a {@link OfferingExtensionProvider} is
     *         available
     */
    public boolean hasOfferingExtensionProviderFor(AbstractServiceCommunicationObject serviceCommunicationObject) {
        boolean hasProvider = false;
        for (String name : getDomains()) {
            hasProvider =
                    hasOfferingExtensionProviderFor(new OfferingExtensionKey(serviceCommunicationObject.getService(),
                            serviceCommunicationObject.getVersion(), name));
            if (hasProvider) {
                return hasProvider;
            }
        }
        return false;
    }

    /**
     * Change the status of the {@link OfferingExtensionProvider} which relates
     * to the requested {@link OfferingExtensionKey}
     * 
     * @param oekt
     *            the {@link OfferingExtensionKey} to change the status for
     * @param active
     *            the new status
     */
    public void setActive(final OfferingExtensionKey oekt, final boolean active) {
        if (getAllOfferingExtensionProviders().containsKey(oekt)) {
            offeringExtensionProviders.get(oekt).setActive(active);
        }
    }

    /**
     * Get map with {@link ServiceOperatorKey} and linked domain values
     * 
     * @return the map with {@link ServiceOperatorKey} and linked domain values
     */
    public Map<ServiceOperatorKey, Collection<String>> getAllDomains() {
        Map<ServiceOperatorKey, Collection<String>> domains = Maps.newHashMap();
        for (OfferingExtensionKey key : getAllOfferingExtensionProviders().keySet()) {
            CollectionHelper.addToCollectionMap(key.getServiceOperatorKey(), key.getDomain(), domains);
        }
        return domains;
    }

    /**
     * Get all domain values from {@link OfferingExtensionKey}
     * 
     * @return the domain values
     */
    private Set<String> getDomains() {
        Set<String> domains = Sets.newHashSet();
        for (OfferingExtensionKey key : getOfferingExtensionProviders().keySet()) {
            domains.add(key.getDomain());
        }
        return domains;
    }
}
