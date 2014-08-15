package org.n52.sos.ds.hibernate.util.observation;

import org.apache.xmlbeans.XmlObject;

import org.n52.sos.ds.hibernate.entities.observation.ValuedObservationVisitor;
import org.n52.sos.ds.hibernate.entities.observation.valued.BlobValuedObservation;
import org.n52.sos.ds.hibernate.entities.observation.valued.BooleanValuedObservation;
import org.n52.sos.ds.hibernate.entities.observation.valued.CategoryValuedObservation;
import org.n52.sos.ds.hibernate.entities.observation.valued.ComplexValuedObservation;
import org.n52.sos.ds.hibernate.entities.observation.valued.CountValuedObservation;
import org.n52.sos.ds.hibernate.entities.observation.valued.GeometryValuedObservation;
import org.n52.sos.ds.hibernate.entities.observation.valued.NumericValuedObservation;
import org.n52.sos.ds.hibernate.entities.observation.valued.SweDataArrayValuedObservation;
import org.n52.sos.ds.hibernate.entities.observation.valued.TextValuedObservation;
import org.n52.sos.ogc.om.values.BooleanValue;
import org.n52.sos.ogc.om.values.CategoryValue;
import org.n52.sos.ogc.om.values.ComplexValue;
import org.n52.sos.ogc.om.values.CountValue;
import org.n52.sos.ogc.om.values.GeometryValue;
import org.n52.sos.ogc.om.values.QuantityValue;
import org.n52.sos.ogc.om.values.SweDataArrayValue;
import org.n52.sos.ogc.om.values.TextValue;
import org.n52.sos.ogc.om.values.UnknownValue;
import org.n52.sos.ogc.om.values.Value;
import org.n52.sos.ogc.ows.OwsExceptionReport;
import org.n52.sos.ogc.swe.SweDataArray;
import org.n52.sos.ogc.swe.SweDataRecord;
import org.n52.sos.util.CodingHelper;
import org.n52.sos.util.XmlHelper;

/**
 * TODO JavaDoc
 *
 * @author Christian Autermann
 */
public class ObservationValueCreator
        implements ValuedObservationVisitor<Value<?>> {

    @Override
    public QuantityValue visit(NumericValuedObservation o) {
        return new QuantityValue(o.getValue());
    }

    @Override
    public UnknownValue visit(BlobValuedObservation o) {
        return new UnknownValue(o.getValue());
    }

    @Override
    public BooleanValue visit(BooleanValuedObservation o) {
        return new BooleanValue(o.getValue());
    }

    @Override
    public CategoryValue visit(CategoryValuedObservation o) {
        return new CategoryValue(o.getValue());
    }

    @Override
    public ComplexValue visit(ComplexValuedObservation o)
            throws OwsExceptionReport {
        SweAbstractDataComponentCreator visitor
                = new SweAbstractDataComponentCreator();
        SweDataRecord record = visitor.visit(o);
        return new ComplexValue(record);
    }

    @Override
    public CountValue visit(CountValuedObservation o) {
        return new CountValue(o.getValue());
    }

    @Override
    public GeometryValue visit(GeometryValuedObservation o) {
        return new GeometryValue(o.getValue());
    }

    @Override
    public TextValue visit(TextValuedObservation o) {
        return new TextValue(o.getValue());
    }

    @Override
    public SweDataArrayValue visit(SweDataArrayValuedObservation o)
            throws OwsExceptionReport {
        XmlObject xml = XmlHelper.parseXmlString(o.getValue());
        SweDataArray array = (SweDataArray) CodingHelper.decodeXmlElement(xml);
        return new SweDataArrayValue(array);
    }

}
