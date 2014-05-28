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
package org.n52.sos.encode.sos.v2;

import java.io.OutputStream;
import java.util.Set;

import javax.xml.stream.XMLStreamException;

import net.opengis.sos.x20.GetObservationResponseDocument;
import net.opengis.sos.x20.GetObservationResponseType;

import org.apache.xmlbeans.XmlObject;
import org.n52.sos.encode.EncodingValues;
import org.n52.sos.encode.ObservationEncoder;
import org.n52.sos.encode.streaming.sos.v2.GetObservationResponseXmlStreamWriter;
import org.n52.sos.exception.ows.NoApplicableCodeException;
import org.n52.sos.ogc.om.OmObservation;
import org.n52.sos.ogc.om.StreamingObservation;
import org.n52.sos.ogc.om.StreamingValue;
import org.n52.sos.ogc.ows.OwsExceptionReport;
import org.n52.sos.ogc.sos.Sos2Constants;
import org.n52.sos.ogc.sos.SosConstants;
import org.n52.sos.response.GetObservationResponse;
import org.n52.sos.util.XmlHelper;
import org.n52.sos.w3c.SchemaLocation;

import com.google.common.collect.Sets;

/**
 * TODO JavaDoc
 * 
 * @author Christian Autermann <c.autermann@52north.org>
 * 
 * @since 4.0.0
 */
public class GetObservationResponseEncoder extends AbstractObservationResponseEncoder<GetObservationResponse> {
    public GetObservationResponseEncoder() {
        super(SosConstants.Operations.GetObservation.name(), GetObservationResponse.class);
    }

    @Override
    public Set<SchemaLocation> getConcreteSchemaLocations() {
        return Sets.newHashSet(Sos2Constants.SOS_GET_OBSERVATION_SCHEMA_LOCATION);
    }

    @Override
    protected XmlObject createResponse(ObservationEncoder<XmlObject, OmObservation> encoder,
            GetObservationResponse response) throws OwsExceptionReport {
        GetObservationResponseDocument doc = GetObservationResponseDocument.Factory.newInstance(getXmlOptions());
        GetObservationResponseType xbResponse = doc.addNewGetObservationResponse();
        if (encoder.shouldObservationsWithSameXBeMerged()) {
            response.mergeObservationsWithSameConstellation();
        }
        for (OmObservation o : response.getObservationCollection()) {
            if (o.getValue() instanceof StreamingObservation) {
                StreamingObservation streamingObservation = (StreamingObservation)o.getValue();
                if (streamingObservation.hasNextValue()) {
                    if (encoder.shouldObservationsWithSameXBeMerged() || response.isSetMergeObservation()) {
                        for (OmObservation obs : streamingObservation.mergeObservation()) {
                            xbResponse.addNewObservationData().addNewOMObservation().set(encoder.encode(obs));
                        }
                    } else {
                        do {
                            xbResponse.addNewObservationData().addNewOMObservation().set(encoder.encode(streamingObservation.nextSingleObservation()));
                        } while (streamingObservation.hasNextValue());
                    }
                } else if (streamingObservation.getValue() != null) {
                    xbResponse.addNewObservationData().addNewOMObservation().set(encoder.encode(streamingObservation.getValue().getValue()));
                }
            } else if (o.getValue() instanceof StreamingValue) {
                StreamingValue streamingValue = (StreamingValue)o.getValue();
                if (streamingValue.hasNextValue()) {
                    if (encoder.shouldObservationsWithSameXBeMerged() || response.isSetMergeObservation()) {
                        for (OmObservation obs : streamingValue.mergeObservation()) {
                            xbResponse.addNewObservationData().addNewOMObservation().set(encoder.encode(obs));
                        }
                    } else {
                        do {
                            xbResponse.addNewObservationData().addNewOMObservation().set(encoder.encode(streamingValue.nextSingleObservation()));
                        } while (streamingValue.hasNextValue());
                    }
                } else if (streamingValue.getValue() != null) {
                    xbResponse.addNewObservationData().addNewOMObservation().set(encoder.encode(streamingValue.getValue().getValue()));
                }
//                StreamingValue streamingValue = (StreamingValue)o.getValue();
//                if (streamingValue.hasNextValue()) {
//                    do {
//                        writeObservationData(streamingValue.nextSingleObservation(), encoder, encodingValues);
//                        writeNewLine();
//                    } while (streamingValue.hasNextValue());
//                } else if (streamingValue.getValue() != null) {
//                    writeObservationData(streamingValue.getValue().getValue(), encoder, encodingValues);
//                    writeNewLine();
//                }
            } else {
                xbResponse.addNewObservationData().addNewOMObservation().set(encoder.encode(o));
            }
        }
        // in a single observation the gml:ids must be unique
        if (response.getObservationCollection().size() > 1) {
            XmlHelper.makeGmlIdsUnique(doc.getDomNode());
        }
        return doc;
    }

    @Override
    protected void createResponse(ObservationEncoder<XmlObject, OmObservation> encoder,
            GetObservationResponse response, OutputStream outputStream, EncodingValues encodingValues)
            throws OwsExceptionReport {
        try {
            encodingValues.setEncoder(this);
            new GetObservationResponseXmlStreamWriter().write(response, outputStream, encodingValues);
        } catch (XMLStreamException xmlse) {
            throw new NoApplicableCodeException().causedBy(xmlse);
        }

    }

}
