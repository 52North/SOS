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

import java.util.Collection;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import org.joda.time.DateTime;
import org.n52.sos.i18n.MultilingualString;
import org.n52.sos.ogc.gml.time.Time;
import org.n52.sos.ogc.gml.time.TimeInstant;
import org.n52.sos.ogc.gml.time.TimePeriod;
import org.n52.sos.ogc.sos.SosEnvelope;
import org.n52.sos.util.CollectionHelper;
import org.n52.sos.util.StringHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.vividsolutions.jts.geom.Envelope;

public class WritableCache extends ReadableCache implements WritableContentCache, CacheConstants {
    private static final Logger LOG = LoggerFactory.getLogger(WritableCache.class);

    private static final long serialVersionUID = 6625851272234063808L;

    /**
     * Creates a {@code TimePeriod} for the specified {@code ITime}.
     *
     * @param time
     *            the abstract time
     *
     * @return the period describing the abstract time
     */
    protected static TimePeriod toTimePeriod(final Time time) {
        if (time instanceof TimeInstant) {
            final DateTime instant = ((TimeInstant) time).getValue();
            return new TimePeriod(instant, instant);
        } else {
            return (TimePeriod) time;
        }
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
        getEpsgCodesSet().add(epsgCode);
    }

    @Override
    public void addFeatureOfInterest(final String featureOfInterest) {
        notNullOrEmpty(FEATURE_OF_INTEREST, featureOfInterest);
        LOG.trace("Adding FeatureOfInterest {}", featureOfInterest);
        getFeaturesOfInterestSet().add(featureOfInterest);
    }

    @Override
    public void addProcedure(final String procedure) {
        notNullOrEmpty(PROCEDURE, procedure);
        LOG.trace("Adding procedure {}", procedure);
        getProceduresSet().add(procedure);
    }

    @Override
    public void addResultTemplate(final String resultTemplate) {
        notNullOrEmpty(RESULT_TEMPLATE, resultTemplate);
        LOG.trace("Adding SosResultTemplate {}", resultTemplate);
        getResultTemplatesSet().add(resultTemplate);
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
        getFeaturesOfInterestSet().remove(featureOfInterest);
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
        getProceduresSet().remove(procedure);
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
        getResultTemplatesSet().remove(resultTemplate);
    }

    @Override
    public void setObservablePropertiesForCompositePhenomenon(final String compositePhenomenon,
            final Collection<String> observableProperties) {
        final Set<String> newValue = newSynchronizedSet(observableProperties);
        LOG.trace("Setting ObservableProperties for CompositePhenomenon {} to {}", compositePhenomenon, newValue);
        getObservablePropertiesForCompositePhenomenonsMap().put(compositePhenomenon, newValue);
    }

    @Override
    public void setObservablePropertiesForOffering(final String offering, final Collection<String> observableProperties) {
        final Set<String> newValue = newSynchronizedSet(observableProperties);
        LOG.trace("Setting ObservableProperties for Offering {} to {}", offering, observableProperties);
        getObservablePropertiesForOfferingsMap().put(offering, newValue);
    }

    @Override
    public void setObservablePropertiesForProcedure(final String procedure,
            final Collection<String> observableProperties) {
        final Set<String> newValue = newSynchronizedSet(observableProperties);
        LOG.trace("Setting ObservableProperties for Procedure {} to {}", procedure, newValue);
        getObservablePropertiesForProceduresMap().put(procedure, newValue);
    }

    @Override
    public void setObservationTypesForOffering(final String offering, final Collection<String> observationTypes) {
        final Set<String> newValue = newSynchronizedSet(observationTypes);
        LOG.trace("Setting ObservationTypes for Offering {} to {}", offering, newValue);
        getObservationTypesForOfferingsMap().put(offering, newValue);
    }

    @Override
    public void setOfferingsForObservableProperty(final String observableProperty, final Collection<String> offerings) {
        final Set<String> newValue = newSynchronizedSet(offerings);
        LOG.trace("Setting Offerings for ObservableProperty {} to {}", observableProperty, newValue);
        getOfferingsForObservablePropertiesMap().put(observableProperty, newValue);
    }

    @Override
    public void setOfferingsForProcedure(final String procedure, final Collection<String> offerings) {
        final Set<String> newValue = newSynchronizedSet(offerings);
        LOG.trace("Setting Offerings for Procedure {} to {}", procedure, newValue);
        getOfferingsForProceduresMap().put(procedure, newValue);
    }

    @Override
    public void setProceduresForFeatureOfInterest(final String featureOfInterest,
            final Collection<String> proceduresForFeatureOfInterest) {
        final Set<String> newValue = newSynchronizedSet(proceduresForFeatureOfInterest);
        LOG.trace("Setting Procedures for FeatureOfInterest {} to {}", featureOfInterest, newValue);
        getProceduresForFeaturesOfInterestMap().put(featureOfInterest, newValue);
    }

    @Override
    public void setProceduresForObservableProperty(final String observableProperty, final Collection<String> procedures) {
        final Set<String> newValue = newSynchronizedSet(procedures);
        LOG.trace("Setting Procedures for ObservableProperty {} to {}", observableProperty, procedures);
        getProceduresForObservablePropertiesMap().put(observableProperty, newValue);
    }

    @Override
    public void setProceduresForOffering(final String offering, final Collection<String> procedures) {
        final Set<String> newValue = newSynchronizedSet(procedures);
        LOG.trace("Setting Procedures for Offering {} to {}", offering, newValue);
        getProceduresForOfferingsMap().put(offering, newValue);
    }

    @Override
    public void setRelatedFeaturesForOffering(final String offering, final Collection<String> relatedFeatures) {
        final Set<String> newValue = newSynchronizedSet(relatedFeatures);
        LOG.trace("Setting Related Features for Offering {} to {}", offering, newValue);
        getRelatedFeaturesForOfferingsMap().put(offering, newValue);
    }

    @Override
    public void setResultTemplatesForOffering(final String offering, final Collection<String> resultTemplates) {
        final Set<String> newValue = newSynchronizedSet(resultTemplates);
        LOG.trace("Setting ResultTemplates for Offering {} to {}", offering, newValue);
        getResultTemplatesForOfferingsMap().put(offering, newValue);
    }

    @Override
    public void setRolesForRelatedFeature(final String relatedFeature, final Collection<String> roles) {
        final Set<String> newValue = newSynchronizedSet(roles);
        LOG.trace("Setting Roles for RelatedFeature {} to {}", relatedFeature, newValue);
        getRolesForRelatedFeaturesMap().put(relatedFeature, newValue);
    }

    @Override
    public void setFeaturesOfInterest(final Collection<String> featuresOfInterest) {
        LOG.trace("Setting FeaturesOfInterest");
        getFeaturesOfInterestSet().clear();
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
        getProceduresSet().clear();
        addProcedures(procedures);
    }

