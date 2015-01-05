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

import java.io.Serializable;
import java.util.Locale;
import java.util.Set;

import org.joda.time.DateTime;
import org.n52.sos.i18n.LocalizedString;
import org.n52.sos.i18n.MultilingualString;
import org.n52.sos.ogc.sos.SosEnvelope;

/**
 * This encapsulates relationships between the different metadata components of
 * this SOS (e.g. fois 4 offerings). The intention is to achieve better
 * performance in getting this information from this cache than to query always
 * the DB for this information. (Usually the informations stored here do not
 * often change)
 *
 * @author <a href="mailto:e.h.juerrens@52north.org">Eike Hinderk
 *         J&uuml;rrens</a>
 * @author Christian Autermann <c.autermann@52north.org>
 *
 * @since 4.0.0
 */
public interface ContentCache extends Serializable {
    /**
     * @return the maximal phenomenon time for all observations
     */
    DateTime getMaxPhenomenonTime();

    /**
     * @return if the maximal phenomenon time is set
     */
    boolean hasMaxPhenomenonTime();

    /**
     * Returns the maximal phenomenon time for the specified offering.
     *
     * @param offering
     *            the offering identifier
     *
     * @return the maximal phenomenon time for or null if it is not set
     */
    DateTime getMaxPhenomenonTimeForOffering(String offering);

    /**
     * Returns the whether or not the maximal phenomenon time for the specified
     * offering is set.
     *
     * @param offering
     *            the offering identifier
     *
     * @return if the maximal phenomenon time is set
     */
    boolean hasMaxPhenomenonTimeForOffering(String offering);

    /**
     * Returns the maximal phenomenon time period for the specified procedure.
     *
     * @param procedure
     *            the procedure identifier
     *
     * @return the maximal phenomenon time for the specified procedure or null
     *         if it is not set
     */
    DateTime getMaxPhenomenonTimeForProcedure(String procedure);

    /**
     * Returns the whether or not the maximal phenomenon time for the specified
     * procedure is set.
     *
     * @param procedure
     *            the procedure identifier
     *
     * @return if the maximal phenomenon time is set
     */
    boolean hasMaxPhenomenonTimeForProcedure(String procedure);

    /**
     * @return the minimal phenomenon time for all observations
     */
    DateTime getMinPhenomenonTime();

    /**
     * @return if the minimal phenomenon time is set
     */
    boolean hasMinPhenomenonTime();

    /**
     * Returns the minimal phenomenon time for the specified offering.
     *
     * @param offering
     *            the offering identifier
     *
     * @return the minimal phenomenon time for or null if it is not set
     */
    DateTime getMinPhenomenonTimeForOffering(String offering);

    /**
     * Returns the whether or not the minimal phenomenon time for the specified
     * offering is set.
     *
     * @param offering
     *            the offering identifier
     *
     * @return if the minimal phenomenon time is set
     */
    boolean hasMinPhenomenonTimeForOffering(String offering);

    /**
     * Returns the minimal phenomenon time period for the specified procedure.
     *
     * @param procedure
     *            the procedure identifier
     *
     * @return the minimal phenomenon time for the specified procedure or null
     *         if it is not set
     */
    DateTime getMinPhenomenonTimeForProcedure(String procedure);

    /**
     * Returns the whether or not the minimal phenomenon time for the specified
     * procedure is set.
     *
     * @param procedure
     *            the procedure identifier
     *
     * @return if the minimal phenomenon time is set
     */
    boolean hasMinPhenomenonTimeForProcedure(String procedure);

    /**
     * @return the maximal result time for all observations
     */
    DateTime getMaxResultTime();

    /**
     * @return if the maximal result time is set
     */
    boolean hasMaxResultTime();

    /**
     * Returns the maximal result time for the specified offering.
     *
     * @param offering
     *            the offering identifier
     *
     * @return the maximal result time for or null if it is not set
     */
    DateTime getMaxResultTimeForOffering(String offering);

