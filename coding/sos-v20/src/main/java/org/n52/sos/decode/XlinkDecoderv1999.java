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

import java.util.Collections;
import java.util.Map;
import java.util.Set;

import org.apache.xmlbeans.XmlObject;
import org.n52.sos.exception.ows.concrete.UnsupportedDecoderInputException;
import org.n52.sos.ogc.ows.OwsExceptionReport;
import org.n52.sos.service.ServiceConstants.SupportedTypeKey;
import org.n52.sos.util.CodingHelper;
import org.n52.sos.w3c.W3CConstants;
import org.n52.sos.w3c.xlink.W3CHrefAttribute;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3.x1999.xlink.HrefAttribute;

public class XlinkDecoderv1999 implements Decoder<Object, XmlObject> {

    private static final Logger LOGGER = LoggerFactory.getLogger(XlinkDecoderv1999.class);

    private static final Set<DecoderKey> DECODER_KEYS = CodingHelper.decoderKeysForElements(W3CConstants.NS_XLINK,
            HrefAttribute.class);

    public XlinkDecoderv1999() {
        StringBuilder builder = new StringBuilder();
        for (DecoderKey decoderKeyType : DECODER_KEYS) {
            builder.append(decoderKeyType.toString());
            builder.append(", ");
        }
        builder.delete(builder.lastIndexOf(", "), builder.length());
        LOGGER.debug("Decoder for the following keys initialized successfully: " + builder.toString() + "!");
    }

    @Override
    public Set<DecoderKey> getDecoderKeyTypes() {
        return Collections.unmodifiableSet(DECODER_KEYS);
    }

    @Override
    public Set<String> getConformanceClasses() {
        return Collections.emptySet();
    }

    @Override
    public Map<SupportedTypeKey, Set<String>> getSupportedTypes() {
        return Collections.emptyMap();
    }

    @Override
    public Object decode(XmlObject xmlObject) throws OwsExceptionReport, UnsupportedDecoderInputException {
        if (xmlObject instanceof HrefAttribute) {
            return encodeHrefAttribute((HrefAttribute) xmlObject);
        } else {
            throw new UnsupportedDecoderInputException(this, xmlObject);
        }
    }

    private W3CHrefAttribute encodeHrefAttribute(HrefAttribute xmlObject) {
        W3CHrefAttribute href = new W3CHrefAttribute();
        if (xmlObject.isSetHref()) {
            href.setHref(xmlObject.getHref());
        }
        return href;
    }

}
