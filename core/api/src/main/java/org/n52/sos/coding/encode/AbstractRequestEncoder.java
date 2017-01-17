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
package org.n52.sos.coding.encode;

import java.io.OutputStream;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.n52.janmayen.http.MediaTypes;
import org.n52.shetland.ogc.ows.service.OwsServiceRequest;
import org.n52.svalbard.OperationKey;
import org.n52.svalbard.encode.EncoderKey;
import org.n52.svalbard.encode.EncodingValues;
import org.n52.svalbard.encode.OperationRequestEncoderKey;
import org.n52.svalbard.encode.XmlEncoderKey;
import org.n52.svalbard.encode.exception.EncodingException;
import org.n52.svalbard.encode.exception.UnsupportedEncoderInputException;

import com.google.common.base.Joiner;

/**
 * @author <a href="mailto:c.hollmann@52north.org">Carsten Hollmann</a>
 * @since 5.0.0
 *
 * @param <T>
 */
public abstract class AbstractRequestEncoder<T extends OwsServiceRequest> extends AbstractXmlResponseEncoder<T> {

    private static final Logger LOGGER = LoggerFactory.getLogger(AbstractResponseEncoder.class);

    private final Set<EncoderKey> encoderKeys;

    /**
     * constructor
     *
     * @param service
     *            Service
     * @param version
     *            Service version
     * @param operation
     *            Service operation name
     * @param namespace
     *            Service XML schema namespace
     * @param prefix
     *            Service XML schema prefix
     * @param responseType
     *            Response type
     * @param validate
     *            Indicator if the created/encoded object should be validated
     */
    public AbstractRequestEncoder(String service, String version, String operation, String namespace, String prefix, Class<T> responseType, boolean validate) {
        super(service, version, operation, namespace, prefix, responseType, validate);
        OperationKey key = new OperationKey(service, version, operation);
        this.encoderKeys = new HashSet<>(Arrays.asList(new XmlEncoderKey(namespace, responseType),
                                                       new OperationRequestEncoderKey(key, MediaTypes.TEXT_XML),
                                                       new OperationRequestEncoderKey(key, MediaTypes.APPLICATION_XML)));
        LOGGER.debug("Encoder for the following keys initialized successfully: {}!", Joiner.on(", ").join(encoderKeys));
    }

    /**
     * constructor
     *
     * @param service
     *            Service
     * @param version
     *            Service version
     * @param operation
     *            Service operation name
     * @param namespace
     *            Service XML schema namespace
     * @param prefix
     *            Service XML schema prefix
     * @param responseType
     *            Response type
     */
    public AbstractRequestEncoder(String service, String version, String operation, String namespace, String prefix, Class<T> responseType) {
        this(service, version, operation, namespace, prefix, responseType, false);
    }

    @Override
    public Set<EncoderKey> getKeys() {
        return Collections.unmodifiableSet(encoderKeys);
    }

    @Override
    public void encode(T element, OutputStream outputStream) throws EncodingException {
        encode(element, outputStream, new EncodingValues());
    }

    @Override
    public void encode(T response, OutputStream outputStream, EncodingValues encodingValues) throws EncodingException {
        if (response == null) {
            throw new UnsupportedEncoderInputException(this, response);
        }
        create(response, outputStream, encodingValues);
    }

    @Override
    public boolean forceStreaming() {
        return false;
    }
}
