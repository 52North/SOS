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
package org.n52.sos.decode.sos.v2;

import static org.n52.sos.util.CodingHelper.decodeXmlObject;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import org.n52.iceland.coding.decode.Decoder;
import org.n52.iceland.coding.decode.DecoderKey;
import org.n52.iceland.exception.ows.OwsExceptionReport;
import org.n52.iceland.exception.ows.concrete.UnsupportedDecoderInputException;
import org.n52.iceland.ogc.sos.Sos2Constants;
import org.n52.iceland.util.CollectionHelper;
import org.n52.sos.ogc.om.OmObservation;
import org.n52.sos.response.GetObservationResponse;
import org.n52.sos.util.CodingHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Joiner;
import com.google.common.collect.Lists;

import net.opengis.sos.x20.GetObservationResponseDocument;
import net.opengis.sos.x20.GetObservationResponseType;
import net.opengis.sos.x20.GetObservationResponseType.ObservationData;

public class GetObservationResponseDocumentDecoder implements SosResponseDecoder, Decoder<GetObservationResponse, GetObservationResponseDocument> {

    private static final Logger LOGGER = LoggerFactory.getLogger(GetObservationResponseDocumentDecoder.class);
    
    private static final Set<DecoderKey> DECODER_KEYS = CodingHelper.decoderKeysForElements(
            Sos2Constants.NS_SOS_20,
            GetObservationResponseDocument.class);

    public GetObservationResponseDocumentDecoder() {
        LOGGER.debug("Decoder for the following keys initialized successfully: {}!", Joiner.on(", ")
                .join(DECODER_KEYS));
    }

    @Override
    public Set<DecoderKey> getKeys() {
        return Collections.unmodifiableSet(DECODER_KEYS);
    }

    @Override
    public GetObservationResponse decode(GetObservationResponseDocument gord)
            throws OwsExceptionReport, UnsupportedDecoderInputException {
        if (gord != null)  {
            GetObservationResponse response = new GetObservationResponse();
            setService(response);
            setVersions(response);
            GetObservationResponseType gort = gord.getGetObservationResponse();
            response.setExtensions(parseExtensibleResponse(gort));
            response.setObservationCollection(parseObservtions(gort));
            
            
            return response;
        }
        throw new UnsupportedDecoderInputException(this, gord);
    }

    private List<OmObservation> parseObservtions(GetObservationResponseType gort) throws OwsExceptionReport {
        if (CollectionHelper.isNotNullOrEmpty(gort.getObservationDataArray())) {
            List<OmObservation> observations = Lists.newArrayList();
            for (ObservationData od : gort.getObservationDataArray()) {
                observations.add((OmObservation)decodeXmlObject(od.getOMObservation()));
            }
            return observations;
        }
        return Collections.emptyList();
    }

}
