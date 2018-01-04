/*
 * Copyright (C) 2012-2018 52°North Initiative for Geospatial Open Source
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
package org.n52.sos.config.sqlite;


import java.io.File;
import java.net.URI;

import org.joda.time.DateTime;

import org.n52.faroe.SettingValue;
import org.n52.faroe.SettingValueFactory;
import org.n52.janmayen.i18n.MultilingualString;
import org.n52.sos.config.sqlite.entities.BooleanSettingValue;
import org.n52.sos.config.sqlite.entities.ChoiceSettingValue;
import org.n52.sos.config.sqlite.entities.FileSettingValue;
import org.n52.sos.config.sqlite.entities.IntegerSettingValue;
import org.n52.sos.config.sqlite.entities.MultilingualStringSettingValue;
import org.n52.sos.config.sqlite.entities.NumericSettingValue;
import org.n52.sos.config.sqlite.entities.StringSettingValue;
import org.n52.sos.config.sqlite.entities.TimeInstantSettingValue;
import org.n52.sos.config.sqlite.entities.UriSettingValue;

/**
 * TODO JavaDoc
 * @author Christian Autermann
 */
public class SQLiteSettingValueFactory implements SettingValueFactory {

    @Override
    public BooleanSettingValue newBooleanSettingValue(String key, Boolean value) {
        return new BooleanSettingValue(key, value);
    }

    @Override
    public IntegerSettingValue newIntegerSettingValue(String key, Integer value) {
        return new IntegerSettingValue(key, value);
    }

    @Override
    public StringSettingValue newStringSettingValue(String key, String value) {
        return new StringSettingValue(key, value);
    }

    @Override
    public FileSettingValue newFileSettingValue(String key, File value) {
        return new FileSettingValue(key, value);
    }

    @Override
    public UriSettingValue newUriSettingValue(String key, URI value) {
        return new UriSettingValue(key, value);
    }

    @Override
    public SettingValue<Double> newNumericSettingValue(String key, Double value) {
        return new NumericSettingValue(key, value);
    }

    @Override
    public SettingValue<DateTime> newDateTimeSettingValue(String key, DateTime value) {
        return new TimeInstantSettingValue(key, value);
    }

    @Override
    public SettingValue<MultilingualString> newMultiLingualStringSettingValue(String key, MultilingualString value) {
        return new MultilingualStringSettingValue(key, value);
    }

    @Override
    public SettingValue<String> newChoiceSettingValue(String key, String value) {
        return new ChoiceSettingValue(key, value);
    }

}
