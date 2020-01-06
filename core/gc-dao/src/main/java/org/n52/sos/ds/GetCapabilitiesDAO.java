/**
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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.xml.namespace.QName;

import org.n52.sos.binding.Binding;
import org.n52.sos.binding.BindingRepository;
import org.n52.sos.coding.CodingRepository;
import org.n52.sos.config.SettingsManager;
import org.n52.sos.config.annotation.Configurable;
import org.n52.sos.decode.Decoder;
import org.n52.sos.encode.Encoder;
import org.n52.sos.exception.CodedException;
import org.n52.sos.exception.ConfigurationException;
import org.n52.sos.exception.ows.InvalidParameterValueException;
import org.n52.sos.i18n.LocaleHelper;
import org.n52.sos.ogc.OGCConstants;
import org.n52.sos.ogc.filter.FilterCapabilities;
import org.n52.sos.ogc.filter.FilterConstants.ComparisonOperator;
import org.n52.sos.ogc.filter.FilterConstants.ConformanceClassConstraintNames;
import org.n52.sos.ogc.filter.FilterConstants.SpatialOperator;
import org.n52.sos.ogc.filter.FilterConstants.TimeOperator;
import org.n52.sos.ogc.gml.GmlConstants;
import org.n52.sos.ogc.gml.ReferenceType;
import org.n52.sos.ogc.gml.time.TimePeriod;
import org.n52.sos.ogc.ows.MergableExtension;
import org.n52.sos.ogc.ows.OWSConstants;
import org.n52.sos.ogc.ows.OWSConstants.RequestParams;
import org.n52.sos.ogc.ows.OfferingExtension;
import org.n52.sos.ogc.ows.OwsDomainType;
import org.n52.sos.ogc.ows.OwsExceptionReport;
import org.n52.sos.ogc.ows.OwsExtendedCapabilitiesProvider;
import org.n52.sos.ogc.ows.OwsExtendedCapabilitiesRepository;
import org.n52.sos.ogc.ows.OwsNoValues;
import org.n52.sos.ogc.ows.OwsOperation;
import org.n52.sos.ogc.ows.OwsOperationsMetadata;
import org.n52.sos.ogc.ows.OwsParameterValuePossibleValues;
import org.n52.sos.ogc.ows.SosServiceIdentification;
import org.n52.sos.ogc.ows.StaticCapabilities;
import org.n52.sos.ogc.sos.CapabilitiesExtension;
import org.n52.sos.ogc.sos.CapabilitiesExtensionProvider;
import org.n52.sos.ogc.sos.CapabilitiesExtensionRepository;
import org.n52.sos.ogc.sos.RelatedOfferingConstants;
import org.n52.sos.ogc.sos.RelatedOfferings;
import org.n52.sos.ogc.sos.Sos1Constants;
import org.n52.sos.ogc.sos.Sos2Constants;
import org.n52.sos.ogc.sos.SosCapabilities;
import org.n52.sos.ogc.sos.SosConstants;
import org.n52.sos.ogc.sos.SosEnvelope;
import org.n52.sos.ogc.sos.SosObservationOffering;
import org.n52.sos.ogc.sos.SosOffering;
import org.n52.sos.ogc.swes.OfferingExtensionProvider;
import org.n52.sos.ogc.swes.OfferingExtensionRepository;
import org.n52.sos.ogc.swes.SwesExtensionImpl;
import org.n52.sos.request.GetCapabilitiesRequest;
import org.n52.sos.request.operator.RequestOperatorKey;
import org.n52.sos.request.operator.RequestOperatorRepository;
import org.n52.sos.response.GetCapabilitiesResponse;
import org.n52.sos.service.Configurator;
import org.n52.sos.service.ServiceConfiguration;
import org.n52.sos.service.operator.ServiceOperatorRepository;
import org.n52.sos.util.CollectionHelper;
import org.n52.sos.util.GeometryHandler;
import org.n52.sos.util.I18NHelper;
import org.n52.sos.util.MultiMaps;
import org.n52.sos.util.OMHelper;
import org.n52.sos.util.SetMultiMap;
import org.n52.sos.util.http.MediaType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Collections2;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

/**
 * Implementation of the interface IGetCapabilitiesDAO
 *
 * @since 4.0.0
 */
@Configurable
public class GetCapabilitiesDAO extends AbstractGetCapabilitiesDAO {

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

    private static final Logger LOGGER = LoggerFactory.getLogger(GetCapabilitiesDAO.class);

    /* section flags (values are powers of 2) */
    private static final int SERVICE_IDENTIFICATION = 0x01;

    private static final int SERVICE_PROVIDER = 0x02;

    private static final int OPERATIONS_METADATA = 0x04;

    private static final int FILTER_CAPABILITIES = 0x08;

