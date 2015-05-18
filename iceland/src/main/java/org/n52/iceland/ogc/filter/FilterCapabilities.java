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

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;
import java.util.SortedSet;
import java.util.TreeMap;
import java.util.TreeSet;

import javax.xml.namespace.QName;

import org.n52.iceland.ogc.filter.FilterConstants.ComparisonOperator;
import org.n52.iceland.ogc.filter.FilterConstants.SpatialOperator;
import org.n52.iceland.ogc.filter.FilterConstants.TimeOperator;
import org.n52.iceland.ogc.ows.OwsDomainType;
import org.n52.iceland.util.CollectionHelper;
import org.n52.iceland.util.QNameComparator;

import com.google.common.collect.Lists;

/**
 * SOS filter capabilities
 * 
 * @since 4.0.0
 */
public class FilterCapabilities {
    /**
     * Spatial operands list
     */
    private SortedSet<QName> spatialOperands = new TreeSet<QName>(QNameComparator.INSTANCE);

    /**
     * Spatial operators map
     */
    private SortedMap<SpatialOperator, SortedSet<QName>> spatialOperators =
            new TreeMap<SpatialOperator, SortedSet<QName>>();

    /**
     * Temporal operands list
     */
    private SortedSet<QName> temporalOperands = new TreeSet<QName>(QNameComparator.INSTANCE);

    /**
     * Temporal operators map
     */
    private SortedMap<TimeOperator, SortedSet<QName>> temporalOperators =
            new TreeMap<TimeOperator, SortedSet<QName>>();

    /**
     * Comparison operators list
     */
    private SortedSet<ComparisonOperator> comparisonOperators = new TreeSet<ComparisonOperator>();
    
    private List<OwsDomainType> conformance = Lists.newArrayList();

    /**
     * Get spatial operands
     * 
     * @return spatial operands
     */
    public SortedSet<QName> getSpatialOperands() {
        return Collections.unmodifiableSortedSet(spatialOperands);
    }

    /**
     * Set spatial operands
     * 
     * @param spatialOperands
     *            spatial operands
     */
    public void setSpatialOperands(Collection<QName> spatialOperands) {
        this.spatialOperands.clear();
        if (spatialOperands != null) {
            this.spatialOperands.addAll(spatialOperands);
        }
    }

    /**
     * Get spatial operators
     * 
     * @return spatial operators
     */
    public SortedMap<SpatialOperator, SortedSet<QName>> getSpatialOperators() {
        return Collections.unmodifiableSortedMap(spatialOperators);
    }

    /**
     * Set spatial operators
     * 
     * @param spatialOperators
     *            spatial operators
     */
    public void setSpatialOperators(Map<SpatialOperator, ? extends Collection<QName>> spatialOperators) {
        this.spatialOperators.clear();
        if (spatialOperators != null) {
            for (SpatialOperator spatialOperator : spatialOperators.keySet()) {
                final TreeSet<QName> set = new TreeSet<QName>(QNameComparator.INSTANCE);
                if (spatialOperators.get(spatialOperator) != null) {
                    set.addAll(spatialOperators.get(spatialOperator));
                }
                this.spatialOperators.put(spatialOperator, set);
            }
        }
    }

    /**
     * Get temporal operands
     * 
     * @return temporal operands
     */
    public SortedSet<QName> getTemporalOperands() {
        return Collections.unmodifiableSortedSet(temporalOperands);
    }

    /**
     * Set temporal operands
     * 
     * @param temporalOperands
     *            temporal operands
     */
    public void setTemporalOperands(Collection<QName> temporalOperands) {
        this.temporalOperands.clear();
        if (temporalOperands != null) {
            this.temporalOperands.addAll(temporalOperands);
        }
    }

    /**
     * Get temporal operators
     * 
     * @return temporal operators
     */
    public SortedMap<TimeOperator, SortedSet<QName>> getTempporalOperators() {
        return Collections.unmodifiableSortedMap(temporalOperators);
    }

    /**
     * Set temporal operators
     * 
     * @param temporalOperators
     *            temporal operators
     */
    public void setTempporalOperators(Map<TimeOperator, ? extends Collection<QName>> temporalOperators) {
        this.temporalOperators.clear();
        if (temporalOperators != null) {
            for (TimeOperator timeOperator : temporalOperators.keySet()) {
                final TreeSet<QName> set = new TreeSet<QName>(QNameComparator.INSTANCE);
                if (temporalOperators.get(timeOperator) != null) {
                    set.addAll(temporalOperators.get(timeOperator));
                }
                this.temporalOperators.put(timeOperator, set);
            }
        }
    }

    /**
     * Get comparison operators
     * 
     * @return comparison operators
     */
    public SortedSet<ComparisonOperator> getComparisonOperators() {
        return Collections.unmodifiableSortedSet(comparisonOperators);
    }

    /**
     * Set comparison operators
     * 
     * @param comparisonOperators
     *            comparison operators
     */
    public void setComparisonOperators(Collection<ComparisonOperator> comparisonOperators) {
        this.comparisonOperators.clear();
        if (comparisonOperators != null) {
            this.comparisonOperators.addAll(comparisonOperators);
        }
    }

    public void addConformance(OwsDomainType domainType) {
        getConformance().add(domainType);
    }
    
    public void addConformance(Collection<OwsDomainType> domainTypes) {
        getConformance().addAll(domainTypes);
    }
    
    public void setConformance(Collection<OwsDomainType> domainTypes) {
        this.conformance = Lists.newArrayList(domainTypes);
    }
    
    public Collection<OwsDomainType> getConformance() {
        return conformance;
    }
    
    public boolean isSetCoinformance() {
        return CollectionHelper.isNotEmpty(getConformance());
    }
}
