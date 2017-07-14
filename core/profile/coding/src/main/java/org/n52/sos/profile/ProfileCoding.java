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
    String SHOW_FULL_OPS_METADATA ="showFullOperationsMetadata";
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
