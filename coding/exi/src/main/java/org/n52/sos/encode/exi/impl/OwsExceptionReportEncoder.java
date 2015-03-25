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
package org.n52.sos.encode.exi.impl;

import java.util.Collections;
import java.util.Map;
import java.util.Set;

import org.apache.xmlbeans.XmlObject;
import org.n52.sos.coding.CodingRepository;
import org.n52.sos.encode.Encoder;
import org.n52.sos.encode.EncoderKey;
import org.n52.sos.encode.ExceptionEncoderKey;
import org.n52.sos.exception.ows.NoApplicableCodeException;
import org.n52.sos.exception.ows.concrete.UnsupportedEncoderInputException;
import org.n52.sos.exi.EXIObject;
import org.n52.sos.ogc.ows.OwsExceptionReport;
import org.n52.sos.ogc.sos.SosConstants.HelperValues;
import org.n52.sos.service.ServiceConstants.SupportedTypeKey;
import org.n52.sos.util.http.MediaType;
import org.n52.sos.util.http.MediaTypes;
import org.n52.sos.w3c.SchemaLocation;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.ImmutableSet.Builder;

/**
 * Response encoder for {@link EXIObject} and {@link OwsExceptionReport}
 * 
 * @author Carsten Hollmann <c.hollmann@52north.org>
 * @since 4.2.0
 *
 */
public class OwsExceptionReportEncoder implements Encoder<EXIObject, OwsExceptionReport> {

    public static final String CONTENT_TYPE = "application/exi";

    private final Set<EncoderKey> encoderKeys;

    /**
     * Constructor
     */
    public OwsExceptionReportEncoder() {
        Builder<EncoderKey> set = ImmutableSet.builder();
        set.add(new ExceptionEncoderKey(MediaTypes.APPLICATION_EXI));
        this.encoderKeys = set.build();
    }

    @Override
    public Set<EncoderKey> getEncoderKeyType() {
        return Collections.unmodifiableSet(encoderKeys);
    }

    @Override
    public Map<SupportedTypeKey, Set<String>> getSupportedTypes() {
        return Collections.emptyMap();
    }

    @Override
    public void addNamespacePrefixToMap(Map<String, String> nameSpacePrefixMap) {
        /* noop */
    }

    @Override
    public MediaType getContentType() {
        return MediaTypes.APPLICATION_EXI;
    }

    @Override
    public Set<SchemaLocation> getSchemaLocations() {
        return Collections.emptySet();
    }

    @Override
    public Set<String> getConformanceClasses() {
        return Collections.emptySet();
    }

    @Override
    public EXIObject encode(OwsExceptionReport objectToEncode) throws OwsExceptionReport,
            UnsupportedEncoderInputException {
        Encoder<Object, OwsExceptionReport> encoder = getEncoder(new ExceptionEncoderKey(MediaTypes.APPLICATION_XML));
        if (encoder != null) {
            Object encode = encoder.encode(objectToEncode);
            if (encode != null && encode instanceof XmlObject) {
                return new EXIObject((XmlObject) encode);
            } else {
                throw new UnsupportedEncoderInputException(encoder, objectToEncode);
            }
        }
        throw new NoApplicableCodeException().withMessage("Unable to encode {}", objectToEncode);
    }

    @Override
    public EXIObject encode(OwsExceptionReport objectToEncode, Map<HelperValues, String> additionalValues)
            throws OwsExceptionReport, UnsupportedEncoderInputException {
        return encode(objectToEncode, null);
    }

    /**
     * Getter for encoder, encapsulates the instance call
     * 
     * @param key
     *            Encoder key
     * @return Matching encoder
     */
    protected <D, S> Encoder<D, S> getEncoder(EncoderKey key) {
        return CodingRepository.getInstance().getEncoder(key);
    }

}
