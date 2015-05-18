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
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * Implementation based on {@link LinkedList}s and a {@link HashMap}.
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
public class LinkedListMultiMap<K, V> extends AbstractMultiHashMap<K, V, List<V>> implements ListMultiMap<K, V>,
        Serializable {
    private static final long serialVersionUID = 5709659568351160402L;

    public LinkedListMultiMap(Map<? extends K, ? extends List<V>> m) {
        super(m);
    }

    public LinkedListMultiMap(int initialCapacity) {
        super(initialCapacity);
    }

    public LinkedListMultiMap(int initialCapacity, float loadFactor) {
        super(initialCapacity, loadFactor);
    }

    public LinkedListMultiMap() {
        super();
    }

    @Override
    protected List<V> newCollection() {
        return new LinkedList<V>();
    }
}
