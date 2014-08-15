package org.n52.sos.ds.hibernate.util.observation;

import org.apache.xmlbeans.XmlObject;

import org.n52.sos.ds.hibernate.entities.observation.ObservationVisitor;
import org.n52.sos.ds.hibernate.entities.observation.full.BlobObservation;
import org.n52.sos.ds.hibernate.entities.observation.full.BooleanObservation;
import org.n52.sos.ds.hibernate.entities.observation.full.CategoryObservation;
import org.n52.sos.ds.hibernate.entities.observation.full.ComplexObservation;
import org.n52.sos.ds.hibernate.entities.observation.full.CountObservation;
import org.n52.sos.ds.hibernate.entities.observation.full.GeometryObservation;
import org.n52.sos.ds.hibernate.entities.observation.full.NumericObservation;
import org.n52.sos.ds.hibernate.entities.observation.full.SweDataArrayObservation;
import org.n52.sos.ds.hibernate.entities.observation.full.TextObservation;
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
        implements ObservationVisitor<Value<?>> {

    @Override
    public QuantityValue visit(NumericObservation o) {
        return new QuantityValue(o.getValue());
    }

    @Override
    public UnknownValue visit(BlobObservation o) {
        return new UnknownValue(o.getValue());
    }

    @Override
    public BooleanValue visit(BooleanObservation o) {
        return new BooleanValue(o.getValue());
    }

    @Override
    public CategoryValue visit(CategoryObservation o) {
        return new CategoryValue(o.getValue());
    }

    @Override
    public ComplexValue visit(ComplexObservation o)
            throws OwsExceptionReport {
        SweAbstractDataComponentCreator visitor
                = new SweAbstractDataComponentCreator();
        SweDataRecord record = visitor.visit(o);
        return new ComplexValue(record);
    }

    @Override
    public CountValue visit(CountObservation o) {
        return new CountValue(o.getValue());
    }

    @Override
    public GeometryValue visit(GeometryObservation o) {
        return new GeometryValue(o.getValue());
    }

    @Override
    public TextValue visit(TextObservation o) {
        return new TextValue(o.getValue());
    }

    @Override
    public SweDataArrayValue visit(SweDataArrayObservation o)
            throws OwsExceptionReport {
        XmlObject xml = XmlHelper.parseXmlString(o.getValue());
        SweDataArray array = (SweDataArray) CodingHelper.decodeXmlElement(xml);
        return new SweDataArrayValue(array);
    }

}
