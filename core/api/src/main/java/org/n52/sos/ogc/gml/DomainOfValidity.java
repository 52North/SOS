package org.n52.sos.ogc.gml;

import org.n52.sos.iso.gmd.EXExtent;

public class DomainOfValidity {

    /* 0..1 */
    private EXExtent exExtent;
    
    /**
     * @return the exExtent
     */
    public EXExtent getExExtent() {
        return exExtent;
    }

    /**
     * @param exExtent the exExtent to set
     */
    public DomainOfValidity setExExtent(EXExtent exExtent) {
        this.exExtent = exExtent;
        return this;
    }
    
    public boolean hasExExtent() {
        return getExExtent() != null;
    }
}
