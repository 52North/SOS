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

import java.io.IOException;
import java.io.OutputStream;
import java.util.AbstractMap;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import javax.xml.namespace.QName;
import javax.xml.soap.SOAPConstants;

import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.XmlString;
import org.n52.sos.encode.streaming.Soap12XmlStreamWriter;
import org.n52.sos.encode.streaming.StreamingEncoder;
import org.n52.sos.exception.CodedException;
import org.n52.sos.exception.ows.NoApplicableCodeException;
import org.n52.sos.exception.ows.OwsExceptionCode;
import org.n52.sos.exception.ows.concrete.UnsupportedEncoderInputException;
import org.n52.sos.ogc.ows.OWSConstants;
import org.n52.sos.ogc.ows.OwsExceptionReport;
import org.n52.sos.ogc.sos.SosConstants;
import org.n52.sos.ogc.sos.SosConstants.HelperValues;
import org.n52.sos.service.SoapHeader;
import org.n52.sos.soap.SoapConstants;
import org.n52.sos.soap.SoapFault;
import org.n52.sos.soap.SoapHelper;
import org.n52.sos.soap.SoapResponse;
import org.n52.sos.util.CodingHelper;
import org.n52.sos.util.CollectionHelper;
import org.n52.sos.util.N52XmlHelper;
import org.n52.sos.util.OwsHelper;
import org.n52.sos.util.XmlOptionsHelper;
import org.n52.sos.w3c.SchemaLocation;
import org.n52.sos.w3c.W3CConstants;
import org.n52.sos.wsa.WsaActionHeader;
import org.n52.sos.wsa.WsaConstants;
import org.n52.sos.wsa.WsaHeader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3.x2003.x05.soapEnvelope.Body;
import org.w3.x2003.x05.soapEnvelope.Envelope;
import org.w3.x2003.x05.soapEnvelope.EnvelopeDocument;
import org.w3.x2003.x05.soapEnvelope.Fault;
import org.w3.x2003.x05.soapEnvelope.FaultDocument;
import org.w3.x2003.x05.soapEnvelope.Faultcode;
import org.w3.x2003.x05.soapEnvelope.Reasontext;
import org.w3.x2003.x05.soapEnvelope.Subcode;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

import com.google.common.base.Joiner;
import com.google.common.collect.Sets;

/**
 * Encoder implementation for SOAP 1.2
 * 
 * @since 4.0.0
 * 
 */
