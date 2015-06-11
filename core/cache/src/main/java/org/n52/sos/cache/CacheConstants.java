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
package org.n52.sos.cache;

/**
 * @since 4.0.0
 * 
 */
public interface CacheConstants {

    String OFFERING = "offering";

    String OFFERINGS = "offerings";

    String PROCEDURE = "procedure";

    String PROCEDURES = "procedures";

    String EPSG_CODE = "epsgCode";

    String EPSG_CODES = "epsgCodes";
    
    String SUPPORTED_LANGUAGE = "supportedLanguage";
    
    String SUPPORTED_LANGUAGES = "supportedLanguages";

    String FEATURE_OF_INTEREST = "featureOfInterest";

    String FEATURES_OF_INTEREST = "featuresOfInterest";

    String OBSERVATION_IDENTIFIER = "observationIdentifier";

    String OBSERVATION_IDENTIFIERS = "observationIdentifiers";

    String RESULT_TEMPLATE = "resultTemplate";

    String RESULT_TEMPLATES = "resultTemplates";

    String OBSERVABLE_PROPERTY = "observableProperty";

    String RELATED_FEATURE = "relatedFeature";

    String RELATED_FEATURES = "relatedFeatures";

    String NAME = "name";
    
    String DESCRIPTION = "description";
    
    String I18N = "language";
    
    String ALLOWED_OBSERVATION_TYPE = "allowedObservationType";

    String ALLOWED_OBSERVATION_TYPES = "allowedObservationTypes";
    
    String ALLOWED_FEATURE_OF_INTEREST_TYPE = "allowedFeatureOfInterestType";

    String ALLOWED_FEATURE_OF_INTEREST_TYPES = "allowedFeatureOfInterestTypes";

    String COMPOSITE_PHENOMENON = "compositePhenomenon";

    String OBSERVATION_TYPE = "observationType";
    
    String FEATURE_OF_INTEREST_TYPE = "featureOfInterestType";

    String ROLE = "role";

    String PARENT_FEATURE = "parentFeature";

    String PARENT_FEATURES = "parentFeatures";

    String PARENT_PROCEDURE = "parentProcedure";

    String PARENT_PROCEDURES = "parentProcedures";

    String ENVELOPE = "envelope";

    String EVENT_TIME = "eventTime";
    
    String NAME_UC = "Name";
    
    String FEATURE_OF_INTEREST_NAME = FEATURE_OF_INTEREST + NAME_UC;
    
    String OBSERVABLE_PROPERTY_NAME = OBSERVABLE_PROPERTY + NAME_UC;
    
    String PROCEDURE_NAME = PROCEDURE + NAME_UC;
    
    String OFFERING_NAME = OFFERING + NAME_UC;

}
