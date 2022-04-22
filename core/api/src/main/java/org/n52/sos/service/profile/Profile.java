/*
 * Copyright (C) 2012-2022 52°North Spatial Information Research GmbH
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

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * @since 4.0.0
 *
 */
public class Profile {

    private Set<String> noDataPlaceholder = new HashSet<String>();

    private Map<String, Boolean> encodeProcedureInObservation = new HashMap<String, Boolean>(0);

    private Map<String, String> observationTypesForEncoding = new HashMap<String, String>(0);

    private final String identifier;

    private boolean active;

    private String definition;

    private String observationResponseFormat;

    private boolean encodeFeatureOfInterestInObservations;

    private String encodingNamespaceForFeatureOfInterest;

    private boolean showMetadataOfEmptyObservations;

    private boolean allowSubsettingForSOS20OM20;

    private boolean mergeValues;

    private boolean returnLatestValueIfTemporalFilterIsMissingInGetObservation;

    private String responseNoDataPlaceholder;

    private boolean listFeatureOfInterestsInOfferings;

    private boolean encodeChildProcedureDescriptions;

    private boolean showFullOperationsMetadata;

    private boolean showFullOperationsMetadataForObservations;

    public Profile(String identifier) {
        this.identifier = identifier;
    }

    public String getIdentifier() {
        return identifier;
    }

    public boolean isActiveProfile() {
        return active;
    }

    public Profile setActiveProfile(boolean active) {
        this.active = active;
        return this;
    }

    public Profile setDefinition(String definition) {
        this.definition = definition;
        return this;
    }

    public String getDefinition() {
        return definition;
    }

    public boolean isSetDefinition() {
        return getDefinition() != null && !getDefinition().isEmpty();
    }

    public Profile setObservationResponseFormat(String observationResponseFormat) {
        this.observationResponseFormat = observationResponseFormat;
        return this;
    }

    public String getObservationResponseFormat() {
        return observationResponseFormat;
    }

    public Profile setEncodeFeatureOfInterestInObservations(boolean encodeFeatureOfInterestInObservations) {
        this.encodeFeatureOfInterestInObservations = encodeFeatureOfInterestInObservations;
        return this;
    }

    public boolean isEncodeFeatureOfInterestInObservations() {
        return encodeFeatureOfInterestInObservations;
    }

    public Profile setEncodingNamespaceForFeatureOfInterest(String encodingNamespaceForFeatureOfInterest) {
        this.encodingNamespaceForFeatureOfInterest = encodingNamespaceForFeatureOfInterest;
        return this;
    }

    public String getEncodingNamespaceForFeatureOfInterest() {
        return encodingNamespaceForFeatureOfInterest;
    }

    public Profile setShowMetadataOfEmptyObservations(boolean showMetadataOfEmptyObservations) {
        this.showMetadataOfEmptyObservations = showMetadataOfEmptyObservations;
        return this;
    }

    public boolean isShowMetadataOfEmptyObservations() {
        return showMetadataOfEmptyObservations;
    }

    public Profile setAllowSubsettingForSOS20OM20(boolean allowSubsettingForSOS20OM20) {
        this.allowSubsettingForSOS20OM20 = allowSubsettingForSOS20OM20;
        return this;
    }

    public boolean isAllowSubsettingForSOS20OM20() {
        return allowSubsettingForSOS20OM20;
    }

    public Profile setMergeValues(boolean mergeValues) {
        this.mergeValues = mergeValues;
        return this;
    }

    public boolean isMergeValues() {
        return mergeValues;
    }

    public boolean isSetEncodeFeatureOfInterestNamespace() {
        return false;
    }

    public boolean isEncodeProcedureInObservation() {
        return encodeProcedureInObservation != null && !encodeProcedureInObservation.isEmpty();
    }

    public boolean isEncodeProcedureInObservation(String namespace) {
        if (encodeProcedureInObservation.get(namespace) != null) {
            return encodeProcedureInObservation.get(namespace);
        }
        return false;
    }

    public Profile addEncodeProcedureInObservation(String namespace, Boolean encode) {
        if (namespace != null && !namespace.isEmpty()) {
            this.encodeProcedureInObservation.put(namespace, encode != null ? encode : false);
        }
        return this;
    }

    private Profile addEncodeProcedureInObservation(Map<String, Boolean> encodeProcedureInObservation) {
        if (encodeProcedureInObservation != null && !encodeProcedureInObservation.isEmpty()) {
            encodeProcedureInObservation.entrySet()
                    .forEach(e -> addEncodeProcedureInObservation(e.getKey(), e.getValue()));
        }
        return null;
    }

