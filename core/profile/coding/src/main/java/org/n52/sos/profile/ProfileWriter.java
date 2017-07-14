package org.n52.sos.profile;

import java.util.Collection;
import java.util.Map.Entry;

import org.n52.janmayen.Json;
import org.n52.sos.service.profile.Profile;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

public class ProfileWriter implements ProfileCoding {

    public JsonNode write(Collection<Profile> values) {
        ObjectNode json = Json.nodeFactory().objectNode();
        ArrayNode profiles = json.putArray(PROFILES);
        for (Profile profile : values) {
            ObjectNode node = profiles.addObject();
            writeIdentifier(profile, node);
            writeActiveProfile(profile, node);
            writeDefinition(profile, node);
            writeListFeatureOfInterestsInOfferings(profile, node);
            writeEncodeChildProcedureDescriptions(profile, node);
            writeShowFullOperationsMetadata(profile, node);
            writeShowFullOperationsMetadataForObservations(profile, node);
            writeAllowSubsettingForSOS20OM20(profile, node);
            writeEncodeFeatureOfInterestInObservations(profile, node);
            writeEncodingNamespaceForFeatureOfInterestEncoding(profile, node);
            writeMergeValues(profile, node);
            writeObservationResponseFormat(profile, node);
            writeNoDataPlaceholder(profile, node);
            writeReturnLatestValueIfTemporalFilterIsMissingInGetObservation(profile, node);
            writeShowMetadataOfEmptyObservations(profile, node);
            writeDefaultObservationTypesForEncoding(profile,  node);
            writeEncodeProcedure(profile, node);
        }
        return json;
    }

    private void writeIdentifier(Profile profile, ObjectNode node) {
        node.put(IDENTIFIER, profile.getIdentifier());

    }

    private void writeActiveProfile(Profile profile, ObjectNode node) {
        node.put(ACTIVE, profile.isActiveProfile());
    }

    private void writeDefinition(Profile profile, ObjectNode node) {
        if (profile.isSetDefinition()) {
            node.put(DEFINITION, profile.getDefinition());
        }
    }

    private void writeListFeatureOfInterestsInOfferings(Profile profile, ObjectNode node) {
        node.put(LIST_FOIS_IN_OFFERINGS, profile.isListFeatureOfInterestsInOfferings());
    }

    private void writeEncodeChildProcedureDescriptions(Profile profile, ObjectNode node) {
        node.put(ENCODE_CHILD_PROCS, profile.isEncodeChildProcedureDescriptions());
    }

    private void writeShowFullOperationsMetadata(Profile profile, ObjectNode node) {
        node.put(SHOW_FULL_OPS_METADATA, profile.isShowFullOperationsMetadata());
    }

    private void writeShowFullOperationsMetadataForObservations(Profile profile, ObjectNode node) {
        node.put(SHOW_FULL_OPS_METADATA_FOR_OBS, profile.isShowFullOperationsMetadataForObservations());
    }

    private void writeAllowSubsettingForSOS20OM20(Profile profile, ObjectNode node) {
        node.put(ALLOW_SUBSETTING, profile.isAllowSubsettingForSOS20OM20());
    }

    private void writeEncodeFeatureOfInterestInObservations(Profile profile, ObjectNode node) {
        node.put(ENCODE_FOI_IN_OBS, profile.isEncodeFeatureOfInterestInObservations());
    }

    private void writeEncodingNamespaceForFeatureOfInterestEncoding(Profile profile, ObjectNode node) {
        node.put(ENCODE_NAMESPACE_FOIS, profile.getEncodingNamespaceForFeatureOfInterest());
    }

    private void writeMergeValues(Profile profile, ObjectNode node) {
        node.put(MERGE_VALUES, profile.isMergeValues());
    }

    private void writeObservationResponseFormat(Profile profile, ObjectNode node) {
        node.put(OBSERVATION_RESPONSE_FORMAT, profile.getObservationResponseFormat());
    }

    private void writeNoDataPlaceholder(Profile profile, ObjectNode objectNode) {
        if (profile.isSetNoDataPlaceholder() || profile.isSetResponseNoDataPlaceholder()) {
            ObjectNode node = objectNode.putObject(NO_DATA_PLACEHOLDER);
            if (profile.isSetResponseNoDataPlaceholder()) {
                node.put(RESPONSE_PLACEHOLDER, profile.getResponseNoDataPlaceholder());
            }
            if (profile.getNoDataPlaceholder().size() == 1) {
                for (String v : profile.getNoDataPlaceholder()) {
                    node.put(PLACEHOLDER, v);
                }
            } else {
                ArrayNode arrayNode = node.putArray(PLACEHOLDER);
                for (String v : profile.getNoDataPlaceholder()) {
                    arrayNode.add(v);
                }
            }
        }
    }

    private void writeReturnLatestValueIfTemporalFilterIsMissingInGetObservation(Profile profile, ObjectNode node) {
        node.put(RETURN_LATEST_VALUE, profile.isReturnLatestValueIfTemporalFilterIsMissingInGetObservation());
    }

    private void writeShowMetadataOfEmptyObservations(Profile profile, ObjectNode node) {
        node.put(SHOW_METADATA_OF_EMPTY_OBS, profile.isShowMetadataOfEmptyObservations());
    }

    private void writeDefaultObservationTypesForEncoding(Profile profile, ObjectNode objectNode) {
        if (profile.isSetDefaultObservationTypesForEncoding()) {
            ObjectNode node = objectNode.putObject(DEFAULT_OBS_TYPE_FOR_ENCODING);
            if (profile.getDefaultObservationTypesForEncoding().size() == 1) {
                for (Entry<String, String> entry : profile.getDefaultObservationTypesForEncoding().entrySet()) {
                    node.put(NAMESPACE, entry.getKey());
                    node.put(OBS_TYPE, entry.getValue());
                }
            } else {
                ArrayNode arrayNode = node.arrayNode();
                for (Entry<String, String> entry : profile.getDefaultObservationTypesForEncoding().entrySet()) {
                    ObjectNode addObject = arrayNode.addObject();
                    addObject.put(NAMESPACE, entry.getKey());
                    addObject.put(OBS_TYPE, entry.getValue());
                }
            }
        }
    }

    private void writeEncodeProcedure(Profile profile, ObjectNode objectNode) {
        if (profile.isEncodeProcedureInObservation()) {
            ObjectNode node = objectNode.putObject(ENCODE_PROCEDURE);
            if (profile.getEncodeProcedureInObservation().size() == 1) {
                for (Entry<String, Boolean> entry : profile.getEncodeProcedureInObservation().entrySet()) {
                    node.put(NAMESPACE, entry.getKey());
                    node.put(ENCCODE, entry.getValue());
                }
            } else {
                ArrayNode arrayNode = node.arrayNode();
                for (Entry<String, Boolean> entry : profile.getEncodeProcedureInObservation().entrySet()) {
                    ObjectNode addObject = arrayNode.addObject();
                    addObject.put(NAMESPACE, entry.getKey());
                    addObject.put(ENCCODE, entry.getValue());
                }
            }
        }
    }

}
