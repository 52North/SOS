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

import static org.n52.sos.util.SosHelper.getHierarchy;

import java.util.Locale;
import java.util.Set;

import org.joda.time.DateTime;
import org.n52.sos.i18n.LocalizedString;
import org.n52.sos.i18n.MultilingualString;
import org.n52.sos.ogc.sos.SosEnvelope;
import org.n52.sos.util.CollectionHelper;

/**
 * {@code ContentCache} implementation that offers a readable interface to the
 * cache. All methods return unmodifiable views of the cache.
 *
 * @author Christian Autermann <c.autermann@52north.org>
 * @author <a href="mailto:e.h.juerrens@52north.org">Eike Hinderk
 *         J&uuml;rrens</a>
 *
 * @since 4.0.0
 */
public class ReadableCache extends AbstractContentCache {
    private static final long serialVersionUID = -2197601373007656256L;

    @Override
    public DateTime getMaxPhenomenonTime() {
        return getGlobalPhenomenonTimeEnvelope().getEnd();
    }

    @Override
    public DateTime getMinPhenomenonTime() {
        return getGlobalPhenomenonTimeEnvelope().getStart();
    }

    @Override
    public Set<Integer> getEpsgCodes() {
        return copyOf(getEpsgCodesSet());
    }

    @Override
    public Set<String> getFeaturesOfInterest() {
        return copyOf(getFeaturesOfInterestSet());
    }

    @Override
    public Set<String> getProcedures() {
        return copyOf(getProceduresSet());
    }

    @Override
    public Set<String> getResultTemplates() {
        return copyOf(getResultTemplatesSet());
    }

    @Override
    public SosEnvelope getGlobalEnvelope() {
        return copyOf(getGlobalSpatialEnvelope());
    }

    @Override
    public Set<String> getOfferings() {
        return copyOf(getOfferingsSet());
    }

    @Override
    public Set<String> getOfferingsForObservableProperty(final String observableProperty) {
        return copyOf(getOfferingsForObservablePropertiesMap().get(observableProperty));
    }

    @Override
    public Set<String> getOfferingsForProcedure(final String procedure) {
        return copyOf(getOfferingsForProceduresMap().get(procedure));
    }

    @Override
    public Set<String> getProceduresForFeatureOfInterest(final String featureOfInterest) {
        return copyOf(getProceduresForFeaturesOfInterestMap().get(featureOfInterest));
    }

    @Override
    public Set<String> getProceduresForObservableProperty(final String observableProperty) {
        return copyOf(getProceduresForObservablePropertiesMap().get(observableProperty));
    }

    @Override
    public Set<String> getProceduresForOffering(final String offering) {
        return copyOf(getProceduresForOfferingsMap().get(offering));
    }

    @Override
    public Set<String> getHiddenChildProceduresForOffering(final String offering) {
        return copyOf(getHiddenChildProceduresForOfferingsMap().get(offering));
    }

    @Override
    public Set<String> getRelatedFeaturesForOffering(final String offering) {
        return copyOf(getRelatedFeaturesForOfferingsMap().get(offering));
    }

    @Override
    public Set<String> getResultTemplatesForOffering(final String offering) {
        return copyOf(getResultTemplatesForOfferingsMap().get(offering));
    }

    @Override
    public Set<String> getRolesForRelatedFeature(final String relatedFeature) {
        return copyOf(getRolesForRelatedFeaturesMap().get(relatedFeature));
    }

    @Override
    public SosEnvelope getEnvelopeForOffering(final String offering) {
        return copyOf(getEnvelopeForOfferingsMap().get(offering));
    }

    @Override
    public String getNameForOffering(final String offering) {
        return getNameForOfferingsMap().get(offering);
    }


    @Override
    public LocalizedString getI18nNameForOffering(String offering, Locale i18n) {
        MultilingualString map = getI18nNameForOfferingsMap().get(offering);
        if (map != null) {
            return map.getLocalization(i18n).orNull();
        }
        return null;
    }

    @Override
    public MultilingualString getI18nNamesForOffering(String offering) {
        return getI18nNameForOfferingsMap().get(offering);
    }


