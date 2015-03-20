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
package org.n52.sos.ogc.sos;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.SortedMap;
import java.util.SortedSet;
import java.util.TreeMap;
import java.util.TreeSet;

import javax.xml.namespace.QName;

import org.n52.sos.ogc.gml.time.Time;
import org.n52.sos.ogc.swes.AbstractSWES;
import org.n52.sos.util.CollectionHelper;
import org.n52.sos.util.Constants;
import org.n52.sos.util.QNameComparator;

/**
 * Class which represents a ObservationOffering. Used in the SosCapabilities.
 * 
 * @since 4.0.0
 */
public class SosObservationOffering extends AbstractSWES implements Comparable<SosObservationOffering> {

    private static final long serialVersionUID = -9094472499167970506L;

    /**
     * add collection to sorted set
     * 
     * @param set
     * @param coll
     */
    private static <T> void set(SortedSet<T> set, Collection<? extends T> coll) {
        if (set != null) {
            set.clear();
            if (coll != null) {
                set.addAll(coll);
            }
        }
    }

    /**
     * Add key and value to map
     * 
     * @param map
     * @param key
     * @param value
     */
    private static <K, V> void addToMap(SortedMap<K, SortedSet<V>> map, K key, V value) {
        if (map != null && key != null && value != null) {
            SortedSet<V> set = map.get(key);
            if (set == null) {
                set = new TreeSet<V>();
                map.put(key, set);
            }
            set.add(value);
        }
    }

    /**
     * Add key and values to map
     * 
     * @param map
     * @param key
     * @param value
     */
    private static <K, V> void addToMap(SortedMap<K, SortedSet<V>> map, K key, Collection<V> value) {
        if (map != null && key != null && value != null) {
            SortedSet<V> set = map.get(key);
            if (set == null) {
                set = new TreeSet<V>();
                map.put(key, set);
            }
            set.addAll(value);
        }
    }

    /**
     * Add map to sorted map
     * 
     * @param sortedMap
     * @param map
     */
    private static <K, V> void set(SortedMap<K, SortedSet<V>> sortedMap, Map<K, ? extends Collection<V>> map) {
        if (sortedMap != null) {
            sortedMap.clear();
            if (map != null) {
                for (Entry<K, ? extends Collection<V>> e : map.entrySet()) {
                    sortedMap.put(e.getKey(), e.getValue() != null ? new TreeSet<V>(e.getValue()) : new TreeSet<V>());
                }
            }
        }
    }

    /**
     * offering identifier for this contents sub section
     */
    private SosOffering offering;

    /**
     * area observed by this offering
     */
    private SosEnvelope observedArea;

    /**
     * All observableProperties contained in the offering
     */
    private SortedSet<String> observableProperties = new TreeSet<String>();

    /**
     * All compositePhenomenon contained in the offering
     */
    private SortedSet<String> compositePhenomena = new TreeSet<String>();

    /**
     * All phenomenon for compositePhenomenon contained in the offering
     */
    private SortedMap<String, SortedSet<String>> phens4CompPhens = new TreeMap<String, SortedSet<String>>();

    /**
     * TimePeriod of data in the offering
     */
    private Time phenomenonTime;

    /**
     * Result TimePeriod of data in the offering
     */
    private Time resultTime;

    /**
     * All featuresOfinterest contained in the offering
     */
    private final SortedSet<String> featureOfInterest = new TreeSet<String>();

    /**
     * All related features contained in the offering
     */
    private final SortedMap<String, SortedSet<String>> relatedFeatures = new TreeMap<String, SortedSet<String>>();

    /**
     * All procedures contained in the offering
     */
    private final SortedSet<String> procedures = new TreeSet<String>();

    /**
     * All resultModels contained in the offering
     */
    private final SortedSet<QName> resultModels = new TreeSet<QName>(new QNameComparator());

    /**
     * All observation types contained in the offering
     */
    private final SortedSet<String> observationTypes = new TreeSet<String>();

    /**
     * All featureOfInterest types contained in the offering
     */
    private final SortedSet<String> featureOfInterestTypes = new TreeSet<String>();

    /**
     * All observation result types contained in the offering
     */
    private final SortedMap<String, SortedSet<String>> observationResultTypes =
            new TreeMap<String, SortedSet<String>>();

    /**
     * All response formats contained in the offering
     */
    private final SortedSet<String> responseFormats = new TreeSet<String>();

    /**
     * All response modes contained in the offering
     */
    private final SortedSet<String> responseModes = new TreeSet<String>();

    /**
     * All procedure description formats contained in the offering
     */
    private final SortedSet<String> procedureDescriptionFormats = new TreeSet<String>();
	
    /**
     * @return Offering identifier
     */
    /**
     * @return
     */
    public SosOffering getOffering() {
        return offering;
    }
    
