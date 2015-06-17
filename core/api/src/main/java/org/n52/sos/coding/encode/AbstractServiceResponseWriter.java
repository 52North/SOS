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
package org.n52.sos.coding.encode;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Collections;
import java.util.Set;

import org.n52.iceland.coding.encode.AbstractResponseWriter;
import org.n52.iceland.coding.encode.Encoder;
import org.n52.iceland.coding.encode.EncoderKey;
import org.n52.iceland.coding.encode.EncoderRepository;
import org.n52.iceland.coding.encode.OperationEncoderKey;
import org.n52.iceland.coding.encode.ResponseProxy;
import org.n52.iceland.coding.encode.ResponseWriter;
import org.n52.iceland.coding.encode.ResponseWriterKey;
import org.n52.iceland.coding.encode.ResponseWriterRepository;
import org.n52.iceland.exception.ows.OwsExceptionReport;
import org.n52.iceland.exception.ows.concrete.NoEncoderForKeyException;
import org.n52.iceland.request.ResponseFormat;
import org.n52.iceland.response.AbstractServiceResponse;
import org.n52.iceland.util.http.MediaType;
import org.n52.sos.encode.streaming.StreamingDataEncoder;
import org.n52.sos.encode.streaming.StreamingEncoder;
import org.n52.sos.response.StreamingDataResponse;

/**
 * {@link ResponseWriter} for {@link AbstractServiceResponse}
 *
 * @author <a href="mailto:c.hollmann@52north.org">Carsten Hollmann</a>
 * @since 4.0.2
 *
 */
public class AbstractServiceResponseWriter extends AbstractResponseWriter<AbstractServiceResponse> {
    private static final ResponseWriterKey KEY
            = new ResponseWriterKey(AbstractServiceResponse.class);

    private final ResponseWriterRepository responseWriterRepository;
    private final EncoderRepository encoderRepository;
    private final boolean forceStreamingEncoding;

    public AbstractServiceResponseWriter(ResponseWriterRepository responseWriterRepository,
                                         EncoderRepository encoderRepository,
                                         boolean forceStreamingEncoding) {
        this.responseWriterRepository = responseWriterRepository;
        this.encoderRepository = encoderRepository;
        this.forceStreamingEncoding = forceStreamingEncoding;
    }

    public ResponseWriterRepository getResponseWriterRepository() {
        return responseWriterRepository;
    }

    @Override
    public void write(AbstractServiceResponse asr, OutputStream out, ResponseProxy responseProxy) throws IOException {
        try {
            Encoder<Object, AbstractServiceResponse> encoder = getEncoder(asr);
            if (encoder != null) {
                if (isStreaming(asr)) {
                    ((StreamingEncoder<?, AbstractServiceResponse>) encoder).encode(asr, out);
                } else {
                    if (asr instanceof StreamingDataResponse && ((StreamingDataResponse)asr).hasStreamingData() && !(encoder instanceof StreamingDataEncoder)) {
                        ((StreamingDataResponse)asr).mergeStreamingData();
                    }
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
        } catch (OwsExceptionReport owsex) {
            throw new IOException(owsex);
        }
    }

    @Override
    public boolean supportsGZip(AbstractServiceResponse asr) {
        return !isStreaming(asr);
    }

    /**
     * Get the {@link Encoder} for the {@link AbstractServiceResponse} and the
     * requested contentType
     *
     * @param asr
     *            {@link AbstractServiceResponse} to get {@link Encoder} for
     * @return {@link Encoder} for the {@link AbstractServiceResponse}
     */
    private Encoder<Object, AbstractServiceResponse> getEncoder(AbstractServiceResponse asr) {
        OperationEncoderKey key = new OperationEncoderKey(asr.getOperationKey(), getEncodedContentType(asr));
        Encoder<Object, AbstractServiceResponse> encoder = getEncoder(key);
        if (encoder == null) {
            throw new RuntimeException(new NoEncoderForKeyException(new OperationEncoderKey(asr.getOperationKey(),
                    getContentType())));
        }
        return encoder;
    }

     /**
     * Getter for encoder, encapsulates the instance call
     *
     * @param key
     *            Encoder key
     * @return Matching encoder
     */
    protected <D, S> Encoder<D, S> getEncoder(EncoderKey key) {
        return this.encoderRepository.getEncoder(key);
    }

    private MediaType getEncodedContentType(AbstractServiceResponse asr) {
        if (asr instanceof ResponseFormat) {
            return getEncodedContentType((ResponseFormat) asr);
        }
        return getContentType();
    }

    /**
     * Check if streaming encoding is forced and the {@link Encoder} for the
     * {@link AbstractServiceResponse} is a {@link StreamingEncoder}
     *
     * @param asr
     *            {@link AbstractServiceResponse} to check the {@link Encoder}
     *            for
     * @return <code>true</code>, if streaming encoding is forced and the
     *         {@link Encoder} for the {@link AbstractServiceResponse} is a
     *         {@link StreamingEncoder}
     */
    private boolean isStreaming(AbstractServiceResponse asr) {
        Encoder<Object, AbstractServiceResponse> encoder = getEncoder(asr);
        if (encoder instanceof StreamingEncoder) {
            StreamingEncoder<?, ?> sencoder = (StreamingEncoder<?, ?>) getEncoder(asr);
            return this.forceStreamingEncoding || sencoder.forceStreaming();
        }
        return false;
    }

    @Override
    public Set<ResponseWriterKey> getKeys() {
        return Collections.singleton(KEY);
    }
}
