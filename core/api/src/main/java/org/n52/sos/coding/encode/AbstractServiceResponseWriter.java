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
package org.n52.sos.coding.encode;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Collections;
import java.util.Set;

import org.n52.iceland.coding.encode.AbstractResponseWriter;
import org.n52.iceland.coding.encode.ResponseProxy;
import org.n52.iceland.coding.encode.ResponseWriter;
import org.n52.iceland.coding.encode.ResponseWriterKey;
import org.n52.iceland.coding.encode.ResponseWriterRepository;
import org.n52.janmayen.http.MediaType;
import org.n52.shetland.ogc.ows.service.OwsOperationKey;
import org.n52.shetland.ogc.ows.service.OwsServiceResponse;
import org.n52.shetland.ogc.ows.service.ResponseFormat;
import org.n52.svalbard.encode.Encoder;
import org.n52.svalbard.encode.EncoderRepository;
import org.n52.svalbard.encode.OperationResponseEncoderKey;
import org.n52.svalbard.encode.StreamingEncoder;
import org.n52.svalbard.encode.exception.EncodingException;
import org.n52.svalbard.encode.exception.NoEncoderForKeyException;

/**
 * {@link ResponseWriter} for {@link OwsServiceResponse}
 *
 * @author <a href="mailto:c.hollmann@52north.org">Carsten Hollmann</a>
 * @since 4.0.2
 *
 */
public class AbstractServiceResponseWriter extends AbstractResponseWriter<OwsServiceResponse> {
    private static final ResponseWriterKey KEY
            = new ResponseWriterKey(OwsServiceResponse.class);

    private final ResponseWriterRepository responseWriterRepository;

    public AbstractServiceResponseWriter(EncoderRepository encoderRepository,
                                         ResponseWriterRepository responseWriterRepository) {
        super(encoderRepository);
        this.responseWriterRepository = responseWriterRepository;
    }


    public ResponseWriterRepository getResponseWriterRepository() {
        return responseWriterRepository;
    }

    @Override
    public void write(OwsServiceResponse asr, OutputStream out, ResponseProxy responseProxy)
            throws IOException, EncodingException {
        Encoder<Object, OwsServiceResponse> encoder = getEncoder(asr);
        if (encoder != null) {
            if (encoder instanceof StreamingEncoder) {
                ((StreamingEncoder<?, OwsServiceResponse>) encoder).encode(asr, out);
            } else {
                // use encoded Object specific writer, e.g. XmlResponseWriter
                Object encode = encoder.encode(asr);
                if (encode != null) {
                    ResponseWriter<Object> writer = this.responseWriterRepository.getWriter(encode.getClass());
                    if (writer == null) {
                        throw new RuntimeException("no writer for " + encode.getClass() + " found!");
                    }
                    writer.write(encode, out, responseProxy);
                }
            }
        }
    }

    @Override
    public boolean supportsGZip(OwsServiceResponse asr) {
        return !isStreaming(asr);
    }

    /**
     * Get the {@link Encoder} for the {@link OwsServiceResponse} and the
     * requested contentType
     *
     * @param asr
     *            {@link OwsServiceResponse} to get {@link Encoder} for
     * @return {@link Encoder} for the {@link OwsServiceResponse}
     * @throws org.n52.svalbard.encode.exception.NoEncoderForKeyException if no encoder could be found
     */
    private Encoder<Object, OwsServiceResponse> getEncoder(OwsServiceResponse asr) throws NoEncoderForKeyException {
        OperationResponseEncoderKey key = getEncoderKey(asr);

        Encoder<Object, OwsServiceResponse> encoder = getEncoder(key);
        if (encoder == null) {
            throw new NoEncoderForKeyException(key);
        }
        return encoder;
    }

    private MediaType getEncodedContentType(OwsServiceResponse asr) {
        if (asr instanceof ResponseFormat) {
            return getEncodedContentType((ResponseFormat) asr);
        }
        return getContentType();
    }

    /**
     * Check if streaming encoding is forced and the {@link Encoder} for the
     * {@link OwsServiceResponse} is a {@link StreamingEncoder}
     *
     * @param asr
     *            {@link OwsServiceResponse} to check the {@link Encoder}
     *            for
     * @return <code>true</code>, if streaming encoding is forced and the
     *         {@link Encoder} for the {@link OwsServiceResponse} is a
     *         {@link StreamingEncoder}
     */
    private boolean isStreaming(OwsServiceResponse asr) {
        return getEncoder(getEncoderKey(asr)) instanceof StreamingEncoder;
    }

    @Override
    public Set<ResponseWriterKey> getKeys() {
        return Collections.singleton(KEY);
    }

    private OperationResponseEncoderKey getEncoderKey(OwsServiceResponse asr) {
        OperationResponseEncoderKey key =
                new OperationResponseEncoderKey(new OwsOperationKey(asr), getEncodedContentType(asr));
        return key;
    }
}
