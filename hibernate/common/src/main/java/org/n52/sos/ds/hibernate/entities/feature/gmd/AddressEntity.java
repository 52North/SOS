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

import java.util.Set;

import com.google.common.base.Strings;

/**
 * Hibernate entity for address.
 * 
 * @author Carsten Hollmann <c.hollmann@52north.org>
 * @since 4.4.0
 *
 */
public class AddressEntity extends AbstractCiEntity {

    private Set<String> deliveryPoint;
    private String city;
    private String administrativeArea;
    private String postalCode;
    private String country;
    private Set<String> electronicMailAddress;

    /**
     * @return the deliveryPoint
     */
    public Set<String> getDeliveryPoint() {
        return deliveryPoint;
    }

    /**
     * @param deliveryPoint
     *            the deliveryPoint to set
     */
    public void setDeliveryPoint(Set<String> deliveryPoint) {
        this.deliveryPoint = deliveryPoint;
    }

    public boolean hasSetDeliveryPoint() {
        return getDeliveryPoint() != null && !getDeliveryPoint().isEmpty();
    }
    
    /**
     * @return the city
     */
    public String getCity() {
        return city;
    }

    /**
     * @param city
     *            the city to set
     */
    public void setCity(String city) {
        this.city = city;
    }
    
    public boolean isSetCity() {
        return !Strings.isNullOrEmpty(getCity());
    }

    /**
     * @return the administrativeArea
     */
    public String getAdministrativeArea() {
        return administrativeArea;
    }

    /**
     * @param administrativeArea
     *            the administrativeArea to set
     */
    public void setAdministrativeArea(String administrativeArea) {
        this.administrativeArea = administrativeArea;
    }
    
    public boolean isSetAdministrativeArea() {
        return !Strings.isNullOrEmpty(getAdministrativeArea());
    }

    /**
     * @return the postalCode
     */
    public String getPostalCode() {
        return postalCode;
    }

    /**
     * @param postalCode
     *            the postalCode to set
     */
    public void setPostalCode(String postalCode) {
        this.postalCode = postalCode;
    }

    public boolean isSetPostalCode() {
        return !Strings.isNullOrEmpty(getPostalCode());
    }
    
    /**
     * @return the country
     */
    public String getCountry() {
        return country;
    }

    /**
     * @param country
     *            the country to set
     */
    public void setCountry(String country) {
        this.country = country;
    }
    
    public boolean isSetCountry() {
        return !Strings.isNullOrEmpty(getCountry());
    }

    /**
     * @return the electronicMailAddress
     */
    public Set<String> getElectronicMailAddress() {
        return electronicMailAddress;
    }

    /**
     * @param electronicMailAddress
     *            the electronicMailAddress to set
     */
    public void setElectronicMailAddress(Set<String> electronicMailAddress) {
        this.electronicMailAddress = electronicMailAddress;
    }
    
    public boolean hasElectronicMailAddress() {
        return getElectronicMailAddress() != null && !getElectronicMailAddress().isEmpty();
    }
}

