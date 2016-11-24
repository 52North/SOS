/*
 * Copyright (C) 2012-2016 52°North Initiative for Geospatial Open Source
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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.inject.Inject;
import javax.xml.namespace.QName;

import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.n52.iceland.binding.BindingRepository;
import org.n52.iceland.coding.decode.DecoderRepository;
import org.n52.iceland.coding.encode.EncoderRepository;
import org.n52.iceland.config.SettingsService;
import org.n52.iceland.ds.OperationHandlerRepository;
import org.n52.iceland.exception.CodedException;
import org.n52.iceland.exception.ows.InvalidParameterValueException;
import org.n52.iceland.exception.ows.NoApplicableCodeException;
import org.n52.iceland.exception.ows.OwsExceptionReport;
import org.n52.iceland.i18n.LocaleHelper;
import org.n52.iceland.ogc.filter.FilterConstants.ConformanceClassConstraintNames;
import org.n52.iceland.ogc.filter.FilterConstants.SpatialOperator;
import org.n52.iceland.ogc.filter.FilterConstants.TimeOperator;
import org.n52.iceland.ogc.gml.GmlConstants;
import org.n52.iceland.ogc.gml.time.TimePeriod;
import org.n52.iceland.ogc.ows.MergableExtension;
import org.n52.iceland.ogc.ows.OWSConstants;
import org.n52.iceland.ogc.ows.OfferingExtension;
import org.n52.iceland.ogc.ows.OwsDomainType;
import org.n52.iceland.ogc.ows.OwsExtendedCapabilitiesProvider;
import org.n52.iceland.ogc.ows.OwsExtendedCapabilitiesProviderRepository;
import org.n52.iceland.ogc.ows.OwsNoValues;
import org.n52.iceland.ogc.ows.OwsOperation;
import org.n52.iceland.ogc.ows.OwsOperationsMetadata;
import org.n52.iceland.ogc.ows.OwsParameterValuePossibleValues;
import org.n52.iceland.ogc.ows.OwsServiceIdentification;
import org.n52.iceland.ogc.ows.ServiceMetadataRepository;
import org.n52.iceland.ogc.ows.StaticCapabilities;
import org.n52.iceland.ogc.ows.StringBasedCapabilitiesExtension;
import org.n52.iceland.ogc.sos.CapabilitiesExtension;
import org.n52.iceland.ogc.sos.CapabilitiesExtensionProvider;
import org.n52.iceland.ogc.sos.CapabilitiesExtensionRepository;
import org.n52.iceland.ogc.sos.Sos2Constants;
import org.n52.iceland.ogc.sos.SosConstants;
import org.n52.iceland.ogc.swes.OfferingExtensionProvider;
import org.n52.iceland.ogc.swes.OfferingExtensionRepository;
import org.n52.iceland.ogc.swes.SwesExtension;
import org.n52.iceland.request.GetCapabilitiesRequest;
import org.n52.iceland.request.operator.RequestOperatorKey;
import org.n52.iceland.request.operator.RequestOperatorRepository;
import org.n52.iceland.response.GetCapabilitiesResponse;
import org.n52.iceland.service.operator.ServiceOperatorRepository;
import org.n52.iceland.util.CollectionHelper;
import org.n52.iceland.util.collections.MultiMaps;
import org.n52.iceland.util.collections.SetMultiMap;
import org.n52.io.request.IoParameters;
import org.n52.io.request.RequestSimpleParameterSet;
import org.n52.series.db.DataAccessException;
import org.n52.series.db.HibernateSessionStore;
import org.n52.series.db.beans.OfferingEntity;
import org.n52.series.db.beans.PhenomenonEntity;
import org.n52.series.db.beans.ProcedureEntity;
import org.n52.series.db.dao.DbQuery;
import org.n52.series.db.dao.OfferingDao;
import org.n52.series.db.dao.PhenomenonDao;
import org.n52.series.db.dao.ProcedureDao;
import org.n52.sos.coding.encode.ProcedureDescriptionFormatRepository;
import org.n52.sos.coding.encode.ResponseFormatRepository;
import org.n52.sos.config.CapabilitiesExtensionService;
import org.n52.sos.ogc.filter.FilterCapabilities;
import org.n52.sos.ogc.sos.SosCapabilities;
import org.n52.sos.ogc.sos.SosEnvelope;
import org.n52.sos.ogc.sos.SosObservationOffering;
import org.n52.sos.ogc.sos.SosOffering;
import org.n52.sos.service.profile.ProfileHandler;
import org.n52.sos.util.GeometryHandler;
import org.n52.sos.util.I18NHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Collections2;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.vividsolutions.jts.geom.Geometry;

/**
 * Implementation of the interface AbstractGetCapabilitiesHandler
 *
 * @since 4.0.0
 */
