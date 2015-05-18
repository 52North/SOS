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

import java.io.Serializable;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * 
 * Implementation based on {@link HashSet}s and a {@link HashMap}.
 * 
 * @param <K>
 * @param <V>
 * 
 * @author Christian Autermann <c.autermann@52north.org>
 * @since 4.0.0
 * 
 */
public class HashSetMultiMap<K, V> extends AbstractMultiHashMap<K, V, Set<V>> implements SetMultiMap<K, V>,
        Serializable {
    private static final long serialVersionUID = 7628009915817528370L;

    public HashSetMultiMap(Map<? extends K, ? extends Set<V>> m) {
        super(m);
    }

    public HashSetMultiMap(int initialCapacity) {
        super(initialCapacity);
    }

    public HashSetMultiMap(int initialCapacity, float loadFactor) {
        super(initialCapacity, loadFactor);
    }

    public HashSetMultiMap() {
        super();
    }

    @Override
    protected Set<V> newCollection() {
        return new HashSet<V>();
    }
}
