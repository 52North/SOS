package org.n52.sos.iso.gmd;

import org.n52.sos.iso.gco.AbstractObject;
import org.n52.sos.w3c.Nillable;
import org.n52.sos.w3c.xlink.AttributeSimpleAttrs;
import org.n52.sos.w3c.xlink.SimpleAttrs;

public class EXVerticalExtent extends AbstractObject implements AttributeSimpleAttrs {
    
    private SimpleAttrs simpleAttrs;
    /* 1..1 */
    private Nillable<Double> minimumValue;
    /* 1..1 */
    private Nillable<Double> maximumValue;
    /* 1..1 */
    private Nillable<ScCRS> verticalCRS;
    
    public EXVerticalExtent() {
        super();
    }

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
     * @return the minimumValue
     */
    public Nillable<Double> getMinimumValue() {
        return minimumValue;
    }

    /**
     * @param minimumValue the minimumValue to set
     */
    public void setMinimumValue(Nillable<Double> minimumValue) {
        this.minimumValue = minimumValue;
    }

    /**
     * @return the maximumValue
     */
    public Nillable<Double> getMaximumValue() {
        return maximumValue;
    }

    /**
     * @param maximumValue the maximumValue to set
     */
    public void setMaximumValue(Nillable<Double> maximumValue) {
        this.maximumValue = maximumValue;
    }

    /**
     * @return the verticalCRS
     */
    public Nillable<ScCRS> getVerticalCRS() {
        return verticalCRS;
    }

    /**
     * @param verticalCRS the verticalCRS to set
     */
    public void setVerticalCRS(Nillable<ScCRS> verticalCRS) {
        this.verticalCRS = verticalCRS;
    }
}
