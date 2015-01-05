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

import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.xml.namespace.QName;
import javax.xml.soap.SOAPConstants;
import javax.xml.soap.SOAPEnvelope;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPMessage;

import org.n52.sos.coding.CodingRepository;
import org.n52.sos.exception.ows.NoApplicableCodeException;
import org.n52.sos.exception.ows.concrete.UnsupportedEncoderInputException;
import org.n52.sos.ogc.ows.OwsExceptionReport;
import org.n52.sos.ogc.sos.SosConstants.HelperValues;
import org.n52.sos.service.SoapHeader;
import org.n52.sos.soap.SoapHelper;
import org.n52.sos.soap.SoapResponse;
import org.n52.sos.util.CodingHelper;
import org.n52.sos.util.N52XmlHelper;
import org.n52.sos.w3c.SchemaLocation;
import org.n52.sos.w3c.W3CConstants;
import org.n52.sos.wsa.WsaActionHeader;
import org.n52.sos.wsa.WsaConstants;
import org.n52.sos.wsa.WsaHeader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Joiner;
import com.google.common.collect.Sets;

/**
 * @author Christian Autermann <c.autermann@52north.org>
 * @since 4.0.0
 */
public class Soap11Encoder extends AbstractSoapEncoder<SOAPMessage, SoapResponse> {

    private static final Logger LOGGER = LoggerFactory.getLogger(Soap11Encoder.class);

    public Soap11Encoder() {
        super(SOAPConstants.URI_NS_SOAP_1_1_ENVELOPE);
        LOGGER.debug("Encoder for the following keys initialized successfully: {}!",
                Joiner.on(", ").join(getEncoderKeyType()));
    }

    @Override
    public Set<SchemaLocation> getSchemaLocations() {
        // TODO return valid schemaLocation
        return Sets.newHashSet();
    }

    @Override
    public SOAPMessage encode(SoapResponse soapResponse, Map<HelperValues, String> additionalValues)
            throws OwsExceptionReport {
        if (soapResponse == null) {
            throw new UnsupportedEncoderInputException(this, soapResponse);
        }
        String soapVersion = soapResponse.getSoapVersion();
        SOAPMessage soapResponseMessage;
        String action = null;
        try {
            soapResponseMessage = SoapHelper.getSoapMessageForProtocol(soapVersion);
            if (soapResponse.getSoapFault() != null) {
                createSOAPFault(soapResponseMessage.getSOAPBody().addFault(), soapResponse.getSoapFault());
            } else {
                if (soapResponse.getException() != null) {
                    action =
                            createSOAPFaultFromExceptionResponse(soapResponseMessage.getSOAPBody().addFault(),
                                    soapResponse.getException());
                    addSchemaLocationForExceptionToSOAPMessage(soapResponseMessage);
                } else {
                    action =
                            createSOAPBody(soapResponseMessage, soapResponse,
                                    soapResponse.getSoapAction());
                }
            }
            if (soapResponse.getHeader() != null) {
                List<SoapHeader> headers = soapResponse.getHeader();
                for (SoapHeader header : headers) {
                    if (WsaConstants.NS_WSA.equals(header.getNamespace()) && header instanceof WsaActionHeader) {
                        ((WsaHeader) header).setValue(action);
                    }
                    try {
                        Encoder<Map<QName, String>, SoapHeader> encoder =
                                CodingRepository.getInstance().getEncoder(
                                        CodingHelper.getEncoderKey(header.getNamespace(), header));
                        if (encoder != null) {
                            Map<QName, String> headerElements = encoder.encode(header);
                            for (QName qName : headerElements.keySet()) {
                                soapResponseMessage.getSOAPHeader().addChildElement(qName)
                                        .setTextContent(headerElements.get(qName));
                            }
                        }
                    } catch (OwsExceptionReport owse) {
                        throw owse;
                    }
                }

            } else {
                soapResponseMessage.getSOAPHeader().detachNode();
            }
            soapResponseMessage.setProperty(SOAPMessage.WRITE_XML_DECLARATION, String.valueOf(true));
            return soapResponseMessage;
        } catch (SOAPException soape) {
            throw new NoApplicableCodeException().causedBy(soape).withMessage("Error while encoding SOAPMessage!");
        }
    }

    private void addSchemaLocationForExceptionToSOAPMessage(SOAPMessage soapResponseMessage) throws SOAPException {
        SOAPEnvelope envelope = soapResponseMessage.getSOAPPart().getEnvelope();
        envelope.addNamespaceDeclaration(W3CConstants.NS_XSI_PREFIX, W3CConstants.NS_XSI);
        StringBuilder schemaLocation = new StringBuilder();
        schemaLocation.append(envelope.getNamespaceURI());
        schemaLocation.append(" ");
        schemaLocation.append(envelope.getNamespaceURI());
        schemaLocation.append(" ");
        schemaLocation.append(N52XmlHelper.getSchemaLocationForOWS110Exception());
        envelope.addAttribute(N52XmlHelper.getSchemaLocationQNameWithPrefix(), schemaLocation.toString());
    }
}
