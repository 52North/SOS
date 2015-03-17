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
package org.n52.sos.profile;

import java.util.Arrays;
import java.util.HashSet;

import org.n52.sos.service.profile.Profile;
import org.x52North.sensorweb.sos.profile.DefaultObservationTypesForEncodingDocument.DefaultObservationTypesForEncoding;
import org.x52North.sensorweb.sos.profile.EncodeProcedureDocument.EncodeProcedure;
import org.x52North.sensorweb.sos.profile.NoDataPlaceholderDocument.NoDataPlaceholder;
import org.x52North.sensorweb.sos.profile.SosProfileDocument;
import org.x52North.sensorweb.sos.profile.SosProfileType;

/*
 * FIXME why is this class a helper class?
 */
/**
 * @since 4.0.0
 * 
 */
public class ProfileParser {

    public static Profile parseSosProfile(SosProfileDocument sosProfileDoc) {
        ProfileImpl profile = new ProfileImpl();
        SosProfileType sosProfile = sosProfileDoc.getSosProfile();
        profile.setIdentifier(sosProfile.getIdentifier());
        profile.setActiveProfile(sosProfile.getActiveProfile());
        profile.setListFeatureOfInterestsInOfferings(sosProfile.getListFeatureOfInterestsInOfferings());
        profile.setEncodeChildProcedureDescriptions(sosProfile.getEncodeChildProcedureDescriptions());
        profile.setShowFullOperationsMetadata(sosProfile.getShowFullOperationsMetadata());
        profile.setShowFullOperationsMetadataForObservations(sosProfile.getShowFullOperationsMetadataForObservations());
        profile.setAllowSubsettingForSOS20OM20(sosProfile.getAllowSubsettingForSOS20OM20());
        profile.setEncodeFeatureOfInterestInObservations(sosProfile.getEncodeFeatureOfInterestInObservations());
        profile.setEncodingNamespaceForFeatureOfInterest(sosProfile.getEncodingNamespaceForFeatureOfInterestEncoding());
        profile.setMergeValues(sosProfile.getMergeValues());
        profile.setObservationResponseFormat(sosProfile.getObservationResponseFormat());
        parseNoDataPlaceholder(profile, sosProfile.getNoDataPlaceholder());
        profile.setReturnLatestValueIfTemporalFilterIsMissingInGetObservation(sosProfile
                .getReturnLatestValueIfTemporalFilterIsMissingInGetObservation());
        profile.setShowMetadataOfEmptyObservations(sosProfile.getShowMetadataOfEmptyObservations());
        if (sosProfile.getDefaultObservationTypesForEncodingArray() != null) {
            parseDefaultObservationTypesForEncoding(profile, sosProfile.getDefaultObservationTypesForEncodingArray());
        }
        if (sosProfile.getEncodeProcedureArray() != null) {
            parseEncodeProcedure(profile, sosProfile.getEncodeProcedureArray());
        }
        if (sosProfile.isSetEncodingNamespaceForFeatureOfInterestEncoding()) {
            profile.setEncodingNamespaceForFeatureOfInterest(sosProfile
                    .getEncodingNamespaceForFeatureOfInterestEncoding());
        }

        return profile;
    }

    private static void parseNoDataPlaceholder(ProfileImpl profile, NoDataPlaceholder noDataPlaceholder) {
        if (noDataPlaceholder.getResponsePlaceholder() != null
                && !noDataPlaceholder.getResponsePlaceholder().isEmpty()) {
            profile.setResponseNoDataPlaceholder(noDataPlaceholder.getResponsePlaceholder());
        }
        if (noDataPlaceholder.getPlaceholderArray() != null && noDataPlaceholder.getPlaceholderArray().length > 0) {
            profile.setNoDataPlaceholder(new HashSet<String>(Arrays.asList(noDataPlaceholder.getPlaceholderArray())));
        }

    }

    private static void parseEncodeProcedure(ProfileImpl profile, EncodeProcedure[] encodeProcedureArray) {
        for (EncodeProcedure encodeProcedure : encodeProcedureArray) {
            profile.addEncodeProcedureInObservation(encodeProcedure.getNamespace(), encodeProcedure.getEncode());
        }

    }

    private static void parseDefaultObservationTypesForEncoding(ProfileImpl profile,
            DefaultObservationTypesForEncoding[] defaultObservationTypesForEncodingArray) {
        for (DefaultObservationTypesForEncoding defaultObservationTypesForEncoding : defaultObservationTypesForEncodingArray) {
            profile.addDefaultObservationTypesForEncoding(defaultObservationTypesForEncoding.getNamespace(),
                    defaultObservationTypesForEncoding.getObservationType());
        }
    }

    private ProfileParser() {
    }
}
