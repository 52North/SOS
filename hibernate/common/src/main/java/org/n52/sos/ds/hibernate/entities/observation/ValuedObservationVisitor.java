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
public interface ValuedObservationVisitor<T> {

    T visit(NumericValuedObservation o);

    T visit(BlobValuedObservation o);

    T visit(BooleanValuedObservation o);

    T visit(CategoryValuedObservation o);

    T visit(ComplexValuedObservation o);

    T visit(CountValuedObservation o);

    T visit(GeometryValuedObservation o);

    T visit(TextValuedObservation o);

    T visit(SweDataArrayValuedObservation o);

}
