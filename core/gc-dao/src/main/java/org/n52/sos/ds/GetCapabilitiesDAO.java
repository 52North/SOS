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
package org.n52.sos.ds;

import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toMap;

import java.net.URI;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.Set;
import java.util.TreeSet;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.inject.Inject;
import javax.xml.namespace.QName;

import org.n52.iceland.exception.ows.concrete.InvalidServiceParameterException;
import org.n52.iceland.ogc.ows.OwsServiceMetadataRepository;
import org.n52.iceland.ogc.ows.extension.OwsCapabilitiesExtensionProvider;
import org.n52.iceland.ogc.ows.extension.OwsCapabilitiesExtensionRepository;
import org.n52.iceland.ogc.ows.extension.OwsOperationMetadataExtensionProvider;
import org.n52.iceland.ogc.ows.extension.OwsOperationMetadataExtensionProviderRepository;
import org.n52.iceland.ogc.ows.extension.StaticCapabilities;
import org.n52.iceland.request.handler.OperationHandlerRepository;
import org.n52.iceland.request.operator.RequestOperatorRepository;
import org.n52.iceland.util.LocalizedProducer;
import org.n52.janmayen.Comparables;
import org.n52.janmayen.function.Functions;
import org.n52.janmayen.function.Suppliers;
import org.n52.janmayen.http.MediaType;
import org.n52.janmayen.i18n.LocaleHelper;
import org.n52.shetland.ogc.OGCConstants;
import org.n52.shetland.ogc.filter.FilterCapabilities;
import org.n52.shetland.ogc.filter.FilterConstants.ComparisonOperator;
import org.n52.shetland.ogc.filter.FilterConstants.ConformanceClassConstraintNames;
import org.n52.shetland.ogc.filter.FilterConstants.SpatialOperator;
import org.n52.shetland.ogc.filter.FilterConstants.TimeOperator;
import org.n52.shetland.ogc.gml.GmlConstants;
import org.n52.shetland.ogc.gml.ReferenceType;
import org.n52.shetland.ogc.gml.time.TimePeriod;
import org.n52.shetland.ogc.ows.OWSConstants;
import org.n52.shetland.ogc.ows.OWSConstants.GetCapabilitiesParams;
import org.n52.shetland.ogc.ows.OwsAllowedValues;
import org.n52.shetland.ogc.ows.OwsCapabilitiesExtension;
import org.n52.shetland.ogc.ows.OwsDomain;
import org.n52.shetland.ogc.ows.OwsNoValues;
import org.n52.shetland.ogc.ows.OwsOperation;
import org.n52.shetland.ogc.ows.OwsOperationMetadataExtension;
import org.n52.shetland.ogc.ows.OwsOperationsMetadata;
import org.n52.shetland.ogc.ows.OwsServiceIdentification;
import org.n52.shetland.ogc.ows.OwsValue;
import org.n52.shetland.ogc.ows.exception.CodedException;
import org.n52.shetland.ogc.ows.exception.CompositeOwsException;
import org.n52.shetland.ogc.ows.exception.InvalidParameterValueException;
import org.n52.shetland.ogc.ows.exception.OwsExceptionReport;
import org.n52.shetland.ogc.ows.exception.VersionNegotiationFailedException;
import org.n52.shetland.ogc.ows.extension.CapabilitiesExtension;
import org.n52.shetland.ogc.ows.extension.MergableExtension;
import org.n52.shetland.ogc.ows.extension.StringBasedCapabilitiesExtension;
import org.n52.shetland.ogc.ows.service.GetCapabilitiesRequest;
import org.n52.shetland.ogc.ows.service.GetCapabilitiesResponse;
import org.n52.shetland.ogc.sos.Sos1Constants;
import org.n52.shetland.ogc.sos.Sos2Constants;
import org.n52.shetland.ogc.sos.SosCapabilities;
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
import org.n52.sos.coding.encode.ProcedureDescriptionFormatRepository;
import org.n52.sos.coding.encode.ResponseFormatRepository;
import org.n52.sos.config.CapabilitiesExtensionService;
import org.n52.sos.ogc.sos.SosObservationOfferingExtensionProvider;
import org.n52.sos.ogc.sos.SosObservationOfferingExtensionRepository;
import org.n52.sos.service.profile.ProfileHandler;
import org.n52.sos.util.GeometryHandler;
import org.n52.sos.util.I18NHelper;
import org.n52.svalbard.ConformanceClass;
import org.n52.svalbard.decode.DecoderRepository;
import org.n52.svalbard.encode.EncoderRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

/**
 * Implementation of the interface AbstractGetCapabilitiesHandler
 *
 * @since 4.0.0
 */
public class GetCapabilitiesDAO extends AbstractGetCapabilitiesHandler {

    private static final Logger LOGGER = LoggerFactory.getLogger(GetCapabilitiesDAO.class);

    /* section flags (values are powers of 2) */
    private static final int SERVICE_IDENTIFICATION = 0x01;
    private static final int SERVICE_PROVIDER = 0x02;
    private static final int OPERATIONS_METADATA = 0x04;
    private static final int FILTER_CAPABILITIES = 0x08;
    private static final int CONTENTS = 0x10;
    private static final int ALL = 0x20 | SERVICE_IDENTIFICATION | SERVICE_PROVIDER | OPERATIONS_METADATA |
                                   FILTER_CAPABILITIES | CONTENTS;

    @Inject
    private CapabilitiesExtensionService capabilitiesExtensionService;
    @Inject
    private EncoderRepository encoderRepository;
    @Inject
    private DecoderRepository decoderRepository;
    @Inject
    private OperationHandlerRepository operationHandlerRepository;
    @Inject
    private ProfileHandler profileHandler;
    @Inject
    private OwsServiceMetadataRepository serviceMetadataRepository;
    @Inject
    private RequestOperatorRepository requestOperatorRepository;
    @Inject
    private ResponseFormatRepository responseFormatRepository;
    @Inject
    private GeometryHandler geometryHandler;
    @Inject
    private OwsOperationMetadataExtensionProviderRepository owsExtendedCapabilitiesProviderRepository;
    @Inject
    private SosObservationOfferingExtensionRepository offeringExtensionRepository;
    @Inject
    private OwsCapabilitiesExtensionRepository capabilitiesExtensionRepository;
    @Inject
    private ProcedureDescriptionFormatRepository procedureDescriptionFormatRepository;

    public GetCapabilitiesDAO() {
        super(SosConstants.SOS);
    }

