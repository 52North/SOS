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

public class I18NProcedureMetadata extends AbstractI18NMetadata {
    private final MultilingualString shortName;
    private final MultilingualString longName;

    public I18NProcedureMetadata(String id,
                               MultilingualString name,
                               MultilingualString description,
                               MultilingualString shortName,
                               MultilingualString longName) {
        super(id, name, description);
        this.shortName = newIfNull(shortName);
        this.longName = newIfNull(longName);
    }

    public I18NProcedureMetadata(String id) {
        this(id, null, null, null, null);
    }

    public MultilingualString getShortName() {
        return this.shortName;
    }

    public MultilingualString getLongName() {
        return this.longName;
    }

    @Override
    public Set<Locale> getLocales() {
        return Sets.union(getShortName().getLocales(),
                          Sets.union(getLongName().getLocales(),
                                     super.getLocales()));
    }

    @Override
    public String toString() {
        return toStringHelper().toString();
    }

    @Override
    protected ToStringHelper toStringHelper() {
        return super.toStringHelper()
                .add("shortName", getShortName())
                .add("longName", getLongName());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(super.hashCode(), getShortName(), getLongName());
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof I18NProcedureMetadata) {
            I18NProcedureMetadata that = (I18NProcedureMetadata) o;
            return super.equals(that) &&
                   Objects.equal(this.getShortName(), that.getShortName()) &&
                   Objects.equal(this.getLongName(), that.getLongName());
        }
        return false;
    }

}
