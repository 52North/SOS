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

public class SosDataMapping {

    // --------------- GETCAPABILITIES --------------//

    public static final String GC_VERSIONS_FIELD = "getcapabilities-versions";
    public static final String GC_FORMATS_FIELD = "getcapabilities-formats";
    public static final String GC_SECTIONS = "getcapabilities-sections";
    public static final String GC_UPDATE_SEQUENCE = "getcapabilities-updatesequence";

    // --------------- DESCRIBE SENSOR --------------//
    public static final String DS_PROCEDURE = "describesensor-procedure";
    public static final String DS_PROCEDURE_DESC_FORMAT = "describesensor-procedure-description-format";

    // -------------- OBJECT ----------------------//
    public static final String DS_VALID_TIME = "describesensor-validtime";
    public static final String DS_VALID_TIME_DURATION = "describesensor-validtime-duration";
    public static final String DS_VALID_TIME_START = "describesensor-validtime-start";
    public static final String DS_VALID_TIME_END = "describesensor-validtime-end";
    public static final String DS_VALID_TIME_TIMEINSTANT = "describesensor-validtime-timeinstant";
    // -------------- END OBJECT ----------------------//

    // --------------- GET OBSERVATION --------------//
    public static final String GO_PROCEDURES = "getobservation-procedures";
    public static final String GO_FEATURE_OF_INTERESTS = "getobservation-feature-of-interests";
    public static final String GO_SPATIAL_FILTER = "getobservation-spatial-filter";
    public static final String GO_OBSERVED_PROPERTIES = "getobservation-observed-properties";
    public static final String GO_OFFERINGS = "getobservation-offerings";

    // --------------- GET OBSERVATION BY ID--------------//
    public static final String GOBID_OBSERVATION_IDENTIFIER = "getobservationbyid-observation-identifier";

    // --------------- GET FEATURE OF INTEREST --------------//
    public static final String GFOI_FEATURE_IDENTIFIERS = "getfeatureofinterest-feature-identifiers";

    public static final String GFOI_NAMESPACES = "getfeatureofinterest-namespaces";

    public static final String GFOI_OBSERVED_PROPERTIES = "getfeatureofinterest-observed-properties";

    public static final String GFOI_PROCEDURES = "getfeatureofinterest-procedures";

    // --------------- INSERT SENSOR --------------//
    public static final String IS_ASSIGNED_OFFERINGS = "insertsensor-assigned-offerings";

    public static final String IS_ASSIGNED_PROCEDURE_IDENTIFIERS = "insertsensor-assigned-procedure-identifiers";

    public static final String IS_OBSERVABLE_PROPERTY = "insertsensor-observable-property";

    public static final String IS_PROCEDURE_DESCRIPTION = "insertsensor-procedure-description";

    public static final String IS_PROCEDURE_DESCRIPTION_FORMAT = "insertsensor-description-format";

    // --------------- UPDATE SENSOR --------------//
    public static final String US_PROCEDURE_IDENTIFIER = "updatesensor-procedure-identifier";

    public static final String US_PROCEDURE_DESCRIPTION_FORMAT = "updatesensor-description-format";

    // --------------- DELETE SENSOR --------------//
    public static final String DELS_PROCEDURE_IDENTIFIER = "deletesensor-procedure-identifier";

    // --------------- INSERT OBSERVATION --------------//
    public static final String IO_ASSIGNED_SENSORID = "insertobservation-assigned-sensorid";

    public static final String IO_OFFERINGS = "insertobservation-offerings";

    // --------------- INSERT RESULT TEMPLATE --------------//
    public static final String IRT_IDENTIFIER = "insertresulttemplate-identifier";

    public static final String IRT_RESULT_ENCODING = "insertresulttemplate-result-encoding";

    // --------------- INSERT RESULT --------------//
    public static final String IR_TEMPLATE_IDENTIFIER = "insertresult-template-identifier";

    // --------------- GET RESULT TEMPLATE --------------//
    public static final String GRT_OBSERVED_PROPERTY = "getresulttemplate-observed-property";

    public static final String GRT_OFFERING = "getresulttemplate-offering";

    // --------------- GET RESULT --------------//
    public static final String GR_FEATURE_IDENTIFIERS = "getresult-feature-identifiers";

    public static final String GR_NAMSPACES = "getresult-namespaces";

    public static final String GR_OBSERVATION_TEMPLATE_IDENTIFIER = "getresult-observation-template-identifier";

    public static final String GR_OBSERVATION_PROPERTY = "getresult-observation-property";

    public static final String GR_OFFERING = "getresult-offering";

    // --------------- GET DATA AVAILABILITY --------------//
    public static final String GDA_FEATURES_OF_INTEREST = "getdataavailability-features-of-interest";

    public static final String GDA_NAMESPACE = "getdataavailability-namespace";

    public static final String GDA_OBSERVED_PROPERTIES = "getdataavailability-observed-properties";

    public static final String GDA_OFFERINGS = "getdataavailability-offerings";

    public static final String GDA_PROCEDURES = "getdataavailability-procedures";

    // --------------- DEFAULT RESPONSE EVENTS --------------//

    public static final String SRESP_MEDIATYPE = "sresp-mediatype";

}
