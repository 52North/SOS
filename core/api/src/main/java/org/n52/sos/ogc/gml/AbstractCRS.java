package org.n52.sos.ogc.gml;

import java.util.ArrayList;
import java.util.List;
import org.n52.sos.util.CollectionHelper;
import org.n52.sos.w3c.xlink.Referenceable;

import com.google.common.collect.Lists;

/**
 * Internal representation of the OGC GML AbstractCRS.
 * 
 * @author Carsten Hollmann <c.hollmann@52north.org>
 * @since 4.4.0
 *
 */
public abstract class AbstractCRS extends IdentifiedObject {

    private static final long serialVersionUID = 2034560874264953187L;
    /* 0..* */
    private List<Referenceable<DomainOfValidity>> domainOfValidity = new ArrayList<>();
    /* 1..* */
    private List<String> scope = new ArrayList<>();
    
    public AbstractCRS(CodeWithAuthority identifier, String scope) {
        this(identifier, Lists.newArrayList(scope));
    }
    
    public AbstractCRS(CodeWithAuthority identifier, List<String> scope) {
        super(identifier);
        setScope(scope);
    }
    
    public List<Referenceable<DomainOfValidity>> getDomainOfValidity() {
        return domainOfValidity;
    }

    public AbstractCRS setDomainOfValidity(List<Referenceable<DomainOfValidity>> domainOfValidity) {
        this.domainOfValidity.clear();
        if (!CollectionHelper.nullEmptyOrContainsOnlyNulls(domainOfValidity)) {
            this.domainOfValidity.addAll(domainOfValidity);
        }
        return this;
    }
    
    public AbstractCRS addDomainOfValidity(List<Referenceable<DomainOfValidity>> domainOfValidity) {
        this.domainOfValidity.addAll(domainOfValidity);
        return this;
    }
    
    public AbstractCRS addDomainOfValidity(Referenceable<DomainOfValidity> domainOfValidity) {
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
