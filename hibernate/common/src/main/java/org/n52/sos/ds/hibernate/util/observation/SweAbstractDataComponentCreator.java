package org.n52.sos.ds.hibernate.util.observation;

import org.apache.xmlbeans.XmlObject;

import org.n52.sos.ds.hibernate.entities.ObservableProperty;
import org.n52.sos.ds.hibernate.entities.observation.Observation;
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
import org.n52.sos.exception.ows.NoApplicableCodeException;
import org.n52.sos.ogc.gml.CodeType;
import org.n52.sos.ogc.ows.OwsExceptionReport;
import org.n52.sos.ogc.swe.SweAbstractDataComponent;
import org.n52.sos.ogc.swe.SweDataArray;
import org.n52.sos.ogc.swe.SweDataRecord;
import org.n52.sos.ogc.swe.SweField;
import org.n52.sos.ogc.swe.simpleType.SweAbstractUomType;
import org.n52.sos.ogc.swe.simpleType.SweBoolean;
import org.n52.sos.ogc.swe.simpleType.SweCategory;
import org.n52.sos.ogc.swe.simpleType.SweCount;
import org.n52.sos.ogc.swe.simpleType.SweQuantity;
import org.n52.sos.ogc.swe.simpleType.SweText;
import org.n52.sos.util.CodingHelper;
import org.n52.sos.util.XmlHelper;

/**
 * {@code ObservationVisitor} to create {@link SweAbstractDataComponent} from
 * observations.
 *
 * @author Christian Autermann
 */
public class SweAbstractDataComponentCreator
        implements ObservationVisitor<SweAbstractDataComponent> {

    @Override
    public SweAbstractDataComponent visit(GeometryObservation o)
            throws OwsExceptionReport {
        // TODO implement SweEnvelope/SweCoordinte etc.
        throw notSupported(o);
    }

    @Override
    public SweAbstractDataComponent visit(BlobObservation o)
            throws OwsExceptionReport {
        throw notSupported(o);
    }

    @Override
    public SweQuantity visit(NumericObservation o) {
        SweQuantity component = new SweQuantity();
        component.setValue(o.getValue().doubleValue());
        return setCommonValues(component, o);
    }

    @Override
    public SweBoolean visit(BooleanObservation o) {
        SweBoolean component = new SweBoolean();
        component.setValue(o.getValue());
        return setCommonValues(component, o);
    }

    @Override
    public SweCategory visit(CategoryObservation o) {
        SweCategory component = new SweCategory();
        component.setValue(o.getValue());
        return setCommonValues(component, o);
    }

    @Override
    public SweDataRecord visit(ComplexObservation o)
            throws OwsExceptionReport {
        SweDataRecord record = new SweDataRecord();
        for (Observation<?> sub : o.getValue()) {
            String fieldName = sub.getObservableProperty().getName();
            record.addField(new SweField(fieldName, sub.accept(this)));
        }
        return setCommonValues(record, o);
    }

    @Override
    public SweCount visit(CountObservation o) {
        SweCount component = new SweCount();
        component.setValue(o.getValue());
        return setCommonValues(component, o);
    }

    @Override
    public SweText visit(TextObservation o)
            throws OwsExceptionReport {
        SweText component = new SweText();
        component.setValue(o.getValue());
        return setCommonValues(component, o);
    }

    @Override
    public SweDataArray visit(SweDataArrayObservation o)
            throws OwsExceptionReport {
        XmlObject xml = XmlHelper.parseXmlString(o.getValue());
        return (SweDataArray) CodingHelper.decodeXmlElement(xml);
    }

    protected <T extends SweAbstractDataComponent> T setCommonValues(
            T component, Observation<?> observation) {
        ObservableProperty op = observation.getObservableProperty();
        component.setIdentifier(op.getIdentifier());
        component.setDefinition(op.getIdentifier());
        component.setDescription(op.getDescription());
        if (op.getCodespace() != null) {
            String codespace = op.getCodespaceName().getCodespace();
            component.setName(new CodeType(op.getName(), codespace));
        } else {
            component.setName(op.getName());
        }
        if (observation.getUnit() != null &&
            component instanceof SweAbstractUomType) {
            SweAbstractUomType<?> uomType = (SweAbstractUomType) component;
            uomType.setUom(observation.getUnit().getUnit());
        }
        return component;
    }

    protected OwsExceptionReport notSupported(Observation<?> o) {
        return new NoApplicableCodeException()
                .withMessage("Complex observation fields of type %s" +
                             " are currently not supported", o.getValue());
    }

}
