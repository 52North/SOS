/**
 * Copyright (C) 2012-2015 52°North Initiative for Geospatial Open Source
 * Software GmbH
 *
 * This program is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License version 2 as published
 * by the Free Software Foundation.
 *
 * If the program is linked with libraries which are licensed under one of
 * the following licenses, the combination of the program with the linked
 * library is not considered a "derivative work" of the program:
 *
 *     - Apache License, version 2.0
 *     - Apache Software License, version 1.0
 *     - GNU Lesser General Public License, version 3
 *     - Mozilla Public License, versions 1.0, 1.1 and 2.0
 *     - Common Development and Distribution License (CDDL), version 1.0
 *
 * Therefore the distribution of the program linked with libraries licensed
 * under the aforementioned licenses, is permitted by the copyright holders
 * if the distribution is compliant with both the GNU General Public
 * License version 2 and the aforementioned licenses.
 *
 * This program is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General
 * Public License for more details.
 */
package org.n52.sos.i18n.metadata;

import java.util.Locale;
import java.util.Set;

import org.n52.sos.i18n.MultilingualString;

import com.google.common.base.Objects;
import com.google.common.base.Objects.ToStringHelper;
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
        return Objects.toStringHelper(this)
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
