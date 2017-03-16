package org.n52.sos.ds.hibernate.entities.feature.gmd;

import org.n52.sos.ds.hibernate.entities.feature.ReferenceEntity;

import com.google.common.base.Strings;

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
