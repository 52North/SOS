/**
 * Copyright (C) 2012-2020 52°North Initiative for Geospatial Open Source
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

import org.n52.sos.encode.AbstractWmlEncoderv20;
import org.n52.sos.encode.Encoder;
import org.n52.sos.ogc.om.features.SfConstants;
import org.n52.sos.ogc.sensorML.SensorMLConstants;
import org.n52.sos.ogc.sos.Sos2Constants;
import org.n52.sos.ogc.sos.SosConstants;
import org.n52.svalbard.inspire.base.InspireBaseConstants;
import org.n52.svalbard.inspire.omor.InspireOMORConstants;
import org.n52.svalbard.inspire.omso.InspireOMSOConstants;

import net.opengis.om.x20.OMObservationType;

/**
 * Abstract {@link Encoder} implementation for INSPIRES OM
 * 
 * @author <a href="mailto:c.hollmann@52north.org">Carsten Hollmann</a>
 * @since 4.4.0
 *
 */
public abstract class AbstractOmInspireEncoder extends AbstractWmlEncoderv20 {

    private static final Map<String, Map<String, Set<String>>> SUPPORTED_RESPONSE_FORMATS =
            Collections.singletonMap(SosConstants.SOS, Collections.singletonMap(Sos2Constants.SERVICEVERSION,
                    Collections.singleton(InspireOMSOConstants.NS_OMSO_30)));

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
    public String getDefaultFeatureEncodingNamespace() {
        return SfConstants.NS_SAMS;
    }

    @Override
    protected String getDefaultProcedureEncodingNamspace() {
        return SensorMLConstants.NS_SML;
    }

    @Override
    protected boolean convertEncodedProcedure() {
        return false;
    }

    @Override
    protected void addObservationType(OMObservationType xbObservation, String observationType) {
        xbObservation.addNewType().setHref(getObservationType());
    }

    @Override
    public void addNamespacePrefixToMap(Map<String, String> nameSpacePrefixMap) {
        super.addNamespacePrefixToMap(nameSpacePrefixMap);
        nameSpacePrefixMap.put(InspireBaseConstants.NS_BASE, InspireBaseConstants.NS_BASE_PREFIX);
        nameSpacePrefixMap.put(InspireOMORConstants.NS_OMOR_30, InspireOMORConstants.NS_OMOR_PREFIX);
        nameSpacePrefixMap.put(InspireOMSOConstants.NS_OMSO_30, InspireOMSOConstants.NS_OMSO_PREFIX);
    }

    protected abstract String getObservationType();

}
