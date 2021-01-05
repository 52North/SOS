/*
 * Copyright (C) 2012-2021 52°North Initiative for Geospatial Open Source
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
package org.n52.sos.web.admin;

import java.util.Collection;
import java.util.Map;
import java.util.TreeMap;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.n52.iceland.cache.ContentCacheController;
import org.n52.sos.cache.SosContentCache;
import org.n52.sos.util.GeometryHandler;
import org.springframework.stereotype.Component;

/**
 * Class to get a summary of the cache objects.
 *
 * @since 4.0.0
 *
 */
@Component
@Singleton
public final class CacheSummaryHandler {

    public static final String LAST_UPDATE_TIME = "last_update";
    public static final String MIN_PHENOMENON_TIME = "min_phenomenon_time";
    public static final String MAX_PHENOMENON_TIME = "max_phenomenon_time";
    public static final String MIN_RESULT_TIME = "min_result_time";
    public static final String MAX_RESULT_TIME = "max_result_time";
    public static final String GLOBAL_ENVELOPE = "global_envelope";
    public static final String NUM_OFFERINGS = "num_offerings";
    public static final String NUM_PROCEDURES = "num_procedures";
    public static final String NUM_OBSERVABLE_PROPERTIES = "num_observable_properties";
    public static final String NUM_FEATURES_OF_INTEREST = "num_features_of_interest";
    public static final String NUM_FEATURE_OF_INTEREST_TYPES = "num_feature_of_interest_types";
    public static final String NUM_OBSERVATION_TYPES = "num_observation_types";
    public static final String NUM_RELATED_FEATURES = "num_related_features";
    public static final String NUM_RESULT_TEMPLATES = "num_result_templates";
    public static final String DEFAULT_EPSG = "default_epsg";
    public static final String NUM_EPSGS = "num_epsgs";

    @Inject
    private GeometryHandler geometryHandler;


    private CacheSummaryHandler() {

    }

    public Map<String, String> getCacheValues(ContentCacheController cacheController) {
        SosContentCache cache = (SosContentCache) cacheController.getCache();
        Map<String, String> values = new TreeMap<>();
        values.put(LAST_UPDATE_TIME, nullSafeToString(cache.getLastUpdateTime()));
        values.put(MIN_PHENOMENON_TIME, nullSafeToString(cache.getMinPhenomenonTime()));
        values.put(MAX_PHENOMENON_TIME, nullSafeToString(cache.getMaxPhenomenonTime()));
        values.put(MIN_RESULT_TIME, nullSafeToString(cache.getMinResultTime()));
        values.put(MAX_RESULT_TIME, nullSafeToString(cache.getMaxResultTime()));
        values.put(GLOBAL_ENVELOPE, nullSafeToString(cache.getGlobalEnvelope()));
        values.put(NUM_OFFERINGS, nullSafeToString(cache.getOfferings()));
        values.put(NUM_PROCEDURES, nullSafeToString(cache.getProcedures()));
        values.put(NUM_OBSERVABLE_PROPERTIES, nullSafeToString(cache.getObservableProperties()));
        values.put(NUM_FEATURES_OF_INTEREST, nullSafeToString(cache.getFeaturesOfInterest()));
        values.put(NUM_FEATURE_OF_INTEREST_TYPES, nullSafeToString(cache.getFeatureOfInterestTypes()));
        values.put(NUM_OBSERVATION_TYPES, nullSafeToString(cache.getObservationTypes()));
        values.put(NUM_RELATED_FEATURES, nullSafeToString(cache.getRelatedFeatures()));
        values.put(NUM_RESULT_TEMPLATES, nullSafeToString(cache.getResultTemplates()));
        values.put(DEFAULT_EPSG, Integer.toString(cache.getDefaultEPSGCode()));
        values.put(NUM_EPSGS, nullSafeToString(geometryHandler.getSupportedCRS()));
        return values;
    }

    private String nullSafeToString(Object obj) {
        if (obj == null) {
            return "null";
        } else if (obj instanceof Collection) {
            return Integer.toString(((Collection<?>) obj).size());
        } else {
            return obj.toString();
        }
    }
}
