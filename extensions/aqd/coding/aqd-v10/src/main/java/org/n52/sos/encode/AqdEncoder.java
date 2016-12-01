/*
 * Copyright (C) 2012-2016 52°North Initiative for Geospatial Open Source
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
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import javax.inject.Inject;
import javax.xml.stream.XMLStreamException;

import org.apache.xmlbeans.XmlException;
import org.apache.xmlbeans.XmlObject;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.n52.shetland.ogc.gml.GmlConstants;
import org.n52.shetland.ogc.gml.time.Time;
import org.n52.shetland.ogc.gml.time.TimeInstant;
import org.n52.shetland.ogc.gml.time.TimePeriod;
import org.n52.shetland.ogc.om.OmConstants;
import org.n52.shetland.ogc.om.OmObservation;
import org.n52.shetland.ogc.om.features.FeatureCollection;
import org.n52.shetland.ogc.ows.exception.OwsExceptionReport;
import org.n52.shetland.ogc.sos.Sos1Constants;
import org.n52.shetland.ogc.sos.SosConstants;
import org.n52.shetland.util.JavaHelper;
import org.n52.shetland.w3c.SchemaLocation;
import org.n52.sos.aqd.AqdConstants;
import org.n52.sos.aqd.AqdHelper;
import org.n52.sos.aqd.ReportObligationType;
import org.n52.sos.coding.encode.ObservationEncoder;
import org.n52.sos.encode.xml.stream.inspire.aqd.EReportingHeaderEncoder;
import org.n52.sos.inspire.aqd.EReportingHeader;
import org.n52.sos.inspire.aqd.ReportObligationRepository;
import org.n52.sos.ogc.om.StreamingValue;
import org.n52.sos.response.AbstractStreaming;
import org.n52.sos.response.GetObservationResponse;
import org.n52.sos.util.CodingHelper;
import org.n52.sos.util.Referenceable;
import org.n52.svalbard.EncodingContext;
import org.n52.svalbard.SosHelperValues;
import org.n52.svalbard.encode.EncoderKey;
import org.n52.svalbard.encode.exception.EncodingException;
import org.n52.svalbard.encode.exception.UnsupportedEncoderInputException;
import org.n52.svalbard.xml.AbstractXmlEncoder;

import com.google.common.base.Joiner;
import com.google.common.collect.Sets;

public class AqdEncoder extends AbstractXmlEncoder<XmlObject, Object> implements ObservationEncoder<XmlObject, Object> {

    private static final Logger LOGGER = LoggerFactory.getLogger(AqdEncoder.class);

    private static final Set<EncoderKey> ENCODER_KEY_TYPES = CodingHelper.encoderKeysForElements(AqdConstants.NS_AQD,
            GetObservationResponse.class, OmObservation.class, EReportingHeader.class);

    private AqdHelper aqdHelper;
    private ReportObligationRepository reportObligationRepository;

    public AqdEncoder() {
        LOGGER.debug("Encoder for the following keys initialized successfully: {}!",
                Joiner.on(", ").join(ENCODER_KEY_TYPES));
    }

    @Override
    public Set<EncoderKey> getKeys() {
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
        if (SosConstants.SOS.equals(service) && Sos1Constants.VERSION.equals(version)) {
          return Sets.newHashSet(AqdConstants.AQD_CONTENT_TYPE.toString());
        }
        return Sets.newHashSet(AqdConstants.NS_AQD);
    }

    @Override
    public XmlObject encode(Object element, EncodingContext additionalValues) throws EncodingException,
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

    private XmlObject encodeGetObservationResponse(GetObservationResponse response) throws EncodingException {
        try {
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
            EncodingContext ctx = EncodingContext.empty()
                            .with(SosHelperValues.ENCODE_NAMESPACE, OmConstants.NS_OM_2)
                            .with(SosHelperValues.DOCUMENT, null);
            return encodeObjectToXml(GmlConstants.NS_GML_32, featureCollection, ctx);
        } catch (OwsExceptionReport ex) {
            throw new EncodingException(ex);
        }
    }

    private XmlObject encodeOmObservation(OmObservation element) throws EncodingException {
        return encodeObjectToXml(OmConstants.NS_OM_2, element);
    }

    private XmlObject encodeEReportingHeader(EReportingHeader element) throws EncodingException {
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            new EReportingHeaderEncoder(element).write(baos);
            return XmlObject.Factory.parse(baos.toString("UTF8"));
        } catch (XMLStreamException | XmlException | UnsupportedEncodingException xmlse) {
            throw new EncodingException("Error encoding response", xmlse);
        }

    }

    private ReportObligationType getReportObligationType(GetObservationResponse response)
            throws EncodingException {
        try {
            return getAqdHelper().getFlow(response.getExtensions());
        } catch (OwsExceptionReport ex) {
            throw new EncodingException(ex);
        }
    }

    private FeatureCollection getFeatureCollection(GetObservationResponse response) throws EncodingException {
        FeatureCollection featureCollection = new FeatureCollection();
        featureCollection.setGmlId("fc_" + JavaHelper.generateID(new DateTime().toString()));

        return featureCollection;
    }

    private AqdHelper getAqdHelper() {
        return this.aqdHelper;
    }

    @Inject
    public void setAqdHelper(AqdHelper aqdHelper) {
        this.aqdHelper = Objects.requireNonNull(aqdHelper);
    }

    protected EReportingHeader getEReportingHeader(ReportObligationType type) throws OwsExceptionReport {
        return this.reportObligationRepository.createHeader(type);
    }

    @Inject
    public void setReportObligationRepository(ReportObligationRepository reportObligationRepository) {
        this.reportObligationRepository = Objects.requireNonNull(reportObligationRepository);
    }
}
