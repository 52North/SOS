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
package org.n52.sos.ogc.sos;

/**
 * {@link CapabilitiesExtension} key class to identify CapabilitiesExtensions.
 * 
 * @since 4.0.0
 * 
 */
public class CapabilitiesExtensionKey implements Comparable<CapabilitiesExtensionKey> {
    private String service;

    private String version;

    /**
     * Default constructor
     */
    public CapabilitiesExtensionKey() {
        super();
    }

    /**
     * Constructor
     * 
     * @param service
     *            Related service
     * @param version
     *            Related version
     */
    public CapabilitiesExtensionKey(String service, String version) {
        super();
        this.service = service;
        this.version = version;
    }

    /**
     * Get the key service
     * 
     * @return Key servcice
     */
    public String getService() {
        return service;
    }

    /**
     * Set the key service
     * 
     * @param service
     *            service to set
     */
    public void setService(String service) {
        this.service = service;
    }

    /**
     * Get the key version
     * 
     * @return Key version
     */
    public String getVersion() {
        return version;
    }

    /**
     * Set the key version
     * 
     * @param version
     *            version to set
     */
    public void setVersion(String version) {
        this.version = version;
    }

    @Override
    public int compareTo(CapabilitiesExtensionKey o) {
        if (o instanceof CapabilitiesExtensionKey) {
            if (service.equals(o.service) && version.equals(o.version)) {
                return 0;
            }
            return 1;
        }
        return -1;
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(Object paramObject) {
        if (service != null && version != null && paramObject instanceof CapabilitiesExtensionKey) {
            CapabilitiesExtensionKey toCheck = (CapabilitiesExtensionKey) paramObject;
            return (service.equals(toCheck.service) && version.equals(toCheck.version));
        }
        return false;
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        final int prime = 31;
        int hash = 7;
        hash = prime * hash + ((this.service != null) ? this.service.hashCode() : 0);
        hash = prime * hash + ((this.version != null) ? this.version.hashCode() : 0);
        return hash;
    }

    @Override
    public String toString() {
        return String.format("CapabilitiesExtensionKey[service=%s, version=%s]", this.service, this.version);
    }
}
