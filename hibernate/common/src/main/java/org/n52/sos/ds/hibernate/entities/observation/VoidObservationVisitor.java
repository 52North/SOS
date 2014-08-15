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
import org.n52.sos.ogc.ows.OwsExceptionReport;

/**
 * TODO JavaDoc
 *
 * @author Christian Autermann
 */
public abstract class VoidObservationVisitor
        implements ObservationVisitor<Void> {

    protected abstract void _visit(NumericObservation o)
            throws OwsExceptionReport;

    protected abstract void _visit(BlobObservation o)
            throws OwsExceptionReport;

    protected abstract void _visit(BooleanObservation o)
            throws OwsExceptionReport;

    protected abstract void _visit(CategoryObservation o)
            throws OwsExceptionReport;

    protected abstract void _visit(ComplexObservation o)
            throws OwsExceptionReport;

    protected abstract void _visit(CountObservation o)
            throws OwsExceptionReport;

    protected abstract void _visit(GeometryObservation o)
            throws OwsExceptionReport;

    protected abstract void _visit(TextObservation o)
            throws OwsExceptionReport;

    protected abstract void _visit(SweDataArrayObservation o)
            throws OwsExceptionReport;

    @Override
    public Void visit(NumericObservation o)
            throws OwsExceptionReport {
        _visit(o);
        return null;
    }

    @Override
    public Void visit(BlobObservation o)
            throws OwsExceptionReport {
        _visit(o);
        return null;
    }

    @Override
    public Void visit(BooleanObservation o)
            throws OwsExceptionReport {
        _visit(o);
        return null;
    }

    @Override
    public Void visit(CategoryObservation o)
            throws OwsExceptionReport {
        _visit(o);
        return null;
    }

    @Override
    public Void visit(ComplexObservation o)
            throws OwsExceptionReport {
        _visit(o);
        return null;
    }

    @Override
    public Void visit(CountObservation o)
            throws OwsExceptionReport {
        _visit(o);
        return null;
    }

    @Override
    public Void visit(GeometryObservation o)
            throws OwsExceptionReport {
        _visit(o);
        return null;
    }

    @Override
    public Void visit(TextObservation o)
            throws OwsExceptionReport {
        _visit(o);
        return null;
    }

    @Override
    public Void visit(SweDataArrayObservation o)
            throws OwsExceptionReport {
        _visit(o);
        return null;
    }

}
