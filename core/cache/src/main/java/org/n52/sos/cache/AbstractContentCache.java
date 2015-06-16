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

import static org.n52.sos.util.MultiMaps.newSynchronizedSetMultiMap;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.joda.time.DateTime;
import org.n52.sos.i18n.MultilingualString;
import org.n52.sos.ogc.gml.time.TimePeriod;
import org.n52.sos.ogc.sos.SosEnvelope;
import org.n52.sos.util.CollectionHelper;
import org.n52.sos.util.Constants;
import org.n52.sos.util.SetMultiMap;

import com.google.common.base.Objects;
import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.google.common.collect.Maps;
import com.vividsolutions.jts.geom.Envelope;

/**
 * Abstract {@code ContentCache} implementation that encapsulates the needed
 * {@code Map}s.
 *
 * @author Christian Autermann <c.autermann@52north.org>
 * @since 4.0.0
 */
public abstract class AbstractContentCache extends AbstractStaticContentCache {
    private static final long serialVersionUID = 5229487811485834059L;

    /**
     * Creates a new synchronized map from the specified map.
     *
     * @param <K>
     *            the key type
     * @param <V>
     *            the value type
     * @param map
     *            the map
     *
     * @return the synchronized map
     */
    protected static <K, V> Map<K, V> newSynchronizedMap(Map<K, V> map) {
        if (map == null) {
            return CollectionHelper.synchronizedMap(0);
        } else {
            return Collections.synchronizedMap(new HashMap<K, V>(map));
        }
    }

    /**
     * Creates a new synchronized set from the specified elements.
     *
     * @param <T>
     *            the element type
     * @param elements
     *            the elements
     *
     * @return the synchronized set
     */
    protected static <T> Set<T> newSynchronizedSet(Iterable<T> elements) {
        if (elements == null) {
            return CollectionHelper.synchronizedSet(0);
        } else {
            if (elements instanceof Collection) {
                return Collections.synchronizedSet(new HashSet<T>((Collection<T>) elements));
            } else {
                HashSet<T> hashSet = new HashSet<T>();
                for (T t : elements) {
                    hashSet.add(t);
                }
                return Collections.synchronizedSet(hashSet);
            }
        }
    }

    /**
     * Creates a new empty synchronized map.
     *
     * @param <K>
     *            the key type
     * @param <V>
     *            the value type
     *
     * @return the synchronized map
     */
    protected static <K, V> Map<K, V> newSynchronizedMap() {
        return newSynchronizedMap(null);
    }
    
    
    /**
     * Creates a new empty synchronized {@link BiMap}.
     *
     * @param <K>
     *            the key type
     * @param <V>
     *            the value type
     *
     * @return the synchronized map
     */
    protected static <K, V> BiMap<K, V> newSynchronizedBiMap() {
        return newSynchronizedBiMap(null);
    }
    
    /**
     * Creates a new synchronized map from the specified map.
     *
     * @param <K>
     *            the key type
     * @param <V>
     *            the value type
     * @param map
     *            the map
     *
     * @return the synchronized map
     */
    protected static <K, V> BiMap<K, V> newSynchronizedBiMap(BiMap<K, V> map) {
        if (map == null) {
            return Maps.synchronizedBiMap(HashBiMap.<K, V>create());
        } else {
            return Maps.synchronizedBiMap(map);
        }
    }
    

    /**
     * Creates a new empty synchronized set.
     *
     * @param <T>
     *            the element type
     *
     * @return a synchronized set
     */
    protected static <T> Set<T> newSynchronizedSet() {
        return newSynchronizedSet(null);
    }

    /**
     * Creates a unmodifiable copy of the specified set.
     *
     * @param <T>
     *            the element type
     * @param set
     *            the set
     *
     * @return a unmodifiable copy
     */
    protected static <T> Set<T> copyOf(Set<T> set) {
        if (set == null) {
            return Collections.emptySet();
        } else {
            return Collections.unmodifiableSet(new HashSet<T>(set));
        }
    }

    /**
     * Creates a copy of the specified envelope.
     *
     * @param e
     *            the envelope
     *
     * @return a copy
     */
    protected static SosEnvelope copyOf(SosEnvelope e) {
        if (e == null) {
            // TODO empty envelope
            return null;
        } else {
            return new SosEnvelope(e.getEnvelope() == null ? null : new Envelope(e.getEnvelope()), e.getSrid());
        }
    }

