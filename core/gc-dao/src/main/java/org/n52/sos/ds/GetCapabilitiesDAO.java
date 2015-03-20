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

import javax.xml.namespace.QName;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.n52.sos.binding.Binding;
import org.n52.sos.binding.BindingRepository;
import org.n52.sos.coding.CodingRepository;
import org.n52.sos.config.SettingsManager;
import org.n52.sos.decode.Decoder;
import org.n52.sos.encode.Encoder;
import org.n52.sos.exception.CodedException;
import org.n52.sos.exception.ConfigurationException;
import org.n52.sos.exception.ows.InvalidParameterValueException;
import org.n52.sos.exception.ows.NoApplicableCodeException;
import org.n52.sos.ogc.OGCConstants;
import org.n52.sos.ogc.filter.FilterCapabilities;
import org.n52.sos.ogc.filter.FilterConstants.ComparisonOperator;
import org.n52.sos.ogc.filter.FilterConstants.ConformanceClassConstraintNames;
import org.n52.sos.ogc.filter.FilterConstants.SpatialOperator;
import org.n52.sos.ogc.filter.FilterConstants.TimeOperator;
import org.n52.sos.ogc.gml.GmlConstants;
import org.n52.sos.ogc.gml.time.TimePeriod;
import org.n52.sos.ogc.ows.MergableExtension;
import org.n52.sos.ogc.ows.OWSConstants;
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
import org.n52.sos.service.operator.ServiceOperatorRepository;
import org.n52.sos.util.CollectionHelper;
import org.n52.sos.util.GeometryHandler;
import org.n52.sos.util.I18NHelper;
import org.n52.sos.i18n.LocaleHelper;
import org.n52.sos.util.MultiMaps;
import org.n52.sos.util.OMHelper;
import org.n52.sos.util.SetMultiMap;

import com.google.common.collect.Collections2;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

