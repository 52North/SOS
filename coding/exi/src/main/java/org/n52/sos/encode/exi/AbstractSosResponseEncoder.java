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
package org.n52.sos.encode.exi;

import java.util.Collections;
import java.util.Map;
import java.util.Set;

import org.apache.xmlbeans.XmlObject;
import org.n52.sos.coding.CodingRepository;
import org.n52.sos.coding.OperationKey;
import org.n52.sos.encode.Encoder;
import org.n52.sos.encode.EncoderKey;
import org.n52.sos.encode.OperationEncoderKey;
import org.n52.sos.encode.streaming.StreamingDataEncoder;
import org.n52.sos.exception.ows.concrete.NoEncoderForKeyException;
import org.n52.sos.exception.ows.concrete.UnsupportedEncoderInputException;
import org.n52.sos.exi.EXIObject;
import org.n52.sos.ogc.ows.OwsExceptionReport;
import org.n52.sos.ogc.sos.SosConstants;
import org.n52.sos.ogc.sos.SosConstants.HelperValues;
import org.n52.sos.request.ResponseFormat;
import org.n52.sos.response.AbstractServiceResponse;
import org.n52.sos.response.StreamingDataResponse;
import org.n52.sos.service.ServiceConstants.SupportedTypeKey;
import org.n52.sos.util.http.MediaType;
import org.n52.sos.util.http.MediaTypes;
import org.n52.sos.w3c.SchemaLocation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Joiner;
import com.google.common.collect.Sets;

/**
 * Abstract response encoder class for {@link EXIObject}
 * 
 * @author Carsten Hollmann <c.hollmann@52north.org>
 * @since 4.2.0
 *
 * @param <T>
 *            concrete {@link AbstractServiceResponse}
 */
public class AbstractSosResponseEncoder<T extends AbstractServiceResponse> implements Encoder<EXIObject, T> {

    private static final Logger LOGGER = LoggerFactory.getLogger(AbstractSosResponseEncoder.class);

    private final Set<EncoderKey> encoderKeys;

    /**
     * Constructor
     * 
     * @param type
     *            Concrete {@link AbstractServiceResponse} class
     * @param operation
     *            SOS operation as {@link String}
     * @param version
     *            SOS version
     */
    public AbstractSosResponseEncoder(Class<T> type, String operation, String version) {
        OperationKey key = new OperationKey(SosConstants.SOS, version, operation);
        this.encoderKeys = Sets.newHashSet((EncoderKey) new OperationEncoderKey(key, MediaTypes.APPLICATION_EXI));
        LOGGER.debug("Encoder for the following keys initialized successfully: {}!", Joiner.on(", ").join(encoderKeys));
    }

    /**
     * Constructor
     * 
     * @param type
     *            Concrete {@link AbstractServiceResponse} class
     * @param operation
     *            SOS operation as {@link Enum}
     * @param version
     *            SOS version
     */
    public AbstractSosResponseEncoder(Class<T> type, Enum<?> operation, String version) {
        this(type, operation.name(), version);
    }

    @Override
    public EXIObject encode(T objectToEncode, Map<HelperValues, String> additionalValues) throws OwsExceptionReport,
            UnsupportedEncoderInputException {
        AbstractServiceResponse asr = objectToEncode;
        Encoder<Object, AbstractServiceResponse> encoder = getEncoder(asr);
        if (asr instanceof StreamingDataResponse && ((StreamingDataResponse)asr).hasStreamingData() && !(encoder instanceof StreamingDataEncoder)) {
            ((StreamingDataResponse)asr).mergeStreamingData();
        }
        Object encode = encoder.encode(asr);
        if (encode != null && encode instanceof XmlObject) {
            return new EXIObject((XmlObject) encode);
        } else {
            throw new UnsupportedEncoderInputException(encoder, asr);
        }
    }

    @Override
    public EXIObject encode(T objectToEncode) throws OwsExceptionReport, UnsupportedEncoderInputException {
        return encode(objectToEncode, null);
    }

    @Override
    public Map<SupportedTypeKey, Set<String>> getSupportedTypes() {
        return Collections.emptyMap();
    }

    @Override
    public Set<String> getConformanceClasses() {
        return Collections.emptySet();
    }

    @Override
    public Set<EncoderKey> getEncoderKeyType() {
        return encoderKeys;
    }

    @Override
    public void addNamespacePrefixToMap(Map<String, String> nameSpacePrefixMap) {
        // nothing to add
    }

    @Override
    public MediaType getContentType() {
        return MediaTypes.APPLICATION_EXI;
    }

    @Override
    public Set<SchemaLocation> getSchemaLocations() {
        return Collections.emptySet();
    }

    /**
     * Get the {@link Encoder} for the {@link AbstractServiceResponse} and the
     * requested contentType
     * 
     * @param asr
     *            {@link AbstractServiceResponse} to get {@link Encoder} for
     * @return {@link Encoder} for the {@link AbstractServiceResponse}
     */
    protected Encoder<Object, AbstractServiceResponse> getEncoder(AbstractServiceResponse asr) {
        OperationEncoderKey key = new OperationEncoderKey(asr.getOperationKey(), getEncodedContentType(asr));
        Encoder<Object, AbstractServiceResponse> encoder = getEncoder(key);
        if (encoder == null) {
            throw new RuntimeException(new NoEncoderForKeyException(new OperationEncoderKey(asr.getOperationKey(),
                    MediaTypes.APPLICATION_XML)));
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
        return CodingRepository.getInstance().getEncoder(key);
    }

    /**
     * Get encoding {@link MediaType} from {@link AbstractServiceResponse}
     * 
     * @param asr
     *            {@link AbstractServiceResponse} to get content type from
     * @return Encoding {@link MediaType}
     */
    protected MediaType getEncodedContentType(AbstractServiceResponse asr) {
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
                contentTypeFromResponseFormat =
                        MediaType.parse(responseFormat.getResponseFormat()).withoutParameters();
            } catch (IllegalArgumentException iae) {
                LOGGER.debug("Requested responseFormat {} is not a MediaType", responseFormat.getResponseFormat());
            }
            if (contentTypeFromResponseFormat != null) {
                if (MediaTypes.COMPATIBLE_TYPES.containsEntry(contentTypeFromResponseFormat,
                        MediaTypes.APPLICATION_XML)) {
                    return MediaTypes.APPLICATION_XML;
                }
                return contentTypeFromResponseFormat;
            }
        }
        return MediaTypes.APPLICATION_XML;
    }
}
