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
package org.n52.sos.request.operator;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.n52.faroe.annotation.Configurable;
import org.n52.faroe.annotation.Setting;
import org.n52.iceland.cache.ContentCacheController;
import org.n52.iceland.convert.RequestResponseModifier;
import org.n52.iceland.convert.RequestResponseModifierRepository;
import org.n52.iceland.event.events.RequestEvent;
import org.n52.iceland.event.events.ResponseEvent;
import org.n52.iceland.exception.ows.concrete.InvalidServiceParameterException;
import org.n52.iceland.exception.ows.concrete.MissingValueReferenceException;
import org.n52.iceland.request.handler.OperationHandler;
import org.n52.iceland.request.handler.OperationHandlerRepository;
import org.n52.iceland.request.operator.RequestOperator;
import org.n52.iceland.request.operator.RequestOperatorKey;
import org.n52.iceland.service.operator.ServiceOperatorRepository;
import org.n52.janmayen.event.EventBus;
import org.n52.shetland.ogc.filter.SpatialFilter;
import org.n52.shetland.ogc.filter.TemporalFilter;
import org.n52.shetland.ogc.gml.time.TimePeriod;
import org.n52.shetland.ogc.ows.OWSConstants;
import org.n52.shetland.ogc.ows.OwsOperation;
import org.n52.shetland.ogc.ows.exception.CodedException;
import org.n52.shetland.ogc.ows.exception.CompositeOwsException;
import org.n52.shetland.ogc.ows.exception.InvalidParameterValueException;
import org.n52.shetland.ogc.ows.exception.MissingParameterValueException;
import org.n52.shetland.ogc.ows.exception.MissingServiceParameterException;
import org.n52.shetland.ogc.ows.exception.MissingVersionParameterException;
import org.n52.shetland.ogc.ows.exception.OperationNotSupportedException;
import org.n52.shetland.ogc.ows.exception.OwsExceptionReport;
import org.n52.shetland.ogc.ows.exception.VersionNegotiationFailedException;
import org.n52.shetland.ogc.ows.service.OwsServiceRequest;
import org.n52.shetland.ogc.ows.service.OwsServiceResponse;
import org.n52.shetland.ogc.sos.Sos2Constants;
import org.n52.shetland.ogc.sos.SosConstants;
import org.n52.shetland.ogc.sos.request.AbstractObservationRequest;
import org.n52.shetland.ogc.sos.response.AbstractObservationResponse;
import org.n52.shetland.ogc.swes.SwesExtensions;
import org.n52.shetland.util.CollectionHelper;
import org.n52.sos.cache.SosContentCache;
import org.n52.sos.exception.ows.concrete.InvalidValueReferenceException;
import org.n52.sos.exception.ows.concrete.MissingProcedureParameterException;
import org.n52.sos.service.profile.Profile;
import org.n52.sos.service.profile.ProfileHandler;

import com.google.common.base.Joiner;
import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

/**
 * @param <D>
 *            the OperationDAO of this operator
 * @param <Q>
 *            the request type
 * @param <A>
 *            the response type
 * @author <a href="mailto:c.autermann@52north.org">Christian Autermann</a>
 *
 * @since 4.0.0
 */
