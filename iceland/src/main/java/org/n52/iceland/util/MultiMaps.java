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


/**
 * @author Christian Autermann <c.autermann@52north.org>
 * @since 4.0.0
 * 
 */
public final class MultiMaps {
    public static <K, V> SetMultiMap<K, V> newSetMultiMap() {
        return new HashSetMultiMap<K, V>();
    }

    public static <K extends Enum<K>, V> SetMultiMap<K, V> newSetMultiMap(Class<K> keyType) {
        return new EnumSetMultiMap<K, V>(keyType);
    }

    public static <K, V> SetMultiMap<K, V> newSynchronizedSetMultiMap() {
        return new SynchronizedSetMultiMap<K, V>();
    }

    public static <K, V> ListMultiMap<K, V> newListMultiMap() {
        return new LinkedListMultiMap<K, V>();
    }

    public static <K, V> ListMultiMap<K, V> newSynchronizedListMultiMap() {
        return new SynchronizedListMultiMap<K, V>();
    }

    private MultiMaps() {
    }
}
