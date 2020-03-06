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
package org.n52.sos.iso.gmd;

import java.net.URI;

import org.n52.sos.w3c.Nillable;

import com.google.common.base.Strings;

/**
 * Internal representation of the ISO GMD OnlineResource.
 * 
 * @author Carsten Hollmann <c.hollmann@52north.org>
 * @since 4.4.0
 *
 */
public class CiOnlineResource extends AbstractObject {
    
    /* 1..1 */
    private Nillable<URI> linkage;
    /* 0..1 */
    private Nillable<String> protocol;
    /* 0..1 */
    private String applicationProfile;
    /* 0..1 */
    private String name;
    /* 0..1 */
    private String description;
    /* 0..1 */
    private String function;
    
//    <xs:element name="linkage" type="gmd:URL_PropertyType"/>
//    <xs:element name="protocol" type="gco:CharacterString_PropertyType" minOccurs="0"/>
//    <xs:element name="applicationProfile" type="gco:CharacterString_PropertyType" minOccurs="0"/>
//    <xs:element name="name" type="gco:CharacterString_PropertyType" minOccurs="0"/>
//    <xs:element name="description" type="gco:CharacterString_PropertyType" minOccurs="0"/>
//    <xs:element name="function" type="gmd:CI_OnLineFunctionCode_PropertyType" minOccurs="0"/>
    
    
    public CiOnlineResource(Nillable<URI> linkage) {
        this.linkage = linkage;
    }
    
    public CiOnlineResource(URI linkage) {
        this.linkage = Nillable.of(linkage);
    }
    
    public CiOnlineResource(String linkage) {
        this.linkage = Nillable.of(URI.create(linkage));
    }

    /**
     * @return the linkage
     */
    public Nillable<URI> getLinkage() {
        return linkage;
    }

    /**
     * @param linkage the linkage to set
     */
    public void setLinkage(Nillable<URI> linkage) {
        this.linkage = linkage;
    }

    /**
     * @return the protocol
     */
    public Nillable<String> getProtocol() {
        return protocol;
    }

    /**
     * @param protocol the protocol to set
     */
    public void setProtocol(Nillable<String> protocol) {
        this.protocol = protocol;
    }
    
    /**
     * @param protocol the protocol to set
     */
    public void setProtocol(String protocol) {
        this.protocol = Nillable.of(protocol);
    }
    
    public boolean isSetProtocol() {
        return getProtocol() != null;
    }

    /**
     * @return the applicationProfile
     */
    public String getApplicationProfile() {
        return applicationProfile;
    }

    /**
     * @param applicationProfile the applicationProfile to set
     */
    public void setApplicationProfile(String applicationProfile) {
        this.applicationProfile = applicationProfile;
    }
    
    public boolean isSetApplicationProfile() {
        return !Strings.isNullOrEmpty(getApplicationProfile());
    }

    /**
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * @param name the name to set
     */
    public void setName(String name) {
        this.name = name;
    }
    
    public boolean isSetName() {
        return !Strings.isNullOrEmpty(getName());
    }

    /**
     * @return the description
     */
    public String getDescription() {
        return description;
    }

    /**
     * @param description the description to set
     */
    public void setDescription(String description) {
        this.description = description;
    }
    
    public boolean isSetDescription() {
        return !Strings.isNullOrEmpty(getDescription());
    }

    /**
     * @return the function
     */
    public String getFunction() {
        return function;
    }

    /**
     * @param function the function to set
     */
    public void setFunction(String function) {
        this.function = function;
    }
    
    public boolean isSetFunction() {
        return !Strings.isNullOrEmpty(getFunction());
    }

}
