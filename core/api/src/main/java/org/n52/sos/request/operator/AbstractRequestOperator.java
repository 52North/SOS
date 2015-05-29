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
package org.n52.sos.request.operator;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.Set;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.n52.iceland.cache.ContentCache;
import org.n52.iceland.cache.ContentCacheController;
import org.n52.iceland.convert.RequestResponseModifier;
import org.n52.iceland.convert.RequestResponseModifierRepository;
import org.n52.iceland.ds.OperationHandler;
import org.n52.iceland.ds.OperationHandlerRepository;
import org.n52.iceland.event.ServiceEventBus;
import org.n52.iceland.event.events.RequestEvent;
import org.n52.iceland.exception.CodedException;
import org.n52.iceland.exception.ows.InvalidParameterValueException;
import org.n52.iceland.exception.ows.MissingParameterValueException;
import org.n52.iceland.exception.ows.OperationNotSupportedException;
import org.n52.iceland.exception.ows.VersionNegotiationFailedException;
import org.n52.iceland.exception.ows.concrete.InvalidServiceParameterException;
import org.n52.iceland.exception.ows.concrete.MissingServiceParameterException;
import org.n52.iceland.exception.ows.concrete.MissingValueReferenceException;
import org.n52.iceland.lifecycle.Constructable;
import org.n52.iceland.ogc.filter.SpatialFilter;
import org.n52.iceland.ogc.filter.TemporalFilter;
import org.n52.iceland.ogc.gml.time.TimePeriod;
import org.n52.iceland.ogc.ows.CompositeOwsException;
import org.n52.iceland.ogc.ows.OWSConstants;
import org.n52.iceland.ogc.ows.OwsExceptionReport;
import org.n52.iceland.ogc.ows.OwsOperation;
import org.n52.iceland.ogc.sos.Sos2Constants;
import org.n52.iceland.ogc.sos.SosConstants;
import org.n52.iceland.ogc.swes.SwesExtensions;
import org.n52.iceland.request.AbstractServiceRequest;
import org.n52.iceland.request.operator.RequestOperator;
import org.n52.iceland.request.operator.RequestOperatorKey;
import org.n52.iceland.response.AbstractServiceResponse;
import org.n52.iceland.service.Configurator;
import org.n52.iceland.service.operator.ServiceOperatorRepository;
import org.n52.iceland.util.CollectionHelper;
import org.n52.sos.exception.ows.concrete.InvalidValueReferenceException;
import org.n52.sos.exception.ows.concrete.MissingProcedureParameterException;
import org.n52.sos.request.AbstractObservationRequest;
import org.n52.sos.response.AbstractObservationResponse;
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
 * @author Christian Autermann <c.autermann@52north.org>
 *
 * @since 4.0.0
 */
