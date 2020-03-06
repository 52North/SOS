/**
 * Copyright (C) 2012-2020 52°North Initiative for Geospatial Open Source
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
package org.n52.svalbard.decode;

import java.util.Set;

import org.apache.xmlbeans.XmlObject;
import org.n52.sos.decode.AbstractXmlDecoder;
import org.n52.sos.decode.DecoderKey;
import org.n52.sos.exception.ows.concrete.UnsupportedDecoderInputException;
import org.n52.sos.ogc.filter.Filter;
import org.n52.sos.ogc.ows.OwsExceptionReport;
import org.n52.sos.ogc.sos.ResultFilter;
import org.n52.sos.ogc.sos.ResultFilterConstants;
import org.n52.sos.util.CodingHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Sets;

import net.opengis.sosrf.x10.ResultFilterDocument;
import net.opengis.sosrf.x10.ResultFilterPropertyType;
import net.opengis.sosrf.x10.ResultFilterType;

public class ResultFilterDecoder extends AbstractXmlDecoder<ResultFilter> {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(ResultFilterDecoder.class);

    private static final Set<DecoderKey> DECODER_KEYS = CodingHelper.decoderKeysForElements(
            ResultFilterConstants.NS_RF, ResultFilterDocument.class, ResultFilterPropertyType.class, ResultFilterType.class);

    @Override
    public Set<DecoderKey> getDecoderKeyTypes() {
        return DECODER_KEYS;
    }

    @Override
    public ResultFilter decode(XmlObject xmlObject)
            throws OwsExceptionReport,
            UnsupportedDecoderInputException {
        if (xmlObject instanceof ResultFilterType) {
            return parseType((ResultFilterType) xmlObject);
        } else if (xmlObject instanceof ResultFilterPropertyType) {
            return parseType(((ResultFilterPropertyType)xmlObject).getResultFilter());
        } else if (xmlObject instanceof ResultFilterDocument) {
            return parseType(((ResultFilterDocument)xmlObject).getResultFilter());
        } else {
            throw new UnsupportedDecoderInputException(this, xmlObject);
        }
    }

    private ResultFilter parseType(ResultFilterType xmlObject) throws OwsExceptionReport {
        return new ResultFilter((Filter<?>)CodingHelper.decodeXmlElement(xmlObject.getComparisonOps()), ResultFilterConstants.NS_RF);
    }

    @Override
    public Set<String> getConformanceClasses() {
        return Sets.newHashSet(ResultFilterConstants.CONFORMANCE_CLASS_XML);
    }
}
