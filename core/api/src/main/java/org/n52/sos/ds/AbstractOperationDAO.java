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

import org.n52.sos.binding.Binding;
import org.n52.sos.binding.BindingRepository;
import org.n52.sos.cache.ContentCache;
import org.n52.sos.coding.OperationKey;
import org.n52.sos.exception.ows.NoApplicableCodeException;
import org.n52.sos.ogc.ows.Constraint;
import org.n52.sos.ogc.ows.DCP;
import org.n52.sos.ogc.ows.OwsExceptionReport;
import org.n52.sos.ogc.ows.OwsOperation;
import org.n52.sos.ogc.ows.OwsParameterValuePossibleValues;
import org.n52.sos.ogc.sos.SosConstants;
import org.n52.sos.service.Configurator;
import org.n52.sos.service.ServiceConfiguration;
import org.n52.sos.util.MultiMaps;
import org.n52.sos.util.SetMultiMap;
import org.n52.sos.util.SosHelper;
import org.n52.sos.util.http.HTTPHeaders;
import org.n52.sos.util.http.HTTPMethods;
import org.n52.sos.util.http.MediaType;

/**
 * @since 4.0.0
 *
 */
public abstract class AbstractOperationDAO implements OperationDAO {

    private static final Logger LOG = LoggerFactory.getLogger(AbstractOperationDAO.class);

    private final OperationDAOKeyType operationDAOIdentifier;

    public AbstractOperationDAO(String service, String operationName) {
        operationDAOIdentifier = new OperationDAOKeyType(service, operationName);
    }

    // TODO check if necessary in feature
    @Override
    public String getOperationName() {
        return this.operationDAOIdentifier.getOperationName();
    }

    @Override
    public OperationDAOKeyType getOperationDAOKeyType() {
        return this.operationDAOIdentifier;
    }

    @Override
    public OwsOperation getOperationsMetadata(String service, String version) throws OwsExceptionReport {
        Map<String, Set<DCP>> dcp =
                getDCP(new OperationKey(service, version, getOperationDAOKeyType().getOperationName()));
        if (dcp == null || dcp.isEmpty()) {
            LOG.debug("Operation {} for Service {} not available due to empty DCP map.", getOperationName(),
                    getOperationDAOKeyType().getService());
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
    public Set<String> getConformanceClasses() {
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
     * @return Map with DCPs for the SOS operation
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
        if (getConfigurator().getProfileHandler().getActiveProfile().isShowFullOperationsMetadataForObservations()) {
            opsMeta.addPossibleValuesParameter(SosConstants.GetObservationParams.procedure, procedures);
        } else {
            opsMeta.addAnyParameterValue(SosConstants.GetObservationParams.procedure);
        }
    }

    protected void addFeatureOfInterestParameter(OwsOperation opsMeta, String version) {
        addFeatureOfInterestParameter(opsMeta, SosHelper.getFeatureIDs(getCache().getFeaturesOfInterest(), version));
    }

    protected void addFeatureOfInterestParameter(OwsOperation opsMeta, Collection<String> featuresOfInterest) {
        if (getConfigurator().getProfileHandler().getActiveProfile().isShowFullOperationsMetadataForObservations()) {
        opsMeta.addPossibleValuesParameter(SosConstants.GetObservationParams.featureOfInterest, featuresOfInterest);
        } else {
            opsMeta.addAnyParameterValue(SosConstants.GetObservationParams.featureOfInterest);
        }
    }

    protected void addObservablePropertyParameter(OwsOperation opsMeta) {
        addObservablePropertyParameter(opsMeta, getCache().getObservableProperties());
    }

    protected void addObservablePropertyParameter(OwsOperation opsMeta, Collection<String> observedProperties) {
        if (getConfigurator().getProfileHandler().getActiveProfile().isShowFullOperationsMetadataForObservations()) {
        opsMeta.addPossibleValuesParameter(SosConstants.GetObservationParams.observedProperty, observedProperties);
        } else {
            opsMeta.addAnyParameterValue(SosConstants.GetObservationParams.observedProperty);
        }
    }

    protected void addOfferingParameter(OwsOperation opsMeta) {
        addOfferingParameter(opsMeta, getCache().getOfferings());
    }

    protected void addOfferingParameter(OwsOperation opsMeta, Collection<String> offerings) {
        if (getConfigurator().getProfileHandler().getActiveProfile().isShowFullOperationsMetadataForObservations()) {
        opsMeta.addPossibleValuesParameter(SosConstants.GetObservationParams.offering, offerings);
        } else {
            opsMeta.addAnyParameterValue(SosConstants.GetObservationParams.offering);
        }
    }
}