    @Override
    public void setMaxPhenomenonTimeForOffering(final String offering, final DateTime maxTime) {
        notNullOrEmpty(OFFERING, offering);
        LOG.trace("Setting maximal EventTime for Offering {} to {}", offering, maxTime);
        if (maxTime == null) {
            getMaxPhenomenonTimeForOfferingsMap().remove(offering);
        } else {
            getMaxPhenomenonTimeForOfferingsMap().put(offering, maxTime);
        }
    }

    @Override
    public void setMinPhenomenonTimeForOffering(final String offering, final DateTime minTime) {
        notNullOrEmpty(OFFERING, offering);
        LOG.trace("Setting minimal EventTime for Offering {} to {}", offering, minTime);
        if (minTime == null) {
            getMinPhenomenonTimeForOfferingsMap().remove(offering);
        } else {
            getMinPhenomenonTimeForOfferingsMap().put(offering, minTime);
        }
    }

    @Override
    public void setMaxPhenomenonTimeForProcedure(final String procedure, final DateTime maxTime) {
        notNullOrEmpty(PROCEDURE, procedure);
        LOG.trace("Setting maximal phenomenon time for procedure {} to {}", procedure, maxTime);
        if (maxTime == null) {
            getMaxPhenomenonTimeForProceduresMap().remove(procedure);
        } else {
            getMaxPhenomenonTimeForProceduresMap().put(procedure, maxTime);
        }
    }

    @Override
    public void setMinPhenomenonTimeForProcedure(final String procedure, final DateTime minTime) {
        notNullOrEmpty(PROCEDURE, procedure);
        LOG.trace("Setting minimal phenomenon time for procedure {} to {}", procedure, minTime);
        if (minTime == null) {
            getMinPhenomenonTimeForProceduresMap().remove(procedure);
        } else {
            getMinPhenomenonTimeForProceduresMap().put(procedure, minTime);
        }
    }

    @Override
    public void setNameForOffering(final String offering, final String name) {
        notNullOrEmpty(OFFERING, offering);
        notNullOrEmpty(NAME, name);
        LOG.trace("Setting Name of Offering {} to {}", offering, name);
        getNameForOfferingsMap().put(offering, name);

    }

    @Override
    public void setI18nNameForOffering(String offering, MultilingualString name) {
        notNullOrEmpty(OFFERING, offering);
        notNull(NAME, name);
        LOG.trace("Setting I18N Name of Offering {} to {}", offering, name);
        getI18nNameForOfferingsMap().put(offering, name);
    }

    @Override
    public void setI18nDescriptionForOffering(String offering, MultilingualString description) {
        notNullOrEmpty(OFFERING, offering);
        notNull(DESCRIPTION, description);
        LOG.trace("Setting I18N Description of Offering {} to {}", offering, description);
        getI18nDescriptionForOfferingsMap().put(offering, description);
    }

    @Override
    public void setEnvelopeForOffering(final String offering, final SosEnvelope envelope) {
        LOG.trace("Setting Envelope for Offering {} to {}", offering, envelope);
        getEnvelopeForOfferingsMap().put(offering, copyOf(envelope));
    }

    @Override
    public Set<String> getFeaturesOfInterestWithOffering() {
        return CollectionHelper.unionOfListOfLists(getFeaturesOfInterestForOfferingMap().values());
    }

    @Override
    public void addAllowedObservationTypeForOffering(final String offering, final String allowedObservationType) {
        notNullOrEmpty(OFFERING, offering);
        notNullOrEmpty(ALLOWED_OBSERVATION_TYPE, allowedObservationType);
        LOG.trace("Adding AllowedObservationType {} to Offering {}", allowedObservationType, offering);
        getAllowedObservationTypesForOfferingsMap().add(offering, allowedObservationType);
    }

    @Override
    public void addAllowedObservationTypesForOffering(final String offering,
            final Collection<String> allowedObservationTypes) {
        notNullOrEmpty(OFFERING, offering);
        noNullValues(ALLOWED_OBSERVATION_TYPES, allowedObservationTypes);
        LOG.trace("Adding AllowedObservationTypes {} to Offering {}", allowedObservationTypes, offering);
        getAllowedObservationTypesForOfferingsMap().addAll(offering, allowedObservationTypes);
    }

    @Override
    public void addCompositePhenomenonForOffering(final String offering, final String compositePhenomenon) {
        notNullOrEmpty(OFFERING, offering);
        notNullOrEmpty(COMPOSITE_PHENOMENON, compositePhenomenon);
        LOG.trace("Adding compositePhenomenon {} to Offering {}", compositePhenomenon, offering);
        getCompositePhenomenonsForOfferingsMap().add(offering, compositePhenomenon);
    }

    @Override
    public void addFeatureOfInterestForOffering(final String offering, final String featureOfInterest) {
        notNullOrEmpty(OFFERING, offering);
        notNullOrEmpty(FEATURE_OF_INTEREST, featureOfInterest);
        LOG.trace("Adding featureOfInterest {} to Offering {}", featureOfInterest, offering);
        getFeaturesOfInterestForOfferingMap().add(offering, featureOfInterest);
    }

    @Override
    public void addFeatureOfInterestForResultTemplate(final String resultTemplate, final String featureOfInterest) {
        notNullOrEmpty(RESULT_TEMPLATE, resultTemplate);
        notNullOrEmpty(FEATURE_OF_INTEREST, featureOfInterest);
        LOG.trace("Adding FeatureOfInterest {} to SosResultTemplate {}", featureOfInterest, resultTemplate);
        getFeaturesOfInterestForResultTemplatesMap().add(resultTemplate, featureOfInterest);
    }

    @Override
    public void addFeaturesOfInterestForResultTemplate(final String resultTemplate,
            final Collection<String> featuresOfInterest) {
        notNullOrEmpty(RESULT_TEMPLATE, resultTemplate);
        noNullValues(FEATURES_OF_INTEREST, featuresOfInterest);
        LOG.trace("Adding FeatureOfInterest {} to SosResultTemplate {}", featuresOfInterest, resultTemplate);
        getFeaturesOfInterestForResultTemplatesMap().addAll(resultTemplate, featuresOfInterest);
    }

    @Override
    public void addObservablePropertyForCompositePhenomenon(final String compositePhenomenon,
            final String observableProperty) {
        notNullOrEmpty(COMPOSITE_PHENOMENON, compositePhenomenon);
        notNullOrEmpty(OBSERVABLE_PROPERTY, observableProperty);
        LOG.trace("Adding ObservableProperty {} to CompositePhenomenon {}", observableProperty, compositePhenomenon);
        getObservablePropertiesForCompositePhenomenonsMap().add(compositePhenomenon, observableProperty);
    }

    @Override
    public void addObservablePropertyForOffering(final String offering, final String observableProperty) {
        notNullOrEmpty(OFFERING, offering);
        notNullOrEmpty(OBSERVABLE_PROPERTY, observableProperty);
        LOG.trace("Adding observableProperty {} to offering {}", observableProperty, offering);
        getObservablePropertiesForOfferingsMap().add(offering, observableProperty);
    }

