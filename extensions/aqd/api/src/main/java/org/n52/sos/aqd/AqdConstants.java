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
package org.n52.sos.aqd;

import java.util.Map;
import java.util.Set;

import javax.xml.namespace.QName;

import org.n52.sos.iso.GcoConstants;
import org.n52.sos.iso.gmd.GmdConstants;
import org.n52.sos.w3c.SchemaLocation;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Sets;

public interface AqdConstants {

    String AQD = "AQD";

    String VERSION = "1.0.0";

    String DEFINITION_VERIFICATION = "http://dd.eionet.europa.eu/vocabularies/aq/observationverification";

    String DEFINITION_VALIDITY = "http://dd.eionet.europa.eu/vocabularies/aq/observationvalidity";
    
    String DEFINITION_DATA_CAPTURE = "http://dd.eionet.europa.eu/vocabulary/aq/primaryObservation/dc";
    
    String DEFINITION_UOM_STATISTICS_PERCENTAGE = "http://dd.eionet.europa.eu/vocabularyconcept/uom/statistics/percentage";

    String NAME_FIXED_OBSERVATIONS = "FixedObservations";

    String NS_AQD = "http://dd.eionet.europa.eu/schemaset/id2011850eu-1.0";

    String NS_AQD_PREFIX = "aqd";

    String NS_AQD_SCHEMA = "http://dd.eionet.europa.eu/schemas/id2011850eu-1.0/AirQualityReporting.xsd";

    SchemaLocation NS_AQD_SCHEMA_LOCATION = new SchemaLocation(NS_AQD, NS_AQD_SCHEMA);

    String NS_AD = "urn:x-inspire:specification:gmlas:Addresses:3.0";

    String NS_AD_PREFIX = "ad";

    String NS_AM = "http://inspire.ec.europa.eu/schemas/am/3.0";

    String NS_AM_PREFIX = "am";

    String NS_AU = "urn:x-inspire:specification:gmlas:AdministrativeUnits:3.0";

    String NS_AU_PREFIX = "au";

    String NS_BASE = "http://inspire.ec.europa.eu/schemas/base/3.3";

    String NS_BASE_PREFIX = "base";

    String NS_BASE2 = "http://inspire.ec.europa.eu/schemas/base2/1.0";

    String NS_BASE2_PREFIX = "base2";

    String NS_EF = "http://inspire.ec.europa.eu/schemas/ef/3.0";

    String NS_EF_PREFIX = "ef";

    String NS_GN = "urn:x-inspire:specification:gmlas:GeographicalNames:3.0";

    String NS_GN_PREFIX = "gn";

    String NS_OMPR = "http://inspire.ec.europa.eu/schemas/ompr/2.0";

    String NS_OMPR_PREFIX = "ompr";

    Map<String, String> NAMESPACE_PREFIX_MAP = ImmutableMap.<String, String> builder()
            .put(NS_AQD, NS_AQD_PREFIX)
            .put(NS_AD, NS_AD_PREFIX)
            .put(NS_AM, NS_AM_PREFIX)
            .put(NS_AU, NS_AU_PREFIX)
            .put(NS_BASE, NS_BASE_PREFIX)
            .put(NS_BASE2, NS_BASE2_PREFIX)
            .put(NS_EF, NS_EF_PREFIX)
            .put(NS_GN, NS_GN_PREFIX)
            .put(NS_OMPR, NS_OMPR_PREFIX)
            .put(GcoConstants.NS_GCO, GcoConstants.NS_GCO_PREFIX)
            .put(GmdConstants.NS_GMD, GmdConstants.NS_GMD_PREFIX)
            .build();

    String AN_CODE_SPACE = "codeSpace";

    String AN_NIL_REASON = "nilReason";

    String EN_ADDRESS = "address";

    String EN_ADDRESS_AREA = "addressArea";

    String EN_ADDRESS_FEATURE = "addressFeature";

    String EN_ADDRESS_REPRESENTATION = "AddressRepresentation";

    String EN_ADMIN_UNIT = "adminUnit";

    String EN_CHANGE = "change";

    String EN_CHANGE_DESCRIPTION = "changeDescription";

    String EN_CONTACT = "contact";

    String EN_C_ONTACT = "Contact";

    String EN_CONTACT_INSTRUCTIONS = "contactInstructions";

    String EN_CONTENT = "content";

    String EN_DELETE = "delete";

