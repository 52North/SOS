/**
 * Copyright (C) 2012-2014 52°North Initiative for Geospatial Open Source
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
package org.n52.sos.aqd;

import org.n52.sos.ogc.om.OmConstants;
import org.n52.sos.util.StringHelper;
import org.n52.sos.w3c.SchemaLocation;

public interface AqdConstants {
	
	String AQD = "AQD";
	
	String VERSION = "1.0.0";

    String DEFINITION_VERIFICATION = "http://dd.eionet.europa.eu/vocabularies/aq/observationverification";
    
    String DEFINITION_VALIDATION = "http://dd.eionet.europa.eu/vocabularies/aq/observationvalidity";
    
    String DEFINITION_PRIMARY_OBSERVATION_HOUR = "http://dd.eionet.europa.eu/vocabularyconcept/aq/primaryObservation/hour";

    String NS_AQD = "http://dd.eionet.europa.eu/schemaset/id2011850eu-1.0";

    String NAME_FIXED_OBSERVATIONS = "FixedObservations";
    
    String SCHEMA_LOCATION_URL_AQD_10 = "http://dd.eionet.europa.eu/schemas/id2011850eu-1.0/AirQualityReporting.xsd";

    SchemaLocation AQD_SCHEMA_LOCATION = new SchemaLocation(NS_AQD, SCHEMA_LOCATION_URL_AQD_10);

	String NS_AQD_PREFIX = "aqd";
            
    enum ElementType {
        StartTime(OmConstants.PHEN_SAMPLING_TIME, OmConstants.PHEN_UOM_ISO8601),
        EndTime(OmConstants.PHEN_SAMPLING_TIME, OmConstants.PHEN_UOM_ISO8601),
        Verification(DEFINITION_VERIFICATION),
        Validation(DEFINITION_VALIDATION),
        Pollutant(DEFINITION_PRIMARY_OBSERVATION_HOUR);
        
        private final String definition;
        
        private final String uom;
        
        ElementType(String definition) {
            this.definition = definition;
            this.uom = null;
        }
        
        ElementType(String definition, String uom) {
            this.definition = definition;
            this.uom = uom;
        }
        
        public String getDefinition() {
            return definition;
        }
        
        public String getUOM() {
            return uom;
        }
        
        public boolean isSetUOM() {
            return StringHelper.isNotEmpty(getUOM());
        }
    }

    enum ProcessParameter {
        AssessmentType("AssessmentType"), CalibrationSamplingPointsOther("CAL-SPother"), CalibrationSamplingPoints(
                "CAL-SPs"), EnvironmentalObjective("EO"), Model("model"), ObjectiveEstimation("objective-estimation"), PrimaryObservationTime(
                "primaryObsTime"), SamplingPoint("SamplingPoint"), MonitoringStation("Station"), VerificationSamplingPoints(
                "VER-SP"), VerificationSamplingPointsOther("VER-SPother");

        private static final String baseURI = "http://dd.eionet.europa.eu/vocabulary/aq/processparameter/";

        private final String id;

        private final String conceptURI;

        ProcessParameter(String id) {
            this.id = id;
            this.conceptURI = baseURI + id;
        }

        public String getId() {
            return id;
        }

        public String getConceptURI() {
            return conceptURI;
        }

    }

    enum AssessmentType {
        Fixed("fixed"), Interactive("interactive"), Model("model"), Objective("objective");

        private static final String baseURI = "http://dd.eionet.europa.eu/vocabulary/aq/assessmenttype/";

        private final String id;

        private final String conceptURI;

        AssessmentType(String id) {
            this.id = id;
            this.conceptURI = baseURI + id;
        }

        public String getId() {
            return id;
        }

        public String getConceptURI() {
            return conceptURI;
        }

        public static AssessmentType from(String v) {
            for (AssessmentType c : AssessmentType.values()) {
                if (c.getConceptURI().equals(v) || c.getId().equals(v)) {
                    return c;
                }
            }
            throw new IllegalArgumentException(v);
        }

        public static AssessmentType fromConceptURI(String v) {
            for (AssessmentType c : AssessmentType.values()) {
                if (c.getConceptURI().equals(v)) {
                    return c;
                }
            }
            throw new IllegalArgumentException(v);
        }

        public static AssessmentType fromId(String v) {
            for (AssessmentType c : AssessmentType.values()) {
                if (c.getId().equals(v)) {
                    return c;
                }
            }
            throw new IllegalArgumentException(v);
        }

    }
    
   
}
