/**
 * Copyright 2015 52°North Initiative for Geospatial Open Source
 * Software GmbH
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.n52.iceland.util;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.apache.xmlbeans.XmlException;
import org.apache.xmlbeans.XmlObject;
import org.n52.iceland.coding.CodingRepository;
import org.n52.iceland.decode.Decoder;
import org.n52.iceland.decode.DecoderKey;
import org.n52.iceland.decode.OperationDecoderKey;
import org.n52.iceland.decode.XmlNamespaceDecoderKey;
import org.n52.iceland.encode.Encoder;
import org.n52.iceland.encode.EncoderKey;
import org.n52.iceland.encode.XmlEncoderKey;
import org.n52.iceland.exception.ows.concrete.NoDecoderForKeyException;
import org.n52.iceland.exception.ows.concrete.NoEncoderForKeyException;
import org.n52.iceland.exception.ows.concrete.XmlDecodingException;
import org.n52.iceland.ogc.ows.OWSConstants;
import org.n52.iceland.ogc.ows.OWSConstants.HelperValues;
import org.n52.iceland.ogc.ows.OwsExceptionReport;
import org.n52.iceland.util.http.MediaTypes;

import com.google.common.collect.Maps;

/**
 * @author Christian Autermann <c.autermann@52north.org> TODO implement
 *         encodeToXml(Object o) using a Map from o.getClass().getName() ->
 *         namespaces
 * @since 4.0.0
 *
 */
public final class CodingHelper {

    public static Object decodeXmlElement(final XmlObject x) throws OwsExceptionReport {
        return decodeXmlObject(x);
    }

    public static <T> XmlObject encodeObjectToXml(String namespace, T o,
            Map<OWSConstants.HelperValues, String> helperValues) throws OwsExceptionReport {
        return getEncoder(namespace, o).encode(o, helperValues);
    }

    public static <T> Encoder<XmlObject, T> getEncoder(final String namespace, final T o) throws OwsExceptionReport {
        final EncoderKey key = getEncoderKey(namespace, o);
        final Encoder<XmlObject, T> encoder = CodingRepository.getInstance().getEncoder(key);
        if (encoder == null) {
            throw new NoEncoderForKeyException(key);
        }
        return encoder;
    }

    public static XmlObject encodeObjectToXml(final String namespace, final Object o) throws OwsExceptionReport {
        return encodeObjectToXml(namespace, o, Maps.<HelperValues, String> newEnumMap(HelperValues.class));
    }

    public static String encodeObjectToXmlText(final String namespace, final Object o) throws OwsExceptionReport {
        return encodeObjectToXml(namespace, o, Maps.<HelperValues, String> newEnumMap(HelperValues.class)).xmlText(
                XmlOptionsHelper.getInstance().getXmlOptions());
    }

    public static String encodeObjectToXmlText(final String namespace, final Object o,
            Map<OWSConstants.HelperValues, String> additionalValues) throws OwsExceptionReport {
        return encodeObjectToXml(namespace, o, additionalValues).xmlText(
                XmlOptionsHelper.getInstance().getXmlOptions());
    }

    public static Set<DecoderKey> decoderKeysForElements(final String namespace, final Class<?>... elements) {
        final HashSet<DecoderKey> keys = new HashSet<DecoderKey>(elements.length);
        for (final Class<?> x : elements) {
            keys.add(new XmlNamespaceDecoderKey(namespace, x));
        }
        return keys;
    }

    public static Set<DecoderKey> xmlDecoderKeysForOperation(String service, String version, Enum<?>... operations) {
        final HashSet<DecoderKey> set = new HashSet<DecoderKey>(operations.length);
        for (final Enum<?> o : operations) {
            set.add(new OperationDecoderKey(service, version, o.name(), MediaTypes.TEXT_XML));
            set.add(new OperationDecoderKey(service, version, o.name(), MediaTypes.APPLICATION_XML));
        }
        return set;
    }

    public static Set<DecoderKey> xmlDecoderKeysForOperation(String service, String version, String... operations) {
        HashSet<DecoderKey> set = new HashSet<DecoderKey>(operations.length);
        for (String o : operations) {
            set.add(new OperationDecoderKey(service, version, o, MediaTypes.TEXT_XML));
            set.add(new OperationDecoderKey(service, version, o, MediaTypes.APPLICATION_XML));
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

    public static EncoderKey getEncoderKey(final String namespace, final Object o) {
        return new XmlEncoderKey(namespace, o.getClass());
    }

    public static DecoderKey getDecoderKey(final XmlObject doc) {
        return new XmlNamespaceDecoderKey(XmlHelper.getNamespace(doc), doc.getClass());
    }

    public static <T extends XmlObject> DecoderKey getDecoderKey(final T[] doc) {
        return new XmlNamespaceDecoderKey(XmlHelper.getNamespace(doc[0]), doc.getClass());
    }

    public static Object decodeXmlObject(final XmlObject xbObject) throws OwsExceptionReport {
        final DecoderKey key = getDecoderKey(xbObject);
        final Decoder<?, XmlObject> decoder = CodingRepository.getInstance().getDecoder(key);
        if (decoder == null) {
            throw new NoDecoderForKeyException(key);
        }
        return decoder.decode(xbObject);
    }

    public static Object decodeXmlObject(final String xmlString) throws OwsExceptionReport {
        try {
            return decodeXmlObject(XmlObject.Factory.parse(xmlString));
        } catch (final XmlException e) {
            throw new XmlDecodingException("XML string", xmlString, e);
        }
    }

    private CodingHelper() {
    }
}
