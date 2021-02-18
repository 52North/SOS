/*
 * Copyright (C) 2012-2021 52°North Initiative for Geospatial Open Source
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
package org.n52.sos.ds.hibernate.util.observation;

import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import javax.inject.Inject;

import org.n52.janmayen.Producer;
import org.n52.janmayen.component.AbstractComponentRepository;
import org.n52.janmayen.lifecycle.Constructable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Maps;

public class AdditionalObservationCreatorRepository extends
        AbstractComponentRepository<AdditionalObservationCreatorKey,
        AdditionalObservationCreator,
        AdditionalObservationCreatorFactory>
        implements Constructable {

    private static final Logger LOGGER = LoggerFactory.getLogger(AdditionalObservationCreatorRepository.class);

    private final Map<AdditionalObservationCreatorKey,
                  Producer<AdditionalObservationCreator>> additionalObservationCreator =
            Maps.newHashMap();

    private Optional<Collection<AdditionalObservationCreator>> components;

    private Optional<Collection<AdditionalObservationCreatorFactory>> componentFactories;

    @Inject
    public void setComponentFactories(Optional<Collection<AdditionalObservationCreatorFactory>> componentFactories) {
        this.componentFactories = componentFactories;
    }

    @Inject
    public void setComponents(Optional<Collection<AdditionalObservationCreator>> components) {
        this.components = components;
    }

    @Override
    public void init() {
        Map<AdditionalObservationCreatorKey, Producer<AdditionalObservationCreator>> implementations =
                getUniqueProviders(this.components, this.componentFactories);
        this.additionalObservationCreator.clear();
        this.additionalObservationCreator.putAll(implementations);
    }

    public AdditionalObservationCreator get(AdditionalObservationCreatorKey key) {
        Producer<AdditionalObservationCreator> producer = additionalObservationCreator.get(key);
        return producer == null ? null : producer.get();
    }

    public AdditionalObservationCreator get(String namespace, Class<?> type) {
        return get(new AdditionalObservationCreatorKey(namespace, type));
    }

    public boolean hasAdditionalObservationCreatorFor(String namespace, Class<?> type) {
        return hasAdditionalObservationCreatorFor(new AdditionalObservationCreatorKey(namespace, type));
    }

    public boolean hasAdditionalObservationCreatorFor(AdditionalObservationCreatorKey key) {
        return additionalObservationCreator.containsKey(key);
    }

    public static Set<AdditionalObservationCreatorKey> encoderKeysForElements(String namespace, Class<?>... elements) {
        HashSet<AdditionalObservationCreatorKey> keys = new HashSet<>(elements.length);
        for (Class<?> x : elements) {
            keys.add(new AdditionalObservationCreatorKey(namespace, x));
        }
        return keys;
    }
}
