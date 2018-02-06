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
package org.n52.sos.ds.procedure.generator;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.Optional;

import javax.inject.Inject;

import org.n52.janmayen.Producer;
import org.n52.janmayen.component.AbstractComponentRepository;
import org.n52.janmayen.component.Component;
import org.n52.janmayen.component.ComponentFactory;
import org.n52.janmayen.lifecycle.Constructable;

import com.google.common.collect.Maps;

public abstract class AbstractProcedureDescriptionGeneratorFactoryRepository<K, C extends Component<K>, F extends ComponentFactory<K, C>>
        extends
        AbstractComponentRepository<K, C, F>
        implements
        Constructable {

    private final Map<K, Producer<C>> factories = Maps.newHashMap();

    @Inject
    private Optional<Collection<C>> components = Optional.of(Collections.emptyList());

    @Inject
    private Optional<Collection<F>> componentFactories = Optional.of(Collections.emptyList());

    @Override
    public void init() {
        Map<K, Producer<C>> implementations = getUniqueProviders(this.components, this.componentFactories);
        this.factories.clear();
        this.factories.putAll(implementations);
    }

    public abstract C getFactory(String descriptionFormat);

    public abstract C getFactory(K key);

    /**
     * Checks if a factory is available to generate the description
     *
     * @param descriptionFormat
     *            Default format
     *
     * @return If a factory is available
     */
    public abstract boolean hasProcedureDescriptionGeneratorFactory(String descriptionFormat);


   protected Map<K, Producer<C>> getFactories() {
       return factories;
   }
}