public class GetCapabilitiesHandler extends AbstractGetCapabilitiesHandler {

    private static final Logger LOGGER = LoggerFactory.getLogger(GetCapabilitiesHandler.class);

    /* section flags (values are powers of 2) */
    private static final int SERVICE_IDENTIFICATION = 0x01;

    private static final int SERVICE_PROVIDER = 0x02;

    private static final int OPERATIONS_METADATA = 0x04;

    private static final int FILTER_CAPABILITIES = 0x08;

    private static final int CONTENTS = 0x10;

    private static final int ALL =
            0x20 | SERVICE_IDENTIFICATION | SERVICE_PROVIDER | OPERATIONS_METADATA | FILTER_CAPABILITIES | CONTENTS;

    @Inject
    private HibernateSessionStore sessionStore;
    
    @Inject
    private SettingsService settingsManager;

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
    private BindingRepository bindingRepository;

    @Inject
    private ServiceMetadataRepository serviceMetadataRepository;

    @Inject
    private RequestOperatorRepository requestOperatorRepository;

    @Inject
    private ServiceOperatorRepository serviceOperatorRepository;

    @Inject
    private ResponseFormatRepository responseFormatRepository;

    @Inject
    private GeometryHandler geometryHandler;

    @Inject
    private OwsExtendedCapabilitiesProviderRepository owsExtendedCapabilitiesProviderRepository;

    @Inject
    private OfferingExtensionRepository offeringExtensionRepository;

    @Inject
    private CapabilitiesExtensionRepository capabilitiesExtensionRepository;

    @Inject
    private ProcedureDescriptionFormatRepository procedureDescriptionFormatRepository;

    public GetCapabilitiesHandler() {
        super(SosConstants.SOS);
    }

    @Override
    public GetCapabilitiesResponse getCapabilities(final GetCapabilitiesRequest request) throws OwsExceptionReport {
        final GetCapabilitiesResponse response = request.getResponse();

        final String scId = request.getCapabilitiesId();
        if (scId == null) {
            if (this.capabilitiesExtensionService.isStaticCapabilitiesActive()) {
                response.setXmlString(this.capabilitiesExtensionService.getActiveStaticCapabilitiesDocument());
                return response;
            }
        } else if (!scId
                .equals(org.n52.iceland.ogc.ows.OWSConstants.GetCapabilitiesParams.DYNAMIC_CAPABILITIES_IDENTIFIER)) {
            final StaticCapabilities sc = this.capabilitiesExtensionService.getStaticCapabilities(scId);
            if (sc == null) {
                throw new InvalidParameterValueException(
                        org.n52.iceland.ogc.ows.OWSConstants.GetCapabilitiesParams.CapabilitiesId, scId);
            }
            response.setXmlString(sc.getDocument());
            return response;
        }

        final Set<String> availableExtensionSections =
                getExtensionSections(response.getService(), response.getVersion());
        final Set<String> requestedExtensionSections = new HashSet<>(availableExtensionSections.size());
        final int requestedSections =
                identifyRequestedSections(request, response, availableExtensionSections, requestedExtensionSections);

        final SosCapabilities sosCapabilities = new SosCapabilities(response.getVersion());

        SectionSpecificContentObject sectionSpecificContentObject = new SectionSpecificContentObject()
                .setRequest(request).setResponse(response).setRequestedExtensionSections(requestedExtensionSections)
                .setRequestedSections(requestedSections).setSosCapabilities(sosCapabilities);
        addSectionSpecificContent(sectionSpecificContentObject, request);
        response.setCapabilities(sosCapabilities);

        return response;
    }

    @Override
    protected Set<String> getExtensionSections(String service, String version) throws OwsExceptionReport {
        return getAndMergeExtensions(service, version).stream().map(CapabilitiesExtension::getSectionName)
                .collect(Collectors.toSet());
    }

