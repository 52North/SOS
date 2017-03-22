package org.n52.sos.ds.hibernate.entities.feature.gml;

import org.n52.sos.ds.hibernate.entities.feature.ReferenceEntity;
import org.n52.sos.ds.hibernate.entities.feature.gmd.ExExtentEntity;

/**
 * Hibernate entiity for the domainOfValidity
 * 
 * @author Carsten Hollmann <c.hollmann@52north.org>
 * @since 4.4.0
 *
 */
public class DomainOfValidityEntity extends ReferenceEntity {

    private ExExtentEntity exExtent;

    /**
     * @return the exExtent
     */
    public ExExtentEntity getExExtent() {
        return exExtent;
    }

    /**
     * @param exExtent
     *            the exExtent to set
     */
    public void setExExtent(ExExtentEntity exExtent) {
        this.exExtent = exExtent;
    }

    public boolean isSetExExtent() {
        return getExExtent() != null;
    }
}
