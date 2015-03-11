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

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.n52.sos.service.profile.Profile;
import org.n52.sos.util.JavaHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @since 4.0.0
 * 
 */
public class ProfileImpl implements Profile {

    private static final Logger LOGGER = LoggerFactory.getLogger(ProfileImpl.class.getName());

    private String identifier;

    private boolean activeProfile;

    private String observationResponseFormat;

    /**
     * boolean indicates, whether SOS encodes the complete FOI-instance within
     * the Observation instance or just the FOI id
     */
    private boolean encodeFeatureOfInterestInObservations;

    private String encodingNamespaceForFeatureOfInterestEncoding;

    private boolean showMetadataOfEmptyObservations;

    private boolean listFeatureOfInterestsInOfferings;

    private boolean encodeChildProcedureDescriptions;

    private boolean showFullOperationsMetadata;

    private boolean showFullOperationsMetadataForObservations;

    private boolean allowSubsettingForSOS20OM20;

    private boolean mergeValues;

    private String responseNoDataPlaceholder;

    private Set<String> noDataPlaceholder = new HashSet<String>(0);

    private boolean returnLatestValueIfTemporalFilterIsMissingInGetObservation;

    private Map<String, Boolean> encodeProcedureInObservation = new HashMap<String, Boolean>(0);

    private Map<String, String> defaultObservationTypesForEncoding = new HashMap<String, String>(0);

    public ProfileImpl() {

    }

    @Override
    public String getIdentifier() {
        return identifier;
    }

    @Override
    public boolean isActiveProfile() {
        return activeProfile;
    }

    @Override
    public String getObservationResponseFormat() {
        return observationResponseFormat;
    }

    @Override
    public boolean isEncodeFeatureOfInterestInObservations() {
        return encodeFeatureOfInterestInObservations;
    }

    @Override
    public String getEncodingNamespaceForFeatureOfInterest() {
        return encodingNamespaceForFeatureOfInterestEncoding;
    }

    @Override
    public boolean isShowMetadataOfEmptyObservations() {
        return showMetadataOfEmptyObservations;
    }

    public void setIdentifier(String identifier) {
        if (identifier != null && !identifier.isEmpty()) {
            this.identifier = identifier;
        } else {
            this.identifier = JavaHelper.generateID(Long.toString(System.currentTimeMillis()));
            LOGGER.warn("The identifier is null or empty! This generated identifier {} is set!", this.identifier);
        }
    }

    public void setActiveProfile(boolean activeProfile) {
        this.activeProfile = activeProfile;
    }

    public void setObservationResponseFormat(String observationResponseFormat) {
        if (observationResponseFormat != null && !observationResponseFormat.isEmpty()) {
            this.observationResponseFormat = observationResponseFormat;
        } else {
            LOGGER.warn("The observationResponseFormat is null or empty! Default observationResponseFormat is set!");
        }
    }

    public void setEncodeFeatureOfInterestInObservations(boolean encodeFeatureOfInterestInObservations) {
        this.encodeFeatureOfInterestInObservations = encodeFeatureOfInterestInObservations;
    }

    public void setEncodingNamespaceForFeatureOfInterest(String encodingNamespateForFeatureOfInterestEncoding) {
        if (encodingNamespateForFeatureOfInterestEncoding != null) {
            this.encodingNamespaceForFeatureOfInterestEncoding = encodingNamespateForFeatureOfInterestEncoding;
        } else {
            LOGGER.warn("The encodingNamespaceForFeatureOfInterestEncoding is null! Default encodingNamespaceForFeatureOfInterestEncoding is set!");
        }

    }

    public void setShowMetadataOfEmptyObservations(boolean showMetadataOfEmptyObservations) {
        this.showMetadataOfEmptyObservations = showMetadataOfEmptyObservations;
    }

    @Override
    public boolean isAllowSubsettingForSOS20OM20() {
        return allowSubsettingForSOS20OM20;
    }

    @Override
    public boolean isMergeValues() {
        return mergeValues;
    }

    public void setAllowSubsettingForSOS20OM20(boolean allowSubsetting) {
        this.allowSubsettingForSOS20OM20 = allowSubsetting;
    }

    public void setMergeValues(boolean mergeValues) {
        this.mergeValues = mergeValues;
    }

