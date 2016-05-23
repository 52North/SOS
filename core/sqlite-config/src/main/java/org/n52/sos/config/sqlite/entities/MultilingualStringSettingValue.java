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
package org.n52.sos.config.sqlite.entities;

import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;

import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.MapKeyColumn;

import org.n52.sos.config.SettingType;
import org.n52.sos.config.SettingValue;
import org.n52.sos.i18n.LocalizedString;
import org.n52.sos.i18n.MultilingualString;

import com.google.common.collect.Maps;

@Entity(name = "multilingual_string_settings")
public class MultilingualStringSettingValue extends AbstractSettingValue<MultilingualString> {
    private static final long serialVersionUID = 4066050522655079267L;

    @ElementCollection(fetch = FetchType.EAGER)
    @MapKeyColumn(name = "lang")
    @Column(name = "value")
    @CollectionTable(name = "multilingual_string_settings_values",
                     joinColumns = @JoinColumn(
                             name = "identifier",
                             referencedColumnName = "identifier"))
    private Map<String, String> value;

    @Override
    public MultilingualString getValue() {
        if (this.value == null) {
            return null;
        } else {
            MultilingualString value = new MultilingualString();
            for (Entry<String, String> e : this.value.entrySet()) {
                value.addLocalization(
                        new LocalizedString(new Locale(e.getKey()), e.getValue()));
            }
            return value;
        }
    }

    @Override
    public SettingValue<MultilingualString> setValue(MultilingualString value) {
        if (value == null) {
            this.value = null;
        } else {

            this.value = Maps.newHashMapWithExpectedSize(value.size());
            for (Locale locale : value.getLocales()) {
                this.value.put(locale.toString(),
                               value.getLocalization(locale).get().getText());
            }
        }
        return this;
    }

    @Override
    public SettingType getType() {
        return SettingType.MULTILINGUAL_STRING;
    }

}
