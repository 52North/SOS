/**
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
package org.n52.svalbard.encode;

import com.google.common.collect.Lists;
import net.opengis.drt.x10.DeleteResultTemplateResponseDocument;
import net.opengis.drt.x10.DeleteResultTemplateResponseType;
import org.hamcrest.CoreMatchers;
import org.hamcrest.core.Is;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.n52.sos.exception.ows.concrete.MissingServiceParameterException;
import org.n52.sos.exception.ows.concrete.MissingVersionParameterException;
import org.n52.sos.exception.ows.concrete.UnsupportedEncoderInputException;
import org.n52.sos.ogc.ows.OwsExceptionReport;
import org.n52.sos.response.DeleteResultTemplateResponse;
/**
 *
 * @author <a href="mailto:e.h.juerrens@52north.org">Eike Hinderk
 * J&uuml;rrens</a>
 */
public class DeleteResultTemplateEncoderTest {
    
    @Rule
    public ExpectedException thrown = ExpectedException.none();
    
    @Test
    public void shouldThrowExceptionOnNullInput() throws OwsExceptionReport {
        thrown.expect(UnsupportedEncoderInputException.class);
        thrown.expectMessage(String.format("Encoder %s can not encode 'null'",
                DeleteResultTemplateEncoder.class.getName()));
        
        new DeleteResultTemplateEncoder().create(null);
    }
    
    @Test
    public void shouldThrowExceptionOnMissingServiceAndVersionParameter() throws OwsExceptionReport {
        thrown.expect(new CompositeExceptionMatcher()
                .with(MissingServiceParameterException.class)
                .with(MissingVersionParameterException.class));
        
        new DeleteResultTemplateEncoder().create(new DeleteResultTemplateResponse());
    }
    
    @Test
    public void shouldEncodeEmptyResponse() throws OwsExceptionReport {
        DeleteResultTemplateResponseDocument encodedResponse = 
                (DeleteResultTemplateResponseDocument)
                new DeleteResultTemplateEncoder().create(
                (DeleteResultTemplateResponse) new DeleteResultTemplateResponse()
                        .setService("test-service")
                        .setVersion("test-version"));
        
        Assert.assertThat(encodedResponse.getDeleteResultTemplateResponse(), CoreMatchers.notNullValue());
    }
    
    @Test
    public void shouldEncodeResultTemplateList() throws OwsExceptionReport {
        DeleteResultTemplateResponseDocument encodedResponse = 
                (DeleteResultTemplateResponseDocument)
                new DeleteResultTemplateEncoder().create(
                (DeleteResultTemplateResponse) new DeleteResultTemplateResponse()
                        .addDeletedResultTemplates(Lists.newArrayList(
                                "test-result-template-1",
                                "test-result-template-2"))
                        .setService("test-service")
                        .setVersion("test-version"));
        
        final DeleteResultTemplateResponseType drtt = encodedResponse.getDeleteResultTemplateResponse();
        
        Assert.assertThat(drtt.sizeOfDeletedTemplateArray(), Is.is(2));
        Assert.assertThat(drtt.getDeletedTemplateArray(0), Is.is("test-result-template-1"));
        Assert.assertThat(drtt.getDeletedTemplateArray(1), Is.is("test-result-template-2"));
    }
    
}