public abstract class AbstractRequestOperator<D extends OperationHandler, Q extends AbstractServiceRequest<?>, A extends AbstractServiceResponse>
        implements RequestOperator, Constructable {
    private static final Logger LOGGER = LoggerFactory.getLogger(AbstractRequestOperator.class);

    // TODO make supported ValueReferences dynamic
    private static final Set<String> validTemporalFilterValueReferences = Sets.newHashSet("phenomenonTime",
            "om:phenomenonTime", "resultTime", "om:resultTime", "validTime", "om:validTime");

    private final String operationName;
    private final RequestOperatorKey requestOperatorKey;
    private final Class<Q> requestType;
    @Inject
    private OperationHandlerRepository operationHandlerRepository;
    @Inject
    private RequestResponseModifierRepository requestResponseModifierRepository;
    @Inject
    private ContentCacheController contentCacheController;
    @Inject
    private ProfileHandler profileHandler;
    @Inject
    private ServiceOperatorRepository serviceOperatorRepository;
    @Inject
    private ServiceEventBus serviceEventBus;
    private D operationHandler;

    public AbstractRequestOperator(String service, String version, String operationName, Class<Q> requestType) {
        this(service, version, operationName, true, requestType);
    }

    public AbstractRequestOperator(String service, String version, String operationName, boolean defaultActive, Class<Q> requestType) {
        this.operationName = operationName;
        this.requestOperatorKey = new RequestOperatorKey(service, version, operationName, defaultActive);
        this.requestType = requestType;
        LOGGER.info("{} initialized successfully!", getClass().getSimpleName());
    }

    @Override
    public void init() {
        this.initOperationHandler(this.requestOperatorKey.getService(),
                                  this.requestOperatorKey.getOperationName());
    }

    @Deprecated
    protected D initDAO(String service, String operationName) {
        return initOperationHandler(service, operationName);
    }

    @SuppressWarnings("unchecked")
    protected D initOperationHandler(String service, String operationName1) {
        D handler = (D) this.operationHandlerRepository.getOperationHandler(service, operationName1);
        Objects.requireNonNull(handler, String.format("OperationDAO for Operation %s has no implementation!", operationName1));
        return handler;
    }

    @Deprecated
    protected D getDao() {
        return getOperationHandler();
    }

    protected D getOperationHandler() {
        return this.operationHandler;
    }

    @Override
    public OwsOperation getOperationMetadata(final String service, final String version) throws OwsExceptionReport {
        return getDao().getOperationsMetadata(service, version);
    }

    protected String getOperationName() {
        return this.operationName;
    }

    @Override
    @Deprecated
    public RequestOperatorKey getRequestOperatorKeyType() {
        return requestOperatorKey;
    }

    @Override
    public Set<RequestOperatorKey> getKeys() {
        return Collections.singleton(requestOperatorKey);
    }

    @Override
    public AbstractServiceResponse receiveRequest(final AbstractServiceRequest<?> abstractRequest)
            throws OwsExceptionReport {
        this.serviceEventBus.submit(new RequestEvent(abstractRequest));
        if (requestType.isAssignableFrom(abstractRequest.getClass())) {
            Q request = requestType.cast(abstractRequest);
            checkForModifierAndProcess(request);
            checkParameters(request);
            A response = receive(request);
            return checkForModifierAndProcess(request, response);
        } else {
            throw new OperationNotSupportedException(abstractRequest.getOperationName());
        }
    }

    private void checkForModifierAndProcess(AbstractServiceRequest<?> request) throws OwsExceptionReport {
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

    private AbstractServiceResponse checkForModifierAndProcess(AbstractServiceRequest<?> request,
            AbstractServiceResponse response) throws OwsExceptionReport {
        if (this.requestResponseModifierRepository.hasRequestResponseModifier(request, response)) {
            List<RequestResponseModifier> defaultMofifier = new ArrayList<>();
            List<RequestResponseModifier> remover = new ArrayList<>();
            List<RequestResponseModifier> merger = new ArrayList<>();
            for (RequestResponseModifier modifier : this.requestResponseModifierRepository.getRequestResponseModifier(request, response)) {
                if (modifier.getFacilitator().isMerger()) {
                    merger.add(modifier);
                } else if (modifier.getFacilitator().isAdderRemover()) {
                    remover.add(modifier);
                } else {
                    defaultMofifier.add(modifier);
                }

            }
            // execute merger
            for (RequestResponseModifier modifier : merger) {
                modifier.modifyResponse(request, response);
            }
            // execute default
            for (RequestResponseModifier modifier : defaultMofifier) {
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

    protected ContentCache getCache() {
        return this.contentCacheController.getCache();
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
    protected List<String> checkAcceptedVersionsParameter(final String service, final List<String> versions)
            throws OwsExceptionReport {

        final List<String> validVersions = new LinkedList<>();
        if (versions != null) {
            final Set<String> supportedVersions =
                    this.serviceOperatorRepository.getSupportedVersions(service);
            for (final String version : versions) {
                if (supportedVersions.contains(version)) {
                    validVersions.add(version);
                }
            }
            if (validVersions.isEmpty()) {
                throw new VersionNegotiationFailedException().at(org.n52.iceland.ogc.ows.OWSConstants.GetCapabilitiesParams.AcceptVersions)
                        .withMessage("The parameter '%s' does not contain a supported Service version!",
                                org.n52.iceland.ogc.ows.OWSConstants.GetCapabilitiesParams.AcceptVersions.name());
            }
            return validVersions;
        } else {
            throw new MissingParameterValueException(org.n52.iceland.ogc.ows.OWSConstants.GetCapabilitiesParams.AcceptVersions);
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
    protected void checkSingleVersionParameter(final AbstractServiceRequest<?> request) throws OwsExceptionReport {

        // if version is incorrect, throw exception
        if (request.getVersion() == null
                || !this.serviceOperatorRepository.isVersionSupported(request.getService(), request.getVersion())) {
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
            throw new MissingParameterValueException(org.n52.iceland.ogc.ows.OWSConstants.GetCapabilitiesParams.AcceptVersions);
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
    protected void checkServiceParameter(final String service) throws OwsExceptionReport {

        if (service == null || service.equalsIgnoreCase("NOT_SET")) {
            throw new MissingServiceParameterException();
        } else if (!service.equals(SosConstants.SOS)) {
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

    protected void checkProcedureIDs(final Collection<String> procedureIDs, final String parameterName)
            throws OwsExceptionReport {
        if (procedureIDs != null) {
            final CompositeOwsException exceptions = new CompositeOwsException();
            for (final String procedureID : procedureIDs) {
                try {
                    checkProcedureID(procedureID, parameterName);
                } catch (final OwsExceptionReport owse) {
                    exceptions.add(owse);
                }
            }
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

    protected void checkObservationIDs(final Collection<String> observationIDs, final String parameterName)
            throws OwsExceptionReport {
        if (CollectionHelper.isEmpty(observationIDs)) {
            throw new MissingParameterValueException(parameterName);
        }
        if (observationIDs != null) {
            final CompositeOwsException exceptions = new CompositeOwsException();
            for (final String observationID : observationIDs) {
                try {
                    checkObservationID(observationID, parameterName);
                } catch (final OwsExceptionReport owse) {
                    exceptions.add(owse);
                }
            }
            exceptions.throwIfNotEmpty();
        }
    }

    protected void checkFeatureOfInterestIdentifiers(final List<String> featuresOfInterest, final String parameterName)
            throws OwsExceptionReport {
        if (featuresOfInterest != null) {
            final CompositeOwsException exceptions = new CompositeOwsException();
            for (final String featureOfInterest : featuresOfInterest) {
                try {
                    checkFeatureOfInterestIdentifier(featureOfInterest, parameterName);
                } catch (final OwsExceptionReport e) {
                    exceptions.add(e);
                }
            }
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

    protected void checkObservedProperties(final List<String> observedProperties, final String parameterName)
            throws OwsExceptionReport {
        if (observedProperties != null) {
            final CompositeOwsException exceptions = new CompositeOwsException();
            for (final String observedProperty : observedProperties) {
                try {
                    checkObservedProperty(observedProperty, parameterName);
                } catch (final OwsExceptionReport e) {
                    exceptions.add(e);
                }
            }
            exceptions.throwIfNotEmpty();
        }
    }

    protected void checkObservedProperty(final String observedProperty, final String parameterName)
            throws OwsExceptionReport {
        if (observedProperty == null || observedProperty.isEmpty()) {
            throw new MissingParameterValueException(parameterName);
        }
        if (!getCache().hasObservableProperty(observedProperty)) {
            throw new InvalidParameterValueException(parameterName, observedProperty);
        }
    }

    protected void checkObservedProperty(final String observedProperty, final Enum<?> parameterName)
            throws OwsExceptionReport {
        checkObservedProperty(observedProperty, parameterName.name());
    }

    protected void checkOfferings(final Collection<String> offerings, final String parameterName)
            throws OwsExceptionReport {
        if (offerings != null) {
            final CompositeOwsException exceptions = new CompositeOwsException();
            for (final String offering : offerings) {
                try {
                    checkOffering(offering, parameterName);
                } catch (final OwsExceptionReport e) {
                    exceptions.add(e);
                }
            }
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
            for (final TemporalFilter temporalFilter : temporalFilters) {
                if (temporalFilter.getValueReference() == null
                        || (temporalFilter.getValueReference() != null && temporalFilter.getValueReference().isEmpty())) {
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
                        "It is not allowed that begin time is before end time! Begin '%s' > End '%s'", tp.getStart(),
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

    // protected void checkLanguageExtension(SwesExtensions extensions) throws
    // OwsExceptionReport {
    // checkLanguageExtension(extensions,
    // ServiceConfiguration.getInstance().getSupportedLanguages());
    // }
    //
    // protected void checkLanguageExtension(SwesExtensions extensions,
    // Set<String> supportedLanguages)
    // throws OwsExceptionReport {
    // if (hasLanguageExtension(extensions)) {
    // SwesExtension<?> extension =
    // extensions.getExtension(SosConstants.InspireParams.language);
    // String value = Constants.EMPTY_STRING;
    // if (extension.getValue() instanceof String) {
    // value = (String) extension.getValue();
    // } else if (extension.getValue() instanceof SweText) {
    // value = ((SweText) extension.getValue()).getValue();
    // } else {
    // throw new
    // MissingParameterValueException(SosConstants.InspireParams.language)
    // .withMessage("The language extension value should be of type 'swe:TextPropertytype'");
    // }
    // if (!supportedLanguages.contains(value)) {
    // throw new
    // InvalidParameterValueException(SosConstants.InspireParams.language,
    // value);
    // }
    // }
    // }

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
            return getCache().hasRequstableProcedureDescriptionFormat(format);
        }
    }

}