    @Override
    public void addObservablePropertyForProcedure(final String procedure, final String observableProperty) {
        notNullOrEmpty(PROCEDURE, procedure);
        notNullOrEmpty(OBSERVABLE_PROPERTY, observableProperty);
        LOG.trace("Adding observableProperty {} to procedure {}", observableProperty, procedure);
        getObservablePropertiesForProceduresMap().add(procedure, observableProperty);
    }

    @Override
    public void addObservablePropertyForResultTemplate(final String resultTemplate, final String observableProperty) {
        notNullOrEmpty(RESULT_TEMPLATE, resultTemplate);
        notNullOrEmpty(OBSERVABLE_PROPERTY, observableProperty);
        LOG.trace("Adding observableProperty {} to resultTemplate {}", observableProperty, resultTemplate);
        getObservablePropertiesForResultTemplatesMap().add(resultTemplate, observableProperty);
    }

    @Override
    public void addObservationTypesForOffering(final String offering, final String observationType) {
        notNullOrEmpty(OFFERING, offering);
        notNullOrEmpty(OBSERVATION_TYPE, observationType);
        LOG.trace("Adding observationType {} to offering {}", observationType, offering);
        getObservationTypesForOfferingsMap().add(offering, observationType);
    }

    @Override
    public void addOfferingForObservableProperty(final String observableProperty, final String offering) {
        notNullOrEmpty(OBSERVABLE_PROPERTY, observableProperty);
        notNullOrEmpty(OFFERING, offering);
        LOG.trace("Adding offering {} to observableProperty {}", offering, observableProperty);
        getOfferingsForObservablePropertiesMap().add(observableProperty, offering);
    }

    @Override
    public void addOfferingForProcedure(final String procedure, final String offering) {
        notNullOrEmpty(PROCEDURE, procedure);
        notNullOrEmpty(OFFERING, offering);
        LOG.trace("Adding offering {} to procedure {}", offering, procedure);
        getOfferingsForProceduresMap().add(procedure, offering);
    }

    @Override
    public void addProcedureForFeatureOfInterest(final String featureOfInterest, final String procedure) {
        notNullOrEmpty(FEATURE_OF_INTEREST, featureOfInterest);
        notNullOrEmpty(PROCEDURE, procedure);
        LOG.trace("Adding procedure {} to featureOfInterest {}", procedure, featureOfInterest);
        getProceduresForFeaturesOfInterestMap().add(featureOfInterest, procedure);
    }

    @Override
    public void addProcedureForObservableProperty(final String observableProperty, final String procedure) {
        notNullOrEmpty(FEATURE_OF_INTEREST, observableProperty);
        notNullOrEmpty(PROCEDURE, procedure);
        LOG.trace("Adding procedure {} to observableProperty {}", procedure, observableProperty);
        getProceduresForObservablePropertiesMap().add(observableProperty, procedure);
    }

    @Override
    public void addProcedureForOffering(final String offering, final String procedure) {
        notNullOrEmpty(OFFERING, offering);
        notNullOrEmpty(PROCEDURE, procedure);
        LOG.trace("Adding procedure {} to offering {}", procedure, offering);
        getProceduresForOfferingsMap().add(offering, procedure);
    }

    @Override
    public void addRelatedFeatureForOffering(final String offering, final String relatedFeature) {
        notNullOrEmpty(OFFERING, offering);
        notNullOrEmpty(RELATED_FEATURE, relatedFeature);
        LOG.trace("Adding relatedFeature {} to offering {}", relatedFeature, offering);
        getRelatedFeaturesForOfferingsMap().add(offering, relatedFeature);
    }

    @Override
    public void addRelatedFeaturesForOffering(final String offering, final Collection<String> relatedFeature) {
        notNullOrEmpty(OFFERING, offering);
        noNullValues(RELATED_FEATURE, relatedFeature);
        LOG.trace("Adding relatedFeatures {} to offering {}", relatedFeature, offering);
        getRelatedFeaturesForOfferingsMap().addAll(offering, relatedFeature);
    }

    @Override
    public void addResultTemplateForOffering(final String offering, final String resultTemplate) {
        notNullOrEmpty(OFFERING, offering);
        notNullOrEmpty(RESULT_TEMPLATE, resultTemplate);
        LOG.trace("Adding resultTemplate {} to offering {}", resultTemplate, offering);
        getResultTemplatesForOfferingsMap().add(offering, resultTemplate);
    }

    @Override
    public void addRoleForRelatedFeature(final String relatedFeature, final String role) {
        notNullOrEmpty(RELATED_FEATURE, relatedFeature);
        notNullOrEmpty("role", role);
        LOG.trace("Adding role {} to relatedFeature {}", role, relatedFeature);
        getRolesForRelatedFeaturesMap().add(relatedFeature, role);
    }

    @Override
    public void removeAllowedObservationTypeForOffering(final String offering, final String allowedObservationType) {
        notNullOrEmpty(OFFERING, offering);
        notNullOrEmpty("allowedObservationType", allowedObservationType);
        LOG.trace("Removing allowedObservationType {} from offering {}", allowedObservationType, offering);
        getAllowedObservationTypesForOfferingsMap().removeWithKey(offering, allowedObservationType);
    }

    @Override
    public void removeAllowedObservationTypesForOffering(final String offering) {
        notNullOrEmpty(OFFERING, offering);
        LOG.trace("Removing allowedObservationTypes for offering {}", offering);
        getAllowedObservationTypesForOfferingsMap().remove(offering);
    }

    @Override
    public void removeCompositePhenomenonForOffering(final String offering, final String compositePhenomenon) {
        notNullOrEmpty(OFFERING, offering);
        notNullOrEmpty(COMPOSITE_PHENOMENON, compositePhenomenon);
        LOG.trace("Removing compositePhenomenon {} from offering {}", compositePhenomenon, offering);
        getCompositePhenomenonsForOfferingsMap().removeWithKey(offering, compositePhenomenon);
    }

    @Override
    public void removeCompositePhenomenonsForOffering(final String offering) {
        notNullOrEmpty(OFFERING, offering);
        LOG.trace("Removing compositePhenomenons for offering {}", offering);
        getCompositePhenomenonsForOfferingsMap().remove(offering);
    }

    @Override
    public void removeEnvelopeForOffering(final String offering) {
        notNullOrEmpty(OFFERING, offering);
        LOG.trace("Removing envelope for offering {}", offering);
        getEnvelopeForOfferingsMap().remove(offering);
    }

    @Override
    public void removeEpsgCode(final Integer epsgCode) {
        notNull(EPSG_CODE, epsgCode);
        LOG.trace("Removing epsgCode {}", epsgCode);
        getEpsgCodesSet().remove(epsgCode);
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
        getFeaturesOfInterestForOfferingMap().removeWithKey(offering, featureOfInterest);
    }

    @Override
    public void removeFeatureOfInterestForResultTemplate(final String resultTemplate, final String featureOfInterest) {
        notNullOrEmpty(RESULT_TEMPLATE, resultTemplate);
        notNullOrEmpty(FEATURE_OF_INTEREST, featureOfInterest);
        LOG.trace("Removing featureOfInterest {} from resultTemplate {}", featureOfInterest, resultTemplate);
        getFeaturesOfInterestForResultTemplatesMap().removeWithKey(resultTemplate, featureOfInterest);
    }

