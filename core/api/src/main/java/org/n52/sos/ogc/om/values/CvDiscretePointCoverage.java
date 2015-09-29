package org.n52.sos.ogc.om.values;

import org.n52.sos.ogc.om.PointValuePair;
import org.n52.sos.ogc.om.values.visitor.ValueVisitor;
import org.n52.sos.ogc.om.values.visitor.VoidValueVisitor;
import org.n52.sos.ogc.ows.OwsExceptionReport;
import org.n52.sos.util.StringHelper;

public class CvDiscretePointCoverage implements Value<PointValuePair> {

    private static final long serialVersionUID = 7475586076168740072L;
    
    private PointValuePair value;
    
    private String unit;

    @Override
    public void setValue(PointValuePair value) {
        this.value = value;
    }

    @Override
    public PointValuePair getValue() {
        return value;
    }

    @Override
    public void setUnit(String unit) {
        this.unit = unit;
    }

    @Override
    public String getUnit() {
        return unit;
    }

    @Override
    public boolean isSetValue() {
        return getValue() != null && !getValue().isEmpty();
    }

    @Override
    public boolean isSetUnit() {
        return StringHelper.isNotEmpty(getUnit());
    }

    @Override
    public <X> X accept(ValueVisitor<X> visitor) throws OwsExceptionReport {
        return visitor.visit(this);
    }

    @Override
    public void accept(VoidValueVisitor visitor) throws OwsExceptionReport {
        visitor.visit(this);
    }

}
