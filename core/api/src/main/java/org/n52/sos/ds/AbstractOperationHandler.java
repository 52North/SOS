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

import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import javax.inject.Inject;

import org.n52.iceland.binding.Binding;
import org.n52.iceland.binding.BindingRepository;
import org.n52.iceland.cache.ContentCacheController;
import org.n52.iceland.coding.OperationKey;
import org.n52.iceland.ds.OperationHandler;
import org.n52.iceland.ds.OperationHandlerKey;
import org.n52.iceland.exception.ows.NoApplicableCodeException;
import org.n52.iceland.exception.ows.OwsExceptionReport;
import org.n52.iceland.ogc.ows.Constraint;
import org.n52.iceland.ogc.ows.DCP;
import org.n52.iceland.ogc.ows.OwsOperation;
import org.n52.iceland.ogc.ows.OwsParameterValuePossibleValues;
import org.n52.iceland.ogc.sos.SosConstants;
import org.n52.iceland.service.ServiceConfiguration;
import org.n52.iceland.util.collections.MultiMaps;
import org.n52.iceland.util.collections.SetMultiMap;
import org.n52.iceland.util.http.HTTPHeaders;
import org.n52.iceland.util.http.HTTPMethods;
import org.n52.iceland.util.http.MediaType;
import org.n52.sos.cache.SosContentCache;
import org.n52.sos.service.Configurator;
import org.n52.sos.service.profile.ProfileHandler;
import org.n52.sos.util.SosHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Renamed, in version 4.x called AbstractOperationDAO
 *
 * @since 5.0.0
 *
 */
public abstract class AbstractOperationHandler implements OperationHandler {

    private static final Logger LOG = LoggerFactory.getLogger(AbstractOperationHandler.class);

    private final OperationHandlerKey key;
    private ContentCacheController contentCacheController;

    public AbstractOperationHandler(String service, String operationName) {
        this.key = new OperationHandlerKey(service, operationName);
    }

    @Inject
    public void setContentCacheController(ContentCacheController contentCacheController) {
        this.contentCacheController = contentCacheController;
    }

    protected ContentCacheController getCacheController() {
        return this.contentCacheController;
    }

    @Deprecated
    protected Configurator getConfigurator() {
        // FIXME use @Inject
        return Configurator.getInstance();
    }


    @Deprecated
    protected BindingRepository getBindingRepository() {
        // FIXME use @Inject
        return BindingRepository.getInstance();
    }

    @Deprecated
    protected ProfileHandler getProfileHandler() {
        // FIXME use @Inject
        return ProfileHandler.getInstance();
    }

    @Deprecated
    protected ServiceConfiguration getServiceConfiguration() {
        // FIXME use @Inject
        return ServiceConfiguration.getInstance();
    }

    // TODO check if necessary in feature
    @Override
    public String getOperationName() {
        return getKey().getOperationName();
    }

    @Deprecated
    public OperationHandlerKey getOperationHandlerKey() {
        return getKey();
    }

    @Override
    public Set<OperationHandlerKey> getKeys() {
        return Collections.singleton(getKey());
    }

    public OperationHandlerKey getKey() {
        return this.key;
    }

    @Override
    public OwsOperation getOperationsMetadata(String service, String version) throws OwsExceptionReport {
        Map<String, Set<DCP>> dcp = getDCP(new OperationKey(service, version, getKey().getOperationName()));
        if (dcp == null || dcp.isEmpty()) {
            LOG.debug("Operation {} for Service {} not available due to empty DCP map.", getOperationName(), getKey().getService());
            return null;
        }
        OwsOperation operation = new OwsOperation();
        operation.setDcp(dcp);
        operation.setOperationName(getOperationName());
        setOperationsMetadata(operation, service, version);
        return operation;
    }

    protected SosContentCache getCache() {
        return (SosContentCache) getCacheController().getCache();
    }

