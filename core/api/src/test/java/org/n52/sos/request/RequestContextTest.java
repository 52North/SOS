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
package org.n52.sos.request;

import org.hamcrest.Matchers;
import org.junit.Assert;

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
        Assert.assertThat(rc.getIPAddress().isPresent(), Matchers.is(false));
        Assert.assertThat(rc.getToken().isPresent(), Matchers.is(false));
    }

    @Test
    public void shouldNotEmptyTokenSet() {
        OwsServiceRequestContext rc = new OwsServiceRequestContext();
        rc.setToken("asfsf2");
        Assert.assertThat(rc.getIPAddress().isPresent(), Matchers.is(false));
        Assert.assertThat(rc.getToken().isPresent(), Matchers.is(true));
    }

    @Test
    public void shouldNotEmptyIpSet() {
        OwsServiceRequestContext rc = new OwsServiceRequestContext();
        rc.setIPAddress(new IPAddress("192.168.1.2"));
        Assert.assertThat(rc.getIPAddress().isPresent(), Matchers.is(true));
    }

    @Test
    public void shouldNotEmptyIpAndTokenSet() {
        OwsServiceRequestContext rc = new OwsServiceRequestContext();
        rc.setIPAddress(new IPAddress("192.168.1.1"));
        rc.setToken("asfsf");
        Assert.assertThat(rc.getIPAddress().isPresent(), Matchers.is(true));
        Assert.assertThat(rc.getToken().isPresent(), Matchers.is(true));
    }

    @Test
    public void shouldNotEmptyAcceptType() {
        OwsServiceRequestContext rc = new OwsServiceRequestContext();
        MediaType xml = new MediaType("application", "xml");
        MediaType json = new MediaType("text", "plain");
        rc.setAcceptType(Arrays.asList(xml, json));
        Assert.assertThat(rc.getAcceptType().isPresent(), Matchers.is(true));
        Assert.assertThat(rc.getAcceptType().get().size(), Matchers.is(2));
    }

    @Test
    public void shouldNotEmptyContentType() {
        OwsServiceRequestContext rc = new OwsServiceRequestContext();
        rc.setContentType("application/xml");
        Assert.assertThat(rc.getContentType().isPresent(), Matchers.is(true));
    }

}
