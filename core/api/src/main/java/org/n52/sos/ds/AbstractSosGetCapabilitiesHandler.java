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
package org.n52.sos.ds;

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
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.inject.Inject;
import javax.xml.namespace.QName;

import org.n52.iceland.binding.Binding;
import org.n52.iceland.binding.MediaTypeBindingKey;
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
import org.n52.janmayen.http.MediaTypes;
import org.n52.janmayen.i18n.LocaleHelper;
import org.n52.shetland.ogc.OGCConstants;
import org.n52.shetland.ogc.filter.FilterCapabilities;
import org.n52.shetland.ogc.filter.FilterConstants.ComparisonOperator;
import org.n52.shetland.ogc.filter.FilterConstants.ConformanceClassConstraintNames;
import org.n52.shetland.ogc.filter.FilterConstants.SpatialOperator;
import org.n52.shetland.ogc.filter.FilterConstants.TimeOperator;
import org.n52.shetland.ogc.gml.GmlConstants;
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
import org.n52.shetland.ogc.ows.exception.CompositeOwsException;
import org.n52.shetland.ogc.ows.exception.InvalidParameterValueException;
import org.n52.shetland.ogc.ows.exception.OwsExceptionReport;
import org.n52.shetland.ogc.ows.exception.VersionNegotiationFailedException;
import org.n52.shetland.ogc.ows.extension.MergableExtension;
import org.n52.shetland.ogc.ows.extension.StringBasedCapabilitiesExtension;
import org.n52.shetland.ogc.ows.service.GetCapabilitiesRequest;
import org.n52.shetland.ogc.ows.service.GetCapabilitiesResponse;
import org.n52.shetland.ogc.sos.Sos1Constants;
import org.n52.shetland.ogc.sos.Sos2Constants;
import org.n52.shetland.ogc.sos.SosCapabilities;
import org.n52.shetland.ogc.sos.SosConstants;
import org.n52.shetland.ogc.sos.SosObservationOffering;
import org.n52.shetland.util.CollectionHelper;
import org.n52.shetland.util.DateTimeHelper;
import org.n52.shetland.util.ReferencedEnvelope;
import org.n52.sos.coding.encode.ProcedureDescriptionFormatRepository;
import org.n52.sos.coding.encode.ResponseFormatRepository;
import org.n52.sos.config.CapabilitiesExtensionService;
import org.n52.sos.ogc.sos.SosObservationOfferingExtensionRepository;
import org.n52.sos.request.operator.AbstractTransactionalRequestOperator;
import org.n52.sos.request.operator.TransactionalRequestChecker;
import org.n52.sos.service.TransactionalSecurityConfiguration;
import org.n52.sos.util.GeometryHandler;
import org.n52.svalbard.ConformanceClass;
import org.n52.svalbard.ConformanceClasses;
import org.n52.svalbard.decode.DecoderRepository;
import org.n52.svalbard.encode.EncoderRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Sets;

/**
 * Implementation of the interface AbstractGetCapabilitiesHandler
 *
 * @since 4.0.0
 */
public abstract class AbstractSosGetCapabilitiesHandler extends AbstractGetCapabilitiesHandler {

    private static final Logger LOGGER = LoggerFactory.getLogger(AbstractSosGetCapabilitiesHandler.class);

    /* section flags (values are powers of 2) */
    private static final int SERVICE_IDENTIFICATION = 0x01;

    private static final int SERVICE_PROVIDER = 0x02;

    private static final int OPERATIONS_METADATA = 0x04;

    private static final int FILTER_CAPABILITIES = 0x08;

    private static final int CONTENTS = 0x10;

    private static final int ALL =
            0x20 | SERVICE_IDENTIFICATION | SERVICE_PROVIDER | OPERATIONS_METADATA | FILTER_CAPABILITIES | CONTENTS;

    @Inject
    private CapabilitiesExtensionService capabilitiesExtensionService;

    @Inject
    private EncoderRepository encoderRepository;

    @Inject
    private DecoderRepository decoderRepository;

    @Inject
    private OperationHandlerRepository operationHandlerRepository;

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

    @Inject
    private Optional<TransactionalSecurityConfiguration> transactionalSecurityConfiguration;

    public AbstractSosGetCapabilitiesHandler() {
        this(SosConstants.SOS);
    }

    public AbstractSosGetCapabilitiesHandler(String service) {
        super(service);
    }

    @Override
    public GetCapabilitiesResponse getCapabilities(GetCapabilitiesRequest request) throws OwsExceptionReport {
        String capabilitiesId = request.getCapabilitiesId();
        String service = request.getService();
        String version = negotiateVersion(request);
        boolean showTransactionalOperations = checkForTransactionalOperations(request);

        GetCapabilitiesResponse response = new GetCapabilitiesResponse(service, version);

        if (capabilitiesId == null && this.capabilitiesExtensionService.isStaticCapabilitiesActive()) {
            createStaticCapabilities(request, response);
        } else if (capabilitiesId != null
                && !capabilitiesId.equals(GetCapabilitiesParams.DYNAMIC_CAPABILITIES_IDENTIFIER)) {
            createStaticCapabilitiesWithId(request, response);
        } else {
            createDynamicCapabilities(request, response, showTransactionalOperations);
        }
        if (getCache().getLastUpdateTime() != null && response.getCapabilities() != null && !response.getCapabilities()
                .getUpdateSequence()
                .isPresent()) {
            response.getCapabilities()
                    .setUpdateSequence(DateTimeHelper.formatDateTime2IsoString(getCache().getLastUpdateTime()));
        }
        return response;
    }

