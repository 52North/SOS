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
package org.n52.sos.util;

import java.io.Serializable;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * Abstract implementation that delegates to a {@link HashMap}.
 * 
 * @param <K>
 *            the key type
 * @param <V>
 *            the value type
 * @param <C>
 *            the collection type
 * 
 * @author Christian Autermann <c.autermann@52north.org>
 * @since 4.0.0
 * 
 */
public abstract class AbstractMultiHashMap<K, V, C extends Collection<V>> extends AbstractDelegatingMultiMap<K, V, C>
        implements MultiMap<K, V, C>, Map<K, C>, Serializable {
    private static final long serialVersionUID = 5980618435134246476L;

    private final Map<K, C> delegate;

    public AbstractMultiHashMap(Map<? extends K, ? extends C> m) {
        delegate = new HashMap<K, C>(m);
    }

    public AbstractMultiHashMap(int initialCapacity) {
        delegate = new HashMap<K, C>(initialCapacity);
    }

    public AbstractMultiHashMap(int initialCapacity, float loadFactor) {
        delegate = new HashMap<K, C>(initialCapacity, loadFactor);
    }

    public AbstractMultiHashMap() {
        this.delegate = new HashMap<K, C>();
    }

    @Override
    protected Map<K, C> getDelegate() {
        return delegate;
    }
}
