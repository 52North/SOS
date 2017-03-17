package org.n52.sos.ds.hibernate.entities.feature.gmd;

import org.n52.sos.ds.hibernate.entities.feature.gml.VerticalCRSEntity;

import com.google.common.base.Strings;

/**
 * Hibernate entity for exVerticalExtent.
 * 
 * @author Carsten Hollmann <c.hollmann@52north.org>
 * @since 4.4.0
 *
 */
public class ExVerticalExtentEntity extends AbstractCiEntity {
    
    private Double minimumValue;
    private String minValuNilReason;
    private Double maximumValue;
    private String maxValuNilReason;
    private VerticalCRSEntity verticalCRS;
    
    /**
     * @return the minimumValue
     */
    public Double getMinimumValue() {
        return minimumValue;
    }
    /**
     * @param minimumValue the minimumValue to set
     */
    public void setMinimumValue(Double minimumValue) {
        this.minimumValue = minimumValue;
    }
    public boolean isSetMinimumValue() {
        return getMinimumValue() != null;
    }
    
    /**
     * @return the minValuNilReason
     */
    public String getMinValuNilReason() {
        return minValuNilReason;
    }
    /**
     * @param minValuNilReason the minValuNilReason to set
     */
    public void setMinValuNilReason(String minValuNilReason) {
        this.minValuNilReason = minValuNilReason;
    }
    
    public boolean isSetMinValuNilReason() {
        return Strings.isNullOrEmpty(getMinValuNilReason());
    }
    /**
     * @return the maximumValue
     */
    public Double getMaximumValue() {
        return maximumValue;
    }
    /**
     * @param maximumValue the maximumValue to set
     */
    public void setMaximumValue(Double maximumValue) {
        this.maximumValue = maximumValue;
    }
    
    public boolean isSetMaximumValue() {
        return getMaximumValue() != null;
    }
    /**
     * @return the maxValuNilReason
     */
    public String getMaxValuNilReason() {
        return maxValuNilReason;
    }
    /**
     * @param maxValuNilReason the maxValuNilReason to set
     */
    public void setMaxValuNilReason(String maxValuNilReason) {
        this.maxValuNilReason = maxValuNilReason;
    }
    
    public boolean isSetMaxValuNilReason() {
        return Strings.isNullOrEmpty(getMaxValuNilReason());
    }
    /**
     * @return the verticalCRS
     */
    public VerticalCRSEntity getVerticalCRS() {
        return verticalCRS;
    }
    /**
     * @param verticalCRS the verticalCRS to set
     */
    public void setVerticalCRS(VerticalCRSEntity verticalCRS) {
        this.verticalCRS = verticalCRS;
    }
    
    public boolean isSetVerticalCRS() {
        return getVerticalCRS() != null;
    }

}