    private String negotiateVersion(GetCapabilitiesRequest request) throws OwsExceptionReport {
        if (request.isSetVersion()) {
            return request.getVersion();
        } else {
            String service = request.getService();
            String version;

            if (request.isSetAcceptVersions()) {
                version = request.getAcceptVersions()
                        .stream()
                        .filter(v -> getServiceOperatorRepository().isVersionSupported(service, v))
                        .findFirst()
                        .orElseThrow(this::versionNegotiationFailed);
            } else {
                version = getServiceOperatorRepository().getSupportedVersions(service)
                        .stream()
                        .max(Comparables.version())
                        .orElseThrow(() -> new InvalidServiceParameterException(service));
            }
            request.setVersion(version);
            return version;
        }
    }

    private OwsExceptionReport versionNegotiationFailed() {
        return new VersionNegotiationFailedException().withMessage(
                "The requested '%s' values are not supported by this service!",
                OWSConstants.GetCapabilitiesParams.AcceptVersions);
    }

    private void addSectionSpecificContent(SectionSpecificContentObject sectionSpecificContentObject,
            GetCapabilitiesRequest request, boolean showTransactionalOperations) throws OwsExceptionReport {
        String version = sectionSpecificContentObject.getGetCapabilitiesResponse()
                .getVersion();
        String service = sectionSpecificContentObject.getGetCapabilitiesResponse()
                .getService();

        if (isServiceIdentificationSectionRequested(sectionSpecificContentObject.getRequestedSections())) {
            sectionSpecificContentObject.getSosCapabilities()
                    .setServiceIdentification(
                            getServiceIdentification(request, service, version, showTransactionalOperations));
        }
        if (isServiceProviderSectionRequested(sectionSpecificContentObject.getRequestedSections())) {
            sectionSpecificContentObject.getSosCapabilities()
                    .setServiceProvider(this.serviceMetadataRepository.getServiceProviderFactory(service)
                            .get());
        }
        if (isOperationsMetadataSectionRequested(sectionSpecificContentObject.getRequestedSections())) {
            sectionSpecificContentObject.getSosCapabilities()
                    .setOperationsMetadata(getOperationsMetadataForOperations(request, service, version,
                            showTransactionalOperations));
        }
        if (isFilterCapabilitiesSectionRequested(sectionSpecificContentObject.getRequestedSections())) {
            sectionSpecificContentObject.getSosCapabilities()
                    .setFilterCapabilities(getFilterCapabilities(version));
        }
        if (isContentsSectionRequested(sectionSpecificContentObject.getRequestedSections())) {
            if (isV2(sectionSpecificContentObject.getGetCapabilitiesResponse())) {
                sectionSpecificContentObject.getSosCapabilities()
                        .setContents(getContentsForSosV2(sectionSpecificContentObject));
            } else {
                sectionSpecificContentObject.getSosCapabilities()
                        .setContents(getContentsForSosV1(sectionSpecificContentObject));
            }
        }

        if (isV2(sectionSpecificContentObject.getGetCapabilitiesResponse())) {
            if (sectionSpecificContentObject.getRequestedSections() == ALL) {
                sectionSpecificContentObject.getSosCapabilities()
                        .setExtensions(getAndMergeExtensions(service, version));
            } else if (!sectionSpecificContentObject.getRequestedExtensionSesctions()
                    .isEmpty()) {
                sectionSpecificContentObject.getSosCapabilities()
                        .setExtensions(getExtensions(sectionSpecificContentObject.getRequestedExtensionSesctions(),
                                service, version));
            }
        }
    }

    protected abstract List<SosObservationOffering> getContentsForSosV1(
            SectionSpecificContentObject sectionSpecificContentObject) throws OwsExceptionReport;

    protected abstract List<SosObservationOffering> getContentsForSosV2(
            SectionSpecificContentObject sectionSpecificContentObject) throws OwsExceptionReport;

