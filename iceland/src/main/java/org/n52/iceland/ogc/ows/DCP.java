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
package org.n52.iceland.ogc.ows;

import java.util.Collections;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import com.google.common.base.Objects;

/**
 * @author Christian Autermann <c.autermann@52north.org>
 * 
 * @since 4.0.0
 */
public class DCP implements Comparable<DCP> {
    private final String url;

    private final SortedSet<Constraint> constraints;

    public DCP(String url) {
        this(url, (Set<Constraint>) null);
    }

    public DCP(String url, Set<Constraint> constraints) {
        this.url = url;
        if (constraints == null) {
            this.constraints = new TreeSet<Constraint>();
        } else {
            this.constraints = new TreeSet<Constraint>(constraints);
        }
    }

    public DCP(String url, Constraint constraint) {
        this(url, Collections.singleton(constraint));
    }

    public String getUrl() {
        return url;
    }

    public Set<Constraint> getConstraints() {
        return Collections.unmodifiableSet(constraints);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getUrl(), getConstraints());
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof DCP) {
            DCP o = (DCP) obj;
            return Objects.equal(getUrl(), o.getUrl()) && Objects.equal(getConstraints(), o.getConstraints());
        }
        return false;
    }

    @Override
    public String toString() {
        return String.format("DCP[]", getUrl(), getConstraints());
    }

    @Override
    public int compareTo(DCP o) {
        if (o == null) {
            throw new NullPointerException();
        }

        if (url == null ^ o.getUrl() == null) {
            return (url == null) ? -1 : 1;
        }

        if (url == null && o.getUrl() == null) {
            return 0;
        }
        
        return url.compareTo(o.getUrl());
    }
}
