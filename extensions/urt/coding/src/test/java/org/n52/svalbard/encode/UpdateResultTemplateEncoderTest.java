/*
 * Copyright (C) 2017 52north.org
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 */
package org.n52.svalbard.encode;
/**
 *
 * @author <a href="mailto:e.h.juerrens@52north.org">Eike Hinderk
 * J&uuml;rrens</a>
 */
public class UpdateResultTemplateEncoderTest {
    /*
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
    */
}
