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
package org.n52.sos.profile;

public interface ProfileCoding {

    String IDENTIFIER = "identifier";
    String ACTIVE = "active";
    String DEFINITION = "definition";
    String OBSERVATION_RESPONSE_FORMAT = "observationResponseFormat";
    String ENCODE_FOI_IN_OBS = "encodeFeatureOfInterestInObservations";
    String ENCODE_NAMESPACE_FOIS = "encodingNamespaceForFeatureOfInterestEncoding";
    String SHOW_METADATA_OF_EMPTY_OBS = "showMetadataOfEmptyObservations";
    String LIST_FOIS_IN_OFFERINGS = "listFeatureOfInterestsInOfferings";
    String ENCODE_CHILD_PROCS = "encodeChildProcedureDescriptions";
    String SHOW_FULL_OPS_METADATA = "showFullOperationsMetadata";
    String SHOW_FULL_OPS_METADATA_FOR_OBS = "showFullOperationsMetadataForObservations";
    String ALLOW_SUBSETTING = "allowSubsettingForSOS20OM20";
    String MERGE_VALUES = "mergeValues";
    String NO_DATA_PLACEHOLDER = "NoDataPlaceholder";
    String RESPONSE_PLACEHOLDER = "responsePlaceholder";
    String PLACEHOLDER = "placeholder";
    String RETURN_LATEST_VALUE = "returnLatestValueIfTemporalFilterIsMissingInGetObservation";
    String ENCODE_PROCEDURE = "EncodeProcedure";
    String NAMESPACE = "namespace";
    String ENCCODE = "encode";
    String DEFAULT_OBS_TYPE_FOR_ENCODING = "DefaultObservationTypesForEncoding";
    String OBS_TYPE = "observationType";
    String PROFILES = "profiles";
}
