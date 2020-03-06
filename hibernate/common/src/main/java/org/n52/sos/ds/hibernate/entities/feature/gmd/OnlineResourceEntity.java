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
package org.n52.sos.ds.hibernate.entities.feature.gmd;

import com.google.common.base.Strings;

/**
 * Hibernate entity for onlineResource.
 * 
 * @author Carsten Hollmann <c.hollmann@52north.org>
 * @since 4.4.0
 *
 */
public class OnlineResourceEntity extends AbstractCiEntity {

    private String linkage;
    private String protocol;
    private String applicationProfile;
    private String function;

    /**
     * @return the linkage
     */
    public String getLinkage() {
        return linkage;
    }

    /**
     * @param linkage
     *            the linkage to set
     */
    public void setLinkage(String linkage) {
        this.linkage = linkage;
    }
    
    public boolean isSetLinkage() {
        return !Strings.isNullOrEmpty(getLinkage());
    }

    /**
     * @return the protocol
     */
    public String getProtocol() {
        return protocol;
    }

    /**
     * @param protocol
     *            the protocol to set
     */
    public void setProtocol(String protocol) {
        this.protocol = protocol;
    }
    
    public boolean isSetProtocol() {
        return !Strings.isNullOrEmpty(getProtocol());
    }

    /**
     * @return the applicationProfile
     */
    public String getApplicationProfile() {
        return applicationProfile;
    }

    /**
     * @param applicationProfile
     *            the applicationProfile to set
     */
    public void setApplicationProfile(String applicationProfile) {
        this.applicationProfile = applicationProfile;
    }
    
    public boolean isSetApplicationProfile() {
        return !Strings.isNullOrEmpty(getApplicationProfile());
    }

    /**
     * @return the function
     */
    public String getFunction() {
        return function;
    }

    /**
     * @param function
     *            the function to set
     */
    public void setFunction(String function) {
        this.function = function;
    }
    
    public boolean isSetFunction() {
        return !Strings.isNullOrEmpty(getFunction());
    }

}
