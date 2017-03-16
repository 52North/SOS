package org.n52.sos.ds.hibernate.entities.feature.gmd;

import com.google.common.base.Strings;

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