    @Override
    public GetCapabilitiesResponse getCapabilities(GetCapabilitiesRequest request) throws OwsExceptionReport {
        String capabilitiesId = request.getCapabilitiesId();
        String service = request.getService();
        String version = negotiateVersion(request);

        GetCapabilitiesResponse response = new GetCapabilitiesResponse(service, version);

        if (capabilitiesId == null && this.capabilitiesExtensionService.isStaticCapabilitiesActive()) {
            createStaticCapabilities(request, response);
        } else if (capabilitiesId != null && !capabilitiesId
                   .equals(GetCapabilitiesParams.DYNAMIC_CAPABILITIES_IDENTIFIER)) {
            createStaticCapabilitiesWithId(request, response);
        } else {
            createDynamicCapabilities(request, response);
        }
        return response;
    }

    @Override
    public boolean isSupported() {
        return true;
    }

    private String negotiateVersion(GetCapabilitiesRequest request) throws OwsExceptionReport {
        if (request.isSetVersion()) {
            return request.getVersion();
        } else {
            String service = request.getService();
            String version;

            if (request.isSetAcceptVersions()) {
                version = request.getAcceptVersions().stream()
                        .filter(v -> getServiceOperatorRepository().isVersionSupported(service, v))
                        .findFirst()
                        .orElseThrow(this::versionNegotiationFailed);
            } else {
                version = getServiceOperatorRepository().getSupportedVersions(service).stream()
                        .max(Comparables.version()).orElseThrow(() -> new InvalidServiceParameterException(service));
            }
            request.setVersion(version);
            return version;
        }
    }

    private OwsExceptionReport versionNegotiationFailed() {
        return new VersionNegotiationFailedException()
                .withMessage("The requested '%s' values are not supported by this service!",
                             OWSConstants.GetCapabilitiesParams.AcceptVersions);
    }

    private void addSectionSpecificContent(SectionSpecificContentObject sectionSpecificContentObject,
                                           GetCapabilitiesRequest request) throws OwsExceptionReport {
        String version = sectionSpecificContentObject.getGetCapabilitiesResponse().getVersion();
        String service = sectionSpecificContentObject.getGetCapabilitiesResponse().getService();

        if (isServiceIdentificationSectionRequested(sectionSpecificContentObject.getRequestedSections())) {
            sectionSpecificContentObject.getSosCapabilities()
                    .setServiceIdentification(getServiceIdentification(request, service, version));
        }
        if (isServiceProviderSectionRequested(sectionSpecificContentObject.getRequestedSections())) {
            sectionSpecificContentObject.getSosCapabilities().setServiceProvider(this.serviceMetadataRepository
                    .getServiceProviderFactory(service).get());
        }
        if (isOperationsMetadataSectionRequested(sectionSpecificContentObject.getRequestedSections())) {
            sectionSpecificContentObject.getSosCapabilities()
                    .setOperationsMetadata(getOperationsMetadataForOperations(request, service, version));
        }
        if (isFilterCapabilitiesSectionRequested(sectionSpecificContentObject.getRequestedSections())) {
            sectionSpecificContentObject.getSosCapabilities().setFilterCapabilities(getFilterCapabilities(version));
        }
        if (isContentsSectionRequested(sectionSpecificContentObject.getRequestedSections())) {
            if (isV2(sectionSpecificContentObject.getGetCapabilitiesResponse())) {
                sectionSpecificContentObject.getSosCapabilities()
                        .setContents(getContentsForSosV2(sectionSpecificContentObject));
            } else {
                sectionSpecificContentObject.getSosCapabilities().setContents(getContents(sectionSpecificContentObject));
            }
        }

        if (isV2(sectionSpecificContentObject.getGetCapabilitiesResponse())) {
            if (sectionSpecificContentObject.getRequestedSections() == ALL) {
                sectionSpecificContentObject.getSosCapabilities().setExtensions(getAndMergeExtensions(service, version));
            } else if (!sectionSpecificContentObject.getRequestedExtensionSesctions().isEmpty()) {
                sectionSpecificContentObject.getSosCapabilities().setExtensions(
                        getExtensions(sectionSpecificContentObject.getRequestedExtensionSesctions(), service, version));
            }
        }
    }

    private int identifyRequestedSections(GetCapabilitiesRequest request,
                                          GetCapabilitiesResponse response,
                                          Set<String> availableExtensionSections,
                                          Set<String> requestedExtensionSections)
            throws OwsExceptionReport {

        if (!request.isSetSections()) {
            // FIXME: requestedExtensionSections.addAll(availableExtensionSections)?
            return ALL;
        }

        int sections = 0;
        for (String section : request.getSections()) {
            if (section.isEmpty()) {
                LOGGER.warn("A section element is empty! Check if operator checks for empty elements!");
            } else if (section.equals(SosConstants.CapabilitiesSections.All.name())) {
                sections |= ALL;
            } else if (section.equals(SosConstants.CapabilitiesSections.ServiceIdentification.name())) {
                sections |= SERVICE_IDENTIFICATION;
            } else if (section.equals(SosConstants.CapabilitiesSections.ServiceProvider.name())) {
                sections |= SERVICE_PROVIDER;
            } else if (section.equals(SosConstants.CapabilitiesSections.OperationsMetadata.name())) {
                sections |= OPERATIONS_METADATA;
            } else if (section.equals(Sos1Constants.CapabilitiesSections.Filter_Capabilities.name()) && isV1(response)) {
                sections |= FILTER_CAPABILITIES;
            } else if (section.equals(Sos2Constants.CapabilitiesSections.FilterCapabilities.name()) && isV2(response)) {
                sections |= FILTER_CAPABILITIES;
            } else if (section.equals(SosConstants.CapabilitiesSections.Contents.name())) {
                sections |= CONTENTS;
            } else if (availableExtensionSections.contains(section) && isV2(response)) {
                requestedExtensionSections.add(section);
            } else {
                throw new InvalidParameterValueException()
                        .at(GetCapabilitiesParams.Section)
                        .withMessage("The requested section '%s' does not exist or is not supported!", section);
            }
        }
        return sections;
    }

    private OwsServiceIdentification getServiceIdentification(GetCapabilitiesRequest request, String service,
                                                              String version) throws OwsExceptionReport {
        Locale locale = getRequestedLocale(request);
        LocalizedProducer<OwsServiceIdentification> serviceIdentificationFactory
                = this.serviceMetadataRepository.getServiceIdentificationFactory(service);
        OwsServiceIdentification serviceIdentification = serviceIdentificationFactory.get(locale);
        if (version.equals(Sos2Constants.SERVICEVERSION)) {
            serviceIdentification.setProfiles(getProfiles(SosConstants.SOS, Sos2Constants.SERVICEVERSION));
        }
        return serviceIdentification;
    }

