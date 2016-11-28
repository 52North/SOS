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
package org.n52.sos.encode.sos.v2;

import java.io.OutputStream;

import org.apache.xmlbeans.XmlObject;

import org.n52.iceland.coding.encode.XmlEncoderKey;
import org.n52.shetland.ogc.om.OmObservation;
import org.n52.sos.coding.encode.EncodingValues;
import org.n52.sos.coding.encode.ObservationEncoder;
import org.n52.sos.response.AbstractObservationResponse;
import org.n52.svalbard.encode.Encoder;
import org.n52.svalbard.encode.exception.EncodingException;

/**
 * TODO JavaDoc
 *
 * @author Christian Autermann <c.autermann@52north.org>
 *
 * @since 4.0.0
 */
public abstract class AbstractObservationResponseEncoder<T extends AbstractObservationResponse>
        extends AbstractSosResponseEncoder<T> {

    public AbstractObservationResponseEncoder(String operation, Class<T> responseType) {
        super(operation, responseType);
    }

    /**
     * Finds a O&Mv2 compatible {@link ObservationEncoder}
     *
     * @param responseFormat
     *            the response format
     *
     * @return the encoder or {@code null} if none is found
     *
     * @throws EncodingException
     *             if the found encoder is not a {@linkplain ObservationEncoder}
     */
    private ObservationEncoder<XmlObject, OmObservation> findObservationEncoder(String responseFormat)
            throws EncodingException {
        Encoder<XmlObject, OmObservation> encoder = getEncoder(new XmlEncoderKey(responseFormat, OmObservation.class));
        if (encoder == null) {
            return null;
        } else if (encoder instanceof ObservationEncoder) {
            ObservationEncoder<XmlObject, OmObservation> oe = (ObservationEncoder<XmlObject, OmObservation>) encoder;
            return oe.isObservationAndMeasurmentV20Type() ? oe : null;
        } else {
            throw new EncodingException("Error while encoding response, encoder is not of type ObservationEncoder!");
        }
    }

    /**
     * Finds a compatible response encoder to delegate to.
     *
     * @param responseFormat
     *            the response format
     *
     * @return the encoder
     * @throws org.n52.svalbard.encode.exception.EncodingException if no encoder is found
     */
    private Encoder<XmlObject, T> findResponseEncoder(String responseFormat) throws EncodingException {
        return getEncoder(responseFormat, getResponseType());
    }

    @Override
    protected XmlObject create(T response) throws EncodingException {
        final String responseFormat = response.getResponseFormat();
        // search for an O&M2 encoder for this response format
        ObservationEncoder<XmlObject, OmObservation> encoder = findObservationEncoder(responseFormat);
        if (encoder != null) {
            // encode the response as a GetObservationResponseDocument
            return createResponse(encoder, response);
        }
        // there is no O&M2 compatible observation encoder:
        // search for a encoder for the response and delegate
        return findResponseEncoder(responseFormat).encode(response);
    }

    @Override
    protected void create(T response, OutputStream outputStream, EncodingValues encodingValues) throws EncodingException {
        final String responseFormat = response.getResponseFormat();
        // search for an O&M2 encoder for this response format
        ObservationEncoder<XmlObject, OmObservation> encoder = findObservationEncoder(responseFormat);
        if (encoder != null) {
            // encode the response as a GetObservationResponseDocument
            createResponse(encoder, response, outputStream, encodingValues);
        }
    }

    /**
     * Create a response using the provided O&M2 compatible observation encoder.
     *
     * @param encoder
     *            the encoder
     * @param response
     *            the response
     *
     * @return the encoded response
     *
     * @throws EncodingException
     *             if an error occurs
     */
    protected abstract XmlObject createResponse(ObservationEncoder<XmlObject, OmObservation> encoder, T response)
            throws EncodingException;

    /**
     * Override this method in concrete response encoder if streaming is
     * supported for this operations.
     *
     * @param encoder
     * @param response
     * @param outputStream
     * @param encodingValues
     * @throws EncodingException
     */
    protected void createResponse(ObservationEncoder<XmlObject, OmObservation> encoder, T response, OutputStream outputStream,  EncodingValues encodingValues)
            throws EncodingException {
        super.create(response, outputStream, encodingValues);
    }

}