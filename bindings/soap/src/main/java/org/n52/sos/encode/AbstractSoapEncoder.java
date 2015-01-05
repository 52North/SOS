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
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import javax.xml.namespace.QName;
import javax.xml.soap.Detail;
import javax.xml.soap.SOAPConstants;
import javax.xml.soap.SOAPElement;
import javax.xml.soap.SOAPEnvelope;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPFault;
import javax.xml.soap.SOAPMessage;

import org.apache.xmlbeans.XmlObject;
import org.n52.sos.coding.CodingRepository;
import org.n52.sos.exception.CodedException;
import org.n52.sos.exception.ows.OwsExceptionCode;
import org.n52.sos.exception.ows.concrete.NoEncoderForKeyException;
import org.n52.sos.exception.sos.SosExceptionCode;
import org.n52.sos.exception.swes.SwesExceptionCode;
import org.n52.sos.ogc.ows.ExceptionCode;
import org.n52.sos.ogc.ows.OWSConstants;
import org.n52.sos.ogc.ows.OwsExceptionReport;
import org.n52.sos.ogc.sos.SosSoapConstants;
import org.n52.sos.response.AbstractServiceResponse;
import org.n52.sos.service.ServiceConstants.SupportedTypeKey;
import org.n52.sos.soap.SoapFault;
import org.n52.sos.soap.SoapHelper;
import org.n52.sos.soap.SoapResponse;
import org.n52.sos.util.Constants;
import org.n52.sos.util.N52XmlHelper;
import org.n52.sos.util.http.MediaType;
import org.n52.sos.util.http.MediaTypes;
import org.n52.sos.w3c.W3CConstants;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

import com.google.common.collect.ImmutableSet;

/**
 * @author Christian Autermann <c.autermann@52north.org>
 * @since 4.0.0
 */
public abstract class AbstractSoapEncoder<T, S> implements Encoder<T, S>, Constants {
    public static final String DEFAULT_FAULT_REASON = "A server exception was encountered.";

    public static final String MISSING_RESPONSE_DETAIL_TEXT = "Missing SOS response document!";

    public static final String MISSING_EXCEPTION_DETAIL_TEXT =
            "Error while creating SOAPFault element from OWSException! OWSException is missing!";

    private final Set<EncoderKey> encoderKey;

    public AbstractSoapEncoder(String namespace) {
        this.encoderKey = ImmutableSet.<EncoderKey> of(new XmlEncoderKey(namespace, SoapResponse.class));
    }

    @Override
    public Set<EncoderKey> getEncoderKeyType() {
        return Collections.unmodifiableSet(encoderKey);
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
        return MediaTypes.APPLICATION_SOAP_XML;
    }

    @Override
    public T encode(S response) throws OwsExceptionReport {
        return encode(response, null);
    }

    /**
     * Creates a SOAPBody element from SOS response
     * 
     * @param soapResponseMessage
     *            SOAPBody element
     * @param sosResponse
     *            SOS response
     * @param actionURI
     *            the action URI
     * 
     * @return the action URI
     * 
     * @throws SOAPException
     *             if an error occurs.
     */
    protected String createSOAPBody(SOAPMessage soapResponseMessage, XmlObject sosResponse, String actionURI)
            throws SOAPException {

        if (sosResponse != null) {
            addAndRemoveSchemaLocationForSOAP(sosResponse, soapResponseMessage);
            soapResponseMessage.getSOAPBody().addDocument((Document) sosResponse.getDomNode());
            return actionURI;
        } else {
            SoapFault fault = new SoapFault();
            fault.setFaultCode(SOAPConstants.SOAP_RECEIVER_FAULT);
            fault.setFaultSubcode(new QName(OWSConstants.NS_OWS, OwsExceptionCode.NoApplicableCode.name(),
                    OWSConstants.NS_OWS_PREFIX));
            fault.setFaultReason(DEFAULT_FAULT_REASON);
            fault.setLocale(Locale.ENGLISH);
            fault.setDetailText(MISSING_RESPONSE_DETAIL_TEXT);
            createSOAPFault(soapResponseMessage.getSOAPBody().addFault(), fault);
        }
        return null;
    }

