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

import java.util.Map;
import java.util.Set;

/**
 * @since 4.0.0
 * 
 */
public interface Profile {

    String getIdentifier();

    boolean isActiveProfile();

    String getObservationResponseFormat();

    boolean isEncodeFeatureOfInterestInObservations();

    String getEncodingNamespaceForFeatureOfInterest();

    boolean isShowMetadataOfEmptyObservations();

    boolean isAllowSubsettingForSOS20OM20();

    boolean isMergeValues();

    boolean isSetEncodeFeatureOfInterestNamespace();

    boolean isEncodeProcedureInObservation();

    boolean isEncodeProcedureInObservation(String namespace);

    boolean isReturnLatestValueIfTemporalFilterIsMissingInGetObservation();

    Map<String, String> getDefaultObservationTypesForEncoding();

    boolean isListFeatureOfInterestsInOfferings();

    boolean isEncodeChildProcedureDescriptions();

    boolean isShowFullOperationsMetadata();

    boolean isShowFullOperationsMetadataForObservations();

    String getResponseNoDataPlaceholder();

    Set<String> getNoDataPlaceholder();

    boolean isSetNoDataPlaceholder();
}
