/*
 * Copyright (C) 2012-2018 52°North Initiative for Geospatial Open Source
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
package org.n52.sos.ds.hibernate.util.procedure.generator;

import java.util.Collection;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;

import org.n52.janmayen.Producer;
import org.n52.janmayen.component.AbstractComponentRepository;
import org.n52.janmayen.lifecycle.Constructable;

import com.google.common.collect.Maps;

/**
 * Repository for {@link HibernateProcedureDescriptionGeneratorFactory}
 *
 * @author Carsten Hollmann <c.hollmann@52north.org>
 * @since 4.2.0
 *
 */
public class HibernateProcedureDescriptionGeneratorFactoryRepository
        extends AbstractComponentRepository<HibernateProcedureDescriptionGeneratorKey, HibernateProcedureDescriptionGenerator, HibernateProcedureDescriptionGeneratorFactory>
        implements Constructable {

    @Deprecated
    private static HibernateProcedureDescriptionGeneratorFactoryRepository instance;

    private final Map<HibernateProcedureDescriptionGeneratorKey, Producer<HibernateProcedureDescriptionGenerator>> factories = Maps.newHashMap();

    @Autowired(required = false)
    private Collection<HibernateProcedureDescriptionGenerator> components;

    @Autowired(required = false)
    private Collection<HibernateProcedureDescriptionGeneratorFactory> componentFactories;

    @Override
    public void init() {
        HibernateProcedureDescriptionGeneratorFactoryRepository.instance = this;
        Map<HibernateProcedureDescriptionGeneratorKey, Producer<HibernateProcedureDescriptionGenerator>> implementations
                = getUniqueProviders(this.components, this.componentFactories);
        this.factories.clear();
        this.factories.putAll(implementations);
    }

    public HibernateProcedureDescriptionGenerator getFactory(String descriptionFormat) {
        return getFactory(new HibernateProcedureDescriptionGeneratorKey(descriptionFormat));
    }

    public HibernateProcedureDescriptionGenerator getFactory(HibernateProcedureDescriptionGeneratorKey key) {
        return Optional.ofNullable(factories.get(key)).map(Producer::get).orElse(null);
    }

    /**
     * Checks if a factory is available to generate the description
     *
     * @param descriptionFormat Default format
     *
     * @return If a factory is available
     */
    public boolean hasHibernateProcedureDescriptionGeneratorFactory(String descriptionFormat) {
        return this.factories.containsKey(new HibernateProcedureDescriptionGeneratorKey(descriptionFormat));
    }

    @Deprecated
    public static HibernateProcedureDescriptionGeneratorFactoryRepository getInstance() {
        return HibernateProcedureDescriptionGeneratorFactoryRepository.instance;
    }

}
