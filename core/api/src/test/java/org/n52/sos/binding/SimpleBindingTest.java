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
package org.n52.sos.binding;

import static com.google.common.collect.Lists.newArrayList;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.n52.sos.exception.HTTPException;
import org.n52.sos.response.GetObservationResponse;
import org.n52.sos.util.http.MediaType;
import org.n52.sos.util.http.MediaTypes;

/**
 * @since 4.0.0
 * 
 */
public class SimpleBindingTest {
    private static final List<MediaType> XML = newArrayList(MediaTypes.APPLICATION_XML);

    private static final List<MediaType> JSON = newArrayList(MediaTypes.APPLICATION_JSON);

    private static final List<MediaType> ANYTHING = newArrayList(MediaTypes.WILD_CARD);

    private static final List<MediaType> XML_AND_JSON = newArrayList(MediaTypes.APPLICATION_XML,
            MediaTypes.APPLICATION_JSON);

    private static final List<MediaType> NOTHING = newArrayList();

    private TestBinding binding;

    private GetObservationResponse response;

    private MediaType defaultContentType;

    @Before
    public void setUp() {
        this.response = new GetObservationResponse();
        this.binding = new TestBinding();
        this.defaultContentType = binding.getDefaultContentType();
    }

    @Test
    public void should_use_default_ContentType() throws HTTPException {
        assertThat(chosenContentTypeWithAccept(NOTHING), is(defaultContentType));
    }

    @Test
    public void should_Accept_Defaul_ContentType() throws HTTPException {
        assertThat(chosenContentTypeWithAccept(XML), is(defaultContentType));
    }

    @Test(expected = HTTPException.class)
    public void should_Accept_NotSupported_ContentType() throws HTTPException {
        assertThat(chosenContentTypeWithAccept(JSON), is(MediaTypes.APPLICATION_JSON));
    }

    @Test
    public void should_Accept_Wildcard_ContentType() throws HTTPException {
        assertThat(chosenContentTypeWithAccept(ANYTHING), is(defaultContentType));
    }

    @Test(expected = HTTPException.class)
    public void should_ResponseFormat_NotSupported_ContentType() throws HTTPException {
        response.setContentType(MediaTypes.APPLICATION_NETCDF);
        assertThat(chosenContentTypeWithAccept(NOTHING), is(defaultContentType));
    }

    @Test
    public void should_Acept_Equals_ResponseFormat_ContentType() throws HTTPException {
        response.setContentType(MediaTypes.APPLICATION_XML);
        assertThat(chosenContentTypeWithAccept(XML), is(defaultContentType));
    }

    @Test(expected = HTTPException.class)
    public void should_Accept_NotContains_ResponseFormat_ContentType() throws HTTPException {
        response.setContentType(MediaTypes.APPLICATION_NETCDF);
        assertThat(chosenContentTypeWithAccept(XML_AND_JSON), is(defaultContentType));
    }

    @Test
    public void should_Accept_Wildcard_ResponseFormat_ContentType() throws HTTPException {
        response.setContentType(MediaTypes.APPLICATION_NETCDF);
        assertThat(chosenContentTypeWithAccept(ANYTHING), is(MediaTypes.APPLICATION_NETCDF));
    }

    private MediaType chosenContentTypeWithAccept(List<MediaType> accept) throws HTTPException {
        return binding.chooseResponseContentType(response, accept, defaultContentType);
    }

}