    /**
     * @param offering
     *            Offering identifier
     */
    public void setOffering(SosOffering offering) {
        this.offering = offering;
        if (!isSetIdentifier() && offering.isSetIdentifier()) {
            this.setIdentifier(offering.getIdentifier());
        }
        if (!isSetName() && offering.isSetName()) {
            this.setName(offering.getName());
        }
        if (!isSetDescription() && offering.isSetDescription()) {
            this.setDescription(offering.getDescription());
        }
    }

    /**
     * @param offering
     *            Offering identifier
     */
    public void setOffering(String offering) {
        setOffering(new SosOffering(offering, Constants.EMPTY_STRING));
    }
    
    /**
     * @return Sorted observableProperties set
     */
    public SortedSet<String> getObservableProperties() {
        return Collections.unmodifiableSortedSet(observableProperties);
    }

    /**
     * @param observableProperties
     */
    public void setObservableProperties(Collection<String> observableProperties) {
        set(this.observableProperties, observableProperties);
    }

    /**
     * @return Sorted composite phenomena set
     */
    public SortedSet<String> getCompositePhenomena() {
        return Collections.unmodifiableSortedSet(compositePhenomena);
    }

    /**
     * @param compositePhenomena
     */
    public void setCompositePhenomena(Collection<String> compositePhenomena) {
        set(this.compositePhenomena, compositePhenomena);
    }

    /**
     * @return Sorted map containing the observableProperties and related
     *         composite phenomena
     */
    public SortedMap<String, SortedSet<String>> getPhens4CompPhens() {
        return Collections.unmodifiableSortedMap(phens4CompPhens);
    }

    /**
     * @param phens4CompPhens
     */
    public void setPhens4CompPhens(Map<String, Collection<String>> phens4CompPhens) {
        set(this.phens4CompPhens, phens4CompPhens);
    }

    /**
     * @param phenomenonTime
     *            the phenomenon time
     */
    public void setPhenomenonTime(Time phenomenonTime) {
        this.phenomenonTime = phenomenonTime;
    }

    /**
     * @return the phenomenon time
     */
    public Time getPhenomenonTime() {
        return phenomenonTime;
    }

    /**
     * @param resultTime
     *            the result time
     */
    public void setResultTime(Time resultTime) {
        this.resultTime = resultTime;
    }

    /**
     * @return the result time
     */
    public Time getResultTime() {
        return resultTime;
    }

    /**
     * @param featureOfInterest
     */
    public void setFeatureOfInterest(Collection<String> featureOfInterest) {
        set(this.featureOfInterest, featureOfInterest);
    }

    /**
     * @return Sorted featureOfInterest list
     */
    public SortedSet<String> getFeatureOfInterest() {
        return Collections.unmodifiableSortedSet(featureOfInterest);
    }

    /**
     * @param relatedFeatures
     */
    public void setRelatedFeatures(Map<String, Set<String>> relatedFeatures) {
        set(this.relatedFeatures, relatedFeatures);
    }

    /**
     * Add a related feature to this offering
     * 
     * @param identifier
     *            Related feature identifier
     * @param role
     *            Related feature role
     */
    public void addRelatedFeature(String identifier, String role) {
        addToMap(this.relatedFeatures, identifier, role);
    }

    /**
     * Add a related feature to this offering
     * 
     * @param identifier
     *            Related feature identifier
     * @param roles
     *            Related feature roles
     */
    public void addRelatedFeature(String identifier, Set<String> roles) {
        addToMap(this.relatedFeatures, identifier, roles);
    }

    /**
     * @return Sorted map of related features
     */
    public SortedMap<String, SortedSet<String>> getRelatedFeatures() {
        return Collections.unmodifiableSortedMap(relatedFeatures);
    }

    /**
     * @return Sorted procedure set
     */
    public SortedSet<String> getProcedures() {
        return Collections.unmodifiableSortedSet(procedures);
    }

    /**
     * @param procedures
     */
    public void setProcedures(Collection<String> procedures) {
        set(this.procedures, procedures);
    }

    /**
     * @return Sorted result models set
     */
    public SortedSet<QName> getResultModels() {
        return Collections.unmodifiableSortedSet(resultModels);
    }

    /**
     * @param resultModels
     */
    public void setResultModels(Collection<QName> resultModels) {
        set(this.resultModels, resultModels);
    }

    /**
     * @return Sorted observation types set
     */
    public SortedSet<String> getObservationTypes() {
        return Collections.unmodifiableSortedSet(observationTypes);
    }

    /**
     * @param observationTypes
     *            the observationTypes to set
     */
    public void setObservationTypes(Collection<String> observationTypes) {
        set(this.observationTypes, observationTypes);
    }

    /**
     * @return the observationResultTypes
     */
    public SortedMap<String, SortedSet<String>> getObservationResultTypes() {
        return Collections.unmodifiableSortedMap(observationResultTypes);
    }

