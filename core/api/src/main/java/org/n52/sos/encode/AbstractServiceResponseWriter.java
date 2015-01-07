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
package org.n52.sos.encode;

import java.io.IOException;
import java.io.OutputStream;

import org.n52.sos.encode.streaming.StreamingDataEncoder;
import org.n52.sos.encode.streaming.StreamingEncoder;
import org.n52.sos.exception.ows.concrete.NoEncoderForKeyException;
import org.n52.sos.ogc.ows.OwsExceptionReport;
import org.n52.sos.request.ResponseFormat;
import org.n52.sos.response.AbstractServiceResponse;
import org.n52.sos.response.StreamingDataResponse;
import org.n52.sos.service.ServiceConfiguration;
import org.n52.sos.util.http.MediaType;

/**
 * {@link ResponseWriter} for {@link AbstractServiceResponse}
 * 
 * @author Carsten Hollmann <c.hollmann@52north.org>
 * @since 4.0.2
 *
 */
public class AbstractServiceResponseWriter extends AbstractResponseWriter<AbstractServiceResponse> {

    // @Override
    // public Class<AbstractServiceResponse> getType() {
    // return AbstractServiceResponse.class;
    // }

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
                        ResponseWriter<Object> writer =
                                ResponseWriterRepository.getInstance().getWriter(encode.getClass());
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
        if (isStreaming(asr)) {
            return false;
        }
        return true;
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
        if (getEncoder(asr) instanceof StreamingEncoder) {
            return ServiceConfiguration.getInstance().isForceStreamingEncoding()
                    || ((StreamingEncoder<?, ?>) getEncoder(asr)).forceStreaming();
        }
        return false;
    }
}
