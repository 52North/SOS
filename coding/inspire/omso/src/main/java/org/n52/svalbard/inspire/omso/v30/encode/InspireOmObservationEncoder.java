/**
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
package org.n52.svalbard.inspire.omso.v30.encode;

import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.apache.xmlbeans.XmlObject;
import org.n52.sos.encode.AbstractXmlEncoder;
import org.n52.sos.encode.EncoderKey;
import org.n52.sos.encode.ObservationEncoder;
import org.n52.sos.exception.ows.concrete.UnsupportedEncoderInputException;
import org.n52.sos.ogc.om.OmConstants;
import org.n52.sos.ogc.om.OmObservation;
import org.n52.sos.ogc.ows.OwsExceptionReport;
import org.n52.sos.ogc.sos.Sos2Constants;
import org.n52.sos.ogc.sos.SosConstants;
import org.n52.sos.ogc.sos.SosConstants.HelperValues;
import org.n52.sos.util.CodingHelper;
import org.n52.sos.w3c.SchemaLocation;
import org.n52.svalbard.inspire.omso.InspireOMSOConstants;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

public class InspireOmObservationEncoder extends AbstractXmlEncoder<Object>
        implements ObservationEncoder<XmlObject, Object> {

    private static final Set<EncoderKey> ENCODER_KEYS =
            CodingHelper.encoderKeysForElements(InspireOMSOConstants.NS_OMSO_30, OmObservation.class);

    private static final Map<String, Map<String, Set<String>>> SUPPORTED_RESPONSE_FORMATS =
            Collections.singletonMap(SosConstants.SOS, 
            Collections.singletonMap(Sos2Constants.SERVICEVERSION,
            Collections.singleton(InspireOMSOConstants.NS_OMSO_30)));

    @Override
    public Set<EncoderKey> getEncoderKeyType() {
        return Collections.unmodifiableSet(ENCODER_KEYS);
    }

    @Override
    public XmlObject encode(Object element, Map<HelperValues, String> additionalValues)
            throws OwsExceptionReport, UnsupportedEncoderInputException {
        if (element instanceof OmObservation) {
            return encodeInspireOmsoType((OmObservation)element);
        }
        throw new UnsupportedEncoderInputException(this, element);
    }

    protected static XmlObject encodeInspireOmsoType(OmObservation o) throws OwsExceptionReport {
        return CodingHelper.encodeObjectToXml(InspireOMSOConstants.NS_OMSO_30, o);
    }

    @Override
    public boolean isObservationAndMeasurmentV20Type() {
        return true;
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
        if (SUPPORTED_RESPONSE_FORMATS.get(service) != null
                && SUPPORTED_RESPONSE_FORMATS.get(service).get(version) != null) {
            return SUPPORTED_RESPONSE_FORMATS.get(service).get(version);
        }
        return new HashSet<>(0);
    }

    @Override
    public Map<String, Set<String>> getSupportedResponseFormatObsrevationTypes() {
        return Maps.newHashMap();
    }
    
    @Override
    public void addNamespacePrefixToMap(Map<String, String> nameSpacePrefixMap) {
        nameSpacePrefixMap.put(InspireOMSOConstants.NS_OMSO_30, InspireOMSOConstants.NS_OMSO_PREFIX);
    }

    @Override
    public Set<SchemaLocation> getSchemaLocations() {
        return Sets.newHashSet(InspireOMSOConstants.OMSO_SCHEMA_LOCATION);
    }
}