    private Set<URI> getProfiles(String service, String version) {

        Set<URI> profiles = Stream.of(getBindingRepository().getBindings().values(),
                                      this.requestOperatorRepository.getRequestOperators(),
                                      this.decoderRepository.getDecoders(),
                                      this.encoderRepository.getEncoders(),
                                      this.operationHandlerRepository.getOperationHandlers().values())
                .flatMap(Collection::stream)
                .filter(c -> c instanceof ConformanceClass)
                .map(c -> (ConformanceClass) c)
                .map(c -> c.getConformanceClasses(service, version))
                .flatMap(Set::stream)
                .map(URI::create)
                .collect(Collectors.toSet());

        // FIXME additional profiles
        if ("hydrology".equalsIgnoreCase(this.profileHandler.getActiveProfile().getIdentifier())) {
            profiles.add(URI.create("http://www.opengis.net/spec/SOS_application-profile_hydrology/1.0/req/hydrosos"));
        }
        return profiles;
    }

    /**
     * Get the OperationsMetadat for all supported operations
     *
     * @param request the request
     * @param service Requested service
     * @param version Requested service version
     *
     * @return OperationsMetadata for all operations supported by the requested service and version
     *
     * @throws OwsExceptionReport If an error occurs
     */
    private OwsOperationsMetadata getOperationsMetadataForOperations(GetCapabilitiesRequest request,
                                                                     String service, String version)
            throws OwsExceptionReport {

        List<OwsDomain> parameters = getParameters(service, version);
        List<OwsOperation> operations = getOperations(service, version);
        List<OwsDomain> constraints = Collections.emptyList();
        OwsOperationMetadataExtension owsExtendedCapabilities = null;
        /*
        * check if an OwsExtendedCapabilities provider is available for this
        * service and check if this provider provides OwsExtendedCapabilities
        * for the request
         */
        OwsOperationMetadataExtensionProvider provider = owsExtendedCapabilitiesProviderRepository
                .getExtendedCapabilitiesProvider(service, version);
        if (provider != null && provider.hasExtendedCapabilitiesFor(request)) {
            owsExtendedCapabilities = provider.getOwsExtendedCapabilities(request);
        }
        return new OwsOperationsMetadata(operations, parameters, constraints, owsExtendedCapabilities);
    }

    /**
     * Get the FilterCapabilities
     *
     * @param version Requested service version
     *
     * @return FilterCapabilities
     */
    private FilterCapabilities getFilterCapabilities(final String version) {
        final FilterCapabilities filterCapabilities = new FilterCapabilities();
        if (Sos2Constants.SERVICEVERSION.equals(version)) {
            getConformance(filterCapabilities);
        }
        getScalarFilterCapabilities(filterCapabilities, version);
        getSpatialFilterCapabilities(filterCapabilities, version);
        getTemporalFilterCapabilities(filterCapabilities, version);

        return filterCapabilities;
    }

    private void getConformance(FilterCapabilities filterCapabilities) {
        OwsValue trueValue = new OwsValue("true");
        OwsValue falseValue = new OwsValue("false");
        OwsNoValues noValues = OwsNoValues.instance();
        filterCapabilities
                .addConformance(new OwsDomain(ConformanceClassConstraintNames.ImplementsQuery, noValues, falseValue));
        filterCapabilities
                .addConformance(new OwsDomain(ConformanceClassConstraintNames.ImplementsAdHocQuery, noValues, falseValue));
        filterCapabilities
                .addConformance(new OwsDomain(ConformanceClassConstraintNames.ImplementsFunctions, noValues, falseValue));
        filterCapabilities
                .addConformance(new OwsDomain(ConformanceClassConstraintNames.ImplementsResourceld, noValues, falseValue));
        filterCapabilities
                .addConformance(new OwsDomain(ConformanceClassConstraintNames.ImplementsMinStandardFilter, noValues, falseValue));
        filterCapabilities
                .addConformance(new OwsDomain(ConformanceClassConstraintNames.ImplementsStandardFilter, noValues, falseValue));
        filterCapabilities
                .addConformance(new OwsDomain(ConformanceClassConstraintNames.ImplementsMinSpatialFilter, noValues, trueValue));
        filterCapabilities
                .addConformance(new OwsDomain(ConformanceClassConstraintNames.ImplementsSpatialFilter, noValues, trueValue));
        filterCapabilities
                .addConformance(new OwsDomain(ConformanceClassConstraintNames.ImplementsMinTemporalFilter, noValues, trueValue));
        filterCapabilities
                .addConformance(new OwsDomain(ConformanceClassConstraintNames.ImplementsTemporalFilter, noValues, trueValue));
        filterCapabilities
                .addConformance(new OwsDomain(ConformanceClassConstraintNames.ImplementsVersionNav, noValues, falseValue));
        filterCapabilities
                .addConformance(new OwsDomain(ConformanceClassConstraintNames.ImplementsSorting, noValues, falseValue));
        filterCapabilities
                .addConformance(new OwsDomain(ConformanceClassConstraintNames.ImplementsExtendedOperators, noValues, falseValue));
        filterCapabilities
                .addConformance(new OwsDomain(ConformanceClassConstraintNames.ImplementsMinimumXPath, noValues, falseValue));
        filterCapabilities
                .addConformance(new OwsDomain(ConformanceClassConstraintNames.ImplementsSchemaElementFunc, noValues, falseValue));
    }

