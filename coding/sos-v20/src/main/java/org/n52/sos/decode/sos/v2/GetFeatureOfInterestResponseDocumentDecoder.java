/*
 * Copyright (C) 2012-2016 52°North Initiative for Geospatial Open Source
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
import java.util.Set;

import org.n52.iceland.coding.decode.Decoder;
import org.n52.iceland.coding.decode.DecoderKey;
import org.n52.iceland.exception.ows.OwsExceptionReport;
import org.n52.iceland.exception.ows.concrete.UnsupportedDecoderInputException;
import org.n52.iceland.ogc.gml.AbstractFeature;
import org.n52.iceland.ogc.sos.Sos2Constants;
import org.n52.iceland.util.CollectionHelper;
import org.n52.sos.ogc.om.features.FeatureCollection;
import org.n52.sos.response.GetFeatureOfInterestResponse;
import org.n52.sos.util.CodingHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Joiner;

import net.opengis.gml.x32.FeaturePropertyType;
import net.opengis.sos.x20.GetFeatureOfInterestResponseDocument;
import net.opengis.sos.x20.GetFeatureOfInterestResponseType;

/**
 * XML {@link Decoder} for {@link GetFeatureOfInterestResponse}
 *
 * @author <a href="mailto:c.hollmann@52north.org">Carsten Hollmann</a>
 * @since 5.0.0
 *
 */
public class GetFeatureOfInterestResponseDocumentDecoder implements SosResponseDecoder, Decoder<GetFeatureOfInterestResponse, GetFeatureOfInterestResponseDocument> {

    private static final Logger LOGGER = LoggerFactory.getLogger(GetFeatureOfInterestResponseDocumentDecoder.class);

    private static final Set<DecoderKey> DECODER_KEYS = CodingHelper.decoderKeysForElements(
            Sos2Constants.NS_SOS_20,
            GetFeatureOfInterestResponseDocument.class);

    public GetFeatureOfInterestResponseDocumentDecoder() {
        LOGGER.debug("Decoder for the following keys initialized successfully: {}!", Joiner.on(", ")
                .join(DECODER_KEYS));
    }

    @Override
    public Set<DecoderKey> getKeys() {
        return Collections.unmodifiableSet(DECODER_KEYS);
    }

    @Override
    public GetFeatureOfInterestResponse decode(GetFeatureOfInterestResponseDocument gfoird)
            throws OwsExceptionReport, UnsupportedDecoderInputException {
        if (gfoird != null)  {
            GetFeatureOfInterestResponse response = new GetFeatureOfInterestResponse();
            setService(response);
            setVersions(response);
            GetFeatureOfInterestResponseType gfoirt = gfoird.getGetFeatureOfInterestResponse();
            response.setExtensions(parseExtensibleResponse(gfoirt));
            response.setAbstractFeature(parseFeatures(gfoirt));
            return response;
        }
        throw new UnsupportedDecoderInputException(this, gfoird);
    }

    private AbstractFeature parseFeatures(GetFeatureOfInterestResponseType gfoirt) throws OwsExceptionReport {
        if (CollectionHelper.isNotNullOrEmpty(gfoirt.getFeatureMemberArray())) {
            if (gfoirt.getFeatureMemberArray().length == 1) {
                return (AbstractFeature)decodeXmlObject(gfoirt.getFeatureMemberArray()[0]);
            } else {
                FeatureCollection featureCollection = new FeatureCollection();
                for (FeaturePropertyType fpt : gfoirt.getFeatureMemberArray()) {
                    featureCollection.addMember((AbstractFeature)decodeXmlObject(fpt));
                }
                return featureCollection;
            }
        }
        return null;
    }

}