    @Override
    public void removeFeaturesOfInterestForOffering(final String offering) {
        notNullOrEmpty(OFFERING, offering);
        LOG.trace("Removing featuresOfInterest for offering {}", offering);
        getFeaturesOfInterestForOfferingMap().remove(offering);
    }

    @Override
    public void removeFeaturesOfInterestForResultTemplate(final String resultTemplate) {
        notNullOrEmpty(RESULT_TEMPLATE, resultTemplate);
        LOG.trace("Removing featuresOfInterest for resultTemplate {}", resultTemplate);
        getFeaturesOfInterestForResultTemplatesMap().remove(resultTemplate);
    }

    @Override
    public void removeMaxPhenomenonTimeForOffering(final String offering) {
        notNullOrEmpty(OFFERING, offering);
        LOG.trace("Removing maxEventTime for offering {}", offering);
        getMaxPhenomenonTimeForOfferingsMap().remove(offering);
    }

    @Override
    public void removeMinPhenomenonTimeForOffering(final String offering) {
        notNullOrEmpty(OFFERING, offering);
        LOG.trace("Removing minEventTime for offering {}", offering);
        getMinPhenomenonTimeForOfferingsMap().remove(offering);
    }

    @Override
    public void removeMaxPhenomenonTimeForProcedure(final String procedure) {
        notNullOrEmpty(PROCEDURE, procedure);
        LOG.trace("Removing maxEventTime for procedure {}", procedure);
        getMaxPhenomenonTimeForProceduresMap().remove(procedure);
    }

    @Override
    public void removeMinPhenomenonTimeForProcedure(final String procedure) {
        notNullOrEmpty(PROCEDURE, procedure);
        LOG.trace("Removing minEventTime for procedure {}", procedure);
        getMinPhenomenonTimeForProceduresMap().remove(procedure);
    }

    @Override
    public void removeNameForOffering(final String offering) {
        notNullOrEmpty(OFFERING, offering);
        LOG.trace("Removing name for offering {}", offering);
        getNameForOfferingsMap().remove(offering);
    }

    @Override
    public void removeObservablePropertiesForCompositePhenomenon(final String compositePhenomenon) {
        notNullOrEmpty(OFFERING, compositePhenomenon);
        LOG.trace("Removing name observableProperties compositePhenomenon {}", compositePhenomenon);
        getObservablePropertiesForCompositePhenomenonsMap().remove(compositePhenomenon);
    }

    @Override
    public void removeObservablePropertiesForOffering(final String offering) {
        notNullOrEmpty(OFFERING, offering);
        LOG.trace("Removing observableProperties for offering {}", offering);
        getObservablePropertiesForOfferingsMap().remove(offering);
    }

    @Override
    public void removeObservablePropertiesForProcedure(final String procedure) {
        notNullOrEmpty(PROCEDURE, procedure);
        LOG.trace("Removing observableProperties for procedure {}", procedure);
        getObservablePropertiesForProceduresMap().remove(procedure);
    }

    @Override
    public void removeObservablePropertiesForResultTemplate(final String resultTemplate) {
        notNullOrEmpty(RESULT_TEMPLATE, resultTemplate);
        LOG.trace("Removing observableProperties for resultTemplate {}", resultTemplate);
        getObservablePropertiesForResultTemplatesMap().remove(resultTemplate);
    }

    @Override
    public void removeObservablePropertyForCompositePhenomenon(final String compositePhenomenon,
            final String observableProperty) {
        notNullOrEmpty(COMPOSITE_PHENOMENON, compositePhenomenon);
        notNullOrEmpty(OBSERVABLE_PROPERTY, observableProperty);
        LOG.trace("Removing observableProperty {} from compositePhenomenon {}", observableProperty,
                compositePhenomenon);
        getObservablePropertiesForCompositePhenomenonsMap().removeWithKey(compositePhenomenon, observableProperty);
    }

    @Override
    public void removeObservablePropertyForOffering(final String offering, final String observableProperty) {
        notNullOrEmpty(OFFERING, offering);
        notNullOrEmpty(OBSERVABLE_PROPERTY, observableProperty);
        LOG.trace("Removing observableProperty {} from offering {}", observableProperty, offering);
        getObservablePropertiesForOfferingsMap().removeWithKey(offering, observableProperty);
    }

    @Override
    public void removeObservablePropertyForProcedure(final String procedure, final String observableProperty) {
        notNullOrEmpty(PROCEDURE, procedure);
        notNullOrEmpty(OBSERVABLE_PROPERTY, observableProperty);
        LOG.trace("Removing observableProperty {} from procedure {}", observableProperty, procedure);
        getObservablePropertiesForProceduresMap().removeWithKey(procedure, observableProperty);
    }

    @Override
    public void removeObservablePropertyForResultTemplate(final String resultTemplate, final String observableProperty) {
        notNullOrEmpty(RESULT_TEMPLATE, resultTemplate);
        notNullOrEmpty(OBSERVABLE_PROPERTY, observableProperty);
        LOG.trace("Removing observableProperty {} from resultTemplate {}", observableProperty, resultTemplate);
        getObservablePropertiesForResultTemplatesMap().removeWithKey(resultTemplate, observableProperty);
    }

    @Override
    public void removeObservationTypeForOffering(final String offering, final String observationType) {
        notNullOrEmpty(OFFERING, offering);
        notNullOrEmpty(OBSERVATION_TYPE, observationType);
        LOG.trace("Removing observationType {} from offering {}", observationType, offering);
        getObservationTypesForOfferingsMap().removeWithKey(offering, observationType);
    }

    @Override
    public void removeObservationTypesForOffering(final String offering) {
        notNullOrEmpty(OFFERING, offering);
        LOG.trace("Removing observationTypes for offering {}", offering);
        getObservationTypesForOfferingsMap().remove(offering);
    }

    @Override
    public void removeOfferingForObservableProperty(final String observableProperty, final String offering) {
        notNullOrEmpty(OBSERVABLE_PROPERTY, observableProperty);
        notNullOrEmpty(OFFERING, offering);
        LOG.trace("Removing offering {} from observableProperty {}", offering, observableProperty);
        getOfferingsForObservablePropertiesMap().removeWithKey(observableProperty, offering);
    }

    @Override
    public void removeOfferingForProcedure(final String procedure, final String offering) {
        notNullOrEmpty(PROCEDURE, procedure);
        notNullOrEmpty(OFFERING, offering);
        LOG.trace("Removing offering {} from procedure {}", offering, procedure);
        getOfferingsForProceduresMap().removeWithKey(procedure, offering);
    }

    @Override
    public void removeOfferingsForObservableProperty(final String observableProperty) {
        notNullOrEmpty(OBSERVABLE_PROPERTY, observableProperty);
        LOG.trace("Removing offerings for observableProperty {}", observableProperty);
        getOfferingsForObservablePropertiesMap().remove(observableProperty);
    }

