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
package org.n52.svalbard.decode;

/**
 * @author <a href="mailto:e.h.juerrens@52north.org">Eike Hinderk J&uuml;rrens</a>
 * 
 * @since 4.4.0
 */
public class UpdateResultTemplateDecoderTest {
/*
    private UpdateResultTemplateDecoder decoder;
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
 */   
}