    /**
     * Returns the whether or not the maximal result time for the specified
     * offering is set.
     *
     * @param offering
     *            the offering identifier
     *
     * @return if the maximal result time is set
     */
    boolean hasMaxResultTimeForOffering(String offering);

    /**
     * @return the minimal result time for all observations
     */
    DateTime getMinResultTime();

    /**
     * @return if the minimal result time is set
     */
    boolean hasMinResultTime();

    /**
     * Returns the minimal result time for the specified offering.
     *
     * @param offering
     *            the offering identifier
     *
     * @return the minimal result time for or null if it is not set
     */
    DateTime getMinResultTimeForOffering(String offering);

    /**
     * Returns the whether or not the minimal result time for the specified
     * offering is set.
     *
     * @param offering
     *            the offering identifier
     *
     * @return if the minimal result time is set
     */
    boolean hasMinResultTimeForOffering(String offering);

    /**
     * @return the default EPSG code
     */
    int getDefaultEPSGCode();

    /**
     * Returns the allowed observation types for the specified offering.
     *
     * @param offering
     *            the offering
     *
     * @return the allowed observation types
     */
    Set<String> getAllowedObservationTypesForOffering(String offering);

    /**
     * Returns the allowed featureOfInterest types for the specified offering.
     *
     * @param offering
     *            the offering
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
     * @param featureOfInterestType
     *            the observation type
     *
     * @return {@code true} if it exists
     */
    boolean hasFeatureOfInterestType(String featureOfInterestType);

    /**
     * Get the featureOfInterest types associated with the specified offering.
     *
     * @param offering
     *            the offering
     *
     * @return the featureOfInterest types
     */
    Set<String> getFeatureOfInterestTypesForOffering(String offering);

    /**
     * Checks whether or not the specified feature is contained in this cache.
     *
     * @param featureOfInterest
     *            the feature
     *
     * @return {@code true} if it is contained
     */
    boolean hasFeatureOfInterest(String featureOfInterest);

    /**
     * Returns all FeaturesOfInterest for the specified offering.
     *
     * @param offering
     *            the offering
     *
     * @return the features associated with the offering
     */
    Set<String> getFeaturesOfInterestForOffering(String offering);

    /**
     * Returns all FeaturesOfInterest for the specified SosResultTemplate.
     *
     * @param resultTemplate
     *            the resultTemplate
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
     * @param observableProperty
     *            the observable property
     *
     * @return {@code true} if it is contained
     */
    boolean hasObservableProperty(String observableProperty);

    /**
     * Get the observable properties associated with the specified offering.
     *
     * @param offering
     *            the offering
     *
     * @return the observable properties
     */
    Set<String> getObservablePropertiesForOffering(String offering);

    /**
     * Get the observable properties associated with the specified procedure.
     *
     * @param procedure
     *            the offering
     *
     * @return the observable properties
     */
    Set<String> getObservablePropertiesForProcedure(String procedure);

    /**
     * @return all observation types
     */
    Set<String> getObservationTypes();

    /**
     * Checks whether the specified observation type exists.
     *
     * @param observationType
     *            the observation type
     *
     * @return {@code true} if it exists
     */
    boolean hasObservationType(String observationType);

    /**
     * Get the observation types associated with the specified offering.
     *
     * @param offering
     *            the offering
     *
     * @return the observation types
     */
    Set<String> getObservationTypesForOffering(String offering);

    /**
     * Get the observable properties associated with the specified result
     * template.
     *
     * @param resultTemplate
     *            the result template
     *
     * @return the observable properties
     */
    Set<String> getObservablePropertiesForResultTemplate(String resultTemplate);

    /**
     * @return all observable properties that are associated with a result
     *         template
     */
    Set<String> getObservablePropertiesWithResultTemplate();

    /**
     * @return all offerings
     */
    Set<String> getOfferings();

    /**
     * Checks whether the specified offering exists.
     *
     * @param offering
     *            the offering
     *
     * @return {@code true} if it exists
     */
    boolean hasOffering(String offering);