    @Override
    public void removeOfferingsForProcedure(final String procedure) {
        notNullOrEmpty(PROCEDURE, procedure);
        LOG.trace("Removing offering for procedure {}", procedure);
        getOfferingsForProceduresMap().remove(procedure);
    }

    @Override
    public void removeProcedureForFeatureOfInterest(final String featureOfInterest, final String procedure) {
        notNullOrEmpty(FEATURE_OF_INTEREST, featureOfInterest);
        notNullOrEmpty(PROCEDURE, procedure);
        LOG.trace("Removing procedure {} from featureOfInterest {}", procedure, featureOfInterest);
        getProceduresForFeaturesOfInterestMap().removeWithKey(featureOfInterest, procedure);
    }

    @Override
    public void removeProcedureForObservableProperty(final String observableProperty, final String procedure) {
        notNullOrEmpty(OBSERVABLE_PROPERTY, observableProperty);
        notNullOrEmpty(PROCEDURE, procedure);
        LOG.trace("Removing procedure {} from observableProperty {}", procedure, observableProperty);
        getProceduresForObservablePropertiesMap().removeWithKey(observableProperty, procedure);
    }

    @Override
    public void removeProcedureForOffering(final String offering, final String procedure) {
        notNullOrEmpty(OFFERING, offering);
        notNullOrEmpty(PROCEDURE, procedure);
        LOG.trace("Removing procedure {} from offering {}", procedure, offering);
        getProceduresForOfferingsMap().removeWithKey(offering, procedure);
    }

    @Override
    public void removeProceduresForFeatureOfInterest(final String featureOfInterest) {
        notNullOrEmpty(FEATURE_OF_INTEREST, featureOfInterest);
        LOG.trace("Removing procedures for featureOfInterest {}", featureOfInterest);
        getProceduresForFeaturesOfInterestMap().remove(featureOfInterest);
    }

    @Override
    public void removeProceduresForObservableProperty(final String observableProperty) {
        notNullOrEmpty(OBSERVABLE_PROPERTY, observableProperty);
        LOG.trace("Removing procedures for observableProperty {}", observableProperty);
        getProceduresForObservablePropertiesMap().remove(observableProperty);
    }

    @Override
    public void removeProceduresForOffering(final String offering) {
        notNullOrEmpty(OFFERING, offering);
        LOG.trace("Removing procedures for offering {}", offering);
        getProceduresForOfferingsMap().remove(offering);
    }

    @Override
    public void removeRelatedFeatureForOffering(final String offering, final String relatedFeature) {
        notNullOrEmpty(OFFERING, offering);
        notNullOrEmpty(RELATED_FEATURE, relatedFeature);
        LOG.trace("Removing relatedFeature {} from offering {}", relatedFeature, offering);
        getRelatedFeaturesForOfferingsMap().removeWithKey(offering, relatedFeature);
    }

    @Override
    public void removeRelatedFeaturesForOffering(final String offering) {
        notNullOrEmpty(OFFERING, offering);
        LOG.trace("Removing RelatedFeatures for offering {}", offering);
        getRelatedFeaturesForOfferingsMap().remove(offering);
    }

    @Override
    public void removeResultTemplateForOffering(final String offering, final String resultTemplate) {
        notNullOrEmpty(OFFERING, offering);
        notNullOrEmpty(RESULT_TEMPLATE, resultTemplate);
        LOG.trace("Removing resultTemplate {} from offering {}", resultTemplate, offering);
        getResultTemplatesForOfferingsMap().removeWithKey(offering, resultTemplate);
    }

    @Override
    public void removeResultTemplatesForOffering(final String offering) {
        notNullOrEmpty(OFFERING, offering);
        LOG.trace("Removing ResultTemplates for offering {}", offering);
        getResultTemplatesForOfferingsMap().remove(offering);
    }

    @Override
    public void removeRoleForRelatedFeature(final String relatedFeature, final String role) {
        notNullOrEmpty(RELATED_FEATURE, relatedFeature);
        notNullOrEmpty(ROLE, role);
        LOG.trace("Removing role {} from relatedFeature {}", role, relatedFeature);
        getRolesForRelatedFeaturesMap().removeWithKey(relatedFeature, role);
    }

    @Override
    public void removeRolesForRelatedFeature(final String relatedFeature) {
        notNullOrEmpty(RELATED_FEATURE, relatedFeature);
        LOG.trace("Removing roles for relatedFeature {}", relatedFeature);
        getRolesForRelatedFeaturesMap().remove(relatedFeature);
    }

    @Override
    public void removeRolesForRelatedFeatureNotIn(final Collection<String> relatedFeatures) {
        notNull(RELATED_FEATURES, relatedFeatures);
        final Iterator<String> iter = getRolesForRelatedFeaturesMap().keySet().iterator();
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
        getAllowedObservationTypesForOfferingsMap().put(offering, newValue);
    }

    @Override
    public void setAllowedFeatureOfInterestTypeForOffering(final String offering,
            final Collection<String> allowedFeatureOfInterestType) {
        notNullOrEmpty(OFFERING, offering);
        final Set<String> newValue = newSynchronizedSet(allowedFeatureOfInterestType);
        LOG.trace("Setting allowedFeatureOfInterestTypes for offering {} to {}", offering, newValue);
        getAllowedFeatureOfInterestTypesForOfferingsMap().put(offering, newValue);
    }

    @Override
    public void setCompositePhenomenonsForOffering(final String offering, final Collection<String> compositePhenomenons) {
        notNullOrEmpty(OFFERING, offering);
        final Set<String> newValue = newSynchronizedSet(compositePhenomenons);
        LOG.trace("Setting compositePhenomenons for offering {} to {}", offering, newValue);
        getCompositePhenomenonsForOfferingsMap().put(offering, newValue);
    }

    @Override
    public void setFeaturesOfInterestForOffering(final String offering, final Collection<String> featureOfInterest) {
        notNullOrEmpty(OFFERING, offering);
        final Set<String> newValue = newSynchronizedSet(featureOfInterest);
        LOG.trace("Setting featureOfInterest for offering {} to {}", offering, newValue);
        getFeaturesOfInterestForOfferingMap().put(offering, newValue);
    }

    @Override
    public void setGlobalEnvelope(final SosEnvelope globalEnvelope) {
        LOG.trace("Global envelope now: '{}'", getGlobalSpatialEnvelope());
        if (globalEnvelope == null) {
            setGlobalSpatialEnvelope(new SosEnvelope(null, getDefaultEPSGCode()));
        } else {
            setGlobalSpatialEnvelope(globalEnvelope);
        }
        LOG.trace("Global envelope updated to '{}' with '{}'", getGlobalSpatialEnvelope(), globalEnvelope);
    }

    @Override
    public void setMaxPhenomenonTime(final DateTime maxEventTime) {
        LOG.trace("Setting Maximal EventTime to {}", maxEventTime);
        getGlobalPhenomenonTimeEnvelope().setEnd(maxEventTime);
    }

