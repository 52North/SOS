/*
 * Copyright (C) 2012-2018 52°North Initiative for Geospatial Open Source
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

import java.util.Set;

import org.n52.sos.service.profile.Profile;

import com.fasterxml.jackson.databind.JsonNode;
import com.google.common.collect.Sets;

/*
 * FIXME why is this class a helper class?
 */
/**
 * @since 4.0.0
 *
 */
public class ProfileParser {

    private static final String IDENTIFIER = "identifier";
    private static final String ACTIVE = "active";
    private static final String OBSERVATION_RESPONSE_FORMAT = "observationResponseFormat";
    private static final String ENCODE_FOI_IN_OBS = "encodeFeatureOfInterestInObservations";
    private static final String ENCODE_NAMESPACE_FOIS = "encodingNamespaceForFeatureOfInterestEncoding";
    private static final String SHOW_METADATA_OF_EMPTY_OBS = "showMetadataOfEmptyObservations";
    private static final String LIST_FOIS_IN_OFFERINGS = "listFeatureOfInterestsInOfferings";
    private static final String ENCODE_CHILD_PROCS = "encodeChildProcedureDescriptions";
    private static final String SHOW_FULL_OPS_METADATA ="showFullOperationsMetadata";
    private static final String SHOW_FULL_OPS_METADATA_FOR_OBS = "showFullOperationsMetadataForObservations";
    private static final String ALLOW_SUBSETTING = "allowSubsettingForSOS20OM20";
    private static final String MERGE_VALUES = "mergeValues";
    private static final String NO_DATA_PLACEHOLDER = "NoDataPlaceholder";
    private static final String RESPONSE_PLACEHOLDER = "responsePlaceholder";
    private static final String PLACEHOLDER = "placeholder";
    private static final String RETURN_LATEST_VALUE = "returnLatestValueIfTemporalFilterIsMissingInGetObservation";

    private static final String ENCODE_PROCEDURE = "EncodeProcedure";
    private static final String NAMESPACE = "namespace";
    private static final String ENCCODE = "encode";

    private static final String DEFAULT_OBS_TYPE_FOR_ENCODING = "DefaultObservationTypesForEncoding";
    private static final String  OBS_TYPE = "observationType";

    public Profile parseSosProfile(JsonNode node) {
        ProfileImpl profile = new ProfileImpl();
        profile.setIdentifier(parseIdentifier(node));
        profile.setActiveProfile(parseActiveProfile(node));
        profile.setListFeatureOfInterestsInOfferings(parseListFeatureOfInterestsInOfferings(node));
        profile.setEncodeChildProcedureDescriptions(parseEncodeChildProcedureDescriptions(node));
        profile.setShowFullOperationsMetadata(parseShowFullOperationsMetadata(node));
        profile.setShowFullOperationsMetadataForObservations(parseShowFullOperationsMetadataForObservations(node));
        profile.setAllowSubsettingForSOS20OM20(parseAllowSubsettingForSOS20OM20(node));
        profile.setEncodeFeatureOfInterestInObservations(parseEncodeFeatureOfInterestInObservations(node));
        profile.setEncodingNamespaceForFeatureOfInterest(parseEncodingNamespaceForFeatureOfInterestEncoding(node));
        profile.setMergeValues(parseMergeValues(node));
        profile.setObservationResponseFormat(parseObservationResponseFormat(node));
        parseNoDataPlaceholder(profile, node);
        profile.setReturnLatestValueIfTemporalFilterIsMissingInGetObservation(parseReturnLatestValueIfTemporalFilterIsMissingInGetObservation(node));
        profile.setShowMetadataOfEmptyObservations(parseShowMetadataOfEmptyObservations(node));
        parseDefaultObservationTypesForEncoding(profile, node);
        parseEncodeProcedure(profile, node);
        return profile;
    }

    private String parseText(JsonNode node) {
        return node.textValue();
    }

    private boolean parseBoolean(JsonNode node) {
        return node.booleanValue();
    }

    private boolean isNotMissingNode(JsonNode node) {
        return !node.isMissingNode();
    }

    private String parseIdentifier(JsonNode node) {
        return parseText(node.path(IDENTIFIER));
    }

    private boolean parseActiveProfile(JsonNode node) {
        return parseBoolean(node.path(ACTIVE));
    }

    private boolean parseListFeatureOfInterestsInOfferings(JsonNode node) {
        return parseBoolean(node.path(LIST_FOIS_IN_OFFERINGS));
    }

