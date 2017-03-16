package org.n52.sos.ds.hibernate.entities.feature.gmd;

import java.util.Set;

import com.google.common.base.Strings;

public class ExExtentEntity extends AbstractCiEntity {

    private Set<ExVerticalExtentEntity> verticalExtent;

    /**
     * @return the verticalExtent
     */
    public Set<ExVerticalExtentEntity> getVerticalExtent() {
        return verticalExtent;
    }

    /**
     * @param verticalExtent
     *            the verticalExtent to set
     */
    public void setVerticalExtent(Set<ExVerticalExtentEntity> verticalExtent) {
        this.verticalExtent = verticalExtent;
    }

    public boolean hasVerticalExtent() {
        return getVerticalExtent() != null && !getVerticalExtent().isEmpty();
    }
}