    @Override
    public void setMinPhenomenonTime(final DateTime minEventTime) {
        LOG.trace("Setting Minimal EventTime to {}", minEventTime);
        getGlobalPhenomenonTimeEnvelope().setStart(minEventTime);
    }

    @Override
    public void setObservablePropertiesForResultTemplate(final String resultTemplate,
            final Collection<String> observableProperties) {
        notNullOrEmpty(RESULT_TEMPLATE, resultTemplate);
        final Set<String> newValue = newSynchronizedSet(observableProperties);
        LOG.trace("Setting observableProperties for resultTemplate {} to {}", resultTemplate, newValue);
        getObservablePropertiesForResultTemplatesMap().put(resultTemplate, newValue);
    }

    @Override
    public void addParentFeature(final String featureOfInterest, final String parentFeature) {
        notNullOrEmpty(FEATURE_OF_INTEREST, featureOfInterest);
        notNullOrEmpty(PARENT_FEATURE, parentFeature);
        LOG.trace("Adding parentFeature {} to featureOfInterest {}", parentFeature, featureOfInterest);
        getParentFeaturesForFeaturesOfInterestMap().add(featureOfInterest, parentFeature);
        getChildFeaturesForFeaturesOfInterestMap().add(parentFeature, featureOfInterest);
    }

    @Override
    public void addParentFeatures(final String featureOfInterest, final Collection<String> parentFeatures) {
        notNullOrEmpty(FEATURE_OF_INTEREST, featureOfInterest);
        noNullOrEmptyValues(PARENT_FEATURES, parentFeatures);
        LOG.trace("Adding parentFeature {} to featureOfInterest {}", parentFeatures, featureOfInterest);
        getParentFeaturesForFeaturesOfInterestMap().addAll(featureOfInterest, parentFeatures);
        for (final String parentFeature : parentFeatures) {
            getChildFeaturesForFeaturesOfInterestMap().add(parentFeature, featureOfInterest);
        }
    }

    @Override
    public void addParentProcedure(final String procedure, final String parentProcedure) {
        notNullOrEmpty(PROCEDURE, procedure);
        notNullOrEmpty(PARENT_PROCEDURE, parentProcedure);
        LOG.trace("Adding parentProcedure {} to procedure {}", parentProcedure, procedure);
        getParentProceduresForProceduresMap().add(procedure, parentProcedure);
        getChildProceduresForProceduresMap().add(parentProcedure, procedure);
    }

    @Override
    public void addParentProcedures(final String procedure, final Collection<String> parentProcedures) {
        notNullOrEmpty(PROCEDURE, procedure);
        noNullOrEmptyValues(PARENT_PROCEDURES, parentProcedures);
        LOG.trace("Adding parentProcedures {} to procedure {}", parentProcedures, procedure);
        getParentProceduresForProceduresMap().addAll(procedure, parentProcedures);
        for (final String parentProcedure : parentProcedures) {
            getChildProceduresForProceduresMap().add(parentProcedure, procedure);
        }
    }

