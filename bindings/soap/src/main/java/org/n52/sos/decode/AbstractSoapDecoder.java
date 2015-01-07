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

import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPHeader;
import javax.xml.soap.SOAPHeaderElement;
import javax.xml.soap.SOAPMessage;

import org.apache.xmlbeans.XmlException;
import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.XmlOptions;
import org.n52.sos.coding.CodingRepository;
import org.n52.sos.exception.swes.InvalidRequestException;
import org.n52.sos.ogc.ows.OwsExceptionReport;
import org.n52.sos.service.ServiceConstants.SupportedTypeKey;
import org.n52.sos.service.SoapHeader;
import org.n52.sos.soap.SoapRequest;
import org.n52.sos.util.CollectionHelper;
import org.n52.sos.util.LinkedListMultiMap;
import org.n52.sos.util.ListMultiMap;
import org.n52.sos.util.W3cHelper;
import org.n52.sos.util.XmlOptionsHelper;
import org.n52.sos.wsa.WsaActionHeader;
import org.n52.sos.wsa.WsaConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;

import com.google.common.collect.Lists;

/**
 * @author Christian Autermann <c.autermann@52north.org>
 * @since 4.0.0
 */
public abstract class AbstractSoapDecoder implements Decoder<SoapRequest, XmlObject> {
    private static final Logger LOGGER = LoggerFactory.getLogger(AbstractSoapDecoder.class);

    private final Set<DecoderKey> decoderKeys;

    public AbstractSoapDecoder(String namespace) {
        this.decoderKeys = Collections.<DecoderKey> singleton(new XmlNamespaceDecoderKey(namespace, XmlObject.class));
    }

    @Override
    public Set<DecoderKey> getDecoderKeyTypes() {
        return Collections.unmodifiableSet(decoderKeys);
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
    public SoapRequest decode(XmlObject xmlObject) throws OwsExceptionReport {
        try {
            return createEnvelope(xmlObject);
        } catch (OwsExceptionReport owse) {
            return createFault(owse);
        }
    }

    protected abstract SoapRequest createEnvelope(XmlObject xml) throws OwsExceptionReport;

    protected abstract SoapRequest createFault(OwsExceptionReport xml);

    /**
     * Parses the SOAPBody content to a text representation
     * 
     * @param message
     *            SOAP message
     * 
     * @return SOAPBody content as text
     * 
     * 
     * @throws OwsExceptionReport
     *             * if an error occurs.
     */
    protected XmlObject getSOAPBodyContent(SOAPMessage message) throws OwsExceptionReport {
        try {
            Document bodyRequestDoc = message.getSOAPBody().extractContentAsDocument();
            XmlOptions options = XmlOptionsHelper.getInstance().getXmlOptions();
            String xmlString = W3cHelper.nodeToXmlString(bodyRequestDoc.getDocumentElement());
            return XmlObject.Factory.parse(xmlString, options);
        } catch (SOAPException soape) {
            throw new InvalidRequestException().causedBy(soape).withMessage(
                    "Error while parsing SOAPMessage body content!");
        } catch (XmlException xmle) {
            throw new InvalidRequestException().causedBy(xmle).withMessage(
                    "Error while parsing SOAPMessage body content!");
        }
    }

    protected List<SoapHeader> getSoapHeader(SOAPHeader soapHeader) {
        ListMultiMap<String, SOAPHeaderElement> headersByNamespace =
                new LinkedListMultiMap<String, SOAPHeaderElement>();
        Iterator<?> headerElements = soapHeader.extractAllHeaderElements();
        while (headerElements.hasNext()) {
            SOAPHeaderElement element = (SOAPHeaderElement) headerElements.next();
            headersByNamespace.add(element.getNamespaceURI(), element);
        }
        List<SoapHeader> soapHeaders = Lists.newArrayList();
        for (String namespace : headersByNamespace.keySet()) {
            try {
                Decoder<?, List<SOAPHeaderElement>> decoder =
                        CodingRepository.getInstance().getDecoder(
                                new XmlNamespaceDecoderKey(namespace, SOAPHeaderElement.class));
                if (decoder != null) {
                    Object object = decoder.decode(headersByNamespace.get(namespace));
                    if (object instanceof SoapHeader) {
                        soapHeaders.add((SoapHeader) object);
                    } else if (object instanceof List<?>) {
                        for (Object o : (List<?>)object) {
                            if (o instanceof SoapHeader) {
                                soapHeaders.add((SoapHeader) o);
                            }
                        }
                    }
                } else {
                    LOGGER.info("The SOAP-Header elements for namespace '{}' are not supported by this server!",
                            namespace);
                }
            } catch (OwsExceptionReport owse) {
                LOGGER.debug("Requested SOAPHeader element is not supported", owse);
            }
        }
        return soapHeaders;
    }

    protected String checkSoapAction(String soapAction, List<SoapHeader> soapHeaders) {
        if (soapAction != null && !soapAction.isEmpty()) {
            return soapAction;
        } else if (CollectionHelper.isEmpty(soapHeaders)) {
            for (SoapHeader soapHeader : soapHeaders) {
                if (WsaConstants.NS_WSA.equals(soapHeader.getNamespace()) && soapHeader instanceof WsaActionHeader) {
                    return ((WsaActionHeader)soapHeader).getValue();
                }
            }
        }
        return null;
    }
}