    private static final int CONTENTS = 0x10;

    private static final int ALL = 0x20 | SERVICE_IDENTIFICATION | SERVICE_PROVIDER | OPERATIONS_METADATA
            | FILTER_CAPABILITIES | CONTENTS;

    public GetCapabilitiesDAO() {
        super(SosConstants.SOS);
    }

    @Override
    public GetCapabilitiesResponse getCapabilities(final GetCapabilitiesRequest request) throws OwsExceptionReport {
        final GetCapabilitiesResponse response = request.getResponse();

        final String scId = request.getCapabilitiesId();
        if (scId == null) {
            if (getSettingsManager().isStaticCapabilitiesActive()) {
                response.setXmlString(getSettingsManager().getActiveStaticCapabilitiesDocument());
                return response;
            }
        } else if (!scId.equals(SosConstants.GetCapabilitiesParams.DYNAMIC_CAPABILITIES_IDENTIFIER)) {
            final StaticCapabilities sc = getSettingsManager().getStaticCapabilities(scId);
            if (sc == null) {
                throw new InvalidParameterValueException(SosConstants.GetCapabilitiesParams.CapabilitiesId, scId);
            }
            response.setXmlString(sc.getDocument());
            return response;
        }

        final Set<String> availableExtensionSections =
                getExtensionSections(response.getService(), response.getVersion());
        final Set<String> requestedExtensionSections = new HashSet<String>(availableExtensionSections.size());
        final int requestedSections =
                identifyRequestedSections(request, response, availableExtensionSections, requestedExtensionSections);

        final SosCapabilities sosCapabilities = new SosCapabilities(response.getVersion());

        SectionSpecificContentObject sectionSpecificContentObject =
                new SectionSpecificContentObject().setRequest(request).setResponse(response)
                        .setRequestedExtensionSections(requestedExtensionSections)
                        .setRequestedSections(requestedSections).setSosCapabilities(sosCapabilities);
        addSectionSpecificContent(sectionSpecificContentObject, request);
        response.setCapabilities(sosCapabilities);

        return response;
    }

    private void addSectionSpecificContent(final SectionSpecificContentObject sectionSpecificContentObject,
            GetCapabilitiesRequest request) throws OwsExceptionReport {
        String version = sectionSpecificContentObject.getGetCapabilitiesResponse().getVersion();
        String service = sectionSpecificContentObject.getGetCapabilitiesResponse().getService();
        if (isServiceIdentificationSectionRequested(sectionSpecificContentObject.getRequestedSections())) {
            sectionSpecificContentObject.getSosCapabilities().setServiceIdentification(
                    getServiceIdentification(request, version));
        }
        if (isServiceProviderSectionRequested(sectionSpecificContentObject.getRequestedSections())) {
            sectionSpecificContentObject.getSosCapabilities().setServiceProvider(
                    getConfigurator().getServiceProvider());
        }
        if (isOperationsMetadataSectionRequested(sectionSpecificContentObject.getRequestedSections())) {
            sectionSpecificContentObject.getSosCapabilities().setOperationsMetadata(
                    getOperationsMetadataForOperations(request, service, version));
        }
        if (isFilterCapabilitiesSectionRequested(sectionSpecificContentObject.getRequestedSections())) {
            sectionSpecificContentObject.getSosCapabilities().setFilterCapabilities(getFilterCapabilities(version));
        }
        if (isContentsSectionRequested(sectionSpecificContentObject.getRequestedSections())) {
            if (isVersionSos2(sectionSpecificContentObject.getGetCapabilitiesResponse())) {
                sectionSpecificContentObject.getSosCapabilities().setContents(
                        getContentsForSosV2(sectionSpecificContentObject));
            } else {
                sectionSpecificContentObject.getSosCapabilities().setContents(
                        getContents(sectionSpecificContentObject));
            }
        }

        if (isVersionSos2(sectionSpecificContentObject.getGetCapabilitiesResponse())) {
            if (sectionSpecificContentObject.getRequestedSections() == ALL) {
                sectionSpecificContentObject.getSosCapabilities().setExensions(getAndMergeExtensions(service, version));
            } else if (!sectionSpecificContentObject.getRequestedExtensionSesctions().isEmpty()) {
                sectionSpecificContentObject.getSosCapabilities().setExensions(
                        getExtensions(sectionSpecificContentObject.getRequestedExtensionSesctions(), service, version));
            }
        }
    }

