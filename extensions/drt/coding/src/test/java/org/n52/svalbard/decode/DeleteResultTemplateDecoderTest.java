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
package org.n52.svalbard.decode;

import net.opengis.drt.x10.DeleteResultTemplateDocument;
import net.opengis.drt.x10.DeleteResultTemplateType;
import net.opengis.drt.x10.DeleteResultTemplateType.Tuple;
import org.apache.xmlbeans.XmlObject;
import org.hamcrest.core.Is;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.n52.sos.exception.ows.concrete.UnsupportedDecoderInputException;
import org.n52.sos.ogc.ows.OwsExceptionReport;
import org.n52.sos.request.DeleteResultTemplateRequest;

/**
 * @author <a href="mailto:e.h.juerrens@52north.org">Eike Hinderk J&uuml;rrens</a>
 * 
 * @since 4.4.0
 */
public class DeleteResultTemplateDecoderTest {

    private DeleteResultTemplateDecoder decoder;
    private DeleteResultTemplateDocument encodedRequest;
    private DeleteResultTemplateType drtt;
    
    @Rule
    public ExpectedException thrown = ExpectedException.none();
    
    @Before
    public void setUp() {
        decoder = new DeleteResultTemplateDecoder();
        encodedRequest = DeleteResultTemplateDocument.Factory.newInstance();
        drtt = encodedRequest.addNewDeleteResultTemplate();
        drtt.setService("test-service");
        drtt.setVersion("test-version");
    }
    
    @Test
    public void shouldThrowExceptionOnWrongInput() throws OwsExceptionReport {
        thrown.expect(UnsupportedDecoderInputException.class);
        thrown.expectMessage("null can not be decoded by "
                + decoder.getClass().getName()
                + " because it is not yet implemented!");
        
        decoder.decode(XmlObject.Factory.newInstance());
    }
    
    @Test
    public void shouldDecodeServiceAndVersion() throws OwsExceptionReport {
        addResultTemplate();
        
        DeleteResultTemplateRequest decodedRequest = decoder.decode(encodedRequest);
        
        Assert.assertThat(decodedRequest.getService(), Is.is("test-service"));
        Assert.assertThat(decodedRequest.getVersion(), Is.is("test-version"));
    }

    @Test
    public void shouldDecodeResultTemplates() throws OwsExceptionReport {
        addResultTemplate();
        
        DeleteResultTemplateRequest decodedRequest = decoder.decode(encodedRequest);
        
        Assert.assertThat(decodedRequest.isSetResultTemplates(), Is.is(true));
        Assert.assertThat(decodedRequest.getResultTemplates().size(), Is.is(1));
        Assert.assertThat(decodedRequest.getResultTemplates().get(0), Is.is("test-template"));
    }
    
    @Test
    public void shouldDecodeObservedPropertyOfferingTuples() throws OwsExceptionReport {
        addObservedPropertyOfferingTuple();
        
        DeleteResultTemplateRequest decodedRequest = decoder.decode(encodedRequest);
        
        Assert.assertThat(decodedRequest.isSetObservedPropertyOfferingPairs(), Is.is(true));
        Assert.assertThat(decodedRequest.getObservedPropertyOfferingPairs().size(), Is.is(1));
        Assert.assertThat(decodedRequest.getObservedPropertyOfferingPairs().get(0).getKey(), Is.is("test-property"));
        Assert.assertThat(decodedRequest.getObservedPropertyOfferingPairs().get(0).getValue(), Is.is("test-offering"));
    }

    private void addObservedPropertyOfferingTuple() {
        Tuple t = drtt.addNewTuple();
        t.setOffering("test-offering");
        t.setObservedProperty("test-property");
    }
    
    private void addResultTemplate() {
        drtt.addNewResultTemplate().setStringValue("test-template");
    }
    
}
