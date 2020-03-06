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
import org.n52.sos.ogc.filter.SpatialFilter;
import org.n52.sos.ogc.ows.OwsExceptionReport;
import org.n52.sos.ogc.sos.SosSpatialFilter;
import org.n52.sos.ogc.sos.SosSpatialFilterConstants;
import org.n52.sos.util.CodingHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Sets;

import net.opengis.sossf.x10.SpatialFilterDocument;
import net.opengis.sossf.x10.SpatialFilterPropertyType;
import net.opengis.sossf.x10.SpatialFilterType;

public class SosSpatialFilterDecoder extends AbstractXmlDecoder<SosSpatialFilter> {

    private static final Logger LOGGER = LoggerFactory.getLogger(SosSpatialFilterDecoder.class);

    private static final Set<DecoderKey> DECODER_KEYS = CodingHelper.decoderKeysForElements(
            SosSpatialFilterConstants.NS_SF, SpatialFilterDocument.class, SpatialFilterPropertyType.class, SpatialFilterType.class);

    @Override
    public Set<DecoderKey> getDecoderKeyTypes() {
        return DECODER_KEYS;
    }

    @Override
    public SosSpatialFilter decode(XmlObject xmlObject)
            throws OwsExceptionReport,
            UnsupportedDecoderInputException {
        if (xmlObject instanceof SpatialFilterType) {
            return parseType((SpatialFilterType) xmlObject);
        } else if (xmlObject instanceof SpatialFilterPropertyType) {
            return parseType(((SpatialFilterPropertyType) xmlObject).getSpatialFilter());
        } else if (xmlObject instanceof SpatialFilterDocument) {
            return parseType(((SpatialFilterDocument) xmlObject).getSpatialFilter());
        } else {
            throw new UnsupportedDecoderInputException(this, xmlObject);
        }
    }

    private SosSpatialFilter parseType(SpatialFilterType xmlObject) throws OwsExceptionReport {
        return new SosSpatialFilter((SpatialFilter) CodingHelper.decodeXmlElement(xmlObject.getSpatialOps()));
    }

    @Override
    public Set<String> getConformanceClasses() {
        return Sets.newHashSet(SosSpatialFilterConstants.CONFORMANCE_CLASS_XML);
    }
}