    private int identifyRequestedSections(final GetCapabilitiesRequest request,
            final GetCapabilitiesResponse response, final Set<String> availableExtensionSections,
            final Set<String> requestedExtensionSections) throws OwsExceptionReport {
        int sections = 0;
        // handle sections array and set requested sections flag
        if (!request.isSetSections()) {
            sections = ALL;
        } else {
            for (final String section : request.getSections()) {
                if (section.isEmpty()) {
                    LOGGER.warn("A {} element is empty! Check if operator checks for empty elements!",
                            SosConstants.GetCapabilitiesParams.Section.name());
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
                } else if ((section.equals(Sos1Constants.CapabilitiesSections.Filter_Capabilities.name()) && response
                        .getVersion().equals(Sos1Constants.SERVICEVERSION))
                        || (section.equals(Sos2Constants.CapabilitiesSections.FilterCapabilities.name()) && isVersionSos2(response))) {
                    sections |= FILTER_CAPABILITIES;
                } else if (section.equals(SosConstants.CapabilitiesSections.Contents.name())) {
                    sections |= CONTENTS;
                } else if (availableExtensionSections.contains(section) && isVersionSos2(response)) {
                    requestedExtensionSections.add(section);
                } else {
                    throw new InvalidParameterValueException().at(SosConstants.GetCapabilitiesParams.Section)
                            .withMessage("The requested section '%s' does not exist or is not supported!", section);
                }
            }
        }
        return sections;
    }

    private SosServiceIdentification getServiceIdentification(
            GetCapabilitiesRequest request, String version) throws OwsExceptionReport {
        Locale locale = LocaleHelper.fromRequest(request);
        SosServiceIdentification serviceIdentification = getConfigurator()
                .getServiceIdentification(locale);
        if (version.equals(Sos2Constants.SERVICEVERSION)) {
            serviceIdentification.setProfiles(getProfiles());
        }
        return serviceIdentification;
    }

