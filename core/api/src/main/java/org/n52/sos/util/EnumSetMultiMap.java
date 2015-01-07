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

import java.util.EnumMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * {@linkplain SetMultiMap} implementation backed with a {@link EnumMap}.
 * 
 * @param <K>
 *            the key type
 * @param <V>
 *            the value type
 * 
 * @author Christian Autermann <c.autermann@52north.org>
 * @since 4.0.0
 * 
 */
public class EnumSetMultiMap<K extends Enum<K>, V> extends AbstractDelegatingMultiMap<K, V, Set<V>> implements
        SetMultiMap<K, V> {
    private static final long serialVersionUID = 1343214593123842785L;

    private final Map<K, Set<V>> delegate;

    public EnumSetMultiMap(Class<K> keyType) {
        this.delegate = new EnumMap<K, Set<V>>(keyType);
    }

    public EnumSetMultiMap(EnumMap<K, ? extends Set<V>> m) {
        this.delegate = new EnumMap<K, Set<V>>(m);
    }

    public EnumSetMultiMap(Map<K, ? extends Set<V>> m) {
        this.delegate = new EnumMap<K, Set<V>>(m);
    }

    @Override
    protected Map<K, Set<V>> getDelegate() {
        return this.delegate;
    }

    @Override
    protected Set<V> newCollection() {
        return new HashSet<V>();
    }
}
