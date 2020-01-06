/*
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

import static java.util.stream.Collectors.toSet;

import java.util.Collections;
import java.util.Locale;
import java.util.Optional;
import java.util.Set;

import org.joda.time.DateTime;

import org.n52.iceland.cache.ContentCache;
import org.n52.janmayen.function.Predicates;
import org.n52.janmayen.i18n.LocalizedString;
import org.n52.janmayen.i18n.MultilingualString;

/**
 * This encapsulates relationships between the different metadata components of this SOS (e.g. fois 4 offerings). The
 * intention is to achieve better performance in getting this information from this cache than to query always the DB
 * for this information. (Usually the informations stored here do not often change)
 *
 * @author <a href="mailto:e.h.juerrens@52north.org">Eike Hinderk J&uuml;rrens</a>
 * @author <a href="mailto:c.autermann@52north.org">Christian Autermann</a>
 *
 * @since 4.0.0
 */
public interface SosContentCache
        extends ContentCache,
                TemporalCache,
                SpatialCache,
                CompositePhenomenonCache {
    /**
     * @return the last cache update time
     */
    DateTime getLastUpdateTime();

    /**
     * @param time set the last cache update time
     */
    void setLastUpdateTime(DateTime time);

    /**
     * @return the maximal phenomenon time for all observations
     */
    DateTime getMaxPhenomenonTime();

    /**
     * Returns the allowed observation types for the specified offering.
     *
     * @param offering the offering
     *
     * @return the allowed observation types
     */
    Set<String> getAllowedObservationTypesForOffering(String offering);

    /**
     * Returns the all observation types for the specified offering.
     *
     * @param offering the offering
     *
     * @return the all observation types
     */
    Set<String> getAllObservationTypesForOffering(String offering);

    /**
     * Returns the allowed featureOfInterest types for the specified offering.
     *
     * @param offering the offering
     *
     * @return the allowed featureOfInterest types
     */
    Set<String> getAllowedFeatureOfInterestTypesForOffering(String offering);

    /**
     * @return all FeatureOfInterest types
     */
    Set<String> getFeatureOfInterestTypes();

    /**
     * Checks whether the specified featureOfInterest type exists.
     *
     * @param featureOfInterestType the observation type
     *
     * @return {@code true} if it exists
     */
    default boolean hasFeatureOfInterestType(String featureOfInterestType) {
        return getFeatureOfInterestTypes().contains(featureOfInterestType);
    }

    /**
     * Get the featureOfInterest types associated with the specified offering.
     *
     * @param offering the offering
     *
     * @return the featureOfInterest types
     */
    Set<String> getFeatureOfInterestTypesForOffering(String offering);

    /**
     * Checks whether or not the specified feature is contained in this cache.
     *
     * @param featureOfInterest the feature
     *
     * @return {@code true} if it is contained
     */
    boolean hasFeatureOfInterest(String featureOfInterest);

    /**
     * Returns all FeaturesOfInterest for the specified offering.
     *
     * @param offering the offering
     *
     * @return the features associated with the offering
     */
    Set<String> getFeaturesOfInterestForOffering(String offering);

    /**
     * Returns all offerings for the specified featureOfInterest.
     *
     * @param featureOfInterest the featureOfInterest
     *
     * @return the offerings associated with the featureOfInterest
     */
    Set<String> getOfferingsForFeatureOfInterest(String featureOfInterest);

    /**
     * Returns all FeaturesOfInterest for the specified SosResultTemplate.
     *
     * @param resultTemplate the resultTemplate
     *
     * @return the features associated with the resulte SosResultTemplate
     */
    Set<String> getFeaturesOfInterestForResultTemplate(String resultTemplate);

    /**
     * @return all features that are associated with a SosResultTemplate
     */
    Set<String> getFeaturesOfInterestWithResultTemplate();

    /**
     * @return all ObservableProperties
     */
    Set<String> getObservableProperties();

    /**
     * Checks whether the specified ObservableProperty is known.
     *
     * @param observableProperty the observable property
     *
     * @return {@code true} if it is contained
     */
    default boolean hasObservableProperty(String observableProperty) {
        return getObservableProperties().contains(observableProperty);
    }

    /**
     * Get the observable properties associated with the specified offering.
     *
     * @param offering the offering
     *
     * @return the observable properties
     */
    Set<String> getObservablePropertiesForOffering(String offering);

    /**
     * Get the observable properties associated with the specified procedure.
     *
     * @param procedure the offering
     *
     * @return the observable properties
     */
    Set<String> getObservablePropertiesForProcedure(String procedure);

    /**
     * Checks if the specified procedure has the specified observable property.
     *
     * @param procedure          the procedure
     * @param observableProperty the observable property
     *
     * @return if there exists an association
     */
    boolean hasObservablePropertyForProcedure(String procedure, String observableProperty);

    /**
     * @return all observation types
     */
    Set<String> getObservationTypes();

    /**
     * Checks whether the specified observation type exists.
     *
     * @param observationType the observation type
     *
     * @return {@code true} if it exists
     */
    default boolean hasObservationType(String observationType) {
        return getObservationTypes().contains(observationType);
    }

    /**
     * Get the observation types associated with the specified offering.
     *
     * @param offering the offering
     *
     * @return the observation types
     */
    Set<String> getObservationTypesForOffering(String offering);

    /**
     * Get the observable properties associated with the specified result template.
     *
     * @param resultTemplate the result template
     *
     * @return the observable properties
     */
    Set<String> getObservablePropertiesForResultTemplate(String resultTemplate);

    /**
     * @return all observable properties that are associated with a result template
     */
    Set<String> getObservablePropertiesWithResultTemplate();

    /**
     * @return all offerings
     */
    Set<String> getOfferings();

    /**
     * Checks whether the specified offering exists.
     *
     * @param offering the offering
     *
     * @return {@code true} if it exists
     */
    boolean hasOffering(String offering);

    /**
     * Get the offerings associated with the specified observable property.
     *
     * @param observableProperty the observable property
     *
     * @return the offerings
     */
    Set<String> getOfferingsForObservableProperty(String observableProperty);

    /**
     * Get the offerings associated with the specified procedure.
     *
     * @param procedure the procedure
     *
     * @return the offerings
     */
    Set<String> getOfferingsForProcedure(String procedure);

    /**
     * Get the offerings associated with the specified procedures.
     *
     * @param procedures the procedures
     *
     * @return the offerings
     */
    default Set<String> getOfferingsForProcedures(Set<String> procedures) {
        return Optional.ofNullable(procedures).orElseGet(Collections::emptySet).stream()
                .map(this::getOfferingsForProcedure).flatMap(Set::stream).collect(toSet());
    }

    /**
     * @return all offerings that are associated with a result template
     */
    Set<String> getOfferingsWithResultTemplate();

    /**
     * @return procedures
     */
    Set<String> getProcedures();

    /**
     * Checks whether the specified procedure exists.
     *
     * @param procedure the procedure
     *
     * @return {@code true} if it exists
     */
    boolean hasProcedure(String procedure);

    /**
     * Get the procedures associated with the specified feature of interest.
     *
     * @param featureOfInterest the feature of interest
     *
     * @return the procedures
     */
    Set<String> getProceduresForFeatureOfInterest(String featureOfInterest);

    /**
     * Get the procedures associated with the specified observable property.
     *
     * @param observableProperty the observable property
     *
     * @return the procedures
     */
    Set<String> getProceduresForObservableProperty(String observableProperty);

    /**
     * Get the procedures associated with the specified offering.
     *
     * @param offering the offering
     *
     * @return the procedures
     */
    Set<String> getProceduresForOffering(String offering);

    /**
     * Get the hidden child procedures associated with the specified offering.
     *
     * @param offering the offering
     *
     * @return the hidden child procedures
     */
    Set<String> getHiddenChildProceduresForOffering(String offering);

    /**
     * @return all related features
     */
    Set<String> getRelatedFeatures();

    /**
     * Checks whether the specified related feature exists.
     *
     * @param relatedFeature the related feature
     *
     * @return {@code true} if it exists
     */
    default boolean hasRelatedFeature(String relatedFeature) {
        return getRelatedFeatures().contains(relatedFeature);
    }

    /**
     * Get the related features associated with the specified offering.
     *
     * @param offering the offering
     *
     * @return the related features
     */
    Set<String> getRelatedFeaturesForOffering(String offering);

    /**
     * @return all result templates
     */
    Set<String> getResultTemplates();

    /**
     * Checks whether the specified result template exists.
     *
     * @param resultTemplate the result template
     *
     * @return {@code true} if it exists
     */
    boolean hasResultTemplate(String resultTemplate);

    /**
     * Get the result templates associated with the specified offering.
     *
     * @param offering the offering
     *
     * @return the result templates
     */
    Set<String> getResultTemplatesForOffering(String offering);

    /**
     * Get the roles associated with the specified related feature.
     *
     * @param relatedFeature the related feature
     *
     * @return the roles
     */
    Set<String> getRolesForRelatedFeature(String relatedFeature);

    /**
     * Gets the name of the specified offering.
     *
     * @param offering the offering
     *
     * @return the name of the offering or null
     */
    String getNameForOffering(String offering);

    /**
     * Get the name in the specified language of the specified offering.
     *
     * @param offering the offering
     * @param i18n     the language
     *
     * @return the name of the offering or null
     */
    LocalizedString getI18nNameForOffering(String offering, Locale i18n);

    /**
     * Get all names of the specified offering.
     *
     * @param offering the offering
     *
     * @return the names of the offering or null
     */
    MultilingualString getI18nNamesForOffering(String offering);

    /**
     * Check if there are I18N names for the specified offering and language.
     *
     * @param offering the offering
     * @param i18n     the language
     *
     * @return <code>true</code>, if there are I18N names for the
     */
    boolean hasI18NNamesForOffering(String offering, Locale i18n);

    /**
     * Get the description in the specified language of the specified offering.
     *
     * @param offering the offering
     * @param i18n     the language
     *
     * @return the description of the offering or null
     */
    LocalizedString getI18nDescriptionForOffering(String offering, Locale i18n);

    /**
     * Check if there is a I18N description for the specified offering and language.
     *
     * @param offering the offering
     * @param i18n     the language
     *
     * @return <code>true</code>, if there are I18N names for the
     */
    boolean hasI18NDescriptionForOffering(String offering, Locale i18n);

    /**
     * Get all descriptions of the specified offering.
     *
     * @param offering the offering
     *
     * @return the names of the offering or null
     */
    MultilingualString getI18nDescriptionsForOffering(String offering);

    /**
     * @return all features of interest
     */
    Set<String> getFeaturesOfInterest();

    /**
     * Returns collection containing parent features for the passed feature, optionally navigating the full hierarchy
     * and including itself.
     *
     * @param featureOfInterest the feature id to find parents for
     * @param fullHierarchy     whether or not to navigate the full feature hierarchy
     * @param includeSelf       whether or not to include the passed feature id in the result
     *
     * @return a set containing the passed features id's parents (and optionally itself)
     */
    Set<String> getParentFeatures(String featureOfInterest, boolean fullHierarchy, boolean includeSelf);

    /**
     * Returns collection containing parent features for the passed features, optionally navigating the full hierarchy
     * and including itself.
     *
     * @param featuresOfInterest the feature id's to find parents for
     * @param fullHierarchy      whether or not to traverse the full feature hierarchy in one direction starting from
     * <tt>featureOfInterest</tt>
     * @param includeSelves      whether or not to include the passed feature id's in the result
     *
     * @return a set containing the passed procedure id's parents (and optionally itself)
     */
    Set<String> getParentFeatures(Set<String> featuresOfInterest, boolean fullHierarchy, boolean includeSelves);

    /**
     * Returns collection containing child features for the passed feature, optionally navigating the full hierarchy and
     * including itself.
     *
     * @param featureOfInterest feature id to find children for
     * @param fullHierarchy     whether or not to traverse the full feature hierarchy in one direction starting from
     * <tt>featureOfInterest</tt>
     * @param includeSelf       whether or not to include the passed feature id in the result
     *
     * @return Collection containing the passed feature id's children (and optionally itself)
     */
    Set<String> getChildFeatures(String featureOfInterest, boolean fullHierarchy, boolean includeSelf);

    /**
     * Returns collection containing parent procedures for the passed procedure, optionally navigating the full
     * hierarchy and including itself.
     *
     * @param procedure     the procedure id to find parents for
     * @param fullHierarchy whether or not to traverse the full procedure hierarchy in one direction starting from
     * <tt>procedure</tt>
     * @param includeSelf   whether or not to include the passed procedure id in the result
     *
     * @return a set containing the passed procedure id's parents (and optionally itself)
     */
    Set<String> getParentProcedures(String procedure, boolean fullHierarchy, boolean includeSelf);

    /**
     * Returns collection containing parent procedures for the passed procedures, optionally navigating the full
     * hierarchy and including itself.
     *
     * @param procedures    the procedure id's to find parents for
     * @param fullHierarchy whether or not to traverse the full procedure hierarchy in one direction starting from
     * <tt>procedure</tt>
     * @param includeSelves whether or not to include the passed procedure id in the result
     *
     * @return a set containing the passed procedure id's parents (and optionally itself)
     */
    Set<String> getParentProcedures(Set<String> procedures, boolean fullHierarchy, boolean includeSelves);

    /**
     * Returns collection containing child procedures for the passed procedures, optionally navigating the full
     * hierarchy and including itself.
     *
     * @param procedure     procedure id to find children for
     * @param fullHierarchy whether or not to navigate the full procedure hierarchy
     * @param includeSelf   whether or not to include the passed procedure id in the result
     *
     * @return Collection containing the passed procedure id's children (and optionally itself)
     */
    Set<String> getChildProcedures(String procedure, boolean fullHierarchy, boolean includeSelf);

    /**
     * Returns collection containing child procedures for the passed procedures, optionally navigating the full
     * hierarchy and including itself.
     *
     * @param procedure     procedure ids to find children for
     * @param fullHierarchy whether or not to navigate the full procedure hierarchy
     * @param includeSelves whether or not to include the passed procedure ids in the result
     *
     * @return Collection containing the passed procedure ids' children (and optionally themselves)
     */
    Set<String> getChildProcedures(Set<String> procedure, boolean fullHierarchy, boolean includeSelves);

    /**
     * Returns collection containing parent offerings for the passed offering, optionally navigating the full hierarchy
     * and including itself.
     *
     * @param offering      the offering id to find parents for
     * @param fullHierarchy whether or not to traverse the full offering hierarchy in one direction starting from
     * <tt>offering</tt>
     * @param includeSelf   whether or not to include the passed offering id in the result
     *
     * @return a set containing the passed offering id's parents (and optionally itself)
     */
    Set<String> getParentOfferings(String offering, boolean fullHierarchy, boolean includeSelf);

    /**
     * Returns collection containing parent offerings for the passed offerings, optionally navigating the full hierarchy
     * and including itself.
     *
     * @param offerings     the offering id's to find parents for
     * @param fullHierarchy whether or not to traverse the full offering hierarchy in one direction starting from
     * <tt>offering</tt>
     * @param includeSelves whether or not to include the passed offering id in the result
     *
     * @return a set containing the passed offering id's parents (and optionally itself)
     */
    Set<String> getParentOfferings(Set<String> offerings, boolean fullHierarchy, boolean includeSelves);

    /**
     * Returns collection containing child offerings for the passed offerings, optionally navigating the full hierarchy
     * and including itself.
     *
     * @param offering      offering id to find children for
     * @param fullHierarchy whether or not to navigate the full offering hierarchy
     * @param includeSelf   whether or not to include the passed offering id in the result
     *
     * @return Collection containing the passed offering id's children (and optionally itself)
     */
    Set<String> getChildOfferings(String offering, boolean fullHierarchy, boolean includeSelf);

    /**
     * Returns collection containing child offerings for the passed offerings, optionally navigating the full hierarchy
     * and including itself.
     *
     * @param offering      offering ids to find children for
     * @param fullHierarchy whether or not to navigate the full offering hierarchy
     * @param includeSelves whether or not to include the passed offering ids in the result
     *
     * @return Collection containing the passed offering ids' children (and optionally themselves)
     */
    Set<String> getChildOfferings(Set<String> offering, boolean fullHierarchy, boolean includeSelves);

    /**
     * Returns <code>true</code> if the passed offering has parent offerings.
     *
     * @param offering offering id to check for parents
     *
     * @return <code>true</code> if the passed offering has parent offerings.
     */
    boolean hasParentOfferings(String offering);

    /**
     * Checks whether the specified related feature has been used as sampling feature
     *
     * @param relatedFeatureIdentifier the relatedFeature identifier
     *
     * @return <tt>true</tt>, if the relatedFeature is related to any feature which is part of an observation.
     */
    default boolean isRelatedFeatureSampled(String relatedFeatureIdentifier) {
        return Optional.ofNullable(relatedFeatureIdentifier)
                .filter(Predicates.not(String::isEmpty))
                .filter(getRelatedFeatures()::contains)
                .filter(id -> !getChildFeatures(id, true, false).isEmpty())
                .isPresent();
    }

    /**
     * Get the supported languages
     *
     * @return Supported languages
     */
    Set<Locale> getSupportedLanguages();

    /**
     * Has the service supported languages
     *
     * @return <code>true</code>, if there are supported languages
     */
    boolean hasSupportedLanguage();

    /**
     * Is the specific language supported
     *
     * @param language Language to check
     *
     * @return <code>true</code>, if the specific lanugage is supported
     */
    boolean isLanguageSupported(Locale language);

    /**
     * Get supported requestable procedure description format
     *
     * @return Supported requestable procedure description format
     */
    Set<String> getRequestableProcedureDescriptionFormat();

    /**
     * Is the specific requestable procedure description format supported
     *
     * @param format format to check
     *
     * @return <code>true</code>, if the specific format is supported
     */
    default boolean hasRequestableProcedureDescriptionFormat(String format) {
        return getRequestableProcedureDescriptionFormat().contains(format);
    }

    String getFeatureOfInterestIdentifierForHumanReadableName(String humanReadableName);

    String getFeatureOfInterestHumanReadableNameForIdentifier(String identifier);

    String getObservablePropertyIdentifierForHumanReadableName(String humanReadableName);

    String getObservablePropertyHumanReadableNameForIdentifier(String identifier);

    String getProcedureIdentifierForHumanReadableName(String humanReadableName);

    String getProcedureHumanReadableNameForIdentifier(String identifier);

    String getOfferingIdentifierForHumanReadableName(String humanReadableName);

    String getOfferingHumanReadableNameForIdentifier(String identifier);

    /**
     * Get procedures usable for transactional insert observation operations (InsertObservation, InsertResultTemplate).
     *
     * @return the procedures
     */
    Set<String> getTransactionalObservationProcedures();

    /**
     * Checks whether the specified procedure exists for transactional insert observation operations (InsertObservation,
     * InsertResultTemplate).
     *
     * @param procedureID the procedure
     *
     * @return {@code true} if it exists
     */
    default boolean hasTransactionalObservationProcedure(String procedureID) {
        return getTransactionalObservationProcedures().contains(procedureID);
    }

    /**
     * Get procedures usable for querying.
     *
     * @return the procedures
     */
    Set<String> getQueryableProcedures(boolean instances, boolean aggregates);

    /**
     * Checks whether the specified procedure exists for querying.
     *
     * @param procedureID the procedure
     *
     * @return {@code true} if it exists
     */
    default boolean hasQueryableProcedure(String procedureID, boolean instances, boolean aggregates) {
        return getQueryableProcedures(instances, aggregates).contains(procedureID);
    }

    Set<String> getTypeInstanceProcedure(TypeInstance typeInstance);

    Set<String> getComponentAggregationProcedure(ComponentAggregation componentAggregation);

    Set<String> getInstancesForProcedure(String identifier);

    boolean hasInstancesForProcedure(String identifier);

    Set<String> getProcedureDescriptionFormatsForProcedure(String procedure);

    Set<String> getPublishedFeatureOfInterest();

    Set<String> getPublishedProcedures();

    Set<String> getPublishedOfferings();

    Set<String> getPublishedObservableProperties();

    enum TypeInstance {
        TYPE,
        INSTANCE;
    }

    enum ComponentAggregation {
        COMPONENT,
        AGGREGATION;
    }

}
