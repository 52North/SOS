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
package org.n52.iceland.encode;

import java.util.Map;
import java.util.Set;

import org.n52.iceland.exception.ows.concrete.UnsupportedEncoderInputException;
import org.n52.iceland.ogc.ows.OWSConstants.HelperValues;
import org.n52.iceland.ogc.ows.OwsExceptionReport;
import org.n52.iceland.service.ConformanceClass;
import org.n52.iceland.service.ServiceConstants.SupportedTypeKey;
import org.n52.iceland.util.http.MediaType;
import org.n52.iceland.w3c.SchemaLocation;

/**
 * Generic interface for Encoders.
 * 
 * @param <T>
 *            the resulting type, the "Target"
 * @param <S>
 *            the input type, the "Source"
 * 
 * @since 4.0.0
 */
public interface Encoder<T, S> extends ConformanceClass {
    /**
     * @return List of supported encodings of this implementation (identified by
     *         {@link EncoderKey})
     */
    Set<EncoderKey> getEncoderKeyType();

    /**
     * Encodes the specified object.
     * 
     * @param objectToEncode
     *            the object to encode
     * 
     * @return the encoded object
     * 
     * @throws OwsExceptionReport
     *             if an error occurs
     * @throws UnsupportedEncoderInputException
     *             if the supplied object (or any of it's contents) is not
     *             supported by this encoder
     */
    T encode(S objectToEncode) throws OwsExceptionReport, UnsupportedEncoderInputException;

    /**
     * Encodes the specified object with the specified {@linkplain HelperValues}
     * .
     * 
     * @param objectToEncode
     *            the object to encode
     * @param additionalValues
     *            the helper values
     * 
     * @return the encoded object
     * 
     * @throws OwsExceptionReport
     *             if an error occurs
     * @throws UnsupportedEncoderInputException
     *             if the supplied object (or any of it's contents) is not
     *             supported by this encoder
     */
    T encode(S objectToEncode, Map<HelperValues, String> additionalValues) throws OwsExceptionReport,
            UnsupportedEncoderInputException;

    /**
     * Get the {@linkplain SupportedTypeKey}
     * 
     * @return the supported key types
     */
    Map<SupportedTypeKey, Set<String>> getSupportedTypes();

    /**
     * Add the namespace prefix of this {@linkplain Encoder} instance to the
     * given {@linkplain Map}.
     * 
     * @param nameSpacePrefixMap
     */
    void addNamespacePrefixToMap(Map<String, String> nameSpacePrefixMap);

    /**
     * @return the content type of the encoded response.
     */
    MediaType getContentType();

    Set<SchemaLocation> getSchemaLocations();
}
