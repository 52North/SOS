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
package org.n52.sos.encode.streaming;

import java.io.OutputStream;
import java.util.Set;

import javax.xml.stream.XMLStreamException;

import org.apache.xmlbeans.XmlObject;
import org.n52.sos.coding.CodingRepository;
import org.n52.sos.encode.Encoder;
import org.n52.sos.encode.EncoderKey;
import org.n52.sos.encode.EncodingValues;
import org.n52.sos.encode.OperationEncoderKey;
import org.n52.sos.encode.XmlStreamWriter;
import org.n52.sos.exception.ows.NoApplicableCodeException;
import org.n52.sos.exception.ows.concrete.NoEncoderForKeyException;
import org.n52.sos.ogc.ows.OwsExceptionReport;
import org.n52.sos.response.AbstractServiceResponse;
import org.n52.sos.soap.SoapConstants;
import org.n52.sos.soap.SoapFault;
import org.n52.sos.soap.SoapResponse;
import org.n52.sos.util.CodingHelper;
import org.n52.sos.util.Constants;
import org.n52.sos.util.XmlOptionsHelper;
import org.n52.sos.util.http.MediaTypes;
import org.n52.sos.w3c.SchemaLocation;
import org.n52.sos.w3c.W3CConstants;

import com.google.common.collect.Sets;

/**
 * {@link XmlStreamWriter} implementation for SOAP 1.2
 * 
 * @author Carsten Hollmann <c.hollmann@52north.org>
 * @since 4.1.0
 *
 */
public class Soap12XmlStreamWriter extends XmlStreamWriter<SoapResponse> {

    private SoapResponse response;

    /**
     * constructor
     */
    public Soap12XmlStreamWriter() {

    }

    /**
     * constructor
     * 
     * @param response
     *            Service internal SOAP response to encode
     */
    public Soap12XmlStreamWriter(SoapResponse response) {
        setResponse(response);
    }

    @Override
    public void write(OutputStream out) throws OwsExceptionReport {
        write(getResponse(), out);
    }

    @Override
    public void write(OutputStream out, EncodingValues encodingValues) throws OwsExceptionReport {
        write(getResponse(), out, encodingValues);
    }

    @Override
    public void write(SoapResponse element, OutputStream out) throws OwsExceptionReport {
        write(element, out, new EncodingValues());
    }

    @Override
    public void write(SoapResponse element, OutputStream out, EncodingValues encodingValues) throws 
            OwsExceptionReport {
        try {
            init(out);
            start(encodingValues.isEmbedded());
            writeSoapEnvelope(element);
            end();
            finish();
        } catch (XMLStreamException xmlse) {
            throw new NoApplicableCodeException().causedBy(xmlse);
        }
    }

    /**
     * Set the response element to encode and write to stream
     * 
     * @param response
     *            Service internal response
     */
    public void setResponse(SoapResponse response) {
        this.response = response;
    }

    /**
     * Get the response element to encode and write to stream
     * 
     * @return The response element to encode and write to stream
     */
    protected SoapResponse getResponse() {
        return response;
    }

    /**
     * Write the SOAP 1.2. envelope element
     * 
     * @param response
     *            The response element to encode and write to stream
     * @throws XMLStreamException
     *             If an error occurs when writing to {@link OutputStream} If an
     *             error occurs when writing to {@link OutputStream}
     * @throws OwsExceptionReport
     *             If an encoding error occurs
     */
    protected void writeSoapEnvelope(SoapResponse response) throws XMLStreamException, OwsExceptionReport {
        start(SoapConstants.SOAP_12_ENVELOPE);
        namespace(W3CConstants.NS_XLINK_PREFIX, W3CConstants.NS_XLINK);
        namespace(SoapConstants.NS_SOAP_PREFIX, SoapConstants.NS_SOAP_12);
        schemaLocation(getSchemaLocation(response));
        writeNewLine();
        // writeSoapHeader()
        writeSoapBody(response);
        writeNewLine();
        end(SoapConstants.SOAP_12_ENVELOPE);

    }

    protected Set<SchemaLocation> getSchemaLocation(SoapResponse response) throws OwsExceptionReport, XMLStreamException {
        Set<SchemaLocation> schemaLocations = Sets.newHashSet();
        schemaLocations.add(SoapConstants.SOAP_12_SCHEMA_LOCATION);
        if (response.isSetBodyContent()) {
            Encoder<Object, AbstractServiceResponse> encoder = getEncoder(response.getBodyContent());
            if (encoder != null) {
                schemaLocations.addAll(encoder.getSchemaLocations());
            }
        }
        return schemaLocations;
    }

