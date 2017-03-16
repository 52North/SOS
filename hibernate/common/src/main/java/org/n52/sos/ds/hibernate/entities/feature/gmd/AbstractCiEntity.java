package org.n52.sos.ds.hibernate.entities.feature.gmd;

import org.n52.sos.ds.hibernate.entities.feature.ReferenceEntity;

import com.google.common.base.Strings;

public class AbstractCiEntity extends ReferenceEntity {

    private String id;
    private String uuid;

    /**
     * @return the id
     */
    public String getId() {
        return id;
    }

    /**
     * @param id
     *            the id to set
     */
    public void setId(String id) {
        this.id = id;
    }
    
    public boolean isSetId() {
        return !Strings.isNullOrEmpty(getId());
    }

    /**
     * @return the uuid
     */
    public String getUuid() {
        return uuid;
    }

    /**
     * @param uuid
     *            the uuid to set
     */
    public void setUuid(String uuid) {
        this.uuid = uuid;
    }
    
    public boolean isSetUuid() {
        return !Strings.isNullOrEmpty(getUuid());
    }
}
