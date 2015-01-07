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
package org.n52.sos.encode.aqd.v1;

import java.io.OutputStream;
import java.util.Collections;
import java.util.EnumMap;
import java.util.Map;
import java.util.Set;

import javax.xml.stream.XMLStreamException;

import org.apache.xmlbeans.XmlObject;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.n52.sos.aqd.ReportObligationType;
import org.n52.sos.encode.EncodingValues;
import org.n52.sos.encode.streaming.StreamingDataEncoder;
import org.n52.sos.encode.streaming.aqd.v1.AqdGetObservationResponseXmlStreamWriter;
import org.n52.sos.exception.CodedException;
import org.n52.sos.exception.ows.InvalidParameterValueException;
import org.n52.sos.exception.ows.NoApplicableCodeException;
import org.n52.sos.inspire.aqd.EReportingHeader;
import org.n52.sos.ogc.gml.time.Time;
import org.n52.sos.ogc.gml.time.TimeInstant;
import org.n52.sos.ogc.gml.time.TimePeriod;
import org.n52.sos.ogc.om.AbstractStreaming;
import org.n52.sos.ogc.om.OmConstants;
import org.n52.sos.ogc.om.OmObservation;
import org.n52.sos.ogc.om.StreamingValue;
import org.n52.sos.ogc.om.features.FeatureCollection;
import org.n52.sos.ogc.ows.OwsExceptionReport;
import org.n52.sos.ogc.sos.SosConstants;
import org.n52.sos.ogc.sos.SosConstants.HelperValues;
import org.n52.sos.response.GetObservationResponse;
import org.n52.sos.util.JavaHelper;
import org.n52.sos.util.Referenceable;
import org.n52.sos.w3c.SchemaLocation;

public class AqdGetObservationResponseEncoder extends AbstractAqdResponseEncoder<GetObservationResponse> implements
        StreamingDataEncoder {

    public AqdGetObservationResponseEncoder() {
        super(SosConstants.Operations.GetObservation.name(), GetObservationResponse.class);
    }

    @Override
    protected Set<SchemaLocation> getConcreteSchemaLocations() {
        return Collections.emptySet();
    }

    @Override
    public boolean forceStreaming() {
        return true;
    }

    @Override
    protected XmlObject create(GetObservationResponse response) throws OwsExceptionReport {
        FeatureCollection featureCollection = getFeatureCollection(response);
        // TODO get FLOW from response
        EReportingHeader eReportingHeader = getEReportingHeader(getReportObligationType(response));
        featureCollection.addMember(eReportingHeader);
        TimePeriod timePeriod = new TimePeriod();
        boolean mergeStreaming = response.hasStreamingData() && !response.isSetMergeObservation();
        TimeInstant resultTime = new TimeInstant(new DateTime(DateTimeZone.UTC));
        int counter = 1;
        for (OmObservation observation : response.getObservationCollection()) {
            if (mergeStreaming) {
                AbstractStreaming value = (AbstractStreaming) observation.getValue();
                if (value instanceof StreamingValue) {
                    for (OmObservation omObservation : value.mergeObservation()) {
                        getAqdHelper().processObservation(omObservation, timePeriod, resultTime, featureCollection,
                                eReportingHeader, counter++);
                    }
                } else {
                    while (value.hasNextValue()) {
                        getAqdHelper().processObservation(value.nextSingleObservation(), timePeriod, resultTime,
                                featureCollection, eReportingHeader, counter++);
                    }
                }
            } else {
                getAqdHelper().processObservation(observation, timePeriod, resultTime, featureCollection,
                        eReportingHeader, counter++);
            }
        }
        if (!timePeriod.isEmpty()) {
            eReportingHeader.setReportingPeriod(Referenceable.of((Time) timePeriod));
        }
        Map<HelperValues, String> additionalValues = new EnumMap<HelperValues, String>(HelperValues.class);
        additionalValues.put(HelperValues.ENCODE_NAMESPACE, OmConstants.NS_OM_2);
        additionalValues.put(HelperValues.DOCUMENT, null);
        return encodeGml(additionalValues, featureCollection);
    }

    @Override
    protected void create(GetObservationResponse response, OutputStream outputStream, EncodingValues encodingValues)
            throws OwsExceptionReport {
        FeatureCollection featureCollection = getFeatureCollection(response);
        EReportingHeader eReportingHeader = getEReportingHeader(getReportObligationType(response));
        featureCollection.addMember(eReportingHeader);
        TimePeriod timePeriod = addToFeatureCollectionAndGetTimePeriod(featureCollection, response, eReportingHeader);
        if (!timePeriod.isEmpty()) {
            eReportingHeader.setReportingPeriod(Referenceable.of((Time) timePeriod));
        }
        encodingValues.setEncodingNamespace(OmConstants.NS_OM_2);
        Map<HelperValues, String> additionalValues = encodingValues.getAdditionalValues();
        additionalValues.put(HelperValues.ENCODE_NAMESPACE, OmConstants.NS_OM_2);
        additionalValues.put(HelperValues.DOCUMENT, null);
        try {
            new AqdGetObservationResponseXmlStreamWriter().write(featureCollection, outputStream, encodingValues);
        } catch (XMLStreamException xmlse) {
            throw new NoApplicableCodeException().causedBy(xmlse)
                    .withMessage("Error while writing element to stream!");
        }
    }

    private ReportObligationType getReportObligationType(GetObservationResponse response)
            throws InvalidParameterValueException {
        return getAqdHelper().getFlow(response.getExtensions());
    }

    private TimePeriod addToFeatureCollectionAndGetTimePeriod(FeatureCollection featureCollection,
            GetObservationResponse response, EReportingHeader eReportingHeader) {
        TimeInstant resultTime = new TimeInstant(new DateTime(DateTimeZone.UTC));
        TimePeriod timePeriod = new TimePeriod();
        int counter = 1;
        for (OmObservation observation : response.getObservationCollection()) {
            getAqdHelper().processObservation(observation, timePeriod, resultTime, featureCollection,
                    eReportingHeader, counter++);

        }
        return timePeriod;
    }

    private FeatureCollection getFeatureCollection(GetObservationResponse response) throws CodedException {
        FeatureCollection featureCollection = new FeatureCollection();
        featureCollection.setGmlId("fc_" + JavaHelper.generateID(new DateTime().toString()));

        return featureCollection;
    }

}
