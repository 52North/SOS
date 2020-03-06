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

import static java.lang.String.format;
import static java.util.Collections.emptyMap;
import static org.n52.sos.ogc.sos.SosConstants.SOS;
import static org.n52.sos.util.CodingHelper.decoderKeysForElements;
import static org.n52.sos.util.CodingHelper.xmlDecoderKeysForOperation;
import static org.n52.sos.util.CollectionHelper.union;

import java.util.Collections;
import java.util.Map;
import java.util.Set;

import org.apache.xmlbeans.XmlObject;
import org.n52.shetland.ogc.sos.ifoi.InsertFeatureOfInterestConstants;
import org.n52.sos.decode.Decoder;
import org.n52.sos.decode.DecoderKey;
import org.n52.sos.exception.ows.NoApplicableCodeException;
import org.n52.sos.exception.ows.concrete.UnsupportedDecoderInputException;
import org.n52.sos.ogc.gml.AbstractFeature;
import org.n52.sos.ogc.ows.OwsExceptionReport;
import org.n52.sos.ogc.sos.Sos2Constants;
import org.n52.sos.request.InsertFeatureOfInterestRequest;
import org.n52.sos.service.ServiceConstants.SupportedTypeKey;
import org.n52.sos.util.CodingHelper;
import org.n52.sos.util.CollectionHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Joiner;
import com.google.common.collect.Sets;

import net.opengis.gml.x32.FeaturePropertyType;
import net.opengis.ifoi.x10.InsertFeatureOfInterestDocument;
import net.opengis.ifoi.x10.InsertFeatureOfInterestType;


/**
 * @since 1.0.0
 */
public class InsertFeatureOfInterestDecoder implements Decoder<InsertFeatureOfInterestRequest, XmlObject> {

    @SuppressWarnings("unchecked")
    private static final Set<DecoderKey> DECODER_KEYS =
            union(decoderKeysForElements(InsertFeatureOfInterestConstants.NS_IFOI, InsertFeatureOfInterestDocument.class), xmlDecoderKeysForOperation(
                    SOS, Sos2Constants.SERVICEVERSION, InsertFeatureOfInterestConstants.OPERATION_NAME));

    private static final Logger LOGGER = LoggerFactory.getLogger(InsertFeatureOfInterestDecoder.class);

    public InsertFeatureOfInterestDecoder() {
        LOGGER.info("Decoder for the following keys initialized successfully: {}!",
                Joiner.on(", ").join(DECODER_KEYS));
    }

    public Set<DecoderKey> getDecoderKeyTypes() {
        return Collections.unmodifiableSet(DECODER_KEYS);
    }

    public InsertFeatureOfInterestRequest decode(XmlObject xmlObject) throws OwsExceptionReport {
        LOGGER.debug(format("REQUESTTYPE: %s", xmlObject != null ? xmlObject.getClass() : "null recevied"));
        // XmlHelper.validateDocument(xmlObject);
        if (xmlObject instanceof InsertFeatureOfInterestDocument) {
            InsertFeatureOfInterestDocument ifoid = (InsertFeatureOfInterestDocument) xmlObject;
            InsertFeatureOfInterestRequest decodedRequest = parseInsertFeatureOfInterest(ifoid);
            LOGGER.debug(String.format("Decoded request: %s", decodedRequest));
            return decodedRequest;
        } else {
            throw new UnsupportedDecoderInputException(this, xmlObject);
        }
    }

    private InsertFeatureOfInterestRequest parseInsertFeatureOfInterest(InsertFeatureOfInterestDocument ifoid)
            throws OwsExceptionReport {
        InsertFeatureOfInterestRequest request = null;

        InsertFeatureOfInterestType ifoit = ifoid.getInsertFeatureOfInterest();

        if (ifoit != null) {
            request = new InsertFeatureOfInterestRequest();
            request.setVersion(ifoit.getVersion());
            request.setService(ifoit.getService());
            if (CollectionHelper.isNotNullOrEmpty(ifoit.getFeatureMemberArray())) {
                parseFeatureMember(ifoit, request);
            }
        } else {
            throw new NoApplicableCodeException()
                    .withMessage("Received XML document is not valid. Set log level to debug to get more details");
        }

        return request;
    }

    private void parseFeatureMember(InsertFeatureOfInterestType ifoit, InsertFeatureOfInterestRequest request) throws OwsExceptionReport {
        for (FeaturePropertyType fpt : ifoit.getFeatureMemberArray()) {
            final Object decodedObject = CodingHelper.decodeXmlElement(fpt);
            if (decodedObject != null && decodedObject instanceof AbstractFeature) {
                request.addFeatureMember((AbstractFeature)decodedObject);
            }
        }
    }

    public Map<SupportedTypeKey, Set<String>> getSupportedTypes() {
        return emptyMap();
    }

    public Set<String> getConformanceClasses() {
        return Sets.newHashSet(InsertFeatureOfInterestConstants.CONFORMANCE_CLASS);
    }

}