    /**
     * Write the SOAP 1.2 body element
     * 
     * @param response
     *            The response element to encode and write to stream
     * @throws XMLStreamException
     *             If an error occurs when writing to {@link OutputStream}
     * @throws OwsExceptionReport
     *             If an encoding error occurs
     */
    protected void writeSoapBody(SoapResponse response) throws XMLStreamException, OwsExceptionReport {
        int before = indent;
        start(SoapConstants.SOAP_12_BODY);
        writeNewLine();
        if (response != null) {
            if (response.isSetSoapFault()) {
                writeSoapFault(response.getSoapFault());
            } else if (response.hasException()) {
                writeSoapFaultFromException(response.getException());
            } else if (response.isSetBodyContent()) {
                writeBodyContent(response.getBodyContent());
            }
        }
        indent = before;
        writeNewLine();
        end(SoapConstants.SOAP_12_BODY);
    }

    /**
     * Encode and write the {@link AbstractServiceResponse} to stream
     * 
     * @param bodyResponse
     *            The service internal response to encode and write
     * @throws XMLStreamException
     *             If an error occurs when writing to {@link OutputStream}
     * @throws OwsExceptionReport
     *             If an encoding error occurs
     */
    @SuppressWarnings({ "rawtypes", "unchecked" })
    protected void writeBodyContent(AbstractServiceResponse bodyResponse) throws XMLStreamException,
            OwsExceptionReport {
        Encoder<Object, AbstractServiceResponse> encoder =
                getEncoder(new OperationEncoderKey(bodyResponse.getOperationKey(), MediaTypes.APPLICATION_XML));
        if (encoder instanceof StreamingEncoder<?, ?>) {
            ((StreamingEncoder) encoder).encode(bodyResponse, getOutputStream(), new EncodingValues().setAsDocument(true).setEmbedded(true).setIndent(indent));
        } else {
            String soapBodyContent =
                    ((XmlObject) encoder.encode(bodyResponse)).xmlText(XmlOptionsHelper.getInstance().getXmlOptions());
            if (soapBodyContent.startsWith("<?xml")) {
                soapBodyContent = soapBodyContent.substring(soapBodyContent.indexOf(Constants.GREATER_THAN_SIGN_STRING));
            }
            rawText(soapBodyContent);
        }
    }

    /**
     * Encode and write SOAP 1.2 fault element to SOAP 1.2 body element
     * 
     * @param fault
     *            Service internal SOAP fault representation
     * @throws OwsExceptionReport
     *             If an encoding error occurs
     * @throws XMLStreamException
     *             If an error occurs when writing to {@link OutputStream}
     */
    protected void writeSoapFault(SoapFault fault) throws OwsExceptionReport, XMLStreamException {
        Encoder<XmlObject, SoapFault> encoder = CodingHelper.getEncoder(SoapConstants.NS_SOAP_12, fault);
        String soapFault = ((XmlObject) encoder.encode(fault)).xmlText(XmlOptionsHelper.getInstance().getXmlOptions());
        if (soapFault.startsWith("<?xml")) {
            soapFault = soapFault.substring(soapFault.indexOf(Constants.GREATER_THAN_SIGN_STRING));
        }
        rawText(soapFault);
    }

    /**
     * Encode and write {@link OwsExceptionReport} element to SOAP 1.2 body
     * element
     * 
     * @param exception
     *            Service internal {@link OwsExceptionReport}
     * @throws OwsExceptionReport
     *             If an encoding error occurs
     * @throws XMLStreamException
     *             If an error occurs when writing to {@link OutputStream}
     */
    protected void writeSoapFaultFromException(OwsExceptionReport exception) throws OwsExceptionReport,
            XMLStreamException {
        Encoder<XmlObject, OwsExceptionReport> encoder = CodingHelper.getEncoder(SoapConstants.NS_SOAP_12, exception);
        String soapFault =
                ((XmlObject) encoder.encode(exception)).xmlText(XmlOptionsHelper.getInstance().getXmlOptions());
        if (soapFault.startsWith("<?xml")) {
            soapFault = soapFault.substring(soapFault.indexOf(Constants.GREATER_THAN_SIGN_STRING));
        }
        rawText(soapFault);
    }

    protected Encoder<Object, AbstractServiceResponse> getEncoder(AbstractServiceResponse abstractServiceResponse) throws NoEncoderForKeyException {
         return getEncoder(new OperationEncoderKey(abstractServiceResponse.getOperationKey(), MediaTypes.APPLICATION_XML));
    }

    /**
     * Get encoder for {@link EncoderKey}
     * 
     * @param key
     *            Encoder key to get encoder for
     * @return Matching encoder
     * @throws NoEncoderForKeyException
     *             If no matching encoder was found
     */
    protected Encoder<Object, AbstractServiceResponse> getEncoder(EncoderKey key) throws NoEncoderForKeyException {
        Encoder<Object, AbstractServiceResponse> encoder = CodingRepository.getInstance().getEncoder(key);
        if (encoder == null) {
            throw new NoEncoderForKeyException(key);
        }
        return encoder;
    }
}
