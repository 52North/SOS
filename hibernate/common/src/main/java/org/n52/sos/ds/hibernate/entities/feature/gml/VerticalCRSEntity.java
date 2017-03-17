package org.n52.sos.ds.hibernate.entities.feature.gml;

import java.util.Set;

import org.n52.sos.ds.hibernate.entities.feature.ReferenceEntity;

import com.google.common.base.Strings;

/**
 * Hibernate entiity for the verticalCRS
 * 
 * @author Carsten Hollmann <c.hollmann@52north.org>
 * @since 4.4.0
 *
 */
public class VerticalCRSEntity extends ReferenceEntity {

    private String remarks;
    private Set<DomainOfValidityEntity> domainOfValidity;
    private Set<String> scope;
    private VerticalCSEntity verticalCS;
    private VerticalDatumEntity verticalDatum;

    /**
     * @return the remarks
     */
    public String getRemarks() {
        return remarks;
    }

    /**
     * @param remarks
     *            the remarks to set
     */
    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }

    public boolean isSetRemarks() {
        return !Strings.isNullOrEmpty(getRemarks());
    }

    /**
     * @return the domainOfValidity
     */
    public Set<DomainOfValidityEntity> getDomainOfValidity() {
        return domainOfValidity;
    }

    /**
     * @param domainOfValidity
     *            the domainOfValidity to set
     */
    public void setDomainOfValidity(Set<DomainOfValidityEntity> domainOfValidity) {
        this.domainOfValidity = domainOfValidity;
    }

    public boolean hasDomainOfValidity() {
        return getDomainOfValidity() != null && !getDomainOfValidity().isEmpty();
    }

    /**
     * @return the scope
     */
    public Set<String> getScope() {
        return scope;
    }

    /**
     * @param scope
     *            the scope to set
     */
    public void setScope(Set<String> scope) {
        this.scope = scope;
    }

    public boolean hasScope() {
        return getScope() != null && !getScope().isEmpty();
    }

    /**
     * @return the verticalCS
     */
    public VerticalCSEntity getVerticalCS() {
        return verticalCS;
    }

    /**
     * @param verticalCS
     *            the verticalCS to set
     */
    public void setVerticalCS(VerticalCSEntity verticalCS) {
        this.verticalCS = verticalCS;
    }

    public boolean isSetVerticalCS() {
        return getVerticalCS() != null;
    }

    /**
     * @return the verticalDatum
     */
    public VerticalDatumEntity getVerticalDatum() {
        return verticalDatum;
    }

    /**
     * @param verticalDatum
     *            the verticalDatum to set
     */
    public void setVerticalDatum(VerticalDatumEntity verticalDatum) {
        this.verticalDatum = verticalDatum;
    }

    public boolean isSetVerticalDatum() {
        return getVerticalDatum() != null;
    }
}