    /**
     * Get the contents for SOS 1.0.0 capabilities
     *
     * @param sectionSpecificContentObject Requested service version
     *
     * @return Offerings for contents
     *
     *
     * @throws OwsExceptionReport * If an error occurs
     */
    private List<SosObservationOffering> getContents(SectionSpecificContentObject sectionSpecificContentObject) throws
            OwsExceptionReport {
        String version = sectionSpecificContentObject.getGetCapabilitiesResponse().getVersion();
        SosContentCache cache = getCache();
        final Collection<String> offerings = cache.getOfferings();
        final List<SosObservationOffering> sosOfferings = new ArrayList<>(offerings.size());
        for (final String offering : offerings) {
            final Collection<String> procedures = getProceduresForOffering(offering, version);
            final ReferencedEnvelope envelopeForOffering = cache.getEnvelopeForOffering(offering);
            final Set<String> featuresForoffering = getFOI4offering(offering);
            final Collection<String> responseFormats = getResponseFormatRepository()
                    .getSupportedResponseFormats(SosConstants.SOS, Sos1Constants.SERVICEVERSION);
            if (checkOfferingValues(envelopeForOffering, featuresForoffering, responseFormats)) {
                final SosObservationOffering sosObservationOffering = new SosObservationOffering();

                // insert observationTypes
                sosObservationOffering.setObservationTypes(getObservationTypes(offering));

                // only if fois are contained for the offering set the values of
                // the envelope
                sosObservationOffering.setObservedArea(processObservedArea(cache.getEnvelopeForOffering(offering)));

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
                if (this.profileHandler.getActiveProfile().isListFeatureOfInterestsInOfferings()) {
                    sosObservationOffering.setFeatureOfInterest(getFOI4offering(offering));
                }

                // set procedures
                sosObservationOffering.setProcedures(procedures);

                // insert result models
                Collection<QName> resultModels = OMHelper.getQNamesForResultModel(
                        cache.getObservationTypesForOffering(offering));
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

    private ReferencedEnvelope processObservedArea(ReferencedEnvelope sosEnvelope) throws CodedException {
        // TODO Check transformation
        // if (requestedSrid >= 0 && sosEnvelope.getSrid() != requestedSrid) {
        // ReferencedEnvelope tranformedEnvelope = new ReferencedEnvelope();
        // tranformedEnvelope.setSrid(requestedSrid);
        // tranformedEnvelope.setEnvelope(GeometryHandler.getInstance().transformEnvelope(sosEnvelope.getEnvelope(),
        // sosEnvelope.getSrid(), requestedSrid));
        // return tranformedEnvelope;
        // }
        return sosEnvelope;
    }

    private boolean checkOfferingValues(final ReferencedEnvelope envelopeForOffering,
                                        final Set<String> featuresForOffering,
                                        final Collection<String> responseFormats) {
        return ReferencedEnvelope.isNotNullOrEmpty(envelopeForOffering) && CollectionHelper
               .isNotEmpty(featuresForOffering) &&
               CollectionHelper.isNotEmpty(responseFormats);
    }

    /**
     * Get the contents for SOS 2.0 capabilities
     *
     * @param sectionSpecificContentObject Requested service version
     *
     * @return Offerings for contents
     *
     *
     * @throws OwsExceptionReport * If an error occurs
     */
    private List<SosObservationOffering> getContentsForSosV2(SectionSpecificContentObject sectionSpecificContentObject)
            throws OwsExceptionReport {
        String version = Sos2Constants.SERVICEVERSION;
        final Collection<String> offerings = getCache().getOfferings();
        final List<SosObservationOffering> sosOfferings = new ArrayList<>(offerings.size());
        final Map<String, List<SosObservationOfferingExtension>> extensions = this.capabilitiesExtensionService
                .getActiveOfferingExtensions();

        if (CollectionHelper.isEmpty(offerings)) {
            // Set empty offering to add empty Contents section to Capabilities
            sosOfferings.add(new SosObservationOffering());
        } else {

            // TODO Parent Offering!!!

            if (checkListOnlyParentOfferings()) {
                sosOfferings.addAll(createAndGetParentOfferings(offerings, version, sectionSpecificContentObject, extensions));
            } else {
                for (final String offering : offerings) {
                    final Collection<String> observationTypes = getObservationTypes(offering);
                    if (observationTypes != null && !observationTypes.isEmpty()) {
                        // FIXME why a loop? We are in SOS 2.0 context -> offering 1
                        // <-> 1 procedure!
                        for (final String procedure : getProceduresForOffering(offering, version)) {

                            final SosObservationOffering sosObservationOffering = new SosObservationOffering();

                            // insert observationTypes
                            sosObservationOffering.setObservationTypes(observationTypes);

                            sosObservationOffering.setObservedArea(getObservedArea(offering));

                            sosObservationOffering.setProcedures(Collections.singletonList(procedure));

                            // TODO: add intended application

                            // add offering to observation offering
                            addSosOfferingToObservationOffering(offering, sosObservationOffering,
                                    sectionSpecificContentObject.getGetCapabilitiesRequest());
                            // add offering extension
                            if (offeringExtensionRepository.hasOfferingExtensionProviderFor(sectionSpecificContentObject.getGetCapabilitiesRequest())) {
                                for (SosObservationOfferingExtensionProvider provider : offeringExtensionRepository.getOfferingExtensionProvider(sectionSpecificContentObject.getGetCapabilitiesRequest())) {
                                    if (provider != null && provider.hasExtendedOfferingFor(offering)) {
                                        sosObservationOffering.addExtensions(provider.getOfferingExtensions(offering));
                                    }
                                }
                            }
                            if (extensions.containsKey(sosObservationOffering.getOffering().getIdentifier())) {
                                for (SosObservationOfferingExtension offeringExtension : extensions.get(sosObservationOffering.getOffering().getIdentifier())) {
                                    sosObservationOffering.addExtension(new CapabilitiesExtension<SosObservationOfferingExtension>().setValue(offeringExtension));
                                }
                            }

                            setUpPhenomenaForOffering(offering, procedure, sosObservationOffering);
                            setUpTimeForOffering(offering, sosObservationOffering);
                            setUpRelatedFeaturesForOffering(offering, version, sosObservationOffering);
                            setUpFeatureOfInterestTypesForOffering(offering, sosObservationOffering);
                            setUpProcedureDescriptionFormatForOffering(sosObservationOffering, version);
                            setUpResponseFormatForOffering(version, sosObservationOffering);


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
        Map<String, Set<String>> parentChilds = Maps.newHashMap();
        for (String offering : offerings) {
            if (!getCache().hasParentOfferings(offering)) {
                parentChilds.put(offering, getCache().getChildOfferings(offering, true, false));
            }
        }
        final List<SosObservationOffering> sosOfferings = new ArrayList<SosObservationOffering>(parentChilds.size());
        for (Entry<String, Set<String>> entry : parentChilds.entrySet()) {
            final Collection<String> observationTypes = getObservationTypes(entry.getValue());
            if (CollectionHelper.isNotEmpty(observationTypes)) {
                Collection<String> procedures = getProceduresForOffering(entry.getValue(), version);
                if (CollectionHelper.isNotEmpty(procedures)) {
                    Set<String> allOfferings =Sets.newHashSet();
                    allOfferings.addAll(entry.getValue());
                    allOfferings.add(entry.getKey());
                    final SosObservationOffering sosObservationOffering = new SosObservationOffering();
                    sosObservationOffering.setObservationTypes(observationTypes);
                    sosObservationOffering.setObservedArea(getObservedArea(entry.getValue()));

                    sosObservationOffering.setProcedures(procedures);
//
//                    // TODO: add intended application
//
//                    // add offering to observation offering
                    addSosOfferingToObservationOffering(entry.getKey(), sosObservationOffering,
                            sectionSpecificContentObject.getGetCapabilitiesRequest());
                    // add offering extension
                    if (offeringExtensionRepository.hasOfferingExtensionProviderFor(
                            sectionSpecificContentObject.getGetCapabilitiesRequest())) {
                        offeringExtensionRepository
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
                        setUpResponseFormatForOffering(version, sosObservationOffering);

                        sosOfferings.add(sosObservationOffering);
                    }
                }
        }
        return sosOfferings;
    }

    private void addSosOfferingToObservationOffering(String offering, SosObservationOffering sosObservationOffering,
                                                     GetCapabilitiesRequest request) throws CodedException {
        SosOffering sosOffering = new SosOffering(offering, false);
        sosObservationOffering.setOffering(sosOffering);
        SosContentCache cache = getCache();
        Locale requestedLocale = getRequestedLocale(request);
        Locale defaultLocale = getDefaultLanguage();
        // add offering name
        I18NHelper.addOfferingNames(cache, sosOffering, requestedLocale, defaultLocale, isShowAllLanguages());
        // add offering description
        I18NHelper.addOfferingDescription(sosOffering, requestedLocale, defaultLocale, cache);
    }

    /**
     * Set SpatialFilterCapabilities to FilterCapabilities
     *
     * @param filterCapabilities FilterCapabilities
     * @param version SOS version
     */
    private void getSpatialFilterCapabilities(final FilterCapabilities filterCapabilities, final String version) {

        // set GeometryOperands
        final List<QName> operands = new LinkedList<>();
        if (version.equals(Sos2Constants.SERVICEVERSION)) {
            operands.add(GmlConstants.QN_ENVELOPE_32);
        } else if (version.equals(Sos1Constants.SERVICEVERSION)) {
            operands.add(GmlConstants.QN_ENVELOPE);
            operands.add(GmlConstants.QN_POINT);
            operands.add(GmlConstants.QN_LINESTRING);
            operands.add(GmlConstants.QN_POLYGON);
        }

        filterCapabilities.setSpatialOperands(operands);

        // set SpatialOperators
        Map<SpatialOperator, Set<QName>> ops = new EnumMap<>(SpatialOperator.class);
//        final SetMultiMap<SpatialOperator, QName> ops = MultiMaps.newSetMultiMap(SpatialOperator.class);
        if (version.equals(Sos2Constants.SERVICEVERSION)) {
            ops.put(SpatialOperator.BBOX, Collections.singleton(GmlConstants.QN_ENVELOPE_32));
        } else if (version.equals(Sos1Constants.SERVICEVERSION)) {
            ops.put(SpatialOperator.BBOX, Collections.singleton(GmlConstants.QN_ENVELOPE));
            // set Contains
            Set<QName> filterOperands = new HashSet<>(Arrays.asList(GmlConstants.QN_POINT,
                                                                    GmlConstants.QN_LINESTRING,
                                                                    GmlConstants.QN_POLYGON));
            Stream.of(SpatialOperator.Contains, SpatialOperator.Intersects, SpatialOperator.Overlaps)
                    .forEach(op -> ops.put(op, filterOperands));
        }

        filterCapabilities.setSpatialOperators(ops);
    }

    /**
     * Set TemporalFilterCapabilities to FilterCapabilities
     *
     * @param filterCapabilities FilterCapabilities
     * @param version SOS version
     */
    private void getTemporalFilterCapabilities(FilterCapabilities filterCapabilities, String version) {

        // set TemporalOperands
        final List<QName> operands = new ArrayList<>(2);
        switch (version) {
            case Sos2Constants.SERVICEVERSION:
                operands.add(GmlConstants.QN_TIME_PERIOD_32);
                operands.add(GmlConstants.QN_TIME_INSTANT_32);
                break;
            case Sos1Constants.SERVICEVERSION:
                operands.add(GmlConstants.QN_TIME_PERIOD);
                operands.add(GmlConstants.QN_TIME_INSTANT);
                break;
            default:
                LOGGER.trace("Not supported version '{}'", version);
                break;
        }

        filterCapabilities.setTemporalOperands(operands);

        // set TemporalOperators
        final Map<TimeOperator, Set<QName>> ops = CollectionHelper.synchronizedMap(0);
        switch (version) {
            case Sos2Constants.SERVICEVERSION:
                for (final TimeOperator op : TimeOperator.values()) {
                    ops.computeIfAbsent(op, createSynchronizedSet()).add(GmlConstants.QN_TIME_INSTANT_32);
                    ops.computeIfAbsent(op, createSynchronizedSet()).add(GmlConstants.QN_TIME_PERIOD_32);
                }
                break;
            case Sos1Constants.SERVICEVERSION:
                for (final TimeOperator op : TimeOperator.values()) {
                    ops.computeIfAbsent(op, createSynchronizedSet()).add(GmlConstants.QN_TIME_INSTANT);
                    ops.computeIfAbsent(op, createSynchronizedSet()).add(GmlConstants.QN_TIME_PERIOD);
                }
                break;
            default:
                LOGGER.trace("Not supported version '{}'", version);
                break;
        }
        filterCapabilities.setTemporalOperators(ops);
    }

    /**
     * Set ScalarFilterCapabilities to FilterCapabilities
     *
     * @param filterCapabilities FilterCapabilities
     */
    private void getScalarFilterCapabilities(final FilterCapabilities filterCapabilities, String version) {
        // TODO PropertyIsNil, PropertyIsNull? better:
        // filterCapabilities.setComparisonOperators(Arrays.asList(ComparisonOperator.values()));
        final List<ComparisonOperator> comparisonOperators = new ArrayList<>(8);
        comparisonOperators.add(ComparisonOperator.PropertyIsBetween);
        comparisonOperators.add(ComparisonOperator.PropertyIsEqualTo);
        if (version.equals(Sos1Constants.SERVICEVERSION)) {
//            comparisonOperators.add(ComparisonOperator.PropertyIsNotEqualTo);
        }
        comparisonOperators.add(ComparisonOperator.PropertyIsLessThan);
        comparisonOperators.add(ComparisonOperator.PropertyIsLessThanOrEqualTo);
        comparisonOperators.add(ComparisonOperator.PropertyIsGreaterThan);
        comparisonOperators.add(ComparisonOperator.PropertyIsGreaterThanOrEqualTo);
        comparisonOperators.add(ComparisonOperator.PropertyIsLike);
        filterCapabilities.setComparisonOperators(comparisonOperators);
    }

    /**
     * Get FOIs contained in an offering
     *
     * @param offering Offering identifier
     *
     * @return FOI identifiers
     *
     *
     * @throws OwsExceptionReport * If an error occurs
     */
    private Set<String> getFOI4offering(final String offering) throws OwsExceptionReport {
        final Set<String> featureIDs = new HashSet<>(0);
        final Set<String> features = getCache().getFeaturesOfInterestForOffering(offering);
        if (!this.profileHandler.getActiveProfile().isListFeatureOfInterestsInOfferings() ||
            features == null) {
            featureIDs.add(OGCConstants.UNKNOWN);
        } else {
            featureIDs.addAll(features);
        }
        return featureIDs;
    }

    private Collection<String> getObservationTypes(Set<String> offerings) {
        Set<String> observationTypes = new TreeSet<>();
        for (String offering : offerings) {
            observationTypes.addAll(getCache().getObservationTypesForOffering(offering).stream()
                    .filter(Predicate.isEqual(SosConstants.NOT_DEFINED).negate())
                    .collect(Collectors.toSet()));
        }
        if (observationTypes.isEmpty()) {
            for (String offering : offerings) {
                getCache().getAllowedObservationTypesForOffering(offering).stream()
                        .filter(Predicate.isEqual(SosConstants.NOT_DEFINED).negate())
                        .forEach(observationTypes::add);
            }
        }
        return observationTypes;
    }

    private Collection<String> getObservationTypes(String offering) {
        final Collection<String> observationTypes = getCache().getAllObservationTypesForOffering(offering);
//        final Set<String> observationTypes = Sets.newHashSet();
        observationTypes.remove(SosConstants.NOT_DEFINED);
//        for (final String observationType : allObservationTypes) {
//            if (!observationType.equals(SosConstants.NOT_DEFINED)) {
//                observationTypes.add(observationType);
//            }
//        }
        return observationTypes;
    }

    @Override
    protected Set<String> getExtensionSections(String service, String version) throws OwsExceptionReport {
        return getAndMergeExtensions(service, version).stream()
                .map(OwsCapabilitiesExtension::getSectionName)
                .collect(Collectors.toSet());
    }

    /**
     * Get extensions and merge MergableExtension of the same class.
     *
     * @return Extensions
     *
     *
     * @throws OwsExceptionReport
     */
    @SuppressWarnings({ "rawtypes", "unchecked" })
    private List<OwsCapabilitiesExtension> getAndMergeExtensions(String service, String version)
            throws OwsExceptionReport {
        List<OwsCapabilitiesExtensionProvider> providers = capabilitiesExtensionRepository
                .getCapabilitiesExtensionProvider(service, version);
        List<OwsCapabilitiesExtension> extensions = new LinkedList<>();
        if (CollectionHelper.isNotEmpty(providers)) {
            HashMap<String, MergableExtension> map = new HashMap<>(providers.size());
            providers.stream().filter(Objects::nonNull)
                    .map(OwsCapabilitiesExtensionProvider::getExtension)
                    .forEachOrdered(extension -> {
                        if (extension instanceof MergableExtension) {
                            map.merge(extension.getSectionName(),
                                      (MergableExtension) extension,
                                      Functions.mergeLeft(MergableExtension::merge));
                        } else {
                            extensions.add(extension);
                        }
                    });
            extensions.addAll(map.values());
        }
        Map<String, StringBasedCapabilitiesExtension> activeCapabilitiesExtensions
                = this.capabilitiesExtensionService.getActiveCapabilitiesExtensions();
        if (activeCapabilitiesExtensions != null && !activeCapabilitiesExtensions.isEmpty()) {
            extensions.addAll(activeCapabilitiesExtensions.values());
        }
        return extensions;
    }

    private Collection<OwsCapabilitiesExtension> getExtensions(Set<String> requestedExtensionSections, String service,
                                                               String version)
            throws OwsExceptionReport {
        return getAndMergeExtensions(service, version).stream()
                .filter(e -> requestedExtensionSections.contains(e.getSectionName()))
                .collect(toList());
    }

    protected void setUpPhenomenaForOffering(String offering, String procedure, SosObservationOffering sosOffering) {
        setUpPhenomenaForOffering(Sets.newHashSet(offering), procedure, sosOffering);
    }

    protected void setUpPhenomenaForOffering(final Set<String> offerings, final String procedure,
            final SosObservationOffering sosOffering) {
        final Collection<String> phenomenons = new LinkedList<>();
        for (String offering : offerings) {
            final Collection<String> observablePropertiesForOffering =
                    getCache().getObservablePropertiesForOffering(offering);
            for (final String observableProperty : observablePropertiesForOffering) {
                final Set<String> proceduresForObservableProperty =
                        getCache().getProceduresForObservableProperty(observableProperty);
                if (proceduresForObservableProperty.contains(procedure)
                        || isHiddenChildProcedureObservableProperty(offering, proceduresForObservableProperty)) {
                    phenomenons.add(observableProperty);
                }
            }
            sosOffering.setObservableProperties(phenomenons);
            sosOffering.setCompositePhenomena(getCache().getCompositePhenomenonsForOffering(offering));

            final Collection<String> compositePhenomenonsForOffering = getCache()
                    .getCompositePhenomenonsForOffering(offering);

            if (compositePhenomenonsForOffering != null) {
                sosOffering.setPhens4CompPhens(compositePhenomenonsForOffering.stream().collect(toMap(Function.identity(),getCache()::getObservablePropertiesForCompositePhenomenon)));
            } else {
                sosOffering.setPhens4CompPhens(Collections.emptyMap());
            }
        }
    }

    private boolean isHiddenChildProcedureObservableProperty(String offering,
                                                             Set<String> proceduresForObservableProperty) {
        return getCache().getHiddenChildProceduresForOffering(offering).stream()
                .anyMatch(proceduresForObservableProperty::contains);
    }

    protected void setUpRelatedFeaturesForOffering(final String offering, final String version,
            final SosObservationOffering sosOffering) throws OwsExceptionReport {
        setUpRelatedFeaturesForOffering(Sets.newHashSet(offering), version, sosOffering);
    }

    protected void setUpRelatedFeaturesForOffering(Set<String> offerings, String version,
            SosObservationOffering sosOffering) {
        for (String offering : offerings) {
            sosOffering.setRelatedFeatures(getCache().getRelatedFeaturesForOffering(offering).stream()
                    .collect(toMap(Function.identity(), getCache()::getRolesForRelatedFeature)));
        }
    }

    protected void setUpTimeForOffering(Set<String> offerings, SosObservationOffering sosOffering) {
        TimePeriod phenomenonTime = new TimePeriod();
        TimePeriod resultTime = new TimePeriod();
        for (String offering : offerings) {
            phenomenonTime.extendToContain(getPhenomeonTime(offering));
            resultTime.extendToContain(getResultTime(offering));
        }
        sosOffering.setPhenomenonTime(phenomenonTime);
        sosOffering.setResultTime(resultTime);
    }

    protected void setUpTimeForOffering(final String offering, final SosObservationOffering sosOffering) {
        sosOffering.setPhenomenonTime(getPhenomeonTime(offering));
        sosOffering.setResultTime(getResultTime(offering));
    }

    private TimePeriod getPhenomeonTime(String offering) {
        return new TimePeriod(getCache().getMinPhenomenonTimeForOffering(offering), getCache()
                .getMaxPhenomenonTimeForOffering(offering));
    }

    private TimePeriod getResultTime(String offering) {
        return new TimePeriod(getCache().getMinResultTimeForOffering(offering), getCache()
                .getMaxResultTimeForOffering(offering));
    }

    protected void setUpFeatureOfInterestTypesForOffering(String offering, SosObservationOffering sosOffering) {
        setUpFeatureOfInterestTypesForOffering(Sets.newHashSet(offering), sosOffering);
    }

    protected void setUpFeatureOfInterestTypesForOffering(Set<String> offerings,
            SosObservationOffering sosOffering) {
        Set<String> featureOfInterestTypes = Sets.newHashSet();
        for (String offering : offerings) {
            featureOfInterestTypes.addAll(getCache().getAllowedFeatureOfInterestTypesForOffering(offering));
        }
        sosOffering.setFeatureOfInterestTypes(featureOfInterestTypes);

    }

    protected void setUpResponseFormatForOffering(final String version, final SosObservationOffering sosOffering) {
        // initialize as new HashSet so that collection is modifiable
        sosOffering.setResponseFormats(new HashSet<>(getResponseFormatRepository()
                .getSupportedResponseFormats(SosConstants.SOS, version)));
        // TODO set as property
    }

    protected Set<String> getResponseFormatForOffering(String offering, String version) {
        Set<String> responseFormats = Sets.newHashSet();
        for (String observationType : getCache().getAllObservationTypesForOffering(offering)) {
            Set<String> responseFormatsForObservationType = getResponseFormatsForObservationType(observationType, SosConstants.SOS, version);
            if (CollectionHelper.isNotEmpty(responseFormatsForObservationType)) {
                responseFormats.addAll(responseFormatsForObservationType);
            }
        }
        if (Sos1Constants.SERVICEVERSION.equals(version)) {
           return checkForMimeType(responseFormats, true);
        } else if (Sos2Constants.SERVICEVERSION.equals(version)) {
           return checkForMimeType(responseFormats, false);
        }
        return responseFormats;
    }

    private Set<String> checkForMimeType(Set<String> responseFormats, boolean onlyMimeTypes) {
        Set<String> validFormats = Sets.newHashSet();
        for (String format : responseFormats) {
            boolean isMediaType = MediaType.parse(format) != null;
            if (isMediaType && onlyMimeTypes) {
                validFormats.add(format);
            } else if (!isMediaType && !onlyMimeTypes) {
                validFormats.add(format);
            }
        }
        return validFormats;
    }

    protected void setUpProcedureDescriptionFormatForOffering(final SosObservationOffering sosOffering,
            final String version) {
        // TODO: set procDescFormat <-- what is required here?
        sosOffering.setProcedureDescriptionFormat(procedureDescriptionFormatRepository
                .getSupportedProcedureDescriptionFormats(SosConstants.SOS, version));
    }

    private ReferencedEnvelope getObservedArea(Set<String> offerings) throws CodedException {
        ReferencedEnvelope envelope = new ReferencedEnvelope();
        for (String offering : offerings) {
            envelope.expandToInclude(getObservedArea(offering));
        }
        return envelope;
    }

    private ReferencedEnvelope getObservedArea(String offering) throws CodedException {
        if (getCache().hasSpatialFilteringProfileEnvelopeForOffering(offering)) {
            return processObservedArea(getCache()
                    .getSpatialFilteringProfileEnvelopeForOffering(offering));
        } else {
            return processObservedArea(getCache()
                    .getEnvelopeForOffering(offering));
        }
    }

    private Collection<String> getProceduresForOffering(Set<String> offerings, String version) throws OwsExceptionReport {
        final Collection<String> procedures = Sets.newHashSet();
        for (String offering : offerings) {
            procedures.addAll(getProceduresForOffering(offering, version));
        }
        return procedures;
    }

    private Collection<String> getProceduresForOffering(final String offering, final String version)
            throws OwsExceptionReport {
        final Collection<String> procedures = Sets.newHashSet(getCache().getProceduresForOffering(offering));
        if (version.equals(Sos1Constants.SERVICEVERSION)) {
            procedures.addAll(getCache().getHiddenChildProceduresForOffering(offering));
        }
        Collection<String> published = Sets.newHashSet();
        for (String procedure : procedures) {
            if (getCache().getPublishedProcedures().contains(procedure)) {
                published.add(procedure);
            }
        }
        return published;
    }

    private boolean isV2(GetCapabilitiesResponse response) {
        return response.getVersion().equals(Sos2Constants.SERVICEVERSION);
    }

    private boolean isContentsSectionRequested(int sections) {
        return (sections & CONTENTS) != 0;
    }

    private boolean isFilterCapabilitiesSectionRequested(int sections) {
        return (sections & FILTER_CAPABILITIES) != 0;
    }

    private boolean isOperationsMetadataSectionRequested(int sections) {
        return (sections & OPERATIONS_METADATA) != 0;
    }

    private boolean isServiceProviderSectionRequested(int sections) {
        return (sections & SERVICE_PROVIDER) != 0;
    }

    private boolean isServiceIdentificationSectionRequested(int sections) {
        return (sections & SERVICE_IDENTIFICATION) != 0;
    }

    private String getGetDataAvailabilityUrl() {
        return new StringBuilder(getBaseGetUrl()).append(addParameter("", "request", "GetDataAvailability")).toString();
    }

    private String getBaseGetUrl() {
        final StringBuilder url = new StringBuilder();
        // service URL
        url.append(getServiceURL());
        // ?
        url.append('?');
        // service
        url.append(OWSConstants.RequestParams.service.name()).append('=').append(SosConstants.SOS);
        // version
        url.append('&').append(OWSConstants.RequestParams.version.name()).append('=')
                .append(Sos2Constants.SERVICEVERSION);
        return url.toString();
    }

    private String addParameter(String url, String parameter, String value) {
        return new StringBuilder(url).append('&').append(parameter).append('=').append(value).toString();
    }

    protected RequestOperatorRepository getRequestOperatorRepository() {
        return this.requestOperatorRepository;
    }

    protected ResponseFormatRepository getResponseFormatRepository() {
        return this.responseFormatRepository;
    }

    private void createStaticCapabilities(GetCapabilitiesRequest request, GetCapabilitiesResponse response) throws
            OwsExceptionReport {
        response.setXmlString(this.capabilitiesExtensionService.getActiveStaticCapabilitiesDocument());
    }

    private void createStaticCapabilitiesWithId(GetCapabilitiesRequest request, GetCapabilitiesResponse response) throws
            OwsExceptionReport {
        StaticCapabilities sc = this.capabilitiesExtensionService.getStaticCapabilities(request.getCapabilitiesId());
        if (sc == null) {
            throw new InvalidParameterValueException(GetCapabilitiesParams.CapabilitiesId, request.getCapabilitiesId());
        }
        response.setXmlString(sc.getDocument());
    }

    private void createDynamicCapabilities(GetCapabilitiesRequest request, GetCapabilitiesResponse response) throws
            OwsExceptionReport {
        Set<String> availableExtensionSections = getExtensionSections(response.getService(), response.getVersion());
        Set<String> requestedExtensionSections = new HashSet<>(availableExtensionSections.size());
        int requestedSections
                = identifyRequestedSections(request, response, availableExtensionSections, requestedExtensionSections);

        SosCapabilities sosCapabilities
                = new SosCapabilities(request.getService(), request.getVersion(), null, null, null, null, null, null, null, null);

        SectionSpecificContentObject sectionSpecificContentObject = new SectionSpecificContentObject()
                .setRequest(request)
                .setResponse(response)
                .setRequestedExtensionSections(requestedExtensionSections)
                .setRequestedSections(requestedSections)
                .setSosCapabilities(sosCapabilities);

        addSectionSpecificContent(sectionSpecificContentObject, request);
        response.setCapabilities(sosCapabilities);

    }

    private List<OwsOperation> getOperations(String service, String version) throws OwsExceptionReport {
        CompositeOwsException exceptions = new CompositeOwsException();
        List<OwsOperation> operations
                = getRequestOperatorRepository()
                        .getActiveRequestOperatorKeys()
                        .stream()
                        .filter(k -> k.getService().equals(service))
                        .filter(k -> k.getVersion().equals(version))
                        .map(getRequestOperatorRepository()::getRequestOperator)
                        .map(operator -> {
                            try {
                                return operator.getOperationMetadata(service, version);
                            } catch (OwsExceptionReport ex) {
                                exceptions.add(ex);
                                return null;
                            }
                        })
                        .filter(Objects::nonNull)
                        .collect(toList());
        exceptions.throwIfNotEmpty();
        return operations;

    }

    private List<OwsDomain> getParameters(String service, String version) {
        return Arrays.asList(getServiceParameter(service, version),
                             getVersionParameter(service, version),
                             getCrsParameter(service, version),
                             getLanguageParameter(service, version));
    }

    private static boolean isV1(GetCapabilitiesResponse response) {
        return response.getVersion().equals(Sos1Constants.SERVICEVERSION);
    }

    private OwsDomain getServiceParameter(String service, String version) {
        OwsValue owsValue = new OwsValue(SosConstants.SOS);
        return new OwsDomain(OWSConstants.RequestParams.service, new OwsAllowedValues(owsValue));
    }

    private OwsDomain getVersionParameter(String service, String version) {
        Set<String> supportedVersions = getServiceOperatorRepository().getSupportedVersions(service);
        Stream<OwsValue> allowedValues = supportedVersions.stream().map(OwsValue::new);
        return new OwsDomain(OWSConstants.RequestParams.version, new OwsAllowedValues(allowedValues));
    }

    private OwsDomain getCrsParameter(String service, String version) {
        Set<String> crs = this.geometryHandler.addOgcCrsPrefix(getCache().getEpsgCodes());
        Stream<OwsValue> allowedValues = crs.stream().map(OwsValue::new);
        return new OwsDomain(OWSConstants.AdditionalRequestParams.crs, new OwsAllowedValues(allowedValues));
    }

    private OwsDomain getLanguageParameter(String service, String version) {
        Set<Locale> languages = getCache().getSupportedLanguages();
        Stream<OwsValue> allowedValues = languages.stream().map(LocaleHelper::encode).map(OwsValue::new);
        return new OwsDomain(OWSConstants.AdditionalRequestParams.language, new OwsAllowedValues(allowedValues));
    }

    private static <X, T> Function<X, Set<T>> createSynchronizedSet() {
        return Suppliers.<X, Set<T>>asFunction(HashSet<T>::new).andThen(Collections::synchronizedSet);
    }

    private class SectionSpecificContentObject {
        private GetCapabilitiesRequest request;
        private GetCapabilitiesResponse response;
        private Set<String> requestedExtensionSections;
        private int requestedSections;
        private SosCapabilities sosCapabilities;

        public SectionSpecificContentObject setRequest(GetCapabilitiesRequest request) {
            this.request = request;
            return this;
        }

        public GetCapabilitiesRequest getGetCapabilitiesRequest() {
            return request;
        }

        public SectionSpecificContentObject setResponse(GetCapabilitiesResponse response) {
            this.response = response;
            return this;
        }

        public GetCapabilitiesResponse getGetCapabilitiesResponse() {
            return response;
        }

        public SectionSpecificContentObject setRequestedExtensionSections(Set<String> requestedExtensionSections) {
            this.requestedExtensionSections = requestedExtensionSections;
            return this;
        }

        public Set<String> getRequestedExtensionSesctions() {
            return requestedExtensionSections;
        }

        public SectionSpecificContentObject setRequestedSections(int requestedSections) {
            this.requestedSections = requestedSections;
            return this;
        }

        public int getRequestedSections() {
            return requestedSections;
        }

        public SectionSpecificContentObject setSosCapabilities(SosCapabilities sosCapabilities) {
            this.sosCapabilities = sosCapabilities;
            return this;
        }

        public SosCapabilities getSosCapabilities() {
            return sosCapabilities;
        }
    }

}