    String EN_ELECTRONIC_MAIL_ADDRESS = "electronicMailAddress";

    String EN_GEOGRAPHICAL_NAME = "GeographicalName";

    String EN_GRAMMATICAL_GENDER = "grammaticalGender";

    String EN_GRAMMATICAL_NUMBER = "grammaticalNumber";

    String EN_HOURS_OF_SERVICE = "hoursOfService";

    String EN_IDENTIFIER = "Identifier";

    String EN_INDIVIDUAL_NAME = "individualName";

    String EN_INSPIRE_ID = "inspireId";

    String EN_LANGUAGE = "language";

    String EN_LOCAL_ID = "localId";

    String EN_LOCATOR_DESIGNATOR = "locatorDesignator";

    String EN_LOCATOR_NAME = "locatorName";

    String EN_NAME_STATUS = "nameStatus";

    String EN_NAMESPACE = "namespace";

    String EN_NATIVENESS = "nativeness";

    String EN_ORGANISATION_NAME = "organisationName";

    String EN_POSITION_NAME = "positionName";

    String EN_POST_CODE = "postCode";

    String EN_POST_NAME = "postName";

    String EN_PRONUNCIATION = "pronunciation";

    String EN_PRONUNCIATION_IPA = "pronunciationIPA";

    String EN_PRONUNCIATION_OF_NAME = "PronunciationOfName";

    String EN_PRONUNCIATION_SOUND_LINK = "pronunciationSoundLink";

    String EN_RELATED_PARTY = "RelatedParty";

    String EN_REPORTING_AUTHORITY = "reportingAuthority";

    String EN_REPORTING_HEADER = "AQD_ReportingHeader";

    String EN_REPORTING_PERIOD = "reportingPeriod";

    String EN_ROLE = "role";

    String EN_SCRIPT = "script";

    String EN_SOURCE_OF_NAME = "sourceOfName";

    String EN_SPELLING = "spelling";

    String EN_SPELLING_OF_NAME = "SpellingOfName";

    String EN_TELEPHONE_FACSIMILE = "telephoneFacsimile";

    String EN_TELEPHONE_VOICE = "telephoneVoice";

    String EN_TEXT = "text";

    String EN_THOROUGHFARE = "thoroughfare";

    String EN_TRANSLITERATION_SCHEME = "transliterationScheme";

    String EN_VERSION_ID = "versionId";

    String EN_WEBSITE = "website";

    QName QN_NIL_REASON = new QName(AN_NIL_REASON);

    QName QN_AD_ADDRESS_AREA = new QName(NS_AD, EN_ADDRESS_AREA, NS_AD_PREFIX);

    QName QN_AD_ADDRESS_FEATURE = new QName(NS_AD, EN_ADDRESS_FEATURE, NS_AD_PREFIX);

    QName QN_AD_ADDRESS_REPRESENTATION = new QName(NS_AD, EN_ADDRESS_REPRESENTATION, NS_AD_PREFIX);

    QName QN_AD_ADMIN_UNIT = new QName(NS_AD, EN_ADMIN_UNIT, NS_AD_PREFIX);

    QName QN_AD_LOCATOR_DESIGNATOR = new QName(NS_AD, EN_LOCATOR_DESIGNATOR, NS_AD_PREFIX);

    QName QN_AD_LOCATOR_NAME = new QName(NS_AD, EN_LOCATOR_NAME, NS_AD_PREFIX);

    QName QN_AD_POST_CODE = new QName(NS_AD, EN_POST_CODE, NS_AD_PREFIX);

    QName QN_AD_POST_NAME = new QName(NS_AD, EN_POST_NAME, NS_AD_PREFIX);

    QName QN_AD_THOROUGHFARE = new QName(NS_AD, EN_THOROUGHFARE, NS_AD_PREFIX);

    QName QN_AQD_CHANGE = new QName(NS_AQD, EN_CHANGE, NS_AQD_PREFIX);

    QName QN_AQD_CHANGE_DESCRIPTION = new QName(NS_AQD, EN_CHANGE_DESCRIPTION, NS_AQD_PREFIX);

    QName QN_AQD_CONTENT = new QName(NS_AQD, EN_CONTENT, NS_AQD_PREFIX);

    QName QN_AQD_DELETE = new QName(NS_AQD, EN_DELETE, NS_AQD_PREFIX);

    QName QN_AQD_INSPIRE_ID = new QName(NS_AQD, EN_INSPIRE_ID, NS_AQD_PREFIX);

