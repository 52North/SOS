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
public interface ValuedObservationVisitor<T> {

    T visit(NumericValuedObservation o)
            throws OwsExceptionReport;

    T visit(BlobValuedObservation o)
            throws OwsExceptionReport;

    T visit(BooleanValuedObservation o)
            throws OwsExceptionReport;

    T visit(CategoryValuedObservation o)
            throws OwsExceptionReport;

    T visit(ComplexValuedObservation o)
            throws OwsExceptionReport;

    T visit(CountValuedObservation o)
            throws OwsExceptionReport;

    T visit(GeometryValuedObservation o)
            throws OwsExceptionReport;

    T visit(TextValuedObservation o)
            throws OwsExceptionReport;

    T visit(SweDataArrayValuedObservation o)
            throws OwsExceptionReport;

}
