package org.n52.sos.ds.hibernate.entities.feature.gmd;

import com.google.common.base.Strings;

/**
 * Hibernate entity for responsibleParty.
 * 
 * @author Carsten Hollmann <c.hollmann@52north.org>
 * @since 4.4.0
 *
 */
public class ResponsiblePartyEntity extends AbstractCiEntity {

    private String individualName;
    private String organizationName;
    private String positionName;
    private ContactEntity contactInfo;
    private RoleEntity role;

    /**
     * @return the individualName
     */
    public String getIndividualName() {
        return individualName;
    }

    /**
     * @param individualName
     *            the individualName to set
     */
    public void setIndividualName(String individualName) {
        this.individualName = individualName;
    }
    
    public boolean isSetIndividualName() {
        return !Strings.isNullOrEmpty(getIndividualName());
    }

    /**
     * @return the organizationName
     */
    public String getOrganizationName() {
        return organizationName;
    }

    /**
     * @param organizationName
     *            the organizationName to set
     */
    public void setOrganizationName(String organizationName) {
        this.organizationName = organizationName;
    }
    
    public boolean isSetOrganizationName() {
        return !Strings.isNullOrEmpty(getOrganizationName());
    }

    /**
     * @return the positionName
     */
    public String getPositionName() {
        return positionName;
    }

    /**
     * @param positionName
     *            the positionName to set
     */
    public void setPositionName(String positionName) {
        this.positionName = positionName;
    }
    
    public boolean isSetPositionName() {
        return !Strings.isNullOrEmpty(getPositionName());
    }

    /**
     * @return the contactInfo
     */
    public ContactEntity getContactInfo() {
        return contactInfo;
    }

    /**
     * @param contactInfo
     *            the contactInfo to set
     */
    public void setContactInfo(ContactEntity contactInfo) {
        this.contactInfo = contactInfo;
    }
    
    public boolean isSetContactInfo() {
        return getContactInfo() != null;
    }

    /**
     * @return the role
     */
    public RoleEntity getCiRole() {
        return role;
    }

    /**
     * @param role
     *            the role to set
     */
    public void setCiRole(RoleEntity role) {
        this.role = role;
    }
}