    /**
     * Throws a {@code NullPointerExceptions} if value is null or a
     * {@code IllegalArgumentException} if value is <= 0.
     *
     * @param name
     *            the name of the value
     * @param value
     *            the value to check
     *
     * @throws NullPointerException
     *             if value is null
     * @throws IllegalArgumentException
     *             if value is <= 0
     */
    protected static void greaterZero(String name, Integer value) throws NullPointerException,
            IllegalArgumentException {
        notNull(name, value);
        if (value <= 0) {
            throw new IllegalArgumentException(name + " may not less or equal 0!");
        }
    }

    /**
     * Throws a {@code NullPointerExceptions} if value is null or a
     * {@code IllegalArgumentException} if value is empty.
     *
     * @param name
     *            the name of the value
     * @param value
     *            the value to check
     *
     * @throws NullPointerException
     *             if value is null
     * @throws IllegalArgumentException
     *             if value is empty
     */
    protected static void notNullOrEmpty(String name, String value) throws NullPointerException,
            IllegalArgumentException {
        notNull(name, value);
        if (value.isEmpty()) {
            throw new IllegalArgumentException(name + " may not be empty!");
        }
    }

    /**
     * Throws a {@code NullPointerExceptions} if value is null or any value
     * within is null.
     *
     * @param name
     *            the name of the value
     * @param value
     *            the value to check
     *
     * @throws NullPointerException
     *             if value == null or value contains null
     */
    protected static void noNullValues(String name, Collection<?> value) throws NullPointerException {
        notNull(name, value);
        for (Object o : value) {
            if (o == null) {
                throw new NullPointerException(name + " may not contain null elements!");
            }
        }
    }

    /**
     * Throws a {@code NullPointerExceptions} if value is null or any value
     * within is null or empty.
     *
     * @param name
     *            the name of the value
     * @param value
     *            the value to check
     *
     * @throws NullPointerException
     *             if value == null or value contains null
     * @throws IllegalArgumentException
     *             if any value is empty
     */
    protected static void noNullOrEmptyValues(String name, Collection<String> value) throws NullPointerException,
            IllegalArgumentException {
        notNull(name, value);
        for (String o : value) {
            if (o == null) {
                throw new NullPointerException(name + " may not contain null elements!");
            }
            if (o.isEmpty()) {
                throw new IllegalArgumentException(name + " may not contain empty elements!");
            }
        }
    }

    /**
     * Throws a {@code NullPointerExceptions} if value is null or any key or
     * value within is null.
     *
     * @param name
     *            the name of the value
     * @param value
     *            the value to check
     *
     * @throws NullPointerException
     *             if value == null or value contains null values
     */
    protected static void noNullValues(String name, Map<?, ?> value) throws NullPointerException {
        notNull(name, value);
        for (Entry<?, ?> e : value.entrySet()) {
            if (e == null || e.getKey() == null || e.getValue() == null) {
                throw new NullPointerException(name + " may not contain null elements!");
            }
        }
    }

    /**
     * Throws a {@code NullPointerExceptions} if value is null.
     *
     * @param name
     *            the name of the value
     * @param value
     *            the value to check
     *
     * @throws NullPointerException
     *             if value == null
     */
    protected static void notNull(String name, Object value) throws NullPointerException {
        if (value == null) {
            throw new NullPointerException(name + " may not be null!");
        }
    }

    private int defaultEpsgCode = Constants.EPSG_WGS84;

    private Map<String, DateTime> maxPhenomenonTimeForOfferings = newSynchronizedMap();

    private Map<String, DateTime> minPhenomenonTimeForOfferings = newSynchronizedMap();

    private Map<String, DateTime> maxResultTimeForOfferings = newSynchronizedMap();

    private Map<String, DateTime> minResultTimeForOfferings = newSynchronizedMap();

    private Map<String, DateTime> maxPhenomenonTimeForProcedures = newSynchronizedMap();

    private Map<String, DateTime> minPhenomenonTimeForProcedures = newSynchronizedMap();

    private SetMultiMap<String, String> allowedObservationTypeForOfferings = newSynchronizedSetMultiMap();

    private SetMultiMap<String, String> allowedFeatureOfInterestTypeForOfferings = newSynchronizedSetMultiMap();

    private SetMultiMap<String, String> childFeaturesForFeatureOfInterest = newSynchronizedSetMultiMap();

