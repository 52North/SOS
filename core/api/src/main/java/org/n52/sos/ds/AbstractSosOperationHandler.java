/*
 * Copyright (C) 2012-2021 52°North Spatial Information Research GmbH
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

import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.inject.Inject;

import org.n52.faroe.annotation.Configurable;
import org.n52.faroe.annotation.Setting;
import org.n52.iceland.cache.ContentCacheController;
import org.n52.iceland.request.handler.OperationHandlerKey;
import org.n52.shetland.ogc.SupportedType;
import org.n52.shetland.ogc.om.ObservationType;
import org.n52.shetland.ogc.ows.OwsAllowedValues;
import org.n52.shetland.ogc.ows.OwsAnyValue;
import org.n52.shetland.ogc.ows.OwsDomain;
import org.n52.shetland.ogc.ows.OwsNoValues;
import org.n52.shetland.ogc.ows.OwsValue;
import org.n52.shetland.ogc.sos.SosConstants;
import org.n52.shetland.util.ReferencedEnvelope;
import org.n52.sos.cache.SosContentCache;
import org.n52.sos.request.operator.AbstractRequestOperator;
import org.n52.sos.service.SosSettings;
import org.n52.sos.service.profile.Profile;
import org.n52.sos.service.profile.ProfileHandler;
import org.n52.sos.util.SosHelper;
import org.n52.svalbard.encode.Encoder;
import org.n52.svalbard.encode.EncoderRepository;
import org.n52.svalbard.encode.ObservationEncoder;

import com.google.common.collect.Sets;

/**
 * Renamed, in version 4.x called AbstractOperationDAO
 *
 * @since 5.0.0
 *
 */
