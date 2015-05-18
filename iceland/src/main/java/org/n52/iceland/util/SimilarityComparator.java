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

import java.util.Comparator;

/**
 * TODO JavaDoc
 * 
 * @param <T>
 *            the type
 * 
 * @author Christian Autermann <c.autermann@52north.org>
 * 
 * @since 4.0.0
 */
public class SimilarityComparator<T extends Similar<T>> implements Comparator<T> {
    private final T ref;

    public SimilarityComparator(T ref) {
        this.ref = ref;
    }

    @Override
    public int compare(T o1, T o2) {
        if (o1 == o2) {
            return 0;
        }
        // FIXME this conflicts with the contract of compare
        if (o1 == null || o2 == null) {
            return -1;
        }
        // check for equals after strict == and null checks
        if (o1.equals(o2)) {
            return 0;
        }        
        int s1 = o1.getSimilarity(ref);
        int s2 = o2.getSimilarity(ref);
        //check for inheritance
        if (s1 == s2 && !o1.getClass().equals(o2.getClass())) {
            if (o1.getClass().isAssignableFrom(o2.getClass())) {
                return 1;
            } else if (o2.getClass().isAssignableFrom(o1.getClass())) {
                return -1;
            }
        }
        if (s1 == 0) {
            return -1;
        }
        if (s2 == 0) {
            return 1;
        }
        if (s1 < 0) {
            return s2 < 0 ? 0 : 1;
        } else if (s2 < 0 || s1 < s2) {
            return -1;
        } else {
            return 1;
        }
    }
}
