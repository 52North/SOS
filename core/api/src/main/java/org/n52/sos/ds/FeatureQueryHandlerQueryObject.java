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
package org.n52.sos.ds;

import java.util.Collection;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import org.n52.shetland.ogc.filter.SpatialFilter;

import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

public class FeatureQueryHandlerQueryObject {

    private Object connection;

    private Locale i18n;

    private List<SpatialFilter> spatialFilters = Lists.newArrayList();

    private Set<String> features = Sets.newHashSet();

    private Object feature;

    private String version;

    public FeatureQueryHandlerQueryObject(Object connection) {
        this.connection = connection;
    }

    public Object getConnection() {
        return connection;
    }

    /**
     * @return the features
     */
    public Set<String> getFeatures() {
        return features;
    }

    /**
     * @param features
     *            the features to set
     */
    public FeatureQueryHandlerQueryObject setFeatures(Collection<String> features) {
        this.features.clear();
        if (features != null) {
            this.features.addAll(features);
        }
        return this;
    }

    public FeatureQueryHandlerQueryObject addFeatures(Collection<String> features) {
        this.features.addAll(features);
        return this;
    }

    public FeatureQueryHandlerQueryObject addFeature(String feature) {
        this.features.add(feature);
        return this;
    }

    public boolean isSetFeatures() {
        return getFeatures() != null && !getFeatures().isEmpty();
    }

    public List<SpatialFilter> getSpatialFilters() {
        return spatialFilters;
    }

    public FeatureQueryHandlerQueryObject setSpatialFilters(List<SpatialFilter> spatialFilters) {
        this.spatialFilters.clear();
        if (spatialFilters != null) {
            this.spatialFilters.addAll(spatialFilters);
        }
        return this;
    }

    public FeatureQueryHandlerQueryObject addSpatialFilter(SpatialFilter spatialFilter) {
        if (spatialFilter != null) {
            spatialFilters.add(spatialFilter);
        }
        return this;
    }

    public SpatialFilter getSpatialFitler() {
        if (isSetSpatialFilters() && getSpatialFilters().size() == 1) {
            return getSpatialFilters().iterator().next();
        }
        return null;
    }

    public boolean isSetSpatialFilters() {
        return getSpatialFilters() != null && !getSpatialFilters().isEmpty();
    }

    public Locale getI18N() {
        return i18n;
    }

    public FeatureQueryHandlerQueryObject setI18N(Locale i18n) {
        this.i18n = i18n;
        return this;
    }

    public FeatureQueryHandlerQueryObject addFeatureIdentifier(String identifier) {
        if (!Strings.isNullOrEmpty(identifier)) {
            features.add(identifier);
        }
        return this;
    }

    public String getFeatureIdentifier() {
        if (!getFeatures().isEmpty()) {
            return getFeatures().iterator().next();
        }
        return "";
    }

    public FeatureQueryHandlerQueryObject setVersion(String version) {
        this.version = version;
        return this;
    }

    public String getVersion() {
        return version;
    }

    public boolean isSetVersion() {
        return !Strings.isNullOrEmpty(getVersion());
    }

    /**
     * @return the feature
     */
    public Object getFeatureObject() {
        return feature;
    }

    /**
     * @param feature the feature to set
     * @return this
     */
    public FeatureQueryHandlerQueryObject setFeatureObject(Object feature) {
        this.feature = feature;
        return this;
    }

    public boolean isSetFeatureObject() {
        return getFeatureObject() != null;
    }
}
