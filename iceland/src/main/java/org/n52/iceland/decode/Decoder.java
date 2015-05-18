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
package org.n52.iceland.decode;

import java.util.Map;
import java.util.Set;

import org.n52.iceland.exception.ows.concrete.UnsupportedDecoderInputException;
import org.n52.iceland.ogc.ows.OwsExceptionReport;
import org.n52.iceland.service.ConformanceClass;
import org.n52.iceland.service.ServiceConstants.SupportedTypeKey;

/**
 * Generic interface for decoders.
 * 
 * @param <T>
 *            the result of the decoding process, the "Target"
 * @param <S>
 *            the input which is decoded, the "Source"
 * 
 * @since 4.0.0
 */
public interface Decoder<T, S> extends ConformanceClass {
    /**
     * @return List encodings this implementation (identified by
     *         {@link DecoderKey}) is able to decode
     */
    Set<DecoderKey> getDecoderKeyTypes();

    /**
     * Decode a object to another representation.
     * 
     * @param objectToDecode
     *            the object to encode
     * 
     * @return the encoded object
     * 
     * @throws OwsExceptionReport
     *             if an error occurs
     * @throws UnsupportedDecoderInputException
     *             if the supplied type (or any of it's contents) is not
     *             supported by this decoder
     */
    T decode(S objectToDecode) throws OwsExceptionReport, UnsupportedDecoderInputException;

    /**
     * Get the {@linkplain SupportedTypeKey} in the case of having only generic
     * java types, e.g. {@linkplain org.n52.sos.ogc.om.OmConstants}. In this
     * case, the returned list provides a mapping from Type &rarr; SubType (e.g.
     * {@linkplain org.n52.sos.service.ServiceConstants}
     * .SupportedTypeKey.ObservationType &rarr;
     * {@linkplain org.n52.sos.ogc.om.OmConstants}
     * .OBS_TYPE_CATEGORY_OBSERVATION}).
     * 
     * @return the supported key types
     */
    Map<SupportedTypeKey, Set<String>> getSupportedTypes();
}
