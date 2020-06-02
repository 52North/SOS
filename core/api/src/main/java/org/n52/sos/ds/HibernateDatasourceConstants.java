/*
 * Copyright (C) 2012-2020 52°North Initiative for Geospatial Open Source
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
package org.n52.sos.ds;

/**
 * @since 4.0.0
 *
 */
public interface HibernateDatasourceConstants {

    String ORM_CONNECTION_PROVIDER_IDENTIFIER = "hibernate.orm";

    String ORM_DATASOURCE_DAO_IDENTIFIER = ORM_CONNECTION_PROVIDER_IDENTIFIER;

    String OGM_CONNECTION_PROVIDER_IDENTIFIER = "hibernate.ogm";

    String OGM_DATASOURCE_DAO_IDENTIFIER = OGM_CONNECTION_PROVIDER_IDENTIFIER;

    String HIBERNATE_MAPPING_PATH = "/hbm";

    // Database concept paths
    String HIBERNATE_MAPPING_SIMPLE_CONCEPT_PATH = HIBERNATE_MAPPING_PATH + "/simple";

    String HIBERNATE_MAPPING_EREPORTING_CONCEPT_PATH = HIBERNATE_MAPPING_PATH + "/ereporting";

    String HIBERNATE_MAPPING_TRANSACTIONAL_CONCEPT_PATH = HIBERNATE_MAPPING_PATH + "/transactional";

    // Database core paths
    String HIBERNATE_MAPPING_CORE_PATH = "/core";

    String HIBERNATE_MAPPING_SIMPLE_CORE_PATH = HIBERNATE_MAPPING_SIMPLE_CONCEPT_PATH + HIBERNATE_MAPPING_CORE_PATH;

    String HIBERNATE_MAPPING_EREPORTING_CORE_PATH =
            HIBERNATE_MAPPING_EREPORTING_CONCEPT_PATH + HIBERNATE_MAPPING_CORE_PATH;

    String HIBERNATE_MAPPING_TRANSACTIONAL_CORE_PATH =
            HIBERNATE_MAPPING_TRANSACTIONAL_CONCEPT_PATH + HIBERNATE_MAPPING_CORE_PATH;

    // Database simple paths
    String HIBERNATE_MAPPING_DATASET_PATH = "/dataset";

    String HIBERNATE_MAPPING_SIMPLE_DATASET_PATH =
            HIBERNATE_MAPPING_SIMPLE_CONCEPT_PATH + HIBERNATE_MAPPING_DATASET_PATH;

    String HIBERNATE_MAPPING_EREPORTING_DATASET_PATH =
            HIBERNATE_MAPPING_EREPORTING_CONCEPT_PATH + HIBERNATE_MAPPING_DATASET_PATH;

    String HIBERNATE_MAPPING_TRANSACTIONAL_DATASET_PATH =
            HIBERNATE_MAPPING_TRANSACTIONAL_CONCEPT_PATH + HIBERNATE_MAPPING_DATASET_PATH;

    // Database sampling extension paths
    String HIBERNATE_MAPPING_SAMPLING_PATH = "/sampling";

    String HIBERNATE_MAPPING_SIMPLE_SAMPLING_PATH =
            HIBERNATE_MAPPING_SIMPLE_CONCEPT_PATH + HIBERNATE_MAPPING_SAMPLING_PATH;

    String HIBERNATE_MAPPING_EREPORTING_SAMPLING_PATH =
            HIBERNATE_MAPPING_EREPORTING_CONCEPT_PATH + HIBERNATE_MAPPING_SAMPLING_PATH;

    String HIBERNATE_MAPPING_TRANSACTIONAL_SAMPLING_PATH =
            HIBERNATE_MAPPING_TRANSACTIONAL_CONCEPT_PATH + HIBERNATE_MAPPING_SAMPLING_PATH;

    // TODO change to /feature
    String HIBERNATE_MAPPING_FEATURE_PATH = HIBERNATE_MAPPING_PATH + "/feature";

    String HIBERNATE_RESOURCES = "HIBERNATE_RESOURCES";

    String HIBERNATE_DIRECTORY = "HIBERNATE_DIRECTORY";

    String HIBERNATE_ANNOTADED_CLASSES = "HIBERNATE_ANNOTADED_CLASSES";

    String PATH_SEPERATOR = ";";

    String PROVIDED_JDBC = "PROVIDED_JDBC";

    String HIBERNATE_DRIVER_CLASS = "hibernate.connection.driver_class";

    String HIBERNATE_DEFAULT_OGM_PACKAGE = "org.n52.sos.ds.hibernate.ogm.entities";

    @Deprecated
    String HIBERNATE_DATASOURCE_TIMEZONE = "hibernate.datasource.timezone";

    String HIBERNATE_DATASOURCE_TIME_STRING_FORMAT = "hibernate.datasource.timeStringFormat";

    String HIBERNATE_DATASOURCE_TIME_STRING_Z = "hibernate.datasource.timeStringZ";

    enum DatabaseConcept {
        SIMPLE("Simple database model"), TRANSACTIONAL("Transactional database model"), EREPORTING(
                "eReporting database model");

        private final String displayName;

        DatabaseConcept(String displayName) {
            this.displayName = displayName;
        }

        public String getDisplayName() {
            return displayName;
        }
    }

    enum DatabaseExtension {
        DEFAULT("Default database model"), SAMPLING("Extended model to support Samplings/MeasuringPrograms");

        private final String displayName;

        DatabaseExtension(String displayName) {
            this.displayName = displayName;
        }

        public String getDisplayName() {
            return displayName;
        }
    }

    enum FeatureConcept {
        DEFAULT_FEATURE_CONCEPT("Default feature concept"), EXTENDED_FEATURE_CONCEPT("Extended feature concept");

        private final String displayName;

        FeatureConcept(String displayName) {
            this.displayName = displayName;
        }

        public String getDisplayName() {
            return displayName;
        }
    }
}
