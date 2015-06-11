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
package org.n52.sos.ogc.ows;

import org.n52.sos.util.StringHelper;

/**
 * @since 4.0.0
 * 
 */
public abstract class OwsCapabilities {

    private String service;

    private String version;

    private String updateSequence;

    /**
     * Service identification, loaded from file.
     */
    private SosServiceIdentification serviceIdentification;

    /**
     * Service provider, loaded from file.
     */
    private SosServiceProvider serviceProvider;

    /**
     * Operations meta data for all supported operations.
     */
    private OwsOperationsMetadata operationsMetadata;

    public OwsCapabilities(String service, String version) {
        this.version = version;
    }
    
    /**
     * @param service the service
     */
    public void setService(String service) {
    	this.service = service;
    }

    /**
     * @return the service
     */
    public String getService() {
        return this.service;
    }

    /**
     * @return the version
     */
    public String getVersion() {
        return version;
    }

    /**
     * @param version
     *            the version to set
     */
    public void setVersion(String version) {
        this.version = version;
    }

    public boolean isSetVersion() {
        return StringHelper.isNotEmpty(getVersion());
    }

    /**
     * @return the updateSequence
     */
    public String getUpdateSequence() {
        return updateSequence;
    }

    /**
     * @param updateSequence
     *            the updateSequence to set
     */
    public void setUpdateSequence(String updateSequence) {
        this.updateSequence = updateSequence;
    }

    public boolean isSetUpdateSequence() {
        return StringHelper.isNotEmpty(getUpdateSequence());
    }

    /**
     * Set service identification
     * 
     * @param serviceIdentification
     *            service identification
     */
    public void setServiceIdentification(SosServiceIdentification serviceIdentification) {
        this.serviceIdentification = serviceIdentification;

    }

    /**
     * Get service identification
     * 
     * @return service identification
     */
    public SosServiceIdentification getServiceIdentification() {
        return serviceIdentification;
    }

    public boolean isSetServiceIdentification() {
        return getServiceIdentification() != null;
    }

    /**
     * Set service provider
     * 
     * @param serviceProvider
     *            service provider
     */
    public void setServiceProvider(SosServiceProvider serviceProvider) {
        this.serviceProvider = serviceProvider;

    }

    /**
     * Get service provider
     * 
     * @return service provider
     */
    public SosServiceProvider getServiceProvider() {
        return serviceProvider;
    }

    public boolean isSetServiceProvider() {
        return getServiceProvider() != null;
    }

    /**
     * Get operations metadata
     * 
     * @return operations metadata
     */
    public OwsOperationsMetadata getOperationsMetadata() {
        return operationsMetadata;
    }

    /**
     * Set operations metadata
     * 
     * @param operationsMetadata
     *            operations metadata
     */
    public void setOperationsMetadata(OwsOperationsMetadata operationsMetadata) {
        this.operationsMetadata = operationsMetadata;
    }

    public boolean isSetOperationsMetadata() {
        return getOperationsMetadata() != null && !getOperationsMetadata().isEmpty();
    }
}