    private SetMultiMap<String, String> childProceduresForProcedures = newSynchronizedSetMultiMap();

    private SetMultiMap<String, String> compositePhenomenonForOfferings = newSynchronizedSetMultiMap();

    private SetMultiMap<String, String> featuresOfInterestForOfferings = newSynchronizedSetMultiMap();

    private SetMultiMap<String, String> featuresOfInterestForResultTemplates = newSynchronizedSetMultiMap();

    private SetMultiMap<String, String> observablePropertiesForCompositePhenomenons = newSynchronizedSetMultiMap();

    private SetMultiMap<String, String> observablePropertiesForOfferings = newSynchronizedSetMultiMap();

    private SetMultiMap<String, String> observablePropertiesForProcedures = newSynchronizedSetMultiMap();

    private SetMultiMap<String, String> observationTypesForOfferings = newSynchronizedSetMultiMap();

    private SetMultiMap<String, String> featureOfInterestTypesForOfferings = newSynchronizedSetMultiMap();

    private SetMultiMap<String, String> observedPropertiesForResultTemplates = newSynchronizedSetMultiMap();

    private SetMultiMap<String, String> offeringsForObservableProperties = newSynchronizedSetMultiMap();

    private SetMultiMap<String, String> offeringsForProcedures = newSynchronizedSetMultiMap();

    private SetMultiMap<String, String> parentFeaturesForFeaturesOfInterest = newSynchronizedSetMultiMap();

    private SetMultiMap<String, String> parentProceduresForProcedures = newSynchronizedSetMultiMap();

    private SetMultiMap<String, String> proceduresForFeaturesOfInterest = newSynchronizedSetMultiMap();

    private SetMultiMap<String, String> proceduresForObservableProperties = newSynchronizedSetMultiMap();

    private SetMultiMap<String, String> proceduresForOfferings = newSynchronizedSetMultiMap();

    private SetMultiMap<String, String> hiddenChildProceduresForOfferings = newSynchronizedSetMultiMap();

    private SetMultiMap<String, String> relatedFeaturesForOfferings = newSynchronizedSetMultiMap();

    private SetMultiMap<String, String> resultTemplatesForOfferings = newSynchronizedSetMultiMap();

    private SetMultiMap<String, String> rolesForRelatedFeatures = newSynchronizedSetMultiMap();

    private Map<String, SosEnvelope> envelopeForOfferings = newSynchronizedMap();

    private Map<String, String> nameForOfferings = newSynchronizedMap();

    private Map<String, MultilingualString> i18nNameForOfferings = newSynchronizedMap();

    private Map<String, MultilingualString> i18nDescriptionForOfferings = newSynchronizedMap();

    private Set<Integer> epsgCodes = newSynchronizedSet();

    private Set<String> featuresOfInterest = newSynchronizedSet();

    private Set<String> procedures = newSynchronizedSet();

    private Set<String> resultTemplates = newSynchronizedSet();

    private Set<String> offerings = newSynchronizedSet();

    private SosEnvelope globalEnvelope = new SosEnvelope(null, defaultEpsgCode);

    private TimePeriod globalPhenomenonTimeEnvelope = new TimePeriod();

    private TimePeriod globalResultTimeEnvelope = new TimePeriod();

    private Map<String, SosEnvelope> spatialFilteringProfileEnvelopeForOfferings = newSynchronizedMap();

    private Set<Locale> supportedLanguages = newSynchronizedSet();
    
    private Set<String> requestableProcedureDescriptionFormats  = newSynchronizedSet();

    
    private BiMap<String, String> featureOfInterestIdentifierHumanReadableName = newSynchronizedBiMap();
    
//    private Map<String, String> featureOfInterestHumanReadableNameForIdentifier = newSynchronizedMap();
    
    private BiMap<String, String> observablePropertyIdentifierHumanReadableName = newSynchronizedBiMap();
    
//    private Map<String, String> observablePropertyHumanReadableNameForIdentifier = newSynchronizedMap();
    
    private BiMap<String, String> procedureIdentifierHumanReadableName = newSynchronizedBiMap();
    
//    private Map<String, String> procedureHumanReadableNameForIdentifier = newSynchronizedMap();
    
    private BiMap<String, String> offeringIdentifierHumanReadableName = newSynchronizedBiMap();
    
//    private Map<String, String> offeringHumanReadableNameForIdentifier = newSynchronizedMap();
    
