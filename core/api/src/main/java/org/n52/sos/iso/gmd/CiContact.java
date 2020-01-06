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
import java.net.URISyntaxException;
import java.util.List;

import org.n52.sos.w3c.Nillable;
import org.n52.sos.w3c.xlink.Reference;
import org.n52.sos.w3c.xlink.Referenceable;

import com.google.common.collect.Lists;

/**
 * Internal representation of the ISO GMD Contact.
 * 
 * @author Carsten Hollmann <c.hollmann@52north.org>
 * @since 4.4.0
 *
 */
public class CiContact extends AbstractObject {
    
    private Referenceable<CiTelephone> phone;
    private Referenceable<CiAddress> address;
    private Referenceable<CiOnlineResource> onlineResource;
    private Nillable<String> hoursOfService;
    private Nillable<String> contactInstructions;
    
    /**
     * @return the phone
     */
    public Referenceable<CiTelephone> getPhone() {
        return phone;
    }

    /**
     * @param phone the phone to set
     */
    public void setPhone(Referenceable<CiTelephone> phone) {
        this.phone = phone;
    }

    public boolean isSetPhone() {
        return getPhone() != null;
    }
    
    private boolean isSetPhoneInstance() {
        return getPhone() != null 
                && getPhone().isInstance() 
                && getPhone().getInstance().isPresent();
    }
    
    private CiTelephone getPhoneInstance() {
        return getPhone().getInstance().get();
    }

    public boolean isSetPhoneVoice() {
        return isSetPhoneInstance()
                && getPhoneInstance().isSetVoice();
    }

    public List<String> getPhoneVoice() {
        if (isSetPhoneVoice()) {
            return getPhoneInstance().getVoice();
        }
        return null;
    }

    public CiContact setPhoneVoice(final List<String> phoneVoice) {
        if (isSetPhoneInstance()) {
            getPhoneInstance().setVoice(phoneVoice);
        } else {
            setPhone(Referenceable.of(new CiTelephone().setVoice(phoneVoice)));
        }
        return this;
    }

    public CiContact addPhoneVoice(final String phoneVoice) {
        if (isSetPhoneInstance()) {
            getPhoneInstance().addVoice(phoneVoice);
        } else {
            setPhone(Referenceable.of(new CiTelephone().addVoice(phoneVoice)));
        }
        return this;
    }

    public boolean isSetPhoneFax() {
        return isSetPhoneInstance()
                && getPhoneInstance().isSetFacsimile();
    }

    public List<String> getPhoneFax() {
        if (isSetPhoneFax()) {
            return getPhoneInstance().getFacsimile();
        }
        return null;
    }

    public CiContact addPhoneFax(final String phoneFax) {
        if (isSetPhoneInstance()) {
            getPhoneInstance().addFacsimile(phoneFax);
        } else {
            setPhone(Referenceable.of(new CiTelephone().addFacsimile(phoneFax)));
        }
        return this;
    }

    public CiContact setPhoneFax(final List<String> phoneFax) {
        if (isSetPhoneInstance()) {
            getPhoneInstance().setFacsimile(phoneFax);
        } else {
            setPhone(Referenceable.of(new CiTelephone().setFacsimile(phoneFax)));
        }
        return this;
    }

    /**
     * @return the address
     */
    public Referenceable<CiAddress> getAddress() {
        return address;
    }

    /**
     * @param address the address to set
     */
    public void setAddress(Referenceable<CiAddress> address) {
        this.address = address;
    }

    public boolean isSetAddress() {
        return getAddress() != null;
    }
    
    private boolean isSetAddressInstance() {
        return getAddress() != null 
                && getAddress().isInstance() 
                && getAddress().getInstance().isPresent();
    }
    
    private CiAddress getAddressInstance() {
        return getAddress().getInstance().get();
    }

    public boolean isSetDeliveryPoint() {
        return isSetAddressInstance()
                && getAddressInstance().hasDeliveryPoints();
    }

    public List<String> getDeliveryPoint() {
        if (isSetDeliveryPoint()) {
            return getAddressInstance().getDeliveryPoints();
        }
        return null;
    }

    public CiContact setDeliveryPoint(final List<String> deliveryPoints) {
        if (isSetAddressInstance()) {
            getAddressInstance().setDeliveryPoints(deliveryPoints);
        } else {
            setAddress(Referenceable.of(new CiAddress().setDeliveryPoints(deliveryPoints)));
        }
        return this;
    }

    public CiContact addDeliveryPoint(final String deliveryPoint) {
        if (isSetAddressInstance()) {
            getAddressInstance().addDeliveryPoints(deliveryPoint);
        } else {
            setAddress(Referenceable.of(new CiAddress().addDeliveryPoints(deliveryPoint)));
        }
        return this;
    }

    public boolean isSetCity() {
        return isSetAddressInstance() && !getAddressInstance().isSetCity();
    }

    public String getCity() {
        if (isSetCity()) {
            return getAddressInstance().getCity();
        }
        return null;
    }

