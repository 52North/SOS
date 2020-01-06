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
 * Hibernate entity for contact.
 * 
 * @author Carsten Hollmann <c.hollmann@52north.org>
 * @since 4.4.0
 *
 */
public class ContactEntity extends AbstractCiEntity {
    
    private TelephoneEntity phone;
    private AddressEntity address;
    private OnlineResourceEntity onlineResource;
    private String hoursOfService;
    private String contactInstructions;

    /**
     * @return the phone
     */
    public TelephoneEntity getPhone() {
        return phone;
    }

    /**
     * @param phone
     *            the phone to set
     */
    public void setPhone(TelephoneEntity phone) {
        this.phone = phone;
    }
    
    public boolean isSetPhone() {
        return getPhone() != null;
    }

    /**
     * @return the address
     */
    public AddressEntity getAddress() {
        return address;
    }

    /**
     * @param address
     *            the address to set
     */
    public void setAddress(AddressEntity address) {
        this.address = address;
    }
    
    public boolean isSetAddress() {
        return getAddress() != null;
    }

    /**
     * @return the onlineResource
     */
    public OnlineResourceEntity getOnlineResource() {
        return onlineResource;
    }

    /**
     * @param onlineResource
     *            the onlineResource to set
     */
    public void setOnlineResource(OnlineResourceEntity onlineResource) {
        this.onlineResource = onlineResource;
    }
    
    public boolean isSetOnlineResource() {
        return getOnlineResource() != null;
    }

    /**
     * @return the hoursOfService
     */
    public String getHoursOfService() {
        return hoursOfService;
    }

    /**
     * @param hoursOfService
     *            the hoursOfService to set
     */
    public void setHoursOfService(String hoursOfService) {
        this.hoursOfService = hoursOfService;
    }
    
    public boolean isSetHoursOfService() {
        return !Strings.isNullOrEmpty(getHoursOfService());
    }

    /**
     * @return the contactInstructions
     */
    public String getContactInstructions() {
        return contactInstructions;
    }

    /**
     * @param contactInstructions
     *            the contactInstructions to set
     */
    public void setContactInstructions(String contactInstructions) {
        this.contactInstructions = contactInstructions;
    }
    
    public boolean isSetContactInstructions() {
        return !Strings.isNullOrEmpty(getContactInstructions());
    }

}
