/*
 * Copyright (C) 2012-2022 52°North Initiative for Geospatial Open Source
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
package org.n52.sos.service.profile;

import org.n52.shetland.inspire.ompr.InspireOMPRConstants;
import org.n52.shetland.inspire.omso.InspireOMSOConstants;
import org.n52.shetland.ogc.om.OmConstants;
import org.n52.shetland.ogc.om.series.wml.WaterMLConstants;

public interface Profiles {

    String DEFAULT_RESPONSE_NODATA_PLACEHOLDER = "noData";

    default Profile createSosProfile() {
        return new Profile("SOS_20_PROFILE").setActiveProfile(true)
                .setObservationResponseFormat(OmConstants.NS_OM_2)
                .setEncodeFeatureOfInterestInObservations(false)
                .setEncodingNamespaceForFeatureOfInterest("")
                .setShowMetadataOfEmptyObservations(false)
                .setListFeatureOfInterestsInOfferings(true)
                .setEncodeChildProcedureDescriptions(false)
                .setShowFullOperationsMetadata(true)
                .setShowFullOperationsMetadataForObservations(true)
                .setAllowSubsettingForSOS20OM20(false)
                .setMergeValues(false)
                .setResponseNoDataPlaceholder(DEFAULT_RESPONSE_NODATA_PLACEHOLDER)
                .addNoDataPlaceholder(DEFAULT_RESPONSE_NODATA_PLACEHOLDER)
                .setReturnLatestValueIfTemporalFilterIsMissingInGetObservation(false);
    }

    default Profile createHydrologyofile() {
        return new Profile("hydrology").setActiveProfile(false)
                .setObservationResponseFormat(WaterMLConstants.NS_WML_20)
                .setEncodeFeatureOfInterestInObservations(false)
                .setEncodingNamespaceForFeatureOfInterest(WaterMLConstants.NS_WML_20)
                .setShowMetadataOfEmptyObservations(true)
                .setListFeatureOfInterestsInOfferings(true)
                .setEncodeChildProcedureDescriptions(false)
                .setShowFullOperationsMetadata(true)
                .setShowFullOperationsMetadataForObservations(true)
                .setAllowSubsettingForSOS20OM20(true)
                .setMergeValues(true)
                .setResponseNoDataPlaceholder(DEFAULT_RESPONSE_NODATA_PLACEHOLDER)
                .addNoDataPlaceholder(DEFAULT_RESPONSE_NODATA_PLACEHOLDER)
                .setReturnLatestValueIfTemporalFilterIsMissingInGetObservation(true)
                .addEncodeProcedureInObservation(WaterMLConstants.NS_WML_20_PROCEDURE_ENCODING, true)
                .addObservationTypesForEncoding(WaterMLConstants.NS_WML_20,
                        WaterMLConstants.OBSERVATION_TYPE_MEASURMENT_TVP);
    }

    default Profile createInspireProfile() {
        return new Profile("inspire").setActiveProfile(false)
                .setObservationResponseFormat(InspireOMSOConstants.NS_OMSO_30)
                .setEncodeFeatureOfInterestInObservations(true)
                .setShowMetadataOfEmptyObservations(false)
                .setListFeatureOfInterestsInOfferings(true)
                .setEncodeChildProcedureDescriptions(false)
                .setShowFullOperationsMetadata(true)
                .setShowFullOperationsMetadataForObservations(true)
                .setAllowSubsettingForSOS20OM20(false)
                .setMergeValues(false)
                .setResponseNoDataPlaceholder(DEFAULT_RESPONSE_NODATA_PLACEHOLDER)
                .addNoDataPlaceholder(DEFAULT_RESPONSE_NODATA_PLACEHOLDER)
                .setReturnLatestValueIfTemporalFilterIsMissingInGetObservation(false)
                .addEncodeProcedureInObservation(InspireOMPRConstants.NS_OMPR_30, true)
                .addObservationTypesForEncoding(InspireOMSOConstants.NS_OMSO_30,
                        InspireOMSOConstants.OBS_TYPE_POINT_OBSERVATION);
    }
}
