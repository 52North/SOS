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
public interface ObservationVisitor<T> {

    T visit(NumericObservation o);

    T visit(BlobObservation o);

    T visit(BooleanObservation o);

    T visit(CategoryObservation o);

    T visit(ComplexObservation o);

    T visit(CountObservation o);

    T visit(GeometryObservation o);

    T visit(TextObservation o);

    T visit(SweDataArrayObservation o);

}
