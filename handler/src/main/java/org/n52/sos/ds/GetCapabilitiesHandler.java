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

import java.net.URI;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
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
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.inject.Inject;
import javax.xml.namespace.QName;

import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.locationtech.jts.geom.Geometry;
import org.n52.iceland.binding.BindingRepository;
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
import org.n52.io.request.IoParameters;
import org.n52.janmayen.Comparables;
import org.n52.janmayen.function.Functions;
import org.n52.janmayen.i18n.LocaleHelper;
import org.n52.series.db.DataAccessException;
import org.n52.series.db.HibernateSessionStore;
import org.n52.series.db.beans.FormatEntity;
import org.n52.series.db.beans.OfferingEntity;
import org.n52.series.db.beans.PhenomenonEntity;
import org.n52.series.db.beans.ProcedureEntity;
import org.n52.series.db.dao.DbQuery;
import org.n52.series.db.dao.OfferingDao;
import org.n52.series.db.dao.PhenomenonDao;
import org.n52.series.db.dao.ProcedureDao;
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
import org.n52.shetland.ogc.ows.exception.NoApplicableCodeException;
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
import org.n52.sos.coding.encode.ProcedureDescriptionFormatRepository;
import org.n52.sos.coding.encode.ResponseFormatRepository;
import org.n52.sos.config.CapabilitiesExtensionService;
import org.n52.sos.ogc.sos.SosObservationOfferingExtensionProvider;
import org.n52.sos.ogc.sos.SosObservationOfferingExtensionRepository;
import org.n52.sos.util.GeometryHandler;
import org.n52.sos.util.I18NHelper;
import org.n52.sos.util.JTSConverter;
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
public class GetCapabilitiesHandler extends AbstractGetCapabilitiesHandler implements ApiQueryHelper {

    private static final Logger LOGGER = LoggerFactory.getLogger(GetCapabilitiesHandler.class);

    /* section flags (values are powers of 2) */
    private static final int SERVICE_IDENTIFICATION = 0x01;
    private static final int SERVICE_PROVIDER = 0x02;
    private static final int OPERATIONS_METADATA = 0x04;
    private static final int FILTER_CAPABILITIES = 0x08;
    private static final int CONTENTS = 0x10;
    private static final int ALL = 0x20 | SERVICE_IDENTIFICATION | SERVICE_PROVIDER | OPERATIONS_METADATA |
                                   FILTER_CAPABILITIES | CONTENTS;

    @Inject
    private HibernateSessionStore sessionStore;
    @Inject
    private CapabilitiesExtensionService capabilitiesExtensionService;
    @Inject
    private EncoderRepository encoderRepository;
    @Inject
    private DecoderRepository decoderRepository;
    @Inject
    private OperationHandlerRepository operationHandlerRepository;
    @Inject
    private BindingRepository bindingRepository;
    @Inject
    private OwsServiceMetadataRepository serviceMetadataRepository;
    @Inject
    private RequestOperatorRepository requestOperatorRepository;
    @Inject
    private ResponseFormatRepository responseFormatRepository;
    @Inject
    private GeometryHandler geometryHandler;
    @Inject
    private OwsOperationMetadataExtensionProviderRepository owsOperationMetadataExtensionProviderRepository;
    @Inject
    private SosObservationOfferingExtensionRepository offeringExtensionRepository;
    @Inject
    private OwsCapabilitiesExtensionRepository capabilitiesExtensionRepository;
    @Inject
    private ProcedureDescriptionFormatRepository procedureDescriptionFormatRepository;

    private String serviceURL;