    QName QN_AQD_REPORTING_AUTHORITY = new QName(NS_AQD, EN_REPORTING_AUTHORITY, NS_AQD_PREFIX);

    QName QN_AQD_REPORTING_HEADER = new QName(NS_AQD, EN_REPORTING_HEADER, NS_AQD_PREFIX);

    QName QN_AQD_REPORTING_PERIOD = new QName(NS_AQD, EN_REPORTING_PERIOD, NS_AQD_PREFIX);

    QName QN_BASE_IDENTIFIER = new QName(NS_BASE, EN_IDENTIFIER, NS_BASE_PREFIX);

    QName QN_BASE_LOCAL_ID = new QName(NS_BASE, EN_LOCAL_ID, NS_BASE_PREFIX);

    QName QN_BASE_NAMESPACE = new QName(NS_BASE, EN_NAMESPACE, NS_BASE_PREFIX);

    QName QN_BASE_VERSION_ID = new QName(NS_BASE, EN_VERSION_ID, NS_BASE_PREFIX);

    QName QN_BASE2_ADDRESS = new QName(NS_BASE2, EN_ADDRESS, NS_BASE2_PREFIX);

    QName QN_BASE2_CONTACT = new QName(NS_BASE2, EN_CONTACT, NS_BASE2_PREFIX);

    QName QN_BASE2_C_ONTACT = new QName(NS_BASE2, EN_C_ONTACT, NS_BASE2_PREFIX);

    QName QN_BASE2_CONTACT_INSTRUCTIONS = new QName(NS_BASE2, EN_CONTACT_INSTRUCTIONS, NS_BASE2_PREFIX);

    QName QN_BASE2_ELECTRONIC_MAIL_ADDRESS = new QName(NS_BASE2, EN_ELECTRONIC_MAIL_ADDRESS, NS_BASE2_PREFIX);

    QName QN_BASE2_HOURS_OF_SERVICE = new QName(NS_BASE2, EN_HOURS_OF_SERVICE, NS_BASE2_PREFIX);

    QName QN_BASE2_INDIVIDUAL_NAME = new QName(NS_BASE2, EN_INDIVIDUAL_NAME, NS_BASE2_PREFIX);

    QName QN_BASE2_ORGANISATION_NAME = new QName(NS_BASE2, EN_ORGANISATION_NAME, NS_BASE2_PREFIX);

    QName QN_BASE2_POSITION_NAME = new QName(NS_BASE2, EN_POSITION_NAME, NS_BASE2_PREFIX);

    QName QN_BASE2_RELATED_PARTY = new QName(NS_BASE2, EN_RELATED_PARTY, NS_BASE2_PREFIX);

    QName QN_BASE2_ROLE = new QName(NS_BASE2, EN_ROLE, NS_BASE2_PREFIX);

    QName QN_BASE2_TELEPHONE_FACSIMILE = new QName(NS_BASE2, EN_TELEPHONE_FACSIMILE, NS_BASE2_PREFIX);

    QName QN_BASE2_TELEPHONE_VOICE = new QName(NS_BASE2, EN_TELEPHONE_VOICE, NS_BASE2_PREFIX);

    QName QN_BASE2_WEBSITE = new QName(NS_BASE2, EN_WEBSITE, NS_BASE2_PREFIX);

    QName QN_GN_GEOGRAPHICAL_NAME = new QName(NS_GN, EN_GEOGRAPHICAL_NAME, NS_GN_PREFIX);

    QName QN_GN_GRAMMATICAL_GENDER = new QName(NS_GN, EN_GRAMMATICAL_GENDER, NS_GN_PREFIX);

    QName QN_GN_GRAMMATICAL_NUMBER = new QName(NS_GN, EN_GRAMMATICAL_NUMBER, NS_GN_PREFIX);

    QName QN_GN_LANGUAGE = new QName(NS_GN, EN_LANGUAGE, NS_GN_PREFIX);

    QName QN_GN_NAME_STATUS = new QName(NS_GN, EN_NAME_STATUS, NS_GN_PREFIX);

    QName QN_GN_NATIVENESS = new QName(NS_GN, EN_NATIVENESS, NS_GN_PREFIX);

    QName QN_GN_PRONUNCIATION = new QName(NS_GN, EN_PRONUNCIATION, NS_GN_PREFIX);