    private boolean parseEncodeChildProcedureDescriptions(JsonNode node) {
        return parseBoolean(node.path(ENCODE_CHILD_PROCS));
    }

    private boolean parseShowFullOperationsMetadata(JsonNode node) {
        return parseBoolean(node.path(SHOW_FULL_OPS_METADATA));
    }

    private boolean parseShowFullOperationsMetadataForObservations(JsonNode node) {
        return parseBoolean(node.path(SHOW_FULL_OPS_METADATA_FOR_OBS));
    }

    private boolean parseAllowSubsettingForSOS20OM20(JsonNode node) {
        return parseBoolean(node.path(ALLOW_SUBSETTING));
    }

    private boolean parseEncodeFeatureOfInterestInObservations(JsonNode node) {
        return parseBoolean(node.path(ENCODE_FOI_IN_OBS));
    }

    private String parseEncodingNamespaceForFeatureOfInterestEncoding(JsonNode node) {
        if (isNotMissingNode(node.path(ENCODE_NAMESPACE_FOIS))) {
            return parseText(node.path(ENCODE_NAMESPACE_FOIS));
        }
        return "";
    }

    private String parseObservationResponseFormat(JsonNode node) {
        return parseText(node.path(OBSERVATION_RESPONSE_FORMAT));
    }

    private boolean parseMergeValues(JsonNode node) {
        return parseBoolean(node.path(MERGE_VALUES));
    }

    private void parseNoDataPlaceholder(ProfileImpl profile, JsonNode node) {
        if (isNotMissingNode(node.path(NO_DATA_PLACEHOLDER))) {
            if (isNotMissingNode(node.path(NO_DATA_PLACEHOLDER).path(RESPONSE_PLACEHOLDER))) {
                profile.setResponseNoDataPlaceholder(parseText(node.path(NO_DATA_PLACEHOLDER).path(RESPONSE_PLACEHOLDER)));
            }
            if (isNotMissingNode(node.path(NO_DATA_PLACEHOLDER).path(PLACEHOLDER))) {
                Set<String> placeholder =  Sets.newHashSet();
                JsonNode phNode = node.path(NO_DATA_PLACEHOLDER).path(PLACEHOLDER);
                if (node.path(NO_DATA_PLACEHOLDER).path(PLACEHOLDER).isArray()) {
                    for (int i = 0; i < phNode.size(); i++) {
                        placeholder.add(phNode.get(i).asText());
                    }
                } else {
                    placeholder.add(phNode.asText());
                }
                profile.setNoDataPlaceholder(placeholder);
            }
        }
    }

    private boolean parseShowMetadataOfEmptyObservations(JsonNode node) {
        return parseBoolean(node.path(SHOW_METADATA_OF_EMPTY_OBS));
    }

    private boolean parseReturnLatestValueIfTemporalFilterIsMissingInGetObservation(JsonNode node) {
        return parseBoolean(node.path(RETURN_LATEST_VALUE));
    }

    private void parseEncodeProcedure(ProfileImpl profile, JsonNode node) {
        if (isNotMissingNode(node.path(ENCODE_PROCEDURE))) {
            JsonNode epNode = node.path(ENCODE_PROCEDURE);
            if (epNode.isArray()) {
                for (int i = 0; i < epNode.size(); i++) {
                    parseEncodeProcedureElement(profile, epNode);
                }
            } else {
                parseEncodeProcedureElement(profile, epNode);
            }
        }
    }

    private void parseEncodeProcedureElement(ProfileImpl profile, JsonNode node) {
        profile.addEncodeProcedureInObservation(parseText(node.path(NAMESPACE)), parseBoolean(node.path(ENCCODE)));
    }

    private void parseDefaultObservationTypesForEncoding(ProfileImpl profile, JsonNode node) {
        if (isNotMissingNode(node.path(DEFAULT_OBS_TYPE_FOR_ENCODING))) {
            JsonNode epNode = node.path(DEFAULT_OBS_TYPE_FOR_ENCODING);
            if (epNode.isArray()) {
                for (int i = 0; i < epNode.size(); i++) {
                    parseDefaultObservationTypesForEncodingElement(profile, epNode);
                }
            } else {
                parseDefaultObservationTypesForEncodingElement(profile, epNode);
            }
        }
    }

    private void parseDefaultObservationTypesForEncodingElement(ProfileImpl profile, JsonNode node) {
        profile.addDefaultObservationTypesForEncoding(parseText(node.path(NAMESPACE)), parseText(node.path(OBS_TYPE)));
    }

}