    @Override
    public boolean isSetEncodeFeatureOfInterestNamespace() {
        return encodingNamespaceForFeatureOfInterestEncoding != null
                && !encodingNamespaceForFeatureOfInterestEncoding.isEmpty();
    }

    @Override
    public boolean isEncodeProcedureInObservation() {
        return encodeProcedureInObservation != null && !encodeProcedureInObservation.isEmpty();
    }

    @Override
    public boolean isEncodeProcedureInObservation(String namespace) {
        Boolean encode = encodeProcedureInObservation.get(namespace);
        if (encode != null) {
            return encode.booleanValue();
        }
        return false;
    }

    public void setEncodeProcedureInObservation(Map<String, Boolean> encodeProcedureInObservation) {
        if (encodeProcedureInObservation != null) {
            this.encodeProcedureInObservation.putAll(encodeProcedureInObservation);
        }
    }

    public void addEncodeProcedureInObservation(String namespace, boolean encode) {
        if (namespace != null && !namespace.isEmpty()) {
            encodeProcedureInObservation.put(namespace, encode);
        }
    }

    @Override
    public boolean isReturnLatestValueIfTemporalFilterIsMissingInGetObservation() {
        return returnLatestValueIfTemporalFilterIsMissingInGetObservation;
    }

    public void setReturnLatestValueIfTemporalFilterIsMissingInGetObservation(
            boolean returnLatestValueIfTemporalFilterIsMissingInGetObservation) {
        this.returnLatestValueIfTemporalFilterIsMissingInGetObservation =
                returnLatestValueIfTemporalFilterIsMissingInGetObservation;
    }

    @Override
    public Map<String, String> getDefaultObservationTypesForEncoding() {
        return defaultObservationTypesForEncoding;
    }

    public void setDefaultObservationTypesForEncoding(Map<String, String> defaultObservationTypesForEncoding) {
        if (defaultObservationTypesForEncoding != null) {
            this.defaultObservationTypesForEncoding = defaultObservationTypesForEncoding;
        }
    }

    public void addDefaultObservationTypesForEncoding(String namespace, String observationType) {
        if (namespace != null && !namespace.isEmpty() && observationType != null && !observationType.isEmpty()) {
            defaultObservationTypesForEncoding.put(namespace, observationType);
        }
    }

    public boolean isListFeatureOfInterestsInOfferings() {
        return listFeatureOfInterestsInOfferings;
    }

    public void setListFeatureOfInterestsInOfferings(boolean listFeatureOfInterestsInOfferings) {
        this.listFeatureOfInterestsInOfferings = listFeatureOfInterestsInOfferings;
    }

    public boolean isEncodeChildProcedureDescriptions() {
        return encodeChildProcedureDescriptions;
    }

    public void setEncodeChildProcedureDescriptions(boolean encodeChildProcedureDescriptions) {
        this.encodeChildProcedureDescriptions = encodeChildProcedureDescriptions;
    }

    public boolean isShowFullOperationsMetadata() {
        return showFullOperationsMetadata;
    }

    public void setShowFullOperationsMetadata(boolean showFullOperationsMetadata) {
        this.showFullOperationsMetadata = showFullOperationsMetadata;
    }

    public boolean isShowFullOperationsMetadataForObservations() {
        return showFullOperationsMetadataForObservations;
    }

    public void setShowFullOperationsMetadataForObservations(boolean showFullOperationsMetadataForObservations) {
        this.showFullOperationsMetadataForObservations = showFullOperationsMetadataForObservations;
    }

    public String getResponseNoDataPlaceholder() {
        return responseNoDataPlaceholder;
    }

    public void setResponseNoDataPlaceholder(String responseNoDataPlaceholder) {
        this.responseNoDataPlaceholder = responseNoDataPlaceholder;
    }

    public Set<String> getNoDataPlaceholder() {
        return noDataPlaceholder;
    }

    public void setNoDataPlaceholder(Set<String> noDataPlaceholder) {
        this.noDataPlaceholder = noDataPlaceholder;
    }

    @Override
    public boolean isSetNoDataPlaceholder() {
        return noDataPlaceholder != null && !noDataPlaceholder.isEmpty();
    }

}