    QName QN_GN_PRONUNCIATION_IPA = new QName(NS_GN, EN_PRONUNCIATION_IPA, NS_GN_PREFIX);

    QName QN_GN_PRONUNCIATION_OF_NAME = new QName(NS_GN, EN_PRONUNCIATION_OF_NAME, NS_GN_PREFIX);

    QName QN_GN_PRONUNCIATION_SOUND_LINK = new QName(NS_GN, EN_PRONUNCIATION_SOUND_LINK, NS_GN_PREFIX);

    QName QN_GN_SCRIPT = new QName(NS_GN, EN_SCRIPT, NS_GN_PREFIX);

    QName QN_GN_SOURCE_OF_NAME = new QName(NS_GN, EN_SOURCE_OF_NAME, NS_GN_PREFIX);

    QName QN_GN_SPELLING = new QName(NS_GN, EN_SPELLING, NS_GN_PREFIX);

    QName QN_GN_SPELLING_OF_NAME = new QName(NS_GN, EN_SPELLING_OF_NAME, NS_GN_PREFIX);

    QName QN_GN_TEXT = new QName(NS_GN, EN_TEXT, NS_GN_PREFIX);

    QName QN_GN_TRANSLITERATION_SCHEME = new QName(NS_GN, EN_TRANSLITERATION_SCHEME, NS_GN_PREFIX);
    
    String HOUR = "hour";
    
    String DAY = "day";
    
    String WEEK = "week";

    String FORTNIGHT = "fortnight";

    String MONTH = "month";

    String QUARTER = "quarter";
    
    String VAR = "var";

    String EXTENSION_FLOW = "flow";
    
    enum PrimaryObservation {
        Hourly(HOUR),
        Daily(DAY),
        Weekly(WEEK),
        Fortnightly(FORTNIGHT),
        Monthly(MONTH),
        Quarterly(QUARTER),
        Variable(VAR);

        private static final String baseURI = "http://dd.eionet.europa.eu/vocabulary/aq/primaryObservation/";

        private final String id;

        private final String conceptURI;
        
        private static final Set<String> multiDay = Sets.newHashSet(WEEK, FORTNIGHT, MONTH, QUARTER);

        PrimaryObservation(String id) {
            this.id = id;
            this.conceptURI = baseURI + id;
        }

        public String getId() {
            return id;
        }

        public String getConceptURI() {
            return conceptURI;
        }
        
        public static PrimaryObservation from(String v) {
            for (PrimaryObservation c : PrimaryObservation.values()) {
                if (c.getConceptURI().equals(v) || c.getId().equals(v)) {
                    return c;
                }
            }
            throw new IllegalArgumentException(v);
        }

        public static PrimaryObservation fromConceptURI(String v) {
            for (PrimaryObservation c : PrimaryObservation.values()) {
                if (c.getConceptURI().equals(v)) {
                    return c;
                }
            }
            throw new IllegalArgumentException(v);
        }

        public static PrimaryObservation fromId(String v) {
            for (PrimaryObservation c : PrimaryObservation.values()) {
                if (c.getId().equals(v)) {
                    return c;
                }
            }
            throw new IllegalArgumentException(v);
        }

        public boolean isMultyDayPrimaryObservation() {
          return multiDay.contains(this.id);
        }
    }

    enum ProcessParameter {
        AssessmentType("AssessmentType"), 
        CalibrationSamplingPointsOther("CAL-SPother"),
        CalibrationSamplingPoints("CAL-SPs"), 
        EnvironmentalObjective("EO"), 
        Model("model"), 
        ObjectiveEstimation("objective-estimation"), 
        PrimaryObservationTime("primaryObsTime"), 
        SamplingPoint("SamplingPoint"), 
        MonitoringStation("Station"),
        Network("Network"),
        VerificationSamplingPoints("VER-SP"), 
        VerificationSamplingPointsOther("VER-SPother"),
        Unknown("Unknown");

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
        
        public static ProcessParameter from(String v) {
            for (ProcessParameter p : ProcessParameter.values()) {
                if (p.getConceptURI().equals(v) || p.getId().equals(v)) {
                    return p;
                }
            }
            throw new IllegalArgumentException(v);
        }

        public static ProcessParameter fromOrUnknown(String v) {
            for (ProcessParameter p : ProcessParameter.values()) {
                if (p.getConceptURI().equals(v) || p.getId().equals(v)) {
                    return p;
                }
            }
            return Unknown;
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
