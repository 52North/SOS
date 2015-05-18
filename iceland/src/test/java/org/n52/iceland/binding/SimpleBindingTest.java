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
package org.n52.iceland.binding;

import static com.google.common.collect.Lists.newArrayList;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.n52.iceland.exception.HTTPException;
import org.n52.iceland.response.TestResponse;
import org.n52.iceland.util.http.MediaType;
import org.n52.iceland.util.http.MediaTypes;

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

    private TestResponse response;

    private MediaType defaultContentType;

    @Before
    public void setUp() {
        this.response = new TestResponse();
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