    public Profile setEncodeProcedureInObservation(Map<String, Boolean> encodeProcedureInObservation) {
        this.encodeProcedureInObservation.clear();
        return addEncodeProcedureInObservation(encodeProcedureInObservation);
    }

    public Map<String, Boolean> getEncodeProcedureInObservation() {
        return Collections.unmodifiableMap(encodeProcedureInObservation);
    }

    public Profile setReturnLatestValueIfTemporalFilterIsMissingInGetObservation(boolean value) {
        this.returnLatestValueIfTemporalFilterIsMissingInGetObservation = value;
        return this;
    }

    public boolean isReturnLatestValueIfTemporalFilterIsMissingInGetObservation() {
        return returnLatestValueIfTemporalFilterIsMissingInGetObservation;
    }

    public Profile addObservationTypesForEncoding(String encoding, String observationType) {
        if (encoding != null && !encoding.isEmpty() && observationType != null && !observationType.isEmpty()) {
            this.observationTypesForEncoding.put(encoding, observationType);
        }
        return this;
    }

    private Profile addObservationTypesForEncoding(Map<String, String> observationTypesForEncoding) {
        if (observationTypesForEncoding != null && !observationTypesForEncoding.isEmpty()) {
            observationTypesForEncoding.entrySet()
                    .forEach(e -> addObservationTypesForEncoding(e.getKey(), e.getValue()));
        }
        return null;
    }

    public Profile setObservationTypesForEncoding(Map<String, String> observationTypesForEncoding) {
        this.observationTypesForEncoding.clear();
        return addObservationTypesForEncoding(observationTypesForEncoding);
    }

    public Map<String, String> getObservationTypesForEncoding() {
        return Collections.unmodifiableMap(observationTypesForEncoding);
    }

    public boolean isObservationTypesForEncoding() {
        return getObservationTypesForEncoding() != null && !getObservationTypesForEncoding().isEmpty();
    }

    public Profile setListFeatureOfInterestsInOfferings(boolean listFeatureOfInterestsInOfferings) {
        this.listFeatureOfInterestsInOfferings = listFeatureOfInterestsInOfferings;
        return this;
    }

    public boolean isListFeatureOfInterestsInOfferings() {
        return listFeatureOfInterestsInOfferings;
    }

    public Profile setEncodeChildProcedureDescriptions(boolean encodeChildProcedureDescriptions) {
        this.encodeChildProcedureDescriptions = encodeChildProcedureDescriptions;
        return this;
    }

    public boolean isEncodeChildProcedureDescriptions() {
        return encodeChildProcedureDescriptions;
    }

    public Profile setShowFullOperationsMetadata(boolean showFullOperationsMetadata) {
        this.showFullOperationsMetadata = showFullOperationsMetadata;
        return this;
    }

    public boolean isShowFullOperationsMetadata() {
        return showFullOperationsMetadata;
    }

    public Profile setShowFullOperationsMetadataForObservations(boolean showFullOperationsMetadataForObservations) {
        this.showFullOperationsMetadataForObservations = showFullOperationsMetadataForObservations;
        return this;
    }

    public boolean isShowFullOperationsMetadataForObservations() {
        return showFullOperationsMetadataForObservations;
    }

    public Profile setResponseNoDataPlaceholder(String responseNoDataPlaceholder) {
        this.responseNoDataPlaceholder = responseNoDataPlaceholder;
        return this;
    }

    public String getResponseNoDataPlaceholder() {
        return responseNoDataPlaceholder;
    }

    public boolean isSetResponseNoDataPlaceholder() {
        return getResponseNoDataPlaceholder() != null && !getResponseNoDataPlaceholder().isEmpty();
    }

    public Profile addNoDataPlaceholder(String noDataPlaceholder) {
        if (noDataPlaceholder != null && !noDataPlaceholder.isEmpty()) {
            this.noDataPlaceholder.add(noDataPlaceholder);
        }
        return this;
    }

    public Profile addNoDataPlaceholder(Collection<String> noDataPlaceholder) {
        if (noDataPlaceholder != null && !noDataPlaceholder.isEmpty()) {
            noDataPlaceholder.forEach(n -> addNoDataPlaceholder(n));
        }
        return this;
    }

    public Profile setNoDataPlaceholder(Collection<String> noDataPlaceholder) {
        this.noDataPlaceholder.clear();
        return addNoDataPlaceholder(noDataPlaceholder);
    }

    public Set<String> getNoDataPlaceholder() {
        return Collections.unmodifiableSet(noDataPlaceholder);
    }

    public boolean isSetNoDataPlaceholder() {
        return getNoDataPlaceholder() != null && !getNoDataPlaceholder().isEmpty();
    }

}
