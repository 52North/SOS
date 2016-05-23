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
package org.n52.sos.encode;

import java.util.Collections;
import java.util.Map;
import java.util.Set;

import org.apache.xmlbeans.XmlObject;
import org.n52.sos.exception.ows.concrete.UnsupportedEncoderInputException;
import org.n52.sos.ogc.ows.OwsExceptionReport;
import org.n52.sos.ogc.sos.SosConstants.HelperValues;
import org.n52.sos.service.ServiceConstants.SupportedTypeKey;
import org.n52.sos.util.CodingHelper;
import org.n52.sos.util.XmlOptionsHelper;
import org.n52.sos.util.http.MediaType;
import org.n52.sos.util.http.MediaTypes;
import org.n52.sos.w3c.SchemaLocation;
import org.n52.sos.wsa.WsaActionHeader;
import org.n52.sos.wsa.WsaConstants;
import org.n52.sos.wsa.WsaHeader;
import org.n52.sos.wsa.WsaMessageIDHeader;
import org.n52.sos.wsa.WsaRelatesToHeader;
import org.n52.sos.wsa.WsaReplyToHeader;
import org.n52.sos.wsa.WsaToHeader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3.x2005.x08.addressing.ActionDocument;
import org.w3.x2005.x08.addressing.MessageIDDocument;
import org.w3.x2005.x08.addressing.RelatesToDocument;
import org.w3.x2005.x08.addressing.ReplyToDocument;
import org.w3.x2005.x08.addressing.ToDocument;

import com.google.common.base.Joiner;
import com.google.common.collect.Sets;

/**
 * @since 4.0.0
 * 
 */
public class WsaEncoder implements Encoder<XmlObject, WsaHeader> {

    private static final Logger LOGGER = LoggerFactory.getLogger(WsaEncoder.class);

    private static final Set<EncoderKey> ENCODER_KEYS = CodingHelper.encoderKeysForElements(WsaConstants.NS_WSA,
            WsaHeader.class);

    public WsaEncoder() {
        LOGGER.debug("Encoder for the following keys initialized successfully: {}!", Joiner.on(", ")
                .join(ENCODER_KEYS));
    }

    @Override
    public Set<EncoderKey> getEncoderKeyType() {
        return Collections.unmodifiableSet(ENCODER_KEYS);
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
    public void addNamespacePrefixToMap(Map<String, String> nameSpacePrefixMap) {
    }

    @Override
    public MediaType getContentType() {
        return MediaTypes.TEXT_XML;
    }

    @Override
    public Set<SchemaLocation> getSchemaLocations() {
        // TODO return valid schemaLocation
        return Sets.newHashSet();
    }

    @Override
    public XmlObject encode(WsaHeader wsaHeader) throws OwsExceptionReport {
        return encode(wsaHeader, null);
    }

    @Override
    public XmlObject encode(WsaHeader wsaHeader, Map<HelperValues, String> additionalValues) throws OwsExceptionReport {
        if (wsaHeader == null) {
            throw new UnsupportedEncoderInputException(this, wsaHeader);
        }
        if (!wsaHeader.isSetValue()) {
            return null;
        }
        if (wsaHeader instanceof WsaReplyToHeader) {
            return encodeReplyToHeader((WsaReplyToHeader) wsaHeader);
        } else if (wsaHeader instanceof WsaMessageIDHeader) {
            return encodeMessageIDHeader((WsaMessageIDHeader) wsaHeader);
        } else if (wsaHeader instanceof WsaActionHeader) {
            return encodeActionHeader((WsaActionHeader) wsaHeader);
        } else if (wsaHeader instanceof WsaToHeader) {
            return encodeToHeader((WsaToHeader) wsaHeader);
        } else if (wsaHeader instanceof WsaRelatesToHeader) {
            return encodeRelatesToHeader((WsaRelatesToHeader) wsaHeader);
        } else {
            throw new UnsupportedEncoderInputException(this, wsaHeader);
        }
    }

    private XmlObject encodeReplyToHeader(WsaReplyToHeader wsaHeader) {
        ReplyToDocument replyToDoc =
                ReplyToDocument.Factory.newInstance(XmlOptionsHelper.getInstance().getXmlOptions());
        replyToDoc.addNewReplyTo().addNewAddress().setStringValue(wsaHeader.getValue());
        return replyToDoc;
    }

    private XmlObject encodeRelatesToHeader(WsaRelatesToHeader wsaHeader) {
        RelatesToDocument relatesToDoc =
                RelatesToDocument.Factory.newInstance(XmlOptionsHelper.getInstance().getXmlOptions());
        relatesToDoc.addNewRelatesTo().setStringValue(wsaHeader.getValue());
        return relatesToDoc;
    }

    private XmlObject encodeMessageIDHeader(WsaMessageIDHeader wsaHeader) {
        MessageIDDocument messageIDDoc =
                MessageIDDocument.Factory.newInstance(XmlOptionsHelper.getInstance().getXmlOptions());
        messageIDDoc.addNewMessageID().setStringValue(wsaHeader.getValue());
        return null;
    }

    private XmlObject encodeActionHeader(WsaActionHeader wsaHeader) {
        ActionDocument actionDoc = ActionDocument.Factory.newInstance(XmlOptionsHelper.getInstance().getXmlOptions());
        actionDoc.addNewAction().setStringValue(wsaHeader.getValue());
        return actionDoc;
    }

    private XmlObject encodeToHeader(WsaToHeader wsaHeader) {
        ToDocument toDoc = ToDocument.Factory.newInstance(XmlOptionsHelper.getInstance().getXmlOptions());
        toDoc.addNewTo().setStringValue(wsaHeader.getValue());
        return toDoc;
    }

}
