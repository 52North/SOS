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

import org.apache.xmlbeans.XmlObject;
import org.n52.sos.util.StringHelper;

/**
 * @since 4.0.0
 * 
 */
public class SosServiceProvider {
    private XmlObject serviceProvider;

    private String name;

    private String site;

    private String individualName;

    private String positionName;

    private String phone;

    private String deliveryPoint;

    private String city;

    private String postalCode;

    private String country;

    private String mailAddress;

    private String administrativeArea;

    public String getName() {
        return name;
    }

    public boolean hasName() {
        return StringHelper.isNotEmpty(getName());
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSite() {
        return site;
    }

    public boolean hasSite() {
        return StringHelper.isNotEmpty(getSite());
    }

    public void setSite(String site) {
        this.site = site;
    }

    public String getIndividualName() {
        return individualName;
    }

    public boolean hasIndividualName() {
        return StringHelper.isNotEmpty(getIndividualName());
    }

    public void setIndividualName(String individualName) {
        this.individualName = individualName;
    }

    public String getPositionName() {
        return positionName;
    }

    public boolean hasPositionName() {
        return StringHelper.isNotEmpty(getPositionName());
    }

    public void setPositionName(String positionName) {
        this.positionName = positionName;
    }

    public String getPhone() {
        return phone;
    }

    public boolean hasPhone() {
        return StringHelper.isNotEmpty(getPhone());
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getDeliveryPoint() {
        return deliveryPoint;
    }

    public boolean hasDeliveryPoint() {
        return StringHelper.isNotEmpty(getDeliveryPoint());
    }

    public void setDeliveryPoint(String deliveryPoint) {
        this.deliveryPoint = deliveryPoint;
    }

    public String getCity() {
        return city;
    }

    public boolean hasCity() {
        return StringHelper.isNotEmpty(getCity());
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getPostalCode() {
        return postalCode;
    }

    public boolean hasPostalCode() {
        return StringHelper.isNotEmpty(getPostalCode());
    }

    public void setPostalCode(String postalCode) {
        this.postalCode = postalCode;
    }

    public String getCountry() {
        return country;
    }

    public boolean hasCountry() {
        return StringHelper.isNotEmpty(getCountry());
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getMailAddress() {
        return mailAddress;
    }

    public boolean hasMailAddress() {
        return StringHelper.isNotEmpty(getMailAddress());
    }

    public void setMailAddress(String mailAddress) {
        this.mailAddress = mailAddress;
    }

    public String getAdministrativeArea() {
        return administrativeArea;
    }

    public boolean hasAdministrativeArea() {
        return StringHelper.isNotEmpty(getAdministrativeArea());
    }

    public void setAdministrativeArea(String administrativeArea) {
        this.administrativeArea = administrativeArea;
    }

    public XmlObject getServiceProvider() {
        return serviceProvider;
    }

    public void setServiceProvider(XmlObject serviceProvider) {
        this.serviceProvider = serviceProvider;
    }
}
