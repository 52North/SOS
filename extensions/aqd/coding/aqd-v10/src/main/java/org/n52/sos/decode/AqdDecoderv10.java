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
import java.util.Set;

import javax.xml.crypto.dsig.XMLObject;

import org.apache.xmlbeans.XmlObject;

import org.n52.iceland.coding.CodingRepository;
import org.n52.iceland.coding.decode.Decoder;
import org.n52.iceland.coding.decode.DecoderKey;
import org.n52.iceland.exception.ows.OwsExceptionReport;
import org.n52.iceland.exception.ows.concrete.UnsupportedDecoderInputException;
import org.n52.iceland.service.AbstractServiceCommunicationObject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.n52.sos.aqd.AqdConstants;
import org.n52.iceland.coding.decode.XmlNamespaceDecoderKey;
import org.n52.sos.util.CodingHelper;
import org.n52.sos.util.XmlHelper;

import com.google.common.base.Joiner;

/**
 * {@link XMLObject} decoder for AQD e-Reporting requests
 * 
 * @author <a href="mailto:c.hollmann@52north.org">Carsten Hollmann</a>
 * @since 4.3.0
 *
 */
public class AqdDecoderv10 implements Decoder<AbstractServiceCommunicationObject, XmlObject> {

    private static final Logger LOGGER = LoggerFactory.getLogger(AqdDecoderv10.class);

    private static final Set<DecoderKey> DECODER_KEYS = 
            CodingHelper.xmlDecoderKeysForOperation(
                    AqdConstants.AQD, AqdConstants.VERSION, 
                    AqdConstants.Operations.GetCapabilities, 
                    AqdConstants.Operations.GetObservation,
                    AqdConstants.Operations.DescribeSensor);

    public AqdDecoderv10() {
        LOGGER.debug("Decoder for the following keys initialized successfully: {}!",
                Joiner.on(", ").join(DECODER_KEYS));
    }

    @Override
    public Set<DecoderKey> getKeys() {
        return Collections.unmodifiableSet(DECODER_KEYS);
    }

    @Override
    public AbstractServiceCommunicationObject decode(XmlObject objectToDecode)
            throws OwsExceptionReport, UnsupportedDecoderInputException {
        return (AbstractServiceCommunicationObject) CodingRepository.getInstance()
                .getDecoder(new XmlNamespaceDecoderKey(XmlHelper.getNamespace(objectToDecode), XmlObject.class))
                .decode(objectToDecode);
    }

}
