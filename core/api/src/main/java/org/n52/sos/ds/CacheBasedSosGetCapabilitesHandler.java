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
package org.n52.sos.ds;

import static java.util.stream.Collectors.toCollection;
import static java.util.stream.Collectors.toMap;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.Set;
import java.util.TreeSet;
import java.util.function.Function;
import java.util.function.Predicate;

import javax.xml.namespace.QName;

import org.n52.shetland.ogc.gml.ReferenceType;
import org.n52.shetland.ogc.ows.exception.OwsExceptionReport;
import org.n52.shetland.ogc.ows.extension.CapabilitiesExtension;
import org.n52.shetland.ogc.ows.service.GetCapabilitiesRequest;
import org.n52.shetland.ogc.sos.Sos1Constants;
import org.n52.shetland.ogc.sos.Sos2Constants;
import org.n52.shetland.ogc.sos.SosConstants;
import org.n52.shetland.ogc.sos.SosObservationOffering;
import org.n52.shetland.ogc.sos.SosOffering;
import org.n52.shetland.ogc.sos.extension.SosObservationOfferingExtension;
import org.n52.shetland.ogc.sos.ro.RelatedOfferingConstants;
import org.n52.shetland.ogc.sos.ro.RelatedOfferings;
import org.n52.shetland.util.CollectionHelper;
import org.n52.shetland.util.OMHelper;
import org.n52.shetland.util.ReferencedEnvelope;
import org.n52.sos.cache.SosContentCache;
import org.n52.sos.util.I18NHelper;

/**
 * Implementation of {@link AbstractSosGetCapabilitiesHandler} that only relies
 * on the {@link SosContentCache}.
 *
 * @author Christian Autermann
 */
public class CacheBasedSosGetCapabilitesHandler extends AbstractSosGetCapabilitiesHandler implements I18NHelper {

    /**
     * Get the contents for SOS 1.0.0 capabilities
     *
     * @param sectionSpecificContentObject
     *            Requested service version
     *
     * @return Offerings for contents
     *
     *
     * @throws OwsExceptionReport
     *             * If an error occurs
     */
    @Override
    protected List<SosObservationOffering> getContentsForSosV1(
            SectionSpecificContentObject sectionSpecificContentObject) throws OwsExceptionReport {
        String version = sectionSpecificContentObject.getGetCapabilitiesResponse().getVersion();
        SosContentCache cache = getCache();
        Collection<String> offerings = cache.getOfferings();
        List<SosObservationOffering> sosOfferings = new ArrayList<>(offerings.size());
        for (String offering : offerings) {
            Collection<String> procedures = getProceduresForOffering(offering, version);
            ReferencedEnvelope envelopeForOffering = cache.getEnvelopeForOffering(offering);
            Set<String> featuresForoffering = getFOI4offering(offering);
            Collection<String> responseFormats = getResponseFormatRepository()
                    .getSupportedResponseFormats(SosConstants.SOS, Sos1Constants.SERVICEVERSION);
            if (checkOfferingValues(envelopeForOffering, featuresForoffering, responseFormats)) {
                SosObservationOffering sosObservationOffering = new SosObservationOffering();

                // insert observationTypes
                sosObservationOffering.setObservationTypes(getObservationTypes(offering));

                // only if fois are contained for the offering set the values of
                // the envelope
                sosObservationOffering.setObservedArea(cache.getEnvelopeForOffering(offering));

                // TODO: add intended application
                // xb_oo.addIntendedApplication("");
                // add offering name
                addSosOfferingToObservationOffering(offering, sosObservationOffering,
                        sectionSpecificContentObject.getGetCapabilitiesRequest());

                // set up phenomena
                sosObservationOffering.setObservableProperties(cache.getObservablePropertiesForOffering(offering));
                sosObservationOffering.setCompositePhenomena(cache.getCompositePhenomenonsForOffering(offering));
                Map<String, Set<String>> phens4CompPhens = cache.getCompositePhenomenonsForOffering(offering).stream()
                        .collect(toMap(Function.identity(), cache::getObservablePropertiesForCompositePhenomenon));
                sosObservationOffering.setPhens4CompPhens(phens4CompPhens);

                // set up time
                setUpTimeForOffering(offering, sosObservationOffering);

                // add feature of interests
                if (getProfileHandler().getActiveProfile().isListFeatureOfInterestsInOfferings()) {
                    sosObservationOffering.setFeatureOfInterest(getFOI4offering(offering));
                }

                // set procedures
                sosObservationOffering.setProcedures(procedures);

                // insert result models
                Collection<QName> resultModels =
                        OMHelper.getQNamesForResultModel(cache.getObservationTypesForOffering(offering));
                sosObservationOffering.setResultModels(resultModels);

                // set response format
                sosObservationOffering.setResponseFormats(responseFormats);

                // set response Mode
                sosObservationOffering.setResponseModes(SosConstants.RESPONSE_MODES);

                sosOfferings.add(sosObservationOffering);
            }
        }

        return sosOfferings;
    }

