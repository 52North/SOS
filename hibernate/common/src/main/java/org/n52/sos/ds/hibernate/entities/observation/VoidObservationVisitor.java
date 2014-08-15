package org.n52.sos.ds.hibernate.entities.observation;

import org.n52.sos.ds.hibernate.entities.observation.full.BlobObservation;
import org.n52.sos.ds.hibernate.entities.observation.full.BooleanObservation;
import org.n52.sos.ds.hibernate.entities.observation.full.CategoryObservation;
import org.n52.sos.ds.hibernate.entities.observation.full.ComplexObservation;
import org.n52.sos.ds.hibernate.entities.observation.full.CountObservation;
import org.n52.sos.ds.hibernate.entities.observation.full.GeometryObservation;
import org.n52.sos.ds.hibernate.entities.observation.full.NumericObservation;
import org.n52.sos.ds.hibernate.entities.observation.full.SweDataArrayObservation;
import org.n52.sos.ds.hibernate.entities.observation.full.TextObservation;

/**
 * TODO JavaDoc
 *
 * @author Christian Autermann
 */
public abstract class VoidObservationVisitor
        implements ObservationVisitor<Void> {

    protected abstract void _visit(NumericObservation o);

    protected abstract void _visit(BlobObservation o);

    protected abstract void _visit(BooleanObservation o);

    protected abstract void _visit(CategoryObservation o);

    protected abstract void _visit(ComplexObservation o);

    protected abstract void _visit(CountObservation o);

    protected abstract void _visit(GeometryObservation o);

    protected abstract void _visit(TextObservation o);

    protected abstract void _visit(SweDataArrayObservation o);

    @Override
    public Void visit(NumericObservation o) {
        _visit(o);
        return null;
    }

    @Override
    public Void visit(BlobObservation o) {
        _visit(o);
        return null;
    }

    @Override
    public Void visit(BooleanObservation o) {
        _visit(o);
        return null;
    }

    @Override
    public Void visit(CategoryObservation o) {
        _visit(o);
        return null;
    }

    @Override
    public Void visit(ComplexObservation o) {
        _visit(o);
        return null;
    }

    @Override
    public Void visit(CountObservation o) {
        _visit(o);
        return null;
    }

    @Override
    public Void visit(GeometryObservation o) {
        _visit(o);
        return null;
    }

    @Override
    public Void visit(TextObservation o) {
        _visit(o);
        return null;
    }

    @Override
    public Void visit(SweDataArrayObservation o) {
        _visit(o);
        return null;
    }

}