public class Soap12Encoder extends AbstractSoapEncoder<XmlObject, Object> implements
        StreamingEncoder<XmlObject, Object> {

    private static final Logger LOGGER = LoggerFactory.getLogger(Soap12Encoder.class);

    private static final Set<EncoderKey> ENCODER_KEY_TYPES = CodingHelper.encoderKeysForElements(
            SoapConstants.NS_SOAP_12, SoapFault.class, OwsExceptionReport.class);

    public Soap12Encoder() {
        super(SoapConstants.NS_SOAP_12);
        LOGGER.debug("Encoder for the following keys initialized successfully: {}!",
                Joiner.on(", ").join(getEncoderKeyType()));
    }
    
    @Override
    public boolean forceStreaming() {
    	return false;
    }

    @SuppressWarnings("unchecked")
    @Override
    public Set<EncoderKey> getEncoderKeyType() {
        return Collections.unmodifiableSet(CollectionHelper.union(ENCODER_KEY_TYPES, super.getEncoderKeyType()));
    }

    @Override
    public Set<SchemaLocation> getSchemaLocations() {
        // TODO return valid schemaLocation
        return Sets.newHashSet();
    }

    @Override
    public XmlObject encode(final Object element, final Map<HelperValues, String> additionalValues)
            throws OwsExceptionReport {
        if (element instanceof SoapResponse) {
            return createSOAP12Envelope((SoapResponse) element, additionalValues);
        } else if (element instanceof SoapFault) {
            return createSOAP12Fault((SoapFault) element);
        } else if (element instanceof OwsExceptionReport) {
            return createSOAP12FaultFromExceptionResponse((OwsExceptionReport) element);
        } else {
            throw new UnsupportedEncoderInputException(this, element);
        }
    }

    @Override
    public void encode(Object element, OutputStream outputStream) throws OwsExceptionReport {
        encode(element, outputStream, new EncodingValues());
    }

    @Override
    public void encode(Object element, OutputStream outputStream, EncodingValues encodingValues)
            throws OwsExceptionReport {
        if (element instanceof SoapResponse) {
            new Soap12XmlStreamWriter().write((SoapResponse) element, outputStream);
        } else {
            try {
                ((XmlObject) encode(element, encodingValues.getAdditionalValues())).save(outputStream, XmlOptionsHelper.getInstance().getXmlOptions());
            } catch (IOException ioe) {
                throw new NoApplicableCodeException().causedBy(ioe).withMessage("Error while writing element to stream!");
            }
        }
    }

    private XmlObject createSOAP12Envelope(final SoapResponse response,
            final Map<HelperValues, String> additionalValues) throws OwsExceptionReport {
        String action = null;
        final EnvelopeDocument envelopeDoc = EnvelopeDocument.Factory.newInstance();
        final Envelope envelope = envelopeDoc.addNewEnvelope();
        final Body body = envelope.addNewBody();
        if (response.getSoapFault() != null) {
            body.set(createSOAP12Fault(response.getSoapFault()));
        } else {
            if (response.getException() != null) {
                if (!response.getException().getExceptions().isEmpty()) {
                    final CodedException firstException = response.getException().getExceptions().get(0);
                    action = getExceptionActionURI(firstException.getCode());
                }
                body.set(createSOAP12FaultFromExceptionResponse(response.getException()));
                N52XmlHelper.setSchemaLocationsToDocument(
                        envelopeDoc,
                        Sets.newHashSet(N52XmlHelper.getSchemaLocationForSOAP12(),
                                N52XmlHelper.getSchemaLocationForOWS110Exception()));
            } else {
                action = response.getSoapAction();

                final XmlObject bodyContent = getBodyContent(response);
                String value = null;
                Node nodeToRemove = null;
                final NamedNodeMap attributeMap = bodyContent.getDomNode().getFirstChild().getAttributes();
                for (int i = 0; i < attributeMap.getLength(); i++) {
                    final Node node = attributeMap.item(i);
                    if (node.getLocalName().equals(W3CConstants.AN_SCHEMA_LOCATION)) {
                        value = node.getNodeValue();
                        nodeToRemove = node;
                    }
                }
                if (nodeToRemove != null) {
                    attributeMap.removeNamedItem(nodeToRemove.getNodeName());
                }
                final Set<SchemaLocation> schemaLocations = Sets.newHashSet();
                schemaLocations.add(N52XmlHelper.getSchemaLocationForSOAP12());
                if (value != null && !value.isEmpty()) {
                    String[] split = value.split(" ");
                    for (int i = 0; i < split.length; i += 2) {
                        schemaLocations.add(new SchemaLocation(split[i], split[i + 1]));
                    }
                }
                N52XmlHelper.setSchemaLocationsToDocument(envelopeDoc, schemaLocations);
                body.set(bodyContent);
            }
        }

        if (response.getHeader() != null) {
            createSOAP12Header(envelope, response.getHeader(),action);
        } else {
            envelope.addNewHeader();
        }

        // TODO for testing an validating
        // checkAndValidateSoapMessage(envelopeDoc);

        return envelopeDoc;
    }

    private void createSOAP12Header(Envelope envelope, List<SoapHeader> headers, String action) throws OwsExceptionReport {
        Node headerDomNode = envelope.addNewHeader().getDomNode();
        for (SoapHeader header : headers) {
            if (WsaConstants.NS_WSA.equals(header.getNamespace()) && header instanceof WsaActionHeader) {
                ((WsaHeader) header).setValue(action);
            }
            try {
                XmlObject xmObject = CodingHelper.encodeObjectToXml(header.getNamespace(), header);
                if (xmObject != null) {
                    Node ownerDoc = headerDomNode.getOwnerDocument().importNode(xmObject.getDomNode().getFirstChild(), true);
                    headerDomNode.insertBefore(ownerDoc, null);
                }
            } catch (OwsExceptionReport owse) {
                throw owse;
            }
        }
    }

    private XmlObject createSOAP12Fault(final SoapFault soapFault) {
        final FaultDocument faultDoc = FaultDocument.Factory.newInstance();
        final Fault fault = faultDoc.addNewFault();
        fault.addNewCode().setValue(soapFault.getFaultCode());
        final Reasontext addNewText = fault.addNewReason().addNewText();
        addNewText.setLang(soapFault.getLocale().getDisplayLanguage());
        addNewText.setStringValue(soapFault.getFaultReason());
        if (soapFault.getDetailText() != null) {
            final XmlString xmlString = XmlString.Factory.newInstance();
            xmlString.setStringValue(soapFault.getDetailText());
            fault.addNewDetail().set(xmlString);
        }
        return faultDoc;
    }

    @SuppressWarnings("unchecked")
    // see
    // http://www.angelikalanger.com/GenericsFAQ/FAQSections/ProgrammingIdioms.html#FAQ300
    // for more details
    private XmlObject createSOAP12FaultFromExceptionResponse(final OwsExceptionReport owsExceptionReport)
            throws OwsExceptionReport {
        final FaultDocument faultDoc = FaultDocument.Factory.newInstance();
        final Fault fault = faultDoc.addNewFault();
        final Faultcode code = fault.addNewCode();
        code.setValue(SOAPConstants.SOAP_SENDER_FAULT);

        // we encode only the first exception because of OGC#09-001 Section
        // 19.2.3 SOAP 1.2 Fault Binding
        if (!owsExceptionReport.getExceptions().isEmpty()) {
            final CodedException firstException = owsExceptionReport.getExceptions().get(0);
            final Subcode subcode = code.addNewSubcode();
            QName qName;
            if (firstException.getCode() != null) {
                qName = OwsHelper.getQNameForLocalName(firstException.getCode().toString());
            } else {
                qName = OwsHelper.getQNameForLocalName(OwsExceptionCode.NoApplicableCode.name());
            }
            subcode.setValue(qName);
            final Reasontext addNewText = fault.addNewReason().addNewText();
            addNewText.setLang(Locale.ENGLISH.getLanguage());
            addNewText.setStringValue(SoapHelper.getSoapFaultReasonText(firstException.getCode()));

            fault.addNewDetail().set(
                    CodingHelper.encodeObjectToXml(OWSConstants.NS_OWS, firstException, CollectionHelper
                            .map(new AbstractMap.SimpleEntry<SosConstants.HelperValues, String>(
                                    SosConstants.HelperValues.ENCODE_OWS_EXCEPTION_ONLY, ""))));
        }
        return faultDoc;
    }

    // private void checkAndValidateSoapMessage(XmlObject response) {
    // try {
    // XmlHelper.validateDocument(response);
    // } catch (OwsExceptionReport e) {
    // LOGGER.info("Error while checking SOAP response", e);
    // }
    // }
}
