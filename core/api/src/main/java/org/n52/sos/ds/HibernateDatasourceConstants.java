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
package org.n52.sos.ds;

/**
 * @since 4.0.0
 * 
 */
public interface HibernateDatasourceConstants {
    
    String ORM_CONNECTION_PROVIDER_IDENTIFIER = "hibernate.orm";
    
    String ORM_DATASOURCE_DAO_IDENTIFIER = "hibernate.orm";
    
    String OGM_CONNECTION_PROVIDER_IDENTIFIER = "hibernate.ogm";
    
    String OGM_DATASOURCE_DAO_IDENTIFIER = "hibernate.ogm";
    
    String HIBERNATE_MAPPING_PATH = "/mapping";

    String HIBERNATE_MAPPING_CORE_PATH = HIBERNATE_MAPPING_PATH + "/core";
    
    String HIBERNATE_MAPPING_TRANSACTIONAL_PATH = HIBERNATE_MAPPING_PATH + "/transactional";
    
    String HIBERNATPE_MAPPING_OLD_CONCEPT_PATH = HIBERNATE_MAPPING_PATH + "/old";
    
    String HIBERNATE_MAPPING_OLD_CONCEPT_OBSERVATION_PATH = HIBERNATPE_MAPPING_OLD_CONCEPT_PATH + "/observation";
    
    String HIBERNATE_MAPPING_SERIES_CONCEPT_PATH = HIBERNATE_MAPPING_PATH + "/series";

    String HIBERNATE_MAPPING_SERIES_CONCEPT_OBSERVATION_PATH = HIBERNATE_MAPPING_SERIES_CONCEPT_PATH + "/observation";
    
    String HIBERNATE_MAPPING_EREPORTING_CONCEPT_OBSERVATION_PATH = HIBERNATE_MAPPING_PATH + "/ereporting";
    
    String HIBERNATE_MAPPING_I18N_PATH = HIBERNATE_MAPPING_PATH + "/i18n";

    String HIBERNATE_MAPPING_EXTENSION = HIBERNATE_MAPPING_PATH + "/extension";

    String HIBERNATE_MAPPING_EXTENSION_READONLY =  HIBERNATE_MAPPING_EXTENSION + "/readonly";
    
    String HIBERNATE_RESOURCES = "HIBERNATE_RESOURCES";

    String HIBERNATE_DIRECTORY = "HIBERNATE_DIRECTORY";
    
    String HIBERNATE_ANNOTADED_CLASSES = "HIBERNATE_ANNOTADED_CLASSES";

    String PATH_SEPERATOR = ";";

    String PROVIDED_JDBC = "PROVIDED_JDBC";

    String HIBERNATE_DRIVER_CLASS = "hibernate.connection.driver_class";
    
    String HIBERNATE_DEFAULT_OGM_PACKAGE = "org.n52.sos.ds.hibernate.ogm.entities";
    
    public enum DatabaseConcept {
        OLD_CONCEPT("Old concept"),
        SERIES_CONCEPT("Series concept"),
        EREPORTING_CONCEPT("eReporting concept (extended Series concept)");
        
        private final String displayName;
        
        private DatabaseConcept(String displayName) {
           this.displayName = displayName;
        }
        
        public String getDisplayName() {
            return displayName;
        }
    }
}