    /**
     * Get the HTTP DCPs for a operation
     *
     * @param operationKey
     *            the decoderKey
     * @return Map with DCPs for the service operation
     *
     * @throws OwsExceptionReport
     */
    protected Map<String, Set<DCP>> getDCP(OperationKey operationKey) throws OwsExceptionReport {
        SetMultiMap<String, DCP> dcps = MultiMaps.newSetMultiMap();
        String serviceURL = getServiceConfiguration().getServiceURL();

        try {
            // TODO support for operation/method specific supported request and
            // response mediatypes
            for (Entry<String, Binding> entry : getBindingRepository().getBindingsByPath().entrySet()) {
                String url = serviceURL + entry.getKey();
                Binding binding = entry.getValue();
                Constraint constraint = null;
                if (binding.getSupportedEncodings() != null && !binding.getSupportedEncodings().isEmpty()) {
                    SortedSet<String> ss = new TreeSet<>();
                    for (MediaType mt : binding.getSupportedEncodings()) {
                        ss.add(mt.toString());
                    }
                    constraint = new Constraint(HTTPHeaders.CONTENT_TYPE, new OwsParameterValuePossibleValues(ss));
                }
                if (binding.checkOperationHttpGetSupported(operationKey)) {
                    dcps.add(HTTPMethods.GET, new DCP(url + "?", constraint));
                }
                if (binding.checkOperationHttpPostSupported(operationKey)) {
                    dcps.add(HTTPMethods.POST, new DCP(url, constraint));
                }
                if (binding.checkOperationHttpPutSupported(operationKey)) {
                    dcps.add(HTTPMethods.PUT, new DCP(url, constraint));
                }
                if (binding.checkOperationHttpDeleteSupported(operationKey)) {
                    dcps.add(HTTPMethods.DELETE, new DCP(url, constraint));
                }
            }
        } catch (Exception e) {
            // FIXME valid exception
            throw new NoApplicableCodeException().causedBy(e);
        }

        return dcps;
    }

    protected abstract void setOperationsMetadata(OwsOperation operation, String service, String version)
            throws OwsExceptionReport;

    protected void addProcedureParameter(OwsOperation opsMeta) {
        addProcedureParameter(opsMeta, getCache().getProcedures());
    }

    protected void addProcedureParameter(OwsOperation opsMeta, Collection<String> procedures) {
        if (getProfileHandler().getActiveProfile().isShowFullOperationsMetadataForObservations()) {
            opsMeta.addPossibleValuesParameter(SosConstants.GetObservationParams.procedure, procedures);
        } else {
            opsMeta.addAnyParameterValue(SosConstants.GetObservationParams.procedure);
        }
    }

    protected void addFeatureOfInterestParameter(OwsOperation opsMeta, String version) {
        addFeatureOfInterestParameter(opsMeta, SosHelper.getFeatureIDs(getCache().getFeaturesOfInterest(), version));
    }

    protected void addFeatureOfInterestParameter(OwsOperation opsMeta, Collection<String> featuresOfInterest) {
        if (getProfileHandler().getActiveProfile().isShowFullOperationsMetadataForObservations()) {
        opsMeta.addPossibleValuesParameter(SosConstants.GetObservationParams.featureOfInterest, featuresOfInterest);
        } else {
            opsMeta.addAnyParameterValue(SosConstants.GetObservationParams.featureOfInterest);
        }
    }

    protected void addObservablePropertyParameter(OwsOperation opsMeta) {
        addObservablePropertyParameter(opsMeta, getCache().getObservableProperties());
    }

    protected void addObservablePropertyParameter(OwsOperation opsMeta, Collection<String> observedProperties) {
        if (getProfileHandler().getActiveProfile().isShowFullOperationsMetadataForObservations()) {
        opsMeta.addPossibleValuesParameter(SosConstants.GetObservationParams.observedProperty, observedProperties);
        } else {
            opsMeta.addAnyParameterValue(SosConstants.GetObservationParams.observedProperty);
        }
    }

    protected void addOfferingParameter(OwsOperation opsMeta) {
        addOfferingParameter(opsMeta, getCache().getOfferings());
    }

    protected void addOfferingParameter(OwsOperation opsMeta, Collection<String> offerings) {
        if (getProfileHandler().getActiveProfile().isShowFullOperationsMetadataForObservations()) {
        opsMeta.addPossibleValuesParameter(SosConstants.GetObservationParams.offering, offerings);
        } else {
            opsMeta.addAnyParameterValue(SosConstants.GetObservationParams.offering);
        }
    }
}
