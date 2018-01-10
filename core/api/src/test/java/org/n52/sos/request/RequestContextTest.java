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
package org.n52.sos.request;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

import java.util.Arrays;

import org.junit.Test;

import org.n52.shetland.ogc.ows.service.OwsServiceRequestContext;
import org.n52.janmayen.http.MediaType;
import org.n52.janmayen.net.IPAddress;

/**
 * @since 4.0.0
 *
 */
public class RequestContextTest {

    @Test
    public void shouldEmpty() {
        OwsServiceRequestContext rc = new OwsServiceRequestContext();
        assertThat(rc.getIPAddress().isPresent(), is(false));
        assertThat(rc.getToken().isPresent(), is(false));
    }

    @Test
    public void shouldNotEmptyTokenSet() {
        OwsServiceRequestContext rc = new OwsServiceRequestContext();
        rc.setToken("asfsf");
        assertThat(rc.getIPAddress().isPresent(), is(false));
        assertThat(rc.getToken().isPresent(), is(true));
    }

    @Test
    public void shouldNotEmptyIpSet() {
        OwsServiceRequestContext rc = new OwsServiceRequestContext();
        rc.setIPAddress(new IPAddress("192.168.1.1"));
        assertThat(rc.getIPAddress().isPresent(), is(true));
    }

    @Test
    public void shouldNotEmptyIpAndTokenSet() {
        OwsServiceRequestContext rc = new OwsServiceRequestContext();
        rc.setIPAddress(new IPAddress("192.168.1.1"));
        rc.setToken("asfsf");
        assertThat(rc.getIPAddress().isPresent(), is(true));
        assertThat(rc.getToken().isPresent(), is(true));
    }

    @Test
    public void shouldNotEmptyAcceptType() {
        OwsServiceRequestContext rc = new OwsServiceRequestContext();
        MediaType xml = new MediaType("application", "xml");
        MediaType json = new MediaType("text", "plain");
        rc.setAcceptType(Arrays.asList(xml, json));
        assertThat(rc.getAcceptType().isPresent(), is(true));
        assertThat(rc.getAcceptType().get().size(), is(2));
    }

    @Test
    public void shouldNotEmptyContentType() {
        OwsServiceRequestContext rc = new OwsServiceRequestContext();
        rc.setContentType("application/xml");
        assertThat(rc.getContentType().isPresent(), is(true));
    }

}