    public CiContact setCity(final String city) {
        if (isSetAddressInstance()) {
            getAddressInstance().setCity(city);
        } else {
            setAddress(Referenceable.of(new CiAddress().setCity(city)));
        }
        return this;
    }

    public boolean isSetAdministrativeArea() {
        return isSetAddressInstance() && !getAddressInstance().isSetAdministrativeArea();
    }

    public String getAdministrativeArea() {
        if (isSetAdministrativeArea()) {
            return getAddressInstance().getAdministrativeArea();
        }
        return null;
    }

    public CiContact setAdministrativeArea(final String administrativeArea) {
        if (isSetAddressInstance()) {
            getAddressInstance().setAdministrativeArea(administrativeArea);
        } else {
            setAddress(Referenceable.of(new CiAddress().setAdministrativeArea(administrativeArea)));
        }
        return this;
    }

    public boolean isSetPostalCode() {
        return isSetAddressInstance() && !getAddressInstance().isSetPostalCode();
    }

    public String getPostalCode() {
        if (isSetAdministrativeArea()) {
            return getAddressInstance().getPostalCode();
        }
        return null;
    }

    public CiContact setPostalCode(final String postalCode) {
        if (isSetAddressInstance()) {
            getAddressInstance().setPostalCode(postalCode);
        } else {
            setAddress(Referenceable.of(new CiAddress().setPostalCode(postalCode)));
        }
        return this;
    }

    public boolean isSetCountry() {
        return isSetAddressInstance() && !getAddressInstance().isSetCountry();
    }

    public String getCountry() {
        if (isSetAdministrativeArea()) {
            return getAddressInstance().getCountry();
        }
        return null;
    }

    public CiContact setCountry(final String country) {
        if (isSetAddressInstance()) {
            getAddressInstance().setCountry(country);
        } else {
            setAddress(Referenceable.of(new CiAddress().setCountry(country)));
        }
        return this;
    }

    public boolean isSetEmail() {
        return isSetAddressInstance() && !getAddressInstance().hasElectronicMailAddresses();
    }

    public String getEmail() {
        if (isSetAdministrativeArea()) {
            return getAddressInstance().getElectronicMailAddresses().iterator().next();
        }
        return null;
    }

    public CiContact setEmail(final String email) {
        if (isSetAddressInstance()) {
            getAddressInstance().setElectronicMailAddresses(Lists.newArrayList(email));
        } else {
            setAddress(Referenceable.of(new CiAddress().setElectronicMailAddresses(Lists.newArrayList(email))));
        }
        return this;
    }

    public boolean isSetOnlineResource() {
        return onlineResource != null
                && ((onlineResource.isReference() && onlineResource.getReference().getHref().isPresent())
                        || onlineResource.isInstance());
    }
    
    public Referenceable<CiOnlineResource>  getOnlineResourceReferenceable() {
        return onlineResource;
    }

    public String getOnlineResource() {
        if (onlineResource.isReference() && onlineResource.getReference().getHref().isPresent()) {
            return onlineResource.getReference().getHref().get().toString();
        } else if (onlineResource.isInstance() && onlineResource.getInstance().isPresent()
                && onlineResource.getInstance().get().getLinkage().isPresent()) {
            return onlineResource.getInstance().get().getLinkage().get().toString();
        }
        return null;
    }
    
    public CiContact setOnlineResource(final Referenceable<CiOnlineResource> onlineResource) {
        this.onlineResource = onlineResource;
        return this;
    }

    public CiContact setOnlineResource(final String onlineResource) {
        try {
            this.onlineResource = Referenceable.of(new Reference().setHref(new URI(onlineResource)));
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
        return this;
    }

    public boolean isSetHoursOfService() {
        return hoursOfService != null && !hoursOfService.isNull();
    }

    public String getHoursOfService() {
        if (hoursOfService.isPresent()) {
            return hoursOfService.get();
        }
        return null;
    }
    
    public Nillable<String> getHoursOfServiceNillable() {
        return hoursOfService;
    }
    
    public CiContact setHoursOfService(final Nillable<String> hoursOfService) {
        this.hoursOfService = hoursOfService;
        return this;
    }

    public CiContact setHoursOfService(final String hoursOfService) {
        this.hoursOfService = Nillable.of(hoursOfService);
        return this;
    }

    public boolean isSetContactInstructions() {
        return contactInstructions != null && !contactInstructions.isNull();
    }

    public String getContactInstructions() {
        if (contactInstructions.isPresent()) {
            return contactInstructions.get();
        }
        return null;
    }
    
    public Nillable<String> getContactInstructionsNillable() {
        return contactInstructions;
    }

    public CiContact setContactInstructions(final Nillable<String> contactInstructions) {
        this.contactInstructions = contactInstructions;
        return this;
    }
    
    public CiContact setContactInstructions(final String contactInstructions) {
        this.contactInstructions = Nillable.of(contactInstructions);
        return this;
    }

    public boolean isSetContactInfo() {
        return isSetPhone() || isSetAddress() || isSetOnlineResource() || isSetHoursOfService()
                || isSetContactInstructions();
    }
}
