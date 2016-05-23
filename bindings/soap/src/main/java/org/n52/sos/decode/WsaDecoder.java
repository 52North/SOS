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
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.xml.soap.Node;
import javax.xml.soap.SOAPHeaderElement;

import org.n52.sos.service.ServiceConstants.SupportedTypeKey;
import org.n52.sos.wsa.WsaActionHeader;
import org.n52.sos.wsa.WsaConstants;
import org.n52.sos.wsa.WsaHeader;
import org.n52.sos.wsa.WsaMessageIDHeader;
import org.n52.sos.wsa.WsaReplyToHeader;
import org.n52.sos.wsa.WsaToHeader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Joiner;
import com.google.common.collect.Lists;

/**
 * @since 4.0.0
 * 
 */
public class WsaDecoder implements Decoder<List<WsaHeader>, List<SOAPHeaderElement>> {

    private static final Logger LOGGER = LoggerFactory.getLogger(WsaDecoder.class);

    private static final Set<DecoderKey> DECODER_KEYS = Collections.<DecoderKey> singleton(new XmlNamespaceDecoderKey(
            WsaConstants.NS_WSA, SOAPHeaderElement.class));

    public WsaDecoder() {
        LOGGER.debug("Decoder for the following keys initialized successfully: {}!", Joiner.on(", ")
                .join(DECODER_KEYS));
    }

    @Override
    public Set<DecoderKey> getDecoderKeyTypes() {
        return Collections.unmodifiableSet(DECODER_KEYS);
    }

    @Override
    public Map<SupportedTypeKey, Set<String>> getSupportedTypes() {
        return Collections.emptyMap();
    }

    @Override
    public Set<String> getConformanceClasses() {
        return Collections.emptySet();
    }

    @Override
    public List<WsaHeader> decode(List<SOAPHeaderElement> list) {
        List<WsaHeader> wsaHeaders = Lists.newArrayListWithCapacity(list.size());
        boolean to = false;
        boolean replyTo = false;
        boolean messageId = false;
        boolean action = false;
        for (SOAPHeaderElement soapHeaderElement : list) {
            if (soapHeaderElement.getLocalName().equals(WsaConstants.EN_TO)) {
                wsaHeaders.add(new WsaToHeader(soapHeaderElement.getValue()));
                to = true;
            } else if (soapHeaderElement.getLocalName().equals(WsaConstants.EN_ACTION)) {
                wsaHeaders.add(new WsaActionHeader(soapHeaderElement.getValue()));
                action = true;
            } else if (soapHeaderElement.getLocalName().equals(WsaConstants.EN_REPLY_TO)) {
                Iterator<?> iter = soapHeaderElement.getChildElements();
                while (iter.hasNext()) {
                    Node node = (Node) iter.next();
                    if (node.getLocalName() != null && node.getLocalName().equals(WsaConstants.EN_ADDRESS)) {
                        wsaHeaders.add(new WsaReplyToHeader(node.getValue()));
                        replyTo = true;
                    }
                }
            } else if (soapHeaderElement.getLocalName().equals(WsaConstants.EN_MESSAGE_ID)) {
                wsaHeaders.add(new WsaMessageIDHeader(soapHeaderElement.getValue()));
                messageId = true;
            }
        }
        if ((to || replyTo || messageId) && !action) {
            wsaHeaders.add(new WsaActionHeader(WsaConstants.WSA_FAULT_ACTION));
        }
        return wsaHeaders;
    }
}
