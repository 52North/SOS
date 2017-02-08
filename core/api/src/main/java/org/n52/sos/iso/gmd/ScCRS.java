package org.n52.sos.iso.gmd;

import org.n52.sos.ogc.gml.AbstractCRS;
import org.n52.sos.w3c.xlink.AttributeSimpleAttrs;
import org.n52.sos.w3c.xlink.SimpleAttrs;

public class ScCRS implements AttributeSimpleAttrs {
    
    private SimpleAttrs simpleAttrs;
    private AbstractCRS abstractCrs;
    
    @Override
    public void setSimpleAttrs(SimpleAttrs simpleAttrs) {
       this.simpleAttrs = simpleAttrs;
    }

    @Override
    public SimpleAttrs getSimpleAttrs() {
        return simpleAttrs;
    }

    @Override
    public boolean isSetSimpleAttrs() {
        return getSimpleAttrs() != null && getSimpleAttrs().isSetHref();
    }

    /**
     * @return the abstractCrs
     */
    public AbstractCRS getAbstractCrs() {
        return abstractCrs;
    }

    /**
     * @param abstractCrs the abstractCrs to set
     */
    public void setAbstractCrs(AbstractCRS abstractCrs) {
        this.abstractCrs = abstractCrs;
    }

}
