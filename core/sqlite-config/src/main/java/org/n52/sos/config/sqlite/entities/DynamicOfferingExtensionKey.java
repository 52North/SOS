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
package org.n52.sos.config.sqlite.entities;

import java.io.Serializable;

import javax.persistence.Embeddable;

import org.n52.sos.ogc.swes.OfferingExtensionKey;

/**
 * Storage key for the entity {@link DynamicOfferingExtension}
 * 
 * @author Carsten Hollmann <c.hollmann@52north.org>
 * @since 4.1.0
 * 
 */
@Embeddable
public class DynamicOfferingExtensionKey implements Serializable {
    private static final long serialVersionUID = -7127147660584447908L;
    
    private String service;

    private String version;

    private String domain;

    /**
     * constructor
     * 
     * @param service
     *            the service to set
     * @param version
     *            the version to set
     * @param domain
     *            the domain to set
     */
    public DynamicOfferingExtensionKey(String service, String version, String domain) {
        setService(service);
        setVersion(version);
        setDomain(domain);
    }

    /**
     * constructor
     * 
     * @param oek
     *            the {@link OfferingExtensionKey} to set
     */
    public DynamicOfferingExtensionKey(OfferingExtensionKey key) {
        this(key.getService(), key.getVersion(), key.getDomain());
    }

    /**
     * constructor
     */
    public DynamicOfferingExtensionKey() {
        this(null, null, null);
    }
    
    /**
     * Get the key service
     * 
     * @return the service
     */
    public String getService() {
        return service;
    }

    /**
     * Set the key service
     * 
     * @param service
     *            the service to set
     * @return this
     */
    public DynamicOfferingExtensionKey setService(String service) {
        this.service = service;
        return this;
    }

    /**
     * Get the key version
     * 
     * @return the version
     */
    public String getVersion() {
        return version;
    }

    /**
     * Set the key service
     * 
     * @param version
     *            the version to seet
     * @return this
     */
    public DynamicOfferingExtensionKey setVersion(String version) {
        this.version = version;
        return this;
    }

    /**
     * Get the key domain
     * 
     * @return the domain
     */
    public String getDomain() {
        return domain;
    }

    /**
     * Set the key domain
     * 
     * @param domain
     *            the domain to set
     * @return this
     */
    public DynamicOfferingExtensionKey setDomain(String domain) {
        this.domain = domain;
        return this;
    }
}
