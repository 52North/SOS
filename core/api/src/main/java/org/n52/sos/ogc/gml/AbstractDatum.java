package org.n52.sos.ogc.gml;

import java.util.ArrayList;
import java.util.List;

import org.joda.time.DateTime;
import org.n52.sos.util.CollectionHelper;

public abstract class AbstractDatum extends IdentifiedObject {
    
    private static final long serialVersionUID = -4804549832513290875L;
    /* 0..1 */
    private DomainOfValidity domainOfValidity;
    /* 1..* */
    private List<String> scope = new ArrayList<>();
    /* 0..1 */
    private CodeType anchorDefinition;
    /* 0..1 */
    private DateTime realizationEpoch;
    
    public AbstractDatum(String scope) {
        addScope(scope);
    }
    
    public AbstractDatum(List<String> scope) {
        setScope(scope);
    }

    public DomainOfValidity getDomainOfValidity() {
        return domainOfValidity;
    }

    public AbstractDatum setDomainOfValidity(DomainOfValidity domainOfValidity) {
        this.domainOfValidity = domainOfValidity;
        return this;
    }
    
    public boolean hasDomainOfValidity() {
        return getDomainOfValidity() != null;
    }
    
    public List<String> getScope() {
        return scope;
    }

    public AbstractDatum setScope(List<String> scope) {
        this.scope.clear();
        if (!CollectionHelper.nullEmptyOrContainsOnlyNulls(scope)) {
            this.scope.addAll(scope);
        }
        return this;
    }
    
    public AbstractDatum addScope(List<String> scope) {
        this.scope.addAll(scope);
        return this;
    }
    
    public AbstractDatum addScope(String scope) {
        this.scope.add(scope);
        return this;
    }
    
    public boolean hasScope() {
        return !CollectionHelper.nullEmptyOrContainsOnlyNulls(getScope());
    }

    public CodeType getAnchorDefinition() {
        return anchorDefinition;
    }

    public AbstractDatum setAnchorDefinition(CodeType anchorDefinition) {
        this.anchorDefinition = anchorDefinition;
        return this;
    }
    
    public boolean hasAnchorDefinition() {
        return getAnchorDefinition() != null && getAnchorDefinition().isSetValue();
    }

    public DateTime getRealizationEpoch() {
        return realizationEpoch;
    }

    public AbstractDatum setRealizationEpoch(DateTime realizationEpoch) {
        this.realizationEpoch = realizationEpoch;
        return this;
    }
    
    public boolean hasRealizationEpoch() {
        return getRealizationEpoch() != null;
    }
    
}
