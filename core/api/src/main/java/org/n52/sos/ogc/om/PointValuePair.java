package org.n52.sos.ogc.om;

import org.n52.sos.ogc.om.values.Value;

import com.vividsolutions.jts.geom.Point;

public class PointValuePair implements Comparable<PointValuePair> {
    /**
     * Point value pair point
     */
    private Point point;

    /**
     * Point value pair value
     */
    private Value<?> value;

    /**
     * Constructor
     * 
     * @param point
     *            Point value pair point
     * @param value
     *            Point value pair value
     */
    public PointValuePair(Point point, Value<?> value) {
        this.point = point;
        this.value = value;
    }

    /**
     * Get point value pair point
     * 
     * @return Point value pair point
     */
    public Point getPoint() {
        return point;
    }

    /**
     * Get point value pair value
     * 
     * @return Point value pair value
     */
    public Value<?> getValue() {
        return value;
    }

    /**
     * Set point value pair point
     * 
     * @param point
     *            Point value pair point to set
     */
    public void setPoint(Point point) {
        this.point = point;
    }

    /**
     * Set point value pair value
     * 
     * @param value
     *            Point value pair value to set
     */
    public void setValue(Value<?> value) {
        this.value = value;
    }
    
    public boolean isSetValue() {
        return getValue() != null && getValue().isSetValue();
    }
    
    public boolean isSetPoint() {
        return getPoint() != null && !getPoint().isEmpty();
    }

    public boolean isEmpty() {
        return isSetPoint() && isSetValue();
    }

    @Override
    public int compareTo(PointValuePair o) {
        return point.compareTo(o.point);
    }
}
