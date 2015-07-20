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
package org.n52.sos.statistics.sos;

import org.n52.sos.statistics.api.parameters.AbstractEsParameter;
import org.n52.sos.statistics.api.parameters.ElasticsearchTypeRegistry;
import org.n52.sos.statistics.api.parameters.ObjectEsParameterFactory;
import org.n52.sos.statistics.api.parameters.SingleEsParameter;

public class SosDataMapping {

    // --------------- GETCAPABILITIES --------------//

    public static final AbstractEsParameter GC_VERSIONS_FIELD = new SingleEsParameter("getcapabilities-versions",
            ElasticsearchTypeRegistry.stringField);
    public static final AbstractEsParameter GC_FORMATS_FIELD =
            new SingleEsParameter("getcapabilities-formats", ElasticsearchTypeRegistry.stringField);
    public static final AbstractEsParameter GC_SECTIONS = new SingleEsParameter("getcapabilities-sections", ElasticsearchTypeRegistry.stringField);
    public static final AbstractEsParameter GC_UPDATE_SEQUENCE = new SingleEsParameter("getcapabilities-updatesequence",
            ElasticsearchTypeRegistry.stringField);

    // --------------- DESCRIBE SENSOR --------------//
    public static final AbstractEsParameter DS_PROCEDURE = new SingleEsParameter("describesensor-procedure", ElasticsearchTypeRegistry.stringField);
    public static final AbstractEsParameter DS_PROCEDURE_DESC_FORMAT = new SingleEsParameter("describesensor-procedure-description-format",
            ElasticsearchTypeRegistry.stringField);
    public static final AbstractEsParameter DS_VALID_TIME = new SingleEsParameter("describesensor-validtime", ElasticsearchTypeRegistry.stringField);

    // --------------- GET OBSERVATION --------------//
    public static final AbstractEsParameter GO_PROCEDURES = new SingleEsParameter("getobservation-procedures", ElasticsearchTypeRegistry.stringField);
    public static final AbstractEsParameter GO_FEATURE_OF_INTERESTS = new SingleEsParameter("getobservation-feature-of-interests",
            ElasticsearchTypeRegistry.stringField);
    public static final AbstractEsParameter GO_SPATIAL_FILTER = ObjectEsParameterFactory.spatialFilter("getobservation-spatial-filter", null);
    public static final AbstractEsParameter GO_TEMPORAL_FILTER = ObjectEsParameterFactory.temporalFilter("getobservation-temporal-filter", null);
    public static final AbstractEsParameter GO_OBSERVED_PROPERTIES = new SingleEsParameter("getobservation-observed-properties",
            ElasticsearchTypeRegistry.stringField);
    public static final AbstractEsParameter GO_OFFERINGS = new SingleEsParameter("getobservation-offerings", ElasticsearchTypeRegistry.stringField);
    public static final AbstractEsParameter GO_RESPONSE_FORMAT = new SingleEsParameter("getobservation-response-format",
            ElasticsearchTypeRegistry.stringField);
    public static final AbstractEsParameter GO_IS_MERGED_OBSERVATION_VALUES = new SingleEsParameter("getobservation-merged-observation-values",
            ElasticsearchTypeRegistry.stringField);

    // --------------- GET OBSERVATION BY ID--------------//
    public static final AbstractEsParameter GOBID_OBSERVATION_IDENTIFIER = new SingleEsParameter("getobservationbyid-observation-identifier",
            ElasticsearchTypeRegistry.stringField);

    // --------------- GET FEATURE OF INTEREST --------------//
    public static final AbstractEsParameter GFOI_FEATURE_IDENTIFIERS = new SingleEsParameter("getfeatureofinterest-feature-identifiers",
            ElasticsearchTypeRegistry.stringField);
    public static final AbstractEsParameter GFOI_OBSERVED_PROPERTIES = new SingleEsParameter("getfeatureofinterest-observed-properties",
            ElasticsearchTypeRegistry.stringField);
    public static final AbstractEsParameter GFOI_PROCEDURES = new SingleEsParameter("getfeatureofinterest-procedures",
            ElasticsearchTypeRegistry.stringField);
    public static final AbstractEsParameter GFOI_SPATIAL_FILTER = ObjectEsParameterFactory.spatialFilter("getfeatureofinterest-spatial-filter", null);
    public static final AbstractEsParameter GFOI_TEMPORAL_FILTER = ObjectEsParameterFactory.temporalFilter("getfeatureofinterest-temporal-filter",
            null);

    // --------------- INSERT SENSOR --------------//
    public static final AbstractEsParameter IS_ASSIGNED_OFFERINGS = new SingleEsParameter("insertsensor-assigned-offerings",
            ElasticsearchTypeRegistry.stringField);
    public static final AbstractEsParameter IS_ASSIGNED_PROCEDURE_IDENTIFIERS = new SingleEsParameter("insertsensor-assigned-procedure-identifiers",
            ElasticsearchTypeRegistry.stringField);
    public static final AbstractEsParameter IS_OBSERVABLE_PROPERTY = new SingleEsParameter("insertsensor-observable-property",
            ElasticsearchTypeRegistry.stringField);
    public static final AbstractEsParameter IS_PROCEDURE_DESCRIPTION = new SingleEsParameter("insertsensor-procedure-description",
            ElasticsearchTypeRegistry.stringField);
    public static final AbstractEsParameter IS_PROCEDURE_DESCRIPTION_FORMAT = new SingleEsParameter("insertsensor-description-format",
            ElasticsearchTypeRegistry.stringField);
    public static final AbstractEsParameter IS_FEATURE_OF_INTEREST_TYPES = new SingleEsParameter("insertsensor-feature-of-interest-types",
            ElasticsearchTypeRegistry.stringField);
    public static final AbstractEsParameter IS_OBSERVATION_TYPES = new SingleEsParameter("insertsensor-observation-types",
            ElasticsearchTypeRegistry.stringField);