    /**
     * Create and add the SOAPBody content
     * 
     * @param soapResponseMessage
     *            SOAPMessage to add the body
     * @param soapResponse
     *            SOS internal SOAP response
     * @param actionURI
     *            The ation URI
     * @return action URI
     * @throws SOAPException
     *             If an error occurs when add content to {@link SOAPMessage}
     * @throws OwsExceptionReport
     *             If an error occurs while encoding the body content
     */
    protected String createSOAPBody(SOAPMessage soapResponseMessage, SoapResponse soapResponse, String actionURI)
            throws SOAPException, OwsExceptionReport {
        return createSOAPBody(soapResponseMessage, getBodyContent(soapResponse), actionURI);
    }

    /**
     * Get the content for the SOAPBody as {@link XmlObject}
     * 
     * @param response
     *            SOAP response
     * @return SOAPBody content as {@link XmlObject}
     * @throws OwsExceptionReport
     *             If no encoder is available, the object to encode is not
     *             supported or an error occurs during the encoding
     */
    protected XmlObject getBodyContent(SoapResponse response) throws OwsExceptionReport {
        if (response.isSetXmlBodyContent()) {
            return response.getSoapBodyContent();
        }
        OperationEncoderKey key =
                new OperationEncoderKey(response.getBodyContent().getOperationKey(), MediaTypes.APPLICATION_XML);
        Encoder<Object, AbstractServiceResponse> encoder = CodingRepository.getInstance().getEncoder(key);
        if (encoder == null) {
            throw new NoEncoderForKeyException(key);
        }
        return (XmlObject) encoder.encode(response.getBodyContent());
    }

    /**
     * Check SOS response for xsi:schemaLocation, remove attribute and add
     * attribute to SOAP message
     * 
     * @param xmlObject
     * @param soapResponseMessage
     *            SOAP response message
     * 
     * @throws SOAPException
     *             If an error occurs
     */
    private void addAndRemoveSchemaLocationForSOAP(XmlObject xmlObject, SOAPMessage soapResponseMessage)
            throws SOAPException {
        String value = null;
        Node nodeToRemove = null;
        NamedNodeMap attributeMap = xmlObject.getDomNode().getFirstChild().getAttributes();
        for (int i = 0; i < attributeMap.getLength(); i++) {
            Node node = attributeMap.item(i);
            if (node.getLocalName().equals(W3CConstants.AN_SCHEMA_LOCATION)) {
                value = node.getNodeValue();
                nodeToRemove = node;
            }
        }
        if (nodeToRemove != null) {
            attributeMap.removeNamedItem(nodeToRemove.getNodeName());
        }
        SOAPEnvelope envelope = soapResponseMessage.getSOAPPart().getEnvelope();
        StringBuilder string = new StringBuilder();
        string.append(envelope.getNamespaceURI());
        string.append(BLANK_CHAR);
        string.append(envelope.getNamespaceURI());
        if (value != null && !value.isEmpty()) {
            string.append(BLANK_CHAR);
            string.append(value);
        }
        envelope.addAttribute(N52XmlHelper.getSchemaLocationQNameWithPrefix(), string.toString());
    }

    /**
     * Creates a SOAPFault element from SOS internal fault
     * 
     * @param fault
     *            SOAPFault element
     * @param soapFault
     *            SOS internal fault
     * 
     * @throws SOAPException
     *             if an error occurs.
     */
    protected void createSOAPFault(SOAPFault fault, SoapFault soapFault) throws SOAPException {
        fault.setFaultCode(soapFault.getFaultCode());
        fault.setFaultString(soapFault.getFaultReason(), soapFault.getLocale());
        if (soapFault.getDetailText() != null) {
            fault.addDetail().setTextContent(soapFault.getDetailText());
        }
    }

