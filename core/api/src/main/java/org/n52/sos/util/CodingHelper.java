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
package org.n52.sos.util;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.apache.xmlbeans.XmlException;
import org.apache.xmlbeans.XmlObject;

import org.n52.iceland.coding.CodingRepository;
import org.n52.svalbard.encode.exception.NoEncoderForKeyException;
import org.n52.iceland.coding.encode.XmlEncoderKey;
import org.n52.janmayen.http.MediaTypes;
import org.n52.sos.exception.ows.concrete.XmlDecodingException;
import org.n52.svalbard.HelperValues;
import org.n52.svalbard.decode.Decoder;
import org.n52.svalbard.decode.DecoderKey;
import org.n52.svalbard.decode.NoDecoderForKeyException;
import org.n52.svalbard.decode.OperationDecoderKey;
import org.n52.svalbard.decode.XmlNamespaceDecoderKey;
import org.n52.svalbard.decode.XmlStringOperationDecoderKey;
import org.n52.svalbard.decode.exception.DecodingException;
import org.n52.svalbard.encode.Encoder;
import org.n52.svalbard.encode.EncoderKey;
import org.n52.svalbard.encode.exception.EncodingException;

import com.google.common.collect.Maps;

/**
 * @author Christian Autermann <c.autermann@52north.org> TODO implement
 *         encodeToXml(Object o) using a Map from o.getClass().getName() ->
 *         namespaces
 * @since 4.0.0
 *
 */
public final class CodingHelper {
    private CodingHelper() {
    }

    @Deprecated
    public static <T> XmlObject encodeObjectToXml(String namespace, T o,
            Map<HelperValues, String> helperValues) throws EncodingException {
        return getEncoder(namespace, o).encode(o, helperValues);
    }

    @Deprecated
    public static <T> Encoder<XmlObject, T> getEncoder(final String namespace, final T o) throws EncodingException {
        final EncoderKey key = getEncoderKey(namespace, o);
        final Encoder<XmlObject, T> encoder = CodingRepository.getInstance().getEncoder(key);
        if (encoder == null) {
            throw new NoEncoderForKeyException(key);
        }
        return encoder;
    }

    @Deprecated
    public static XmlObject encodeObjectToXml(final String namespace, final Object o) throws EncodingException {
        return encodeObjectToXml(namespace, o, Maps.<HelperValues, String> newEnumMap(HelperValues.class));
    }

    @Deprecated
    public static String encodeObjectToXmlText(final String namespace, final Object o) throws EncodingException {
        return encodeObjectToXml(namespace, o, Maps.<HelperValues, String> newEnumMap(HelperValues.class)).xmlText(
                XmlOptionsHelper.getInstance().getXmlOptions());
    }

    @Deprecated
    public static String encodeObjectToXmlText(final String namespace, final Object o,
            Map<HelperValues, String> additionalValues) throws EncodingException {
        return encodeObjectToXml(namespace, o, additionalValues).xmlText(
                XmlOptionsHelper.getInstance().getXmlOptions());
    }
  @Deprecated
    public static <T> T decodeXmlElement(final XmlObject x) throws DecodingException {
        return decodeXmlObject(x);
    }
    public static Set<DecoderKey> decoderKeysForElements(final String namespace, final Class<?>... elements) {
        final HashSet<DecoderKey> keys = new HashSet<>(elements.length);
        for (final Class<?> x : elements) {
            keys.add(new XmlNamespaceDecoderKey(namespace, x));
        }
        return keys;
    }

    public static Set<DecoderKey> xmlDecoderKeysForOperation(String service, String version, Enum<?>... operations) {
        final HashSet<DecoderKey> set = new HashSet<>(operations.length);
        for (final Enum<?> o : operations) {
            set.add(new OperationDecoderKey(service, version, o.name(), MediaTypes.TEXT_XML));
            set.add(new OperationDecoderKey(service, version, o.name(), MediaTypes.APPLICATION_XML));
        }
        return set;
    }

    public static Set<DecoderKey> xmlDecoderKeysForOperation(String service, String version, String... operations) {
        HashSet<DecoderKey> set = new HashSet<>(operations.length);
        for (String o : operations) {
            set.add(new OperationDecoderKey(service, version, o, MediaTypes.TEXT_XML));
            set.add(new OperationDecoderKey(service, version, o, MediaTypes.APPLICATION_XML));
        }
        return set;
    }

    public static Set<DecoderKey> xmlStringDecoderKeysForOperationAndMediaType(String service, String version, Enum<?>... operations) {
        final HashSet<DecoderKey> set = new HashSet<>(operations.length);
        for (final Enum<?> o : operations) {
            set.add(new XmlStringOperationDecoderKey(service, version, o, MediaTypes.TEXT_XML));
            set.add(new XmlStringOperationDecoderKey(service, version, o, MediaTypes.APPLICATION_XML));
        }
        return set;
    }

    public static Set<DecoderKey> xmlStringDecoderKeysForOperationAndMediaType(String service, String version, String... operations) {
        HashSet<DecoderKey> set = new HashSet<>(operations.length);
        for (String o : operations) {
            set.add(new XmlStringOperationDecoderKey(service, version, o, MediaTypes.TEXT_XML));
            set.add(new XmlStringOperationDecoderKey(service, version, o, MediaTypes.APPLICATION_XML));
        }
        return set;
    }

    public static Set<EncoderKey> encoderKeysForElements(final String namespace, final Class<?>... elements) {
        final HashSet<EncoderKey> keys = new HashSet<>(elements.length);
        for (final Class<?> x : elements) {
            keys.add(new XmlEncoderKey(namespace, x));
        }
        return keys;
    }

    public static EncoderKey getEncoderKey(String namespace, Object o) {
        return new XmlEncoderKey(namespace, o.getClass());
    }

    public static DecoderKey getDecoderKey(final XmlObject doc) {
        return new XmlNamespaceDecoderKey(XmlHelper.getNamespace(doc), doc.getClass());
    }

    public static <T extends XmlObject> DecoderKey getDecoderKey(final T[] doc) {
        return new XmlNamespaceDecoderKey(XmlHelper.getNamespace(doc[0]), doc.getClass());
    }

    @Deprecated
    public static <T> T decodeXmlObject(final XmlObject xbObject) throws DecodingException {
        final DecoderKey key = getDecoderKey(xbObject);
        final Decoder<T, XmlObject> decoder = CodingRepository.getInstance().getDecoder(key);
        if (decoder == null) {
            throw new NoDecoderForKeyException(key);
        }
        return decoder.decode(xbObject);
    }

    @Deprecated
    public static Object decodeXmlObject(final String xmlString) throws DecodingException {
        try {
            return decodeXmlObject(XmlObject.Factory.parse(xmlString));
        } catch (final XmlException e) {
            throw new XmlDecodingException("XML string", xmlString, e);
        }
    }

    public static XmlObject readXML(String string) throws XmlDecodingException {
        try {
            return XmlObject.Factory.parse(string);
        } catch (XmlException e) {
            throw new XmlDecodingException("XML string", string, e);
        }
    }
}
