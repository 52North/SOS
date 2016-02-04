package org.n52.sos.ogc.om.values;

import org.n52.sos.ogc.swe.DataRecord;
import org.n52.sos.ogc.swe.simpleType.SweQuantity;

/**
 * Represents the GroundWaterML 2.0 LogValue
 * 
 * @author <a href="mailto:c.hollmann@52north.org">Carsten Hollmann</a>
 * @since 4.4.0
 *
 */
public class LogValue {

    private SweQuantity fromDepth;
    private SweQuantity toDepth;
    private DataRecord value;
    
    /**
     * constructor
     */
    public LogValue() {
        super();
    }
    
    /**
     * constructor
     * 
     * @param fromDepth
     *            the fromDepth value
     * @param toDepth
     *            the toDepth value
     * @param value
     *            the values
     */
    public LogValue(SweQuantity fromDepth, SweQuantity toDepth, DataRecord value) {
        super();
        this.fromDepth = fromDepth;
        this.toDepth = toDepth;
        this.value = value;
    }

    /**
     * @return the fromDepth
     */
    public SweQuantity getFromDepth() {
        return fromDepth;
    }

    /**
     * @param fromDepth
     *            the fromDepth to set
     */
    public LogValue setFromDepth(SweQuantity fromDepth) {
        this.fromDepth = fromDepth;
        return this;
    }
    
    public boolean isSetFromDepth() {
        return getFromDepth() != null;
    }

    /**
     * @return the toDepth
     */
    public SweQuantity getToDepth() {
        return toDepth;
    }

    /**
     * @param toDepth
     *            the toDepth to set
     */
    public LogValue setToDepth(SweQuantity toDepth) {
        this.toDepth = toDepth;
        return this;
    }
    
    public boolean isSetToDepth() {
        return getToDepth() != null;
    }

    /**
     * @return the value
     */
    public DataRecord getValue() {
        return value;
    }

    /**
     * @param value
     *            the value to set
     */
    public LogValue setValue(DataRecord value) {
        this.value = value;
        return this;
    }
    
    public boolean isSetValue() {
        return getValue() != null;
    }

}