    /**
     * Get the offerings associated with the specified observable property.
     *
     * @param observableProperty
     *            the observable property
     *
     * @return the offerings
     */
    Set<String> getOfferingsForObservableProperty(String observableProperty);

    /**
     * Get the offerings associated with the specified procedure.
     *
     * @param procedure
     *            the procedure
     *
     * @return the offerings
     */
    Set<String> getOfferingsForProcedure(String procedure);

    /**
     * @return all offerings that are associated with a result template
     */
    Set<String> getOfferingsWithResultTemplate();

    /**
     * @return all procedures
     */
    Set<String> getProcedures();

    /**
     * Checks whether the specified procedure exists.
     *
     * @param procedure
     *            the procedure
     *
     * @return {@code true} if it exists
     */
    boolean hasProcedure(String procedure);

    /**
     * Get the procedures associated with the specified feature of interest.
     *
     * @param featureOfInterest
     *            the feature of interest
     *
     * @return the procedures
     */
    Set<String> getProceduresForFeatureOfInterest(String featureOfInterest);

    /**
     * Get the procedures associated with the specified observable property.
     *
     * @param observableProperty
     *            the observable property
     *
     * @return the procedures
     */
    Set<String> getProceduresForObservableProperty(String observableProperty);

    /**
     * Get the procedures associated with the specified offering.
     *
     * @param offering
     *            the offering
     *
     * @return the procedures
     */
    Set<String> getProceduresForOffering(String offering);

    /**
     * Get the hidden child procedures associated with the specified offering.
     *
     * @param offering
     *            the offering
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
     * @param relatedFeature
     *            the related feature
     *
     * @return {@code true} if it exists
     */
    boolean hasRelatedFeature(String relatedFeature);

    /**
     * Get the related features associated with the specified offering.
     *
     * @param offering
     *            the offering
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
     * @param resultTemplate
     *            the result template
     *
     * @return {@code true} if it exists
     */
    boolean hasResultTemplate(String resultTemplate);

    /**
     * Get the result templates associated with the specified offering.
     *
     * @param offering
     *            the offering
     *
     * @return the result templates
     */
    Set<String> getResultTemplatesForOffering(String offering);

    /**
     * Get the roles associated with the specified related feature.
     *
     * @param relatedFeature
     *            the related feature
     *
     * @return the roles
     */
    Set<String> getRolesForRelatedFeature(String relatedFeature);

    /**
     * Get the envelope associated with the specified offering.
     *
     * @param offering
     *            the offering
     *
     * @return the envelope
     */
    SosEnvelope getEnvelopeForOffering(String offering);

    /**
     * Get the Spatial Filtering Profile envelope associated with the specified
     * offering.
     *
     * @param offering
     *            the offering
     *
     * @return the envelope
     */
    SosEnvelope getSpatialFilteringProfileEnvelopeForOffering(String offering);

    /**
     * Checks whether the specified offering has a envelope.
     *
     * @param offering
     *            the offering
     *
     * @return {@code true} if it has a envelope
     */
    boolean hasEnvelopeForOffering(String offering);

    /**
     * Checks whether the specified offering has a Spatial Filtering Profile
     * envelope.
     *
     * @param offering
     *            the offering
     *
     * @return {@code true} if it has a envelope
     */
    boolean hasSpatialFilteringProfileEnvelopeForOffering(String offering);

    /**
     * @return the global spatial envelope (never null)
     */
    SosEnvelope getGlobalEnvelope();

    /**
     * @return whether the global spatial envelope is set or not
     */
    boolean hasGlobalEnvelope();

    /**
     * Gets the name of the specified offering.
     *
     * @param offering
     *            the offering
     *
     * @return the name of the offering or null
     */
    String getNameForOffering(String offering);

    /**
     * Get the name in the specified language of the specified offering.
     *
     * @param offering
     *            the offering
     * @param i18n
     *            the language
     * @return the name of the offering or null
     */
    LocalizedString getI18nNameForOffering(String offering, Locale i18n);

