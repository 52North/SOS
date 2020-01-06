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

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.google.common.base.Strings;

/**
 * Internal representation of the ISO GMD Address.
 * 
 * @author Carsten Hollmann <c.hollmann@52north.org>
 * @since 4.4.0
 *
 */
public class CiAddress extends AbstractObject {
    
    private List<String> deliveryPoints = new ArrayList<>();
    private String city;
    private String administrativeArea;
    private String postalCode;
    private String country;
    private List<String> electronicMailAddresses = new ArrayList<>();

    /**
     * @return the deliveryPoints
     */
    public List<String> getDeliveryPoints() {
        return deliveryPoints;
    }

    /**
     * @param deliveryPoints
     *            the deliveryPoints to set
     */
    public CiAddress setDeliveryPoints(Collection<String> deliveryPoints) {
        if (electronicMailAddresses != null) {
            this.deliveryPoints.addAll(deliveryPoints);
        }
        return this;
    }
    
    /**
     * @param deliveryPoints
     *            the deliveryPoints to add
     */
    public CiAddress addDeliveryPoints(String deliveryPoints) {
        if (electronicMailAddresses != null) {
            this.deliveryPoints.add(deliveryPoints);
        }
        return this;
    }
    
    public boolean hasDeliveryPoints() {
        return getDeliveryPoints() != null && !getDeliveryPoints().isEmpty();
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
    public CiAddress setCity(String city) {
        this.city = city;
        return this;
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
    public CiAddress setAdministrativeArea(String administrativeArea) {
        this.administrativeArea = administrativeArea;
        return this;
    }

    public boolean isSetAdministrativeArea() {
        return !Strings.isNullOrEmpty(getCity());
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
    public CiAddress setPostalCode(String postalCode) {
        this.postalCode = postalCode;
        return this;
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
    public CiAddress setCountry(String country) {
        this.country = country;
        return this;
    }
    
    public boolean isSetCountry() {
        return !Strings.isNullOrEmpty(getCountry());
    }

    /**
     * @return the electronicMailAddresses
     */
    public List<String> getElectronicMailAddresses() {
        return electronicMailAddresses;
    }

    /**
     * @param electronicMailAddresses
     *            the electronicMailAddresses to set
     */
    public CiAddress setElectronicMailAddresses(Collection<String> electronicMailAddresses) {
        this.electronicMailAddresses.clear();
        if (electronicMailAddresses != null) {
            this.electronicMailAddresses.addAll(electronicMailAddresses);
        }
        return this;
    }
    
    /**
     * @param electronicMailAddresses
     *            the electronicMailAddresses to add
     */
    public CiAddress addElectronicMailAddresses(String electronicMailAddresses) {
        if (electronicMailAddresses != null) {
            this.electronicMailAddresses.add(electronicMailAddresses);
        }
        return this;
    }
    
    public boolean hasElectronicMailAddresses() {
        return getElectronicMailAddresses() != null && !getElectronicMailAddresses().isEmpty();
    }

}
