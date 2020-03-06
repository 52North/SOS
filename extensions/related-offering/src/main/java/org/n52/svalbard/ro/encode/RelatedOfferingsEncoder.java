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
package org.n52.svalbard.ro.encode;

import java.io.ByteArrayOutputStream;
import java.io.UnsupportedEncodingException;
import java.util.Map;
import java.util.Set;

import javax.xml.stream.XMLStreamException;

import org.apache.xmlbeans.XmlException;
import org.apache.xmlbeans.XmlObject;
import org.n52.sos.encode.AbstractXmlEncoder;
import org.n52.sos.encode.EncoderKey;
import org.n52.sos.exception.ows.NoApplicableCodeException;
import org.n52.sos.exception.ows.concrete.UnsupportedEncoderInputException;
import org.n52.sos.ogc.ows.OwsExceptionReport;
import org.n52.sos.ogc.sos.RelatedOfferingConstants;
import org.n52.sos.ogc.sos.RelatedOfferings;
import org.n52.sos.ogc.sos.SosConstants.HelperValues;
import org.n52.sos.util.CodingHelper;
import org.n52.svalbard.ro.encode.streaming.RelatedOfferingXmlStreamWriter;

import net.opengis.sosro.x10.RelatedOfferingsPropertyType;

public class RelatedOfferingsEncoder extends AbstractXmlEncoder<RelatedOfferings> {
    
    private static final Set<EncoderKey> ENCODER_KEYS = CodingHelper.encoderKeysForElements(RelatedOfferingConstants.NS_RO,
            RelatedOfferings.class);

    @Override
    public Set<EncoderKey> getEncoderKeyType() {
        return ENCODER_KEYS;
    }
    
    @Override
    public void addNamespacePrefixToMap(Map<String, String> nameSpacePrefixMap) {
        nameSpacePrefixMap.put(RelatedOfferingConstants.NS_RO, RelatedOfferingConstants.NS_RO_PREFIX);
    }

    @Override
    public XmlObject encode(RelatedOfferings objectToEncode, Map<HelperValues, String> additionalValues)
            throws OwsExceptionReport, UnsupportedEncoderInputException {
        try {
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            new RelatedOfferingXmlStreamWriter(objectToEncode).write(out);
            return RelatedOfferingsPropertyType.Factory.parse(out.toString("UTF8"));
        } catch (XMLStreamException | XmlException | UnsupportedEncodingException ex) {
            throw new NoApplicableCodeException().causedBy(ex).withMessage("Error encoding %s", objectToEncode.getClass().getSimpleName());
        }
    }



}
