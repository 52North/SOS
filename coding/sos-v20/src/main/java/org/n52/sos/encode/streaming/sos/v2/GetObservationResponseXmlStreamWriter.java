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
package org.n52.sos.encode.streaming.sos.v2;

import java.io.OutputStream;
import java.util.Set;

import javax.xml.stream.XMLStreamException;

import org.apache.xmlbeans.XmlObject;
import org.n52.sos.coding.CodingRepository;
import org.n52.sos.encode.Encoder;
import org.n52.sos.encode.EncodingValues;
import org.n52.sos.encode.ObservationEncoder;
import org.n52.sos.encode.XmlEncoderKey;
import org.n52.sos.encode.XmlStreamWriter;
import org.n52.sos.encode.streaming.StreamingDataEncoder;
import org.n52.sos.encode.streaming.StreamingEncoder;
import org.n52.sos.exception.ows.NoApplicableCodeException;
import org.n52.sos.ogc.om.OmObservation;
import org.n52.sos.ogc.om.StreamingObservation;
import org.n52.sos.ogc.om.StreamingValue;
import org.n52.sos.ogc.ows.OwsExceptionReport;
import org.n52.sos.ogc.sos.Sos2Constants;
import org.n52.sos.ogc.sos.Sos2StreamingConstants;
import org.n52.sos.ogc.sos.SosConstants.HelperValues;
import org.n52.sos.response.GetObservationResponse;
import org.n52.sos.util.CollectionHelper;
import org.n52.sos.util.XmlOptionsHelper;
import org.n52.sos.w3c.SchemaLocation;
import org.n52.sos.w3c.W3CConstants;

import com.google.common.collect.Sets;

/**
 * Implementatio of {@link XmlStreamWriter} for {@link GetObservationResponse}
 * 
 * @author Carsten Hollmann <c.hollmann@52north.org>
 * @since 4.1.0
 *
 */
public class GetObservationResponseXmlStreamWriter extends XmlStreamWriter<GetObservationResponse> implements StreamingDataEncoder {

    private GetObservationResponse response;

    /**
     * constructor
     */
    public GetObservationResponseXmlStreamWriter() {
    }