    private void addSectionSpecificContent(final SectionSpecificContentObject sectionSpecificContentObject,
            GetCapabilitiesRequest request) throws OwsExceptionReport {
        String verion = sectionSpecificContentObject.getGetCapabilitiesResponse().getVersion();
        String service = sectionSpecificContentObject.getGetCapabilitiesResponse().getService();
        if (isServiceIdentificationSectionRequested(sectionSpecificContentObject.getRequestedSections())) {
            sectionSpecificContentObject.getSosCapabilities()
                    .setServiceIdentification(getServiceIdentification(request, service, verion));
        }
        if (isServiceProviderSectionRequested(sectionSpecificContentObject.getRequestedSections())) {
            sectionSpecificContentObject.getSosCapabilities()
                    .setServiceProvider(this.serviceMetadataRepository.getServiceProviderFactory(service).get());
        }
        if (isOperationsMetadataSectionRequested(sectionSpecificContentObject.getRequestedSections())) {
            sectionSpecificContentObject.getSosCapabilities()
                    .setOperationsMetadata(getOperationsMetadataForOperations(request, service, verion));
        }
        if (isFilterCapabilitiesSectionRequested(sectionSpecificContentObject.getRequestedSections())) {
            sectionSpecificContentObject.getSosCapabilities().setFilterCapabilities(getFilterCapabilities());
        }
        if (isContentsSectionRequested(sectionSpecificContentObject.getRequestedSections())) {
            sectionSpecificContentObject.getSosCapabilities().setContents(getContents(sectionSpecificContentObject));
        }

        if (sectionSpecificContentObject.getRequestedSections() == ALL) {
            sectionSpecificContentObject.getSosCapabilities().setExensions(getAndMergeExtensions(service, verion));
        } else if (!sectionSpecificContentObject.getRequestedExtensionSesctions().isEmpty()) {
            sectionSpecificContentObject.getSosCapabilities().setExensions(
                    getExtensions(sectionSpecificContentObject.getRequestedExtensionSesctions(), service, verion));
        }
    }

    private int identifyRequestedSections(final GetCapabilitiesRequest request, final GetCapabilitiesResponse response,
            final Set<String> availableExtensionSections, final Set<String> requestedExtensionSections)
            throws OwsExceptionReport {
        int sections = 0;
        // handle sections array and set requested sections flag
        if (!request.isSetSections()) {
            sections = ALL;
        } else {
            for (final String section : request.getSections()) {
                if (section.isEmpty()) {
                    LOGGER.warn("A {} element is empty! Check if operator checks for empty elements!",
                            org.n52.iceland.ogc.ows.OWSConstants.GetCapabilitiesParams.Section.name());
                    continue;
                }
                if (section.equals(SosConstants.CapabilitiesSections.All.name())) {
                    sections = ALL;
                    break;
                } else if (section.equals(SosConstants.CapabilitiesSections.ServiceIdentification.name())) {
                    sections |= SERVICE_IDENTIFICATION;
                } else if (section.equals(SosConstants.CapabilitiesSections.ServiceProvider.name())) {
                    sections |= SERVICE_PROVIDER;
                } else if (section.equals(SosConstants.CapabilitiesSections.OperationsMetadata.name())) {
                    sections |= OPERATIONS_METADATA;
                } else if (section.equals(Sos2Constants.CapabilitiesSections.FilterCapabilities.name())) {
                    sections |= FILTER_CAPABILITIES;
                } else if (section.equals(SosConstants.CapabilitiesSections.Contents.name())) {
                    sections |= CONTENTS;
                } else if (availableExtensionSections.contains(section)) {
                    requestedExtensionSections.add(section);
                } else {
                    throw new InvalidParameterValueException()
                            .at(org.n52.iceland.ogc.ows.OWSConstants.GetCapabilitiesParams.Section)
                            .withMessage("The requested section '%s' does not exist or is not supported!", section);
                }
            }
        }
        return sections;
    }

    private OwsServiceIdentification getServiceIdentification(GetCapabilitiesRequest request, String service,
            String version) throws OwsExceptionReport {
        OwsServiceIdentification serviceIdentification = this.serviceMetadataRepository
                .getServiceIdentificationFactory(service).get(request.getRequestedLocale());
        if (version.equals(Sos2Constants.SERVICEVERSION)) {
            serviceIdentification.setProfiles(getProfiles(SosConstants.SOS, Sos2Constants.SERVICEVERSION));
        }
        return serviceIdentification;
    }

    private Set<String> getProfiles(String service, String version) {

        Set<String> profiles = Stream
                .of(this.bindingRepository.getBindings().values(),
                        this.requestOperatorRepository.getRequestOperators(), this.decoderRepository.getDecoders(),
                        this.encoderRepository.getEncoders(),
                        this.operationHandlerRepository.getOperationHandlers().values())
                .flatMap(Collection::stream).map(c -> c.getConformanceClasses(service, version)).flatMap(Set::stream)
                .collect(Collectors.toSet());

        // FIXME additional profiles
        if ("hydrology".equalsIgnoreCase(this.profileHandler.getActiveProfile().getIdentifier())) {
            profiles.add("http://www.opengis.net/spec/SOS_application-profile_hydrology/1.0/req/hydrosos");
        }
        return profiles;
    }

