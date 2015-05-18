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
package org.n52.iceland.util;

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
