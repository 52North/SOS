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
package org.n52.sos.soap;

import java.io.IOException;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.xml.soap.MessageFactory;
import javax.xml.soap.MimeHeaders;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPHeader;
import javax.xml.soap.SOAPMessage;

import org.n52.sos.exception.ows.NoApplicableCodeException;
import org.n52.sos.ogc.ows.ExceptionCode;
import org.n52.sos.ogc.ows.OWSConstants;
import org.n52.sos.ogc.ows.OwsExceptionReport;
import org.n52.sos.ogc.sos.SosConstants.Operations;
import org.n52.sos.ogc.sos.SosSoapConstants;
import org.n52.sos.service.SoapHeader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.NodeList;

/**
 * Utility class for SOAP requests.
 * 
 * @since 4.0.0
 */
public class SoapHelper {

    private static final Logger LOGGER = LoggerFactory.getLogger(SoapHelper.class);

    /**
     * Checks the HTTP-Header for action or SOAPAction elements.
     * 
     * @param request
     *            HTTP request
     * @return SOAP action element
     */
    public static String checkSoapHeader(HttpServletRequest request) {
        Enumeration<?> headerNames = request.getHeaderNames();
        while (headerNames.hasMoreElements()) {
            String headerNameKey = (String) headerNames.nextElement();
            if (headerNameKey.equalsIgnoreCase("type")) {
                String type = request.getHeader(headerNameKey);
                String[] typeArray = type.split(";");
                for (String string : typeArray) {
                    if (string.startsWith("action")) {
                        String soapAction = string.replace("action=", "");
                        soapAction = soapAction.replace("\"", "");
                        soapAction = soapAction.trim();
                        return soapAction;
                    }
                }
            } else if (headerNameKey.equalsIgnoreCase("SOAPAction")) {
                return request.getHeader(headerNameKey);
            }
        }
        return null;
    }

    /**
     * Get text content from element by namespace.
     * 
     * @param soapHeader
     *            SOAPHeader element
     * @param namespaceURI
     *            Namespace URI
     * @param localName
     *            local name
     * @return Text content.
     */
    public static String getContentFromElement(SOAPHeader soapHeader, String namespaceURI, String localName) {
        String elementContent = null;
        NodeList nodes = soapHeader.getElementsByTagNameNS(namespaceURI, localName);
        for (int i = 0; i < nodes.getLength(); i++) {
            elementContent = nodes.item(i).getTextContent();
        }
        return elementContent;
    }

    /**
     * Creates a SOAP message for SOAP 1.2 or 1.1
     * 
     * @param soapVersion
     *            SOAP version
     * @return Version depending SOAP message
     * @throws SOAPException
     *             if an error occurs.
     */
    public static SOAPMessage getSoapMessageForProtocol(String soapVersion) throws SOAPException {
        return MessageFactory.newInstance(soapVersion).createMessage();
    }

    public static SOAPMessage getSoapMessageForProtocol(String soapVersion, InputStream inputStream)
            throws SOAPException, IOException {
        return MessageFactory.newInstance(soapVersion).createMessage(new MimeHeaders(), inputStream);
    }

    /**
     * Get the reason for a SOAP fault from Exception code
     * 
     * @param exceptionCode
     *            OWS exception code to get reason for.
     * @return Text for SOAP fault reason
     */
    public static String getSoapFaultReasonText(ExceptionCode exceptionCode) {
        if (exceptionCode != null && exceptionCode.getSoapFaultReason() != null) {
            return exceptionCode.getSoapFaultReason();
        } else {
            return OWSConstants.SOAP_REASON_UNKNOWN;
        }
    }

    public static String checkActionURIWithBodyContent(String soapAction, String operationName)
            throws OwsExceptionReport {
        if (soapAction != null && !soapAction.isEmpty()) {
            if (operationName.equals(Operations.GetCapabilities.name())
                    && soapAction.equals(SosSoapConstants.REQ_ACTION_GET_CAPABILITIES)) {
                LOGGER.debug("ActionURI and SOAPBody content are valid!");
                return SosSoapConstants.RESP_ACTION_GET_CAPABILITIES;
            } else if (operationName.equals(Operations.DescribeSensor.name())
                    && soapAction.equals(SosSoapConstants.REQ_ACTION_DESCRIBE_SENSOR)) {
                LOGGER.debug("ActionURI and SOAPBody content are valid!");
                return SosSoapConstants.RESP_ACTION_DESCRIBE_SENSOR;
            } else if (operationName.equals(Operations.GetObservation.name())
                    && soapAction.equals(SosSoapConstants.REQ_ACTION_GET_OBSERVATION)) {
                LOGGER.debug("ActionURI and SOAPBody content are valid!");
                return SosSoapConstants.RESP_ACTION_GET_OBSERVATION;
            } else {
                throw new NoApplicableCodeException().withMessage(
                        "Error while actionURI (%s) is not compatible with the SOAPBody content (%s request)!",
                        soapAction, operationName);
            }
        }
        return null;
    }

    private SoapHelper() {
    }

    public static byte[] headerToXML(Map<String, SoapHeader> soapHeader) {
        // TODO Auto-generated method stub
        return null;
    }
}