    /**
     * Get the OperationsMetadat for all supported operations
     *
     * @param service
     *            Requested service
     * @param version
     *            Requested service version
     * @return OperationsMetadata for all operations supported by the requested
     *         service and version
     * @throws OwsExceptionReport
     *             If an error occurs
     */
    private OwsOperationsMetadata getOperationsMetadataForOperations(final GetCapabilitiesRequest request,
            final String service, final String version) throws OwsExceptionReport {

        final OwsOperationsMetadata operationsMetadata = new OwsOperationsMetadata();
        operationsMetadata.addCommonValue(OWSConstants.RequestParams.service.name(),
                new OwsParameterValuePossibleValues(SosConstants.SOS));
        operationsMetadata.addCommonValue(OWSConstants.RequestParams.version.name(),
                new OwsParameterValuePossibleValues(getServiceOperatorRepository().getSupportedVersions(service)));
        // crs
        operationsMetadata.addCommonValue(OWSConstants.AdditionalRequestParams.crs.name(),
                new OwsParameterValuePossibleValues(this.geometryHandler.addOgcCrsPrefix(getCache().getEpsgCodes())));
        // language
        operationsMetadata.addCommonValue(OWSConstants.AdditionalRequestParams.language.name(),
                new OwsParameterValuePossibleValues(
                        Collections2.transform(getCache().getSupportedLanguages(), LocaleHelper.toStringFunction())));

        // FIXME: OpsMetadata for InsertSensor, InsertObservation SOS 2.0
        final Set<RequestOperatorKey> requestOperatorKeyTypes =
                getRequestOperatorRepository().getActiveRequestOperatorKeys();
        final List<OwsOperation> opsMetadata = new ArrayList<>(requestOperatorKeyTypes.size());
        for (final RequestOperatorKey requestOperatorKey : requestOperatorKeyTypes) {
            if (requestOperatorKey.getServiceOperatorKey().getVersion().equals(version)) {
                OwsOperation operationMetadata = getRequestOperatorRepository().getRequestOperator(requestOperatorKey)
                        .getOperationMetadata(service, version);
                if (operationMetadata != null) {
                    opsMetadata.add(operationMetadata);
                }
            }
        }
        operationsMetadata.setOperations(opsMetadata);

        /*
         * check if an OwsExtendedCapabilities provider is available for this
         * service and check if this provider provides OwsExtendedCapabilities
         * for the request
         */
        if (owsExtendedCapabilitiesProviderRepository.hasExtendedCapabilitiesProvider(request)) {
            OwsExtendedCapabilitiesProvider extendedCapabilitiesProvider =
                    owsExtendedCapabilitiesProviderRepository.getExtendedCapabilitiesProvider(request);
            if (extendedCapabilitiesProvider != null
                    && extendedCapabilitiesProvider.hasExtendedCapabilitiesFor(request)) {
                operationsMetadata
                        .setExtendedCapabilities(extendedCapabilitiesProvider.getOwsExtendedCapabilities(request));
            }
        }

        return operationsMetadata;
    }

    /**
     * Get the FilterCapabilities
     *
     * @param version
     *            Requested service version
     * @return FilterCapabilities
     */
    private FilterCapabilities getFilterCapabilities() {
        final FilterCapabilities filterCapabilities = new FilterCapabilities();
        getConformance(filterCapabilities);
        getSpatialFilterCapabilities(filterCapabilities);
        getTemporalFilterCapabilities(filterCapabilities);

        return filterCapabilities;
    }

