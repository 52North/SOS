package org.n52.sos.ogc.gml;

import java.util.ArrayList;
import java.util.List;

import org.n52.sos.w3c.xlink.Referenceable;

public abstract class AbstractCoordinateSystem extends IdentifiedObject {

    private static final long serialVersionUID = -8178398617300946309L;
    
    /* 1..* */
    private List<Referenceable<CoordinateSystemAxis>> coordinateSystemAxis = new ArrayList<>();
    
    private Aggregation aggregation;
    
    public AbstractCoordinateSystem(CodeWithAuthority identifier, Referenceable<CoordinateSystemAxis> coordinateSystemAxis) {
        super(identifier);
        addCoordinateSystemAxis(coordinateSystemAxis);
    }
    
    public AbstractCoordinateSystem(CodeWithAuthority identifier, List<Referenceable<CoordinateSystemAxis>> coordinateSystemAxis) {
        super(identifier);
        addCoordinateSystemAxis(coordinateSystemAxis);
    }

    /**
     * @return the coordinateSystemAxis
     */
    public List<Referenceable<CoordinateSystemAxis>> getCoordinateSystemAxis() {
        return coordinateSystemAxis;
    }

    /**
     * @param coordinateSystemAxis the coordinateSystemAxis to set
     * @return 
     */
    public AbstractCoordinateSystem setCoordinateSystemAxis(List<Referenceable<CoordinateSystemAxis>> coordinateSystemAxis) {
        this.coordinateSystemAxis.clear();
        this.coordinateSystemAxis.addAll(coordinateSystemAxis);
        return this;
    }
    
    /**
     * @param coordinateSystemAxis the coordinateSystemAxis to set
     * @return 
     */
    public AbstractCoordinateSystem addCoordinateSystemAxis(List<Referenceable<CoordinateSystemAxis>> coordinateSystemAxis) {
        this.coordinateSystemAxis.addAll(coordinateSystemAxis);
        return this;
    }
    
    
    /**
     * @param coordinateSystemAxis the coordinateSystemAxis to set
     * @return 
     */
    public AbstractCoordinateSystem addCoordinateSystemAxis(Referenceable<CoordinateSystemAxis> coordinateSystemAxis) {
        this.coordinateSystemAxis.add(coordinateSystemAxis);
        return this;
    }

    /**
     * @return the aggregation
     */
    public Aggregation getAggregation() {
        return aggregation;
    }

    /**
     * @param aggregation the aggregation to set
     * @return 
     */
    public AbstractCoordinateSystem setAggregation(Aggregation aggregation) {
        this.aggregation = aggregation;
        return this;
    }
    
    public boolean isSetAggregation() {
        return getAggregation() != null;
    }

}
