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

import com.google.common.base.MoreObjects.ToStringHelper;
import com.google.common.base.Objects;
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
