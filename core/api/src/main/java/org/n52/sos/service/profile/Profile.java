/*
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
package org.n52.sos.service.profile;

import java.util.Map;
import java.util.Set;

import com.google.common.base.Strings;

/**
 * @since 4.0.0
 *
 */
public interface Profile {

    String getIdentifier();

    boolean isActiveProfile();

    void setActiveProfile(boolean active);

    String getObservationResponseFormat();

    boolean isEncodeFeatureOfInterestInObservations();

    String getEncodingNamespaceForFeatureOfInterest();

    boolean isShowMetadataOfEmptyObservations();

    boolean isAllowSubsettingForSOS20OM20();

    boolean isMergeValues();

    boolean isSetEncodeFeatureOfInterestNamespace();

    default boolean isEncodeProcedureInObservation() {
        return getEncodeProcedureInObservation() != null && !getEncodeProcedureInObservation().isEmpty();
    }

    boolean isEncodeProcedureInObservation(String namespace);

    Map<String, Boolean> getEncodeProcedureInObservation();

    boolean isReturnLatestValueIfTemporalFilterIsMissingInGetObservation();

    Map<String, String> getDefaultObservationTypesForEncoding();

    default boolean isSetDefaultObservationTypesForEncoding() {
        return getDefaultObservationTypesForEncoding() != null && !getDefaultObservationTypesForEncoding().isEmpty();
    }

    boolean isListFeatureOfInterestsInOfferings();

    boolean isEncodeChildProcedureDescriptions();

    boolean isShowFullOperationsMetadata();

    boolean isShowFullOperationsMetadataForObservations();

    String getResponseNoDataPlaceholder();

    default boolean isSetResponseNoDataPlaceholder() {
        return !Strings.isNullOrEmpty(getResponseNoDataPlaceholder());
    }

    Set<String> getNoDataPlaceholder();

    default boolean isSetNoDataPlaceholder() {
        return getNoDataPlaceholder() != null && !getNoDataPlaceholder().isEmpty();
    }

    void setDefinition(String definition);

    String getDefinition();

    default boolean isSetDefinition() {
        return !Strings.isNullOrEmpty(getDefinition());
    }
}
