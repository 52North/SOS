package org.n52.sos.ds.hibernate.entities.feature;

import org.n52.sos.ds.hibernate.entities.AbstractIdentifierNameDescriptionEntity;

import com.google.common.base.Strings;

/**
 * @author Carsten Hollmann <c.hollmann@52north.org>
 * @since 4.4.0
 *
 */
public abstract class NilReasonEntity extends AbstractIdentifierNameDescriptionEntity {

    private long pkid;
    private String nilReason;

    /**
     * @return the pkid
     */
    public long getPkid() {
        return pkid;
    }

    /**
     * @param pkid the pkid to set
     */
    public void setPkid(long pkid) {
        this.pkid = pkid;
    }

    /**
     * @return the nilReason
     */
    public String getNilReason() {
        return nilReason;
    }

    /**
     * @param nilReason the nilReason to set
     */
    public void setNilReason(String nilReason) {
        this.nilReason = nilReason;
    }
    
    public boolean isSetNilReason() {
        return !Strings.isNullOrEmpty(getNilReason());
    }
}
