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

import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;

/**
 * TODO JavaDoc
 * 
 * @param <T>
 *            the type to compare
 * @param <K>
 *            the similarity type of {@code T}
 * 
 * @author Christian Autermann <c.autermann@52north.org>
 * 
 * @since 4.0.0
 */
public abstract class ProxySimilarityComparator<T, K extends Similar<K>> implements Comparator<T> {
    private final SimilarityComparator<K> comp;

    public ProxySimilarityComparator(K ref) {
        this.comp = new SimilarityComparator<K>(ref);
    }

    @Override
    public int compare(T o1, T o2) {
        int compResult = comp.compare(Collections.min(getSimilars(o1), comp), Collections.min(getSimilars(o2), comp));
        // check inheritance hierarchy if key matches are equal and classes are not
        if (compResult == 0 && !o1.getClass().equals(o2.getClass())) {
            if (o1.getClass().isAssignableFrom(o2.getClass())) {
                return 1;
            } else if (o2.getClass().isAssignableFrom(o1.getClass())) {
                return -1;
            }
        }
        return compResult;
    }

    protected abstract Collection<K> getSimilars(T t);
}
