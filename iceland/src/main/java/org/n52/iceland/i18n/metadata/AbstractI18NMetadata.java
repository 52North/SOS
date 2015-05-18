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
package org.n52.iceland.i18n.metadata;

import java.util.Locale;
import java.util.Set;

import org.n52.iceland.i18n.MultilingualString;

import com.google.common.base.MoreObjects;
import com.google.common.base.MoreObjects.ToStringHelper;
import com.google.common.base.Objects;
import com.google.common.collect.Sets;

public abstract class AbstractI18NMetadata {

    private final String identifier;
    private final MultilingualString name;
    private final MultilingualString description;

    /**
     * constructor
     *
     * @param id          The identifier of this object
     * @param name        The multilingual name of this object
     * @param description the multilingual description of this object
     */
    public AbstractI18NMetadata(String id,
                        MultilingualString name,
                        MultilingualString description) {
        this.identifier = id;
        this.name = newIfNull(name);
        this.description = newIfNull(description);
    }

    /**
     * constructor
     *
     * @param id The identifier of this object
     */
    public AbstractI18NMetadata(String id) {
        this(id, null, null);
    }

    /**
     * The the identifier of this object
     *
     * @return The object identifier
     */
    public String getIdentifier() {
        return identifier;
    }

    /**
     * @return the multilingual name of this object
     */
    public MultilingualString getName() {
        return this.name;
    }

    /**
     * @return the multilingual description of this object
     */
    public MultilingualString getDescription() {
        return this.description;
    }

    /**
     * @return a {@link ToStringHelper} filled with the state of this class
     */
    protected ToStringHelper toStringHelper() {
        return MoreObjects.toStringHelper(this)
                .add("identifier", getIdentifier())
                .add("name", getName())
                .add("description", getDescription());
    }

    @Override
    public String toString() {
        return toStringHelper().toString();
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getIdentifier(), getName(), getDescription());
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof AbstractI18NMetadata) {
            AbstractI18NMetadata that = (AbstractI18NMetadata) o;
            return Objects.equal(this.getIdentifier(), that.getIdentifier()) &&
                   Objects.equal(this.getName(), that.getName()) &&
                   Objects.equal(this.getDescription(), that.getDescription());
        }
        return false;
    }

    /**
     * @return a unmodifiable set of all {@link Locale}s present in this object.
     */
    public Set<Locale> getLocales() {
        return Sets.union(getName().getLocales(),
                          getDescription().getLocales());
    }

    /**
     * Creates a new {@link MultilingualString} if the supplied string is
     * {@code null}.
     *
     * @param string the string
     *
     * @return either {@code string} or a new {@code MultilingualString}
     */
    protected static MultilingualString newIfNull(MultilingualString string) {
        return string == null ? new MultilingualString() : string;
    }

}
