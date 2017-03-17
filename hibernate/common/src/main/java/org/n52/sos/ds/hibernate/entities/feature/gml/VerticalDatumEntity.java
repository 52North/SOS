package org.n52.sos.ds.hibernate.entities.feature.gml;

import java.util.Date;
import java.util.Set;

import org.n52.sos.ds.hibernate.entities.Codespace;
import org.n52.sos.ds.hibernate.entities.feature.ReferenceEntity;

import com.google.common.base.Strings;

/**
 * Hibernate entiity for the verticalDatum
 * 
 * @author Carsten Hollmann <c.hollmann@52north.org>
 * @since 4.4.0
 *
 */
public class VerticalDatumEntity extends ReferenceEntity {

    private String remarks;
    private DomainOfValidityEntity domainOfValidity;
    private Set<String> scope;
    private String anchorDefinition;
    private Codespace codespaceAnchorDefinition;
    private Date realizationEpoch;

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
    public DomainOfValidityEntity getDomainOfValidity() {
        return domainOfValidity;
    }

    /**
     * @param domainOfValidity
     *            the domainOfValidity to set
     */
    public void setDomainOfValidity(DomainOfValidityEntity domainOfValidity) {
        this.domainOfValidity = domainOfValidity;
    }

    public boolean isSetDomainOfValidity() {
        return getDomainOfValidity() != null;
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
     * @return the anchorDefinition
     */
    public String getAnchorDefinition() {
        return anchorDefinition;
    }

    /**
     * @param anchorDefinition
     *            the anchorDefinition to set
     */
    public void setAnchorDefinition(String anchorDefinition) {
        this.anchorDefinition = anchorDefinition;
    }

    public boolean isSetAnchorDefinition() {
        return !Strings.isNullOrEmpty(getAnchorDefinition());
    }

    public Codespace getCodespaceAnchorDef() {
        return codespaceAnchorDefinition;
    }

    public void setCodespaceAnchorDef(Codespace codespaceAnchorDefinition) {
        this.codespaceAnchorDefinition = codespaceAnchorDefinition;
    }

    public boolean isSetCodespaceAnchorDefinition() {
        return getCodespaceAnchorDef() != null && getCodespaceAnchorDef().isSetCodespace();
    }

    /**
     * @return the realizationEpoch
     */
    public Date getRealizationEpoch() {
        return realizationEpoch;
    }

    /**
     * @param realizationEpoch
     *            the realizationEpoch to set
     */
    public void setRealizationEpoch(Date realizationEpoch) {
        this.realizationEpoch = realizationEpoch;
    }

    public boolean isSetRealizationEpoch() {
        return getRealizationEpoch() != null;
    }
}
