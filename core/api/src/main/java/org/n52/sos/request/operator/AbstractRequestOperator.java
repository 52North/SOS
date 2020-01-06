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
package org.n52.sos.request.operator;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.Map.Entry;

import org.n52.sos.cache.ContentCache;
import org.n52.sos.coding.CodingRepository;
import org.n52.sos.convert.ConverterRepository;
import org.n52.sos.convert.RequestResponseModifier;
import org.n52.sos.convert.RequestResponseModifierRepository;
import org.n52.sos.ds.OperationDAO;
import org.n52.sos.ds.OperationDAORepository;
import org.n52.sos.event.SosEventBus;
import org.n52.sos.event.events.RequestEvent;
import org.n52.sos.event.events.ResponseEvent;
import org.n52.sos.exception.CodedException;
import org.n52.sos.exception.ows.CodedOwsException;
import org.n52.sos.exception.ows.InvalidParameterValueException;
import org.n52.sos.exception.ows.MissingParameterValueException;
import org.n52.sos.exception.ows.OperationNotSupportedException;
import org.n52.sos.exception.ows.VersionNegotiationFailedException;
import org.n52.sos.exception.ows.concrete.InvalidServiceParameterException;
import org.n52.sos.exception.ows.concrete.InvalidValueReferenceException;
import org.n52.sos.exception.ows.concrete.MissingProcedureParameterException;
import org.n52.sos.exception.ows.concrete.MissingServiceParameterException;
import org.n52.sos.exception.ows.concrete.MissingValueReferenceException;
import org.n52.sos.exception.ows.concrete.NotYetSupportedException;
import org.n52.sos.ogc.filter.BinaryLogicFilter;
import org.n52.sos.ogc.filter.ComparisonFilter;
import org.n52.sos.ogc.filter.Filter;
import org.n52.sos.ogc.filter.FilterConstants.SpatialOperator;
import org.n52.sos.ogc.filter.SpatialFilter;
import org.n52.sos.ogc.filter.TemporalFilter;
import org.n52.sos.ogc.gml.time.TimePeriod;
import org.n52.sos.ogc.ows.CompositeOwsException;
import org.n52.sos.ogc.ows.OWSConstants;
import org.n52.sos.ogc.ows.OwsExceptionReport;
import org.n52.sos.ogc.ows.OwsOperation;
import org.n52.sos.ogc.sensorML.SensorMLConstants;
import org.n52.sos.ogc.sos.ResultFilter;
import org.n52.sos.ogc.sos.ResultFilterConstants;
import org.n52.sos.ogc.sos.Sos2Constants;
import org.n52.sos.ogc.sos.SosConstants;
import org.n52.sos.ogc.sos.SosSpatialFilter;
import org.n52.sos.ogc.sos.SosSpatialFilterConstants;
import org.n52.sos.ogc.swes.SwesExtension;
import org.n52.sos.ogc.swes.SwesExtensions;
import org.n52.sos.request.AbstractObservationRequest;
import org.n52.sos.request.AbstractServiceRequest;
import org.n52.sos.response.AbstractObservationResponse;
import org.n52.sos.response.AbstractServiceResponse;
import org.n52.sos.service.Configurator;
import org.n52.sos.service.operator.ServiceOperatorKey;
import org.n52.sos.service.operator.ServiceOperatorRepository;
import org.n52.sos.service.profile.Profile;
import org.n52.sos.util.CollectionHelper;
import org.n52.sos.util.Constants;
import org.n52.sos.util.http.MediaType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.n52.sos.service.ServiceConfiguration;

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
public abstract class AbstractRequestOperator<D extends OperationDAO, Q extends AbstractServiceRequest<?>, A extends AbstractServiceResponse>
        implements RequestOperator {
    private static final Logger LOGGER = LoggerFactory.getLogger(AbstractRequestOperator.class);

    // TODO make supported ValueReferences dynamic
    private static final Set<String> validTemporalFilterValueReferences = Sets.newHashSet("phenomenonTime",
            "om:phenomenonTime", "resultTime", "om:resultTime", "validTime", "om:validTime");

    private final D dao;

    private final String operationName;

    private final RequestOperatorKey requestOperatorKeyType;

    private final Class<Q> requestType;

    public AbstractRequestOperator(String service, String version, String operationName, Class<Q> requestType) {
        this(service, version, operationName, true, requestType);
    }
    
    public AbstractRequestOperator(String service, String version, String operationName, boolean defaultActive, Class<Q> requestType) {
        this.operationName = operationName;
        this.requestOperatorKeyType = new RequestOperatorKey(service, version, operationName, defaultActive);
        this.requestType = requestType;
        this.dao = initDAO(service, operationName);
        LOGGER.info("{} initialized successfully!", getClass().getSimpleName());
    }

    @SuppressWarnings("unchecked")
    protected D initDAO(String service, String operationName) {
        D dao = (D) OperationDAORepository.getInstance().getOperationDAO(service, operationName);
        if (dao == null) {
            throw new NullPointerException(String.format("OperationDAO for Operation %s has no implementation!",
                    operationName));
        }
        return dao;
    }

    protected D getDao() {
        return this.dao;
    }
    
    @Override
    public boolean isSupported() {
        return getDao() != null && getDao().isSupported();
    }

    // @Override
    // public SosCapabilitiesExtension getExtension() throws OwsExceptionReport
    // {
    // return getDao().getExtension();
    // }

    @Override
    public OwsOperation getOperationMetadata(final String service, final String version) throws OwsExceptionReport {
        return getDao().getOperationsMetadata(service, version);
    }

    protected String getOperationName() {
        return this.operationName;
    }

    @Override
    public RequestOperatorKey getRequestOperatorKeyType() {
        return requestOperatorKeyType;
    }

    @Override
    public AbstractServiceResponse receiveRequest(final AbstractServiceRequest<?> abstractRequest)
            throws OwsExceptionReport {
        SosEventBus.fire(new RequestEvent(abstractRequest));
        if (requestType.isAssignableFrom(abstractRequest.getClass()) && isSupported()) {
            Q request = requestType.cast(abstractRequest);
            checkForModifierAndProcess(request);
            checkParameters(request);
            A response = receive(request);
            SosEventBus.fire(new ResponseEvent(response));
            return checkForModifierAndProcess(request, response);
        } else {
            throw new OperationNotSupportedException(abstractRequest.getOperationName());
        }
    }

    private void checkForModifierAndProcess(AbstractServiceRequest<?> request) throws OwsExceptionReport {
        if (RequestResponseModifierRepository.getInstance().hasRequestResponseModifier(request)) {
            List<RequestResponseModifier<AbstractServiceRequest<?>, AbstractServiceResponse>> splitter =
                    new ArrayList<RequestResponseModifier<AbstractServiceRequest<?>, AbstractServiceResponse>>();
            List<RequestResponseModifier<AbstractServiceRequest<?>, AbstractServiceResponse>> remover =
                    new ArrayList<RequestResponseModifier<AbstractServiceRequest<?>, AbstractServiceResponse>>();
            List<RequestResponseModifier<AbstractServiceRequest<?>, AbstractServiceResponse>> defaultMofifier =
                    new ArrayList<RequestResponseModifier<AbstractServiceRequest<?>, AbstractServiceResponse>>();
            for (RequestResponseModifier<AbstractServiceRequest<?>, AbstractServiceResponse> modifier : RequestResponseModifierRepository
                    .getInstance().getRequestResponseModifier(request)) {
                if (modifier.getFacilitator().isSplitter()) {
                    splitter.add(modifier);
                } else if (modifier.getFacilitator().isAdderRemover()) {
                    remover.add( modifier);
                } else {
                    defaultMofifier.add(modifier);
                }
            }
            // execute adder/remover
            for (RequestResponseModifier<AbstractServiceRequest<?>, AbstractServiceResponse> modifier : remover) {
                modifier.modifyRequest(request);
            }
            // execute default
            for (RequestResponseModifier<AbstractServiceRequest<?>, AbstractServiceResponse> modifier : defaultMofifier) {
                modifier.modifyRequest(request);
            }
            // execute splitter
            for (RequestResponseModifier<AbstractServiceRequest<?>, AbstractServiceResponse> modifier : splitter) {
                modifier.modifyRequest(request);
            }
        }
    }

    private AbstractServiceResponse checkForModifierAndProcess(AbstractServiceRequest<?> request,
            AbstractServiceResponse response) throws OwsExceptionReport {
        if (RequestResponseModifierRepository.getInstance().hasRequestResponseModifier(request, response)) {
            List<RequestResponseModifier<AbstractServiceRequest<?>, AbstractServiceResponse>> defaultModifier =
                    new ArrayList<RequestResponseModifier<AbstractServiceRequest<?>, AbstractServiceResponse>>();
            List<RequestResponseModifier<AbstractServiceRequest<?>, AbstractServiceResponse>> remover =
                    new ArrayList<RequestResponseModifier<AbstractServiceRequest<?>, AbstractServiceResponse>>();
            List<RequestResponseModifier<AbstractServiceRequest<?>, AbstractServiceResponse>> merger =
                    new ArrayList<RequestResponseModifier<AbstractServiceRequest<?>, AbstractServiceResponse>>();
            for (RequestResponseModifier<AbstractServiceRequest<?>, AbstractServiceResponse> modifier : RequestResponseModifierRepository
                    .getInstance().getRequestResponseModifier(request, response)) {
                if (modifier.getFacilitator().isMerger()) {
                    merger.add(modifier);
                } else if (modifier.getFacilitator().isAdderRemover()) {
                    remover.add(modifier);
                } else {
                    defaultModifier.add(modifier);
                }

            }
            // execute merger
            for (RequestResponseModifier<AbstractServiceRequest<?>, AbstractServiceResponse> modifier : merger) {
                modifier.modifyResponse(request, response);
            }
            // execute default
            for (RequestResponseModifier<AbstractServiceRequest<?>, AbstractServiceResponse> modifier : defaultModifier) {
                modifier.modifyResponse(request, response);
            }

            // execute adder/remover
            for (RequestResponseModifier<AbstractServiceRequest<?>, AbstractServiceResponse> modifier : remover) {
                modifier.modifyResponse(request, response);
            }
            return response;
        }
        return response;
    }

    protected abstract A receive(Q request) throws OwsExceptionReport;

    protected abstract void checkParameters(Q request) throws OwsExceptionReport;

    protected ContentCache getCache() {
        return Configurator.getInstance().getCache();
    }

    protected Profile getActiveProfile() {
        return Configurator.getInstance().getProfileHandler().getActiveProfile();
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
    protected List<String> checkAcceptedVersionsParameter(final String service, final Collection<String> versions)
            throws OwsExceptionReport {

        final List<String> validVersions = new LinkedList<String>();
        if (versions != null) {
            final Set<String> supportedVersions =
                    ServiceOperatorRepository.getInstance().getSupportedVersions(service);
            for (final String version : versions) {
                if (supportedVersions.contains(version)) {
                    validVersions.add(version);
                }
            }
            if (validVersions.isEmpty()) {
                throw new VersionNegotiationFailedException().at(SosConstants.GetCapabilitiesParams.AcceptVersions)
                        .withMessage("The parameter '%s' does not contain a supported Service version!",
                                SosConstants.GetCapabilitiesParams.AcceptVersions.name());
            }
            return validVersions;
        } else {
            throw new MissingParameterValueException(SosConstants.GetCapabilitiesParams.AcceptVersions);
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
                || !ServiceOperatorRepository.getInstance().isVersionSupported(request.getService(),
                        request.getVersion())) {
            throw new InvalidParameterValueException().at(OWSConstants.RequestParams.version).withMessage(
                    "The parameter '%s' does not contain version(s) supported by this Service: '%s'!",
                    OWSConstants.RequestParams.version.name(),
                    Joiner.on(", ").join(
                            ServiceOperatorRepository.getInstance().getSupportedVersions(request.getService())));
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
            throw new MissingParameterValueException(SosConstants.GetCapabilitiesParams.AcceptVersions);
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
     * @throws OwsExceptionReport
     *             * if the value of the sensor ID parameter is incorrect
     */
    protected void checkProcedure(final String procedureID, final Enum<?> parameterName) throws OwsExceptionReport {
        checkProcedure(procedureID, parameterName.name());
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
    protected void checkProcedure(final String procedureID, final String parameterName) throws OwsExceptionReport {
        if (Strings.isNullOrEmpty(procedureID)) {
            throw new MissingProcedureParameterException();
        } else if (!getCache().getPublishedProcedures().contains(procedureID)) {
            throw new InvalidParameterValueException(parameterName, procedureID);
        }
    }

    protected void checkProcedures(final Collection<String> procedureIDs, final String parameterName)
            throws OwsExceptionReport {
        if (procedureIDs != null) {
            final CompositeOwsException exceptions = new CompositeOwsException();
            for (final String procedureID : procedureIDs) {
                try {
                    checkProcedure(procedureID, parameterName);
                } catch (final OwsExceptionReport owse) {
                    exceptions.add(owse);
                }
            }
            exceptions.throwIfNotEmpty();
        }
    }
    
    protected void checkTransactionalProcedure(final String procedureID, final String parameterName) throws OwsExceptionReport {
        if (Strings.isNullOrEmpty(procedureID)) {
            throw new MissingProcedureParameterException();
        } else if (!getCache().hasTransactionalObservationProcedure(procedureID)) {
            throw new InvalidParameterValueException(parameterName, procedureID);
        }
    }
    
    protected void checkTransactionalProcedures(final Collection<String> procedureIDs, final String parameterName)
            throws OwsExceptionReport {
        if (procedureIDs != null) {
            final CompositeOwsException exceptions = new CompositeOwsException();
            for (final String procedureID : procedureIDs) {
                try {
                    checkTransactionalProcedure(procedureID, parameterName);
                } catch (final OwsExceptionReport owse) {
                    exceptions.add(owse);
                }
            }
            exceptions.throwIfNotEmpty();
        }
    }

    protected void checkQueryableProcedure(final String procedureID, final String parameterName) throws OwsExceptionReport {
        if (Strings.isNullOrEmpty(procedureID)) {
            throw new MissingProcedureParameterException();
        } else if (!getCache().hasQueryableProcedure(procedureID)) {
            throw new InvalidParameterValueException(parameterName, procedureID);
        }
    }

    protected void checkQueryableProcedures(final Collection<String> procedureIDs, final String parameterName)
            throws OwsExceptionReport {
        if (procedureIDs != null) {
            final CompositeOwsException exceptions = new CompositeOwsException();
            for (final String procedureID : procedureIDs) {
                try {
                    checkQueryableProcedure(procedureID, parameterName);
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

    protected void checkFeatureOfInterestIdentifiers(final Collection<String> featuresOfInterest, final String parameterName)
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
        if (getCache().getPublishedFeatureOfInterest().contains(featureOfInterest)) {
            return;
        }
        if (getCache().hasRelatedFeature(featureOfInterest) && getCache().isRelatedFeatureSampled(featureOfInterest)) {
            return;
        }
        throw new InvalidParameterValueException(parameterName, featureOfInterest);
    }

    protected void checkObservedProperties(final Collection<String> observedProperties, final Enum<?> parameterName, boolean insertion)
            throws OwsExceptionReport {
        checkObservedProperties(observedProperties, parameterName.name(), insertion);
    }

    protected void checkObservedProperties(final Collection<String> observedProperties, final String parameterName, boolean insertion)
            throws OwsExceptionReport {
        if (observedProperties != null) {
            final CompositeOwsException exceptions = new CompositeOwsException();
            for (final String observedProperty : observedProperties) {
                try {
                    checkObservedProperty(observedProperty, parameterName, insertion);
                } catch (final OwsExceptionReport e) {
                    exceptions.add(e);
                }
            }
            exceptions.throwIfNotEmpty();
        }
    }
    
    protected void checkObservedProperties(final List<String> observedProperties, final String parameterName, boolean all)
            throws OwsExceptionReport {
        if (observedProperties != null) {
            final CompositeOwsException exceptions = new CompositeOwsException();
            for (final String observedProperty : observedProperties) {
                try {
                    checkObservedProperty(observedProperty, parameterName, all);
                } catch (final OwsExceptionReport e) {
                    exceptions.add(e);
                }
            }
            exceptions.throwIfNotEmpty();
        }
    }
    
    protected void checkObservedProperty(final String observedProperty, final String parameterName, boolean insertion)
            throws OwsExceptionReport {
        if (observedProperty == null || observedProperty.isEmpty()) {
            throw new MissingParameterValueException(parameterName);
        }
        if (insertion) {
            if (!getCache().hasObservableProperty(observedProperty)) {
                throw new InvalidParameterValueException(parameterName, observedProperty);
            }
        } else if (ServiceConfiguration.getInstance().isIncludeChildObservableProperties()) {
            if (getCache().isCompositePhenomenon(observedProperty) ||
                !(getCache().isCompositePhenomenonComponent(observedProperty) ||
                  getCache().hasObservableProperty(observedProperty))) {
                throw new InvalidParameterValueException(parameterName, observedProperty);
            }
        } else if (!getCache().getPublishedObservableProperties().contains(observedProperty)) {
            throw new InvalidParameterValueException(parameterName, observedProperty);
        }

    }

    protected void checkObservedProperty(final String observedProperty, final Enum<?> parameterName, boolean insertion)
            throws OwsExceptionReport {
        checkObservedProperty(observedProperty, parameterName.name(), insertion);
    }

    protected void checkOfferings(final Collection<String> offerings, final String parameterName) throws OwsExceptionReport {
        checkOfferings(offerings, parameterName, false);
    }
    
    protected void checkOfferings(final Collection<String> offerings, final String parameterName, boolean all)
            throws OwsExceptionReport {
        if (offerings != null) {
            final CompositeOwsException exceptions = new CompositeOwsException();
            for (final String offering : offerings) {
                try {
                    checkOffering(offering, parameterName, all);
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
    
    protected void checkOfferings(Collection<String> offerings, Enum<?> parameterName, boolean all) throws OwsExceptionReport {
        checkOfferings(offerings, parameterName.name(), all);
    }

    protected void checkOffering(final String offering, final Enum<?> parameterName) throws OwsExceptionReport {
        checkOffering(offering, parameterName.name(), false);
    }

    protected void checkOffering(final String offering, final String parameterName, boolean all) throws OwsExceptionReport {
        if (offering == null || offering.isEmpty()) {
            throw new MissingParameterValueException(parameterName);
        }
        if (all) {
            if (!getCache().getOfferings().contains(offering)) {
                throw new InvalidParameterValueException(parameterName, offering);
            }
        } else {
            if (!getCache().getPublishedOfferings().contains(offering)) {
                throw new InvalidParameterValueException(parameterName, offering);
            }
        }
        
    }

    protected void checkSpatialFilters(final Collection<SpatialFilter> spatialFilters, final String name)
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

    protected void checkTemporalFilter(Collection<TemporalFilter> temporalFilters, String name) throws OwsExceptionReport {
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
                        "It is not allowed that begin time is after end time! Begin '%s' > End '%s'", tp.getStart(),
                        tp.getEnd());
            }
        }

    }

    protected void checkTemporalFilter(final Collection<TemporalFilter> temporalFilters, final Enum<?> name)
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
        for (String value : values) {
            try {
                checkReservedCharacter(value, parameterName);
            } catch (OwsExceptionReport owse) {
                exceptions.add(owse);
            }
        }
        exceptions.throwIfNotEmpty();
    }

    protected void checkReservedCharacter(String value, Enum<?> parameterName) throws OwsExceptionReport {
        checkReservedCharacter(value, parameterName.name());
    }

    protected void checkReservedCharacter(String value, String parameterName) throws OwsExceptionReport {
        if (value != null && value.contains(Constants.COMMA_STRING)) {
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
        if (ServiceConfiguration.getInstance().isIncludeChildObservableProperties()) {
            for (String observedProperty : observedProperties) {
                if (getCache().isCompositePhenomenon(observedProperty)) {
                    allObservedProperties.addAll(getCache().getObservablePropertiesForCompositePhenomenon(observedProperty));
                }
            }
        }
        return Lists.newArrayList(allObservedProperties);
    }
    
    protected List<String> addChildOfferings(List<String> offerings) {
        Set<String> allOfferings = Sets.newHashSet(offerings);
        if (offerings != null) {
            for (final String offering : offerings) {
                allOfferings.add(offering);
                allOfferings.addAll(getCache().getChildOfferings(offering, true, false));
            }
        }
        return Lists.newArrayList(allOfferings);
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
    
    protected void checkResultFilterExtension(AbstractServiceRequest request) throws CodedException {
        if (request.isSetExtensions() && request.hasExtension(ResultFilterConstants.RESULT_FILTER)) {
            if (request.getExtensionCount(ResultFilterConstants.RESULT_FILTER) > 1) {
                throw new InvalidParameterValueException(ResultFilterConstants.RESULT_FILTER, "duplicated");
            }
            SwesExtension<?> extension = request.getExtension(ResultFilterConstants.RESULT_FILTER);
            if (extension.getValue() == null) {
                throw new MissingParameterValueException(ResultFilterConstants.RESULT_FILTER);
            }
            Filter<?> filter = ((ResultFilter) extension).getValue();
            if (filter instanceof BinaryLogicFilter) {
               checkBinaryLogicFilter((BinaryLogicFilter) filter);
            } else if (filter instanceof ComparisonFilter) {
                checkFilter((ComparisonFilter) filter);
            } else {
                throw new NotYetSupportedException().withMessage("The %s does not yet support filters of type '%s'", ResultFilterConstants.RESULT_FILTER, filter);
            }
        }
    }
    
    private void checkBinaryLogicFilter(BinaryLogicFilter filter) throws CodedException {
        for (Filter<?> f : filter.getFilterPredicates()) {
            if (f instanceof BinaryLogicFilter) {
                checkBinaryLogicFilter((BinaryLogicFilter) f);
             } else if (f instanceof ComparisonFilter) {
                 checkFilter((ComparisonFilter) f);
             } else {
                 throw new NotYetSupportedException().withMessage("The %s does not yet support filters of type '%s'", ResultFilterConstants.RESULT_FILTER, f);
             }
        }
    }

    private void checkFilter(ComparisonFilter filter) throws CodedException {
        if (!filter.hasValueReference()) {
            throw new MissingParameterValueException(ResultFilterConstants.RESULT_FILTER + ".valueReference");
        } else if (!(filter.getValueReference().startsWith(".") || filter.getValueReference().startsWith("om:result"))) {
            throw new InvalidParameterValueException(ResultFilterConstants.RESULT_FILTER + ".valueReference", filter.getValueReference());
        }
        if (filter.getOperator() == null) {
            throw new MissingParameterValueException(ResultFilterConstants.RESULT_FILTER + ".operator");
        }
        
        if (filter.getValue() == null) {
            throw new MissingParameterValueException(ResultFilterConstants.RESULT_FILTER + ".value");
        }
    }

    protected void checkSpatialFilterExtension(AbstractServiceRequest request) throws CodedOwsException {
        if (request.isSetExtensions() && request.hasExtension(SosSpatialFilterConstants.SPATIAL_FILTER)) {
            if (request.getExtensionCount(SosSpatialFilterConstants.SPATIAL_FILTER) > 1) {
                throw new InvalidParameterValueException(SosSpatialFilterConstants.SPATIAL_FILTER, "duplicated");
            }
            SwesExtension<?> extension = request.getExtension(SosSpatialFilterConstants.SPATIAL_FILTER);
            if (extension.getValue() == null) {
                throw new MissingParameterValueException(SosSpatialFilterConstants.SPATIAL_FILTER);
            }
            SpatialFilter filter = ((SosSpatialFilter)extension).getValue();
            if (!filter.hasValueReference()) {
                throw new MissingParameterValueException(SosSpatialFilterConstants.SPATIAL_FILTER + ".valueReference");
            } else if (!checkFeatureValueReference(filter.getValueReference())
                        && !checkSpatialFilteringProfileValueReference(filter.getValueReference())) {
                    throw new InvalidValueReferenceException(filter.getValueReference());
            }
            if (filter.getOperator() == null) {
                throw new MissingParameterValueException(SosSpatialFilterConstants.SPATIAL_FILTER + ".operator");
            } else if (!filter.getOperator().equals(SpatialOperator.BBOX)) {
                throw new InvalidParameterValueException(SosSpatialFilterConstants.SPATIAL_FILTER + ".operator", filter.getOperator().toString()); 
            }
            
            if (filter.getGeometry() == null) {
                throw new MissingParameterValueException(SosSpatialFilterConstants.SPATIAL_FILTER + ".value");
            }
        }
    }

    protected boolean checkOnlyRequestableProcedureDescriptionFromats(String format, Enum<?> parameter, boolean mimeTypeAllowed)
            throws CodedOwsException {
        if (Strings.isNullOrEmpty(format)) {
            throw new MissingParameterValueException(parameter);
        } else {
            if (!mimeTypeAllowed && MediaType.check(format)) {
                throw new InvalidParameterValueException(parameter, format);
            }
            return getCache().hasRequestableProcedureDescriptionFormat(format) ? true : hasPossibleProcedureDescriptionFormats(format, mimeTypeAllowed);
        }
    }
    
    /**
     * Get possible procedure description formats for this procedure description
     * format. More precise, are there converter available.
     *
     * @param procedureDescriptionFormat
     *            Procedure description format to check
     * @return All possible procedure description formats
     */
    private boolean hasPossibleProcedureDescriptionFormats(String procedureDescriptionFormat, boolean mimeTypeAllowed) {
        Set<String> possibleFormats = Sets.newHashSet();
        if (mimeTypeAllowed) {
            possibleFormats.addAll(checkForUrlVsMimeType(procedureDescriptionFormat));
        }
        String procedureDescriptionFormatMatchingString =
                getProcedureDescriptionFormatMatchingString(procedureDescriptionFormat);
        for (Entry<ServiceOperatorKey, Set<String>> pdfByServiceOperatorKey : CodingRepository.getInstance()
                .getAllProcedureDescriptionFormats().entrySet()) {
            for (String pdfFromRepository : pdfByServiceOperatorKey.getValue()) {
                if (procedureDescriptionFormatMatchingString
                        .equals(getProcedureDescriptionFormatMatchingString(pdfFromRepository))) {
                    possibleFormats.add(pdfFromRepository);
                }
            }
        }
        possibleFormats.addAll(ConverterRepository.getInstance().getFromNamespaceConverterTo(
                procedureDescriptionFormat));
        return !possibleFormats.isEmpty();
    }
    
    private Set<String> checkForUrlVsMimeType(String procedureDescriptionFormat) {
        Set<String> possibleFormats = Sets.newHashSet();
        possibleFormats.add(procedureDescriptionFormat);
        if (SensorMLConstants.SENSORML_OUTPUT_FORMAT_MIME_TYPE.equalsIgnoreCase(procedureDescriptionFormat)) {
            possibleFormats.add(SensorMLConstants.SENSORML_OUTPUT_FORMAT_URL);
        } else if (SensorMLConstants.SENSORML_OUTPUT_FORMAT_URL.equalsIgnoreCase(procedureDescriptionFormat)) {
            possibleFormats.add(SensorMLConstants.SENSORML_OUTPUT_FORMAT_MIME_TYPE);
        }
        return possibleFormats;
   }

    /**
     * Get procedure description format matching String, to lower case replace
     * \s
     *
     * @param procedureDescriptionFormat
     *            Procedure description formats to format
     * @return Formatted procedure description format String
     */
    private String getProcedureDescriptionFormatMatchingString(String procedureDescriptionFormat) {
        // match against lowercase string, ignoring whitespace
        return procedureDescriptionFormat.toLowerCase().replaceAll("\\s", "");
    }

}
