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

/**
 * TODO JavaDoc
 *
 * @author Christian Autermann
 */
public abstract class VoidValuedObservationVisitor
        implements ValuedObservationVisitor<Void> {

    protected abstract void _visit(NumericValuedObservation o);

    protected abstract void _visit(BlobValuedObservation o);

    protected abstract void _visit(BooleanValuedObservation o);

    protected abstract void _visit(CategoryValuedObservation o);

    protected abstract void _visit(ComplexValuedObservation o);

    protected abstract void _visit(CountValuedObservation o);

    protected abstract void _visit(GeometryValuedObservation o);

    protected abstract void _visit(TextValuedObservation o);

    protected abstract void _visit(SweDataArrayValuedObservation o);

    @Override
    public Void visit(NumericValuedObservation o) {
        _visit(o);
        return null;
    }

    @Override
    public Void visit(BlobValuedObservation o) {
        _visit(o);
        return null;
    }

    @Override
    public Void visit(BooleanValuedObservation o) {
        _visit(o);
        return null;
    }

    @Override
    public Void visit(CategoryValuedObservation o) {
        _visit(o);
        return null;
    }

    @Override
    public Void visit(ComplexValuedObservation o) {
        _visit(o);
        return null;
    }

    @Override
    public Void visit(CountValuedObservation o) {
        _visit(o);
        return null;
    }

    @Override
    public Void visit(GeometryValuedObservation o) {
        _visit(o);
        return null;
    }

    @Override
    public Void visit(TextValuedObservation o) {
        _visit(o);
        return null;
    }

    @Override
    public Void visit(SweDataArrayValuedObservation o) {
        _visit(o);
        return null;
    }

}
