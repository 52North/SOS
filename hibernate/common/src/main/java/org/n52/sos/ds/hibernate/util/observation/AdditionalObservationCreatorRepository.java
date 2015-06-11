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
package org.n52.sos.ds.hibernate.util.observation;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.n52.sos.exception.ConfigurationException;
import org.n52.sos.util.AbstractConfiguringServiceLoaderRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@SuppressWarnings("rawtypes")
public class AdditionalObservationCreatorRepository extends AbstractConfiguringServiceLoaderRepository<AdditionalObservationCreator> {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(AdditionalObservationCreatorRepository.class);
    
    private static class LazyHolder {
        private static final AdditionalObservationCreatorRepository INSTANCE = new AdditionalObservationCreatorRepository();
        
        private LazyHolder() {};
    }
    

    public static AdditionalObservationCreatorRepository getInstance() {
        return LazyHolder.INSTANCE;
    }
    
    private final Map<AdditionalObservationCreatorKey, AdditionalObservationCreator<?>> additionalObservationCreator =
            new HashMap<AdditionalObservationCreatorKey, AdditionalObservationCreator<?>>(0);

    /**
     * private constructor for singleton
     * 
     * @throws ConfigurationException
     */
    private AdditionalObservationCreatorRepository() throws ConfigurationException {
        super(AdditionalObservationCreator.class, false);
        load(false);
    }


    @Override
    protected void processConfiguredImplementations(Set<AdditionalObservationCreator> additionalObservationCreators)
            throws ConfigurationException {
        this.additionalObservationCreator.clear();
        for (final AdditionalObservationCreator<?> aoc : additionalObservationCreators) {
            for (AdditionalObservationCreatorKey key : aoc.getKeys()) {
                LOGGER.debug("Registered AdditionalObservationCreator for {}", key);
                this.additionalObservationCreator.put(key, aoc);
            }
        }
    }

    
    public AdditionalObservationCreator get(AdditionalObservationCreatorKey key) {
        return additionalObservationCreator.get(key);
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
    
    public static Set<AdditionalObservationCreatorKey> encoderKeysForElements(final String namespace, final Class<?>... elements) {
        final HashSet<AdditionalObservationCreatorKey> keys = new HashSet<AdditionalObservationCreatorKey>(elements.length);
        for (final Class<?> x : elements) {
            keys.add(new AdditionalObservationCreatorKey(namespace, x));
        }
        return keys;
    }

}
