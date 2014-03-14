/**
 * Copyright (C) 2012-2014 52°North Initiative for Geospatial Open Source
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
package org.n52.sos.gda;

import java.util.Collections;
import java.util.Set;

import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.impl.values.XmlAnyTypeImpl;
import org.n52.sos.decode.AbstractXmlDecoder;
import org.n52.sos.decode.DecoderKey;
import org.n52.sos.ogc.ows.OwsExceptionReport;
import org.n52.sos.ogc.sos.Sos2Constants;
import org.n52.sos.ogc.sos.SosConstants;
import org.n52.sos.util.CodingHelper;
import org.n52.sos.util.CollectionHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Joiner;

/**
 * {@code Decoder} to handle {@link GetDataAvailabilityRequest}s.
 * 
 * @author Christian Autermann
 * 
 * @since 4.0.0
 */
public class GetDataAvailabilityXmlDecoder extends AbstractXmlDecoder<GetDataAvailabilityRequest> {

    private static final Logger LOG = LoggerFactory.getLogger(GetDataAvailabilityXmlDecoder.class);

    private static final String XPATH_PREFIXES = String.format("declare namespace sos='%s';", Sos2Constants.NS_SOS_20);

    private static final String BASE_PATH = XPATH_PREFIXES + "/sos:GetDataAvailability";

    @SuppressWarnings("unchecked")
    private static final Set<DecoderKey> DECODER_KEYS = CollectionHelper.union(CodingHelper.decoderKeysForElements(
            Sos2Constants.NS_SOS_20, XmlObject.class), CodingHelper.xmlDecoderKeysForOperation(SosConstants.SOS,
            Sos2Constants.SERVICEVERSION, GetDataAvailabilityConstants.OPERATION_NAME));

    /**
     * Constructs a new {@code GetDataAvailabilityDecoder}.
     */
    public GetDataAvailabilityXmlDecoder() {
        LOG.debug("Decoder for the following keys initialized successfully: {}!", Joiner.on(", ").join(DECODER_KEYS));
    }

    @Override
    public Set<DecoderKey> getDecoderKeyTypes() {
        return Collections.unmodifiableSet(DECODER_KEYS);
    }

    @Override
    public GetDataAvailabilityRequest decode(XmlObject xml) throws OwsExceptionReport {
        return parseGetDataAvailability(xml);
    }

    /**
     * Parses a {@code GetDataAvailabilityRequest}.
     * 
     * @param xml
     *            the request
     * 
     * @return the parsed request
     */
    public GetDataAvailabilityRequest parseGetDataAvailability(XmlObject xml) {
        GetDataAvailabilityRequest request = new GetDataAvailabilityRequest();
        XmlObject[] roots = xml.selectPath(BASE_PATH);
        if (roots != null && roots.length > 0) {
            XmlObject version = roots[0].selectAttribute(GetDataAvailabilityConstants.SOS_VERSION);
            if (version == null) {
                version = roots[0].selectAttribute(GetDataAvailabilityConstants.VERSION);
            }
            if (version != null) {
                request.setVersion(((XmlAnyTypeImpl) version).getStringValue());
            }
            XmlObject service = roots[0].selectAttribute(GetDataAvailabilityConstants.SOS_SERVICE);
            if (service == null) {
                service = roots[0].selectAttribute(GetDataAvailabilityConstants.SERVICE);
            }
            if (service != null) {
                request.setService(((XmlAnyTypeImpl) service).getStringValue());
            }
        }

        for (XmlObject x : xml.selectPath(BASE_PATH + "/sos:observedProperty")) {
            request.addObservedProperty(((XmlAnyTypeImpl) x).getStringValue());
        }
        for (XmlObject x : xml.selectPath(BASE_PATH + "/sos:procedure")) {
            request.addProcedure(((XmlAnyTypeImpl) x).getStringValue());
        }
        for (XmlObject x : xml.selectPath(BASE_PATH + "/sos:featureOfInterest")) {
            request.addFeatureOfInterest(((XmlAnyTypeImpl) x).getStringValue());
        }
        for (XmlObject x : xml.selectPath(BASE_PATH + "/sos:offering")) {
            request.addOffering(((XmlAnyTypeImpl) x).getStringValue());
        }
        return request;
    }
}
