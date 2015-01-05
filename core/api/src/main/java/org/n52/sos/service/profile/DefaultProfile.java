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
package org.n52.sos.service.profile;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.n52.sos.ogc.om.OmConstants;

/**
 * @since 4.0.0
 * 
 */
public class DefaultProfile implements Profile {

    private static final String DEFAULT_IDENTIFIER = "SOS_20_PROFILE";

    private static final String DEFAULT_OBSERVATION_RESPONSE_FORMAT = OmConstants.NS_OM_2;

    private static final String DEFAULT_ENCODING_NAMESPACE_FOR_FEATUTREOFINTEREST_SOS_20 = "";

    private static final boolean DEFAULT_ENCODE_FEATUREOFINTEREST_IN_OBSERVATION = true;

    private static final boolean DEFAULT_SHOW_METADATA_OF_EMPTY_OBSERVATIONS = false;

    private static final boolean DEFAULT_ALLOW_SUBSETTING_FOR_OM_20 = false;

    private static final boolean DEAFULT_MERGE_VALUES = false;

    private static final boolean DEAFULT_RETURN_LATEST_VALUE_IF_TEMPORAL_FILTER_IS_MISSING_IN_GETOBSERVATION = false;

    private static final boolean DEFAULT_LIST_FEATURE_OF_INSEREST_IN_OFFERINGS = true;

    private static final boolean DEFAULT_ENCODE_CHILD_PROCEDURE_DESCRIPTION = false;

    private static final boolean DEFAULT_SHOW_FULL_OPERATIONS_METADATA = false;

    private static final boolean DEFAULT_SHOW_FULL_OPERATIONS_METADATA_FOR_OBSERVATIONS = false;

    private static final String DAFUALT_RESPONSE_NODATA_PLACEHOLDER = "noData";

    private Set<String> defaultNoDataPlaceholder = new HashSet<String>();

    private Map<String, Boolean> encodeProcedureInObservation = new HashMap<String, Boolean>(0);

    private Map<String, String> defaultObservationTypesForEncoding = new HashMap<String, String>(0);

    @Override
    public String getIdentifier() {
        return DEFAULT_IDENTIFIER;
    }

    @Override
    public boolean isActiveProfile() {
        return true;
    }

    @Override
    public String getObservationResponseFormat() {
        return DEFAULT_OBSERVATION_RESPONSE_FORMAT;
    }

    @Override
    public boolean isEncodeFeatureOfInterestInObservations() {
        return DEFAULT_ENCODE_FEATUREOFINTEREST_IN_OBSERVATION;
    }

    @Override
    public String getEncodingNamespaceForFeatureOfInterest() {
        return DEFAULT_ENCODING_NAMESPACE_FOR_FEATUTREOFINTEREST_SOS_20;
    }

    @Override
    public boolean isShowMetadataOfEmptyObservations() {
        return DEFAULT_SHOW_METADATA_OF_EMPTY_OBSERVATIONS;
    }

    @Override
    public boolean isAllowSubsettingForSOS20OM20() {
        return DEFAULT_ALLOW_SUBSETTING_FOR_OM_20;
    }

    @Override
    public boolean isMergeValues() {
        return DEAFULT_MERGE_VALUES;
    }

    @Override
    public boolean isSetEncodeFeatureOfInterestNamespace() {
        return false;
    }

    @Override
    public boolean isEncodeProcedureInObservation() {
        return encodeProcedureInObservation != null && !encodeProcedureInObservation.isEmpty();
    }

    @Override
    public boolean isEncodeProcedureInObservation(String namespace) {
        if (encodeProcedureInObservation.get(namespace) != null) {
            return encodeProcedureInObservation.get(namespace);
        }
        return false;
    }

    @Override
    public boolean isReturnLatestValueIfTemporalFilterIsMissingInGetObservation() {
        return DEAFULT_RETURN_LATEST_VALUE_IF_TEMPORAL_FILTER_IS_MISSING_IN_GETOBSERVATION;
    }

    @Override
    public Map<String, String> getDefaultObservationTypesForEncoding() {
        return defaultObservationTypesForEncoding;
    }

    @Override
    public boolean isListFeatureOfInterestsInOfferings() {
        return DEFAULT_LIST_FEATURE_OF_INSEREST_IN_OFFERINGS;
    }

    @Override
    public boolean isEncodeChildProcedureDescriptions() {
        return DEFAULT_ENCODE_CHILD_PROCEDURE_DESCRIPTION;
    }

    @Override
    public boolean isShowFullOperationsMetadata() {
        return DEFAULT_SHOW_FULL_OPERATIONS_METADATA;
    }

    @Override
    public boolean isShowFullOperationsMetadataForObservations() {
        return DEFAULT_SHOW_FULL_OPERATIONS_METADATA_FOR_OBSERVATIONS;
    }

    @Override
    public String getResponseNoDataPlaceholder() {
        return DAFUALT_RESPONSE_NODATA_PLACEHOLDER;
    }

    @Override
    public Set<String> getNoDataPlaceholder() {
        return defaultNoDataPlaceholder;
    }

    @Override
    public boolean isSetNoDataPlaceholder() {
        return defaultNoDataPlaceholder != null && !defaultNoDataPlaceholder.isEmpty();
    }

}
