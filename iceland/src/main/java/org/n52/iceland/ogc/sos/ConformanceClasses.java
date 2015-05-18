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
package org.n52.iceland.ogc.sos;

/**
 * @author Christian Autermann <c.autermann@52north.org>
 * 
 * @since 4.0.0
 */
public interface ConformanceClasses {

    String SOS_V2_CORE_PROFILE = "http://www.opengis.net/spec/SOS/2.0/conf/core";

    String SOS_V2_SOAP_BINDING = "http://www.opengis.net/spec/SOS/2.0/conf/soap";

    String SOS_V2_KVP_CORE_BINDING = "http://www.opengis.net/spec/SOS/2.0/conf/kvp-core";

    String SOS_V2_POX_BINDING = "http://www.opengis.net/spec/SOS/2.0/conf/pox";

    String SOS_V2_FEATURE_OF_INTEREST_RETRIEVAL = "http://www.opengis.net/spec/SOS/2.0/conf/foiRetrieval";

    String SOS_V2_OBSERVATION_BY_ID_RETRIEVAL = "http://www.opengis.net/spec/SOS/2.0/conf/obsByIdRetrieval";

    String SOS_V2_SENSOR_INSERTION = "http://www.opengis.net/spec/SOS/2.0/conf/sensorInsertion";

    String SOS_V2_SENSOR_DELETION = "http://www.opengis.net/spec/SOS/2.0/conf/sensorDeletion";

    String SOS_V2_UPDATE_SENSOR_DESCRIPTION = "http://www.opengis.net/spec/SOS/2.0/conf/updateSensorDescription";

    String SOS_V2_INSERTION_CAPABILITIES = "http://www.opengis.net/spec/SOS/2.0/conf/insertionCap";

    String SOS_V2_OBSERVATION_INSERTION = "http://www.opengis.net/spec/SOS/2.0/conf/obsInsertion";

    String SOS_V2_RESULT_RETRIEVAL = "http://www.opengis.net/spec/SOS/2.0/conf/resultRetrieval";

    String SOS_V2_RESULT_INSERTION = "http://www.opengis.net/spec/SOS/2.0/conf/resultInsertion";

    String SOS_V2_SPATIAL_FILTERING_PROFILE = "http://www.opengis.net/spec/SOS/2.0/conf/spatialFilteringProfile";

    String OM_V2_TEXT_OBSERVATION = "http://www.opengis.net/spec/OMXML/2.0/conf/textObservation";

    String OM_V2_TRUTH_OBSERVATION = "http://www.opengis.net/spec/OMXML/2.0/conf/truthObservation";

    String OM_V2_MEASUREMENT = "http://www.opengis.net/spec/OMXML/2.0/conf/measurement";

    String OM_V2_GEOMETRY_OBSERVATION = "http://www.opengis.net/spec/OMXML/2.0/conf/geometryObservation";

    String OM_V2_COUNT_OBSERVATION = "http://www.opengis.net/spec/OMXML/2.0/conf/countObservation";

    String OM_V2_CATEGORY_OBSERVATION = "http://www.opengis.net/spec/OMXML/2.0/conf/categoryObservation";

    String OM_V2_SAMPLING_POINT = "http://www.opengis.net/spec/OMXML/2.0/conf/samplingPoint";

    String OM_V2_SAMPLING_CURVE = "http://www.opengis.net/spec/OMXML/2.0/conf/samplingCurve";

    String OM_V2_SAMPLING_SURFACE = "http://www.opengis.net/spec/OMXML/2.0/conf/samplingSurface";

    String OM_V2_SPATIAL_SAMPLING = "http://www.opengis.net/spec/OMXML/2.0/conf/spatialSampling";

    String SWE_V2_CORE = "http://www.opengis.net/spec/SWE/2.0/conf/core";

    String SWE_V2_UML_SIMPLE_COMPONENTS = "http://www.opengis.net/spec/SWE/2.0/conf/uml-simple-components";

    String SWE_V2_UML_RECORD_COMPONENTS = "http://www.opengis.net/spec/SWE/2.0/conf/uml-record-components";

    String SWE_V2_UML_BLOCK_ENCODINGS = "http://www.opengis.net/spec/SWE/2.0/conf/uml-block-components";

    String SWE_V2_UML_SIMPLE_ENCODINGS = "http://www.opengis.net/spec/SWE/2.0/conf/uml-simple-encodings";

    String SWE_V2_XSD_SIMPLE_COMPONENTS = "http://www.opengis.net/spec/SWE/2.0/conf/xsd-simple-components";

    String SWE_V2_XSD_RECORD_COMPONENTS = "http://www.opengis.net/spec/SWE/2.0/conf/xsd-record-components";

    String SWE_V2_XSD_BLOCK_COMPONENTS = "http://www.opengis.net/spec/SWE/2.0/conf/xsd-block-components";

    String SWE_V2_XSD_SIMPLE_ENCODINGS = "http://www.opengis.net/spec/SWE/2.0/conf/xsd-simple-encodings";

    String SWE_V2_GENERAL_ENCODING_RULES = "http://www.opengis.net/spec/SWE/2.0/conf/general-encoding-rules";

    String SWE_V2_TEXT_ENCODING_RULES = "http://www.opengis.net/spec/SWE/2.0/conf/text-encoding-rules";

}
