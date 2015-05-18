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

import org.apache.xmlbeans.XmlObject;
import org.n52.iceland.util.StringHelper;

/**
 * @since 4.0.0
 * 
 */
public class OwsServiceProvider {
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
