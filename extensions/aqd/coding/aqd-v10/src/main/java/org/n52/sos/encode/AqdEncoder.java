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

import java.io.ByteArrayOutputStream;
import java.io.UnsupportedEncodingException;
import java.util.Collections;
import java.util.EnumMap;
import java.util.Map;
import java.util.Set;

import javax.xml.stream.XMLStreamException;

import org.apache.xmlbeans.XmlException;
import org.apache.xmlbeans.XmlObject;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.n52.sos.aqd.AqdConstants;
import org.n52.sos.aqd.AqdHelper;
import org.n52.sos.aqd.ReportObligationType;
import org.n52.sos.encode.xml.stream.inspire.aqd.EReportingHeaderEncoder;
import org.n52.sos.exception.CodedException;
import org.n52.sos.exception.ows.InvalidParameterValueException;
import org.n52.sos.exception.ows.NoApplicableCodeException;
import org.n52.sos.exception.ows.concrete.UnsupportedEncoderInputException;
import org.n52.sos.inspire.aqd.EReportingHeader;
import org.n52.sos.inspire.aqd.ReportObligationRepository;
import org.n52.sos.ogc.gml.GmlConstants;
import org.n52.sos.ogc.gml.time.Time;
import org.n52.sos.ogc.gml.time.TimeInstant;
import org.n52.sos.ogc.gml.time.TimePeriod;
import org.n52.sos.ogc.om.AbstractStreaming;
import org.n52.sos.ogc.om.OmConstants;
import org.n52.sos.ogc.om.OmObservation;
import org.n52.sos.ogc.om.StreamingValue;
import org.n52.sos.ogc.om.features.FeatureCollection;
import org.n52.sos.ogc.ows.OwsExceptionReport;
import org.n52.sos.ogc.sos.SosConstants.HelperValues;
import org.n52.sos.response.GetObservationResponse;
import org.n52.sos.util.CodingHelper;
import org.n52.sos.util.JavaHelper;
import org.n52.sos.util.Referenceable;
import org.n52.sos.w3c.SchemaLocation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Joiner;
import com.google.common.collect.Sets;

public class AqdEncoder extends AbstractXmlEncoder<Object> implements ObservationEncoder<XmlObject, Object> {

    private static final Logger LOGGER = LoggerFactory.getLogger(AqdEncoder.class);

    private static final Set<EncoderKey> ENCODER_KEY_TYPES = CodingHelper.encoderKeysForElements(AqdConstants.NS_AQD,
            GetObservationResponse.class, OmObservation.class, EReportingHeader.class);

    public AqdEncoder() {
        LOGGER.debug("Encoder for the following keys initialized successfully: {}!",
                Joiner.on(", ").join(ENCODER_KEY_TYPES));
    }

    @Override
    public Set<EncoderKey> getEncoderKeyType() {
        return Collections.unmodifiableSet(ENCODER_KEY_TYPES);
    }

    @Override
    public Set<SchemaLocation> getSchemaLocations() {
        return Sets.newHashSet(AqdConstants.NS_AQD_SCHEMA_LOCATION);
    }

    @Override
    public void addNamespacePrefixToMap(Map<String, String> nameSpacePrefixMap) {
        nameSpacePrefixMap.put(AqdConstants.NS_AQD, AqdConstants.NS_AQD_PREFIX);
    }

    @Override
    public boolean isObservationAndMeasurmentV20Type() {
        return false;
    }

    @Override
    public boolean shouldObservationsWithSameXBeMerged() {
        return false;
    }

    @Override
    public boolean supportsResultStreamingForMergedValues() {
        return false;
    }

    @Override
    public Set<String> getSupportedResponseFormats(String service, String version) {
        return Sets.newHashSet(AqdConstants.NS_AQD);
    }

    @Override
    public XmlObject encode(Object element, Map<HelperValues, String> additionalValues) throws OwsExceptionReport,
            UnsupportedEncoderInputException {
        if (element instanceof GetObservationResponse) {
            return encodeGetObservationResponse((GetObservationResponse) element);
        } else if (element instanceof OmObservation) {
            return encodeOmObservation((OmObservation) element);
        } else if (element instanceof EReportingHeader) {
            return encodeEReportingHeader((EReportingHeader) element);
        }
        throw new UnsupportedEncoderInputException(this, element);
    }

    private XmlObject encodeGetObservationResponse(GetObservationResponse response) throws OwsExceptionReport {
        FeatureCollection featureCollection = getFeatureCollection(response);
        // TODO get FLOW from response
        EReportingHeader eReportingHeader = getEReportingHeader(getReportObligationType(response));
        featureCollection.addMember(eReportingHeader);
        TimePeriod timePeriod = new TimePeriod();
        TimeInstant resultTime = new TimeInstant(new DateTime(DateTimeZone.UTC));
        boolean mergeStreaming = response.hasStreamingData() && !response.isSetMergeObservation();
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
        return CodingHelper.encodeObjectToXml(GmlConstants.NS_GML_32, featureCollection, additionalValues);
    }

    private XmlObject encodeOmObservation(OmObservation element) throws OwsExceptionReport {
        return CodingHelper.encodeObjectToXml(OmConstants.NS_OM_2, element);
    }

    private XmlObject encodeEReportingHeader(EReportingHeader element) throws OwsExceptionReport {
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            new EReportingHeaderEncoder(element).write(baos);
            return XmlObject.Factory.parse(baos.toString("UTF8"));
        } catch (XMLStreamException xmlse) {
            throw new NoApplicableCodeException().causedBy(xmlse).withMessage("Error encoding response");
        } catch (XmlException xmle) {
            throw new NoApplicableCodeException().causedBy(xmle).withMessage("Error encoding response");
        } catch (UnsupportedEncodingException uee) {
            throw new NoApplicableCodeException().causedBy(uee).withMessage("Error encoding response");
        }

    }

    private ReportObligationType getReportObligationType(GetObservationResponse response)
            throws InvalidParameterValueException {
        return getAqdHelper().getFlow(response.getExtensions());
    }

    private FeatureCollection getFeatureCollection(GetObservationResponse response) throws CodedException {
        FeatureCollection featureCollection = new FeatureCollection();
        featureCollection.setGmlId("fc_" + JavaHelper.generateID(new DateTime().toString()));

        return featureCollection;
    }

    private AqdHelper getAqdHelper() {
        return AqdHelper.getInstance();
    }

    protected EReportingHeader getEReportingHeader(ReportObligationType type) throws CodedException {
        return ReportObligationRepository.getInstance().createHeader(type);
    }
}