    /**
     * Creates a SOAPFault element from SOS exception
     * 
     * @param soapFault
     *            SOAPFault element
     * @param owsExceptionReport
     *            SOS exception
     * 
     * @return SOAP action URI.
     * 
     * @throws SOAPException
     *             if an error occurs.
     */
    protected String createSOAPFaultFromExceptionResponse(SOAPFault soapFault, OwsExceptionReport owsExceptionReport)
            throws SOAPException {
        // FIXME: check and fix support for ExceptionReport with multiple
        // exceptions!
        if (!owsExceptionReport.getExceptions().isEmpty()) {
            CodedException firstException = owsExceptionReport.getExceptions().iterator().next();
            if (soapFault.getNamespaceURI().equalsIgnoreCase(SOAPConstants.URI_NS_SOAP_1_1_ENVELOPE)) {
                QName qname = new QName(soapFault.getNamespaceURI(), "Client", soapFault.getPrefix());
                soapFault.setFaultCode(qname);
            } else {
                soapFault.setFaultCode(SOAPConstants.SOAP_SENDER_FAULT);
                if (firstException.getCode() != null) {
                    soapFault.appendFaultSubcode(new QName(OWSConstants.NS_OWS, firstException.getCode().toString(),
                            OWSConstants.NS_OWS_PREFIX));
                } else {
                    soapFault.appendFaultSubcode(OWSConstants.QN_NO_APPLICABLE_CODE);
                }
            }
            soapFault.addFaultReasonText(SoapHelper.getSoapFaultReasonText(firstException.getCode()), Locale.ENGLISH);
            Detail detail = soapFault.addDetail();
            for (CodedException exception : owsExceptionReport.getExceptions()) {
                createSOAPFaultDetail(detail, exception);
            }
            return getExceptionActionURI(firstException.getCode());
        } else {
            SoapFault fault = new SoapFault();
            fault.setFaultCode(SOAPConstants.SOAP_RECEIVER_FAULT);
            fault.setFaultSubcode(OWSConstants.QN_NO_APPLICABLE_CODE);
            fault.setFaultReason(DEFAULT_FAULT_REASON);
            fault.setLocale(Locale.ENGLISH);
            fault.setDetailText(MISSING_EXCEPTION_DETAIL_TEXT);
            createSOAPFault(soapFault, fault);
            return SosSoapConstants.RESP_ACTION_SOS;
        }
    }

    /**
     * Get SOAP action URI depending on Exception code
     * 
     * @param exceptionCode
     *            Exception code
     * 
     * @return SOAP action URI
     */
    protected String getExceptionActionURI(ExceptionCode exceptionCode) {
        if (exceptionCode instanceof OwsExceptionCode) {
            return SosSoapConstants.RESP_ACTION_OWS;
        } else if (exceptionCode instanceof SwesExceptionCode) {
            return SosSoapConstants.RESP_ACTION_SWES;
        } else if (exceptionCode instanceof SosExceptionCode) {
            return SosSoapConstants.RESP_ACTION_SOS;
        } else {
            return SosSoapConstants.RESP_ACTION_OWS;
        }
    }

    /**
     * Creates a SOAPDetail element from SOS exception document.
     * 
     * @param detail
     *            SOAPDetail
     * @param exception
     *            SOS Exception document
     * 
     * @throws SOAPException
     *             if an error occurs.
     */
    private void createSOAPFaultDetail(Detail detail, CodedException exception) throws SOAPException {
        SOAPElement exRep = detail.addChildElement(OWSConstants.QN_EXCEPTION);
        exRep.addNamespaceDeclaration(OWSConstants.NS_OWS_PREFIX, OWSConstants.NS_OWS);
        String code = exception.getCode().toString();
        String locator = exception.getLocator();
        StringBuilder exceptionText = new StringBuilder();
        exceptionText.append(exception.getMessage());
        exceptionText.append(LINE_SEPARATOR_CHAR);
        if (exception.getCause() != null) {
            exceptionText.append(LINE_SEPARATOR_CHAR).append("[EXCEPTION]: ").append(LINE_SEPARATOR_CHAR);
            if (exception.getCause().getLocalizedMessage() != null
                    && !exception.getCause().getLocalizedMessage().isEmpty()) {
                exceptionText.append(exception.getCause().getLocalizedMessage());
                exceptionText.append(LINE_SEPARATOR_CHAR);
            }
            if (exception.getCause().getMessage() != null && !exception.getCause().getMessage().isEmpty()) {
                exceptionText.append(exception.getCause().getMessage());
                exceptionText.append(LINE_SEPARATOR_CHAR);
            }
        }
        exRep.addAttribute(new QName(OWSConstants.EN_EXCEPTION_CODE), code);
        if (locator != null && !locator.isEmpty()) {
            exRep.addAttribute(new QName(OWSConstants.EN_LOCATOR), locator);
        }
        if (exceptionText.length() != 0) {
            SOAPElement execText = exRep.addChildElement(OWSConstants.QN_EXCEPTION_TEXT);
            execText.setTextContent(exceptionText.toString());
        }
    }
}
