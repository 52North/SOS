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
package org.n52.svalbard.decode.inspire;

import java.util.Collections;
import java.util.Set;

import org.apache.xmlbeans.XmlObject;
import org.joda.time.DateTime;
import org.n52.sos.decode.AbstractXmlDecoder;
import org.n52.sos.decode.DecoderKey;
import org.n52.sos.exception.ows.concrete.UnsupportedDecoderInputException;
import org.n52.sos.ogc.ows.OwsExceptionReport;
import org.n52.sos.util.CodingHelper;
import org.n52.sos.w3c.Nillable;
import org.n52.svalbard.inspire.base2.DocumentCitation;
import org.n52.svalbard.inspire.ompr.InspireOMPRConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.europa.ec.inspire.schemas.base2.x20.DocumentCitationType;
import eu.europa.ec.inspire.schemas.base2.x20.DocumentCitationType.Link;

public class DocumentCitationTypeDecoder extends AbstractXmlDecoder<DocumentCitation> {

    private static final Logger LOGGER = LoggerFactory.getLogger(DocumentCitationTypeDecoder.class);

    private static final Set<DecoderKey> DECODER_KEYS = CodingHelper.decoderKeysForElements(
            InspireOMPRConstants.NS_OMPR_30, DocumentCitationType.class);

    @Override
    public Set<DecoderKey> getDecoderKeyTypes() {
        return Collections.unmodifiableSet(DECODER_KEYS);
    }

    @Override
    public DocumentCitation decode(XmlObject xmlObject)
            throws OwsExceptionReport, UnsupportedDecoderInputException {
        if (xmlObject instanceof DocumentCitationType) {
            DocumentCitation documentCitation = new DocumentCitation();
            DocumentCitationType dct = (DocumentCitationType)xmlObject;
            documentCitation.setDescription(dct.getDescription().getStringValue());
            if (dct.isNilDate()) {
                if (dct.getDate().isSetNilReason()) {
                    documentCitation.setDate(Nillable.<DateTime>nil(dct.getDate().getNilReason().toString()));
                }
            } else {
                documentCitation.setDate(new DateTime(dct.getDate().getCIDate().getDate().getDate().getTime()));
            }
            if (dct.getLinkArray() != null) {
                for (Link link : dct.getLinkArray()) {
                    if (link.isNil() && link.isSetNilReason()) {
                        documentCitation.addLink(Nillable.<String>nil(link.getNilReason().toString()));
                    } else {
                        documentCitation.addLink(link.getStringValue());
                    }
                }
            }
            return documentCitation;
        }
        throw new UnsupportedDecoderInputException(this, xmlObject);
    }

}
