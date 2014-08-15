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
public interface ObservationVisitor<T> {

    T visit(NumericObservation o)
            throws OwsExceptionReport;

    T visit(BlobObservation o)
            throws OwsExceptionReport;

    T visit(BooleanObservation o)
            throws OwsExceptionReport;

    T visit(CategoryObservation o)
            throws OwsExceptionReport;

    T visit(ComplexObservation o)
            throws OwsExceptionReport;

    T visit(CountObservation o)
            throws OwsExceptionReport;

    T visit(GeometryObservation o)
            throws OwsExceptionReport;

    T visit(TextObservation o)
            throws OwsExceptionReport;

    T visit(SweDataArrayObservation o)
            throws OwsExceptionReport;

}