    /**
     * @param observationResultTypes
     *            the observationResultTypes to set
     */
    public void setObservationResultTypes(Map<String, Collection<String>> observationResultTypes) {
        set(this.observationResultTypes, observationResultTypes);
    }

    /**
     * @return Sorted response formats set
     */
    public SortedSet<String> getResponseFormats() {
        return Collections.unmodifiableSortedSet(responseFormats);
    }

    /**
     * @param responseFormats
     */
    public void setResponseFormats(Collection<String> responseFormats) {
        set(this.responseFormats, responseFormats);
    }

    /**
     * @return Sorted response mode set
     */
    public SortedSet<String> getResponseModes() {
        return Collections.unmodifiableSortedSet(responseModes);
    }

    /**
     * @param responseModes
     */
    public void setResponseModes(Collection<String> responseModes) {
        set(this.responseModes, responseModes);
    }

    public SosEnvelope getObservedArea() {
        return observedArea;
    }

    public void setObservedArea(SosEnvelope observedArea) {
        this.observedArea = observedArea;
    }

    public void setFeatureOfInterestTypes(Collection<String> featureOfInterestTypes) {
        set(this.featureOfInterestTypes, featureOfInterestTypes);
    }

    public SortedSet<String> getFeatureOfInterestTypes() {
        return Collections.unmodifiableSortedSet(featureOfInterestTypes);
    }

    public void setProcedureDescriptionFormat(Collection<String> procedureDescriptionFormats) {
        set(this.procedureDescriptionFormats, procedureDescriptionFormats);
    }

    public SortedSet<String> getProcedureDescriptionFormats() {
        return Collections.unmodifiableSortedSet(this.procedureDescriptionFormats);
    }

    @Override
    public int compareTo(SosObservationOffering o) {
        if (o == null) {
            throw new NullPointerException();
        }

        if (getOffering() == null ^ o.getOffering() == null) {
            return (getOffering() == null) ? -1 : 1;
        }

        if (getOffering() == null && o.getOffering() == null) {
            return 0;
        }

        return getOffering().compareTo(o.getOffering());
    }

    public boolean isEmpty() {
        return !isSetOffering() && !isSetObservedArea() && !isSetObservableProperties()
                && !isSetCompositePhenomena() && !isSetPhens4CompPhens() && !isSetPhenomenonTime()
                && !isSetResultTime() && !isSetFeatureOfInterest() && !isSetRelatedFeature() && !isSetProcedures()
                && !isSetresultModels() && !isSetObservationTypes() && !isSetFeatureOfInterestTypes()
                && !isSetObservationResultTypes() && !isSetResponseFormats() && !isSetResponseModes()
                && !isSetProcedureDescriptionFormats();
    }

    public boolean isValidObservationOffering() {
        return isSetOffering() && isSetProcedures();
    }

    public boolean isSetOffering() {
        return getOffering() != null;
    }

    public boolean isSetObservedArea() {
        return getObservedArea() != null && getObservedArea().isSetEnvelope();
    }

    public boolean isSetObservableProperties() {
        return CollectionHelper.isNotEmpty(getObservableProperties());
    }

    public boolean isSetCompositePhenomena() {
        return CollectionHelper.isNotEmpty(getCompositePhenomena());
    }

    public boolean isSetPhens4CompPhens() {
        return CollectionHelper.isNotEmpty(getPhens4CompPhens());
    }

    public boolean isSetPhenomenonTime() {
        return getPhenomenonTime() != null;
    }

    public boolean isSetResultTime() {
        return getResultTime() != null;
    }

    public boolean isSetFeatureOfInterest() {
        return  CollectionHelper.isNotEmpty(getFeatureOfInterest());
    }

    public boolean isSetRelatedFeature() {
        return  CollectionHelper.isNotEmpty(getRelatedFeatures());
    }

    public boolean isSetProcedures() {
        return  CollectionHelper.isNotEmpty(getProcedures());
    }

    public boolean isSetresultModels() {
        return  CollectionHelper.isNotEmpty(getResultModels());
    }

    public boolean isSetObservationTypes() {
        return  CollectionHelper.isNotEmpty(getObservationTypes());
    }

    public boolean isSetFeatureOfInterestTypes() {
        return  CollectionHelper.isNotEmpty(getFeatureOfInterestTypes());
    }

    private boolean isSetObservationResultTypes() {
        return  CollectionHelper.isNotEmpty(getObservationResultTypes());
    }

    public boolean isSetResponseFormats() {
        return  CollectionHelper.isNotEmpty(getResponseFormats());
    }

    private boolean isSetResponseModes() {
        return  CollectionHelper.isNotEmpty(getResponseModes());
    }

    public boolean isSetProcedureDescriptionFormats() {
        return  CollectionHelper.isNotEmpty(getProcedureDescriptionFormats());
    }

    @Override
    public String toString() {
        return "SosObservationOffering [offering=" + offering + "]";
    }
}