    public GetCapabilitiesHandler() {
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
        } else if (capabilitiesId != null && !capabilitiesId.equals(GetCapabilitiesParams.DYNAMIC_CAPABILITIES_IDENTIFIER)) {
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
                        .max(Comparables.version()).orElseThrow(() ->  new InvalidServiceParameterException(service));
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
        String verion = sectionSpecificContentObject.getGetCapabilitiesResponse().getVersion();
        String service = sectionSpecificContentObject.getGetCapabilitiesResponse().getService();



        if (isServiceIdentificationSectionRequested(sectionSpecificContentObject.getRequestedSections())) {
            sectionSpecificContentObject.getSosCapabilities()
                    .setServiceIdentification(getServiceIdentification(request, service, verion));
        }
        if (isServiceProviderSectionRequested(sectionSpecificContentObject.getRequestedSections())) {
            sectionSpecificContentObject.getSosCapabilities().setServiceProvider(this.serviceMetadataRepository
                    .getServiceProviderFactory(service).get());
        }
        if (isOperationsMetadataSectionRequested(sectionSpecificContentObject.getRequestedSections())) {
            sectionSpecificContentObject.getSosCapabilities()
                    .setOperationsMetadata(getOperationsMetadataForOperations(request, service, verion));
        }
        if (isFilterCapabilitiesSectionRequested(sectionSpecificContentObject.getRequestedSections())) {
            sectionSpecificContentObject.getSosCapabilities().setFilterCapabilities(getFilterCapabilities(verion));
        }
        if (isContentsSectionRequested(sectionSpecificContentObject.getRequestedSections())) {
            if (isV2(sectionSpecificContentObject.getGetCapabilitiesResponse())) {
                sectionSpecificContentObject.getSosCapabilities().setContents(getContentsForSosV2(sectionSpecificContentObject));
            } else {
                sectionSpecificContentObject.getSosCapabilities().setContents(getContents(sectionSpecificContentObject));
            }
        }

        if (isV2(sectionSpecificContentObject.getGetCapabilitiesResponse())) {
            if (sectionSpecificContentObject.getRequestedSections() == ALL) {
                sectionSpecificContentObject.getSosCapabilities().setExtensions(getAndMergeExtensions(service, verion));
            } else if (!sectionSpecificContentObject.getRequestedExtensionSections().isEmpty()) {
                sectionSpecificContentObject.getSosCapabilities().setExtensions(
                        getExtensions(sectionSpecificContentObject.getRequestedExtensionSections(), service, verion));
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

        Set<URI> profiles = Stream.of(this.bindingRepository.getBindings().values(),
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
        if ("hydrology".equalsIgnoreCase(getProfileHandler().getActiveProfile().getIdentifier())) {
            profiles.add(URI.create("http://www.opengis.net/spec/SOS_application-profile_hydrology/1.0/req/hydrosos"));
        }
        return profiles;
    }

    /**
     * Get the OperationsMetadat for all supported operations
     *
     * @param service
     *                Requested service
     * @param version
     *                Requested service version
     *
     * @return OperationsMetadata for all operations supported by the requested
     *         service and version
     *
     * @throws OwsExceptionReport
     *                            If an error occurs
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
        OwsOperationMetadataExtensionProvider provider = owsOperationMetadataExtensionProviderRepository.getExtendedCapabilitiesProvider(service, version);
        if (provider != null && provider.hasExtendedCapabilitiesFor(request)) {
            owsExtendedCapabilities = provider.getOwsExtendedCapabilities(request);
        }
        return new OwsOperationsMetadata(operations, parameters, constraints, owsExtendedCapabilities);
    }

    /**
     * Get the FilterCapabilities
     *
     * @param version
     *                Requested service version
     *
     * @return FilterCapabilities
     */
    private FilterCapabilities getFilterCapabilities(final String version) {
        final FilterCapabilities filterCapabilities = new FilterCapabilities();
        if (Sos2Constants.SERVICEVERSION.equals(version)) {
            getConformance(filterCapabilities);
        }
        // !!! Modify methods addicted to your implementation !!!
        if (version.equals(Sos1Constants.SERVICEVERSION)) {
            getScalarFilterCapabilities(filterCapabilities);
        }
        getSpatialFilterCapabilities(filterCapabilities, version);
        getTemporalFilterCapabilities(filterCapabilities, version);

        return filterCapabilities;
    }

    private void getConformance(FilterCapabilities filterCapabilities) {
        OwsValue trueValue = new OwsValue("true");
        OwsValue falseValue = new OwsValue("false");
        OwsNoValues noValues = OwsNoValues.instance();
        filterCapabilities.addConformance(new OwsDomain(ConformanceClassConstraintNames.ImplementsQuery, noValues, falseValue));
        filterCapabilities.addConformance(new OwsDomain(ConformanceClassConstraintNames.ImplementsAdHocQuery, noValues, falseValue));
        filterCapabilities.addConformance(new OwsDomain(ConformanceClassConstraintNames.ImplementsFunctions, noValues, falseValue));
        filterCapabilities.addConformance(new OwsDomain(ConformanceClassConstraintNames.ImplementsResourceld, noValues, falseValue));
        filterCapabilities.addConformance(new OwsDomain(ConformanceClassConstraintNames.ImplementsMinStandardFilter, noValues, falseValue));
        filterCapabilities.addConformance(new OwsDomain(ConformanceClassConstraintNames.ImplementsStandardFilter, noValues, falseValue));
        filterCapabilities.addConformance(new OwsDomain(ConformanceClassConstraintNames.ImplementsMinSpatialFilter, noValues, trueValue));
        filterCapabilities.addConformance(new OwsDomain(ConformanceClassConstraintNames.ImplementsSpatialFilter, noValues, trueValue));
        filterCapabilities.addConformance(new OwsDomain(ConformanceClassConstraintNames.ImplementsMinTemporalFilter, noValues, trueValue));
        filterCapabilities.addConformance(new OwsDomain(ConformanceClassConstraintNames.ImplementsTemporalFilter, noValues, trueValue));
        filterCapabilities.addConformance(new OwsDomain(ConformanceClassConstraintNames.ImplementsVersionNav, noValues, falseValue));
        filterCapabilities.addConformance(new OwsDomain(ConformanceClassConstraintNames.ImplementsSorting, noValues, falseValue));
        filterCapabilities.addConformance(new OwsDomain(ConformanceClassConstraintNames.ImplementsExtendedOperators, noValues, falseValue));
        filterCapabilities.addConformance(new OwsDomain(ConformanceClassConstraintNames.ImplementsMinimumXPath, noValues, falseValue));
        filterCapabilities.addConformance(new OwsDomain(ConformanceClassConstraintNames.ImplementsSchemaElementFunc, noValues, falseValue));
    }

    /**
     * Get the contents for SOS 1.0.0 capabilities
     *
     * @param version
     *                Requested service version
     *
     * @return Offerings for contents
     *
     *
     * @throws OwsExceptionReport
     *             * If an error occurs
     */
    private List<SosObservationOffering> getContents(SectionSpecificContentObject sectionSpecificContentObject) throws
            OwsExceptionReport {
        Session session = null;
        try {
            session = sessionStore.getSession();
            String version = sectionSpecificContentObject.getGetCapabilitiesResponse().getVersion();
            final Collection<OfferingEntity> offerings = new OfferingDao(session).getAllInstances(new DbQuery(IoParameters.createDefaults()));
            final List<SosObservationOffering> sosOfferings = new ArrayList<>(offerings.size());
            for (final OfferingEntity offering : offerings) {
                final Collection<ProcedureEntity> procedures = getProceduresForOffering(offering, session);
                final ReferencedEnvelope envelopeForOffering = getCache().getEnvelopeForOffering(offering.getIdentifier());
                final Set<String> featuresForoffering = getFOI4offering(offering.getIdentifier());
                final Collection<String> responseFormats = getResponseFormatRepository()
                        .getSupportedResponseFormats(SosConstants.SOS, Sos1Constants.SERVICEVERSION);
                if (checkOfferingValues(envelopeForOffering, featuresForoffering, responseFormats)) {
                    final SosObservationOffering sosObservationOffering = new SosObservationOffering();

                    // insert observationTypes
                    sosObservationOffering.setObservationTypes(getObservationTypes(offering.getIdentifier()));

                    // only if fois are contained for the offering set the values of
                    // the envelope
                    if (offering.isSetGeometry()) {
                        sosObservationOffering.setObservedArea(processObservedArea(JTSConverter.convert(offering.getGeometry())));
                    } else if (getCache().hasEnvelopeForOffering(offering.getIdentifier())) {
                        sosObservationOffering.setObservedArea(getCache().getEnvelopeForOffering(offering.getIdentifier()));
                    }

                    // TODO: add intended application
                    // xb_oo.addIntendedApplication("");
                    // add offering name
                    addSosOfferingToObservationOffering(offering, sosObservationOffering,
                                                        sectionSpecificContentObject.getGetCapabilitiesRequest());

                    // set up phenomena
                    sosObservationOffering
                            .setObservableProperties(getCache().getObservablePropertiesForOffering(offering.getIdentifier()));
                    sosObservationOffering.setCompositePhenomena(getCache().getCompositePhenomenonsForOffering(offering.getIdentifier()));
                    final Map<String, Collection<String>> phens4CompPhens = new HashMap<>();
                    if (getCache().getCompositePhenomenonsForOffering(offering.getIdentifier()) != null) {
                        for (final String compositePhenomenon : getCache().getCompositePhenomenonsForOffering(offering.getIdentifier())) {
                            phens4CompPhens.put(compositePhenomenon, getCache()
                                                .getObservablePropertiesForCompositePhenomenon(compositePhenomenon));
                        }
                    }
                    sosObservationOffering.setPhens4CompPhens(phens4CompPhens);

                    // set up time
                    setUpTimeForOffering(offering, sosObservationOffering);

                    // add feature of interests
                    if (getProfileHandler().getActiveProfile().isListFeatureOfInterestsInOfferings()) {
                        sosObservationOffering.setFeatureOfInterest(getFOI4offering(offering.getIdentifier()));
                    }

                    // set procedures
                    sosObservationOffering.setProcedures(
                            procedures.stream().map(p -> p.getIdentifier()).collect(Collectors.toSet()));

                    // insert result models
                    final Collection<QName> resultModels = OMHelper.getQNamesForResultModel(getCache()
                            .getObservationTypesForOffering(offering.getIdentifier()));
                    sosObservationOffering.setResultModels(resultModels);

                    // set response format
                    sosObservationOffering.setResponseFormats(responseFormats);

                    // set response Mode
                    sosObservationOffering.setResponseModes(SosConstants.RESPONSE_MODES);

                    sosOfferings.add(sosObservationOffering);
                }
            }

            return sosOfferings;
        } catch (final HibernateException | DataAccessException e) {
            throw new NoApplicableCodeException().causedBy(e).withMessage(
                    "Error while querying data for GetCapabilities document!");
        } finally {
            sessionStore.returnSession(session);
        }
    }

//    private ReferencedEnvelope processObservedArea(ReferencedEnvelope sosEnvelope) throws CodedException {
//        // TODO Check transformation
//        // if (requestedSrid >= 0 && sosEnvelope.getSrid() != requestedSrid) {
//        // ReferencedEnvelope tranformedEnvelope = new ReferencedEnvelope();
//        // tranformedEnvelope.setSrid(requestedSrid);
//        // tranformedEnvelope.setEnvelope(GeometryHandler.getInstance().transformEnvelope(sosEnvelope.getEnvelope(),
//        // sosEnvelope.getSrid(), requestedSrid));
//        // return tranformedEnvelope;
//        // }
//        return sosEnvelope;
//    }

    private ReferencedEnvelope processObservedArea(Geometry geometry) throws CodedException {
        return new ReferencedEnvelope(geometry.getEnvelopeInternal(), geometry.getSRID());
    }

    private boolean checkOfferingValues(final ReferencedEnvelope envelopeForOffering, final Set<String> featuresForOffering,
                                        final Collection<String> responseFormats) {
        return ReferencedEnvelope.isNotNullOrEmpty(envelopeForOffering) && CollectionHelper.isNotEmpty(featuresForOffering) &&
                 CollectionHelper.isNotEmpty(responseFormats);
    }

    /**
     * Get the contents for SOS 2.0 capabilities
     *
     * @param version
     *                Requested service version
     *
     * @return Offerings for contents
     *
     *
     * @throws OwsExceptionReport
     *             * If an error occurs
     */
    // FIXME why version parameter? The method signature cleary states which
    // version is supported by this!
    private List<SosObservationOffering> getContentsForSosV2(SectionSpecificContentObject sectionSpecificContentObject)
            throws OwsExceptionReport {
        Session session = null;
        try {
            session = sessionStore.getSession();
            String verssion = sectionSpecificContentObject.getGetCapabilitiesResponse().getVersion();
            final Collection<OfferingEntity> offerings = new OfferingDao(session).getAllInstances(new DbQuery(IoParameters.createDefaults()));
            final List<SosObservationOffering> sosOfferings = new ArrayList<>(offerings.size());
            final Map<String, List<SosObservationOfferingExtension>> extensions = this.capabilitiesExtensionService
                    .getActiveOfferingExtensions();

            if (CollectionHelper.isEmpty(offerings)) {
                // Set empty offering to add empty Contents section to Capabilities
                sosOfferings.add(new SosObservationOffering());
            } else {
                if (checkListOnlyParentOfferings()) {
                    sosOfferings.addAll(createAndGetParentOfferings(offerings, sectionSpecificContentObject, extensions, session));
                } else {
                    for (final OfferingEntity offering : offerings) {
                        final Collection<ProcedureEntity> procedures = getProceduresForOffering(offering, session);
                        if (!procedures.isEmpty()) {
                            final Collection<FormatEntity> observationTypes = offering.getObservationTypes();
                            if (observationTypes != null && !observationTypes.isEmpty()) {
                                // FIXME why a loop? We are in SOS 2.0 context -> offering 1
                                // <-> 1 procedure!
                                for (final ProcedureEntity procedure : procedures) {

                                    final SosObservationOffering sosObservationOffering = new SosObservationOffering();

                                    // insert observationTypes
                                    sosObservationOffering.setObservationTypes(toStringSet(observationTypes));

                                    if (offering.isSetGeometry()) {
                                        sosObservationOffering.setObservedArea(processObservedArea(JTSConverter.convert(offering.getGeometry())));
                                    } else if (getCache().hasEnvelopeForOffering(offering.getIdentifier())) {
                                        sosObservationOffering.setObservedArea(getCache().getEnvelopeForOffering(offering.getIdentifier()));
                                    }

                                    sosObservationOffering.setProcedures(Collections.singletonList(procedure.getIdentifier()));

                                    // TODO: add intended application

                                    // add offering to observation offering
                                    addSosOfferingToObservationOffering(offering, sosObservationOffering,
                                            sectionSpecificContentObject.getGetCapabilitiesRequest());
                                    // add offering extension
                                    if (offeringExtensionRepository.hasOfferingExtensionProviderFor(
                                            sectionSpecificContentObject.getGetCapabilitiesRequest())) {
                                        for (SosObservationOfferingExtensionProvider provider : offeringExtensionRepository
                                                .getOfferingExtensionProvider(
                                                        sectionSpecificContentObject.getGetCapabilitiesRequest())) {
                                            if (provider != null && provider.hasExtendedOfferingFor(offering.getIdentifier())) {
                                                sosObservationOffering.addExtensions(provider.getOfferingExtensions(offering.getIdentifier()));
                                            }
                                        }
                                    }
                                    if (extensions.containsKey(sosObservationOffering.getOffering().getIdentifier())) {
                                        for (SosObservationOfferingExtension offeringExtension : extensions
                                                .get(sosObservationOffering.getOffering().getIdentifier())) {
                                            sosObservationOffering.addExtension(
                                                    new CapabilitiesExtension<SosObservationOfferingExtension>().setValue(offeringExtension));
                                        }
                                    }

                                    setUpPhenomenaForOffering(offering, procedure, sosObservationOffering, session);
                                    setUpTimeForOffering(offering, sosObservationOffering);
                                    setUpRelatedFeaturesForOffering(offering, sosObservationOffering);
                                    setUpFeatureOfInterestTypesForOffering(offering, sosObservationOffering);
                                    setUpProcedureDescriptionFormatForOffering(sosObservationOffering);
                                    setUpResponseFormatForOffering(sosObservationOffering);

                                    sosOfferings.add(sosObservationOffering);
                                }
                            }
                        } else {
                            LOGGER.error( "No procedures are contained in the database for the offering {}! Please contact the admin of this SOS.", offering.getIdentifier());
                        }
                    }
                }
            }

            return sosOfferings;
        } catch (final HibernateException | DataAccessException e) {
            throw new NoApplicableCodeException().causedBy(e).withMessage(
                    "Error while querying data for GetCapabilities document!");
        } finally {
            sessionStore.returnSession(session);
        }
    }

    private Collection<? extends SosObservationOffering> createAndGetParentOfferings(Collection<OfferingEntity> offerings,
            SectionSpecificContentObject sectionSpecificContentObject,
            Map<String, List<SosObservationOfferingExtension>> extensions, Session session) throws OwsExceptionReport, DataAccessException {
        Map<OfferingEntity, Set<OfferingEntity>> parentChilds  = getParentOfferings(offerings);
        final List<SosObservationOffering> sosOfferings = new ArrayList<SosObservationOffering>(parentChilds.size());
        for (Entry<OfferingEntity, Set<OfferingEntity>> entry : parentChilds.entrySet()) {
            final Collection<String> observationTypes = getObservationTypes(entry);
            if (CollectionHelper.isNotEmpty(observationTypes)) {
                Collection<ProcedureEntity> procedures = getProceduresForOffering(entry, session);
                if (CollectionHelper.isNotEmpty(procedures)) {
                    Set<OfferingEntity> allOfferings =Sets.newHashSet();
                    allOfferings.addAll(entry.getValue());
                    allOfferings.add(entry.getKey());
                    final SosObservationOffering sosObservationOffering = new SosObservationOffering();
                    sosObservationOffering.setObservationTypes(observationTypes);
                    sosObservationOffering.setObservedArea(getObservedArea(entry));

                    sosObservationOffering.setProcedures(
                            procedures.stream().map(p -> p.getIdentifier()).collect(Collectors.toSet()));
                    //
//                    // TODO: add intended application
//
//                    // add offering to observation offering
                    addSosOfferingToObservationOffering(entry.getKey(), sosObservationOffering,
                            sectionSpecificContentObject.getGetCapabilitiesRequest());
                    // add offering extension
                    if (offeringExtensionRepository.hasOfferingExtensionProviderFor(
                            sectionSpecificContentObject.getGetCapabilitiesRequest())) {
                        for (SosObservationOfferingExtensionProvider provider : offeringExtensionRepository
                                .getOfferingExtensionProvider(
                                        sectionSpecificContentObject.getGetCapabilitiesRequest())) {
                            if (provider != null && provider.hasExtendedOfferingFor(entry.getKey().getIdentifier())) {
                                sosObservationOffering.addExtensions(provider.getOfferingExtensions(entry.getKey().getIdentifier()));
                            }
                        }
                    }
                    if (extensions.containsKey(sosObservationOffering.getOffering().getIdentifier())) {
                        for (SosObservationOfferingExtension offeringExtension : extensions.get(sosObservationOffering.getOffering().getIdentifier())) {
                            sosObservationOffering.addExtension(offeringExtension);
                        }
                    }
                    // add sub-level offerings
                    if (!entry.getValue().isEmpty()) {
                        RelatedOfferings relatedOfferings = new RelatedOfferings();
                        String gdaURL = getGetDataAvailabilityUrl();
                        gdaURL = addParameter(gdaURL, "responseFormat", "http://www.opengis.net/sosgda/2.0");
                        for (OfferingEntity offering : entry.getValue()) {
                            relatedOfferings.addValue(new ReferenceType(RelatedOfferingConstants.ROLE),
                                    new ReferenceType(
                                            addParameter(new StringBuilder(gdaURL).toString(), "offering", offering.getIdentifier()),
                                            offering.getIdentifier()));
                        }
                        sosObservationOffering.addExtension(relatedOfferings);
                    }

                    setUpPhenomenaForOffering(allOfferings, procedures.iterator().next(), sosObservationOffering, session);
                    setUpTimeForOffering(allOfferings, sosObservationOffering);
                    setUpRelatedFeaturesForOffering(allOfferings, sosObservationOffering);
                    setUpFeatureOfInterestTypesForOffering(allOfferings, sosObservationOffering);
                    setUpProcedureDescriptionFormatForOffering(sosObservationOffering, Sos2Constants.SERVICEVERSION);
                    setUpResponseFormatForOffering(Sos2Constants.SERVICEVERSION, sosObservationOffering);


                    sosOfferings.add(sosObservationOffering);
                }
            }
        }
        return sosOfferings;
    }



//    private void addSosOfferingToObservationOffering(String offering, SosObservationOffering sosObservationOffering,
//                                                     GetCapabilitiesRequest request) throws CodedException {
//        SosOffering sosOffering = new SosOffering(offering, false);
//        sosObservationOffering.setOffering(sosOffering);
//        // add offering name
//        I18NHelper.addOfferingNames(sosOffering, request);
//        // add offering description
//        I18NHelper.addOfferingDescription(sosOffering, request);
//    }

    private Map<OfferingEntity, Set<OfferingEntity>> getParentOfferings(Collection<OfferingEntity> offerings) {
        Map<OfferingEntity, Set<OfferingEntity>> parentChilds = Maps.newHashMap();
        for (OfferingEntity offering : offerings) {
            if (offering.hasParents()) {
                parentChilds.put(offering, getAllParents(offering));
            }
        }
        return parentChilds;
    }

    private  Set<OfferingEntity> getAllParents(OfferingEntity entity) {
        Set<OfferingEntity> parents = Sets.newHashSet();
         for (OfferingEntity offeringEntity : entity.getParents()) {
             parents.add(offeringEntity);
             parents.addAll(getAllParents(offeringEntity));
        }
        return parents;
    }

    private void addSosOfferingToObservationOffering(OfferingEntity offering,
            SosObservationOffering sosObservationOffering, GetCapabilitiesRequest request) throws CodedException {
        SosOffering sosOffering = new SosOffering(offering.getIdentifier(), false);
        sosObservationOffering.setOffering(sosOffering);
        // add offering name
        I18NHelper.addOfferingNames(getCache(), sosOffering, getRequestedLocale(request), Locale.ROOT, false);
        // add offering description
        I18NHelper.addOfferingDescription(sosOffering, getRequestedLocale(request), Locale.ROOT, getCache());
    }

    /**
     * Set SpatialFilterCapabilities to FilterCapabilities
     *
     * @param filterCapabilities
     *                           FilterCapabilities
     * @param version
     *                           SOS version
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
        final Map<SpatialOperator, Set<QName>> ops = Maps.newHashMap();
        if (version.equals(Sos2Constants.SERVICEVERSION)) {
            ops.put(SpatialOperator.BBOX, Sets.newHashSet(GmlConstants.QN_ENVELOPE_32));
        } else if (version.equals(Sos1Constants.SERVICEVERSION)) {
            ops.put(SpatialOperator.BBOX, Sets.newHashSet(GmlConstants.QN_ENVELOPE));
            // set Contains
            ops.put(SpatialOperator.Contains,
                    Sets.newHashSet(GmlConstants.QN_POINT, GmlConstants.QN_LINESTRING, GmlConstants.QN_POLYGON));
            // set Intersects
            ops.put(SpatialOperator.Intersects,
                    Sets.newHashSet(GmlConstants.QN_POINT, GmlConstants.QN_LINESTRING, GmlConstants.QN_POLYGON));
            // set Overlaps
            ops.put(SpatialOperator.Overlaps,
                    Sets.newHashSet(GmlConstants.QN_POINT, GmlConstants.QN_LINESTRING, GmlConstants.QN_POLYGON));
        }

        filterCapabilities.setSpatialOperators(ops);
    }

    /**
     * Set TemporalFilterCapabilities to FilterCapabilities
     *
     * @param filterCapabilities
     *                           FilterCapabilities
     * @param version
     *                           SOS version
     */
    private void getTemporalFilterCapabilities(final FilterCapabilities filterCapabilities, final String version) {

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
        final Map<TimeOperator, Set<QName>> ops = Maps.newHashMap();
        switch (version) {
            case Sos2Constants.SERVICEVERSION:
                for (final TimeOperator op : TimeOperator.values()) {
                    ops.put(op, Sets.newHashSet(GmlConstants.QN_TIME_INSTANT_32, GmlConstants.QN_TIME_PERIOD_32));
                }
                break;
            case Sos1Constants.SERVICEVERSION:
                for (final TimeOperator op : TimeOperator.values()) {
                    ops.put(op, Sets.newHashSet(GmlConstants.QN_TIME_INSTANT, GmlConstants.QN_TIME_PERIOD));
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
     * @param filterCapabilities
     *                           FilterCapabilities
     */
    private void getScalarFilterCapabilities(final FilterCapabilities filterCapabilities) {
        // TODO PropertyIsNil, PropertyIsNull? better:
        // filterCapabilities.setComparisonOperators(Arrays.asList(ComparisonOperator.values()));
        final List<ComparisonOperator> comparisonOperators = new ArrayList<>(8);
        comparisonOperators.add(ComparisonOperator.PropertyIsBetween);
        comparisonOperators.add(ComparisonOperator.PropertyIsEqualTo);
        comparisonOperators.add(ComparisonOperator.PropertyIsNotEqualTo);
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
     * @param offering
     *                 Offering identifier
     *
     * @return FOI identifiers
     *
     *
     * @throws OwsExceptionReport
     *             * If an error occurs
     */
    private Set<String> getFOI4offering(final String offering) throws OwsExceptionReport {
        final Set<String> featureIDs = new HashSet<>(0);
        final Set<String> features = getCache().getFeaturesOfInterestForOffering(offering);
        if (!getProfileHandler().getActiveProfile().isListFeatureOfInterestsInOfferings() ||
                 features == null) {
            featureIDs.add(OGCConstants.UNKNOWN);
        } else {
            featureIDs.addAll(features);
        }
        return featureIDs;
    }

    private Collection<String> getObservationTypes(Entry<OfferingEntity, Set<OfferingEntity>> entry) {
        final Set<String> observationTypes = Sets.newHashSet();
        if (!entry.getValue().isEmpty()) {
            for (OfferingEntity offering : entry.getValue()) {
                observationTypes.addAll(getObservationTypes(offering.getIdentifier()));
            }
        } else {
            observationTypes.addAll(getObservationTypes(entry.getKey().getIdentifier()));
        }
        return observationTypes;
    }

    private Collection<String> getObservationTypes(final String offering) {
        Set<String> observationTypes = getCache().getObservationTypesForOffering(offering).stream()
                .filter(Predicate.isEqual(SosConstants.NOT_DEFINED).negate())
                .collect(Collectors.toSet());

        if (observationTypes.isEmpty()) {
            getCache().getAllowedObservationTypesForOffering(offering).stream()
                    .filter(Predicate.isEqual(SosConstants.NOT_DEFINED).negate())
                    .forEach(observationTypes::add);
        }
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

    private Collection<OwsCapabilitiesExtension> getExtensions(Set<String> requestedExtensionSections, String service, String version)
            throws OwsExceptionReport {
        return getAndMergeExtensions(service, version).stream()
                .filter(e -> requestedExtensionSections.contains(e.getSectionName()))
                .collect(toList());
    }

    //    private void addSosOfferingToObservationOffering(String offering, SosObservationOffering sosObservationOffering,
    //                                                     GetCapabilitiesRequest request) throws CodedException {
    //        SosOffering sosOffering = new SosOffering(offering, false);
    //        sosObservationOffering.setOffering(sosOffering);
    //        // add offering name
    //        I18NHelper.addOfferingNames(sosOffering, request);
    //        // add offering description
    //        I18NHelper.addOfferingDescription(sosOffering, request);
    //    }

    protected void setUpPhenomenaForOffering(Set<OfferingEntity> allOfferings, ProcedureEntity procedure,
                SosObservationOffering sosObservationOffering, Session session) throws DataAccessException {
           for (OfferingEntity offering : allOfferings) {
               setUpPhenomenaForOffering(offering, procedure, sosObservationOffering, session);
           }
        }

    protected void setUpPhenomenaForOffering(OfferingEntity offering, ProcedureEntity procedure,
            SosObservationOffering sosOffering, Session session) throws DataAccessException {
        Map<String, String> map = Maps.newHashMap();
        map.put(IoParameters.OFFERINGS, Long.toString(offering.getId()));
        map.put(IoParameters.PROCEDURES, Long.toString(procedure.getId()));

        List<PhenomenonEntity> observableProperties =
                new PhenomenonDao(session).getAllInstances(new DbQuery(IoParameters.createFromSingleValueMap(map)));

        Collection<String> phenomenons = new LinkedList<>();
        Map<String, Collection<String>> phens4CompPhens = new HashMap<>();
        for (PhenomenonEntity observableProperty : observableProperties) {
            if (!observableProperty.hasChildren() && !observableProperty.hasParents()) {
                phenomenons.add(observableProperty.getIdentifier());
            } else if (observableProperty.hasChildren() && !observableProperty.hasParents()) {
                Set<String> childs = new TreeSet<String>();
                for (PhenomenonEntity child : observableProperty.getChildren()) {
                    childs.add(child.getIdentifier());
                }
            }
        }
        sosOffering.setObservableProperties(phenomenons);
        sosOffering.setPhens4CompPhens(phens4CompPhens);
        // sosOffering.setCompositePhenomena(getCache().getCompositePhenomenonsForOffering(offering));
        //
        // final Collection<String> compositePhenomenonsForOffering =
        // getCache().getCompositePhenomenonsForOffering(offering);
        //
        // if (compositePhenomenonsForOffering != null) {
        // final Map<String, Collection<String>> phens4CompPhens =
        // new HashMap<>(compositePhenomenonsForOffering.size());
        // for (final String compositePhenomenon :
        // compositePhenomenonsForOffering) {
        // final Collection<String> phenomenonsForComposite =
        // getCache().getObservablePropertiesForCompositePhenomenon(compositePhenomenon);
        // phens4CompPhens.put(compositePhenomenon, phenomenonsForComposite);
        // }
        // sosOffering.setPhens4CompPhens(phens4CompPhens);
        // } else {
        // sosOffering.setPhens4CompPhens(Collections.<String,
        // Collection<String>> emptyMap());
        // }

    }

    private void setUpTimeForOffering(Set<OfferingEntity> allOfferings,
            SosObservationOffering sosObservationOffering) {
        for (OfferingEntity offeringEntity : allOfferings) {
            setUpTimeForOffering(offeringEntity, sosObservationOffering);
        }
    }

    protected void setUpTimeForOffering(OfferingEntity offering, SosObservationOffering sosOffering) {
        sosOffering
                .setPhenomenonTime(new TimePeriod(offering.getPhenomenonTimeStart(), offering.getPhenomenonTimeEnd()));
        sosOffering.setResultTime(new TimePeriod(offering.getResultTimeStart(), offering.getResultTimeEnd()));
    }

    protected void setUpFeatureOfInterestTypesForOffering(Collection<OfferingEntity> offerings,
            SosObservationOffering sosOffering) {
        Set<String> types = new HashSet<>();
        for (OfferingEntity offeringEntity : offerings) {
            types.addAll(toStringSet(offeringEntity.getFeatureTypes()));
        }
        sosOffering.setFeatureOfInterestTypes(types);
    }

    protected void setUpFeatureOfInterestTypesForOffering(OfferingEntity offering,
            SosObservationOffering sosOffering) {
        sosOffering.setFeatureOfInterestTypes(toStringSet(offering.getFeatureTypes()));
    }


    protected void setUpResponseFormatForOffering(SosObservationOffering sosOffering) {
        // initialize as new HashSet so that collection is modifiable
        final Collection<String> responseFormats = new HashSet<>(
                getResponseFormatRepository().getSupportedResponseFormats(SosConstants.SOS, Sos2Constants.SERVICEVERSION));
        sosOffering.setResponseFormats(responseFormats);
        // TODO set as property
    }

    protected void setUpProcedureDescriptionFormatForOffering(SosObservationOffering sosOffering) {
        // TODO: set procDescFormat <-- what is required here?
        sosOffering.setProcedureDescriptionFormat(procedureDescriptionFormatRepository
                .getSupportedProcedureDescriptionFormats(SosConstants.SOS, Sos2Constants.SERVICEVERSION));
    }


//    protected void setUpPhenomenaForOffering(String offering, String procedure, SosObservationOffering sosOffering) {
//        final Collection<String> phenomenons = new LinkedList<>();
//        final Collection<String> observablePropertiesForOffering = getCache()
//                .getObservablePropertiesForOffering(offering);
//        observablePropertiesForOffering.forEach(observableProperty -> {
//            Set<String> proceduresForObservableProperty = getCache().getProceduresForObservableProperty(observableProperty);
//            if (proceduresForObservableProperty.contains(procedure) || isHiddenChildProcedureObservableProperty(offering, proceduresForObservableProperty)) {
//                phenomenons.add(observableProperty);
//            }
//        });
//        sosOffering.setObservableProperties(phenomenons);
//        sosOffering.setCompositePhenomena(getCache().getCompositePhenomenonsForOffering(offering));
//
//        final Collection<String> compositePhenomenonsForOffering = getCache()
//                .getCompositePhenomenonsForOffering(offering);
//
//        if (compositePhenomenonsForOffering != null) {
//            sosOffering.setPhens4CompPhens(compositePhenomenonsForOffering.stream().collect(toMap(Function.identity(),getCache()::getObservablePropertiesForCompositePhenomenon)));
//        } else {
//            sosOffering.setPhens4CompPhens(Collections.emptyMap());
//        }
//
//    }

    private boolean isHiddenChildProcedureObservableProperty(String offering, Set<String> proceduresForObservableProperty) {
        return getCache().getHiddenChildProceduresForOffering(offering).stream()
                .anyMatch(proceduresForObservableProperty::contains);
    }

//    protected void setUpRelatedFeaturesForOffering(String offering, String version, String procedure, SosObservationOffering sosOffering) throws OwsExceptionReport {
//
//            // TODO add setting to set FeatureOfInterest if relatedFeatures are empty.
//        sosOffering.setRelatedFeatures(getCache().getRelatedFeaturesForOffering(offering).stream()
//                .collect(toMap(Function.identity(), getCache()::getRolesForRelatedFeature)));
//    }

    private void setUpRelatedFeaturesForOffering(OfferingEntity offering,
            SosObservationOffering sosObservationOffering)
            throws OwsExceptionReport {
        setUpRelatedFeaturesForOffering(Sets.newHashSet(offering), sosObservationOffering);
    }

    private void setUpRelatedFeaturesForOffering(Collection<OfferingEntity> offerings,
            SosObservationOffering sosObservationOffering)
            throws OwsExceptionReport {
        final Map<String, Set<String>> relatedFeatures = Maps.newHashMap();
        for (OfferingEntity offering : offerings) {
            final Set<String> relatedFeaturesForThisOffering =
                    getCache().getRelatedFeaturesForOffering(offering.getIdentifier());
            if (CollectionHelper.isNotEmpty(relatedFeaturesForThisOffering)) {
                for (final String relatedFeature : relatedFeaturesForThisOffering) {
                    relatedFeatures.put(relatedFeature, getCache().getRolesForRelatedFeature(relatedFeature));
                }
                /*
                 * TODO add setting to set FeatureOfInterest if relatedFeatures
                 * are empty. } else { final Set<String> role =
                 * Collections.singleton("featureOfInterestID"); final
                 * Set<String> featuresForOffering =
                 * getCache().getFeaturesOfInterestForOffering(offering); if
                 * (featuresForOffering != null) { for (final String foiID :
                 * featuresForOffering) { if
                 * (getCache().getProceduresForFeatureOfInterest
                 * (foiID).contains(procedure)) { relatedFeatures.put(foiID,
                 * role); } } }
                 */

            }
        }
        sosObservationOffering.setRelatedFeatures(relatedFeatures);
    }

    protected void setUpTimeForOffering(String offering, SosObservationOffering sosOffering) {
        sosOffering.setPhenomenonTime(new TimePeriod(getCache().getMinPhenomenonTimeForOffering(offering),
                                                     getCache().getMaxPhenomenonTimeForOffering(offering)));
        sosOffering.setResultTime(new TimePeriod(getCache().getMinResultTimeForOffering(offering),
                                                 getCache().getMaxResultTimeForOffering(offering)));
    }

    protected void setUpFeatureOfInterestTypesForOffering(String offering, SosObservationOffering sosOffering) {
        sosOffering.setFeatureOfInterestTypes(getCache().getAllowedFeatureOfInterestTypesForOffering(offering));
    }

    protected void setUpResponseFormatForOffering(String version, SosObservationOffering sosOffering) {
        // initialize as new HashSet so that collection is modifiable
        sosOffering.setResponseFormats(new HashSet<>(getResponseFormatRepository()
                .getSupportedResponseFormats(SosConstants.SOS, version)));
        // TODO set as property
    }

    protected void setUpProcedureDescriptionFormatForOffering(SosObservationOffering sosOffering, String version) {
        // TODO: set procDescFormat <-- what is required here?
        sosOffering.setProcedureDescriptionFormat(procedureDescriptionFormatRepository
                .getSupportedProcedureDescriptionFormats(SosConstants.SOS, version));
    }

    private ReferencedEnvelope getObservedArea(Entry<OfferingEntity, Set<OfferingEntity>> entry) throws CodedException {
        ReferencedEnvelope envelope = new ReferencedEnvelope();
        if (!entry.getValue().isEmpty()) {
            for (OfferingEntity offering : entry.getValue()) {
                envelope.expandToInclude(getObservedArea(offering.getIdentifier()));
            }
        } else {
            envelope.expandToInclude(getObservedArea(entry.getKey().getIdentifier()));
        }
        return envelope;
    }

    private ReferencedEnvelope getObservedArea(String offering)
            throws CodedException {
        if (getCache().hasSpatialFilteringProfileEnvelopeForOffering(offering)) {
            return getCache().getSpatialFilteringProfileEnvelopeForOffering(offering);
        } else {
            return getCache().getEnvelopeForOffering(offering);
        }
    }

    private Collection<ProcedureEntity> getProceduresForOffering(Entry<OfferingEntity, Set<OfferingEntity>> entry, Session session) throws OwsExceptionReport, DataAccessException {
        final Collection<ProcedureEntity> procedures = Sets.newHashSet();
        if (!entry.getValue().isEmpty()) {
            for (OfferingEntity offering : entry.getValue()) {
                procedures.addAll(getProceduresForOffering(offering, session));
            }
        } else {
            procedures.addAll(getProceduresForOffering(entry.getKey(), session));
        }
        return procedures;
    }

    private Collection<ProcedureEntity> getProceduresForOffering(final OfferingEntity offering, Session session)
            throws OwsExceptionReport, DataAccessException {
        Map<String, String> map = Maps.newHashMap();
        map.put(IoParameters.OFFERINGS, Long.toString(offering.getId()));
        List<ProcedureEntity> procedures =
                new ProcedureDao(session).getAllInstances(new DbQuery(IoParameters.createFromSingleValueMap(map)));
        // if (procedures.isEmpty()) {
        // throw new NoApplicableCodeException().withMessage(
        // "No procedures are contained in the database for the offering '%s'!
        // Please contact the admin of this SOS.",
        // offering);
        // }
        return procedures;
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

    protected RequestOperatorRepository getRequestOperatorRepository() {
        return this.requestOperatorRepository;
    }

    protected ResponseFormatRepository getResponseFormatRepository() {
        return this.responseFormatRepository;
    }

    private void createStaticCapabilities(GetCapabilitiesRequest request, GetCapabilitiesResponse response) throws OwsExceptionReport {
        response.setXmlString(this.capabilitiesExtensionService.getActiveStaticCapabilitiesDocument());
    }

    private void createStaticCapabilitiesWithId(GetCapabilitiesRequest request, GetCapabilitiesResponse response) throws OwsExceptionReport{
        StaticCapabilities sc = this.capabilitiesExtensionService.getStaticCapabilities(request.getCapabilitiesId());
        if (sc == null) {
            throw new InvalidParameterValueException(GetCapabilitiesParams.CapabilitiesId, request.getCapabilitiesId());
        }
        response.setXmlString(sc.getDocument());
    }

    private void createDynamicCapabilities(GetCapabilitiesRequest request, GetCapabilitiesResponse response) throws OwsExceptionReport {
        Set<String> availableExtensionSections = getExtensionSections(response.getService(), response.getVersion());
        Set<String> requestedExtensionSections = new HashSet<>(availableExtensionSections.size());
        int requestedSections = identifyRequestedSections(request, response, availableExtensionSections, requestedExtensionSections);

        SosCapabilities sosCapabilities = new SosCapabilities(request.getService(), request.getVersion(), null, null, null, null, null, null, null, null);

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

    private String getGetDataAvailabilityUrl() {
        return new StringBuilder(getBaseGetUrl()).append(getRequest("GetDataAvailability")).toString();
    }

    private String getBaseGetUrl() {
        final StringBuilder url = new StringBuilder();
        // service URL
        url.append(getServiceURL());
        // ?
        if (!url.toString().endsWith("?")) {
            url.append('?');
        }
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

    private String getRequest(String requestName) {
        return new StringBuilder().append('&').append(OWSConstants.RequestParams.request.name()).append('=').append(requestName)
                .toString();
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

        public Set<String> getRequestedExtensionSections() {
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

//import java.util.ArrayList;
//import java.util.Collection;
//import java.util.Collections;
//import java.util.HashMap;
//import java.util.HashSet;
//import java.util.LinkedList;
//import java.util.List;
//import java.util.Locale;
//import java.util.Map;
//import java.util.Set;
//import java.util.TreeSet;
//import java.util.stream.Collectors;
//import java.util.stream.Stream;
//
//import javax.inject.Inject;
//import javax.xml.namespace.QName;
//
//import org.hibernate.HibernateException;
//import org.hibernate.Session;
//import org.n52.iceland.binding.BindingRepository;
//import org.n52.iceland.config.SettingsService;
//import org.n52.iceland.i18n.LocaleHelper;
//import org.n52.iceland.ogc.ows.ServiceMetadataRepository;
//import org.n52.iceland.ogc.ows.extension.OwsExtendedCapabilitiesProvider;
//import org.n52.iceland.ogc.ows.extension.OwsOperationMetadataExtensionProviderRepository;
//import org.n52.iceland.ogc.ows.extension.StaticCapabilities;
//import org.n52.iceland.ogc.sos.CapabilitiesExtensionProvider;
//import org.n52.iceland.ogc.sos.CapabilitiesExtensionRepository;
//import org.n52.iceland.ogc.swes.OfferingExtensionProvider;
//import org.n52.iceland.ogc.swes.SosObservationOfferingExtensionRepository;
//import org.n52.iceland.request.handler.OperationHandlerRepository;
//import org.n52.iceland.request.operator.RequestOperatorKey;
//import org.n52.iceland.request.operator.RequestOperatorRepository;
//import org.n52.iceland.service.operator.ServiceOperatorRepository;
//import org.n52.iceland.util.collections.MultiMaps;
//import org.n52.iceland.util.collections.SetMultiMap;
//import org.n52.io.request.IoParameters;
//import org.n52.io.request.RequestSimpleParameterSet;
//import org.n52.series.db.DataAccessException;
//import org.n52.series.db.HibernateSessionStore;
//import org.n52.series.db.beans.OfferingEntity;
//import org.n52.series.db.beans.PhenomenonEntity;
//import org.n52.series.db.beans.ProcedureEntity;
//import org.n52.series.db.dao.DbQuery;
//import org.n52.series.db.dao.OfferingDao;
//import org.n52.series.db.dao.PhenomenonDao;
//import org.n52.series.db.dao.ProcedureDao;
//import org.n52.shetland.ogc.filter.FilterCapabilities;
//import org.n52.shetland.ogc.gml.time.TimePeriod;
//import org.n52.shetland.ogc.ows.OWSConstants;
//import org.n52.shetland.ogc.ows.OwsNoValues;
//import org.n52.shetland.ogc.ows.OwsOperation;
//import org.n52.shetland.ogc.ows.OwsOperationsMetadata;
//import org.n52.shetland.ogc.ows.OwsServiceIdentification;
//import org.n52.shetland.ogc.ows.exception.CodedException;
//import org.n52.shetland.ogc.ows.exception.InvalidParameterValueException;
//import org.n52.shetland.ogc.ows.exception.OwsExceptionReport;
//import org.n52.shetland.ogc.ows.service.GetCapabilitiesRequest;
//import org.n52.shetland.ogc.ows.service.GetCapabilitiesResponse;
//import org.n52.shetland.ogc.sos.Sos2Constants;
//import org.n52.shetland.ogc.sos.SosCapabilities;
//import org.n52.shetland.ogc.sos.SosConstants;
//import org.n52.shetland.ogc.sos.SosObservationOffering;
//import org.n52.shetland.util.CollectionHelper;
//import org.n52.sos.coding.encode.ProcedureDescriptionFormatRepository;
//import org.n52.sos.coding.encode.ResponseFormatRepository;
//import org.n52.sos.config.CapabilitiesExtensionService;
//import org.n52.sos.service.profile.ProfileHandler;
//import org.n52.sos.util.GeometryHandler;
//import org.n52.sos.util.I18NHelper;
//import org.n52.svalbard.decode.DecoderRepository;
//import org.n52.svalbard.encode.EncoderRepository;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//
//import com.google.common.collect.Collections2;
//import com.google.common.collect.Lists;
//import com.google.common.collect.Maps;
//import com.vividsolutions.jts.geom.Geometry;
//
///**
// * Implementation of the interface AbstractGetCapabilitiesHandler
// *
// * @since 4.0.0
// */
//public class GetCapabilitiesHandler extends AbstractGetCapabilitiesHandler {
//
//    private static final Logger LOGGER = LoggerFactory.getLogger(GetCapabilitiesHandler.class);
//
//    /* section flags (values are powers of 2) */
//    private static final int SERVICE_IDENTIFICATION = 0x01;
//
//    private static final int SERVICE_PROVIDER = 0x02;
//
//    private static final int OPERATIONS_METADATA = 0x04;
//
//    private static final int FILTER_CAPABILITIES = 0x08;
//
//    private static final int CONTENTS = 0x10;
//
//    private static final int ALL =
//            0x20 | SERVICE_IDENTIFICATION | SERVICE_PROVIDER | OPERATIONS_METADATA | FILTER_CAPABILITIES | CONTENTS;
//
//    @Inject
//    private HibernateSessionStore sessionStore;
//
//    @Inject
//    private SettingsService settingsManager;
//
//    @Inject
//    private CapabilitiesExtensionService capabilitiesExtensionService;
//
//    @Inject
//    private EncoderRepository encoderRepository;
//
//    @Inject
//    private DecoderRepository decoderRepository;
//
//    @Inject
//    private OperationHandlerRepository operationHandlerRepository;
//
//    @Inject
//    private ProfileHandler profileHandler;
//
//    @Inject
//    private BindingRepository bindingRepository;
//
//    @Inject
//    private ServiceMetadataRepository serviceMetadataRepository;
//
//    @Inject
//    private RequestOperatorRepository requestOperatorRepository;
//
//    @Inject
//    private ServiceOperatorRepository serviceOperatorRepository;
//
//    @Inject
//    private ResponseFormatRepository responseFormatRepository;
//
//    @Inject
//    private GeometryHandler geometryHandler;
//
//    @Inject
//    private OwsOperationMetadataExtensionProviderRepository OwsOperationMetadataExtensionProviderRepository;
//
//    @Inject
//    private SosObservationOfferingExtensionRepository SosObservationOfferingExtensionRepository;
//
//    @Inject
//    private CapabilitiesExtensionRepository capabilitiesExtensionRepository;
//
//    @Inject
//    private ProcedureDescriptionFormatRepository procedureDescriptionFormatRepository;
//
//    public GetCapabilitiesHandler() {
//        super(SosConstants.SOS);
//    }
//
//    @Override
//    public GetCapabilitiesResponse getCapabilities(final GetCapabilitiesRequest request) throws OwsExceptionReport {
//        final GetCapabilitiesResponse response = new GetCapabilitiesResponse();
//        response.setService(request.getService());
//        response.setVersion(request.getVersion());
//
//        final String scId = request.getCapabilitiesId();
//        if (scId == null) {
//            if (this.capabilitiesExtensionService.isStaticCapabilitiesActive()) {
//                response.setXmlString(this.capabilitiesExtensionService.getActiveStaticCapabilitiesDocument());
//                return response;
//            }
//        } else if (!scId
//                .equals(OWSConstants.GetCapabilitiesParams.DYNAMIC_CAPABILITIES_IDENTIFIER)) {
//            final StaticCapabilities sc = this.capabilitiesExtensionService.getStaticCapabilities(scId);
//            if (sc == null) {
//                throw new InvalidParameterValueException(
//                        OWSConstants.GetCapabilitiesParams.CapabilitiesId, scId);
//            }
//            response.setXmlString(sc.getDocument());
//            return response;
//        }
//
//        final Set<String> availableExtensionSections =
//                getExtensionSections(response.getService(), response.getVersion());
//        final Set<String> requestedExtensionSections = new HashSet<>(availableExtensionSections.size());
//        final int requestedSections =
//                identifyRequestedSections(request, response, availableExtensionSections, requestedExtensionSections);
//
//        final SosCapabilities sosCapabilities = new SosCapabilities(response.getVersion());
//
//        SectionSpecificContentObject sectionSpecificContentObject = new SectionSpecificContentObject()
//                .setRequest(request).setResponse(response).setRequestedExtensionSections(requestedExtensionSections)
//                .setRequestedSections(requestedSections).setSosCapabilities(sosCapabilities);
//        addSectionSpecificContent(sectionSpecificContentObject, request);
//        response.setCapabilities(sosCapabilities);
//
//        return response;
//    }
//
//    @Override
//    protected Set<String> getExtensionSections(String service, String version) throws OwsExceptionReport {
//        return getAndMergeExtensions(service, version).stream().map(CapabilitiesExtension::getSectionName)
//                .collect(Collectors.toSet());
//    }
//
//    private void addSectionSpecificContent(final SectionSpecificContentObject sectionSpecificContentObject,
//            GetCapabilitiesRequest request) throws OwsExceptionReport {
//        String verion = sectionSpecificContentObject.getGetCapabilitiesResponse().getVersion();
//        String service = sectionSpecificContentObject.getGetCapabilitiesResponse().getService();
//        if (isServiceIdentificationSectionRequested(sectionSpecificContentObject.getRequestedSections())) {
//            sectionSpecificContentObject.getSosCapabilities()
//                    .setServiceIdentification(getServiceIdentification(request, service, verion));
//        }
//        if (isServiceProviderSectionRequested(sectionSpecificContentObject.getRequestedSections())) {
//            sectionSpecificContentObject.getSosCapabilities()
//                    .setServiceProvider(this.serviceMetadataRepository.getServiceProviderFactory(service).get());
//        }
//        if (isOperationsMetadataSectionRequested(sectionSpecificContentObject.getRequestedSections())) {
//            sectionSpecificContentObject.getSosCapabilities()
//                    .setOperationsMetadata(getOperationsMetadataForOperations(request, service, verion));
//        }
//        if (isFilterCapabilitiesSectionRequested(sectionSpecificContentObject.getRequestedSections())) {
//            sectionSpecificContentObject.getSosCapabilities().setFilterCapabilities(getFilterCapabilities());
//        }
//        if (isContentsSectionRequested(sectionSpecificContentObject.getRequestedSections())) {
//            sectionSpecificContentObject.getSosCapabilities().setContents(getContents(sectionSpecificContentObject));
//        }
//
//        if (sectionSpecificContentObject.getRequestedSections() == ALL) {
//            sectionSpecificContentObject.getSosCapabilities().setExensions(getAndMergeExtensions(service, verion));
//        } else if (!sectionSpecificContentObject.getRequestedExtensionSesctions().isEmpty()) {
//            sectionSpecificContentObject.getSosCapabilities().setExensions(
//                    getExtensions(sectionSpecificContentObject.getRequestedExtensionSesctions(), service, verion));
//        }
//    }
//
//    private int identifyRequestedSections(final GetCapabilitiesRequest request, final GetCapabilitiesResponse response,
//            final Set<String> availableExtensionSections, final Set<String> requestedExtensionSections)
//            throws OwsExceptionReport {
//        int sections = 0;
//        // handle sections array and set requested sections flag
//        if (!request.isSetSections()) {
//            sections = ALL;
//        } else {
//            for (final String section : request.getSections()) {
//                if (section.isEmpty()) {
//                    LOGGER.warn("A {} element is empty! Check if operator checks for empty elements!",
//                            OWSConstants.GetCapabilitiesParams.Section.name());
//                    continue;
//                }
//                if (section.equals(SosConstants.CapabilitiesSections.All.name())) {
//                    sections = ALL;
//                    break;
//                } else if (section.equals(SosConstants.CapabilitiesSections.ServiceIdentification.name())) {
//                    sections |= SERVICE_IDENTIFICATION;
//                } else if (section.equals(SosConstants.CapabilitiesSections.ServiceProvider.name())) {
//                    sections |= SERVICE_PROVIDER;
//                } else if (section.equals(SosConstants.CapabilitiesSections.OperationsMetadata.name())) {
//                    sections |= OPERATIONS_METADATA;
//                } else if (section.equals(Sos2Constants.CapabilitiesSections.FilterCapabilities.name())) {
//                    sections |= FILTER_CAPABILITIES;
//                } else if (section.equals(SosConstants.CapabilitiesSections.Contents.name())) {
//                    sections |= CONTENTS;
//                } else if (availableExtensionSections.contains(section)) {
//                    requestedExtensionSections.add(section);
//                } else {
//                    throw new InvalidParameterValueException()
//                            .at(OWSConstants.GetCapabilitiesParams.Section)
//                            .withMessage("The requested section '%s' does not exist or is not supported!", section);
//                }
//            }
//        }
//        return sections;
//    }
//
//    private OwsServiceIdentification getServiceIdentification(GetCapabilitiesRequest request, String service,
//            String version) throws OwsExceptionReport {
//        OwsServiceIdentification serviceIdentification = this.serviceMetadataRepository
//                .getServiceIdentificationFactory(service).get(request.getRequestedLocale());
//        if (version.equals(Sos2Constants.SERVICEVERSION)) {
//            serviceIdentification.setProfiles(getProfiles(SosConstants.SOS, Sos2Constants.SERVICEVERSION));
//        }
//        return serviceIdentification;
//    }
//
//    private Set<String> getProfiles(String service, String version) {
//
//        Set<String> profiles = Stream
//                .of(this.bindingRepository.getBindings().values(),
//                        this.requestOperatorRepository.getRequestOperators(), this.decoderRepository.getDecoders(),
//                        this.encoderRepository.getEncoders(),
//                        this.operationHandlerRepository.getOperationHandlers().values())
//                .flatMap(Collection::stream).map(c -> c.getConformanceClasses(service, version)).flatMap(Set::stream)
//                .collect(Collectors.toSet());
//
//        // FIXME additional profiles
//        if ("hydrology".equalsIgnoreCase(this.profileHandler.getActiveProfile().getIdentifier())) {
//            profiles.add("http://www.opengis.net/spec/SOS_application-profile_hydrology/1.0/req/hydrosos");
//        }
//        return profiles;
//    }
//
//    /**
//     * Get the OperationsMetadat for all supported operations
//     *
//     * @param service
//     *            Requested service
//     * @param version
//     *            Requested service version
//     * @return OperationsMetadata for all operations supported by the requested
//     *         service and version
//     * @throws OwsExceptionReport
//     *             If an error occurs
//     */
//    private OwsOperationsMetadata getOperationsMetadataForOperations(final GetCapabilitiesRequest request,
//            final String service, final String version) throws OwsExceptionReport {
//
//        final OwsOperationsMetadata operationsMetadata = new OwsOperationsMetadata();
//        operationsMetadata.addCommonValue(OWSConstants.RequestParams.service.name(),
//                new OwsParameterValuePossibleValues(SosConstants.SOS));
//        operationsMetadata.addCommonValue(OWSConstants.RequestParams.version.name(),
//                new OwsParameterValuePossibleValues(getServiceOperatorRepository().getSupportedVersions(service)));
//        // crs
//        operationsMetadata.addCommonValue(OWSConstants.AdditionalRequestParams.crs.name(),
//                new OwsParameterValuePossibleValues(this.geometryHandler.addOgcCrsPrefix(getCache().getEpsgCodes())));
//        // language
//        operationsMetadata.addCommonValue(OWSConstants.AdditionalRequestParams.language.name(),
//                new OwsParameterValuePossibleValues(
//                        Collections2.transform(getCache().getSupportedLanguages(), LocaleHelper.toStringFunction())));
//
//        // FIXME: OpsMetadata for InsertSensor, InsertObservation SOS 2.0
//        final Set<RequestOperatorKey> requestOperatorKeyTypes =
//                getRequestOperatorRepository().getActiveRequestOperatorKeys();
//        final List<OwsOperation> opsMetadata = new ArrayList<>(requestOperatorKeyTypes.size());
//        for (final RequestOperatorKey requestOperatorKey : requestOperatorKeyTypes) {
//            if (requestOperatorKey.getServiceOperatorKey().getVersion().equals(version)) {
//                OwsOperation operationMetadata = getRequestOperatorRepository().getRequestOperator(requestOperatorKey)
//                        .getOperationMetadata(service, version);
//                if (operationMetadata != null) {
//                    opsMetadata.add(operationMetadata);
//                }
//            }
//        }
//        operationsMetadata.setOperations(opsMetadata);
//
//        /*
//         * check if an OwsExtendedCapabilities provider is available for this
//         * service and check if this provider provides OwsExtendedCapabilities
//         * for the request
//         */
//        if (OwsOperationMetadataExtensionProviderRepository.hasExtendedCapabilitiesProvider(request)) {
//            OwsExtendedCapabilitiesProvider extendedCapabilitiesProvider =
//                    OwsOperationMetadataExtensionProviderRepository.getExtendedCapabilitiesProvider(request);
//            if (extendedCapabilitiesProvider != null
//                    && extendedCapabilitiesProvider.hasExtendedCapabilitiesFor(request)) {
//                operationsMetadata
//                        .setExtendedCapabilities(extendedCapabilitiesProvider.getOwsExtendedCapabilities(request));
//            }
//        }
//
//        return operationsMetadata;
//    }
//
//    /**
//     * Get the FilterCapabilities
//     *
//     * @param version
//     *            Requested service version
//     * @return FilterCapabilities
//     */
//    private FilterCapabilities getFilterCapabilities() {
//        final FilterCapabilities filterCapabilities = new FilterCapabilities();
//        getConformance(filterCapabilities);
//        getSpatialFilterCapabilities(filterCapabilities);
//        getTemporalFilterCapabilities(filterCapabilities);
//
//        return filterCapabilities;
//    }
//
//    private void getConformance(final FilterCapabilities filterCapabilities) {
//        // set Query conformance class
//        filterCapabilities.addConformance(
//                new OwsDomainType(ConformanceClassConstraintNames.ImplementsQuery.name(), new OwsNoValues(), FALSE));
//        // set Ad hoc query conformance class
//        filterCapabilities.addConformance(new OwsDomainType(
//                ConformanceClassConstraintNames.ImplementsAdHocQuery.name(), new OwsNoValues(), FALSE));
//        // set Functions conformance class
//        filterCapabilities.addConformance(new OwsDomainType(ConformanceClassConstraintNames.ImplementsFunctions.name(),
//                new OwsNoValues(), FALSE));
//        // set Resource Identification conformance class
//        filterCapabilities.addConformance(new OwsDomainType(
//                ConformanceClassConstraintNames.ImplementsResourceld.name(), new OwsNoValues(), FALSE));
//        // set Minimum Standard Filter conformance class
//        filterCapabilities.addConformance(new OwsDomainType(
//                ConformanceClassConstraintNames.ImplementsMinStandardFilter.name(), new OwsNoValues(), FALSE));
//        // set Standard Filter conformance class
//        filterCapabilities.addConformance(new OwsDomainType(
//                ConformanceClassConstraintNames.ImplementsStandardFilter.name(), new OwsNoValues(), FALSE));
//        // set Minimum Spatial Filter conformance class
//        filterCapabilities.addConformance(new OwsDomainType(
//                ConformanceClassConstraintNames.ImplementsMinSpatialFilter.name(), new OwsNoValues(), TRUE));
//        // set Spatial Filter conformance class
//        filterCapabilities.addConformance(new OwsDomainType(
//                ConformanceClassConstraintNames.ImplementsSpatialFilter.name(), new OwsNoValues(), TRUE));
//        // set Minimum Temporal Filter conformance class
//        filterCapabilities.addConformance(new OwsDomainType(
//                ConformanceClassConstraintNames.ImplementsMinTemporalFilter.name(), new OwsNoValues(), TRUE));
//        // set Temporal Filter conformance class
//        filterCapabilities.addConformance(new OwsDomainType(
//                ConformanceClassConstraintNames.ImplementsTemporalFilter.name(), new OwsNoValues(), TRUE));
//        // set Version navigation conformance class
//        filterCapabilities.addConformance(new OwsDomainType(
//                ConformanceClassConstraintNames.ImplementsVersionNav.name(), new OwsNoValues(), FALSE));
//        // set Sorting conformance class
//        filterCapabilities.addConformance(
//                new OwsDomainType(ConformanceClassConstraintNames.ImplementsSorting.name(), new OwsNoValues(), FALSE));
//        // set Extended Operators conformance class
//        filterCapabilities.addConformance(new OwsDomainType(
//                ConformanceClassConstraintNames.ImplementsExtendedOperators.name(), new OwsNoValues(), FALSE));
//        // set Minimum XPath conformance class
//        filterCapabilities.addConformance(new OwsDomainType(
//                ConformanceClassConstraintNames.ImplementsMinimumXPath.name(), new OwsNoValues(), FALSE));
//        // set Schema Element Function conformance class
//        filterCapabilities.addConformance(new OwsDomainType(
//                ConformanceClassConstraintNames.ImplementsSchemaElementFunc.name(), new OwsNoValues(), FALSE));
//    }
//
//    private SosEnvelope processObservedArea(Geometry geometry) throws CodedException {
//        // TODO Check transformation
//        // if (requestedSrid >= 0 && sosEnvelope.getSrid() != requestedSrid) {
//        // SosEnvelope tranformedEnvelope = new SosEnvelope();
//        // tranformedEnvelope.setSrid(requestedSrid);
//        // tranformedEnvelope.setEnvelope(GeometryHandler.getInstance().transformEnvelope(sosEnvelope.getEnvelope(),
//        // sosEnvelope.getSrid(), requestedSrid));
//        // return tranformedEnvelope;
//        // }
//        return new SosEnvelope(geometry.getEnvelopeInternal(), geometry.getSRID());
//    }
//
//    /**
//     * Get the contents for SOS 2.0 capabilities
//     *
//     * @param version
//     *            Requested service version
//     * @return Offerings for contents
//     *
//     *
//     * @throws OwsExceptionReport
//     *             * If an error occurs
//     */
//    // FIXME why version parameter? The method signature cleary states which
//    // version is supported by this!
//    private List<SosObservationOffering> getContents(SectionSpecificContentObject sectionSpecificContentObject)
//            throws OwsExceptionReport {
//        Session session = null;
//        try {
//            session = sessionStore.getSession();
//        String version = sectionSpecificContentObject.getGetCapabilitiesResponse().getVersion();
//        final Collection<OfferingEntity> offerings = new OfferingDao(session).getAllInstances(new DbQuery(IoParameters.createDefaults()));
//        final List<SosObservationOffering> sosOfferings = new ArrayList<>(offerings.size());
//        final Map<String, List<OfferingExtension>> extensions =
//                this.capabilitiesExtensionService.getActiveOfferingExtensions();
//
//        if (CollectionHelper.isEmpty(offerings)) {
//            // Set empty offering to add empty Contents section to Capabilities
//            sosOfferings.add(new SosObservationOffering());
//        } else {
//            for (final OfferingEntity offering : offerings) {
//                final Collection<ProcedureEntity> procedures = getProceduresForOffering(offering, session);
//                if (!procedures.isEmpty()) {
//                    final Collection<String> observationTypes = offering.getObservationTypes();
//                    if (observationTypes != null && !observationTypes.isEmpty()) {
//                        // FIXME why a loop? We are in SOS 2.0 context -> offering 1
//                        // <-> 1 procedure!
//                        for (final ProcedureEntity procedure : procedures) {
//
//                            final SosObservationOffering sosObservationOffering = new SosObservationOffering();
//
//                            // insert observationTypes
//                            sosObservationOffering.setObservationTypes(observationTypes);
//
//                            if (offering.hasEnvelope()) {
//                                sosObservationOffering.setObservedArea(processObservedArea(offering.getEnvelope()));
//                            }
//
//                            sosObservationOffering.setProcedures(Collections.singletonList(procedure.getIdentifier()));
//
//                            // TODO: add intended application
//
//                            // add offering to observation offering
//                            addSosOfferingToObservationOffering(offering, sosObservationOffering,
//                                    sectionSpecificContentObject.getGetCapabilitiesRequest());
//                            // add offering extension
//                            if (SosObservationOfferingExtensionRepository.hasOfferingExtensionProviderFor(
//                                    sectionSpecificContentObject.getGetCapabilitiesRequest())) {
//                                for (OfferingExtensionProvider provider : SosObservationOfferingExtensionRepository
//                                        .getOfferingExtensionProvider(
//                                                sectionSpecificContentObject.getGetCapabilitiesRequest())) {
//                                    if (provider != null && provider.hasExtendedOfferingFor(offering.getIdentifier())) {
//                                        sosObservationOffering.addExtensions(provider.getOfferingExtensions(offering.getIdentifier()));
//                                    }
//                                }
//                            }
//                            if (extensions.containsKey(sosObservationOffering.getOffering().getIdentifier())) {
//                                for (OfferingExtension offeringExtension : extensions
//                                        .get(sosObservationOffering.getOffering().getIdentifier())) {
//                                    sosObservationOffering.addExtension(
//                                            new SwesExtension<OfferingExtension>().setValue(offeringExtension));
//                                }
//                            }
//
//                            setUpPhenomenaForOffering(offering, procedure, sosObservationOffering, session);
//                            setUpTimeForOffering(offering, sosObservationOffering);
//                            setUpRelatedFeaturesForOffering(offering, sosObservationOffering);
//                            setUpFeatureOfInterestTypesForOffering(offering, sosObservationOffering);
//                            setUpProcedureDescriptionFormatForOffering(sosObservationOffering);
//                            setUpResponseFormatForOffering(sosObservationOffering);
//
//                            sosOfferings.add(sosObservationOffering);
//                        }
//                    }
//                } else {
//                    LOGGER.error( "No procedures are contained in the database for the offering {}! Please contact the admin of this SOS.", offering.getIdentifier());
//                }
//            }
//        }
//
//        return sosOfferings;
//        } catch (final HibernateException | DataAccessException e) {
//            throw new NoApplicableCodeException().causedBy(e).withMessage(
//                    "Error while querying data for GetCapabilities document!");
//        } finally {
//            sessionStore.returnSession(session);
//        }
//    }
//
//    private void addSosOfferingToObservationOffering(OfferingEntity offering, SosObservationOffering sosObservationOffering,
//            GetCapabilitiesRequest request) throws CodedException {
//        SosOffering sosOffering = new SosOffering(offering.getIdentifier(), false);
//        sosObservationOffering.setOffering(sosOffering);
//        // add offering name
//        I18NHelper.addOfferingNames(getCache(), sosOffering, request.getRequestedLocale(), Locale.ROOT, false);
//        // add offering description
//        I18NHelper.addOfferingDescription(sosOffering, request.getRequestedLocale(), Locale.ROOT, getCache());
//    }
//
//    /**
//     * Set SpatialFilterCapabilities to FilterCapabilities
//     *
//     * @param filterCapabilities
//     *            FilterCapabilities
//     * @param version
//     *            SOS version
//     */
//    private void getSpatialFilterCapabilities(FilterCapabilities filterCapabilities) {
//
//        // set GeometryOperands
//        final List<QName> operands = new LinkedList<>();
//        operands.add(GmlConstants.QN_ENVELOPE_32);
//
//        filterCapabilities.setSpatialOperands(operands);
//
//        // set SpatialOperators
//        final SetMultiMap<SpatialOperator, QName> ops = MultiMaps.newSetMultiMap(SpatialOperator.class);
//        ops.add(SpatialOperator.BBOX, GmlConstants.QN_ENVELOPE_32);
//
//        filterCapabilities.setSpatialOperators(ops);
//    }
//
//    /**
//     * Set TemporalFilterCapabilities to FilterCapabilities
//     *
//     * @param filterCapabilities
//     *            FilterCapabilities
//     * @param version
//     *            SOS version
//     */
//    private void getTemporalFilterCapabilities(FilterCapabilities filterCapabilities) {
//
//        // set TemporalOperands
//        final List<QName> operands = new ArrayList<>(2);
//        operands.add(GmlConstants.QN_TIME_PERIOD_32);
//        operands.add(GmlConstants.QN_TIME_INSTANT_32);
//
//        filterCapabilities.setTemporalOperands(operands);
//
//        // set TemporalOperators
//        final SetMultiMap<TimeOperator, QName> ops = MultiMaps.newSetMultiMap(TimeOperator.class);
//        for (final TimeOperator op : TimeOperator.values()) {
//            ops.add(op, GmlConstants.QN_TIME_INSTANT_32);
//            ops.add(op, GmlConstants.QN_TIME_PERIOD_32);
//        }
//        filterCapabilities.setTemporalOperators(ops);
//    }
//
//    /**
//     * Get extensions and merge MergableExtension of the same class.
//     *
//     * @return Extensions
//     *
//     *
//     * @throws OwsExceptionReport
//     */
//    @SuppressWarnings({ "rawtypes", "unchecked" })
//    private List<CapabilitiesExtension> getAndMergeExtensions(final String service, final String version)
//            throws OwsExceptionReport {
//        final List<CapabilitiesExtensionProvider> capabilitiesExtensionProviders =
//                capabilitiesExtensionRepository.getCapabilitiesExtensionProvider(service, version);
//        final List<CapabilitiesExtension> extensions = Lists.newLinkedList();
//        if (CollectionHelper.isNotEmpty(capabilitiesExtensionProviders)) {
//            final HashMap<String, MergableExtension> map = new HashMap<>(capabilitiesExtensionProviders.size());
//            for (final CapabilitiesExtensionProvider capabilitiesExtensionDAO : capabilitiesExtensionProviders) {
//                if (capabilitiesExtensionDAO.getExtension() != null) {
//                    if (capabilitiesExtensionDAO.getExtension() instanceof MergableExtension) {
//                        final MergableExtension me = (MergableExtension) capabilitiesExtensionDAO.getExtension();
//                        final MergableExtension previous = map.get(me.getSectionName());
//                        if (previous == null) {
//                            map.put(me.getSectionName(), me);
//                        } else {
//                            previous.merge(me);
//                        }
//                    } else {
//                        extensions.add(capabilitiesExtensionDAO.getExtension());
//                    }
//                }
//            }
//            extensions.addAll(map.values());
//        }
//        Map<String, StringBasedCapabilitiesExtension> activeCapabilitiesExtensions =
//                this.capabilitiesExtensionService.getActiveCapabilitiesExtensions();
//        if (activeCapabilitiesExtensions != null && !activeCapabilitiesExtensions.isEmpty()) {
//            extensions.addAll(activeCapabilitiesExtensions.values());
//        }
//        return extensions;
//    }
//
//    private Collection<CapabilitiesExtension> getExtensions(final Set<String> requestedExtensionSections,
//            final String service, final String version) throws OwsExceptionReport {
//        return getAndMergeExtensions(service, version).stream()
//                .filter(e -> requestedExtensionSections.contains(e.getSectionName())).collect(Collectors.toList());
//    }
//
//    protected void setUpPhenomenaForOffering(OfferingEntity offering, ProcedureEntity procedure,
//            SosObservationOffering sosOffering, Session session) throws DataAccessException {
//        RequestSimpleParameterSet rsps = new RequestSimpleParameterSet();
//        rsps.addParameter(IoParameters.OFFERINGS, IoParameters.getJsonNodeFrom(offering.getPkid()));
//        rsps.addParameter(IoParameters.PROCEDURES, IoParameters.getJsonNodeFrom(procedure.getPkid()));
//        List<PhenomenonEntity> observableProperties = new PhenomenonDao(session).getAllInstances(new DbQuery(rsps.toParameters()));
//
//        Collection<String> phenomenons = new LinkedList<>();
//        Map<String, Collection<String>> phens4CompPhens = new HashMap<>();
//        for (PhenomenonEntity observableProperty : observableProperties) {
//            if (!observableProperty.hasChilds() && !observableProperty.hasParents()) {
//                phenomenons.add(observableProperty.getIdentifier());
//            } else if (observableProperty.hasChilds() && !observableProperty.hasParents()) {
//                Set<String> childs = new TreeSet<String>();
//                for (PhenomenonEntity child : observableProperty.getChilds()) {
//                    childs.add(child.getIdentifier());
//                }
//            }
//        }
//        sosOffering.setObservableProperties(phenomenons);
//        sosOffering.setPhens4CompPhens(phens4CompPhens);
////        sosOffering.setCompositePhenomena(getCache().getCompositePhenomenonsForOffering(offering));
////
////        final Collection<String> compositePhenomenonsForOffering =
////                getCache().getCompositePhenomenonsForOffering(offering);
////
////        if (compositePhenomenonsForOffering != null) {
////            final Map<String, Collection<String>> phens4CompPhens =
////                    new HashMap<>(compositePhenomenonsForOffering.size());
////            for (final String compositePhenomenon : compositePhenomenonsForOffering) {
////                final Collection<String> phenomenonsForComposite =
////                        getCache().getObservablePropertiesForCompositePhenomenon(compositePhenomenon);
////                phens4CompPhens.put(compositePhenomenon, phenomenonsForComposite);
////            }
////            sosOffering.setPhens4CompPhens(phens4CompPhens);
////        } else {
////            sosOffering.setPhens4CompPhens(Collections.<String, Collection<String>> emptyMap());
////        }
//
//    }
//
////    private boolean isHiddenChildProcedureObservableProperty(final String offering,
////            final Set<String> proceduresForObservableProperty) {
////        for (final String hiddenProcedure : getCache().getHiddenChildProceduresForOffering(offering)) {
////            if (proceduresForObservableProperty.contains(hiddenProcedure)) {
////                return true;
////            }
////        }
////        return false;
////    }
//
//    private void setUpRelatedFeaturesForOffering(OfferingEntity offering,
//            SosObservationOffering sosObservationOffering) throws OwsExceptionReport {
//        final Map<String, Set<String>> relatedFeatures = Maps.newHashMap();
//        final Set<String> relatedFeaturesForThisOffering =
//                getCache().getRelatedFeaturesForOffering(offering.getIdentifier());
//        if (CollectionHelper.isNotEmpty(relatedFeaturesForThisOffering)) {
//            for (final String relatedFeature : relatedFeaturesForThisOffering) {
//                relatedFeatures.put(relatedFeature, getCache().getRolesForRelatedFeature(relatedFeature));
//            }
//            /*
//             * TODO add setting to set FeatureOfInterest if relatedFeatures are
//             * empty. } else { final Set<String> role =
//             * Collections.singleton("featureOfInterestID"); final Set<String>
//             * featuresForOffering =
//             * getCache().getFeaturesOfInterestForOffering(offering); if
//             * (featuresForOffering != null) { for (final String foiID :
//             * featuresForOffering) { if
//             * (getCache().getProceduresForFeatureOfInterest
//             * (foiID).contains(procedure)) { relatedFeatures.put(foiID, role);
//             * } } }
//             */
//
//        }
//        sosObservationOffering.setRelatedFeatures(relatedFeatures);
//    }
//
//    protected void setUpTimeForOffering(OfferingEntity offering, SosObservationOffering sosOffering) {
//        sosOffering.setPhenomenonTime(new TimePeriod(offering.getPhenomenonTimeStart(), offering.getPhenomenonTimeEnd()));
//        sosOffering.setResultTime(new TimePeriod(offering.getResultTimeStart(), offering.getResultTimeEnd()));
//    }
//
//    protected void setUpFeatureOfInterestTypesForOffering(OfferingEntity offering, SosObservationOffering sosOffering) {
//        sosOffering.setFeatureOfInterestTypes(offering.getFeatureTypes());
//    }
//
//    protected void setUpResponseFormatForOffering(SosObservationOffering sosOffering) {
//        // initialize as new HashSet so that collection is modifiable
//        final Collection<String> responseFormats =
//                new HashSet<>(getResponseFormatRepository().getSupportedResponseFormats(SosConstants.SOS, Sos2Constants.SERVICEVERSION));
//        sosOffering.setResponseFormats(responseFormats);
//        // TODO set as property
//    }
//
//    protected void setUpProcedureDescriptionFormatForOffering(SosObservationOffering sosOffering) {
//        // TODO: set procDescFormat <-- what is required here?
//        sosOffering.setProcedureDescriptionFormat(procedureDescriptionFormatRepository
//                .getSupportedProcedureDescriptionFormats(SosConstants.SOS, Sos2Constants.SERVICEVERSION));
//    }
//
//    private Collection<ProcedureEntity> getProceduresForOffering(final OfferingEntity offering, Session session)
//            throws OwsExceptionReport, DataAccessException {
//        RequestSimpleParameterSet rsps = new RequestSimpleParameterSet();
//        rsps.addParameter(IoParameters.OFFERINGS, IoParameters.getJsonNodeFrom(offering.getPkid()));
//        List<ProcedureEntity> procedures =
//                new ProcedureDao(session).getAllInstances(new DbQuery(rsps.toParameters()));
//        // if (procedures.isEmpty()) {
//        // throw new NoApplicableCodeException().withMessage(
//        // "No procedures are contained in the database for the offering '%s'!
//        // Please contact the admin of this SOS.",
//        // offering);
//        // }
//        return procedures;
//    }
//
//    private boolean isContentsSectionRequested(final int sections) {
//        return (sections & CONTENTS) != 0;
//    }
//
//    private boolean isFilterCapabilitiesSectionRequested(final int sections) {
//        return (sections & FILTER_CAPABILITIES) != 0;
//    }
//
//    private boolean isOperationsMetadataSectionRequested(final int sections) {
//        return (sections & OPERATIONS_METADATA) != 0;
//    }
//
//    private boolean isServiceProviderSectionRequested(final int sections) {
//        return (sections & SERVICE_PROVIDER) != 0;
//    }
//
//    private boolean isServiceIdentificationSectionRequested(final int sections) {
//        return (sections & SERVICE_IDENTIFICATION) != 0;
//    }
//
//    protected RequestOperatorRepository getRequestOperatorRepository() {
//        return this.requestOperatorRepository;
//    }
//
//    protected ServiceOperatorRepository getServiceOperatorRepository() {
//        return this.serviceOperatorRepository;
//    }
//
//    protected ResponseFormatRepository getResponseFormatRepository() {
//        return this.responseFormatRepository;
//    }
//
//    private class SectionSpecificContentObject {
//        private GetCapabilitiesRequest request;
//
//        private GetCapabilitiesResponse response;
//
//        private Set<String> requestedExtensionSections;
//
//        private int requestedSections;
//
//        private SosCapabilities sosCapabilities;
//
//        public SectionSpecificContentObject setRequest(GetCapabilitiesRequest request) {
//            this.request = request;
//            return this;
//        }
//
//        public GetCapabilitiesRequest getGetCapabilitiesRequest() {
//            return request;
//        }
//
//        public SectionSpecificContentObject setResponse(GetCapabilitiesResponse response) {
//            this.response = response;
//            return this;
//        }
//
//        public GetCapabilitiesResponse getGetCapabilitiesResponse() {
//            return response;
//        }
//
//        public SectionSpecificContentObject setRequestedExtensionSections(Set<String> requestedExtensionSections) {
//            this.requestedExtensionSections = requestedExtensionSections;
//            return this;
//        }
//
//        public Set<String> getRequestedExtensionSesctions() {
//            return requestedExtensionSections;
//        }
//
//        public SectionSpecificContentObject setRequestedSections(int requestedSections) {
//            this.requestedSections = requestedSections;
//            return this;
//        }
//
//        public int getRequestedSections() {
//            return requestedSections;
//        }
//
//        public SectionSpecificContentObject setSosCapabilities(SosCapabilities sosCapabilities) {
//            this.sosCapabilities = sosCapabilities;
//            return this;
//        }
//
//        public SosCapabilities getSosCapabilities() {
//            return sosCapabilities;
//        }
//    }
//}
