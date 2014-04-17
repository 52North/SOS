/**
 * Copyright (C) 2012-2014 52°North Initiative for Geospatial Open Source
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
package org.n52.sos.encode.streaming.sos.v2;

import java.io.OutputStream;
import java.util.Map;

import javax.xml.stream.XMLStreamException;

import org.apache.xmlbeans.XmlObject;
import org.n52.sos.coding.CodingRepository;
import org.n52.sos.encode.Encoder;
import org.n52.sos.encode.EncodingValues;
import org.n52.sos.encode.ObservationEncoder;
import org.n52.sos.encode.OperationEncoderKey;
import org.n52.sos.encode.XmlEncoderKey;
import org.n52.sos.encode.XmlStreamWriter;
import org.n52.sos.encode.streaming.StreamingEncoder;
import org.n52.sos.exception.ows.NoApplicableCodeException;
import org.n52.sos.ogc.om.OmObservation;
import org.n52.sos.ogc.ows.OwsExceptionReport;
import org.n52.sos.ogc.sos.Sos2StreamingConstants;
import org.n52.sos.ogc.sos.SosConstants.HelperValues;
import org.n52.sos.response.AbstractServiceResponse;
import org.n52.sos.response.GetObservationResponse;
import org.n52.sos.util.Constants;
import org.n52.sos.util.XmlOptionsHelper;
import org.n52.sos.util.http.MediaTypes;
import org.n52.sos.w3c.W3CConstants;

import com.google.common.collect.Maps;

public class GetObservationResponseXmlStreamWriter extends XmlStreamWriter<GetObservationResponse> {
    
    private GetObservationResponse response;
    
    public GetObservationResponseXmlStreamWriter() {
    }
    
    public GetObservationResponseXmlStreamWriter(GetObservationResponse response) {
        setResponse(response);
    }

    public void setResponse(GetObservationResponse response) {
        this.response = response;
    }

    protected GetObservationResponse getResponse() {
        return response;
    }
    
    @Override
    public void write(OutputStream out) throws XMLStreamException, OwsExceptionReport {
        write(getResponse(), out);
    }
    

    @Override
    public void write(OutputStream out, EncodingValues encodingValues) throws XMLStreamException, OwsExceptionReport {
        write(getResponse(), out, encodingValues);
    }

    @Override
    public void write(GetObservationResponse response, OutputStream out) throws XMLStreamException,
            OwsExceptionReport {
        write(response, out, new EncodingValues());
    }
    
    @Override
    public void write(GetObservationResponse response, OutputStream out, EncodingValues encodingValues) throws XMLStreamException,
            OwsExceptionReport {
        try {
            init(out, encodingValues);
            start(encodingValues.isEmbedded());
            writeGetObservationResponseDoc(response);
            end();
            finish();
        } catch (XMLStreamException xmlse) {
            throw new NoApplicableCodeException().causedBy(xmlse);
        }
    }

    private void writeGetObservationResponseDoc(GetObservationResponse response) throws XMLStreamException, OwsExceptionReport {
        start(Sos2StreamingConstants.GET_OBSERVATION_RESPONSE);
        namespace(W3CConstants.NS_XLINK_PREFIX, W3CConstants.NS_XLINK);
        namespace(Sos2StreamingConstants.NS_SOS_PREFIX, Sos2StreamingConstants.NS_SOS_20);
        writeNewLine();
        ObservationEncoder<XmlObject, OmObservation> encoder = findObservationEncoder(response.getResponseFormat());
        Map<HelperValues, String> additionalValues = Maps.newHashMap();
        additionalValues.put(HelperValues.DOCUMENT, null);
        // TODO remove this if the values are streamed
        if (encoder.shouldObservationsWithSameXBeMerged()) {
            response.mergeObservationsWithSameConstellation();
        }
        for (OmObservation observation : response.getObservationCollection()) {
            writeObservationData(observation, encoder, additionalValues);
            writeNewLine();
        }
        indent--;
        end(Sos2StreamingConstants.GET_OBSERVATION_RESPONSE);
    }
    
    @SuppressWarnings("unchecked")
    private void writeObservationData(OmObservation observation, ObservationEncoder<XmlObject, OmObservation> encoder, Map<HelperValues, String> additionalValues) throws XMLStreamException, OwsExceptionReport {
        start(Sos2StreamingConstants.OBSERVATION_DATA);
        writeNewLine();
        if (encoder instanceof StreamingEncoder<?, ?>) {
            ((StreamingEncoder<XmlObject, OmObservation>) encoder).encode(observation, getOutputStream(), new EncodingValues(additionalValues).setAsDocument(true).setEmbedded(true).setIndent(indent));
        } else {
            rawText(((XmlObject) encoder.encode(observation, additionalValues)).xmlText(XmlOptionsHelper.getInstance().getXmlOptions()));
        }
        indent--;
        writeNewLine();
        end(Sos2StreamingConstants.OBSERVATION_DATA);
        indent++;
    }
    
    /**
     * Finds a O&Mv2 compatible {@link ObservationEncoder}
     * 
     * @param responseFormat
     *            the response format
     * 
     * @return the encoder or {@code null} if none is found
     * 
     * @throws OwsExceptionReport
     *             if the found encoder is not a {@linkplain ObservationEncoder}
     */
    private ObservationEncoder<XmlObject, OmObservation> findObservationEncoder(String responseFormat)
            throws OwsExceptionReport {
        Encoder<XmlObject, OmObservation> encoder =
                CodingRepository.getInstance().getEncoder(new XmlEncoderKey(responseFormat, OmObservation.class));
        if (encoder == null) {
            return null;
        } else if (encoder instanceof ObservationEncoder) {
            ObservationEncoder<XmlObject, OmObservation> oe = (ObservationEncoder<XmlObject, OmObservation>) encoder;
            return oe.isObservationAndMeasurmentV20Type() ? oe : null;
        } else {
            throw new NoApplicableCodeException()
                    .withMessage("Error while encoding response, encoder is not of type ObservationEncoder!");
        }
    }
 
}