    @Override
    public void updateEnvelopeForOffering(final String offering, final Envelope envelope) {
        notNullOrEmpty(OFFERING, offering);
        notNull(ENVELOPE, envelope);
        if (hasEnvelopeForOffering(offering)) {
            final SosEnvelope offeringEnvelope = getEnvelopeForOfferingsMap().get(offering);
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
            LOG.trace("Expanding envelope {} to include {}", getGlobalSpatialEnvelope(), envelope);
            getGlobalSpatialEnvelope().expandToInclude(envelope);
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
        getMaxResultTimeForOfferingsMap().remove(offering);
    }

    @Override
    public void removeMinResultTimeForOffering(final String offering) {
        notNullOrEmpty(OFFERING, offering);
        LOG.trace("Removing minResultTime for offering {}", offering);
        getMinResultTimeForOfferingsMap().remove(offering);
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
        getGlobalResultTimeEnvelope().setEnd(maxResultTime);
    }

    @Override
    public void setMaxResultTimeForOffering(final String offering, final DateTime maxTime) {
        notNullOrEmpty(OFFERING, offering);
        LOG.trace("Setting maximal ResultTime for Offering {} to {}", offering, maxTime);
        if (maxTime == null) {
            getMaxResultTimeForOfferingsMap().remove(offering);
        } else {
            getMaxResultTimeForOfferingsMap().put(offering, maxTime);
        }
    }

    @Override
    public void setMinResultTime(final DateTime minResultTime) {
        LOG.trace("Setting Minimal ResultTime to {}", minResultTime);
        getGlobalResultTimeEnvelope().setStart(minResultTime);
    }

    @Override
    public void setMinResultTimeForOffering(final String offering, final DateTime minTime) {
        notNullOrEmpty(OFFERING, offering);
        LOG.trace("Setting minimal ResultTime for Offering {} to {}", offering, minTime);
        if (minTime == null) {
            getMinResultTimeForOfferingsMap().remove(offering);
        } else {
            getMinResultTimeForOfferingsMap().put(offering, minTime);
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
        getFeaturesOfInterestSet().clear();
    }

    @Override
    public void clearProceduresForFeatureOfInterest() {
        LOG.trace("Clearing procedures for feature of interest");
        getProceduresForFeaturesOfInterestMap().clear();
    }

    @Override
    public void clearFeatureHierarchy() {
        LOG.trace("Clearing feature hierarchy");
        getChildFeaturesForFeaturesOfInterestMap().clear();
        getParentFeaturesForFeaturesOfInterestMap().clear();
    }

    @Override
    public void clearProceduresForOfferings() {
        LOG.trace("Clearing procedures for offerings");
        getProceduresForOfferingsMap().clear();
    }

    @Override
    public void clearNameForOfferings() {
        LOG.trace("Clearing names for offerings");
        getNameForOfferingsMap().clear();
    }

    @Override
    public void clearI18nNamesForOfferings() {
        LOG.trace("Clearing i18n names for offerings");
        getI18nNameForOfferingsMap().clear();

    }

    @Override
    public void clearI18nDescriptionsNameForOfferings() {
        LOG.trace("Clearing i18n descriptions for offerings");
        getI18nDescriptionForOfferingsMap().clear();
    }

    @Override
    public void clearObservablePropertiesForOfferings() {
        LOG.trace("Clearing observable properties for offerings");
        getObservablePropertiesForOfferingsMap().clear();
    }

    @Override
    public void clearRelatedFeaturesForOfferings() {
        LOG.trace("Clearing related features for offerings");
        getRelatedFeaturesForOfferingsMap().clear();
    }

    @Override
    public void clearObservationTypesForOfferings() {
        LOG.trace("Clearing observation types for offerings");
        getObservationTypesForOfferingsMap().clear();
    }

    @Override
    public void clearAllowedObservationTypeForOfferings() {
        LOG.trace("Clearing allowed observation types for offerings");
        getAllowedObservationTypesForOfferingsMap().clear();
    }

    @Override
    public void clearEnvelopeForOfferings() {
        LOG.trace("Clearing envelope for offerings");
        getEnvelopeForOfferingsMap().clear();
    }

    @Override
    public void clearFeaturesOfInterestForOfferings() {
        LOG.trace("Clearing features of interest for offerings");
        getFeaturesOfInterestForOfferingMap().clear();
    }

    @Override
    public void clearMinPhenomenonTimeForOfferings() {
        LOG.trace("Clearing min phenomenon time for offerings");
        getMinPhenomenonTimeForOfferingsMap().clear();
    }

    @Override
    public void clearMaxPhenomenonTimeForOfferings() {
        LOG.trace("Clearing max phenomenon time for offerings");
        getMaxPhenomenonTimeForOfferingsMap().clear();
    }

    @Override
    public void clearMinPhenomenonTimeForProcedures() {
        LOG.trace("Clearing min phenomenon time for procedures");
        getMinPhenomenonTimeForProceduresMap().clear();
    }

    @Override
    public void clearMaxPhenomenonTimeForProcedures() {
        LOG.trace("Clearing max phenomenon time for procedures");
        getMaxPhenomenonTimeForProceduresMap().clear();
    }

    @Override
    public void clearMinResultTimeForOfferings() {
        LOG.trace("Clearing min result time for offerings");
        getMinResultTimeForOfferingsMap().clear();
    }

    @Override
    public void clearMaxResultTimeForOfferings() {
        LOG.trace("Clearing max result time for offerings");
        getMaxResultTimeForOfferingsMap().clear();
    }

    @Override
    public void clearOfferings() {
        LOG.trace("Clearing offerings");
        getOfferingsSet().clear();
    }

    @Override
    public void addOffering(final String offering) {
        notNullOrEmpty(OFFERING, offering);
        LOG.trace("Adding offering {}", offering);
        getOfferingsSet().add(offering);
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
        getOfferingsSet().remove(offering);
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
        getHiddenChildProceduresForOfferingsMap().add(offering, procedure);
    }

    @Override
    public void removeHiddenChildProcedureForOffering(final String offering, final String procedure) {
        notNullOrEmpty(OFFERING, offering);
        notNullOrEmpty(PROCEDURE, procedure);
        LOG.trace("Removing hidden chil procedure {} from offering {}", procedure, offering);
        getHiddenChildProceduresForOfferingsMap().removeWithKey(offering, procedure);
    }

    @Override
    public void setHiddenChildProceduresForOffering(final String offering, final Collection<String> procedures) {
        final Set<String> newValue = newSynchronizedSet(procedures);
        LOG.trace("Setting hidden child Procedures for Offering {} to {}", offering, newValue);
        getHiddenChildProceduresForOfferingsMap().put(offering, newValue);
    }

    @Override
    public void clearHiddenChildProceduresForOfferings() {
        LOG.trace("Clearing hidden child procedures for offerings");
        getHiddenChildProceduresForOfferingsMap().clear();
    }

    @Override
    public void removeSpatialFilteringProfileEnvelopeForOffering(String offering) {
        notNullOrEmpty(OFFERING, offering);
        LOG.trace("Removing Spatial Filtering Profile envelope for offering {}", offering);
        getSpatialFilteringProfileEnvelopeForOfferingsMap().remove(offering);
    }

    @Override
    public void setSpatialFilteringProfileEnvelopeForOffering(String offering, SosEnvelope envelope) {
        LOG.trace("Setting Spatial Filtering Profile Envelope for Offering {} to {}", offering, envelope);
        getSpatialFilteringProfileEnvelopeForOfferingsMap().put(offering, copyOf(envelope));
    }

    @Override
    public void updateSpatialFilteringProfileEnvelopeForOffering(String offering, Envelope envelope) {
        notNullOrEmpty(OFFERING, offering);
        notNull(ENVELOPE, envelope);
        if (hasSpatialFilteringProfileEnvelopeForOffering(offering)) {
            final SosEnvelope offeringEnvelope = getSpatialFilteringProfileEnvelopeForOfferingsMap().get(offering);
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
        getSpatialFilteringProfileEnvelopeForOfferingsMap().clear();
    }

    @Override
    public void addFeatureOfInterestTypesForOffering(String offering, String featureOfInterestType) {
        notNullOrEmpty(OFFERING, offering);
        notNullOrEmpty(FEATURE_OF_INTEREST_TYPE, featureOfInterestType);
        LOG.trace("Adding observationType {} to offering {}", featureOfInterestType, offering);
        getFeatureOfInterestTypesForOfferingsMap().add(offering, featureOfInterestType);
    }

    @Override
    public void removeFeatureOfInterestTypeForOffering(String offering, String featureOfInterestType) {
        notNullOrEmpty(OFFERING, offering);
        notNullOrEmpty(FEATURE_OF_INTEREST_TYPE, featureOfInterestType);
        LOG.trace("Removing observationType {} from offering {}", featureOfInterestType, offering);
        getFeatureOfInterestTypesForOfferingsMap().removeWithKey(offering, featureOfInterestType);
    }

    @Override
    public void removeFeatureOfInterestTypesForOffering(String offering) {
        notNullOrEmpty(OFFERING, offering);
        LOG.trace("Removing featureOfInterestTypes for offering {}", offering);
        getFeatureOfInterestTypesForOfferingsMap().remove(offering);
    }

    @Override
    public void setFeatureOfInterestTypesForOffering(String offering, Collection<String> featureOfInterestTypes) {
        final Set<String> newValue = newSynchronizedSet(featureOfInterestTypes);
        LOG.trace("Setting FeatureOfInterestTypes for Offering {} to {}", offering, newValue);
        getFeatureOfInterestTypesForOfferingsMap().put(offering, newValue);
    }

    @Override
    public void addAllowedFeatureOfInterestTypeForOffering(String offering, String allowedFeatureOfInterestType) {
        notNullOrEmpty(OFFERING, offering);
        notNullOrEmpty(ALLOWED_FEATURE_OF_INTEREST_TYPE, allowedFeatureOfInterestType);
        LOG.trace("Adding AllowedFeatureOfInterestType {} to Offering {}", allowedFeatureOfInterestType, offering);
        getAllowedFeatureOfInterestTypesForOfferingsMap().add(offering, allowedFeatureOfInterestType);
    }

    @Override
    public void addAllowedFeatureOfInterestTypesForOffering(String offering,
            Collection<String> allowedFeatureOfInterestTypes) {
        notNullOrEmpty(OFFERING, offering);
        noNullValues(ALLOWED_FEATURE_OF_INTEREST_TYPES, allowedFeatureOfInterestTypes);
        LOG.trace("Adding AllowedFeatureOfInterestTypes {} to Offering {}", allowedFeatureOfInterestTypes, offering);
        getAllowedFeatureOfInterestTypesForOfferingsMap().addAll(offering, allowedFeatureOfInterestTypes);
    }

    @Override
    public void addSupportedLanguage(Locale language) {
        notNull(SUPPORTED_LANGUAGE, language);
        LOG.trace("Adding Language {}", language);
        getSupportedLanguageSet().add(language);
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
        getSupportedLanguageSet().clear();
    }

    @Override
    public void removeSupportedLanguage(Locale language) {
        LOG.trace("Removing Language {}", language);
        getSupportedLanguageSet().remove(language);
    }

    @Override
    public void setRequestableProcedureDescriptionFormat(Collection<String> formats) {
        LOG.trace("Adding requestable procedureDescriptionFormat");
        getRequestableProcedureDescriptionFormats().addAll(formats);
    }

    /**
     * Check if identifier and humanReadableName already contained in the map,
     * if not, add the mapping.
     * 
     * @param identifier
     *            Identifier to check
     * @param humanReadableName
     *            Human readable name to check
     * @param map
     *            Map of type
     * @param type
     *            Text type to check, e.g. procedure
     */
    protected void checkAndAddIdentifierHumanReadableName(String identifier, String humanReadableName,
            Map<String, String> map, String type) {
        if (StringHelper.isNotEmpty(identifier) && StringHelper.isNotEmpty(humanReadableName)) {
            if (!map.containsKey(humanReadableName) && !map.containsValue(identifier)) {
                map.put(humanReadableName, identifier);
            } else if (map.containsKey(humanReadableName) && !map.containsValue(identifier)) {
                LOG.error("Duplicity of the {} humanReadableName '{}'", type, humanReadableName);
            } else if (!map.containsKey(humanReadableName) && map.containsValue(identifier)) {
                LOG.error("Duplicity of the {} identifier '{}'", type, identifier);
            } else if (!identifier.equals(map.get(humanReadableName))) {
                LOG.error("Duplicity of the {} humanReadableName '{}' and identifier '{}'", type, humanReadableName,
                        identifier);
            }
        }
    }

    @Override
    public void addFeatureOfInterestIdentifierHumanReadableName(String identifier, String humanReadableName) {
        checkAndAddIdentifierHumanReadableName(identifier, humanReadableName,
                getFeatureOfInterestIdentifierForHumanReadableName(), "featureOfInterest");
    }

    @Override
    public void addObservablePropertyIdentifierHumanReadableName(String identifier, String humanReadableName) {
        checkAndAddIdentifierHumanReadableName(identifier, humanReadableName,
                getObservablePropertyIdentifierForHumanReadableName(), "observableProperty");
    }

    @Override
    public void addProcedureIdentifierHumanReadableName(String identifier, String humanReadableName) {
        checkAndAddIdentifierHumanReadableName(identifier, humanReadableName,
                getProcedureIdentifierForHumanReadableName(), "procedure");
    }

    @Override
    public void addOfferingIdentifierHumanReadableName(String identifier, String humanReadableName) {
        checkAndAddIdentifierHumanReadableName(identifier, humanReadableName,
                getOfferingIdentifierForHumanReadableName(), "offering");
    }

    @Override
    public void removeFeatureOfInterestIdentifierForHumanReadableName(String humanReadableName) {
        notNullOrEmpty(FEATURE_OF_INTEREST_NAME, humanReadableName);
        LOG.trace("Removing featuresOfInterest identifier for humanReadableName {}", humanReadableName);
        getFeatureOfInterestIdentifierForHumanReadableName().remove(humanReadableName);
        if (getFeatureOfInterestIdentifierForHumanReadableName().containsKey(humanReadableName)) {
            removeFeatureOfInterestHumanReadableNameForIdentifier(getFeatureOfInterestIdentifierForHumanReadableName()
                    .get(humanReadableName));
        }
    }

    @Override
    public void removeFeatureOfInterestHumanReadableNameForIdentifier(String identifier) {
        notNullOrEmpty(FEATURE_OF_INTEREST, identifier);
        LOG.trace("Removing featuresOfInterest human readable name for identifier {}", identifier);
        getFeatureOfInterestHumanReadableNameForIdentifier().remove(identifier);
    }

    @Override
    public void removeObservablePropertyIdentifierForHumanReadableName(String humanReadableName) {
        notNullOrEmpty(OBSERVABLE_PROPERTY_NAME, humanReadableName);
        LOG.trace("Removing featuresOfInterest identifier for humanReadableName {}", humanReadableName);
        getObservablePropertyIdentifierForHumanReadableName().remove(humanReadableName);
        if (getObservablePropertyIdentifierForHumanReadableName().containsKey(humanReadableName)) {
            removeObservablePropertyHumanReadableNameForIdentifier(getObservablePropertyIdentifierForHumanReadableName()
                    .get(humanReadableName));
        }
    }

    @Override
    public void removeObservablePropertyHumanReadableNameForIdentifier(String identifier) {
        notNullOrEmpty(OBSERVABLE_PROPERTY, identifier);
        LOG.trace("Removing observableProperty human readable name for identifier {}", identifier);
        getObservablePropertyHumanReadableNameForIdentifier().remove(identifier);
    }

    @Override
    public void removeProcedureIdentifierForHumanReadableName(String humanReadableName) {
        notNullOrEmpty(PROCEDURE_NAME, humanReadableName);
        LOG.trace("Removing procedure identifier for humanReadableName {}", humanReadableName);
        getProcedureIdentifierForHumanReadableName().remove(humanReadableName);
        if (getProcedureIdentifierForHumanReadableName().containsKey(humanReadableName)) {
            removeProcedureHumanReadableNameForIdentifier(getProcedureIdentifierForHumanReadableName().get(
                    humanReadableName));
        }
    }

    @Override
    public void removeProcedureHumanReadableNameForIdentifier(String identifier) {
        notNullOrEmpty(PROCEDURE, identifier);
        LOG.trace("Removing procedure human readable name for identifier {}", identifier);
        getProcedureHumanReadableNameForIdentifier().remove(identifier);
    }

    @Override
    public void removeOfferingIdentifierForHumanReadableName(String humanReadableName) {
        notNullOrEmpty(OFFERING_NAME, humanReadableName);
        LOG.trace("Removing offering identifier for humanReadableName {}", humanReadableName);
        getOfferingIdentifierForHumanReadableName().remove(humanReadableName);
        if (getOfferingIdentifierForHumanReadableName().containsKey(humanReadableName)) {
            removeOfferingHumanReadableNameForIdentifier(getOfferingIdentifierForHumanReadableName().get(
                    humanReadableName));
        }
    }

    @Override
    public void removeOfferingHumanReadableNameForIdentifier(String identifier) {
        notNullOrEmpty(OFFERING, identifier);
        LOG.trace("Removing offering human readable name for identifier {}", identifier);
        getOfferingHumanReadableNameForIdentifier().remove(identifier);
    }

    @Override
    public void clearFeatureOfInterestIdentifierHumanReadableNameMaps() {
        getFeatureOfInterestIdentifierForHumanReadableName().clear();
    }

    @Override
    public void clearObservablePropertyIdentifierHumanReadableNameMaps() {
        getObservablePropertyIdentifierForHumanReadableName().clear();
    }

    @Override
    public void clearProcedureIdentifierHumanReadableNameMaps() {
        getProcedureIdentifierForHumanReadableName().clear();
    }

    @Override
    public void clearOfferingIdentifierHumanReadableNameMaps() {
        getOfferingIdentifierForHumanReadableName().clear();
    }

}
