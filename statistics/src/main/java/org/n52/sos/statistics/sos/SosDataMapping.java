/*
 * Copyright (C) 2012-2021 52°North Spatial Information Research GmbH
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
package org.n52.sos.statistics.sos;

import org.n52.iceland.statistics.api.parameters.AbstractEsParameter;
import org.n52.iceland.statistics.api.parameters.Description;
import org.n52.iceland.statistics.api.parameters.ElasticsearchTypeRegistry;
import org.n52.iceland.statistics.api.parameters.ObjectEsParameterFactory;
import org.n52.iceland.statistics.api.parameters.SingleEsParameter;
import org.n52.iceland.statistics.api.parameters.Description.InformationOrigin;
import org.n52.iceland.statistics.api.parameters.Description.Operation;

public class SosDataMapping {
    public static final String SPATIAL_FILTER = "Spatial filter";
    public static final String TEMPORAL_FILTER = "Temporal filter";
    public static final String OBSERVED_PROPERTIES = "Observed properties";
    public static final String PROCEDURES = "Procedures";
    public static final String PDF = "Description format of the procedure";
    public static final String FEATURE_OF_INTEREST = "Feature of interest";
    public static final String PROCEDURE_ID = "Procedure ID";
    public static final String OFFERING = "Offering";
    public static final String OBSERVATION_TEMPLATE_ID = "Observation template ID";
    public static final String OBSERVED_PROPERTY = "Observed property";

    // --------------- GETCAPABILITIES --------------//

    public static final AbstractEsParameter GC_VERSIONS_FIELD = new SingleEsParameter("getcapabilities-versions",
            new Description(InformationOrigin.RequestEvent, Operation.GetCapabilities, "Accept versions"),
            ElasticsearchTypeRegistry.STRING_FIELD);

    public static final AbstractEsParameter GC_FORMATS_FIELD = new SingleEsParameter("getcapabilities-formats",
            new Description(InformationOrigin.RequestEvent, Operation.GetCapabilities, "Accept formats"),
            ElasticsearchTypeRegistry.STRING_FIELD);

    public static final AbstractEsParameter GC_SECTIONS = new SingleEsParameter("getcapabilities-sections",
            new Description(InformationOrigin.RequestEvent, Operation.GetCapabilities, "Sections"),
            ElasticsearchTypeRegistry.STRING_FIELD);

    public static final AbstractEsParameter GC_UPDATE_SEQUENCE =
            new SingleEsParameter("getcapabilities-updatesequence",
                    new Description(InformationOrigin.RequestEvent, Operation.GetCapabilities, "Update sequence"),
                    ElasticsearchTypeRegistry.STRING_FIELD);

    // --------------- DESCRIBE SENSOR --------------//
    public static final AbstractEsParameter DS_PROCEDURE = new SingleEsParameter("describesensor-procedure",
            new Description(InformationOrigin.RequestEvent, Operation.DescribeSensor, "Procedure to describe"),
            ElasticsearchTypeRegistry.STRING_FIELD);

    public static final AbstractEsParameter DS_PROCEDURE_DESC_FORMAT =
            new SingleEsParameter("describesensor-procedure-description-format",
                    new Description(InformationOrigin.RequestEvent, Operation.DescribeSensor, "Description format"),
                    ElasticsearchTypeRegistry.STRING_FIELD);

    public static final AbstractEsParameter DS_VALID_TIME = ObjectEsParameterFactory.time("describesensor-validtime",
            new Description(InformationOrigin.RequestEvent, Operation.DescribeSensor, "Validtime of the request"));

    // --------------- GET OBSERVATION --------------//
    public static final AbstractEsParameter GO_PROCEDURES = new SingleEsParameter("getobservation-procedures",
            new Description(InformationOrigin.RequestEvent, Operation.GetObservation, "Procedure of the observation"),
            ElasticsearchTypeRegistry.STRING_FIELD);

    public static final AbstractEsParameter GO_FEATURE_OF_INTERESTS =
            new SingleEsParameter("getobservation-feature-of-interests",
                    new Description(InformationOrigin.RequestEvent, Operation.GetObservation, "Feature of interests"),
                    ElasticsearchTypeRegistry.STRING_FIELD);

    public static final AbstractEsParameter GO_SPATIAL_FILTER =
            ObjectEsParameterFactory.spatialFilter("getobservation-spatial-filter",
                    new Description(InformationOrigin.RequestEvent, Operation.GetObservation, SPATIAL_FILTER));

    public static final AbstractEsParameter GO_TEMPORAL_FILTER =
            ObjectEsParameterFactory.temporalFilter("getobservation-temporal-filter",
                    new Description(InformationOrigin.RequestEvent, Operation.GetObservation, TEMPORAL_FILTER));

    public static final AbstractEsParameter GO_OBSERVED_PROPERTIES =
            new SingleEsParameter("getobservation-observed-properties",
                    new Description(InformationOrigin.RequestEvent, Operation.GetObservation, OBSERVED_PROPERTIES),
                    ElasticsearchTypeRegistry.STRING_FIELD);

    public static final AbstractEsParameter GO_OFFERINGS = new SingleEsParameter("getobservation-offerings",
            new Description(InformationOrigin.RequestEvent, Operation.GetObservation, "Offerings"),
            ElasticsearchTypeRegistry.STRING_FIELD);

    public static final AbstractEsParameter GO_RESPONSE_FORMAT =
            new SingleEsParameter("getobservation-response-format",
                    new Description(InformationOrigin.RequestEvent, Operation.GetObservation, "Response format"),
                    ElasticsearchTypeRegistry.STRING_FIELD);

    public static final AbstractEsParameter GO_IS_MERGED_OBSERVATION_VALUES = new SingleEsParameter(
            "getobservation-merged-observation-values", new Description(InformationOrigin.RequestEvent,
                    Operation.GetObservation, "Are the observation values merged"),
            ElasticsearchTypeRegistry.BOOLEAN_FIELD);

    // --------------- GET OBSERVATION BY ID--------------//
    public static final AbstractEsParameter GOBID_OBSERVATION_IDENTIFIER =
            new SingleEsParameter(
                    "getobservationbyid-observation-identifier", new Description(InformationOrigin.RequestEvent,
                            Operation.GetObservationById, "ID of the observation"),
                    ElasticsearchTypeRegistry.STRING_FIELD);

    // --------------- GET FEATURE OF INTEREST --------------//
    public static final AbstractEsParameter GFOI_FEATURE_IDENTIFIERS =
            new SingleEsParameter(
                    "getfeatureofinterest-feature-identifiers", new Description(InformationOrigin.RequestEvent,
                            Operation.GetFeatureOfInterest, "Feature identifiers"),
                    ElasticsearchTypeRegistry.STRING_FIELD);

    public static final AbstractEsParameter GFOI_OBSERVED_PROPERTIES =
            new SingleEsParameter(
                    "getfeatureofinterest-observed-properties", new Description(InformationOrigin.RequestEvent,
                            Operation.GetFeatureOfInterest, OBSERVED_PROPERTIES),
                    ElasticsearchTypeRegistry.STRING_FIELD);

    public static final AbstractEsParameter GFOI_PROCEDURES = new SingleEsParameter("getfeatureofinterest-procedures",
            new Description(InformationOrigin.RequestEvent, Operation.GetFeatureOfInterest, PROCEDURES),
            ElasticsearchTypeRegistry.STRING_FIELD);

    public static final AbstractEsParameter GFOI_SPATIAL_FILTER =
            ObjectEsParameterFactory.spatialFilter("getfeatureofinterest-spatial-filter",
                    new Description(InformationOrigin.RequestEvent, Operation.GetFeatureOfInterest, SPATIAL_FILTER));

    public static final AbstractEsParameter GFOI_TEMPORAL_FILTER = ObjectEsParameterFactory.temporalFilter(
            "getfeatureofinterest-temporal-filter",
            new Description(InformationOrigin.RequestEvent, Operation.GetFeatureOfInterest, TEMPORAL_FILTER));

    // --------------- INSERT SENSOR --------------//
    public static final AbstractEsParameter IS_ASSIGNED_OFFERINGS =
            new SingleEsParameter("insertsensor-assigned-offerings",
                    new Description(InformationOrigin.RequestEvent, Operation.InsertSensor, "Assigned offerings"),
                    ElasticsearchTypeRegistry.STRING_FIELD);

    public static final AbstractEsParameter IS_ASSIGNED_PROCEDURE_IDENTIFIERS =
            new SingleEsParameter("insertsensor-assigned-procedure-identifiers",
                    new Description(InformationOrigin.RequestEvent, Operation.InsertSensor, PROCEDURES),
                    ElasticsearchTypeRegistry.STRING_FIELD);

    public static final AbstractEsParameter IS_OBSERVABLE_PROPERTY =
            new SingleEsParameter("insertsensor-observable-property",
                    new Description(InformationOrigin.RequestEvent, Operation.InsertSensor, "Observable properties"),
                    ElasticsearchTypeRegistry.STRING_FIELD);

    public static final AbstractEsParameter IS_PROCEDURE_DESCRIPTION =
            new SingleEsParameter(
                    "insertsensor-procedure-description", new Description(InformationOrigin.RequestEvent,
                            Operation.InsertSensor, "Description of the procedure"),
                    ElasticsearchTypeRegistry.STRING_FIELD);

    public static final AbstractEsParameter IS_PROCEDURE_DESCRIPTION_FORMAT =
            new SingleEsParameter(
                    "insertsensor-description-format", new Description(InformationOrigin.RequestEvent,
                            Operation.InsertSensor, PDF),
                    ElasticsearchTypeRegistry.STRING_FIELD);

    public static final AbstractEsParameter IS_FEATURE_OF_INTEREST_TYPES =
            new SingleEsParameter("insertsensor-feature-of-interest-types",
                    new Description(InformationOrigin.RequestEvent, Operation.InsertSensor, FEATURE_OF_INTEREST),
                    ElasticsearchTypeRegistry.STRING_FIELD);

    public static final AbstractEsParameter IS_OBSERVATION_TYPES =
            new SingleEsParameter("insertsensor-observation-types",
                    new Description(InformationOrigin.RequestEvent, Operation.InsertSensor, "Observation types"),
                    ElasticsearchTypeRegistry.STRING_FIELD);

    // --------------- UPDATE SENSOR --------------//
    public static final AbstractEsParameter US_PROCEDURE_IDENTIFIER =
            new SingleEsParameter("updatesensor-procedure-identifier",
                    new Description(InformationOrigin.RequestEvent, Operation.UpdateSensor, PROCEDURE_ID),
                    ElasticsearchTypeRegistry.STRING_FIELD);

    public static final AbstractEsParameter US_PROCEDURE_DESCRIPTION_FORMAT =
            new SingleEsParameter(
                    "updatesensor-description-format", new Description(InformationOrigin.RequestEvent,
                            Operation.UpdateSensor, PDF),
                    ElasticsearchTypeRegistry.STRING_FIELD);

    // --------------- DELETE SENSOR --------------//
    public static final AbstractEsParameter DELS_PROCEDURE_IDENTIFIER =
            new SingleEsParameter("deletesensor-procedure-identifier",
                    new Description(InformationOrigin.RequestEvent, Operation.DeleteSensor, PROCEDURE_ID),
                    ElasticsearchTypeRegistry.STRING_FIELD);

    // --------------- INSERT OBSERVATION --------------//
    public static final AbstractEsParameter IO_ASSIGNED_SENSORID =
            new SingleEsParameter("insertobservation-assigned-sensorid",
                    new Description(InformationOrigin.RequestEvent, Operation.InsertObservation, "Assigned sensor ID"),
                    ElasticsearchTypeRegistry.STRING_FIELD);

    public static final AbstractEsParameter IO_OFFERINGS = new SingleEsParameter("insertobservation-offerings",
            new Description(InformationOrigin.RequestEvent, Operation.InsertObservation, OFFERING),
            ElasticsearchTypeRegistry.STRING_FIELD);

    public static final AbstractEsParameter IO_OBSERVATION =
            ObjectEsParameterFactory.omObservation("insertobservation-observation",
                    new Description(InformationOrigin.RequestEvent, Operation.InsertObservation, "Observations"));

    // --------------- INSERT RESULT TEMPLATE --------------//
    public static final AbstractEsParameter IRT_IDENTIFIER = new SingleEsParameter("insertresulttemplate-identifier",
            new Description(InformationOrigin.RequestEvent, Operation.InsertResultTemplate, "ID"),
            ElasticsearchTypeRegistry.STRING_FIELD);

    public static final AbstractEsParameter IRT_OBSERVATION_TEMPLATE =
            new SingleEsParameter(
                    "insertresulttemplate-observation-template", new Description(InformationOrigin.RequestEvent,
                            Operation.InsertResultTemplate, OBSERVATION_TEMPLATE_ID),
                    ElasticsearchTypeRegistry.STRING_FIELD);

    // --------------- INSERT RESULT --------------//
    public static final AbstractEsParameter IR_TEMPLATE_IDENTIFIER =
            new SingleEsParameter("insertresult-template-identifier",
                    new Description(InformationOrigin.RequestEvent, Operation.InsertResult, "Template ID"),
                    ElasticsearchTypeRegistry.STRING_FIELD);

    public static final AbstractEsParameter IR_RESULT_VALUES = new SingleEsParameter("insertresult-result-values",
            new Description(InformationOrigin.RequestEvent, Operation.InsertResult, "Result values"),
            ElasticsearchTypeRegistry.STRING_FIELD);

    // --------------- GET RESULT TEMPLATE --------------//
    public static final AbstractEsParameter GRT_OBSERVED_PROPERTY =
            new SingleEsParameter("getresulttemplate-observed-property",
                    new Description(InformationOrigin.RequestEvent, Operation.GetResultTemplate, OBSERVED_PROPERTY),
                    ElasticsearchTypeRegistry.STRING_FIELD);

    public static final AbstractEsParameter GRT_OFFERING = new SingleEsParameter("getresulttemplate-offering",
            new Description(InformationOrigin.RequestEvent, Operation.GetResultTemplate, OFFERING),
            ElasticsearchTypeRegistry.STRING_FIELD);

    // --------------- GET RESULT --------------//
    public static final AbstractEsParameter GR_FEATURE_IDENTIFIERS =
            new SingleEsParameter("getresult-feature-identifiers",
                    new Description(InformationOrigin.RequestEvent, Operation.GetResult, "Feature IDs"),
                    ElasticsearchTypeRegistry.STRING_FIELD);

    public static final AbstractEsParameter GR_OBSERVATION_TEMPLATE_IDENTIFIER =
            new SingleEsParameter("getresult-observation-template-identifier",
                    new Description(InformationOrigin.RequestEvent, Operation.GetResult, OBSERVATION_TEMPLATE_ID),
                    ElasticsearchTypeRegistry.STRING_FIELD);

    public static final AbstractEsParameter GR_OBSERVATION_PROPERTY =
            new SingleEsParameter("getresult-observation-property",
                    new Description(InformationOrigin.RequestEvent, Operation.GetResult, "Observation property"),
                    ElasticsearchTypeRegistry.STRING_FIELD);

    public static final AbstractEsParameter GR_OFFERING = new SingleEsParameter("getresult-offering",
            new Description(InformationOrigin.RequestEvent, Operation.GetResult, OFFERING),
            ElasticsearchTypeRegistry.STRING_FIELD);

    public static final AbstractEsParameter GR_SPATIAL_FILTER =
            ObjectEsParameterFactory.spatialFilter("getresult-spatial-filter",
                    new Description(InformationOrigin.RequestEvent, Operation.GetResult, SPATIAL_FILTER));

    public static final AbstractEsParameter GR_TEMPORAL_FILTER =
            ObjectEsParameterFactory.temporalFilter("getresult-temporal-filter",
                    new Description(InformationOrigin.RequestEvent, Operation.GetResult, TEMPORAL_FILTER));

    // --------------- GET DATA AVAILABILITY --------------//
    public static final AbstractEsParameter GDA_FEATURES_OF_INTEREST =
            new SingleEsParameter(
                    "getdataavailability-features-of-interest", new Description(InformationOrigin.RequestEvent,
                            Operation.GetDataAvailability, FEATURE_OF_INTEREST),
                    ElasticsearchTypeRegistry.STRING_FIELD);

    public static final AbstractEsParameter GDA_OBSERVED_PROPERTIES =
            new SingleEsParameter(
                    "getdataavailability-observed-properties", new Description(InformationOrigin.RequestEvent,
                            Operation.GetDataAvailability, OBSERVED_PROPERTY),
                    ElasticsearchTypeRegistry.STRING_FIELD);

    public static final AbstractEsParameter GDA_OFFERINGS = new SingleEsParameter("getdataavailability-offerings",
            new Description(InformationOrigin.RequestEvent, Operation.GetDataAvailability, OFFERING),
            ElasticsearchTypeRegistry.STRING_FIELD);

    public static final AbstractEsParameter GDA_PROCEDURES = new SingleEsParameter("getdataavailability-procedures",
            new Description(InformationOrigin.RequestEvent, Operation.GetDataAvailability, "Procedure"),
            ElasticsearchTypeRegistry.STRING_FIELD);

}
