/**
 * Copyright (C) 2012-2020 52°North Initiative for Geospatial Open Source
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
import static org.n52.sos.util.SosHelper.getHierarchy;

import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.n52.sos.i18n.LocalizedString;
import org.n52.sos.i18n.MultilingualString;
import org.n52.sos.ogc.gml.time.Time;
import org.n52.sos.ogc.gml.time.TimePeriod;
import org.n52.sos.ogc.sos.SosEnvelope;
import org.n52.sos.request.ProcedureRequestSettings;
import org.n52.sos.util.CollectionHelper;
import org.n52.sos.util.Constants;
import org.n52.sos.util.DateTimeHelper;
import org.n52.sos.util.SetMultiMap;
import org.n52.sos.util.StringHelper;

import com.google.common.base.Objects;
import com.google.common.collect.Sets;
import com.vividsolutions.jts.geom.Envelope;


public class InMemoryCacheImpl extends AbstractStaticContentCache implements WritableContentCache, CacheConstants {
    private static final Logger LOG = LoggerFactory.getLogger(InMemoryCacheImpl.class);
    private static final long serialVersionUID = 5229487811485834059L;
    private DateTime updateTime;
    private final Map<String, DateTime> maxPhenomenonTimeForOfferings = newSynchronizedMap();
    private final Map<String, DateTime> minPhenomenonTimeForOfferings = newSynchronizedMap();
    private final Map<String, DateTime> maxResultTimeForOfferings = newSynchronizedMap();
    private final Map<String, DateTime> minResultTimeForOfferings = newSynchronizedMap();
    private final Map<String, DateTime> maxPhenomenonTimeForProcedures = newSynchronizedMap();
    private final Map<String, DateTime> minPhenomenonTimeForProcedures = newSynchronizedMap();
    private final SetMultiMap<String, String> allowedObservationTypeForOfferings = newSynchronizedSetMultiMap();
    private final SetMultiMap<String, String> allowedFeatureOfInterestTypeForOfferings = newSynchronizedSetMultiMap();
    private final SetMultiMap<String, String> childFeaturesForFeatureOfInterest = newSynchronizedSetMultiMap();
    private final SetMultiMap<String, String> childProceduresForProcedures = newSynchronizedSetMultiMap();
    private final SetMultiMap<String, String> childOfferingsForOfferings = newSynchronizedSetMultiMap();
    private final SetMultiMap<String, String> featuresOfInterestForOfferings = newSynchronizedSetMultiMap();
    private final SetMultiMap<String, String> offeringsForFeaturesOfInterest = newSynchronizedSetMultiMap();
    private final SetMultiMap<String, String> featuresOfInterestForResultTemplates = newSynchronizedSetMultiMap();
    private final SetMultiMap<String, String> observablePropertiesForOfferings = newSynchronizedSetMultiMap();
    private final SetMultiMap<String, String> observablePropertiesForProcedures = newSynchronizedSetMultiMap();
    private final SetMultiMap<String, String> observationTypesForOfferings = newSynchronizedSetMultiMap();
    private final SetMultiMap<String, String> featureOfInterestTypesForOfferings = newSynchronizedSetMultiMap();
    private final SetMultiMap<String, String> observedPropertiesForResultTemplates = newSynchronizedSetMultiMap();
    private final SetMultiMap<String, String> offeringsForObservableProperties = newSynchronizedSetMultiMap();
    private final SetMultiMap<String, String> offeringsForProcedures = newSynchronizedSetMultiMap();
    private final SetMultiMap<String, String> parentFeaturesForFeaturesOfInterest = newSynchronizedSetMultiMap();
    private final SetMultiMap<String, String> parentProceduresForProcedures = newSynchronizedSetMultiMap();
    private final SetMultiMap<String, String> parentOfferingsForOfferings = newSynchronizedSetMultiMap();
    private final SetMultiMap<String, String> proceduresForFeaturesOfInterest = newSynchronizedSetMultiMap();
    private final SetMultiMap<String, String> proceduresForObservableProperties = newSynchronizedSetMultiMap();
    private final SetMultiMap<String, String> proceduresForOfferings = newSynchronizedSetMultiMap();
    private final SetMultiMap<String, String> hiddenChildProceduresForOfferings = newSynchronizedSetMultiMap();
    private final SetMultiMap<String, String> relatedFeaturesForOfferings = newSynchronizedSetMultiMap();
    private final SetMultiMap<String, String> resultTemplatesForOfferings = newSynchronizedSetMultiMap();
    private final SetMultiMap<String, String> rolesForRelatedFeatures = newSynchronizedSetMultiMap();
    private final Map<String, SosEnvelope> envelopeForOfferings = newSynchronizedMap();
    private final Map<String, String> nameForOfferings = newSynchronizedMap();
    private final Map<String, MultilingualString> i18nNameForOfferings = newSynchronizedMap();
    private final Map<String, MultilingualString> i18nDescriptionForOfferings = newSynchronizedMap();
    private final Set<Integer> epsgCodes = newSynchronizedSet();
    private final Set<String> featuresOfInterest = newSynchronizedSet();
    private final Set<String> procedures = newSynchronizedSet();
    private final Set<String> resultTemplates = newSynchronizedSet();
    private final Set<String> offerings = newSynchronizedSet();
    private final TimePeriod globalPhenomenonTimeEnvelope = new TimePeriod();
    private final TimePeriod globalResultTimeEnvelope = new TimePeriod();
    private final Map<String, SosEnvelope> spatialFilteringProfileEnvelopeForOfferings = newSynchronizedMap();
    private final Set<Locale> supportedLanguages = newSynchronizedSet();
    private final Map<String, String> featureOfInterestIdentifierForHumanReadableName = newSynchronizedMap();
    private final Map<String, String> featureOfInterestHumanReadableNameForIdentifier = newSynchronizedMap();
    private final Map<String, String> observablePropertyIdentifierForHumanReadableName = newSynchronizedMap();
    private final Map<String, String> observablePropertyHumanReadableNameForIdentifier = newSynchronizedMap();
    private final Map<String, String> procedureIdentifierForHumanReadableName = newSynchronizedMap();
    private final Map<String, String> procedureHumanReadableNameForIdentifier = newSynchronizedMap();
    private final Map<String, String> offeringIdentifierForHumanReadableName = newSynchronizedMap();
    private final Map<String, String> offeringHumanReadableNameForIdentifier = newSynchronizedMap();
    private final Set<String> compositePhenomenons =  newSynchronizedSet();
    private final SetMultiMap<String,String> compositePhenomenonsForProcedure = newSynchronizedSetMultiMap();
    private final SetMultiMap<String,String> compositePhenomenonsForOffering = newSynchronizedSetMultiMap();
    private final SetMultiMap<String,String> observablePropertiesForCompositePhenomenon = newSynchronizedSetMultiMap();
    private final SetMultiMap<String,String> compositePhenomenonForObservableProperty = newSynchronizedSetMultiMap();
    private Set<String> requestableProcedureDescriptionFormats = newSynchronizedSet();
    private int defaultEpsgCode = Constants.EPSG_WGS84;
    private SosEnvelope globalEnvelope = new SosEnvelope(null, defaultEpsgCode);
    private Map<TypeInstance, Set<String>> typeInstanceProcedures = newSynchronizedMap();
    private Map<ComponentAggregation, Set<String>> componentAggregationProcedures = newSynchronizedMap();
    private Map<String, Set<String>> typeOfProcedures = newSynchronizedMap();
    private final SetMultiMap<String,String> procedureProcedureDescriptionFormats = newSynchronizedSetMultiMap();
    private Set<String> publishedFeatureOfInterest = newSynchronizedSet();
    private Set<String> publishedProcedure= newSynchronizedSet();
    private Set<String> publishedOffering = newSynchronizedSet();
    private Set<String> publishedObservableProperty = newSynchronizedSet();
    
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
        return Objects.hashCode(updateTime,
                                defaultEpsgCode,
                                maxPhenomenonTimeForOfferings,
                                minPhenomenonTimeForOfferings,
                                maxResultTimeForOfferings,
                                minResultTimeForOfferings,
                                maxPhenomenonTimeForProcedures,
                                minPhenomenonTimeForProcedures,
                                allowedObservationTypeForOfferings,
                                childFeaturesForFeatureOfInterest,
                                childProceduresForProcedures,
                                childOfferingsForOfferings,
                                featuresOfInterestForOfferings,
                                offeringsForFeaturesOfInterest,
                                featuresOfInterestForResultTemplates,
                                observablePropertiesForOfferings,
                                observablePropertiesForProcedures,
                                observationTypesForOfferings,
                                observedPropertiesForResultTemplates,
                                offeringsForObservableProperties,
                                offeringsForProcedures,
                                parentFeaturesForFeaturesOfInterest,
                                parentProceduresForProcedures,
                                parentOfferingsForOfferings,
                                proceduresForFeaturesOfInterest,
                                proceduresForObservableProperties,
                                proceduresForOfferings,
                                hiddenChildProceduresForOfferings,
                                relatedFeaturesForOfferings,
                                resultTemplatesForOfferings,
                                rolesForRelatedFeatures,
                                envelopeForOfferings,
                                nameForOfferings,
                                i18nNameForOfferings,
                                i18nDescriptionForOfferings,
                                epsgCodes,
                                featuresOfInterest,
                                procedures,
                                resultTemplates,
                                offerings,
                                globalEnvelope,
                                globalResultTimeEnvelope,
                                globalPhenomenonTimeEnvelope,
                                supportedLanguages,
                                featureOfInterestHumanReadableNameForIdentifier,
                                featureOfInterestIdentifierForHumanReadableName,
                                observablePropertyHumanReadableNameForIdentifier,
                                observablePropertyIdentifierForHumanReadableName,
                                procedureHumanReadableNameForIdentifier,
                                procedureIdentifierForHumanReadableName,
                                offeringHumanReadableNameForIdentifier,
                                offeringIdentifierForHumanReadableName,
                                compositePhenomenons,
                                compositePhenomenonsForProcedure,
                                compositePhenomenonsForOffering,
                                observablePropertiesForCompositePhenomenon,
                                compositePhenomenonForObservableProperty,
                                typeInstanceProcedures,
                                componentAggregationProcedures,
                                typeOfProcedures,
                                procedureProcedureDescriptionFormats);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof InMemoryCacheImpl) {
            final InMemoryCacheImpl other = (InMemoryCacheImpl) obj;
            return Objects.equal(this.updateTime, other.getLastUpdateTime())
                    && Objects.equal(this.defaultEpsgCode, other.getDefaultEPSGCode())
                    && Objects.equal(this.maxPhenomenonTimeForOfferings, other.maxPhenomenonTimeForOfferings)
                    && Objects.equal(this.minPhenomenonTimeForOfferings, other.minPhenomenonTimeForOfferings)
                    && Objects.equal(this.maxResultTimeForOfferings, other.maxResultTimeForOfferings)
                    && Objects.equal(this.minResultTimeForOfferings, other.minResultTimeForOfferings)
                    && Objects.equal(this.maxPhenomenonTimeForProcedures, other.maxPhenomenonTimeForProcedures)
                    && Objects.equal(this.minPhenomenonTimeForProcedures, other.minPhenomenonTimeForProcedures)
                    && Objects.equal(this.allowedObservationTypeForOfferings, other.allowedObservationTypeForOfferings)
                    && Objects.equal(this.childFeaturesForFeatureOfInterest, other.childFeaturesForFeatureOfInterest)
                    && Objects.equal(this.childProceduresForProcedures, other.childProceduresForProcedures)
                    && Objects.equal(this.childOfferingsForOfferings, other.childOfferingsForOfferings)
                    && Objects.equal(this.featuresOfInterestForOfferings, other.featuresOfInterestForOfferings)
                    && Objects.equal(this.offeringsForFeaturesOfInterest, other.offeringsForFeaturesOfInterest)
                    && Objects.equal(this.featuresOfInterestForResultTemplates, other.featuresOfInterestForResultTemplates)
                    && Objects.equal(this.observablePropertiesForOfferings, other.observablePropertiesForOfferings)
                    && Objects.equal(this.observablePropertiesForProcedures, other.observablePropertiesForProcedures)
                    && Objects.equal(this.observedPropertiesForResultTemplates, other.observedPropertiesForResultTemplates)
                    && Objects.equal(this.offeringsForObservableProperties, other.offeringsForObservableProperties)
                    && Objects.equal(this.offeringsForProcedures, other.offeringsForProcedures)
                    && Objects.equal(this.parentFeaturesForFeaturesOfInterest, other.parentFeaturesForFeaturesOfInterest)
                    && Objects.equal(this.parentProceduresForProcedures, other.parentProceduresForProcedures)
                    && Objects.equal(this.parentOfferingsForOfferings, other.parentOfferingsForOfferings)
                    && Objects.equal(this.proceduresForFeaturesOfInterest, other.proceduresForFeaturesOfInterest)
                    && Objects.equal(this.proceduresForObservableProperties, other.proceduresForObservableProperties)
                    && Objects.equal(this.proceduresForOfferings, other.proceduresForOfferings)
                    && Objects.equal(this.hiddenChildProceduresForOfferings, other.hiddenChildProceduresForOfferings)
                    && Objects.equal(this.relatedFeaturesForOfferings, other.relatedFeaturesForOfferings)
                    && Objects.equal(this.resultTemplatesForOfferings, other.resultTemplatesForOfferings)
                    && Objects.equal(this.rolesForRelatedFeatures, other.rolesForRelatedFeatures)
                    && Objects.equal(this.envelopeForOfferings, other.envelopeForOfferings)
                    && Objects.equal(this.nameForOfferings, other.nameForOfferings)
                    && Objects.equal(this.i18nNameForOfferings, other.i18nNameForOfferings)
                    && Objects.equal(this.i18nDescriptionForOfferings, other.i18nDescriptionForOfferings)
                    && Objects.equal(this.epsgCodes, other.epsgCodes)
                    && Objects.equal(this.featuresOfInterest, other.featuresOfInterest)
                    && Objects.equal(this.procedures, other.procedures)
                    && Objects.equal(this.resultTemplates, other.resultTemplates)
                    && Objects.equal(this.globalEnvelope, other.getGlobalEnvelope())
                    && Objects.equal(this.globalPhenomenonTimeEnvelope, other.globalPhenomenonTimeEnvelope)
                    && Objects.equal(this.globalResultTimeEnvelope, other.globalResultTimeEnvelope)
                    && Objects.equal(this.offerings, other.offerings)
                    && Objects.equal(this.supportedLanguages, other.getSupportedLanguages())
                    && Objects.equal(this.featureOfInterestHumanReadableNameForIdentifier, other.featureOfInterestHumanReadableNameForIdentifier)
                    && Objects.equal(this.featureOfInterestIdentifierForHumanReadableName, other.featureOfInterestIdentifierForHumanReadableName)
                    && Objects.equal(this.observablePropertyHumanReadableNameForIdentifier,other.observablePropertyHumanReadableNameForIdentifier)
                    && Objects.equal(this.observablePropertyIdentifierForHumanReadableName, other.observablePropertyIdentifierForHumanReadableName)
                    && Objects.equal(this.procedureHumanReadableNameForIdentifier, other.procedureHumanReadableNameForIdentifier)
                    && Objects.equal(this.procedureIdentifierForHumanReadableName, other.procedureIdentifierForHumanReadableName)
                    && Objects.equal(this.offeringHumanReadableNameForIdentifier, other.offeringHumanReadableNameForIdentifier)
                    && Objects.equal(this.offeringIdentifierForHumanReadableName, other.offeringIdentifierForHumanReadableName)
                    && Objects.equal(this.compositePhenomenons, other.compositePhenomenons)
                    && Objects.equal(this.compositePhenomenonsForProcedure, other.compositePhenomenonsForProcedure)
                    && Objects.equal(this.compositePhenomenonsForOffering, other.compositePhenomenonsForOffering)
                    && Objects.equal(this.observablePropertiesForCompositePhenomenon, other.observablePropertiesForCompositePhenomenon)
                    && Objects.equal(this.compositePhenomenonForObservableProperty, other.compositePhenomenonForObservableProperty)
                    && Objects.equal(this.typeInstanceProcedures, other.typeInstanceProcedures)
                    && Objects.equal(this.componentAggregationProcedures, other.componentAggregationProcedures)
                    && Objects.equal(this.typeOfProcedures, other.typeOfProcedures)
                    && Objects.equal(this.procedureProcedureDescriptionFormats, other.procedureProcedureDescriptionFormats);
            }
        return false;
    }

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
    public DateTime getMinPhenomenonTime() {
        return this.globalPhenomenonTimeEnvelope.getStart();
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
    public Set<String> getProcedures() {
        return copyOf(this.procedures);
    }

    @Override
    public Set<String> getResultTemplates() {
        return copyOf(this.resultTemplates);
    }

    @Override
    public SosEnvelope getGlobalEnvelope() {
        return copyOf(this.globalEnvelope);
    }

    @Override
    public Set<String> getOfferings() {
        return copyOf(this.offerings);
    }

    @Override
    public Set<String> getOfferingsForObservableProperty(final String observableProperty) {
        return copyOf(this.offeringsForObservableProperties.get(observableProperty));
    }

    @Override
    public Set<String> getOfferingsForProcedure(final String procedure) {
        return copyOf(this.offeringsForProcedures.get(procedure));
    }

    @Override
    public Set<String> getProceduresForFeatureOfInterest(final String featureOfInterest) {
        return copyOf(this.proceduresForFeaturesOfInterest.get(featureOfInterest));
    }

    @Override
    public Set<String> getProceduresForObservableProperty(final String observableProperty) {
        return copyOf(this.proceduresForObservableProperties.get(observableProperty));
    }

    @Override
    public Set<String> getProceduresForOffering(final String offering) {
        return copyOf(this.proceduresForOfferings.get(offering));
    }

    @Override
    public Set<String> getHiddenChildProceduresForOffering(final String offering) {
        return copyOf(this.hiddenChildProceduresForOfferings.get(offering));
    }

    @Override
    public Set<String> getRelatedFeaturesForOffering(final String offering) {
        return copyOf(this.relatedFeaturesForOfferings.get(offering));
    }

    @Override
    public Set<String> getResultTemplatesForOffering(final String offering) {
        return copyOf(this.resultTemplatesForOfferings.get(offering));
    }

    @Override
    public Set<String> getRolesForRelatedFeature(final String relatedFeature) {
        return copyOf(this.rolesForRelatedFeatures.get(relatedFeature));
    }

    @Override
    public SosEnvelope getEnvelopeForOffering(final String offering) {
        return copyOf(this.envelopeForOfferings.get(offering));
    }

    @Override
    public String getNameForOffering(final String offering) {
        return this.nameForOfferings.get(offering);
    }


    @Override
    public LocalizedString getI18nNameForOffering(String offering, Locale i18n) {
        MultilingualString map = this.i18nNameForOfferings.get(offering);
        if (map != null) {
            return map.getLocalization(i18n).orNull();
        }
        return null;
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
        MultilingualString map = this.i18nDescriptionForOfferings.get(offering);
        if (map != null) {
            return map.getLocalization(i18n).orNull();
        }
        return null;
    }

    @Override
    public MultilingualString getI18nDescriptionsForOffering(String offering) {
        return this.i18nDescriptionForOfferings.get(offering);
    }

    @Override
    public boolean hasI18NDescriptionForOffering(String offering, Locale i18n) {
        return this.i18nDescriptionForOfferings.containsKey(offering) && this.i18nDescriptionForOfferings.get(offering).hasLocale(i18n);
    }

    @Override
    public DateTime getMaxPhenomenonTimeForOffering(final String offering) {
        return this.maxPhenomenonTimeForOfferings.get(offering);
    }

    @Override
    public DateTime getMinPhenomenonTimeForOffering(final String offering) {
        return this.minPhenomenonTimeForOfferings.get(offering);
    }

    @Override
    public DateTime getMaxPhenomenonTimeForProcedure(final String procedure) {
        DateTime maxTime = null;
        for (final String thisProcedure : getChildProcedures(procedure, true, true)) {
            if (this.maxPhenomenonTimeForProcedures.get(thisProcedure) != null) {
                final DateTime thisTime = this.maxPhenomenonTimeForProcedures.get(thisProcedure);
                if (maxTime == null || maxTime.isBefore(thisTime)) {
                    maxTime = thisTime;
                }
            }
        }
        return maxTime;
    }

    @Override
    public DateTime getMinPhenomenonTimeForProcedure(final String procedure) {
        DateTime minTime = null;
        for (final String thisProcedure : getChildProcedures(procedure, true, true)) {
            if (this.minPhenomenonTimeForProcedures.get(thisProcedure) != null) {
                final DateTime thisTime = this.minPhenomenonTimeForProcedures.get(thisProcedure);
                if (minTime == null || minTime.isBefore(thisTime)) {
                    minTime = thisTime;
                }
            }
        }
        return minTime;
    }

    @Override
    public Set<String> getAllowedObservationTypesForOffering(final String offering) {
        return copyOf(this.allowedObservationTypeForOfferings.get(offering));
    }
    
    @Override
    public Set<String> getAllObservationTypesForOffering(final String offering) {
        Set<String> observationTypes  =Sets.newHashSet(copyOf(this.allowedObservationTypeForOfferings.get(offering)));
        observationTypes.addAll(getObservationTypesForOffering(offering));
        return observationTypes;
    }

    @Override
    public Set<String> getFeaturesOfInterestForOffering(final String offering) {
        return copyOf(this.featuresOfInterestForOfferings.get(offering));
    }
    
    @Override
    public Set<String> getOfferingsForFeatureOfInterest(final String featureOfInterest) {
        return copyOf(this.offeringsForFeaturesOfInterest.get(featureOfInterest));
    }

    @Override
    public Set<String> getFeaturesOfInterestForResultTemplate(final String resultTemplate) {
        return copyOf(this.featuresOfInterestForResultTemplates.get(resultTemplate));
    }

    @Override
    public Set<String> getObservablePropertiesForOffering(final String offering) {
        return copyOf(this.observablePropertiesForOfferings.get(offering));
    }

    @Override
    public Set<String> getObservablePropertiesForProcedure(final String procedure) {
        return copyOf(this.observablePropertiesForProcedures.get(procedure));
    }

    @Override
    public Set<String> getObservationTypesForOffering(final String offering) {
        return copyOf(this.observationTypesForOfferings.get(offering));
    }

    @Override
    public Set<String> getObservablePropertiesForResultTemplate(final String resultTemplate) {
        return copyOf(this.observedPropertiesForResultTemplates.get(resultTemplate));
    }

    @Override
    public Set<String> getParentProcedures(final String procedureIdentifier, final boolean fullHierarchy,
            final boolean includeSelf) {
        return getHierarchy(this.parentProceduresForProcedures, procedureIdentifier, fullHierarchy, includeSelf);
    }

    @Override
    public Set<String> getParentProcedures(final Set<String> procedureIdentifiers, final boolean fullHierarchy,
            final boolean includeSelves) {
        return getHierarchy(this.parentProceduresForProcedures, procedureIdentifiers, fullHierarchy, includeSelves);
    }

    @Override
    public Set<String> getParentFeatures(final String featureIdentifier, final boolean fullHierarchy,
            final boolean includeSelf) {
        return getHierarchy(this.parentFeaturesForFeaturesOfInterest, featureIdentifier, fullHierarchy, includeSelf);
    }

    @Override
    public Set<String> getParentFeatures(final Set<String> featureIdentifiers, final boolean fullHierarchy,
            final boolean includeSelves) {
        return getHierarchy(this.parentFeaturesForFeaturesOfInterest, featureIdentifiers, fullHierarchy,
                includeSelves);
    }


    @Override
    public Set<String> getChildProcedures(final String procedureIdentifier, final boolean fullHierarchy,
            final boolean includeSelf) {
        return getHierarchy(this.childProceduresForProcedures, procedureIdentifier, fullHierarchy, includeSelf);
    }

    @Override
    public Set<String> getChildProcedures(final Set<String> procedureIdentifiers, final boolean fullHierarchy,
            final boolean includeSelves) {
        return getHierarchy(this.childProceduresForProcedures, procedureIdentifiers, fullHierarchy, includeSelves);
    }
    

    @Override
    public Set<String> getParentOfferings(final String offeringIdentifier, final boolean fullHierarchy,
            final boolean includeSelf) {
        return getHierarchy(this.parentOfferingsForOfferings, offeringIdentifier, fullHierarchy, includeSelf);
    }

    @Override
    public Set<String> getParentOfferings(final Set<String> offeringIdentifiers, final boolean fullHierarchy,
            final boolean includeSelves) {
        return getHierarchy(this.parentOfferingsForOfferings, offeringIdentifiers, fullHierarchy,
                includeSelves);
    }


    @Override
    public Set<String> getChildOfferings(final String offeringIdentifier, final boolean fullHierarchy,
            final boolean includeSelf) {
        return getHierarchy(this.childOfferingsForOfferings, offeringIdentifier, fullHierarchy, includeSelf);
    }

    @Override
    public Set<String> getChildOfferings(final Set<String> offeringIdentifiers, final boolean fullHierarchy,
            final boolean includeSelves) {
        return getHierarchy(this.childOfferingsForOfferings, offeringIdentifiers, fullHierarchy, includeSelves);
    }
    
    @Override
    public boolean hasParentOfferings(String offering) {
        return this.parentOfferingsForOfferings.containsKey(offering);
    }
    
    @Override
    public Set<String> getChildFeatures(final String featureIdentifier, final boolean fullHierarchy,
            final boolean includeSelf) {
        return getHierarchy(this.childFeaturesForFeatureOfInterest, featureIdentifier, fullHierarchy, includeSelf);
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
    public boolean hasFeatureOfInterest(final String featureOfInterest) {
        return this.featuresOfInterest.contains(featureOfInterest);
    }

    @Override
    public boolean hasObservableProperty(final String observableProperty) {
        return this.getObservableProperties().contains(observableProperty);
    }

    @Override
    public boolean hasObservationType(final String observationType) {
        return getObservationTypes().contains(observationType);
    }

    @Override
    public boolean hasOffering(final String offering) {
        return this.offerings.contains(offering);
    }

    @Override
    public boolean hasProcedure(final String procedure) {
        return this.procedures.contains(procedure);
    }

    @Override
    public boolean hasRelatedFeature(final String relatedFeature) {
        return getRelatedFeatures().contains(relatedFeature);
    }

    @Override
    public boolean hasResultTemplate(final String resultTemplate) {
        return this.resultTemplates.contains(resultTemplate);
    }

    @Override
    public boolean hasEpsgCode(final Integer epsgCode) {
        return getEpsgCodes().contains(epsgCode);
    }

    @Override
    public boolean hasMaxPhenomenonTimeForOffering(final String offering) {
        return getMaxPhenomenonTimeForOffering(offering) != null;
    }

    @Override
    public boolean hasMinPhenomenonTimeForOffering(final String offering) {
        return getMinPhenomenonTimeForOffering(offering) != null;
    }

    @Override
    public boolean hasMaxPhenomenonTimeForProcedure(final String procedure) {
        for (final String thisProcedure : getChildProcedures(procedure, true, true)) {
            if (this.maxPhenomenonTimeForProcedures.get(thisProcedure) != null) {
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean hasMinPhenomenonTimeForProcedure(final String procedure) {
        for (final String thisProcedure : getChildProcedures(procedure, true, true)) {
            if (this.minPhenomenonTimeForProcedures.get(thisProcedure) != null) {
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean hasEnvelopeForOffering(final String offering) {
        final SosEnvelope e = getEnvelopeForOffering(offering);
        return e != null && e.isSetEnvelope();
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
        final SosEnvelope e = getGlobalEnvelope();
        return e != null && e.isSetEnvelope();
    }

    @Override
    public DateTime getMaxResultTime() {
        return this.globalResultTimeEnvelope.getEnd();
    }

    @Override
    public boolean hasMaxResultTime() {
        return getMaxResultTime() != null;
    }

    @Override
    public DateTime getMaxResultTimeForOffering(final String offering) {
        return this.maxResultTimeForOfferings.get(offering);
    }

    @Override
    public boolean hasMaxResultTimeForOffering(final String offering) {
        return getMaxResultTimeForOffering(offering) != null;
    }

    @Override
    public DateTime getMinResultTime() {
        return this.globalResultTimeEnvelope.getStart();
    }

    @Override
    public boolean hasMinResultTime() {
        return getMinResultTime() != null;
    }

    @Override
    public DateTime getMinResultTimeForOffering(final String offering) {
        return this.minResultTimeForOfferings.get(offering);
    }

    @Override
    public boolean hasMinResultTimeForOffering(final String offering) {
        return getMinResultTimeForOffering(offering) != null;
    }

    @Override
    public boolean isRelatedFeatureSampled(final String relatedFeatureIdentifier) {
        return relatedFeatureIdentifier != null && !relatedFeatureIdentifier.isEmpty()
                && getRelatedFeatures().contains(relatedFeatureIdentifier)
                && !getChildFeatures(relatedFeatureIdentifier, true, false).isEmpty();
    }

    @Override
    public SosEnvelope getSpatialFilteringProfileEnvelopeForOffering(String offering) {
        return copyOf(this.spatialFilteringProfileEnvelopeForOfferings.get(offering));
    }

    @Override
    public boolean hasSpatialFilteringProfileEnvelopeForOffering(String offering) {
        final SosEnvelope e = getSpatialFilteringProfileEnvelopeForOffering(offering);
        return e != null && e.isSetEnvelope();
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
    public boolean isLanguageSupported(final Locale language) {
        return this.supportedLanguages.contains(language);
    }

    @Override
    public String getFeatureOfInterestIdentifierForHumanReadableName(String humanReadableName) {
    	if (featureOfInterestIdentifierForHumanReadableName.containsKey(humanReadableName)) {
    		return featureOfInterestIdentifierForHumanReadableName.get(humanReadableName);
    	}
    	return humanReadableName;
    }

    @Override
    public String getFeatureOfInterestHumanReadableNameForIdentifier(String identifier) {
    	if (featureOfInterestHumanReadableNameForIdentifier.containsKey(identifier)) {
    		return featureOfInterestHumanReadableNameForIdentifier.get(identifier);
    	}
    	return identifier;
    }

    @Override
    public String getObservablePropertyIdentifierForHumanReadableName(String humanReadableName) {
    	if (observablePropertyIdentifierForHumanReadableName.containsKey(humanReadableName)) {
    		return observablePropertyIdentifierForHumanReadableName.get(humanReadableName);
    	}
    	return humanReadableName;
    }

    @Override
    public String getObservablePropertyHumanReadableNameForIdentifier(String identifier) {
    	if (observablePropertyHumanReadableNameForIdentifier.containsKey(identifier)) {
    		return observablePropertyHumanReadableNameForIdentifier.get(identifier);
    	}
    	return identifier;
    }

    @Override
    public String getProcedureIdentifierForHumanReadableName(String humanReadableName) {
    	if (procedureIdentifierForHumanReadableName.containsKey(humanReadableName)) {
    		return procedureIdentifierForHumanReadableName.get(humanReadableName);
    	}
    	return humanReadableName;
    }

    @Override
    public String getProcedureHumanReadableNameForIdentifier(String identifier) {
    	if (procedureHumanReadableNameForIdentifier.containsKey(identifier)) {
    		return procedureHumanReadableNameForIdentifier.get(identifier);
    	}
    	return identifier;
    }

    @Override
    public String getOfferingIdentifierForHumanReadableName(String humanReadableName) {
    	if (offeringIdentifierForHumanReadableName.containsKey(humanReadableName)) {
    		return offeringIdentifierForHumanReadableName.get(humanReadableName);
    	}
    	return humanReadableName;
    }

    @Override
    public String getOfferingHumanReadableNameForIdentifier(String identifier) {
    	if (offeringHumanReadableNameForIdentifier.containsKey(identifier)) {
    		return offeringHumanReadableNameForIdentifier.get(identifier);
    	}
    	return identifier;
    }

    @Override
    public void removeResultTemplates(final Collection<String> resultTemplates) {
        for (final String resultTemplate : resultTemplates) {
            removeResultTemplate(resultTemplate);
        }
    }

    @Override
    public void addEpsgCode(final Integer epsgCode) {
        greaterZero(EPSG_CODE, epsgCode);
        LOG.trace("Adding EpsgCode {}", epsgCode);
        this.epsgCodes.add(epsgCode);
    }

    @Override
    public void addFeatureOfInterest(final String featureOfInterest) {
        notNullOrEmpty(FEATURE_OF_INTEREST, featureOfInterest);
        LOG.trace("Adding FeatureOfInterest {}", featureOfInterest);
        this.featuresOfInterest.add(featureOfInterest);
    }

    @Override
    public void addProcedure(final String procedure) {
        notNullOrEmpty(PROCEDURE, procedure);
        LOG.trace("Adding procedure {}", procedure);
        this.procedures.add(procedure);
    }

    @Override
    public void addResultTemplate(final String resultTemplate) {
        notNullOrEmpty(RESULT_TEMPLATE, resultTemplate);
        LOG.trace("Adding SosResultTemplate {}", resultTemplate);
        this.resultTemplates.add(resultTemplate);
    }

    @Override
    public void addResultTemplates(final Collection<String> resultTemplates) {
        noNullValues(RESULT_TEMPLATES, resultTemplates);
        for (final String resultTemplate : resultTemplates) {
            addResultTemplate(resultTemplate);
        }
    }

    @Override
    public void addEpsgCodes(final Collection<Integer> epsgCodes) {
        noNullValues(EPSG_CODES, epsgCodes);
        for (final Integer epsgCode : epsgCodes) {
            addEpsgCode(epsgCode);
        }
    }

    @Override
    public void addFeaturesOfInterest(final Collection<String> featuresOfInterest) {
        noNullValues(FEATURES_OF_INTEREST, featuresOfInterest);
        for (final String featureOfInterest : featuresOfInterest) {
            addFeatureOfInterest(featureOfInterest);
        }
    }

    @Override
    public void addProcedures(final Collection<String> procedures) {
        noNullValues(PROCEDURES, procedures);
        for (final String procedure : procedures) {
            addProcedure(procedure);
        }
    }

    @Override
    public void removeFeatureOfInterest(final String featureOfInterest) {
        notNullOrEmpty(FEATURE_OF_INTEREST, featureOfInterest);
        LOG.trace("Removing FeatureOfInterest {}", featureOfInterest);
        this.featuresOfInterest.remove(featureOfInterest);
    }

    @Override
    public void removeFeaturesOfInterest(final Collection<String> featuresOfInterest) {
        noNullValues(FEATURES_OF_INTEREST, featuresOfInterest);
        for (final String featureOfInterest : featuresOfInterest) {
            removeFeatureOfInterest(featureOfInterest);
        }
    }

    @Override
    public void removeProcedure(final String procedure) {
        notNullOrEmpty(PROCEDURE, procedure);
        LOG.trace("Removing Procedure {}", procedure);
        this.procedures.remove(procedure);
    }

    @Override
    public void removeProcedures(final Collection<String> procedures) {
        noNullValues(PROCEDURES, procedures);
        for (final String procedure : procedures) {
            removeProcedure(procedure);
        }
    }

    @Override
    public void removeResultTemplate(final String resultTemplate) {
        notNullOrEmpty(RESULT_TEMPLATE, resultTemplate);
        LOG.trace("Removing SosResultTemplate {}", resultTemplate);
        this.resultTemplates.remove(resultTemplate);
    }

    @Override
    public void setObservablePropertiesForOffering(final String offering, final Collection<String> observableProperties) {
        final Set<String> newValue = newSynchronizedSet(observableProperties);
        LOG.trace("Setting ObservableProperties for Offering {} to {}", offering, observableProperties);
        this.observablePropertiesForOfferings.put(offering, newValue);
    }

    @Override
    public void setObservablePropertiesForProcedure(final String procedure,
            final Collection<String> observableProperties) {
        final Set<String> newValue = newSynchronizedSet(observableProperties);
        LOG.trace("Setting ObservableProperties for Procedure {} to {}", procedure, newValue);
        this.observablePropertiesForProcedures.put(procedure, newValue);
    }

    @Override
    public void setObservationTypesForOffering(final String offering, final Collection<String> observationTypes) {
        final Set<String> newValue = newSynchronizedSet(observationTypes);
        LOG.trace("Setting ObservationTypes for Offering {} to {}", offering, newValue);
        this.observationTypesForOfferings.put(offering, newValue);
    }

    @Override
    public void setOfferingsForObservableProperty(final String observableProperty, final Collection<String> offerings) {
        final Set<String> newValue = newSynchronizedSet(offerings);
        LOG.trace("Setting Offerings for ObservableProperty {} to {}", observableProperty, newValue);
        this.offeringsForObservableProperties.put(observableProperty, newValue);
    }

    @Override
    public void setOfferingsForProcedure(final String procedure, final Collection<String> offerings) {
        final Set<String> newValue = newSynchronizedSet(offerings);
        LOG.trace("Setting Offerings for Procedure {} to {}", procedure, newValue);
        this.offeringsForProcedures.put(procedure, newValue);
    }

    @Override
    public void setProceduresForFeatureOfInterest(final String featureOfInterest,
            final Collection<String> proceduresForFeatureOfInterest) {
        final Set<String> newValue = newSynchronizedSet(proceduresForFeatureOfInterest);
        LOG.trace("Setting Procedures for FeatureOfInterest {} to {}", featureOfInterest, newValue);
        this.proceduresForFeaturesOfInterest.put(featureOfInterest, newValue);
    }

    @Override
    public void setProceduresForObservableProperty(final String observableProperty, final Collection<String> procedures) {
        final Set<String> newValue = newSynchronizedSet(procedures);
        LOG.trace("Setting Procedures for ObservableProperty {} to {}", observableProperty, procedures);
        this.proceduresForObservableProperties.put(observableProperty, newValue);
    }

    @Override
    public void setProceduresForOffering(final String offering, final Collection<String> procedures) {
        final Set<String> newValue = newSynchronizedSet(procedures);
        LOG.trace("Setting Procedures for Offering {} to {}", offering, newValue);
        this.proceduresForOfferings.put(offering, newValue);
    }

    @Override
    public void setRelatedFeaturesForOffering(final String offering, final Collection<String> relatedFeatures) {
        final Set<String> newValue = newSynchronizedSet(relatedFeatures);
        LOG.trace("Setting Related Features for Offering {} to {}", offering, newValue);
        this.relatedFeaturesForOfferings.put(offering, newValue);
    }

    @Override
    public void setResultTemplatesForOffering(final String offering, final Collection<String> resultTemplates) {
        final Set<String> newValue = newSynchronizedSet(resultTemplates);
        LOG.trace("Setting ResultTemplates for Offering {} to {}", offering, newValue);
        this.resultTemplatesForOfferings.put(offering, newValue);
    }

    @Override
    public void setRolesForRelatedFeature(final String relatedFeature, final Collection<String> roles) {
        final Set<String> newValue = newSynchronizedSet(roles);
        LOG.trace("Setting Roles for RelatedFeature {} to {}", relatedFeature, newValue);
        this.rolesForRelatedFeatures.put(relatedFeature, newValue);
    }

    @Override
    public void setFeaturesOfInterest(final Collection<String> featuresOfInterest) {
        LOG.trace("Setting FeaturesOfInterest");
        this.featuresOfInterest.clear();
        addFeaturesOfInterest(featuresOfInterest);
    }

    @Override
    public void setPhenomenonTime(final DateTime minEventTime, final DateTime maxEventTime) {
        setMinPhenomenonTime(minEventTime);
        setMaxPhenomenonTime(maxEventTime);
    }

    @Override
    public void setProcedures(final Collection<String> procedures) {
        LOG.trace("Setting Procedures");
        this.procedures.clear();
        addProcedures(procedures);
    }

    @Override
    public void setMaxPhenomenonTimeForOffering(final String offering, final DateTime maxTime) {
        notNullOrEmpty(OFFERING, offering);
        LOG.trace("Setting maximal EventTime for Offering {} to {}", offering, maxTime);
        if (maxTime == null) {
            this.maxPhenomenonTimeForOfferings.remove(offering);
        } else {
            this.maxPhenomenonTimeForOfferings.put(offering, DateTimeHelper.toUTC(maxTime));
        }
    }

    @Override
    public void setMinPhenomenonTimeForOffering(final String offering, final DateTime minTime) {
        notNullOrEmpty(OFFERING, offering);
        LOG.trace("Setting minimal EventTime for Offering {} to {}", offering, minTime);
        if (minTime == null) {
            this.minPhenomenonTimeForOfferings.remove(offering);
        } else {
            this.minPhenomenonTimeForOfferings.put(offering, DateTimeHelper.toUTC(minTime));
        }
    }

    @Override
    public void setMaxPhenomenonTimeForProcedure(final String procedure, final DateTime maxTime) {
        notNullOrEmpty(PROCEDURE, procedure);
        LOG.trace("Setting maximal phenomenon time for procedure {} to {}", procedure, maxTime);
        if (maxTime == null) {
            this.maxPhenomenonTimeForProcedures.remove(procedure);
        } else {
            this.maxPhenomenonTimeForProcedures.put(procedure, DateTimeHelper.toUTC(maxTime));
        }
    }

    @Override
    public void setMinPhenomenonTimeForProcedure(final String procedure, final DateTime minTime) {
        notNullOrEmpty(PROCEDURE, procedure);
        LOG.trace("Setting minimal phenomenon time for procedure {} to {}", procedure, minTime);
        if (minTime == null) {
            this.minPhenomenonTimeForProcedures.remove(procedure);
        } else {
            this.minPhenomenonTimeForProcedures.put(procedure, DateTimeHelper.toUTC(minTime));
        }
    }

    @Override
    public void setNameForOffering(final String offering, final String name) {
        notNullOrEmpty(OFFERING, offering);
        notNullOrEmpty(NAME, name);
        LOG.trace("Setting Name of Offering {} to {}", offering, name);
        this.nameForOfferings.put(offering, name);

    }

    @Override
    public void setI18nNameForOffering(String offering, MultilingualString name) {
        notNullOrEmpty(OFFERING, offering);
        notNull(NAME, name);
        LOG.trace("Setting I18N Name of Offering {} to {}", offering, name);
        this.i18nNameForOfferings.put(offering, name);
    }

    @Override
    public void setI18nDescriptionForOffering(String offering,
                                              MultilingualString description) {
        notNullOrEmpty(OFFERING, offering);
        notNull(DESCRIPTION, description);
        LOG.trace("Setting I18N Description of Offering {} to {}", offering, description);
        this.i18nDescriptionForOfferings.put(offering, description);
    }

    @Override
    public void setEnvelopeForOffering(final String offering, final SosEnvelope envelope) {
        LOG.trace("Setting Envelope for Offering {} to {}", offering, envelope);
        this.envelopeForOfferings.put(offering, copyOf(envelope));
    }

    @Override
    public Set<String> getFeaturesOfInterestWithOffering() {
        return CollectionHelper.unionOfListOfLists(this.featuresOfInterestForOfferings.values());
    }
    
    @Override
    public Set<String> getOfferingWithFeaturesOfInterest() {
        return CollectionHelper.unionOfListOfLists(this.offeringsForFeaturesOfInterest.values());
    }

    @Override
    public void addAllowedObservationTypeForOffering(final String offering, final String allowedObservationType) {
        notNullOrEmpty(OFFERING, offering);
        notNullOrEmpty(ALLOWED_OBSERVATION_TYPE, allowedObservationType);
        LOG.trace("Adding AllowedObservationType {} to Offering {}", allowedObservationType, offering);
        this.allowedObservationTypeForOfferings.add(offering, allowedObservationType);
    }

    @Override
    public void addAllowedObservationTypesForOffering(final String offering,
            final Collection<String> allowedObservationTypes) {
        notNullOrEmpty(OFFERING, offering);
        noNullValues(ALLOWED_OBSERVATION_TYPES, allowedObservationTypes);
        LOG.trace("Adding AllowedObservationTypes {} to Offering {}", allowedObservationTypes, offering);
        this.allowedObservationTypeForOfferings.addAll(offering, allowedObservationTypes);
    }

    @Override
    public void addFeatureOfInterestForOffering(final String offering, final String featureOfInterest) {
        notNullOrEmpty(OFFERING, offering);
        notNullOrEmpty(FEATURE_OF_INTEREST, featureOfInterest);
        LOG.trace("Adding featureOfInterest {} to Offering {}", featureOfInterest, offering);
        this.featuresOfInterestForOfferings.add(offering, featureOfInterest);
        this.offeringsForFeaturesOfInterest.add(featureOfInterest, offering);
    }

    @Override
    public void addFeatureOfInterestForResultTemplate(final String resultTemplate, final String featureOfInterest) {
        notNullOrEmpty(RESULT_TEMPLATE, resultTemplate);
        notNullOrEmpty(FEATURE_OF_INTEREST, featureOfInterest);
        LOG.trace("Adding FeatureOfInterest {} to SosResultTemplate {}", featureOfInterest, resultTemplate);
        this.featuresOfInterestForResultTemplates.add(resultTemplate, featureOfInterest);
    }

    @Override
    public void addFeaturesOfInterestForResultTemplate(final String resultTemplate,
            final Collection<String> featuresOfInterest) {
        notNullOrEmpty(RESULT_TEMPLATE, resultTemplate);
        noNullValues(FEATURES_OF_INTEREST, featuresOfInterest);
        LOG.trace("Adding FeatureOfInterest {} to SosResultTemplate {}", featuresOfInterest, resultTemplate);
        this.featuresOfInterestForResultTemplates.addAll(resultTemplate, featuresOfInterest);
    }

    @Override
    public void addObservablePropertyForOffering(final String offering, final String observableProperty) {
        notNullOrEmpty(OFFERING, offering);
        notNullOrEmpty(OBSERVABLE_PROPERTY, observableProperty);
        LOG.trace("Adding observableProperty {} to offering {}", observableProperty, offering);
        this.observablePropertiesForOfferings.add(offering, observableProperty);
    }

    @Override
    public void addObservablePropertyForProcedure(final String procedure, final String observableProperty) {
        notNullOrEmpty(PROCEDURE, procedure);
        notNullOrEmpty(OBSERVABLE_PROPERTY, observableProperty);
        LOG.trace("Adding observableProperty {} to procedure {}", observableProperty, procedure);
        this.observablePropertiesForProcedures.add(procedure, observableProperty);
    }

    @Override
    public void addObservablePropertyForResultTemplate(final String resultTemplate, final String observableProperty) {
        notNullOrEmpty(RESULT_TEMPLATE, resultTemplate);
        notNullOrEmpty(OBSERVABLE_PROPERTY, observableProperty);
        LOG.trace("Adding observableProperty {} to resultTemplate {}", observableProperty, resultTemplate);
        this.observedPropertiesForResultTemplates.add(resultTemplate, observableProperty);
    }

    @Override
    public void addObservationTypesForOffering(final String offering, final String observationType) {
        notNullOrEmpty(OFFERING, offering);
        notNullOrEmpty(OBSERVATION_TYPE, observationType);
        LOG.trace("Adding observationType {} to offering {}", observationType, offering);
        this.observationTypesForOfferings.add(offering, observationType);
    }

    @Override
    public void addOfferingForObservableProperty(final String observableProperty, final String offering) {
        notNullOrEmpty(OBSERVABLE_PROPERTY, observableProperty);
        notNullOrEmpty(OFFERING, offering);
        LOG.trace("Adding offering {} to observableProperty {}", offering, observableProperty);
        this.offeringsForObservableProperties.add(observableProperty, offering);
    }

    @Override
    public void addOfferingForProcedure(final String procedure, final String offering) {
        notNullOrEmpty(PROCEDURE, procedure);
        notNullOrEmpty(OFFERING, offering);
        LOG.trace("Adding offering {} to procedure {}", offering, procedure);
        this.offeringsForProcedures.add(procedure, offering);
    }

    @Override
    public void addProcedureForFeatureOfInterest(final String featureOfInterest, final String procedure) {
        notNullOrEmpty(FEATURE_OF_INTEREST, featureOfInterest);
        notNullOrEmpty(PROCEDURE, procedure);
        LOG.trace("Adding procedure {} to featureOfInterest {}", procedure, featureOfInterest);
        this.proceduresForFeaturesOfInterest.add(featureOfInterest, procedure);
    }

    @Override
    public void addProcedureForObservableProperty(final String observableProperty, final String procedure) {
        notNullOrEmpty(FEATURE_OF_INTEREST, observableProperty);
        notNullOrEmpty(PROCEDURE, procedure);
        LOG.trace("Adding procedure {} to observableProperty {}", procedure, observableProperty);
        this.proceduresForObservableProperties.add(observableProperty, procedure);
    }

    @Override
    public void addProcedureForOffering(final String offering, final String procedure) {
        notNullOrEmpty(OFFERING, offering);
        notNullOrEmpty(PROCEDURE, procedure);
        LOG.trace("Adding procedure {} to offering {}", procedure, offering);
        this.proceduresForOfferings.add(offering, procedure);
    }

    @Override
    public void addRelatedFeatureForOffering(final String offering, final String relatedFeature) {
        notNullOrEmpty(OFFERING, offering);
        notNullOrEmpty(RELATED_FEATURE, relatedFeature);
        LOG.trace("Adding relatedFeature {} to offering {}", relatedFeature, offering);
        this.relatedFeaturesForOfferings.add(offering, relatedFeature);
    }

    @Override
    public void addRelatedFeaturesForOffering(final String offering, final Collection<String> relatedFeature) {
        notNullOrEmpty(OFFERING, offering);
        noNullValues(RELATED_FEATURE, relatedFeature);
        LOG.trace("Adding relatedFeatures {} to offering {}", relatedFeature, offering);
        this.relatedFeaturesForOfferings.addAll(offering, relatedFeature);
    }

    @Override
    public void addResultTemplateForOffering(final String offering, final String resultTemplate) {
        notNullOrEmpty(OFFERING, offering);
        notNullOrEmpty(RESULT_TEMPLATE, resultTemplate);
        LOG.trace("Adding resultTemplate {} to offering {}", resultTemplate, offering);
        this.resultTemplatesForOfferings.add(offering, resultTemplate);
    }

    @Override
    public void addRoleForRelatedFeature(final String relatedFeature, final String role) {
        notNullOrEmpty(RELATED_FEATURE, relatedFeature);
        notNullOrEmpty("role", role);
        LOG.trace("Adding role {} to relatedFeature {}", role, relatedFeature);
        this.rolesForRelatedFeatures.add(relatedFeature, role);
    }

    @Override
    public void removeAllowedObservationTypeForOffering(final String offering, final String allowedObservationType) {
        notNullOrEmpty(OFFERING, offering);
        notNullOrEmpty("allowedObservationType", allowedObservationType);
        LOG.trace("Removing allowedObservationType {} from offering {}", allowedObservationType, offering);
        this.allowedObservationTypeForOfferings.removeWithKey(offering, allowedObservationType);
    }

    @Override
    public void removeAllowedObservationTypesForOffering(final String offering) {
        notNullOrEmpty(OFFERING, offering);
        LOG.trace("Removing allowedObservationTypes for offering {}", offering);
        this.allowedObservationTypeForOfferings.remove(offering);
    }

    @Override
    public void removeEnvelopeForOffering(final String offering) {
        notNullOrEmpty(OFFERING, offering);
        LOG.trace("Removing envelope for offering {}", offering);
        this.envelopeForOfferings.remove(offering);
    }

    @Override
    public void removeEpsgCode(final Integer epsgCode) {
        notNull(EPSG_CODE, epsgCode);
        LOG.trace("Removing epsgCode {}", epsgCode);
        this.epsgCodes.remove(epsgCode);
    }

    @Override
    public void removeEpsgCodes(final Collection<Integer> epsgCodes) {
        noNullValues(EPSG_CODES, epsgCodes);
        for (final Integer code : epsgCodes) {
            removeEpsgCode(code);
        }
    }

    @Override
    public void removeFeatureOfInterestForOffering(final String offering, final String featureOfInterest) {
        notNullOrEmpty(OFFERING, offering);
        notNullOrEmpty(FEATURE_OF_INTEREST, featureOfInterest);
        LOG.trace("Removing featureOfInterest {} from offering {}", featureOfInterest, offering);
        this.featuresOfInterestForOfferings.removeWithKey(offering, featureOfInterest);
        this.offeringsForFeaturesOfInterest.removeWithKey(featureOfInterest, offering);
    }

    @Override
    public void removeFeatureOfInterestForResultTemplate(final String resultTemplate, final String featureOfInterest) {
        notNullOrEmpty(RESULT_TEMPLATE, resultTemplate);
        notNullOrEmpty(FEATURE_OF_INTEREST, featureOfInterest);
        LOG.trace("Removing featureOfInterest {} from resultTemplate {}", featureOfInterest, resultTemplate);
        this.featuresOfInterestForResultTemplates.removeWithKey(resultTemplate, featureOfInterest);
    }

    @Override
    public void removeFeaturesOfInterestForOffering(final String offering) {
        notNullOrEmpty(OFFERING, offering);
        LOG.trace("Removing featuresOfInterest for offering {}", offering);
        if (featuresOfInterestForOfferings.containsKey(offering)) {
            for (String featureOfInterest : featuresOfInterestForOfferings.get(offering)) {
                this.offeringsForFeaturesOfInterest.removeWithKey(featureOfInterest, offering);
            }
        }
        this.featuresOfInterestForOfferings.remove(offering);
    }

    @Override
    public void removeFeaturesOfInterestForResultTemplate(final String resultTemplate) {
        notNullOrEmpty(RESULT_TEMPLATE, resultTemplate);
        LOG.trace("Removing featuresOfInterest for resultTemplate {}", resultTemplate);
        this.featuresOfInterestForResultTemplates.remove(resultTemplate);
    }

    @Override
    public void removeMaxPhenomenonTimeForOffering(final String offering) {
        notNullOrEmpty(OFFERING, offering);
        LOG.trace("Removing maxEventTime for offering {}", offering);
        this.maxPhenomenonTimeForOfferings.remove(offering);
    }

    @Override
    public void removeMinPhenomenonTimeForOffering(final String offering) {
        notNullOrEmpty(OFFERING, offering);
        LOG.trace("Removing minEventTime for offering {}", offering);
        this.minPhenomenonTimeForOfferings.remove(offering);
    }

    @Override
    public void removeMaxPhenomenonTimeForProcedure(final String procedure) {
        notNullOrEmpty(PROCEDURE, procedure);
        LOG.trace("Removing maxEventTime for procedure {}", procedure);
        this.maxPhenomenonTimeForProcedures.remove(procedure);
    }

    @Override
    public void removeMinPhenomenonTimeForProcedure(final String procedure) {
        notNullOrEmpty(PROCEDURE, procedure);
        LOG.trace("Removing minEventTime for procedure {}", procedure);
        this.minPhenomenonTimeForProcedures.remove(procedure);
    }

    @Override
    public void removeNameForOffering(final String offering) {
        notNullOrEmpty(OFFERING, offering);
        LOG.trace("Removing name for offering {}", offering);
        this.nameForOfferings.remove(offering);
    }

    @Override
    public void removeObservablePropertiesForOffering(final String offering) {
        notNullOrEmpty(OFFERING, offering);
        LOG.trace("Removing observableProperties for offering {}", offering);
        this.observablePropertiesForOfferings.remove(offering);
    }

    @Override
    public void removeObservablePropertiesForProcedure(final String procedure) {
        notNullOrEmpty(PROCEDURE, procedure);
        LOG.trace("Removing observableProperties for procedure {}", procedure);
        this.observablePropertiesForProcedures.remove(procedure);
    }

    @Override
    public void removeObservablePropertiesForResultTemplate(final String resultTemplate) {
        notNullOrEmpty(RESULT_TEMPLATE, resultTemplate);
        LOG.trace("Removing observableProperties for resultTemplate {}", resultTemplate);
        this.observedPropertiesForResultTemplates.remove(resultTemplate);
    }

    @Override
    public void removeObservablePropertyForOffering(final String offering, final String observableProperty) {
        notNullOrEmpty(OFFERING, offering);
        notNullOrEmpty(OBSERVABLE_PROPERTY, observableProperty);
        LOG.trace("Removing observableProperty {} from offering {}", observableProperty, offering);
        this.observablePropertiesForOfferings.removeWithKey(offering, observableProperty);
    }

    @Override
    public void removeObservablePropertyForProcedure(final String procedure, final String observableProperty) {
        notNullOrEmpty(PROCEDURE, procedure);
        notNullOrEmpty(OBSERVABLE_PROPERTY, observableProperty);
        LOG.trace("Removing observableProperty {} from procedure {}", observableProperty, procedure);
        this.observablePropertiesForProcedures.removeWithKey(procedure, observableProperty);
    }

    @Override
    public void removeObservablePropertyForResultTemplate(final String resultTemplate, final String observableProperty) {
        notNullOrEmpty(RESULT_TEMPLATE, resultTemplate);
        notNullOrEmpty(OBSERVABLE_PROPERTY, observableProperty);
        LOG.trace("Removing observableProperty {} from resultTemplate {}", observableProperty, resultTemplate);
        this.observedPropertiesForResultTemplates.removeWithKey(resultTemplate, observableProperty);
    }

    @Override
    public void removeObservationTypeForOffering(final String offering, final String observationType) {
        notNullOrEmpty(OFFERING, offering);
        notNullOrEmpty(OBSERVATION_TYPE, observationType);
        LOG.trace("Removing observationType {} from offering {}", observationType, offering);
        this.observationTypesForOfferings.removeWithKey(offering, observationType);
    }

    @Override
    public void removeObservationTypesForOffering(final String offering) {
        notNullOrEmpty(OFFERING, offering);
        LOG.trace("Removing observationTypes for offering {}", offering);
        this.observationTypesForOfferings.remove(offering);
    }

    @Override
    public void removeOfferingForObservableProperty(final String observableProperty, final String offering) {
        notNullOrEmpty(OBSERVABLE_PROPERTY, observableProperty);
        notNullOrEmpty(OFFERING, offering);
        LOG.trace("Removing offering {} from observableProperty {}", offering, observableProperty);
        this.offeringsForObservableProperties.removeWithKey(observableProperty, offering);
    }

    @Override
    public void removeOfferingForProcedure(final String procedure, final String offering) {
        notNullOrEmpty(PROCEDURE, procedure);
        notNullOrEmpty(OFFERING, offering);
        LOG.trace("Removing offering {} from procedure {}", offering, procedure);
        this.offeringsForProcedures.removeWithKey(procedure, offering);
    }

    @Override
    public void removeOfferingsForObservableProperty(final String observableProperty) {
        notNullOrEmpty(OBSERVABLE_PROPERTY, observableProperty);
        LOG.trace("Removing offerings for observableProperty {}", observableProperty);
        this.offeringsForObservableProperties.remove(observableProperty);
    }

    @Override
    public void removeOfferingsForProcedure(final String procedure) {
        notNullOrEmpty(PROCEDURE, procedure);
        LOG.trace("Removing offering for procedure {}", procedure);
        this.offeringsForProcedures.remove(procedure);
    }

    @Override
    public void removeProcedureForFeatureOfInterest(final String featureOfInterest, final String procedure) {
        notNullOrEmpty(FEATURE_OF_INTEREST, featureOfInterest);
        notNullOrEmpty(PROCEDURE, procedure);
        LOG.trace("Removing procedure {} from featureOfInterest {}", procedure, featureOfInterest);
        this.proceduresForFeaturesOfInterest.removeWithKey(featureOfInterest, procedure);
    }

    @Override
    public void removeProcedureForObservableProperty(final String observableProperty, final String procedure) {
        notNullOrEmpty(OBSERVABLE_PROPERTY, observableProperty);
        notNullOrEmpty(PROCEDURE, procedure);
        LOG.trace("Removing procedure {} from observableProperty {}", procedure, observableProperty);
        this.proceduresForObservableProperties.removeWithKey(observableProperty, procedure);
    }

    @Override
    public void removeProcedureForOffering(final String offering, final String procedure) {
        notNullOrEmpty(OFFERING, offering);
        notNullOrEmpty(PROCEDURE, procedure);
        LOG.trace("Removing procedure {} from offering {}", procedure, offering);
        this.proceduresForOfferings.removeWithKey(offering, procedure);
    }

    @Override
    public void removeProceduresForFeatureOfInterest(final String featureOfInterest) {
        notNullOrEmpty(FEATURE_OF_INTEREST, featureOfInterest);
        LOG.trace("Removing procedures for featureOfInterest {}", featureOfInterest);
        this.proceduresForFeaturesOfInterest.remove(featureOfInterest);
    }

    @Override
    public void removeProceduresForObservableProperty(final String observableProperty) {
        notNullOrEmpty(OBSERVABLE_PROPERTY, observableProperty);
        LOG.trace("Removing procedures for observableProperty {}", observableProperty);
        this.proceduresForObservableProperties.remove(observableProperty);
    }

    @Override
    public void removeProceduresForOffering(final String offering) {
        notNullOrEmpty(OFFERING, offering);
        LOG.trace("Removing procedures for offering {}", offering);
        this.proceduresForOfferings.remove(offering);
    }

    @Override
    public void removeRelatedFeatureForOffering(final String offering, final String relatedFeature) {
        notNullOrEmpty(OFFERING, offering);
        notNullOrEmpty(RELATED_FEATURE, relatedFeature);
        LOG.trace("Removing relatedFeature {} from offering {}", relatedFeature, offering);
        this.relatedFeaturesForOfferings.removeWithKey(offering, relatedFeature);
    }

    @Override
    public void removeRelatedFeaturesForOffering(final String offering) {
        notNullOrEmpty(OFFERING, offering);
        LOG.trace("Removing RelatedFeatures for offering {}", offering);
        this.relatedFeaturesForOfferings.remove(offering);
    }

    @Override
    public void removeResultTemplateForOffering(final String offering, final String resultTemplate) {
        notNullOrEmpty(OFFERING, offering);
        notNullOrEmpty(RESULT_TEMPLATE, resultTemplate);
        LOG.trace("Removing resultTemplate {} from offering {}", resultTemplate, offering);
        this.resultTemplatesForOfferings.removeWithKey(offering, resultTemplate);
    }

    @Override
    public void removeResultTemplatesForOffering(final String offering) {
        notNullOrEmpty(OFFERING, offering);
        LOG.trace("Removing ResultTemplates for offering {}", offering);
        this.resultTemplatesForOfferings.remove(offering);
    }

    @Override
    public void removeRoleForRelatedFeature(final String relatedFeature, final String role) {
        notNullOrEmpty(RELATED_FEATURE, relatedFeature);
        notNullOrEmpty(ROLE, role);
        LOG.trace("Removing role {} from relatedFeature {}", role, relatedFeature);
        this.rolesForRelatedFeatures.removeWithKey(relatedFeature, role);
    }

    @Override
    public void removeRolesForRelatedFeature(final String relatedFeature) {
        notNullOrEmpty(RELATED_FEATURE, relatedFeature);
        LOG.trace("Removing roles for relatedFeature {}", relatedFeature);
        this.rolesForRelatedFeatures.remove(relatedFeature);
    }

    @Override
    public void removeRolesForRelatedFeatureNotIn(final Collection<String> relatedFeatures) {
        notNull(RELATED_FEATURES, relatedFeatures);
        final Iterator<String> iter = this.rolesForRelatedFeatures.keySet().iterator();
        while (iter.hasNext()) {
            if (!relatedFeatures.contains(iter.next())) {
                iter.remove();
            }
        }
    }

    @Override
    public void setAllowedObservationTypeForOffering(final String offering,
            final Collection<String> allowedObservationType) {
        notNullOrEmpty(OFFERING, offering);
        final Set<String> newValue = newSynchronizedSet(allowedObservationType);
        LOG.trace("Setting allowedObservationTypes for offering {} to {}", offering, newValue);
        this.allowedObservationTypeForOfferings.put(offering, newValue);
    }

    @Override
    public void setAllowedFeatureOfInterestTypeForOffering(final String offering,
            final Collection<String> allowedFeatureOfInterestType) {
        notNullOrEmpty(OFFERING, offering);
        final Set<String> newValue = newSynchronizedSet(allowedFeatureOfInterestType);
        LOG.trace("Setting allowedFeatureOfInterestTypes for offering {} to {}", offering, newValue);
        this.allowedFeatureOfInterestTypeForOfferings.put(offering, newValue);
    }

    @Override
    public void setFeaturesOfInterestForOffering(final String offering, final Collection<String> featureOfInterest) {
        notNullOrEmpty(OFFERING, offering);
        final Set<String> newValue = newSynchronizedSet(featureOfInterest);
        LOG.trace("Setting featureOfInterest for offering {} to {}", offering, newValue);
        this.featuresOfInterestForOfferings.put(offering, newValue);
    }
    
    @Override
    public void addOfferingForFeaturesOfInterest(final String offering, final Collection<String> featuresOfInterest) {
        notNullOrEmpty(OFFERING, offering);
        noNullOrEmptyValues(FEATURES_OF_INTEREST, featuresOfInterest);
        LOG.trace("Adding offering {} to featureOfInterest {}", offering, featuresOfInterest);
        for (final String featureOfInterest : featuresOfInterest) {
            this.offeringsForFeaturesOfInterest.add(featureOfInterest, offering);
        }
    }

    @Override
    public void setGlobalEnvelope(final SosEnvelope globalEnvelope) {
        LOG.trace("Global envelope now: '{}'", this.globalEnvelope);
        if (globalEnvelope == null) {
            setGlobalSpatialEnvelope(new SosEnvelope(null, getDefaultEPSGCode()));
        } else {
            setGlobalSpatialEnvelope(globalEnvelope);
        }
        LOG.trace("Global envelope updated to '{}' with '{}'", this.globalEnvelope, globalEnvelope);
    }

    @Override
    public void setMaxPhenomenonTime(final DateTime maxEventTime) {
        LOG.trace("Setting Maximal EventTime to {}", maxEventTime);
        this.globalPhenomenonTimeEnvelope.setEnd(DateTimeHelper.toUTC(maxEventTime));
    }

    @Override
    public void setMinPhenomenonTime(final DateTime minEventTime) {
        LOG.trace("Setting Minimal EventTime to {}", minEventTime);
        this.globalPhenomenonTimeEnvelope.setStart(DateTimeHelper.toUTC(minEventTime));
    }

    @Override
    public void setObservablePropertiesForResultTemplate(final String resultTemplate,
            final Collection<String> observableProperties) {
        notNullOrEmpty(RESULT_TEMPLATE, resultTemplate);
        final Set<String> newValue = newSynchronizedSet(observableProperties);
        LOG.trace("Setting observableProperties for resultTemplate {} to {}", resultTemplate, newValue);
        this.observedPropertiesForResultTemplates.put(resultTemplate, newValue);
    }

    @Override
    public void addParentFeature(final String featureOfInterest, final String parentFeature) {
        notNullOrEmpty(FEATURE_OF_INTEREST, featureOfInterest);
        notNullOrEmpty(PARENT_FEATURE, parentFeature);
        LOG.trace("Adding parentFeature {} to featureOfInterest {}", parentFeature, featureOfInterest);
        this.parentFeaturesForFeaturesOfInterest.add(featureOfInterest, parentFeature);
        this.childFeaturesForFeatureOfInterest.add(parentFeature, featureOfInterest);
    }

    @Override
    public void addParentFeatures(final String featureOfInterest, final Collection<String> parentFeatures) {
        notNullOrEmpty(FEATURE_OF_INTEREST, featureOfInterest);
        noNullOrEmptyValues(PARENT_FEATURES, parentFeatures);
        LOG.trace("Adding parentFeature {} to featureOfInterest {}", parentFeatures, featureOfInterest);
        this.parentFeaturesForFeaturesOfInterest.addAll(featureOfInterest, parentFeatures);
        for (final String parentFeature : parentFeatures) {
            this.childFeaturesForFeatureOfInterest.add(parentFeature, featureOfInterest);
        }
    }

    @Override
    public void addParentProcedure(final String procedure, final String parentProcedure) {
        notNullOrEmpty(PROCEDURE, procedure);
        notNullOrEmpty(PARENT_PROCEDURE, parentProcedure);
        LOG.trace("Adding parentProcedure {} to procedure {}", parentProcedure, procedure);
        this.parentProceduresForProcedures.add(procedure, parentProcedure);
        this.childProceduresForProcedures.add(parentProcedure, procedure);
    }

    @Override
    public void addParentProcedures(final String procedure, final Collection<String> parentProcedures) {
        notNullOrEmpty(PROCEDURE, procedure);
        noNullOrEmptyValues(PARENT_PROCEDURES, parentProcedures);
        LOG.trace("Adding parentProcedures {} to procedure {}", parentProcedures, procedure);
        this.parentProceduresForProcedures.addAll(procedure, parentProcedures);
        for (final String parentProcedure : parentProcedures) {
            this.childProceduresForProcedures.add(parentProcedure, procedure);
        }
    }
    
    @Override
    public void addParentOffering(final String offering, final String parentOffering) {
        notNullOrEmpty(OFFERING, offering);
        notNullOrEmpty(PARENT_OFFERING, parentOffering);
        LOG.trace("Adding parentOffering {} to offering {}", parentOffering, offering);
        this.parentOfferingsForOfferings.add(offering, parentOffering);
        this.childOfferingsForOfferings.add(parentOffering, offering);
    }

    @Override
    public void addParentOfferings(final String offering, final Collection<String> parentOfferings) {
        notNullOrEmpty(OFFERING, offering);
        noNullOrEmptyValues(PARENT_OFFERINGS, parentOfferings);
        LOG.trace("Adding parentOfferings {} to offering {}", parentOfferings, offering);
        this.parentOfferingsForOfferings.addAll(offering, parentOfferings);
        for (final String parentProcedure : parentOfferings) {
            this.childOfferingsForOfferings.add(parentProcedure, offering);
        }
    }

    @Override
    public void updateEnvelopeForOffering(final String offering, final Envelope envelope) {
        notNullOrEmpty(OFFERING, offering);
        notNull(ENVELOPE, envelope);
        if (hasEnvelopeForOffering(offering)) {
            final SosEnvelope offeringEnvelope = this.envelopeForOfferings.get(offering);
            LOG.trace("Expanding envelope {} for offering {} to include {}", offeringEnvelope, offering, envelope);
            offeringEnvelope.expandToInclude(envelope);
        } else {
            setEnvelopeForOffering(offering, new SosEnvelope(envelope, getDefaultEPSGCode()));
        }
    }

    @Override
    public void updatePhenomenonTime(final Time eventTime) {
        notNull(EVENT_TIME, eventTime);
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
    public void updateGlobalEnvelope(final Envelope envelope) {
        notNull(ENVELOPE, envelope);
        if (hasGlobalEnvelope()) {
            LOG.trace("Expanding envelope {} to include {}", this.globalEnvelope, envelope);
            this.globalEnvelope.expandToInclude(envelope);
        } else {
            setGlobalEnvelope(new SosEnvelope(new Envelope(envelope), getDefaultEPSGCode()));
        }
    }

    @Override
    public void updatePhenomenonTimeForOffering(final String offering, final Time eventTime) {
        notNullOrEmpty(OFFERING, offering);
        notNull(EVENT_TIME, eventTime);
        final TimePeriod tp = toTimePeriod(eventTime);
        LOG.trace("Expanding EventTime of offering {} to include {}", offering, tp);
        if (!hasMaxPhenomenonTimeForOffering(offering)
                || getMaxPhenomenonTimeForOffering(offering).isBefore(tp.getEnd())) {
            setMaxPhenomenonTimeForOffering(offering, tp.getEnd());
        }
        if (!hasMinPhenomenonTimeForOffering(offering)
                || getMinPhenomenonTimeForOffering(offering).isAfter(tp.getStart())) {
            setMinPhenomenonTimeForOffering(offering, tp.getStart());
        }
    }

    @Override
    public void updatePhenomenonTimeForProcedure(final String procedure, final Time eventTime) {
        notNullOrEmpty(PROCEDURE, procedure);
        notNull(EVENT_TIME, eventTime);
        final TimePeriod tp = toTimePeriod(eventTime);
        LOG.trace("Expanding phenomenon time of procedure {} to include {}", procedure, tp);
        if (!hasMaxPhenomenonTimeForProcedure(procedure)
                || getMaxPhenomenonTimeForProcedure(procedure).isBefore(tp.getEnd())) {
            setMaxPhenomenonTimeForProcedure(procedure, tp.getEnd());
        }
        if (!hasMinPhenomenonTimeForProcedure(procedure)
                || getMinPhenomenonTimeForProcedure(procedure).isAfter(tp.getStart())) {
            setMinPhenomenonTimeForProcedure(procedure, tp.getStart());
        }
    }

    @Override
    public void recalculateGlobalEnvelope() {
        LOG.trace("Recalculating global spatial envelope based on offerings");
        SosEnvelope globalEnvelope = null;
        if (!getOfferings().isEmpty()) {
            for (final String offering : getOfferings()) {
                final SosEnvelope e = getEnvelopeForOffering(offering);
                if (e != null) {
                    if (globalEnvelope == null) {
                        if (e.isSetEnvelope()) {
                            globalEnvelope = new SosEnvelope(new Envelope(e.getEnvelope()), e.getSrid());
                            LOG.trace("First envelope '{}' used as starting point", globalEnvelope);
                        }
                    } else {
                        globalEnvelope.getEnvelope().expandToInclude(e.getEnvelope());
                        LOG.trace("Envelope expanded to include '{}' resulting in '{}'", e, globalEnvelope);
                    }
                }
            }
            if (globalEnvelope == null) {
                LOG.error("Global envelope could not be resetted");
            }
        } else {
            globalEnvelope = new SosEnvelope(null, getDefaultEPSGCode());
        }
        setGlobalEnvelope(globalEnvelope);
        LOG.trace("Spatial envelope finally set to '{}'", getGlobalEnvelope());
    }

    @Override
    public void recalculatePhenomenonTime() {
        LOG.trace("Recalculating global phenomenon time based on offerings");
        DateTime globalMax = null, globalMin = null;
        if (!getOfferings().isEmpty()) {
            for (final String offering : getOfferings()) {
                if (hasMaxPhenomenonTimeForOffering(offering)) {
                    final DateTime offeringMax = getMaxPhenomenonTimeForOffering(offering);
                    if (globalMax == null || offeringMax.isAfter(globalMax)) {
                        globalMax = offeringMax;
                    }
                }
                if (hasMinPhenomenonTimeForOffering(offering)) {
                    final DateTime offeringMin = getMinPhenomenonTimeForOffering(offering);
                    if (globalMin == null || offeringMin.isBefore(globalMin)) {
                        globalMin = offeringMin;
                    }
                }
            }
            if (globalMin == null || globalMax == null) {
                LOG.error("Error in cache! Reset of global temporal bounding box failed. Max: '{}'; Min: '{}'",
                        globalMax, globalMin);
            }
        }
        setPhenomenonTime(globalMin, globalMax);
        LOG.trace("Global temporal bounding box reset done. Min: '{}'; Max: '{}'", getMinPhenomenonTime(),
                getMaxPhenomenonTime());
    }

    @Override
    public void removeMaxResultTimeForOffering(final String offering) {
        notNullOrEmpty(OFFERING, offering);
        LOG.trace("Removing maxResultTime for offering {}", offering);
        this.maxResultTimeForOfferings.remove(offering);
    }

    @Override
    public void removeMinResultTimeForOffering(final String offering) {
        notNullOrEmpty(OFFERING, offering);
        LOG.trace("Removing minResultTime for offering {}", offering);
        this.minResultTimeForOfferings.remove(offering);
    }

    @Override
    public void setResultTime(final DateTime min, final DateTime max) {
        setMinResultTime(min);
        setMaxResultTime(max);
    }

    @Override
    public void updateResultTime(final Time resultTime) {
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
        DateTime globalMax = null, globalMin = null;
        if (!getOfferings().isEmpty()) {
            for (final String offering : getOfferings()) {
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
    public void setMaxResultTime(final DateTime maxResultTime) {
        LOG.trace("Setting Maximal ResultTime to {}", maxResultTime);
        this.globalResultTimeEnvelope.setEnd(DateTimeHelper.toUTC(maxResultTime));
    }

    @Override
    public void setMaxResultTimeForOffering(final String offering, final DateTime maxTime) {
        notNullOrEmpty(OFFERING, offering);
        LOG.trace("Setting maximal ResultTime for Offering {} to {}", offering, maxTime);
        if (maxTime == null) {
            this.maxResultTimeForOfferings.remove(offering);
        } else {
            this.maxResultTimeForOfferings.put(offering, DateTimeHelper.toUTC(maxTime));
        }
    }

    @Override
    public void setMinResultTime(final DateTime minResultTime) {
        LOG.trace("Setting Minimal ResultTime to {}", minResultTime);
        this.globalResultTimeEnvelope.setStart(DateTimeHelper.toUTC(minResultTime));
    }

    @Override
    public void setMinResultTimeForOffering(final String offering, final DateTime minTime) {
        notNullOrEmpty(OFFERING, offering);
        LOG.trace("Setting minimal ResultTime for Offering {} to {}", offering, minTime);
        if (minTime == null) {
            this.minResultTimeForOfferings.remove(offering);
        } else {
            this.minResultTimeForOfferings.put(offering, DateTimeHelper.toUTC(minTime));
        }
    }

    @Override
    public void updateResultTimeForOffering(final String offering, final Time resultTime) {
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
    public void clearOfferingsForFeaturesOfInterest() {
        LOG.trace("Clearing offerings for features of interest");
        this.offeringsForFeaturesOfInterest.clear();
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
    public void addOffering(final String offering) {
        notNullOrEmpty(OFFERING, offering);
        LOG.trace("Adding offering {}", offering);
        this.offerings.add(offering);
    }

    @Override
    public void setOfferings(final Collection<String> offerings) {
        clearOfferings();
        addOfferings(offerings);
    }

    @Override
    public void addOfferings(final Collection<String> offerings) {
        noNullValues(OFFERINGS, offerings);
        for (final String offering : offerings) {
            addOffering(offering);
        }
    }

    @Override
    public void removeOffering(final String offering) {
        notNullOrEmpty(OFFERING, offering);
        LOG.trace("Removing Offering {}", offering);
        this.offerings.remove(offering);
    }

    @Override
    public void removeOfferings(final Collection<String> offerings) {
        noNullValues(OFFERINGS, offerings);
        for (final String offering : offerings) {
            removeOffering(offering);
        }
    }

    @Override
    public void addHiddenChildProcedureForOffering(final String offering, final String procedure) {
        notNullOrEmpty(OFFERING, offering);
        notNullOrEmpty(PROCEDURE, procedure);
        LOG.trace("Adding hidden child procedure {} to offering {}", procedure, offering);
        this.hiddenChildProceduresForOfferings.add(offering, procedure);
    }

    @Override
    public void removeHiddenChildProcedureForOffering(final String offering, final String procedure) {
        notNullOrEmpty(OFFERING, offering);
        notNullOrEmpty(PROCEDURE, procedure);
        LOG.trace("Removing hidden chil procedure {} from offering {}", procedure, offering);
        this.hiddenChildProceduresForOfferings.removeWithKey(offering, procedure);
    }

    @Override
    public void setHiddenChildProceduresForOffering(final String offering, final Collection<String> procedures) {
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
    public void setSpatialFilteringProfileEnvelopeForOffering(String offering, SosEnvelope envelope) {
        LOG.trace("Setting Spatial Filtering Profile Envelope for Offering {} to {}", offering, envelope);
        this.spatialFilteringProfileEnvelopeForOfferings.put(offering, copyOf(envelope));
    }

    @Override
    public void updateSpatialFilteringProfileEnvelopeForOffering(String offering, Envelope envelope) {
        notNullOrEmpty(OFFERING, offering);
        notNull(ENVELOPE, envelope);
        if (hasSpatialFilteringProfileEnvelopeForOffering(offering)) {
            final SosEnvelope offeringEnvelope = this.spatialFilteringProfileEnvelopeForOfferings.get(offering);
            LOG.trace("Expanding Spatial Filtering Profile envelope {} for offering {} to include {}",
                    offeringEnvelope, offering, envelope);
            offeringEnvelope.expandToInclude(envelope);
        } else {
            setSpatialFilteringProfileEnvelopeForOffering(offering, new SosEnvelope(envelope, getDefaultEPSGCode()));
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
        this.featureOfInterestTypesForOfferings.add(offering, featureOfInterestType);
    }

    @Override
    public void removeFeatureOfInterestTypeForOffering(String offering, String featureOfInterestType) {
        notNullOrEmpty(OFFERING, offering);
        notNullOrEmpty(FEATURE_OF_INTEREST_TYPE, featureOfInterestType);
        LOG.trace("Removing observationType {} from offering {}", featureOfInterestType, offering);
        this.featureOfInterestTypesForOfferings.removeWithKey(offering, featureOfInterestType);
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
        this.allowedFeatureOfInterestTypeForOfferings.add(offering, allowedFeatureOfInterestType);
    }

    @Override
    public void addAllowedFeatureOfInterestTypesForOffering(String offering,
            Collection<String> allowedFeatureOfInterestTypes) {
        notNullOrEmpty(OFFERING, offering);
        noNullValues(ALLOWED_FEATURE_OF_INTEREST_TYPES, allowedFeatureOfInterestTypes);
        LOG.trace("Adding AllowedFeatureOfInterestTypes {} to Offering {}", allowedFeatureOfInterestTypes, offering);
        this.allowedFeatureOfInterestTypeForOfferings.addAll(offering, allowedFeatureOfInterestTypes);
    }

    @Override
    public void addSupportedLanguage(Locale language){
      notNull(SUPPORTED_LANGUAGE, language);
      LOG.trace("Adding Language {}", language);
      this.supportedLanguages.add(language);
    }

    @Override
    public void addSupportedLanguage(Collection<Locale> languages) {
        noNullValues(SUPPORTED_LANGUAGES, languages);
      for (final Locale language : languages) {
          addSupportedLanguage(language);
      }
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
        if (StringHelper.isNotEmpty(identifier) && StringHelper.isNotEmpty(humanReadableName)) {
            if (!featureOfInterestIdentifierForHumanReadableName.containsKey(humanReadableName)) {
                featureOfInterestIdentifierForHumanReadableName.put(humanReadableName, identifier);
            }
            if (!featureOfInterestHumanReadableNameForIdentifier.containsKey(identifier)) {
                featureOfInterestHumanReadableNameForIdentifier.put(identifier, humanReadableName);
            }
        }
    }

    @Override
    public void addObservablePropertyIdentifierHumanReadableName(String identifier, String humanReadableName) {
        if (StringHelper.isNotEmpty(identifier) && StringHelper.isNotEmpty(humanReadableName)) {
            if (!observablePropertyIdentifierForHumanReadableName.containsKey(humanReadableName)) {
                observablePropertyIdentifierForHumanReadableName.put(humanReadableName, identifier);
            }
            if (!observablePropertyHumanReadableNameForIdentifier.containsKey(humanReadableName)) {
                observablePropertyHumanReadableNameForIdentifier.put(identifier, humanReadableName);
            }
        }
    }

    @Override
    public void addProcedureIdentifierHumanReadableName(String identifier, String humanReadableName) {
        if (StringHelper.isNotEmpty(identifier) && StringHelper.isNotEmpty(humanReadableName)) {
            if (!procedureIdentifierForHumanReadableName.containsKey(humanReadableName)) {
                procedureIdentifierForHumanReadableName.put(humanReadableName, identifier);
            }
            if (!procedureHumanReadableNameForIdentifier.containsKey(humanReadableName)) {
                procedureHumanReadableNameForIdentifier.put(identifier, humanReadableName);
            }
        }
    }

    @Override
    public void addOfferingIdentifierHumanReadableName(String identifier, String humanReadableName) {
        if (StringHelper.isNotEmpty(identifier) && StringHelper.isNotEmpty(humanReadableName)) {
            if (!offeringIdentifierForHumanReadableName.containsKey(humanReadableName)) {
                offeringIdentifierForHumanReadableName.put(humanReadableName, identifier);
            }
            if (!offeringHumanReadableNameForIdentifier.containsKey(humanReadableName)) {
                offeringHumanReadableNameForIdentifier.put(identifier, humanReadableName);
            }
        }
    }

    @Override
    public void removeFeatureOfInterestIdentifierForHumanReadableName(String humanReadableName) {
        notNullOrEmpty(FEATURE_OF_INTEREST_NAME, humanReadableName);
        LOG.trace("Removing featuresOfInterest identifier for humanReadableName {}", humanReadableName);
        featureOfInterestIdentifierForHumanReadableName.remove(humanReadableName);
    }

	@Override
	public void removeFeatureOfInterestHumanReadableNameForIdentifier(
			String identifier) {
		notNullOrEmpty(FEATURE_OF_INTEREST, identifier);
	    LOG.trace("Removing featuresOfInterest human readable name for identifier {}", identifier);
        featureOfInterestHumanReadableNameForIdentifier.remove(identifier);
	}

	@Override
	public void removeObservablePropertyIdentifierForHumanReadableName(
			String humanReadableName) {
		notNullOrEmpty(OBSERVABLE_PROPERTY_NAME, humanReadableName);
	    LOG.trace("Removing featuresOfInterest identifier for humanReadableName {}", humanReadableName);
        observablePropertyIdentifierForHumanReadableName.remove(humanReadableName);
	}

	@Override
	public void removeObservablePropertyHumanReadableNameForIdentifier(
			String identifier) {
		notNullOrEmpty(OBSERVABLE_PROPERTY, identifier);
	    LOG.trace("Removing observableProperty human readable name for identifier {}", identifier);
        observablePropertyHumanReadableNameForIdentifier.remove(identifier);
	}

	@Override
	public void removeProcedureIdentifierForHumanReadableName(
			String humanReadableName) {
		notNullOrEmpty(PROCEDURE_NAME, humanReadableName);
	    LOG.trace("Removing procedure identifier for humanReadableName {}", humanReadableName);
        procedureIdentifierForHumanReadableName.remove(humanReadableName);
	}

	@Override
	public void removeProcedureHumanReadableNameForIdentifier(String identifier) {
		notNullOrEmpty(PROCEDURE, identifier);
	    LOG.trace("Removing procedure human readable name for identifier {}", identifier);
        procedureHumanReadableNameForIdentifier.remove(identifier);
	}

	@Override
	public void removeOfferingIdentifierForHumanReadableName(
			String humanReadableName) {
		notNullOrEmpty(OFFERING_NAME, humanReadableName);
	    LOG.trace("Removing offering identifier for humanReadableName {}", humanReadableName);
        offeringIdentifierForHumanReadableName.remove(humanReadableName);
	}

	@Override
	public void removeOfferingHumanReadableNameForIdentifier(String identifier) {
		notNullOrEmpty(OFFERING, identifier);
	    LOG.trace("Removing offering human readable name for identifier {}", identifier);
        offeringHumanReadableNameForIdentifier.remove(identifier);
	}

	@Override
	public void clearFeatureOfInterestIdentifierHumanReadableNameMaps() {
        featureOfInterestIdentifierForHumanReadableName.clear();
        featureOfInterestHumanReadableNameForIdentifier.clear();
	}

	@Override
	public void clearObservablePropertyIdentifierHumanReadableNameMaps() {
        observablePropertyIdentifierForHumanReadableName.clear();
        observablePropertyHumanReadableNameForIdentifier.clear();
	}

	@Override
	public void clearProcedureIdentifierHumanReadableNameMaps() {
        procedureIdentifierForHumanReadableName.clear();
        procedureHumanReadableNameForIdentifier.clear();
	}

	@Override
	public void clearOfferingIdentifierHumanReadableNameMaps() {
        offeringIdentifierForHumanReadableName.clear();
        offeringHumanReadableNameForIdentifier.clear();
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
        return copyOf(this.observablePropertiesForCompositePhenomenon.get(compositePhenomenon));
    }

    @Override
    public boolean isObservablePropertyOfCompositePhenomenon(String compositePhenomenon, String observableProperty) {
        return this.observablePropertiesForCompositePhenomenon.containsKey(compositePhenomenon) &&
               this.observablePropertiesForCompositePhenomenon.get(compositePhenomenon).contains(observableProperty);
    }

    @Override
    public Set<String> getCompositePhenomenonForObservableProperty(String observableProperty) {
        return copyOf(this.compositePhenomenonForObservableProperty.get(observableProperty));
    }

    @Override
    public boolean isCompositePhenomenonComponent(String observableProperty) {
        return this.compositePhenomenonForObservableProperty.containsKey(observableProperty) &&
               !this.compositePhenomenonForObservableProperty.get(observableProperty).isEmpty();
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
        this.compositePhenomenonsForProcedure.add(procedure, compositePhenomenon);
        addCompositePhenomenon(compositePhenomenon);
    }

    @Override
    public void addCompositePhenomenonForProcedure(String procedure,
                                                   Collection<String> compositePhenomenon) {
        notNullOrEmpty(PROCEDURE, procedure);
        noNullOrEmptyValues(COMPOSITE_PHENOMENON, compositePhenomenon);
        LOG.trace("Adding composite phenomenon {} to procedure {}", compositePhenomenon, procedure);
        this.compositePhenomenonsForProcedure.addAll(procedure, compositePhenomenon);
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
    public void addCompositePhenomenonForOffering(String offering,
                                                  String compositePhenomenon) {
        notNullOrEmpty(OFFERING, offering);
        notNullOrEmpty(COMPOSITE_PHENOMENON, compositePhenomenon);
        LOG.trace("Adding composite phenomenon {} to offering {}", offering);
        this.compositePhenomenonsForOffering.add(offering, compositePhenomenon);
        addCompositePhenomenon(compositePhenomenon);
    }

    @Override
    public void addCompositePhenomenonForOffering(String offering,
                                                  Collection<String> compositePhenomenon) {
        notNullOrEmpty(OFFERING, offering);
        noNullOrEmptyValues(COMPOSITE_PHENOMENON, compositePhenomenon);
        LOG.trace("Adding composite phenomenon {} to offering {}", offering);
        this.compositePhenomenonsForOffering.addAll(offering, compositePhenomenon);
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
        LOG.trace("Adding composite phenomenon {} to to observable property {}", compositePhenomenon, observableProperty);
        this.compositePhenomenonForObservableProperty.add(observableProperty, compositePhenomenon);
        addCompositePhenomenon(compositePhenomenon);
    }

    @Override
    public void addObservablePropertyForCompositePhenomenon(String compositePhenomenon,
                                                            String observableProperty) {
        notNullOrEmpty(COMPOSITE_PHENOMENON, compositePhenomenon);
        notNullOrEmpty(OBSERVABLE_PROPERTY, observableProperty);
        LOG.trace("Adding observable property {} to composite phenomenon {}", observableProperty, compositePhenomenon);
        this.observablePropertiesForCompositePhenomenon.add(compositePhenomenon, observableProperty);
        addCompositePhenomenon(compositePhenomenon);
    }

    @Override
    public void addObservablePropertiesForCompositePhenomenon(String compositePhenomenon,
                                                              Collection<String> observableProperty) {
        notNullOrEmpty(COMPOSITE_PHENOMENON, compositePhenomenon);
        noNullOrEmptyValues(OBSERVABLE_PROPERTY, observableProperty);
        LOG.trace("Adding observable properties {} to composite phenomenon {}", observableProperty, compositePhenomenon);
        this.observablePropertiesForCompositePhenomenon.addAll(compositePhenomenon, observableProperty);
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
        this.observablePropertiesForCompositePhenomenon.remove(compositePhenomenon);
    }

    @Override
    public void clearObservablePropertiesForCompositePhenomenon() {
        LOG.trace("Clearing observable properties for composite phenomenon");
        this.observablePropertiesForCompositePhenomenon.clear();
    }

    @Override
    public void clearCompositePhenomenonsForObservableProperty() {
        LOG.trace("Clearing composite phenomenon for observable properties");
        this.compositePhenomenonForObservableProperty.clear();
    }

    @Override
    public void clearCompositePhenomenonsForObservableProperty(String observableProperty) {
        LOG.trace("Clearing composite phenomenon for observable property {}", observableProperty);
        this.compositePhenomenonForObservableProperty.remove(observableProperty);
    }

    @Override
    public Set<String> getRequestableProcedureDescriptionFormat() {
        return this.requestableProcedureDescriptionFormats;
    }
    
    @Override
    public boolean hasRequestableProcedureDescriptionFormat(String format) {
        return getRequestableProcedureDescriptionFormat().contains(format);
    }

    @Override
    public void setRequestableProcedureDescriptionFormat(Collection<String> formats) {
        LOG.trace("Adding requestable procedureDescriptionFormat");
        getRequestableProcedureDescriptionFormat().addAll(formats);
    }
    
    @Override
    public Set<String> getOfferingsForProcedures(Set<String> procedures) {
        HashSet<String> offerings = Sets.newHashSet();
        if (procedures != null) {
            for (String procedure : procedures) {
                offerings.addAll(getOfferingsForProcedure(procedure));   
            }
        }
        return offerings;
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
        Set<String> procedures = getPublishedProcedures();
        // allowQueryingForInstancesOnly
        if (ProcedureRequestSettings.getInstance().isAllowQueryingForInstancesOnly()) {
            procedures = CollectionHelper.conjunctCollectionsToSet(procedures, getTypeInstanceProcedure(TypeInstance.INSTANCE));
        }
        // showOnlyAggregatedProcedures
        if (ProcedureRequestSettings.getInstance().isShowOnlyAggregatedProcedures()) {
            procedures = CollectionHelper.conjunctCollectionsToSet(procedures, getComponentAggregationProcedure(ComponentAggregation.AGGREGATION));
            
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
        
        return copyOf(typeOfProcedures.get(identifier));
    }

    @Override
    public boolean hasInstancesForProcedure(String identifier) {
        return typeOfProcedures.containsKey(identifier);
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
    public void addTypeOfProcedure(String type , String instance) {
        notNullOrEmpty(TYPE_PROCEDURE, type);
        notNullOrEmpty(PROCEDURE_INSTANCE, instance);
        LOG.trace("Adding instance '{}' to type '{}'", instance, type);
        if (hasInstancesForProcedure(type)) {
            typeOfProcedures.get(type).add(instance);
        } else {
            typeOfProcedures.put(type, Sets.newHashSet(instance));
        }
    }

    @Override
    public void addTypeOfProcedure(String type, Set<String> instances) {
        notNullOrEmpty(TYPE_PROCEDURE, type);
        noNullValues(PROCEDURE_INSTANCES, instances);
        LOG.trace("Adding instances {} to type '{}'", instances, type);
        if (hasInstancesForProcedure(type)) {
            typeOfProcedures.get(type).addAll(instances);
        } else {
            typeOfProcedures.put(type, instances);
        }
    }

    @Override
    public void removeTypeOfProcedure(String type) {
        notNullOrEmpty(TYPE_PROCEDURE, type);
        LOG.trace("Removing type '{}'", type);
        if (hasInstancesForProcedure(type)) {
            typeOfProcedures.remove(type);
        }
        // check for values
        removeValue(typeOfProcedures, type);
    }

    @Override
    public void removeTypeOfProcedure(String type, String instance) {
        notNullOrEmpty(TYPE_PROCEDURE, type);
        notNullOrEmpty(PROCEDURE_INSTANCE, instance);
        LOG.trace("Removing instance '{}' of type '{}'", type);
        if (hasInstancesForProcedure(type)) {
            typeOfProcedures.get(type).remove(instance);
        }
    }

    @Override
    public void clearTypeOfProcedure() {
       logClearing("Clearing type instance procedure map");
       typeOfProcedures.clear();
    }

    /**
     * Logs to trace: "Adding 'value' to 'type'".
     * 
     * @param type
     *            Add to
     * @param value
     *            Value to add
     */
    protected void logAdding(String type, String value) {
        LOG.trace("Adding '{}' to '{}'", value, type);
    }

    /**
     * Logs to trace: "Removing 'value' from 'type'".
     * 
     * @param type
     *            Remove from
     * @param value
     *            Value to remove
     */
    protected void logRemoving(String type, String value) {
        LOG.trace("Removing '{}' from '{}'", value, type);
    }

    /**
     * Logs to trace: "Clearing 'type'
     * 
     * @param type
     *            Type to clear
     */
    protected void logClearing(String type) {
        LOG.trace("Clearing '{}'", type);
    }


    @Override
    public void addProcedureDescriptionFormatsForProcedure(String procedure, Set<String> formats) {
        procedureProcedureDescriptionFormats.addAll(procedure, formats);
    }


    @Override
    public void removeProcedureDescriptionFormatsForProcedure(String procedure) {
        procedureProcedureDescriptionFormats.remove(procedure);
    }


    @Override
    public Set<String> getProcedureDescriptionFormatsForProcedure(String procedure) {
        return procedureProcedureDescriptionFormats.get(procedure);
    }


    @Override
    public Set<String> getPublishedFeatureOfInterest() {
        return copyOf(publishedFeatureOfInterest);
    }
    
    @Override
    public Set<String> getPublishedProcedures() {
        return copyOf(publishedProcedure);
    }
    
    @Override
    public Set<String> getPublishedOfferings() {
        return copyOf(publishedOffering);
    }
    
    @Override
    public Set<String> getPublishedObservableProperties() {
        return copyOf(publishedObservableProperty);
    }


    @Override
    public void addPublishedFeatureOfInterest(String featureOfInterest) {
        notNullOrEmpty(PUBLISHED_FEATURE_OF_INTEREST, featureOfInterest);
        LOG.trace("Adding published FeatureOfInterest {}", featureOfInterest);
        publishedFeatureOfInterest.add(featureOfInterest);
    }

    @Override
    public void addPublishedFeaturesOfInterest(Collection<String> featuresOfInterest) {
        noNullValues(PUBLISHED_FEATURES_OF_INTEREST, featuresOfInterest);
        for (final String featureOfInterest : featuresOfInterest) {
            addPublishedFeatureOfInterest(featureOfInterest);
        }
    }
    
    @Override
    public void setPublishedFeaturesOfInterest(final Collection<String> featuresOfInterest) {
        LOG.trace("Setting published FeaturesOfInterest");
        clearPublishedFeaturesOfInterest();
        addPublishedFeaturesOfInterest(featuresOfInterest);
    }

    @Override
    public void clearPublishedFeaturesOfInterest() {
        LOG.trace("Clearing published features of interest");
        publishedFeatureOfInterest.clear();
    }

    @Override
    public void removePublishedFeatureOfInterest(final String featureOfInterest) {
        notNullOrEmpty(PUBLISHED_FEATURE_OF_INTEREST, featureOfInterest);
        LOG.trace("Removing published FeatureOfInterest {}", featureOfInterest);
        publishedFeatureOfInterest.remove(featureOfInterest);
    }

    @Override
    public void removePublishedFeaturesOfInterest(final Collection<String> featuresOfInterest) {
        noNullValues(PUBLISHED_FEATURES_OF_INTEREST, featuresOfInterest);
        for (final String featureOfInterest : featuresOfInterest) {
            removePublishedFeatureOfInterest(featureOfInterest);
        }
    }

   @Override
   public void addPublishedProcedure(String procedure) {
       notNullOrEmpty(PUBLISHED_PROCEDURE, procedure);
       LOG.trace("Adding published procedure {}", procedure);
       publishedProcedure.add(procedure);
   }

   @Override
   public void addPublishedProcedures(Collection<String> procedures) {
       noNullValues(PUBLISHED_PROCEDURES, procedures);
       for (final String procedure : procedures) {
           addPublishedProcedure(procedure);
       }
   }
   
   @Override
   public void setPublishedProcedures(final Collection<String> procedures) {
       LOG.trace("Setting published procedure");
       clearPublishedProcedure();
       addPublishedProcedures(procedures);
   }

   @Override
   public void clearPublishedProcedure() {
       LOG.trace("Clearing published procedure");
       publishedProcedure.clear();
   }

   @Override
   public void removePublishedProcedure(final String procedure) {
       notNullOrEmpty(PUBLISHED_PROCEDURE, procedure);
       LOG.trace("Removing published procedure {}", procedure);
       publishedProcedure.remove(procedure);
   }

   @Override
   public void removePublishedProcedures(final Collection<String> procedures) {
       noNullValues(PUBLISHED_PROCEDURES, procedures);
       for (final String procedure : procedures) {
           removePublishedProcedure(procedure);
       }
   }

   @Override
   public void addPublishedOffering(String offering) {
       notNullOrEmpty(PUBLISHED_OFFERING, offering);
       LOG.trace("Adding published offering {}", offering);
       publishedOffering.add(offering);
   }

   @Override
   public void addPublishedOfferings(Collection<String> offerings) {
       noNullValues(PUBLISHED_OFFERINGS, offerings);
       for (final String offering : offerings) {
           addPublishedOffering(offering);
       }
   }
   
   @Override
   public void setPublishedOfferings(final Collection<String> offerings) {
       LOG.trace("Setting published offering");
       clearPublishedOffering();
       addPublishedOfferings(offerings);
   }

   @Override
   public void clearPublishedOffering() {
       LOG.trace("Clearing published offering");
       publishedOffering.clear();
   }

   @Override
   public void removePublishedOffering(final String offering) {
       notNullOrEmpty(PUBLISHED_OFFERING, offering);
       LOG.trace("Removing published offering {}", offering);
       publishedOffering.remove(offering);
   }

   @Override
   public void removePublishedOfferings(final Collection<String> offerings) {
       noNullValues(PUBLISHED_OFFERINGS, offerings);
       for (final String offering : offerings) {
           removePublishedOffering(offering);
       }
   }

   @Override
   public void addPublishedObservableProperty(String observableProperty) {
       notNullOrEmpty(PUBLISHED_OBSERVABLE_PROPERTY, observableProperty);
       LOG.trace("Adding published observableProperty {}", observableProperty);
       publishedObservableProperty.add(observableProperty);
   }

   @Override
   public void addPublishedObservableProperties(Collection<String> observableProperties) {
       noNullValues(PUBLISHED_OBSERVABLE_PROPERTIES, observableProperties);
       for (final String observableProperty : observableProperties) {
           addPublishedObservableProperty(observableProperty);
       }
   }
   
   @Override
   public void setPublishedObservableProperties(final Collection<String> observableProperties) {
       LOG.trace("Setting published observableProperties");
       clearPublishedFeaturesOfInterest();
       addPublishedObservableProperties(observableProperties);
   }

   @Override
   public void clearPublishedObservableProperty() {
       LOG.trace("Clearing published observableProperties");
       publishedObservableProperty.clear();
   }

   @Override
   public void removePublishedObservableProperty(final String observableProperty) {
       notNullOrEmpty(PUBLISHED_OBSERVABLE_PROPERTY, observableProperty);
       LOG.trace("Removing published observableProperty {}", observableProperty);
       publishedObservableProperty.remove(observableProperty);
   }

   @Override
   public void removePublishedObservableProperties(final Collection<String> observableProperties) {
       noNullValues(PUBLISHED_OBSERVABLE_PROPERTIES, observableProperties);
       for (final String observableProperty : observableProperties) {
           removePublishedObservableProperty(observableProperty);
       }
   }
}