    /**
     * @return the relating offering -> max phenomenon time
     */
    protected Map<String, DateTime> getMaxPhenomenonTimeForOfferingsMap() {
        return this.maxPhenomenonTimeForOfferings;
    }

    /**
     * @return the relating offering -> min phenomenon time
     */
    protected Map<String, DateTime> getMinPhenomenonTimeForOfferingsMap() {
        return this.minPhenomenonTimeForOfferings;
    }

    /**
     * @return the relating procedure -> max phenomenon time
     */
    protected Map<String, DateTime> getMaxPhenomenonTimeForProceduresMap() {
        return this.maxPhenomenonTimeForProcedures;
    }

    /**
     * @return the relating procedure -> min phenomenon time
     */
    protected Map<String, DateTime> getMinPhenomenonTimeForProceduresMap() {
        return this.minPhenomenonTimeForProcedures;
    }

    /**
     * @return the relating offering -> max result time
     */
    protected Map<String, DateTime> getMaxResultTimeForOfferingsMap() {
        return this.maxResultTimeForOfferings;
    }

    /**
     * @return the relating offering -> min result time
     */
    protected Map<String, DateTime> getMinResultTimeForOfferingsMap() {
        return this.minResultTimeForOfferings;
    }

    /**
     * @return the relating offering -> allowed observation type
     */
    protected SetMultiMap<String, String> getAllowedObservationTypesForOfferingsMap() {
        return this.allowedObservationTypeForOfferings;
    }

    /**
     * @return the relating offering -> allowed featureOfInterest type
     */
    protected SetMultiMap<String, String> getAllowedFeatureOfInterestTypesForOfferingsMap() {
        return this.allowedFeatureOfInterestTypeForOfferings;
    }

    /**
     * @return the relating feature -> child feature
     */
    protected SetMultiMap<String, String> getChildFeaturesForFeaturesOfInterestMap() {
        return this.childFeaturesForFeatureOfInterest;
    }

    /**
     * @return the relating offering -> composite phenomenons
     */
    protected SetMultiMap<String, String> getCompositePhenomenonsForOfferingsMap() {
        return this.compositePhenomenonForOfferings;
    }

    /**
     * @return the relating offering -> feature
     */
    protected SetMultiMap<String, String> getFeaturesOfInterestForOfferingMap() {
        return this.featuresOfInterestForOfferings;
    }

    /**
     * @return the relating result template -> feature
     */
    protected SetMultiMap<String, String> getFeaturesOfInterestForResultTemplatesMap() {
        return this.featuresOfInterestForResultTemplates;
    }

    /**
     * @return the relating composite phenomenon -> observable property
     */
    protected SetMultiMap<String, String> getObservablePropertiesForCompositePhenomenonsMap() {
        return this.observablePropertiesForCompositePhenomenons;
    }

    /**
     * @return the relating offering -> observable property
     */
    protected SetMultiMap<String, String> getObservablePropertiesForOfferingsMap() {
        return this.observablePropertiesForOfferings;
    }

    /**
     * @return the relating offering -> observation types
     */
    protected SetMultiMap<String, String> getObservationTypesForOfferingsMap() {
        return this.observationTypesForOfferings;
    }

    /**
     * @return the relating offering -> featureOfInterest types
     */
    protected SetMultiMap<String, String> getFeatureOfInterestTypesForOfferingsMap() {
        return this.featureOfInterestTypesForOfferings;
    }

    /**
     * @return the relating result template -> obsevable properties
     */
    protected SetMultiMap<String, String> getObservablePropertiesForResultTemplatesMap() {
        return this.observedPropertiesForResultTemplates;
    }

    /**
     * @return the relating observable property -> offerings
     */
    protected SetMultiMap<String, String> getOfferingsForObservablePropertiesMap() {
        return this.offeringsForObservableProperties;
    }

    /**
     * @return the relating procedure -> offerings
     */
    protected SetMultiMap<String, String> getOfferingsForProceduresMap() {
        return this.offeringsForProcedures;
    }

    /**
     * @return the relating feature -> parent feature
     */
    protected SetMultiMap<String, String> getParentFeaturesForFeaturesOfInterestMap() {
        return this.parentFeaturesForFeaturesOfInterest;
    }

    /**
     * @return the relating feature -> procedure
     */
    protected SetMultiMap<String, String> getProceduresForFeaturesOfInterestMap() {
        return this.proceduresForFeaturesOfInterest;
    }

