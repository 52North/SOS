package org.n52.sos.ds.hibernate.entities.observation;

import org.n52.sos.ds.hibernate.entities.observation.valued.BlobValuedObservation;
import org.n52.sos.ds.hibernate.entities.observation.valued.BooleanValuedObservation;
import org.n52.sos.ds.hibernate.entities.observation.valued.CategoryValuedObservation;
import org.n52.sos.ds.hibernate.entities.observation.valued.ComplexValuedObservation;
import org.n52.sos.ds.hibernate.entities.observation.valued.CountValuedObservation;
import org.n52.sos.ds.hibernate.entities.observation.valued.GeometryValuedObservation;
import org.n52.sos.ds.hibernate.entities.observation.valued.NumericValuedObservation;
import org.n52.sos.ds.hibernate.entities.observation.valued.SweDataArrayValuedObservation;
import org.n52.sos.ds.hibernate.entities.observation.valued.TextValuedObservation;
import org.n52.sos.ogc.ows.OwsExceptionReport;

/**
 * TODO JavaDoc
 *
 * @author Christian Autermann
 */
public abstract class VoidValuedObservationVisitor
        implements ValuedObservationVisitor<Void> {

    protected abstract void _visit(NumericValuedObservation o)
            throws OwsExceptionReport;

    protected abstract void _visit(BlobValuedObservation o)
            throws OwsExceptionReport;

    protected abstract void _visit(BooleanValuedObservation o)
            throws OwsExceptionReport;

    protected abstract void _visit(CategoryValuedObservation o)
            throws OwsExceptionReport;

    protected abstract void _visit(ComplexValuedObservation o)
            throws OwsExceptionReport;

    protected abstract void _visit(CountValuedObservation o)
            throws OwsExceptionReport;

    protected abstract void _visit(GeometryValuedObservation o)
            throws OwsExceptionReport;

    protected abstract void _visit(TextValuedObservation o)
            throws OwsExceptionReport;

    protected abstract void _visit(SweDataArrayValuedObservation o)
            throws OwsExceptionReport;

    @Override
    public Void visit(NumericValuedObservation o)
            throws OwsExceptionReport {
        _visit(o);
        return null;
    }

    @Override
    public Void visit(BlobValuedObservation o)
            throws OwsExceptionReport {
        _visit(o);
        return null;
    }

    @Override
    public Void visit(BooleanValuedObservation o)
            throws OwsExceptionReport {
        _visit(o);
        return null;
    }

    @Override
    public Void visit(CategoryValuedObservation o)
            throws OwsExceptionReport {
        _visit(o);
        return null;
    }

    @Override
    public Void visit(ComplexValuedObservation o)
            throws OwsExceptionReport {
        _visit(o);
        return null;
    }

    @Override
    public Void visit(CountValuedObservation o)
            throws OwsExceptionReport {
        _visit(o);
        return null;
    }

    @Override
    public Void visit(GeometryValuedObservation o)
            throws OwsExceptionReport {
        _visit(o);
        return null;
    }

    @Override
    public Void visit(TextValuedObservation o)
            throws OwsExceptionReport {
        _visit(o);
        return null;
    }

    @Override
    public Void visit(SweDataArrayValuedObservation o)
            throws OwsExceptionReport {
        _visit(o);
        return null;
    }

}
