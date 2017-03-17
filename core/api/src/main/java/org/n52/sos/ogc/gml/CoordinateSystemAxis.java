package org.n52.sos.ogc.gml;

/**
 * Internal representation of the OGC GML CoordinateSystemAxis.
 * 
 * @author Carsten Hollmann <c.hollmann@52north.org>
 * @since 4.4.0
 *
 */
public class CoordinateSystemAxis extends IdentifiedObject {

    private static final long serialVersionUID = 2044040407272459804L;
    /* 1..1 */
    private CodeType axisAbbrev;
    /* 1..1 */
    private CodeWithAuthority axisDirection;
    /* 0..1 */
    private Double minimumValue;
    /* 0..1 */
    private Double maximumValue;
    /* 0..1 */
    private CodeWithAuthority rangeMeaning;
    /* 1..1 */
    private String uom;

    public CoordinateSystemAxis(CodeWithAuthority identifier, CodeType axisAbbrev, CodeWithAuthority axisDirection, String uom) {
        super(identifier);
        this.axisAbbrev = axisAbbrev;
        this.axisDirection = axisDirection;
        this.uom = uom;
    }

    /**
     * @return the axisAbbrev
     */
    public CodeType getAxisAbbrev() {
        return axisAbbrev;
    }

    /**
     * @return the axisDirection
     */
    public CodeWithAuthority getAxisDirection() {
        return axisDirection;
    }

    /**
     * @return the minimumValue
     */
    public Double getMinimumValue() {
        return minimumValue;
    }

    /**
     * @param minimumValue
     *            the minimumValue to set
     */
    public CoordinateSystemAxis setMinimumValue(Double minimumValue) {
        this.minimumValue = minimumValue;
        return this;
    }
    
    public boolean isSetMinimumValue() {
        return getMinimumValue() != null;
    }

    /**
     * @return the maximumValue
     */
    public Double getMaximumValue() {
        return maximumValue;
    }

    /**
     * @param maximumValue
     *            the maximumValue to set
     */
    public CoordinateSystemAxis setMaximumValue(Double maximumValue) {
        this.maximumValue = maximumValue;
        return this;
    }
    
    public boolean isSetMaximumValue() {
        return getMaximumValue() != null;
    }

    /**
     * @return the rangeMeaning
     */
    public CodeWithAuthority getRangeMeaning() {
        return rangeMeaning;
    }

    /**
     * @param rangeMeaning
     *            the rangeMeaning to set
     */
    public CoordinateSystemAxis setRangeMeaning(CodeWithAuthority rangeMeaning) {
        this.rangeMeaning = rangeMeaning;
        return this;
    }
    
    public boolean isSetRangeMeaning() {
        return getRangeMeaning() != null;
    }

    /**
     * @return the uom
     */
    public String getUom() {
        return uom;
    }

}
