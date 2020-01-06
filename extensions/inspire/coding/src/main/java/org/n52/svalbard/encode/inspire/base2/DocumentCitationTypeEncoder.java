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
package org.n52.svalbard.encode.inspire.base2;

import java.util.Collections;
import java.util.Map;
import java.util.Set;

import org.apache.xmlbeans.XmlObject;
import org.n52.sos.encode.AbstractXmlEncoder;
import org.n52.sos.encode.ClassToClassEncoderKey;
import org.n52.sos.encode.EncoderKey;
import org.n52.sos.encode.XmlEncoderKey;
import org.n52.sos.exception.ows.concrete.UnsupportedEncoderInputException;
import org.n52.sos.ogc.ows.OwsExceptionReport;
import org.n52.sos.ogc.sos.SosConstants.HelperValues;
import org.n52.sos.w3c.Nillable;
import org.n52.svalbard.inspire.base2.DocumentCitation;
import org.n52.svalbard.inspire.base2.InspireBase2Constants;
import org.n52.svalbard.inspire.ompr.InspireOMPRConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Sets;

import eu.europa.ec.inspire.schemas.base2.x20.DocumentCitationType;
import eu.europa.ec.inspire.schemas.base2.x20.DocumentCitationType.Link;

public class DocumentCitationTypeEncoder extends AbstractXmlEncoder<DocumentCitation> {

    private static final Logger LOGGER = LoggerFactory.getLogger(DocumentCitationTypeEncoder.class);

    private static final Set<EncoderKey> ENCODER_KEYS =
            Sets.newHashSet(new ClassToClassEncoderKey(DocumentCitationType.class, DocumentCitation.class),
                    new XmlEncoderKey(InspireBase2Constants.NS_BASE2, DocumentCitation.class));

    @Override
    public Set<EncoderKey> getEncoderKeyType() {
        return Collections.unmodifiableSet(ENCODER_KEYS);
    }

    @Override
    public XmlObject encode(DocumentCitation documentCitation, Map<HelperValues, String> additionalValues)
            throws OwsExceptionReport, UnsupportedEncoderInputException {
        DocumentCitationType dct = DocumentCitationType.Factory.newInstance(getXmlOptions());
        if (documentCitation.isSetDate()) {
            dct.addNewDate().addNewCIDate().addNewDate().setDateTime(documentCitation.getDate().get().toGregorianCalendar());
        }
        if (documentCitation.isSetName()) {
            dct.setName2(documentCitation.getFirstName().getValue());
        }
        if (documentCitation.isSetLinks()) {
           for (Nillable<String> link : documentCitation.getLinks()) {
               if (link.isPresent()) {
                   dct.addNewLink().setStringValue(link.get());
               } else {
                   Link l = dct.addNewLink();
                   l.setNil();
                   if (link.getNilReason().isPresent()) {
                       l.setNilReason(link.getNilReason().get());
                   }
               }
           }
        }
        return dct;
    }


}
