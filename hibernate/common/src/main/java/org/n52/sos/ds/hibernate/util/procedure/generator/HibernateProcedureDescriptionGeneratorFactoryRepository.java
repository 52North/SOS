/*
 * Copyright (C) 2012-2016 52°North Initiative for Geospatial Open Source
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

import org.springframework.beans.factory.annotation.Autowired;

import org.n52.iceland.component.AbstractComponentRepository;
import org.n52.iceland.lifecycle.Constructable;
import org.n52.iceland.util.Producer;

import com.google.common.collect.Maps;

/**
 * Repository for {@link HibernateProcedureDescriptionGeneratorFactory}
 *
 * @author Carsten Hollmann <c.hollmann@52north.org>
 * @since 4.2.0
 *
 */
public class HibernateProcedureDescriptionGeneratorFactoryRepository
    extends AbstractComponentRepository<HibernateProcedureDescriptionGeneratorFactoryKey, HibernateProcedureDescriptionGeneratorFactory, HibernateProcedureDescriptionGeneratorFactoryFactory> implements Constructable {

    @Deprecated
    private static HibernateProcedureDescriptionGeneratorFactoryRepository instance;

    private final Map<HibernateProcedureDescriptionGeneratorFactoryKey, Producer<HibernateProcedureDescriptionGeneratorFactory>> factories = Maps.newHashMap();

    @Autowired(required = false)
    private Collection<HibernateProcedureDescriptionGeneratorFactory> components;

    @Autowired(required = false)
    private Collection<HibernateProcedureDescriptionGeneratorFactoryFactory> componentFactories;

    @Override
    public void init() {
        HibernateProcedureDescriptionGeneratorFactoryRepository.instance = this;
        Map<HibernateProcedureDescriptionGeneratorFactoryKey, Producer<HibernateProcedureDescriptionGeneratorFactory>> implementations
                = getUniqueProviders(this.components, this.componentFactories);
        this.factories.clear();
        this.factories.putAll(implementations);
    }

    public HibernateProcedureDescriptionGeneratorFactory getFactory(final String descriptionFormat) {
        return getFactory(new HibernateProcedureDescriptionGeneratorFactoryKey(descriptionFormat));
    }

    public HibernateProcedureDescriptionGeneratorFactory getFactory(final HibernateProcedureDescriptionGeneratorFactoryKey key) {
        Producer<HibernateProcedureDescriptionGeneratorFactory> producer = factories.get(key);
        return producer == null ? null : producer.get();
    }

    /**
     * Checks if a factory is available to generate the description
     *
     * @param descriptionFormat
     *            Default format
     * @return If a factory is available
     */
    public boolean hasHibernateProcedureDescriptionGeneratorFactory(final String descriptionFormat) {
        return this.factories.containsKey(new HibernateProcedureDescriptionGeneratorFactoryKey(descriptionFormat));
    }


    @Deprecated
    public static HibernateProcedureDescriptionGeneratorFactoryRepository getInstance() {
        return HibernateProcedureDescriptionGeneratorFactoryRepository.instance;
    }

}
