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
package org.n52.iceland.ogc.sos;


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
