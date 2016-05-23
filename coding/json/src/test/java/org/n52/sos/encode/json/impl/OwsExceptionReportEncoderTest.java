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
package org.n52.sos.encode.json.impl;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.Assert.assertThat;
import static org.n52.sos.coding.json.JSONConstants.EXCEPTIONS;
import static org.n52.sos.coding.json.JSONConstants.LOCATOR;
import static org.n52.sos.coding.json.JSONConstants.TEXT;
import static org.n52.sos.coding.json.JSONConstants.VERSION;
import static org.n52.sos.coding.json.matchers.Does.does;
import static org.n52.sos.coding.json.matchers.JSONMatchers.arrayOfLength;
import static org.n52.sos.coding.json.matchers.JSONMatchers.equalTo;
import static org.n52.sos.coding.json.matchers.JSONMatchers.exist;
import static org.n52.sos.coding.json.matchers.JSONMatchers.isObject;
import static org.n52.sos.coding.json.matchers.ValidationMatchers.instanceOf;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ErrorCollector;
import org.n52.sos.coding.json.SchemaConstants;
import org.n52.sos.encode.json.JSONEncodingException;
import org.n52.sos.exception.ows.concrete.EncoderResponseUnsupportedException;

import com.fasterxml.jackson.databind.JsonNode;

/**
 * TODO JavaDoc
 * 
 * @author Christian Autermann <c.autermann@52north.org>
 * 
 * @since 4.0.0
 */
public class OwsExceptionReportEncoderTest {
    private OwsExceptionReportEncoder enc;

    @Rule
    public final ErrorCollector e = new ErrorCollector();

    @Before
    public void setUp() {
        enc = new OwsExceptionReportEncoder();
    }

    @Test
    public void testExceptionWithoutCause() throws JSONEncodingException {
        final EncoderResponseUnsupportedException owse = new EncoderResponseUnsupportedException();
        owse.setVersion("2.0.0");
        final JsonNode json = enc.encodeJSON(owse);
        assertThat(json, is(notNullValue()));
        final String message = "The encoder response is not supported!";
        e.checkThat(json, is(instanceOf(SchemaConstants.Common.EXCEPTION_REPORT)));
        e.checkThat(json.path(VERSION), is(equalTo("2.0.0")));
        e.checkThat(json.path(EXCEPTIONS), is(arrayOfLength(1)));
        e.checkThat(json.path(EXCEPTIONS).path(0), isObject());
        e.checkThat(json.path(EXCEPTIONS).path(0).path(LOCATOR), does(not(exist())));
        e.checkThat(json.path(EXCEPTIONS).path(0).path(TEXT), is(equalTo(message)));
    }
}
