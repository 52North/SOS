package org.n52.sos.ds.hibernate.entities.feature.gmd;

import java.util.Set;

/**
 * Hibernate entity for exExtent.
 * 
 * @author Carsten Hollmann <c.hollmann@52north.org>
 * @since 4.4.0
 *
 */
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
