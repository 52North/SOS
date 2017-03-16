package org.n52.sos.iso.gmd;

import org.n52.sos.w3c.Nillable;
import org.n52.sos.w3c.xlink.Referenceable;

public class EXVerticalExtent extends AbstractObject{
    
    /* 1..1 */
    private Nillable<Double> minimumValue;
    /* 1..1 */
    private Nillable<Double> maximumValue;
    /* 1..1 */
    private Referenceable<ScCRS> verticalCRS;
    
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
    public Referenceable<ScCRS> getVerticalCRS() {
        return verticalCRS;
    }

    /**
     * @param verticalCRS the verticalCRS to set
     */
    public void setVerticalCRS(Referenceable<ScCRS> verticalCRS) {
        this.verticalCRS = verticalCRS;
    }
}
