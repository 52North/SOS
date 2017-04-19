/*
 * Copyright (C) 2012-2017 52°North Initiative for Geospatial Open Source
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
package org.n52.sos.encode.exi;

import org.apache.xmlbeans.XmlObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.n52.iceland.coding.encode.OwsEncodingException;
import org.n52.janmayen.http.MediaType;
import org.n52.janmayen.http.MediaTypes;
import org.n52.shetland.ogc.ows.exception.OwsExceptionReport;
import org.n52.shetland.ogc.ows.service.OwsServiceResponse;
import org.n52.shetland.ogc.ows.service.ResponseFormat;
import org.n52.shetland.ogc.sos.response.StreamingDataResponse;
import org.n52.sos.exi.EXIObject;
import org.n52.svalbard.encode.AbstractDelegatingEncoder;
import org.n52.svalbard.encode.Encoder;
import org.n52.svalbard.encode.EncoderKey;
import org.n52.svalbard.encode.EncodingContext;
import org.n52.svalbard.encode.StreamingDataEncoder;
import org.n52.svalbard.encode.exception.EncodingException;
import org.n52.svalbard.encode.exception.NoEncoderForKeyException;
import org.n52.svalbard.encode.exception.UnsupportedEncoderInputException;

/**
 * TODO JavaDoc
 *
 * @author Christian Autermann
 */
public abstract class ExiEncoder<T> extends AbstractDelegatingEncoder<EXIObject, T> {
    private static final Logger LOGGER = LoggerFactory.getLogger(ExiResponseEncoder.class);

    @Override
    public MediaType getContentType() {
        return MediaTypes.APPLICATION_EXI;
    }

    @Override
    public EXIObject encode(T response, EncodingContext additionalValues) throws EncodingException {
        return encode(response);
    }

    @Override
    public EXIObject encode(T response) throws EncodingException {
        Encoder<Object, T> encoder = getEncoder(response);
        if (response instanceof StreamingDataResponse &&
            ((StreamingDataResponse) response).hasStreamingData() &&
            !(encoder instanceof StreamingDataEncoder)) {
            try {
                ((StreamingDataResponse) response).mergeStreamingData();
            } catch (OwsExceptionReport ex) {
                throw new OwsEncodingException(ex);
            }
        }
        Object encode = encoder.encode(response);
        if (encode != null && encode instanceof XmlObject) {
            return new EXIObject((XmlObject) encode);
        } else {
            throw new UnsupportedEncoderInputException(encoder, response);
        }
    }

    /**
     * Get the {@link Encoder} for the {@link OwsServiceResponse} and the requested contentType
     *
     * @param response {@link OwsServiceResponse} to get {@link Encoder} for
     *
     * @return {@link Encoder} for the {@link OwsServiceResponse}
     *
     * @throws EncodingException if no encoder could be found
     */
    protected Encoder<Object, T> getEncoder(T response) throws EncodingException {
        EncoderKey key = getKey(response);
        Encoder<Object, T> encoder = getEncoder(key);
        if (encoder == null) {
            throw new NoEncoderForKeyException(key);
        }
        return encoder;
    }

    protected abstract EncoderKey getKey(T object);

    /**
     * Get encoding {@link MediaType} from {@link OwsServiceResponse}
     *
     * @param response {@link OwsServiceResponse} to get content type from
     *
     * @return Encoding {@link MediaType}
     */
    protected MediaType getEncodedContentType(OwsServiceResponse response) {
        if (response instanceof ResponseFormat) {
            return getEncodedContentType((ResponseFormat) response);
        }
        return MediaTypes.APPLICATION_XML;
    }

    /**
     * Get encoding {@link MediaType} from {@link ResponseFormat}
     *
     * @param responseFormat {@link ResponseFormat} to get content type from
     *
     * @return Encoding {@link MediaType}
     */
    protected MediaType getEncodedContentType(ResponseFormat responseFormat) {
        if (responseFormat.isSetResponseFormat()) {
            MediaType contentTypeFromResponseFormat = null;
            try {
                contentTypeFromResponseFormat = MediaType.parse(responseFormat.getResponseFormat()).withoutParameters();
            } catch (IllegalArgumentException iae) {
                LOGGER.debug("Requested responseFormat {} is not a MediaType", responseFormat.getResponseFormat());
            }
            if (contentTypeFromResponseFormat != null) {
                if (MediaTypes.COMPATIBLE_TYPES.containsEntry(contentTypeFromResponseFormat, MediaTypes.APPLICATION_XML)) {
                    return MediaTypes.APPLICATION_XML;
                }
                return contentTypeFromResponseFormat;
            }
        }
        return MediaTypes.APPLICATION_XML;
    }

}
