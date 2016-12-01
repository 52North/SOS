/*
 * Copyright (C) 2012-2016 52°North Initiative for Geospatial Open Source
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
package org.n52.sos.coding.json;

import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.Assert.assertThat;
import static org.junit.internal.matchers.ThrowableMessageMatcher.hasMessage;
import static org.n52.sos.coding.json.matchers.JSONMatchers.equalTo;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import org.n52.janmayen.http.MediaTypes;
import org.n52.shetland.ogc.ows.exception.NoApplicableCodeException;
import org.n52.sos.encode.json.JSONEncoder;
import org.n52.sos.encode.json.JSONEncoderKey;
import org.n52.svalbard.EncodingContext;
import org.n52.svalbard.encode.exception.EncodingException;

/**
 * TODO JavaDoc
 *
 * @author Christian Autermann <c.autermann@52north.org>
 *
 */
public class JSONEncoderTest {


    private final JSONEncoder<String> encoder = new JSONEncoderForTesting(String.class);
    private final JSONEncoder<String> throwingEncoder = new JSONEncoderForExceptionTesting(String.class);

    @Rule
    public final ExpectedException thrown = ExpectedException.none();

    @Test
    public void testEncoderKeyTypes() {
        assertThat(encoder.getKeys(), is(notNullValue()));
        assertThat(encoder.getKeys(), hasSize(1));
        assertThat(encoder.getKeys(), hasItem(new JSONEncoderKey(String.class)));
    }

    @Test
    public void testSupportedTypes() {
        assertThat(encoder.getSupportedTypes(), is(notNullValue()));
        assertThat(encoder.getSupportedTypes().size(), is(0));
    }

    @Test
    public void testContentType() {
        assertThat(encoder.getContentType(), is(MediaTypes.APPLICATION_JSON));
    }

    @Test
    public void testEncode() throws EncodingException {
        assertThat(encoder.encode("test"), equalTo("test"));
    }

    @Test
    public void testEncodeWithHelperValues() throws EncodingException {
        assertThat(encoder.encode("test", EncodingContext.empty()), equalTo("test"));
    }

    @Test
    public void testThrowingEncoder() throws EncodingException {
        thrown.expect(NoApplicableCodeException.class);
        thrown.expectCause(hasMessage(is("message")));
        throwingEncoder.encode("test");
    }
}