    private void getConformance(final FilterCapabilities filterCapabilities) {
        // set Query conformance class
        filterCapabilities.addConformance(
                new OwsDomainType(ConformanceClassConstraintNames.ImplementsQuery.name(), new OwsNoValues(), FALSE));
        // set Ad hoc query conformance class
        filterCapabilities.addConformance(new OwsDomainType(
                ConformanceClassConstraintNames.ImplementsAdHocQuery.name(), new OwsNoValues(), FALSE));
        // set Functions conformance class
        filterCapabilities.addConformance(new OwsDomainType(ConformanceClassConstraintNames.ImplementsFunctions.name(),
                new OwsNoValues(), FALSE));
        // set Resource Identification conformance class
        filterCapabilities.addConformance(new OwsDomainType(
                ConformanceClassConstraintNames.ImplementsResourceld.name(), new OwsNoValues(), FALSE));
        // set Minimum Standard Filter conformance class
        filterCapabilities.addConformance(new OwsDomainType(
                ConformanceClassConstraintNames.ImplementsMinStandardFilter.name(), new OwsNoValues(), FALSE));
        // set Standard Filter conformance class
        filterCapabilities.addConformance(new OwsDomainType(
                ConformanceClassConstraintNames.ImplementsStandardFilter.name(), new OwsNoValues(), FALSE));
        // set Minimum Spatial Filter conformance class
        filterCapabilities.addConformance(new OwsDomainType(
                ConformanceClassConstraintNames.ImplementsMinSpatialFilter.name(), new OwsNoValues(), TRUE));
        // set Spatial Filter conformance class
        filterCapabilities.addConformance(new OwsDomainType(
                ConformanceClassConstraintNames.ImplementsSpatialFilter.name(), new OwsNoValues(), TRUE));
        // set Minimum Temporal Filter conformance class
        filterCapabilities.addConformance(new OwsDomainType(
                ConformanceClassConstraintNames.ImplementsMinTemporalFilter.name(), new OwsNoValues(), TRUE));
        // set Temporal Filter conformance class
        filterCapabilities.addConformance(new OwsDomainType(
                ConformanceClassConstraintNames.ImplementsTemporalFilter.name(), new OwsNoValues(), TRUE));
        // set Version navigation conformance class
        filterCapabilities.addConformance(new OwsDomainType(
                ConformanceClassConstraintNames.ImplementsVersionNav.name(), new OwsNoValues(), FALSE));
        // set Sorting conformance class
        filterCapabilities.addConformance(
                new OwsDomainType(ConformanceClassConstraintNames.ImplementsSorting.name(), new OwsNoValues(), FALSE));
        // set Extended Operators conformance class
        filterCapabilities.addConformance(new OwsDomainType(
                ConformanceClassConstraintNames.ImplementsExtendedOperators.name(), new OwsNoValues(), FALSE));
        // set Minimum XPath conformance class
        filterCapabilities.addConformance(new OwsDomainType(
                ConformanceClassConstraintNames.ImplementsMinimumXPath.name(), new OwsNoValues(), FALSE));
        // set Schema Element Function conformance class
        filterCapabilities.addConformance(new OwsDomainType(
                ConformanceClassConstraintNames.ImplementsSchemaElementFunc.name(), new OwsNoValues(), FALSE));
    }

    private SosEnvelope processObservedArea(Geometry geometry) throws CodedException {
        // TODO Check transformation
        // if (requestedSrid >= 0 && sosEnvelope.getSrid() != requestedSrid) {
        // SosEnvelope tranformedEnvelope = new SosEnvelope();
        // tranformedEnvelope.setSrid(requestedSrid);
        // tranformedEnvelope.setEnvelope(GeometryHandler.getInstance().transformEnvelope(sosEnvelope.getEnvelope(),
        // sosEnvelope.getSrid(), requestedSrid));
        // return tranformedEnvelope;
        // }
        return new SosEnvelope(geometry.getEnvelopeInternal(), geometry.getSRID());
    }

