/*
 * Copyright (C) 2012-2018 52°North Initiative for Geospatial Open Source
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

import static java.util.stream.Collectors.toSet;
import static org.n52.sos.util.SosHelper.getHierarchy;

import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;

import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.n52.janmayen.function.Functions;
import org.n52.janmayen.function.Predicates;
import org.n52.janmayen.function.Suppliers;
import org.n52.janmayen.i18n.LocalizedString;
import org.n52.janmayen.i18n.MultilingualString;
import org.n52.shetland.ogc.gml.time.Time;
import org.n52.shetland.ogc.gml.time.TimePeriod;
import org.n52.shetland.util.CollectionHelper;
import org.n52.shetland.util.DateTimeHelper;
import org.n52.shetland.util.MinMax;
import org.n52.shetland.util.ReferencedEnvelope;
import org.n52.sos.cache.SosContentCache.ComponentAggregation;
import org.n52.sos.cache.SosContentCache.TypeInstance;
import org.n52.sos.request.ProcedureRequestSettingProvider;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Strings;
import com.google.common.collect.BiMap;
import com.google.common.collect.Sets;
import org.locationtech.jts.geom.Envelope;

public class InMemoryCacheImpl extends AbstractStaticSosContentCache implements SosWritableContentCache, CacheConstants {
    private static final Logger LOG = LoggerFactory.getLogger(InMemoryCacheImpl.class);
    private static final long serialVersionUID = 3630601584420744019L;

    private final Map<String, DateTime> maxPhenomenonTimeForOfferings = newSynchronizedMap();
    private final Map<String, DateTime> minPhenomenonTimeForOfferings = newSynchronizedMap();
    private final Map<String, DateTime> maxResultTimeForOfferings = newSynchronizedMap();
    private final Map<String, DateTime> minResultTimeForOfferings = newSynchronizedMap();
    private final Map<String, DateTime> maxPhenomenonTimeForProcedures = newSynchronizedMap();
    private final Map<String, DateTime> minPhenomenonTimeForProcedures = newSynchronizedMap();
    private final Map<String, Set<String>> allowedObservationTypeForOfferings = newSynchronizedMap();
    private final Map<String, Set<String>> allowedFeatureOfInterestTypeForOfferings = newSynchronizedMap();
    private final Map<String, Set<String>> childFeaturesForFeatureOfInterest = newSynchronizedMap();
    private final Map<String, Set<String>> childProceduresForProcedures = newSynchronizedMap();
    private final Map<String, Set<String>> compositePhenomenonsForProcedure = newSynchronizedMap();
    private final Map<String, Set<String>> compositePhenomenonsForOffering = newSynchronizedMap();
    private final Map<String, Set<String>> compositePhenomenonsForObservableProperty = newSynchronizedMap();
    private final Map<String, Set<String>> featuresOfInterestForOfferings = newSynchronizedMap();
    private final Map<String, Set<String>> featuresOfInterestForResultTemplates = newSynchronizedMap();
    private final Map<String, Set<String>> observablePropertiesForCompositePhenomenons = newSynchronizedMap();
    private final Map<String, Set<String>> observablePropertiesForOfferings = newSynchronizedMap();
    private final Map<String, Set<String>> observablePropertiesForProcedures = newSynchronizedMap();
    private final Map<String, Set<String>> observationTypesForOfferings = newSynchronizedMap();
    private final Map<String, Set<String>> featureOfInterestTypesForOfferings = newSynchronizedMap();
    private final Map<String, Set<String>> observedPropertiesForResultTemplates = newSynchronizedMap();
    private final Map<String, Set<String>> offeringsForObservableProperties = newSynchronizedMap();
    private final Map<String, Set<String>> offeringsForProcedures = newSynchronizedMap();
    private final Map<String, Set<String>> parentFeaturesForFeaturesOfInterest = newSynchronizedMap();
    private final Map<String, Set<String>> parentProceduresForProcedures = newSynchronizedMap();
    private final Map<String, Set<String>> proceduresForFeaturesOfInterest = newSynchronizedMap();
    private final Map<String, Set<String>> proceduresForObservableProperties = newSynchronizedMap();
    private final Map<String, Set<String>> proceduresForOfferings = newSynchronizedMap();
    private final Map<String, Set<String>> hiddenChildProceduresForOfferings = newSynchronizedMap();
    private final Map<String, Set<String>> relatedFeaturesForOfferings = newSynchronizedMap();
    private final Map<String, Set<String>> resultTemplatesForOfferings = newSynchronizedMap();
    private final Map<String, Set<String>> rolesForRelatedFeatures = newSynchronizedMap();
    private final Map<String, ReferencedEnvelope> envelopeForOfferings = newSynchronizedMap();
    private final Map<String, String> nameForOfferings = newSynchronizedMap();
    private final Map<String, MultilingualString> i18nNameForOfferings = newSynchronizedMap();
    private final Map<String, MultilingualString> i18nDescriptionForOfferings = newSynchronizedMap();
    private final Set<Integer> epsgCodes = newSynchronizedSet();
    private final Set<String> featuresOfInterest = newSynchronizedSet();
    private final Set<String> procedures = newSynchronizedSet();
    private final Set<String> resultTemplates = newSynchronizedSet();
    private final Set<String> offerings = newSynchronizedSet();
    private final Set<String> compositePhenomenons = newSynchronizedSet();
    private final TimePeriod globalPhenomenonTimeEnvelope = new TimePeriod();
    private final TimePeriod globalResultTimeEnvelope = new TimePeriod();
    private final Map<String, ReferencedEnvelope> spatialFilteringProfileEnvelopeForOfferings = newSynchronizedMap();
    private final Set<Locale> supportedLanguages = newSynchronizedSet();
    private final Set<String> requestableProcedureDescriptionFormats = newSynchronizedSet();
    private final BiMap<String, String> featureOfInterestIdentifierHumanReadableName = newSynchronizedBiMap();
    private final BiMap<String, String> observablePropertyIdentifierHumanReadableName = newSynchronizedBiMap();
    private final BiMap<String, String> procedureIdentifierHumanReadableName = newSynchronizedBiMap();
    private final BiMap<String, String> offeringIdentifierHumanReadableName = newSynchronizedBiMap();
    private final Map<TypeInstance, Set<String>> typeInstanceProcedures = newSynchronizedMap();
    private final Map<ComponentAggregation, Set<String>> componentAggregationProcedures = newSynchronizedMap();
    private final Map<String, Set<String>> typeOfProceduresMap = newSynchronizedMap();
    private int defaultEpsgCode = 4326;
    private ReferencedEnvelope globalEnvelope = new ReferencedEnvelope(null, defaultEpsgCode);
    private DateTime updateTime;

    @Override
    public DateTime getLastUpdateTime() {
        return this.updateTime;
    }

    @Override
    public void setLastUpdateTime(DateTime time) {
        this.updateTime = time;
    }

    @Override
    public DateTime getMaxPhenomenonTime() {
        return this.globalPhenomenonTimeEnvelope.getEnd();
    }

    @Override
    public void setMaxPhenomenonTime(DateTime maxEventTime) {
        LOG.trace("Setting Maximal EventTime to {}", maxEventTime);
        this.globalPhenomenonTimeEnvelope.setEnd(DateTimeHelper.toUTC(maxEventTime));
    }

    @Override
    public DateTime getMinPhenomenonTime() {
        return this.globalPhenomenonTimeEnvelope.getStart();
    }

    @Override
    public void setMinPhenomenonTime(DateTime minEventTime) {
        LOG.trace("Setting Minimal EventTime to {}", minEventTime);
        this.globalPhenomenonTimeEnvelope.setStart(DateTimeHelper.toUTC(minEventTime));
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
    protected ReferencedEnvelope getGlobalSpatialEnvelope() {
        return this.globalEnvelope;
    }

    /**
     * @param envelope the new global spatial envelope
     */
    protected void setGlobalSpatialEnvelope(ReferencedEnvelope envelope) {
        this.globalEnvelope = Objects.requireNonNull(envelope, "envelope");
    }

    /**
     * @return the updateTime
     */
    public DateTime getUpdateTime() {
        return updateTime;
    }

    /**
     * @param updateTime the updateTime to set
     */
    public void setUpdateTime(DateTime updateTime) {
        this.updateTime = updateTime;
    }

    /**
     * @param defaultEpsgCode the new default EPSG code
     */
    public void setDefaultEPSGCode(int defaultEpsgCode) {
        this.defaultEpsgCode = defaultEpsgCode;
    }

    @Override
    public int getDefaultEPSGCode() {
        return this.defaultEpsgCode;
    }

    @Override
    public Set<Integer> getEpsgCodes() {
        return copyOf(this.epsgCodes);
    }

    @Override
    public Set<String> getFeaturesOfInterest() {
        return copyOf(this.featuresOfInterest);
    }

    @Override
    public void setFeaturesOfInterest(Collection<String> featuresOfInterest) {
        LOG.trace("Setting FeaturesOfInterest");
        this.featuresOfInterest.clear();
        addFeaturesOfInterest(featuresOfInterest);
    }

    @Override
    public Set<String> getProcedures() {
        return copyOf(this.procedures);
    }

    @Override
    public void setProcedures(Collection<String> procedures) {
        LOG.trace("Setting Procedures");
        this.procedures.clear();
        addProcedures(procedures);
    }

    @Override
    public Set<String> getResultTemplates() {
        return copyOf(this.resultTemplates);
    }

    @Override
    public ReferencedEnvelope getGlobalEnvelope() {
        return copyOf(this.globalEnvelope);
    }

    @Override
    public void setGlobalEnvelope(ReferencedEnvelope globalEnvelope) {
        LOG.trace("Global envelope now: '{}'", this.globalEnvelope);
        if (globalEnvelope == null) {
            setGlobalSpatialEnvelope(new ReferencedEnvelope(new Envelope(), getDefaultEPSGCode()));
        } else {
            setGlobalSpatialEnvelope(globalEnvelope);
        }
        LOG.trace("Global envelope updated to '{}' with '{}'", this.globalEnvelope, globalEnvelope);
    }

    @Override
    public Set<String> getOfferings() {
        return copyOf(this.offerings);
    }

    @Override
    public void setOfferings(Collection<String> offerings) {
        clearOfferings();
        addOfferings(offerings);
    }

    @Override
    public Set<String> getOfferingsForObservableProperty(String observableProperty) {
        return copyOf(this.offeringsForObservableProperties.get(observableProperty));
    }

    @Override
    public Set<String> getOfferingsForProcedure(String procedure) {
        return copyOf(this.offeringsForProcedures.get(procedure));
    }

    @Override
    public Set<String> getProceduresForFeatureOfInterest(String featureOfInterest) {
        return copyOf(this.proceduresForFeaturesOfInterest.get(featureOfInterest));
    }

    @Override
    public Set<String> getProceduresForObservableProperty(String observableProperty) {
        return copyOf(this.proceduresForObservableProperties.get(observableProperty));
    }

    @Override
    public Set<String> getProceduresForOffering(String offering) {
        return copyOf(this.proceduresForOfferings.get(offering));
    }

    @Override
    public Set<String> getHiddenChildProceduresForOffering(String offering) {
        return copyOf(this.hiddenChildProceduresForOfferings.get(offering));
    }

    @Override
    public Set<String> getRelatedFeaturesForOffering(String offering) {
        return copyOf(this.relatedFeaturesForOfferings.get(offering));
    }

    @Override
    public Set<String> getResultTemplatesForOffering(String offering) {
        return copyOf(this.resultTemplatesForOfferings.get(offering));
    }

    @Override
    public Set<String> getRolesForRelatedFeature(String relatedFeature) {
        return copyOf(this.rolesForRelatedFeatures.get(relatedFeature));
    }

    @Override
    public ReferencedEnvelope getEnvelopeForOffering(String offering) {
        return copyOf(this.envelopeForOfferings.get(offering));
    }

    @Override
    public String getNameForOffering(String offering) {
        return this.nameForOfferings.get(offering);
    }

    @Override
    public LocalizedString getI18nNameForOffering(String offering, Locale i18n) {
        return Optional.ofNullable(this.i18nNameForOfferings.get(offering))
                .flatMap(m -> m.getLocalization(i18n)).orElse(null);
    }

    @Override
    public MultilingualString getI18nNamesForOffering(String offering) {
        return this.i18nNameForOfferings.get(offering);
    }

    @Override
    public boolean hasI18NNamesForOffering(String offering, Locale i18n) {
        return this.i18nNameForOfferings.containsKey(offering) && getI18nNamesForOffering(offering).hasLocale(i18n);
    }

    @Override
    public LocalizedString getI18nDescriptionForOffering(String offering, Locale i18n) {
        return Optional.ofNullable(this.i18nDescriptionForOfferings.get(offering))
                .flatMap(m -> m.getLocalization(i18n)).orElse(null);
    }

    @Override
    public MultilingualString getI18nDescriptionsForOffering(String offering) {
        return this.i18nDescriptionForOfferings.get(offering);
    }

    @Override
    public boolean hasI18NDescriptionForOffering(String offering, Locale i18n) {
        return this.i18nDescriptionForOfferings.containsKey(offering) &&
               this.i18nDescriptionForOfferings.get(offering).hasLocale(i18n);
    }

    @Override
    public DateTime getMaxPhenomenonTimeForOffering(String offering) {
        return this.maxPhenomenonTimeForOfferings.get(offering);
    }

    @Override
    public DateTime getMinPhenomenonTimeForOffering(String offering) {
        return this.minPhenomenonTimeForOfferings.get(offering);
    }

    @Override
    public DateTime getMaxPhenomenonTimeForProcedure(String procedure) {
        return getChildProcedures(procedure, true, true).stream()
                .map(this.minPhenomenonTimeForProcedures::get)
                .filter(Objects::nonNull)
                .reduce((a, b) -> a.isAfter(b) ? a : b)
                .orElse(null);
    }

    @Override
    public DateTime getMinPhenomenonTimeForProcedure(String procedure) {
        return getChildProcedures(procedure, true, true).stream()
                .map(this.minPhenomenonTimeForProcedures::get)
                .filter(Objects::nonNull)
                .reduce((a, b) -> a.isBefore(b) ? a : b)
                .orElse(null);
    }

    @Override
    public Set<String> getAllowedObservationTypesForOffering(String offering) {
        return copyOf(this.allowedObservationTypeForOfferings.get(offering));
    }

    @Override
    public Set<String> getFeaturesOfInterestForOffering(String offering) {
        return copyOf(this.featuresOfInterestForOfferings.get(offering));
    }

    @Override
    public Set<String> getFeaturesOfInterestForResultTemplate(String resultTemplate) {
        return copyOf(this.featuresOfInterestForResultTemplates.get(resultTemplate));
    }

    @Override
    public Set<String> getObservablePropertiesForOffering(String offering) {
        return copyOf(this.observablePropertiesForOfferings.get(offering));
    }

    @Override
    public Set<String> getObservablePropertiesForProcedure(String procedure) {
        return copyOf(this.observablePropertiesForProcedures.get(procedure));
    }

    @Override
    public boolean hasObservablePropertyForProcedure(String procedure, String observableProperty) {
        return this.observablePropertiesForProcedures.containsKey(procedure) &&
               this.observablePropertiesForProcedures.get(procedure).contains(observableProperty);
    }

    @Override
    public Set<String> getObservationTypesForOffering(String offering) {
        return copyOf(this.observationTypesForOfferings.get(offering));
    }

    @Override
    public Set<String> getObservablePropertiesForResultTemplate(String resultTemplate) {
        return copyOf(this.observedPropertiesForResultTemplates.get(resultTemplate));
    }

    @Override
    public Set<String> getParentProcedures(String procedureIdentifier, boolean fullHierarchy, boolean includeSelf) {
        return getHierarchy(this.parentProceduresForProcedures, procedureIdentifier, fullHierarchy, includeSelf);
    }

    @Override
    public Set<String> getParentFeatures(String featureIdentifier, boolean fullHierarchy, boolean includeSelf) {
        return getHierarchy(this.parentFeaturesForFeaturesOfInterest, featureIdentifier, fullHierarchy, includeSelf);
    }

    @Override
    public Set<String> getChildProcedures(String procedureIdentifier, boolean fullHierarchy, boolean includeSelf) {
        return getHierarchy(this.childProceduresForProcedures, procedureIdentifier, fullHierarchy, includeSelf);
    }

    @Override
    public Set<String> getChildProcedures(Set<String> procedureIdentifiers, boolean fullHierarchy, boolean includeSelves) {
        return getHierarchy(this.childProceduresForProcedures, procedureIdentifiers, fullHierarchy, includeSelves);
    }

    @Override
    public Set<String> getChildFeatures(String featureIdentifier, boolean fullHierarchy, boolean includeSelf) {
        return getHierarchy(this.childFeaturesForFeatureOfInterest, featureIdentifier, fullHierarchy, includeSelf);
    }

    @Override
    public Set<String> getParentProcedures(Set<String> procedureIdentifiers, boolean fullHierarchy,
                                           boolean includeSelves) {
        return getHierarchy(this.parentProceduresForProcedures, procedureIdentifiers, fullHierarchy, includeSelves);
    }

    @Override
    public Set<String> getParentFeatures(Set<String> featureIdentifiers, boolean fullHierarchy, boolean includeSelves) {
        return getHierarchy(this.parentFeaturesForFeaturesOfInterest, featureIdentifiers, fullHierarchy, includeSelves);
    }

    @Override
    public Set<String> getFeaturesOfInterestWithResultTemplate() {
        return CollectionHelper.unionOfListOfLists(this.featuresOfInterestForResultTemplates.values());
    }

    @Override
    public Set<String> getObservableProperties() {
        return CollectionHelper.unionOfListOfLists(this.observablePropertiesForOfferings.values());
    }

    @Override
    public Set<String> getObservablePropertiesWithResultTemplate() {
        return CollectionHelper.unionOfListOfLists(this.observedPropertiesForResultTemplates.values());
    }

    @Override
    public Set<String> getOfferingsWithResultTemplate() {
        return copyOf(this.resultTemplatesForOfferings.keySet());
    }

    @Override
    public Set<String> getRelatedFeatures() {
        return CollectionHelper.unionOfListOfLists(this.relatedFeaturesForOfferings.values());
    }

    @Override
    public boolean hasFeatureOfInterest(String featureOfInterest) {
        return this.featuresOfInterest.contains(featureOfInterest);
    }

    @Override
    public boolean hasObservableProperty(String observableProperty) {
        return getObservableProperties().contains(observableProperty);
    }

    @Override
    public boolean hasObservationType(String observationType) {
        return getObservationTypes().contains(observationType);
    }

    @Override
    public boolean hasOffering(String offering) {
        return this.offerings.contains(offering);
    }

    @Override
    public boolean hasProcedure(String procedure) {
        return this.procedures.contains(procedure);
    }

    @Override
    public boolean hasRelatedFeature(String relatedFeature) {
        return getRelatedFeatures().contains(relatedFeature);
    }

    @Override
    public boolean hasResultTemplate(String resultTemplate) {
        return this.resultTemplates.contains(resultTemplate);
    }

    @Override
    public boolean hasEpsgCode(Integer epsgCode) {
        return getEpsgCodes().contains(epsgCode);
    }

    @Override
    public boolean hasMaxPhenomenonTimeForOffering(String offering) {
        return getMaxPhenomenonTimeForOffering(offering) != null;
    }

    @Override
    public boolean hasMinPhenomenonTimeForOffering(String offering) {
        return getMinPhenomenonTimeForOffering(offering) != null;
    }

    @Override
    public boolean hasMaxPhenomenonTimeForProcedure(String procedure) {
        return getChildProcedures(procedure, true, true).stream()
                .anyMatch(this.maxPhenomenonTimeForProcedures::containsKey);
    }

    @Override
    public boolean hasMinPhenomenonTimeForProcedure(String procedure) {
        return getChildProcedures(procedure, true, true).stream()
                .anyMatch(this.minPhenomenonTimeForProcedures::containsKey);
    }

    @Override
    public boolean hasEnvelopeForOffering(String offering) {
        return Optional.ofNullable(getEnvelopeForOffering(offering))
                .filter(ReferencedEnvelope::isSetEnvelope).isPresent();
    }

    @Override
    public boolean hasMaxPhenomenonTime() {
        return getMaxPhenomenonTime() != null;
    }

    @Override
    public boolean hasMinPhenomenonTime() {
        return getMinPhenomenonTime() != null;
    }

    @Override
    public boolean hasGlobalEnvelope() {
        return Optional.ofNullable(getGlobalEnvelope())
                .filter(ReferencedEnvelope::isSetEnvelope).isPresent();
    }

    @Override
    public DateTime getMaxResultTime() {
        return this.globalResultTimeEnvelope.getEnd();
    }

    @Override
    public void setMaxResultTime(DateTime maxResultTime) {
        LOG.trace("Setting Maximal ResultTime to {}", maxResultTime);
        this.globalResultTimeEnvelope.setEnd(DateTimeHelper.toUTC(maxResultTime));
    }

    @Override
    public boolean hasMaxResultTime() {
        return getMaxResultTime() != null;
    }

    @Override
    public DateTime getMaxResultTimeForOffering(String offering) {
        return this.maxResultTimeForOfferings.get(offering);
    }

    @Override
    public boolean hasMaxResultTimeForOffering(String offering) {
        return getMaxResultTimeForOffering(offering) != null;
    }

    @Override
    public DateTime getMinResultTime() {
        return this.globalResultTimeEnvelope.getStart();
    }

    @Override
    public void setMinResultTime(DateTime minResultTime) {
        LOG.trace("Setting Minimal ResultTime to {}", minResultTime);
        this.globalResultTimeEnvelope.setStart(DateTimeHelper.toUTC(minResultTime));
    }

    @Override
    public boolean hasMinResultTime() {
        return getMinResultTime() != null;
    }

    @Override
    public DateTime getMinResultTimeForOffering(String offering) {
        return this.minResultTimeForOfferings.get(offering);
    }

    @Override
    public boolean hasMinResultTimeForOffering(String offering) {
        return getMinResultTimeForOffering(offering) != null;
    }

    @Override
    public boolean isRelatedFeatureSampled(String relatedFeatureIdentifier) {
        return Optional.ofNullable(relatedFeatureIdentifier)
                .filter(Predicates.not(String::isEmpty))
                .filter(getRelatedFeatures()::contains)
                .filter(id -> !getChildFeatures(id, true, false).isEmpty())
                .isPresent();
    }

    @Override
    public ReferencedEnvelope getSpatialFilteringProfileEnvelopeForOffering(String offering) {
        return copyOf(this.spatialFilteringProfileEnvelopeForOfferings.get(offering));
    }

    @Override
    public boolean hasSpatialFilteringProfileEnvelopeForOffering(String offering) {
        return Optional.ofNullable(getSpatialFilteringProfileEnvelopeForOffering(offering))
                .filter(ReferencedEnvelope::isSetEnvelope).isPresent();
    }

    @Override
    public boolean hasFeatureOfInterestType(String featureOfInterestType) {
        return getFeatureOfInterestTypes().contains(featureOfInterestType);
    }

    @Override
    public Set<String> getFeatureOfInterestTypesForOffering(String offering) {
        return copyOf(this.featureOfInterestTypesForOfferings.get(offering));
    }

    @Override
    public Set<String> getAllowedFeatureOfInterestTypesForOffering(String offering) {
        return copyOf(this.allowedFeatureOfInterestTypeForOfferings.get(offering));
    }

    @Override
    public Set<Locale> getSupportedLanguages() {
        return copyOf(this.supportedLanguages);
    }

    @Override
    public boolean hasSupportedLanguage() {
        return CollectionHelper.isNotEmpty(this.supportedLanguages);
    }

    @Override
    public boolean isLanguageSupported(Locale language) {
        return this.supportedLanguages.contains(language);
    }

    @Override
    public String getFeatureOfInterestIdentifierForHumanReadableName(String humanReadableName) {
        return this.featureOfInterestIdentifierHumanReadableName.inverse()
                .getOrDefault(humanReadableName, humanReadableName);
    }

    @Override
    public String getFeatureOfInterestHumanReadableNameForIdentifier(String identifier) {
        return this.featureOfInterestIdentifierHumanReadableName.getOrDefault(identifier, identifier);
    }

    @Override
    public String getObservablePropertyIdentifierForHumanReadableName(String humanReadableName) {
        return this.observablePropertyIdentifierHumanReadableName.inverse()
                .getOrDefault(humanReadableName, humanReadableName);
    }

    @Override
    public String getObservablePropertyHumanReadableNameForIdentifier(String identifier) {
        return this.observablePropertyIdentifierHumanReadableName.getOrDefault(identifier, identifier);
    }

    @Override
    public String getProcedureIdentifierForHumanReadableName(String humanReadableName) {
        return this.procedureIdentifierHumanReadableName.inverse().getOrDefault(humanReadableName, humanReadableName);
    }

    @Override
    public String getProcedureHumanReadableNameForIdentifier(String identifier) {
        return this.procedureIdentifierHumanReadableName.getOrDefault(identifier, identifier);
    }

    @Override
    public String getOfferingIdentifierForHumanReadableName(String humanReadableName) {
        return this.offeringIdentifierHumanReadableName.inverse().getOrDefault(humanReadableName, humanReadableName);
    }

    @Override
    public String getOfferingHumanReadableNameForIdentifier(String identifier) {
        return this.offeringIdentifierHumanReadableName.getOrDefault(identifier, identifier);
    }

    @Override
    public void removeResultTemplates(Collection<String> resultTemplates) {
        resultTemplates.forEach(this::removeResultTemplate);
    }

    @Override
    public void addEpsgCode(Integer epsgCode) {
        greaterZero(EPSG_CODE, epsgCode);
        LOG.trace("Adding EpsgCode {}", epsgCode);
        this.epsgCodes.add(epsgCode);
    }

    @Override
    public void addFeatureOfInterest(String featureOfInterest) {
        notNullOrEmpty(FEATURE_OF_INTEREST, featureOfInterest);
        LOG.trace("Adding FeatureOfInterest {}", featureOfInterest);
        this.featuresOfInterest.add(featureOfInterest);
    }

    @Override
    public void addProcedure(String procedure) {
        notNullOrEmpty(PROCEDURE, procedure);
        LOG.trace("Adding procedure {}", procedure);
        this.procedures.add(procedure);
    }

    @Override
    public void addResultTemplate(String resultTemplate) {
        notNullOrEmpty(RESULT_TEMPLATE, resultTemplate);
        LOG.trace("Adding SosResultTemplate {}", resultTemplate);
        this.resultTemplates.add(resultTemplate);
    }

    @Override
    public void addResultTemplates(Collection<String> resultTemplates) {
        noNullValues(RESULT_TEMPLATES, resultTemplates);
        resultTemplates.forEach(this::addResultTemplate);
    }

    @Override
    public void addEpsgCodes(Collection<Integer> epsgCodes) {
        noNullValues(EPSG_CODES, epsgCodes);
        epsgCodes.forEach(this::addEpsgCode);
    }

    @Override
    public void addFeaturesOfInterest(Collection<String> featuresOfInterest) {
        noNullValues(FEATURES_OF_INTEREST, featuresOfInterest);
        featuresOfInterest.forEach(this::addFeatureOfInterest);
    }

    @Override
    public void addProcedures(Collection<String> procedures) {
        noNullValues(PROCEDURES, procedures);
        procedures.forEach(this::addProcedure);
    }

    @Override
    public void removeFeatureOfInterest(String featureOfInterest) {
        notNullOrEmpty(FEATURE_OF_INTEREST, featureOfInterest);
        LOG.trace("Removing FeatureOfInterest {}", featureOfInterest);
        this.featuresOfInterest.remove(featureOfInterest);
    }

    @Override
    public void removeFeaturesOfInterest(Collection<String> featuresOfInterest) {
        noNullValues(FEATURES_OF_INTEREST, featuresOfInterest);
        featuresOfInterest.forEach(this::removeFeatureOfInterest);
    }

    @Override
    public void removeProcedure(String procedure) {
        notNullOrEmpty(PROCEDURE, procedure);
        LOG.trace("Removing Procedure {}", procedure);
        this.procedures.remove(procedure);
    }

    @Override
    public void removeProcedures(Collection<String> procedures) {
        noNullValues(PROCEDURES, procedures);
        procedures.forEach(this::removeProcedure);
    }

    @Override
    public void removeResultTemplate(String resultTemplate) {
        notNullOrEmpty(RESULT_TEMPLATE, resultTemplate);
        LOG.trace("Removing SosResultTemplate {}", resultTemplate);
        this.resultTemplates.remove(resultTemplate);
    }

    @Override
    public void setObservablePropertiesForOffering(String offering, Collection<String> observableProperties) {
        final Set<String> newValue = newSynchronizedSet(observableProperties);
        LOG.trace("Setting ObservableProperties for Offering {} to {}", offering, observableProperties);
        this.observablePropertiesForOfferings.put(offering, newValue);
    }

    @Override
    public void setObservablePropertiesForProcedure(String procedure,
                                                    final Collection<String> observableProperties) {
        final Set<String> newValue = newSynchronizedSet(observableProperties);
        LOG.trace("Setting ObservableProperties for Procedure {} to {}", procedure, newValue);
        this.observablePropertiesForProcedures.put(procedure, newValue);
    }

    @Override
    public void setObservationTypesForOffering(String offering, Collection<String> observationTypes) {
        final Set<String> newValue = newSynchronizedSet(observationTypes);
        LOG.trace("Setting ObservationTypes for Offering {} to {}", offering, newValue);
        this.observationTypesForOfferings.put(offering, newValue);
    }

    @Override
    public void setOfferingsForObservableProperty(String observableProperty, Collection<String> offerings) {
        final Set<String> newValue = newSynchronizedSet(offerings);
        LOG.trace("Setting Offerings for ObservableProperty {} to {}", observableProperty, newValue);
        this.offeringsForObservableProperties.put(observableProperty, newValue);
    }

    @Override
    public void setOfferingsForProcedure(String procedure, Collection<String> offerings) {
        final Set<String> newValue = newSynchronizedSet(offerings);
        LOG.trace("Setting Offerings for Procedure {} to {}", procedure, newValue);
        this.offeringsForProcedures.put(procedure, newValue);
    }

    @Override
    public void setProceduresForFeatureOfInterest(String featureOfInterest, Collection<String> procedures) {
        final Set<String> newValue = newSynchronizedSet(procedures);
        LOG.trace("Setting Procedures for FeatureOfInterest {} to {}", featureOfInterest, newValue);
        this.proceduresForFeaturesOfInterest.put(featureOfInterest, newValue);
    }

    @Override
    public void setProceduresForObservableProperty(String observableProperty, Collection<String> procedures) {
        final Set<String> newValue = newSynchronizedSet(procedures);
        LOG.trace("Setting Procedures for ObservableProperty {} to {}", observableProperty, procedures);
        this.proceduresForObservableProperties.put(observableProperty, newValue);
    }

    @Override
    public void setProceduresForOffering(String offering, Collection<String> procedures) {
        final Set<String> newValue = newSynchronizedSet(procedures);
        LOG.trace("Setting Procedures for Offering {} to {}", offering, newValue);
        this.proceduresForOfferings.put(offering, newValue);
    }

    @Override
    public void setRelatedFeaturesForOffering(String offering, Collection<String> relatedFeatures) {
        final Set<String> newValue = newSynchronizedSet(relatedFeatures);
        LOG.trace("Setting Related Features for Offering {} to {}", offering, newValue);
        this.relatedFeaturesForOfferings.put(offering, newValue);
    }

    @Override
    public void setResultTemplatesForOffering(String offering, Collection<String> resultTemplates) {
        final Set<String> newValue = newSynchronizedSet(resultTemplates);
        LOG.trace("Setting ResultTemplates for Offering {} to {}", offering, newValue);
        this.resultTemplatesForOfferings.put(offering, newValue);
    }

    @Override
    public void setRolesForRelatedFeature(String relatedFeature, Collection<String> roles) {
        final Set<String> newValue = newSynchronizedSet(roles);
        LOG.trace("Setting Roles for RelatedFeature {} to {}", relatedFeature, newValue);
        this.rolesForRelatedFeatures.put(relatedFeature, newValue);
    }

    @Override
    public void setPhenomenonTime(DateTime minEventTime, DateTime maxEventTime) {
        setMinPhenomenonTime(minEventTime);
        setMaxPhenomenonTime(maxEventTime);
    }

    @Override
    public void setMaxPhenomenonTimeForOffering(String offering, DateTime maxTime) {
        notNullOrEmpty(OFFERING, offering);
        LOG.trace("Setting maximal EventTime for Offering {} to {}", offering, maxTime);
        if (maxTime == null) {
            this.maxPhenomenonTimeForOfferings.remove(offering);
        } else {
            this.maxPhenomenonTimeForOfferings.put(offering, DateTimeHelper.toUTC(maxTime));
        }
    }

    @Override
    public void setMinPhenomenonTimeForOffering(String offering, DateTime minTime) {
        notNullOrEmpty(OFFERING, offering);
        LOG.trace("Setting minimal EventTime for Offering {} to {}", offering, minTime);
        if (minTime == null) {
            this.minPhenomenonTimeForOfferings.remove(offering);
        } else {
            this.minPhenomenonTimeForOfferings.put(offering, DateTimeHelper.toUTC(minTime));
        }
    }

    @Override
    public void setMaxPhenomenonTimeForProcedure(String procedure, DateTime maxTime) {
        notNullOrEmpty(PROCEDURE, procedure);
        LOG.trace("Setting maximal phenomenon time for procedure {} to {}", procedure, maxTime);
        if (maxTime == null) {
            this.maxPhenomenonTimeForProcedures.remove(procedure);
        } else {
            this.maxPhenomenonTimeForProcedures.put(procedure, DateTimeHelper.toUTC(maxTime));
        }
    }

    @Override
    public void setMinPhenomenonTimeForProcedure(String procedure, DateTime minTime) {
        notNullOrEmpty(PROCEDURE, procedure);
        LOG.trace("Setting minimal phenomenon time for procedure {} to {}", procedure, minTime);
        if (minTime == null) {
            this.minPhenomenonTimeForProcedures.remove(procedure);
        } else {
            this.minPhenomenonTimeForProcedures.put(procedure, DateTimeHelper.toUTC(minTime));
        }
    }

    @Override
    public void setNameForOffering(String offering, String name) {
        notNullOrEmpty(OFFERING, offering);
        notNullOrEmpty(NAME, name);
        LOG.trace("Setting Name of Offering {} to {}", offering, name);
        this.nameForOfferings.put(offering, name);

    }

    @Override
    public void setI18nNameForOffering(String offering, MultilingualString name) {
        notNullOrEmpty(OFFERING, offering);
        Objects.requireNonNull(name, NAME);
        LOG.trace("Setting I18N Name of Offering {} to {}", offering, name);
        this.i18nNameForOfferings.put(offering, name);
    }

    @Override
    public void setI18nDescriptionForOffering(String offering,
                                              MultilingualString description) {
        notNullOrEmpty(OFFERING, offering);
        Objects.requireNonNull(description, DESCRIPTION);
        LOG.trace("Setting I18N Description of Offering {} to {}", offering, description);
        this.i18nDescriptionForOfferings.put(offering, description);
    }

    @Override
    public void setEnvelopeForOffering(String offering, ReferencedEnvelope envelope) {
        LOG.trace("Setting Envelope for Offering {} to {}", offering, envelope);
        this.envelopeForOfferings.put(offering, copyOf(envelope));
    }

    @Override
    public Set<String> getFeaturesOfInterestWithOffering() {
        return CollectionHelper.unionOfListOfLists(this.featuresOfInterestForOfferings.values());
    }

    @Override
    public void addAllowedObservationTypeForOffering(String offering, String allowedObservationType) {
        notNullOrEmpty(OFFERING, offering);
        notNullOrEmpty(ALLOWED_OBSERVATION_TYPE, allowedObservationType);
        LOG.trace("Adding AllowedObservationType {} to Offering {}", allowedObservationType, offering);
        this.allowedObservationTypeForOfferings.computeIfAbsent(offering, createSynchronizedSet())
                .add(allowedObservationType);
    }

    @Override
    public void addAllowedObservationTypesForOffering(String offering, Collection<String> allowedObservationTypes) {
        notNullOrEmpty(OFFERING, offering);
        noNullValues(ALLOWED_OBSERVATION_TYPES, allowedObservationTypes);
        LOG.trace("Adding AllowedObservationTypes {} to Offering {}", allowedObservationTypes, offering);
        this.allowedObservationTypeForOfferings.computeIfAbsent(offering, createSynchronizedSet())
                .addAll(allowedObservationTypes);
    }

    @Override
    public void addFeatureOfInterestForOffering(String offering, String featureOfInterest) {
        notNullOrEmpty(OFFERING, offering);
        notNullOrEmpty(FEATURE_OF_INTEREST, featureOfInterest);
        LOG.trace("Adding featureOfInterest {} to Offering {}", featureOfInterest, offering);
        this.featuresOfInterestForOfferings.computeIfAbsent(offering, createSynchronizedSet()).add(featureOfInterest);
    }

    @Override
    public void addFeatureOfInterestForResultTemplate(String resultTemplate, String featureOfInterest) {
        notNullOrEmpty(RESULT_TEMPLATE, resultTemplate);
        notNullOrEmpty(FEATURE_OF_INTEREST, featureOfInterest);
        LOG.trace("Adding FeatureOfInterest {} to SosResultTemplate {}", featureOfInterest, resultTemplate);
        this.featuresOfInterestForResultTemplates.computeIfAbsent(resultTemplate, createSynchronizedSet())
                .add(featureOfInterest);
    }

    @Override
    public void addFeaturesOfInterestForResultTemplate(String resultTemplate, Collection<String> featuresOfInterest) {
        notNullOrEmpty(RESULT_TEMPLATE, resultTemplate);
        noNullValues(FEATURES_OF_INTEREST, featuresOfInterest);
        LOG.trace("Adding FeatureOfInterest {} to SosResultTemplate {}", featuresOfInterest, resultTemplate);
        this.featuresOfInterestForResultTemplates.computeIfAbsent(resultTemplate, createSynchronizedSet())
                .addAll(featuresOfInterest);
    }

    @Override
    public void addObservablePropertyForOffering(String offering, String observableProperty) {
        notNullOrEmpty(OFFERING, offering);
        notNullOrEmpty(OBSERVABLE_PROPERTY, observableProperty);
        LOG.trace("Adding observableProperty {} to offering {}", observableProperty, offering);
        this.observablePropertiesForOfferings.computeIfAbsent(offering, createSynchronizedSet()).add(observableProperty);
    }

    @Override
    public void addObservablePropertyForProcedure(String procedure, String observableProperty) {
        notNullOrEmpty(PROCEDURE, procedure);
        notNullOrEmpty(OBSERVABLE_PROPERTY, observableProperty);
        LOG.trace("Adding observableProperty {} to procedure {}", observableProperty, procedure);
        this.observablePropertiesForProcedures.computeIfAbsent(procedure, createSynchronizedSet())
                .add(observableProperty);
    }

    @Override
    public void addObservablePropertyForResultTemplate(String resultTemplate, String observableProperty) {
        notNullOrEmpty(RESULT_TEMPLATE, resultTemplate);
        notNullOrEmpty(OBSERVABLE_PROPERTY, observableProperty);
        LOG.trace("Adding observableProperty {} to resultTemplate {}", observableProperty, resultTemplate);
        this.observedPropertiesForResultTemplates.computeIfAbsent(resultTemplate, createSynchronizedSet())
                .add(observableProperty);
    }

    @Override
    public void addObservationTypesForOffering(String offering, String observationType) {
        notNullOrEmpty(OFFERING, offering);
        notNullOrEmpty(OBSERVATION_TYPE, observationType);
        LOG.trace("Adding observationType {} to offering {}", observationType, offering);
        this.observationTypesForOfferings.computeIfAbsent(offering, createSynchronizedSet()).add(observationType);
    }

    @Override
    public void addOfferingForObservableProperty(String observableProperty, String offering) {
        notNullOrEmpty(OBSERVABLE_PROPERTY, observableProperty);
        notNullOrEmpty(OFFERING, offering);
        LOG.trace("Adding offering {} to observableProperty {}", offering, observableProperty);
        this.offeringsForObservableProperties.computeIfAbsent(observableProperty, createSynchronizedSet()).add(offering);
    }

    @Override
    public void addOfferingForProcedure(String procedure, String offering) {
        notNullOrEmpty(PROCEDURE, procedure);
        notNullOrEmpty(OFFERING, offering);
        LOG.trace("Adding offering {} to procedure {}", offering, procedure);
        this.offeringsForProcedures.computeIfAbsent(procedure, createSynchronizedSet()).add(offering);
    }

    @Override
    public void addProcedureForFeatureOfInterest(String featureOfInterest, String procedure) {
        notNullOrEmpty(FEATURE_OF_INTEREST, featureOfInterest);
        notNullOrEmpty(PROCEDURE, procedure);
        LOG.trace("Adding procedure {} to featureOfInterest {}", procedure, featureOfInterest);
        this.proceduresForFeaturesOfInterest.computeIfAbsent(featureOfInterest, createSynchronizedSet()).add(procedure);
    }

    @Override
    public void addProcedureForObservableProperty(String observableProperty, String procedure) {
        notNullOrEmpty(FEATURE_OF_INTEREST, observableProperty);
        notNullOrEmpty(PROCEDURE, procedure);
        LOG.trace("Adding procedure {} to observableProperty {}", procedure, observableProperty);
        this.proceduresForObservableProperties.computeIfAbsent(observableProperty, createSynchronizedSet())
                .add(procedure);
    }

    @Override
    public void addProcedureForOffering(String offering, String procedure) {
        notNullOrEmpty(OFFERING, offering);
        notNullOrEmpty(PROCEDURE, procedure);
        LOG.trace("Adding procedure {} to offering {}", procedure, offering);
        this.proceduresForOfferings.computeIfAbsent(offering, createSynchronizedSet()).add(procedure);
    }

    @Override
    public void addRelatedFeatureForOffering(String offering, String relatedFeature) {
        notNullOrEmpty(OFFERING, offering);
        notNullOrEmpty(RELATED_FEATURE, relatedFeature);
        LOG.trace("Adding relatedFeature {} to offering {}", relatedFeature, offering);
        this.relatedFeaturesForOfferings.computeIfAbsent(offering, createSynchronizedSet()).add(relatedFeature);
    }

    @Override
    public void addRelatedFeaturesForOffering(String offering, Collection<String> relatedFeature) {
        notNullOrEmpty(OFFERING, offering);
        noNullValues(RELATED_FEATURE, relatedFeature);
        LOG.trace("Adding relatedFeatures {} to offering {}", relatedFeature, offering);
        this.relatedFeaturesForOfferings.computeIfAbsent(offering, createSynchronizedSet()).addAll(relatedFeature);
    }

    @Override
    public void addResultTemplateForOffering(String offering, String resultTemplate) {
        notNullOrEmpty(OFFERING, offering);
        notNullOrEmpty(RESULT_TEMPLATE, resultTemplate);
        LOG.trace("Adding resultTemplate {} to offering {}", resultTemplate, offering);
        this.resultTemplatesForOfferings.computeIfAbsent(offering, createSynchronizedSet()).add(resultTemplate);
    }

    @Override
    public void addRoleForRelatedFeature(String relatedFeature, String role) {
        notNullOrEmpty(RELATED_FEATURE, relatedFeature);
        notNullOrEmpty("role", role);
        LOG.trace("Adding role {} to relatedFeature {}", role, relatedFeature);
        this.rolesForRelatedFeatures.computeIfAbsent(relatedFeature, createSynchronizedSet()).add(role);
    }

    @Override
    public void removeAllowedObservationTypeForOffering(String offering, String allowedObservationType) {
        notNullOrEmpty(OFFERING, offering);
        notNullOrEmpty("allowedObservationType", allowedObservationType);
        LOG.trace("Removing allowedObservationType {} from offering {}", allowedObservationType, offering);
        this.allowedObservationTypeForOfferings.getOrDefault(offering, Collections.emptySet())
                .remove(allowedObservationType);
    }

    @Override
    public void removeAllowedObservationTypesForOffering(String offering) {
        notNullOrEmpty(OFFERING, offering);
        LOG.trace("Removing allowedObservationTypes for offering {}", offering);
        this.allowedObservationTypeForOfferings.remove(offering);
    }

    @Override
    public void removeEnvelopeForOffering(String offering) {
        notNullOrEmpty(OFFERING, offering);
        LOG.trace("Removing envelope for offering {}", offering);
        this.envelopeForOfferings.remove(offering);
    }

    @Override
    public void removeEpsgCode(Integer epsgCode) {
        Objects.requireNonNull(epsgCode, EPSG_CODE);
        LOG.trace("Removing epsgCode {}", epsgCode);
        this.epsgCodes.remove(epsgCode);
    }

    @Override
    public void removeEpsgCodes(Collection<Integer> epsgCodes) {
        noNullValues(EPSG_CODES, epsgCodes);
        epsgCodes.forEach(this::removeEpsgCode);
    }

    @Override
    public void removeFeatureOfInterestForOffering(String offering, String featureOfInterest) {
        notNullOrEmpty(OFFERING, offering);
        notNullOrEmpty(FEATURE_OF_INTEREST, featureOfInterest);
        LOG.trace("Removing featureOfInterest {} from offering {}", featureOfInterest, offering);
        this.featuresOfInterestForOfferings.getOrDefault(offering, Collections.emptySet()).remove(featureOfInterest);
    }

    @Override
    public void removeFeatureOfInterestForResultTemplate(String resultTemplate, String featureOfInterest) {
        notNullOrEmpty(RESULT_TEMPLATE, resultTemplate);
        notNullOrEmpty(FEATURE_OF_INTEREST, featureOfInterest);
        LOG.trace("Removing featureOfInterest {} from resultTemplate {}", featureOfInterest, resultTemplate);
        this.featuresOfInterestForResultTemplates.getOrDefault(resultTemplate, Collections.emptySet())
                .remove(featureOfInterest);
    }

    @Override
    public void removeFeaturesOfInterestForOffering(String offering) {
        notNullOrEmpty(OFFERING, offering);
        LOG.trace("Removing featuresOfInterest for offering {}", offering);
        this.featuresOfInterestForOfferings.remove(offering);
    }

    @Override
    public void removeFeaturesOfInterestForResultTemplate(String resultTemplate) {
        notNullOrEmpty(RESULT_TEMPLATE, resultTemplate);
        LOG.trace("Removing featuresOfInterest for resultTemplate {}", resultTemplate);
        this.featuresOfInterestForResultTemplates.remove(resultTemplate);
    }

    @Override
    public void removeMaxPhenomenonTimeForOffering(String offering) {
        notNullOrEmpty(OFFERING, offering);
        LOG.trace("Removing maxEventTime for offering {}", offering);
        this.maxPhenomenonTimeForOfferings.remove(offering);
    }

    @Override
    public void removeMinPhenomenonTimeForOffering(String offering) {
        notNullOrEmpty(OFFERING, offering);
        LOG.trace("Removing minEventTime for offering {}", offering);
        this.minPhenomenonTimeForOfferings.remove(offering);
    }

    @Override
    public void removeMaxPhenomenonTimeForProcedure(String procedure) {
        notNullOrEmpty(PROCEDURE, procedure);
        LOG.trace("Removing maxEventTime for procedure {}", procedure);
        this.maxPhenomenonTimeForProcedures.remove(procedure);
    }

    @Override
    public void removeMinPhenomenonTimeForProcedure(String procedure) {
        notNullOrEmpty(PROCEDURE, procedure);
        LOG.trace("Removing minEventTime for procedure {}", procedure);
        this.minPhenomenonTimeForProcedures.remove(procedure);
    }

    @Override
    public void removeNameForOffering(String offering) {
        notNullOrEmpty(OFFERING, offering);
        LOG.trace("Removing name for offering {}", offering);
        this.nameForOfferings.remove(offering);
    }

    @Override
    public void removeObservablePropertiesForOffering(String offering) {
        notNullOrEmpty(OFFERING, offering);
        LOG.trace("Removing observableProperties for offering {}", offering);
        this.observablePropertiesForOfferings.remove(offering);
    }

    @Override
    public void removeObservablePropertiesForProcedure(String procedure) {
        notNullOrEmpty(PROCEDURE, procedure);
        LOG.trace("Removing observableProperties for procedure {}", procedure);
        this.observablePropertiesForProcedures.remove(procedure);
    }

    @Override
    public void removeObservablePropertiesForResultTemplate(String resultTemplate) {
        notNullOrEmpty(RESULT_TEMPLATE, resultTemplate);
        LOG.trace("Removing observableProperties for resultTemplate {}", resultTemplate);
        this.observedPropertiesForResultTemplates.remove(resultTemplate);
    }

    @Override
    public void removeObservablePropertyForOffering(String offering, String observableProperty) {
        notNullOrEmpty(OFFERING, offering);
        notNullOrEmpty(OBSERVABLE_PROPERTY, observableProperty);
        LOG.trace("Removing observableProperty {} from offering {}", observableProperty, offering);
        this.observablePropertiesForOfferings.getOrDefault(offering, Collections.emptySet()).remove(observableProperty);
    }

    @Override
    public void removeObservablePropertyForProcedure(String procedure, String observableProperty) {
        notNullOrEmpty(PROCEDURE, procedure);
        notNullOrEmpty(OBSERVABLE_PROPERTY, observableProperty);
        LOG.trace("Removing observableProperty {} from procedure {}", observableProperty, procedure);
        this.observablePropertiesForProcedures.getOrDefault(procedure, Collections.emptySet())
                .remove(observableProperty);
    }

    @Override
    public void removeObservablePropertyForResultTemplate(String resultTemplate, String observableProperty) {
        notNullOrEmpty(RESULT_TEMPLATE, resultTemplate);
        notNullOrEmpty(OBSERVABLE_PROPERTY, observableProperty);
        LOG.trace("Removing observableProperty {} from resultTemplate {}", observableProperty, resultTemplate);
        this.observedPropertiesForResultTemplates.getOrDefault(resultTemplate, Collections.emptySet())
                .remove(observableProperty);
    }

    @Override
    public void removeObservationTypeForOffering(String offering, String observationType) {
        notNullOrEmpty(OFFERING, offering);
        notNullOrEmpty(OBSERVATION_TYPE, observationType);
        LOG.trace("Removing observationType {} from offering {}", observationType, offering);
        this.observationTypesForOfferings.getOrDefault(offering, Collections.emptySet()).remove(observationType);
    }

    @Override
    public void removeObservationTypesForOffering(String offering) {
        notNullOrEmpty(OFFERING, offering);
        LOG.trace("Removing observationTypes for offering {}", offering);
        this.observationTypesForOfferings.remove(offering);
    }

    @Override
    public void removeOfferingForObservableProperty(String observableProperty, String offering) {
        notNullOrEmpty(OBSERVABLE_PROPERTY, observableProperty);
        notNullOrEmpty(OFFERING, offering);
        LOG.trace("Removing offering {} from observableProperty {}", offering, observableProperty);
        this.offeringsForObservableProperties.getOrDefault(observableProperty, Collections.emptySet()).remove(offering);
    }

    @Override
    public void removeOfferingForProcedure(String procedure, String offering) {
        notNullOrEmpty(PROCEDURE, procedure);
        notNullOrEmpty(OFFERING, offering);
        LOG.trace("Removing offering {} from procedure {}", offering, procedure);
        this.offeringsForProcedures.getOrDefault(procedure, Collections.emptySet()).remove(offering);
    }

    @Override
    public void removeOfferingsForObservableProperty(String observableProperty) {
        notNullOrEmpty(OBSERVABLE_PROPERTY, observableProperty);
        LOG.trace("Removing offerings for observableProperty {}", observableProperty);
        this.offeringsForObservableProperties.remove(observableProperty);
    }

    @Override
    public void removeOfferingsForProcedure(String procedure) {
        notNullOrEmpty(PROCEDURE, procedure);
        LOG.trace("Removing offering for procedure {}", procedure);
        this.offeringsForProcedures.remove(procedure);
    }

    @Override
    public void removeProcedureForFeatureOfInterest(String featureOfInterest, String procedure) {
        notNullOrEmpty(FEATURE_OF_INTEREST, featureOfInterest);
        notNullOrEmpty(PROCEDURE, procedure);
        LOG.trace("Removing procedure {} from featureOfInterest {}", procedure, featureOfInterest);
        this.proceduresForFeaturesOfInterest.getOrDefault(featureOfInterest, Collections.emptySet()).remove(procedure);
    }

    @Override
    public void removeProcedureForObservableProperty(String observableProperty, String procedure) {
        notNullOrEmpty(OBSERVABLE_PROPERTY, observableProperty);
        notNullOrEmpty(PROCEDURE, procedure);
        LOG.trace("Removing procedure {} from observableProperty {}", procedure, observableProperty);
        this.proceduresForObservableProperties.getOrDefault(observableProperty, Collections.emptySet())
                .remove(procedure);
    }

    @Override
    public void removeProcedureForOffering(String offering, String procedure) {
        notNullOrEmpty(OFFERING, offering);
        notNullOrEmpty(PROCEDURE, procedure);
        LOG.trace("Removing procedure {} from offering {}", procedure, offering);
        this.proceduresForOfferings.getOrDefault(offering, Collections.emptySet()).remove(procedure);
    }

    @Override
    public void removeProceduresForFeatureOfInterest(String featureOfInterest) {
        notNullOrEmpty(FEATURE_OF_INTEREST, featureOfInterest);
        LOG.trace("Removing procedures for featureOfInterest {}", featureOfInterest);
        this.proceduresForFeaturesOfInterest.remove(featureOfInterest);
    }

    @Override
    public void removeProceduresForObservableProperty(String observableProperty) {
        notNullOrEmpty(OBSERVABLE_PROPERTY, observableProperty);
        LOG.trace("Removing procedures for observableProperty {}", observableProperty);
        this.proceduresForObservableProperties.remove(observableProperty);
    }

    @Override
    public void removeProceduresForOffering(String offering) {
        notNullOrEmpty(OFFERING, offering);
        LOG.trace("Removing procedures for offering {}", offering);
        this.proceduresForOfferings.remove(offering);
    }

    @Override
    public void removeRelatedFeatureForOffering(String offering, String relatedFeature) {
        notNullOrEmpty(OFFERING, offering);
        notNullOrEmpty(RELATED_FEATURE, relatedFeature);
        LOG.trace("Removing relatedFeature {} from offering {}", relatedFeature, offering);
        this.relatedFeaturesForOfferings.getOrDefault(offering, Collections.emptySet()).remove(relatedFeature);
    }

    @Override
    public void removeRelatedFeaturesForOffering(String offering) {
        notNullOrEmpty(OFFERING, offering);
        LOG.trace("Removing RelatedFeatures for offering {}", offering);
        this.relatedFeaturesForOfferings.remove(offering);
    }

    @Override
    public void removeResultTemplateForOffering(String offering, String resultTemplate) {
        notNullOrEmpty(OFFERING, offering);
        notNullOrEmpty(RESULT_TEMPLATE, resultTemplate);
        LOG.trace("Removing resultTemplate {} from offering {}", resultTemplate, offering);
        this.resultTemplatesForOfferings.getOrDefault(offering, Collections.emptySet()).remove(resultTemplate);
    }

    @Override
    public void removeResultTemplatesForOffering(String offering) {
        notNullOrEmpty(OFFERING, offering);
        LOG.trace("Removing ResultTemplates for offering {}", offering);
        this.resultTemplatesForOfferings.remove(offering);
    }

    @Override
    public void removeRoleForRelatedFeature(String relatedFeature, String role) {
        notNullOrEmpty(RELATED_FEATURE, relatedFeature);
        notNullOrEmpty(ROLE, role);
        LOG.trace("Removing role {} from relatedFeature {}", role, relatedFeature);
        this.rolesForRelatedFeatures.getOrDefault(relatedFeature, Collections.emptySet()).remove(role);
    }

    @Override
    public void removeRolesForRelatedFeature(String relatedFeature) {
        notNullOrEmpty(RELATED_FEATURE, relatedFeature);
        LOG.trace("Removing roles for relatedFeature {}", relatedFeature);
        this.rolesForRelatedFeatures.remove(relatedFeature);
    }

    @Override
    public void removeRolesForRelatedFeatureNotIn(Collection<String> relatedFeatures) {
        Objects.requireNonNull(relatedFeatures, RELATED_FEATURES);
        final Iterator<String> iter = this.rolesForRelatedFeatures.keySet().iterator();
        while (iter.hasNext()) {
            if (!relatedFeatures.contains(iter.next())) {
                iter.remove();
            }
        }
    }

    @Override
    public void setAllowedObservationTypeForOffering(String offering, Collection<String> observationTypes) {
        notNullOrEmpty(OFFERING, offering);
        final Set<String> newValue = newSynchronizedSet(observationTypes);
        LOG.trace("Setting allowedObservationTypes for offering {} to {}", offering, newValue);
        this.allowedObservationTypeForOfferings.put(offering, newValue);
    }

    @Override
    public void setAllowedFeatureOfInterestTypeForOffering(String offering, Collection<String> featureTypes) {
        notNullOrEmpty(OFFERING, offering);
        final Set<String> newValue = newSynchronizedSet(featureTypes);
        LOG.trace("Setting allowedFeatureOfInterestTypes for offering {} to {}", offering, newValue);
        this.allowedFeatureOfInterestTypeForOfferings.put(offering, newValue);
    }

    @Override
    public void setFeaturesOfInterestForOffering(String offering, Collection<String> featureOfInterest) {
        notNullOrEmpty(OFFERING, offering);
        final Set<String> newValue = newSynchronizedSet(featureOfInterest);
        LOG.trace("Setting featureOfInterest for offering {} to {}", offering, newValue);
        this.featuresOfInterestForOfferings.put(offering, newValue);
    }

    @Override
    public void setObservablePropertiesForResultTemplate(String resultTemplate,
                                                         Collection<String> observableProperties) {
        notNullOrEmpty(RESULT_TEMPLATE, resultTemplate);
        final Set<String> newValue = newSynchronizedSet(observableProperties);
        LOG.trace("Setting observableProperties for resultTemplate {} to {}", resultTemplate, newValue);
        this.observedPropertiesForResultTemplates.put(resultTemplate, newValue);
    }

    @Override
    public void addParentFeature(String featureOfInterest, String parentFeature) {
        notNullOrEmpty(FEATURE_OF_INTEREST, featureOfInterest);
        notNullOrEmpty(PARENT_FEATURE, parentFeature);
        LOG.trace("Adding parentFeature {} to featureOfInterest {}", parentFeature, featureOfInterest);
        this.parentFeaturesForFeaturesOfInterest.computeIfAbsent(featureOfInterest, createSynchronizedSet())
                .add(parentFeature);
        this.childFeaturesForFeatureOfInterest.computeIfAbsent(parentFeature, createSynchronizedSet())
                .add(featureOfInterest);
    }

    @Override
    public void addParentFeatures(String featureOfInterest, Collection<String> parentFeatures) {
        notNullOrEmpty(FEATURE_OF_INTEREST, featureOfInterest);
        noNullOrEmptyValues(PARENT_FEATURES, parentFeatures);
        LOG.trace("Adding parentFeature {} to featureOfInterest {}", parentFeatures, featureOfInterest);
        this.parentFeaturesForFeaturesOfInterest.computeIfAbsent(featureOfInterest, createSynchronizedSet())
                .addAll(parentFeatures);
        parentFeatures.forEach(parentFeature
                -> this.childFeaturesForFeatureOfInterest.computeIfAbsent(parentFeature, createSynchronizedSet())
                        .add(featureOfInterest)
        );
    }

    @Override
    public void addParentProcedure(String procedure, String parentProcedure) {
        notNullOrEmpty(PROCEDURE, procedure);
        notNullOrEmpty(PARENT_PROCEDURE, parentProcedure);
        LOG.trace("Adding parentProcedure {} to procedure {}", parentProcedure, procedure);
        this.parentProceduresForProcedures.computeIfAbsent(procedure, createSynchronizedSet()).add(parentProcedure);
        this.childProceduresForProcedures.computeIfAbsent(parentProcedure, createSynchronizedSet()).add(procedure);
    }

    @Override
    public void addParentProcedures(String procedure, Collection<String> parentProcedures) {
        notNullOrEmpty(PROCEDURE, procedure);
        noNullOrEmptyValues(PARENT_PROCEDURES, parentProcedures);
        LOG.trace("Adding parentProcedures {} to procedure {}", parentProcedures, procedure);
        this.parentProceduresForProcedures.computeIfAbsent(procedure, createSynchronizedSet()).addAll(parentProcedures);
        parentProcedures.forEach((parentProcedure)
                -> this.childProceduresForProcedures.computeIfAbsent(parentProcedure, createSynchronizedSet())
                        .add(procedure));
    }

    @Override
    public void updateEnvelopeForOffering(String offering, Envelope envelope) {
        notNullOrEmpty(OFFERING, offering);
        Objects.requireNonNull(envelope, ENVELOPE);
        if (hasEnvelopeForOffering(offering)) {
            final ReferencedEnvelope offeringEnvelope = this.envelopeForOfferings.get(offering);
            LOG.trace("Expanding envelope {} for offering {} to include {}", offeringEnvelope, offering, envelope);
            offeringEnvelope.expandToInclude(envelope);
        } else {
            setEnvelopeForOffering(offering, new ReferencedEnvelope(envelope, getDefaultEPSGCode()));
        }
    }

    @Override
    public void updatePhenomenonTime(Time eventTime) {
        Objects.requireNonNull(eventTime, EVENT_TIME);
        final TimePeriod tp = toTimePeriod(eventTime);
        LOG.trace("Expanding global EventTime to include {}", tp);
        if (!hasMinPhenomenonTime() || getMinPhenomenonTime().isAfter(tp.getStart())) {
            setMinPhenomenonTime(tp.getStart());
        }
        if (!hasMaxPhenomenonTime() || getMaxPhenomenonTime().isBefore(tp.getEnd())) {
            setMaxPhenomenonTime(tp.getEnd());
        }
    }

    @Override
    public void updateGlobalEnvelope(Envelope envelope) {
        Objects.requireNonNull(envelope, ENVELOPE);
        if (hasGlobalEnvelope()) {
            LOG.trace("Expanding envelope {} to include {}", this.globalEnvelope, envelope);
            this.globalEnvelope.expandToInclude(envelope);
        } else {
            setGlobalEnvelope(new ReferencedEnvelope(new Envelope(envelope), getDefaultEPSGCode()));
        }
    }

    @Override
    public void updatePhenomenonTimeForOffering(String offering, Time eventTime) {
        notNullOrEmpty(OFFERING, offering);
        Objects.requireNonNull(eventTime, EVENT_TIME);
        final TimePeriod tp = toTimePeriod(eventTime);
        LOG.trace("Expanding EventTime of offering {} to include {}", offering, tp);
        if (!hasMaxPhenomenonTimeForOffering(offering) ||
            getMaxPhenomenonTimeForOffering(offering).isBefore(tp.getEnd())) {
            setMaxPhenomenonTimeForOffering(offering, tp.getEnd());
        }
        if (!hasMinPhenomenonTimeForOffering(offering) ||
            getMinPhenomenonTimeForOffering(offering).isAfter(tp.getStart())) {
            setMinPhenomenonTimeForOffering(offering, tp.getStart());
        }
    }

    @Override
    public void updatePhenomenonTimeForProcedure(String procedure, Time eventTime) {
        notNullOrEmpty(PROCEDURE, procedure);
        Objects.requireNonNull(eventTime, EVENT_TIME);
        final TimePeriod tp = toTimePeriod(eventTime);
        LOG.trace("Expanding phenomenon time of procedure {} to include {}", procedure, tp);
        if (!hasMaxPhenomenonTimeForProcedure(procedure) ||
            getMaxPhenomenonTimeForProcedure(procedure).isBefore(tp.getEnd())) {
            setMaxPhenomenonTimeForProcedure(procedure, tp.getEnd());
        }
        if (!hasMinPhenomenonTimeForProcedure(procedure) ||
            getMinPhenomenonTimeForProcedure(procedure).isAfter(tp.getStart())) {
            setMinPhenomenonTimeForProcedure(procedure, tp.getStart());
        }
    }

    @Override
    public void recalculateGlobalEnvelope() {
        LOG.trace("Recalculating global spatial envelope based on offerings");
        ReferencedEnvelope envelope = new ReferencedEnvelope(new Envelope(), defaultEpsgCode);
        this.offerings.stream().map(this::getEnvelopeForOffering)
                .filter(Objects::nonNull)
                .map(ReferencedEnvelope::getEnvelope)
                .filter(e -> !e.isNull())
                .forEach(e -> {
                    envelope.expandToInclude(e);
                    LOG.trace("Envelope expanded to include '{}' resulting in '{}'", e, envelope);
                });
        setGlobalEnvelope(envelope);
        LOG.trace("Spatial envelope finally set to '{}'", getGlobalEnvelope());
    }

    @Override
    public void recalculatePhenomenonTime() {
        LOG.trace("Recalculating global phenomenon time based on offerings");
        MinMax<DateTime> minMax = this.offerings.stream()
                .map(offering -> new MinMax<>(getMinPhenomenonTimeForOffering(offering),
                                              getMaxPhenomenonTimeForOffering(offering)))
                .reduce(Functions.mergeLeft((a, b) -> a.extend(b, Comparator.naturalOrder())))
                .orElseGet(MinMax::new);
        if (!getOfferings().isEmpty() && minMax.getMinimum() == null || minMax.getMaximum() == null) {
                LOG.error("Error in cache! Reset of global temporal bounding box failed. Max: '{}'; Min: '{}'",
                          minMax.getMaximum(), minMax.getMinimum());
        }
        setPhenomenonTime(minMax.getMinimum(), minMax.getMaximum());
        LOG.trace("Global temporal bounding box reset done. Min: '{}'; Max: '{}'", getMinPhenomenonTime(),
                  getMaxPhenomenonTime());
    }

    @Override
    public void removeMaxResultTimeForOffering(String offering) {
        notNullOrEmpty(OFFERING, offering);
        LOG.trace("Removing maxResultTime for offering {}", offering);
        this.maxResultTimeForOfferings.remove(offering);
    }

    @Override
    public void removeMinResultTimeForOffering(String offering) {
        notNullOrEmpty(OFFERING, offering);
        LOG.trace("Removing minResultTime for offering {}", offering);
        this.minResultTimeForOfferings.remove(offering);
    }

    @Override
    public void setResultTime(DateTime min, DateTime max) {
        setMinResultTime(min);
        setMaxResultTime(max);
    }

    @Override
    public void updateResultTime(Time resultTime) {
        if (resultTime == null) {
            return;
        }
        final TimePeriod tp = toTimePeriod(resultTime);
        LOG.trace("Expanding global ResultTime to include {}", tp);
        if (!hasMinResultTime() || getMinResultTime().isAfter(tp.getStart())) {
            setMinResultTime(tp.getStart());
        }
        if (!hasMaxResultTime() || getMaxResultTime().isBefore(tp.getEnd())) {
            setMaxResultTime(tp.getEnd());
        }
    }

    @Override
    public void recalculateResultTime() {
        LOG.trace("Recalculating global result time based on offerings");
        DateTime globalMax = null;
        DateTime globalMin = null;
        if (!getOfferings().isEmpty()) {
            for (String offering : getOfferings()) {
                if (hasMaxResultTimeForOffering(offering)) {
                    final DateTime offeringMax = getMaxResultTimeForOffering(offering);
                    if (globalMax == null || offeringMax.isAfter(globalMax)) {
                        globalMax = offeringMax;
                    }
                }
                if (hasMinResultTimeForOffering(offering)) {
                    final DateTime offeringMin = getMinResultTimeForOffering(offering);
                    if (globalMin == null || offeringMin.isBefore(globalMin)) {
                        globalMin = offeringMin;
                    }
                }
            }
        }
        setResultTime(globalMin, globalMax);
        LOG.trace("Global result time bounding box reset done. Min: '{}'); Max: '{}'", getMinResultTime(),
                  getMaxResultTime());
    }

    @Override
    public void setMaxResultTimeForOffering(String offering, DateTime maxTime) {
        notNullOrEmpty(OFFERING, offering);
        LOG.trace("Setting maximal ResultTime for Offering {} to {}", offering, maxTime);
        if (maxTime == null) {
            this.maxResultTimeForOfferings.remove(offering);
        } else {
            this.maxResultTimeForOfferings.put(offering, DateTimeHelper.toUTC(maxTime));
        }
    }

    @Override
    public void setMinResultTimeForOffering(String offering, DateTime minTime) {
        notNullOrEmpty(OFFERING, offering);
        LOG.trace("Setting minimal ResultTime for Offering {} to {}", offering, minTime);
        if (minTime == null) {
            this.minResultTimeForOfferings.remove(offering);
        } else {
            this.minResultTimeForOfferings.put(offering, DateTimeHelper.toUTC(minTime));
        }
    }

    @Override
    public void updateResultTimeForOffering(String offering, Time resultTime) {
        notNullOrEmpty(OFFERING, offering);
        if (resultTime == null) {
            return;
        }
        final TimePeriod tp = toTimePeriod(resultTime);
        LOG.trace("Expanding EventTime of offering {} to include {}", offering, tp);
        if (!hasMaxResultTimeForOffering(offering) || getMaxResultTimeForOffering(offering).isBefore(tp.getEnd())) {
            setMaxResultTimeForOffering(offering, tp.getEnd());
        }
        if (!hasMinResultTimeForOffering(offering) || getMinResultTimeForOffering(offering).isAfter(tp.getStart())) {
            setMinResultTimeForOffering(offering, tp.getStart());
        }
    }

    @Override
    public void clearFeaturesOfInterest() {
        LOG.trace("Clearing features of interest");
        this.featuresOfInterest.clear();
    }

    @Override
    public void clearProceduresForFeatureOfInterest() {
        LOG.trace("Clearing procedures for feature of interest");
        this.proceduresForFeaturesOfInterest.clear();
    }

    @Override
    public void clearFeatureHierarchy() {
        LOG.trace("Clearing feature hierarchy");
        this.childFeaturesForFeatureOfInterest.clear();
        this.parentFeaturesForFeaturesOfInterest.clear();
    }

    @Override
    public void clearProceduresForOfferings() {
        LOG.trace("Clearing procedures for offerings");
        this.proceduresForOfferings.clear();
    }

    @Override
    public void clearNameForOfferings() {
        LOG.trace("Clearing names for offerings");
        this.nameForOfferings.clear();
    }

    @Override
    public void clearI18nNamesForOfferings() {
        LOG.trace("Clearing i18n names for offerings");
        this.i18nNameForOfferings.clear();
    }

    @Override
    public void clearI18nDescriptionsNameForOfferings() {
        LOG.trace("Clearing i18n descriptions for offerings");
        this.i18nDescriptionForOfferings.clear();
    }

    @Override
    public void clearObservablePropertiesForOfferings() {
        LOG.trace("Clearing observable properties for offerings");
        this.observablePropertiesForOfferings.clear();
    }

    @Override
    public void clearRelatedFeaturesForOfferings() {
        LOG.trace("Clearing related features for offerings");
        this.relatedFeaturesForOfferings.clear();
    }

    @Override
    public void clearObservationTypesForOfferings() {
        LOG.trace("Clearing observation types for offerings");
        this.observationTypesForOfferings.clear();
    }

    @Override
    public void clearAllowedObservationTypeForOfferings() {
        LOG.trace("Clearing allowed observation types for offerings");
        this.allowedObservationTypeForOfferings.clear();
    }

    @Override
    public void clearEnvelopeForOfferings() {
        LOG.trace("Clearing envelope for offerings");
        this.envelopeForOfferings.clear();
    }

    @Override
    public void clearFeaturesOfInterestForOfferings() {
        LOG.trace("Clearing features of interest for offerings");
        this.featuresOfInterestForOfferings.clear();
    }

    @Override
    public void clearMinPhenomenonTimeForOfferings() {
        LOG.trace("Clearing min phenomenon time for offerings");
        this.minPhenomenonTimeForOfferings.clear();
    }

    @Override
    public void clearMaxPhenomenonTimeForOfferings() {
        LOG.trace("Clearing max phenomenon time for offerings");
        this.maxPhenomenonTimeForOfferings.clear();
    }

    @Override
    public void clearMinPhenomenonTimeForProcedures() {
        LOG.trace("Clearing min phenomenon time for procedures");
        this.minPhenomenonTimeForProcedures.clear();
    }

    @Override
    public void clearMaxPhenomenonTimeForProcedures() {
        LOG.trace("Clearing max phenomenon time for procedures");
        this.maxPhenomenonTimeForProcedures.clear();
    }

    @Override
    public void clearMinResultTimeForOfferings() {
        LOG.trace("Clearing min result time for offerings");
        this.minResultTimeForOfferings.clear();
    }

    @Override
    public void clearMaxResultTimeForOfferings() {
        LOG.trace("Clearing max result time for offerings");
        this.maxResultTimeForOfferings.clear();
    }

    @Override
    public void clearOfferings() {
        LOG.trace("Clearing offerings");
        this.offerings.clear();
    }

    @Override
    public void addOffering(String offering) {
        notNullOrEmpty(OFFERING, offering);
        LOG.trace("Adding offering {}", offering);
        this.offerings.add(offering);
    }

    @Override
    public void addOfferings(Collection<String> offerings) {
        noNullValues(OFFERINGS, offerings);
        offerings.forEach(this::addOffering);
    }

    @Override
    public void removeOffering(String offering) {
        notNullOrEmpty(OFFERING, offering);
        LOG.trace("Removing Offering {}", offering);
        this.offerings.remove(offering);
    }

    @Override
    public void removeOfferings(Collection<String> offerings) {
        noNullValues(OFFERINGS, offerings);
        offerings.forEach(this::removeOffering);
    }

    @Override
    public void addHiddenChildProcedureForOffering(String offering, String procedure) {
        notNullOrEmpty(OFFERING, offering);
        notNullOrEmpty(PROCEDURE, procedure);
        LOG.trace("Adding hidden child procedure {} to offering {}", procedure, offering);
        this.hiddenChildProceduresForOfferings.computeIfAbsent(offering, createSynchronizedSet()).add(procedure);
    }

    @Override
    public void removeHiddenChildProcedureForOffering(String offering, String procedure) {
        notNullOrEmpty(OFFERING, offering);
        notNullOrEmpty(PROCEDURE, procedure);
        LOG.trace("Removing hidden chil procedure {} from offering {}", procedure, offering);
        this.hiddenChildProceduresForOfferings.getOrDefault(offering, Collections.emptySet()).remove(procedure);
    }

    @Override
    public void setHiddenChildProceduresForOffering(String offering, Collection<String> procedures) {
        final Set<String> newValue = newSynchronizedSet(procedures);
        LOG.trace("Setting hidden child Procedures for Offering {} to {}", offering, newValue);
        this.hiddenChildProceduresForOfferings.put(offering, newValue);
    }

    @Override
    public void clearHiddenChildProceduresForOfferings() {
        LOG.trace("Clearing hidden child procedures for offerings");
        this.hiddenChildProceduresForOfferings.clear();
    }

    @Override
    public void removeSpatialFilteringProfileEnvelopeForOffering(String offering) {
        notNullOrEmpty(OFFERING, offering);
        LOG.trace("Removing Spatial Filtering Profile envelope for offering {}", offering);
        this.spatialFilteringProfileEnvelopeForOfferings.remove(offering);
    }

    @Override
    public void setSpatialFilteringProfileEnvelopeForOffering(String offering, ReferencedEnvelope envelope) {
        LOG.trace("Setting Spatial Filtering Profile Envelope for Offering {} to {}", offering, envelope);
        this.spatialFilteringProfileEnvelopeForOfferings.put(offering, copyOf(envelope));
    }

    @Override
    public void updateSpatialFilteringProfileEnvelopeForOffering(String offering, Envelope envelope) {
        notNullOrEmpty(OFFERING, offering);
        Objects.requireNonNull(envelope, ENVELOPE);
        if (hasSpatialFilteringProfileEnvelopeForOffering(offering)) {
            final ReferencedEnvelope offeringEnvelope = this.spatialFilteringProfileEnvelopeForOfferings.get(offering);
            LOG.trace("Expanding Spatial Filtering Profile envelope {} for offering {} to include {}",
                      offeringEnvelope, offering, envelope);
            offeringEnvelope.expandToInclude(envelope);
        } else {
            setSpatialFilteringProfileEnvelopeForOffering(offering, new ReferencedEnvelope(envelope, getDefaultEPSGCode()));
        }
    }

    @Override
    public void clearSpatialFilteringProfileEnvelopeForOfferings() {
        LOG.trace("Clearing Spatial Filtering Profile envelope for offerings");
        this.spatialFilteringProfileEnvelopeForOfferings.clear();
    }

    @Override
    public void addFeatureOfInterestTypesForOffering(String offering, String featureOfInterestType) {
        notNullOrEmpty(OFFERING, offering);
        notNullOrEmpty(FEATURE_OF_INTEREST_TYPE, featureOfInterestType);
        LOG.trace("Adding observationType {} to offering {}", featureOfInterestType, offering);
        this.featureOfInterestTypesForOfferings.computeIfAbsent(offering, createSynchronizedSet())
                .add(featureOfInterestType);
    }

    @Override
    public void removeFeatureOfInterestTypeForOffering(String offering, String featureOfInterestType) {
        notNullOrEmpty(OFFERING, offering);
        notNullOrEmpty(FEATURE_OF_INTEREST_TYPE, featureOfInterestType);
        LOG.trace("Removing observationType {} from offering {}", featureOfInterestType, offering);
        this.featureOfInterestTypesForOfferings.getOrDefault(offering, Collections.emptySet())
                .remove(featureOfInterestType);
    }

    @Override
    public void removeFeatureOfInterestTypesForOffering(String offering) {
        notNullOrEmpty(OFFERING, offering);
        LOG.trace("Removing featureOfInterestTypes for offering {}", offering);
        this.featureOfInterestTypesForOfferings.remove(offering);
    }

    @Override
    public void setFeatureOfInterestTypesForOffering(String offering, Collection<String> featureOfInterestTypes) {
        final Set<String> newValue = newSynchronizedSet(featureOfInterestTypes);
        LOG.trace("Setting FeatureOfInterestTypes for Offering {} to {}", offering, newValue);
        this.featureOfInterestTypesForOfferings.put(offering, newValue);
    }

    @Override
    public void addAllowedFeatureOfInterestTypeForOffering(String offering, String allowedFeatureOfInterestType) {
        notNullOrEmpty(OFFERING, offering);
        notNullOrEmpty(ALLOWED_FEATURE_OF_INTEREST_TYPE, allowedFeatureOfInterestType);
        LOG.trace("Adding AllowedFeatureOfInterestType {} to Offering {}", allowedFeatureOfInterestType, offering);
        this.allowedFeatureOfInterestTypeForOfferings.computeIfAbsent(offering, createSynchronizedSet())
                .add(allowedFeatureOfInterestType);
    }

    @Override
    public void addAllowedFeatureOfInterestTypesForOffering(String offering,
                                                            Collection<String> allowedFeatureOfInterestTypes) {
        notNullOrEmpty(OFFERING, offering);
        noNullValues(ALLOWED_FEATURE_OF_INTEREST_TYPES, allowedFeatureOfInterestTypes);
        LOG.trace("Adding AllowedFeatureOfInterestTypes {} to Offering {}", allowedFeatureOfInterestTypes, offering);
        this.allowedFeatureOfInterestTypeForOfferings.computeIfAbsent(offering, createSynchronizedSet())
                .addAll(allowedFeatureOfInterestTypes);
    }

    @Override
    public void addSupportedLanguage(Locale language) {
        Objects.requireNonNull(language, SUPPORTED_LANGUAGE);
        LOG.trace("Adding Language {}", language);
        this.supportedLanguages.add(language);
    }

    @Override
    public void addSupportedLanguage(Collection<Locale> languages) {
        noNullValues(SUPPORTED_LANGUAGES, languages);
        languages.forEach(this::addSupportedLanguage);
    }

    @Override
    public void clearSupportedLanguage() {
        LOG.trace("Clearing supported languages");
        this.supportedLanguages.clear();
    }

    @Override
    public void removeSupportedLanguage(Locale language) {
        LOG.trace("Removing Language {}", language);
        this.supportedLanguages.remove(language);
    }

    @Override
    public void addFeatureOfInterestIdentifierHumanReadableName(String identifier, String humanReadableName) {
        if (!Strings.isNullOrEmpty(identifier) && !Strings.isNullOrEmpty(humanReadableName)) {
            featureOfInterestIdentifierHumanReadableName.put(identifier, humanReadableName);
        }
    }

    @Override
    public void addObservablePropertyIdentifierHumanReadableName(String identifier, String humanReadableName) {
        if (!Strings.isNullOrEmpty(identifier) && !Strings.isNullOrEmpty(humanReadableName)) {
            observablePropertyIdentifierHumanReadableName.put(identifier, humanReadableName);
        }
    }

    @Override
    public void addProcedureIdentifierHumanReadableName(String identifier, String humanReadableName) {
        if (!Strings.isNullOrEmpty(identifier) && !Strings.isNullOrEmpty(humanReadableName)) {
            procedureIdentifierHumanReadableName.put(identifier, humanReadableName);
        }
    }

    @Override
    public void addOfferingIdentifierHumanReadableName(String identifier, String humanReadableName) {
        if (!Strings.isNullOrEmpty(identifier) && !Strings.isNullOrEmpty(humanReadableName)) {
            offeringIdentifierHumanReadableName.put(identifier, humanReadableName);
        }
    }

    @Override
    public void removeFeatureOfInterestIdentifierForHumanReadableName(String humanReadableName) {
        notNullOrEmpty(FEATURE_OF_INTEREST_NAME, humanReadableName);
        LOG.trace("Removing featuresOfInterest identifier for humanReadableName {}", humanReadableName);
        featureOfInterestIdentifierHumanReadableName.inverse().remove(humanReadableName);
    }

    @Override
    public void removeFeatureOfInterestHumanReadableNameForIdentifier(String identifier) {
        notNullOrEmpty(FEATURE_OF_INTEREST, identifier);
        LOG.trace("Removing featuresOfInterest human readable name for identifier {}", identifier);
        featureOfInterestIdentifierHumanReadableName.remove(identifier);
    }

    @Override
    public void removeObservablePropertyIdentifierForHumanReadableName(String humanReadableName) {
        notNullOrEmpty(OBSERVABLE_PROPERTY_NAME, humanReadableName);
        LOG.trace("Removing featuresOfInterest identifier for humanReadableName {}", humanReadableName);
        observablePropertyIdentifierHumanReadableName.inverse().remove(humanReadableName);
    }

    @Override
    public void removeObservablePropertyHumanReadableNameForIdentifier(String identifier) {
        notNullOrEmpty(OBSERVABLE_PROPERTY, identifier);
        LOG.trace("Removing observableProperty human readable name for identifier {}", identifier);
        observablePropertyIdentifierHumanReadableName.remove(identifier);
    }

    @Override
    public void removeProcedureIdentifierForHumanReadableName(String humanReadableName) {
        notNullOrEmpty(PROCEDURE_NAME, humanReadableName);
        LOG.trace("Removing procedure identifier for humanReadableName {}", humanReadableName);
        procedureIdentifierHumanReadableName.inverse().remove(humanReadableName);
    }

    @Override
    public void removeProcedureHumanReadableNameForIdentifier(String identifier) {
        notNullOrEmpty(PROCEDURE, identifier);
        LOG.trace("Removing procedure human readable name for identifier {}", identifier);
        procedureIdentifierHumanReadableName.remove(identifier);
    }

    @Override
    public void removeOfferingIdentifierForHumanReadableName(String humanReadableName) {
        notNullOrEmpty(OFFERING_NAME, humanReadableName);
        LOG.trace("Removing offering identifier for humanReadableName {}", humanReadableName);
        offeringIdentifierHumanReadableName.inverse().remove(humanReadableName);
    }

    @Override
    public void removeOfferingHumanReadableNameForIdentifier(String identifier) {
        notNullOrEmpty(OFFERING, identifier);
        LOG.trace("Removing offering human readable name for identifier {}", identifier);
        offeringIdentifierHumanReadableName.remove(identifier);
    }

    @Override
    public void clearFeatureOfInterestIdentifierHumanReadableNameMaps() {
        featureOfInterestIdentifierHumanReadableName.clear();
    }

    @Override
    public void clearObservablePropertyIdentifierHumanReadableNameMaps() {
        observablePropertyIdentifierHumanReadableName.clear();
    }

    @Override
    public void clearProcedureIdentifierHumanReadableNameMaps() {
        procedureIdentifierHumanReadableName.clear();
    }

    @Override
    public void clearOfferingIdentifierHumanReadableNameMaps() {
        offeringIdentifierHumanReadableName.clear();
    }

    @Override
    public Set<String> getCompositePhenomenons() {
        return copyOf(this.compositePhenomenons);
    }

    @Override
    public boolean isCompositePhenomenon(String observableProperty) {
        return this.compositePhenomenons.contains(observableProperty);
    }

    @Override
    public Set<String> getCompositePhenomenonsForProcedure(String procedure) {
        return copyOf(this.compositePhenomenonsForProcedure.get(procedure));
    }

    @Override
    public boolean isCompositePhenomenonForProcedure(String procedure,
                                                     String observableProperty) {
        return this.compositePhenomenonsForProcedure.containsKey(procedure) &&
               this.compositePhenomenonsForProcedure.get(procedure).contains(observableProperty);
    }

    @Override
    public Set<String> getCompositePhenomenonsForOffering(String offering) {
        return copyOf(this.compositePhenomenonsForOffering.get(offering));
    }

    @Override
    public boolean isCompositePhenomenonForOffering(String offering, String observableProperty) {
        return this.compositePhenomenonsForOffering.containsKey(offering) &&
               this.compositePhenomenonsForOffering.get(offering).contains(observableProperty);
    }

    @Override
    public Set<String> getObservablePropertiesForCompositePhenomenon(String compositePhenomenon) {
        return copyOf(this.observablePropertiesForCompositePhenomenons.get(compositePhenomenon));
    }

    @Override
    public boolean isObservablePropertyOfCompositePhenomenon(String compositePhenomenon, String observableProperty) {
        return this.observablePropertiesForCompositePhenomenons.containsKey(compositePhenomenon) &&
               this.observablePropertiesForCompositePhenomenons.get(compositePhenomenon).contains(observableProperty);
    }

    @Override
    public Set<String> getCompositePhenomenonForObservableProperty(String observableProperty) {
        return copyOf(this.compositePhenomenonsForObservableProperty.get(observableProperty));
    }

    @Override
    public boolean isCompositePhenomenonComponent(String observableProperty) {
        return this.compositePhenomenonsForObservableProperty.containsKey(observableProperty) &&
               !this.compositePhenomenonsForObservableProperty.get(observableProperty).isEmpty();
    }

    @Override
    public void addCompositePhenomenon(String compositePhenomenon) {
        notNullOrEmpty(COMPOSITE_PHENOMENON, compositePhenomenon);
        LOG.trace("Adding composite phenomenon {}", compositePhenomenon);
        this.compositePhenomenons.add(compositePhenomenon);
    }

    @Override
    public void addCompositePhenomenon(Collection<String> compositePhenomenon) {
        noNullOrEmptyValues(COMPOSITE_PHENOMENON, compositePhenomenon);
        LOG.trace("Adding composite phenomenon {}", compositePhenomenon);
        this.compositePhenomenons.addAll(compositePhenomenon);
    }

    @Override
    public void setCompositePhenomenon(Collection<String> compositePhenomenon) {
        clearCompositePhenomenon();
        addCompositePhenomenon(compositePhenomenon);
    }

    @Override
    public void clearCompositePhenomenon() {
        LOG.trace("Clearing composite phenomenon");
        this.compositePhenomenons.clear();
    }

    @Override
    public void addCompositePhenomenonForProcedure(String procedure,
                                                   String compositePhenomenon) {
        notNullOrEmpty(PROCEDURE, procedure);
        notNullOrEmpty(COMPOSITE_PHENOMENON, compositePhenomenon);
        LOG.trace("Adding composite phenomenon {} to procedure {}", compositePhenomenon, procedure);
        this.compositePhenomenonsForProcedure.computeIfAbsent(procedure, createSynchronizedSet())
                .add(compositePhenomenon);
        addCompositePhenomenon(compositePhenomenon);
    }

    @Override
    public void addCompositePhenomenonForProcedure(String procedure,
                                                   Collection<String> compositePhenomenon) {
        notNullOrEmpty(PROCEDURE, procedure);
        noNullOrEmptyValues(COMPOSITE_PHENOMENON, compositePhenomenon);
        LOG.trace("Adding composite phenomenon {} to procedure {}", compositePhenomenon, procedure);
        this.compositePhenomenonsForProcedure.computeIfAbsent(procedure, createSynchronizedSet())
                .addAll(compositePhenomenon);
        addCompositePhenomenon(compositePhenomenon);
    }

    @Override
    public void setCompositePhenomenonForProcedure(String procedure,
                                                   Collection<String> compositePhenomenon) {
        this.clearCompositePhenomenonForProcedure(procedure);
        this.addCompositePhenomenonForProcedure(procedure, compositePhenomenon);
    }

    @Override
    public void clearCompositePhenomenonForProcedure(String procedure) {
        LOG.trace("Clearing composite phenomenons for procedure {}", procedure);
        this.compositePhenomenonsForProcedure.remove(procedure);
    }

    @Override
    public void clearCompositePhenomenonForProcedures() {
        LOG.trace("Clearing composite phenomenons for procedures");
        this.compositePhenomenonsForProcedure.clear();
    }

    @Override
    public void addCompositePhenomenonForOffering(String offering, String compositePhenomenon) {
        notNullOrEmpty(OFFERING, offering);
        notNullOrEmpty(COMPOSITE_PHENOMENON, compositePhenomenon);
        LOG.trace("Adding composite phenomenon {} to offering {}", offering);
        this.compositePhenomenonsForOffering.computeIfAbsent(offering, createSynchronizedSet()).add(compositePhenomenon);
        addCompositePhenomenon(compositePhenomenon);
    }

    @Override
    public void addCompositePhenomenonForOffering(String offering, Collection<String> compositePhenomenon) {
        notNullOrEmpty(OFFERING, offering);
        noNullOrEmptyValues(COMPOSITE_PHENOMENON, compositePhenomenon);
        LOG.trace("Adding composite phenomenon {} to offering {}", offering);
        this.compositePhenomenonsForOffering.computeIfAbsent(offering, createSynchronizedSet())
                .addAll(compositePhenomenon);
        addCompositePhenomenon(compositePhenomenon);
    }

    @Override
    public void setCompositePhenomenonForOffering(String offering,
                                                  Collection<String> compositePhenomenon) {
        clearCompositePhenomenonForOffering(offering);
        addCompositePhenomenonForProcedure(offering, compositePhenomenon);
    }

    @Override
    public void clearCompositePhenomenonForOffering(String offering) {
        LOG.trace("Clearing composite phenomenons for offering {}", offering);
        this.compositePhenomenonsForOffering.remove(offering);
    }

    @Override
    public void clearCompositePhenomenonForOfferings() {
        LOG.trace("Clearing composite phenomenons for offerings");
        this.compositePhenomenonsForOffering.clear();
    }

    @Override
    public void addCompositePhenomenonForObservableProperty(String observableProperty,
                                                            String compositePhenomenon) {
        notNullOrEmpty(COMPOSITE_PHENOMENON, compositePhenomenon);
        notNullOrEmpty(OBSERVABLE_PROPERTY, observableProperty);
        LOG
                .trace("Adding composite phenomenon {} to to observable property {}", compositePhenomenon, observableProperty);
        this.compositePhenomenonsForObservableProperty.computeIfAbsent(observableProperty, createSynchronizedSet())
                .add(compositePhenomenon);
        addCompositePhenomenon(compositePhenomenon);
    }

    @Override
    public void addObservablePropertyForCompositePhenomenon(String compositePhenomenon,
                                                            String observableProperty) {
        notNullOrEmpty(COMPOSITE_PHENOMENON, compositePhenomenon);
        notNullOrEmpty(OBSERVABLE_PROPERTY, observableProperty);
        LOG.trace("Adding observable property {} to composite phenomenon {}", observableProperty, compositePhenomenon);
        this.observablePropertiesForCompositePhenomenons.computeIfAbsent(compositePhenomenon, createSynchronizedSet())
                .add(observableProperty);
        addCompositePhenomenon(compositePhenomenon);
    }

    @Override
    public void addObservablePropertiesForCompositePhenomenon(String compositePhenomenon,
                                                              Collection<String> observableProperty) {
        notNullOrEmpty(COMPOSITE_PHENOMENON, compositePhenomenon);
        noNullOrEmptyValues(OBSERVABLE_PROPERTY, observableProperty);
        LOG.trace("Adding observable properties {} to composite phenomenon {}", observableProperty, compositePhenomenon);
        this.observablePropertiesForCompositePhenomenons.computeIfAbsent(compositePhenomenon, createSynchronizedSet())
                .addAll(observableProperty);
        addCompositePhenomenon(compositePhenomenon);
    }

    @Override
    public void setObservablePropertiesForCompositePhenomenon(String compositePhenomenon,
                                                              Collection<String> observableProperty) {
        clearObservablePropertiesForCompositePhenomenon(compositePhenomenon);
        addObservablePropertiesForCompositePhenomenon(compositePhenomenon, observableProperty);
    }

    @Override
    public void clearObservablePropertiesForCompositePhenomenon(String compositePhenomenon) {
        LOG.trace("Clearing observable properties for composite phenomenon {}", compositePhenomenon);
        this.observablePropertiesForCompositePhenomenons.remove(compositePhenomenon);
    }

    @Override
    public void clearObservablePropertiesForCompositePhenomenon() {
        LOG.trace("Clearing observable properties for composite phenomenon");
        this.observablePropertiesForCompositePhenomenons.clear();
    }

    @Override
    public void clearCompositePhenomenonsForObservableProperty() {
        LOG.trace("Clearing composite phenomenon for observable properties");
        this.compositePhenomenonsForObservableProperty.clear();
    }

    @Override
    public void clearCompositePhenomenonsForObservableProperty(String observableProperty) {
        LOG.trace("Clearing composite phenomenon for observable property {}", observableProperty);
        this.compositePhenomenonsForObservableProperty.remove(observableProperty);
    }

    @Override
    public Set<String> getRequestableProcedureDescriptionFormat() {
        return copyOf(this.requestableProcedureDescriptionFormats);
    }

    @Override
    public void setRequestableProcedureDescriptionFormat(Collection<String> formats) {
        LOG.trace("Adding requestable procedureDescriptionFormat");
        this.requestableProcedureDescriptionFormats.addAll(formats);
    }

    @Override
    public boolean hasRequestableProcedureDescriptionFormat(String format) {
        return getRequestableProcedureDescriptionFormat().contains(format);
    }

    @Override
    public Set<String> getOfferingsForProcedures(Set<String> procedures) {
        return Optional.ofNullable(procedures)
                .orElseGet(Collections::emptySet)
                .stream()
                .map(this::getOfferingsForProcedure)
                .flatMap(Set::stream)
                .collect(toSet());
    }

    @SuppressWarnings("unchecked")
    @Override
    public Set<String> getTransactionalObservationProcedures() {
        return CollectionHelper.union(
                CollectionHelper.union(copyOf(hiddenChildProceduresForOfferings.values())),
                CollectionHelper.union(copyOf(proceduresForOfferings.values())));
    }

    @Override
    public boolean hasTransactionalObservationProcedure(String procedureID) {
        return getTransactionalObservationProcedures().contains(procedureID);
    }

    @Override
    public Set<String> getQueryableProcedures() {
        Set<String> procedures = getProcedures();
        // allowQueryingForInstancesOnly
        if (isAllowQueryingForInstancesOnly()) {
            procedures = CollectionHelper
                    .conjunctCollectionsToSet(procedures, getTypeInstanceProcedure(TypeInstance.INSTANCE));
        }
        // showOnlyAggregatedProcedures
        if (isShowOnlyAggregatedProcedures()) {
            procedures = CollectionHelper
                    .conjunctCollectionsToSet(procedures, getComponentAggregationProcedure(ComponentAggregation.AGGREGATION));

        }
        return procedures;
    }

    @Override
    public boolean hasQueryableProcedure(String procedureID) {
        return getQueryableProcedures().contains(procedureID);
    }

    @Override
    public Set<String> getTypeInstanceProcedure(TypeInstance typeInstance) {
        return copyOf(typeInstanceProcedures.get(typeInstance));
    }

    @Override
    public Set<String> getComponentAggregationProcedure(ComponentAggregation componentAggregation) {
        return copyOf(componentAggregationProcedures.get(componentAggregation));
    }

    @Override
    public Set<String> getInstancesForProcedure(String identifier) {
        return copyOf(typeOfProceduresMap.get(identifier));
    }

    @Override
    public boolean hasInstancesForProcedure(String identifier) {
        return typeOfProceduresMap.containsKey(identifier);
    }

    @Override
    public void addTypeInstanceProcedure(TypeInstance typeInstance, String identifier) {
        notNullOrEmpty(TYPE_PROCEDURE, identifier);
        logAdding(TYPE_PROCEDURE, identifier);
        if (typeInstanceProcedures.containsKey(typeInstance)) {
            typeInstanceProcedures.get(typeInstance).add(identifier);
        } else {
            typeInstanceProcedures.put(typeInstance, Sets.newHashSet(identifier));
        }
    }

    @Override
    public void removeTypeInstanceProcedure(String identifier) {
        notNullOrEmpty(TYPE_PROCEDURE, identifier);
        logRemoving(TYPE_PROCEDURE, identifier);
        removeValue(typeInstanceProcedures, identifier);
    }

    @Override
    public void clearTypeInstanceProcedure() {
        logClearing(TYPE_PROCEDURE);
        typeInstanceProcedures.clear();
    }

    @Override
    public void addComponentAggregationProcedure(ComponentAggregation componentAggregation, String identifier) {
        notNullOrEmpty(AGGREGATED_PROCEDURE, identifier);
        logAdding(AGGREGATED_PROCEDURE, identifier);
        if (componentAggregationProcedures.containsKey(componentAggregation)) {
            componentAggregationProcedures.get(componentAggregation).add(identifier);
        } else {
            componentAggregationProcedures.put(componentAggregation, Sets.newHashSet(identifier));
        }
    }

    @Override
    public void removeComponentAggregationProcedure(String identifier) {
        notNullOrEmpty(AGGREGATED_PROCEDURE, identifier);
        logRemoving(AGGREGATED_PROCEDURE, identifier);
        removeValue(componentAggregationProcedures, identifier);
    }

    @Override
    public void clearComponentAggregationProcedure() {
        logClearing(AGGREGATED_PROCEDURE);
        componentAggregationProcedures.clear();
    }

    @Override
    public void addTypeOfProcedure(String type, String instance) {
        notNullOrEmpty(TYPE_PROCEDURE, type);
        notNullOrEmpty(PROCEDURE_INSTANCE, instance);
        LOG.trace("Adding instance '{}' to type '{}'", instance, type);
        if (hasInstancesForProcedure(type)) {
            typeOfProceduresMap.get(type).add(instance);
        } else {
            typeOfProceduresMap.put(type, Sets.newHashSet(instance));
        }
    }

    @Override
    public void addTypeOfProcedure(String type, Set<String> instances) {
        notNullOrEmpty(TYPE_PROCEDURE, type);
        noNullValues(PROCEDURE_INSTANCES, instances);
        LOG.trace("Adding instances {} to type '{}'", instances, type);
        if (hasInstancesForProcedure(type)) {
            typeOfProceduresMap.get(type).addAll(instances);
        } else {
            typeOfProceduresMap.put(type, instances);
        }
    }

    @Override
    public void removeTypeOfProcedure(String type) {
        notNullOrEmpty(TYPE_PROCEDURE, type);
        LOG.trace("Removing type '{}'", type);
        if (hasInstancesForProcedure(type)) {
            typeOfProceduresMap.remove(type);
        }
        // check for values
        removeValue(typeOfProceduresMap, type);
    }

    @Override
    public void removeTypeOfProcedure(String type, String instance) {
        notNullOrEmpty(TYPE_PROCEDURE, type);
        notNullOrEmpty(PROCEDURE_INSTANCE, instance);
        logRemoving(type, instance);
        if (hasInstancesForProcedure(type)) {
            typeOfProceduresMap.get(type).remove(instance);
        }
    }

    @Override
    public void clearTypeOfProcedure() {
        logClearing("Clearing type instance procedure map");
        typeOfProceduresMap.clear();
    }

    /**
     * Logs to trace: "Adding 'value' to 'type'".
     *
     * @param type  Add to
     * @param value Value to add
     */
    protected void logAdding(String type, String value) {
        LOG.trace("Adding '{}' to '{}'", value, type);
    }

    /**
     * Logs to trace: "Removing 'value' from 'type'".
     *
     * @param type  Remove from
     * @param value Value to remove
     */
    protected void logRemoving(String type, String value) {
        LOG.trace("Removing '{}' from '{}'", value, type);
    }

    /**
     * Logs to trace: "Clearing 'type'
     *
     * @param type Type to clear
     */
    protected void logClearing(String type) {
        LOG.trace("Clearing '{}'", type);
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 61 * hash + Objects.hashCode(this.maxPhenomenonTimeForOfferings);
        hash = 61 * hash + Objects.hashCode(this.minPhenomenonTimeForOfferings);
        hash = 61 * hash + Objects.hashCode(this.maxResultTimeForOfferings);
        hash = 61 * hash + Objects.hashCode(this.minResultTimeForOfferings);
        hash = 61 * hash + Objects.hashCode(this.maxPhenomenonTimeForProcedures);
        hash = 61 * hash + Objects.hashCode(this.minPhenomenonTimeForProcedures);
        hash = 61 * hash + Objects.hashCode(this.allowedObservationTypeForOfferings);
        hash = 61 * hash + Objects.hashCode(this.allowedFeatureOfInterestTypeForOfferings);
        hash = 61 * hash + Objects.hashCode(this.childFeaturesForFeatureOfInterest);
        hash = 61 * hash + Objects.hashCode(this.childProceduresForProcedures);
        hash = 61 * hash + Objects.hashCode(this.compositePhenomenonsForProcedure);
        hash = 61 * hash + Objects.hashCode(this.compositePhenomenonsForOffering);
        hash = 61 * hash + Objects.hashCode(this.compositePhenomenonsForObservableProperty);
        hash = 61 * hash + Objects.hashCode(this.featuresOfInterestForOfferings);
        hash = 61 * hash + Objects.hashCode(this.featuresOfInterestForResultTemplates);
        hash = 61 * hash + Objects.hashCode(this.observablePropertiesForCompositePhenomenons);
        hash = 61 * hash + Objects.hashCode(this.observablePropertiesForOfferings);
        hash = 61 * hash + Objects.hashCode(this.observablePropertiesForProcedures);
        hash = 61 * hash + Objects.hashCode(this.observationTypesForOfferings);
        hash = 61 * hash + Objects.hashCode(this.featureOfInterestTypesForOfferings);
        hash = 61 * hash + Objects.hashCode(this.observedPropertiesForResultTemplates);
        hash = 61 * hash + Objects.hashCode(this.offeringsForObservableProperties);
        hash = 61 * hash + Objects.hashCode(this.offeringsForProcedures);
        hash = 61 * hash + Objects.hashCode(this.parentFeaturesForFeaturesOfInterest);
        hash = 61 * hash + Objects.hashCode(this.parentProceduresForProcedures);
        hash = 61 * hash + Objects.hashCode(this.proceduresForFeaturesOfInterest);
        hash = 61 * hash + Objects.hashCode(this.proceduresForObservableProperties);
        hash = 61 * hash + Objects.hashCode(this.proceduresForOfferings);
        hash = 61 * hash + Objects.hashCode(this.hiddenChildProceduresForOfferings);
        hash = 61 * hash + Objects.hashCode(this.relatedFeaturesForOfferings);
        hash = 61 * hash + Objects.hashCode(this.resultTemplatesForOfferings);
        hash = 61 * hash + Objects.hashCode(this.rolesForRelatedFeatures);
        hash = 61 * hash + Objects.hashCode(this.envelopeForOfferings);
        hash = 61 * hash + Objects.hashCode(this.nameForOfferings);
        hash = 61 * hash + Objects.hashCode(this.i18nNameForOfferings);
        hash = 61 * hash + Objects.hashCode(this.i18nDescriptionForOfferings);
        hash = 61 * hash + Objects.hashCode(this.epsgCodes);
        hash = 61 * hash + Objects.hashCode(this.featuresOfInterest);
        hash = 61 * hash + Objects.hashCode(this.procedures);
        hash = 61 * hash + Objects.hashCode(this.resultTemplates);
        hash = 61 * hash + Objects.hashCode(this.offerings);
        hash = 61 * hash + Objects.hashCode(this.compositePhenomenons);
        hash = 61 * hash + Objects.hashCode(this.globalPhenomenonTimeEnvelope);
        hash = 61 * hash + Objects.hashCode(this.globalResultTimeEnvelope);
        hash = 61 * hash + Objects.hashCode(this.spatialFilteringProfileEnvelopeForOfferings);
        hash = 61 * hash + Objects.hashCode(this.supportedLanguages);
        hash = 61 * hash + Objects.hashCode(this.requestableProcedureDescriptionFormats);
        hash = 61 * hash + Objects.hashCode(this.featureOfInterestIdentifierHumanReadableName);
        hash = 61 * hash + Objects.hashCode(this.observablePropertyIdentifierHumanReadableName);
        hash = 61 * hash + Objects.hashCode(this.procedureIdentifierHumanReadableName);
        hash = 61 * hash + Objects.hashCode(this.offeringIdentifierHumanReadableName);
        hash = 61 * hash + Objects.hashCode(this.typeInstanceProcedures);
        hash = 61 * hash + Objects.hashCode(this.componentAggregationProcedures);
        hash = 61 * hash + Objects.hashCode(this.typeOfProceduresMap);
        hash = 61 * hash + this.defaultEpsgCode;
        hash = 61 * hash + Objects.hashCode(this.globalEnvelope);
        hash = 61 * hash + Objects.hashCode(this.updateTime);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final InMemoryCacheImpl other = (InMemoryCacheImpl) obj;
        if (this.defaultEpsgCode != other.defaultEpsgCode) {
            return false;
        }
        if (!Objects.equals(this.maxPhenomenonTimeForOfferings, other.maxPhenomenonTimeForOfferings)) {
            return false;
        }
        if (!Objects.equals(this.minPhenomenonTimeForOfferings, other.minPhenomenonTimeForOfferings)) {
            return false;
        }
        if (!Objects.equals(this.maxResultTimeForOfferings, other.maxResultTimeForOfferings)) {
            return false;
        }
        if (!Objects.equals(this.minResultTimeForOfferings, other.minResultTimeForOfferings)) {
            return false;
        }
        if (!Objects.equals(this.maxPhenomenonTimeForProcedures, other.maxPhenomenonTimeForProcedures)) {
            return false;
        }
        if (!Objects.equals(this.minPhenomenonTimeForProcedures, other.minPhenomenonTimeForProcedures)) {
            return false;
        }
        if (!Objects.equals(this.allowedObservationTypeForOfferings, other.allowedObservationTypeForOfferings)) {
            return false;
        }
        if (!Objects
                .equals(this.allowedFeatureOfInterestTypeForOfferings, other.allowedFeatureOfInterestTypeForOfferings)) {
            return false;
        }
        if (!Objects.equals(this.childFeaturesForFeatureOfInterest, other.childFeaturesForFeatureOfInterest)) {
            return false;
        }
        if (!Objects.equals(this.childProceduresForProcedures, other.childProceduresForProcedures)) {
            return false;
        }
        if (!Objects.equals(this.compositePhenomenonsForProcedure, other.compositePhenomenonsForProcedure)) {
            return false;
        }
        if (!Objects.equals(this.compositePhenomenonsForOffering, other.compositePhenomenonsForOffering)) {
            return false;
        }
        if (!Objects
                .equals(this.compositePhenomenonsForObservableProperty, other.compositePhenomenonsForObservableProperty)) {
            return false;
        }
        if (!Objects.equals(this.featuresOfInterestForOfferings, other.featuresOfInterestForOfferings)) {
            return false;
        }
        if (!Objects.equals(this.featuresOfInterestForResultTemplates, other.featuresOfInterestForResultTemplates)) {
            return false;
        }
        if (!Objects
                .equals(this.observablePropertiesForCompositePhenomenons, other.observablePropertiesForCompositePhenomenons)) {
            return false;
        }
        if (!Objects.equals(this.observablePropertiesForOfferings, other.observablePropertiesForOfferings)) {
            return false;
        }
        if (!Objects.equals(this.observablePropertiesForProcedures, other.observablePropertiesForProcedures)) {
            return false;
        }
        if (!Objects.equals(this.observationTypesForOfferings, other.observationTypesForOfferings)) {
            return false;
        }
        if (!Objects.equals(this.featureOfInterestTypesForOfferings, other.featureOfInterestTypesForOfferings)) {
            return false;
        }
        if (!Objects.equals(this.observedPropertiesForResultTemplates, other.observedPropertiesForResultTemplates)) {
            return false;
        }
        if (!Objects.equals(this.offeringsForObservableProperties, other.offeringsForObservableProperties)) {
            return false;
        }
        if (!Objects.equals(this.offeringsForProcedures, other.offeringsForProcedures)) {
            return false;
        }
        if (!Objects.equals(this.parentFeaturesForFeaturesOfInterest, other.parentFeaturesForFeaturesOfInterest)) {
            return false;
        }
        if (!Objects.equals(this.parentProceduresForProcedures, other.parentProceduresForProcedures)) {
            return false;
        }
        if (!Objects.equals(this.proceduresForFeaturesOfInterest, other.proceduresForFeaturesOfInterest)) {
            return false;
        }
        if (!Objects.equals(this.proceduresForObservableProperties, other.proceduresForObservableProperties)) {
            return false;
        }
        if (!Objects.equals(this.proceduresForOfferings, other.proceduresForOfferings)) {
            return false;
        }
        if (!Objects.equals(this.hiddenChildProceduresForOfferings, other.hiddenChildProceduresForOfferings)) {
            return false;
        }
        if (!Objects.equals(this.relatedFeaturesForOfferings, other.relatedFeaturesForOfferings)) {
            return false;
        }
        if (!Objects.equals(this.resultTemplatesForOfferings, other.resultTemplatesForOfferings)) {
            return false;
        }
        if (!Objects.equals(this.rolesForRelatedFeatures, other.rolesForRelatedFeatures)) {
            return false;
        }
        if (!Objects.equals(this.envelopeForOfferings, other.envelopeForOfferings)) {
            return false;
        }
        if (!Objects.equals(this.nameForOfferings, other.nameForOfferings)) {
            return false;
        }
        if (!Objects.equals(this.i18nNameForOfferings, other.i18nNameForOfferings)) {
            return false;
        }
        if (!Objects.equals(this.i18nDescriptionForOfferings, other.i18nDescriptionForOfferings)) {
            return false;
        }
        if (!Objects.equals(this.epsgCodes, other.epsgCodes)) {
            return false;
        }
        if (!Objects.equals(this.featuresOfInterest, other.featuresOfInterest)) {
            return false;
        }
        if (!Objects.equals(this.procedures, other.procedures)) {
            return false;
        }
        if (!Objects.equals(this.resultTemplates, other.resultTemplates)) {
            return false;
        }
        if (!Objects.equals(this.offerings, other.offerings)) {
            return false;
        }
        if (!Objects.equals(this.compositePhenomenons, other.compositePhenomenons)) {
            return false;
        }
        if (!Objects.equals(this.globalPhenomenonTimeEnvelope, other.globalPhenomenonTimeEnvelope)) {
            return false;
        }
        if (!Objects.equals(this.globalResultTimeEnvelope, other.globalResultTimeEnvelope)) {
            return false;
        }
        if (!Objects
                .equals(this.spatialFilteringProfileEnvelopeForOfferings, other.spatialFilteringProfileEnvelopeForOfferings)) {
            return false;
        }
        if (!Objects.equals(this.supportedLanguages, other.supportedLanguages)) {
            return false;
        }
        if (!Objects.equals(this.requestableProcedureDescriptionFormats, other.requestableProcedureDescriptionFormats)) {
            return false;
        }
        if (!Objects
                .equals(this.featureOfInterestIdentifierHumanReadableName, other.featureOfInterestIdentifierHumanReadableName)) {
            return false;
        }
        if (!Objects
                .equals(this.observablePropertyIdentifierHumanReadableName, other.observablePropertyIdentifierHumanReadableName)) {
            return false;
        }
        if (!Objects.equals(this.procedureIdentifierHumanReadableName, other.procedureIdentifierHumanReadableName)) {
            return false;
        }
        if (!Objects.equals(this.offeringIdentifierHumanReadableName, other.offeringIdentifierHumanReadableName)) {
            return false;
        }
        if (!Objects.equals(this.typeInstanceProcedures, other.typeInstanceProcedures)) {
            return false;
        }
        if (!Objects.equals(this.componentAggregationProcedures, other.componentAggregationProcedures)) {
            return false;
        }
        if (!Objects.equals(this.typeOfProceduresMap, other.typeOfProceduresMap)) {
            return false;
        }
        if (!Objects.equals(this.globalEnvelope, other.globalEnvelope)) {
            return false;
        }
        if (!Objects.equals(this.updateTime, other.updateTime)) {
            return false;
        }
        return true;
    }

    private ProcedureRequestSettingProvider getProcedureRequestSettingProvider() {
        return ProcedureRequestSettingProvider.getInstance();
    }

    @VisibleForTesting
    boolean isAllowQueryingForInstancesOnly() {
        return getProcedureRequestSettingProvider().isAllowQueryingForInstancesOnly();
    }

    @VisibleForTesting
    boolean isShowOnlyAggregatedProcedures() {
        return getProcedureRequestSettingProvider().isShowOnlyAggregatedProcedures();
    }

    private static <X, T> Function<X, Set<T>> createSynchronizedSet() {
        return Suppliers.<X, Set<T>>asFunction(HashSet<T>::new).andThen(Collections::synchronizedSet);
    }

}