    /**
     * constructor
     * 
     * @param response
     *            {@link GetObservationResponse} to write to stream
     */
    public GetObservationResponseXmlStreamWriter(GetObservationResponse response) {
        setResponse(response);
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
    public void write(GetObservationResponse response, OutputStream out) throws XMLStreamException, OwsExceptionReport {
        write(response, out, new EncodingValues());
    }

    @Override
    public void write(GetObservationResponse response, OutputStream out, EncodingValues encodingValues)
            throws XMLStreamException, OwsExceptionReport {
        try {
            init(out, encodingValues);
            start(encodingValues.isEmbedded());
            writeGetObservationResponseDoc(response, encodingValues);
            end();
            finish();
        } catch (XMLStreamException xmlse) {
            throw new NoApplicableCodeException().causedBy(xmlse);
        }
    }

    /**
     * Set the {@link GetObservationResponse} to be written to stream
     * 
     * @param response
     *            {@link GetObservationResponse} to write to stream
     */
    protected void setResponse(GetObservationResponse response) {
        this.response = response;
    }

    /**
     * Get the {@link GetObservationResponse} to write to stream
     * 
     * @return {@link GetObservationResponse} to write
     */
    protected GetObservationResponse getResponse() {
        return response;
    }

    private void writeGetObservationResponseDoc(GetObservationResponse response, EncodingValues encodingValues)
            throws XMLStreamException, OwsExceptionReport {
        start(Sos2StreamingConstants.GET_OBSERVATION_RESPONSE);
        namespace(W3CConstants.NS_XLINK_PREFIX, W3CConstants.NS_XLINK);
        namespace(Sos2StreamingConstants.NS_SOS_PREFIX, Sos2StreamingConstants.NS_SOS_20);
        // get observation encoder
        ObservationEncoder<XmlObject, OmObservation> encoder = findObservationEncoder(response.getResponseFormat());
        encodingValues.getAdditionalValues().put(HelperValues.DOCUMENT, null);
        encodingValues.setEncodingNamespace(response.getResponseFormat());
        // write schemaLocation
        schemaLocation(getSchemaLocation(encodingValues, encoder));
        writeNewLine();
        // Map<HelperValues, String> additionalValues = Maps.newHashMap();
        // additionalValues.put(HelperValues.DOCUMENT, null);
        // EncodingValues encodingValues = new
        // EncodingValues(additionalValues).setEncodingNamespace(response.getResponseFormat());
        if (!response.isSetMergeObservation()) {
            response.setMergeObservations(encoder.shouldObservationsWithSameXBeMerged());
        }
        for (OmObservation o : response.getObservationCollection()) {
            if (o.getValue() instanceof StreamingObservation) {
                StreamingObservation streamingObservation = (StreamingObservation) o.getValue();
                if (streamingObservation.hasNextValue()) {
                    if (response.isSetMergeObservation()) {
                        for (OmObservation obs : streamingObservation.mergeObservation()) {
                            writeObservationData(obs, encoder, encodingValues);
                            writeNewLine();
                        }
                    } else {
                        do {
                            writeObservationData(streamingObservation.nextSingleObservation(), encoder, encodingValues);
                            writeNewLine();
                        } while (streamingObservation.hasNextValue());
                    }
                } else if (streamingObservation.getValue() != null) {
                    writeObservationData(streamingObservation.getValue().getValue(), encoder, encodingValues);
                    writeNewLine();
                }
            } else if (o.getValue() instanceof StreamingValue) {
                StreamingValue<?> streamingValue = (StreamingValue<?>) o.getValue();
                if (streamingValue.hasNextValue()) {
                    if (response.isSetMergeObservation()) {
                        if (encoder.supportsResultStreamingForMergedValues()) {
                            writeObservationData(o, encoder, encodingValues);
                            writeNewLine();
                        } else {
                            for (OmObservation obs : streamingValue.mergeObservation()) {
                                writeObservationData(obs, encoder, encodingValues);
                                writeNewLine();
                            }
                        }
                    } else {
                        do {
                            writeObservationData(streamingValue.nextSingleObservation(), encoder, encodingValues);
                            writeNewLine();
                        } while (streamingValue.hasNextValue());
                    }
                } else if (streamingValue.getValue() != null) {
                    writeObservationData(streamingValue.getValue().getValue(), encoder, encodingValues);
                    writeNewLine();
                }
            } else {
                writeObservationData(o, encoder, encodingValues);
                writeNewLine();
            }
        }
        indent--;
        end(Sos2StreamingConstants.GET_OBSERVATION_RESPONSE);
    }

    private Set<SchemaLocation> getSchemaLocation(EncodingValues encodingValue,
            ObservationEncoder<XmlObject, OmObservation> encoder) {
        Set<SchemaLocation> schemaLocations = Sets.newHashSet();
        if (encodingValue.isSetEncoder()
                && CollectionHelper.isNotEmpty(encodingValue.getEncoder().getSchemaLocations())) {
            schemaLocations.addAll(encodingValue.getEncoder().getSchemaLocations());
        } else {
            schemaLocations.add(Sos2Constants.SOS_GET_OBSERVATION_SCHEMA_LOCATION);
        }
        if (encoder != null && CollectionHelper.isNotEmpty(encoder.getSchemaLocations())) {
            schemaLocations.addAll(encoder.getSchemaLocations());
        }
        return schemaLocations;
    }

    @SuppressWarnings("unchecked")
    private void writeObservationData(OmObservation observation, ObservationEncoder<XmlObject, OmObservation> encoder,
            EncodingValues encodingValues) throws XMLStreamException, OwsExceptionReport {
        start(Sos2StreamingConstants.OBSERVATION_DATA);
        writeNewLine();
        if (encoder instanceof StreamingEncoder<?, ?>) {
            ((StreamingEncoder<XmlObject, OmObservation>) encoder).encode(observation, getOutputStream(),
                    encodingValues.setAsDocument(true).setEmbedded(true).setIndent(indent));
        } else {
            rawText((encoder.encode(observation, encodingValues.getAdditionalValues()))
                    .xmlText(XmlOptionsHelper.getInstance().getXmlOptions()));
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
