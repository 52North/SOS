package org.n52.sos.ogc.gml;

import java.util.ArrayList;
import java.util.List;
import org.n52.sos.util.CollectionHelper;

public abstract class AbstractCRS extends IdentifiedObject {

    private static final long serialVersionUID = 2034560874264953187L;
    /* 0..* */
    private List<DomainOfValidity> domainOfValidity = new ArrayList<>();
    /* 1..* */
    private List<String> scope = new ArrayList<>();
    
    public AbstractCRS(String scope) {
        addScope(scope);
    }
    
    public AbstractCRS(List<String> scope) {
        setScope(scope);
    }
    
    public List<DomainOfValidity> getDomainOfValidity() {
        return domainOfValidity;
    }

    public AbstractCRS setDomainOfValidity(List<DomainOfValidity> domainOfValidity) {
        this.domainOfValidity.clear();
        if (!CollectionHelper.nullEmptyOrContainsOnlyNulls(domainOfValidity)) {
            this.domainOfValidity.addAll(domainOfValidity);
        }
        return this;
    }
    
    public AbstractCRS addDomainOfValidity(List<DomainOfValidity> domainOfValidity) {
        this.domainOfValidity.addAll(domainOfValidity);
        return this;
    }
    
    public AbstractCRS addDomainOfValidity(DomainOfValidity domainOfValidity) {
        this.domainOfValidity.add(domainOfValidity);
        return this;
    }
    
    public boolean hasDomainOfValidity() {
        return !CollectionHelper.nullEmptyOrContainsOnlyNulls(getDomainOfValidity());
    }
    
    public List<String> getScope() {
        return scope;
    }

    public AbstractCRS setScope(List<String> scope) {
        this.scope.clear();
        if (!CollectionHelper.nullEmptyOrContainsOnlyNulls(scope)) {
            this.scope.addAll(scope);
        }
        return this;
    }
    
    public AbstractCRS addScope(List<String> scope) {
        this.scope.addAll(scope);
        return this;
    }
    
    public AbstractCRS addScope(String scope) {
        this.scope.add(scope);
        return this;
    }
    
    public boolean hasScope() {
        return !CollectionHelper.nullEmptyOrContainsOnlyNulls(getScope());
    }
}