    // --------------- UPDATE SENSOR --------------//
    public static final AbstractEsParameter US_PROCEDURE_IDENTIFIER = new SingleEsParameter("updatesensor-procedure-identifier",
            ElasticsearchTypeRegistry.stringField);
    public static final AbstractEsParameter US_PROCEDURE_DESCRIPTION_FORMAT = new SingleEsParameter("updatesensor-description-format",
            ElasticsearchTypeRegistry.stringField);

    // --------------- DELETE SENSOR --------------//
    public static final AbstractEsParameter DELS_PROCEDURE_IDENTIFIER = new SingleEsParameter("deletesensor-procedure-identifier",
            ElasticsearchTypeRegistry.stringField);

    // --------------- INSERT OBSERVATION --------------//
    public static final AbstractEsParameter IO_ASSIGNED_SENSORID = new SingleEsParameter("insertobservation-assigned-sensorid",
            ElasticsearchTypeRegistry.stringField);
    public static final AbstractEsParameter IO_OFFERINGS =
            new SingleEsParameter("insertobservation-offerings", ElasticsearchTypeRegistry.stringField);
    public static final AbstractEsParameter IO_OBSERVATION = ObjectEsParameterFactory.omObservation("insertobservation-observation", null);

    // --------------- INSERT RESULT TEMPLATE --------------//
    public static final AbstractEsParameter IRT_IDENTIFIER = new SingleEsParameter("insertresulttemplate-identifier",
            ElasticsearchTypeRegistry.stringField);
    public static final AbstractEsParameter IRT_RESULT_ENCODING = new SingleEsParameter("insertresulttemplate-result-encoding",
            ElasticsearchTypeRegistry.stringField);
    public static final AbstractEsParameter IRT_OBSERVATION_TEMPLATE = new SingleEsParameter("insertresulttemplate-observation-template",
            ElasticsearchTypeRegistry.stringField);

    // --------------- INSERT RESULT --------------//
    public static final AbstractEsParameter IR_TEMPLATE_IDENTIFIER = new SingleEsParameter("insertresult-template-identifier",
            ElasticsearchTypeRegistry.stringField);
    public static final AbstractEsParameter IR_RESULT_VALUES = new SingleEsParameter("insertresult-result-values",
            ElasticsearchTypeRegistry.stringField);

    // --------------- GET RESULT TEMPLATE --------------//
    public static final AbstractEsParameter GRT_OBSERVED_PROPERTY = new SingleEsParameter("getresulttemplate-observed-property",
            ElasticsearchTypeRegistry.stringField);
    public static final AbstractEsParameter GRT_OFFERING = new SingleEsParameter("getresulttemplate-offering", ElasticsearchTypeRegistry.stringField);

    // --------------- GET RESULT --------------//
    public static final AbstractEsParameter GR_FEATURE_IDENTIFIERS = new SingleEsParameter("getresult-feature-identifiers",
            ElasticsearchTypeRegistry.stringField);
    public static final AbstractEsParameter GR_OBSERVATION_TEMPLATE_IDENTIFIER = new SingleEsParameter("getresult-observation-template-identifier",
            ElasticsearchTypeRegistry.stringField);
    public static final AbstractEsParameter GR_OBSERVATION_PROPERTY = new SingleEsParameter("getresult-observation-property",
            ElasticsearchTypeRegistry.stringField);
    public static final AbstractEsParameter GR_OFFERING = new SingleEsParameter("getresult-offering", ElasticsearchTypeRegistry.stringField);
    public static final AbstractEsParameter GR_SPATIAL_FILTER = ObjectEsParameterFactory.spatialFilter("getresult-spatial-filter", null);
    public static final AbstractEsParameter GR_TEMPORAL_FILTER = ObjectEsParameterFactory.temporalFilter("getresult-temporal-filter", null);

    // --------------- GET DATA AVAILABILITY --------------//
    public static final AbstractEsParameter GDA_FEATURES_OF_INTEREST = new SingleEsParameter("getdataavailability-features-of-interest",
            ElasticsearchTypeRegistry.stringField);
    public static final AbstractEsParameter GDA_OBSERVED_PROPERTIES = new SingleEsParameter("getdataavailability-observed-properties",
            ElasticsearchTypeRegistry.stringField);
    public static final AbstractEsParameter GDA_OFFERINGS = new SingleEsParameter("getdataavailability-offerings",
            ElasticsearchTypeRegistry.stringField);
    public static final AbstractEsParameter GDA_PROCEDURES = new SingleEsParameter("getdataavailability-procedures",
            ElasticsearchTypeRegistry.stringField);
}
