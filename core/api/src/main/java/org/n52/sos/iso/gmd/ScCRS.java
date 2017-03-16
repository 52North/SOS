package org.n52.sos.iso.gmd;

import org.n52.sos.ogc.gml.AbstractCRS;

public class ScCRS {
    
    private AbstractCRS abstractCrs;
    
    /**
     * @return the abstractCrs
     */
    public AbstractCRS getAbstractCrs() {
        return abstractCrs;
    }

    /**
     * @param abstractCrs the abstractCrs to set
     * @return 
     */
    public ScCRS setAbstractCrs(AbstractCRS abstractCrs) {
        this.abstractCrs = abstractCrs;
        return this;
    }

}