    /**
     * Get the contents for SOS 2.0 capabilities
     *
     * @param sectionSpecificContentObject
     *            Requested service version
     *
     * @return Offerings for contents
     *
     *
     * @throws OwsExceptionReport
     *             * If an error occurs
     */
    @Override
    protected List<SosObservationOffering> getContentsForSosV2(
            SectionSpecificContentObject sectionSpecificContentObject) throws OwsExceptionReport {
        String version = Sos2Constants.SERVICEVERSION;
        Collection<String> offerings = getCache().getOfferings();
        List<SosObservationOffering> sosOfferings = new ArrayList<>(offerings.size());
        Map<String, List<SosObservationOfferingExtension>> extensions =
                getCapabilitiesExtensionService().getActiveOfferingExtensions();

        if (CollectionHelper.isEmpty(offerings)) {
            // Set empty offering to add empty Contents section to Capabilities
            sosOfferings.add(new SosObservationOffering());
        } else {

            // TODO Parent Offering!!!
            if (checkListOnlyParentOfferings()) {
                sosOfferings.addAll(
                        createAndGetParentOfferings(offerings, version, sectionSpecificContentObject, extensions));
            } else {
                for (String offering : offerings) {
                    Collection<String> observationTypes = getObservationTypes(offering);
                    if (observationTypes != null && !observationTypes.isEmpty()) {
                        // FIXME why a loop? We are in SOS 2.0 context ->
                        // offering 1
                        // <-> 1 procedure!
                        for (String procedure : getProceduresForOffering(offering, version)) {

                            SosObservationOffering sosObservationOffering = new SosObservationOffering();

                            // insert observationTypes
                            sosObservationOffering.setObservationTypes(observationTypes);

                            sosObservationOffering.setObservedArea(getObservedArea(offering));

                            sosObservationOffering.setProcedures(Collections.singletonList(procedure));
                            GetCapabilitiesRequest request = sectionSpecificContentObject.getGetCapabilitiesRequest();

                            // TODO: add intended application
                            // add offering to observation offering
                            addSosOfferingToObservationOffering(offering, sosObservationOffering, request);
                            // add offering extension
                            if (getOfferingExtensionRepository().hasOfferingExtensionProviderFor(request)) {
                                getOfferingExtensionRepository().getOfferingExtensionProvider(request).stream()
                                        .filter(Objects::nonNull)
                                        .filter(provider -> provider.hasExtendedOfferingFor(offering))
                                        .map(provider -> provider.getOfferingExtensions(offering))
                                        .forEach(sosObservationOffering::addExtensions);

                            }
                            if (extensions.containsKey(sosObservationOffering.getOffering().getIdentifier())) {
                                extensions.get(sosObservationOffering.getOffering().getIdentifier()).stream()
                                        .map(CapabilitiesExtension::new).forEach(sosObservationOffering::addExtension);
                            }

                            setUpPhenomenaForOffering(offering, procedure, sosObservationOffering);
                            setUpTimeForOffering(offering, sosObservationOffering);
                            setUpRelatedFeaturesForOffering(offering, version, sosObservationOffering);
                            setUpFeatureOfInterestTypesForOffering(offering, sosObservationOffering);
                            setUpProcedureDescriptionFormatForOffering(sosObservationOffering, version);
                            setUpResponseFormatForOffering(sosObservationOffering, version);

                            sosOfferings.add(sosObservationOffering);
                        }
                    }
                }
            }
        }

        return sosOfferings;
    }

