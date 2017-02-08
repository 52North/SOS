package org.n52.sos.ogc.gml;

import org.n52.sos.iso.gmd.EXExtent;
import org.n52.sos.w3c.xlink.Referenceable;

public class DomainOfValidity {

    /* 0..1 */
    private Referenceable<EXExtent> exExtent;
    
    /**
     * @return the exExtent
     */
    public Referenceable<EXExtent> getExExtent() {
        return exExtent;
    }

    /**
     * @param exExtent the exExtent to set
     */
    public DomainOfValidity setExExtent(Referenceable<EXExtent> exExtent) {
        this.exExtent = exExtent;
        return this;
    }
    
    public boolean hasExExtent() {
        return getExExtent() != null && getExExtent().isInstance();
    }
}
