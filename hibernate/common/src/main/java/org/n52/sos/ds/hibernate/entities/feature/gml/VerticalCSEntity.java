package org.n52.sos.ds.hibernate.entities.feature.gml;

import java.util.Set;

import org.n52.sos.ds.hibernate.entities.feature.ReferenceEntity;

import com.google.common.base.Strings;

public class VerticalCSEntity extends ReferenceEntity {

    private String remarks;
    private Set<CoordinateSystemAxisEntity> coordinateSystemAxis;
    private String aggregation;

    /**
     * @return the remarks
     */
    public String getRemarks() {
        return remarks;
    }

    /**
     * @param remarks
     *            the remarks to set
     */
    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }
    
    public boolean isSetRemarks() {
        return !Strings.isNullOrEmpty(getRemarks());
    }

    /**
     * @return the coordinateSystemAxis
     */
    public Set<CoordinateSystemAxisEntity> getCoordinateSystemAxis() {
        return coordinateSystemAxis;
    }

    /**
     * @param coordinateSystemAxis
     *            the coordinateSystemAxis to set
     */
    public void setCoordinateSystemAxis(Set<CoordinateSystemAxisEntity> coordinateSystemAxis) {
        this.coordinateSystemAxis = coordinateSystemAxis;
    }

    public boolean hasCoordinateSystemAxis() {
        return getCoordinateSystemAxis() != null && !getCoordinateSystemAxis().isEmpty();
    }

    /**
     * @return the aggregation
     */
    public String getAggregation() {
        return aggregation;
    }

    /**
     * @param aggregation
     *            the aggregation to set
     */
    public void setAggregation(String aggregation) {
        this.aggregation = aggregation;
    }

    public boolean isSetAggregation() {
        return !Strings.isNullOrEmpty(getAggregation());
    }
}