    /**
     * @return the relating observable property -> procedure
     */
    protected SetMultiMap<String, String> getProceduresForObservablePropertiesMap() {
        return this.proceduresForObservableProperties;
    }

    /**
     * @return the relating offering -> procedure
     */
    protected SetMultiMap<String, String> getProceduresForOfferingsMap() {
        return this.proceduresForOfferings;
    }

    /**
     * @return the relating offering -> procedure
     */
    protected SetMultiMap<String, String> getHiddenChildProceduresForOfferingsMap() {
        return this.hiddenChildProceduresForOfferings;
    }

    /**
     * @return the relating offering -> related features
     */
    protected SetMultiMap<String, String> getRelatedFeaturesForOfferingsMap() {
        return this.relatedFeaturesForOfferings;
    }

    /**
     * @return the relating offering -> resulte templates
     */
    protected SetMultiMap<String, String> getResultTemplatesForOfferingsMap() {
        return this.resultTemplatesForOfferings;
    }

    /**
     * @return the relating related feature -> roles
     */
    protected SetMultiMap<String, String> getRolesForRelatedFeaturesMap() {
        return this.rolesForRelatedFeatures;
    }

    /**
     * @return the relating offering -> envelope
     */
    protected Map<String, SosEnvelope> getEnvelopeForOfferingsMap() {
        return this.envelopeForOfferings;
    }

    /**
     * @return the relating offering -> envelope
     */
    protected Map<String, SosEnvelope> getSpatialFilteringProfileEnvelopeForOfferingsMap() {
        return this.spatialFilteringProfileEnvelopeForOfferings;
    }

    /**
     * @return the relating offering -> offering name
     */
    protected Map<String, String> getNameForOfferingsMap() {
        return this.nameForOfferings;
    }

    /**
     * @return the relating offering -> language / offering name
     */
    protected Map<String,  MultilingualString> getI18nNameForOfferingsMap() {
        return this.i18nNameForOfferings;
    }

    /**
     * @return the relating offering -> language / offering description
     */
    protected Map<String,  MultilingualString> getI18nDescriptionForOfferingsMap() {
        return this.i18nDescriptionForOfferings;
    }

    /**
     * @return the relating procedure -> observable properties
     */
    protected SetMultiMap<String, String> getObservablePropertiesForProceduresMap() {
        return this.observablePropertiesForProcedures;
    }

    /**
     * @return the relating procedure -> parent procedure
     */
    protected SetMultiMap<String, String> getParentProceduresForProceduresMap() {
        return this.parentProceduresForProcedures;
    }

    /**
     * @return the relating procedure -> child procedure
     */
    protected SetMultiMap<String, String> getChildProceduresForProceduresMap() {
        return this.childProceduresForProcedures;
    }

    /**
     * @return the epsg codes
     */
    protected Set<Integer> getEpsgCodesSet() {
        return this.epsgCodes;
    }

    /**
     * @return the features of interest
     */
    protected Set<String> getFeaturesOfInterestSet() {
        return this.featuresOfInterest;
    }

    /**
     * @return the procedures
     */
    protected Set<String> getProceduresSet() {
        return this.procedures;
    }

    /**
     * @return the procedures
     */
    protected Set<String> getOfferingsSet() {
        return this.offerings;
    }

    /**
     * @return the result templates
     */
    protected Set<String> getResultTemplatesSet() {
        return this.resultTemplates;
    }

    /**
     * @return the global phenomenon time envelope
     */
    protected TimePeriod getGlobalPhenomenonTimeEnvelope() {
        return this.globalPhenomenonTimeEnvelope;
    }

    /**
     * @return the global result time envelope
     */
    protected TimePeriod getGlobalResultTimeEnvelope() {
        return this.globalResultTimeEnvelope;
    }

    /**
     * @return the global spatial envelope
     */
    protected SosEnvelope getGlobalSpatialEnvelope() {
        return this.globalEnvelope;
    }

    /**
     * @param envelope
     *            the new global spatial envelope
     */
    protected void setGlobalSpatialEnvelope(SosEnvelope envelope) {
        if (envelope == null) {
            throw new NullPointerException();
        }
        this.globalEnvelope = envelope;
    }

    protected Set<Locale> getSupportedLanguageSet() {
        return this.supportedLanguages;
    }
    
    protected Set<String> getRequestableProcedureDescriptionFormats() {
        return this.requestableProcedureDescriptionFormats;
    }
    