    @Override
    public boolean hasI18NNamesForOffering(String offering, Locale i18n) {
        return getI18nNameForOfferingsMap().containsKey(offering) && getI18nNamesForOffering(offering).hasLocale(i18n);
    }

    @Override
    public LocalizedString getI18nDescriptionForOffering(String offering, Locale i18n) {
        MultilingualString map = getI18nDescriptionForOfferingsMap().get(offering);
        if (map != null) {
            return map.getLocalization(i18n).orNull();
        }
        return null;
    }

    @Override
    public MultilingualString getI18nDescriptionsForOffering(String offering) {
        return getI18nDescriptionForOfferingsMap().get(offering);
    }

    @Override
    public boolean hasI18NDescriptionForOffering(String offering, Locale i18n) {
        return getI18nDescriptionForOfferingsMap().containsKey(offering) && getI18nDescriptionForOfferingsMap().get(offering).hasLocale(i18n);
    }

    @Override
    public Set<String> getCompositePhenomenonsForOffering(final String offering) {
        return copyOf(getCompositePhenomenonsForOfferingsMap().get(offering));
    }

    @Override
    public Set<String> getObservablePropertiesForCompositePhenomenon(final String compositePhenomenon) {
        return copyOf(getObservablePropertiesForCompositePhenomenonsMap().get(compositePhenomenon));
    }

    @Override
    public DateTime getMaxPhenomenonTimeForOffering(final String offering) {
        return getMaxPhenomenonTimeForOfferingsMap().get(offering);
    }

    @Override
    public DateTime getMinPhenomenonTimeForOffering(final String offering) {
        return getMinPhenomenonTimeForOfferingsMap().get(offering);
    }