@Configurable
public abstract class AbstractRequestOperator<D extends OperationHandler, Q extends OwsServiceRequest, A extends OwsServiceResponse>
        implements RequestOperator {
    private static final Logger LOGGER = LoggerFactory.getLogger(AbstractRequestOperator.class);

    public static final String EXPOSE_CHILD_OBSERVABLE_PROPERTIES = "service.exposeChildObservableProperties";

    // TODO make supported ValueReferences dynamic
    private static final Set<String> validTemporalFilterValueReferences = Sets.newHashSet(
            "phenomenonTime",
            "om:phenomenonTime",
            "resultTime",
            "om:resultTime",
            "validTime",
            "om:validTime");

    private final RequestOperatorKey requestOperatorKey;
    private final Class<Q> requestType;
    private OperationHandlerRepository operationHandlerRepository;
    private RequestResponseModifierRepository requestResponseModifierRepository;
    private ContentCacheController contentCacheController;
    private ProfileHandler profileHandler;
    private ServiceOperatorRepository serviceOperatorRepository;
    private EventBus serviceEventBus;
    private boolean includeChildObservableProperties;


    private final String service;

    public AbstractRequestOperator(String service,
                                   String version,
                                   String operationName,
                                   Class<Q> requestType) {
        this(service, version, operationName, true, requestType);
    }

    public AbstractRequestOperator(String service,
                                   String version,
                                   String operationName,
                                   boolean defaultActive,
                                   Class<Q> requestType) {
        this.requestOperatorKey = new RequestOperatorKey(service, version, operationName, defaultActive);
        this.service = service;
        this.requestType = requestType;
        LOGGER.info("{} initialized successfully!", getClass().getSimpleName());
    }

    @Inject
    public void setOperationHandlerRepository(OperationHandlerRepository repo) {
        this.operationHandlerRepository = repo;
    }

    public OperationHandlerRepository getOperationHandlerRepository() {
        return operationHandlerRepository;
    }

    @Inject
    public void setRequestResponseModifierRepository(RequestResponseModifierRepository repo) {
        this.requestResponseModifierRepository = repo;
    }

    public RequestResponseModifierRepository getRequestResponseModifierRepository() {
        return requestResponseModifierRepository;
    }

    @Inject
    public void setContentCacheController(ContentCacheController ctrl) {
        this.contentCacheController = ctrl;
    }

    public ContentCacheController getContentCacheController() {
        return contentCacheController;
    }

    @Inject
    public void setProfileHandler(ProfileHandler profileHandler) {
        this.profileHandler = profileHandler;
    }

    public ProfileHandler getProfileHandler() {
        return profileHandler;
    }

    @Inject
    public void setServiceOperatorRepository(ServiceOperatorRepository repo) {
        this.serviceOperatorRepository = repo;
    }

    public ServiceOperatorRepository getServiceOperatorRepository() {
        return serviceOperatorRepository;
    }

    @Inject
    public void setServiceEventBus(EventBus serviceEventBus) {
        this.serviceEventBus = serviceEventBus;
    }

    public EventBus getServiceEventBus() {
        return serviceEventBus;
    }

    /**
     * @deprecated Use {@link #getOperationHandler()}
     */
    @Deprecated
    protected D getDao() {
        return getOperationHandler();
    }

    @Override
    public boolean isSupported() {
        return getOptionalOperationHandler().isPresent() && getOptionalOperationHandler().get().isSupported();
    }

    protected D getOperationHandler() {
        return getOptionalOperationHandler().orElseThrow(() ->
                new NullPointerException(String.format("OperationDAO for Operation %s has no implementation!",
                                                       requestOperatorKey.getOperationName())));
    }

    protected Optional<D> getOptionalOperationHandler() {
        String service = this.requestOperatorKey.getService();
        String operationName = this.requestOperatorKey.getOperationName();
        return getOptionalOperationHandler(service, operationName);
    }

    @SuppressWarnings("unchecked")
    protected Optional<D> getOptionalOperationHandler(String service, String operationName) {
        return Optional.ofNullable(this.operationHandlerRepository.getOperationHandler(service, operationName))
                .map(x -> (D) x);
    }

    @Override
    public OwsOperation getOperationMetadata(String service, String version) throws OwsExceptionReport {
        Optional<D> optionalOperationHandler = getOptionalOperationHandler();
        if (optionalOperationHandler.isPresent()) {
            return optionalOperationHandler.get().getOperationsMetadata(service, version);
        } else {
            return null;
        }
    }

    protected String getOperationName() {
        return this.requestOperatorKey.getOperationName();
    }

    @Override
    public Set<RequestOperatorKey> getKeys() {
        return Collections.singleton(requestOperatorKey);
    }

    @Override
    public OwsServiceResponse receiveRequest(final OwsServiceRequest abstractRequest)
            throws OwsExceptionReport {
        this.serviceEventBus.submit(new RequestEvent(abstractRequest));
        if (requestType.isAssignableFrom(abstractRequest.getClass())) {
            Q request = requestType.cast(abstractRequest);
            preProcessRequest(request);
            checkForModifierAndProcess(request);
            checkParameters(request);
            A response = receive(request);
            this.serviceEventBus.submit(new ResponseEvent(response));
            postProcessResponse(response);
            return checkForModifierAndProcess(request, response);
        } else {
            throw new OperationNotSupportedException(abstractRequest.getOperationName());
        }
    }

    protected void preProcessRequest(Q request) {
        // nothing to do
    }

    protected OwsServiceResponse postProcessResponse(A response) {
        return response;
    }

    private void checkForModifierAndProcess(OwsServiceRequest request) throws OwsExceptionReport {
        if (this.requestResponseModifierRepository.hasRequestResponseModifier(request)) {
            List<RequestResponseModifier> splitter = new ArrayList<>();
            List<RequestResponseModifier> remover = new ArrayList<>();
            List<RequestResponseModifier> defaultMofifier = new ArrayList<>();
            for (RequestResponseModifier modifier : this.requestResponseModifierRepository.getRequestResponseModifier(request)) {
                if (modifier.getFacilitator().isSplitter()) {
                    splitter.add(modifier);
                } else if (modifier.getFacilitator().isAdderRemover()) {
                    remover.add( modifier);
                } else {
                    defaultMofifier.add(modifier);
                }
            }
            // execute adder/remover
            for (RequestResponseModifier modifier : remover) {
                modifier.modifyRequest(request);
            }
            // execute default
            for (RequestResponseModifier modifier : defaultMofifier) {
                modifier.modifyRequest(request);
            }
            // execute splitter
            for (RequestResponseModifier modifier : splitter) {
                modifier.modifyRequest(request);
            }
        }
    }

    private OwsServiceResponse checkForModifierAndProcess(OwsServiceRequest request,
            OwsServiceResponse response) throws OwsExceptionReport {
        if (this.requestResponseModifierRepository.hasRequestResponseModifier(request, response)) {
            List<RequestResponseModifier> defaultModifier = new ArrayList<>();
            List<RequestResponseModifier> remover = new ArrayList<>();
            List<RequestResponseModifier> merger = new ArrayList<>();
            for (RequestResponseModifier modifier : this.requestResponseModifierRepository.getRequestResponseModifier(request, response)) {
                if (modifier.getFacilitator().isMerger()) {
                    merger.add(modifier);
                } else if (modifier.getFacilitator().isAdderRemover()) {
                    remover.add(modifier);
                } else {
                    defaultModifier.add(modifier);
                }

            }
            // execute merger
            for (RequestResponseModifier modifier : merger) {
                modifier.modifyResponse(request, response);
            }
            // execute default
            for (RequestResponseModifier modifier : defaultModifier) {
                modifier.modifyResponse(request, response);
            }

            // execute adder/remover
            for (RequestResponseModifier modifier : remover) {
                modifier.modifyResponse(request, response);
            }
            return response;
        }
        return response;
    }

    protected abstract A receive(Q request) throws OwsExceptionReport;

    protected abstract void checkParameters(Q request) throws OwsExceptionReport;

    protected SosContentCache getCache() {
        return (SosContentCache) this.contentCacheController.getCache();
    }

    protected Profile getActiveProfile() {
        return this.profileHandler.getActiveProfile();
    }

    /**
     * method checks whether this SOS supports the requested versions
     *
     * @param service
     *            requested service
     *
     * @param versions
     *            the requested versions of the SOS
     *
     * @throws OwsExceptionReport
     *             * if this SOS does not support the requested versions
     */
    protected List<String> checkAcceptedVersionsParameter(String service, List<String> versions)
            throws OwsExceptionReport {
        if (versions != null) {
            Set<String> supportedVersions = this.serviceOperatorRepository
                    .getSupportedVersions(service);
            List<String> validVersions = versions.stream()
                    .filter(supportedVersions::contains)
                    .collect(Collectors.toList());

            if (validVersions.isEmpty()) {
                throw new VersionNegotiationFailedException().at(org.n52.shetland.ogc.ows.OWSConstants.GetCapabilitiesParams.AcceptVersions)
                        .withMessage("The parameter '%s' does not contain a supported Service version!",
                                org.n52.shetland.ogc.ows.OWSConstants.GetCapabilitiesParams.AcceptVersions.name());
            }
            return validVersions;
        } else {
            throw new MissingParameterValueException(org.n52.shetland.ogc.ows.OWSConstants.GetCapabilitiesParams.AcceptVersions);
        }
    }

    /**
     * method checks whether this SOS supports the single requested version
     *
     * @param request
     *            the request
     *
     *
     * @throws OwsExceptionReport
     *             * if this SOS does not support the requested versions
     */
    protected void checkSingleVersionParameter(final OwsServiceRequest request) throws OwsExceptionReport {

        // if version is incorrect, throw exception
        if (request.getVersion() == null) {
            throw new MissingVersionParameterException();
        }
        if ( !this.serviceOperatorRepository.isVersionSupported(request.getService(), request.getVersion())) {
            throw new InvalidParameterValueException().at(OWSConstants.RequestParams.version).withMessage(
                    "The parameter '%s' does not contain version(s) supported by this Service: '%s'!",
                    OWSConstants.RequestParams.version.name(),
                    Joiner.on(", ").join(this.serviceOperatorRepository.getSupportedVersions(request.getService())));
        }
    }

    /**
     * method checks, whether the passed string containing the requested
     * versions of the SOS contains the versions, the 52n SOS supports
     *
     * @param service
     *            requested service
     * @param versionsString
     *            comma seperated list of requested service versions
     *
     *
     * @throws OwsExceptionReport
     *             * if the versions list is empty or no matching version is *
     *             contained
     */
    protected void checkAcceptedVersionsParameter(final String service, final String versionsString)
            throws OwsExceptionReport {
        // check acceptVersions
        if (versionsString != null && !versionsString.isEmpty()) {
            final String[] versionsArray = versionsString.split(",");
            checkAcceptedVersionsParameter(service, Arrays.asList(versionsArray));
        } else {
            throw new MissingParameterValueException(org.n52.shetland.ogc.ows.OWSConstants.GetCapabilitiesParams.AcceptVersions);
        }
    }

    /**
     * checks whether the required service parameter is correct
     *
     * @param service
     *            service parameter of the request
     *
     *
     * @throws OwsExceptionReport
     *             if service parameter is incorrect
     */
    protected void checkServiceParameter(String service) throws OwsExceptionReport {
        if (service == null || service.equalsIgnoreCase("NOT_SET")) {
            throw new MissingServiceParameterException();
        } else if (!service.equals(this.service)) {
            throw new InvalidServiceParameterException(service);
        }
    }

    /**
     * checks whether the requested sensor ID is valid
     *
     * @param procedureID
     *            the sensor ID which should be checked
     * @param parameterName
     *            the parameter name
     *
     *
     * @throws OwsExceptionReport
     *             * if the value of the sensor ID parameter is incorrect
     */
    protected void checkProcedureID(final String procedureID, final String parameterName) throws OwsExceptionReport {
        if (Strings.isNullOrEmpty(procedureID)) {
            throw new MissingProcedureParameterException();
        } else if (!getCache().hasProcedure(procedureID)) {
            throw new InvalidParameterValueException(parameterName, procedureID);
        }
    }

    protected void checkTransactionalProcedureID(final String procedureID, final String parameterName) throws OwsExceptionReport {
        if (Strings.isNullOrEmpty(procedureID)) {
            throw new MissingProcedureParameterException();
        } else if (!getCache().hasTransactionalObservationProcedure(procedureID)) {
            throw new InvalidParameterValueException(parameterName, procedureID);
        }
    }

    protected void checkQueryableProcedureID(final String procedureID, final String parameterName) throws OwsExceptionReport {
        if (Strings.isNullOrEmpty(procedureID)) {
            throw new MissingProcedureParameterException();
        } else if (!getCache().hasQueryableProcedure(procedureID)) {
            throw new InvalidParameterValueException(parameterName, procedureID);
        }
    }

    /**
     * checks whether the requested sensor ID is valid
     *
     * @param procedureID
     *            the sensor ID which should be checked
     * @param parameterName
     *            the parameter name
     *
     * @throws OwsExceptionReport
     *             * if the value of the sensor ID parameter is incorrect
     */
    protected void checkProcedureID(final String procedureID, final Enum<?> parameterName) throws OwsExceptionReport {
        checkProcedureID(procedureID, parameterName.name());
    }

    protected void checkProcedureIDs(final Collection<String> procedureIDs, final String parameterName)
            throws OwsExceptionReport {
        if (procedureIDs != null) {
            CompositeOwsException exceptions = new CompositeOwsException();
            procedureIDs.forEach(id -> {
                try {
                    checkProcedureID(id, parameterName);
                } catch (OwsExceptionReport owse) {
                    exceptions.add(owse);
                }
            });
            exceptions.throwIfNotEmpty();
        }
    }

    protected void checkTransactionalProcedureIDs(final Collection<String> procedureIDs, final String parameterName)
            throws OwsExceptionReport {
        if (procedureIDs != null) {
            final CompositeOwsException exceptions = new CompositeOwsException();
            procedureIDs.forEach(id -> {
                try {
                    checkTransactionalProcedureID(id, parameterName);
                } catch (final OwsExceptionReport owse) {
                    exceptions.add(owse);
                }
            });
            exceptions.throwIfNotEmpty();
        }
    }

    protected void checkQueryableProcedureIDs(final Collection<String> procedureIDs, final String parameterName)
            throws OwsExceptionReport {
        if (procedureIDs != null) {
            final CompositeOwsException exceptions = new CompositeOwsException();
            procedureIDs.forEach(id -> {
                try {
                    checkQueryableProcedureID(id, parameterName);
                } catch (final OwsExceptionReport owse) {
                    exceptions.add(owse);
                }
            });
            exceptions.throwIfNotEmpty();
        }
    }

    protected void checkObservationID(final String observationID, final String parameterName)
            throws OwsExceptionReport {
        if (observationID == null || observationID.isEmpty()) {
            throw new MissingParameterValueException(parameterName);
            // } else if (!getCache().hasObservationIdentifier(observationID)) {
            // throw new InvalidParameterValueException(parameterName,
            // observationID);
        }
    }

    protected void checkObservationIDs(Collection<String> observationIDs, String parameterName)
            throws OwsExceptionReport {
        if (CollectionHelper.isEmpty(observationIDs)) {
            throw new MissingParameterValueException(parameterName);
        }
        if (observationIDs != null) {
            CompositeOwsException exceptions = new CompositeOwsException();
            observationIDs.forEach(id -> {
                try {
                    checkObservationID(id, parameterName);
                } catch (final OwsExceptionReport owse) {
                    exceptions.add(owse);
                }
            });
            exceptions.throwIfNotEmpty();
        }
    }

    protected void checkFeatureOfInterestIdentifiers(List<String> featuresOfInterest, String parameterName)
            throws OwsExceptionReport {
        if (featuresOfInterest != null) {
            CompositeOwsException exceptions = new CompositeOwsException();
            featuresOfInterest.forEach(id -> {
                try {
                    checkFeatureOfInterestIdentifier(id, parameterName);
                } catch (OwsExceptionReport e) {
                    exceptions.add(e);
                }
            });
            exceptions.throwIfNotEmpty();
        }
    }

    protected void checkFeatureOfInterestIdentifier(final String featureOfInterest, final String parameterName)
            throws OwsExceptionReport {
        if (featureOfInterest == null || featureOfInterest.isEmpty()) {
            throw new MissingParameterValueException(parameterName);
        }
        if (getCache().hasFeatureOfInterest(featureOfInterest)) {
            return;
        }
        if (getCache().hasRelatedFeature(featureOfInterest) && getCache().isRelatedFeatureSampled(featureOfInterest)) {
            return;
        }
        throw new InvalidParameterValueException(parameterName, featureOfInterest);
    }

    protected void checkObservedProperties(final List<String> observedProperties, final Enum<?> parameterName, boolean insertion)
            throws OwsExceptionReport {
        checkObservedProperties(observedProperties, parameterName.name(), insertion);
    }

    protected void checkObservedProperties(final List<String> observedProperties, final String parameterName, boolean insertion)
            throws OwsExceptionReport {
        if (observedProperties != null) {
            final CompositeOwsException exceptions = new CompositeOwsException();
            observedProperties.forEach(id -> {
                try {
                    checkObservedProperty(id, parameterName, insertion);
                } catch (final OwsExceptionReport e) {
                    exceptions.add(e);
                }
            });
            exceptions.throwIfNotEmpty();
        }
    }

    protected void checkObservedProperty(String observedProperty, String parameterName, boolean insertion)
            throws OwsExceptionReport {
        if (observedProperty == null || observedProperty.isEmpty()) {
            throw new MissingParameterValueException(parameterName);
        }
        if (insertion) {
            if (!getCache().hasObservableProperty(observedProperty)) {
                throw new InvalidParameterValueException(parameterName, observedProperty);
            }
        } else if (isIncludeChildObservableProperties()) {
            if (getCache().isCompositePhenomenon(observedProperty) ||
                !(getCache().isCompositePhenomenonComponent(observedProperty) ||
                  getCache().hasObservableProperty(observedProperty))) {
                throw new InvalidParameterValueException(parameterName, observedProperty);
            }
        } else if (!getCache().hasObservableProperty(observedProperty)) {
            throw new InvalidParameterValueException(parameterName, observedProperty);
        }

    }

    protected void checkObservedProperty(final String observedProperty, final Enum<?> parameterName, boolean insertion)
            throws OwsExceptionReport {
        checkObservedProperty(observedProperty, parameterName.name(), insertion);
    }

    protected void checkOfferings(final Collection<String> offerings, final String parameterName)
            throws OwsExceptionReport {
        if (offerings != null) {
            CompositeOwsException exceptions = new CompositeOwsException();
            offerings.forEach(id -> {
                try {
                    checkOffering(id, parameterName);
                } catch (final OwsExceptionReport e) {
                    exceptions.add(e);
                }
            });
            exceptions.throwIfNotEmpty();
        }
    }

    protected void checkOfferings(Collection<String> offerings, Enum<?> parameterName) throws OwsExceptionReport {
        checkOfferings(offerings, parameterName.name());
    }

    protected void checkOffering(final String offering, final String parameterName) throws OwsExceptionReport {
        if (offering == null || offering.isEmpty()) {
            throw new MissingParameterValueException(parameterName);
        }
        if (!getCache().hasOffering(offering)) {
            throw new InvalidParameterValueException(parameterName, offering);
        }
    }

    protected void checkOffering(final String offering, final Enum<?> parameterName) throws OwsExceptionReport {
        checkOffering(offering, parameterName.name());
    }

    protected void checkSpatialFilters(final List<SpatialFilter> spatialFilters, final String name)
            throws OwsExceptionReport {
        // TODO make supported ValueReferences dynamic
        if (spatialFilters != null) {
            for (final SpatialFilter spatialFilter : spatialFilters) {
                checkSpatialFilter(spatialFilter, name);
            }
        }

    }

    protected void checkSpatialFilter(final SpatialFilter spatialFilter, final String name) throws OwsExceptionReport {
        // TODO make supported ValueReferences dynamic
        if (spatialFilter != null) {
            if (!spatialFilter.hasValueReference()) {
                throw new MissingValueReferenceException();
            } else if (!checkFeatureValueReference(spatialFilter.getValueReference())
                    && !checkSpatialFilteringProfileValueReference(spatialFilter.getValueReference())) {
                throw new InvalidValueReferenceException(spatialFilter.getValueReference());
            }
        }
    }

    protected void checkSpatialFilter(SpatialFilter spatialFilter, Enum<?> name) throws OwsExceptionReport {
        checkSpatialFilter(spatialFilter, name.name());
    }

    protected void checkTemporalFilter(List<TemporalFilter> temporalFilters, String name) throws OwsExceptionReport {
        if (temporalFilters != null) {
            for (TemporalFilter temporalFilter : temporalFilters) {
                if (temporalFilter.getValueReference() == null || (temporalFilter.getValueReference() != null && temporalFilter.getValueReference().isEmpty())) {
                    throw new MissingValueReferenceException();
                } else if (!validTemporalFilterValueReferences.contains(temporalFilter.getValueReference())) {
                    throw new InvalidValueReferenceException(temporalFilter.getValueReference());
                }
                checkTemporalFilter(temporalFilter);
            }
        }
    }

    protected void checkTemporalFilter(TemporalFilter temporalFilter) throws CodedException {
        if (temporalFilter.getTime() instanceof TimePeriod) {
            TimePeriod tp = (TimePeriod) temporalFilter.getTime();
            if (tp.isEmpty()) {
                throw new InvalidParameterValueException(SosConstants.Filter.TimePeriod, tp.toString())
                        .withMessage("Begin/end time is missing!");
            }
            if (tp.getStart().isEqual(tp.getEnd())) {
                throw new InvalidParameterValueException(SosConstants.Filter.TimePeriod, tp.toString()).withMessage(
                        "It is not allowed that begin and end time are equal! Begin '%s' == End '%s'", tp.getStart(),
                        tp.getEnd());
            }
            if (tp.getStart().isAfter(tp.getEnd())) {
                throw new InvalidParameterValueException(SosConstants.Filter.TimePeriod, tp.toString()).withMessage(
                        "It is not allowed that begin time is after end time! Begin '%s' > End '%s'", tp.getStart(),
                        tp.getEnd());
            }
        }

    }

    protected void checkTemporalFilter(final List<TemporalFilter> temporalFilters, final Enum<?> name)
            throws OwsExceptionReport {
        checkTemporalFilter(temporalFilters, name.name());
    }

    protected void checkResultTemplate(final String resultTemplate, final String parameterName)
            throws OwsExceptionReport {
        if (resultTemplate == null || resultTemplate.isEmpty()) {
            throw new MissingParameterValueException(parameterName);
        } else if (!getCache().hasResultTemplate(resultTemplate)) {
            throw new InvalidParameterValueException(parameterName, resultTemplate);
        }
    }

    protected void checkReservedCharacter(Collection<String> values, Enum<?> parameterName) throws OwsExceptionReport {
        checkReservedCharacter(values, parameterName.name());
    }

    protected void checkReservedCharacter(Collection<String> values, String parameterName) throws OwsExceptionReport {
        CompositeOwsException exceptions = new CompositeOwsException();
        values.forEach((value) -> {
            try {
                checkReservedCharacter(value, parameterName);
            } catch (OwsExceptionReport owse) {
                exceptions.add(owse);
            }
        });
        exceptions.throwIfNotEmpty();
    }

    protected void checkReservedCharacter(String value, Enum<?> parameterName) throws OwsExceptionReport {
        checkReservedCharacter(value, parameterName.name());
    }

    protected void checkReservedCharacter(String value, String parameterName) throws OwsExceptionReport {
        if (value != null && value.contains(",")) {
            throw new InvalidParameterValueException(parameterName, value)
                    .withMessage("The value '%s' contains the reserved parameter ','", value);
        }
    }

    protected List<String> addChildProcedures(final Collection<String> procedures) {
        final Set<String> allProcedures = Sets.newHashSet();
        if (procedures != null) {
            for (final String procedure : procedures) {
                allProcedures.add(procedure);
                allProcedures.addAll(getCache().getChildProcedures(procedure, true, false));
            }
        }
        return Lists.newArrayList(allProcedures);
    }

    protected List<String> addInstanceProcedures(final Collection<String> procedures) {
        final Set<String> allProcedures = Sets.newHashSet();
        if (procedures != null) {
            for (String procedure : procedures) {
                allProcedures.add(procedure);
                if (getCache().hasInstancesForProcedure(procedure)) {
                    allProcedures.addAll(getCache().getInstancesForProcedure(procedure));
                }
            }
        }
        return Lists.newArrayList(allProcedures);
    }

    protected List<String> addChildFeatures(final Collection<String> features) {
        final Set<String> allFeatures = Sets.newHashSet();
        if (features != null) {
            for (final String feature : features) {
                allFeatures.add(feature);
                allFeatures.addAll(getCache().getChildFeatures(feature, true, false));
            }
        }
        return Lists.newArrayList(allFeatures);
    }

    protected List<String> addChildObservableProperties(List<String> observedProperties) {
        Set<String> allObservedProperties = Sets.newHashSet(observedProperties);
        if (isIncludeChildObservableProperties()) {
            for (String observedProperty : observedProperties) {
                if (getCache().isCompositePhenomenon(observedProperty)) {
                    allObservedProperties.addAll(getCache().getObservablePropertiesForCompositePhenomenon(observedProperty));
                }
            }
        }
        return Lists.newArrayList(allObservedProperties);
    }

    protected void checkObservationType(final String observationType, final String parameterName)
            throws OwsExceptionReport {
        if (observationType == null || observationType.isEmpty()) {
            throw new MissingParameterValueException(parameterName);
        } else if (!getCache().hasObservationType(observationType)) {
            throw new InvalidParameterValueException(parameterName, observationType);
        }
    }

    protected void setObservationResponseResponseFormatAndContentType(AbstractObservationRequest obsRequest,
            AbstractObservationResponse obsResponse) {
        if (obsRequest.isSetResponseFormat()) {
            // don't normalize response format with MediaType parsing here,
            // that's the job of the v1 decoders
            obsResponse.setResponseFormat(obsRequest.getResponseFormat());
        }
    }

    protected boolean hasLanguageExtension(SwesExtensions extensions) {
        return extensions != null && extensions.containsExtension(OWSConstants.AdditionalRequestParams.language);
    }

    private boolean checkFeatureValueReference(String valueReference) {
        return "sams:shape".equals(valueReference)
                || "om:featureOfInterest/sams:SF_SpatialSamplingFeature/sams:shape".equals(valueReference)
                || "om:featureOfInterest/*/sams:shape".equals(valueReference);
    }

    private boolean checkSpatialFilteringProfileValueReference(String valueReference) {
        return Sos2Constants.VALUE_REFERENCE_SPATIAL_FILTERING_PROFILE.equals(valueReference);
    }

    protected boolean checkOnlyRequestableProcedureDescriptionFromats(String format, Enum<?> parameter)
            throws MissingParameterValueException {
        if (Strings.isNullOrEmpty(format)) {
            throw new MissingParameterValueException(parameter);
        } else {
            return getCache().hasRequestableProcedureDescriptionFormat(format);
        }
    }

    public boolean isIncludeChildObservableProperties() {
        return includeChildObservableProperties;
    }

    @Setting(EXPOSE_CHILD_OBSERVABLE_PROPERTIES)
    public void setIncludeChildObservableProperties(boolean include) {
        this.includeChildObservableProperties = include;
    }
}
