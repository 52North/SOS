package org.n52.sos.ogc.gml;

import java.util.ArrayList;
import java.util.List;

import org.joda.time.DateTime;
import org.n52.sos.util.CollectionHelper;
import org.n52.sos.w3c.Nillable;
import org.n52.sos.w3c.xlink.Referenceable;

import com.google.common.collect.Lists;

/**
 * Internal representation of the OGC GML AbstractDatum.
 * 
 * @author Carsten Hollmann <c.hollmann@52north.org>
 * @since 4.4.0
 *
 */
public abstract class AbstractDatum extends IdentifiedObject {
    
    private static final long serialVersionUID = -4804549832513290875L;
    /* 0..1 */
    private Referenceable<DomainOfValidity> domainOfValidity;
    /* 1..* */
    private List<String> scope = new ArrayList<>();
    /* 0..1 */
    private CodeType anchorDefinition;
    /* 0..1 */
    private DateTime realizationEpoch;
    
    public AbstractDatum(CodeWithAuthority identifier, String scope) {
        this(identifier, Lists.newArrayList(scope));
    }
    
    public AbstractDatum(CodeWithAuthority identifier, List<String> scope) {
        super(identifier);
        setScope(scope);
    }

    public Referenceable<DomainOfValidity> getDomainOfValidity() {
        return domainOfValidity;
    }

    public AbstractDatum setDomainOfValidity(Referenceable<DomainOfValidity> domainOfValidity) {
        this.domainOfValidity = domainOfValidity;
        return this;
    }
    
    public AbstractDatum setDomainOfValidity(DomainOfValidity domainOfValidity) {
        if (domainOfValidity != null) {
            this.domainOfValidity = Referenceable.of(domainOfValidity);
        } else {
            this.domainOfValidity = Referenceable.of(Nillable.<DomainOfValidity>missing());
        }
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
