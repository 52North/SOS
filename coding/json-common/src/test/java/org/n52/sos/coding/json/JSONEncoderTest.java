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
package org.n52.sos.coding.json;

import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.Assert.assertThat;
import static org.junit.internal.matchers.ThrowableMessageMatcher.hasMessage;
import static org.n52.sos.coding.json.matchers.JSONMatchers.equalTo;

import java.util.EnumMap;
import java.util.HashMap;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.n52.sos.encode.json.JSONEncoder;
import org.n52.sos.encode.json.JSONEncoderKey;
import org.n52.sos.encode.json.JSONEncodingException;
import org.n52.sos.exception.ows.NoApplicableCodeException;
import org.n52.sos.ogc.ows.OwsExceptionReport;
import org.n52.sos.ogc.sos.SosConstants.HelperValues;
import org.n52.sos.util.http.MediaTypes;

import com.fasterxml.jackson.databind.JsonNode;
import com.google.common.collect.Maps;

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
        assertThat(encoder.getEncoderKeyType(), is(notNullValue()));
        assertThat(encoder.getEncoderKeyType(), hasSize(1));
        assertThat(encoder.getEncoderKeyType(), hasItem(new JSONEncoderKey(String.class)));
    }

    @Test
    public void testSupportedTypes() {
        assertThat(encoder.getSupportedTypes(), is(notNullValue()));
        assertThat(encoder.getSupportedTypes().size(), is(0));
    }

    @Test
    public void testAddNamespacePrefixToMap() {
        HashMap<String, String> empty = Maps.newHashMap();
        encoder.addNamespacePrefixToMap(empty);
        assertThat(empty.size(), is(0));
    }

    @Test
    public void testContentType() {
        assertThat(encoder.getContentType(), is(MediaTypes.APPLICATION_JSON));
    }

    @Test
    public void testSchemaLocations() {
        assertThat(encoder.getSchemaLocations(), is(empty()));
    }

    @Test
    public void testEncode() throws OwsExceptionReport {
        assertThat(encoder.encode("test"), equalTo("test"));
    }

    @Test
    public void testEncodeWithHelperValues() throws OwsExceptionReport {
        final EnumMap<HelperValues, String> vals = Maps.newEnumMap(HelperValues.class);
        assertThat(encoder.encode("test", vals), equalTo("test"));
    }

    @Test
    public void testThrowingEncoder() throws OwsExceptionReport {
        thrown.expect(NoApplicableCodeException.class);
        thrown.expectCause(hasMessage(is("message")));
        throwingEncoder.encode("test");
    }

    @Test
    public void testConformaceClasses() throws OwsExceptionReport {
        assertThat(encoder.getConformanceClasses(), is(empty()));
    }
}
