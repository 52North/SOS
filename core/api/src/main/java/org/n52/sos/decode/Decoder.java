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
package org.n52.sos.decode;

import java.util.Map;
import java.util.Set;

import org.n52.sos.exception.ows.concrete.UnsupportedDecoderInputException;
import org.n52.sos.ogc.ows.OwsExceptionReport;
import org.n52.sos.service.ConformanceClass;
import org.n52.sos.service.ServiceConstants.SupportedTypeKey;

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