    /**
     * Get all names of the specified offering.
     *
     * @param offering
     *            the offering
     * @return the names of the offering or null
     */
    MultilingualString getI18nNamesForOffering(String offering);

    /**
     * Check if there are I18N names for the specified offering and language.
     *
     * @param offering
     *            the offering
     * @param i18n
     *            the language
     * @return <code>true</code>, if there are I18N names for the
     */
    boolean hasI18NNamesForOffering(String offering, Locale i18n);

    /**
     * Get the description in the specified language of the specified offering.
     *
     * @param offering
     *            the offering
     * @param i18n
     *            the language
     * @return the description of the offering or null
     */
    LocalizedString getI18nDescriptionForOffering(String offering, Locale i18n);

    /**
     * Check if there is a I18N description for the specified offering and language.
     *
     * @param offering
     *            the offering
     * @param i18n
     *            the language
     * @return <code>true</code>, if there are I18N names for the
     */
    boolean hasI18NDescriptionForOffering(String offering, Locale i18n);

    /**
     * Get all descriptions of the specified offering.
     *
     * @param offering
     *            the offering
     * @return the names of the offering or null
     */
    MultilingualString getI18nDescriptionsForOffering(String offering);

    /**
     * Get the composite phenomenons associated with the specified offering.
     *
     * @param offering
     *            the offering
     *
     * @return the composite phenomenons
     */
    Set<String> getCompositePhenomenonsForOffering(String offering);

    /**
     * @return all features of interest
     */
    Set<String> getFeaturesOfInterest();

    /**
     * Get the observable properties associated with the specified procedure.
     *
     * @param compositePhenomenon
     *            the composite phenomenon
     *
     * @return the observable properties
     */
    Set<String> getObservablePropertiesForCompositePhenomenon(String compositePhenomenon);

    /**
     * Returns collection containing parent features for the passed feature,
     * optionally navigating the full hierarchy and including itself.
     *
     * @param featureOfInterest
     *            the feature id to find parents for
     * @param fullHierarchy
     *            whether or not to navigate the full feature hierarchy
     * @param includeSelf
     *            whether or not to include the passed feature id in the result
     *
     * @return a set containing the passed features id's parents (and optionally
     *         itself)
     */
    Set<String> getParentFeatures(String featureOfInterest, boolean fullHierarchy, boolean includeSelf);

    /**
     * Returns collection containing parent features for the passed features,
     * optionally navigating the full hierarchy and including itself.
     *
     * @param featuresOfInterest
     *            the feature id's to find parents for
     * @param fullHierarchy
     *            whether or not to traverse the full feature hierarchy in one
     *            direction starting from <tt>featureOfInterest</tt>
     * @param includeSelves
     *            whether or not to include the passed feature id's in the
     *            result
     *
     * @return a set containing the passed procedure id's parents (and
     *         optionally itself)
     */
    Set<String> getParentFeatures(Set<String> featuresOfInterest, boolean fullHierarchy, boolean includeSelves);

    /**
     * Returns collection containing child features for the passed feature,
     * optionally navigating the full hierarchy and including itself.
     *
     * @param featureOfInterest
     *            feature id to find children for
     * @param fullHierarchy
     *            whether or not to traverse the full feature hierarchy in one
     *            direction starting from <tt>featureOfInterest</tt>
     * @param includeSelf
     *            whether or not to include the passed feature id in the result
     *
     * @return Collection<String> containing the passed feature id's children
     *         (and optionally itself)
     */
    Set<String> getChildFeatures(String featureOfInterest, boolean fullHierarchy, boolean includeSelf);

