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

import java.util.Collection;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import org.n52.iceland.ogc.filter.SpatialFilter;
import org.n52.iceland.util.CollectionHelper;
import org.n52.iceland.util.Constants;
import org.n52.iceland.util.StringHelper;

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
