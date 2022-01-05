/*
 * Copyright (C) 2012-2022 52°North Initiative for Geospatial Open Source
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

import java.util.Collection;
import java.util.Locale;
import java.util.Objects;
import java.util.Set;

import org.joda.time.DateTime;

import org.n52.iceland.cache.WritableContentCache;
import org.n52.janmayen.i18n.LocaleHelper;
import org.n52.janmayen.i18n.MultilingualString;

/**
 * Extension of {@code ContentCache} to allow the manipulation of the cache.
 *
 * @author <a href="mailto:c.autermann@52north.org">Christian Autermann</a>
 *
 * @since 4.0.0
 */
public interface SosWritableContentCache
        extends SosContentCache,
                WritableContentCache,
                WritableSpatialCache,
                WriteableTimeCache,
                WritableCompositePhenomenonCache,
                CacheConstants {

    /**
     * Set the last cache update time
     *
     * @param time Last cache update time
     */
    void setLastUpdateTime(DateTime time);

    /**
     * @return all features of interest associated with an offering
     */
    Set<String> getFeaturesOfInterestWithOffering();

    /**
     * @return all offerings associated with a features of interest
     */
    Set<String> getOfferingWithFeaturesOfInterest();

    /**
     * Allow the specified observation type for the passed offering.
     *
     * @param offering               the offering
     * @param allowedObservationType the observation type
     */
    void addAllowedObservationTypeForOffering(String offering, String allowedObservationType);

    /**
     * Allow the specified observation types for the passed offering.
     *
     * @param offering                the offering
     * @param allowedObservationTypes the observation types
     */
    void addAllowedObservationTypesForOffering(String offering, Collection<String> allowedObservationTypes);

    /**
     * Allow the specified featureOfInterest type for the passed offering.
     *
     * @param offering                     the offering
     * @param allowedFeatureOfInterestType the featureOfInterest type
     */
    void addAllowedFeatureOfInterestTypeForOffering(String offering, String allowedFeatureOfInterestType);

    /**
     * Allow the specified featureOfInterest types for the passed offering.
     *
     * @param offering                      the offering
     * @param allowedFeatureOfInterestTypes the featureOfInterest types
     */
    void addAllowedFeatureOfInterestTypesForOffering(String offering, Collection<String> allowedFeatureOfInterestTypes);

    /**
     * Add the specified feature of interest.
     *
     * @param featureOfInterest the feature of interest
     */
    void addFeatureOfInterest(String featureOfInterest);

    /**
     * Associate the specified feature of interest with the specified offering.
     *
     * @param offering the offering
     * @param feature  the feature of interest
     */
    void addFeatureOfInterestForOffering(String offering, String feature);

    /**
     * Associate the specified offering with the specified features of interest.
     *
     * @param offering           he offering
     * @param featuresOfInterest the features of interest
     */
    void addOfferingForFeaturesOfInterest(String offering, Collection<String> featuresOfInterest);

    /**
     * Associate the specified result template with the specified feature of interest.
     *
     * @param resultTemplate    the result template
     * @param featureOfInterest the feature of interest
     */
    void addFeatureOfInterestForResultTemplate(String resultTemplate, String featureOfInterest);

    /**
     * Add the specified feature of interest.
     *
     * @param featuresOfInterest the feature of interest
     */
    default void addFeaturesOfInterest(Collection<String> featuresOfInterest) {
        CacheValidation.noNullValues(FEATURES_OF_INTEREST, featuresOfInterest);
        featuresOfInterest.forEach(this::addFeatureOfInterest);
    }

    /**
     * Associate the specified features of interest with the specified result template.
     *
     * @param resultTemplate     the result template
     * @param featuresOfInterest the features of interest
     */
    void addFeaturesOfInterestForResultTemplate(String resultTemplate, Collection<String> featuresOfInterest);

    /**
     * Associate the specified observable property with the specified offering.
     *
     * @param offering           the offering
     * @param observableProperty the observable property
     */
    void addObservablePropertyForOffering(String offering, String observableProperty);

    /**
     * Associate the specified observable property with the specified procedure.
     *
     * @param procedure          the procedure
     * @param observableProperty the observable property
     */
    void addObservablePropertyForProcedure(String procedure, String observableProperty);

    /**
     * Associate the specified observable property with the specified result template.
     *
     * @param resultTemplate     the result template
     * @param observableProperty the observable property
     */
    void addObservablePropertyForResultTemplate(String resultTemplate, String observableProperty);

    /**
     * Associate the specified observation type with the specified offering.
     *
     * @param offering        the offering
     * @param observationType the observation type
     */
    void addObservationTypesForOffering(String offering, String observationType);

    /**
     * Associate the specified featureOfInterest type with the specified offering.
     *
     * @param offering              the offering
     * @param featureOfInterestType the featureOfInterest type
     */
    void addFeatureOfInterestTypesForOffering(String offering, String featureOfInterestType);

    /**
     * Associate the specified observable property to the specified offering.
     *
     * @param observableProperty the observable property
     * @param offering           the offering
     */
    void addOfferingForObservableProperty(String observableProperty, String offering);

    /**
     * Associate the specified offering with the specified procedure.
     *
     * @param procedure the procedure
     * @param offering  the offering
     */
    void addOfferingForProcedure(String procedure, String offering);

    /**
     * Associate the specified parent feature with the specified feature of interest.
     *
     * @param featureOfInterest the feature of interest
     * @param parentFeature     the parent feature
     */
    void addParentFeature(String featureOfInterest, String parentFeature);

    /**
     * Associate the specified parent features with the specified feature of interest.
     *
     * @param featureOfInterest the feature of interest
     * @param parentFeatures    the parent features
     */
    void addParentFeatures(String featureOfInterest, Collection<String> parentFeatures);

    /**
     * Associate the specified parent procedure with the specified procedure
     *
     * @param procedure       the procedure
     * @param parentProcedure the parent procedure
     */
    void addParentProcedure(String procedure, String parentProcedure);

    /**
     * Associate the specified parent procedures with the specified procedure
     *
     * @param procedure        the procedure
     * @param parentProcedures the parent procedures
     */
    void addParentProcedures(String procedure, Collection<String> parentProcedures);

    /**
     * Associate the specified parent offering with the specified offering
     *
     * @param offering       the offering
     * @param parentOffering the parent offering
     */
    void addParentOffering(String offering, String parentOffering);

    /**
     * Associate the specified parent procedures with the specified offering
     *
     * @param offering        the offering
     * @param parentOfferings the parent offerings
     */
    void addParentOfferings(String offering, Collection<String> parentOfferings);

    /**
     * Add the specified procedure.
     *
     * @param procedure the procedure
     */
    void addProcedure(String procedure);

    /**
     * Associate the specified procedure with the specified feature of interest.
     *
     * @param featureOfInterest the feature of interest
     * @param procedure         the procedure
     */
    void addProcedureForFeatureOfInterest(String featureOfInterest, String procedure);

    /**
     * Associate the specified procedure with the specified observable property.
     *
     * @param observableProperty the observable property
     * @param procedure          the procedure
     */
    void addProcedureForObservableProperty(String observableProperty, String procedure);

    /**
     * Associate the specified procedure with the specified offering.
     *
     * @param offering  the offering
     * @param procedure the procedure
     */
    void addProcedureForOffering(String offering, String procedure);

    /**
     * Associate the specified hidden child procedure with the specified offering.
     *
     * @param offering  the offering
     * @param procedure the procedure
     */
    void addHiddenChildProcedureForOffering(String offering, String procedure);

    /**
     * Add the specified procedures.
     *
     * @param procedures the procedures
     */
    default void addProcedures(Collection<String> procedures) {
        CacheValidation.noNullValues(PROCEDURES, procedures);
        procedures.forEach(this::addProcedure);
    }

    /**
     * Associate the specified related feature with the specified offering.
     *
     * @param offering       the offering
     * @param relatedFeature the related feature
     */
    void addRelatedFeatureForOffering(String offering, String relatedFeature);

    /**
     * Associate the specified related features with the specified offering.
     *
     * @param offering        the offering
     * @param relatedFeatures the related features
     */
    void addRelatedFeaturesForOffering(String offering, Collection<String> relatedFeatures);

    /**
     * Add the specified result template.
     *
     * @param resultTemplate the result template
     */
    void addResultTemplate(String resultTemplate);

    /**
     * Associate the specified result template with the specified offering.
     *
     * @param offering       the offering
     * @param resultTemplate the result template
     */
    void addResultTemplateForOffering(String offering, String resultTemplate);

    /**
     * Add the specified result templates.
     *
     * @param resultTemplates the result templates
     */
    void addResultTemplates(Collection<String> resultTemplates);

    /**
     * Associate the specified role with the specified related feature.
     *
     * @param relatedFeature the related feature
     * @param role           the role
     */
    void addRoleForRelatedFeature(String relatedFeature, String role);

    void addFeatureOfInterestIdentifierHumanReadableName(String identifier, String humanReadableName);

    void addObservablePropertyIdentifierHumanReadableName(String identifier, String humanReadableName);

    void addProcedureIdentifierHumanReadableName(String identifier, String humanReadableName);

    void addOfferingIdentifierHumanReadableName(String identifier, String humanReadableName);

    void addProcedureDescriptionFormatsForProcedure(String identifier, Set<String> formats);

    /**
     * Dissociate the specified allowed observation type with the specified offering.
     *
     * @param offering               the offering
     * @param allowedObservationType the allowed observation type
     */
    void removeAllowedObservationTypeForOffering(String offering, String allowedObservationType);

    /**
     * Dissociate all allowed observation type with the specified offering.
     *
     * @param offering the offering
     */
    void removeAllowedObservationTypesForOffering(String offering);

    /**
     * Remove the specified feature of interest.
     *
     * @param featureOfInterest the feature of interest
     */
    void removeFeatureOfInterest(String featureOfInterest);

    /**
     * Dissociate the specified feature with the specified offering.
     *
     * @param offering          the offering
     * @param featureOfInterest the feature of interest
     */
    void removeFeatureOfInterestForOffering(String offering, String featureOfInterest);

    /**
     * Dissociate the specified feature of interest with the specified result template.
     *
     * @param resultTemplate    the result template
     * @param featureOfInterest the feature of interest
     */
    void removeFeatureOfInterestForResultTemplate(String resultTemplate, String featureOfInterest);

    /**
     * Remove the specified features of interest.
     *
     * @param featuresOfInterest the features of interest
     */
    void removeFeaturesOfInterest(Collection<String> featuresOfInterest);

    /**
     * Dissociate all features of interest with the specified offering.
     *
     * @param offering the offering
     */
    void removeFeaturesOfInterestForOffering(String offering);

    /**
     * Dissociate all features of interest with the specified result template.
     *
     * @param resultTemplate the result template
     */
    void removeFeaturesOfInterestForResultTemplate(String resultTemplate);

    /**
     * Remove the name for the specified offering.
     *
     * @param offering the offering
     */
    void removeNameForOffering(String offering);

    /**
     * Dissociate all observable properties with the specified offering.
     *
     * @param offering the offering
     */
    void removeObservablePropertiesForOffering(String offering);

    /**
     * Dissociate all observable properties with the specified procedure.
     *
     * @param procedure the procedure
     */
    void removeObservablePropertiesForProcedure(String procedure);

    /**
     * Dissociate all observable properties with the specified result template.
     *
     * @param resultTemplate the result template
     */
    void removeObservablePropertiesForResultTemplate(String resultTemplate);

    /**
     * Dissociate the specified observable property with the specified offering.
     *
     * @param offering           the offering
     * @param observableProperty the observable property
     */
    void removeObservablePropertyForOffering(String offering, String observableProperty);

    /**
     * Dissociate the specified observable property with the specified procedure.
     *
     * @param procedure          the procedure
     * @param observableProperty the observable property
     */
    void removeObservablePropertyForProcedure(String procedure, String observableProperty);

    /**
     * Dissociate the specified observable property with the specified result template.
     *
     * @param resultTemplate     the result template
     * @param observableProperty the observable property
     */
    void removeObservablePropertyForResultTemplate(String resultTemplate, String observableProperty);

    /**
     * Dissociate the specified featureOfInterest type with the specified offering.
     *
     * @param offering              the offering
     * @param featureOfInterestType the featureOfInterest type
     */
    void removeFeatureOfInterestTypeForOffering(String offering, String featureOfInterestType);

    /**
     * Dissociate all featureOfInterest types with the specified offering.
     *
     * @param offering the offering
     */
    void removeFeatureOfInterestTypesForOffering(String offering);

    /**
     * Dissociate the specified observation type with the specified offering.
     *
     * @param offering        the offering
     * @param observationType the observation type
     */
    void removeObservationTypeForOffering(String offering, String observationType);

    /**
     * Dissociate all observation types with the specified offering.
     *
     * @param offering the offering
     */
    void removeObservationTypesForOffering(String offering);

    /**
     * Dissociate the specified offering with the specified observable property.
     *
     * @param observableProperty the observable property
     * @param offering           the offering
     */
    void removeOfferingForObservableProperty(String observableProperty, String offering);

    /**
     * Dissociate the specified offering with the specified procedure.
     *
     * @param procedure the procedure
     * @param offering  the offering
     */
    void removeOfferingForProcedure(String procedure, String offering);

    /**
     * Dissociate all offerings with the specified observable property.
     *
     * @param observableProperty the observable property
     */
    void removeOfferingsForObservableProperty(String observableProperty);

    /**
     * Dissociate all offerings with the specified procedure.
     *
     * @param procedure the procedure
     */
    void removeOfferingsForProcedure(String procedure);

    /**
     * Remove the specified procedure.
     *
     * @param procedure the procedure
     */
    void removeProcedure(String procedure);

    /**
     * Dissociate the specified procedure with the specified feature of interest.
     *
     * @param featureOfInterest the feature of interest
     * @param procedure         the procedure
     */
    void removeProcedureForFeatureOfInterest(String featureOfInterest, String procedure);

    /**
     * Dissociate the specified procedure with the specified observable property.
     *
     * @param observableProperty the observable property
     * @param procedure          the procedure
     */
    void removeProcedureForObservableProperty(String observableProperty, String procedure);

    /**
     * Dissociate the specified procedure with the specified offering.
     *
     * @param offering  the offering
     * @param procedure the procedure
     */
    void removeProcedureForOffering(String offering, String procedure);

    /**
     * Remove the specified procedure from map.
     *
     * @param identifier the procedure
     */
    void removeProcedureDescriptionFormatsForProcedure(String identifier);

    /**
     * Dissociate the specified procedure with the specified offering.
     *
     * @param offering  the offering
     * @param procedure the procedure
     */
    void removeHiddenChildProcedureForOffering(String offering, String procedure);

    /**
     * Remove the specified procedures.
     *
     * @param procedures the procedures
     */
    default void removeProcedures(Collection<String> procedures) {
        CacheValidation.noNullValues(PROCEDURES, procedures);
        procedures.forEach(this::removeProcedure);
    }

    /**
     * Dissociate all procedures with the specified feature of interest.
     *
     * @param featureOfInterest the feature of interest
     */
    void removeProceduresForFeatureOfInterest(String featureOfInterest);

    /**
     * Dissociate all procedures with the specified observable property.
     *
     * @param observableProperty the observable property
     */
    void removeProceduresForObservableProperty(String observableProperty);

    /**
     * Dissociate all procedures with the specified offering.
     *
     * @param offering the offering
     */
    void removeProceduresForOffering(String offering);

    /**
     * Dissociate the specified related feature with the specified offering.
     *
     * @param offering       the offering
     * @param relatedFeature the related feature
     */
    void removeRelatedFeatureForOffering(String offering, String relatedFeature);

    /**
     * Dissociate all related features with the specified offering.
     *
     * @param offering the offering
     */
    void removeRelatedFeaturesForOffering(String offering);

    /**
     * Remove the specified result template.
     *
     * @param resultTemplate the result template
     */
    void removeResultTemplate(String resultTemplate);

    /**
     * Dissociate the specified result template with the specified result template.
     *
     * @param offering       the offering
     * @param resultTemplate the result template
     */
    void removeResultTemplateForOffering(String offering, String resultTemplate);

    /**
     * Remove the specified result templates.
     *
     * @param resultTemplates the result templates
     */
    default void removeResultTemplates(Collection<String> resultTemplates) {
        resultTemplates.forEach(this::removeResultTemplate);
    }

    /**
     * Dissociate all result templates with the specified result template.
     *
     * @param offering the offering
     */
    void removeResultTemplatesForOffering(String offering);

    /**
     * Dissociate the specified role with the specified related feature.
     *
     * @param relatedFeature the related feature
     * @param role           the role
     */
    void removeRoleForRelatedFeature(String relatedFeature, String role);

    /**
     * Dissociate all roles with the specified related feature.
     *
     * @param relatedFeature the related feature
     */
    void removeRolesForRelatedFeature(String relatedFeature);

    /**
     * Dissociate all roles with the specified related feature that are not contained in the specified collection.
     *
     * @param features the related features for which the roles should kept
     */
    void removeRolesForRelatedFeatureNotIn(Collection<String> features);

    void removeFeatureOfInterestIdentifierForHumanReadableName(String humanReadableName);

    void removeFeatureOfInterestHumanReadableNameForIdentifier(String identifier);

    void removeObservablePropertyIdentifierForHumanReadableName(String humanReadableName);

    void removeObservablePropertyHumanReadableNameForIdentifier(String identifier);

    void removeProcedureIdentifierForHumanReadableName(String humanReadableName);

    void removeProcedureHumanReadableNameForIdentifier(String identifier);

    void removeOfferingIdentifierForHumanReadableName(String humanReadableName);

    void removeOfferingHumanReadableNameForIdentifier(String identifier);

    /**
     * Sets the allowed observation types for the specified offering.
     *
     * @param offering                the offering
     * @param allowedObservationTypes the allowed observation types
     */
    void setAllowedObservationTypeForOffering(String offering, Collection<String> allowedObservationTypes);

    /**
     * Sets the allowed featureOfInterest types for the specified offering.
     *
     * @param offering                      the offering
     * @param allowedFeatureOfInterestTypes the allowed featureOfInterest types
     */
    void setAllowedFeatureOfInterestTypeForOffering(String offering, Collection<String> allowedFeatureOfInterestTypes);

    /**
     * Sets the features of interest.
     *
     * @param featuresOfInterest the features of interest
     */
    void setFeaturesOfInterest(Collection<String> featuresOfInterest);

    /**
     * Sets the features of interest for the specified offering.
     *
     * @param offering           the offering
     * @param featuresOfInterest the features of interest.
     */
    void setFeaturesOfInterestForOffering(String offering, Collection<String> featuresOfInterest);

    /**
     * Sets the name of the specified offering.
     *
     * @param offering the offering
     * @param name     the name
     */
    void setNameForOffering(String offering, String name);

    /**
     * Sets the name of the specified language and the specified offering.
     *
     * @param offering the offering
     * @param name     the name
     */
    void setI18nNameForOffering(String offering, MultilingualString name);

    /**
     * Sets the description of the specified language and the specified offering.
     *
     * @param offering    the offering
     * @param description the description
     */
    void setI18nDescriptionForOffering(String offering, MultilingualString description);

    /**
     * Sets the observable properties for the specified offering.
     *
     * @param offering             the offering
     * @param observableProperties the observable properties
     */
    void setObservablePropertiesForOffering(String offering, Collection<String> observableProperties);

    /**
     * Sets the observable properties for the specified procedure.
     *
     * @param procedure            the procedure
     * @param observableProperties the observable properties
     */
    void setObservablePropertiesForProcedure(String procedure, Collection<String> observableProperties);

    /**
     * Sets the observable properties for the specified result template.
     *
     * @param resultTemplate       the result template
     * @param observableProperties the observable properties
     */
    void setObservablePropertiesForResultTemplate(String resultTemplate, Collection<String> observableProperties);

    /**
     * Sets the observation types for the specified offering.
     *
     * @param offering         the offering
     * @param observationTypes the observation types
     */
    void setObservationTypesForOffering(String offering, Collection<String> observationTypes);

    /**
     * Sets the featureOfInterest types for the specified offering.
     *
     * @param offering               the offering
     * @param featureOfInterestTypes the featureOfInterest types
     */
    void setFeatureOfInterestTypesForOffering(String offering, Collection<String> featureOfInterestTypes);

    /**
     * Sets the specified offerings for the specified observable property.
     *
     * @param observableProperty the observable property
     * @param offerings          the offerings
     */
    void setOfferingsForObservableProperty(String observableProperty, Collection<String> offerings);

    /**
     * Sets the offerings for the specified procedure.
     *
     * @param procedure the procedure
     * @param offerings the offerings
     */
    void setOfferingsForProcedure(String procedure, Collection<String> offerings);

    /**
     * Sets the procedures.
     *
     * @param procedures the procedures
     */
    void setProcedures(Collection<String> procedures);

    /**
     * Sets the procedures for the specified feature of interest.
     *
     * @param featureOfInterest the feature of interest
     * @param procedures        the procedure
     */
    void setProceduresForFeatureOfInterest(String featureOfInterest, Collection<String> procedures);

    /**
     * Sets the procedures for the specified observable property.
     *
     * @param observableProperty the observable property
     * @param procedures         the procedures
     */
    void setProceduresForObservableProperty(String observableProperty, Collection<String> procedures);

    /**
     * Sets the procedures for the specified offering.
     *
     * @param offering   the offering
     * @param procedures the procedures
     */
    void setProceduresForOffering(String offering, Collection<String> procedures);

    /**
     * Sets the hidden child procedures for the specified offering. To create a sensor system for SOS 2.0.
     *
     * @param offering   the offering
     * @param procedures the procedures
     */
    void setHiddenChildProceduresForOffering(String offering, Collection<String> procedures);

    /**
     * Sets the related features for the specified offering.
     *
     * @param offering        the offering
     * @param relatedFeatures the related features
     */
    void setRelatedFeaturesForOffering(String offering, Collection<String> relatedFeatures);

    /**
     * Sets the result template for the specified offering.
     *
     * @param offering        the offering
     * @param resultTemplates the result templates
     */
    void setResultTemplatesForOffering(String offering, Collection<String> resultTemplates);

    /**
     * Sets the roles for the specified related feature.
     *
     * @param relatedFeature the related feature
     * @param roles          the roles
     */
    void setRolesForRelatedFeature(String relatedFeature, Collection<String> roles);

    /**
     * Reset the features of interest.
     */
    void clearFeaturesOfInterest();

    /**
     * Reset the procedures to feature of interest relation.
     */
    void clearProceduresForFeatureOfInterest();

    /**
     * Reset the feature hierarchy.
     */
    void clearFeatureHierarchy();

    /**
     * Reset the offerings.
     */
    void clearOfferings();

    /**
     * Reset the offering to procedures relation.
     */
    void clearProceduresForOfferings();

    /**
     * Reset the offering to hidden child procedures relation.
     */
    void clearHiddenChildProceduresForOfferings();

    /**
     * Reset the offering to offering name relation.
     */
    void clearNameForOfferings();

    /**
     * Reset the offering to language and offering name relation.
     */
    void clearI18nNamesForOfferings();

    /**
     * Reset the offering to language offering description relation.
     */
    void clearI18nDescriptionsNameForOfferings();

    /**
     * Reset the offering to observable property relation.
     */
    void clearObservablePropertiesForOfferings();

    /**
     * Reset the offering to related features relation.
     */
    void clearRelatedFeaturesForOfferings();

    /**
     * Reset the offerings to observation types relation.
     */
    void clearObservationTypesForOfferings();

    /**
     * Reset the offerings to allowed observation types relation.
     */
    void clearAllowedObservationTypeForOfferings();

    /**
     * Reset the offering to feature of interest relation.
     */
    void clearFeaturesOfInterestForOfferings();

    /**
     * Reset the feature of interest to offering relation.
     */
    void clearOfferingsForFeaturesOfInterest();

    /**
     * Add the specified offering.
     *
     * @param offering the offering
     */
    void addOffering(String offering);

    /**
     * Sets the offerings.
     *
     * @param offerings the offerings
     */
    default void setOfferings(Collection<String> offerings) {
        clearOfferings();
        addOfferings(offerings);
    }

    /**
     * Add the specified offerings.
     *
     * @param offerings the offerings
     */
    default void addOfferings(Collection<String> offerings) {
        CacheValidation.noNullValues(OFFERINGS, offerings);
        offerings.forEach(this::addOffering);
    }

    /**
     * Remove the specified offering.
     *
     * @param offering the offering
     */
    void removeOffering(String offering);

    /**
     * Remove the specified offerings.
     *
     * @param offerings the offerings
     */
    default void removeOfferings(Collection<String> offerings) {
        CacheValidation.noNullValues(OFFERINGS, offerings);
        offerings.forEach(this::removeOffering);
    }

    /**
     * Add the specified language.
     *
     * @param language the new language
     */
    void addSupportedLanguage(Locale language);

    /**
     * Add the specified language.
     *
     * @param language the new language
     */
    default void addSupportedLanguage(String language) {
        Objects.requireNonNull(language, SUPPORTED_LANGUAGE);
        addSupportedLanguage(LocaleHelper.decode(language));
    }

    /**
     * Add the specified languages.
     *
     * @param languages the new languages
     */
    default void addSupportedLanguage(Collection<Locale> languages) {
        CacheValidation.noNullValues(SUPPORTED_LANGUAGES, languages);
        languages.forEach(this::addSupportedLanguage);
    }

    /**
     * Clear the specified languages.
     *
     */
    void clearSupportedLanguage();

    /**
     * Remove the specified language.
     *
     * @param language the new language to remove
     */
    void removeSupportedLanguage(Locale language);

    /**
     * Set the specified requestable procedureDescriptionFormat.
     *
     * @param formats the new formats
     */
    void setRequestableProcedureDescriptionFormat(Collection<String> formats);

    void clearFeatureOfInterestIdentifierHumanReadableNameMaps();

    void clearObservablePropertyIdentifierHumanReadableNameMaps();

    void clearProcedureIdentifierHumanReadableNameMaps();

    void clearOfferingIdentifierHumanReadableNameMaps();

    void addTypeInstanceProcedure(TypeInstance typeInstance, String identifier);

    void removeTypeInstanceProcedure(String identifier);

    void clearTypeInstanceProcedure();

    void addComponentAggregationProcedure(ComponentAggregation componentAggregation, String identifier);

    void removeComponentAggregationProcedure(String identifier);

    void clearComponentAggregationProcedure();

    void addTypeOfProcedure(String type, String instance);

    void addTypeOfProcedure(String type, Set<String> instances);

    void removeTypeOfProcedure(String type);

    void removeTypeOfProcedure(String type, String instance);

    void clearTypeOfProcedure();

    void addPublishedFeatureOfInterest(String featureOfInterest);

    default void addPublishedFeaturesOfInterest(Collection<String> featuresOfInterest) {
        CacheValidation.noNullValues(PUBLISHED_FEATURES_OF_INTEREST, featuresOfInterest);
        featuresOfInterest.forEach(this::addPublishedFeatureOfInterest);
    }

    default void setPublishedFeaturesOfInterest(Collection<String> featuresOfInterest) {
        clearPublishedFeaturesOfInterest();
        addPublishedFeaturesOfInterest(featuresOfInterest);
    }

    void clearPublishedFeaturesOfInterest();

    void removePublishedFeatureOfInterest(String featureOfInterest);

    default void removePublishedFeaturesOfInterest(Collection<String> featuresOfInterest) {
        CacheValidation.noNullValues(PUBLISHED_FEATURES_OF_INTEREST, featuresOfInterest);
        featuresOfInterest.forEach(this::removePublishedFeatureOfInterest);
    }

    void addPublishedProcedure(String procedure);

    default void addPublishedProcedures(Collection<String> procedures) {
        CacheValidation.noNullValues(CacheConstants.PUBLISHED_PROCEDURES, procedures);
        procedures.forEach(this::addPublishedProcedure);
    }

    default void setPublishedProcedures(Collection<String> procedures) {
        clearPublishedProcedure();
        addPublishedProcedures(procedures);
    }

    void clearPublishedProcedure();

    void removePublishedProcedure(String procedure);

    default void removePublishedProcedures(Collection<String> procedures) {
        CacheValidation.noNullValues(PUBLISHED_PROCEDURES, procedures);
        procedures.forEach(this::removePublishedProcedure);
    }

    void addPublishedOffering(String offering);

    default void addPublishedOfferings(Collection<String> offerings) {
        CacheValidation.noNullValues(PUBLISHED_OFFERINGS, offerings);
        offerings.forEach(this::addPublishedOffering);
    }

    default void setPublishedOfferings(Collection<String> offerings) {
        clearPublishedOffering();
        addPublishedOfferings(offerings);
    }

    void clearPublishedOffering();

    void removePublishedOffering(String offering);

    default void removePublishedOfferings(Collection<String> offerings) {
        CacheValidation.noNullValues(PUBLISHED_OFFERINGS, offerings);
        offerings.forEach(this::removePublishedOffering);
    }

    void addPublishedObservableProperty(String observableProperty);

    default void addPublishedObservableProperties(Collection<String> observableProperties) {
        CacheValidation.noNullValues(PUBLISHED_OBSERVABLE_PROPERTIES, observableProperties);
        observableProperties.forEach(this::addPublishedObservableProperty);
    }

    default void setPublishedObservableProperties(Collection<String> observableProperties) {
        clearPublishedFeaturesOfInterest();
        addPublishedObservableProperties(observableProperties);
    }

    void clearPublishedObservableProperty();

    void removePublishedObservableProperty(String observableProperty);

    default void removePublishedObservableProperties(Collection<String> observableProperties) {
        CacheValidation.noNullValues(CacheConstants.PUBLISHED_OBSERVABLE_PROPERTIES, observableProperties);
        observableProperties.forEach(this::removePublishedObservableProperty);
    }

}
