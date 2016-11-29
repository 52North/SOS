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
package org.n52.sos.encode.exi;

import java.util.Collections;
import java.util.Map;
import java.util.Set;

import org.apache.xmlbeans.XmlObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.n52.iceland.coding.OperationKey;
import org.n52.iceland.coding.encode.OperationResponseEncoderKey;
import org.n52.iceland.coding.encode.OwsEncodingException;
import org.n52.iceland.request.ResponseFormat;
import org.n52.janmayen.http.MediaType;
import org.n52.janmayen.http.MediaTypes;
import org.n52.shetland.ogc.ows.exception.OwsExceptionReport;
import org.n52.shetland.ogc.ows.service.OwsServiceResponse;
import org.n52.shetland.ogc.sos.SosConstants;
import org.n52.sos.encode.streaming.StreamingDataEncoder;
import org.n52.sos.exi.EXIObject;
import org.n52.sos.response.StreamingDataResponse;
import org.n52.svalbard.AbstractDelegatingEncoder;
import org.n52.svalbard.HelperValues;
import org.n52.svalbard.encode.Encoder;
import org.n52.svalbard.encode.EncoderKey;
import org.n52.svalbard.encode.exception.EncodingException;
import org.n52.svalbard.encode.exception.NoEncoderForKeyException;
import org.n52.svalbard.encode.exception.UnsupportedEncoderInputException;

import com.google.common.base.Joiner;
import com.google.common.collect.Sets;

/**
 * Abstract response encoder class for {@link EXIObject}
 *
 * @author <a href="mailto:c.hollmann@52north.org">Carsten Hollmann</a>
 * @since 4.2.0
 *
 * @param <T>
 *            concrete {@link OwsServiceResponse}
 */
public class AbstractSosResponseEncoder<T extends OwsServiceResponse> extends AbstractDelegatingEncoder<EXIObject, T> {

    private static final Logger LOGGER = LoggerFactory.getLogger(AbstractSosResponseEncoder.class);

    private final Set<EncoderKey> encoderKeys;

    /**
     * Constructor
     *
     * @param type
     *            Concrete {@link OwsServiceResponse} class
     * @param operation
     *            SOS operation as {@link String}
     * @param version
     *            SOS version
     */
    public AbstractSosResponseEncoder(Class<T> type, String operation, String version) {
        OperationKey key = new OperationKey(SosConstants.SOS, version, operation);
        this.encoderKeys = Sets.<EncoderKey>newHashSet(new OperationResponseEncoderKey(key, MediaTypes.APPLICATION_EXI));
        LOGGER.debug("Encoder for the following keys initialized successfully: {}!", Joiner.on(", ").join(encoderKeys));
    }

    /**
     * Constructor
     *
     * @param type
     *            Concrete {@link OwsServiceResponse} class
     * @param operation
     *            SOS operation as {@link Enum}
     * @param version
     *            SOS version
     */
    public AbstractSosResponseEncoder(Class<T> type, Enum<?> operation, String version) {
        this(type, operation.name(), version);
    }

    @Override
    public EXIObject encode(T objectToEncode, Map<HelperValues, String> additionalValues)
            throws EncodingException {
        OwsServiceResponse asr = objectToEncode;
        Encoder<Object, OwsServiceResponse> encoder = getEncoder(asr);
        if (asr instanceof StreamingDataResponse && ((StreamingDataResponse)asr).hasStreamingData() && !(encoder instanceof StreamingDataEncoder)) {
            try {
                ((StreamingDataResponse)asr).mergeStreamingData();
            } catch (OwsExceptionReport ex) {
                throw new OwsEncodingException(ex);
            }
        }
        Object encode = encoder.encode(asr);
        if (encode != null && encode instanceof XmlObject) {
            return new EXIObject((XmlObject) encode);
        } else {
            throw new UnsupportedEncoderInputException(encoder, asr);
        }
    }

    @Override
    public EXIObject encode(T objectToEncode) throws EncodingException {
        return encode(objectToEncode, null);
    }

    @Override
    public Set<EncoderKey> getKeys() {
        return Collections.unmodifiableSet(encoderKeys);
    }

    @Override
    public MediaType getContentType() {
        return MediaTypes.APPLICATION_EXI;
    }

    /**
     * Get the {@link Encoder} for the {@link OwsServiceResponse} and the
     * requested contentType
     *
     * @param asr
     *            {@link OwsServiceResponse} to get {@link Encoder} for
     * @return {@link Encoder} for the {@link OwsServiceResponse}
     */
    protected Encoder<Object, OwsServiceResponse> getEncoder(OwsServiceResponse asr) {
        OperationResponseEncoderKey key = new OperationResponseEncoderKey(new OperationKey(asr), getEncodedContentType(asr));
        Encoder<Object, OwsServiceResponse> encoder = getEncoder(key);
        if (encoder == null) {
            throw new RuntimeException(new NoEncoderForKeyException(key));
        }
        return encoder;
    }

    /**
     * Get encoding {@link MediaType} from {@link OwsServiceResponse}
     *
     * @param asr
     *            {@link OwsServiceResponse} to get content type from
     * @return Encoding {@link MediaType}
     */
    protected MediaType getEncodedContentType(OwsServiceResponse asr) {
        if (asr instanceof ResponseFormat) {
            return getEncodedContentType((ResponseFormat) asr);
        }
        return MediaTypes.APPLICATION_XML;
    }

    /**
     * Get encoding {@link MediaType} from {@link ResponseFormat}
     *
     * @param responseFormat
     *            {@link ResponseFormat} to get content type from
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
