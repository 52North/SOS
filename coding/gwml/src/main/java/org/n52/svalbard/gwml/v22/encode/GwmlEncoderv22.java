/**
 * Copyright (C) 2012-2017 52°North Initiative for Geospatial Open Source
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
package org.n52.svalbard.gwml.v22.encode;

import java.io.OutputStream;
import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.xml.stream.XMLStreamException;

import org.n52.sos.encode.EncoderKey;
import org.n52.sos.encode.EncodingValues;
import org.n52.sos.encode.OmEncoderv20;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Joiner;
import com.google.common.collect.Sets;

import net.opengis.om.x20.OMObservationType;

import org.n52.sos.exception.ows.NoApplicableCodeException;
import org.n52.sos.ogc.gwml.GWMLConstants;
import org.n52.sos.ogc.om.MultiObservationValues;
import org.n52.sos.ogc.om.NamedValue;
import org.n52.sos.ogc.om.OmConstants;
import org.n52.sos.ogc.om.OmObservation;
import org.n52.sos.ogc.om.SingleObservationValue;
import org.n52.sos.ogc.ows.OwsExceptionReport;
import org.n52.sos.ogc.sos.Sos2Constants;
import org.n52.sos.ogc.sos.SosConstants;
import org.n52.sos.service.ServiceConstants.SupportedTypeKey;
import org.n52.sos.util.CodingHelper;
import org.n52.sos.util.CollectionHelper;
import org.n52.sos.util.http.MediaType;
import org.n52.sos.w3c.SchemaLocation;
import org.n52.svalbard.gwml.v22.encode.streaming.GwmlV22XmlStreamWriter;

public class GwmlEncoderv22 extends OmEncoderv20 {
    /**
     * logger, used for logging while initializing the constants from config
     * file
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(GwmlEncoderv22.class);
    
    @SuppressWarnings("unchecked")
    private static final Set<EncoderKey> ENCODER_KEYS = CollectionHelper.union(
            CodingHelper.encoderKeysForElements(GWMLConstants.NS_GWML_22,
                    OmObservation.class, NamedValue.class, SingleObservationValue.class, MultiObservationValues.class),
            CodingHelper.encoderKeysForElements(GWMLConstants.NS_GWML_WELL_22,
                    OmObservation.class, NamedValue.class, SingleObservationValue.class, MultiObservationValues.class));

    private static final Map<SupportedTypeKey, Set<String>> SUPPORTED_TYPES = Collections.singletonMap(
            SupportedTypeKey.ObservationType, (Set<String>) Sets.newHashSet(GWMLConstants.OBS_TYPE_GEOLOGY_LOG, OmConstants.OBS_TYPE_PROFILE_OBSERVATION));

    private static final Map<String, Map<String, Set<String>>> SUPPORTED_RESPONSE_FORMATS = Collections.singletonMap(
            SosConstants.SOS,
            Collections.singletonMap(Sos2Constants.SERVICEVERSION, Collections.singleton(GWMLConstants.NS_GWML_22)));

    public GwmlEncoderv22() {
        LOGGER.debug("Encoder for the following keys initialized successfully: {}!", Joiner.on(", ")
                .join(ENCODER_KEYS));
    }


    @Override
    public Set<EncoderKey> getEncoderKeyType() {
        return Collections.unmodifiableSet(ENCODER_KEYS);
    }

    @Override
    public Map<SupportedTypeKey, Set<String>> getSupportedTypes() {
        return Collections.unmodifiableMap(SUPPORTED_TYPES);
    }
    
    @Override
    public Map<String, Set<String>> getSupportedResponseFormatObservationTypes() {
        return Collections.singletonMap(GWMLConstants.NS_GWML_22, getSupportedTypes().get(SupportedTypeKey.ObservationType));
    }

    @Override
    public boolean isObservationAndMeasurmentV20Type() {
        return true;
    }

    @Override
    public Set<String> getSupportedResponseFormats(String service, String version) {
        if (SUPPORTED_RESPONSE_FORMATS.get(service) != null
                && SUPPORTED_RESPONSE_FORMATS.get(service).get(version) != null) {
            return SUPPORTED_RESPONSE_FORMATS.get(service).get(version);
        }
        return new HashSet<>(0);
    }

    @Override
    public boolean shouldObservationsWithSameXBeMerged() {
        return false;
    }

    @Override
    public boolean supportsResultStreamingForMergedValues() {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public MediaType getContentType() {
        return OmConstants.CONTENT_TYPE_OM_2;
    }

    @Override
    public Set<SchemaLocation> getSchemaLocations() {
        Set<SchemaLocation> schemaLocations = Sets.newHashSet(GWMLConstants.GWML_22_SCHEMA_LOCATION, GWMLConstants.GWML_WELL_22_SCHEMA_LOCATION);
        schemaLocations.addAll(super.getSchemaLocations());
        return schemaLocations;
    }
    
    @Override
    protected OMObservationType createOmObservationType() {
        return super.createOmObservationType();
    }


    @Override
    public void encode(Object objectToEncode, OutputStream outputStream, EncodingValues encodingValues)
            throws OwsExceptionReport {
        encodingValues.setEncoder(this);
        if (objectToEncode instanceof OmObservation) {
            try {
                new GwmlV22XmlStreamWriter().write((OmObservation)objectToEncode, outputStream, encodingValues);
            } catch (XMLStreamException xmlse) {
                throw new NoApplicableCodeException().causedBy(xmlse).withMessage("Error while writing element to stream!");
            }
        } else {
            super.encode(objectToEncode, outputStream, encodingValues);
        }
    }
}
