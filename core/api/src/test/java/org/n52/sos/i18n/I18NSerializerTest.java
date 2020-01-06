/*
 * Copyright (C) 2012-2020 52°North Initiative for Geospatial Open Source
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
package org.n52.sos.i18n;

import org.hamcrest.Matchers;

import java.util.Locale;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ErrorCollector;
import org.junit.rules.ExpectedException;

import org.n52.iceland.i18n.I18NSerializer;
import org.n52.janmayen.i18n.MultilingualString;

/**
 * TODO JavaDoc
 *
 * @author Christian Autermann
 */
public class I18NSerializerTest {

    private static final String TEXT = "text!;\")=?§";
    @Rule
    public final ErrorCollector errors = new ErrorCollector();
    @Rule
    public final ExpectedException thrown = ExpectedException.none();

    @Test
    public void testSingle() {
        test(new MultilingualString().addLocalization(Locale.ENGLISH, "text"));
        test(new MultilingualString()
                .addLocalization(Locale.ENGLISH, TEXT));
    }

    @Test
    public void testMultiple() {
        test(new MultilingualString()
                .addLocalization(Locale.ENGLISH, TEXT)
                .addLocalization(Locale.CANADA_FRENCH, TEXT)
                .addLocalization(Locale.TRADITIONAL_CHINESE, TEXT)
                .addLocalization(Locale.GERMANY, TEXT)
                .addLocalization(Locale.KOREAN, TEXT));
    }

    private void test(MultilingualString string) {
        errors.checkThat(string, Matchers.is(Matchers.notNullValue()));
        String encoded = new I18NSerializer().encode(string);
        System.out.println(encoded);
        errors.checkThat(encoded.isEmpty(), Matchers.is(string.isEmpty()));
        MultilingualString decoded = new I18NSerializer().decode(encoded);
        errors.checkThat(decoded, Matchers.is(Matchers.equalTo(string)));
    }

}
