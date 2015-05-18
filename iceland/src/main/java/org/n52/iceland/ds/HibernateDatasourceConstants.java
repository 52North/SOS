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
package org.n52.iceland.ds;

/**
 * @since 4.0.0
 * 
 */
public interface HibernateDatasourceConstants {
    
    String ORM_CONNECTION_PROVIDER_IDENTIFIER = "hibernate.orm";
    
    String ORM_DATASOURCE_DAO_IDENTIFIER = "hibernate.orm";
    
    String OGM_CONNECTION_PROVIDER_IDENTIFIER = "hibernate.ogm";
    
    String OGM_DATASOURCE_DAO_IDENTIFIER = "hibernate.ogm";
    
    String HIBERNATE_MAPPING_BASE = "/base";
    
    String HIBERNATE_MAPPING_OBSERVATION = "/observation";
    
    String HIBERNATE_MAPPING_VALUE = "/value";
    
    String HIBERNATE_MAPPING_PATH = "/mapping";

    String HIBERNATE_MAPPING_CORE_PATH = HIBERNATE_MAPPING_PATH + "/core";
    
    String HIBERNATE_MAPPING_TRANSACTIONAL_PATH = HIBERNATE_MAPPING_PATH + "/transactional";
    
    // concrete observation/value mapping file names
    String HIBERNATE_MAPPING_FILE_BLOB = "Blob.hbm.xml";
    
    String HIBERNATE_MAPPING_FILE_BOOLEAN = "Boolean.hbm.xml";
    
    String HIBERNATE_MAPPING_FILE_CATEGORY = "Category.hbm.xml";
    
    String HIBERNATE_MAPPING_FILE_COUNT = "Count.hbm.xml";
    
    String HIBERNATE_MAPPING_FILE_GEOMETRY = "Geometry.hbm.xml";
    
    String HIBERNATE_MAPPING_FILE_NUMERIC = "Numeric.hbm.xml";
    
    String HIBERNATE_MAPPING_FILE_SWE_DATA_ARRAY = "SweDataArray.hbm.xml";
    
    String HIBERNATE_MAPPING_FILE_TEXT = "Text.hbm.xml";
    
    // old concept
    String HIBERNATPE_MAPPING_OLD_CONCEPT_PATH = HIBERNATE_MAPPING_PATH + "/old";
    
    String HIBERNATE_MAPPING_OLD_CONCEPT_BASE_PATH = HIBERNATPE_MAPPING_OLD_CONCEPT_PATH + HIBERNATE_MAPPING_BASE;
    
    String HIBERNATE_MAPPING_OLD_CONCEPT_OBSERVATION_PATH = HIBERNATPE_MAPPING_OLD_CONCEPT_PATH + HIBERNATE_MAPPING_OBSERVATION;
    
    String HIBERNATE_MAPPING_OLD_CONCEPT_VALUE_PATH = HIBERNATPE_MAPPING_OLD_CONCEPT_PATH + HIBERNATE_MAPPING_VALUE;
    
    // series concept
    String HIBERNATE_MAPPING_SERIES_CONCEPT_PATH = HIBERNATE_MAPPING_PATH + "/series";
    
    String HIBERNATE_MAPPING_SERIES_CONCEPT_BASE_PATH = HIBERNATE_MAPPING_SERIES_CONCEPT_PATH + HIBERNATE_MAPPING_BASE;

    String HIBERNATE_MAPPING_SERIES_CONCEPT_OBSERVATION_PATH = HIBERNATE_MAPPING_SERIES_CONCEPT_PATH + HIBERNATE_MAPPING_OBSERVATION;
    
    String HIBERNATE_MAPPING_SERIES_CONCEPT_VALUE_PATH = HIBERNATE_MAPPING_SERIES_CONCEPT_PATH + HIBERNATE_MAPPING_VALUE;
    
    // eReporting concept
    String HIBERNATE_MAPPING_EREPORTING_CONCEPT_PATH = HIBERNATE_MAPPING_PATH + "/ereporting";
    
    String HIBERNATE_MAPPING_EREPORTING_CONCEPT_BASE_PATH = HIBERNATE_MAPPING_EREPORTING_CONCEPT_PATH + HIBERNATE_MAPPING_BASE;
    
    String HIBERNATE_MAPPING_EREPORTING_CONCEPT_OBSERVATION_PATH = HIBERNATE_MAPPING_EREPORTING_CONCEPT_PATH + HIBERNATE_MAPPING_OBSERVATION;
    
    String HIBERNATE_MAPPING_EREPORTING_CONCEPT_VALUE_PATH = HIBERNATE_MAPPING_EREPORTING_CONCEPT_PATH + HIBERNATE_MAPPING_VALUE;
    
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
