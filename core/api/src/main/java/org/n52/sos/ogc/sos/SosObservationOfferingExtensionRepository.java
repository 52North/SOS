/*
 * Copyright (C) 2012-2022 52°North Spatial Information Research GmbH
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

import static java.util.stream.Collectors.toSet;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import javax.inject.Inject;

import org.n52.iceland.util.activation.Activatables;
import org.n52.iceland.util.activation.ActivationListener;
import org.n52.iceland.util.activation.ActivationListeners;
import org.n52.iceland.util.activation.ActivationManager;
import org.n52.iceland.util.activation.ActivationSource;
import org.n52.janmayen.Producer;
import org.n52.janmayen.Producers;
import org.n52.janmayen.component.AbstractComponentRepository;
import org.n52.janmayen.lifecycle.Constructable;
import org.n52.shetland.ogc.ows.service.OwsServiceCommunicationObject;
import org.n52.shetland.ogc.ows.service.OwsServiceKey;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

/**
 * Repository for {@link SosObservationOfferingExtensionProvider}
 * implementations
 *
 * @since 1.0.0
 *
 */
public class SosObservationOfferingExtensionRepository extends
        AbstractComponentRepository<SosObservationOfferingExtensionKey,
        SosObservationOfferingExtensionProvider,
        SosObservationOfferingExtensionProviderFactory>
        implements ActivationManager<SosObservationOfferingExtensionKey>,
        ActivationSource<SosObservationOfferingExtensionKey>, Constructable {

    private final Map<SosObservationOfferingExtensionKey, Producer<SosObservationOfferingExtensionProvider>>
        offeringExtensionProviders = new HashMap<>(0);

    private final ActivationListeners<SosObservationOfferingExtensionKey> activation = new ActivationListeners<>(true);

    @Inject
    private Optional<Collection<SosObservationOfferingExtensionProvider>> components =
            Optional.of(Collections.emptyList());

    @Inject
    private Optional<Collection<SosObservationOfferingExtensionProviderFactory>> componentFactories =
            Optional.of(Collections.emptyList());

    @Override
    public void init() {
        this.offeringExtensionProviders.clear();
        this.offeringExtensionProviders.putAll(getUniqueProviders(this.components, this.componentFactories));
    }

    @Override
    public void registerListener(ActivationListener<SosObservationOfferingExtensionKey> listener) {
        this.activation.registerListener(listener);
    }

    @Override
    public void deregisterListener(ActivationListener<SosObservationOfferingExtensionKey> listener) {
        this.activation.deregisterListener(listener);
    }

    @Override
    public boolean isActive(SosObservationOfferingExtensionKey key) {
        return this.activation.isActive(key);
    }

    @Override
    public void activate(SosObservationOfferingExtensionKey key) {
        this.activation.activate(key);
    }

    @Override
    public void deactivate(SosObservationOfferingExtensionKey key) {
        this.activation.deactivate(key);
    }

    @Override
    public Set<SosObservationOfferingExtensionKey> getKeys() {
        return Collections.unmodifiableSet(this.offeringExtensionProviders.keySet());
    }

    /**
     * Get map of all, active and inactive,
     * {@link SosObservationOfferingExtensionProvider}s
     *
     * @return the map with all {@link SosObservationOfferingExtensionProvider}s
     */
    public Map<SosObservationOfferingExtensionKey, SosObservationOfferingExtensionProvider>
        getAllOfferingExtensionProviders() {
        return Producers.produce(this.offeringExtensionProviders);
    }

    /**
     * Get map of all active {@link SosObservationOfferingExtensionProvider}s
     *
     * @return the map with all active
     *         {@link SosObservationOfferingExtensionProvider}s
     */
    public Map<SosObservationOfferingExtensionKey, SosObservationOfferingExtensionProvider>
        getOfferingExtensionProviders() {
        return Producers.produce(Activatables.activatedMap(offeringExtensionProviders, this.activation));
    }

    /**
     * Get the loaded {@link SosObservationOfferingExtensionProvider}
     * implementation for the specific service and version
     *
     * @param message
     *            The {@link OwsServiceCommunicationObject} with service and
     *            version
     *
     * @return loaded {@link SosObservationOfferingExtensionProvider}
     *         implementation
     */
    public Set<SosObservationOfferingExtensionProvider> getOfferingExtensionProvider(
            OwsServiceCommunicationObject message) {
        Set<SosObservationOfferingExtensionProvider> providers = Sets.newHashSet();
        for (String name : getDomains()) {
            SosObservationOfferingExtensionKey key =
                    new SosObservationOfferingExtensionKey(message.getService(), message.getVersion(), name);
            SosObservationOfferingExtensionProvider provider = getOfferingExtensionProvider(key);
            if (provider != null) {
                providers.add(provider);
            }
        }
        return providers;
    }

    /**
     * Get the loaded {@link SosObservationOfferingExtensionProvider}
     * implementation for the specific
     * {@link SosObservationOfferingExtensionKey}
     *
     * @param key
     *            The related {@link SosObservationOfferingExtensionKey}
     *
     * @return loaded {@link SosObservationOfferingExtensionProvider}
     *         implementation
     */
    public SosObservationOfferingExtensionProvider getOfferingExtensionProvider(
            SosObservationOfferingExtensionKey key) {
        return getOfferingExtensionProviders().get(key);
    }

    /**
     * Check if a {@link SosObservationOfferingExtensionProvider} implementation
     * is loaded for the specific {@link SosObservationOfferingExtensionKey}
     *
     * @param key
     *            The related {@link SosObservationOfferingExtensionKey} to
     *            check for
     *
     * @return <code>true</code>, if a
     *         {@link SosObservationOfferingExtensionProvider} implementation is
     *         loaded for the specific service
     */
    public boolean hasOfferingExtensionProviderFor(SosObservationOfferingExtensionKey key) {
        return getOfferingExtensionProviders().containsKey(key);
    }

    /**
     * Check if a provider is available for the requested service and version
     *
     * @param message
     *            request object with service and version
     *
     * @return <code>true</code>, if a
     *         {@link SosObservationOfferingExtensionProvider} is available
     */
    public boolean hasOfferingExtensionProviderFor(OwsServiceCommunicationObject message) {
        boolean hasProvider;
        for (String name : getDomains()) {
            SosObservationOfferingExtensionKey key =
                    new SosObservationOfferingExtensionKey(message.getService(), message.getVersion(), name);
            hasProvider = hasOfferingExtensionProviderFor(key);
            if (hasProvider) {
                return activation.isActive(key);
            }
        }
        return false;
    }

    /**
     * Change the status of the {@link SosObservationOfferingExtensionProvider}
     * which relates to the requested {@link SosObservationOfferingExtensionKey}
     *
     * @param oekt
     *            the {@link SosObservationOfferingExtensionKey} to change the
     *            status for
     * @param active
     *            the new status
     */
    @Override
    public void setActive(final SosObservationOfferingExtensionKey oekt, final boolean active) {
        this.activation.setActive(oekt, active);
    }

    /**
     * Get map with {@link OwsServiceKey} and linked domain values
     *
     * @return the map with {@link OwsServiceKey} and linked domain values
     */
    public Map<OwsServiceKey, Collection<String>> getAllDomains() {
        Map<OwsServiceKey, Collection<String>> domains = Maps.newHashMap();
        Activatables.activatedKeys(this.offeringExtensionProviders, this.activation).stream().forEach(key -> {
            domains.computeIfAbsent(key.getServiceOperatorKey(), sok -> new LinkedList<>()).add(key.getDomain());
        });
        Activatables.deactivatedKeys(this.offeringExtensionProviders, this.activation).stream().forEach(key -> {
            domains.computeIfAbsent(key.getServiceOperatorKey(), sok -> new LinkedList<>()).add(key.getDomain());
        });
        return domains;
    }

    /**
     * Get all domain values from {@link SosObservationOfferingExtensionKey}
     *
     * @return the domain values
     */
    private Set<String> getDomains() {
        return Activatables.activatedKeys(this.offeringExtensionProviders, this.activation).stream()
                .map(SosObservationOfferingExtensionKey::getDomain).collect(toSet());
    }

}