/**
 * Implementation of the interface IGetCapabilitiesDAO
 *
 * @since 4.0.0
 */
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
        String verion = sectionSpecificContentObject.getGetCapabilitiesResponse().getVersion();
        String service = sectionSpecificContentObject.getGetCapabilitiesResponse().getService();
        if (isServiceIdentificationSectionRequested(sectionSpecificContentObject.getRequestedSections())) {
            sectionSpecificContentObject.getSosCapabilities().setServiceIdentification(
                    getServiceIdentification(request, verion));
        }
        if (isServiceProviderSectionRequested(sectionSpecificContentObject.getRequestedSections())) {
            sectionSpecificContentObject.getSosCapabilities().setServiceProvider(
                    getConfigurator().getServiceProvider());
        }
        if (isOperationsMetadataSectionRequested(sectionSpecificContentObject.getRequestedSections())) {
            sectionSpecificContentObject.getSosCapabilities().setOperationsMetadata(
                    getOperationsMetadataForOperations(request, service, verion));
        }
        if (isFilterCapabilitiesSectionRequested(sectionSpecificContentObject.getRequestedSections())) {
            sectionSpecificContentObject.getSosCapabilities().setFilterCapabilities(getFilterCapabilities(verion));
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
                sectionSpecificContentObject.getSosCapabilities().setExensions(getAndMergeExtensions(service, verion));
            } else if (!sectionSpecificContentObject.getRequestedExtensionSesctions().isEmpty()) {
                sectionSpecificContentObject.getSosCapabilities().setExensions(
                        getExtensions(sectionSpecificContentObject.getRequestedExtensionSesctions(), service, verion));
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
        // !!! Modify methods addicted to your implementation !!!
        if (version.equals(Sos1Constants.SERVICEVERSION)) {
            getScalarFilterCapabilities(filterCapabilities);
        }
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
        final List<SosObservationOffering> sosOfferings = new ArrayList<SosObservationOffering>(offerings.size());
        for (final String offering : offerings) {
            final Collection<String> procedures = getProceduresForOffering(offering, version);
            final SosEnvelope envelopeForOffering = getCache().getEnvelopeForOffering(offering);
            final Set<String> featuresForoffering = getFOI4offering(offering);
            final Collection<String> responseFormats =
                    CodingRepository.getInstance().getSupportedResponseFormats(SosConstants.SOS,
                            Sos1Constants.SERVICEVERSION);
            if (checkOfferingValues(envelopeForOffering, featuresForoffering, responseFormats)) {
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
                final Map<String, Collection<String>> phens4CompPhens = new HashMap<String, Collection<String>>();
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

    private boolean checkOfferingValues(final SosEnvelope envelopeForOffering, final Set<String> featuresForOffering,
            final Collection<String> responseFormats) {
        return SosEnvelope.isNotNullOrEmpty(envelopeForOffering) && CollectionHelper.isNotEmpty(featuresForOffering)
                && CollectionHelper.isNotEmpty(responseFormats);
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
    private List<SosObservationOffering> getContentsForSosV2(SectionSpecificContentObject sectionSpecificContentObject)
            throws OwsExceptionReport {
        String version = sectionSpecificContentObject.getGetCapabilitiesResponse().getVersion();
        final Collection<String> offerings = getCache().getOfferings();
        final List<SosObservationOffering> sosOfferings = new ArrayList<SosObservationOffering>(offerings.size());
        final Map<String, List<OfferingExtension>> extensions = getSettingsManager().getActiveOfferingExtensions();

        if (CollectionHelper.isEmpty(offerings)) {
            // Set empty offering to add empty Contents section to Capabilities
            sosOfferings.add(new SosObservationOffering());
        } else {
            for (final String offering : offerings) {
                final Collection<String> procedures = getProceduresForOffering(offering, version);
                final Collection<String> observationTypes = getObservationTypes(offering);
                if (observationTypes != null && !observationTypes.isEmpty()) {
                    // FIXME why a loop? We are in SOS 2.0 context -> offering 1
                    // <-> 1 procedure!
                    for (final String procedure : procedures) {

                        final SosObservationOffering sosObservationOffering = new SosObservationOffering();

                        // insert observationTypes
                        sosObservationOffering.setObservationTypes(observationTypes);

                        if (getCache().hasSpatialFilteringProfileEnvelopeForOffering(offering)) {
                            sosObservationOffering.setObservedArea(processObservedArea(getCache()
                                    .getSpatialFilteringProfileEnvelopeForOffering(offering)));
                        } else {
                            sosObservationOffering.setObservedArea(processObservedArea(getCache()
                                    .getEnvelopeForOffering(offering)));
                        }

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
                        setUpRelatedFeaturesForOffering(offering, version, procedure, sosObservationOffering);
                        setUpFeatureOfInterestTypesForOffering(offering, sosObservationOffering);
                        setUpProcedureDescriptionFormatForOffering(sosObservationOffering, version);
                        setUpResponseFormatForOffering(version, sosObservationOffering);


                        sosOfferings.add(sosObservationOffering);
                    }
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
    private void getScalarFilterCapabilities(final FilterCapabilities filterCapabilities) {
        // TODO PropertyIsNil, PropertyIsNull? better:
        // filterCapabilities.setComparisonOperators(Arrays.asList(ComparisonOperator.values()));
        final List<ComparisonOperator> comparisonOperators = new ArrayList<ComparisonOperator>(8);
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

    private Collection<String> getObservationTypes(final String offering) {
        final Collection<String> allObservationTypes = getCache().getObservationTypesForOffering(offering);
        final List<String> observationTypes = new ArrayList<String>(allObservationTypes.size());

        for (final String observationType : allObservationTypes) {
            if (!observationType.equals(SosConstants.NOT_DEFINED)) {
                observationTypes.add(observationType);
            }
        }
        if (observationTypes.isEmpty()) {
            for (final String observationType : getCache().getAllowedObservationTypesForOffering(offering)) {
                if (!observationType.equals(SosConstants.NOT_DEFINED)) {
                    observationTypes.add(observationType);
                }
            }
        }
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
        final Collection<String> phenomenons = new LinkedList<String>();
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
            final Map<String, Collection<String>> phens4CompPhens =
                    new HashMap<String, Collection<String>>(compositePhenomenonsForOffering.size());
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
            final String procedure, final SosObservationOffering sosOffering) throws OwsExceptionReport {
        final Map<String, Set<String>> relatedFeatures = Maps.newHashMap();
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
        sosOffering.setRelatedFeatures(relatedFeatures);
    }

    protected void setUpTimeForOffering(final String offering, final SosObservationOffering sosOffering) {
        sosOffering.setPhenomenonTime(new TimePeriod(getCache().getMinPhenomenonTimeForOffering(offering), getCache()
                .getMaxPhenomenonTimeForOffering(offering)));
        sosOffering.setResultTime(new TimePeriod(getCache().getMinResultTimeForOffering(offering), getCache()
                .getMaxResultTimeForOffering(offering)));
    }

    protected void setUpFeatureOfInterestTypesForOffering(final String offering,
            final SosObservationOffering sosOffering) {
        sosOffering.setFeatureOfInterestTypes(getCache().getAllowedFeatureOfInterestTypesForOffering(offering));
    }

    protected void setUpResponseFormatForOffering(final String version, final SosObservationOffering sosOffering) {
        // initialize as new HashSet so that collection is modifiable
        final Collection<String> responseFormats =
                new HashSet<String>(CodingRepository.getInstance().getSupportedResponseFormats(SosConstants.SOS,
                        version));
        sosOffering.setResponseFormats(responseFormats);
        // TODO set as property
    }

    protected void setUpProcedureDescriptionFormatForOffering(final SosObservationOffering sosOffering,
            final String version) {
        // TODO: set procDescFormat <-- what is required here?
        sosOffering.setProcedureDescriptionFormat(CodingRepository.getInstance()
                .getSupportedProcedureDescriptionFormats(SosConstants.SOS, version));
    }

    private Collection<String> getProceduresForOffering(final String offering, final String version)
            throws OwsExceptionReport {
        final Collection<String> procedures = Sets.newHashSet(getCache().getProceduresForOffering(offering));
        if (version.equals(Sos1Constants.SERVICEVERSION)) {
            procedures.addAll(getCache().getHiddenChildProceduresForOffering(offering));
        }
        if (procedures.isEmpty()) {
            throw new NoApplicableCodeException()
                    .withMessage(
                            "No procedures are contained in the database for the offering '%s'! Please contact the admin of this SOS.",
                            offering);
        }
        return procedures;
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

    @Override
    public String getDatasourceDaoIdentifier() {
        return IDEPENDET_IDENTIFIER;
    }
}