@Configurable
public abstract class AbstractSosOperationHandler
        extends org.n52.iceland.request.handler.AbstractOperationHandler {

    public static final String ALLOW_QUERYING_FOR_INSTANCES_ONLY = "request.procedure.instancesOnly";
    public static final String SHOW_ONLY_AGGREGATED_PROCEDURES = "request.procedure.aggregationOnly";

    private final OperationHandlerKey key;
    private ContentCacheController contentCacheController;
    private boolean includeChildObservableProperties;
    private ProfileHandler profileHandler;
    private EncoderRepository encoderRepository;
    private boolean listOnlyParentOfferings;
    private boolean allowQueryingForInstancesOnly;
    private boolean showOnlyAggregatedProcedures;

    public AbstractSosOperationHandler(String service, String operationName) {
        this.key = new OperationHandlerKey(service, operationName);
    }

    public boolean isIncludeChildObservableProperties() {
        return includeChildObservableProperties;
    }

    @Setting(AbstractRequestOperator.EXPOSE_CHILD_OBSERVABLE_PROPERTIES)
    public void setIncludeChildObservableProperties(boolean include) {
        this.includeChildObservableProperties = include;
    }

    /**
     * @return the allowQueryingForInstancesOnly
     */
    public boolean isAllowQueryingForInstancesOnly() {
        return allowQueryingForInstancesOnly;
    }

    /**
     * @param allowQueryingForInstancesOnly
     *            the allowQueryingForInstancesOnly to set
     */
    @Setting(ALLOW_QUERYING_FOR_INSTANCES_ONLY)
    public void setAllowQueryingForInstancesOnly(boolean allowQueryingForInstancesOnly) {
        this.allowQueryingForInstancesOnly = allowQueryingForInstancesOnly;
    }

    /**
     * @return the showOnlyAggregatedProcedures
     */
    public boolean isShowOnlyAggregatedProcedures() {
        return showOnlyAggregatedProcedures;
    }

    protected ContentCacheController getCacheController() {
        return this.contentCacheController;
    }

    @Inject
    public void setCacheController(ContentCacheController contentCacheController) {
        this.contentCacheController = contentCacheController;
    }

    protected EncoderRepository getEncoderRepository() {
        return this.encoderRepository;
    }

    @Inject
    public void setEncoderRepository(EncoderRepository encoderRepository) {
        this.encoderRepository = encoderRepository;
    }

    protected ProfileHandler getProfileHandler() {
        return profileHandler;
    }

    @Inject
    public void setProfileHandler(ProfileHandler profileHandler) {
        this.profileHandler = profileHandler;
    }

    protected Profile getActiveProfile() {
        return getProfileHandler().getActiveProfile();
    }

    @Setting(SosSettings.LIST_ONLY_PARENT_OFFERINGS)
    public void setListOnlyParentOfferings(boolean listOnlyParentOfferings) {
        this.listOnlyParentOfferings = listOnlyParentOfferings;
    }


    protected boolean checkListOnlyParentOfferings() {
        return listOnlyParentOfferings;
    }

    // TODO check if necessary in feature
    @Override
    public String getOperationName() {
        return getKey().getOperationName();
    }

    @Override
    public Set<OperationHandlerKey> getKeys() {
        return Collections.singleton(getKey());
    }

    public OperationHandlerKey getKey() {
        return this.key;
    }

    protected String getServiceUrl(String service) {
        return super.getServiceURL().toString();
    }

    protected SosContentCache getCache() {
        return (SosContentCache) getCacheController().getCache();
    }

    protected OwsDomain getProcedureParameter(String service, String version) {
        return getProcedureParameter(service, version, getCache().getProcedures());
    }

    protected OwsDomain getProcedureParameter(String service, String version, Collection<String> procedures) {
        return createDomain(SosConstants.GetObservationParams.procedure, procedures,
                getProfileHandler().getActiveProfile().isShowFullOperationsMetadataForObservations());
    }

    protected OwsDomain getQueryableProcedureParameter(String service, String version) {
        return getProcedureParameter(service, version, getCache()
                .getQueryableProcedures(isAllowQueryingForInstancesOnly(), isShowOnlyAggregatedProcedures()));
    }

    protected OwsDomain getPublishedProcedureParameter(String service, String version) {
        return getProcedureParameter(service, version, getCache().getPublishedProcedures());
    }

    protected OwsDomain getFeatureOfInterestParameter(String service, String version) {
        return getFeatureOfInterestParameter(service, version,
                SosHelper.getFeatureIDs(getCache().getFeaturesOfInterest(), version));
    }

    protected OwsDomain getFeatureOfInterestParameter(String service, String version,
            Collection<String> featuresOfInterest) {
        return createDomain(SosConstants.GetObservationParams.featureOfInterest, featuresOfInterest,
                getProfileHandler().getActiveProfile().isShowFullOperationsMetadataForObservations());
    }

    protected OwsDomain getPublishedFeatureOfInterestParameter(String service, String version) {
        return getFeatureOfInterestParameter(service, version,
                SosHelper.getFeatureIDs(getCache().getPublishedFeatureOfInterest(), version));
    }

    protected OwsDomain getPublishedObservablePropertyParameter(String service, String version) {
        return getObservablePropertyParameter(service, version, getCache().getPublishedObservableProperties());
    }

    protected OwsDomain getObservablePropertyParameter(String service, String version) {
        return getObservablePropertyParameter(service, version, getObservableProperties());
    }

    protected OwsDomain getObservablePropertyParameter(String service, String version,
            Collection<String> observedProperties) {
        return createDomain(SosConstants.GetObservationParams.observedProperty, observedProperties,
                getProfileHandler().getActiveProfile().isShowFullOperationsMetadataForObservations());
    }

    protected Collection<String> getObservableProperties() {
        Set<String> observableProperties = getCache().getObservableProperties();
        if (isIncludeChildObservableProperties()) {
            Set<String> compositePhenomenons = getCache().getCompositePhenomenons();
            observableProperties.removeAll(compositePhenomenons);
            compositePhenomenons.stream().map(getCache()::getObservablePropertiesForCompositePhenomenon)
                    .flatMap(Set::stream).forEach(observableProperties::add);
        }
        return observableProperties;
    }

    protected OwsDomain getOfferingParameter(String service, String version) {
        if (checkListOnlyParentOfferings()) {
            return getOfferingParameter(service, version,
                    getCache().getParentOfferings(getCache().getOfferings(), false, false));
        } else {
            return getOfferingParameter(service, version, getCache().getOfferings());
        }
    }

    protected OwsDomain getOfferingParameter(String service, String version, Collection<String> offerings) {
        return createDomain(SosConstants.GetObservationParams.offering, offerings,
                getProfileHandler().getActiveProfile().isShowFullOperationsMetadataForObservations());
    }

    protected OwsDomain createDomain(Enum<?> name, Collection<String> values, boolean show) {
        if (values == null || values.isEmpty()) {
            return new OwsDomain(name, OwsNoValues.instance());
        } else if (show) {
            return new OwsDomain(name, new OwsAllowedValues(values.stream().map(OwsValue::new)));
        } else {
            return new OwsDomain(name, OwsAnyValue.instance());
        }
    }

    protected OwsDomain getEnvelopeParameter(Enum<?> name, Collection<String> featureIDs) {
        if (featureIDs != null && !featureIDs.isEmpty()) {
            ReferencedEnvelope envelope = getCache().getGlobalEnvelope();
            if (envelope != null && envelope.isSetEnvelope()) {
                return new OwsDomain(name,
                        new OwsAllowedValues(SosHelper.getOwsRangeFromEnvelope(envelope.getEnvelope())));
            }
        }
        return new OwsDomain(name, OwsAnyValue.instance());
    }

    protected Set<String> getResponseFormatsForObservationType(String observationType, String service,
            String version) {
        Set<String> responseFormats = Sets.newHashSet();
        for (Encoder<?, ?> e : getEncoderRepository().getEncoders()) {
            if (e instanceof ObservationEncoder) {
                final ObservationEncoder<?, ?> oe = (ObservationEncoder<?, ?>) e;
                Map<String, Set<SupportedType>> supportedResponseFormatObservationTypes =
                        oe.getSupportedResponseFormatObservationTypes();
                if (supportedResponseFormatObservationTypes != null
                        && !supportedResponseFormatObservationTypes.isEmpty()) {
                    for (final Entry<String, Set<SupportedType>> entry : supportedResponseFormatObservationTypes
                            .entrySet()) {
                        for (SupportedType st : supportedResponseFormatObservationTypes.get(entry.getKey())) {
                            if (st instanceof ObservationType
                                    && observationType.equals(((ObservationType) st).getValue())) {
                                responseFormats.add(entry.getKey());
                            }
                        }
                    }
                }
            }
        }
        return responseFormats;
    }

}