    private int identifyRequestedSections(GetCapabilitiesRequest request, GetCapabilitiesResponse response,
            Set<String> availableExtensionSections, Set<String> requestedExtensionSections) throws OwsExceptionReport {

        if (!request.isSetSections()) {
            // FIXME:
            // requestedExtensionSections.addAll(availableExtensionSections)?
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
            } else if (section.equals(Sos1Constants.CapabilitiesSections.Filter_Capabilities.name())
                    && isV1(response)) {
                sections |= FILTER_CAPABILITIES;
            } else if (section.equals(Sos2Constants.CapabilitiesSections.FilterCapabilities.name())
                    && isV2(response)) {
                sections |= FILTER_CAPABILITIES;
            } else if (section.equals(SosConstants.CapabilitiesSections.Contents.name())) {
                sections |= CONTENTS;
            } else if (availableExtensionSections.contains(section) && isV2(response)) {
                requestedExtensionSections.add(section);
            } else {
                throw new InvalidParameterValueException().at(GetCapabilitiesParams.Section)
                        .withMessage("The requested section '%s' does not exist or is not supported!", section);
            }
        }
        return sections;
    }

    private OwsServiceIdentification getServiceIdentification(GetCapabilitiesRequest request, String service,
            String version, boolean showTransactionalOperations) throws OwsExceptionReport {
        Locale locale = getRequestedLocale(request);
        LocalizedProducer<OwsServiceIdentification> serviceIdentificationFactory =
                this.serviceMetadataRepository.getServiceIdentificationFactory(service);
        OwsServiceIdentification serviceIdentification = serviceIdentificationFactory.get(locale);
        if (version.equals(Sos2Constants.SERVICEVERSION)) {
            serviceIdentification.setProfiles(
                    getProfiles(SosConstants.SOS, Sos2Constants.SERVICEVERSION, showTransactionalOperations));
        }
        return serviceIdentification;
    }

    private Set<URI> getProfiles(String service, String version, boolean showTransactionalOperations) {
        Set<URI> profiles = Stream.of(getBindingRepository().getBindings()
                .values(), getRequestOperatorRepository().getRequestOperators(), getDecoderRepository().getDecoders(),
                getEncoderRepository().getEncoders(), getOperationHandlerRepository().getOperationHandlers()
                        .values())
                .flatMap(Collection::stream)
                .filter(o -> !(o instanceof AbstractTransactionalRequestOperator)
                        || (o instanceof AbstractTransactionalRequestOperator && showTransactionalOperations))
                .filter(c -> c instanceof ConformanceClass)
                .map(c -> (ConformanceClass) c)
                .map(c -> c.getConformanceClasses(service, version))
                .flatMap(Set::stream)
                .map(URI::create)
                .collect(Collectors.toSet());
        if (Sos2Constants.SERVICEVERSION.equals(version)) {
            checkBindingConformanceClasses(getBindingRepository().getBindings()
                    .values(), profiles);
        }

        // FIXME additional profiles
        if ("hydrology".equalsIgnoreCase(getProfileHandler().getActiveProfile().getIdentifier())) {
            profiles.add(URI.create("http://www.opengis.net/spec/SOS_application-profile_hydrology/1.0/req/hydrosos"));
        }
        return profiles;
    }

    private void checkBindingConformanceClasses(Collection<Binding> values, Set<URI> profiles) {
        Set<URI> collect = Stream.of(getBindingRepository().getBindings()
                .values())
                .flatMap(Collection::stream)
                .map(b -> b.getKeys())
                .flatMap(Set::stream)
                .filter(k -> k instanceof MediaTypeBindingKey)
                .map(k -> (MediaTypeBindingKey) k)
                .map(k -> {
                    if (k.getMediaType()
                            .equals(MediaTypes.APPLICATION_KVP)) {
                        return ConformanceClasses.SOS_V2_KVP_CORE_BINDING;
                    } else if (k.getMediaType()
                            .equals(MediaTypes.APPLICATION_JSON)) {
                        return "http://www.opengis.net/spec/SOS/2.0/conf/json";
                    } else if (k.getMediaType()
                            .equals(MediaTypes.APPLICATION_SOAP_XML)) {
                        return ConformanceClasses.SOS_V2_SOAP_BINDING;
                    } else if (k.getMediaType()
                            .equals(MediaTypes.APPLICATION_XML)) {
                        return ConformanceClasses.SOS_V2_POX_BINDING;
                    } else if (k.getMediaType()
                            .equals(MediaTypes.APPLICATION_EXI)) {
                        return "http://www.opengis.net/spec/SOS/2.0/conf/exi";
                    }
                    return null;
                })
                .filter(Objects::nonNull)
                .map(URI::create)
                .collect(Collectors.toSet());
        profiles.addAll(collect);

    }

    /**
     * Get the OperationsMetadat for all supported operations
     *
     * @param request
     *            the request
     * @param service
     *            Requested service
     * @param version
     *            Requested service version
     *
     * @return OperationsMetadata for all operations supported by the requested
     *         service and version
     *
     * @throws OwsExceptionReport
     *             If an error occurs
     */
    private OwsOperationsMetadata getOperationsMetadataForOperations(GetCapabilitiesRequest request, String service,
            String version, boolean showTransactionalOperations) throws OwsExceptionReport {

        List<OwsDomain> parameters = getParameters(service, version);
        List<OwsOperation> operations = getOperations(service, version, showTransactionalOperations);
        List<OwsDomain> constraints = Collections.emptyList();
        OwsOperationMetadataExtension owsExtendedCapabilities = null;
        /*
         * check if an OwsExtendedCapabilities provider is available for this
         * service and check if this provider provides OwsExtendedCapabilities
         * for the request
         */
        OwsOperationMetadataExtensionProvider provider =
                getOwsExtendedCapabilitiesProviderRepository().getExtendedCapabilitiesProvider(service, version);
        if (provider != null && provider.hasExtendedCapabilitiesFor(request)) {
            owsExtendedCapabilities = provider.getOwsExtendedCapabilities(request);
        }
        return new OwsOperationsMetadata(operations, parameters, constraints, owsExtendedCapabilities);
    }

    private boolean checkForTransactionalOperations(GetCapabilitiesRequest request) {
        return isSetTransactionalSecurityConfiguration()
                ? new TransactionalRequestChecker(getTransactionalSecurityConfiguration())
                        .checkBoolean(request.getRequestContext())
                : true;
    }

    /**
     * Get the FilterCapabilities
     *
     * @param version
     *            Requested service version
     *
     * @return FilterCapabilities
     */
    private FilterCapabilities getFilterCapabilities(final String version) {
        final FilterCapabilities filterCapabilities = new FilterCapabilities();
        if (Sos2Constants.SERVICEVERSION.equals(version)) {
            getConformance(filterCapabilities);
        }
        if (version.equals(Sos1Constants.SERVICEVERSION)) {
            getScalarFilterCapabilities(filterCapabilities, version);
        }
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
        filterCapabilities.addConformance(
                new OwsDomain(ConformanceClassConstraintNames.ImplementsAdHocQuery, noValues, falseValue));
        filterCapabilities.addConformance(
                new OwsDomain(ConformanceClassConstraintNames.ImplementsFunctions, noValues, falseValue));
        filterCapabilities.addConformance(
                new OwsDomain(ConformanceClassConstraintNames.ImplementsResourceld, noValues, falseValue));
        filterCapabilities.addConformance(
                new OwsDomain(ConformanceClassConstraintNames.ImplementsMinStandardFilter, noValues, falseValue));
        filterCapabilities.addConformance(
                new OwsDomain(ConformanceClassConstraintNames.ImplementsStandardFilter, noValues, falseValue));
        filterCapabilities.addConformance(
                new OwsDomain(ConformanceClassConstraintNames.ImplementsMinSpatialFilter, noValues, trueValue));
        filterCapabilities.addConformance(
                new OwsDomain(ConformanceClassConstraintNames.ImplementsSpatialFilter, noValues, trueValue));
        filterCapabilities.addConformance(
                new OwsDomain(ConformanceClassConstraintNames.ImplementsMinTemporalFilter, noValues, trueValue));
        filterCapabilities.addConformance(
                new OwsDomain(ConformanceClassConstraintNames.ImplementsTemporalFilter, noValues, trueValue));
        filterCapabilities.addConformance(
                new OwsDomain(ConformanceClassConstraintNames.ImplementsVersionNav, noValues, falseValue));
        filterCapabilities.addConformance(
                new OwsDomain(ConformanceClassConstraintNames.ImplementsSorting, noValues, falseValue));
        filterCapabilities.addConformance(
                new OwsDomain(ConformanceClassConstraintNames.ImplementsExtendedOperators, noValues, falseValue));
        filterCapabilities.addConformance(
                new OwsDomain(ConformanceClassConstraintNames.ImplementsMinimumXPath, noValues, falseValue));
        filterCapabilities.addConformance(
                new OwsDomain(ConformanceClassConstraintNames.ImplementsSchemaElementFunc, noValues, falseValue));
    }

    protected boolean checkOfferingValues(ReferencedEnvelope envelopeForOffering, Set<String> featuresForOffering,
            Collection<String> responseFormats) {
        return ReferencedEnvelope.isNotNullOrEmpty(envelopeForOffering)
                && CollectionHelper.isNotEmpty(featuresForOffering) && CollectionHelper.isNotEmpty(responseFormats);
    }

    /**
     * Set SpatialFilterCapabilities to FilterCapabilities
     *
     * @param filterCapabilities
     *            FilterCapabilities
     * @param version
     *            SOS version
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
        // final SetMultiMap<SpatialOperator, QName> ops =
        // MultiMaps.newSetMultiMap(SpatialOperator.class);
        if (version.equals(Sos2Constants.SERVICEVERSION)) {
            ops.put(SpatialOperator.BBOX, Collections.singleton(GmlConstants.QN_ENVELOPE_32));
        } else if (version.equals(Sos1Constants.SERVICEVERSION)) {
            ops.put(SpatialOperator.BBOX, Collections.singleton(GmlConstants.QN_ENVELOPE));
            // set Contains
            Set<QName> filterOperands = new HashSet<>(
                    Arrays.asList(GmlConstants.QN_POINT, GmlConstants.QN_LINESTRING, GmlConstants.QN_POLYGON));
            Stream.of(SpatialOperator.Contains, SpatialOperator.Intersects, SpatialOperator.Overlaps)
                    .forEach(op -> ops.put(op, filterOperands));
        }

        filterCapabilities.setSpatialOperators(ops);
    }

    /**
     * Set TemporalFilterCapabilities to FilterCapabilities
     *
     * @param filterCapabilities
     *            FilterCapabilities
     * @param version
     *            SOS version
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
                logNotSupportedVersion(version);
                break;
        }

        filterCapabilities.setTemporalOperands(operands);

        // set TemporalOperators
        final Map<TimeOperator, Set<QName>> ops = CollectionHelper.synchronizedMap(0);
        switch (version) {
            case Sos2Constants.SERVICEVERSION:
                for (final TimeOperator op : TimeOperator.values()) {
                    ops.computeIfAbsent(op, createSynchronizedSet())
                            .add(GmlConstants.QN_TIME_INSTANT_32);
                    ops.computeIfAbsent(op, createSynchronizedSet())
                            .add(GmlConstants.QN_TIME_PERIOD_32);
                }
                break;
            case Sos1Constants.SERVICEVERSION:
                for (final TimeOperator op : TimeOperator.values()) {
                    ops.computeIfAbsent(op, createSynchronizedSet())
                            .add(GmlConstants.QN_TIME_INSTANT);
                    ops.computeIfAbsent(op, createSynchronizedSet())
                            .add(GmlConstants.QN_TIME_PERIOD);
                }
                break;
            default:
                logNotSupportedVersion(version);
                break;
        }
        filterCapabilities.setTemporalOperators(ops);
    }

    private void logNotSupportedVersion(String version) {
        LOGGER.trace("Not supported version '{}'", version);
    }

    /**
     * Set ScalarFilterCapabilities to FilterCapabilities
     *
     * @param filterCapabilities
     *            FilterCapabilities
     * @param version
     *            the service version
     */
    private void getScalarFilterCapabilities(final FilterCapabilities filterCapabilities, String version) {
        // TODO PropertyIsNil, PropertyIsNull? better:
        // filterCapabilities.setComparisonOperators(Arrays.asList(ComparisonOperator.values()));
        final List<ComparisonOperator> comparisonOperators = new ArrayList<>(8);
        comparisonOperators.add(ComparisonOperator.PropertyIsBetween);
        comparisonOperators.add(ComparisonOperator.PropertyIsEqualTo);
        if (version.equals(Sos1Constants.SERVICEVERSION)) {
            comparisonOperators.add(ComparisonOperator.PropertyIsNotEqualTo);
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
     * @param offering
     *            Offering identifier
     *
     * @return FOI identifiers
     *
     *
     * @throws OwsExceptionReport
     *             * If an error occurs
     */
    protected Set<String> getFOI4offering(final String offering) throws OwsExceptionReport {
        final Set<String> featureIDs = new HashSet<>(0);
        final Set<String> features = getCache().getFeaturesOfInterestForOffering(offering);
        if (!getProfileHandler().getActiveProfile()
                .isListFeatureOfInterestsInOfferings() || features == null) {
            featureIDs.add(OGCConstants.UNKNOWN);
        } else {
            featureIDs.addAll(features);
        }
        return featureIDs;
    }

    protected Collection<String> getObservationTypes(String offering) {
        Set<String> observationTypes = getCache().getObservationTypesForOffering(offering)
                .stream()
                .filter(Predicate.isEqual(SosConstants.NOT_DEFINED)
                        .negate())
                .collect(Collectors.toSet());

        if (observationTypes.isEmpty()) {
            getCache().getAllowedObservationTypesForOffering(offering)
                    .stream()
                    .filter(Predicate.isEqual(SosConstants.NOT_DEFINED)
                            .negate())
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
     * @param service
     *            the service name
     * @param version
     *            the service version
     * @return Extensions
     *
     *
     * @throws OwsExceptionReport
     *             if an error occurs
     */
    @SuppressWarnings({ "rawtypes", "unchecked" })
    private List<OwsCapabilitiesExtension> getAndMergeExtensions(String service, String version)
            throws OwsExceptionReport {
        List<OwsCapabilitiesExtensionProvider> providers =
                getCapabilitiesExtensionRepository().getCapabilitiesExtensionProvider(service, version);
        List<OwsCapabilitiesExtension> extensions = new LinkedList<>();
        if (CollectionHelper.isNotEmpty(providers)) {
            Map<String, MergableExtension> map = new HashMap<>(providers.size());
            providers.stream()
                    .filter(Objects::nonNull)
                    .map(OwsCapabilitiesExtensionProvider::getExtension)
                    .forEachOrdered(extension -> {
                        if (extension instanceof MergableExtension) {
                            map.merge(extension.getSectionName(), (MergableExtension) extension,
                                    Functions.mergeLeft(MergableExtension::merge));
                        } else {
                            extensions.add(extension);
                        }
                    });
            extensions.addAll(map.values());
        }
        Map<String, StringBasedCapabilitiesExtension> activeCapabilitiesExtensions =
                this.capabilitiesExtensionService.getActiveCapabilitiesExtensions();
        if (activeCapabilitiesExtensions != null && !activeCapabilitiesExtensions.isEmpty()) {
            extensions.addAll(activeCapabilitiesExtensions.values());
        }
        return extensions;
    }

    private Collection<OwsCapabilitiesExtension> getExtensions(Set<String> requestedExtensionSections, String service,
            String version) throws OwsExceptionReport {
        return getAndMergeExtensions(service, version).stream()
                .filter(e -> requestedExtensionSections.contains(e.getSectionName()))
                .collect(Collectors.toList());
    }

    protected void setUpPhenomenaForOffering(String offering, String procedure, SosObservationOffering sosOffering) {
        setUpPhenomenaForOffering(Sets.newHashSet(offering), procedure, sosOffering);
    }

    protected void setUpPhenomenaForOffering(final Set<String> offerings, final String procedure,
            final SosObservationOffering sosOffering) {
        Collection<String> phenomenons = offerings.stream()
                .flatMap(offering -> {
                    Collection<String> observableProperties = getCache().getObservablePropertiesForOffering(offering);
                    return observableProperties.stream()
                            .filter(observableProperty -> {
                                Set<String> procedures =
                                        getCache().getProceduresForObservableProperty(observableProperty);
                                return procedures.contains(procedure)
                                        || isHiddenChildProcedureObservableProperty(offering, procedures);
                            });
                })
                .collect(Collectors.toList());

        sosOffering.setCompositePhenomena(offerings.stream()
                .map(getCache()::getCompositePhenomenonsForOffering)
                .flatMap(Set::stream)
                .collect(Collectors.toSet()));
        sosOffering.setPhens4CompPhens(sosOffering.getCompositePhenomena()
                .stream()
                .collect(Collectors.toMap(Function.identity(),
                        getCache()::getObservablePropertiesForCompositePhenomenon)));

        sosOffering.setObservableProperties(phenomenons);
    }

    private boolean isHiddenChildProcedureObservableProperty(String offering,
            Set<String> proceduresForObservableProperty) {
        return getCache().getHiddenChildProceduresForOffering(offering)
                .stream()
                .anyMatch(proceduresForObservableProperty::contains);
    }

    protected void setUpRelatedFeaturesForOffering(String offering, String version, SosObservationOffering sosOffering)
            throws OwsExceptionReport {
        setUpRelatedFeaturesForOffering(Collections.singleton(offering), version, sosOffering);
    }

    protected void setUpRelatedFeaturesForOffering(Set<String> offerings, String version,
            SosObservationOffering sosOffering) {
        setUpRelatedFeaturesForOffering(offerings.stream(), sosOffering);
    }

    protected void setUpRelatedFeaturesForOffering(Stream<String> offerings, SosObservationOffering sosOffering) {

        sosOffering.setRelatedFeatures(offerings.map(getCache()::getRelatedFeaturesForOffering)
                .flatMap(Set::stream)
                .collect(Collectors.toMap(Function.identity(), getCache()::getRolesForRelatedFeature)));
    }

    protected void setUpTimeForOffering(Stream<String> offerings, SosObservationOffering sosOffering) {
        TimePeriod phenomenonTime = new TimePeriod();
        TimePeriod resultTime = new TimePeriod();
        offerings.forEach(offering -> {
            phenomenonTime.extendToContain(getPhenomeonTime(offering));
            resultTime.extendToContain(getResultTime(offering));
        });
        sosOffering.setPhenomenonTime(phenomenonTime);
        sosOffering.setResultTime(resultTime);
    }

    protected void setUpTimeForOffering(Set<String> offerings, SosObservationOffering sosOffering) {
        setUpTimeForOffering(offerings.stream(), sosOffering);
    }

    protected void setUpTimeForOffering(final String offering, final SosObservationOffering sosOffering) {
        sosOffering.setPhenomenonTime(getPhenomeonTime(offering));
        sosOffering.setResultTime(getResultTime(offering));
    }

    private TimePeriod getPhenomeonTime(String offering) {
        return new TimePeriod(getCache().getMinPhenomenonTimeForOffering(offering),
                getCache().getMaxPhenomenonTimeForOffering(offering));
    }

    private TimePeriod getResultTime(String offering) {
        return new TimePeriod(getCache().getMinResultTimeForOffering(offering),
                getCache().getMaxResultTimeForOffering(offering));
    }

    protected void setUpFeatureOfInterestTypesForOffering(String offering, SosObservationOffering sosOffering) {
        setUpFeatureOfInterestTypesForOffering(Collections.singleton(offering), sosOffering);
    }

    protected void setUpFeatureOfInterestTypesForOffering(Set<String> offerings, SosObservationOffering sosOffering) {
        sosOffering.setFeatureOfInterestTypes(offerings.stream()
                .map(getCache()::getAllObservationTypesForOffering)
                .flatMap(Set::stream)
                .collect(Collectors.toSet()));

    }

    protected void setUpResponseFormatForOffering(SosObservationOffering sosOffering, String version) {
        // initialize as new HashSet so that collection is modifiable
        sosOffering.setResponseFormats(
                new HashSet<>(getResponseFormatRepository().getSupportedResponseFormats(SosConstants.SOS, version)));
        // TODO set as property
    }

    protected Set<String> getResponseFormatForOffering(String offering, String version) {
        Set<String> responseFormats = getCache().getAllObservationTypesForOffering(offering)
                .stream()
                .map(observationType -> getResponseFormatsForObservationType(observationType, SosConstants.SOS,
                        version))
                .filter(Objects::nonNull)
                .flatMap(Set::stream)
                .collect(Collectors.toSet());

        switch (version) {
            case Sos1Constants.SERVICEVERSION:
                return checkForMimeType(responseFormats, true);
            case Sos2Constants.SERVICEVERSION:
                return checkForMimeType(responseFormats, false);
            default:
                return responseFormats;
        }
    }

    private Set<String> checkForMimeType(Set<String> responseFormats, boolean onlyMimeTypes) {
        return responseFormats.stream()
                .filter(format -> isMimeType(format) == onlyMimeTypes)
                .collect(Collectors.toSet());
    }

    private boolean isMimeType(String responseFormat) {
        try {
            return MediaType.parse(responseFormat) != null;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }

    protected void setUpProcedureDescriptionFormatForOffering(SosObservationOffering sosOffering, String version) {
        // TODO: set procDescFormat <-- what is required here?
        sosOffering.setProcedureDescriptionFormat(procedureDescriptionFormatRepository
                .getSupportedProcedureDescriptionFormats(SosConstants.SOS, version));
    }

    protected ReferencedEnvelope getObservedArea(String offering) throws OwsExceptionReport {
        if (getCache().hasSpatialFilteringProfileEnvelopeForOffering(offering)) {
            return getCache().getSpatialFilteringProfileEnvelopeForOffering(offering);
        } else {
            return getCache().getEnvelopeForOffering(offering);
        }
    }

    protected Collection<String> getProceduresForOffering(String offering, String version) throws OwsExceptionReport {
        Collection<String> procedures = new HashSet<>(getCache().getProceduresForOffering(offering));
        if (version.equals(Sos1Constants.SERVICEVERSION)) {
            procedures.addAll(getCache().getHiddenChildProceduresForOffering(offering));
        }
        return procedures.stream()
                .filter(getCache().getPublishedProcedures()::contains)
                .collect(Collectors.toSet());
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

    protected String getGetDataAvailabilityUrl() {
        return addParameter(getBaseGetUrl(), "request", "GetDataAvailability");
    }

    private String getBaseGetUrl() {
        final StringBuilder url = new StringBuilder();
        // service URL
        url.append(getServiceURL());
        // ?
        url.append('?');
        // service
        url.append(OWSConstants.RequestParams.service.name())
                .append('=')
                .append(SosConstants.SOS);
        // version
        url.append('&')
                .append(OWSConstants.RequestParams.version.name())
                .append('=')
                .append(Sos2Constants.SERVICEVERSION);
        return url.toString();
    }

    protected String addParameter(String url, String parameter, String value) {
        return new StringBuilder(url).append('&')
                .append(parameter)
                .append('=')
                .append(value)
                .toString();
    }

    protected RequestOperatorRepository getRequestOperatorRepository() {
        return this.requestOperatorRepository;
    }

    protected ResponseFormatRepository getResponseFormatRepository() {
        return this.responseFormatRepository;
    }

    private void createStaticCapabilities(GetCapabilitiesRequest request, GetCapabilitiesResponse response)
            throws OwsExceptionReport {
        response.setXmlString(this.capabilitiesExtensionService.getActiveStaticCapabilitiesDocument());
    }

    private void createStaticCapabilitiesWithId(GetCapabilitiesRequest request, GetCapabilitiesResponse response)
            throws OwsExceptionReport {
        StaticCapabilities sc = this.capabilitiesExtensionService.getStaticCapabilities(request.getCapabilitiesId());
        if (sc == null) {
            throw new InvalidParameterValueException(GetCapabilitiesParams.CapabilitiesId,
                    request.getCapabilitiesId());
        }
        response.setXmlString(sc.getDocument());
    }

    private void createDynamicCapabilities(GetCapabilitiesRequest request, GetCapabilitiesResponse response,
            boolean showTransactionalOperations) throws OwsExceptionReport {
        Set<String> availableExtensionSections = getExtensionSections(response.getService(), response.getVersion());
        Set<String> requestedExtensionSections = new HashSet<>(availableExtensionSections.size());
        int requestedSections =
                identifyRequestedSections(request, response, availableExtensionSections, requestedExtensionSections);

        SosCapabilities sosCapabilities = new SosCapabilities(request.getService(), request.getVersion(), null, null,
                null, null, null, null, null, null);

        SectionSpecificContentObject sectionSpecificContentObject =
                new SectionSpecificContentObject().setRequest(request)
                        .setResponse(response)
                        .setRequestedExtensionSections(requestedExtensionSections)
                        .setRequestedSections(requestedSections)
                        .setSosCapabilities(sosCapabilities);

        addSectionSpecificContent(sectionSpecificContentObject, request, showTransactionalOperations);
        response.setCapabilities(sosCapabilities);

    }

    private List<OwsOperation> getOperations(String service, String version, boolean showTransactionalOperations)
            throws OwsExceptionReport {
        CompositeOwsException exceptions = new CompositeOwsException();
        List<OwsOperation> operations = getRequestOperatorRepository().getActiveRequestOperatorKeys()
                .stream()
                .filter(k -> k.getService()
                        .equals(service))
                .filter(k -> k.getVersion()
                        .equals(version))
                .map(getRequestOperatorRepository()::getRequestOperator)
                .map(operator -> {
                    if (!(operator instanceof AbstractTransactionalRequestOperator)
                            || (operator instanceof AbstractTransactionalRequestOperator
                                    && showTransactionalOperations)) {
                        try {
                            return operator.getOperationMetadata(service, version);
                        } catch (OwsExceptionReport ex) {
                            exceptions.add(ex);
                            return null;
                        }
                    }
                    return null;
                })
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
        exceptions.throwIfNotEmpty();
        return operations;
    }

    private List<OwsDomain> getParameters(String service, String version) {
        return Arrays.asList(getServiceParameter(service, version), getVersionParameter(service, version),
                getCrsParameter(service, version), getLanguageParameter(service, version));
    }

    private OwsDomain getServiceParameter(String service, String version) {
        OwsValue owsValue = new OwsValue(SosConstants.SOS);
        return new OwsDomain(OWSConstants.RequestParams.service, new OwsAllowedValues(owsValue));
    }

    private OwsDomain getVersionParameter(String service, String version) {
        Set<String> supportedVersions = getServiceOperatorRepository().getSupportedVersions(service);
        Stream<OwsValue> allowedValues = supportedVersions.stream()
                .map(OwsValue::new);
        return new OwsDomain(OWSConstants.RequestParams.version, new OwsAllowedValues(allowedValues));
    }

    private OwsDomain getCrsParameter(String service, String version) {
        Set<String> crs = this.geometryHandler.addOgcCrsPrefix(getGeometryHandler().getSupportedCRS());
        Stream<OwsValue> allowedValues = crs.stream()
                .map(OwsValue::new);
        return new OwsDomain(OWSConstants.AdditionalRequestParams.crs, new OwsAllowedValues(allowedValues));
    }

    private OwsDomain getLanguageParameter(String service, String version) {
        Set<Locale> languages = getCache().getSupportedLanguages();
        Stream<OwsValue> allowedValues = languages.stream()
                .map(LocaleHelper::encode)
                .map(OwsValue::new);
        return new OwsDomain(OWSConstants.AdditionalRequestParams.language, new OwsAllowedValues(allowedValues));
    }

    protected CapabilitiesExtensionService getCapabilitiesExtensionService() {
        return capabilitiesExtensionService;
    }

    protected EncoderRepository getEncoderRepository() {
        return encoderRepository;
    }

    protected DecoderRepository getDecoderRepository() {
        return decoderRepository;
    }

    protected OperationHandlerRepository getOperationHandlerRepository() {
        return operationHandlerRepository;
    }

    protected OwsServiceMetadataRepository getServiceMetadataRepository() {
        return serviceMetadataRepository;
    }

    protected GeometryHandler getGeometryHandler() {
        return geometryHandler;
    }

    protected OwsOperationMetadataExtensionProviderRepository getOwsExtendedCapabilitiesProviderRepository() {
        return owsExtendedCapabilitiesProviderRepository;
    }

    protected SosObservationOfferingExtensionRepository getOfferingExtensionRepository() {
        return offeringExtensionRepository;
    }

    protected OwsCapabilitiesExtensionRepository getCapabilitiesExtensionRepository() {
        return capabilitiesExtensionRepository;
    }

    protected ProcedureDescriptionFormatRepository getProcedureDescriptionFormatRepository() {
        return procedureDescriptionFormatRepository;
    }

    protected TransactionalSecurityConfiguration getTransactionalSecurityConfiguration() {
        return isSetTransactionalSecurityConfiguration() ? transactionalSecurityConfiguration.get() : null;
    }

    private boolean isSetTransactionalSecurityConfiguration() {
        return transactionalSecurityConfiguration.isPresent();
    }

    private static boolean isV2(GetCapabilitiesResponse response) {
        return response.getVersion()
                .equals(Sos2Constants.SERVICEVERSION);
    }

    private static boolean isV1(GetCapabilitiesResponse response) {
        return response.getVersion()
                .equals(Sos1Constants.SERVICEVERSION);
    }

    private static <X, T> Function<X, Set<T>> createSynchronizedSet() {
        return Suppliers.<X, Set<T>> asFunction(HashSet<T>::new)
                .andThen(Collections::synchronizedSet);
    }

    protected static class SectionSpecificContentObject {
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
