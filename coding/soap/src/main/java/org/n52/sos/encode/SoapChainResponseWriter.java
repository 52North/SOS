/*
 * Copyright (C) 2012-2016 52°North Initiative for Geospatial Open Source
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
package org.n52.sos.encode;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Collections;
import java.util.Set;

import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPMessage;

import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.XmlOptions;

import org.n52.iceland.coding.encode.AbstractResponseWriter;
import org.n52.svalbard.encode.Encoder;
import org.n52.svalbard.encode.EncoderKey;
import org.n52.svalbard.encode.EncoderRepository;
import org.n52.svalbard.encode.exception.EncodingException;
import org.n52.iceland.coding.encode.ResponseProxy;
import org.n52.iceland.coding.encode.ResponseWriterKey;
import org.n52.svalbard.encode.exception.NoEncoderForKeyException;
import org.n52.iceland.w3c.soap.SoapChain;
import org.n52.iceland.w3c.soap.SoapResponse;
import org.n52.janmayen.Producer;
import org.n52.sos.encode.streaming.StreamingEncoder;
import org.n52.sos.util.CodingHelper;


/**
 * Streaming SOAP response writer implementation.
 *
 * @author <a href="mailto:c.hollmann@52north.org">Carsten Hollmann</a>
 * @since 4.1.0
 *
 */
public class SoapChainResponseWriter extends AbstractResponseWriter<SoapChain> {
    public static final ResponseWriterKey KEY = new ResponseWriterKey(SoapChain.class);

    private final EncoderRepository encoderRepository;
    private final Producer<XmlOptions> xmlOptions;
    private final boolean forceStreamingEncoding;

    public SoapChainResponseWriter(EncoderRepository encoderRepository, Producer<XmlOptions> xmlOptions, boolean forceStreamingEncoding) {
        this.encoderRepository = encoderRepository;
        this.xmlOptions = xmlOptions;
        this.forceStreamingEncoding = forceStreamingEncoding;
    }

    @Override
    public Set<ResponseWriterKey> getKeys() {
        return Collections.singleton(KEY);
    }

    @Override
    public void write(SoapChain chain, OutputStream out, ResponseProxy responseProxy) throws IOException {
        try {
            write(chain, out);
        } catch (EncodingException ex) {
            throw new IOException(ex);
        }
    }

    private void write(SoapChain chain, OutputStream out) throws EncodingException, IOException {
        String namespace = chain.getSoapResponse().getSoapNamespace();
        EncoderKey key = CodingHelper.getEncoderKey(namespace, chain.getSoapResponse());
        Encoder<?, SoapResponse> encoder = this.encoderRepository.getEncoder(key);
        if (encoder == null) {
            throw new NoEncoderForKeyException(key);
        }
        write(encoder, chain, out);
    }

    private void write(Encoder<?, SoapResponse> encoder, SoapChain chain, OutputStream out)
            throws IOException, EncodingException {
        if (this.forceStreamingEncoding && encoder instanceof StreamingEncoder) {
            ((StreamingEncoder<?, ? super SoapResponse>) encoder)
                    .encode(chain.getSoapResponse(), out);
        } else {
            try {
                Object object = encoder.encode(chain.getSoapResponse());
                if (object instanceof SOAPMessage) {
                    ((SOAPMessage) object).writeTo(out);
                } else if (object instanceof XmlObject) {
                    ((XmlObject) object).save(out, this.xmlOptions.get());
                }
            } catch (SOAPException ex) {
                throw new IOException(ex);
            }
        }
    }

    @Override
    public boolean supportsGZip(SoapChain t) {
        return false;
    }

}
