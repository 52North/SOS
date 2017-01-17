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

import org.apache.xmlbeans.XmlException;
import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.XmlOptions;

import org.n52.janmayen.Producer;
import org.n52.sos.exception.ows.concrete.XmlDecodingException;
import org.n52.sos.util.XmlHelper;
import org.n52.svalbard.AbstractDelegatingDecoder;
import org.n52.svalbard.decode.Decoder;
import org.n52.svalbard.decode.DecoderKey;
import org.n52.svalbard.decode.XmlNamespaceDecoderKey;
import org.n52.svalbard.decode.exception.DecodingException;
import org.n52.svalbard.decode.exception.NoDecoderForKeyException;

/**
 * TODO JavaDoc
 *
 * @author Christian Autermann <c.autermann@52north.org>
 *
 * @since 4.0.0
 */
public abstract class AbstractXmlDecoder<T, S> extends AbstractDelegatingDecoder<S, T> {

    private Producer<XmlOptions> xmlOptions;

    public XmlOptions getXmlOptions() {
        return xmlOptions.get();
    }

    @Inject
    public void setXmlOptions(Producer<XmlOptions> xmlOptions) {
        this.xmlOptions = xmlOptions;
    }

    @SuppressWarnings("unchecked")
    public <T> T decodeXmlElement(XmlObject x) throws DecodingException {
        return decodeXmlObject(x);
    }

    public DecoderKey getDecoderKey(XmlObject doc) {
        return new XmlNamespaceDecoderKey(XmlHelper.getNamespace(doc), doc.getClass());
    }

    public <T> T decodeXmlObject(XmlObject xbObject) throws DecodingException {
        final DecoderKey key = getDecoderKey(xbObject);
        final Decoder<T, XmlObject> decoder = getDecoderRepository().getDecoder(key);
        if (decoder == null) {
            throw new NoDecoderForKeyException(key);
        }
        return decoder.decode(xbObject);
    }

    public Object decodeXmlObject(String xmlString) throws DecodingException {
        try {
            return decodeXmlObject(XmlObject.Factory.parse(xmlString));
        } catch (final XmlException e) {
            throw new XmlDecodingException("XML string", xmlString, e);
        }
    }

}
