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

import java.util.Collections;
import java.util.Set;

import com.google.common.base.Joiner;
import com.google.common.base.Objects;
import com.google.common.collect.Sets;

/**
 * TODO JavaDoc
 * 
 * @author Christian Autermann <c.autermann@52north.org>
 * 
 * @since 4.0.0
 */
public class CompositeSimilar<T extends Similar<T>> implements Similar<T> {
    private final Set<T> similars;

    public CompositeSimilar(Iterable<T> similars) {
        this.similars = Sets.newHashSet(similars);
    }

    protected Set<T> getSimilars() {
        return Collections.unmodifiableSet(this.similars);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj != null && obj.getClass() == getClass()) {
            @SuppressWarnings(value = "unchecked")
            CompositeSimilar<T> key = (CompositeSimilar) obj;
            return key.matches(getSimilars()) && matches(key.getSimilars());
        }
        return false;
    }

    @Override
    public String toString() {
        return String.format("%s[%s]", getClass().getSimpleName(), Joiner.on(", ").join(similars));
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(similars.toArray());
    }

    public boolean matches(Set<T> toTest) {
        return toTest == null ? similars.isEmpty() : toTest.containsAll(similars);
    }

    @Override
    public int getSimilarity(T key) {
        if (key != null && key.getClass() == getClass()) {
            CompositeSimilar<T> cek = (CompositeSimilar<T>) key;
            if (cek.getSimilars().size() != similars.size()) {
                return -1;
            }
            int similarity = 0;
            for (T k1 : similars) {
                int s = -1;
                for (T k2 : cek.getSimilars()) {
                    int ks = k1.getSimilarity(k2);
                    s = (s < 0) ? ks : Math.min(s, ks);
                    if (s == 0) {
                        break;
                    }
                }
                if (s < 0) {
                    return -1;
                } else {
                    similarity += s;
                }
            }
            return similarity;
        }
        return -1;
    }
}