    protected Map<String, String> getFeatureOfInterestIdentifierForHumanReadableName() {
    	return featureOfInterestIdentifierHumanReadableName.inverse();
    }
    
    protected Map<String, String> getFeatureOfInterestHumanReadableNameForIdentifier() {
    	return featureOfInterestIdentifierHumanReadableName;
    }
    
    protected Map<String, String> getObservablePropertyIdentifierForHumanReadableName() {
    	return observablePropertyIdentifierHumanReadableName.inverse();
    }
    
    protected Map<String, String> getObservablePropertyHumanReadableNameForIdentifier() {
    	return observablePropertyIdentifierHumanReadableName;
    }
    
    protected Map<String, String> getProcedureIdentifierForHumanReadableName() {
    	return procedureIdentifierHumanReadableName.inverse();
    }
    
    protected Map<String, String> getProcedureHumanReadableNameForIdentifier() {
    	return procedureIdentifierHumanReadableName;
    }
    
    protected Map<String, String> getOfferingIdentifierForHumanReadableName() {
    	return offeringIdentifierHumanReadableName;
    }
    
    protected Map<String, String> getOfferingHumanReadableNameForIdentifier() {
    	return offeringIdentifierHumanReadableName.inverse();
    }

    /**
     * @param defaultEpsgCode
     *            the new default EPSG code
     */
    public void setDefaultEPSGCode(int defaultEpsgCode) {
        this.defaultEpsgCode = defaultEpsgCode;
    }

    @Override
    public int getDefaultEPSGCode() {
        return this.defaultEpsgCode;
    }


