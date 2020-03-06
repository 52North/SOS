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

import com.google.common.base.Joiner;
import com.google.common.collect.Sets;
import java.util.Collections;
import java.util.Map;
import java.util.Set;
import net.opengis.drt.x10.DeleteResultTemplateDocument;
import net.opengis.drt.x10.DeleteResultTemplateType;
import net.opengis.drt.x10.DeleteResultTemplateType.Tuple;
import org.apache.xmlbeans.XmlObject;
import org.n52.shetland.ogc.sos.drt.DeleteResultTemplateConstants;
import org.n52.sos.decode.Decoder;
import org.n52.sos.decode.DecoderKey;
import org.n52.sos.exception.ows.concrete.UnsupportedDecoderInputException;
import org.n52.sos.ogc.ows.OwsExceptionReport;
import org.n52.sos.ogc.sos.Sos2Constants;
import org.n52.sos.ogc.sos.SosConstants;
import org.n52.sos.request.DeleteResultTemplateRequest;
import org.n52.sos.service.ServiceConstants.SupportedTypeKey;
import org.n52.sos.util.CodingHelper;
import org.n52.sos.util.CollectionHelper;
import org.n52.sos.util.XmlHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author <a href="mailto:e.h.juerrens@52north.org">Eike Hinderk J&uuml;rrens</a>
 * @since 4.4.0
 */
public class DeleteResultTemplateDecoder implements Decoder<DeleteResultTemplateRequest, XmlObject> {

    private static final Set<DecoderKey> DECODER_KEYS =
            CollectionHelper.union(
                    CodingHelper.decoderKeysForElements(
                            DeleteResultTemplateConstants.NS,
                            DeleteResultTemplateDocument.class),
                    CodingHelper.xmlDecoderKeysForOperation(
                            SosConstants.SOS,
                            Sos2Constants.SERVICEVERSION,
                            DeleteResultTemplateConstants.OPERATION_NAME
                    )
            );

    private static final Logger LOGGER = 
            LoggerFactory.getLogger(DeleteResultTemplateDecoder.class);

    public DeleteResultTemplateDecoder() {
        LOGGER.info("Decoder for the following keys initialized successfully: {}!",
                Joiner.on(", ").join(DECODER_KEYS));
    }

    @Override
    public DeleteResultTemplateRequest decode(XmlObject xmlObject) throws OwsExceptionReport {
        LOGGER.debug(String.format("REQUESTTYPE: %s", xmlObject != null ? xmlObject.getClass() : "null recevied"));
        XmlHelper.validateDocument(xmlObject);
        if (xmlObject instanceof DeleteResultTemplateDocument) {
            DeleteResultTemplateDocument drtd = (DeleteResultTemplateDocument) xmlObject;
            DeleteResultTemplateRequest decodedRequest = parseDeleteResultTemplate(drtd);
            LOGGER.debug(String.format("Decoded request: %s", decodedRequest));
            return decodedRequest;
        } else {
            throw new UnsupportedDecoderInputException(this, xmlObject);
        }
    }
    
    private DeleteResultTemplateRequest parseDeleteResultTemplate(DeleteResultTemplateDocument drtd)
            throws OwsExceptionReport {
        DeleteResultTemplateRequest request = null;

        DeleteResultTemplateType drtt = drtd.getDeleteResultTemplate();

        request = new DeleteResultTemplateRequest();
        request.setVersion(drtt.getVersion());
        request.setService(drtt.getService());
        if (drtt.sizeOfResultTemplateArray() > 0) {
            parseResultTemplates(drtt.getResultTemplateArray(), request);
        }
        if (drtt.sizeOfTupleArray() > 0) {
            parseObservedPropertyOfferingPairs(drtt.getTupleArray(), request);
        }

        return request;
    }

    private void parseResultTemplates(String[] resultTemplateArray,
            DeleteResultTemplateRequest request) {
        for (String resultTemplateId : resultTemplateArray) {
            request.addResultTemplate(resultTemplateId);
        }
    }

    @Override
    public Map<SupportedTypeKey, Set<String>> getSupportedTypes() {
        return Collections.emptyMap();
    }

    @Override
    public Set<String> getConformanceClasses() {
        return Sets.newHashSet(
                DeleteResultTemplateConstants.CONFORMANCE_CLASS_INSERTION,
                DeleteResultTemplateConstants.CONFORMANCE_CLASS_RETRIEVAL);
    }

        @Override
    public Set<DecoderKey> getDecoderKeyTypes() {
        return Collections.unmodifiableSet(DECODER_KEYS);
    }

    private void parseObservedPropertyOfferingPairs(
            Tuple[] tuples, DeleteResultTemplateRequest request) {
        for (Tuple tuple : tuples) {
            request.addObservedPropertyOfferingPair(
                    tuple.getObservedProperty(),
                    tuple.getOffering());
        }
    }
    
}