    private Set<String> getProfiles() {
        final List<String> profiles = new LinkedList<String>();
        for (final Binding bindig : BindingRepository.getInstance().getBindings().values()) {
            profiles.addAll(bindig.getConformanceClasses());
        }
        for (final RequestOperatorKey k : RequestOperatorRepository.getInstance().getActiveRequestOperatorKeys()) {
            profiles.addAll(RequestOperatorRepository.getInstance().getRequestOperator(k).getConformanceClasses());
        }
        for (final Decoder<?, ?> decoder : CodingRepository.getInstance().getDecoders()) {
            profiles.addAll(decoder.getConformanceClasses());
        }
        for (final Encoder<?, ?> encoder : CodingRepository.getInstance().getEncoders()) {
            profiles.addAll(encoder.getConformanceClasses());
        }
        for (final OperationDAO operationDAO : OperationDAORepository.getInstance().getOperationDAOs().values()) {
            profiles.addAll(operationDAO.getConformanceClasses());
        }
        // FIXME additional profiles
        if ("hydrology".equalsIgnoreCase(Configurator.getInstance().getProfileHandler().getActiveProfile()
                .getIdentifier())) {
            profiles.add("http://www.opengis.net/spec/SOS_application-profile_hydrology/1.0/req/hydrosos");
        }
        return Sets.newHashSet(profiles);
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
        operationsMetadata.addCommonValue(
                OWSConstants.RequestParams.version.name(),
                new OwsParameterValuePossibleValues(ServiceOperatorRepository.getInstance().getSupportedVersions(
                        service)));
        // crs
        operationsMetadata.addCommonValue(OWSConstants.AdditionalRequestParams.crs.name(),
                new OwsParameterValuePossibleValues(GeometryHandler.getInstance().addOgcCrsPrefix(getCache().getEpsgCodes())));
        // language
        operationsMetadata.addCommonValue(OWSConstants.AdditionalRequestParams.language.name(),
                new OwsParameterValuePossibleValues(Collections2.transform(getCache().getSupportedLanguages(), LocaleHelper.toStringFunction())));

        // FIXME: OpsMetadata for InsertSensor, InsertObservation SOS 2.0
        final Set<RequestOperatorKey> requestOperatorKeyTypes =
                RequestOperatorRepository.getInstance().getActiveRequestOperatorKeys();
        final List<OwsOperation> opsMetadata = new ArrayList<OwsOperation>(requestOperatorKeyTypes.size());
        for (final RequestOperatorKey requestOperatorKeyType : requestOperatorKeyTypes) {
            if (requestOperatorKeyType.getServiceOperatorKey().getVersion().equals(version)) {
                final OwsOperation operationMetadata =
                        RequestOperatorRepository.getInstance().getRequestOperator(requestOperatorKeyType)
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
        if (OwsExtendedCapabilitiesRepository.getInstance().hasExtendedCapabilitiesProvider(request)) {
            OwsExtendedCapabilitiesProvider extendedCapabilitiesProvider =
                    OwsExtendedCapabilitiesRepository.getInstance().getExtendedCapabilitiesProvider(request);
            if (extendedCapabilitiesProvider != null && extendedCapabilitiesProvider.hasExtendedCapabilitiesFor(request)) {
                operationsMetadata.setExtendedCapabilities(extendedCapabilitiesProvider
                        .getOwsExtendedCapabilities(request));
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

    private void getConformance(final FilterCapabilities filterCapabilities) {
        // set Query conformance class
        filterCapabilities.addConformance(new OwsDomainType(ConformanceClassConstraintNames.ImplementsQuery.name(),
                new OwsNoValues(), FALSE));
        // set Ad hoc query conformance class
        filterCapabilities.addConformance(new OwsDomainType(ConformanceClassConstraintNames.ImplementsAdHocQuery
                .name(), new OwsNoValues(), FALSE));
        // set Functions conformance class
        filterCapabilities.addConformance(new OwsDomainType(
                ConformanceClassConstraintNames.ImplementsFunctions.name(), new OwsNoValues(), FALSE));
        // set Resource Identification conformance class
        filterCapabilities.addConformance(new OwsDomainType(ConformanceClassConstraintNames.ImplementsResourceld
                .name(), new OwsNoValues(), FALSE));
        // set Minimum Standard Filter conformance class
        filterCapabilities.addConformance(new OwsDomainType(
                ConformanceClassConstraintNames.ImplementsMinStandardFilter.name(), new OwsNoValues(), FALSE));
        // set Standard Filter conformance class
        filterCapabilities.addConformance(new OwsDomainType(ConformanceClassConstraintNames.ImplementsStandardFilter
                .name(), new OwsNoValues(), FALSE));
        // set Minimum Spatial Filter conformance class
        filterCapabilities.addConformance(new OwsDomainType(ConformanceClassConstraintNames.ImplementsMinSpatialFilter
                .name(), new OwsNoValues(), TRUE));
        // set Spatial Filter conformance class
        filterCapabilities.addConformance(new OwsDomainType(ConformanceClassConstraintNames.ImplementsSpatialFilter
                .name(), new OwsNoValues(), TRUE));
        // set Minimum Temporal Filter conformance class
        filterCapabilities.addConformance(new OwsDomainType(
                ConformanceClassConstraintNames.ImplementsMinTemporalFilter.name(), new OwsNoValues(), TRUE));
        // set Temporal Filter conformance class
        filterCapabilities.addConformance(new OwsDomainType(ConformanceClassConstraintNames.ImplementsTemporalFilter
                .name(), new OwsNoValues(), TRUE));
        // set Version navigation conformance class
        filterCapabilities.addConformance(new OwsDomainType(ConformanceClassConstraintNames.ImplementsVersionNav
                .name(), new OwsNoValues(), FALSE));
        // set Sorting conformance class
        filterCapabilities.addConformance(new OwsDomainType(ConformanceClassConstraintNames.ImplementsSorting.name(),
                new OwsNoValues(), FALSE));
        // set Extended Operators conformance class
        filterCapabilities.addConformance(new OwsDomainType(
                ConformanceClassConstraintNames.ImplementsExtendedOperators.name(), new OwsNoValues(), FALSE));
        // set Minimum XPath conformance class
        filterCapabilities.addConformance(new OwsDomainType(ConformanceClassConstraintNames.ImplementsMinimumXPath
                .name(), new OwsNoValues(), FALSE));
        // set Schema Element Function conformance class
        filterCapabilities.addConformance(new OwsDomainType(
                ConformanceClassConstraintNames.ImplementsSchemaElementFunc.name(), new OwsNoValues(), FALSE));
    }

    /**
     * Get the contents for SOS 1.0.0 capabilities
     *
     * @param version
     *            Requested service version
     * @return Offerings for contents
     *
     *
     * @throws OwsExceptionReport
     *             * If an error occurs
     */
    private List<SosObservationOffering> getContents(SectionSpecificContentObject sectionSpecificContentObject)
            throws OwsExceptionReport {
        String version = sectionSpecificContentObject.getGetCapabilitiesResponse().getVersion();
        final Collection<String> offerings = getCache().getOfferings();
        final List<SosObservationOffering> sosOfferings = new ArrayList<>(offerings.size());
        for (final String offering : offerings) {
            final Collection<String> procedures = getProceduresForOffering(offering, version);
            final SosEnvelope envelopeForOffering = getCache().getEnvelopeForOffering(offering);
            final Set<String> featuresForoffering = getFOI4offering(offering);
            final Collection<String> responseFormats = getResponseFormatForOffering(offering, Sos1Constants.SERVICEVERSION);
            if (checkOfferingValues(procedures, envelopeForOffering, featuresForoffering, responseFormats)) {
                final SosObservationOffering sosObservationOffering = new SosObservationOffering();

                // insert observationTypes
                sosObservationOffering.setObservationTypes(getObservationTypes(offering));

                // only if fois are contained for the offering set the values of
                // the envelope
                sosObservationOffering
                        .setObservedArea(processObservedArea(getCache().getEnvelopeForOffering(offering)));

                // TODO: add intended application
                // xb_oo.addIntendedApplication("");

                // add offering name
                addSosOfferingToObservationOffering(offering, sosObservationOffering,
                        sectionSpecificContentObject.getGetCapabilitiesRequest());

                // set up phenomena
                sosObservationOffering
                        .setObservableProperties(getCache().getObservablePropertiesForOffering(offering));
                sosObservationOffering.setCompositePhenomena(getCache().getCompositePhenomenonsForOffering(offering));
                final Map<String, Collection<String>> phens4CompPhens = new HashMap<>();
                if (getCache().getCompositePhenomenonsForOffering(offering) != null) {
                    for (final String compositePhenomenon : getCache().getCompositePhenomenonsForOffering(offering)) {
                        phens4CompPhens.put(compositePhenomenon, getCache()
                                .getObservablePropertiesForCompositePhenomenon(compositePhenomenon));
                    }
                }
                sosObservationOffering.setPhens4CompPhens(phens4CompPhens);

                // set up time
                setUpTimeForOffering(offering, sosObservationOffering);

                // add feature of interests
                if (getConfigurator().getProfileHandler().getActiveProfile().isListFeatureOfInterestsInOfferings()) {
                    sosObservationOffering.setFeatureOfInterest(getFOI4offering(offering));
                }

                // set procedures
                sosObservationOffering.setProcedures(procedures);

                // insert result models
                final Collection<QName> resultModels =
                        OMHelper.getQNamesForResultModel(getCache().getObservationTypesForOffering(offering));
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

    protected SettingsManager getSettingsManager() throws ConfigurationException {
        return SettingsManager.getInstance();
    }

    private SosEnvelope processObservedArea(SosEnvelope sosEnvelope) throws CodedException {
        // TODO Check transformation
        // if (requestedSrid >= 0 && sosEnvelope.getSrid() != requestedSrid) {
        // SosEnvelope tranformedEnvelope = new SosEnvelope();
        // tranformedEnvelope.setSrid(requestedSrid);
        // tranformedEnvelope.setEnvelope(GeometryHandler.getInstance().transformEnvelope(sosEnvelope.getEnvelope(),
        // sosEnvelope.getSrid(), requestedSrid));
        // return tranformedEnvelope;
        // }
        return sosEnvelope;
    }

    private boolean checkOfferingValues(final Collection<String> procedures, final SosEnvelope envelopeForOffering, final Set<String> featuresForOffering,
            final Collection<String> responseFormats) {
        return CollectionHelper.isNotEmpty(procedures) && SosEnvelope.isNotNullOrEmpty(envelopeForOffering) && CollectionHelper.isNotEmpty(featuresForOffering)
                && CollectionHelper.isNotEmpty(responseFormats) && CollectionHelper.isNotEmpty(procedures);
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
    private List<SosObservationOffering> getContentsForSosV2(SectionSpecificContentObject sectionSpecificContentObject)
            throws OwsExceptionReport {
        String version = Sos2Constants.SERVICEVERSION;
        final Collection<String> offerings = getCache().getOfferings();
        final List<SosObservationOffering> sosOfferings = new ArrayList<SosObservationOffering>(offerings.size());
        final Map<String, List<OfferingExtension>> extensions = getSettingsManager().getActiveOfferingExtensions();

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
                            if (OfferingExtensionRepository.getInstance().hasOfferingExtensionProviderFor(sectionSpecificContentObject.getGetCapabilitiesRequest())) {
                                for (OfferingExtensionProvider provider : OfferingExtensionRepository.getInstance().getOfferingExtensionProvider(sectionSpecificContentObject.getGetCapabilitiesRequest())) {
                                    if (provider != null && provider.hasExtendedOfferingFor(offering)) {
                                        sosObservationOffering.addExtensions(provider.getOfferingExtensions(offering));
                                    }
                                }
                            }
                            if (extensions.containsKey(sosObservationOffering.getOffering().getIdentifier())) {
                                for (OfferingExtension offeringExtension : extensions.get(sosObservationOffering.getOffering().getIdentifier())) {
                                    sosObservationOffering.addExtension(new SwesExtensionImpl<OfferingExtension>().setValue(offeringExtension));
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
            Map<String, List<OfferingExtension>> extensions) throws OwsExceptionReport {
        Map<String, Set<String>> parentChilds = Maps.newHashMap();
        for (String offering : offerings) {
            if (!getCache().hasParentOfferings(offering)) {
                parentChilds.put(offering, getCache().getChildOfferings(offering, true, false));
            }
        }
        final List<SosObservationOffering> sosOfferings = new ArrayList<SosObservationOffering>(parentChilds.size());
        for (Entry<String, Set<String>> entry : parentChilds.entrySet()) {
            final Collection<String> observationTypes = getObservationTypes(entry);
            if (CollectionHelper.isNotEmpty(observationTypes)) {
                Collection<String> procedures = getProceduresForOffering(entry, version);
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
                    if (OfferingExtensionRepository.getInstance().hasOfferingExtensionProviderFor(sectionSpecificContentObject.getGetCapabilitiesRequest())) {
                        for (OfferingExtensionProvider provider : OfferingExtensionRepository.getInstance().getOfferingExtensionProvider(sectionSpecificContentObject.getGetCapabilitiesRequest())) {
                            if (provider != null && provider.hasExtendedOfferingFor(entry.getKey())) {
                                sosObservationOffering.addExtensions(provider.getOfferingExtensions(entry.getKey()));
                            }
                        }
                    }
                    if (extensions.containsKey(sosObservationOffering.getOffering().getIdentifier())) {
                        for (OfferingExtension offeringExtension : extensions.get(sosObservationOffering.getOffering().getIdentifier())) {
                            sosObservationOffering.addExtension(new SwesExtensionImpl<OfferingExtension>().setValue(offeringExtension));
                        }
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
            GetCapabilitiesRequest request) {
        SosOffering sosOffering = new SosOffering(offering, false);
        sosObservationOffering.setOffering(sosOffering);
        // add offering name
        I18NHelper.addOfferingNames(sosOffering, request);
        // add offering description
        I18NHelper.addOfferingDescription(sosOffering, request);
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
        final List<QName> operands = new LinkedList<QName>();
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
        final SetMultiMap<SpatialOperator, QName> ops = MultiMaps.newSetMultiMap(SpatialOperator.class);
        if (version.equals(Sos2Constants.SERVICEVERSION)) {
            ops.add(SpatialOperator.BBOX, GmlConstants.QN_ENVELOPE_32);
        } else if (version.equals(Sos1Constants.SERVICEVERSION)) {
            ops.add(SpatialOperator.BBOX, GmlConstants.QN_ENVELOPE);
            // set Contains
            ops.add(SpatialOperator.Contains, GmlConstants.QN_POINT);
            ops.add(SpatialOperator.Contains, GmlConstants.QN_LINESTRING);
            ops.add(SpatialOperator.Contains, GmlConstants.QN_POLYGON);
            // set Intersects
            ops.add(SpatialOperator.Intersects, GmlConstants.QN_POINT);
            ops.add(SpatialOperator.Intersects, GmlConstants.QN_LINESTRING);
            ops.add(SpatialOperator.Intersects, GmlConstants.QN_POLYGON);
            // set Overlaps
            ops.add(SpatialOperator.Overlaps, GmlConstants.QN_POINT);
            ops.add(SpatialOperator.Overlaps, GmlConstants.QN_LINESTRING);
            ops.add(SpatialOperator.Overlaps, GmlConstants.QN_POLYGON);
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
    private void getTemporalFilterCapabilities(final FilterCapabilities filterCapabilities, final String version) {

        // set TemporalOperands
        final List<QName> operands = new ArrayList<QName>(2);
        if (version.equals(Sos2Constants.SERVICEVERSION)) {
            operands.add(GmlConstants.QN_TIME_PERIOD_32);
            operands.add(GmlConstants.QN_TIME_INSTANT_32);
        } else if (version.equals(Sos1Constants.SERVICEVERSION)) {
            operands.add(GmlConstants.QN_TIME_PERIOD);
            operands.add(GmlConstants.QN_TIME_INSTANT);
        }

        filterCapabilities.setTemporalOperands(operands);

        // set TemporalOperators
        final SetMultiMap<TimeOperator, QName> ops = MultiMaps.newSetMultiMap(TimeOperator.class);
        if (version.equals(Sos2Constants.SERVICEVERSION)) {
            for (final TimeOperator op : TimeOperator.values()) {
                ops.add(op, GmlConstants.QN_TIME_INSTANT_32);
                ops.add(op, GmlConstants.QN_TIME_PERIOD_32);
            }
        } else if (version.equals(Sos1Constants.SERVICEVERSION)) {
            for (final TimeOperator op : TimeOperator.values()) {
                ops.add(op, GmlConstants.QN_TIME_INSTANT);
                ops.add(op, GmlConstants.QN_TIME_PERIOD);
            }
        }
        filterCapabilities.setTempporalOperators(ops);
    }

    /**
     * Set ScalarFilterCapabilities to FilterCapabilities
     *
     * @param filterCapabilities
     *            FilterCapabilities
     */
    private void getScalarFilterCapabilities(final FilterCapabilities filterCapabilities, String version) {
        // TODO PropertyIsNil, PropertyIsNull? better:
        // filterCapabilities.setComparisonOperators(Arrays.asList(ComparisonOperator.values()));
        final List<ComparisonOperator> comparisonOperators = new ArrayList<ComparisonOperator>(8);
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
     * @param offering
     *            Offering identifier
     * @return FOI identifiers
     *
     *
     * @throws OwsExceptionReport
     *             * If an error occurs
     */
    private Set<String> getFOI4offering(final String offering) throws OwsExceptionReport {
        final Set<String> featureIDs = new HashSet<String>(0);
        final Set<String> features = getConfigurator().getCache().getFeaturesOfInterestForOffering(offering);
        if (!getConfigurator().getProfileHandler().getActiveProfile().isListFeatureOfInterestsInOfferings()
                || features == null) {
            featureIDs.add(OGCConstants.UNKNOWN);
        } else {
            featureIDs.addAll(features);
        }
        return featureIDs;
    }

    private Collection<String> getObservationTypes(Entry<String, Set<String>> entry) {
        final Set<String> observationTypes = Sets.newHashSet();
        if (!entry.getValue().isEmpty()) {
            for (String offering : entry.getValue()) {
                observationTypes.addAll(getObservationTypes(offering));
            }
        } else {
            observationTypes.addAll(getObservationTypes(entry.getKey()));
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
    protected Set<String> getExtensionSections(final String service, final String version) throws OwsExceptionReport {
        final Collection<CapabilitiesExtension> extensions = getAndMergeExtensions(service, version);
        final HashSet<String> sections = new HashSet<String>(extensions.size());
        for (final CapabilitiesExtension e : extensions) {
            sections.add(e.getSectionName());
        }
        return sections;
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
                CapabilitiesExtensionRepository.getInstance().getCapabilitiesExtensionProvider(service, version);
        final List<CapabilitiesExtension> extensions = Lists.newLinkedList();
        if (CollectionHelper.isNotEmpty(capabilitiesExtensionProviders)) {
            final HashMap<String, MergableExtension> map =
                    new HashMap<String, MergableExtension>(capabilitiesExtensionProviders.size());
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
        extensions.addAll(getSettingsManager().getActiveCapabilitiesExtensions().values());
        return extensions;
    }

    private Collection<CapabilitiesExtension> getExtensions(final Set<String> requestedExtensionSections,
            final String service, final String version) throws OwsExceptionReport {
        final List<CapabilitiesExtension> extensions = getAndMergeExtensions(service, version);
        final List<CapabilitiesExtension> filtered =
                new ArrayList<CapabilitiesExtension>(requestedExtensionSections.size());
        for (final CapabilitiesExtension e : extensions) {
            if (requestedExtensionSections.contains(e.getSectionName())) {
                filtered.add(e);
            }
        }
        return filtered;
    }

    protected void setUpPhenomenaForOffering(final String offering, final String procedure,
            final SosObservationOffering sosOffering) {
//        final Collection<String> phenomenons = new LinkedList<>();
//        final Collection<String> observablePropertiesForOffering =
//                getCache().getObservablePropertiesForOffering(offering);
//        for (final String observableProperty : observablePropertiesForOffering) {
//            final Set<String> proceduresForObservableProperty =
//                    getCache().getProceduresForObservableProperty(observableProperty);
//            if (proceduresForObservableProperty.contains(procedure)
//                    || isHiddenChildProcedureObservableProperty(offering, proceduresForObservableProperty)) {
//                phenomenons.add(observableProperty);
//            }
//        }
//        sosOffering.setObservableProperties(phenomenons);
//        sosOffering.setCompositePhenomena(getCache().getCompositePhenomenonsForOffering(offering));
//
//        final Collection<String> compositePhenomenonsForOffering =
//                getCache().getCompositePhenomenonsForOffering(offering);
//
//        if (compositePhenomenonsForOffering != null) {
//            final Map<String, Collection<String>> phens4CompPhens = new HashMap<>(compositePhenomenonsForOffering.size());
//            for (final String compositePhenomenon : compositePhenomenonsForOffering) {
//                final Collection<String> phenomenonsForComposite =
//                        getCache().getObservablePropertiesForCompositePhenomenon(compositePhenomenon);
//                phens4CompPhens.put(compositePhenomenon, phenomenonsForComposite);
//            }
//            sosOffering.setPhens4CompPhens(phens4CompPhens);
//        } else {
//            sosOffering.setPhens4CompPhens(Collections.<String, Collection<String>> emptyMap());
//        }
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

            final Collection<String> compositePhenomenonsForOffering =
                    getCache().getCompositePhenomenonsForOffering(offering);

            if (compositePhenomenonsForOffering != null) {
                final Map<String, Collection<String>> phens4CompPhens = new HashMap<>(compositePhenomenonsForOffering.size());
                for (final String compositePhenomenon : compositePhenomenonsForOffering) {
                    final Collection<String> phenomenonsForComposite =
                            getCache().getObservablePropertiesForCompositePhenomenon(compositePhenomenon);
                    phens4CompPhens.put(compositePhenomenon, phenomenonsForComposite);
                }
                sosOffering.setPhens4CompPhens(phens4CompPhens);
            } else {
                sosOffering.setPhens4CompPhens(Collections.<String, Collection<String>> emptyMap());
            }
        }
    }

    private boolean isHiddenChildProcedureObservableProperty(final String offering,
            final Set<String> proceduresForObservableProperty) {
        for (final String hiddenProcedure : getCache().getHiddenChildProceduresForOffering(offering)) {
            if (proceduresForObservableProperty.contains(hiddenProcedure)) {
                return true;
            }
        }
        return false;
    }

    protected void setUpRelatedFeaturesForOffering(final String offering, final String version,
            final SosObservationOffering sosOffering) throws OwsExceptionReport {
        setUpRelatedFeaturesForOffering(Sets.newHashSet(offering), version, sosOffering);
    }

    protected void setUpRelatedFeaturesForOffering(Set<String> offerings, String version,
            SosObservationOffering sosOffering) {
        final Map<String, Set<String>> relatedFeatures = Maps.newHashMap();
        for (String offering : offerings) {
            final Set<String> relatedFeaturesForThisOffering = getCache().getRelatedFeaturesForOffering(offering);
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
        }
        sosOffering.setRelatedFeatures(relatedFeatures);
        
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

    protected void setUpFeatureOfInterestTypesForOffering(final String offering,
            final SosObservationOffering sosOffering) {
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
        sosOffering.setResponseFormats(getResponseFormatForOffering(sosOffering.getOffering().getIdentifier(), version));
    }
    
    protected Set<String> getResponseFormatForOffering(String offering, String version) {
        Set<String> responseFormats = Sets.newHashSet();
        for (String observationType : getCache().getAllObservationTypesForOffering(offering)) {
            Set<String> responseFormatsForObservationType = CodingRepository.getInstance().getResponseFormatsForObservationType(observationType, SosConstants.SOS, version);
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
            boolean isMediaType = MediaType.check(format);
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
        Set<String> formats = Sets.newHashSet();
        for (String procedure : sosOffering.getProcedures()) {
            formats.addAll(getCache().getProcedureDescriptionFormatsForProcedure(procedure));
        }
        if (Sos1Constants.SERVICEVERSION.equals(version)) {
            sosOffering.setProcedureDescriptionFormat(checkForMimeType(formats, true));
        } else if (Sos2Constants.SERVICEVERSION.equals(version)) {
            sosOffering.setProcedureDescriptionFormat(checkForMimeType(formats, false));
        }
    }

    private SosEnvelope getObservedArea(Set<String> offerings) throws CodedException {
        SosEnvelope envelope = new SosEnvelope();
        for (String offering : offerings) {
            envelope.expandToInclude(getObservedArea(offering));
        }
        return envelope;
    }
    
    private SosEnvelope getObservedArea(String offering) throws CodedException {
        if (getCache().hasSpatialFilteringProfileEnvelopeForOffering(offering)) {
            return processObservedArea(getCache()
                    .getSpatialFilteringProfileEnvelopeForOffering(offering));
        } else {
            return processObservedArea(getCache()
                    .getEnvelopeForOffering(offering));
        }
    }

    private Collection<String> getProceduresForOffering(Entry<String, Set<String>> entry, String version) throws OwsExceptionReport {
        final Collection<String> procedures = Sets.newHashSet();
        if (!entry.getValue().isEmpty()) {
            for (String offering : entry.getValue()) {
                procedures.addAll(getProceduresForOffering(offering, version));
            }
        } else {
            procedures.addAll(getProceduresForOffering(entry.getKey(), version));
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

    private boolean isVersionSos2(final GetCapabilitiesResponse response) {
        return response.getVersion().equals(Sos2Constants.SERVICEVERSION);
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
    
    private String getGetDataAvailabilityUrl() {
        return new StringBuilder(getBaseGetUrl()).append(getRequest("GetDataAvailability")).toString();
    }
    
    private String getBaseGetUrl() {
        final StringBuilder url = new StringBuilder();
        // service URL
        url.append(getServiceConfiguration().getServiceURL());
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

    private String getRequest(String requestName) {
        return new StringBuilder().append('&').append(RequestParams.request.name()).append('=').append(requestName)
                .toString();
    }
    
    private ServiceConfiguration getServiceConfiguration() {
        return ServiceConfiguration.getInstance();
    }

    @Override
    public String getDatasourceDaoIdentifier() {
        return IDEPENDET_IDENTIFIER;
    }
    
    @Override
    public boolean isSupported() {
        return true;
    }
}
