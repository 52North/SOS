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