    private Collection<? extends SosObservationOffering> createAndGetParentOfferings(Collection<String> offerings,
            String version, SectionSpecificContentObject sectionSpecificContentObject,
            Map<String, List<SosObservationOfferingExtension>> extensions) throws OwsExceptionReport {
        Map<String, Set<String>> parentChilds =
                offerings.stream().filter(offering -> !getCache().hasParentOfferings(offering)).collect(
                        toMap(Function.identity(), offering -> getCache().getChildOfferings(offering, true, false)));
        List<SosObservationOffering> sosOfferings = new ArrayList<>(parentChilds.size());
        for (Entry<String, Set<String>> entry : parentChilds.entrySet()) {
            Collection<String> observationTypes = getObservationTypes(entry.getValue());
            if (CollectionHelper.isNotEmpty(observationTypes)) {
                Collection<String> procedures = getProceduresForOffering(entry, version);
                if (CollectionHelper.isNotEmpty(procedures)) {
                    Set<String> allOfferings = new HashSet<>(entry.getValue().size() + 1);
                    allOfferings.addAll(entry.getValue());
                    allOfferings.add(entry.getKey());
                    SosObservationOffering sosObservationOffering = new SosObservationOffering();
                    sosObservationOffering.setObservationTypes(observationTypes);
                    sosObservationOffering.setObservedArea(getObservedArea(entry.getValue()));

                    sosObservationOffering.setProcedures(procedures);
                    // TODO: add intended application
                    // add offering to observation offering
                    addSosOfferingToObservationOffering(entry.getKey(), sosObservationOffering,
                            sectionSpecificContentObject.getGetCapabilitiesRequest());
                    // add offering extension
                    if (getOfferingExtensionRepository().hasOfferingExtensionProviderFor(
                            sectionSpecificContentObject.getGetCapabilitiesRequest())) {
                        getOfferingExtensionRepository()
                                .getOfferingExtensionProvider(sectionSpecificContentObject.getGetCapabilitiesRequest())
                                .stream().filter(Objects::nonNull)
                                .filter(provider -> provider.hasExtendedOfferingFor(entry.getKey()))
                                .map(provider -> provider.getOfferingExtensions(entry.getKey()))
                                .forEach(sosObservationOffering::addExtensions);

                    }
                    if (extensions.containsKey(sosObservationOffering.getOffering().getIdentifier())) {
                        extensions.get(sosObservationOffering.getOffering().getIdentifier()).stream()
                                .map(offeringExtension -> new CapabilitiesExtension<>().setValue(offeringExtension))
                                .forEach(sosObservationOffering::addExtension);
                    }
                    // add sub-level offerings
                    if (!entry.getValue().isEmpty()) {
                        RelatedOfferings relatedOfferings = new RelatedOfferings();
                        String gdaURL = getGetDataAvailabilityUrl();
                        gdaURL = addParameter(gdaURL, "responseFormat", "http://www.opengis.net/sosgda/2.0");
                        for (String offering : entry.getValue()) {
                            relatedOfferings.addValue(new ReferenceType(RelatedOfferingConstants.ROLE),
                                    new ReferenceType(
                                            addParameter(new StringBuilder(gdaURL).toString(), "offering", offering),
                                            offering));
                        }
                        sosObservationOffering.addExtension(relatedOfferings);
                    }

                    setUpPhenomenaForOffering(allOfferings, procedures.iterator().next(), sosObservationOffering);
                    setUpTimeForOffering(allOfferings, sosObservationOffering);
                    setUpRelatedFeaturesForOffering(allOfferings, version, sosObservationOffering);
                    setUpFeatureOfInterestTypesForOffering(allOfferings, sosObservationOffering);
                    setUpProcedureDescriptionFormatForOffering(sosObservationOffering, version);
                    setUpResponseFormatForOffering(sosObservationOffering, version);

                    sosOfferings.add(sosObservationOffering);
                }
            }
        }
        return sosOfferings;
    }

    private void addSosOfferingToObservationOffering(String offering, SosObservationOffering sosObservationOffering,
            GetCapabilitiesRequest request) throws OwsExceptionReport {
        SosOffering sosOffering = new SosOffering(offering, false);
        sosObservationOffering.setOffering(sosOffering);
        SosContentCache cache = getCache();
        Locale requestedLocale = getRequestedLocale(request);
        Locale defaultLocale = getDefaultLanguage();
        // add offering name
        addOfferingNames(cache, sosOffering, requestedLocale, defaultLocale, isShowAllLanguages());
        // add offering description
        addOfferingDescription(sosOffering, requestedLocale, defaultLocale, cache);
    }

    private Collection<String> getProceduresForOffering(Entry<String, Set<String>> entry, String version)
            throws OwsExceptionReport {
        Collection<String> procedures = new HashSet<>();
        for (String offering : entry.getValue()) {
            procedures.addAll(getProceduresForOffering(offering, version));
        }
        procedures.addAll(getProceduresForOffering(entry.getKey(), version));
        return procedures;
    }

    protected Collection<String> getObservationTypes(Set<String> offerings) {
        Set<String> observationTypes = offerings.stream().map(getCache()::getObservationTypesForOffering)
                .flatMap(Set::stream).filter(Predicate.isEqual(SosConstants.NOT_DEFINED).negate())
                .collect(toCollection(TreeSet::new));

        if (!observationTypes.isEmpty()) {
            return observationTypes;
        }
        return offerings.stream().map(getCache()::getAllObservationTypesForOffering).flatMap(Set::stream)
                .filter(Predicate.isEqual(SosConstants.NOT_DEFINED).negate()).collect(toCollection(TreeSet::new));
    }

    private ReferencedEnvelope getObservedArea(Set<String> offerings) throws OwsExceptionReport {
        ReferencedEnvelope envelope = new ReferencedEnvelope();
        for (String offering : offerings) {
            envelope.expandToInclude(getObservedArea(offering));
        }
        return envelope;
    }

}
