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
package org.n52.svalbard.xml;

import javax.inject.Inject;

import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.XmlOptions;

import org.n52.iceland.coding.encode.XmlEncoderKey;
import org.n52.janmayen.Producer;
import org.n52.janmayen.http.MediaType;
import org.n52.janmayen.http.MediaTypes;
import org.n52.sos.util.XmlHelper;
import org.n52.svalbard.AbstractDelegatingEncoder;
import org.n52.svalbard.EncodingContext;
import org.n52.svalbard.encode.Encoder;
import org.n52.svalbard.encode.EncoderKey;
import org.n52.svalbard.encode.SchemaAwareEncoder;
import org.n52.svalbard.encode.exception.EncodingException;
import org.n52.svalbard.encode.exception.NoEncoderForKeyException;

/**
 * @param <T>
 *
 * @since 4.0.0
 *
 * @param <S>
 */
public abstract class AbstractXmlEncoder<T, S>
        extends AbstractDelegatingEncoder<T, S>
        implements SchemaAwareEncoder<T, S> {

    private Producer<XmlOptions> xmlOptions;

    public XmlOptions getXmlOptions() {
        return xmlOptions.get();
    }

    @Inject
    public void setXmlOptions(Producer<XmlOptions> xmlOptions) {
        this.xmlOptions = xmlOptions;
    }

    @Override
    public T encode(S element) throws EncodingException {
        return encode(element, EncodingContext.empty());
    }

    @Override
    public MediaType getContentType() {
        return MediaTypes.TEXT_XML;
    }

    protected XmlObject substitute(XmlObject elementToSubstitute, XmlObject substitutionElement) {
        XmlObject substituteElement = XmlHelper.substituteElement(elementToSubstitute, substitutionElement);
        substituteElement.set(substitutionElement);
        return substituteElement;
    }

    public <T> Encoder<XmlObject, T> getEncoder(String namespace, T o) throws EncodingException {
        EncoderKey key = getEncoderKey(namespace, o);
        Encoder<XmlObject, T> encoder = getEncoder(key);
        if (encoder == null) {
            throw new NoEncoderForKeyException(key);
        }
        return encoder;
    }

    public <T> Encoder<XmlObject, T> getEncoder(String namespace, Class<? super T> o) throws EncodingException {
        EncoderKey key = getEncoderKey(namespace, o);
        Encoder<XmlObject, T> encoder = getEncoder(key);
        if (encoder == null) {
            throw new NoEncoderForKeyException(key);
        }
        return encoder;
    }

    public <T> XmlObject encodeObjectToXml(String namespace, T object, EncodingContext helperValues)
            throws EncodingException {
        return getEncoder(namespace, object).encode(object, helperValues == null ? EncodingContext.empty() : helperValues);
    }

    public XmlObject encodeObjectToXml(String namespace, Object object) throws EncodingException {
        return encodeObjectToXml(namespace, object, null);
    }

    public String encodeObjectToXmlText(String namespace, Object object, EncodingContext helperValues) throws EncodingException {
        return encodeObjectToXml(namespace, object, helperValues).xmlText(getXmlOptions());
    }

    public String encodeObjectToXmlText(String namespace, Object object) throws EncodingException {
        return encodeObjectToXmlText(namespace, object, null);
    }

    public EncoderKey getEncoderKey(String namespace, Object o) {
        return new XmlEncoderKey(namespace, o.getClass());
    }

    public EncoderKey getEncoderKey(String namespace, Class<?> o) {
        return new XmlEncoderKey(namespace, o);
    }

}
