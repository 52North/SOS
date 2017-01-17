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
package org.n52.sos.decode;

import java.util.Collections;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.n52.shetland.w3c.soap.SoapConstants;
import org.n52.shetland.w3c.soap.SoapRequest;
import org.n52.svalbard.decode.Decoder;
import org.n52.svalbard.decode.DecoderKey;
import org.n52.svalbard.decode.XmlNamespaceOperationDecoderKey;
import org.n52.svalbard.decode.exception.DecodingException;
import org.n52.svalbard.xml.AbstractXmlDecoder;

import com.google.common.base.Joiner;

/**
 * Abstract SOAP {@link Decoder} for {@link String} XML representation
 *
 * @author <a href="mailto:c.hollmann@52north.org">Carsten Hollmann</a>
 * @since 1.0.0
 *
 */
public abstract class AbstractSoapStringDecoder extends AbstractXmlDecoder<String, SoapRequest> {

    private static final Logger LOGGER = LoggerFactory.getLogger(AbstractSoapStringDecoder.class);

    private final Set<DecoderKey> decoderKeys;

    public AbstractSoapStringDecoder(String namespace) {
        this.decoderKeys =
                Collections.<DecoderKey> singleton(new XmlNamespaceOperationDecoderKey(namespace,
                        SoapConstants.EN_SOAP_ENVELOPE));
        LOGGER.debug("Decoder for the following keys initialized successfully: {}!",
                Joiner.on(", ").join(getKeys()));
    }

    @Override
    public Set<DecoderKey> getKeys() {
        return Collections.unmodifiableSet(decoderKeys);
    }

    @Override
    public SoapRequest decode(String xmlString) throws DecodingException {
        return (SoapRequest) decodeXmlObject(xmlString);
    }
}