    @Override
    public DateTime getMaxPhenomenonTimeForProcedure(final String procedure) {
        DateTime maxTime = null;
        for (final String thisProcedure : getChildProcedures(procedure, true, true)) {
            if (getMaxPhenomenonTimeForProceduresMap().get(thisProcedure) != null) {
                final DateTime thisTime = getMaxPhenomenonTimeForProceduresMap().get(thisProcedure);
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
            if (getMinPhenomenonTimeForProceduresMap().get(thisProcedure) != null) {
                final DateTime thisTime = getMinPhenomenonTimeForProceduresMap().get(thisProcedure);
                if (minTime == null || minTime.isBefore(thisTime)) {
                    minTime = thisTime;
                }
            }
        }
        return minTime;
    }

    @Override
    public Set<String> getAllowedObservationTypesForOffering(final String offering) {
        return copyOf(getAllowedObservationTypesForOfferingsMap().get(offering));
    }

    @Override
    public Set<String> getFeaturesOfInterestForOffering(final String offering) {
        return copyOf(getFeaturesOfInterestForOfferingMap().get(offering));
    }

    @Override
    public Set<String> getFeaturesOfInterestForResultTemplate(final String resultTemplate) {
        return copyOf(getFeaturesOfInterestForResultTemplatesMap().get(resultTemplate));
    }

    @Override
    public Set<String> getObservablePropertiesForOffering(final String offering) {
        final Set<String> result = copyOf(getObservablePropertiesForOfferingsMap().get(offering));
        final Set<String> compositePhenomenonsForOffering = getCompositePhenomenonsForOfferingsMap().get(offering);
        if (compositePhenomenonsForOffering != null) {
            for (final String cp : compositePhenomenonsForOffering) {
                result.addAll(getObservablePropertiesForCompositePhenomenon(cp));
            }
        }
        return result;
    }

    @Override
    public Set<String> getObservablePropertiesForProcedure(final String procedure) {
        return copyOf(getObservablePropertiesForProceduresMap().get(procedure));
    }

    @Override
    public Set<String> getObservationTypesForOffering(final String offering) {
        return copyOf(getObservationTypesForOfferingsMap().get(offering));
    }

    @Override
    public Set<String> getObservablePropertiesForResultTemplate(final String resultTemplate) {
        return copyOf(getObservablePropertiesForResultTemplatesMap().get(resultTemplate));
    }

    @Override
    public Set<String> getParentProcedures(final String procedureIdentifier, final boolean fullHierarchy,
            final boolean includeSelf) {
        return getHierarchy(getParentProceduresForProceduresMap(), procedureIdentifier, fullHierarchy, includeSelf);
    }

    @Override
    public Set<String> getParentFeatures(final String featureIdentifier, final boolean fullHierarchy,
            final boolean includeSelf) {
        return getHierarchy(getParentFeaturesForFeaturesOfInterestMap(), featureIdentifier, fullHierarchy, includeSelf);
    }

    @Override
    public Set<String> getChildProcedures(final String procedureIdentifier, final boolean fullHierarchy,
            final boolean includeSelf) {
        return getHierarchy(getChildProceduresForProceduresMap(), procedureIdentifier, fullHierarchy, includeSelf);
    }

    @Override
    public Set<String> getChildProcedures(final Set<String> procedureIdentifiers, final boolean fullHierarchy,
            final boolean includeSelves) {
        return getHierarchy(getChildProceduresForProceduresMap(), procedureIdentifiers, fullHierarchy, includeSelves);
    }

    @Override
    public Set<String> getChildFeatures(final String featureIdentifier, final boolean fullHierarchy,
            final boolean includeSelf) {
        return getHierarchy(getChildFeaturesForFeaturesOfInterestMap(), featureIdentifier, fullHierarchy, includeSelf);
    }

    @Override
    public Set<String> getParentProcedures(final Set<String> procedureIdentifiers, final boolean fullHierarchy,
            final boolean includeSelves) {
        return getHierarchy(getParentProceduresForProceduresMap(), procedureIdentifiers, fullHierarchy, includeSelves);
    }

    @Override
    public Set<String> getParentFeatures(final Set<String> featureIdentifiers, final boolean fullHierarchy,
            final boolean includeSelves) {
        return getHierarchy(getParentFeaturesForFeaturesOfInterestMap(), featureIdentifiers, fullHierarchy,
                includeSelves);
    }

    @Override
    public Set<String> getFeaturesOfInterestWithResultTemplate() {
        return CollectionHelper.unionOfListOfLists(getFeaturesOfInterestForResultTemplatesMap().values());
    }

    @Override
    public Set<String> getObservableProperties() {
        return CollectionHelper.unionOfListOfLists(getObservablePropertiesForOfferingsMap().values());
    }

    @Override
    public Set<String> getObservablePropertiesWithResultTemplate() {
        return CollectionHelper.unionOfListOfLists(getObservablePropertiesForResultTemplatesMap().values());
    }

    @Override
    public Set<String> getOfferingsWithResultTemplate() {
        return copyOf(getResultTemplatesForOfferingsMap().keySet());
    }

    @Override
    public Set<String> getRelatedFeatures() {
        return CollectionHelper.unionOfListOfLists(getRelatedFeaturesForOfferingsMap().values());
    }

    @Override
    public boolean hasFeatureOfInterest(final String featureOfInterest) {
        return getFeaturesOfInterest().contains(featureOfInterest);
    }

    @Override
    public boolean hasObservableProperty(final String observableProperty) {
        return getObservableProperties().contains(observableProperty);
    }

    @Override
    public boolean hasObservationType(final String observationType) {
        return getObservationTypes().contains(observationType);
    }

    @Override
    public boolean hasOffering(final String offering) {
        return getOfferings().contains(offering);
    }

    @Override
    public boolean hasProcedure(final String procedure) {
        return getProcedures().contains(procedure);
    }

    @Override
    public boolean hasRelatedFeature(final String relatedFeature) {
        return getRelatedFeatures().contains(relatedFeature);
    }

    @Override
    public boolean hasResultTemplate(final String resultTemplate) {
        return getResultTemplates().contains(resultTemplate);
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
            if (getMaxPhenomenonTimeForProceduresMap().get(thisProcedure) != null) {
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean hasMinPhenomenonTimeForProcedure(final String procedure) {
        for (final String thisProcedure : getChildProcedures(procedure, true, true)) {
            if (getMinPhenomenonTimeForProceduresMap().get(thisProcedure) != null) {
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
        return getGlobalResultTimeEnvelope().getEnd();
    }

    @Override
    public boolean hasMaxResultTime() {
        return getMaxResultTime() != null;
    }

    @Override
    public DateTime getMaxResultTimeForOffering(final String offering) {
        return getMaxResultTimeForOfferingsMap().get(offering);
    }

    @Override
    public boolean hasMaxResultTimeForOffering(final String offering) {
        return getMaxResultTimeForOffering(offering) != null;
    }

    @Override
    public DateTime getMinResultTime() {
        return getGlobalResultTimeEnvelope().getStart();
    }

    @Override
    public boolean hasMinResultTime() {
        return getMinResultTime() != null;
    }

    @Override
    public DateTime getMinResultTimeForOffering(final String offering) {
        return getMinResultTimeForOfferingsMap().get(offering);
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
        return copyOf(getSpatialFilteringProfileEnvelopeForOfferingsMap().get(offering));
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
        return copyOf(getFeatureOfInterestTypesForOfferingsMap().get(offering));
    }

    @Override
    public Set<String> getAllowedFeatureOfInterestTypesForOffering(String offering) {
        return copyOf(getAllowedFeatureOfInterestTypesForOfferingsMap().get(offering));
    }

    @Override
    public Set<Locale> getSupportedLanguages() {
        return copyOf(getSupportedLanguageSet());
    }

    @Override
    public boolean hasSupportedLanguage() {
        return CollectionHelper.isNotEmpty(getSupportedLanguageSet());
    }

    @Override
    public boolean isLanguageSupported(final Locale language) {
        return getSupportedLanguageSet().contains(language);
    }
    
    @Override
    public Set<String> getRequstableProcedureDescriptionFormat() {
        return getRequestableProcedureDescriptionFormats();
    }
    
    @Override
    public boolean hasRequstableProcedureDescriptionFormat(String format) {
        return getRequestableProcedureDescriptionFormats().contains(format);
    }
    
    @Override
    public String getFeatureOfInterestIdentifierForHumanReadableName(String humanReadableName) {
    	if (getFeatureOfInterestIdentifierForHumanReadableName().containsKey(humanReadableName)) {
    		return getFeatureOfInterestIdentifierForHumanReadableName().get(humanReadableName);
    	}
    	return humanReadableName;
    }
    
    @Override
    public String getFeatureOfInterestHumanReadableNameForIdentifier(String identifier) {
    	if (getFeatureOfInterestHumanReadableNameForIdentifier().containsKey(identifier)) {
    		return getFeatureOfInterestHumanReadableNameForIdentifier().get(identifier);
    	}
    	return identifier;
    }
    
    @Override
    public String getObservablePropertyIdentifierForHumanReadableName(String humanReadableName) {
    	if (getObservablePropertyIdentifierForHumanReadableName().containsKey(humanReadableName)) {
    		return getObservablePropertyIdentifierForHumanReadableName().get(humanReadableName);
    	}
    	return humanReadableName;
    }
    
    @Override
    public String getObservablePropertyHumanReadableNameForIdentifier(String identifier) {
    	if (getObservablePropertyHumanReadableNameForIdentifier().containsKey(identifier)) {
    		return getObservablePropertyHumanReadableNameForIdentifier().get(identifier);
    	}
    	return identifier;
    }
    
    @Override
    public String getProcedureIdentifierForHumanReadableName(String humanReadableName) {
    	if (getProcedureIdentifierForHumanReadableName().containsKey(humanReadableName)) {
    		return getProcedureIdentifierForHumanReadableName().get(humanReadableName);
    	}
    	return humanReadableName;
    }
    
    @Override
    public String getProcedureHumanReadableNameForIdentifier(String identifier) {
    	if (getProcedureHumanReadableNameForIdentifier().containsKey(identifier)) {
    		return getProcedureHumanReadableNameForIdentifier().get(identifier);
    	}
    	return identifier;
    }
    
    @Override
    public String getOfferingIdentifierForHumanReadableName(String humanReadableName) {
    	if (getOfferingIdentifierForHumanReadableName().containsKey(humanReadableName)) {
    		return getOfferingIdentifierForHumanReadableName().get(humanReadableName);
    	}
    	return humanReadableName;
    }
    
    @Override
    public String getOfferingHumanReadableNameForIdentifier(String identifier) {
    	if (getOfferingHumanReadableNameForIdentifier().containsKey(identifier)) {
    		return getOfferingHumanReadableNameForIdentifier().get(identifier);
    	}
    	return identifier;
    }
}