    @Override
    public int hashCode() {
        return Objects.hashCode(defaultEpsgCode, maxPhenomenonTimeForOfferings, minPhenomenonTimeForOfferings,
                maxResultTimeForOfferings, minResultTimeForOfferings, maxPhenomenonTimeForProcedures,
                minPhenomenonTimeForProcedures, allowedObservationTypeForOfferings, childFeaturesForFeatureOfInterest,
                childProceduresForProcedures, compositePhenomenonForOfferings, featuresOfInterestForOfferings,
                featuresOfInterestForResultTemplates, observablePropertiesForCompositePhenomenons,
                observablePropertiesForOfferings, observablePropertiesForProcedures,
                observationTypesForOfferings, observedPropertiesForResultTemplates, offeringsForObservableProperties, offeringsForProcedures,
                parentFeaturesForFeaturesOfInterest, parentProceduresForProcedures, proceduresForFeaturesOfInterest,
                proceduresForObservableProperties, proceduresForOfferings, hiddenChildProceduresForOfferings,
                relatedFeaturesForOfferings, resultTemplatesForOfferings, rolesForRelatedFeatures,
                envelopeForOfferings, nameForOfferings, i18nNameForOfferings, i18nDescriptionForOfferings, epsgCodes, featuresOfInterest,
                procedures, resultTemplates, offerings, globalEnvelope, globalResultTimeEnvelope,
                globalPhenomenonTimeEnvelope, supportedLanguages, requestableProcedureDescriptionFormats,
                featureOfInterestIdentifierHumanReadableName, observablePropertyIdentifierHumanReadableName,
				procedureIdentifierHumanReadableName, offeringIdentifierHumanReadableName);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof AbstractContentCache) {
            final AbstractContentCache other = (AbstractContentCache) obj;
            return Objects.equal(this.defaultEpsgCode, other.getDefaultEPSGCode())
                    && Objects.equal(this.maxPhenomenonTimeForOfferings, other.getMaxPhenomenonTimeForOfferingsMap())
                    && Objects.equal(this.minPhenomenonTimeForOfferings, other.getMinPhenomenonTimeForOfferingsMap())
                    && Objects.equal(this.maxResultTimeForOfferings, other.getMaxResultTimeForOfferingsMap())
                    && Objects.equal(this.minResultTimeForOfferings, other.getMinResultTimeForOfferingsMap())
                    && Objects
                            .equal(this.maxPhenomenonTimeForProcedures, other.getMaxPhenomenonTimeForProceduresMap())
                    && Objects
                            .equal(this.minPhenomenonTimeForProcedures, other.getMinPhenomenonTimeForProceduresMap())
                    && Objects.equal(this.allowedObservationTypeForOfferings,
                            other.getAllowedObservationTypesForOfferingsMap())
                    && Objects.equal(this.childFeaturesForFeatureOfInterest,
                            other.getChildFeaturesForFeaturesOfInterestMap())
                    && Objects.equal(this.childProceduresForProcedures, other.getChildProceduresForProceduresMap())
                    && Objects.equal(this.compositePhenomenonForOfferings,
                            other.getCompositePhenomenonsForOfferingsMap())
                    && Objects.equal(this.featuresOfInterestForOfferings, other.getFeaturesOfInterestForOfferingMap())
                    && Objects.equal(this.featuresOfInterestForResultTemplates,
                            other.getFeaturesOfInterestForResultTemplatesMap())
                    && Objects.equal(this.observablePropertiesForCompositePhenomenons,
                            other.getObservablePropertiesForCompositePhenomenonsMap())
                    && Objects.equal(this.observablePropertiesForOfferings,
                            other.getObservablePropertiesForOfferingsMap())
                    && Objects.equal(this.observablePropertiesForProcedures,
                            other.getObservablePropertiesForProceduresMap())
                    && Objects.equal(this.observedPropertiesForResultTemplates,
                            other.getObservablePropertiesForResultTemplatesMap())
                    && Objects.equal(this.offeringsForObservableProperties,
                            other.getOfferingsForObservablePropertiesMap())
                    && Objects.equal(this.offeringsForProcedures, other.getOfferingsForProceduresMap())
                    && Objects.equal(this.parentFeaturesForFeaturesOfInterest,
                            other.getParentFeaturesForFeaturesOfInterestMap())
                    && Objects.equal(this.parentProceduresForProcedures, other.getParentProceduresForProceduresMap())
                    && Objects.equal(this.proceduresForFeaturesOfInterest,
                            other.getProceduresForFeaturesOfInterestMap())
                    && Objects.equal(this.proceduresForObservableProperties,
                            other.getProceduresForObservablePropertiesMap())
                    && Objects.equal(this.proceduresForOfferings, other.getProceduresForOfferingsMap())
                    && Objects.equal(this.hiddenChildProceduresForOfferings,
                            other.getHiddenChildProceduresForOfferingsMap())
                    && Objects.equal(this.relatedFeaturesForOfferings, other.getRelatedFeaturesForOfferingsMap())
                    && Objects.equal(this.resultTemplatesForOfferings, other.getResultTemplatesForOfferingsMap())
                    && Objects.equal(this.rolesForRelatedFeatures, other.getRolesForRelatedFeaturesMap())
                    && Objects.equal(this.envelopeForOfferings, other.getEnvelopeForOfferingsMap())
                    && Objects.equal(this.nameForOfferings, other.getNameForOfferingsMap())
                    && Objects.equal(this.i18nNameForOfferings, other.getI18nNameForOfferingsMap())
                    && Objects.equal(this.i18nDescriptionForOfferings, other.getI18nDescriptionForOfferingsMap())
                    && Objects.equal(this.epsgCodes, other.getEpsgCodesSet())
                    && Objects.equal(this.featuresOfInterest, other.getFeaturesOfInterestSet())
                    && Objects.equal(this.procedures, other.getProceduresSet())
                    && Objects.equal(this.resultTemplates, other.getResultTemplatesSet())
                    && Objects.equal(this.globalEnvelope, other.getGlobalEnvelope())
                    && Objects.equal(this.globalPhenomenonTimeEnvelope, other.getGlobalPhenomenonTimeEnvelope())
                    && Objects.equal(this.globalResultTimeEnvelope, other.getGlobalResultTimeEnvelope())
                    && Objects.equal(this.offerings, other.getOfferingsSet())
                    && Objects.equal(this.supportedLanguages, other.getSupportedLanguages())
                    && Objects.equal(this.requestableProcedureDescriptionFormats, other.getRequestableProcedureDescriptionFormats())
                    && Objects.equal(this.getFeatureOfInterestIdentifierForHumanReadableName(), other.getFeatureOfInterestIdentifierForHumanReadableName())
                    && Objects.equal(this.getObservablePropertyIdentifierForHumanReadableName(), other.getObservablePropertyIdentifierForHumanReadableName())
                    && Objects.equal(this.getProcedureIdentifierForHumanReadableName(), other.getProcedureIdentifierForHumanReadableName())
                    && Objects.equal(this.getOfferingIdentifierForHumanReadableName(), other.getOfferingIdentifierForHumanReadableName());
        }
        return false;
    }
}