    /**
     * Returns collection containing parent procedures for the passed procedure,
     * optionally navigating the full hierarchy and including itself.
     *
     * @param procedure
     *            the procedure id to find parents for
     * @param fullHierarchy
     *            whether or not to traverse the full procedure hierarchy in one
     *            direction starting from <tt>procedure</tt>
     * @param includeSelf
     *            whether or not to include the passed procedure id in the
     *            result
     *
     * @return a set containing the passed procedure id's parents (and
     *         optionally itself)
     */
    Set<String> getParentProcedures(String procedure, boolean fullHierarchy, boolean includeSelf);

    /**
     * Returns collection containing parent procedures for the passed
     * procedures, optionally navigating the full hierarchy and including
     * itself.
     *
     * @param procedures
     *            the procedure id's to find parents for
     * @param fullHierarchy
     *            whether or not to traverse the full procedure hierarchy in one
     *            direction starting from <tt>procedure</tt>
     * @param includeSelves
     *            whether or not to include the passed procedure id in the
     *            result
     *
     * @return a set containing the passed procedure id's parents (and
     *         optionally itself)
     */
    Set<String> getParentProcedures(Set<String> procedures, boolean fullHierarchy, boolean includeSelves);

    /**
     * Returns collection containing child procedures for the passed procedures,
     * optionally navigating the full hierarchy and including itself.
     *
     * @param procedure
     *            procedure id to find children for
     * @param fullHierarchy
     *            whether or not to navigate the full procedure hierarchy
     * @param includeSelf
     *            whether or not to include the passed procedure id in the
     *            result
     *
     * @return Collection<String> containing the passed procedure id's children
     *         (and optionally itself)
     */
    Set<String> getChildProcedures(String procedure, boolean fullHierarchy, boolean includeSelf);

    /**
     * Returns collection containing child procedures for the passed procedures,
     * optionally navigating the full hierarchy and including itself.
     *
     * @param procedure
     *            procedure ids to find children for
     * @param fullHierarchy
     *            whether or not to navigate the full procedure hierarchy
     * @param includeSelves
     *            whether or not to include the passed procedure ids in the
     *            result
     *
     * @return Collection<String> containing the passed procedure ids' children
     *         (and optionally themselves)
     */
    Set<String> getChildProcedures(Set<String> procedure, boolean fullHierarchy, boolean includeSelves);

    /**
     * @return all epsg codes
     */
    Set<Integer> getEpsgCodes();

    /**
     * Checks whether the specified epsg code exists.
     *
     * @param epsgCode
     *            the epsg code
     *
     * @return {@code true} if it exists
     */
    boolean hasEpsgCode(Integer epsgCode);

    /**
     * Checks whether the specified related feature has been used as sampling
     * feature
     *
     * @param relatedFeatureIdentifier
     *            the relatedFeature identifier
     * @return <tt>true</tt>, if the relatedFeature is related to any feature
     *         which is part of an observation.
     */
    boolean isRelatedFeatureSampled(String relatedFeatureIdentifier);

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
     * @param language
     *            Language to check
     * @return <code>true</code>, if the specific lanugage is supported
     */
    boolean isLanguageSupported(Locale language);
    
    /**
     * Get supported requestable procedure description format
     *
     * @return Supported requestable procedure description format
     */
    public Set<String> getRequstableProcedureDescriptionFormat();
    
    /**
     * Is the specific requestable procedure description format supported
     *
     * @param format
     *            format to check
     * @return <code>true</code>, if the specific format is supported
     */
    public boolean hasRequstableProcedureDescriptionFormat(String format);
    
    String getFeatureOfInterestIdentifierForHumanReadableName(String humanReadableName);
    
    String getFeatureOfInterestHumanReadableNameForIdentifier(String identifier);
    
    String getObservablePropertyIdentifierForHumanReadableName(String humanReadableName);
    
    String getObservablePropertyHumanReadableNameForIdentifier(String identifier);
    
    String getProcedureIdentifierForHumanReadableName(String humanReadableName);
    
    String getProcedureHumanReadableNameForIdentifier(String identifier);
    
	String getOfferingIdentifierForHumanReadableName(String humanReadableName);
    
    String getOfferingHumanReadableNameForIdentifier(String identifier);

}
