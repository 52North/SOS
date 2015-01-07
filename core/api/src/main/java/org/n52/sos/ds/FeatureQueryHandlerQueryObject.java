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

import java.util.Collection;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import org.n52.sos.ogc.filter.SpatialFilter;
import org.n52.sos.util.CollectionHelper;
import org.n52.sos.util.Constants;
import org.n52.sos.util.StringHelper;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

public class FeatureQueryHandlerQueryObject {

    private Object connection;

//    private int requestedSrid = Integer.MIN_VALUE;

    private Locale i18n;

    private List<SpatialFilter> spatialFilters = Lists.newArrayList();

    private Set<String> featureIdentifiers = Sets.newHashSet();

    private String version;

    public Object getConnection() {
        return connection;
    }

    public FeatureQueryHandlerQueryObject setConnection(Object connection) {
        this.connection = connection;
                return this;
    }

//    public int getRequestedSrid() {
//        return requestedSrid;
//    }
//
//    public FeatureQueryHandlerQueryObject setRequestedSrid(int requestedSrid) {
//        this.requestedSrid = requestedSrid;
//        return this;
//    }

    public Locale getI18N() {
        return i18n;
    }

    public FeatureQueryHandlerQueryObject setI18N(Locale i18n) {
        this.i18n = i18n;
        return this;
    }

    public List<SpatialFilter> getSpatialFilters() {
        return spatialFilters;
    }

    public FeatureQueryHandlerQueryObject setSpatialFilters(List<SpatialFilter> spatialFilters) {
        if (CollectionHelper.isNotEmpty(spatialFilters)) {
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

    public Set<String> getFeatureIdentifiers() {
        return featureIdentifiers;
    }

    public FeatureQueryHandlerQueryObject setFeatureIdentifiers(Collection<String> featureIdentifiers) {
        if (CollectionHelper.isNotEmpty(featureIdentifiers)) {
            this.featureIdentifiers.addAll(featureIdentifiers);
        }
        return this;
    }

    public FeatureQueryHandlerQueryObject addFeatureIdentifier(String identifier) {
        if (StringHelper.isNotEmpty(identifier)) {
            featureIdentifiers.add(identifier);
        }
        return this;
    }

    public String getFeatureIdentifier() {
        if (isSetFeatureIdentifiers() && getFeatureIdentifiers().size() == 1) {
            return getFeatureIdentifiers().iterator().next();
        }
        return Constants.EMPTY_STRING;
    }

    public FeatureQueryHandlerQueryObject setVersion(String version) {
        this.version = version;
        return this;
    }

    public String getVersion() {
        return version;
    }

    public boolean isSetConnection() {
        return getConnection() != null;
    }

//    public boolean isSetRequestedSrid() {
//        return requestedSrid > 0;
//    }

    public boolean isSetI18N() {
        return getI18N() != null;
    }

    public boolean isSetSpatialFilters() {
        return CollectionHelper.isNotEmpty(getSpatialFilters());
    }

    public boolean isSetFeatureIdentifiers() {
        return CollectionHelper.isNotEmpty(getFeatureIdentifiers());
    }

    public boolean isSetVersion() {
        return StringHelper.isNotEmpty(getVersion());
    }
}
