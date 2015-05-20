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
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.n52.iceland.binding.Binding;
import org.n52.iceland.binding.BindingRepository;
import org.n52.iceland.cache.ContentCache;
import org.n52.iceland.coding.OperationKey;
import org.n52.iceland.ds.OperationHandler;
import org.n52.iceland.ds.OperationHandlerKeyType;
import org.n52.iceland.exception.ows.NoApplicableCodeException;
import org.n52.iceland.ogc.ows.Constraint;
import org.n52.iceland.ogc.ows.DCP;
import org.n52.iceland.ogc.ows.OwsExceptionReport;
import org.n52.iceland.ogc.ows.OwsOperation;
import org.n52.iceland.ogc.ows.OwsParameterValuePossibleValues;
import org.n52.iceland.ogc.sos.SosConstants;
import org.n52.iceland.service.Configurator;
import org.n52.iceland.service.ServiceConfiguration;
import org.n52.iceland.util.MultiMaps;
import org.n52.iceland.util.SetMultiMap;
import org.n52.iceland.util.http.HTTPHeaders;
import org.n52.iceland.util.http.HTTPMethods;
import org.n52.iceland.util.http.MediaType;
import org.n52.sos.service.profile.ProfileHandler;
import org.n52.sos.util.SosHelper;

/**
 * Renamed, in version 4.x called AbstractOperationDAO
 *
 * @since 5.0.0
 *
 */
public abstract class AbstractOperationHandler implements OperationHandler {

    private static final Logger LOG = LoggerFactory.getLogger(AbstractOperationHandler.class);

    private final OperationHandlerKeyType operationHandlerIdentifier;

    public AbstractOperationHandler(String service, String operationName) {
        operationHandlerIdentifier = new OperationHandlerKeyType(service, operationName);
    }

    // TODO check if necessary in feature
    @Override
    public String getOperationName() {
        return this.operationHandlerIdentifier.getOperationName();
    }

    @Override
    public OperationHandlerKeyType getOperationHandlerKeyType() {
        return this.operationHandlerIdentifier;
    }

    @Override
    public OwsOperation getOperationsMetadata(String service, String version) throws OwsExceptionReport {
        Map<String, Set<DCP>> dcp =
                getDCP(new OperationKey(service, version, getOperationHandlerKeyType().getOperationName()));
        if (dcp == null || dcp.isEmpty()) {
            LOG.debug("Operation {} for Service {} not available due to empty DCP map.", getOperationName(),
                    getOperationHandlerKeyType().getService());
            return null;
        }
        OwsOperation operation = new OwsOperation();
        operation.setDcp(dcp);
        operation.setOperationName(getOperationName());
        setOperationsMetadata(operation, service, version);
        return operation;
    }

    // @Override
    // /* provide a default implementation for extension-less DAO's */
    // public SosCapabilitiesExtension getExtension() throws OwsExceptionReport
    // {
    // return null;
    // }

    @Override
    public Set<String> getConformanceClasses(String service, String version) {
        return Collections.emptySet();
    }

    protected ContentCache getCache() {
        return getConfigurator().getCache();
    }

    protected Configurator getConfigurator() {
        return Configurator.getInstance();
    }

    /**
     * Get the HTTP DCPs for a operation
     *
     * @param decoderKey
     *            the decoderKey
     * @return Map with DCPs for the service operation
     *
     * @throws OwsExceptionReport
     */
    protected Map<String, Set<DCP>> getDCP(OperationKey decoderKey) throws OwsExceptionReport {
        SetMultiMap<String, DCP> dcps = MultiMaps.newSetMultiMap();
        String serviceURL = ServiceConfiguration.getInstance().getServiceURL();
        try {
            // TODO support for operation/method specific supported request and
            // response mediatypes
            for (Binding binding : BindingRepository.getInstance().getBindings().values()) {
                String url = serviceURL + binding.getUrlPattern();
                Constraint constraint = null;
                if (binding.getSupportedEncodings() != null && !binding.getSupportedEncodings().isEmpty()) {
                    SortedSet<String> ss = new TreeSet<String>();
                    for (MediaType mt : binding.getSupportedEncodings()) {
                        ss.add(mt.toString());
                    }
                    constraint = new Constraint(HTTPHeaders.CONTENT_TYPE, new OwsParameterValuePossibleValues(ss));
                }
                if (binding.checkOperationHttpGetSupported(decoderKey)) {
                    dcps.add(HTTPMethods.GET, new DCP(url + "?", constraint));
                }
                if (binding.checkOperationHttpPostSupported(decoderKey)) {
                    dcps.add(HTTPMethods.POST, new DCP(url, constraint));
                }
                if (binding.checkOperationHttpPutSupported(decoderKey)) {
                    dcps.add(HTTPMethods.PUT, new DCP(url, constraint));
                }
                if (binding.checkOperationHttpDeleteSupported(decoderKey)) {
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
        if (ProfileHandler.getInstance().getActiveProfile().isShowFullOperationsMetadataForObservations()) {
            opsMeta.addPossibleValuesParameter(SosConstants.GetObservationParams.procedure, procedures);
        } else {
            opsMeta.addAnyParameterValue(SosConstants.GetObservationParams.procedure);
        }
    }

    protected void addFeatureOfInterestParameter(OwsOperation opsMeta, String version) {
        addFeatureOfInterestParameter(opsMeta, SosHelper.getFeatureIDs(getCache().getFeaturesOfInterest(), version));
    }

    protected void addFeatureOfInterestParameter(OwsOperation opsMeta, Collection<String> featuresOfInterest) {
        if (ProfileHandler.getInstance().getActiveProfile().isShowFullOperationsMetadataForObservations()) {
        opsMeta.addPossibleValuesParameter(SosConstants.GetObservationParams.featureOfInterest, featuresOfInterest);
        } else {
            opsMeta.addAnyParameterValue(SosConstants.GetObservationParams.featureOfInterest);
        }
    }

    protected void addObservablePropertyParameter(OwsOperation opsMeta) {
        addObservablePropertyParameter(opsMeta, getCache().getObservableProperties());
    }

    protected void addObservablePropertyParameter(OwsOperation opsMeta, Collection<String> observedProperties) {
        if (ProfileHandler.getInstance().getActiveProfile().isShowFullOperationsMetadataForObservations()) {
        opsMeta.addPossibleValuesParameter(SosConstants.GetObservationParams.observedProperty, observedProperties);
        } else {
            opsMeta.addAnyParameterValue(SosConstants.GetObservationParams.observedProperty);
        }
    }

    protected void addOfferingParameter(OwsOperation opsMeta) {
        addOfferingParameter(opsMeta, getCache().getOfferings());
    }

    protected void addOfferingParameter(OwsOperation opsMeta, Collection<String> offerings) {
        if (ProfileHandler.getInstance().getActiveProfile().isShowFullOperationsMetadataForObservations()) {
        opsMeta.addPossibleValuesParameter(SosConstants.GetObservationParams.offering, offerings);
        } else {
            opsMeta.addAnyParameterValue(SosConstants.GetObservationParams.offering);
        }
    }
}
