/**
 * Copyright 2015 52°North Initiative for Geospatial Open Source
 * Software GmbH
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.n52.iceland.service.profile;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.n52.iceland.ogc.om.OmConstants;

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
