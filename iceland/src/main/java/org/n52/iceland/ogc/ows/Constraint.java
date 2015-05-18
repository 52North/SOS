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
import java.util.List;

import com.google.common.base.Objects;

/**
 * TODO JavaDoc
 * 
 * @author Christian Autermann <c.autermann@52north.org>
 * 
 * @since 4.0.0
 */
public class Constraint implements Comparable<Constraint> {
    private final String name;

    private final List<OwsParameterValue> values;

    public Constraint(String name, List<OwsParameterValue> values) {
        if (name == null) {
            throw new NullPointerException();
        }
        if (values == null) {
            throw new NullPointerException();
        }
        this.name = name;
        this.values = values;
    }

    public Constraint(String name, OwsParameterValue value) {
        this(name, Collections.singletonList(value));
    }

    public String getName() {
        return name;
    }

    public List<OwsParameterValue> getValues() {
        return Collections.unmodifiableList(values);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getName(), getValues());
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Constraint) {
            Constraint c = (Constraint) obj;
            return Objects.equal(getName(), c.getName()) && Objects.equal(getValues(), c.getValues());
        }
        return false;
    }

    @Override
    public String toString() {
        return String.format("Constraint[name=%s, values=%s]", getName(), getValues());
    }

    @Override
    public int compareTo(Constraint o) {
        return getName().compareTo(o.getName());
    }
}
