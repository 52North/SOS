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
package org.n52.iceland.ogc.filter;

import java.util.List;

import com.google.common.collect.Lists;

/**
 * SOS class for FES SortBy element
 * 
 * @author Carsten Hollmann <c.hollmann@52north.org>
 * 
 * @since 4.0.0
 *
 */
public class FesSortBy implements AbstractSortingClause {

    private List<FesSortProperty> sortProperties = Lists.newArrayList();

    
    public FesSortBy(FesSortProperty sortProperty) {
        super();
        addSortProperty(sortProperty);
    }
    
    public FesSortBy(List<FesSortProperty> sortProperties) {
        super();
        setSortProperties(sortProperties);
    }

    /**
     * @return the sortProperties
     */
    public List<FesSortProperty> getSortProperties() {
        return sortProperties;
    }

    public FesSortBy addSortProperty(FesSortProperty sortProperty) {
       getSortProperties().add(sortProperty);
       return this;
    }

    public FesSortBy addSortProperties(List<FesSortProperty> sortProperties) {
        getSortProperties().addAll(sortProperties);
        return this;
    }

    /**
     * @param sortProperties
     *            the sortProperties to set
     */
    private void setSortProperties(List<FesSortProperty> sortProperties) {
        this.sortProperties = sortProperties;
    }

}
