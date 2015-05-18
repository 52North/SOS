/**
 * Copyright 2015 52°North Initiative for Geospatial Open Source
 * Software GmbH
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.n52.iceland.ogc.ows;

import org.n52.iceland.util.StringHelper;

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
    private OwsServiceIdentification serviceIdentification;

    /**
     * Service provider, loaded from file.
     */
    private OwsServiceProvider serviceProvider;

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
    public void setServiceIdentification(OwsServiceIdentification serviceIdentification) {
        this.serviceIdentification = serviceIdentification;

    }

    /**
     * Get service identification
     * 
     * @return service identification
     */
    public OwsServiceIdentification getServiceIdentification() {
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
    public void setServiceProvider(OwsServiceProvider serviceProvider) {
        this.serviceProvider = serviceProvider;

    }

    /**
     * Get service provider
     * 
     * @return service provider
     */
    public OwsServiceProvider getServiceProvider() {
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