    /**
     * Get the contents for SOS 2.0 capabilities
     *
     * @param version
     *            Requested service version
     * @return Offerings for contents
     *
     *
     * @throws OwsExceptionReport
     *             * If an error occurs
     */
    // FIXME why version parameter? The method signature cleary states which
    // version is supported by this!
    private List<SosObservationOffering> getContents(SectionSpecificContentObject sectionSpecificContentObject)
            throws OwsExceptionReport {
        Session session = null;
        try {
            session = sessionStore.getSession();
        String version = sectionSpecificContentObject.getGetCapabilitiesResponse().getVersion();
        final Collection<OfferingEntity> offerings = new OfferingDao(session).getAllInstances(new DbQuery(IoParameters.createDefaults()));
        final List<SosObservationOffering> sosOfferings = new ArrayList<>(offerings.size());
        final Map<String, List<OfferingExtension>> extensions =
                this.capabilitiesExtensionService.getActiveOfferingExtensions();

        if (CollectionHelper.isEmpty(offerings)) {
            // Set empty offering to add empty Contents section to Capabilities
            sosOfferings.add(new SosObservationOffering());
        } else {
            for (final OfferingEntity offering : offerings) {
                final Collection<ProcedureEntity> procedures = getProceduresForOffering(offering, session);
                if (!procedures.isEmpty()) {
                    final Collection<String> observationTypes = offering.getObservationTypes();
                    if (observationTypes != null && !observationTypes.isEmpty()) {
                        // FIXME why a loop? We are in SOS 2.0 context -> offering 1
                        // <-> 1 procedure!
                        for (final ProcedureEntity procedure : procedures) {
    
                            final SosObservationOffering sosObservationOffering = new SosObservationOffering();
    
                            // insert observationTypes
                            sosObservationOffering.setObservationTypes(observationTypes);
    
                            if (offering.hasEnvelope()) {
                                sosObservationOffering.setObservedArea(processObservedArea(offering.getEnvelope()));
                            }
    
                            sosObservationOffering.setProcedures(Collections.singletonList(procedure.getDomainId()));
    
                            // TODO: add intended application
    
                            // add offering to observation offering
                            addSosOfferingToObservationOffering(offering, sosObservationOffering,
                                    sectionSpecificContentObject.getGetCapabilitiesRequest());
                            // add offering extension
                            if (offeringExtensionRepository.hasOfferingExtensionProviderFor(
                                    sectionSpecificContentObject.getGetCapabilitiesRequest())) {
                                for (OfferingExtensionProvider provider : offeringExtensionRepository
                                        .getOfferingExtensionProvider(
                                                sectionSpecificContentObject.getGetCapabilitiesRequest())) {
                                    if (provider != null && provider.hasExtendedOfferingFor(offering.getDomainId())) {
                                        sosObservationOffering.addExtensions(provider.getOfferingExtensions(offering.getDomainId()));
                                    }
                                }
                            }
                            if (extensions.containsKey(sosObservationOffering.getOffering().getIdentifier())) {
                                for (OfferingExtension offeringExtension : extensions
                                        .get(sosObservationOffering.getOffering().getIdentifier())) {
                                    sosObservationOffering.addExtension(
                                            new SwesExtension<OfferingExtension>().setValue(offeringExtension));
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
                    LOGGER.error( "No procedures are contained in the database for the offering {}! Please contact the admin of this SOS.", offering.getDomainId());
                }
            }
        }

        return sosOfferings;
        } catch (final HibernateException | DataAccessException e) {
            throw new NoApplicableCodeException().causedBy(e).withMessage(
                    "Error while querying data for DescribeSensor document!");
        } finally {
            sessionStore.returnSession(session);
        }
    }

    private void addSosOfferingToObservationOffering(OfferingEntity offering, SosObservationOffering sosObservationOffering,
            GetCapabilitiesRequest request) throws CodedException {
        SosOffering sosOffering = new SosOffering(offering.getDomainId(), false);
        sosObservationOffering.setOffering(sosOffering);
        // add offering name
        I18NHelper.addOfferingNames(getCache(), sosOffering, request.getRequestedLocale(), Locale.ROOT, false);
        // add offering description
        I18NHelper.addOfferingDescription(sosOffering, request.getRequestedLocale(), Locale.ROOT, getCache());
    }

    /**
     * Set SpatialFilterCapabilities to FilterCapabilities
     *
     * @param filterCapabilities
     *            FilterCapabilities
     * @param version
     *            SOS version
     */
    private void getSpatialFilterCapabilities(FilterCapabilities filterCapabilities) {

        // set GeometryOperands
        final List<QName> operands = new LinkedList<>();
        operands.add(GmlConstants.QN_ENVELOPE_32);

        filterCapabilities.setSpatialOperands(operands);

        // set SpatialOperators
        final SetMultiMap<SpatialOperator, QName> ops = MultiMaps.newSetMultiMap(SpatialOperator.class);
        ops.add(SpatialOperator.BBOX, GmlConstants.QN_ENVELOPE_32);

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
    private void getTemporalFilterCapabilities(FilterCapabilities filterCapabilities) {

        // set TemporalOperands
        final List<QName> operands = new ArrayList<>(2);
        operands.add(GmlConstants.QN_TIME_PERIOD_32);
        operands.add(GmlConstants.QN_TIME_INSTANT_32);

        filterCapabilities.setTemporalOperands(operands);

        // set TemporalOperators
        final SetMultiMap<TimeOperator, QName> ops = MultiMaps.newSetMultiMap(TimeOperator.class);
        for (final TimeOperator op : TimeOperator.values()) {
            ops.add(op, GmlConstants.QN_TIME_INSTANT_32);
            ops.add(op, GmlConstants.QN_TIME_PERIOD_32);
        }
        filterCapabilities.setTemporalOperators(ops);
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
    private List<CapabilitiesExtension> getAndMergeExtensions(final String service, final String version)
            throws OwsExceptionReport {
        final List<CapabilitiesExtensionProvider> capabilitiesExtensionProviders =
                capabilitiesExtensionRepository.getCapabilitiesExtensionProvider(service, version);
        final List<CapabilitiesExtension> extensions = Lists.newLinkedList();
        if (CollectionHelper.isNotEmpty(capabilitiesExtensionProviders)) {
            final HashMap<String, MergableExtension> map = new HashMap<>(capabilitiesExtensionProviders.size());
            for (final CapabilitiesExtensionProvider capabilitiesExtensionDAO : capabilitiesExtensionProviders) {
                if (capabilitiesExtensionDAO.getExtension() != null) {
                    if (capabilitiesExtensionDAO.getExtension() instanceof MergableExtension) {
                        final MergableExtension me = (MergableExtension) capabilitiesExtensionDAO.getExtension();
                        final MergableExtension previous = map.get(me.getSectionName());
                        if (previous == null) {
                            map.put(me.getSectionName(), me);
                        } else {
                            previous.merge(me);
                        }
                    } else {
                        extensions.add(capabilitiesExtensionDAO.getExtension());
                    }
                }
            }
            extensions.addAll(map.values());
        }
        Map<String, StringBasedCapabilitiesExtension> activeCapabilitiesExtensions =
                this.capabilitiesExtensionService.getActiveCapabilitiesExtensions();
        if (activeCapabilitiesExtensions != null && !activeCapabilitiesExtensions.isEmpty()) {
            extensions.addAll(activeCapabilitiesExtensions.values());
        }
        return extensions;
    }

    private Collection<CapabilitiesExtension> getExtensions(final Set<String> requestedExtensionSections,
            final String service, final String version) throws OwsExceptionReport {
        return getAndMergeExtensions(service, version).stream()
                .filter(e -> requestedExtensionSections.contains(e.getSectionName())).collect(Collectors.toList());
    }

    protected void setUpPhenomenaForOffering(OfferingEntity offering, ProcedureEntity procedure,
            SosObservationOffering sosOffering, Session session) throws DataAccessException {
        RequestSimpleParameterSet rsps = new RequestSimpleParameterSet();
        rsps.addParameter(IoParameters.OFFERINGS, IoParameters.getJsonNodeFrom(offering.getPkid()));
        rsps.addParameter(IoParameters.PROCEDURES, IoParameters.getJsonNodeFrom(procedure.getPkid()));
        List<PhenomenonEntity> observableProperties = new PhenomenonDao(session).getAllInstances(new DbQuery(IoParameters.createFromQuery(rsps)));
        
        Collection<String> phenomenons = new LinkedList<>();
        Map<String, Collection<String>> phens4CompPhens = new HashMap<>();
        for (PhenomenonEntity observableProperty : observableProperties) {
            if (!observableProperty.hasChilds() && !observableProperty.hasParents()) {
                phenomenons.add(observableProperty.getDomainId());
            } else if (observableProperty.hasChilds() && !observableProperty.hasParents()) {
                Set<String> childs = new TreeSet<String>();
                for (PhenomenonEntity child : observableProperty.getChilds()) {
                    childs.add(child.getDomainId());
                }
            }
        }
        sosOffering.setObservableProperties(phenomenons);
        sosOffering.setPhens4CompPhens(phens4CompPhens);
//        sosOffering.setCompositePhenomena(getCache().getCompositePhenomenonsForOffering(offering));
//
//        final Collection<String> compositePhenomenonsForOffering =
//                getCache().getCompositePhenomenonsForOffering(offering);
//
//        if (compositePhenomenonsForOffering != null) {
//            final Map<String, Collection<String>> phens4CompPhens =
//                    new HashMap<>(compositePhenomenonsForOffering.size());
//            for (final String compositePhenomenon : compositePhenomenonsForOffering) {
//                final Collection<String> phenomenonsForComposite =
//                        getCache().getObservablePropertiesForCompositePhenomenon(compositePhenomenon);
//                phens4CompPhens.put(compositePhenomenon, phenomenonsForComposite);
//            }
//            sosOffering.setPhens4CompPhens(phens4CompPhens);
//        } else {
//            sosOffering.setPhens4CompPhens(Collections.<String, Collection<String>> emptyMap());
//        }

    }

//    private boolean isHiddenChildProcedureObservableProperty(final String offering,
//            final Set<String> proceduresForObservableProperty) {
//        for (final String hiddenProcedure : getCache().getHiddenChildProceduresForOffering(offering)) {
//            if (proceduresForObservableProperty.contains(hiddenProcedure)) {
//                return true;
//            }
//        }
//        return false;
//    }

    private void setUpRelatedFeaturesForOffering(OfferingEntity offering,
            SosObservationOffering sosObservationOffering) throws OwsExceptionReport {
        final Map<String, Set<String>> relatedFeatures = Maps.newHashMap();
        final Set<String> relatedFeaturesForThisOffering =
                getCache().getRelatedFeaturesForOffering(offering.getDomainId());
        if (CollectionHelper.isNotEmpty(relatedFeaturesForThisOffering)) {
            for (final String relatedFeature : relatedFeaturesForThisOffering) {
                relatedFeatures.put(relatedFeature, getCache().getRolesForRelatedFeature(relatedFeature));
            }
            /*
             * TODO add setting to set FeatureOfInterest if relatedFeatures are
             * empty. } else { final Set<String> role =
             * Collections.singleton("featureOfInterestID"); final Set<String>
             * featuresForOffering =
             * getCache().getFeaturesOfInterestForOffering(offering); if
             * (featuresForOffering != null) { for (final String foiID :
             * featuresForOffering) { if
             * (getCache().getProceduresForFeatureOfInterest
             * (foiID).contains(procedure)) { relatedFeatures.put(foiID, role);
             * } } }
             */

        }
        sosObservationOffering.setRelatedFeatures(relatedFeatures);
    }

    protected void setUpTimeForOffering(OfferingEntity offering, SosObservationOffering sosOffering) {
        sosOffering.setPhenomenonTime(new TimePeriod(offering.getPhenomenonTimeStart(), offering.getPhenomenonTimeEnd()));
        sosOffering.setResultTime(new TimePeriod(offering.getResultTimeStart(), offering.getResultTimeEnd()));
    }

    protected void setUpFeatureOfInterestTypesForOffering(OfferingEntity offering, SosObservationOffering sosOffering) {
        sosOffering.setFeatureOfInterestTypes(offering.getFeatureTypes());
    }

    protected void setUpResponseFormatForOffering(SosObservationOffering sosOffering) {
        // initialize as new HashSet so that collection is modifiable
        final Collection<String> responseFormats =
                new HashSet<>(getResponseFormatRepository().getSupportedResponseFormats(SosConstants.SOS, Sos2Constants.VERSION));
        sosOffering.setResponseFormats(responseFormats);
        // TODO set as property
    }

    protected void setUpProcedureDescriptionFormatForOffering(SosObservationOffering sosOffering) {
        // TODO: set procDescFormat <-- what is required here?
        sosOffering.setProcedureDescriptionFormat(procedureDescriptionFormatRepository
                .getSupportedProcedureDescriptionFormats(SosConstants.SOS, Sos2Constants.VERSION));
    }

    private Collection<ProcedureEntity> getProceduresForOffering(final OfferingEntity offering, Session session)
            throws OwsExceptionReport, DataAccessException {
        RequestSimpleParameterSet rsps = new RequestSimpleParameterSet();
        rsps.addParameter(IoParameters.OFFERINGS, IoParameters.getJsonNodeFrom(offering.getPkid()));
        List<ProcedureEntity> procedures =
                new ProcedureDao(session).getAllInstances(new DbQuery(IoParameters.createFromQuery(rsps)));
        // if (procedures.isEmpty()) {
        // throw new NoApplicableCodeException().withMessage(
        // "No procedures are contained in the database for the offering '%s'!
        // Please contact the admin of this SOS.",
        // offering);
        // }
        return procedures;
    }

    private boolean isContentsSectionRequested(final int sections) {
        return (sections & CONTENTS) != 0;
    }

    private boolean isFilterCapabilitiesSectionRequested(final int sections) {
        return (sections & FILTER_CAPABILITIES) != 0;
    }

    private boolean isOperationsMetadataSectionRequested(final int sections) {
        return (sections & OPERATIONS_METADATA) != 0;
    }

    private boolean isServiceProviderSectionRequested(final int sections) {
        return (sections & SERVICE_PROVIDER) != 0;
    }

    private boolean isServiceIdentificationSectionRequested(final int sections) {
        return (sections & SERVICE_IDENTIFICATION) != 0;
    }

    protected RequestOperatorRepository getRequestOperatorRepository() {
        return this.requestOperatorRepository;
    }

    protected ServiceOperatorRepository getServiceOperatorRepository() {
        return this.serviceOperatorRepository;
    }

    protected ResponseFormatRepository getResponseFormatRepository() {
        return this.responseFormatRepository;
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
